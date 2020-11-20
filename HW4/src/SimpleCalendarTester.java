import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.time.LocalDate;

public class SimpleCalendarTester {
    public static void main(String[] args) throws FileNotFoundException {
        JFrame frame = new JFrame();
        final int FRAME_WIDTH = 700;
        final int FRAME_HEIGHT = 700;
//        frame.setLayout(new FlowLayout());
//        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLayout(new BorderLayout());
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);

        MyCalendar calender = new MyCalendar();
//        calender.loadEvents(args[0]);
        View view = new View(calender);
        calender.attach(view);
        Controller controller = new Controller(calender);
        frame.add(controller);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);


    }
}
