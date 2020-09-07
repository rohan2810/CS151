/**
 * @author Rohan Surana
 * @version 1.0.0
 * @copyright 09-06-2020
 */


public class Reservation {
    private String name;
    private String seat;
    private String type;
    private String service_class;

    public Reservation(String name, String seat, String type, String service_class) {
        this.name = name;
        this.seat = seat;
        this.type = type;
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

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String print() {
        return "Reservation{" +
                "name='" + name + '\'' +
                ", seat='" + seat + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
