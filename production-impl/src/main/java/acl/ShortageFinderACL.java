package acl;

import entities.DemandEntity;
import entities.ProductionEntity;
import entities.ShortageEntity;
import external.CurrentStock;
import shortages.ShortagePrediction;
import shortages.ShortagePredictionRepository;
import shortages.ShortagePredictionService;
import shortages.Shortages;
import tools.ShortageFinder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShortageFinderACL {

    private static boolean toggleOn = false;

    private ShortageFinderACL() {
    }

    public static List<ShortageEntity> findShortages(LocalDate today, int daysAhead, CurrentStock stock,
                                                     List<ProductionEntity> productions, List<DemandEntity> demands) {
        List<ShortageEntity> oldCalculation = ShortageFinder.findShortages(today, daysAhead, stock, productions, demands);

        try {
            if (toggleOn) {
                ShortagePredictionLegacyBasedRepository repository = new ShortagePredictionLegacyBasedRepository(stock, productions, demands);
                Shortages newCalculation = newCalculation(today, daysAhead, repository);
                var oldShortages = toMap(oldCalculation);
                var newShortages = toMap(newCalculation.build());
                if (oldShortages.equals(newShortages)) {
                    // log success
                } else {
                    Diff diff = diff(oldShortages, newShortages);
                    // log scenario minimum: productRefNo, today, daysAhead
                    ShortagePrediction prediction = repository.get(today, daysAhead);
                    // log scenario optimum: ShortagePrediction
                    // log scenario maximum: LocalDate today, int daysAhead, CurrentStock stock,
                    // List<ProductionEntity> productions, List<DemandEntity> demands
                    // log miss match, oldShortages, newShortages, diff
                }
            }
        } catch (Exception e) {
            // log exception
        }
        return oldCalculation;
    }

    record Diff(Map<LocalDate, Long> extra, Map<LocalDate, Long> missing) {
    }

    private static Diff diff(Map<LocalDate, Long> oldShortages, Map<LocalDate, Long> newShortages) {
        Map<LocalDate, Long> missingInNew = new HashMap<>(oldShortages);
        newShortages.forEach(missingInNew::remove);
        Map<LocalDate, Long> missingInOld = new HashMap<>(newShortages);
        oldShortages.forEach(missingInNew::remove);
        return new Diff(missingInNew, missingInOld);
    }

    private static Map<LocalDate, Long> toMap(List<ShortageEntity> oldCalculation) {
        return oldCalculation.stream()
                .collect(Collectors.toMap(
                        ShortageEntity::getAtDay,
                        ShortageEntity::getMissing
                ));
    }

    private static Shortages newCalculation(LocalDate today, int daysAhead, ShortagePredictionRepository repository) {
        ShortagePredictionService service = new ShortagePredictionService(repository);
        Shortages shortages = service.predictShortages(today, daysAhead);
        return shortages;
    }

}
