package labs.pm.app;

import labs.pm.data.*;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *  represents class to manage Products
 * @author mserge
 * @version 1
 */
public class Shop {
    /**
     * @param args string cmdline arguments
     */
    public static void main(String[] args) {
        ProductManager pm = ProductManager.getInstance();
        pm.createProduct(103, "Ice tea", BigDecimal.valueOf(1.99), Rating.NOT_RATED);
//        pm.printProductReport(103);
        pm.reviewProduct(103, Rating.FOUR_STAR, "Nice hot cup of tea");
        pm.reviewProduct(103, Rating.TWO_STAR, "Too cold cup of tea");
        pm.reviewProduct(103, Rating.TWO_STAR, "3 comment");
        pm.reviewProduct(103, Rating.ONE_STAR, "Yoo hot cup of tea");
        pm.reviewProduct(103, Rating.TWO_STAR, "2 stars only");
        AtomicInteger clienCount = new AtomicInteger(0);
        Callable<String> client = () -> {
            String clientName = "client" + clienCount.getAndIncrement();
            String threadName = Thread.currentThread().getName();
            int productId = ThreadLocalRandom.current().nextInt(2) + 101;
            String langTag = ProductManager.getSupportedLocales().stream().skip(ThreadLocalRandom.current().nextInt(2)).findFirst().get();
            StringBuilder log = new StringBuilder();
            log.append(clientName).append(" " ). append(threadName).append("\n").append("start of log").append("\n");
            log.append(pm.getDiscounts(langTag)
                    .entrySet().stream()
                    .map(entry -> entry.getKey() + "\t " + entry.getValue())
                    .collect(Collectors.joining("\n")));
            Product product = pm.reviewProduct(productId, Rating.FOUR_STAR, "Generated review");
            log.append(product != null ?
                    "\nProduct "+ productId + " reviewed.\n" :
                    "\nProduct "+ productId + " not reviewed.\n"
                    );
            pm.printProductReport(productId, langTag, clientName);
            log.append("Report generated for "+ productId + " product.\n");
            log.append("\n").append("end of log").append("\n");
            return log.toString();
        };
        List<Callable<String>> clients = Stream.generate(() -> client).limit(5).collect(Collectors.toList());
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        try {
            List<Future<String>> results    = executorService.invokeAll(clients);
            executorService.shutdown(); // will no accept new
            results.stream().forEach(result -> {
                try {
                    System.out.println(result.get());
                } catch (InterruptedException | ExecutionException e) {
                        Logger.getLogger(Shop.class.getName()).log(Level.SEVERE, "Error get results " + e.getMessage(), e);
                    }
            });
        } catch (InterruptedException e) {
            Logger.getLogger(Shop.class.getName()).log(Level.SEVERE, "Error invoking " + e.getMessage(), e);
        }
//        Comparator<Product> byRating =(o1, o2) -> o2.getPrice().compareTo(o1.getPrice());
//        Comparator<Product> byPrice =(o1, o2) -> o2.getRating().ordinal() - o1.getRating().ordinal();
//        pm.printProducts(byRating.thenComparing(byPrice.reversed()), "ru-RU");
//        pm.dumpData();
//        pm.restoreData();
//        pm.printProducts(byRating.thenComparing(byPrice.reversed()), "en-GB");
//
//        pm.printProducts((p) -> p.getPrice().floatValue() < 1.0, byRating.thenComparing(byPrice.reversed()), "en-GB");
//        // pm.createProduct(105, "Cons", 10, Rating.TWO_STAR,
//        pm.getDiscounts("en-EN").forEach(
//                (rating, discount) -> System.out.println( rating + " " + discount)
//        );
    }
}
