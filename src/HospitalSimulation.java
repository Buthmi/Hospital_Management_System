/**
 * Author: S.A.D.Buthmi Mithara Abeysena
 * IIT ID - 20221142
 * UoW ID - w1954122
 * Created Date - 30/12/2025
 **/

import java.util.HashMap;
import java.util.Map;

/**
 * Main simulation class for Royal Manchester Hospital A&E system.
 * Orchestrates the producer-consumer threading model with patient arrivals and consultant processing.
 */
public class HospitalSimulation {
    private final Map<Patient.Speciality, PatientQueue> queues;
    private Thread patientArrivalThread;
    private PatientArrival patientArrivalGenerator;
    private static final int NUMBER_OF_SHIFTS = 2;

    /**
     * Initializes the simulation with empty queues for each speciality.
     */
    public HospitalSimulation() {
        this.queues = new HashMap<>();
        initializeQueues();
    }

    /**
     * Creates a separate queue for each medical speciality.
     */
    private void initializeQueues() {
        for (Patient.Speciality speciality : Patient.Speciality.values()) {
            queues.put(speciality, new PatientQueue(speciality));
        }
    }

    /**
     * Main simulation entry point.
     * Coordinates patient arrivals (producer), shift management (consumers), and shutdown.
     */
    public void start() {
        printWelcomeBanner();
        printHospitalConsultants();
        startPatientArrivalGenerator();     // Start continuous patient arrival generator (Producer)

        // Brief delay to allow some patients to arrive before first shift
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Start shift management system (Consumers)
        startShiftManagement();

        // Shutdown
        shutdown();

        // Print final statistics
        printFinalReport();
    }

    /**
     * Starts the patient arrival generator thread (producer).
     */
    private void startPatientArrivalGenerator() {
        patientArrivalGenerator = new PatientArrival(queues);
        patientArrivalThread = new Thread(patientArrivalGenerator, "PatientArrivalGenerator");
        patientArrivalThread.start();
    }

    /**
     * Starts the shift management system.
     * This is a blocking call that runs all shifts sequentially.
     */
    private void startShiftManagement() {
        ShiftManager shiftManager = new ShiftManager(queues);

        System.out.println("\n..........Shift management system activated..........");
        System.out.println();

        // Run shift management (blocking call - runs all shifts)
        shiftManager.manageShifts(NUMBER_OF_SHIFTS);
    }

    /**
     * Performs graceful shutdown of the simulation.
     * Stops producer thread and ensures all threads terminate properly.
     */
    private void shutdown() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("SIMULATION ENDING - INITIATING SHUTDOWN SEQUENCE");
        System.out.println("=".repeat(80));

        // Stop patient arrival generator (Producer)
        if (patientArrivalGenerator != null) {
            patientArrivalGenerator.stop();
            System.out.println("[SHUTDOWN] Stopping producer thread (patient arrivals)...");
        }

        // Wait for arrival thread to finish
        if (patientArrivalThread != null) {
            patientArrivalThread.interrupt();
            try {
                patientArrivalThread.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("[SHUTDOWN] All systems stopped gracefully");
        System.out.println("[SHUTDOWN] Thread-safe shutdown complete\n");
    }

    /**
     * Prints comprehensive final statistics for the simulation.
     * Shows per-speciality and overall performance metrics.
     */
    private void printFinalReport() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("FINAL SIMULATION REPORT - ROYAL MANCHESTER HOSPITAL A&E");
        System.out.println("=".repeat(80));

        int totalPatients = 0;
        int totalProcessed = 0;
        int totalPending = 0;

        System.out.println("\nPER-SPECIALITY STATISTICS:");
        System.out.println("-".repeat(80));

        // Iterate through each speciality and print statistics
        for (Patient.Speciality speciality : Patient.Speciality.values()) {
            PatientQueue queue = queues.get(speciality);
            System.out.printf("%-15s | Added: %4d | Processed: %4d | Pending: %4d | Current Queue: %4d%n",
                    speciality.getDisplayName(),
                    queue.getTotalAdded(),
                    queue.getTotalProcessed(),
                    queue.getPendingPatients(),
                    queue.getCurrentSize());

            totalPatients += queue.getTotalAdded();
            totalProcessed += queue.getTotalProcessed();
            totalPending += queue.getPendingPatients();
        }

        System.out.println("-".repeat(80));
        System.out.printf("TOTAL           | Added: %4d | Processed: %4d | Pending: %4d%n",
                totalPatients, totalProcessed, totalPending);

        // Calculate success metrics
        double processingRate = totalPatients > 0 ?
                (totalProcessed * 100.0 / totalPatients) : 0;

        System.out.println("\n" + "=".repeat(80));
        System.out.println("PERFORMANCE METRICS:");
        System.out.println("-".repeat(80));
        System.out.printf("Total Patients Arrived:     %d%n", totalPatients);
        System.out.printf("Total Patients Processed:   %d%n", totalProcessed);
        System.out.printf("Processing Rate:            %.2f%%%n", processingRate);
        System.out.printf("Patients Still Waiting:     %d%n", totalPending);
        System.out.printf("Number of Shifts Completed: %d%n", NUMBER_OF_SHIFTS);
    }

    /**
     * Prints welcome banner.
     */
    private void printWelcomeBanner() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ROYAL MANCHESTER HOSPITAL - A&E PATIENT MANAGEMENT SYSTEM");
        System.out.println("=".repeat(80));
        System.out.println();
    }

    /**
     * Prints hospital consultants roster showing day and night shift doctors.
     */
    private void printHospitalConsultants() {
        System.out.println("CONSULTANTS CURRENTLY EMPLOYED AT THE HOSPITAL");
        System.out.println("─".repeat(80));

        System.out.println("[DAY SHIFT - 12 hours]");
        System.out.println("  C001 – Dr. Alice Fernando       (Paediatrician)");
        System.out.println("  C002 – Dr. Ravi Perera          (Surgeon)");
        System.out.println("  C003 – Dr. Nimal Silva          (Cardiologist)");

        System.out.println("\n[NIGHT SHIFT - 12 hours]");
        System.out.println("  C004 – Dr. Sarah Jayasinghe     (Paediatrician)");
        System.out.println("  C005 – Dr. Kasun Wijeratne      (Surgeon)");
        System.out.println("  C006 – Dr. Chaminda Fernando    (Cardiologist)");

        System.out.println("-".repeat(80));
        System.out.println();
    }

    /**
     * Main entry point for the simulation.
     */
    public static void main(String[] args) {
        HospitalSimulation simulation = new HospitalSimulation();
        simulation.start();

        System.out.println("\nThank you for using Royal Manchester Hospital Management System.");
        System.out.println("System shutdown complete.\n");
    }
}