import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Represents a consultant doctor who processes patients from a queue.
 * Implements Runnable to work as a consumer thread in the producer-consumer pattern.
 */
public class Consultant implements Runnable {
    private final String consultantId;
    private final String consultantName;
    private final Patient.Speciality speciality;
    private final PatientQueue queue;
    private final String shift;
    private final Random random;
    private int patientsProcessed = 0;
    private static final int MIN_TREATMENT_TIME_MS = 200;
    private static final int MAX_TREATMENT_TIME_MS = 800;

    /**
     * Creates a new consultant.
     * @param consultantId unique identifier for the consultant
     * @param consultantName consultant's full name
     * @param speciality medical speciality of this consultant
     * @param queue patient queue for this speciality
     * @param shift shift type (DAY or NIGHT)
     */
    public Consultant(String consultantId, String consultantName,
                      Patient.Speciality speciality, PatientQueue queue, String shift) {
        this.consultantId = consultantId;
        this.consultantName = consultantName;
        this.speciality = speciality;
        this.queue = queue;
        this.shift = shift;
        this.random = new Random();
    }

    /**
     * Main thread execution: continuously processes patients until interrupted.
     * Demonstrates consumer behavior in producer-consumer pattern.
     */
    @Override
    public void run() {
        System.out.printf("[%s] Consultant %s (%s | %s) started shift%n",
                shift, consultantName, consultantId, speciality.getDisplayName());

        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Blocks here if queue is empty - demonstrates thread-safe waiting
                Patient patient = queue.takePatient();
                treatPatient(patient);
                patientsProcessed++;
            }
        } catch (InterruptedException e) {
            // Graceful shutdown when shift ends
            System.out.printf("[%s] Consultant %s (%s) ending shift | Processed: %d patients%n",
                    shift, consultantName, consultantId, patientsProcessed);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Treats a patient and calculates wait times.
     * Simulates treatment with random duration and checks 4-hour target.
     * @param patient the patient to treat
     */
    private void treatPatient(Patient patient) {
        LocalDateTime start = LocalDateTime.now();
        Duration waitTime = Duration.between(patient.getArrivalTime(), start);

        System.out.printf("[%s] %s (%s) treating %s | Wait Time: %d seconds | Queue: %d%n",
                shift,
                consultantName,
                consultantId,
                patient.getPatientId(),
                waitTime.getSeconds(),
                queue.getCurrentSize());

        // Simulate treatment time
        int treatmentTime = MIN_TREATMENT_TIME_MS +
                random.nextInt(MAX_TREATMENT_TIME_MS - MIN_TREATMENT_TIME_MS);

        try {
            Thread.sleep(treatmentTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Check if 4-hour target was met
        Duration totalTime = Duration.between(patient.getArrivalTime(), LocalDateTime.now());
        boolean breached = totalTime.toHours() >= 4; // 4-hour target

        System.out.printf("[%s] %s (%s) completed %s | Total Time: %d seconds %s%n",
                shift,
                consultantName,
                consultantId,
                patient.getPatientId(),
                totalTime.getSeconds(),
                breached ? "[TARGET BREACHED]" : "[WITHIN TARGET]");
    }
}