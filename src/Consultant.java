import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Consultant implements Runnable {

    private final String name;
    private final BlockingQueue<Patient> queue;
    private final HospitalStatistics statistics;
    private volatile boolean working = true;

    public Consultant(String name,
                      BlockingQueue<Patient> queue,
                      HospitalStatistics statistics) {
        this.name = name;
        this.queue = queue;
        this.statistics = statistics;
    }

    public void stop() {
        working = false;
    }

    @Override
    public void run() {
        try {
            while (working) {
                Patient patient = queue.poll(1, TimeUnit.SECONDS);
                if (patient != null) {
                    treatPatient(patient);
                }
            }
        } catch (InterruptedException ignored) {
        }
        System.out.println(name + " ended shift.");
    }

    private void treatPatient(Patient patient) throws InterruptedException {
        long waitTime = patient.getWaitingTime();

        System.out.println(name + " treating patient "
                + patient.getId() + " (" + patient.getSpeciality()
                + "), waited " + waitTime / 1000 + "s");

        Thread.sleep(1000); // simulate treatment
        statistics.recordTreatment(waitTime);
    }
}
