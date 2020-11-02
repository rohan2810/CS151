import javax.swing.*;
import java.awt.*;

public class Controller extends JPanel {
    private final JTextField jTextField;
    String text;

    public Controller(Model model) {
        this.setLayout(new FlowLayout());
        JButton jButton = new JButton("ADD");
        jTextField = new JTextField(20);

        jButton.addActionListener(e -> {
            text = jTextField.getText();
            model.update(text);
            jTextField.setText("");
        });

        add(jTextField);
        add(jButton);
        this.setVisible(true);
    }
}
