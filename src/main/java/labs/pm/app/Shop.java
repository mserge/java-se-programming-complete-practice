package labs.pm.app;

import labs.pm.data.*;

import java.math.BigDecimal;
import java.util.Comparator;
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


        Comparator<Product> byRating =(o1, o2) -> o2.getPrice().compareTo(o1.getPrice());
        Comparator<Product> byPrice =(o1, o2) -> o2.getRating().ordinal() - o1.getRating().ordinal();
        pm.printProducts(byRating.thenComparing(byPrice.reversed()));
        pm.dumpData();
        pm.restoreData();
        pm.printProducts(byRating.thenComparing(byPrice.reversed()));

        pm.printProducts((p) -> p.getPrice().floatValue() < 1.0, byRating.thenComparing(byPrice.reversed()));
        // pm.createProduct(105, "Cons", 10, Rating.TWO_STAR,
        pm.getDiscounts().forEach(
                (rating, discount) -> System.out.println( rating + " " + discount)
        );
    }
}
