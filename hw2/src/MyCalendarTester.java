import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Scanner;

public class MyCalendarTester {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(System.in);
        MyCalendar calender = new MyCalendar(sc);
        calender.loadEvents(args[0]);
        LocalDate today = LocalDate.now();
        calender.printMonth(today, true);

        System.out.println("\nSelect one of the following options:\n"
                + "[V]iew by  [C]reate, [G]o to [E]vent list [D]elete  [Q]uit");

        while (sc.hasNext()) {
            String operation = sc.nextLine();
            switch (operation.toUpperCase()) {
                case "C": {
                    calender.createEvent();
                    break;
                }
                case "V": {
                    calender.viewBy();
                    break;
                }
                case "G": {
                    calender.goTo();
                    break;
                }
                case "E": {
                    calender.eventList();
                    break;
                }
                case "D": {
                    calender.delete();
                    break;
                }
                case "Q": {
                    calender.quit();
                    System.out.println("Bye! Exiting Application");
                    return;
                }
                default:
                    System.out.println("Invalid Option Selected!, Try again");
            }
            System.out.println("Select one of the following options:\n"
                    + "[V]iew by  [C]reate, [G]o to [E]vent list [D]elete  [Q]uit");
        }

    }
}
