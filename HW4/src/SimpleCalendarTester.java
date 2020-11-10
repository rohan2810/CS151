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
        LocalDate today = LocalDate.now();
        calender.printMonth(today, true);
        calender.attach(new View.MonthViewAndCreate(calender));
        calender.attach(new View.CalendarDayView(calender));

        Controller controller = new Controller(calender);
        View.MonthViewAndCreate monthViewAndCreate = new View.MonthViewAndCreate(calender);
        View.CalendarDayView dayView = new View.CalendarDayView(calender);

//        frame.add(controller);
//        frame.add(monthViewAndCreate);
////        frame.add(dayView);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setVisible(true);

        frame.add(controller);
        frame.add(monthViewAndCreate, BorderLayout.WEST);
//        frame.add(dayView, BorderLayout.EAST);
//        frame.add(calendar_header, BorderLayout.NORTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);


    }
}
