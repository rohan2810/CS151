/**
 * HOMEWORK 2
 *
 * @author Rohan Surana
 * @version 1.0.0 09/22/2020
 */

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * MyCalenderTester class is responsible for testing the solution.
 * It contains the main method which interacts with the user
 *
 * @author Rohan Surana
 * @version 1.0.0 09/12/2020
 */
public class MyCalendarTester {
    /**
     * This main method is responsible for interacting with the user.
     * It creates an single instance of MyCalender per run which is used for providing the functionality to the user.
     *
     * @param args takes the file name as input.
     *             If the file is present, the events are loaded.
     *             If the file is not there, it creates one and updated it at the end of the program.
     */
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
