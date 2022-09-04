package io.github.smiley4.ktorswaggerui.tests

import io.github.smiley4.ktorswaggerui.specbuilder.ComponentsContext
import io.github.smiley4.ktorswaggerui.specbuilder.OApiSchemaBuilder
import io.kotest.core.spec.style.StringSpec
import java.math.BigDecimal

class PrimitiveSchemaGenerationTests : StringSpec({

    "generate schema for byte" {
        getOApiSchemaBuilder().build(Byte::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "integer"
            minimum = BigDecimal.valueOf(-128)
            maximum = BigDecimal.valueOf(127)
        }
    }

    "generate schema for unsigned byte" {
        getOApiSchemaBuilder().build(UByte::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "integer"
            minimum = BigDecimal.valueOf(0)
            maximum = BigDecimal.valueOf(255)
        }
    }

    "generate schema for short" {
        getOApiSchemaBuilder().build(Short::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "integer"
            minimum = BigDecimal.valueOf(-32768)
            maximum = BigDecimal.valueOf(32767)
        }
    }

    "generate schema for unsigned short" {
        getOApiSchemaBuilder().build(UShort::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "integer"
            minimum = BigDecimal.valueOf(0)
            maximum = BigDecimal.valueOf(65535)
        }
    }

    "generate schema for integer" {
        getOApiSchemaBuilder().build(Int::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "integer"
            format = "int32"
        }
    }

    "generate schema for unsigned integer" {
        getOApiSchemaBuilder().build(UInt::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "integer"
            minimum = BigDecimal.valueOf(0)
            maximum = BigDecimal.valueOf(4294967295)
        }
    }

    "generate schema for long" {
        getOApiSchemaBuilder().build(Long::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "integer"
            format = "int64"
        }
    }

    "generate schema for unsigned long" {
        getOApiSchemaBuilder().build(ULong::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "integer"
            minimum = BigDecimal.valueOf(0)
        }
    }

    "generate schema for float" {
        getOApiSchemaBuilder().build(Float::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "number"
            format = "float"
        }
    }

    "generate schema for double" {
        getOApiSchemaBuilder().build(Double::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "number"
            format = "double"
        }
    }

    "generate schema for character" {
        getOApiSchemaBuilder().build(Char::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "string"
            minLength = 1
            maxLength = 1
        }
    }

    "generate schema for string" {
        getOApiSchemaBuilder().build(String::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "string"
        }
    }

    "generate schema for boolean" {
        getOApiSchemaBuilder().build(Boolean::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "boolean"
        }
    }

})