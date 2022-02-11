package acl;

import demands.DemandDto;
import demands.DemandsReads;
import external.CurrentStock;
import external.StockService;
import production.OutputDto;
import production.ProductionReads;
import shortages.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

class ShortagePredictionLegacyBasedRepository implements ShortagePredictionRepository {
    private final DemandsReads demands;
    private final ProductionReads productions;
    private final StockService stockService;

    public ShortagePredictionLegacyBasedRepository(DemandsReads demands, ProductionReads productions, StockService stockService) {
        this.demands = demands;
        this.productions = productions;
        this.stockService = stockService;
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
        return new Demands(
                demands.getDemandsApi(productRefNo, today).stream()
                        .collect(toUnmodifiableMap(
                                DemandDto::date,
                                demand -> Demands.daily(
                                        demand.demand(),
                                        LevelOnDeliveryPick.pickStrategyVariant(demand.schema())
                                ))
                        ));
    }

    private ProductionOutputs createProductionOutputs(String productRefNo, LocalDate today) {
        return new ProductionOutputs(productRefNo, Collections.unmodifiableMap(
                productions.getOutputs(productRefNo, today).stream()
                        .collect(groupingBy(
                                OutputDto::date,
                                Collectors.summingLong(OutputDto::output)
                        ))
        ));
    }
}
