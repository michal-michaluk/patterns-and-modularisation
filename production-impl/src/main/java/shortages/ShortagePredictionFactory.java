package shortages;

import enums.DeliverySchema;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

class ShortagePredictionFactory {

    static ShortagePrediction create(String productRefNo,
                                     WarehouseStock stock,
                                     List<LocalDate> dates,
                                     Map<LocalDate, Demands.DailyDemand> demands,
                                     Map<LocalDate, Long> outputs) {
        return new ShortagePrediction(
                productRefNo,
                stock,
                dates,
                new ProductionOutputs(outputs),
                new Demands(demands)
        );
    }

    static Demands.DailyDemand daily(long demand, DeliverySchema schema) {
        return Demands.daily(demand, LevelOnDeliveryPick.pickStrategyVariant(schema));
    }
}
