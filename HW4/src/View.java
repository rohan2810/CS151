import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

public class View extends JPanel implements ChangeListener {


    private static int today;
    private static int month;
    private static int currentMonth;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JLabel label;
    //        private JLabel head;
    // JTextArea allows to have multiple lines (multiple events)
    private JTextArea eventDetails;
    private MyCalendar model;
    private ArrayList<String> monthYear = new ArrayList<>();
    private JFrame frame;
    private JButton createEvent = new JButton("Create");

    public View(MyCalendar model) {
        GregorianCalendar calendar = new GregorianCalendar();
        //            head = new JLabel();
//            eventDetails = new JTextArea();
//            add(head);
//            add(eventDetails);
        monthYear.add("January");
        monthYear.add("February");
        monthYear.add("March");
        monthYear.add("April");
        monthYear.add("May");
        monthYear.add("June");
        monthYear.add("July");
        monthYear.add("August");
        monthYear.add("September");
        monthYear.add("October");
        monthYear.add("November");
        monthYear.add("December");

        this.model = model;
        tableModel = new DefaultTableModel();
        tableModel.setColumnCount(7);
        tableModel.setRowCount(6);
        table = new JTable(tableModel);
        table.setRowHeight(30);
        label = new JLabel();
        frame = new JFrame();
        frame.setSize(100, 100);
        JPanel panel = new JPanel(null);
        panel.add(label);

        //change this
        panel.setBounds(0, 0, 320, 335);
        label.setBounds(160 - label.getPreferredSize().width / 2, 25, 100, 25);


        print(calendar);

        createEvent.setPreferredSize(new Dimension(50, 50));
        createEvent.addActionListener(e -> createEventPopup());
        frame.add(createEvent);
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createEventPopup() {
        JFrame createFrame = new JFrame();
        createFrame.setTitle("Create New Event");
        JTextField eventName = new JTextField(30);
        JTextField startTime = new JTextField(10);
        JTextField endTime = new JTextField(10);
        JButton saveButton = new JButton("SAVE");
        saveButton.setSize(50, 50);
        saveButton.addActionListener(e -> {
            if (eventName.getText().length() == 0) {
                return;
            } else if ((startTime.getText().isEmpty() && startTime.getText().length() != 5) &&
                    (endTime.getText().isEmpty() && endTime.getText().length() != 5)) {
                JFrame error = new JFrame();
                error.setLayout(new GridLayout(2, 0));
                JLabel errorMessage = new JLabel("Please check your start or end time! It should be in HH:mm format");
                error.add(errorMessage);
                JButton goBack = new JButton("Go Back");
                goBack.addActionListener(e1 -> error.dispose());
                error.add(goBack);
                error.pack();
                error.setVisible(true);
            } else if (model.saveEvents(eventName.getText(), model.getGregorianCalendar().toZonedDateTime().toLocalDate(),
                    LocalTime.parse(startTime.getText(), DateTimeFormatter.ISO_LOCAL_TIME),
                    LocalTime.parse(endTime.getText(), DateTimeFormatter.ISO_LOCAL_TIME)).getValue()) {
                JFrame conflictMessage = new JFrame();
                conflictMessage.setLayout(new GridLayout(2, 0));
                JLabel jLabel = new JLabel("Event Time is conflicting! Please try again.");
                conflictMessage.add(jLabel);
                JButton goBack = new JButton("Go Back");
                goBack.addActionListener(e2 -> conflictMessage.dispose());
                conflictMessage.add(goBack);
                conflictMessage.setVisible(true);  // check this order
                conflictMessage.pack();
            } else {
                createFrame.dispose();
                model.saveEvents(eventName.getText(), model.getGregorianCalendar().toZonedDateTime().toLocalDate(),
                        LocalTime.parse(startTime.getText(), DateTimeFormatter.ISO_LOCAL_TIME),
                        LocalTime.parse(endTime.getText(), DateTimeFormatter.ISO_LOCAL_TIME));


            }
        });
        createFrame.setLayout(new GridBagLayout());
        createFrame.add(new JLabel("Event"));
        createFrame.add(eventName);
        createFrame.add(startTime);
        createFrame.add(endTime);
        createFrame.add(saveButton);
        createFrame.pack();
        createFrame.setVisible(true);
    }


    private void print(GregorianCalendar calendar) {

        //check this
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.setColumnSelectionAllowed(true);
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        today = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        label.setText(monthYear.get(model.getGregorianCalendar().get(Calendar.MONTH)) + " "
                + model.getGregorianCalendar().get(Calendar.YEAR));
        int currentDate = today;
        currentMonth = month;
        List<String> dayName = Arrays.asList("S", "M", "T", "W", "T", "F", "S");
        for (String s : dayName) {
            tableModel.addColumn(s);
        }

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    GregorianCalendar calendar = new GregorianCalendar(model.getGregorianCalendar().get(Calendar.YEAR),
                            model.getGregorianCalendar().get(Calendar.MONTH), model.getGregorianCalendar().get(Calendar.DATE));
                    model.updateListeners(calendar);
                }
            }
        });
        printCalendar(year, month);
        frame.setVisible(true);

    }

    private void printCalendar(int year, int month) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(year, month, 1);
        int date = gregorianCalendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        int day = gregorianCalendar.get(GregorianCalendar.DAY_OF_WEEK);

        for (int i = 0; i < date; i++) {
            int dayCol = (i + day - 1) % 7;
            int dateRow = (i + day - 1) / 7;
            tableModel.setValueAt(i + 1, dateRow, dayCol);
        }
        table.setDefaultRenderer(table.getColumnClass(0), new cellDesignMonthView());
        frame.setVisible(true);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        print(model.getGregorianCalendar());
    }

    private static class cellDesignMonthView extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if ((int) value == today && (currentMonth == month)) {
                setBounds(new Rectangle(11, 11, 11, 10));
            } else if (row == 1 && column == 1) {
                setBounds(new Rectangle(11, 11, 11, 10));
            }
            return this;
        }
    }
}

