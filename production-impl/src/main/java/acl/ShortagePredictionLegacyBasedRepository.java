package acl;

import dao.DemandDao;
import dao.ProductionDao;
import entities.DemandEntity;
import entities.ProductionEntity;
import external.CurrentStock;
import external.StockService;
import shortages.*;
import tools.Util;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

class ShortagePredictionLegacyBasedRepository implements ShortagePredictionRepository {
    private final DemandDao demandDao;
    private final StockService stockService;
    private final ProductionDao productionDao;

    public ShortagePredictionLegacyBasedRepository(DemandDao demandDao, StockService stockService, ProductionDao productionDao) {
        this.demandDao = demandDao;
        this.stockService = stockService;
        this.productionDao = productionDao;
    }

    @Override
    public ShortagePrediction get(String productRefNo, LocalDate today, int daysAhead) {
        List<LocalDate> dates = Stream.iterate(today, date -> date.plusDays(1))
                .limit(daysAhead)
                .collect(toList());

        WarehouseStock stock = getWarehouseStock(productRefNo);
        ProductionOutputs outputs = createProductionOutputs(productRefNo, today);
        Demands demands = createDemands(today, productRefNo);
        return new ShortagePrediction(stock, dates, outputs, demands);
    }

    private WarehouseStock getWarehouseStock(String productRefNo) {
        CurrentStock stock = stockService.getCurrentStock(productRefNo);
        return new WarehouseStock(stock.getLevel(), stock.getLocked());
    }

    private Demands createDemands(LocalDate today, String productRefNo) {
        List<DemandEntity> demands = demandDao.findFrom(today.atStartOfDay(), productRefNo);
        return new Demands(demands.stream()
                .collect(toUnmodifiableMap(
                        DemandEntity::getDay,
                        demand -> Demands.daily(
                                Util.getLevel(demand),
                                LevelOnDeliveryPick.pickStrategyVariant(Util.getDeliverySchema(demand))
                        ))
                ));
    }

    private ProductionOutputs createProductionOutputs(String productRefNo, LocalDate today) {
        List<ProductionEntity> productions = productionDao.findFromTime(productRefNo, today.atStartOfDay());
        return new ProductionOutputs(productRefNo, Collections.unmodifiableMap(
                productions.stream()
                        .collect(groupingBy(
                                production -> production.getStart().toLocalDate(),
                                Collectors.summingLong(ProductionEntity::getOutput)
                        ))
        ));
    }
}
