import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyCalender {
    private final Scanner sc;
    private HashMap<LocalDate, ArrayList<Event>> events;
    private HashMap<String, HashMap<LocalDate, ArrayList<Event>>> reoccurringEvents;

    public MyCalender(Scanner sc) {
        this.sc = sc;
        this.events = new HashMap<>();
        this.reoccurringEvents = new HashMap<>();
    }

    public void printCalender() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        System.out.println(new SimpleDateFormat("MMMM yyyy").format(calendar.getTime()));
        System.out.println(" S  M  T  W  T  F  S");

        StringBuilder initialSpace = new StringBuilder();
        for (int i = 0; i < dayOfWeek - 1; i++) {
            initialSpace.append("   ");
        }
        System.out.print(initialSpace);

        for (int i = 0, dayOfMonth = 1; dayOfMonth <= daysInMonth; i++) {
            for (int j = ((i == 0) ? dayOfWeek - 1 : 0); j < 7 && (dayOfMonth <= daysInMonth); j++) {
                System.out.printf("%2d ", dayOfMonth);
                dayOfMonth++;
            }
            System.out.println();
        }
    }

    public void loadEvents(String args) throws FileNotFoundException {
        File file = new File(args + ".txt");
        if (file.exists() && !file.isDirectory()) {
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNext()) {
                String name = fileScanner.nextLine();
                String[] details = fileScanner.nextLine().split("\\s+");
                if (details.length == 3) {
                    loadAndSaveEvents(name, details[0], details[1], details[2]);
                } else {
                    String[] actualDate = details[3].split("/");
                    int year = Integer.parseInt(actualDate[2]) + 2000;
                    int dayOfMonth = Integer.parseInt(actualDate[1]);
                    int month = Integer.parseInt(actualDate[0]);
                    LocalDate startDate = LocalDate.of(year, month, dayOfMonth);
                    LocalDate date = startDate;
                    String[] actualDate1 = details[4].split("/");
                    int year1 = Integer.parseInt(actualDate1[2]) + 2000;
                    int dayOfMonth1 = Integer.parseInt(actualDate1[1]);
                    int month1 = Integer.parseInt(actualDate1[0]);
                    LocalDate endDate = LocalDate.of(year1, month1, dayOfMonth1);
                    long weeks = ChronoUnit.WEEKS.between(date, endDate);
                    String days = details[0];  // event days For eg . TF
                    for (int i = 0; i < days.length(); i++) {
                        int w = (int) weeks;
                        date = LocalDate.of(year, month, dayOfMonth);
                        DayOfWeek dayOfWeek = date.getDayOfWeek();  // gets the day on the given starting day
                        String dayOfWeek2 = getDayOfWeek(days.substring(i, i + 1));  // gets the first two letters of that day
                        boolean sub = false;
                        while (!dayOfWeek2.equals(dayOfWeek.toString().substring(0, 2))) {
                            date = date.plusDays(1);
                            dayOfWeek = dayOfWeek.plus(1);
                            sub = true;
                        }
                        if (!sub) {
                            w = (int) (weeks + 1);
                        }
                        for (int j = 0; j < w; j++) {
                            loadAndSaveEventsReoccurring(name, details[0], date, details[1], details[2], startDate, endDate);
                            date = date.plusDays(7);
                        }
                    }
                }
            }
        } else {
            System.out.println("This is the first run! Any previous records not found.");
        }
        System.out.println("Successfully Loaded");

    }

    public void createEvent() {
        System.out.println("Enter the name of the event:");
        String name = sc.nextLine();
        System.out.println("Enter the date of the event in the format MM/DD/YYYY:");
        String date = sc.nextLine();
        System.out.println("Enter the starting time of the event in 24 hour format HH:MM :");
        String startingTime = sc.nextLine();
        System.out.println("Enter the ending time of the event in 24 hour format HH:MM :");
        String endingTime = sc.nextLine();
        LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        LocalTime parsedStartTime = LocalTime.parse(startingTime, DateTimeFormatter.ISO_LOCAL_TIME);
        LocalTime parsedEndTime = LocalTime.parse(endingTime, DateTimeFormatter.ISO_LOCAL_TIME);
        if (saveEvents(name, parsedDate, parsedStartTime, parsedEndTime)) return;
        for (Map.Entry<LocalDate, ArrayList<Event>> entry : events.entrySet()) {
            System.out.println(entry.getKey() + "  " + entry.getValue());
        }
    }

    private boolean saveEvents(String name, LocalDate parsedDate, LocalTime parsedStartTime, LocalTime parsedEndTime) {
        Event event = new Event(name, parsedDate, parsedStartTime, parsedEndTime);
        if ((event.getStartTime() == null || event.getEndTime() == null)) {
            System.out.println("Event not created!. Time cannot be null");
            return true;
        }
        if (event.getStartTime().compareTo(event.getEndTime()) > 0) {
            System.out.println("Event not created!");
            System.out.println("Start time has to be before the end time!");
            return true;
        }
        for (Map.Entry<LocalDate, ArrayList<Event>> entry : events.entrySet()) {
            for (Event e : entry.getValue()) {
                if (TimeInterval.isConflicting(e, event)) {
                    System.out.println("Event cannot be created. It conflicts with " + e.getName());
                    return true;
                }
            }
        }

        ArrayList<Event> list;
        if (events.containsKey(parsedDate)) {
            list = events.get(parsedDate);
        } else {
            list = new ArrayList<>();
        }
        list.add(event);
        events.put(parsedDate, list);
        return false;
    }

    private void loadAndSaveEvents(String name, String date, String startingTime, String endingTime) {

        String[] actualDate = date.split("/");
        int year = Integer.parseInt(actualDate[2]) + 2000;
        int dayOfMonth = Integer.parseInt(actualDate[1]);
        int month = Integer.parseInt(actualDate[0]);
        LocalDate date1 = LocalDate.of(year, month, dayOfMonth);
        Pair<LocalTime, LocalTime> times = timeFormatter(startingTime, endingTime);
        if (saveEvents(name, date1, times.getKey(), times.getValue())) return;
        for (Map.Entry<LocalDate, ArrayList<Event>> entry : events.entrySet()) {
            System.out.println(entry.getKey() + "  ");
            entry.getValue().forEach(x -> System.out.println(x.getName()));
        }
    }

    private void loadAndSaveEventsReoccurring(String name, String reoccursOn, LocalDate date, String startingTime, String endingTime, LocalDate startDate, LocalDate endDate) {
        Pair<LocalTime, LocalTime> times = timeFormatter(startingTime, endingTime);
        Event event = new Event(name, reoccursOn, date, times.getKey(), times.getValue(), startDate, endDate);
        if (event.getStartTime().compareTo(event.getEndTime()) > 0) {
            System.out.println("Event not created!");
            System.out.println("Start time has to be before the end time!");
            return;
        }
        // todo fix this
//        for (Map.Entry<LocalDate, ArrayList<Event>> entry : events.entrySet()) {
//            for (Event e : entry.getValue()) {
//                if (TimeInterval.isConflicting(e, event)) {
//                    System.out.println("Event cannot be created. It conflicts with " + e.getName());
//                    return;
//                }
//            }
//        }

        HashMap<LocalDate, ArrayList<Event>> map;
        if (reoccurringEvents.containsKey(name)) {
            map = reoccurringEvents.get(name);
        } else {
            map = new HashMap<>();
        }
        ArrayList<Event> list;
        if (map.containsKey(date)) {
            list = map.get(date);
        } else {
            list = new ArrayList<>();
        }
        list.add(event);
        map.put(date, list);
        reoccurringEvents.put(name, map);
        for (Map.Entry<LocalDate, ArrayList<Event>> entry : events.entrySet()) {
            System.out.println(entry.getKey() + "  ");
            entry.getValue().forEach(x -> System.out.println(x.getName()));
        }
    }

    public void goTo() {
        System.out.println("Enter the date [MM/DD/YYYY]");
        String date = sc.nextLine();
        LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        if (events.containsKey(parsedDate) || !events.isEmpty()) {
            ArrayList<Event> thatDay = events.get(parsedDate);
            System.out.println("All events on: " + date);
            thatDay.sort(Comparator.comparing(Event::getStartTime));
            thatDay.forEach(x -> System.out.println(x.getStartTime() + " - " + x.getEndTime() + " " + x.getName()));
        } else {
            System.out.println("No events found on the particular date! Try again");
        }
    }

    public void eventList() {
        System.out.println("ONE TIME EVENTS");
        List<LocalDate> dates = new ArrayList<>(events.keySet());
        Collections.sort(dates);
        int yearVal = 0;
        for (LocalDate date : dates) {
            ArrayList<Event> l = events.get(date);
            l.sort(Comparator.comparing(Event::getStartTime));
            for (Event event : l) {
                if (yearVal != event.getDate().getYear()) {
                    System.out.println(event.getDate().getYear());
                    yearVal = event.getDate().getYear();
                }
                System.out.println(
                        " "
                                + (event.getDate().getDayOfWeek().toString().substring(0, 1) + event.getDate().getDayOfWeek().toString().substring(1).toLowerCase())
                                + " "
                                + (event.getDate().getMonth().toString().substring(0, 1) + event.getDate().getMonth().toString().substring(1).toLowerCase())
                                + " "
                                + event.getDate().getDayOfMonth()
                                + " "
                                + event.getStartTime() + " - "
                                + event.getEndTime() + " "
                                + event.getName()
                );
            }
        }

        System.out.println("RECURRING EVENTS");
        List<String> names = new ArrayList<>(reoccurringEvents.keySet());
        Collections.sort(names);
        for (String name : names) {
            HashMap<LocalDate, ArrayList<Event>> l = reoccurringEvents.get(name);
            List<LocalDate> dates1 = new ArrayList<>(l.keySet());
            ArrayList<Event> l1 = l.get(dates1.get(0));
            Event event = l1.get(0);
            System.out.println(event.getName());
            System.out.println(
                    event.getReoccursOn() + " "
                            + event.getStartTime() + " "
                            + event.getEndTime() + " "
                            + event.getStartDate().format(DateTimeFormatter.ofPattern("MM/dd/yy")) + " "
                            + event.getEndDate().format(DateTimeFormatter.ofPattern("MM/dd/yy")));
        }
    }

    public void viewBy() {

    }

    public void delete() {
        System.out.println("Delete  [S]elected or [A]ll events or DeleteRecurring[DR]?");
        String type = sc.nextLine();
        switch (type.toUpperCase()) {
            case "S": {
                System.out.println("Enter the date [MM/DD/YYYY]");
                String date = sc.nextLine();
                LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                if (events.containsKey(parsedDate) || !events.isEmpty()) {
                    ArrayList<Event> thatDay = events.get(parsedDate);
                    System.out.println("All events on: " + date);
                    thatDay.sort(Comparator.comparing(Event::getStartTime));
                    thatDay.forEach(x -> System.out.println(x.getStartTime() + " - " + x.getEndTime() + " " + x.getName()));
                    System.out.println("Enter the name of the event to delete");
                    String toDelete = sc.nextLine();
                    AtomicBoolean contains = new AtomicBoolean(false);
                    thatDay.forEach(x -> {
                        if (x.getName().equals(toDelete)) {
                            contains.set(true);
                        }
                    });
                    thatDay.removeIf(value -> value.getName().equals(toDelete));
                    if (contains.get()) {
                        events.replace(parsedDate, thatDay);
                        if (events.get(parsedDate).size() == 0) {
                            events.remove(parsedDate);
                        }
                        System.out.println("Successfully removed " + toDelete);
                    } else {
                        System.out.println("No event found under name " + toDelete + ". Please Try Again!");
                    }

                } else {
                    System.out.println("No events found on the particular date! Try again");
                }
                break;
            }
            case "A": {
                System.out.println("Enter the date [MM/DD/YYYY]");
                String date = sc.nextLine();
                LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                if (events.isEmpty() || !events.containsKey(parsedDate)) {
                    System.out.println("No events found on the particular date! Try again");
                } else {
                    ArrayList<Event> thatDay = events.get(parsedDate);
                    System.out.println("All events for the day");
                    thatDay.sort(Comparator.comparing(Event::getStartTime));
                    thatDay.forEach(x -> System.out.println(x.getStartTime() + " - " + x.getEndTime() + " " + x.getName()));
                    int size = events.get(parsedDate).size();
                    events.remove(parsedDate);
                    System.out.println("Successfully Removed " + size + " events");
                }
                break;
            }
            case "DR": {
                System.out.println("Enter the name of the reoccurring event to delete");
                String toDelete = sc.nextLine();
                if (reoccurringEvents.containsKey(toDelete)) {
                    reoccurringEvents.remove(toDelete);
                    System.out.println("Successfully removed");
                } else {
                    System.out.println("No reoccurring events found under the given name. Please try again");
                }
            }
        }
    }

    public void quit() {
        System.out.println("Good Bye!");
        List<String> toWrite = new ArrayList<>();
        for (Map.Entry<LocalDate, ArrayList<Event>> entry : events.entrySet()) {
            for (Event e : entry.getValue()) {
                toWrite.add(e.getName());
                toWrite.add(
                        e.getDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + " " + e.getStartTime() + " " + e.getEndTime()
                );
            }
        }
        try {
            Path file = Paths.get("output.txt");
            Files.write(file, toWrite, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Pair<LocalTime, LocalTime> timeFormatter(String startingTime, String endingTime) {
        String[] timeStart = startingTime.split(":");
        String[] timeEnd = endingTime.split(":");
        LocalTime start = LocalTime.of(Integer.parseInt(timeStart[0]), Integer.parseInt(timeStart[1]));
        LocalTime last = LocalTime.of(Integer.parseInt(timeEnd[0]), Integer.parseInt(timeEnd[1]));
        return new Pair<>(start, last);
    }

    private String getDayOfWeek(String detail) {
        String toReturn;
        switch (detail.toUpperCase()) {
            case "S": {
                toReturn = "SU";
                break;
            }
            case "M": {
                toReturn = "MO";
                break;
            }
            case "T": {
                toReturn = "TU";
                break;
            }
            case "W": {
                toReturn = "WE";
                break;
            }
            case "R": {
                toReturn = "TH";
                break;
            }
            case "F": {
                toReturn = "FR";
                break;
            }
            case "A": {
                toReturn = "SA";
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + detail.toLowerCase());
        }
        return toReturn;

    }

    //todo
    // change print calender
    // implement view by
    // introduce the concept of Recurring events everywhere
    // fix time conflict reoccur/one-time

}
