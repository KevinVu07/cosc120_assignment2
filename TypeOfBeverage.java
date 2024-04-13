public enum TypeOfBeverage {
    BEVERAGE, TEA;
    /**
     * @return a prettified version of the relevant enum constant
     */
    public String toString() {
        String prettified = "NA";
        switch (this) {
            case BEVERAGE -> prettified =  "Coffee";
            case TEA -> prettified = "Tea";
        }
        return prettified;
    }
}
