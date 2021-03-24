package labs.pm.data;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.Function;
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
    private Path reportsFolder=Path.of(config.getString("reports.folder"));
    private Path dataFolder=Path.of(config.getString("data.folder"));
    private Path tempFolder=Path.of(config.getString("temp.folder"));

    private static Map<String, ResourceFormatter> formatters =
            Map.of(
                    "en-GB", new ResourceFormatter(Locale.UK),
                    "ru-RU", new ResourceFormatter(new Locale("ru", "RU")),
                    "fr-FR", new ResourceFormatter(Locale.FRANCE)
            );
    private ResourceFormatter formatter;

    public ProductManager(String localetag) {
        changeLocale(localetag);
        loadAllData();
    }

    public ProductManager(Locale locale) {
        this(locale.toLanguageTag());
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

    public void printProductReport(Product product) throws IOException {
        // StringBuilder txt = new StringBuilder();
        Path productFile = reportsFolder.resolve(MessageFormat.format(config.getString("report.file"), product.getId()));

        try (PrintWriter txt = new PrintWriter(
                new OutputStreamWriter(Files.newOutputStream(productFile, StandardOpenOption.CREATE),StandardCharsets.UTF_8),
                true) ){
            txt.append(formatter.formatProduct(product));
            txt.append(System.lineSeparator());
            List<Review> reviews = products.get(product);
            Collections.sort(reviews);

            if (reviews.isEmpty()) {
                txt.append(formatter.getText("no.reviews"));
                txt.append(System.lineSeparator());
            } else {
                String reviewtxt = reviews.stream().
                        map(r -> formatter.formatReview(r))
                        .collect(Collectors.joining(System.lineSeparator()));
                txt.append(reviewtxt);
            }
        }
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
        } catch (IOException e) {
            logger.log(Level.SEVERE,"Error printing product" + e.getMessage(), e);
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
    public Review parseReview(String text){
        Review review = null;
        try {
            Object[] objects = reviewFormat.parse(text);
            review = new Review(
                    Rateable.convert(Integer.parseInt((String) objects[0])),
                    (String) objects[1]);
        } catch (ParseException | NumberFormatException e) {
            logger.log(Level.WARNING, "Error parsing review: "+ text,  e);
        }
        return review;
    }
    public Product parseProduct(String text){
        Product product = null;
        try {
            Object[] objects = productFormat.parse(text);
            String type = (String) objects[0];
            int id = Integer.parseInt((String) objects[1]);
            String name = (String) objects[2];
            BigDecimal price  = BigDecimal.valueOf(Double.parseDouble((String) objects[3]));
            Rating rating = Rateable.convert(Integer.parseInt((String) objects[4]));
            switch (type){
                case "D":
                    product = new Drink(id, name, price, rating);
                    break;
                case "F":
                    LocalDate bestBefore = LocalDate.parse((String) objects[5]);
                    product = new Food(id, name, price, rating, bestBefore);
                    break;
                default:
                    logger.log(Level.WARNING, "Error parsing product: "+ text);
            }
        } catch (ParseException | NumberFormatException | DateTimeException e) {
            logger.log(Level.WARNING, "Error parsing product: "+ text,  e);
        }
        return product;
    }
    private List<Review> loadReviews(Product product){
        List<Review> reviews = null;
        Path file = dataFolder.resolve(MessageFormat.format(config.getString("reviews.data.file"), product.getId()));
        if(Files.notExists(file)){
            reviews = new ArrayList<>();
        } else {
            try {
                reviews = Files.lines(file)// UTF-8 by default
                        .map(text -> parseReview(text))
                        .filter(review -> review != null)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                logger.log(Level.WARNING, "Cannot parse reviews" + e.getMessage(), e);
            }
        }
        return reviews;
    }
    private Product loadProduct(Path file){
        Product product = null;
        try {
            product = parseProduct(Files.lines(dataFolder.resolve(file), StandardCharsets.UTF_8).findFirst().orElseThrow());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error parse product " + e.getMessage(), e);
        }
        return product;

    }

    private void loadAllData(){
        try {
            products = Files.list(dataFolder)
                    .filter(file -> file.getFileName().toString().startsWith("product"))
                    .map(this::loadProduct)
                    .filter(product -> product != null)
                    .collect(Collectors.toMap(Function.identity(), product -> loadReviews(product)));
        } catch (Exception e) {
           logger.log(Level.SEVERE, "Error loading data " +  e.getMessage(), e);
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

