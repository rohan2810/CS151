public class TimeInterval {
    public static boolean isConflicting(Event event1, Event event2) {
        if (event1.getDate().equals(event2.getDate())) {
            return (event1.getStartTime().compareTo(event2.getEndTime()) < 0 && event1.getStartTime().compareTo(event2.getEndTime()) > 0) ||
                    (event1.getEndTime().compareTo(event2.getStartTime()) > 0 && event1.getEndTime().compareTo(event2.getEndTime()) < 0);
        }
        return false;
    }
}
