import javafx.util.Pair;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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

/**
 * MyCalendar class handles all the backend logic for this solution.
 * It is responsible to providing the functionality to the user.
 * This acts as the model for the application.
 *
 * @author Rohan Surana
 * @version 1.0.0 11/21/2020
 */
public class MyCalendar {
    private final Scanner sc = null;
    private final HashMap<LocalDate, ArrayList<Event>> events;
    private final HashMap<String, HashMap<LocalDate, ArrayList<Event>>> recurringEvents;
    private final ArrayList<ChangeListener> listeners;
    private GregorianCalendar gregorianCalendar;

    /**
     * Constructor for the MyCalendar class.
     * It initializes the following:
     * HashMap of events
     * HashMap of recurringEvents
     *
     * @param sc scanner
     */
    public MyCalendar() {
        this.events = new HashMap<>();
        this.recurringEvents = new HashMap<>();
        this.listeners = new ArrayList<>();
        this.gregorianCalendar = new GregorianCalendar();
    }

    /**
     * Used to read and load all the existing events from the file.
     * It reads the file in a particular format and generate events from it and store them in the appropriate map.
     *
     * @param args file File: file to read reservations from
     * @throws FileNotFoundException
     */
    public void loadEvents(String args) throws FileNotFoundException {
        print("########################################");
        print("##      Loading One-Time events      ##");
        print("########################################");
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
                            loadAndSaveEventsRecurring(name, details[0], date, details[1], details[2], startDate, endDate);
                            date = date.plusDays(7);
                        }
                    }
                }
            }
            fileScanner.close();
        } else {
            print("This is the first run! Any previous records not found.");
        }
        print("########################################");
        print("##      Loading Recurring events      ##");
        print("########################################");
        recurringEvents.forEach((key, value) -> print("Loaded " + key));
        print("Loading is done!");

    }

    /**
     * This option allows the user to schedule an event.
     * The calendar asks the user to enter the name, date, starting time, and ending time of an event.
     * If the user input is correct and there's no conflict, the event is created and the user is notified.
     * If the input is not is appropriate format or the time given for the event conflicts with the other event, then a error is thrown with appropriate message.
     */

    public void createEvent() {
        print("Enter the name of the event:");
        String name = sc.nextLine();
        print("Enter the date of the event in the format MM/DD/YYYY:");
        String date = sc.nextLine();
        print("Enter the starting time of the event in 24 hour format HH:MM :");
        String startingTime = sc.nextLine();
        print("Enter the ending time of the event in 24 hour format HH:MM :");
        String endingTime = sc.nextLine();
        LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        LocalTime parsedStartTime = LocalTime.parse(startingTime, DateTimeFormatter.ISO_LOCAL_TIME);
        LocalTime parsedEndTime = LocalTime.parse(endingTime, DateTimeFormatter.ISO_LOCAL_TIME);
        Pair<Event, Boolean> isSaved = saveEvents(name, parsedDate, parsedStartTime, parsedEndTime);
        if (isSaved.getValue()) {
            print("########################################");
            print("##          EVENT CREATED !!          ##");
            print("########################################");
            print(isSaved.getKey().getName() + " "
                    + isSaved.getKey().getDate() + " "
                    + isSaved.getKey().getStartTime() + " "
                    + isSaved.getKey().getEndTime() + " ");
        }
    }

    public Pair<Event, Boolean> saveEvents(String name, LocalDate parsedDate, LocalTime parsedStartTime, LocalTime parsedEndTime) {
        Event event = new Event(name, parsedDate, parsedStartTime, parsedEndTime);
        if ((event.getStartTime() == null || event.getEndTime() == null)) {
            print("Event not created!. Time cannot be null");
            return new Pair<>(null, false);
        }
        if (event.getStartTime().compareTo(event.getEndTime()) > 0) {
            print("Event not created!");
            print("Start time has to be before the end time!");
            return new Pair<>(null, false);
        }
        Pair<Event, Boolean> conflicts = new Pair<>(null, false);
        for (Map.Entry<LocalDate, ArrayList<Event>> entry : events.entrySet()) {
            for (Event e : entry.getValue()) {
                if (TimeInterval.isConflicting(e, event)) {
                    conflicts = new Pair<>(e, true);
                }
            }
        }
        for (Map.Entry<String, HashMap<LocalDate, ArrayList<Event>>> entry : recurringEvents.entrySet()) {
            for (Map.Entry<LocalDate, ArrayList<Event>> entry2 : entry.getValue().entrySet()) {
                for (Event e : entry2.getValue()) {
                    if (TimeInterval.isConflicting(e, event)) {
                        conflicts = new Pair<>(e, true);
                    }
                }

            }
        }
        if (conflicts.getValue()) {
            print("########################################");
            print("##             ERROR !!               ##");
            print("########################################");
            System.out.println("\" " + event.getName() + " \"" + " cannot be created. It conflicts with " + "\" " + conflicts.getKey().getName() + " \"");
            return new Pair<>(null, false);
        }


        ArrayList<Event> list;
        if (events.containsKey(parsedDate)) {
            list = events.get(parsedDate);
        } else {
            list = new ArrayList<>();
        }
        list.add(event);
        events.put(parsedDate, list);
        return new Pair<>(event, true);

    }

    private void loadAndSaveEvents(String name, String date, String startingTime, String endingTime) {

        String[] actualDate = date.split("/");
        int year = Integer.parseInt(actualDate[2]) + 2000;
        int dayOfMonth = Integer.parseInt(actualDate[1]);
        int month = Integer.parseInt(actualDate[0]);
        LocalDate date1 = LocalDate.of(year, month, dayOfMonth);
        Pair<LocalTime, LocalTime> times = timeFormatter(startingTime, endingTime);
        Pair<Event, Boolean> isSaved = saveEvents(name, date1, times.getKey(), times.getValue());
        if (isSaved.getValue()) {
            print(isSaved.getKey().getName() + " "
                    + isSaved.getKey().getDate() + " "
                    + isSaved.getKey().getStartTime() + " "
                    + isSaved.getKey().getEndTime() + " ");
        }
    }

    private void loadAndSaveEventsRecurring(String name, String recursOn, LocalDate date, String startingTime, String endingTime, LocalDate startDate, LocalDate endDate) {
        Pair<LocalTime, LocalTime> times = timeFormatter(startingTime, endingTime);
        Event event = new Event(name, recursOn, date, times.getKey(), times.getValue(), startDate, endDate);
        if (event.getStartTime().compareTo(event.getEndTime()) > 0) {
            print("Event not created!");
            print("Start time has to be before the end time!");
            return;
        }
        Pair<Event, Boolean> conflicts = new Pair<>(null, false);
        for (Map.Entry<LocalDate, ArrayList<Event>> entry : events.entrySet()) {
            for (Event e : entry.getValue()) {
                if (TimeInterval.isConflicting(e, event)) {
                    conflicts = new Pair<>(e, true);
                }
            }
        }
        for (Map.Entry<String, HashMap<LocalDate, ArrayList<Event>>> entry : recurringEvents.entrySet()) {
            for (Map.Entry<LocalDate, ArrayList<Event>> entry2 : entry.getValue().entrySet()) {
                for (Event e : entry2.getValue()) {
                    if (TimeInterval.isConflicting(e, event)) {
                        conflicts = new Pair<>(e, true);
                    }
                }

            }
        }
        if (conflicts.getValue()) {
            print("########################################");
            print("##             ERROR !!               ##");
            print("########################################");
            print("\" " + event.getName() + " \"" + " cannot be created. It conflicts with " + "\" " + conflicts.getKey().getName() + " \"");
        } else {
            HashMap<LocalDate, ArrayList<Event>> map;
            if (recurringEvents.containsKey(name)) {
                map = recurringEvents.get(name);
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
            recurringEvents.put(name, map);
        }
    }

    /**
     * With this option, the user is asked to enter a date in the form of MM/DD/YYYY.
     * The calendar displays the Day view of the requested date including an event scheduled on that day in the order of starting time.
     */

    public void goTo() {
        print("Enter the date [MM/DD/YYYY]");
        String date = sc.nextLine();
        LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        ArrayList<Event> thatDay = new ArrayList<>();
        boolean contains = false;

        if (events.containsKey(parsedDate)) {
            thatDay.addAll(events.get(parsedDate));
            contains = true;
        }

        for (Map.Entry<String, HashMap<LocalDate, ArrayList<Event>>> entry : recurringEvents.entrySet()) {
            if (entry.getValue().containsKey(parsedDate)) {
                thatDay.add(entry.getValue().get(parsedDate).get(0));
                contains = true;
            }
        }


        if (contains) {
            print("All events on: " + date);
            thatDay.sort(Comparator.comparing(Event::getStartTime));
            thatDay.forEach(x -> print(x.getStartTime() + " - " + x.getEndTime() + " " + x.getName()));
        } else {
            print("No events found on the particular date! Try again");
        }

    }

    /**
     * The user can see all the scheduled events using this method.
     * The scheduled events are presented in two categories: One time events and Recurring events.
     */

    public void eventList() {
        print("ONE TIME EVENTS");
        List<LocalDate> dates = new ArrayList<>(events.keySet());
        Collections.sort(dates);
        int yearVal = 0;
        for (LocalDate date : dates) {
            ArrayList<Event> l = events.get(date);
            l.sort(Comparator.comparing(Event::getStartTime));
            for (Event event : l) {
                if (yearVal != event.getDate().getYear()) {
                    print(event.getDate().getYear());
                    yearVal = event.getDate().getYear();
                }
                print(
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

        print("RECURRING EVENTS");
        List<String> names = new ArrayList<>(recurringEvents.keySet());
        ArrayList<Event> allEvents = new ArrayList<>();
        for (String name : names) {
            HashMap<LocalDate, ArrayList<Event>> l = recurringEvents.get(name);
            List<LocalDate> dates1 = new ArrayList<>(l.keySet());
            ArrayList<Event> l1 = l.get(dates1.get(0));
            allEvents.add(l1.get(0));
        }
        allEvents.sort(Comparator.comparing(Event::getStartDate));
        for (Event e : allEvents) {
            print(e.getName());
            print(
                    e.getRecursOn() + " "
                            + e.getStartTime() + " "
                            + e.getEndTime() + " "
                            + e.getStartDate().format(DateTimeFormatter.ofPattern("MM/dd/yy")) + " "
                            + e.getEndDate().format(DateTimeFormatter.ofPattern("MM/dd/yy")));
        }
    }

    /**
     * With this option the user can see the events in day or month view.
     * The user can choose a Day or a Month view.
     * If a Day view is chosen, the program prints today's date and prints all the scheduled events for the day.
     * With a Month view, it displays the current month and marks the dates on which the event is scheduled with {}.
     * User can navigate to next day/month, previous day/month or go back using [N],[P],[G]
     */

    public void viewBy() {
        LocalDate today = LocalDate.now();
        print("[D]ay view or [M]view ? ");
        String view = sc.nextLine().toLowerCase();
        outerLoop:
        switch (view) {
            case "m": {
                printMonth(today, false);
                print("\n[P]revious or [N]ext or [G]o back to the main menu ? ");

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
                            print("Enter a valid selection!");
                    }

                    print("\n[P]revious or [N]ext or [G]o back to the main menu ? ");
                }
            }
            case "d": {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, MMM d yyyy");
                print(" " + formatter.format(today));
                DateTimeFormatter pattern = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate parsedDate = LocalDate.parse(today.format(pattern), pattern);
                showEventsOnDay(parsedDate);
                print("[P]revious or [N]ext or [G]o back to the main menu ? ");
                while (sc.hasNext()) {
                    String selection = sc.nextLine();
                    switch (selection.toLowerCase()) {
                        case "p": {
                            today = today.minusDays(1);
                            parsedDate = parsedDate.minusDays(1);
                            print(" " + formatter.format(today));
                            showEventsOnDay(parsedDate);
                            break;
                        }
                        case "n": {
                            today = today.plusDays(1);
                            parsedDate = parsedDate.plusDays(1);
                            print(" " + formatter.format(today));
                            showEventsOnDay(parsedDate);
                            break;
                        }
                        case "g": {
                            break outerLoop;
                        }
                    }
                    print("[P]revious or [N]ext or [G]o back to the main menu ? ");
                }
                break;
            }
            default:
                print("Invalid Input! Please try again");
        }
    }

    private void showEventsOnDay(LocalDate today) {
        ArrayList<Event> allEvents = new ArrayList<>();
        if (events.containsKey(today)) {
            allEvents.addAll(events.get(today));
        }
        for (Map.Entry<String, HashMap<LocalDate, ArrayList<Event>>> entry : recurringEvents.entrySet()) {
            if (entry.getValue().containsKey(today)) {
                allEvents.addAll(entry.getValue().get(today));
            }
        }
        allEvents.sort(Comparator.comparing(Event::getStartTime));
        for (Event e : allEvents) {
            print(e.getName() + " : " + e.getStartTime() + " - " + e.getEndTime());
        }
    }

    /**
     * This is used to print to print the calender for the given month.
     *
     * @param date   date of month for which the calender has to be created.
     * @param square boolean requires square[] brackets?
     */
    public void printMonth(LocalDate date, boolean square) {
        print(" " + date.getMonth() + " " + date.getYear());
        GregorianCalendar calendar = GregorianCalendar.from(date.atStartOfDay(ZoneId.systemDefault()));
        GregorianCalendar tempCal = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        int total_week_days = 7;
        int dayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK) - 1;

        print(" Su Mo Tu We Th Fr Sa");

        for (int i = 0; i < dayOfWeek; i++) {
            System.out.printf("%3s", " ");//change
            total_week_days--;
        }

        for (int i = 0; i < calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            if (total_week_days <= 0) {
                total_week_days = 7;
                System.out.println();
            }
            LocalDate day = tempCal.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            boolean oneTime = events.containsKey(day);
            boolean recur = false;
            for (Map.Entry<String, HashMap<LocalDate, ArrayList<Event>>> entry : recurringEvents.entrySet()) {
                if (entry.getValue().containsKey(day)) {
                    recur = true;
                    break;
                }
            }
            boolean todayEvent = oneTime || recur;
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

    /**
     * The user can delete an event from the Calendar.
     * It supports 3 types of deletion, Selected event, All events on a particular day, and recurring events by the given name.
     */
    public void delete() {
        print("Delete  [S]elected or [A]ll events or DeleteRecurring[DR]?");
        String type = sc.nextLine();
        switch (type.toUpperCase()) {
            case "S": {
                print("Enter the date [MM/DD/YYYY]");
                String date = sc.nextLine();
                LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                if (events.containsKey(parsedDate) || !events.isEmpty()) {
                    ArrayList<Event> thatDay = events.get(parsedDate);
                    print("All events on: " + date);
                    thatDay.sort(Comparator.comparing(Event::getStartTime));
                    thatDay.forEach(x -> print(x.getStartTime() + " - " + x.getEndTime() + " " + x.getName()));
                    print("Enter the name of the event to delete");
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
                        print("##################################################");
                        print("##" + "Successfully removed " + toDelete + " #####");
                        print("##################################################");
                        print("Successfully removed " + toDelete);
                    } else {
                        print("No event found under name " + toDelete + ". Please Try Again!");
                    }

                } else {
                    print("No events found on the particular date! Try again");
                }
                break;
            }
            case "A": {
                print("Enter the date [MM/DD/YYYY]");
                String date = sc.nextLine();
                LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                if (events.isEmpty() || !events.containsKey(parsedDate)) {
                    print("No events found on the particular date! Try again");
                } else {
                    ArrayList<Event> thatDay = events.get(parsedDate);
                    print("All events for the day");
                    thatDay.sort(Comparator.comparing(Event::getStartTime));
                    thatDay.forEach(x -> print(x.getStartTime() + " - " + x.getEndTime() + " " + x.getName()));
                    int size = events.get(parsedDate).size();
                    events.remove(parsedDate);
                    print("Successfully Removed " + size + " events");
                }
                break;
            }
            case "DR": {
                print("Enter the name of the recurring event to delete");
                String toDelete = sc.nextLine();
                if (recurringEvents.containsKey(toDelete)) {
                    recurringEvents.remove(toDelete);
                    print("Successfully removed");
                } else {
                    print("No recurring events found under the given name. Please try again");
                }
            }
        }
    }

    /**
     * Quits the program and saves all the events made in a txt file.
     */
    public void quit() {
        print("Good Bye!");
        List<String> toWrite = new ArrayList<>();
        List<Event> toWrite1 = new ArrayList<>();
        List<Event> toWrite2 = new ArrayList<>();

        events.forEach((key2, value2) -> {
            value2.sort(Comparator.comparing(Event::getDate));
            toWrite1.addAll(value2);
        });
        recurringEvents.forEach((key, value) -> value.entrySet().stream().findFirst().ifPresent(y -> toWrite2.addAll(y.getValue())));

        toWrite2.sort(Comparator.comparing(Event::getStartDate));
        toWrite1.addAll(toWrite2);
        for (Event e : toWrite1) {
            if (e.getRecursOn() != null) {
                toWrite.add(e.getName());
                toWrite.add(e.getRecursOn() + " " + e.getStartTime() + " " + e.getEndTime() + " " + e.getStartDate().format(DateTimeFormatter.ofPattern("MM/dd/yy")) + " " + e.getEndDate().format(DateTimeFormatter.ofPattern("MM/dd/yy")) + " ");
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
        HashMap<String, String> val = new HashMap<>();
        val.put("s", "SU");
        val.put("m", "MO");
        val.put("t", "TU");
        val.put("w", "WE");
        val.put("r", "TH");
        val.put("f", "FR");
        val.put("a", "SA");
        return val.get(detail.toLowerCase());
    }

    private void print(Object x) {
        System.out.println(x);
    }

    /**
     * attaches the listener to the list of listener
     *
     * @param listener
     */
    public void attach(ChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * returns the GregorianCalendar from the model.
     *
     * @param listener
     */
    public GregorianCalendar getGregorianCalendar() {
        return gregorianCalendar;
    }

    /**
     * Update all the listeners given the GregorianCalendar
     *
     * @param calendar
     */

    public void updateListeners(GregorianCalendar calendar) {
        gregorianCalendar = calendar;
        for (ChangeListener listener : listeners) {
            listener.stateChanged(new ChangeEvent(this));
        }
    }

    /**
     * Update all the listeners
     */
    public void update() {
        for (ChangeListener l : listeners) {
            l.stateChanged(new ChangeEvent(this));
        }
    }

    /**
     * add event to the eventMap and update the ChangeListener
     *
     * @param event
     */
    public void updateEvent(Event event) {
        ArrayList<Event> list = events.get(event.getDate());
        list.add(event);
        events.put(event.getDate(), list);
        for (ChangeListener l : listeners) {
            l.stateChanged(new ChangeEvent(this));
        }
    }

    /**
     * return the event map.
     *
     * @return
     */
    public HashMap<LocalDate, ArrayList<Event>> getEventMap() {
        return events;
    }

}



