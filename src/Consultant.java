import java.util.Random;

public class Consultant implements Runnable {

    private final String name;
    private final PatientQueue queue;
    private final Random random = new Random();

    public Consultant(String name, PatientQueue queue) {
        this.name = name;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {

                Patient patient = queue.takePatient();

                System.out.println("[TREATMENT START] " + name +
                        " treating " + patient);

                Thread.sleep(1000 + random.nextInt(2000)); // treatment time

                System.out.println("[TREATMENT END] " + name +
                        " finished " + patient);
            }
        } catch (InterruptedException e) {
            System.out.println("[SHIFT END] " + name + " going off duty.");
        }
    }
}
