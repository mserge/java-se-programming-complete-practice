package labs.pm.data;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * {@code Product} represents the objects of separate product with price and discount {@link #DISCOUNT_RATE}  applied
 * @author mserge
 * @version 1
 */
public class Product {
    /**
     * Discount rate currently constant {@link BigDecimal}
     */
    public static final BigDecimal DISCOUNT_RATE = BigDecimal.valueOf(0.1);


    /**
     * id is unique identifier
     */
    private int id;
    private String name;
    private BigDecimal price;

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", getDiscount=" + getDiscount() +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    /**
     * @return amount of discount applied to price
     */
    public  BigDecimal getDiscount() {
        return this.price.multiply(DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP);
    }
}
