package io.github.smiley4.ktorswaggerui.tests

import io.github.smiley4.ktorswaggerui.dsl.OpenApiExample
import io.github.smiley4.ktorswaggerui.specbuilder.ComponentsContext
import io.kotest.core.spec.style.StringSpec
import io.swagger.v3.oas.models.examples.Example

class ExampleObjectTest : StringSpec({

    "test default example object" {
        val exampleValue = ExampleTestClass("TestText", true)
        val example = buildExampleObject("TestExample", exampleValue, ComponentsContext.NOOP)
        example shouldBeExample {
            value = exampleValue
        }
    }

    "test complete example object" {
        val exampleValue = ExampleTestClass("TestText", true)
        val example = buildExampleObject("TestExample", exampleValue, ComponentsContext.NOOP) {
            summary = "Test Summary"
            description = "Test Description"
        }
        example shouldBeExample {
            value = exampleValue
            summary = "Test Summary"
            description = "Test Description"
        }
    }

    "test referencing example in components" {
        val componentsContext = ComponentsContext(false, mutableMapOf(), true, mutableMapOf(), false)
        val exampleValue = ExampleTestClass("TestText", true)
        val example = buildExampleObject("TestExample", exampleValue, componentsContext) {
            summary = "Test Summary"
            description = "Test Description"
        }
        example shouldBeExample {
            `$ref` = "#/components/examples/TestExample"
        }
    }

}) {

    companion object {

        private fun buildExampleObject(name: String, example: Any, context: ComponentsContext): Example {
            return getOApiExampleBuilder().build(name, OpenApiExample(example), context)
        }

        private fun buildExampleObject(
            name: String,
            example: Any,
            context: ComponentsContext,
            builder: OpenApiExample.() -> Unit
        ): Example {
            return getOApiExampleBuilder().build(name, OpenApiExample(example).apply(builder), context)
        }

        private data class ExampleTestClass(
            val someText: String,
            val someFlat: Boolean
        )

    }

}