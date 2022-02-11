package shortages;

import java.time.LocalDate;

interface ShortagePredictionRepository {
    ShortagePrediction get(String productRefNo, LocalDate today, int daysAhead);
}
