package edu.byteme.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import edu.byteme.data.entities.Client;
import edu.byteme.data.repositories.ClientRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class SecurityService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private static final String LOGOUT_SUCCESS_URL = "/";

    @Autowired
    public SecurityService(ClientRepository clientRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostConstruct
    public void initialize() {
        // Initialisierungslogik nach dem Konstruktor
    }

    public UserDetails getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        return (UserDetails) authentication.getPrincipal();
    }

    public void logout() {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
        UI.getCurrent().navigate(LOGOUT_SUCCESS_URL);
    }

    public String signup(String username, String email, String rawPassword, String firstName, String lastName) {
        // Check if the username already exists in the repository
        if (clientRepository.findByUserName(username).isPresent()) {
            return "Username already exists.";
        }

        // Check if the email is already in use
        if (clientRepository.findByEmail(email).isPresent()) {
            return "Email is already registered.";
        }

        // Create a new client instance and populate the fields
        Client client = new Client();
        client.setUserName(username);
        client.setEmail(email);
        client.setPassword(passwordEncoder.encode(rawPassword)); // Encode the password securely
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setRole("USER"); // Assign the default role

        // Save the new client to the repository
        clientRepository.save(client);

        System.out.println("Signup successful: " +
                client.getUserName() + ", " +
                client.getEmail() + ", " +
                client.getFirstName() + " " + client.getLastName());

        return "Registration successful.";
    }
}
