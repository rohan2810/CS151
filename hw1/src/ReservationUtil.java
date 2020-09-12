import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Reservation Util class handles all the backend logic for this solution.
 * It is responsible to providing the functionality to the user.
 *
 * @author Rohan Surana
 * @version 1.0.0 09/12/2020
 */
public class ReservationUtil {

    private final Scanner sc;
    private final ArrayList<Reservation> reservations;
    private Pair<HashMap<Integer, List<String>>, HashMap<Integer, List<String>>> model;
    private Map<String, List<Reservation>> grpMap;

    /**
     * Constructor for the Reservation Util class.
     * It initializes the following:
     * Scanner
     * ArrayList of Reservation  --> stores all the reservations
     * Map <String, List<Reservation>>  -->
     *      Key   (String) : Group Name
     *      Value (List<Reservation>) : Reservations linked to the group name(key)
     * @param sc scanner
     */
    public ReservationUtil(Scanner sc) {
        this.sc = sc;
        reservations = new ArrayList<>();
        grpMap = new HashMap<>();
    }

    /**
     * This method is used to create a new Model
     * This is called at every run
     */
    public void createNewData() {
        this.model = Model.createNewStr();
    }

    /**
     * This method is responsible for reading the reservations from the existing file.
     * It reads the file in a particular format and generate reservations from it and store them in the appropriate list or map.
     *
     * @param arg File: file to read reservations from
     * @return alrExists(boolean) : return true if the file exists, false if does not exists(int he case of first run)
     * @throws IOException
     */
    public boolean readFromExistingFile(String arg) throws IOException {

        List<Reservation> grpReservations = new ArrayList<>();
        boolean alrExists = false;
        File file = new File(arg+".txt");
        String[] data;
        if (file.exists() && !file.isDirectory()) {
            Scanner fileScanner = new Scanner(file);
            alrExists = true;
            while (fileScanner.hasNext()) {
                data = fileScanner.nextLine().split(", ");
                System.out.println("Previous Bookings:");
                System.out.println(Arrays.toString(data));  // remove me
                Reservation reservation;
                if (data.length == 4) { // group reservation
                    String grpName;
                    if (data[0].length() == 2) { // first
                        grpName = data[2];
                        reservation = new Reservation(data[3], data[0], data[1], "First");
                        grpReservations.add(reservation);
                    } else {//economy
                        grpName = data[2];
                        reservation = new Reservation(data[3], data[0], data[1], "Economy");
                        grpReservations.add(reservation);
                    }
                    reservations.add(reservation);
                    grpMap.put(grpName, grpReservations);
                } else {  // individual
                    if (data[0].length() == 2) {  // first
                        reservation = new Reservation(data[2], data[0], data[1], "First");
                    } else {//economy
                        reservation = new Reservation(data[2], data[0], data[1], "Economy");
                    }
                    reservations.add(reservation);
                }
            }
        } else {
            System.out.println("This is the first run! Any previous records not found.");
        }
        return alrExists;
    }

    /**
     * This method is used for updating the data structure for the program.
     * After reading the file and generating the reservations out of it, this method is called to update the data structure(model) for the program.
     */
    public void updateModelAfterReadingFromFile() {
        for (Reservation r : reservations) {
            String seat = r.getAllottedSeat();
            String serviceClass = r.getService_class();
            if (serviceClass.equals("First")) {
                HashMap<Integer, List<String>> first = model.getValue();
                int num = Integer.parseInt(seat.substring(0, 1));
                List<String> list = first.get(num);
                List<String> modifiedList = new ArrayList<>(list);
                modifiedList.remove(seat.substring(1, 2));
                first.replace(num, modifiedList);
            } else {
                //economy
                HashMap<Integer, List<String>> econ = model.getKey();
                int num = Integer.parseInt(seat.substring(0, 2));
                List<String> list = econ.get(num);
                List<String> modifiedList = new ArrayList<>(list);
                modifiedList.remove(seat.substring(2, 3));
                econ.replace(num, modifiedList);
            }
        }
    }

    /**
     * This method is responsible for generating a reservation for an individual passenger.
     * It is called when user requests to add an passenger.
     * It asks for name, service_class, and preference.
     * If the selected preference is not available, then user is prompted to re-enter their preference.
     * If the given new preference is also not available, the user is notified that the reservation cannot be made.
     *
     * @throws Exception
     */
    public void addPassenger() throws Exception {
        System.out.println("Enter Name");
        String name = sc.nextLine();
        System.out.println("Select Class [First] or [Economy]");
        String service_class = sc.nextLine();
        String preference = null;
        int maxAvailableSeats = 0;
        boolean canBeAdded = false;
        if (service_class.equals("First")) {
            System.out.println("Select preference [A]isle [W]indow");
            preference = sc.nextLine();
            for (int i = 0; i < 2; i++) {
                if (preference.equals("W")) {
                    for (int j = 0; j < model.getValue().get(i + 1).size(); j++) {
                        if (model.getValue().get(i + 1).get(j).equals("A") || (model.getValue().get(i + 1).get(j).equals("D"))) {
                            maxAvailableSeats++;
                        }
                    }
                } else {
                    for (int j = 0; j < model.getValue().get(i + 1).size(); j++) {
                        if (model.getValue().get(i + 1).get(j).equals("B") || (model.getValue().get(i + 1).get(j).equals("C"))) {
                            maxAvailableSeats++;
                        }
                    }
                }
            }
            if (maxAvailableSeats > 0) {
                canBeAdded = true;
            }
        } else if (service_class.equals("Economy")) {
            System.out.println("Select preference [A]isle [W]indow [C]enter");
            preference = sc.nextLine();
            for (int i = 9; i < 29; i++) {
                if (preference.equals("W")) {
                    for (int j = 0; j < model.getKey().get(i + 1).size(); j++) {
                        if (model.getKey().get(i + 1).get(j).equals("A") || (model.getKey().get(i + 1).get(j).equals("F"))) {
                            maxAvailableSeats++;
                        }
                    }
                } else if (preference.equals("C")) {
                    for (int j = 0; j < model.getKey().get(i + 1).size(); j++) {
                        if (model.getKey().get(i + 1).get(j).equals("E") || (model.getKey().get(i + 1).get(j).equals("B"))) {
                            maxAvailableSeats++;
                        }
                    }
                } else {
                    for (int j = 0; j < model.getKey().get(i + 1).size(); j++) {
                        if (model.getKey().get(i + 1).get(j).equals("C") || (model.getKey().get(i + 1).get(j).equals("D"))) {
                            maxAvailableSeats++;
                        }
                    }
                }
            }
            if (maxAvailableSeats > 0) {
                canBeAdded = true;
            }
        } else {
            System.out.println("Invalid Selection!, Try again");
        }
        if (canBeAdded) {
            Reservation newReservation = new Reservation(name, assignSeat(service_class, preference), "I", service_class);
            reservations.add(newReservation);
            System.out.println(newReservation.getAllottedSeat() + ": " + newReservation.getName());
        } else {
            System.out.println("No more seats available for this particular choice. Please try again!");
            System.out.println("Select preference [A]isle [W]indow");
            String preference2 = sc.nextLine();
            assert preference != null;
            addPassengerWithArgs(name, service_class, preference2, preference);
        }

    }

    /**
     * Method used when no seats are available for the given preference.
     * It asks for another another preference.
     * If that exists creates the reservation, else prints try again to the console.
     *
     * @param name          passenger name
     * @param service_class requested service_class
     * @param preference2   the new preference
     * @param preferences   the old preference
     * @throws Exception
     */
    private void addPassengerWithArgs(String name, String service_class, String preference2, String preferences) throws Exception {
        boolean same = false;
        if (preferences.equals(preference2)) {
            same = true;
        }
        String preference = null;
        int maxAvailableSeats = 0;
        boolean canBeAdded = false;
        if (service_class.equals("First")) {
            preference = preference2;
            for (int i = 0; i < 2; i++) {
                if (preference.equals("W")) {
                    for (int j = 0; j < model.getValue().get(i + 1).size(); j++) {
                        if (model.getValue().get(i + 1).get(j).equals("A") || (model.getValue().get(i + 1).get(j).equals("D"))) {
                            maxAvailableSeats++;
                        }
                    }
                } else {
                    for (int j = 0; j < model.getValue().get(i + 1).size(); j++) {
                        if (model.getValue().get(i + 1).get(j).equals("B") || (model.getValue().get(i + 1).get(j).equals("C"))) {
                            maxAvailableSeats++;
                        }
                    }
                }
            }
            if (maxAvailableSeats > 0) {
                canBeAdded = true;
            }
        } else if (service_class.equals("Economy")) {
            preference = preference2;
            for (int i = 9; i < 29; i++) {
                if (preference.equals("W")) {
                    for (int j = 0; j < model.getKey().get(i + 1).size(); j++) {
                        if (model.getKey().get(i + 1).get(j).equals("A") || (model.getKey().get(i + 1).get(j).equals("F"))) {
                            maxAvailableSeats++;
                        }
                    }
                } else if (preference.equals("C")) {
                    for (int j = 0; j < model.getKey().get(i + 1).size(); j++) {
                        if (model.getKey().get(i + 1).get(j).equals("E") || (model.getKey().get(i + 1).get(j).equals("B"))) {
                            maxAvailableSeats++;
                        }
                    }
                } else {
                    for (int j = 0; j < model.getKey().get(i + 1).size(); j++) {
                        if (model.getKey().get(i + 1).get(j).equals("C") || (model.getKey().get(i + 1).get(j).equals("D"))) {
                            maxAvailableSeats++;
                        }
                    }
                }
            }
            if (maxAvailableSeats > 0) {
                canBeAdded = true;
            }
        } else {
            System.out.println("Invalid Selection!, Try again");
        }
        if (canBeAdded) {
            Reservation newReservation = new Reservation(name, assignSeat(service_class, preference), "I", service_class);
            reservations.add(newReservation);
            System.out.println(newReservation.getAllottedSeat() + ": " + newReservation.getName());
        } else {
            if (same) {
                System.out.println("No more seats available for this particular choice. Please try again!");
                System.out.println("Select preference [A]isle [W]indow");
                String preference3 = sc.nextLine();
                assert preference != null;
                addPassengerWithArgs(name, service_class, preference3, preference);
            } else {
                System.out.println("No more seats available! Please start again with different choices.");
            }
        }

    }


    /**
     * This method is responsible for generating a reservation for an all passengers on the group.
     * It is called when user requests to add a group reservation.
     *
     * @throws Exception
     */
    public void addGroup() throws Exception {
        System.out.println("Enter Group Name");
        String groupName = sc.nextLine();
        System.out.println("Enter Passenger Names");
        String commaSeparatedNames = sc.nextLine();
        String[] names = commaSeparatedNames.split(", ");
        System.out.println("Select Class [First] or [Economy]");
        String service_class = sc.nextLine();
        List<Reservation> grpReservations = new ArrayList<>();
        Reservation newReservation;
        boolean canBeAdded = false;
        int maxAvailableSeats = 0;
        if (service_class.equals("First")) {
            for (int i = 0; i < 2; i++) {
                maxAvailableSeats += model.getValue().get(i + 1).size();
            }
        } else {
            //economy
            for (int i = 9; i < 29; i++) {
                maxAvailableSeats += model.getKey().get(i + 1).size();
            }
        }
        if (names.length <= maxAvailableSeats)
            canBeAdded = true;
        else {
            System.out.println("Sorry! Reservation cannot be made. Not enough seats available1");
        }

        if (canBeAdded && !grpMap.containsKey(groupName)) {
            for (String name : names) {
                newReservation = new Reservation(name, assignSeatForGroup(service_class), "G", service_class);
                reservations.add(newReservation);
                grpReservations.add(newReservation);
                System.out.println(newReservation.getAllottedSeat() + ": " + newReservation.getName());
            }
            if (grpMap.containsKey(groupName)) {
                System.out.println("Group with same name exists! Try Again");
            } else
                grpMap.put(groupName, grpReservations);
        }
    }

    /**
     * This is an supplement method which is used by 'addPassenger' method for assigning seat to a reservation request.
     *
     * @param service_class requested service class: First or Economy
     * @param preference    Seat preference : W and A in case of First class && W, C, and A for the Economy class.
     * @return the assignedSeat : (Column + Row)
     * @throws Exception
     */
    private String assignSeat(String service_class, String preference) throws Exception {
        String assignedSeat = null;
        if (service_class.equals("First")) {
            HashMap<Integer, List<String>> first = model.getValue();
            outer:
            for (int i = 0; i < 2; i++) {
                List<String> list = first.get(i + 1);
                if (preference.equals("W")) {
                    for (int j = 0; j < list.size(); j++) {
                        String seat = list.get(j);
                        if ((seat.equals("A")) || (seat.equals("D"))) {
                            assignedSeat = getSeatAndUpdateModel(first, i, list, j, seat);
                            break outer;
                        }
                    }
                } else if (preference.equals("A")) {
                    for (int j = 0; j < list.size(); j++) {
                        String pref = list.get(j);
                        if ((pref.equals("B")) || (pref.equals("C"))) {
                            assignedSeat = getSeatAndUpdateModel(first, i, list, j, pref);
                            break outer;
                        }
                    }
                } else {
                    System.out.println("Requires new selection, Preference" + preference + " not available! ");
                    break;
                }


            }
        } else if (service_class.equals("Economy")) {
            HashMap<Integer, List<String>> econ = model.getKey();
            outer:
            for (int i = 9; i < 29; i++) {
                List<String> list = econ.get(i + 1);
                switch (preference) {
                    case "W":
                        for (int j = 0; j < list.size(); j++) {
                            String pref = list.get(j);
                            if ((pref.equals("A")) || (pref.equals("F"))) {
                                assignedSeat = getSeatAndUpdateModel(econ, i, list, j, pref);
                                break outer;
                            }
                        }
                        break;
                    case "A":
                        for (int j = 0; j < list.size(); j++) {
                            String pref = list.get(j);
                            if ((pref.equals("C")) || (pref.equals("D"))) {
                                assignedSeat = getSeatAndUpdateModel(econ, i, list, j, pref);
                                break outer;
                            }
                        }
                        break;
                    case "C":
                        for (int j = 0; j < list.size(); j++) {
                            String pref = list.get(j);
                            if ((pref.equals("B")) || (pref.equals("E"))) {
                                assignedSeat = getSeatAndUpdateModel(econ, i, list, j, pref);
                                break outer;
                            }
                        }
                        break;
                    default:
                        System.out.println("Requires new selection, Preference" + preference + " not available! ");
                        break outer;
                }
            }

        } else
            throw new Exception("Invalid Input! Was Expecting First or Economy, found " + service_class);
        return assignedSeat;

    }

    /**
     * This is an supplement method which is used by 'addGroup' method for assigning seat to a reservation request for a group.
     *
     * @param service_class the requested service class: First or Economy
     * @return assignedSeat the assignedSeat : (Column + Row)
     * @throws Exception
     */
    private String assignSeatForGroup(String service_class) throws Exception {
        String assignedSeat = null;
        if (service_class.equals("First")) {
            HashMap<Integer, List<String>> first = model.getValue();
            outer:
            for (int i = 0; i < 2; i++) {
                List<String> list = first.get(i + 1);
                for (int j = 0; j < list.size(); j++) {
                    String seat = list.get(j);
                    assignedSeat = getSeatAndUpdateModel(first, i, list, j, seat);
                    break outer;

                }
            }
        } else if (service_class.equals("Economy")) {
            HashMap<Integer, List<String>> econ = model.getKey();
            outer:
            for (int i = 9; i < 29; i++) {
                List<String> list = econ.get(i + 1);
                for (int j = 0; j < list.size(); j++) {
                    String seat = list.get(j);
                    assignedSeat = getSeatAndUpdateModel(econ, i, list, j, seat);
                    break outer;
                }
            }
        } else
            throw new Exception("Invalid Input! Was Expecting First or Economy, found " + service_class);
        return assignedSeat;
    }

    /**
     * This is an supplement method which is used by the 'assignSeat' method.
     * It is responsible for generating the seatNo for the request and updating the data structure(model) for the program.
     *
     * @return the assignedSeat
     */
    private String getSeatAndUpdateModel(HashMap<Integer, List<String>> first, int i, List<String> list, int j, String seat) {
        String assignedSeat = generateSeatNo(seat, i);
        List<String> modifiedList = new ArrayList<>(list);
        modifiedList.remove(list.get(j));
        first.replace(i + 1, modifiedList);
        return assignedSeat;
    }

    /**
     * This method is used for generating the seatNo. for an request.
     *
     * @param key The row
     * @param i   The column
     * @return the generated SeatNo. For eg. (1B, 15E)
     */
    private String generateSeatNo(String key, int i) {
        return i + 1 + key;
    }

    /**
     * This method is called when the user inputs 'C' to cancel the reservation.
     * It prompts the user to enter individual or group cancellation.
     * If individual is selected, it asks for the name provided while making the reservation, and then cancels the particular reservation if exists and add the cancelled seat to the model.
     * If group is selected, it asks for the group name provided while making the reservation, and then cancels all the reservations under the group name if exists and add the cancelled seats to the model.
     */
    public void cancelReservation() {
        System.out.println("Select [I]ndividual or [G]roup");
        String type = sc.nextLine();

        if (type.equals("I")) {
            System.out.println("Enter name");
            String name = sc.nextLine();
            boolean removed = false;
            List<String> seat = new ArrayList<>();
            for (Iterator<Reservation> iterator = reservations.iterator(); iterator.hasNext(); ) {
                Reservation value = iterator.next();
                if (value.getName().equals(name)) {
                    seat.add(value.getAllottedSeat());
                    iterator.remove();
                    removed = true;
                    break;
                } else {
                    removed = false;
                }
            }

            if (!removed) {
                System.out.println("No such reservation!");
            } else {
                addAgain(seat);
                System.out.println("Removed Successfully");
            }
        } else if (type.equals("G")) {
            System.out.println("Enter Group Name");
            String grpName = sc.nextLine();
            boolean removed = false;
            List<String> seat = new ArrayList<>();
            List<Reservation> allReservations = new ArrayList<>();
            if (grpMap.containsKey(grpName)) {
                List<Reservation> grpReservationList = grpMap.get(grpName);
                for (Iterator<Reservation> grpItr = grpReservationList.iterator(); grpItr.hasNext(); ) {
                    Reservation value = grpItr.next();
                    allReservations.add(value);
                    seat.add(value.getAllottedSeat());
                    grpItr.remove();
                }
                for (Reservation r : allReservations) {
                    reservations.remove(r);
                    removed = true;
                }

                if (!removed) {
                    System.out.println("No such reservation!");
                } else {
                    System.out.println(seat);
                    addAgain(seat);
                    System.out.println("Removed Successfully");
                }
            } else {
                System.out.println("No reservation found under Group Name: " + grpName + "! .Please try again");
            }

        } else {
            System.out.println("Invalid choice! " + type + " is not a valid choice. Try [I]ndividual or [G]roup");
        }
    }

    /**
     * This is an supplement method which is used by the 'cancelReservation' method.
     * It is responsible for adding all the seats from the cancelled reservations to the model again.
     *
     * @param toAdd List of cancelled reservations, which are used to add the cancelled seats to the model again.
     */
    private void addAgain(List<String> toAdd) {
        for (String seat : toAdd) {
            if (seat.length() == 2) {
                int num = Integer.parseInt(seat.substring(0, 1));
                List<String> modifiedList = model.getValue().get(num);
                modifiedList.add(seat.substring(1, 2));
//                Set<String> set = new LinkedHashSet<>(modifiedList);
//                modifiedList.clear();
//                modifiedList.addAll(set);
                modifiedList = modifiedList.stream()
                        .distinct()
                        .collect(Collectors.toList());
                Collections.sort(modifiedList);
                List<String> newList = new ArrayList<>(modifiedList);
                model.getValue().replace(num, newList);
            } else if (seat.length() == 3) {
                int num = Integer.parseInt(seat.substring(0, 2));
                List<String> modifiedList = model.getKey().get(num);
                modifiedList.add(seat.substring(2, 3));
//                Set<String> set = new LinkedHashSet<>(modifiedList);
//                modifiedList.clear();
//                modifiedList.addAll(set);
                modifiedList = modifiedList.stream()
                        .distinct()
                        .collect(Collectors.toList());
                Collections.sort(modifiedList);
                List<String> newList = new ArrayList<>(modifiedList);
                model.getKey().replace(num, newList);
            }
        }
    }

    /**
     * This method is called when the user inputs 'M' to get all the current reservations details of a particular service class.
     * it is responsible for printing the reservations for a particular service class(requested from the user) to the console.
     */
    public void printManifest() {
        System.out.println("Select Class [First] or [Economy]");
        String service_class = sc.nextLine();
        System.out.println(service_class);
        reservations.sort(new CompareReservation());
        for (Reservation reservation : reservations) {
            if (reservation.getService_class().equals(service_class)) {
                String manifest = reservation.getAllottedSeat() +
                        ": " +
                        reservation.getName();

                System.out.println(manifest);
            }
        }
    }

    /**
     * This method is called when the user input 'A' to get the availability of a particular service class.
     * It is responsible for printing out the current state of the model or the available seats for a particular service class(requested from the user) to the console.
     */
    public void getAvailability() {
        System.out.println("Select Class [First] or [Economy]");
        String service_class = sc.nextLine();
        HashMap<Integer, List<String>> seats;
        if (service_class.equals("First")) {
            seats = model.getValue();
            for (Map.Entry<Integer, List<String>> entry : seats.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue().toString());
            }
        } else if (service_class.equals("Economy")) {

            seats = model.getKey();
            for (Map.Entry<Integer, List<String>> entry : seats.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue().toString());
            }
        } else {
            System.out.println("Invalid Input, Expected [First] or [Economy], got " + service_class);
        }
    }

    /**
     * This method is called when the user inputs 'Q'.
     * It is responsible for writing all the reservations in a particular format to the file.
     */
    public void saveToFile() {
        List<String> toWrite = new ArrayList<>();
        for (Reservation res : reservations) {
            if (res.getBookingType().equals("I")) {
                String stringBuilder = res.getAllottedSeat() +
                        ", " +
                        res.getBookingType() +
                        ", " +
                        res.getName();
                toWrite.add(stringBuilder);
            } else {
                String key = null;
                for (Map.Entry<String, List<Reservation>> entry : grpMap.entrySet()) {
                    if (entry.getValue().contains(res)) {
                        key = entry.getKey();
                    }
                }
                String stringBuilder = res.getAllottedSeat() +
                        ", " +
                        res.getBookingType() +
                        ", " +
                        key +
                        ", " +
                        res.getName();

                toWrite.add(stringBuilder);
            }
        }
        try {
            Path file = Paths.get("CL34.txt");
            Collections.sort(toWrite);
            Files.write(file, toWrite, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used for generating the seatNo for the reservation and updating the model.
     */
    @Deprecated
    private String getSeatAndUpdateModelForGroup(HashMap<Integer, List<String>> first, int i, List<String> list, int j, String seat) {
        String assignedSeat = generateSeatNo(seat, i);
        List<String> modifiedList = new ArrayList<>(list);
        modifiedList.remove(list.get(j));
        first.replace(i + 1, modifiedList);
        return assignedSeat;
    }
//
//
//    public void newAddGroup() throws Exception {
//        System.out.println("Enter Group Name");
//        String groupName = sc.nextLine();
//        System.out.println("Enter Passenger Names");
//        String commaSeparatedNames = sc.nextLine();
//        String[] names = commaSeparatedNames.split(", ");
//        System.out.println("Select Class [First] or [Economy]");
//        String service_class = sc.nextLine();
//        List<Reservation> grpReservations = new ArrayList<>();
//        Reservation newReservation;
//        boolean canBeAdded = false;
//        int maxAvailableSeats = 0;
//        if (service_class.equals("First")) {
//            for (int i = 0; i < 2; i++) {
//                maxAvailableSeats += model.getValue().get(i + 1).size();
//            }
//        } else {//economy
//            for (int i = 9; i < 29; i++) {
//                maxAvailableSeats += model.getKey().get(i + 1).size();
//            }
//        }
//        if (names.length <= maxAvailableSeats)
//            canBeAdded = true;
//        else {
//            System.out.println("Sorry! Reservation cannot be made. Not enough seats available1");
//        }
//        List<String> seats = new ArrayList<>();
//        if (canBeAdded && !grpMap.containsKey(groupName)) {
//            Pair<Integer, List<Character>> pair = generateAdjacentBooking(names, service_class, groupName);
//            if (pair != null) {
//                for (int i = 0; i < pair.getValue().size(); i++) {
//                    seats.add(String.valueOf(pair.getKey()) + pair.getValue().get(i));
//                }
//                for (int i = 0; i < names.length; i++) {
//                    String name = names[i];
//                    newReservation = new Reservation(name, seats.get(i), "G", service_class);
//                    reservations.add(newReservation);
//                    grpReservations.add(newReservation);
//                    reservations.forEach(x -> System.out.println(x.getAllottedSeat() + " : " + x.getName())); // remove me
//                    if (service_class.equals("First")) {
//                        HashMap<Integer, List<String>> first = model.getValue();
//                        List<String> list = first.get(pair.getKey());
//                        List<String> modifiedList = new ArrayList<>(list);
//                        modifiedList.remove(seats.get(i).substring(1, 2));
//                        first.replace(Integer.parseInt(seats.get(i).substring(0, 1)), modifiedList);
//                    } else {//economy
//                        HashMap<Integer, List<String>> first = model.getKey();
//                        List<String> list = first.get(pair.getKey());
//                        List<String> modifiedList = new ArrayList<>(list);
//                        modifiedList.remove(seats.get(i).substring(2, 3));
//                        first.replace(Integer.parseInt(seats.get(i).substring(0, 2)), modifiedList);
//                    }
//                }
//                if (grpMap.containsKey(groupName)) {
//                    System.out.println("Group with same name exists! Try Again");
//                } else
//                    grpMap.put(groupName, grpReservations);
//            }
//        }
//
//
//    }
//
//    private Pair<Integer, List<Character>> generateAdjacentBooking(String[] names, String service_class, String groupName) throws Exception {
//        boolean nullReturn = false;
//        boolean nothingCanBeDone = false;
//        List<String> seats = new ArrayList<>();
//        int col = 0;
//        if (service_class.equals("First")) {
//            HashMap<Integer, List<String>> first = model.getValue();
//            for (int i = 0; i < 2; i++) {
//                List<String> list = first.get(i + 1);
//                Pair<String, Boolean> pair = isOrdered(names, list);
//                if (pair.getValue()) {
//                    seats.add(pair.getKey());
//                    col = i + 1;
//
////                    break;
//                } else {
//                    if (i == 1) {
//                        nothingCanBeDone = true;
//                    }
//                }
//            }
//            if (nothingCanBeDone) {
//                nullReturn = true;
//                addModifiedGrp(names, service_class, groupName);
//            }
//        } else {//economy
//            HashMap<Integer, List<String>> econ = model.getKey();
//            for (int i = 9; i < 29; i++) {
//                List<String> list = econ.get(i + 1);
//                Pair<String, Boolean> pair = isOrdered(names, list);
//                if (pair.getValue()) {
//                    seats.add(pair.getKey());
//                    col = i + 1;
//                    break;
//                }
//            }
//        }
//        if (!nullReturn) {
//            List<Character> cha = new ArrayList<>();
//            String seat = seats.get(0);
//            for (int i = 0; i < seat.length(); i++) {
//                cha.add(seat.charAt(i));
//            }
//            return new Pair<>(col, cha);
//        } else {
//            return null;
//        }
//    }
//
//    private Pair<String, Boolean> isOrdered(String[] names, List<String> list) {
//        StringBuilder str = new StringBuilder();
//        boolean isSequenced;
//        for (int j = 0; j < list.size(); j++) {
//            str.append(list.get(j));
//        }
//        boolean l = isInSequence(str.toString().toCharArray());
//        isSequenced = l;
//        return new Pair<>(str.toString(), isSequenced);
//
//    }
//
//    //    private Pair<String, Boolean> isInSequence(char[] s) {
////        StringBuilder b = new StringBuilder();
////        List<Character> ch = new ArrayList<>();
////        for (char c : s) {
////            ch.add(c);
////        }
////        int l = s.length;
////        Arrays.sort(s);
////        if (s.length >= 2) {
////            for (int i = 1; i < l; i++) {
////                if (ch.get(i) - ch.get(i - 1) != 1) {
////                    ch.remove(i - 1);
////                    char[] myCharArray = new char[ch.size()];
////                    for (int j = 0; j < ch.size(); j++) {
////                        myCharArray[j] = ch.get(j);
////                    }
////                    isInSequence(myCharArray);
////                }
////                for (char g : ch) {
////                    b.append(g);
////                }
////                return new Pair<>(b.toString(), true);
////            }
////        }
////        return new Pair<>(b.toString(), true);
////    }
//    private boolean isInSequence(char[] s) {
//        int l = s.length;
//        Arrays.sort(s);
//        for (int i = 1; i < l; i++) {
//            if (s[i] - s[i - 1] != 1)
//                return false;
//        }
//        return true;
//    }
//
//    private void addModifiedGrp(String[] names, String service_class, String groupName) throws Exception {
//        List<Reservation> grpReservations = new ArrayList<>();
//        Reservation newReservation;
//        boolean canBeAdded = false;
//        int maxAvailableSeats = 0;
//        if (service_class.equals("First")) {
//            for (int i = 0; i < 2; i++) {
//                maxAvailableSeats += model.getValue().get(i + 1).size();
//            }
//        } else {
//            //economy
//            for (int i = 9; i < 29; i++) {
//                maxAvailableSeats += model.getKey().get(i + 1).size();
//            }
//        }
//        if (names.length <= maxAvailableSeats)
//            canBeAdded = true;
//        else {
//            System.out.println("Sorry! Reservation cannot be made. Not enough seats available1");
//        }
//
//        if (canBeAdded && !grpMap.containsKey(groupName)) {
//            for (String name : names) {
//                newReservation = new Reservation(name, assignSeatForGroup(service_class), "G", service_class);
//                reservations.add(newReservation);
//                grpReservations.add(newReservation);
//                reservations.forEach(x -> System.out.println(x.getAllottedSeat() + " : " + x.getName())); // remove me
//            }
//            if (grpMap.containsKey(groupName)) {
//                System.out.println("Group with same name exists! Try Again");
//            } else
//                grpMap.put(groupName, grpReservations);
//        }
//    }
//

}

// todo basic flow
//  after finishes add null  -- fixed
//  no such reservation  --fixed
//  after remove add the seat -- fixed
//  multiple center seats  -- fixed
//  fix economy ordering  --fixed
//  remove error when multiple -- fixed
//  null pointer on economy while reading from file  --  how to initialize model when loading from file  --fixed
//  read from command line cl34 and input  --fixed
//  even if no seats are left, it adds as null -- group -- fixed
//  fix say no more seats for grp when full -- fixed
//  when adding in group it is assigned to last available seats instead of first occurrence --fixed
//  when removing group -- manifest is updated but the availibility is not updated --fixed
//   EXTRA  cancel individual reservation within group -- fixed
//  add group support for everything -- fixed
//  read from file for group reservation -- fixed
//  null pointer when manifest for group after cancel  --fixed
//  even if no seats are left, it adds as null -- individual -- FIXED
//  add documentation  --fixed
//  check if null given at any stage -- fixed


//todo corner cases
// reservation with same names cancellation?
// cancel individual reservation within group  -- fixed