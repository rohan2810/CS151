import javax.swing.*;
import java.util.ArrayList;

/**
 * A class for testing an implementation of the MVC pattern.
 */
public class MVCTester {
    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        jFrame.setSize(300, 300);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        ArrayList<String> text = new ArrayList<>();
        Model model = new Model(text);
        View view = new View(model);
        model.updateView(view.changeListener);
        Controller controller = new Controller(model);
        jFrame.add(view);
        jFrame.add(controller);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);

    }
}
