
import java.util.Random;


public class Customer extends Thread {
    private Random random = new Random();
    private int customerNumber;
    private boolean serviced = false;
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
        int currentPriority = getPriority();
        // Increase thread priority
        setPriority(Thread.MAX_PRIORITY);
        msg("Rushing to buy food and toys.");
        try {
            Thread.sleep(random.nextInt(3000)); // Random sleep time up to 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Reset thread priority
        setPriority(currentPriority);

        msg("Browsing the aisles.");
        try {
            Thread.sleep(random.nextInt(3000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void waitAtCashier(){
        msg("Waiting at the cashier.");
        // Add customer to the cashier line. This operation will be synchronized due to the Vector class
        Cashier.cashierLine.add(this);
        while(!serviced){};
    }

    private void waitForAdoption() {
        // If there is no pets left then the customer will leave
        if(AdoptionClerk.availablePets.get() == 0){
            msg("Adoption room is closed");
        }
        else{
            // Customer waits for the adoption clerk's signal
            AdoptionClerk.addWaitingCustomer(this);
            msg("Waiting to enter the adoption room.");
            try {
                sleep(10000); // Simulate waiting a long time
            } catch (InterruptedException e) {
                msg("Interrupted to enter the adoption room.");
                adoptPet();
            }
        }
    }

    public void customerLeave(){
        msg("is leaving.");
        // Update the counter for the number of customers left in the store
        HappyPetStore.customersLeft.decrementAndGet();
    }
    private void adoptPet() {
        try {
            sleep(random.nextInt(3000));
            msg("Checking all pets in the room.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Random random = new Random();
        int adoptOrNot = random.nextInt(10) + 1; // Random decision for pet adoption

        if (adoptOrNot < 6) {
            // Adopt a pet
            msg("Adopting a pet.");
            AdoptionClerk.petAdopted(this); // Inform the clerk about the adoption
            msg("Taking a break at coffee center.");
            this.yield();
            this.yield();
            try {
                sleep(random.nextInt(2000));
                msg("Completing forms.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Customer who adopts a pet will busy wait until all pets are adopted or until there are no more customers
            // left out of the 20
            while(!adoptedAndLeave){ };
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

    public boolean isServiced() {
        return serviced;
    }

    public void setServiced(boolean serviced) {
        this.serviced = serviced;
    }

}
