package net

import io.circe._, io.circe.parser._, io.circe.generic.semiauto._
import cats._
import cats.data._
import cats.implicits._

sealed trait RootJsonType 
case object JObj extends RootJsonType with JsonType
case object JArr extends RootJsonType with JsonType

object RootJsonType {
	implicit val rootJsonTypeDecoder: Decoder[RootJsonType] = deriveDecoder[RootJsonType]
}

sealed trait JsonType
case object JStr  extends JsonType
case object JInt extends JsonType

// TODO: Use a `JString` decoder rather than whatever's given by default
object JsonType {
	def read(x: String): Option[JsonType] = {
		if      (x == "JStr") Some(JStr)
		else if (x == "JInt") Some(JInt)
		else 	None
	}
}

case class Property(key: String, _type: JsonType, description: Option[String], minimum: Option[Int], maximum: Option[Int])
case class PartialProperty(_type: String, description: Option[String], minimum: Option[Int], maximum: Option[Int])
case class OutputClass(name: String, fields: Map[String, MyType], required: List[String])

object PartialProperty {
	implicit val propertyDecoder: Decoder[PartialProperty] = deriveDecoder[PartialProperty]
}

object Property {

	type AccType = Option[List[Property]]

	def read(pairs: Map[String, Json]): Option[List[Property]] = {
		pairs.foldLeft[AccType](Some(List.empty)){ (acc, elem) =>
			val (key, obj) = elem
			for {
				currentAcc <- acc
				partial    <- obj.as[PartialProperty].toOption
				keyType   <- JsonType.read(partial._type)
			} yield Property(key, keyType, partial.description, partial.minimum, partial.maximum) :: currentAcc
		}
	}
}

sealed trait MyType
case class MyInt(min: Option[Int], max: Option[Int]) extends MyType
case object MyStr            				         extends MyType

object JsonMapper {

	def cursor(json: Json): HCursor = json.hcursor

	def read(str: String): Option[OutputClass] =  {
		for {
			json       <- parse(str).toOption
			jsonCursor <- Some( cursor(json) )
			title      <- jsonCursor.downField("title").as[String].toOption
			_type      <- jsonCursor.downField("type").as[String].toOption
			_          <- if(_type == "JObj") Some( () ) else None  
			jsonProps  <- jsonCursor.downField("properties").as[Map[String, Json]].toOption
			required   <- jsonCursor.downField("required").as[List[String]].toOption
			properties <- Property.read(jsonProps)
		} yield OutputClass(title, properties.map(extractField).toMap, required)
	}

	private def extractField(property: Property): (String, MyType) = 
		property._type match {
			case JStr  => (property.key, MyStr)
			case JInt  => (property.key, MyInt(property.minimum, property.maximum))
			case _ 	   => ??? // TODO: extract array or object as a List and another case class, respectively, maybe?
		} 
}

case class NotInBound(min: Int, max: Int, value: Int)

class BoundedInt private (val x: Int)
object BoundedInt {
	def apply(min: Int)(max: Int)(value: Int): Either[NotInBound, BoundedInt] = 
		if(value >= min && value <= max) Right( new BoundedInt(value) ) else Left( NotInBound(min, max, value) )
}