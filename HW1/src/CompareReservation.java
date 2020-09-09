import java.util.Comparator;

/**
 * CompareReservation is used to compare two reservations by their allottedSeatNo.
 * It implements the Comparator interface.
 *
 * @author Rohan Surana
 * @version 1.0.0
 * @copyright 09-06-2020
 */
public class CompareReservation implements Comparator<Reservation> {
    /**
     * This compare method is an overridden
     * @param r1
     * @param r2
     * @return
     */
    @Override
    public int compare(Reservation r1, Reservation r2) {
        return r1.getAllottedSeat().compareTo(r2.getAllottedSeat());
    }
}
