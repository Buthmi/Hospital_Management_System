import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class PatientArrival implements Runnable {

    private final Map<String, PatientQueue> queues;
    private final AtomicInteger patientCounter = new AtomicInteger(1);
    private final Random random = new Random();

    private final String[] specialities = {
            "Paediatrician",
            "Surgeon",
            "Cardiologist"
    };

    public PatientArrival(Map<String, PatientQueue> queues) {
        this.queues = queues;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {

                String speciality = specialities[random.nextInt(specialities.length)];
                Patient patient = new Patient(patientCounter.getAndIncrement(), speciality);

                queues.get(speciality).addPatient(patient);

                System.out.println("[ARRIVAL] " + patient);

                Thread.sleep(500 + random.nextInt(1000)); // random arrival
            }
        } catch (InterruptedException e) {
            System.out.println("[ARRIVAL] Patient generator stopped.");
        }
    }
}
