/**
 * Reservation class represents an reservation.
 * It contains of identifiers like 'name', 'seat', 'type', 'service_class' which can uniquely identify a reservation.
 * This class consists of constructor, and getters and setters.
 *
 * @author Rohan Surana
 * @version 1.0.0
 * @copyright 09-06-2020
 */


public class Reservation {
    private String name;
    private String allottedSeat;
    private String bookingType;
    private String service_class;

    public Reservation(String name, String allottedSeat, String bookingType, String service_class) {
        this.name = name;
        this.allottedSeat = allottedSeat;
        this.bookingType = bookingType;
        this.service_class = service_class;
    }

    public String getService_class() {
        return service_class;
    }

    public void setService_class(String service_class) {
        this.service_class = service_class;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAllottedSeat() {
        return allottedSeat;
    }

    public void setAllottedSeat(String allottedSeat) {
        this.allottedSeat = allottedSeat;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public String print() {
        return "Reservation{" +
                "name='" + name + '\'' +
                ", seat='" + allottedSeat + '\'' +
                ", type='" + bookingType + '\'' +
                '}';
    }
}
