package demands;

import enums.DeliverySchema;

public record DemandAdjusted(
        String productRefNo,
        long level,
        DeliverySchema deliverySchema) {
}
