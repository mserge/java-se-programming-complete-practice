package labs.pm.data;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.ResourceBundle;

public class ProductManager {

    private Product product;
    private Review review;
    private Locale locale;
    private ResourceBundle resourceBundle;
    private DateTimeFormatter dateTimeFormatter;
    private NumberFormat numberFormat;

    public ProductManager(Locale locale) {
        this.locale = locale;
        resourceBundle = ResourceBundle.getBundle("resources", locale);
        dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).localizedBy(locale);
        numberFormat = NumberFormat.getCurrencyInstance(locale);
    }

    public void printProductReport(){
        StringBuilder txt = new StringBuilder();
        txt.append(
            MessageFormat.format(
                resourceBundle.getString("product"),
                product.getName(),
                numberFormat.format(product.getPrice()),
                product.getRating().getStars(),
                dateTimeFormatter.format(product.getBestBefore())
                ));
        txt.append("\n");
        if(review != null){
            txt.append(
                    MessageFormat.format(
                            resourceBundle.getString("review"),
                            review.getRating().getStars(),
                            review.getComments()
                    ));
        } else {
            txt.append(resourceBundle.getString("no.reviews"));
        }
        txt.append("\n");
        System.out.println(txt);
    }
    public  Product createProduct(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore){
        product = new Food(id, name, price, rating, bestBefore);
        return product;
    }
    public  Product createProduct(int id, String name, BigDecimal price, Rating rating ){
        product = new Drink(id, name, price, rating);
        return product;
    }

    public Product reviewProduct(Product oldProduct, Rating rating, String comments){
        review = new Review(rating, comments);
        product = oldProduct.applyRating(rating);
        return product;
    }
}

