package edu.byteme.security;

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

import com.vaadin.flow.spring.security.VaadinWebSecurity;

import edu.byteme.views.accountstuff.LoginView;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends VaadinWebSecurity {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("DEBUG: filterChain method executed");
        
        http.csrf(csrf -> csrf.ignoringRequestMatchers(
                "/VAADIN/**", "/UIDL/**", "/frontend/**", "/images/**", "/icons/**", "/favicon.ico", "/login"
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

        // Configure form login with role-based success handler
        http.formLogin(formLogin -> {
            formLogin
                .loginPage("/login")
                .successHandler(new RoleBasedAuthenticationSuccessHandler())  // Use role-based handler
                .permitAll();
            
            System.out.println("DEBUG: Form login configured with role-based success handler");
        });

        super.configure(http);
        return http.build();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        System.out.println("DEBUG: SecurityConfig.configure called");
        
        super.configure(http);

        http.csrf(csrf -> {
            csrf.ignoringRequestMatchers("/VAADIN/**", "/UIDL/**", "/frontend/**", "/resources/**", "/public/**", "/login");
            System.out.println("DEBUG: CSRF configuration applied");
        });

        http.headers(headers -> {
            headers.frameOptions(frameOptions -> frameOptions.disable());
            System.out.println("DEBUG: Header configuration applied");
        });

        // Deaktiviere gespeicherte Weiterleitung (z. B. zu SVGs etc.)
        http.requestCache(requestCache -> {
            requestCache.requestCache(new NullRequestCache());
            System.out.println("DEBUG: Request cache configuration applied");
        });

        // Setze LoginView + Custom Role-based Success Handler
        System.out.println("DEBUG: Setting LoginView with role-based success handler");
        setLoginView(http, LoginView.class);
        
        // Apply our custom role-based authentication success handler
        http.formLogin(formLogin -> {
            formLogin.successHandler(new RoleBasedAuthenticationSuccessHandler());
            System.out.println("DEBUG: Role-based authentication success handler applied");
        });
    }

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
