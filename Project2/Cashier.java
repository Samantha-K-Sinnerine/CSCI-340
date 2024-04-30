public class Cashier extends Thread {

    private volatile boolean isLastCashier = false;

    public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - HappyPetStore.time) + "] " + getName() + ": " + m);
    }

    /* Note: There is an error with my serveNextCustomer() method.
    Suppose there is only 1 customer in the cashierLine. 2 cashiers will evaluate the if statement to be true but only
    1 cashier will remove and serve the customer. This leads to the ArrayIndexOutOfBoundsException when the other cashier attempts to
    remove an element from an empty Vector. I have added a try/catch block to handle this critical section of code.
    However, there is still no mutual exclusion.
    */

    public void serveCustomer() {
        msg("Serving Customer");
        HappyPetStore.cashier.release(); // Signal newly available cashier to customer
    }

    public void run() {
        // While there are still customers left in the store, keep serving them
        while (HappyPetStore.remainingCustomers != 0) {
            // Cashier waits until there is a customer waiting in line
            try {
                HappyPetStore.cashierWait.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            serveCustomer();
        }
        // Cashier leaves if all customers leave
        System.out.println(getName() + " is leaving.");
        if (lastCashierLeaving()) {
            // Last cashier signals Adoption Clerk to leave
            AdoptionClerk.adoptionClerkLeave.release();
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


