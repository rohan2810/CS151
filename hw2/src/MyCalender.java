import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MyCalender {
    private final Scanner sc;
    private HashMap<LocalDate, ArrayList<Event>> events;

    public MyCalender(Scanner sc) {
        this.sc = sc;
        this.events = new HashMap<>();
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

        Event event = new Event(name, parsedDate, parsedStartTime, parsedEndTime);
        if ((event.getStartTime() == null || event.getEndTime() == null)) {
            System.out.println("Event not created!. Time cannot be null");
            return;
        }
        if (event.getStartTime().compareTo(event.getEndTime()) > 0) {
            System.out.println("\nEvent not created!");
            System.out.println("Start time has to be before the end time!");
            return;
        }
        for (Map.Entry<LocalDate, ArrayList<Event>> entry : events.entrySet()) {
            for (Event e : entry.getValue()) {
                if (TimeInterval.isConflicting(e, event)) {
                    System.out.println("Event cannot be created. It conflicts with " + e.getName());
                    return;
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
        for (Map.Entry<LocalDate, ArrayList<Event>> entry : events.entrySet()) {
            System.out.println(entry.getKey() + "  " + entry.getValue());
        }

    }

    public void quit() {
        System.out.println("Good Bye!");
        List<String> toWrite = new ArrayList<>();
        for (Map.Entry<LocalDate, ArrayList<Event>> entry : events.entrySet()) {
            for (Event e : entry.getValue()) {
                toWrite.add(e.getName());
                toWrite.add(
                        e.getDate() + " " + e.getStartTime() + " " + e.getEndTime()
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


        //todo
        //System.out.println("RECURRING EVENTS");
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
                    thatDay.removeIf(value -> value.getName().equals(toDelete));
                    events.replace(parsedDate, thatDay);
                    if (events.get(parsedDate).size() == 0) {
                        events.remove(parsedDate);
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
                // todo
            }
        }


    }

    public void loadEvents() {

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
}
