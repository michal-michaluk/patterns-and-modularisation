package acl;

import demands.DemandAdjusted;
import demands.Events;
import shortages.ShortagePredictionService;

public class EventsMediator implements Events {

    private ShortagePredictionService shortages;

    @Override
    public void publish(DemandAdjusted event) {
        shortages.processShortagesFromLogistic(event.productRefNo());
    }
}
