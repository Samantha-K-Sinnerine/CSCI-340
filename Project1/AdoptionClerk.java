import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.PriorityQueue;

public class AdoptionClerk extends Thread {
    private static final int MAX_VISITORS = 3;
    private static AtomicBoolean adoptionRoomOpen = new AtomicBoolean(true);
    public static AtomicInteger availablePets = new AtomicInteger(12);
    private static AtomicInteger numVisitors = new AtomicInteger(0);
    public static volatile boolean canLeave = false;
    public static Vector<Customer> waitingCustomers = new Vector<>();
    private AtomicBoolean announcingCustomer = new AtomicBoolean(false);

    public static PriorityQueue<Customer> adoptedCustomers = new PriorityQueue<>((c1, c2) -> Integer.compare(c2.getCustomerNumber(), c1.getCustomerNumber()));

    private void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - HappyPetStore.time) + "] " + getName() + ": " + m);
    }
    public static void addWaitingCustomer(Customer customer) {
        // Add a customer to the waiting list or interrupt if within the maximum visitor limit
        if (numVisitors.get() < MAX_VISITORS) {
            numVisitors.incrementAndGet();
            customer.interrupt();
        } else {
            waitingCustomers.add(customer);
        }
    }

    public static void petAdopted(Customer customer) {
        numVisitors.decrementAndGet();
        availablePets.decrementAndGet();
        AdoptionClerk.adoptedCustomers.add(customer);
        // Check if all pets are adopted or there are no customers left
        if (availablePets.decrementAndGet() == 0 || HappyPetStore.customersLeft.get() == 0){
            while (!adoptedCustomers.isEmpty()) {
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
        }
    }

    public void run() {
        while (!canLeave) {
            if (!announcingCustomer.get() && !waitingCustomers.isEmpty()) {
                announcingCustomer.set(true); // Set flag to announce customer
                Customer nextCustomer = waitingCustomers.get(0); // Get the next customer
                if (nextCustomer != null) {
                    msg("Announcing "+ nextCustomer.getName() + ", it's your turn.");
                    nextCustomer.interrupt();  // Interrupt the customer's thread to allow into adoption room
                    waitingCustomers.remove(0);
                }
                announcingCustomer.set(false);
            }
        }
    }
}
