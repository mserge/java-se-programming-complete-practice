package labs.pm.app;

import labs.pm.data.*;

import java.math.BigDecimal;
import java.util.Locale;

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
        ProductManager pm = new ProductManager(Locale.UK);
        pm.createProduct(101, "Tea", BigDecimal.valueOf(1.99), Rating.NOT_RATED);
        pm.printProductReport(101);
        pm.reviewProduct(101, Rating.FOUR_STAR, "Nice hot cup of tea");
        pm.reviewProduct(101, Rating.TWO_STAR, "Too cold cup of tea");
        pm.reviewProduct(101, Rating.TWO_STAR, "3 comment");
        pm.reviewProduct(101, Rating.ONE_STAR, "Yoo hot cup of tea");
        pm.reviewProduct(101, Rating.TWO_STAR, "2 stars only");
        pm.printProductReport(101);
        pm.changeLocale("ru-RU");
        pm.createProduct(102, "Coffee", BigDecimal.valueOf(1.99), Rating.NOT_RATED);
        pm.printProductReport(102);
        pm.reviewProduct(102, Rating.FOUR_STAR, "Nice hot cup of coffee");
        pm.reviewProduct(102, Rating.FOUR_STAR, "Too cold cup of coffee");
        pm.reviewProduct(102, Rating.FOUR_STAR, "3 comment");
        pm.reviewProduct(102, Rating.ONE_STAR, "Yoo hot cup of coffee");
        pm.reviewProduct(102, Rating.FOUR_STAR, "2 stars only");
        pm.printProductReport(102);

    }
}
