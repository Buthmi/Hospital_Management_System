import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShiftManager {
    private final Map<String, PatientQueue> queues;
    private final List<Thread> activeConsultants = new ArrayList<>();

    private static final int SHIFT_DURATION_MS = 12_000;

    public ShiftManager(Map<String, PatientQueue> queues) {
        this.queues = queues;
    }

    public void start() throws InterruptedException {
        boolean dayShift = true;

        while (true) {
            String shiftName = dayShift ? "DAY" : "NIGHT";
            System.out.println("\n=== " + shiftName + " SHIFT START ===");

            startConsultants(shiftName);
            Thread.sleep(SHIFT_DURATION_MS);

            stopConsultants();
            System.out.println("=== " + shiftName + " SHIFT END ===\n");

            dayShift = !dayShift;
        }
    }

    private void startConsultants(String shiftName) {
        activeConsultants.clear();

        for (String speciality : queues.keySet()) {
            Consultant consultant = new Consultant(
                    shiftName + "-" + speciality,
                    queues.get(speciality)
            );
            Thread t = new Thread(consultant);
            activeConsultants.add(t);
            t.start();
        }
    }
    private void stopConsultants() {
        for (Thread t : activeConsultants) {
            t.interrupt();
        }
    }
}
