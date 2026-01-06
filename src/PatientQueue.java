import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe queue for managing patients waiting for a specific speciality.
 * Uses LinkedBlockingQueue for thread-safe producer-consumer operations.
 */
public class PatientQueue {
    private final LinkedBlockingQueue<Patient> queue;
    private final Patient.Speciality speciality;
    private final AtomicInteger totalAdded;     // thread-safe counter
    private final AtomicInteger totalProcessed; // thread-safe counter

    /**
     * Creates a new patient queue for a specific speciality.
     * @param speciality the medical speciality this queue handles
     */
    public PatientQueue(Patient.Speciality speciality) {
        this.queue = new LinkedBlockingQueue<>(); // Thread-safe queue
        this.speciality = speciality;
        this.totalAdded = new AtomicInteger(0);
        this.totalProcessed = new AtomicInteger(0);
    }

    /**
     * Adds a patient to the queue (producer operation).
     * Thread-safe blocking operation.
     * @param patient the patient to add
     */
    public void addPatient(Patient patient) {
        try {
            queue.put(patient); // Blocking call, thread-safe
            totalAdded.incrementAndGet(); // Atomic increment
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[QUEUE-ERROR] Interrupted while adding patient: " + patient);
        }
    }

    /**
     * Removes and returns a patient from the queue (consumer operation).
     * Blocks if queue is empty until a patient becomes available.
     * Thread-safe operation.
     * @return the next patient to be treated
     * @throws InterruptedException if interrupted while waiting
     */
    public Patient takePatient() throws InterruptedException {
        Patient patient = queue.take(); // Blocking call - waits if empty
        totalProcessed.incrementAndGet(); // Atomic increment
        return patient;
    }

    public Patient.Speciality getSpeciality() {
        return speciality;
    }

    /**
     * @return current number of patients waiting in queue
     */
    public int getCurrentSize() {
        return queue.size();
    }

    /**
     * @return total number of patients added to this queue
     */
    public int getTotalAdded() {
        return totalAdded.get();
    }

    /**
     * @return total number of patients processed from this queue
     */
    public int getTotalProcessed() {
        return totalProcessed.get();
    }

    /**
     * @return number of patients who have been added but not yet processed
     */
    public int getPendingPatients() {
        return getTotalAdded() - getTotalProcessed();
    }

    @Override
    public String toString() {
        return String.format("%s Queue: Current=%d, Total Added=%d, Total Processed=%d, Pending=%d",
                speciality.getDisplayName(), getCurrentSize(), getTotalAdded(),
                getTotalProcessed(), getPendingPatients());
    }
}