package shortages;

import entities.DemandEntity;
import tools.Util;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demands {
    Map<LocalDate, DemandEntity> demandsPerDay;

    public Demands(List<DemandEntity> demands) {
        HashMap<LocalDate, DemandEntity> demandsPerDay = new HashMap<>();
        for (DemandEntity demand1 : demands) {
            demandsPerDay.put(demand1.getDay(), demand1);
        }
        this.demandsPerDay = Collections.unmodifiableMap(demandsPerDay);
    }

    boolean hasNoDemand(LocalDate day) {
        return !demandsPerDay.containsKey(day);
    }

    DailyDemand get(LocalDate day) {
        if (demandsPerDay.containsKey(day)) {
            return new DailyDemand(demandsPerDay.get(day));
        }
        return null;
    }

    static class DailyDemand {
        private final long level;
        private final LevelOnDeliveryCalculation calculation;

        DailyDemand(DemandEntity demand) {
            this.level = Util.getLevel(demand);
            this.calculation = LevelOnDeliveryPick.pickStrategyVariant(Util.getDeliverySchema(demand));
        }

        long getLevel() {
            return level;
        }

        long levelOnDelivery(long level, long produced) {
            return calculation.levelOnDelivery(level, produced, getLevel());
        }
    }
}
