package edu.byteme.security;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.byteme.data.entities.Client;
import edu.byteme.data.repositories.ClientRepository;


@Service
public class CustomUserDetailsService implements UserDetailsService {


    private final ClientRepository clientRepository;

    public CustomUserDetailsService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("DEBUG: CustomUserDetailsService.loadUserByUsername called for: " + username);
        try {
            Client client = clientRepository.findByUserName(username)
                    .orElseThrow(() -> {
                        System.out.println("DEBUG: User not found in database: " + username);
                        return new UsernameNotFoundException("Benutzer nicht gefunden: " + username);
                    });

            System.out.println("DEBUG: User found in database: " + client.getUserName() + ", role: " + client.getRole());
            
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    client.getUserName(),
                    client.getPassword(),
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_" + client.getRole()))
            );
            
            System.out.println("DEBUG: UserDetails created successfully with authorities: " + userDetails.getAuthorities());
            return userDetails;
        } catch (Exception e) {
            System.out.println("DEBUG: Unexpected error in loadUserByUsername: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


}
