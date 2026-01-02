import java.util.HashMap;
import java.util.Map;

public class HospitalSimulation {

    public static void main(String[] args) throws InterruptedException {

        Map<String, PatientQueue> queues = new HashMap<>();
        queues.put("Paediatrician", new PatientQueue("Paediatrician"));
        queues.put("Surgeon", new PatientQueue("Surgeon"));
        queues.put("Cardiologist", new PatientQueue("Cardiologist"));

        Thread patientArrivalThread = new Thread(
                new PatientArrival(queues)
        );
        patientArrivalThread.start();

        ShiftManager shiftManager = new ShiftManager(queues);
        shiftManager.start(); // runs indefinitely
    }
}
