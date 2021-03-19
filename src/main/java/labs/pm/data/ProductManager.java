package labs.pm.data;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ProductManager {
    private Map<Product, List<Review>> products = new HashMap<>();
    private static Map<String, ResourceFormatter> formatters =
            Map.of(
                    "en-GB", new ResourceFormatter(Locale.UK),
                    "ru-RU", new ResourceFormatter(new Locale("ru", "RU")),
                    "fr-FR", new ResourceFormatter(Locale.FRANCE)
            );
    private ResourceFormatter formatter;

    public ProductManager(String localetag) {
        changeLocale(localetag);
    }

    public ProductManager(Locale locale) {
        changeLocale(locale.toLanguageTag());
    }

    public void changeLocale(String localeTag) {
        formatter = formatters.getOrDefault(localeTag, formatters.get("en-GB"));
    }

    public static Set<String> getSupportedLocales() {
        return formatters.keySet();
    }

    private static class ResourceFormatter {

        private Locale locale;
        private ResourceBundle resourceBundle;
        private DateTimeFormatter dateTimeFormatter;
        private NumberFormat numberFormat;

        public ResourceFormatter(Locale locale) {
            this.locale = locale;
            resourceBundle = ResourceBundle.getBundle("resources", locale);
            dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).localizedBy(locale);
            numberFormat = NumberFormat.getCurrencyInstance(locale);
        }


        private String getText(String s) {
            return resourceBundle.getString(s);
        }

        private String formatReview(Review review) {
            return MessageFormat.format(
                    getText("review"),
                    review.getRating().getStars(),
                    review.getComments()
            );
        }

        private String formatProduct(Product product) {
            return MessageFormat.format(
                    getText("product"),
                    product.getName(),
                    numberFormat.format(product.getPrice()),
                    product.getRating().getStars(),
                    dateTimeFormatter.format(product.getBestBefore())
            );
        }
    }

    public void printProductReport(Product product) {
        StringBuilder txt = new StringBuilder();
        txt.append(formatter.formatProduct(product));
        txt.append("\n");
        List<Review> reviews = products.get(product);
        Collections.sort(reviews);

        if (reviews.isEmpty()) {
            txt.append(formatter.getText("no.reviews"));
            txt.append("\n");
        } else {
            String reviewtxt = reviews.stream().
                    map(r -> formatter.formatReview(r))
                    .collect(Collectors.joining("\n"));
            txt.append(reviewtxt);
        }
        print(txt);
    }

    private void print(StringBuilder txt) {
        PrintWriter printWriter = new PrintWriter(
                new OutputStreamWriter(System.out, StandardCharsets.UTF_8),
                true);
        printWriter.println(txt);
    }

    public void printProductReport(int id) {
        printProductReport(findProduct(id));
    }

    public void printProducts(Comparator<Product> sorter) {
        printProducts(t -> true, sorter);
    }

    public void printProducts(Predicate<Product> filter, Comparator<Product> sorter) {

        StringBuilder txt = new StringBuilder();
        products.keySet().stream().
                sorted(sorter).
                filter(filter).
                forEach(p -> txt.append(formatter.formatProduct(p) + "\n"));
        print(txt);
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
        Product product = new Food(id, name, price, rating, bestBefore);
        products.putIfAbsent(product, new ArrayList<>());
        return product;
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating) {
        Product product = new Drink(id, name, price, rating);
        products.putIfAbsent(product, new ArrayList<>());
        return product;
    }

    public Product reviewProduct(int id, Rating rating, String comments) {
        return reviewProduct(findProduct(id), rating, comments);
    }

    public Product reviewProduct(Product oldProduct, Rating rating, String comments) {
        List<Review> reviews = products.get(oldProduct);
        products.remove(oldProduct);
        reviews.add(new Review(rating, comments));
        int stars = (int) Math.round(reviews.stream().mapToInt(r -> r.getRating().ordinal()).average().orElse(0));
        Product product = oldProduct.applyRating(Rateable.convert(stars));
        products.put(product, reviews);
        return product;
    }

    public Product findProduct(int id) {
        return products.keySet().stream().
                filter(p -> p.getId() == id).
                findFirst().orElse(null);
    }

}

