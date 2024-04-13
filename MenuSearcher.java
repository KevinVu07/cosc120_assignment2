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

        // Create a Beverage object userBeverage with preferred criteria input from the user
        Beverage userBeverage = getUserCriteria();

        List<Beverage> matchBeverages = menu.findDreamBeverage(userBeverage);
        if(matchBeverages.size()>0){ //if the list is not empty
            //this will map the item names and item ID to the corresponding Item objects
            Map<String, Beverage> options = new HashMap<>();
            //build a text description of the menu items for the user to view
            StringBuilder infoToShow = new StringBuilder("Matches found!! The following items meet your criteria:\n");
            for (Beverage matchBeverage : matchBeverages) {
                StringBuilder displayMenu = matchBeverage.displayMenu();
                infoToShow.append("\n").append(displayMenu);
                options.put(matchBeverage.getItemName() + " (" + matchBeverage.getItemId() + ")", matchBeverage);
            }
            String order = (String) JOptionPane.showInputDialog(null,infoToShow+"\n\nPlease select which menu item you would like to order",appName, JOptionPane.QUESTION_MESSAGE,icon,options.keySet().toArray(), "");
            if(order==null) System.exit(0);
            else{
                Beverage chosenBeverage = options.get(order);
                Geek customer = getGeekDetails();
                writeOrderToFile(customer, chosenBeverage, userBeverage);
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
    private static Beverage getUserCriteria(){

        Milk milk = (Milk) JOptionPane.showInputDialog(null,"What milk type would you like in your coffee?",appName, JOptionPane.QUESTION_MESSAGE,icon,Milk.values(),Milk.WHOLE);
        if(milk==null) System.exit(0);
        Set<Milk> milks= new HashSet<>();
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

        Beverage userBeverage = new Beverage(0, "", 0, numberOfShots, sugar, milks, extras, "");
        userBeverage.setMinPrice(minPrice);
        userBeverage.setMaxPrice(maxPrice);

        return userBeverage;
    }


    private static DreamBeverage searchForBeverage(TypeOfBeverage type){
        int numberOfShots = 0;
        int temperature = 0;
        int steepingTime = 0;
        if (type == TypeOfBeverage.BEVERAGE) {
            Integer[] numberOfShotsOptions = {1, 2, 3};

            while(numberOfShots<1) {
                numberOfShots = (int) JOptionPane.showInputDialog(null,"How many shots you would like in your coffee?",appName, JOptionPane.QUESTION_MESSAGE, icon, numberOfShotsOptions, numberOfShotsOptions[0]);
            }
        } else if (type == TypeOfBeverage.TEA) {
            String[] tempOptions = {"80 degrees: For a mellow, gentler taste", "85 degrees: For slightly sharper than mellow", "90 degrees: Balanced, strong but not too strong", "95 degrees: Strong, but not acidic", "100 degrees: For a bold, strong flavour"};
            while(temperature < 1) {
                String response = (String) JOptionPane.showInputDialog(null, "Which temperature would you like for your tea?", appName, JOptionPane.QUESTION_MESSAGE, icon, tempOptions, tempOptions[0]);
                // use switch case to assign proper values to the temperature variable
                // to do
            }
        }

        Map<Criteria,Object> criteriaMap = new HashMap<>();
        //EDIT 13: add the user's preferred type of relationship to the map
        criteriaMap.put(Criteria.TYPE_OF_RELATIONSHIP,type);

        StarSign starSign = (StarSign) JOptionPane.showInputDialog(null,"Please select your preferred star sign: ",
                appName, JOptionPane.QUESTION_MESSAGE,icon,StarSign.values(),StarSign.CAPRICORN);
        if(starSign==null) System.exit(0);
        if(!starSign.equals(StarSign.NA)) criteriaMap.put(Criteria.STAR_SIGN,starSign);

        Gender gender = (Gender) JOptionPane.showInputDialog(null,"Please select your preferred gender: ",appName, JOptionPane.QUESTION_MESSAGE,icon,Gender.values(),Gender.OTHER);
        if(gender==null) System.exit(0);
        //EDIT 2: only add the user’s Gender selection to the criteria Map if it is not NA.
        if(!gender.equals(Gender.NA)) criteriaMap.put(Criteria.GENDER,gender);

        if(type==TypeOfDreamGeek.STUDY_BUDDY) {
            String[] options = {"Institution and course", "Subject area"};
            String selectedOption = (String) JOptionPane.showInputDialog(null, "How would you like to search for a study buddy?",
                    appName, JOptionPane.QUESTION_MESSAGE, icon, options, "");
            if (selectedOption.equals(options[0])) {
                String institution = (String) JOptionPane.showInputDialog(null, "Please enter the institution you're interested in: ",
                        appName, JOptionPane.QUESTION_MESSAGE, icon, null, null);
                if(institution==null) System.exit(0);
                institution=institution.toLowerCase();
                String course = (String) JOptionPane.showInputDialog(null, "Please enter the course you're interested in: ",
                        appName, JOptionPane.QUESTION_MESSAGE, icon, null, null);
                if(course==null) System.exit(0);
                course=course.toLowerCase();
                criteriaMap.put(Criteria.INSTITUTION,institution);
                criteriaMap.put(Criteria.COURSE,course);
            } else {
                String subjectArea = (String) JOptionPane.showInputDialog(null, "Please enter your subject area: ",
                        appName, JOptionPane.QUESTION_MESSAGE, icon, null, null);
                if(subjectArea==null) System.exit(0);
                subjectArea=subjectArea.toUpperCase();
                criteriaMap.put(Criteria.SUBJECT_AREA,subjectArea);
            }
        }
        //EDIT 9: add an else if(OldSchoolFriend statement), and request user input for
        // school and graduation year. Add these to the Map.
        else if(type==TypeOfDreamGeek.OLD_SCHOOL_FRIEND){
            String school = (String) JOptionPane.showInputDialog(null, "At what school did your old friend attend? ",
                    appName, JOptionPane.QUESTION_MESSAGE, icon, null, null);
            if(school==null) System.exit(0);
            criteriaMap.put(Criteria.SCHOOL,school);
            //add error catching and input validation, e.g. year must start with 19 or 20 and be 4 digits long.
            String graduationYear = (String) JOptionPane.showInputDialog(null, "During which year did your old friend graduate? ",
                    appName, JOptionPane.QUESTION_MESSAGE, icon, null, null);
            if(graduationYear==null) System.exit(0);
            criteriaMap.put(Criteria.GRADUATION_YEAR,graduationYear);
        }
        else{
            Religion religion = (Religion) JOptionPane.showInputDialog(null,"Please select your preferred religion:",
                    appName, JOptionPane.QUESTION_MESSAGE,icon,Religion.values(),Religion.UNAFFILIATED);
            if(religion==null) System.exit(0);
            if(!religion.equals(Religion.NA)) criteriaMap.put(Criteria.RELIGION,religion);

            Set<String> favouriteGames = getUserFavouriteCollection("playing computer games","game");
            if(favouriteGames.size()!=0) criteriaMap.put(Criteria.FAVOURITE_COMPUTER_GAMES,favouriteGames);

            Set<String> favouriteTVShows = getUserFavouriteCollection("binging tv shows","tv show");
            if(favouriteTVShows.size()!=0) criteriaMap.put(Criteria.FAVOURITE_TV_SHOWS,favouriteTVShows);

            //EDIT 6: use our good design to easily get user hobby data
            Set<String> hobbies = getUserFavouriteCollection("hobbies","hobby");
            if(hobbies.size()!=0) criteriaMap.put(Criteria.HOBBIES,hobbies);

            if(type.equals(TypeOfDreamGeek.MORE_THAN_A_FRIEND)){
                ValentinesGifts valentinesGift = (ValentinesGifts) JOptionPane.showInputDialog(null, "The kind of Valentine's gift a Geek loves " +
                                "says a lot about their personality. From the list, which would you be most likely to buy for your dream geek on Valentine's Day?",
                        appName, JOptionPane.QUESTION_MESSAGE, icon, ValentinesGifts.values(), ValentinesGifts.CHESS_SET);
                if(valentinesGift==null) System.exit(0);
                criteriaMap.put(Criteria.VALENTINES_GIFT,valentinesGift);

                RomanticActivities favouriteRomanticActivity = (RomanticActivities) JOptionPane.showInputDialog(null, "Select your favourite " +
                        "romantic activity from the list.", appName, JOptionPane.QUESTION_MESSAGE, icon, RomanticActivities.values(), RomanticActivities.BINGE_TV_SHOWS);
                if(favouriteRomanticActivity==null) System.exit(0);
                criteriaMap.put(Criteria.ROMANTIC_ACTIVITY,favouriteRomanticActivity);
            }
        }
        return new DreamGeek(minAge,maxAge,criteriaMap);
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


    /**
     * a method to write the order details of a geek to a file, including the geek details (name, phone number) and the geek chosen coffee drink details
     * @param geek the Geek object that has the details of the geek who placed the order
     * @param chosenBeverage the Coffee object that has the details of the chosen coffee that the geek wants
     * @param userBeverage the Coffee object that has some details needed for the order (e.g. milk choice)
     */
    private static void writeOrderToFile(Geek geek, Beverage chosenBeverage, Beverage userBeverage) {
        String filePath = "./Assignment1/order_" + geek.phoneNumber() + ".txt";
        Path path = Path.of(filePath);
        /* Order details:
        Name: Dr. Walter Shepman
        Order number: 0486756465
        Item: Mocha (30213)
        Milk: Full-cream */

        String milkChoice = Arrays.toString(userBeverage.getMilks().toArray()).replace("[", "").replace("]","");
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
