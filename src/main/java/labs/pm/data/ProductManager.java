package labs.pm.data;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProductManager {
    public static final Logger logger = Logger.getLogger(ProductManager.class.getName());
    private Map<Product, List<Review>> products = new HashMap<>();
    private ResourceBundle config = ResourceBundle.getBundle("config");
    private MessageFormat productFormat = new MessageFormat(config.getString("product.data.format"));
    private MessageFormat reviewFormat = new MessageFormat(config.getString("review.data.format"));

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
        try {
            printProductReport(findProduct(id));
        } catch (ProductManagerException e) {
            logger.log(Level.INFO, e.getMessage());
        }
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
    public void parseReview(String text){
        try {
            Object[] objects = reviewFormat.parse(text);
            reviewProduct(Integer.parseInt((String) objects[0]),
                    Rateable.convert(Integer.parseInt((String) objects[1])),
                    (String) objects[2]);
        } catch (ParseException | NumberFormatException e) {
            logger.log(Level.WARNING, "Error parsing review: "+ text,  e);
        }
    }
    public void parseProduct(String text){
        try {
            Object[] objects = productFormat.parse(text);
            String type = (String) objects[0];
            int id = Integer.parseInt((String) objects[1]);
            String name = (String) objects[2];
            BigDecimal price  = BigDecimal.valueOf(Double.parseDouble((String) objects[3]));
            Rating rating = Rateable.convert(Integer.parseInt((String) objects[4]));
            switch (type){
                case "D":
                    createProduct(id, name, price, rating);
                    break;
                case "F":
                    LocalDate bestBefore = LocalDate.parse((String) objects[5]);
                    createProduct(id, name, price, rating, bestBefore);
                    break;
                default:
                    logger.log(Level.WARNING, "Error parsing product: "+ text);
                    return;
            }
        } catch (ParseException | NumberFormatException | DateTimeException e) {
            logger.log(Level.WARNING, "Error parsing product: "+ text,  e);
        }
    }
    public Map<String, String> getDiscounts() {
        return products.keySet().stream().collect(
                Collectors.groupingBy(
                        p -> p.getRating().getStars(),
                        Collectors.collectingAndThen(
                                Collectors.summingDouble(p -> p.getDiscount().doubleValue()),
                                number -> formatter.numberFormat.format(number)
                        )
                ));
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
        try {
            return reviewProduct(findProduct(id), rating, comments);
        } catch (ProductManagerException e) {
            logger.log(Level.INFO, e.getMessage());
            return null;
        }
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

    public Product findProduct(int id) throws ProductManagerException {
        return products.keySet().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ProductManagerException("Product with id " + id + " not found"));
    }

}

