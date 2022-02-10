package shortages;

import java.time.LocalDate;
import java.util.Map;

public class ProductionOutputs {
    private final String productRefNo;
    private final Map<LocalDate, Long> outputs;

    public ProductionOutputs(String productRefNo, Map<LocalDate, Long> outputs) {
        this.productRefNo = productRefNo;
        this.outputs = outputs;
    }

    long outputsFor(LocalDate day) {
        return outputs.getOrDefault(day, 0L);
    }

    String getProductRefNo() {
        return productRefNo;
    }
}
