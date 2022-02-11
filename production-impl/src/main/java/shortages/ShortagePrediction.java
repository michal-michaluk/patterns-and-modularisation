package shortages;

import java.time.LocalDate;
import java.util.List;

public class ShortagePrediction {
    private final WarehouseStock stock;
    private final List<LocalDate> dates;
    private final ProductionOutputs outputs;
    private final Demands demandsPerDay;

    public ShortagePrediction(WarehouseStock stock, List<LocalDate> dates, ProductionOutputs outputs, Demands demandsPerDay) {
        this.stock = stock;
        this.dates = dates;
        this.outputs = outputs;
        this.demandsPerDay = demandsPerDay;
    }

    Shortages predict() {
        long level = stock.level();
        Shortages shortages = Shortages.builder(outputs.getProductRefNo());
        for (LocalDate day : dates) {
            if (demandsPerDay.hasNoDemand(day)) {
                level += outputs.outputsFor(day);
                continue;
            }
            long produced = outputs.outputsFor(day);
            Demands.DailyDemand demand = demandsPerDay.get(day);
            long levelOnDelivery = demand.levelOnDelivery(level, produced);

            if (levelOnDelivery < 0) {
                shortages.missing(day, levelOnDelivery);
            }
            long endOfDayLevel = level + produced - demand.getLevel();
            level = Math.max(endOfDayLevel, 0);
        }
        return shortages;
    }

    public long getLockedPartsOnStock() {
        return stock.locked();
    }
}
