import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates random patient arrivals continuously.
 * Implements Runnable to work as a producer thread in the producer-consumer pattern.
 */
public class PatientArrival implements Runnable {
    private final Map<Patient.Speciality, PatientQueue> queues;
    private final AtomicInteger patientCounter;
    private final Random random;
    private volatile boolean running;   // volatile ensures visibility across threads
    private static final int MIN_ARRIVAL_INTERVAL_MS = 100;
    private static final int MAX_ARRIVAL_INTERVAL_MS = 250;

    /**
     * Creates a new patient arrival generator.
     * @param queues map of speciality queues to add patients to
     */
    public PatientArrival(Map<Patient.Speciality, PatientQueue> queues) {
        this.queues = queues;
        this.patientCounter = new AtomicInteger(1);
        this.random = new Random();
        this.running = true;
    }

    /**
     * Main thread execution: continuously generates patients at random intervals.
     * Demonstrates producer behavior in producer-consumer pattern.
     */
    @Override
    public void run() {
        System.out.println("[ARRIVAL] Patient arrival generator started.............");
        System.out.println();

        try {
            while (running && !Thread.currentThread().isInterrupted()) {
                generatePatient();

                // Random delay between arrivals
                int delay = MIN_ARRIVAL_INTERVAL_MS +
                        random.nextInt(MAX_ARRIVAL_INTERVAL_MS - MIN_ARRIVAL_INTERVAL_MS);
                Thread.sleep(delay);
            }
        } catch (InterruptedException e) {
            System.out.println("[ARRIVAL] Patient arrival generator interrupted - shutting down gracefully");
            Thread.currentThread().interrupt();
        }
        System.out.println("[ARRIVAL] Patient arrival generator stopped - total patients generated: " +
                (patientCounter.get() - 1));
    }

    /**
     * Generates a new patient with random speciality and adds to appropriate queue.
     */
    private void generatePatient() {
        String patientId = String.format("P%04d", patientCounter.getAndIncrement());    // Create unique patient ID

        // Randomly assign speciality
        Patient.Speciality[] specialities = Patient.Speciality.values();
        Patient.Speciality speciality = specialities[random.nextInt(specialities.length)];

        // Create patient and add to queue
        Patient patient = new Patient(patientId, speciality, LocalDateTime.now());
        PatientQueue queue = queues.get(speciality);
        queue.addPatient(patient);

        // Green colored output for patient arrivals
        System.out.printf("\u001B[32mNew patient arrived: %s -----> %s (Queue size: %d)\u001B[0m%n",
                patient.getPatientId(),
                speciality.getDisplayName(),
                queue.getCurrentSize());
    }

    /**
     * Signals the generator to stop creating new patients.
     * Called during shutdown sequence.
     */
    public void stop() {
        running = false;
    }
}