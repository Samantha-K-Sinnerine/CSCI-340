# Happy Pet Store Thread Project

## Description

This project simulates the operations of the Happy Pet store during a pet adoption event using Java threads and synchronization methods provided by the Thread class. The scenario involves customers arriving at the store, shopping for pet supplies, and potentially adopting pets. Cashiers and an adoption clerk assist customers in a coordinated manner.

## Project Structure

The project is structured into multiple threads representing different roles within the store:

- **Customers**: Simulated by individual threads, each representing a customer. Customers commute to the store, shop for pet supplies, and may adopt pets.
- **Cashiers**: Multiple threads representing cashiers who assist customers in the checkout process.
- **Adoption Clerk**: A single thread representing the adoption clerk who manages the pet adoption process.

## Operations

1. **Customer Arrival and Shopping**:
   - Customers commute to the store, simulated by a random sleep time.
   - Upon arrival, customers decide whether to shop for pet supplies only or also consider adopting pets.
   - If shopping, customers rush to complete their shopping tasks and then wait in line at the cashier.

2. **Checkout Process**:
   - Cashiers assist customers in a first-come-first-serve (FCFS) order.
   - A single queue is maintained using a Vector to handle multiple cashiers efficiently.

3. **Pet Adoption**:
   - Customers interested in adopting pets may enter the adoption area after waiting.
   - Only a limited number of visitors are allowed in the adoption area to prevent congestion.
   - The adoption clerk announces waiting customers in FCFS order using interrupts.
   - Customers decide whether to adopt pets randomly.
   - If a pet is adopted, the adoption clerk updates the number of available pets.
   - Once all pets are adopted, no more visitors are allowed in the adoption area.

4. **Post-Adoption Formalities**:
   - Adopting customers complete necessary forms, simulated by a sleep of random time.
   - Customers take a break at the coffee center before leaving.

5. **Customer Departure**:
   - Adopting customers leave in decreasing order based on their arrival sequence.
   - Cashiers and the adoption clerk leave after all customers have departed.

## Synchronization Methods Used 

- `start()`: Start thread execution.
- `sleep(random time)`: Simulate delays for commuting, shopping, waiting, etc.
- `join()`: Ensure orderly departure of customers and store staff.
- `yield()`: Allow threads to pause and give way to others.
- `isAlive()`: Check if a thread is still active.
- `getPriority()` and `setPriority()`: Adjust thread priorities if necessary.
- `interrupt()`: Signal waiting customers or handle interruptions in the adoption area.
  
Project2 utilizes semaphores for synchronization. Below are the key semaphore methods used:

- `acquire()`: Acquire a permit from the semaphore. If no permit is available, the calling thread will block until one is available.
- `release()`: Release a permit, increasing the number of available permits.

