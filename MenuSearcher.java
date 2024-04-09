import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MenuSearcher {
    // fields
    private static final String filePath = "./Assignment1/src/menu.txt";
    private final static String appName = "The Caffeinated Geek";
    private static Menu menu;
    private final static String iconPath = "./Assignment1/src/icon.png";
    static ImageIcon icon = new ImageIcon(iconPath);

    /**
     * main method used to allow user to search The Caffeinated Geek database of coffee drink, and place an order request
     * @param args none required
     */
    public static void main(String[] args) {
        menu = loadItems();

        JOptionPane.showMessageDialog(null, "Welcome to The Caffeinated Geek!\n\tTo start, click OK.", appName, JOptionPane.QUESTION_MESSAGE, icon);
        Coffee userCoffee = getUserCriteria();
        List<Coffee> matchCoffees = menu.findMatch(userCoffee);
        if(matchCoffees.size()>0){ //if the list is not empty
            //this will map the item names and item ID to the corresponding Item objects
            Map<String, Coffee> options = new HashMap<>();
            //build a text description of the menu items for the user to view
            StringBuilder infoToShow = new StringBuilder("Matches found!! The following items meet your criteria:\n");
            for (Coffee matchCoffee : matchCoffees) {
                StringBuilder displayMenu = matchCoffee.displayMenu();
                infoToShow.append("\n").append(displayMenu);
                options.put(matchCoffee.getItemName() + " (" + matchCoffee.getItemId() + ")", matchCoffee);
            }
            String order = (String) JOptionPane.showInputDialog(null,infoToShow+"\n\nPlease select which menu item you would like to order",appName, JOptionPane.QUESTION_MESSAGE,icon,options.keySet().toArray(), "");
            if(order==null) System.exit(0);
            else{
                Coffee chosenCoffee = options.get(order);
                Geek customer = getGeekDetails();
                writeOrderToFile(customer, chosenCoffee, userCoffee);
                JOptionPane.showMessageDialog(null, "Thank you! Your order has been placed. " +
                        "We will bring your coffee out shortly.", appName, JOptionPane.QUESTION_MESSAGE, icon);
            }
        } else JOptionPane.showMessageDialog(null, "Unfortunately none of our menu items meet your criteria :(" +
                "\n\tTo exit, click OK.", appName, JOptionPane.QUESTION_MESSAGE, icon);
        System.exit(0);
    }


    /**
     * method to get user's criteria about their order preference
     * @return a Coffee object that have the user's criteria
     * this method was sourced and adapted from COSC120 Tutorial 4 FindADog.java getUserCriteria
     */
    private static Coffee getUserCriteria(){

        Milk milk = (Milk) JOptionPane.showInputDialog(null,"What milk type would you like in your coffee?",appName, JOptionPane.QUESTION_MESSAGE,icon,Milk.values(),Milk.WHOLE);
        if(milk==null) System.exit(0);
        List<Milk> milks= new ArrayList<>();
        milks.add(milk);

        Sugar sugar = Sugar.valueOf("NO");
        int sugarChoice = JOptionPane.showConfirmDialog(null,"Would you like to add sugar?",appName, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
        if (sugarChoice == 0) {
            sugar = Sugar.valueOf("YES");
        }

        int numberOfShots = 0;
        Integer[] numberOfShotsOptions = new Integer[3];
        numberOfShotsOptions[0] = 1;
        numberOfShotsOptions[1] = 2;
        numberOfShotsOptions[2] = 3;

        while(numberOfShots<1) {
            try {
                int response = JOptionPane.showOptionDialog(null,"How many shots you would like in your coffee?",appName, JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, icon, numberOfShotsOptions, numberOfShotsOptions[0]);
                if (response == 0) {
                    numberOfShots = 1;
                } else if (response == 1) {
                    numberOfShots = 2;
                } else if (response == 2) {
                    numberOfShots = 3;
                }
            }
            catch (NumberFormatException e){
                JOptionPane.showMessageDialog(null,"Invalid input. Input needs to be an integer greater than 0. Please try again.");
            }
        }

        Set<String> extras = new HashSet<>();
        String extra = JOptionPane.showInputDialog(null, "What extra ingredient would you like to add to your coffee (syrups, ice cream, cream, etc)?. Enter d to finish your extras input.");
        if (extra == null) {
            System.exit(0);
        }
        while (!extra.equals("d")) {
            extra = extra.trim().toLowerCase();
            extras.add(extra);
            extra = JOptionPane.showInputDialog(null, "Any other extra would you like? When finish, enter d to complete extras input.");
        }

        float minPrice = -1, maxPrice = -1;
        while(minPrice==-1) {
            try {
                minPrice = Float.parseFloat(JOptionPane.showInputDialog(null,"What is the minimum price you would like for your coffee? ",appName,JOptionPane.QUESTION_MESSAGE));
            }
            catch (NumberFormatException e){
                JOptionPane.showMessageDialog(null,"Invalid input. The input must be a float. Please try again.");
            }
        }

        while(maxPrice<minPrice) {
            try {
                maxPrice = Float.parseFloat(JOptionPane.showInputDialog(null,"What is the maximum price you would like for your coffee (must be greater than or equal to the minimum price)? ",appName,JOptionPane.QUESTION_MESSAGE));
            }
            catch (NumberFormatException e){
                JOptionPane.showMessageDialog(null,"Invalid input. The input must be a float. Please try again.");
            }
            if(maxPrice<minPrice) JOptionPane.showMessageDialog(null,"Max price must be >= min price.");
        }

        Coffee userCoffee = new Coffee(0, "", 0, numberOfShots, sugar, milks, extras, "");
        userCoffee.setMinPrice(minPrice);
        userCoffee.setMaxPrice(maxPrice);

        return userCoffee;
    }

    /**
     * method to load all menu items from dataset menu.txt
     * @return a Menu object that has a list allCoffees containing all the coffees from the menu.txt dataset
     * this method was sourced and adapted from COSC120 Lecture 4 SeekAGeek.java loadGeeks
     */
    private static Menu loadItems() {
        Menu menu = new Menu();
        Path path = Path.of(filePath);

        List<String> itemData = null;
        try{
            itemData = Files.readAllLines(path);
        }catch (IOException io){
            System.out.println("The file could not be loaded. Check file path is correct. Terminating.\nError message: "+io.getMessage());
            System.exit(0);
        }

        for (int i=1;i<itemData.size();i++) {
            String[] elements = itemData.get(i).split("\\[");
            String[] itemInfo = elements[0].split(",");
            long itemId = Long.parseLong(itemInfo[0]);
            String itemName = itemInfo[1].trim();
            float price = Float.parseFloat(itemInfo[2]);
            int numberOfShots = Integer.parseInt(itemInfo[3]);
            Sugar sugar = Sugar.valueOf(itemInfo[4].trim().toUpperCase());

            String[] milksString = elements[1].replace("],","").split(",");
            List<Milk> milks = new ArrayList<>();
            for (String milkString:milksString) {
                Milk milk = Milk.OAT;
                if (milkString.equals("Full-cream")) {
                    milk = Milk.valueOf("WHOLE");
                } else if (milkString.equals("")) {
                    milk = Milk.valueOf("NONE");
                } else {
                    milkString = milkString.trim().toUpperCase();
                    try {
                        milk = Milk.valueOf(milkString);
                    } catch (IllegalArgumentException e) {
                        System.out.println("There is an error with the argument milkString passed in the Milk.valueOf() method");
                    }

                }
                milks.add(milk);
            }

            String[] extraString = elements[2].replace("],","").trim().toLowerCase().split(",");
            Set<String> extras = new HashSet<>();
            for (String extra: extraString) {
                extra = extra.trim();
                extras.add(extra);
            }

            String description = elements[3].replace("]", "");

            Coffee coffee = new Coffee(itemId, itemName, price, numberOfShots, sugar, milks, extras, description);
            menu.addItem(coffee);
        }
        return menu;
    }

    /**
     * a method to write the order details of a geek to a file, including the geek details (name, phone number) and the geek chosen coffee drink details
     * @param geek the Geek object that has the details of the geek who placed the order
     * @param chosenCoffee the Coffee object that has the details of the chosen coffee that the geek wants
     * @param userCoffee the Coffee object that has some details needed for the order (e.g. milk choice)
     */
    private static void writeOrderToFile(Geek geek, Coffee chosenCoffee, Coffee userCoffee) {
        String filePath = "./Assignment1/order_" + geek.phoneNumber() + ".txt";
        Path path = Path.of(filePath);
        /* Order details:
        Name: Dr. Walter Shepman
        Order number: 0486756465
        Item: Mocha (30213)
        Milk: Full-cream */

        String milkChoice = Arrays.toString(userCoffee.getMilks().toArray()).replace("[", "").replace("]","");
        String lineToWrite = "Order details:\n" +
                "Name: " + geek.name() + "\n" +
                "Order number: " + geek.phoneNumber() + "\n" +
                "Item: " + chosenCoffee.getItemName() + " (" + chosenCoffee.getItemId() + ")\n" +
                "Milk: " + milkChoice;

        try {
            Files.writeString(path, lineToWrite);
        }catch (IOException io){
            System.out.println("File could not be written. \nError message: "+io.getMessage());
            System.exit(0);
        }
    }

    /**
     * a method that get the geek details from user input and store it in a Geek object
     * @return a Geek object that has all the details of the geek who wants to order a drink
     */
    private static Geek getGeekDetails(){

        String name;
        do{
            name = JOptionPane.showInputDialog("Please enter your full name (in format Firstname Surname): ");
            if(name==null) System.exit(0);
        } while(!isValidFullName(name));

        String phoneNumber;
        do{
            phoneNumber = JOptionPane.showInputDialog("Please enter your phone number (10-digit number in the format 0412345678): ");
            if(phoneNumber==null) System.exit(0);}
        while(!isValidPhoneNumber(phoneNumber));

        return new Geek(name, phoneNumber);
    }

    /**
     * regex for full name in Firstname Surname format
     * @param fullName the geek full name entered by the geek
     * @return true if name matches regex/false if not
     * this method was sourced and adapted from COSC120 Lecture 4 SeekAGeek.java isValidFullName
     */
    public static boolean isValidFullName(String fullName) {
        String regex = "^[A-Z][a-z]+\\s[A-Z][a-zA-Z]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fullName);
        return matcher.matches();
    }

    /**
     * a regex matcher that ensures that the user's entry starts with a 0 and is followed by 9 digits
     * @param phoneNumber the geek phone number entered by the geek
     * @return true if phone number matches regex/false if not
     * this method was sourced and adapted from COSC120 Lecture 4 SeekAGeek.java isValidPhoneNumber
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile("^0\\d{9}$");
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}
