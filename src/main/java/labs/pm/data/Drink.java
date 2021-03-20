package labs.pm.data;

import java.math.BigDecimal;
import java.time.LocalTime;

public class Drink extends Product{
     Drink(int id, String name, BigDecimal price, Rating rating) {
        super(id, name, price, rating);
    }

    @Override
    public BigDecimal getDiscount() {
        LocalTime now = LocalTime.now();
        return (now.isAfter(LocalTime.of(17, 30)) && now.isBefore(LocalTime.of(19, 30)))
        ? super.getDiscount(): BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "Drink{" +
                super.toString() +
                '}';
    }
    @Override
    public Product applyRating(Rating newRating) {
        return new Drink(this.getId(), this.getName(), this.getPrice(), newRating);
    }
}
