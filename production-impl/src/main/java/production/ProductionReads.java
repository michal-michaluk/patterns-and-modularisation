package production;

import dao.ProductionDao;

import java.time.LocalDate;
import java.util.List;

public class ProductionReads {
    private final ProductionDao productionDao;

    public ProductionReads(ProductionDao productionDao) {
        this.productionDao = productionDao;
    }

    public List<OutputDto> getOutputs(String productRefNo, LocalDate today) {
        List<OutputDto> productions = productionDao.findFromTime(productRefNo, today.atStartOfDay())
                .stream()
                .map(production -> new OutputDto(production.getStart().toLocalDate(), production.getOutput()))
                .toList();
        return productions;
    }
}
