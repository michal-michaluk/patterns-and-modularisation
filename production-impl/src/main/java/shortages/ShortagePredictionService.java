package shortages;

import acl.ShortageFinderACL;
import dao.DemandDao;
import dao.ProductionDao;
import dao.ShortageDao;
import entities.ProductionEntity;
import entities.ShortageEntity;
import external.CurrentStock;
import external.JiraService;
import external.NotificationsService;
import external.StockService;
import tools.ShortageFinder;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class ShortagePredictionService {
    //Inject all
    private ShortagePredictionRepository repository;
    private DemandDao demandDao;
    private ShortageDao shortageDao;
    private StockService stockService;
    private ProductionDao productionDao;

    private NotificationsService notificationService;
    private JiraService jiraService;
    private Clock clock;

    private int confShortagePredictionDaysAhead;
    private long confIncreaseQATaskPriorityInDays;

    public ShortagePredictionService(ShortagePredictionRepository repository) {
        this.repository = repository;
    }

    public Shortages predictShortages(LocalDate today, int daysAhead) {
        ShortagePrediction prediction = repository.get(today, daysAhead);
        return prediction.predict();
    }

    public void processShortagesFromLogistic(String productRefNo) {
        LocalDate today = LocalDate.now(clock);
        CurrentStock stock = stockService.getCurrentStock(productRefNo);
        List<ShortageEntity> shortages = ShortageFinderACL.findShortages(
                today, confShortagePredictionDaysAhead,
                stock,
                productionDao.findFromTime(productRefNo, today.atStartOfDay()),
                demandDao.findFrom(today.atStartOfDay(), productRefNo)
        );
        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        // TODO REFACTOR: lookup for shortages -> ShortageFound / ShortagesGone
        if (!shortages.isEmpty() && !shortages.equals(previous)) {
            notificationService.alertPlanner(shortages);
            // TODO REFACTOR: policy why to increase task priority
            if (stock.getLocked() > 0 &&
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
            CurrentStock currentStock = stockService.getCurrentStock(production.getForm().getRefNo());
            List<ShortageEntity> shortages = ShortageFinder.findShortages(
                    today, confShortagePredictionDaysAhead,
                    currentStock,
                    productionDao.findFromTime(production.getForm().getRefNo(), today.atStartOfDay()),
                    demandDao.findFrom(today.atStartOfDay(), production.getForm().getRefNo())
            );
            List<ShortageEntity> previous = shortageDao.getForProduct(production.getForm().getRefNo());
            if (!shortages.isEmpty() && !shortages.equals(previous)) {
                notificationService.markOnPlan(shortages);
                if (currentStock.getLocked() > 0 &&
                        shortages.get(0).getAtDay()
                                .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                    jiraService.increasePriorityFor(production.getForm().getRefNo());
                }
                shortageDao.save(shortages);
            }
            if (shortages.isEmpty() && !previous.isEmpty()) {
                shortageDao.delete(production.getForm().getRefNo());
            }
        }
    }

    public void processShortagesFromQuality(String productRefNo) {
        LocalDate today = LocalDate.now(clock);
        CurrentStock currentStock = stockService.getCurrentStock(productRefNo);
        List<ShortageEntity> shortages = ShortageFinder.findShortages(
                today, confShortagePredictionDaysAhead,
                currentStock,
                productionDao.findFromTime(productRefNo, today.atStartOfDay()),
                demandDao.findFrom(today.atStartOfDay(), productRefNo)
        );

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        if (!shortages.isEmpty() && !shortages.equals(previous)) {
            notificationService.softNotifyPlanner(shortages);
            if (currentStock.getLocked() > 0 &&
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
        CurrentStock currentStock = stockService.getCurrentStock(productRefNo);
        List<ShortageEntity> shortages = ShortageFinder.findShortages(
                today, confShortagePredictionDaysAhead,
                currentStock,
                productionDao.findFromTime(productRefNo, today.atStartOfDay()),
                demandDao.findFrom(today.atStartOfDay(), productRefNo)
        );

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        if (shortages != null && !shortages.equals(previous)) {
            notificationService.alertPlanner(shortages);
            if (currentStock.getLocked() > 0 &&
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
