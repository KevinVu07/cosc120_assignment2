public enum Criteria {
NUMBER_OF_SHOTS, TEMPERATURE, STEEPING_TIME, MILK, EXTRAS, SUGAR;

public String toString() {
    return switch(this){
        case NUMBER_OF_SHOTS -> "Number of shots";
        case TEMPERATURE -> "Temperatur (celsius)";
        case STEEPING_TIME -> "Steeping time (mins)";
        case MILK -> "Milk options";
        case EXTRAS -> "Extra/s";
        case SUGAR -> "Sugar";
    };
}


}
