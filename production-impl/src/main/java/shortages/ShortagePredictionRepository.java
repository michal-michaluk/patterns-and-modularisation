package shortages;

import java.time.LocalDate;

public interface ShortagePredictionRepository {
    ShortagePrediction get(LocalDate today, int daysAhead);
}
