import java.text.NumberFormat;
import java.util.*;

public class Beverage {
    // fields
    private final long itemId;
    private final String itemName;
    private float price;
    private final String description;
    private final DreamBeverage genericFeatures;

    //constructor
    public Beverage(long itemId, String itemName, float price, String description, DreamBeverage dreamBeverage) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.description = description;
        this.genericFeatures = dreamBeverage;
    }

    //getters and setters

    public long getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public float getPrice() {
        return price;
    }

    public String formatPrice(float price) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        return nf.format(price);
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public DreamBeverage getGenericFeatures() {
        return genericFeatures;
    }

    public StringBuilder displayMenu() {
        StringBuilder displayMenu = new StringBuilder();
        String priceString = formatPrice(this.getPrice());
        displayMenu = displayMenu.append(this.getItemName()).append(" (").append(this.getItemId()).append(")\n").append(this.getDescription()).append("\n").append("Ingredients:").append(this.genericFeatures.getDescription()).append("\nPrice: " + priceString).append("\n\n");
        return displayMenu;
    }

}
