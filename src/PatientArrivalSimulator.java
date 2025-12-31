import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class PatientArrivalSimulator implements Runnable {

    private final BlockingQueue<Patient> pediatricsQueue;
    private final BlockingQueue<Patient> surgeryQueue;
    private final BlockingQueue<Patient> cardiologyQueue;
    private final HospitalStatistics statistics;

    private final Random random = new Random();
    private volatile boolean running = true;
    private int patientId = 1;

    public PatientArrivalSimulator(
            BlockingQueue<Patient> pediatricsQueue,
            BlockingQueue<Patient> surgeryQueue,
            BlockingQueue<Patient> cardiologyQueue,
            HospitalStatistics statistics) {

        this.pediatricsQueue = pediatricsQueue;
        this.surgeryQueue = surgeryQueue;
        this.cardiologyQueue = cardiologyQueue;
        this.statistics = statistics;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        try {
            while (running) {
                Thread.sleep(500 + random.nextInt(1000));

                Speciality speciality =
                        Speciality.values()[random.nextInt(3)];

                Patient patient = new Patient(patientId++, speciality);
                statistics.recordArrival();

                switch (speciality) {
                    case PEDIATRICS -> pediatricsQueue.put(patient);
                    case SURGERY -> surgeryQueue.put(patient);
                    case CARDIOLOGY -> cardiologyQueue.put(patient);
                }

                System.out.println("New patient arrived: "
                        + patient.getId() + " (" + speciality + ")");
            }
        } catch (InterruptedException ignored) {
        }
    }
}
