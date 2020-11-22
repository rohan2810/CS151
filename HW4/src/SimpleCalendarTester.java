/**
 * @author Rohan Surana
 * @version 1.0.0 11/21/2020
 */
public class SimpleCalendarTester {
    /**
     * Main for testing the application.
     *
     * @param args
     */
    public static void main(String[] args) {
        MyCalendar calender = new MyCalendar();
        View view = new View(calender);
        calender.attach(view);


    }
}
