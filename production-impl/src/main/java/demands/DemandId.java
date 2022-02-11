package demands;

import java.time.LocalDate;

public record DemandId(String productRefNo, LocalDate date) {
}
