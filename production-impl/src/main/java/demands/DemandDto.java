package demands;

import enums.DeliverySchema;

import java.time.LocalDate;

public record DemandDto(
        LocalDate date,
        long demand,
        DeliverySchema schema
) {
}
