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
 * @author Rohan Surana
 * @version 1.0.0
 * @copyright 09-06-2020
 */
public class ReservationUtil {
    private final Scanner sc;
    private final ArrayList<Reservation> reservations;
    private Pair<HashMap<Integer, List<String>>, HashMap<Integer, List<String>>> model;
    private Map<String, List<Reservation>> grpMap;


    public ReservationUtil() {
        sc = new Scanner(System.in);
        reservations = new ArrayList<>();
        grpMap = new HashMap<>();
    }

    public void createNewData() {
        this.model = Model.createNewStr();
    }

    public boolean readFromExistingFile(String arg) throws IOException {

        List<Reservation> grpReservations = new ArrayList<>();
        boolean alrExists = false;
        File file = new File(arg);
        String[] data;
        if (file.exists() && !file.isDirectory()) {
            Scanner fileScanner = new Scanner(file);
            alrExists = true;
            while (fileScanner.hasNext()) {
                data = fileScanner.nextLine().split(", ");
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
            System.out.println("not exists");
        }
//        boolean k = file.createNewFile(); // if file already exists will do nothing
//        FileOutputStream outFile = new FileOutputStream(file, false);


//        boolean alrExists = false;
//        Reservation reservation;
//        File file = new File("Reservations.txt");
//        if (!file.exists()) {
//            System.out.println("No such file found, Possible reasons: First Run, or No such file exists");
//        } else {
//            try {
//                FileInputStream fileInputStream = new FileInputStream(file);
//                ObjectInputStream objInpStream = new ObjectInputStream(fileInputStream);
//                while (fileInputStream.available() > 0) {
//                    reservation = (Reservation) objInpStream.readObject();
//                    reservations.add(reservation);
//                }
//                if (fileInputStream.available() > 0) {
//                    System.out.println("Previous Reservations Loaded!");
//                    alrExists = true;
//                } else {
//                    System.out.println("No reservations found!");
//                }
//                fileInputStream.close();
//                objInpStream.close();
//            } catch (IOException | ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
        return alrExists;
    }

    public void updateModelAfterReadingFromFile() {
        for (Reservation r : reservations) {
            String seat = r.getSeat();
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

    public void addPassenger() throws Exception {
        System.out.println("Enter Name");
        String name = sc.nextLine();
        System.out.println("Select Class [First] or [Economy]");
        String service_class = sc.nextLine();
        String preference = null;
        int maxAvailableSeats = 0;
        boolean canBeAdded = false;
        if (service_class.equals("First")) {
            for (int i = 0; i < 2; i++) {
                maxAvailableSeats += model.getValue().get(i + 1).size();
            }
            if (maxAvailableSeats > 0) {
                canBeAdded = true;
            }
            System.out.println("Select preference [A]isle [W]indow");
            preference = sc.nextLine();
        } else if (service_class.equals("Economy")) {
            for (int i = 9; i < 29; i++) {
                maxAvailableSeats += model.getKey().get(i + 1).size();
            }
            if (maxAvailableSeats > 0) {
                canBeAdded = true;
            }
            System.out.println("Select preference [A]isle [W]indow [C]enter");
            preference = sc.nextLine();
        } else {
            System.out.println("Invalid Selection!, Try again");
        }
        if (canBeAdded) {
            Reservation newReservation = new Reservation(name, assignSeat(service_class, preference), "I", service_class);
            reservations.add(newReservation);
            System.out.println(reservations.size());
            reservations.forEach(x -> System.out.println(x.getSeat() + " : " + x.getName()));
        } else {
            System.out.println("No more seats available for this particular choice. Please try again!");
        }

    }

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

        if (canBeAdded) {
            for (String name : names) {
                newReservation = new Reservation(name, assignSeatForGroup(service_class), "G", service_class);
                reservations.add(newReservation);
                grpReservations.add(newReservation);
                reservations.forEach(x -> System.out.println(x.getSeat() + " : " + x.getName())); // remove me
            }
            grpMap.put(groupName, grpReservations);
        }
    }

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

    private String assignSeatForGroup(String service_class) throws Exception {
        String assignedSeat = null;
        int booked = 0;
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

    private String getSeatAndUpdateModel(HashMap<Integer, List<String>> first, int i, List<String> list, int j, String seat) {
        String assignedSeat = generateSeatNo(seat, i);
        List<String> modifiedList = new ArrayList<>(list);
        modifiedList.remove(list.get(j));
        first.replace(i + 1, modifiedList);
        return assignedSeat;
    }

    private String generateSeatNo(String key, int i) {
        return i + 1 + key;
    }

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
                    seat.add(value.getSeat());
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
                    seat.add(value.getSeat());
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

    public void printManifest() {
        System.out.println("Select Class [First] or [Economy]");
        String service_class = sc.nextLine();
        System.out.println(service_class);
        reservations.sort(new CompareReservation());
        for (Reservation reservation : reservations) {
            if (reservation.getService_class().equals(service_class)) {
                String manifest = reservation.getSeat() +
                        ": " +
                        reservation.getName();

                System.out.println(manifest);
            }
        }
    }

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

    public void saveToFile() {
        List<String> toWrite = new ArrayList<>();
        for (Reservation res : reservations) {
            if (res.getType().equals("I")) {
                String stringBuilder = res.getSeat() +
                        ", " +
                        res.getType() +
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
                String stringBuilder = res.getSeat() +
                        ", " +
                        res.getType() +
                        ", " +
                        key +
                        ", " +
                        res.getName();

                toWrite.add(stringBuilder);
            }
        }
        try {
            Path file = Paths.get("Reservations.txt");
            Collections.sort(toWrite);
            Files.write(file, toWrite, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getSeatAndUpdateModelForGroup(HashMap<Integer, List<String>> first, int i, List<String> list, int j, String seat) {
        String assignedSeat = generateSeatNo(seat, i);
        List<String> modifiedList = new ArrayList<>(list);
        modifiedList.remove(list.get(j));
        first.replace(i + 1, modifiedList);
        return assignedSeat;
    }
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
//  add documentation
//  check if null given at any stage


//todo corner cases
// reservation with same names cancellation?
// cancel individual reservation within group  -- fixed