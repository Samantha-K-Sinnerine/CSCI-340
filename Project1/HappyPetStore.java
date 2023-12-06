import java.util.concurrent.atomic.AtomicInteger;


public class HappyPetStore {
    public static long time = System.currentTimeMillis();
    public static final int num_customers = 20;
    public static final int num_cashiers = 3;
    // Vector to simulate the customer line
    public static Customer[] customers = new Customer[num_customers];
    public static Cashier[] cashiers = new Cashier[num_cashiers];
    public static AtomicInteger customersLeft = new AtomicInteger(20);
    public static AdoptionClerk adoptionClerk = new AdoptionClerk();

    public static void main(String[] args) {

        // Set up and start the Adoption Clerk thread
        adoptionClerk.setName("AdoptionClerk");
        adoptionClerk.start();

        // Create and start threads for each customer
        for (int i = 0; i < num_customers; i++) {
            customers[i] = new Customer();
            customers[i].setName("Customer-" + (i));
            customers[i].setCustomerNumber(i);
            customers[i].start();
        }

        // Create and start threads for each cashier
        for (int i = 0; i < num_cashiers; i++) {
            cashiers[i] = new Cashier();
            cashiers[i].setName("Cashier-" + (i));
            cashiers[i].start();
        }
    }
}