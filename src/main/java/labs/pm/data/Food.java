package labs.pm.data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 */
public class Food extends Product{

    /**
     *
     */
    private LocalDate bestBefore;

    /**
     * @param id
     * @param name
     * @param price
     * @param rating
     * @param bestBefore
     */
     Food(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
        super(id, name, price, rating);
        this.bestBefore = bestBefore;
    }

    /**
     * @return
     */
    public LocalDate getBestBefore() {
        return bestBefore;
    }

    @Override
    public String toString() {
        return "Food{" +
                super.toString() +
                '}';
    }

    @Override
    public BigDecimal getDiscount() {
        return bestBefore.isBefore(LocalDate.now()) ? super.getDiscount() : BigDecimal.ZERO;
    }

    @Override
    public Product applyRating(Rating newRating) {
        return new Food(this.getId(), this.getName(), this.getPrice(), newRating, bestBefore);
    }
}
