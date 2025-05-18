package edu.byteme.data.repositories;

/*
 * spring data repository for Client
 */

import edu.byteme.data.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // Required for cart
    @Query("SELECT c FROM Client c LEFT JOIN FETCH c.orders WHERE c.userName = :userName")
    Optional<Client> findByUserNameWithOrders(@Param("userName") String userName);
}
