package shortages;

import java.time.LocalDate;
import java.util.Map;

class ProductionOutputs {
    private final Map<LocalDate, Long> outputs;

    ProductionOutputs(Map<LocalDate, Long> outputs) {
        this.outputs = outputs;
    }

    long outputsFor(LocalDate day) {
        return outputs.getOrDefault(day, 0L);
    }
}
