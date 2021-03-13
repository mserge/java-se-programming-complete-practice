package labs.pm.data;

public enum Rating {

    NOT_RATED("-"),
    ONE_STAR("*"),
    TWO_STAR("**"),
    THREE_STAR("***"),
    FOUR_STAR("****"),
    FIVE_STAR("*****");


    private String stars;

    Rating(String stars) {
        this.stars = stars;
    }

    public String getStars() {
        return stars;
    }
}
