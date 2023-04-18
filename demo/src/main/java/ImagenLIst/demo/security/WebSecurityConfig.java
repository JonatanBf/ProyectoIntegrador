package ImagenLIst.demo.security;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig {

    private final UserDetailsService userDetailsService;

    private JWTAuthorizationFilter authorizationFilter;

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
            final CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(List.of("http://127.0.0.1:5173", "http://localhost:5173", "http://equipo7-tomi.s3-website.us-east-2.amazonaws.com",  "http://localhost:8080"));
            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE","PATCH"));
            configuration.setAllowCredentials(true);
            configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));

            final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);

            return source;
        }

        @Bean
        SecurityFilterChain filterChain(HttpSecurity http, @Autowired AuthenticationManager authManager) throws Exception {

            JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter();
            jwtAuthenticationFilter.setAuthenticationManager(authManager);
            jwtAuthenticationFilter.setFilterProcessesUrl("/login");

            return http
                    .cors().and()
                    .csrf().disable()
                    .authorizeRequests()
                    .requestMatchers(String.valueOf(PathRequest.toStaticResources().atCommonLocations())).permitAll() // Permitir acceso a recursos estáticos (como css, js, etc.)
                    .requestMatchers(new AntPathRequestMatcher("/reservas/agregar")).authenticated() // Requiere autenticación solo para las solicitudes a '/reservas'
                    .anyRequest().permitAll() // Permitir acceso para cualquier otra solicitud
                    .and()
                    .httpBasic()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .addFilter(jwtAuthenticationFilter)
                    .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
                    .build();
        }

        @Bean
        AuthenticationManager authManager(HttpSecurity http) throws Exception {
            return http.getSharedObject(AuthenticationManagerBuilder.class)
                    .userDetailsService(userDetailsService)
                    .passwordEncoder(passwordEncoder())
                    .and()
                    .build();
        }

    // Producir un passwordEncoder
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


}
