import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PatientQueue {

    private final String speciality;
    private final BlockingQueue<Patient> queue;

    public PatientQueue(String speciality) {
        this.speciality = speciality;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void addPatient(Patient patient) throws InterruptedException {
        queue.put(patient);
    }

    public Patient takePatient() throws InterruptedException {
        return queue.take();
    }

    public String getSpeciality() {
        return speciality;
    }
}
