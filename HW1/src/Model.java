import javafx.util.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Rohan Surana
 * @version 1.0.0
 * @copyright 09-06-2020
 */
public class Model {
    static Pair<HashMap<Integer, List<String>>, HashMap<Integer, List<String>>> createNewStr() {
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
        HashMap<Integer, List<String>> business = new HashMap<>();

        for (int i = 0; i < 2; i++) {
            business.put(i + 1, bus);
        }
        for (int i = 9; i < 29; i++) {
            economy.put(i + 1, econ);
        }
        return new Pair<>(economy, business);
    }

    public void createFromExisting() {

    }
}
