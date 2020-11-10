import javafx.util.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * This class creates a structure for the plane.
 *
 * @author Rohan Surana
 * @version 1.0.0 09/12/2020
 */
public class Model {
    /**
     * This static method(createNewStr) is responsible for creating a new Data Structure for the plane model.
     * The structure is as follow:
     * Pair :
     * Right:  (First)       -> HashMap<Integer, List<String>>   ==>
     * -> Integer          => Column: ( 1, 2 ) for first class
     * -> List of String   => Rows: (A,B,C,D) for first class
     * <p>
     * Left: (Economy)      -> HashMap<Integer, List<String>>  ==>
     * -> Integer          => Column: ( 10, 11, 12 ... 28, 29 ) for economy class
     * -> List of String   => Rows: ( A,B,C,D,E,F ) for economy class
     *
     * @return model --> Pair<HashMap<Integer, List<String>>, HashMap<Integer, List<String>>>
     */
    public static Pair<HashMap<Integer, List<String>>, HashMap<Integer, List<String>>> createNewStr() {
        List<String> econ = new LinkedList<>();
        econ.add("A");
        econ.add("B");
        econ.add("C");
        econ.add("D");
        econ.add("E");
        econ.add("F");

        List<String> bus = new LinkedList<>();
        bus.add("A");
        bus.add("B");
        bus.add("C");
        bus.add("D");

        HashMap<Integer, List<String>> economy = new HashMap<>();
        HashMap<Integer, List<String>> first = new HashMap<>();

        for (int i = 0; i < 2; i++) {
            first.put(i + 1, bus);
        }
        for (int i = 9; i < 29; i++) {
            economy.put(i + 1, econ);
        }
        return new Pair<>(economy, first);
    }

}
