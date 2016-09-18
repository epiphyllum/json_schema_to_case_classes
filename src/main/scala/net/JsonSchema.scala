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
case object JBool extends JsonType
case object JInt extends JsonType

object JsonType {
	implicit val jsonTypeDecoder: Decoder[JsonType] = deriveDecoder[JsonType]
}

case class Property(name: String, _type: JsonType, description: Option[String], minimum: Option[Int], maximum: Option[Int])
case class JsonSchema(title: String, _type: RootJsonType, properties: List[Property], requiredFields: List[String])

case class OutputClass(name: String, fields: Map[String, MyType], required: List[String])

object Property {
	implicit val propertyDecoder: Decoder[Property] = deriveDecoder[Property]
}

sealed trait MyType
case class MyInt(min: Option[Int], max: Option[Int]) extends MyType
case object MyStr            				         extends MyType
case object MyBool                        			 extends MyType

object JsonMapper {

	implicit val decoder: Decoder[JsonSchema] = deriveDecoder[JsonSchema]

	def read(x: String): Option[OutputClass] = 
		parse(x).toOption >>= (json => json.as[JsonSchema].toOption) >>= readHelper

	def readHelper(schema: JsonSchema): Option[OutputClass] = schema._type match {
		case JObj => 
			val name                        = schema.title	
			val outputClassFields: Map[String, MyType] = schema.properties.map(extractField).toMap
			Some( OutputClass(name, outputClassFields, schema.requiredFields) )
		case JArr => None
	}

	private def extractField(property: Property): (String, MyType) = {
		val fieldName = property.name
		property._type match {
			case JStr  => (fieldName, MyStr)
			case JBool => (fieldName, MyBool)
			case JInt  => (fieldName, MyInt(property.minimum, property.maximum))
			case _ 	   => ??? // TODO: extract array or object as a List and another case class, respectively, maybe?
		} 
	}
}

case class NotInBound(min: Int, max: Int, value: Int)

class BoundedInt private (val x: Int)
object BoundedInt {
	def apply(min: Int)(max: Int)(value: Int): Either[NotInBound, BoundedInt] = 
		if(value >= min && value <= max) Right( new BoundedInt(value) ) else Left( NotInBound(min, max, value) )
}