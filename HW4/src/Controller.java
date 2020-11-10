import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Controller extends JPanel {

    public Controller(MyCalendar model) {
        JButton previousDayButton = new JButton("<");
        previousDayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GregorianCalendar cal = model.getGregorianCalendar();
                cal.add(Calendar.DAY_OF_MONTH, -1);
                model.updateListeners(cal);
            }
        });
        JButton nextDayButton = new JButton(">");
        nextDayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GregorianCalendar cal = model.getGregorianCalendar();
                cal.add(Calendar.DAY_OF_MONTH, 1);
                model.updateListeners(cal);
            }
        });
        JButton quitButton = new JButton("Quit");
        quitButton.setPreferredSize(new Dimension(60, 30));
        quitButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5, true));
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.quit();
                Runtime.getRuntime().exit(0);
            }
        });
        add(previousDayButton);
        add(nextDayButton);
        add(quitButton);
    }
}
