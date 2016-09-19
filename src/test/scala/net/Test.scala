package net

import java.net.URI

object Test {

	def main(args: Array[String]): Unit = {
		val url: URI                    = getClass.getResource("/example_schema.json").toURI
    	val lines                       = scala.io.Source.fromFile(url).getLines.mkString("\n")
    	val output: Option[OutputClass] = JsonMapper.read(lines)

		println(s"lines: \n: $lines")

    	println(s"output: $output")
	}

}