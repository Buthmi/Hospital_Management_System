public class Patient {

    private final int id;
    private final Speciality speciality;
    private final long arrivalTime;

    public Patient(int id, Speciality speciality) {
        this.id = id;
        this.speciality = speciality;
        this.arrivalTime = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public Speciality getSpeciality() {
        return speciality;
    }

    public long getWaitingTime() {
        return System.currentTimeMillis() - arrivalTime;
    }
}
