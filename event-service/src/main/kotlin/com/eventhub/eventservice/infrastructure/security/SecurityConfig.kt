package com.eventhub.eventservice.infrastructure.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter
import org.springframework.security.web.server.SecurityWebFilterChain
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
class SecurityConfig {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers("/actuator/health/**").permitAll()
                    .pathMatchers(HttpMethod.GET, "/api/v1/events").permitAll()
                    .pathMatchers(HttpMethod.GET, "/api/v1/events/*").permitAll()
                    .pathMatchers(HttpMethod.POST, "/api/v1/events").hasRole("ORGANIZER")
                    .pathMatchers(
                        HttpMethod.POST,
                        "/api/v1/events/*/publish",
                        "/api/v1/events/*/cancel"
                    ).hasRole("ORGANIZER")
                    .pathMatchers(HttpMethod.GET, "/api/v1/events/*/registrations").hasAnyRole("ORGANIZER", "ADMIN")
                    .pathMatchers(HttpMethod.POST, "/api/v1/events/*/registrations").authenticated()
                    .pathMatchers(HttpMethod.GET, "/api/v1/events/*/registrations/me").authenticated()
                    .pathMatchers(HttpMethod.DELETE, "/api/v1/events/*/registrations/me").authenticated()
                    .anyExchange().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { jwt ->
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                }
            }
            .build()
    }

    private fun jwtAuthenticationConverter(): Converter<Jwt, Mono<AbstractAuthenticationToken>> {
        val converter = JwtAuthenticationConverter()
        converter.setJwtGrantedAuthoritiesConverter { jwt ->
            val realmAccess = jwt.getClaimAsMap("realm_access")
            val roles = realmAccess?.get("roles") as? Collection<*> ?: emptyList<Any>()
            roles.filterIsInstance<String>()
                .map { role -> SimpleGrantedAuthority("ROLE_$role") }
        }
        return ReactiveJwtAuthenticationConverterAdapter(converter)
    }
}
