import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a patient in the hospital A&E system.
 * Immutable class containing patient details and arrival time.
 */
public class Patient {
    private final String patientId;
    private final Speciality speciality;
    private final LocalDateTime arrivalTime;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Medical specialities available in the hospital.
     */
    public enum Speciality {
        PAEDIATRICIAN("Paediatrician"),
        SURGEON("Surgeon"),
        CARDIOLOGIST("Cardiologist");

        private final String displayName;

        Speciality(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Creates a new patient with the specified details.
     * @param patientId unique identifier for the patient
     * @param speciality medical speciality required
     * @param arrivalTime time the patient arrived at A&E
     */
    public Patient(String patientId, Speciality speciality, LocalDateTime arrivalTime) {
        this.patientId = patientId;
        this.speciality = speciality;
        this.arrivalTime = arrivalTime;
    }
    public String getPatientId() {
        return patientId;
    }
    public Speciality getSpeciality() {
        return speciality;
    }
    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public String toString() {
        return String.format("Patient[ID=%s, Speciality=%s, Arrival=%s]",
                patientId, speciality.getDisplayName(), arrivalTime.format(formatter));
    }
}