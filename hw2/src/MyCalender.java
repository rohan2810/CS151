import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
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

    public void loadEvents(String args) throws FileNotFoundException {
        File file = new File(args);
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
        LocalDate today = LocalDate.now();
        System.out.println("[D]ay view or [M]view ? ");
        String view = sc.nextLine().toLowerCase();
        outerLoop:
        switch (view) {
            case "m": {
                printMonth(today, false);
                System.out.println("\n[P]revious or [N]ext or [G]o back to the main menu ? ");

                while (sc.hasNext()) {
                    String selection = sc.nextLine();

                    switch (selection.toLowerCase()) {
                        case "p":
                            today = today.minusMonths(1);
                            printMonth(today, false);
                            break;
                        case "n":
                            today = today.plusMonths(1);

                            printMonth(today, false);
                            break;
                        case "g":
                            break outerLoop;
                        default:
                            System.out.println();
                            System.out.println("Enter a valid selection!");
                    }

                    System.out.println("\n[P]revious or [N]ext or [G]o back to the main menu ? ");
                }
            }
            case "d": {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, MMM d yyyy");
                System.out.println(" " + formatter.format(today));
                DateTimeFormatter pattern = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate parsedDate = LocalDate.parse(today.format(pattern), pattern);
                showEventsOnDay(parsedDate);
                System.out.println("[P]revious or [N]ext or [G]o back to the main menu ? ");
                while (sc.hasNext()) {
                    String selection = sc.nextLine();
                    switch (selection.toLowerCase()) {
                        case "p": {
                            today = today.minusDays(1);
                            parsedDate = parsedDate.minusDays(1);
                            System.out.println(" " + formatter.format(today));
                            showEventsOnDay(parsedDate);
                            break;
                        }
                        case "n": {
                            today = today.plusDays(1);
                            parsedDate = parsedDate.plusDays(1);
                            System.out.println(" " + formatter.format(today));
                            showEventsOnDay(parsedDate);
                            break;
                        }
                        case "g": {
                            break outerLoop;
                        }
                    }
                    System.out.println("[P]revious or [N]ext or [G]o back to the main menu ? ");
                }
                break;
            }
            default:
                System.out.println("Invalid Input! Please try again");
        }
    }

    private void showEventsOnDay(LocalDate today) {
        ArrayList<Event> allEvents = new ArrayList<>();
        if (events.containsKey(today)) {
            allEvents.addAll(events.get(today));
        }
        for (Map.Entry<String, HashMap<LocalDate, ArrayList<Event>>> entry : reoccurringEvents.entrySet()) {
            if (entry.getValue().containsKey(today)) {
                allEvents.addAll(entry.getValue().get(today));
            }
        }
        allEvents.sort(Comparator.comparing(Event::getStartTime));
        for (Event e : allEvents) {
            System.out.println(e.getName() + " : " + e.getStartTime() + " - " + e.getEndTime());
        }
    }

    public void printMonth(LocalDate date, boolean square) {
        System.out.println(" " + date.getMonth() + " " + date.getYear());
        GregorianCalendar calendar = GregorianCalendar.from(date.atStartOfDay(ZoneId.systemDefault()));
        GregorianCalendar tempCal = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        int total_week_days = 7;
        int dayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK) - 1;

        System.out.println(" Su Mo Tu We Th Fr Sa");

        for (int i = 0; i < dayOfWeek; i++) {
            System.out.printf("%3s", " ");
            total_week_days--;
        }

        for (int i = 0; i < calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            if (total_week_days <= 0) {
                total_week_days = 7;
                System.out.println();
            }
            LocalDate day = tempCal.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            boolean oneTime = events.containsKey(day);
            boolean reoccur = false;
            for (Map.Entry<String, HashMap<LocalDate, ArrayList<Event>>> entry : reoccurringEvents.entrySet()) {
                if (entry.getValue().containsKey(day)) {
                    reoccur = true;
                    break;
                }
            }
            boolean todayEvent = oneTime || reoccur;
            boolean todayDate = tempCal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) &&
                    tempCal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                    tempCal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR);
            if (square) {
                if (todayDate)
                    System.out.printf("[%2d]", tempCal.get(Calendar.DAY_OF_MONTH));
                else
                    System.out.printf("%3d", tempCal.get(Calendar.DAY_OF_MONTH));

            } else {
                if (todayEvent)
                    System.out.printf("{%2d}", tempCal.get(Calendar.DAY_OF_MONTH));
                else
                    System.out.printf("%3d", tempCal.get(Calendar.DAY_OF_MONTH));
            }
            tempCal.add(Calendar.DAY_OF_MONTH, 1);
            total_week_days--;
        }
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
        List<Event> toWrite1 = new ArrayList<>();

        events.forEach((key2, value2) -> toWrite1.addAll(value2));
        reoccurringEvents.forEach((key, value) -> value.entrySet().stream().findFirst().ifPresent(y -> toWrite1.addAll(y.getValue())));

        toWrite1.sort(Comparator.comparing(Event::getStartTime));
        for (Event e : toWrite1) {
            if (e.getReoccursOn() != null) {
                toWrite.add(e.getName());
                toWrite.add(e.getReoccursOn() + " " + e.getStartTime() + " " + e.getEndTime() + " " + e.getStartDate().format(DateTimeFormatter.ofPattern("MM/dd/yy")) + " " + e.getEndDate().format(DateTimeFormatter.ofPattern("MM/dd/yy")) + " ");
            } else {
                toWrite.add(e.getName());
                toWrite.add(e.getDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + " " + e.getStartTime() + " " + e.getEndTime());
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

}
