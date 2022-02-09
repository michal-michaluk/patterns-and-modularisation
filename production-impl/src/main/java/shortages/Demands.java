package shortages;

import entities.DemandEntity;
import enums.DeliverySchema;
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

    public boolean hasNoDemand(LocalDate day) {
        return !demandsPerDay.containsKey(day);
    }

    public DailyDemand get(LocalDate day) {
        if (demandsPerDay.containsKey(day)) {
            return new DailyDemand(demandsPerDay.get(day));
        }
        return null;
    }

    public static class DailyDemand {
        private final long level;
        private final DeliverySchema schema;

        public DailyDemand(DemandEntity demand) {
            this.level = Util.getLevel(demand);
            this.schema = Util.getDeliverySchema(demand);
        }

        public long getLevel() {
            return level;
        }

        private DeliverySchema getDeliverySchema() {
            return schema;
        }

        public long levelOnDelivery(long level, long produced) {
            if (getDeliverySchema() == DeliverySchema.atDayStart) {
                return level - getLevel();
            } else if (getDeliverySchema() == DeliverySchema.tillEndOfDay) {
                return level - getLevel() + produced;
            } else if (getDeliverySchema() == DeliverySchema.every3hours) {
                // TODO WTF ?? we need to rewrite that app :/
                throw new UnsupportedOperationException();
            } else {
                // TODO implement other variants
                throw new UnsupportedOperationException();
            }
        }
    }
}
