package demands;

import api.AdjustDemandDto;
import entities.DemandEntity;
import entities.ManualAdjustmentEntity;

import java.time.Clock;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

class Demand {
    private DemandEntity demand;
    List<Object> events;
    private Clock clock;

    Demand(DemandEntity demand, List<Object> events, Clock clock) {
        this.demand = demand;
        this.events = events;
        this.clock = clock;
    }

    void adjust(AdjustDemandDto adjustment) {
        if (adjustment.getAtDay().isBefore(LocalDate.now(clock))) {
            return; // TODO it is UI issue or reproduced post
        }
        ManualAdjustmentEntity manualAdjustment = new ManualAdjustmentEntity();
        manualAdjustment.setLevel(adjustment.getLevel());
        manualAdjustment.setNote(adjustment.getNote());
        manualAdjustment.setDeliverySchema(adjustment.getDeliverySchema());

        if (demand.getAdjustment() == null) {
            demand.setAdjustment(new LinkedList<>());
        }
        demand.getAdjustment().add(manualAdjustment);

        events.add(new DemandAdjusted(adjustment.getProductRefNo()));
    }
}
