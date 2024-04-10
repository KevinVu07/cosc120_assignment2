public enum Milk {
    WHOLE, SKIM, SOY, ALMOND, OAT, COCONUT, NONE;

    /**
     * @return a prettified version of the relevant enum constant
     */
    public String toString() {
        String prettified = "NA";
        switch (this) {
            case WHOLE -> prettified =  "Full-cream";
            case SKIM -> prettified = "Skim";
            case SOY -> prettified = "Soy";
            case ALMOND -> prettified = "Almond";
            case OAT -> prettified = "Oat";
            case COCONUT -> prettified = "Coconut";
            case NONE -> prettified = "None";
        }
        return prettified;
    }
}
