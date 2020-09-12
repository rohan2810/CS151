/**
 * HOMEWORK 1
 *
 * @author Rohan Surana
 * @version 1.0.0
 */

import java.util.Scanner;

/**
 * ReservationSystem class is responsible for testing the solution.
 * It contains the main method which interacts with the user
 *
 * @author Rohan Surana
 * @version 1.0.0
 */
public class ReservationSystem {
    /**
     * This main method is responsible for interacting with the user.
     * It creates an single instance of ReservationUtil per run which is used for providing the functionality to the user.
     *
     * @param args takes the file name as input.
     *             If the file is present, the model is updated with the reservations in the file.
     *             If the file is not there, it creates one and updated it at the end of the program.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);
        ReservationUtil reservation = new ReservationUtil(sc);
        boolean alrExists = reservation.readFromExistingFile(args[0]);
        reservation.createNewData();
        if (alrExists) {
            reservation.updateModelAfterReadingFromFile();
        }
        System.out.print("\nSelect one of the following options:\n"
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
            System.out.print("\nSelect one of the following options:\n"
                    + "Add [P]assenger, Add [G]roup, [C]ancel Reservations, Print Seating [A]vailability Chart, Print [M]anifest, [Q]uit \n");

        }


    }
}
