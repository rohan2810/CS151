import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;

public class View extends JTextArea {

    private final Model model;
    private ArrayList<String> text;
    ChangeListener changeListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            text = model.getText();
            StringBuilder s = new StringBuilder();
            for (String value : text) {
                s.append(value).append("\n");
            }
            setText(s.toString());
            repaint();
        }
    };

    public View(Model model) {
        this.model = model;
        text = model.getText();
        this.setEditable(false);
        this.setRows(20);
        StringBuilder s = new StringBuilder();
        for (String value : text) {
            s.append(value).append("\n");
        }
        setText(s.toString());
    }

}
