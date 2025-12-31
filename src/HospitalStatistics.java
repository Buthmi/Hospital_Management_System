import java.util.concurrent.atomic.AtomicInteger;

public class HospitalStatistics {

    private final AtomicInteger arrived = new AtomicInteger();
    private final AtomicInteger treated = new AtomicInteger();
    private final AtomicInteger treatedWithinTarget = new AtomicInteger();

    private static final long TARGET_WAIT = 4000; // 4 seconds = 4 hours (simulated)

    public void recordArrival() {
        arrived.incrementAndGet();
    }

    public void recordTreatment(long waitTime) {
        treated.incrementAndGet();
        if (waitTime <= TARGET_WAIT) {
            treatedWithinTarget.incrementAndGet();
        }
    }

    public void printStatistics() {
        System.out.println("\n=== Hospital Statistics ===");
        System.out.println("Total arrived : " + arrived.get());
        System.out.println("Total treated : " + treated.get());

        if (treated.get() > 0) {
            double percentage =
                    (treatedWithinTarget.get() * 100.0) / treated.get();
            System.out.printf("Seen within target: %.2f%%\n", percentage);
        }
    }
}
