package labs.pm.app;

import labs.pm.data.*;

import java.math.BigDecimal;
import java.time.LocalDate;

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
        ProductManager pm = new ProductManager();
        Product p1 = pm.createProduct(101, "Tea", BigDecimal.valueOf(1.99), Rating.THREE_STAR);
        Product p2 = pm.createProduct(102, "Cofee", BigDecimal.valueOf(1.99), Rating.FOUR_STAR);
        Product p3 = pm.createProduct(103, "Cake", BigDecimal.valueOf(1.99), Rating.THREE_STAR, LocalDate.now().plusDays(2));
        Product p4 = pm.createProduct(105, "Cookie", BigDecimal.valueOf(3.99), Rating.TWO_STAR, LocalDate.now());
        Product p5 = p3.applyRating(Rating.FIVE_STAR);
        Product p8 = p4.applyRating(Rating.FIVE_STAR);
        Product p9 = p1.applyRating(Rating.TWO_STAR);
        Product p6 = pm.createProduct(104, "cHOCOLATE", BigDecimal.valueOf(1.99), Rating.THREE_STAR);
        Product p7 = pm.createProduct(104, "cHOCOLATE", BigDecimal.valueOf(1.99), Rating.THREE_STAR, LocalDate.now().plusDays(2));
        System.out.println("p1 = " + p1);
        System.out.println("p2 = " + p2);
        System.out.println("p3 = " + p3);
        System.out.println("p4 = " + p4);
        System.out.println("p5 = " + p5);
        System.out.println("p6 = " + p6);
        System.out.println("p7 = " + p7);
        System.out.println("p8 = " + p8);
        System.out.println("p9 = " + p9);
        System.out.println("p7 == p6 " + (p7.equals(p6)));
    }
}
