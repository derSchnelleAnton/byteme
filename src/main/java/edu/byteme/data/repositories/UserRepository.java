package edu.byteme.data.repositories;

/*
 * enables shared access methods for Admin and Client
 */

import edu.byteme.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface UserRepository<T extends User> extends JpaRepository<T, Integer> {

    // shared user operations
    boolean findByUserName(String userName);
    boolean findByEmail(String email);
}
