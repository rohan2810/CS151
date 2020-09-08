import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Rohan Surana
 * @version 1.0.0
 * @copyright 09-06-2020
 */
public class ReservationUtil {
    private final Scanner sc;
    private final ArrayList<Reservation> reservations;
    private Pair<HashMap<Integer, List<String>>, HashMap<Integer, List<String>>> model;


    public ReservationUtil() {
        sc = new Scanner(System.in);
        reservations = new ArrayList<>();
    }

    public void createNewData() {
        this.model = Model.createNewStr();
    }

    public void addPassenger() throws Exception {
        System.out.println("Enter Name");
        String name = sc.nextLine();
        System.out.println("Select Class [First] or [Economy]");
        String service_class = sc.nextLine();
        String preference = null;
        if (service_class.equals("First")) {
            System.out.println("Select preference [A]isle [W]indow");
            preference = sc.nextLine();
        } else if (service_class.equals("Economy")) {
            System.out.println("Select preference [A]isle [W]indow [C]enter");
            preference = sc.nextLine();
        } else {
            System.out.println("Invalid Selection!, Try again");
        }

        Reservation newReservation = new Reservation(name, assignSeat(service_class, preference), "I", service_class);
        reservations.add(newReservation);
        System.out.println(reservations.size());
        reservations.forEach(x -> System.out.println(x.getSeat() + " : " + x.getName()));

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
                        String type = list.get(j);
                        if ((type.equals("A")) || (type.equals("D"))) {
                            assignedSeat = getSeatAndUpdateModel(first, i, list, j, type);
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

    private String getSeatAndUpdateModel(HashMap<Integer, List<String>> first, int i, List<String> list, int j, String type) {
        String assignedSeat;
        assignedSeat = generateSeatNo(type, i);
        List<String> modifiedList = new ArrayList<>(list);
        modifiedList.remove(list.get(j));
        first.replace(i + 1, modifiedList);
//        first.remove(i + 1);
//        first.put(i + 1, modifiedList);
        return assignedSeat;
    }

    private String generateSeatNo(String key, int i) {
        return i + 1 + key;
    }

    public void saveToFile() {
        List<String> toWrite = new ArrayList<>();
        for (Reservation res : reservations) {
            String stringBuilder = res.getSeat() +
                    ", " +
                    res.getType() +
                    " , " +
                    res.getName();
            toWrite.add(stringBuilder);
        }
        try {
            Path file = Paths.get("Reservations.txt");
            Files.write(file, toWrite, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
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

    public void cancelReservation() {
        System.out.println("Select [I]ndividual or [G]roup");
        String type = sc.nextLine();  //todo  fixme
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
    }

    public boolean readFromExistingFile() throws IOException {


        boolean alrExists = false;
        File file = new File("Reservations.txt");
        String[] data;
        if (file.exists() && !file.isDirectory()) {
            Scanner fileScanner = new Scanner(file);
            alrExists = true;
            while (fileScanner.hasNext()) {
                data = fileScanner.nextLine().split(",");
                System.out.println(Arrays.toString(data));  // remove me
                Reservation reservation;
                if (data.length == 4) {
                    //todo fixme
                } else {
                    //business
                    if (data[0].length() == 2) {
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

    private void addAgain(List<String> toAdd) {
        for (String seat : toAdd) {
            if (seat.length() == 2) {
                int num = Integer.parseInt(seat.substring(0, 1));
                List<String> modifiedList = model.getValue().get(num);
                modifiedList.add(seat.substring(1, 2));
                Collections.sort(modifiedList);
                List<String> newList = new ArrayList<>(modifiedList);
                model.getValue().replace(num, newList);
            } else if (seat.length() == 3) {
                int num = Integer.parseInt(seat.substring(0, 2));
                List<String> modifiedList = model.getKey().get(num);
                modifiedList.add(seat.substring(2, 3));
                Collections.sort(modifiedList);
                List<String> newList = new ArrayList<>(modifiedList);
                model.getKey().replace(num, newList);
            }
        }
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

}

// todo basic flow
//  after finishes add null  -- fixed
//  no such reservation  --fixed
//  after remove add the seat -- fixed
//  multiple center seats  -- fixed
//  fix economy ordering  --fixed
//  remove error when multiple -- fixed
//  null pointer on economy while reading from file  --  how to initialize model when loading from file  --fixed
//  add group support for everything
//  add documentation


//todo corner cases
// reservation with same names cancellation?