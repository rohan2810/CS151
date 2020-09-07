import java.util.Comparator;

public class CompareReservation implements Comparator<Reservation> {
    @Override
    public int compare(Reservation r1, Reservation r2) {
        return r1.getSeat().compareTo(r2.getSeat());
    }
}
