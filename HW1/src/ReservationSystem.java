import java.util.Scanner;

/**
 * @author Rohan Surana
 * @version 1.0.0
 * @copyright 09-06-2020
 */
public class ReservationSystem {

    public static void main(String[] args) throws Exception {

        ReservationUtil reservation = new ReservationUtil();
        boolean alrExists = reservation.readFromExistingFile(args[0]);
        reservation.createNewData();
        if (alrExists) {
            reservation.updateModelAfterReadingFromFile();
        }
        Scanner sc = new Scanner(System.in);
        System.out.print("\n\nSelect one of the following options:\n"
                + "Add [P]assenger, Add [G]roup, [C]ancel Reservations, Print Seating [A]vailability Chart, Print [M]anifest, [Q]uit \n");
        while (sc.hasNext()) {
            String operation = sc.nextLine();
            switch (operation) {
                case "P": {
                    reservation.addPassenger();
                    break;
                }
                case "G": {
                    reservation.addGroup();
                    break;
                }
                case "C": {
                    reservation.cancelReservation();
                    break;
                }
                case "A": {
                    reservation.getAvailability();
                    break;
                }
                case "M": {
                    reservation.printManifest();
                    break;
                }
                case "Q": {
                    reservation.saveToFile();
                    System.out.println("Bye! Exiting Application");
                    return;
                }
                default:
                    System.out.println("Invalid Option Selected!, Try again");
            }
            System.out.print("\n\nSelect one of the following options:\n"
                    + "Add [P]assenger, Add [G]roup, [C]ancel Reservations, Print Seating [A]vailability Chart, Print [M]anifest, [Q]uit \n");

        }


    }
}
