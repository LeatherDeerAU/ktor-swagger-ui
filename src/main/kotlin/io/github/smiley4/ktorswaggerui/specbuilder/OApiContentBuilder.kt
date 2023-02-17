package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.CustomArraySchemaRef
import io.github.smiley4.ktorswaggerui.dsl.CustomJsonSchema
import io.github.smiley4.ktorswaggerui.dsl.CustomObjectSchemaRef
import io.github.smiley4.ktorswaggerui.dsl.CustomOpenApiSchema
import io.github.smiley4.ktorswaggerui.dsl.CustomSchemaRef
import io.github.smiley4.ktorswaggerui.dsl.CustomSchemas
import io.github.smiley4.ktorswaggerui.dsl.OpenApiBaseBody
import io.github.smiley4.ktorswaggerui.dsl.OpenApiExample
import io.github.smiley4.ktorswaggerui.dsl.OpenApiMultipartBody
import io.github.smiley4.ktorswaggerui.dsl.OpenApiSimpleBody
import io.github.smiley4.ktorswaggerui.dsl.RemoteSchema
import io.ktor.http.ContentType
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.Encoding
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.XML
import java.lang.reflect.Type

/**
 * Builder for the OpenAPI Content Object (e.g. request and response bodies)
 */
class OApiContentBuilder {

    private val schemaBuilder = OApiSchemaBuilder()
    private val exampleBuilder = OApiExampleBuilder()
    private val headerBuilder = OApiHeaderBuilder()
    private val jsonToSchemaConverter = JsonToOpenApiSchemaConverter()

    fun build(body: OpenApiBaseBody, components: ComponentsContext, config: SwaggerUIPluginConfig): Content {
        return when (body) {
            is OpenApiSimpleBody -> buildSimpleBody(body, components, config)
            is OpenApiMultipartBody -> buildMultipartBody(body, components, config)
        }
    }

    private fun buildSimpleBody(body: OpenApiSimpleBody, components: ComponentsContext, config: SwaggerUIPluginConfig): Content {
        return Content().apply {
            val maybeSchemaObj = buildSchema(body, components, config)
            body.getMediaTypes().forEach { mediaType ->
                if (maybeSchemaObj == null) {
                    addMediaType(mediaType.toString(), MediaType())
                } else {
                    addMediaType(mediaType.toString(), buildMediaType(body.getExamples(), maybeSchemaObj, components))
                }
            }
            if (body.getMediaTypes().isEmpty() && maybeSchemaObj != null) {
                addMediaType(chooseMediaType(maybeSchemaObj).toString(), buildMediaType(body.getExamples(), maybeSchemaObj, components))
            }
        }
    }

    private fun buildMultipartBody(body: OpenApiMultipartBody, components: ComponentsContext, config: SwaggerUIPluginConfig): Content {
        val mediaTypes = body.getMediaTypes().ifEmpty { setOf(ContentType.MultiPart.FormData) }
        return Content().apply {
            mediaTypes.forEach { mediaType ->
                addMediaType(mediaType.toString(), MediaType().apply {
                    schema = buildMultipartSchema(body, components, config)
                    encoding = buildMultipartEncoding(body, config)
                })
            }
        }
    }

    private fun buildMultipartSchema(
        body: OpenApiMultipartBody,
        components: ComponentsContext,
        config: SwaggerUIPluginConfig
    ): Schema<Any> {
        return Schema<Any>().apply {
            type = "object"
            properties = mutableMapOf<String?, Schema<*>?>().also { props ->
                body.getParts().forEach { part ->
                    if (part.customSchema != null) {
                        buildSchemaFromCustom(part.customSchema!!, components, config.getCustomSchemas())
                    } else {
                        props[part.name] = buildSchemaFromType(part.type, components, config)
                    }
                }
            }
        }
    }

    private fun buildMultipartEncoding(body: OpenApiMultipartBody, config: SwaggerUIPluginConfig): MutableMap<String, Encoding>? {
        if (body.getParts().flatMap { it.mediaTypes }.isEmpty()) {
            return null
        } else {
            return mutableMapOf<String, Encoding>().also {
                body.getParts()
                    .filter { it.mediaTypes.isNotEmpty() || it.getHeaders().isNotEmpty() }
                    .forEach { part ->
                        it[part.name] = Encoding().apply {
                            contentType = part.mediaTypes.joinToString(", ") { it.toString() }
                            headers = part.getHeaders().mapValues { headerBuilder.build(it.value, config) }
                        }
                    }
            }
        }
    }

    private fun buildSchema(body: OpenApiSimpleBody, components: ComponentsContext, config: SwaggerUIPluginConfig): Schema<Any>? {
        return if (body.customSchema != null) {
            buildSchemaFromCustom(body.customSchema!!, components, config.getCustomSchemas())
        } else {
            buildSchemaFromType(body.type, components, config)
        }
    }

    private fun buildSchemaFromType(type: Type?, components: ComponentsContext, config: SwaggerUIPluginConfig): Schema<Any>? {
        return type
            ?.let { schemaBuilder.build(it, components, config) }
            ?.let { prepareForXml(type, it) }
    }

    private fun buildSchemaFromCustom(
        customSchema: CustomSchemaRef,
        components: ComponentsContext,
        customSchemas: CustomSchemas
    ): Schema<Any> {
        val custom = customSchemas.getSchema(customSchema.schemaId)
        if (custom == null) {
            return Schema<Any>()
        } else {
            return when (custom) {
                is CustomJsonSchema -> {
                    val schema = jsonToSchemaConverter.toSchema(custom.provider())
                    components.addSchema(customSchema.schemaId, schema)
                }
                is CustomOpenApiSchema -> {
                    components.addSchema(customSchema.schemaId, custom.provider())
                }
                is RemoteSchema -> {
                    Schema<Any>().apply {
                        type = "object"
                        `$ref` = custom.url
                    }
                }
            }.let { schema ->
                when (customSchema) {
                    is CustomObjectSchemaRef -> schema
                    is CustomArraySchemaRef -> Schema<Any>().apply {
                        this.type = "array"
                        this.items = schema
                    }
                }
            }
        }
    }

    private fun buildMediaType(examples: Map<String, OpenApiExample>, schema: Schema<*>, components: ComponentsContext): MediaType {
        return MediaType().apply {
            this.schema = schema
            examples.forEach { (name, obj) ->
                addExamples(name, exampleBuilder.build(name, obj, components))
            }
        }
    }

    private fun chooseMediaType(schema: Schema<*>): ContentType {
        return when (schema.type) {
            "integer" -> ContentType.Text.Plain
            "number" -> ContentType.Text.Plain
            "boolean" -> ContentType.Text.Plain
            "string" -> ContentType.Text.Plain
            "object" -> ContentType.Application.Json
            "array" -> ContentType.Application.Json
            null -> ContentType.Application.Json
            else -> ContentType.Text.Plain
        }
    }

    private fun prepareForXml(type: Type, schema: Schema<Any>): Schema<Any> {
        schema.xml = XML().apply {
            if (type is Class<*>) {
                name = if (type.isArray) {
                    type.componentType.simpleName
                } else {
                    type.simpleName
                }
            }
        }
        return schema
    }

}