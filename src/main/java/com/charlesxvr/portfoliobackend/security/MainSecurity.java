package com.charlesxvr.portfoliobackend.security;

import com.charlesxvr.portfoliobackend.security.enums.Permission;
import com.charlesxvr.portfoliobackend.security.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class MainSecurity {

    @Autowired
    private AuthenticationProvider authenticationProvider;
    @Autowired
    private JwtAuthenticationFilter authenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf( csrfConfig -> csrfConfig.disable() )
                .sessionManagement( sessionManagment -> sessionManagment.sessionCreationPolicy(SessionCreationPolicy.STATELESS) )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(
                        authenticationFilter, UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }
    private static Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> builderRequestMatchers() {
        return authConfig -> {

            authConfig.requestMatchers(HttpMethod.POST, "/api/auth/signin").permitAll();
            authConfig.requestMatchers(HttpMethod.POST, "/api/auth").permitAll();

            authConfig.requestMatchers(HttpMethod.GET, "/api/auth").permitAll();
            authConfig.requestMatchers("/error").permitAll();

            authConfig.requestMatchers(HttpMethod.GET, "/api/users").hasAuthority(Permission.READ_ALL_USERS.name());
            authConfig.requestMatchers(HttpMethod.DELETE, "/api/users").hasAuthority(Permission.DELETE_ONE_USER.name());

            authConfig.anyRequest().denyAll();
        };
    }
}
