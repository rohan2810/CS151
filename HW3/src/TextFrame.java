import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * This class is mostly taken from Hortsmann's solutions.
 * Newly implemented:- the class now extends ChangeListener
 * A class for displaying the model as a column of textfields in a frame.
 */
public class TextFrame extends JFrame implements ChangeListener {
    DataModel dataModel;
    JTextField[] fieldList;

    /**
     * Constructs a JFrame that contains the textfields containing the data
     * in the model.
     *
     * @param d the model to display
     */
    public TextFrame(DataModel d) {
        dataModel = d;

        final Container contentPane = this.getContentPane();
        setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        ArrayList<Double> a = dataModel.getData();
        fieldList = new JTextField[a.size()];

        // A listener for action events in the text fields
        ActionListener l = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Figure out which field generated the event
                JTextField c = (JTextField) e.getSource();
                int i = 0;
                int count = fieldList.length;
                while (i < count && fieldList[i] != c)
                    i++;

                String text = c.getText().trim();

                try {
                    double value = Double.parseDouble(text);
                    dataModel.update(i, value);
                } catch (Exception exc) {
                    c.setText("Error.  No update");
                }
            }
        };

        final int FIELD_WIDTH = 11;
        for (int i = 0; i < a.size(); i++) {
            JTextField textField = new JTextField(a.get(i).toString(), FIELD_WIDTH);
            textField.addActionListener(l);
            add(textField);
            fieldList[i] = textField;
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        ArrayList<Double> data = dataModel.getData();
        for (int i = 0; i < fieldList.length; i++) {
            fieldList[i].setText(data.get(i).toString());
            fieldList[i].repaint();
        }
    }
}