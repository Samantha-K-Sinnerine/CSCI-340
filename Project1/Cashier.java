import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
public class Cashier extends Thread {
    public static Vector<Customer> cashierLine = new Vector<>();
    private AtomicBoolean servingCustomer = new AtomicBoolean();
    public Cashier() {
        servingCustomer.set(false);
    }

    public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - HappyPetStore.time) + "] " + getName() + ": " + m);
    }

    /* Note: There is an error with my serveNextCustomer() method.
    Suppose there is only 1 customer in the cashierLine. 2 cashiers will evaluate the if statement to be true but only
    1 cashier will remove and serve the customer. This leads to the ArrayIndexOutOfBoundsException when the other cashier attempts to
    remove an element from an empty Vector. I have added a try/catch block to handle this critical section of code.
    However, there is still no mutual exclusion.
    */

    public void serveNextCustomer() {
        // Check if the cashier is available and there are customers in line
        if (!servingCustomer.get() && !Cashier.cashierLine.isEmpty()) {
            servingCustomer.set(true); // Set the status of the cashier to serving
            try{
                Customer nextCustomer = Cashier.cashierLine.remove(0);
                msg("Serving " + nextCustomer.getName());
                nextCustomer.setServiced(true);

            } catch (ArrayIndexOutOfBoundsException e){
                msg("will not be serving."); // Handle an empty queue scenario
            }
            finally{
                servingCustomer.set(false); // Reset the status of the cashier to not serving
            }
        }
    }

    public void run() {
        // While there are still customers left in the store, keep serving them
        while (HappyPetStore.customersLeft.get() > 0) {
            serveNextCustomer();
        }
        // Cashier leaves if all customers leave
        System.out.println(getName() + " is leaving.");
        // Signal the Adoption Clerk to leave
        if (lastCashierLeaving()) {
            AdoptionClerk.canLeave = true;
        }
    }
   private boolean lastCashierLeaving() {
       // Check if this cashier is the last one alive
        for (Cashier cashier : HappyPetStore.cashiers) {
            if (cashier != this && cashier.isAlive()) {
                return false; // Another alive cashier found, not the last one
            }
        }
        return true;  // This is the last cashier alive
    }
}


