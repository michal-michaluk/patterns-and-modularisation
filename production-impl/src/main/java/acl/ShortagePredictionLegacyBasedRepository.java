package acl;

import entities.DemandEntity;
import entities.ProductionEntity;
import external.CurrentStock;
import shortages.Demands;
import shortages.ProductionOutputs;
import shortages.ShortagePrediction;
import shortages.ShortagePredictionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

class ShortagePredictionLegacyBasedRepository implements ShortagePredictionRepository {
    private CurrentStock stock;
    private List<ProductionEntity> productions;
    private List<DemandEntity> demands;

    ShortagePredictionLegacyBasedRepository(CurrentStock stock, List<ProductionEntity> productions, List<DemandEntity> demands) {
        this.stock = stock;
        this.productions = productions;
        this.demands = demands;
    }

    @Override
    public ShortagePrediction get(LocalDate today, int daysAhead) {
        List<LocalDate> dates = Stream.iterate(today, date -> date.plusDays(1))
                .limit(daysAhead)
                .collect(toList());

        ProductionOutputs outputs = new ProductionOutputs(productions);

        Demands demandsPerDay = new Demands(demands);
        return new ShortagePrediction(stock, dates, outputs, demandsPerDay);
    }
}
