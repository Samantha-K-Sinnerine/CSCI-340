import java.util.Vector;
import java.util.concurrent.Semaphore;


public class HappyPetStore {
    public static long time = System.currentTimeMillis();
    public static final int num_customers = 20;
    public static final int num_cashiers = 3;
    public static Vector<Customer> cashierLine = new Vector<>();
    // Array to hold customer threads
    public static Customer[] customers = new Customer[num_customers];
    // Array to hold cashier threads
    public static Cashier[] cashiers = new Cashier[num_cashiers];
    // Counting semaphore cashier initialized to 3 since there are 3 cashiers available at the start.
    // Customer threads will perform wait/.acquire() and Cashier threads will signal/.release()
    public static Semaphore cashier =new Semaphore(3, true);
    // Counting semaphore customersWait initialized to 0 since there are no customers are waiting at the start.
    // Cashier threads will perform wait/.acquire() and Customer threads will signal/.release()
    public static Semaphore cashierWait = new Semaphore(0);
    // Binary mutex semaphore to control access to the shared variable remainingCustomers
    // Ensures mutual exclusion for operations involving remainingCustomers
    public static Semaphore RemainingMutex = new Semaphore(1);
    /// Variable tracking the number of remaining customers (initialized to the number of customers at the start)
    public static int remainingCustomers = num_customers;
    // Create the adoption clerk thread
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