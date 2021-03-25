package labs.pm.app;

import labs.pm.data.*;

import java.util.Comparator;

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


        Comparator<Product> byRating =(o1, o2) -> o2.getPrice().compareTo(o1.getPrice());
        Comparator<Product> byPrice =(o1, o2) -> o2.getRating().ordinal() - o1.getRating().ordinal();
        pm.printProducts(byRating.thenComparing(byPrice.reversed()), "ru-RU");
        pm.dumpData();
        pm.restoreData();
        pm.printProducts(byRating.thenComparing(byPrice.reversed()), "en-GB");

        pm.printProducts((p) -> p.getPrice().floatValue() < 1.0, byRating.thenComparing(byPrice.reversed()), "en-GB");
        // pm.createProduct(105, "Cons", 10, Rating.TWO_STAR,
        pm.getDiscounts("en-EN").forEach(
                (rating, discount) -> System.out.println( rating + " " + discount)
        );
    }
}
