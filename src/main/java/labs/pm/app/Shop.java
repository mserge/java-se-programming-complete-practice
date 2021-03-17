package labs.pm.app;

import labs.pm.data.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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
        Product p1 = pm.createProduct(101, "Tea", BigDecimal.valueOf(1.99), Rating.NOT_RATED);
        pm.printProductReport();
        p1 = pm.reviewProduct(p1, Rating.FOUR_STAR, "Nice hot cup of tea");
        pm.printProductReport();

    }
}
