import java.io.FileNotFoundException;

public class SimpleCalendarTester {
    public static void main(String[] args) throws FileNotFoundException {
        MyCalendar calender = new MyCalendar();
//        calender.loadEvents(args[0]);
        View view = new View(calender);
        calender.attach(view);


    }
}
