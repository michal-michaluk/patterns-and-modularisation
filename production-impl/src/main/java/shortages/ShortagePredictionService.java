package shortages;

import java.time.LocalDate;

public class ShortagePredictionService {
    private ShortagePredictionRepository repository;

    public ShortagePredictionService(ShortagePredictionRepository repository) {
        this.repository = repository;
    }

    public Shortages predictShortages(LocalDate today, int daysAhead) {
        ShortagePrediction prediction = repository.get(today, daysAhead);
        return prediction.predict();
    }
}
