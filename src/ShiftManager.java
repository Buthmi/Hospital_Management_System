import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manages consultant shifts and coordinates patient treatment.
 * Creates and monitors consultant threads (consumers) for each shift.
 */
public class ShiftManager {
    private final Map<Patient.Speciality, PatientQueue> queues;
    private final List<Thread> currentShiftThreads;
    private static final long SHIFT_DURATION_MS = 12000;

    /**
     * Creates a new shift manager.
     * @param queues map of patient queues for each speciality
     */
    public ShiftManager(Map<Patient.Speciality, PatientQueue> queues) {
        this.queues = queues;
        this.currentShiftThreads = new ArrayList<>();
    }

    /**
     * Runs multiple shifts sequentially, alternating between DAY and NIGHT.
     * @param numberOfShifts total number of shifts to run
     */
    public void manageShifts(int numberOfShifts) {
        for (int shiftNumber = 1; shiftNumber <= numberOfShifts; shiftNumber++) {
            String shiftType = (shiftNumber % 2 == 1) ? "DAY" : "NIGHT";

            System.out.println("=".repeat(80));
            System.out.printf("SHIFT %d - %s SHIFT STARTING%n", shiftNumber, shiftType);
            System.out.println("=".repeat(80));

            startShift(shiftType, shiftNumber);

            // Let shift run for specified duration
            try {
                Thread.sleep(SHIFT_DURATION_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            endShift(shiftType, shiftNumber);
        }
    }

    /**
     * Starts a new shift by creating consultant threads.
     * Creates 3 consultants (one per speciality) based on shift type.
     * @param shiftType DAY or NIGHT
     * @param shiftNumber sequential shift number
     */
    private void startShift(String shiftType, int shiftNumber) {
        currentShiftThreads.clear();

        if (shiftType.equals("DAY")) {
            // Day shift consultants
            createConsultant("C001", "Dr. Alice Fernando",
                    Patient.Speciality.PAEDIATRICIAN, shiftType);

            createConsultant("C002", "Dr. Ravi Perera",
                    Patient.Speciality.SURGEON, shiftType);

            createConsultant("C003", "Dr. Nimal Silva",
                    Patient.Speciality.CARDIOLOGIST, shiftType);

        } else {
            // Night shift consultants
            createConsultant("C004", "Dr. Sarah Jayasinghe",
                    Patient.Speciality.PAEDIATRICIAN, shiftType);

            createConsultant("C005", "Dr. Kasun Wijeratne",
                    Patient.Speciality.SURGEON, shiftType);

            createConsultant("C006", "Dr. Chaminda Fernando",
                    Patient.Speciality.CARDIOLOGIST, shiftType);
        }

        System.out.printf("\n[SHIFT-MANAGER] %s Shift #%d operational with 3 concurrent consultants%n",
                shiftType, shiftNumber);

        printQueueStatus("SHIFT START");
    }

    /**
     * Creates and starts a consultant thread (consumer).
     * @param id consultant identifier
     * @param name consultant full name
     * @param speciality medical speciality
     * @param shift shift type
     */
    private void createConsultant(String id, String name,
                                  Patient.Speciality speciality, String shift) {

        PatientQueue queue = queues.get(speciality);
        Consultant consultant = new Consultant(id, name, speciality, queue, shift);

        Thread thread = new Thread(consultant, id);
        currentShiftThreads.add(thread);
        thread.start(); // Start consumer thread
    }

    /**
     * Ends the current shift by interrupting consultant threads.
     * Demonstrates graceful shutdown and handover process.
     * @param shiftType DAY or NIGHT
     * @param shiftNumber sequential shift number
     */
    private void endShift(String shiftType, int shiftNumber) {
        System.out.println("\n" + "-".repeat(80));
        System.out.printf("%s SHIFT #%d ENDING – HANDOVER IN PROGRESS%n",
                shiftType, shiftNumber);
        System.out.println("-".repeat(80));

        printQueueStatus("PRE-HANDOVER");

        // Signal all consultants to finish
        System.out.println("\n[SHIFT-MANAGER] Shift time ended - signaling consultants to finish...");
        for (Thread t : currentShiftThreads) {
            t.interrupt();
        }

        // Wait for consultants to finish gracefully
        for (Thread t : currentShiftThreads) {
            try {
                t.join(1000); // Give 1 second for graceful shutdown
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Calculate patients waiting for handover
        int totalWaiting = 0;
        for (PatientQueue q : queues.values()) {
            totalWaiting += q.getCurrentSize();
        }

        System.out.printf("\n[SHIFT-MANAGER] %s Shift #%d ended%n", shiftType, shiftNumber);
        System.out.printf("[SHIFT-MANAGER] Patients handed over to next shift: %d%n", totalWaiting);
        System.out.println("[SHIFT-MANAGER] Patient queues PRESERVED (no patient loss)\n");
    }

    /**
     * Prints current status of all patient queues.
     * @param context descriptive label for when this is printed
     */
    private void printQueueStatus(String context) {
        System.out.println("\n─ QUEUE STATUS [" + context + "] " + "─".repeat(68 - context.length()));
        for (Patient.Speciality spec : Patient.Speciality.values()) {
            PatientQueue q = queues.get(spec);
            System.out.printf("│ %-15s │ Waiting: %3d │ Total Added: %4d │ Processed: %4d │ Pending: %3d │%n",
                    spec.getDisplayName(),
                    q.getCurrentSize(),
                    q.getTotalAdded(),
                    q.getTotalProcessed(),
                    q.getPendingPatients());
        }
        System.out.println("─".repeat(88));
    }
}