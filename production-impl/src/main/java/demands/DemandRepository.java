package demands;

import dao.DemandDao;
import entities.DemandEntity;

import java.time.Clock;
import java.util.ArrayList;

public class DemandRepository {

    private DemandDao demandDao;
    private Events events;
    private Clock clock;

    public Demand get(DemandId id) {
        DemandEntity data = demandDao.getCurrent(id.productRefNo(), id.date());

        return new Demand(data, new ArrayList<>(), clock);
    }

    public void save(Demand demand) {
        demand.events.forEach(event -> events.publish(event));
    }
}
