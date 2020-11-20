import java.util.ArrayList;

/**
 * This class is taken from Hortsmann's solutions.
 * A class for testing an implementation of the Observer pattern.
 */
public class ObserverTester {
    /**
     * Creates a DataModel and attaches barchart and textfield listeners
     *
     * @param args unused
     */
    public static void main(String[] args) {
        ArrayList<Double> data = new ArrayList<>();

        data.add(33.0);
        data.add(44.0);
        data.add(22.0);
        data.add(22.0);



        data.add(45.6);

        Double d = data.get(0);
        Double j = d.doubleValue();





        DataModel model = new DataModel(data);
        TextFrame frame = new TextFrame(model);
        BarFrame barFrame = new BarFrame(model);
        model.attach(barFrame);
        model.attach(frame);
    }
}