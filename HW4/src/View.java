import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class View extends JPanel implements ChangeListener {


    private final MyCalendar model;
    private final JPanel monthView;
    private final ArrayList<JButton> daysButtons = new ArrayList<>();
    private JPanel dayContainer;
    private boolean firstRun;
    private JTextArea dayView;

    public View(MyCalendar model) {

        firstRun = true;
        this.model = model;
        // JTextArea allows to have multiple lines (multiple events)
        dayView = new JTextArea();
        dayView.setPreferredSize(new Dimension(450, 350));
        dayView.setEditable(false);
        monthView = new JPanel();
        monthView.setLayout(new GridLayout(0, 7));
        monthView.setPreferredSize(new Dimension(300, 300));
        createButtons(model.getGregorianCalendar().get(Calendar.DAY_OF_MONTH), model.getGregorianCalendar());
        highlight(model.getGregorianCalendar().get(Calendar.DAY_OF_MONTH) - 1, model.getGregorianCalendar());

        JButton createEvent = new JButton("Create");
        createEvent.setPreferredSize(new Dimension(50, 50));
        createEvent.addActionListener(e -> createEventPopup());
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        JLabel monthName = new JLabel();
        monthName.setText(Util.getMonths().get(model.getGregorianCalendar().get(Calendar.MONTH)) + " " + model.getGregorianCalendar().get(Calendar.YEAR));
        container.add(monthName, BorderLayout.NORTH);
        container.add(new JLabel("       S             M             T             W             T              F             S"), BorderLayout.CENTER);
        container.add(monthView, BorderLayout.SOUTH);


        JButton previousDayButton = new JButton("<");
        previousDayButton.addActionListener(e -> {
            GregorianCalendar cal = model.getGregorianCalendar();
            cal.add(Calendar.DAY_OF_MONTH, -1);
//            this.createButtons(cal.get(Calendar.DAY_OF_MONTH), cal);
            model.updateListeners(cal);
//            model.update();
        });
        JButton nextDayButton = new JButton(">");
        nextDayButton.addActionListener(e -> {
            GregorianCalendar cal = model.getGregorianCalendar();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            model.updateListeners(cal);
//            this.createButtons(cal.get(Calendar.DAY_OF_MONTH), cal);
//            model.update();
        });
        JButton quitButton = new JButton("Quit");
        quitButton.setPreferredSize(new Dimension(60, 30));
        quitButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5, true));
        quitButton.addActionListener(e -> {
            model.quit();
            Runtime.getRuntime().exit(0);
        });
        add(previousDayButton);
        add(nextDayButton);
        add(quitButton);
        dateAndDetails(model.getGregorianCalendar().get(Calendar.DAY_OF_MONTH));

        JFrame frame = new JFrame();
        frame.add(container);
        frame.add(dayView);
        frame.add(createEvent);
        frame.add(previousDayButton);
        frame.add(nextDayButton);
        frame.add(quitButton);
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    public void createButtons(int i, GregorianCalendar gregorianCalendar) {

//        monthView.removeAll();
        for (int k = 1; k <= gregorianCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); k++) {
            JButton button = new JButton("" + k);
            button.setPreferredSize(new Dimension(4, 4));

            button.addActionListener(x -> {
                String date = button.getText();
                GregorianCalendar calendar = new GregorianCalendar(model.getGregorianCalendar().get(Calendar.YEAR), model.getGregorianCalendar().get(Calendar.MONTH), model.getGregorianCalendar().get(Calendar.DAY_OF_MONTH));
                highlight(Integer.parseInt(date) - 1, calendar);
                model.setToday(LocalDate.of(model.getGregorianCalendar().get(Calendar.YEAR), model.getGregorianCalendar().get(Calendar.MONTH), Integer.parseInt(date)));
                model.update();
            });
            daysButtons.add(button);
        }

        for (int j = 1; j < gregorianCalendar.get(Calendar.DAY_OF_WEEK); j++) {
            JButton blank = new JButton();
//            blank.setEnabled(false);
            monthView.add(blank);
        }


        for (JButton button : daysButtons) {
            monthView.add(button);
        }


    }

    public void highlight(int i, GregorianCalendar calendar) {
        if (i != 0) {
            System.out.println(daysButtons.get(i).getText());
            daysButtons.get(i).setBorder(new LineBorder(Color.RED, 2));
        }
        if (!firstRun) {
            daysButtons.get(i - 1).setBorder(new JButton().getBorder());
        }
        firstRun = false;

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

    private void dateAndDetails(int o) {
        model.setToday(LocalDate.of(model.getGregorianCalendar().get(Calendar.YEAR), model.getGregorianCalendar().get(Calendar.MONTH), o));
        int dayOfWeek = model.getGregorianCalendar().get(Calendar.DAY_OF_WEEK);
        String date = (model.getGregorianCalendar().get(Calendar.MONTH) + "/" + o);
        String events = "";
//        if (model.hasEvent(date)) {
//            events += model.getEvents(date);
//        }
        dayView.setText(Util.getDays().get(dayOfWeek - 1) + " " + date + "\n" + events);
    }


    @Override
    public void stateChanged(ChangeEvent e) {
//        daysButtons.clear();
//        monthView.removeAll();
        monthView.validate();
        monthView.repaint();
        dateAndDetails(model.getGregorianCalendar().get(Calendar.DAY_OF_MONTH));
//        createButtons(model.getGregorianCalendar().get(Calendar.DAY_OF_MONTH), model.getGregorianCalendar());
        highlight(model.getGregorianCalendar().get(Calendar.DAY_OF_MONTH) - 1, model.getGregorianCalendar());


    }

}

