package acl;

import entities.DemandEntity;
import entities.ProductionEntity;
import external.CurrentStock;
import shortages.*;
import tools.Util;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

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

        ProductionOutputs outputs = createProductionOutputs();

        Demands demandsPerDay = createDemands();
        return new ShortagePrediction(stock, dates, outputs, demandsPerDay);
    }

    private Demands createDemands() {
        return new Demands(demands.stream()
                .collect(toUnmodifiableMap(
                        DemandEntity::getDay,
                        demand -> Demands.daily(
                                Util.getLevel(demand),
                                LevelOnDeliveryPick.pickStrategyVariant(Util.getDeliverySchema(demand))
                        ))
                ));
    }

    private ProductionOutputs createProductionOutputs() {
        String productRefNo = productions.stream()
                .map(production -> production.getForm().getRefNo())
                .findAny()
                .orElse(null);

        return new ProductionOutputs(productRefNo, Collections.unmodifiableMap(
                productions.stream()
                        .collect(groupingBy(
                                production -> production.getStart().toLocalDate(),
                                Collectors.summingLong(ProductionEntity::getOutput)
                        ))
        ));
    }
}
