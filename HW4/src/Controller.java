import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @Deprecated
 * Controller class represents the controller for the application.
 * @author Rohan Surana
 * @version 1.0.0 11/21/2020
 */
@Deprecated
public class Controller extends JPanel {

    public Controller(MyCalendar model) {
        View view = new View(model);
        JButton previousDayButton = new JButton("<");
        previousDayButton.addActionListener(e -> {
            GregorianCalendar cal = model.getGregorianCalendar();
            cal.add(Calendar.DAY_OF_MONTH, -1);
            model.updateListeners(cal);
            view.createButtons(0, cal);
        });
        JButton nextDayButton = new JButton(">");
        nextDayButton.addActionListener(e -> {
            GregorianCalendar cal = model.getGregorianCalendar();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            model.updateListeners(cal);
            view.createButtons(0, cal);
        });
        JButton quitButton = new JButton("Quit");
        quitButton.setPreferredSize(new Dimension(60, 30));
        quitButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5, true));
        quitButton.addActionListener(e -> {
            model.quit();
            Runtime.getRuntime().exit(0);
        });
        add(previousDayButton);
        add(nextDayButton);
        add(quitButton);
    }
}
