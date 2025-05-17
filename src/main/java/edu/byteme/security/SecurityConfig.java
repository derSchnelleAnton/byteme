package edu.byteme.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import edu.byteme.views.accountstuff.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends VaadinWebSecurity {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;


    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        // Ignoriert CSRF für Vaadin-Ressourcen
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/VAADIN/**", "/UIDL/**", "/frontend/**", "/resources/**", "/public/**"));

        // Frame-Optionen deaktivieren, falls eine embedded Anwendung dies benötigt (z. B. H2 Console)
        http.headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable()));

        // Login-View einstellen (Vaadin-Integration)
        setLoginView(http, LoginView.class);
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
