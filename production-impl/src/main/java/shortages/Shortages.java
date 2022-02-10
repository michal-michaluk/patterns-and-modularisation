package shortages;

import entities.ShortageEntity;
import lombok.EqualsAndHashCode;

import java.time.Clock;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@EqualsAndHashCode
public class Shortages {
    private final String productRefNo;
    private final List<ShortageEntity> shortages = new LinkedList<>();
    private final Clock clock;

    public static Shortages builder(String productRefNo) {
        return new Shortages(productRefNo, Clock.systemDefaultZone());
    }

    Shortages(String productRefNo, Clock clock) {
        this.productRefNo = productRefNo;
        this.clock = clock;
    }

    public void missing(LocalDate day, long levelOnDelivery) {
        ShortageEntity entity = new ShortageEntity();
        entity.setRefNo(productRefNo);
        entity.setFound(LocalDate.now(clock));
        entity.setAtDay(day);
        entity.setMissing(Math.abs(levelOnDelivery));
        shortages.add(entity);
    }

    public List<ShortageEntity> build() {
        return shortages;
    }
}
