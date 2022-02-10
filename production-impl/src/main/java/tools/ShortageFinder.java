package tools;

import entities.DemandEntity;
import entities.ProductionEntity;
import entities.ShortageEntity;
import external.CurrentStock;
import shortages.Demands;
import shortages.ProductionOutputs;
import shortages.Shortages;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ShortageFinder {

    private ShortageFinder() {
    }

    /**
     * Production at day of expected delivery is quite complex:
     * We are able to produce and deliver just in time at same day
     * but depending on delivery time or scheme of multiple deliveries,
     * we need to plan properly to have right amount of parts ready before delivery time.
     * <p/>
     * Typical schemas are:
     * <li>Delivery at prod day start</li>
     * <li>Delivery till prod day end</li>
     * <li>Delivery during specified shift</li>
     * <li>Multiple deliveries at specified times</li>
     * Schema changes the way how we calculate shortages.
     * Pick of schema depends on customer demand on daily basis and for each product differently.
     * Some customers includes that information in callof document,
     * other stick to single schema per product. By manual adjustments of demand,
     * customer always specifies desired delivery schema
     * (increase amount in scheduled transport or organize extra transport at given time)
     */
    public static List<ShortageEntity> findShortages(LocalDate today, int daysAhead, CurrentStock stock,
                                                     List<ProductionEntity> productions, List<DemandEntity> demands) {

        List<LocalDate> dates = Stream.iterate(today, date -> date.plusDays(1))
                .limit(daysAhead)
                .collect(toList());

        ProductionOutputs outputs = new ProductionOutputs(productions);
        Demands demandsPerDay = new Demands(demands);

        long level = stock.getLevel();

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
        return shortages.build();
    }
}
