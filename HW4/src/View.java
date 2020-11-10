import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;

public class View {

    public static class MonthViewAndCreate extends JPanel implements ChangeListener {
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

        public MonthViewAndCreate(MyCalendar model) {
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

            frame.setResizable(false);
            frame.setVisible(true);
            print(calendar);

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


    public static class CalendarDayView extends JPanel implements ChangeListener {
        private MyCalendar model;
        private JLabel head;
        // JTextArea allows to have multiple lines (multiple events)
        private JTextArea eventDetails;


        public CalendarDayView(MyCalendar model) {
            this.model = model;
            setLayout(new GridBagLayout());
            head = new JLabel();
            eventDetails = new JTextArea();
            add(head);
            add(eventDetails);
        }

        private void printDay() {

        }

        @Override
        public void stateChanged(ChangeEvent e) {

        }
    }
}
