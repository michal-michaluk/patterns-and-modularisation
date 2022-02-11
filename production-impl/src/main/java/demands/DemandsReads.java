package demands;

import dao.DemandDao;
import tools.Util;

import java.time.LocalDate;
import java.util.List;

public class DemandsReads {
    private final DemandDao demandDao;

    public DemandsReads(DemandDao demandDao) {
        this.demandDao = demandDao;
    }

    public List<DemandDto> getDemandsApi(String productRefNo, LocalDate today) {
        return demandDao.findFrom(today.atStartOfDay(), productRefNo).stream()
                .map(demand -> new DemandDto(
                        demand.getDay(),
                        Util.getLevel(demand),
                        Util.getDeliverySchema(demand)
                ))
                .toList();
    }
}
