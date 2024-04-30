import java.util.Random;
import java.util.concurrent.Semaphore;


public class Customer extends Thread {
    private Random random = new Random();
    private int customerNumber;
    public boolean adoptedAndLeave = false;

    public Customer() {
    }

    public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - HappyPetStore.time) + "] " + getName() + ": " + m);
    }

    public void run() {
        Random random = new Random();
        // Customer commutes to the pet store
        try {
            int commuteTime = random.nextInt(5000);
            Thread.sleep(commuteTime);
            msg("Arrived at the pet store.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Generate a random number to decide customer's action
        int decision = random.nextInt(10) + 1;

        if (decision < 4) {
            // Customer only buys food and toys for their pets
            buyFoodAndToys();
            waitAtCashier();
            customerLeave();
        } else {
            if (decision % 2 == 0) {
                // Customer interested in adopting a pet only
                msg("Interested in adopting a pet.");
                waitForAdoption();
                customerLeave();
            } else {
                // Customer does some shopping and checks pets for adoption
                msg("Shopping and checking pets for adoption.");
                buyFoodAndToys();
                waitAtCashier();
                waitForAdoption();
                customerLeave();
            }
        }
    }
    private void buyFoodAndToys() {
        msg("Browsing the aisles.");
        try {
            Thread.sleep(random.nextInt(3000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void waitAtCashier(){
        msg("Waiting at the cashier.");

        HappyPetStore.cashierWait.release(); // Signal new customer to cashier
        try {
            HappyPetStore.cashier.acquire(); // Customer waits on available cashier

        } catch (InterruptedException e) {
            e.printStackTrace();
        };
    }

    private void waitForAdoption(){
        // If there is no pets left then the customer will leave
        if(AdoptionClerk.availablePets == 0){
            msg("Adoption room is closed");
        }
        else{
            msg("Waiting to enter the adoption room.");
            try {
                // Customer waits on available space in adoption room
                AdoptionClerk.Visitors.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            msg("Entering the adoption room.");
            adoptPet(); // Customer enters adoption room
        }
    }

    public void customerLeave(){
        msg("is leaving.");
        // Update the counter for the number of customers left in the store
        try {
            HappyPetStore.RemainingMutex.acquire(); // Acquire mutex for shared variable remainingCustomers
            HappyPetStore.remainingCustomers--;
            HappyPetStore.RemainingMutex.release(); // Release mutex after decrementing value
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void adoptPet() {
        msg("Checking all pets in the room.");
        try {
            sleep(random.nextInt(3000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Random random = new Random();
        int adoptOrNot = random.nextInt(10) + 1; // Random decision for pet adoption

        if (adoptOrNot < 6) {
            // Adopt a pet
            msg("Adopting a pet.");
            AdoptionClerk.petAdopted(this); // Inform the clerk about the adoption and update remaining pets
            msg("Taking a break at coffee center.");
            this.yield();
            this.yield();
            try {
                sleep(random.nextInt(2000));
                msg("Completing forms.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // All Customers who adopt a pet wait to leave until all pets are adopted or until there are no more customers left
            try {
                AdoptionClerk.adoptedAndLeave.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            // Do not adopt
            msg("Decided not to adopt a pet.");
        }
    }

    public void setCustomerNumber(int number) {
        this.customerNumber = number;
    }

    public int getCustomerNumber() {
        return customerNumber;
    }
}
