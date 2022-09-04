package io.github.smiley4.ktorswaggerui.tests

import io.github.smiley4.ktorswaggerui.AuthKeyLocation
import io.github.smiley4.ktorswaggerui.AuthScheme
import io.github.smiley4.ktorswaggerui.AuthType
import io.github.smiley4.ktorswaggerui.OpenApiSecuritySchemeConfig
import io.github.smiley4.ktorswaggerui.apispec.OApiSecuritySchemesGenerator
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.maps.shouldHaveSize
import io.swagger.v3.oas.models.security.OAuthFlow
import io.swagger.v3.oas.models.security.OAuthFlows
import io.swagger.v3.oas.models.security.Scopes
import io.swagger.v3.oas.models.security.SecurityScheme

class SecuritySchemeObjectTest : StringSpec({

    "test default security scheme object" {
        val securityScheme = generateSecuritySchemeObject("TestAuth") {}
        securityScheme shouldBeSecurityScheme {
            name = "TestAuth"
        }
    }

    "test multiple security scheme objects" {
        val securitySchemes = generateSecuritySchemeObjects(mapOf(
            "TestAuth1" to {
                type = AuthType.HTTP
                scheme = AuthScheme.BASIC
            },
            "TestAuth2" to {
                type = AuthType.HTTP
                scheme = AuthScheme.BASIC
            }
        ))
        securitySchemes[0] shouldBeSecurityScheme {
            name = "TestAuth1"
            type = SecurityScheme.Type.HTTP
            scheme = "Basic"
        }
        securitySchemes[1] shouldBeSecurityScheme {
            name = "TestAuth2"
            type = SecurityScheme.Type.HTTP
            scheme = "Basic"
        }
    }

    "test complete security scheme object" {
        val securityScheme = generateSecuritySchemeObject("TestAuth") {
            type = AuthType.HTTP
            location = AuthKeyLocation.COOKIE
            scheme = AuthScheme.BASIC
            bearerFormat = "test"
            openIdConnectUrl = "Test IOD-Connect URL"
            description = "Test Description"
            flows {
                implicit {
                    authorizationUrl = "Implicit Auth Url"
                    tokenUrl = "Implicity Token Url"
                    refreshUrl = "Implicity Token Url"
                    scopes = mapOf(
                        "implicit1" to "scope1",
                        "implicit2" to "scope2"
                    )
                }
                password {
                    authorizationUrl = "Password Auth Url"
                    tokenUrl = "Password Token Url"
                    refreshUrl = "Password Token Url"
                    scopes = mapOf(
                        "password1" to "scope1",
                        "password2" to "scope2"
                    )
                }
                clientCredentials {
                    authorizationUrl = "ClientCredentials Auth Url"
                    tokenUrl = "ClientCredentials Token Url"
                    refreshUrl = "ClientCredentials Token Url"
                    scopes = mapOf(
                        "clientCredentials1" to "scope1",
                        "clientCredentials2" to "scope2"
                    )
                }
                authorizationCode {
                    authorizationUrl = "AuthorizationCode Auth Url"
                    tokenUrl = "AuthorizationCode Token Url"
                    refreshUrl = "AuthorizationCode Token Url"
                    scopes = mapOf(
                        "authorizationCode1" to "scope1",
                        "authorizationCode2" to "scope2"
                    )
                }
            }
        }
        securityScheme shouldBeSecurityScheme {
            name = "TestAuth"
            type = SecurityScheme.Type.HTTP
            `in` = SecurityScheme.In.COOKIE
            scheme = "Basic"
            bearerFormat = "test"
            openIdConnectUrl = "Test IOD-Connect URL"
            description = "Test Description"
            flows = OAuthFlows().apply {
                implicit = OAuthFlow().apply {
                    authorizationUrl = "Implicit Auth Url"
                    tokenUrl = "Implicity Token Url"
                    refreshUrl = "Implicity Token Url"
                    scopes = Scopes().apply {
                        addString("implicit1", "scope1")
                        addString("implicit2", "scope2")
                    }
                }
                password = OAuthFlow().apply {
                    authorizationUrl = "Password Auth Url"
                    tokenUrl = "Password Token Url"
                    refreshUrl = "Password Token Url"
                    scopes = Scopes().apply {
                        addString("password1", "scope1")
                        addString("password2", "scope2")
                    }
                }
                clientCredentials = OAuthFlow().apply {
                    authorizationUrl = "ClientCredentials Auth Url"
                    tokenUrl = "ClientCredentials Token Url"
                    refreshUrl = "ClientCredentials Token Url"
                    scopes = Scopes().apply {
                        addString("clientCredentials1", "scope1")
                        addString("clientCredentials2", "scope2")
                    }
                }
                authorizationCode = OAuthFlow().apply {
                    authorizationUrl = "AuthorizationCode Auth Url"
                    tokenUrl = "AuthorizationCode Token Url"
                    refreshUrl = "AuthorizationCode Token Url"
                    scopes = Scopes().apply {
                        addString("authorizationCode1", "scope1")
                        addString("authorizationCode2", "scope2")
                    }
                }
            }
        }
    }

}) {

    companion object {

        private fun generateSecuritySchemeObject(name: String, builder: OpenApiSecuritySchemeConfig.() -> Unit): SecurityScheme {
            return OApiSecuritySchemesGenerator().generate(listOf(OpenApiSecuritySchemeConfig(name).apply(builder))).let {
                it shouldHaveSize 1
                it shouldContainKey name
                it[name]!!
            }
        }

        private fun generateSecuritySchemeObjects(builders: Map<String, OpenApiSecuritySchemeConfig.() -> Unit>): List<SecurityScheme> {
            val schemes = mutableListOf<SecurityScheme>()
            builders.forEach { (name, builder) ->
                schemes.addAll(OApiSecuritySchemesGenerator().generate(listOf(OpenApiSecuritySchemeConfig(name).apply(builder))).values)
            }
            schemes shouldHaveSize builders.size
            return schemes
        }

    }

}