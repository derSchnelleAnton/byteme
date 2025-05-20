package data;

import edu.byteme.data.entities.*; // Assuming these are your entity classes
import org.junit.jupiter.api.BeforeEach; // JUnit 5: Runs before *each* test method
import org.junit.jupiter.api.Test;      // JUnit 5: Marks a test method
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*; // JUnit 5 assertions

public class TestData {

    // Declare instances for each test to ensure fresh state
    private Order order;
    private MenuItem menuItem;
    private Client client;
    private Admin admin;
    private LocalDateTime testDateTime;

    // This method runs before every single test method in this class
    @BeforeEach
    public void setUp() {
        // Initialize objects for a fresh test context
        testDateTime = LocalDateTime.now();

        // 1. Address
        Address address = new Address();
        address.setHouseNumber(50);
        address.setStreet("Bahnhofstreet");

        // 2. Client
        client = new Client();
        client.setAddress(address);
        client.setFirstName("John");
        client.setLastName("Doe");

        // 3. Admin
        admin = new Admin();
        admin.setFirstName("Muster");
        admin.setLastName("Mustermann");

        // 4. Menu Item
        menuItem = new MenuItem();
        menuItem.setPrice(2.5);
        menuItem.setName("Test_menu_item");
        menuItem.setDescription("Test_menu_item_description");

        // 5. Order
        order = new Order();
        order.setOrderDate(testDateTime);
        order.addMenuItem(menuItem); // Add one menu item
        order.setClient(client);
        order.setAdmin(admin);
    }
    @Test
    void OrderAddress(){
        assertNotNull(order.getClient().getAddress(),"Address is null, should not be.");
        assertEquals("Bahnhofstreet", order.getClient().getAddress().getStreet(),"Address's house number is not equal.");
        assertEquals(50, order.getClient().getAddress().getHouseNumber(),"Address's house number is not correctly set.");
    }
    @Test
    void orderClient() {
        assertEquals(client, order.getClient(), "Setting client to order and retrieving it fails.");
    }

    @Test
    void orderAdmin() {
        assertEquals(admin, order.getAdmin(), "Setting admin to order and retrieving it fails..");
    }

    @Test
    void orderDate() {
        assertEquals(testDateTime, order.getOrderDate(), "Failed on Order date.");
    }

    @Test
    void orderMenuItems() {
        assertFalse(order.getMenuItems().isEmpty(), "Menu List empty.");
        assertEquals(1, order.getMenuItems().size(), "Menu item contents dont match.");
        assertEquals("Test_menu_item", order.getMenuItems().get(0).getName(), "Menu Item name doesn't match.");
        assertEquals(2.5, order.getMenuItems().get(0).getPrice(), 0.001, "The first menu item price should match.");
    }

    @Test
    void addingMenuItemsToOrder() {
        MenuItem anotherMenuItem = new MenuItem();
        anotherMenuItem.setName("Another Item");
        anotherMenuItem.setPrice(10.0);
        order.addMenuItem(anotherMenuItem);

        assertEquals(2, order.getMenuItems().size(), "adding another menu Item failed.");
        assertTrue(order.getMenuItems().contains(menuItem), "Lost first menu Item");
        assertTrue(order.getMenuItems().contains(anotherMenuItem), "Last added Menu Item Lost");
    }


    @Test
    void OrderClientDelete() {
        assertEquals("John",order.getClient().getFirstName(),"Client added to order has a wrong property");
        order.setClient(null);
        assertNull(order.getClient(), "Order should be able to have null value for Client if client is deleted");
    }

    @Test
    void orderAdminDelete() {
        assertEquals("Mustermann",order.getAdmin().getLastName(),"Admin added to order has a wrong property");
        order.setAdmin(null);
        assertNull(order.getAdmin(), "Order should be able to have null value for Admin if admin is deleted or not set");
    }
}