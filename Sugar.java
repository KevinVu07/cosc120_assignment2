public enum Sugar {
    YES, NO;

    /**
     * @return a prettified version of the relevant enum constant
     */
    public String toString() {
        String prettified = "NA";
        switch (this) {
            case YES -> prettified =  "yes";
            case NO -> prettified = "no";
        }
        return prettified;
    }
}
