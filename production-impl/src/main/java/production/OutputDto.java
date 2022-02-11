package production;

import java.time.LocalDate;

public record OutputDto(
        LocalDate date,
        long output) {
}
