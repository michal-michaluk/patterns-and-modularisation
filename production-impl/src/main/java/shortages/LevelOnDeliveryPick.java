package shortages;

import enums.DeliverySchema;

import java.util.Map;

class LevelOnDeliveryPick {

    private final static Map<DeliverySchema, LevelOnDeliveryCalculation> mapping = Map.of(
            DeliverySchema.atDayStart, LevelOnDeliveryCalculation.atDayStart,
            DeliverySchema.tillEndOfDay, LevelOnDeliveryCalculation.tillEndOfDay,
            DeliverySchema.every3hours, LevelOnDeliveryCalculation.notImplemented
    );

    static LevelOnDeliveryCalculation pickStrategyVariant(DeliverySchema deliverySchema) {
        return mapping.getOrDefault(deliverySchema, LevelOnDeliveryCalculation.notImplemented);
    }
}
