import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MenuSearcher {
    // fields
    private static final String filePath = "./assignment2/menu.txt";
    private final static String appName = "The Caffeinated Geek";
    private static Menu menu;
    private final static String iconPath = "./assignment2/icon.png";
    static ImageIcon icon = new ImageIcon(iconPath);

    /**
     * main method used to allow user to search The Caffeinated Geek database of coffee drink, and place an order request
     * @param args none required
     */
    public static void main(String[] args) {
        // load all items from menu.txt to a Menu object
        menu = loadItems();

        JOptionPane.showMessageDialog(null, "Welcome to The Caffeinated Geek!\n\tTo start, click OK.", appName, JOptionPane.QUESTION_MESSAGE, icon);

        TypeOfBeverage type = (TypeOfBeverage) JOptionPane.showInputDialog(null,"What type of beverage are you looking for (coffee/tea)?",
                appName,JOptionPane.QUESTION_MESSAGE,icon,TypeOfBeverage.values(),TypeOfBeverage.TEA);

        // Create a DreamBeverage object dreamBeverage with preferred criteria input from the user
        DreamBeverage dreamBeverage = searchForBeverage(type);

        processSearchResults(dreamBeverage);
    }

    private static DreamBeverage searchForBeverage(TypeOfBeverage type) {
        int numberOfShots = 0;
        int temperature = 0;
        int steepingTime = 0;

        Map<Criteria, Object> criteriaMap = new HashMap<>();

        // Add the user's preferred type of beverage to the map
        criteriaMap.put(Criteria.TYPE_OF_BEVERAGE, type);

        if (type == TypeOfBeverage.BEVERAGE) {
            // coffee number of shots
            Integer[] numberOfShotsOptions = {1, 2, 3};
            while (numberOfShots < 1) {
                numberOfShots = (int) JOptionPane.showInputDialog(null, "How many shots you would like in your coffee?", appName, JOptionPane.QUESTION_MESSAGE, icon, numberOfShotsOptions, numberOfShotsOptions[0]);
                criteriaMap.put(Criteria.NUMBER_OF_SHOTS, numberOfShots);
            }
        } else if (type == TypeOfBeverage.TEA) {
            // tea temperature
            String[] tempOptions = {"80 degrees: For a mellow, gentler taste", "85 degrees: For slightly sharper than mellow", "90 degrees: Balanced, strong but not too strong", "95 degrees: Strong, but not acidic", "100 degrees: For a bold, strong flavour", "SKIP: any will do"};
            while (temperature < 1) {
                String response = (String) JOptionPane.showInputDialog(null, "Which temperature would you like for your tea?", appName, JOptionPane.QUESTION_MESSAGE, icon, tempOptions, tempOptions[0]);
                // use switch case to assign proper values to the temperature variable
                if (!response.equals("SKIP: any will do")) {
                    switch (response) {
                        case "80 degrees: For a mellow, gentler taste" -> temperature = 80;
                        case "85 degrees: For slightly sharper than mellow" -> temperature = 85;
                        case "90 degrees: Balanced, strong but not too strong" -> temperature = 90;
                        case "95 degrees: Strong, but not acidic" -> temperature = 95;
                        case "100 degrees: For a bold, strong flavour" -> temperature = 100;
                    }
                    criteriaMap.put(Criteria.TEMPERATURE, temperature);
                } else {
                    break;
                }
            }

            // tea steeping time
            String[] steepingTimeOptions = {"1", "2", "3", "4", "5", "6", "7", "8", "SKIP: any will do"};
            while (steepingTime < 1) {
                String response = (String) JOptionPane.showInputDialog(null, "How long would you like the steeping time to be for your tea?", appName, JOptionPane.QUESTION_MESSAGE, icon, steepingTimeOptions, steepingTimeOptions[0]);
                // use switch case to assign proper values to the steeping time variable
                if (!response.equals("SKIP: any will do")) {
                    switch (response) {
                        case "1" -> steepingTime = 1;
                        case "2" -> steepingTime = 2;
                        case "3" -> steepingTime = 3;
                        case "4" -> steepingTime = 4;
                        case "5" -> steepingTime = 5;
                        case "6" -> steepingTime = 6;
                        case "7" -> steepingTime = 7;
                        case "8" -> steepingTime = 8;
                    }
                    criteriaMap.put(Criteria.STEEPING_TIME, steepingTime);
                } else {
                    break;
                }
            }
        }

        // Common criteria for both coffee and tea
        Milk milk = (Milk) JOptionPane.showInputDialog(null, "What milk type would you like in your coffee?", appName, JOptionPane.QUESTION_MESSAGE, icon, Milk.values(), Milk.WHOLE);
        if (milk == null) System.exit(0);
        // only add the user’s milk selection to the criteria Map if it is not I dont mind.
        if (!milk.equals(Milk.I_DONT_MIND)) criteriaMap.put(Criteria.MILK, milk);

        // Sugar choice
        Sugar sugar = (Sugar) JOptionPane.showInputDialog(null, "Would you like sugar with your drink?", appName, JOptionPane.QUESTION_MESSAGE, icon, Sugar.values(), Sugar.values()[0]);
        if (sugar == null) System.exit(0);
        // only add the user’s sugar selection to the criteria Map if it is not I dont mind.
        if (!sugar.equals(Sugar.I_DONT_MIND)) criteriaMap.put(Criteria.SUGAR, sugar);

        // Extra/s choice
        // Get all the extras options from the menu.txt file, separating into 2 different sets for coffee and tea
        Set<String> coffeeExtras = loadExtraOptions("beverage");
        Set<String> teaExtras = loadExtraOptions("tea");
        // convert the 2 sets above to array to be used in Joptionpane input below
        Object[] coffeeExtrasOptions = coffeeExtras.toArray();
        Object[] teaExtrasOptions = teaExtras.toArray();

        Set<String> userExtras = new HashSet<>();
        // show different extras options depending on if the user chose "tea" or "coffee" at the start of the program
        if (type == TypeOfBeverage.BEVERAGE) {
            String response = (String) JOptionPane.showInputDialog(null, "Which extra/s would you like for your coffee? (choose \"Finish\" or \"SKIP\" to complete this selection)", appName, JOptionPane.QUESTION_MESSAGE, icon, coffeeExtrasOptions, coffeeExtrasOptions[0]);
            while (!response.equals("Finish") || !response.equals("SKIP: any will do")) {
                userExtras.add(response);
                coffeeExtras.remove(response); // remove the extra option the user just selected off the list of extra offered to the user
                coffeeExtrasOptions = coffeeExtras.toArray();
                response = (String) JOptionPane.showInputDialog(null, "Any other extra would you like for your coffee?", appName, JOptionPane.QUESTION_MESSAGE, icon, coffeeExtrasOptions, coffeeExtrasOptions[0]);
            }
        }
        if (userExtras.size() > 0) criteriaMap.put(Criteria.EXTRAS, userExtras);

        float minPrice = -1, maxPrice = -1;
        while (minPrice == -1) {
            try {
                minPrice = Float.parseFloat(JOptionPane.showInputDialog(null, "What is the minimum price you would like for your coffee? ", appName, JOptionPane.QUESTION_MESSAGE));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. The input must be a float. Please try again.");
            }
        }

        while (maxPrice < minPrice) {
            try {
                maxPrice = Float.parseFloat(JOptionPane.showInputDialog(null, "What is the maximum price you would like for your coffee (must be greater than or equal to the minimum price)? ", appName, JOptionPane.QUESTION_MESSAGE));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. The input must be a float. Please try again.");
            }
            if (maxPrice < minPrice) JOptionPane.showMessageDialog(null, "Max price must be >= min price.");
        }

        return new DreamBeverage(minPrice, maxPrice, criteriaMap);
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
            String[] itemInfo = elements[0].split(","); // [type, menu item ID, menu item name, price, numberOfShots, temperature, steeping time, sugar]
            TypeOfBeverage typeOfBeverage = null;
            try {
                typeOfBeverage = TypeOfBeverage.valueOf(itemInfo[0].trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("There is an error with the argument passed in the TypeOfBeverage.valueOf() method");
            }
            long itemId = -1;
            try {
                itemId = Long.parseLong(itemInfo[1]);
            } catch (NumberFormatException e) {
                System.out.println("There is an error trying to import a beverage item ID. Terminating.\nError message: "+e.getMessage());
                System.exit(0);
            }
            String itemName = itemInfo[2].trim();
            float price = -1;
            try {
                price = Float.parseFloat(itemInfo[3]);
            } catch (NumberFormatException e) {
                System.out.println("There is an error trying to import an item price. Terminating.\nError message: "+e.getMessage());
                System.exit(0);
            }
            int numberOfShots = -1;
            if (typeOfBeverage == TypeOfBeverage.BEVERAGE) {
                try {
                    numberOfShots = Integer.parseInt(itemInfo[4]);
                } catch (NumberFormatException e) {
                    System.out.println("There is an error trying to import an item number of shots. Terminating.\nError message: "+e.getMessage());
                    System.exit(0);
                }
            }
            int temperature = -1;
            int steepingTime = -1;
            if (typeOfBeverage == TypeOfBeverage.TEA) {
                try {
                    temperature = Integer.parseInt(itemInfo[5]);
                } catch (NumberFormatException e) {
                    System.out.println("There is an error trying to import a tea temperature. Terminating.\nError message: "+e.getMessage());
                    System.exit(0);
                }
                try {
                    steepingTime = Integer.parseInt(itemInfo[6]);
                } catch (NumberFormatException e) {
                    System.out.println("There is an error trying to import a tea steeping time. Terminating.\nError message: "+e.getMessage());
                    System.exit(0);
                }
            }
            Sugar sugar = null;
            try {
                sugar = Sugar.valueOf(itemInfo[7].trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("There is an error with the argument passed in the Sugar.valueOf() method");
            }

            String[] milksString = elements[1].replace("],","").split(",");
            Set<Milk> milks = new HashSet<>();
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

            Map<Criteria,Object> criteriaMap = new LinkedHashMap<>();
            criteriaMap.put(Criteria.TYPE_OF_BEVERAGE, typeOfBeverage);
            if (numberOfShots >= 0) criteriaMap.put(Criteria.NUMBER_OF_SHOTS, numberOfShots);
            if (temperature >= 0) criteriaMap.put(Criteria.TEMPERATURE, temperature);
            if (steepingTime >= 0) criteriaMap.put(Criteria.STEEPING_TIME, steepingTime);
            criteriaMap.put(Criteria.SUGAR, sugar);
            criteriaMap.put(Criteria.MILK_LIST, milks);
            criteriaMap.put(Criteria.EXTRAS, extras);

            DreamBeverage dreamBeverage = new DreamBeverage(criteriaMap);
            Beverage beverage = new Beverage(itemId, itemName, price, description, dreamBeverage);

            menu.addItem(beverage);
        }
        return menu;
    }


    private static Set<String> loadExtraOptions(String type) {
        Menu menu = new Menu();
        Path path = Path.of(filePath);

        List<String> itemData = null;
        try {
            itemData = Files.readAllLines(path);
        } catch (IOException io) {
            System.out.println("The file could not be loaded. Check file path is correct. Terminating.\nError message: " + io.getMessage());
            System.exit(0);
        }

        Set<String> extraOptions = new LinkedHashSet<>();
        extraOptions.add("Finish");
        for (int i = 1; i < itemData.size(); i++) {
            String[] elements = itemData.get(i).split("\\[");
            String[] extras = elements[2].replace("],", "").trim().toLowerCase().split(",");
            for (String extra : extras) {
                extra = extra.trim();
                // check if type of beverage is coffee or tea, and then add the extra option to the coffee or tea extras hash sets accordingly
                String[] itemInfo = elements[0].split(",");
                String typeOfBeverage = itemInfo[0];
                if (typeOfBeverage.equals(type)) {
                    extraOptions.add(extra);
                }
            }
        }
        extraOptions.add("SKIP: any will do");
        return extraOptions;
    }

    public static void processSearchResults(DreamBeverage dreamBeverage){
        List<Beverage> matches = menu.findDreamBeverage(dreamBeverage);
        if(matches.size()>0){ //if the list is not empty
            //this will map the item names and item ID to the corresponding Item objects
            Map<String, Beverage> options = new HashMap<>();
            //build a text description of the menu items for the user to view
            StringBuilder infoToShow = new StringBuilder("Matches found!! The following items meet your criteria:\n");
            for (Beverage match : matches) {
                StringBuilder displayMenu = match.displayMenu();
                infoToShow.append("\n").append(displayMenu);
                options.put(match.getItemName() + " (" + match.getItemId() + ")", match);
            }
            String order = (String) JOptionPane.showInputDialog(null,infoToShow+"\n\nPlease select which menu item you would like to order",appName, JOptionPane.QUESTION_MESSAGE,icon,options.keySet().toArray(), "");
            if(order==null) System.exit(0);
            else{
                Beverage chosenBeverage = options.get(order);
                Geek customer = getGeekDetails();
                writeOrderToFile(customer, chosenBeverage);
                JOptionPane.showMessageDialog(null, "Thank you! Your order has been placed. " +
                        "We will bring your coffee out shortly.", appName, JOptionPane.QUESTION_MESSAGE, icon);
            }
        } else JOptionPane.showMessageDialog(null, "Unfortunately none of our menu items meet your criteria :(" +
                "\n\tTo exit, click OK.", appName, JOptionPane.QUESTION_MESSAGE, icon);
        System.exit(0);
    }


    /**
     * a method to write the order details of a geek to a file, including the geek details (name, phone number) and the geek chosen coffee drink details
     * @param geek the Geek object that has the details of the geek who placed the order
     * @param chosenBeverage the Coffee object that has the details of the chosen coffee that the geek wants
     */
    private static void writeOrderToFile(Geek geek, Beverage chosenBeverage) {
        String filePath = "./assignment2/order_" + geek.phoneNumber() + ".txt";
        Path path = Path.of(filePath);
        /* Order details:
        Name: Dr. Walter Shepman
        Order number: 0486756465
        Item: Mocha (30213)
        Milk: Full-cream */

        String milkChoice = (String) chosenBeverage.getGenericFeatures().getCriteria(Criteria.MILK);
        String lineToWrite = "Order details:\n" +
                "Name: " + geek.name() + "\n" +
                "Order number: " + geek.phoneNumber() + "\n" +
                "Item: " + chosenBeverage.getItemName() + " (" + chosenBeverage.getItemId() + ")\n" +
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

