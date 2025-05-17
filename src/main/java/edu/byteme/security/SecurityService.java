package edu.byteme.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinSession;
import edu.byteme.data.entities.Address;
import edu.byteme.data.entities.Client;
import edu.byteme.data.repositories.AddressRepository;
import edu.byteme.data.repositories.ClientRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

@Component
public class SecurityService {

    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private static final String LOGOUT_SUCCESS_URL = "/";

    @Autowired
    public SecurityService(ClientRepository clientRepository,
                           AddressRepository addressRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager) {
        this.clientRepository = clientRepository;
        this.addressRepository = addressRepository;
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

    public String signup(String username, String email, String rawPassword,
                         String firstName, String lastName,
                         String postalCode, String street, Integer houseNumber, String phone) {
        // Überprüfen, ob der Benutzername bereits existiert
        if (clientRepository.findByUserName(username).isPresent()) {
            return "Username already exists.";
        }

        // Überprüfen, ob die E-Mail bereits verwendet wird
        if (clientRepository.findByEmail(email).isPresent()) {
            return "Email is already registered.";
        }

        // Erstelle ein neues Benutzerobjekt
        Client client = new Client();
        client.setUserName(username);
        client.setEmail(email);
        client.setPassword(passwordEncoder.encode(rawPassword)); // Passwort verschlüsseln
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setRole("USER"); // Standardrolle

        // Optionale Adresse hinzufügen, falls vollständig
        if (postalCode != null && street != null && houseNumber != null && phone != null) {
            Address address = new Address();
            address.setPostalCode(postalCode);
            address.setStreet(street);
            address.setHouseNumber(houseNumber);
            address.setPhone(phone);

            Address savedAddress = addressRepository.save(address);
            client.setAddress(savedAddress);
        }

        // Speichere den neuen Benutzer
        clientRepository.save(client);

        // Benutzer automatisch einloggen
        autoLogin(username, rawPassword);

        System.out.println("User signed up and logged in: " + username);

        // Optional: Erfolgsnachricht kann angezeigt werden, wenn nötig
        return "Registration and login successful.";
    }

    private void autoLogin(String username, String password) {
        try {
            // Authentifiziere den Benutzer
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = authenticationManager.authenticate(authToken);

            // Setze die Authentifizierung im SecurityContext
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);

            // Synchronisieren des SecurityContext mit Vaadin HTTP-Session
            VaadinSession.getCurrent().access(() -> {
                VaadinSession.getCurrent().getSession().setAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        securityContext
                );
            });

                 UI.getCurrent().getPage().setLocation("/");
        } catch (Exception e) {
                    System.out.println("Error during auto-login: " + e.getMessage());
        }
    }
}
