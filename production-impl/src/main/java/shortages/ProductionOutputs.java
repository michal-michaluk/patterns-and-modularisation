package shortages;

import entities.ProductionEntity;

import java.time.LocalDate;
import java.util.*;

public class ProductionOutputs {
    private final String productRefNo;
    private final Map<LocalDate, List<ProductionEntity>> outputs;

    public ProductionOutputs(List<ProductionEntity> productions) {
        HashMap<LocalDate, List<ProductionEntity>> outputs = new HashMap<>();
        String productRefNo = null;
        for (ProductionEntity production : productions) {
            if (!outputs.containsKey(production.getStart().toLocalDate())) {
                outputs.put(production.getStart().toLocalDate(), new ArrayList<>());
            }
            outputs.get(production.getStart().toLocalDate()).add(production);
            productRefNo = production.getForm().getRefNo();
        }
        this.productRefNo = productRefNo;
        this.outputs = Collections.unmodifiableMap(outputs);
    }

    long outputsFor(LocalDate day) {
        long level = 0;
        for (ProductionEntity production : outputs.get(day)) {
            level += production.getOutput();
        }
        return level;
    }

    String getProductRefNo() {
        return productRefNo;
    }
}
