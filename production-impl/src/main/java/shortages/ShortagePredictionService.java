package shortages;

import dao.ShortageDao;
import entities.ProductionEntity;
import entities.ShortageEntity;
import external.JiraService;
import external.NotificationsService;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class ShortagePredictionService {
    //Inject all
    private ShortagePredictionRepository repository;
    private ShortageDao shortageDao;

    private NotificationsService notificationService;
    private JiraService jiraService;
    private Clock clock;

    private int confShortagePredictionDaysAhead;
    private long confIncreaseQATaskPriorityInDays;

    public ShortagePredictionService(ShortagePredictionRepository repository) {
        this.repository = repository;
    }

    public void processShortagesFromLogistic(String productRefNo) {
        LocalDate today = LocalDate.now(clock);
        ShortagePrediction prediction = repository.get(productRefNo, today, confShortagePredictionDaysAhead);
        List<ShortageEntity> shortages = prediction.predict().build();

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        // TODO REFACTOR: lookup for shortages -> ShortageFound / ShortagesGone
        if (!shortages.isEmpty() && !shortages.equals(previous)) {
            notificationService.alertPlanner(shortages);
            // TODO REFACTOR: policy why to increase task priority
            if (prediction.getLockedPartsOnStock() > 0 &&
                    shortages.get(0).getAtDay()
                            .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                jiraService.increasePriorityFor(productRefNo);
            }
            shortageDao.save(shortages);
        }
        if (shortages.isEmpty() && !previous.isEmpty()) {
            shortageDao.delete(productRefNo);
        }
    }

    public void processShortagesFromPlanner(List<ProductionEntity> products) {
        LocalDate today = LocalDate.now(clock);

        for (ProductionEntity production : products) {
            String productRefNo = production.getForm().getRefNo();

            ShortagePrediction prediction = repository.get(productRefNo, today, confShortagePredictionDaysAhead);
            List<ShortageEntity> shortages = prediction.predict().build();

            List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
            if (!shortages.isEmpty() && !shortages.equals(previous)) {
                notificationService.markOnPlan(shortages);
                if (prediction.getLockedPartsOnStock() > 0 &&
                        shortages.get(0).getAtDay()
                                .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                    jiraService.increasePriorityFor(productRefNo);
                }
                shortageDao.save(shortages);
            }
            if (shortages.isEmpty() && !previous.isEmpty()) {
                shortageDao.delete(productRefNo);
            }
        }
    }

    public void processShortagesFromQuality(String productRefNo) {
        LocalDate today = LocalDate.now(clock);

        ShortagePrediction prediction = repository.get(productRefNo, today, confShortagePredictionDaysAhead);
        List<ShortageEntity> shortages = prediction.predict().build();

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        if (!shortages.isEmpty() && !shortages.equals(previous)) {
            notificationService.softNotifyPlanner(shortages);
            if (prediction.getLockedPartsOnStock() > 0 &&
                    shortages.get(0).getAtDay()
                            .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                jiraService.increasePriorityFor(productRefNo);
            }
        }
        if (shortages.isEmpty() && !previous.isEmpty()) {
            shortageDao.delete(productRefNo);
        }
    }

    public void processShortagesFromWarehouse(String productRefNo) {
        LocalDate today = LocalDate.now(clock);

        ShortagePrediction prediction = repository.get(productRefNo, today, confShortagePredictionDaysAhead);
        List<ShortageEntity> shortages = prediction.predict().build();

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        if (shortages != null && !shortages.equals(previous)) {
            notificationService.alertPlanner(shortages);
            if (prediction.getLockedPartsOnStock() > 0 &&
                    shortages.get(0).getAtDay()
                            .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                jiraService.increasePriorityFor(productRefNo);
            }
        }
        if (shortages.isEmpty() && !previous.isEmpty()) {
            shortageDao.delete(productRefNo);
        }
    }
}
