import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ShiftManager {

    private ExecutorService consultantPool;
    private final List<Consultant> consultants = new ArrayList<>();
    private final HospitalStatistics statistics;

    private final BlockingQueue<Patient> pediatricsQueue;
    private final BlockingQueue<Patient> surgeryQueue;
    private final BlockingQueue<Patient> cardiologyQueue;

    public ShiftManager(BlockingQueue<Patient> pediatricsQueue,
                        BlockingQueue<Patient> surgeryQueue,
                        BlockingQueue<Patient> cardiologyQueue,
                        HospitalStatistics statistics) {

        this.pediatricsQueue = pediatricsQueue;
        this.surgeryQueue = surgeryQueue;
        this.cardiologyQueue = cardiologyQueue;
        this.statistics = statistics;
    }

    public synchronized void startShift(String name) {
        System.out.println("\n--- " + name + " Shift Started ---");

        consultantPool = Executors.newFixedThreadPool(3);
        consultants.clear();

        consultants.add(new Consultant("Pediatrics Consultant",
                pediatricsQueue, statistics));
        consultants.add(new Consultant("Surgery Consultant",
                surgeryQueue, statistics));
        consultants.add(new Consultant("Cardiology Consultant",
                cardiologyQueue, statistics));

        consultants.forEach(consultantPool::submit);
    }

    public synchronized void endShift() {
        System.out.println("\n--- Shift Ending ---");

        if (consultantPool == null) return;

        consultants.forEach(Consultant::stop);
        consultantPool.shutdown();

        try {
            consultantPool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
    }
}
