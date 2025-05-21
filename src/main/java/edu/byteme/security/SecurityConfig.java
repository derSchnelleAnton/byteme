package edu.byteme.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import edu.byteme.views.accountstuff.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.NullRequestCache;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends VaadinWebSecurity {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.ignoringRequestMatchers(
                "/VAADIN/**", "/UIDL/**", "/frontend/**", "/images/**", "/icons/**", "/favicon.ico"
        ));

        http.requestCache(requestCache -> requestCache
                .requestCache(new NullRequestCache())
        );

        http.authorizeHttpRequests(authz -> authz
                .requestMatchers(
                        "/VAADIN/**", "/UIDL/**", "/frontend/**",
                        "/images/**", "/icons/**", "/favicon.ico"
                ).permitAll()
        );

        super.configure(http);
        return http.build();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        setLoginView(http, LoginView.class, "/menu");
    }


//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        super.configure(http);
//
//        http.csrf(csrf -> csrf
//                .ignoringRequestMatchers(
//                        "/VAADIN/**", "/UIDL/**", "/frontend/**",
//                        "/images/**", "/icons/**", "/favicon.ico"
//                ));
//
//        http.headers(headers -> headers
//                .frameOptions(frameOptions -> frameOptions.disable()));
//
//        http.requestCache(requestCache -> requestCache
//                .requestCache(new NullRequestCache()));
//
//        setLoginView(http, LoginView.class, "/menu");
//    }



    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
