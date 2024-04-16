import java.util.*;

public class DreamBeverage {
    private float minPrice;
    private float maxPrice;
    private final Map<Criteria,Object> criteria;

    /**
     * This constructor is used to create a user's dream beverage
     * @param minPrice the lowest price a user desires
     * @param maxPrice the highest price a user desires
     * @param criteria a Map containing the criteria used to compare DreamBeverages
     */
    public DreamBeverage(float minPrice, float maxPrice, Map<Criteria, Object> criteria) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        if(criteria==null) this.criteria=new HashMap<>();
        else this.criteria = new HashMap<>(criteria);
    }

    /**
     * This constructor is used to create the 'dream' aspects of real beverages
     * @param criteria a Map containing the criteria used to compare DreamBeverages
     */
    public DreamBeverage(Map<Criteria,Object> criteria){
        if(criteria==null) this.criteria=new LinkedHashMap<>();
        else this.criteria = new LinkedHashMap<>(criteria);
        minPrice=-1;
        maxPrice=-1;
    }

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

    /**
     * @return the entire map of criteria keys, and values
     */
    public Map<Criteria, Object> getAllCriteria() {
        return new HashMap<>(criteria);
    }

    /**
     * @param key a Criteria (enum) constant representing a search criteria
     * @return a value stored in the map at a given key
     */
    public Object getCriteria(Criteria key){return getAllCriteria().get(key);}

    /**
     * @return a String describing all the 'dream' features of a beverage
     */
    public String getDescription(){
        StringBuilder description = new StringBuilder();
        for(Criteria key: criteria.keySet()) description.append("\n").append(key).append(": ").append(getCriteria(key));
        return description.toString();
    }

    /**
     * compares a DreamBeverage against a real DreamBeverage features
     * @param realBeverage a Beverage object representing a real, registered beverage
     * @return true if the real Beverage's 'dream' features match this DreamBeverage's features
     */
    public boolean matches(DreamBeverage realBeverage) {
        for(Criteria key : realBeverage.getAllCriteria().keySet()) {
            if(this.getAllCriteria().containsKey(key)){
                if(getCriteria(key) instanceof Collection<?> && realBeverage.getCriteria(key) instanceof Collection<?>){
                    Set<Object> intersect = new HashSet<>((Collection<?>) realBeverage.getCriteria(key));
                    intersect.retainAll((Collection<?>) getCriteria(key));
                    if(intersect.isEmpty()) return false;
                }
                else if(realBeverage.getCriteria(key) instanceof Collection<?> && !(getCriteria(key) instanceof Collection<?>)){
                    if(!((Collection<?>) realBeverage.getCriteria(key)).contains(getCriteria(key))) return false;
                }
                else if(!(realBeverage.getCriteria(key) instanceof Collection<?>) && getCriteria(key) instanceof Collection<?>){
                    if(!((Collection<?>) getCriteria(key)).contains(realBeverage.getCriteria(key))) return false;
                }
                //SUGGESTION: you could add logic hear to see if a real geek's singular value is contained in a dream geek's collection value
                //AS PREVIOUSLY DONE: finally, if they're both singular, check for equality
                else if(!getCriteria(key).equals(realBeverage.getCriteria(key))) return false;
            }
        }
        return true;
    }
}
