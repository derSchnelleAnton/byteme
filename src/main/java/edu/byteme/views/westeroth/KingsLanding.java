package edu.byteme.views.westeroth;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import edu.byteme.data.entities.MenuItem;
import edu.byteme.data.entities.Order;
import edu.byteme.data.repositories.MenuRepository;
import edu.byteme.services.OrderService;
import edu.byteme.views.MainLayout;
import edu.byteme.views.menu.CartComponent;
import edu.byteme.views.menu.MenuListView;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

public class KingsLanding extends HorizontalLayout{
    private final CartComponent cartPanel;

    /*
     * |---------------------------------------------------------------|
     * | Horizontal layout(root)                                       |
     * |  |---------------------------------|------------------------| |
     * |  | vertical layout                 | cartComponent          | |                         
     * |  |      contains list              |   shoping cart         | |
     * |  |                                 |                        | |
     * |  |                                 |                        | |
     * |  |                                 |------------------------| |
     * |  |---------------------------------| orders                 | |
     * |  | Footer                          |                        | |
     * |  |                                 |                        | |
     * |  |_________________________________|________________________| |
     * |_______________________________________________________________|
     */


    //private Div cartPanel;
    private VerticalLayout frame;
    private MenuListView listView;
    private HorizontalLayout footer;


    public KingsLanding(CartComponent cartPanel){
        this.cartPanel = cartPanel;

        configureframe();
        configureCart();
        configureFooter(new Component() {
            // place holder
        });


    }


    private void configureCart() {
        //cartPanel = new CartComponent();
        add(cartPanel);
        // add cart here
    }


    private void configureframe() {

    }

    private void configureFooter(Component component){

    }

    public void setFooterComponent(Component component){
        footer.removeAll();
        if(component != null){
            footer.add(component);
        }
    }

    
}
