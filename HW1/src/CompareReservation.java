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
     * This compare method is an overridden method from Comparator interface.
     * It compares two reservations on basis of their allotted seat.
     *
     * @param r1 First reservation
     * @param r2 Second reservation
     * @return the result of the comparison (-1,0,1)
     */
    @Override
    public int compare(Reservation r1, Reservation r2) {
        return r1.getAllottedSeat().compareTo(r2.getAllottedSeat());
    }
}
