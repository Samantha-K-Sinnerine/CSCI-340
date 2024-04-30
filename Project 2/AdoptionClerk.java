import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;

public class AdoptionClerk extends Thread {

    // Binary mutex semaphore to control access to the shared variable availablePets
    // Ensures mutual exclusion for operations involving availablePets
    public static Semaphore adoptedPetsMutex = new Semaphore(1, true);
    public static int availablePets = 12;

    // Counting semaphore Visitors initialized to 3 since the adoption room can have a maximum of 3 visitors at the same time.
    // Customer threads will perform wait/.acquire() and AdoptionClerk thread will signal/.release()
    public static Semaphore Visitors = new Semaphore(3, true);

    // Counting semaphore for customers who adopted.
    // Initialized to 0 since there are initially 0 customers who have adopted a pet.
    // Customer threads will perform wait/.acquire() and AdoptionClerk thread will signal/.release()
    public static Semaphore adoptedAndLeave = new Semaphore(0);
    // Counting semaphore adoptionClerkLeave initialized to 0 so the clerk waits to leave
    // AdoptionClerk thread will perform wait/.acquire() and the last Cashier thread will signal/.release()
    public static Semaphore adoptionClerkLeave = new Semaphore(0);

    public static PriorityQueue<Customer> adoptedCustomers = new PriorityQueue<>((c1, c2) -> Integer.compare(c2.getCustomerNumber(), c1.getCustomerNumber()));

    private void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - HappyPetStore.time) + "] " + getName() + ": " + m);
    }

    public static void petAdopted(Customer customer) {
        try {
            adoptedPetsMutex.acquire(); // Acquire mutex for availablePets
            availablePets--; // Decrement availablePets variable
            adoptedPetsMutex.release(); // Release mutex after decrementing
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Adoption Clerk signals new available spot in adoption room
        Visitors.release();

        AdoptionClerk.adoptedCustomers.add(customer);

        // Update the number of remaining customers
        try {
            HappyPetStore.RemainingMutex.acquire(); // Acquire mutex for customersLeft
            HappyPetStore.remainingCustomers--;
            HappyPetStore.RemainingMutex.release(); // Release mutex after decrementing value
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public void run() {
        while (true) {
            if (availablePets == 0 || HappyPetStore.remainingCustomers == 0) {
                // Signals until there are no more customers left in the priority queue
                while (!adoptedCustomers.isEmpty()) {
                    // Signal each customer who adopted
                    adoptedAndLeave.release();
                    Customer nextToLeave = adoptedCustomers.poll(); // Retrieve the next customer to leave
                    nextToLeave.adoptedAndLeave = true;

                    try {
                        if (nextToLeave != null && nextToLeave.isAlive()) {
                            nextToLeave.join(); // Wait for the next customer to leave
                            nextToLeave.adoptedAndLeave = true;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            // Wait for all cashiers to leave before allowing the adoption clerk to leave
            try {
                adoptionClerkLeave.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

