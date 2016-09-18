package net

import shapeless._
import net._

object Mapper {

	def f(m: Map[String, MyType]): HList = m.foldLeft[HList](HNil){ (acc, elem) =>
		val (key, t) = elem
		t match {
			case MyInt(_,_) => classOf[Int]     :: acc
			case MyStr      => classOf[String]  :: acc
			case MyBool     => classOf[Boolean] :: acc
		} 
	}
}
