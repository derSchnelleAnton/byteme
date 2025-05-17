package edu.byteme.security;

import edu.byteme.data.entities.Client;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import edu.byteme.data.repositories.ClientRepository;

import java.util.Collections;


@Service
public class CustomUserDetailsService implements UserDetailsService {


    private final ClientRepository clientRepository;

    public CustomUserDetailsService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //System.out.println("Benutzername wird geladen: " + username);
        Client client = clientRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + username));

        //System.out.println("Benutzer gefunden: " + client.getUserName());
        return new org.springframework.security.core.userdetails.User(
                client.getUserName(),
                client.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(client.getRole()))
        );
    }


}
