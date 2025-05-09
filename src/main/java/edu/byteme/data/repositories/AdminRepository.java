package edu.byteme.data.repositories;

/*
 * spring data repository for Admin
 */

import edu.byteme.data.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Integer> {

}
