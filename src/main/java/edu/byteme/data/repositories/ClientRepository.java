package edu.byteme.data.repositories;

/*
 * spring data repository for Client
 */

import edu.byteme.data.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Integer> {

    // Benutzer anhand des Benutzernamens finden
    Optional<Client> findByUserName(String userName);

    // Benutzer anhand der E-Mail finden
    Optional<Client> findByEmail(String email);

    // Existenzprüfung für Benutzername
    boolean existsByUserName(String usernName);

    // Existenzprüfung für E-Mail
    boolean existsByEmail(String email);
}
