package demands;

public interface Events {
    void publish(DemandAdjusted demandAdjusted);

    default void publish(Object event) {
        switch (event) {
            case DemandAdjusted adjusted -> publish(adjusted);
            case null, default -> {
            }
        }
    }
}
