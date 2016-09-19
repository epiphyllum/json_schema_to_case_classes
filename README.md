# map_to_case_class

## Problem:

Read a JSON schema into a case class.

Example: `Foo.json`

```json
{
	"title": "Example Schema",
	"type": "object",
	"properties": {
		"firstName": {
			"type": "string"
		},
		"lastName": {
			"type": "string"
		},
		"age": {
			"description": "Age in years",
			"type": "integer",
			"minimum": 0
		}
	},
	"required": ["firstName", "lastName"]
}
```

source: http://json-schema.org/examples.html

## Ideal Solution

Given the above JSON Schema, output a `Foo.scala` file by:

* reading the JSON Schema into an `OutputClass` class, i.e. which captures the key-value fields to be 
  used for the generated case class, e.g. `Foo` below

`case class Foo(firstName: String, lastName: String, age: Option[BoundedInt])`

where `BoundedInt` is already defined by:

```
class BoundedInt private (age: Int) 
object BoundedInt {
	def apply(x: Int): Option[BoundedInt] = 
	 if(x >= 0) Some( new BoundedInt(x) ) else None
}
```

## Current Proof of Concept

```
> test:run
[info] Compiling 1 Scala source to C:\Users\kevin\workspace\map_to_case_class\target\scala-2.11\classes...
[info] Compiling 1 Scala source to C:\Users\kevin\workspace\map_to_case_class\target\scala-2.11\classes...
[info] Compiling 1 Scala source to C:\Users\kevin\workspace\map_to_case_class\target\scala-2.11\test-classes...
[info] Running net.Test
lines:
: {
        "title": "Example Schema",
        "type": "JObj",
        "properties": {
                "firstName": {
                        "_type": "JStr"
                },
                "lastName": {
                        "_type": "JStr"
                },
                "age": {
                        "description": "Age in years",
                        "_type": "JInt",
                        "minimum": 0
                }
        },
        "required": ["firstName", "lastName"]
}
output: Some(OutputClass(Example Schema,Map(age -> MyInt(Some(0),None), lastName -> MyStr, firstName -> MyStr),List(firstName, lastName)))
[success] Total time: 6 s, completed Sep 19, 2016 3:00:56 AM
```

### Notes

I'm cheating by using `_type` as the key name.

## Conclusion

Although the `OutputClass` to `.scala` file is not shown here, I believe that it'll be simple to implement:

```
def f(outputClass: OutputClass): Unit 
```

which, given an `OutputClass`, produces a `.scala` file.

Lastly, as a side thought, I believe that I can test that the output `$FILE.scala` compiles via ScalaTest.