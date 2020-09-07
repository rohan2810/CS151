import java.util.Comparator;
/**
 * @author Rohan Surana
 * @version 1.0.0
 * @copyright 09-06-2020
 */
public class CompareReservation implements Comparator<Reservation> {
    @Override
    public int compare(Reservation r1, Reservation r2) {
        return r1.getSeat().compareTo(r2.getSeat());
    }
}
