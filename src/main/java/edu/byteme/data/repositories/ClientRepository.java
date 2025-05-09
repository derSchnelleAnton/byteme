package edu.byteme.data.repositories;

/*
 * spring data repository for Client
 */

import edu.byteme.data.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Integer> {

}
