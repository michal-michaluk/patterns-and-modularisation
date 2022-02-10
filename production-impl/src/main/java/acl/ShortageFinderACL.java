package acl;

import entities.DemandEntity;
import entities.ProductionEntity;
import entities.ShortageEntity;
import external.CurrentStock;
import shortages.ShortagePredictionRepository;
import shortages.ShortagePredictionService;
import shortages.Shortages;
import tools.ShortageFinder;

import java.time.LocalDate;
import java.util.List;

public class ShortageFinderACL {

    private static boolean toggleOn = false;

    private ShortageFinderACL() {
    }

    public static List<ShortageEntity> findShortages(LocalDate today, int daysAhead, CurrentStock stock,
                                                     List<ProductionEntity> productions, List<DemandEntity> demands) {
        if (toggleOn) {
            ShortagePredictionRepository repository = new ShortagePredictionLegacyBasedRepository(stock, productions, demands);
            ShortagePredictionService service = new ShortagePredictionService(repository);

            Shortages shortages = service.predictShortages(today, daysAhead);

            return shortages.build();
        } else {
            return ShortageFinder.findShortages(today, daysAhead, stock, productions, demands);
        }
    }

}
