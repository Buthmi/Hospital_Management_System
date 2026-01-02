public class Patient {
    private final int id;
    private final String speciality;
    private final long arrivalTime;

    public Patient(int id, String speciality) {
        this.id = id;
        this.speciality = speciality;
        this.arrivalTime = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public String getSpeciality() {
        return speciality;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", speciality='" + speciality + '\'' +
                '}';
    }
}
