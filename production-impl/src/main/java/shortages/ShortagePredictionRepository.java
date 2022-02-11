package shortages;

import java.time.LocalDate;

public interface ShortagePredictionRepository {
    ShortagePrediction get(String productRefNo, LocalDate today, int daysAhead);
}
