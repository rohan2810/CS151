import java.util.Scanner;

public class MyCalenderTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        MyCalender calender = new MyCalender(sc);
        calender.printCalender();  //fix this
        //print calender here

        System.out.println("Select one of the following options:\n"
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
