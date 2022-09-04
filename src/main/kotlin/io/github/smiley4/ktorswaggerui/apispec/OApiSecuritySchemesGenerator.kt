package io.github.smiley4.ktorswaggerui.apispec

import io.github.smiley4.ktorswaggerui.AuthKeyLocation
import io.github.smiley4.ktorswaggerui.AuthScheme
import io.github.smiley4.ktorswaggerui.AuthType
import io.github.smiley4.ktorswaggerui.OpenApiSecuritySchemeConfig
import io.swagger.v3.oas.models.security.SecurityScheme

/**
 * Generator for OpenAPI SecurityScheme-Objects
 */
class OApiSecuritySchemesGenerator {

    /**
     * Generate the OpenAPI SecurityScheme-Objects from the given configs
     */
    fun generate(configs: List<OpenApiSecuritySchemeConfig>): Map<String, SecurityScheme> {
        return mutableMapOf<String, SecurityScheme>().apply {
            configs.forEach {
                put(it.name, SecurityScheme().apply {
                    description = it.description
                    name = it.name
                    type = when (it.type) {
                        AuthType.API_KEY -> SecurityScheme.Type.APIKEY
                        AuthType.HTTP -> SecurityScheme.Type.HTTP
                        AuthType.OAUTH2 -> SecurityScheme.Type.OAUTH2
                        AuthType.OPENID_CONNECT -> SecurityScheme.Type.OPENIDCONNECT
                        AuthType.MUTUAL_TLS -> SecurityScheme.Type.MUTUALTLS
                        null -> null
                    }
                    `in` = when (it.location) {
                        AuthKeyLocation.QUERY -> SecurityScheme.In.QUERY
                        AuthKeyLocation.HEADER -> SecurityScheme.In.HEADER
                        AuthKeyLocation.COOKIE -> SecurityScheme.In.COOKIE
                        null -> null
                    }
                    scheme = when (it.scheme) {
                        AuthScheme.BASIC -> "Basic"
                        AuthScheme.BEARER -> "Bearer"
                        AuthScheme.DIGEST -> "Digest"
                        AuthScheme.HOBA -> "HOBA"
                        AuthScheme.MUTUAL -> "Mutual"
                        AuthScheme.OAUTH -> "OAuth"
                        AuthScheme.SCRAM_SHA_1 -> "SCRAM-SHA-1"
                        AuthScheme.SCRAM_SHA_256 -> "SCRAM-SHA-256"
                        AuthScheme.VAPID -> "vapid"
                        else -> null
                    }
                    bearerFormat = it.bearerFormat
                    flows = it.getFlows()?.let { f -> OApiOAuthFlowsGenerator().generate(f) }
                    openIdConnectUrl = it.openIdConnectUrl
                })
            }
        }
    }

}