import java.text.NumberFormat;
import java.util.*;

public class Coffee {
    // fields
    private final long itemId;
    private final String itemName;
    private float price;
    private final int numberOfShots;
    private final Sugar sugar;
    private List<Milk> milks = new ArrayList<>();
    private Set<String> extras = new HashSet<>();
    private final String description;
    private float minPrice;
    private float maxPrice;

    //constructor
    public Coffee(long itemId, String itemName, float price, int numberOfShots, Sugar sugar, List<Milk> milks, Set<String> extras, String description) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.numberOfShots = numberOfShots;
        this.sugar = sugar;
        if(!milks.isEmpty()) this.milks =new ArrayList<>(milks);
        if(!extras.isEmpty()) this.extras=new HashSet<>(extras);
        this.description = description;
    }

    //getters and setters
    public float getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(float minPrice) {
        this.minPrice = minPrice;
    }

    public float getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(float maxPrice) {
        this.maxPrice = maxPrice;
    }


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

    public int getNumberOfShots() {
        return numberOfShots;
    }

    public Sugar getSugar() {
        return sugar;
    }

    public List<Milk> getMilks() {
        return new ArrayList<>(milks);
    }

    public Set<String> getExtras() {
        return new HashSet<>(extras);
    }

    public String getDescription() {
        return description;
    }

    public StringBuilder displayMenu() {
        String milkList = Arrays.toString(this.getMilks().toArray()).replace("[", "").replace("]","");
        String extraList = Arrays.toString(this.getExtras().toArray()).replace("[", "").replace("]","");
        StringBuilder displayMenu = new StringBuilder();
        String priceString = formatPrice(this.getPrice());
        displayMenu = displayMenu.append(this.getItemName()).append(" (").append(this.getItemId()).append(")\n").append(this.getDescription()).append("\n").append("Ingredients:\n").append("Number of shots: " + this.getNumberOfShots() + "\n").append("Sugar: " + this.getSugar() + "\n").append("Milk options: " + milkList + "\n").append("Extra/s: " + extraList + "\n").append("Price: " + priceString).append("\n\n");
        return displayMenu;
    }

}
