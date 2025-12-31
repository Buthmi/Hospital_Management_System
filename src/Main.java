import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        BlockingQueue<Patient> pediatricsQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Patient> surgeryQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Patient> cardiologyQueue = new LinkedBlockingQueue<>();

        HospitalStatistics statistics = new HospitalStatistics();

        PatientArrivalSimulator arrivalSimulator =
                new PatientArrivalSimulator(
                        pediatricsQueue,
                        surgeryQueue,
                        cardiologyQueue,
                        statistics);

        Thread arrivalThread = new Thread(arrivalSimulator);
        arrivalThread.start();

        ShiftManager shiftManager =
                new ShiftManager(
                        pediatricsQueue,
                        surgeryQueue,
                        cardiologyQueue,
                        statistics);

        // Start Day shift
        shiftManager.startShift("DAY");

        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        // Rotate shifts every 10 seconds (simulation)
        scheduler.scheduleAtFixedRate(() -> {
            shiftManager.endShift();
            shiftManager.startShift("NIGHT");
        }, 10, 10, TimeUnit.SECONDS);

        // Run simulation for 30 seconds
        Thread.sleep(30000);

        // Stop everything cleanly
        arrivalSimulator.stop();
        arrivalThread.interrupt();
        scheduler.shutdownNow();
        shiftManager.endShift();

        statistics.printStatistics();
        System.out.println("\nSystem shutdown completed.");
    }
}
