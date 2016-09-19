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

Output:

`case class Foo(firstName: String, lastName: String, age: Option[BoundedInt])`

where `BoundedInt` is already defined by:

```
class BoundedInt private (age: Int) 
object BoundedInt {
	def apply(x: Int): Option[BoundedInt] = 
	 if(x >= 0) Some( new BoundedInt(x) ) else None
}
```