import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Menu {
    //fields
    private final List<Coffee> allCoffees = new ArrayList<>();

    //default constructor used, therefore no need to declare it

    //methods
    /**
     * method to add a menu Coffee object to the database (allCoffees)
     * @param coffee a menu Coffee object
     */
    public void addItem(Coffee coffee){
        this.allCoffees.add(coffee);
    }

    /**
     * returns a collection of Coffee objects that meet all the customer's criteria
     * @param userCoffee a Coffee object representing a user's preferred Coffee
     * @return a collection list of matching Coffee objects
     * this method was sourced and adapted from COSC120 Lecture 4 AllGeeks.java findDreamGeek
     */
    public List<Coffee> findMatch(Coffee userCoffee){
        List<Coffee> matchingCoffees = new ArrayList<>();
        for(Coffee coffee : this.allCoffees){
            // check if the menu item contains user's chosen milk
            if(!coffee.getMilks().containsAll(userCoffee.getMilks())) continue;
            // check if the menu item sugar choice is same with user coffee sugar choice
            if(!coffee.getSugar().equals(userCoffee.getSugar())) continue;
            // check if the menu item number of shots is the same with user coffee number of shots
            if(coffee.getNumberOfShots() != userCoffee.getNumberOfShots()) continue;
            // check if the menu item has extra/s that is common with the user chosen extra/s
            if(userCoffee.getExtras().size()!=0) {
                Set<String> extrasInCommon = new HashSet<>(userCoffee.getExtras());
                extrasInCommon.retainAll(coffee.getExtras());
                if(extrasInCommon.size()==0) continue;
            }
            // check if the menu coffee price is in the same range with the user price range
            if(coffee.getPrice()<userCoffee.getMinPrice() || coffee.getPrice()>userCoffee.getMaxPrice()) continue;
            // if pass all check, add the menu coffee to the matchingCoffees list
            matchingCoffees.add(coffee);
        }
        // return the collection of matching menu coffee
        return matchingCoffees;
    }
}
