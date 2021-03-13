package labs.pm.app;

import labs.pm.data.Product;
import labs.pm.data.Rating;

import java.math.BigDecimal;

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
        Product p1 = new Product(101, "Tea", BigDecimal.valueOf(1.99));
        Product p2 = new Product(102, "Cofee", BigDecimal.valueOf(1.99), Rating.FOUR_STAR);
        Product p3 = new Product(103, "Cake", BigDecimal.valueOf(1.99), Rating.FIVE_STAR);
        Product p4 = new Product();
        Product p5 = p3.applyRating(Rating.FIVE_STAR);
        System.out.println("p1 = " + p1);
        System.out.println("p2 = " + p2);
        System.out.println("p3 = " + p3);
        System.out.println("p4 = " + p4);
        System.out.println("p5 = " + p5);
    }
}
