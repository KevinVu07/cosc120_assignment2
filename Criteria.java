public enum Criteria {
NUMBER_OF_SHOTS, TEMPERATURE, STEEPING_TIME, MILK_LIST, MILK, EXTRAS, SUGAR;

public String toString() {
    return switch(this){
        case NUMBER_OF_SHOTS -> "Number of shots";
        case TEMPERATURE -> "Temperatur (celsius)";
        case STEEPING_TIME -> "Steeping time (mins)";
        case MILK_LIST -> "List of milk options";
        case MILK -> "Milk choice";
        case EXTRAS -> "Extra/s";
        case SUGAR -> "Sugar";
    };
}


}
