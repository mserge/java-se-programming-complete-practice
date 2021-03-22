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
        pm.createProduct(101, "Tea", BigDecimal.valueOf(1.99), Rating.NOT_RATED);
//        pm.printProductReport(101);
        pm.parseReview("101,3,Some review");
        pm.parseReview("10,3,Some review");
        pm.parseReview("x,3,Some review"); // cause WARNING
        pm.parseReview("3,Some review"); //cause WARNING
//        pm.reviewProduct(10, Rating.FOUR_STAR, "Nice hot cup of tea");
        pm.reviewProduct(101, Rating.TWO_STAR, "Too cold cup of tea");
        pm.reviewProduct(101, Rating.TWO_STAR, "3 comment");
        pm.reviewProduct(101, Rating.ONE_STAR, "Yoo hot cup of tea");
        pm.reviewProduct(101, Rating.TWO_STAR, "2 stars only");
       pm.printProductReport(101);
        pm.changeLocale("ru-RU");
        pm.createProduct(102, "Coffee", BigDecimal.valueOf(0.99), Rating.NOT_RATED);
//        pm.printProductReport(102);
        pm.reviewProduct(102, Rating.FOUR_STAR, "Nice hot cup of coffee");
        pm.reviewProduct(102, Rating.FOUR_STAR, "Too cold cup of coffee");
        pm.reviewProduct(102, Rating.FOUR_STAR, "3 comment");
        pm.reviewProduct(102, Rating.ONE_STAR, "Yoo hot cup of coffee");
        pm.reviewProduct(102, Rating.FOUR_STAR, "2 stars only");
        pm.createProduct(103, "Machiato", BigDecimal.valueOf(0.99), Rating.TWO_STAR);

        pm.printProductReport(44);
        Comparator<Product> byRating =(o1, o2) -> o2.getPrice().compareTo(o1.getPrice());
        Comparator<Product> byPrice =(o1, o2) -> o2.getRating().ordinal() - o1.getRating().ordinal();
        pm.printProducts(byRating.thenComparing(byPrice.reversed()));
        pm.printProducts((p) -> p.getPrice().floatValue() < 1.0, byRating.thenComparing(byPrice.reversed()));
        // pm.createProduct(105, "Cons", 10, Rating.TWO_STAR, 
        pm.getDiscounts().forEach(
                (rating, discount) -> System.out.println( rating + " " + discount)
        );
    }
}
