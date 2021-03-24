package labs.pm.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

/**
 * {@code Product} represents the objects of separate product with price and discount {@link #DISCOUNT_RATE}  applied
 * @author mserge
 * @version 1
 */
public abstract class Product implements Rateable<Product>, Serializable {
    /**
     * Discount rate currently constant {@link BigDecimal}
     */
    public static final BigDecimal DISCOUNT_RATE = BigDecimal.valueOf(0.1);


    /**
     * id is unique identifier
     */
    private final int id;
    private final String name;
    private final BigDecimal price;
    private final Rating rating;

    Product(int id, String name, BigDecimal price, Rating rating) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", getDiscount=" + getDiscount() +
                ", rating=" + rating.getStars() +
                ", expiring=" + this.getBestBefore() +
                '}';
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getId() {
        return id;
    }

    /**
     * @return amount of discount applied to price
     */
    public  BigDecimal getDiscount() {
        return this.price.multiply(DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Rating getRating() {
        return rating;
    }

    public abstract Product applyRating(Rating newRating);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Product) {
            Product product = (Product) o;
            return id == product.id ;
        } else
            return false;
    }

    public LocalDate getBestBefore() {
        return LocalDate.now();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
