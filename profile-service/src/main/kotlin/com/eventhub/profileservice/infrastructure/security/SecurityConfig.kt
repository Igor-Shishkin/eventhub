package com.eventhub.profileservice.infrastructure.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
class SecurityConfig {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain = http
        .csrf { it.disable() }
        .authorizeExchange { exchanges ->
            exchanges
                .pathMatchers("/actuator/health/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/v1/me").authenticated()
                .pathMatchers(HttpMethod.PUT, "/api/v1/me").authenticated()
                .anyExchange().authenticated()
        }
        .oauth2ResourceServer { oauth2 ->
            oauth2.jwt { jwt ->
                jwt.jwtAuthenticationConverter(
                    ReactiveJwtAuthenticationConverterAdapter(JwtAuthenticationConverter())
                )
            }
        }
        .build()
}
