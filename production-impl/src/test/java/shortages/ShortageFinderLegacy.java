package shortages;

import entities.DemandEntity;
import entities.ProductionEntity;
import entities.ShortageEntity;
import external.CurrentStock;

import java.time.LocalDate;
import java.util.List;

public class ShortageFinderLegacy {

    private ShortageFinderLegacy() {
    }

    public static List<ShortageEntity> findShortages(LocalDate today, int daysAhead, CurrentStock stock,
                                                     List<ProductionEntity> productions, List<DemandEntity> demands) {
        ShortagePrediction prediction = createShortagePrediction(today, daysAhead, stock, productions, demands);
        return prediction.predict().build();
    }

    private static ShortagePrediction createShortagePrediction(LocalDate today, int daysAhead, CurrentStock stock, List<ProductionEntity> productions, List<DemandEntity> demands) {
        return null;
    }

}
