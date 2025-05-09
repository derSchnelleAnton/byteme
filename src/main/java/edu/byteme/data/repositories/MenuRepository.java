package edu.byteme.data.repositories;

/*
 * spring data repository for MenuItem
 */

import edu.byteme.data.entities.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<MenuItem, Integer> {
    java.util.List<MenuItem> findByIsAvailableTrue();
}
