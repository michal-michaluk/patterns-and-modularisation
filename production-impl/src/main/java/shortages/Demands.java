package shortages;

import java.time.LocalDate;
import java.util.Map;

public class Demands {
    Map<LocalDate, DailyDemand> demandsPerDay;

    public Demands(Map<LocalDate, DailyDemand> demands) {
        demandsPerDay = demands;
    }

    public static DailyDemand daily(long level, LevelOnDeliveryCalculation calculation) {
        return new DailyDemand(level, calculation);
    }

    boolean hasNoDemand(LocalDate day) {
        return !demandsPerDay.containsKey(day);
    }

    DailyDemand get(LocalDate day) {
        return demandsPerDay.getOrDefault(day, null);
    }

    public static class DailyDemand {
        private final long level;
        private final LevelOnDeliveryCalculation calculation;

        DailyDemand(long level, LevelOnDeliveryCalculation calculation) {
            this.level = level;
            this.calculation = calculation;
        }

        long getLevel() {
            return level;
        }

        long levelOnDelivery(long level, long produced) {
            return calculation.levelOnDelivery(level, produced, getLevel());
        }
    }
}
