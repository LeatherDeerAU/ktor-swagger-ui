package io.github.smiley4.ktorswaggerui.tests

import io.github.smiley4.ktorswaggerui.apispec.ComponentsContext
import io.github.smiley4.ktorswaggerui.apispec.OApiComponentsGenerator
import io.github.smiley4.ktorswaggerui.apispec.OApiExampleGenerator
import io.github.smiley4.ktorswaggerui.apispec.OApiSchemaGenerator
import io.github.smiley4.ktorswaggerui.documentation.ExampleDocumentation
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.media.Schema
import kotlin.reflect.KClass

class ComponentsObjectTest : StringSpec({

    "test nothing in components section" {
        val context = ComponentsContext(false, mutableMapOf(), false, mutableMapOf())

        generateSchema(ComponentsTestClass1::class, context).let {
            it.type shouldBe "object"
            it.properties shouldHaveSize 2
            it.`$ref`.shouldBeNull()
        }

        generateSchema(ComponentsTestClass2::class, context).let {
            it.type shouldBe "object"
            it.properties shouldHaveSize 2
            it.`$ref`.shouldBeNull()
        }

        generateSchema(Array<ComponentsTestClass2>::class, context).let {
            it.type shouldBe "array"
            it.items.shouldNotBeNull()
            it.`$ref`.shouldBeNull()
            it.items.shouldNotBeNull()
            it.items.type shouldBe "object"
        }

        generateExample("Example1", ComponentsTestClass1("test1", true), context).let {
            it.value.shouldNotBeNull()
            it.`$ref`.shouldBeNull()
        }

        generateExample("Example1", ComponentsTestClass1("test1-different", false), context).let {
            it.value.shouldNotBeNull()
            it.`$ref`.shouldBeNull()
        }

        generateExample("Example2", ComponentsTestClass2("testCounter", 42), context).let {
            it.value.shouldNotBeNull()
            it.`$ref`.shouldBeNull()
        }

        generateComponentsObject(context).let {
            it.schemas shouldHaveSize 0
            it.examples shouldHaveSize 0
            it.responses.shouldBeNull()
            it.parameters.shouldBeNull()
            it.requestBodies.shouldBeNull()
            it.headers.shouldBeNull()
            it.securitySchemes.shouldBeNull()
            it.links.shouldBeNull()
            it.callbacks.shouldBeNull()
            it.extensions.shouldBeNull()
        }
    }

    "test schemas in components section" {
        val context = ComponentsContext(true, mutableMapOf(), false, mutableMapOf())

        generateSchema(ComponentsTestClass1::class, context).let {
            it.type.shouldBeNull()
            it.properties.shouldBeNull()
            it.`$ref` shouldBe "#/components/schemas/io.github.smiley4.ktorswaggerui.tests.ComponentsObjectTest.Companion.ComponentsTestClass1"
        }

        generateSchema(ComponentsTestClass2::class, context).let {
            it.type.shouldBeNull()
            it.properties.shouldBeNull()
            it.`$ref` shouldBe "#/components/schemas/io.github.smiley4.ktorswaggerui.tests.ComponentsObjectTest.Companion.ComponentsTestClass2"
        }

        generateSchema(Array<ComponentsTestClass2>::class, context).let {
            it.type shouldBe "array"
            it.properties.shouldBeNull()
            it.`$ref`.shouldBeNull()
            it.items.shouldNotBeNull()
            it.items.type.shouldBeNull()
            it.items.`$ref` shouldBe "#/components/schemas/io.github.smiley4.ktorswaggerui.tests.ComponentsObjectTest.Companion.ComponentsTestClass2"
        }

        generateExample("Example1", ComponentsTestClass1("test1", true), context).let {
            it.value.shouldNotBeNull()
            it.`$ref`.shouldBeNull()
        }

        generateExample("Example1", ComponentsTestClass1("test1-different", false), context).let {
            it.value.shouldNotBeNull()
            it.`$ref`.shouldBeNull()
        }

        generateExample("Example2", ComponentsTestClass2("testCounter", 42), context).let {
            it.value.shouldNotBeNull()
            it.`$ref`.shouldBeNull()
        }

        generateComponentsObject(context).let {
            it.schemas shouldHaveSize 2
            it.schemas.keys shouldContainExactlyInAnyOrder listOf(
                "io.github.smiley4.ktorswaggerui.tests.ComponentsObjectTest.Companion.ComponentsTestClass1",
                "io.github.smiley4.ktorswaggerui.tests.ComponentsObjectTest.Companion.ComponentsTestClass2"
            )
            it.schemas["io.github.smiley4.ktorswaggerui.tests.ComponentsObjectTest.Companion.ComponentsTestClass1"]!!.let { schema ->
                schema.type shouldBe "object"
                schema.properties shouldHaveSize 2
                schema.`$ref`.shouldBeNull()
            }
            it.schemas["io.github.smiley4.ktorswaggerui.tests.ComponentsObjectTest.Companion.ComponentsTestClass2"]!!.let { schema ->
                schema.type shouldBe "object"
                schema.properties shouldHaveSize 2
                schema.`$ref`.shouldBeNull()
            }
            it.examples shouldHaveSize 0
            it.responses.shouldBeNull()
            it.parameters.shouldBeNull()
            it.requestBodies.shouldBeNull()
            it.headers.shouldBeNull()
            it.securitySchemes.shouldBeNull()
            it.links.shouldBeNull()
            it.callbacks.shouldBeNull()
            it.extensions.shouldBeNull()
        }
    }

    "test examples in components section" {
        val context = ComponentsContext(false, mutableMapOf(), true, mutableMapOf())

        generateSchema(ComponentsTestClass1::class, context).let {
            it.type shouldBe "object"
            it.properties shouldHaveSize 2
            it.`$ref`.shouldBeNull()
        }

        generateSchema(ComponentsTestClass2::class, context).let {
            it.type shouldBe "object"
            it.properties shouldHaveSize 2
            it.`$ref`.shouldBeNull()
        }

        generateSchema(Array<ComponentsTestClass2>::class, context).let {
            it.type shouldBe "array"
            it.items.shouldNotBeNull()
            it.`$ref`.shouldBeNull()
            it.items.shouldNotBeNull()
            it.items.type shouldBe "object"
        }

        generateExample("Example1", ComponentsTestClass1("test1", true), context).let {
            it.value.shouldBeNull()
            it.`$ref` shouldBe "#/components/examples/Example1"
        }

        val exampleValue1Different = ExampleDocumentation(ComponentsTestClass1("test1-different", false))
        generateExample("Example1", exampleValue1Different, context).let {
            it.value.shouldBeNull()
            it.`$ref` shouldBe "#/components/examples/Example1#" + exampleValue1Different.hashCode().toString(16)
        }

        generateExample("Example2", ComponentsTestClass2("testCounter", 42), context).let {
            it.value.shouldBeNull()
            it.`$ref` shouldBe "#/components/examples/Example2"
        }

        generateComponentsObject(context).let {
            it.examples shouldHaveSize 3
            it.examples.keys shouldContainExactlyInAnyOrder listOf(
                "Example1",
                "Example1#" + exampleValue1Different.hashCode().toString(16),
                "Example2",
            )
            it.examples.values.forEach { example ->
                example.value.shouldNotBeNull()
                example.`$ref`.shouldBeNull()
            }
            it.schemas shouldHaveSize 0
            it.responses.shouldBeNull()
            it.parameters.shouldBeNull()
            it.requestBodies.shouldBeNull()
            it.headers.shouldBeNull()
            it.securitySchemes.shouldBeNull()
            it.links.shouldBeNull()
            it.callbacks.shouldBeNull()
            it.extensions.shouldBeNull()
        }
    }

}) {

    companion object {

        private fun generateComponentsObject(context: ComponentsContext): Components {
            return OApiComponentsGenerator().generate(context)
        }

        private fun generateSchema(type: KClass<*>, context: ComponentsContext): Schema<*> {
            return OApiSchemaGenerator().generate(type, context)
        }

        private fun generateExample(name: String, example: Any, context: ComponentsContext): Example {
            return OApiExampleGenerator().generate(name, ExampleDocumentation(example), context)
        }

        private fun generateExample(name: String, example: ExampleDocumentation, context: ComponentsContext): Example {
            return OApiExampleGenerator().generate(name, example, context)
        }

        private data class ComponentsTestClass1(
            val someText: String,
            val someFlat: Boolean
        )

        private data class ComponentsTestClass2(
            val name: String,
            val counter: Int,
        )

    }

}