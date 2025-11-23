package com.arekalov.papersplease.security

import com.arekalov.papersplease.security.Roles.BOSS
import com.arekalov.papersplease.security.Roles.GOD
import com.arekalov.papersplease.security.Roles.INSPECTOR
import com.arekalov.papersplease.security.Roles.MIGRANT
import com.arekalov.papersplease.security.Roles.SECURITY
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    @Suppress("ForbiddenComment")
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    // Public endpoints
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    .requestMatchers("/api/v1/health").permitAll()
                    .requestMatchers("/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()
                    // User profile endpoints (all authenticated users)
                    .requestMatchers("/api/v1/users/me", "/api/v1/users/me/**").authenticated()
                    // UPK endpoints
                    .requestMatchers(HttpMethod.GET, "/api/v1/upks/**").hasAnyRole(INSPECTOR, BOSS, SECURITY, GOD)
                    .requestMatchers(HttpMethod.POST, "/api/v1/upks/**").hasAnyRole(BOSS, GOD)
                    .requestMatchers(HttpMethod.PUT, "/api/v1/upks/**").hasAnyRole(BOSS, GOD)
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/upks/**").hasAnyRole(BOSS, GOD)
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/upks/**").hasRole(GOD)
                    // User management endpoints
                    .requestMatchers("/api/v1/users/**").hasAnyRole(BOSS, SECURITY, GOD)
                    // Shift endpoints - MIGRANT explicitly excluded
                    .requestMatchers("/api/v1/shifts/**").hasAnyRole(INSPECTOR, BOSS, SECURITY, GOD)
                    // Participation endpoints
                    .requestMatchers("/api/v1/participations/**").hasAnyRole(INSPECTOR, BOSS, SECURITY, GOD)
                    // Event endpoints
                    .requestMatchers("/api/v1/events/**").hasAnyRole(INSPECTOR, BOSS, SECURITY, GOD)
                    // Ticket endpoints
                    .requestMatchers("/api/v1/tickets/**").hasAnyRole(INSPECTOR, BOSS, SECURITY, MIGRANT, GOD)
                    // Document endpoints
                    .requestMatchers("/api/v1/documents/**").hasAnyRole(INSPECTOR, BOSS, SECURITY, MIGRANT, GOD)
                    // Appeal endpoints
                    .requestMatchers("/api/v1/appeals/**").hasAnyRole(BOSS, SECURITY, GOD)
                    // Notification endpoints (all authenticated users)
                    .requestMatchers("/api/v1/notifications/**").authenticated()
                    // Report endpoints
                    .requestMatchers("/api/v1/reports/**").hasAnyRole(BOSS, GOD)
                    // Default: all other requests require authentication
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
