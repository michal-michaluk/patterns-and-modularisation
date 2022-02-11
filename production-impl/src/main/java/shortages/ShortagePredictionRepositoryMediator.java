package shortages;

import demands.DemandDto;
import demands.DemandsReads;
import external.CurrentStock;
import external.StockService;
import production.OutputDto;
import production.ProductionReads;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

class ShortagePredictionRepositoryMediator implements ShortagePredictionRepository {
    private final DemandsReads demands;
    private final ProductionReads productions;
    private final StockService stockService;

    public ShortagePredictionRepositoryMediator(DemandsReads demands, ProductionReads productions, StockService stockService) {
        this.demands = demands;
        this.productions = productions;
        this.stockService = stockService;
    }

    @Override
    public ShortagePrediction get(String productRefNo, LocalDate today, int daysAhead) {
        List<LocalDate> dates = Stream.iterate(today, date -> date.plusDays(1))
                .limit(daysAhead)
                .collect(toList());

        return ShortagePredictionFactory.create(
                productRefNo,
                getWarehouseStock(productRefNo),
                dates,
                createDemands(today, productRefNo),
                createProductionOutputs(productRefNo, today)
        );
    }

    private WarehouseStock getWarehouseStock(String productRefNo) {
        CurrentStock stock = stockService.getCurrentStock(productRefNo);
        return new WarehouseStock(stock.getLevel(), stock.getLocked());
    }

    private Map<LocalDate, Demands.DailyDemand> createDemands(LocalDate today, String productRefNo) {
        return demands.getDemandsApi(productRefNo, today).stream()
                .collect(toUnmodifiableMap(
                        DemandDto::date,
                        demand -> ShortagePredictionFactory.daily(
                                demand.demand(),
                                demand.schema()
                        ))
                );
    }

    private Map<LocalDate, Long> createProductionOutputs(String productRefNo, LocalDate today) {
        return Collections.unmodifiableMap(
                productions.getOutputs(productRefNo, today).stream()
                        .collect(groupingBy(
                                OutputDto::date,
                                Collectors.summingLong(OutputDto::output)
                        ))
        );
    }
}
