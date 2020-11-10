/**
 * TimeInterval is used to check if two events are conflicting.
 *
 * @author Rohan Surana
 * @version 1.0.0 09/22/2020
 */
public class TimeInterval {
    /**
     * This static method takes two events and returns if they are conflicting
     *
     * @param event1 First event
     * @param event2 Second event
     * @return boolean isConflicting?
     */
    public static boolean isConflicting(Event event1, Event event2) {
        if (event1.getDate().equals(event2.getDate())) {
            return (event1.getStartTime().compareTo(event2.getEndTime()) < 0 && event1.getStartTime().compareTo(event2.getEndTime()) > 0) ||
                    (event1.getEndTime().compareTo(event2.getStartTime()) > 0 && event1.getEndTime().compareTo(event2.getEndTime()) < 0);
        }
        return false;
    }
}
