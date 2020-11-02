import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;

public class Model {
    ArrayList<String> text;
    ArrayList<ChangeListener> listeners;

    public Model(ArrayList<String> text) {
        this.text = text;
        listeners = new ArrayList<>();
    }

    public ArrayList<String> getText() {
        return text;
    }

    public void updateView(ChangeListener changeListener) {
        listeners.add(changeListener);
    }

    public void update(String newText) {
        text.add(newText);
        for (ChangeListener cl : listeners) {
            cl.stateChanged(new ChangeEvent(this));
        }
    }
}
