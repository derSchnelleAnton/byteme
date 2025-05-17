package edu.byteme.services;

import edu.byteme.data.entities.MenuItem;
import edu.byteme.data.repositories.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuService {

    private final MenuRepository menuRepository;

    @Autowired
    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<MenuItem> getAllItems() {
        return menuRepository.findAll();
    }

    public Optional<MenuItem> getItemById(Integer id) {
        return menuRepository.findById(id);
    }

    public MenuItem saveItem(MenuItem item) {
        return menuRepository.save(item);
    }

    public void deleteItem(Integer id) {
        menuRepository.deleteById(id);
    }

    public void setAvailability(Integer id, boolean status) {
        menuRepository.findById(id).ifPresent(item -> {
            item.setAvailable(status);
            menuRepository.save(item);
        });
    }
}
