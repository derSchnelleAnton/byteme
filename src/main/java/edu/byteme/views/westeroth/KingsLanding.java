package edu.byteme.views.westeroth;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.Component;
import edu.byteme.views.menu.CartComponent;
import edu.byteme.views.menu.LargeListComponent;
@Deprecated
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
    private LargeListComponent listView;
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
