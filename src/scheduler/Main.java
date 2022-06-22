package scheduler;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;



public class Main extends Application {
    @Override
    /**
     * This is the main function where we call all of our functions in
     * the Controller class.
     */
    public void start(Stage primaryStage) throws SQLException {
        AtomicInteger UserIDRes = new AtomicInteger(-1);
        AnchorPane root = new AnchorPane();
        PasswordField passwordField = new PasswordField();
        TextField usernameField = new TextField();
        Button loginButton = new Button();
        Alert alert = new Alert(Alert.AlertType.ERROR, "Bad username/password combo!", ButtonType.OK);
        String userLang = Controller.getUserLang();
        String userZone = TimeZone.getTimeZone(ZoneId.systemDefault()).getDisplayName();



        System.out.println(userZone);
        //System.out.println(LocalTime.now(userZone));
        ObservableList<Country> countryList = Controller.getCountries();



        Label zoneLabel = new Label(userLang + "-" + userZone);



        if (userLang.equals("fr")) {
            usernameField.setPromptText("Nom d'utilisateur");
            passwordField.setPromptText("Le mot de passe");
            loginButton.setText("Connexion");
            primaryStage.setTitle("Planificateur");
            alert.setContentText("Mauvais combo nom d'utilisateur/mot de passe !");
            alert.setTitle("ERREUR");

        }else{
            usernameField.setPromptText("Username");
            passwordField.setPromptText("Password");
            loginButton.setText("Login");
            primaryStage.setTitle("Scheduler");
        }

        System.out.println(userLang);
        Stage mainMenu = new Stage();
        Button btnCustomers = new Button("Customers");
        Button btnSchedule = new Button("Schedule");
        Button btnApptReport = new Button("Appointment Report");
        Button btnContactReport = new Button("Contact Report");
        Button btnCustomerReport = new Button("Customer Report");
        AnchorPane mainAnchor = new AnchorPane();
        mainAnchor.getChildren().addAll(btnSchedule, btnCustomers, btnApptReport, btnContactReport, btnCustomerReport);
        AnchorPane.setRightAnchor(btnCustomers, 300d);
        AnchorPane.setTopAnchor(btnCustomers, 75d);
        AnchorPane.setLeftAnchor(btnSchedule, 300d);
        AnchorPane.setTopAnchor(btnSchedule, 75d);
        AnchorPane.setLeftAnchor(btnContactReport, 200d);
        AnchorPane.setTopAnchor(btnContactReport, 150d);
        AnchorPane.setTopAnchor(btnApptReport, 150d);
        AnchorPane.setRightAnchor(btnApptReport, 200d);
        AnchorPane.setTopAnchor(btnCustomerReport, 150d);
        AnchorPane.setRightAnchor(btnCustomerReport,375d);

        Scene mainScene = new Scene(mainAnchor, 850, 250);
        mainMenu.setScene(mainScene);

        btnApptReport.setOnAction(e -> {
            try {
                Controller.showApptReport();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

        btnContactReport.setOnAction(e -> {
            try {
                Controller.showContactReport();
            } catch (SQLException | ParseException throwables) {
                throwables.printStackTrace();
            }
        });

        btnCustomerReport.setOnAction(e -> {
            try {
                Controller.showCustomerReport();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

        TableView<Schedule> scheduleTable = Controller.generateScheduleTable();
        TableView<Customer> customerTable = Controller.generateCustomerTable();


        Stage userScheduleStage = new Stage();
        final ToggleGroup schedView = new ToggleGroup();

        RadioButton byWeek = new RadioButton("Week");
        byWeek.setToggleGroup(schedView);
        byWeek.setOnAction(x -> {
            ObservableList<Schedule> scheduleList = null;
            try {
                scheduleList = Controller.getScheduleByTime(UserIDRes.get(), "byWeek");
            } catch (SQLException | ParseException throwables) {
                throwables.printStackTrace();
            }
            Controller.updateTable(scheduleTable, scheduleList);
        });

        RadioButton byMonth = new RadioButton("Month");
        byMonth.setToggleGroup(schedView);
        byMonth.setOnAction(x -> {
            ObservableList<Schedule> scheduleList = null;
            try {
                scheduleList = Controller.getScheduleByTime(UserIDRes.get(), "byMonth");
            } catch (SQLException | ParseException throwables) {
                throwables.printStackTrace();
            }
            Controller.updateTable(scheduleTable, scheduleList);
        });

        RadioButton byAll = new RadioButton("All");
        byAll.setToggleGroup(schedView);
        byAll.setSelected(true);
        byAll.setOnAction(x -> {
            ObservableList<Schedule> scheduleList = null;
            try {
                scheduleList = Controller.getScheduleByTime(UserIDRes.get(), "all");
            } catch (SQLException | ParseException throwables) {
                throwables.printStackTrace();
            }
            Controller.updateTable(scheduleTable, scheduleList);
        });


        Button schedAddBtn = new Button("Add");
        Button schedModBtn = new Button("Modify");
        Button schedChangeTime = new Button("Change Time");
        Button schedDelBtn = new Button("Delete");
        HBox viewBox = new HBox(5, byAll, byWeek, byMonth);

        HBox schedBtnBox = new HBox(5, schedAddBtn, schedModBtn, schedChangeTime, schedDelBtn);
        AnchorPane userSchedPane = new AnchorPane(scheduleTable, schedBtnBox, viewBox);
        AnchorPane.setTopAnchor(viewBox, 10d);
        AnchorPane.setLeftAnchor(viewBox, 10d);
        AnchorPane.setRightAnchor(viewBox, 10d);
        AnchorPane.setBottomAnchor(schedBtnBox, 10d);
        AnchorPane.setRightAnchor(schedBtnBox, 10d);
        AnchorPane.setTopAnchor(scheduleTable, 40d);
        AnchorPane.setBottomAnchor(scheduleTable, 45d);
        AnchorPane.setRightAnchor(scheduleTable, 10d);
        AnchorPane.setLeftAnchor(scheduleTable, 10d);
        Scene userSchedScene = new Scene(userSchedPane, 850, 250);
        userScheduleStage.setScene(userSchedScene);
        userScheduleStage.setTitle("Schedule");

        schedAddBtn.setOnAction(x -> {
            try {
                Controller.addAppt(UserIDRes.get(), scheduleTable);
            } catch (SQLException | ParseException throwables) {
                throwables.printStackTrace();
            }
        });

        schedDelBtn.setOnAction(e -> {
            Schedule selectedAppt = scheduleTable.getSelectionModel().getSelectedItem();
            try {
                Controller.delAppt(UserIDRes.get(), scheduleTable, selectedAppt);
            } catch (SQLException | ParseException throwables) {
                throwables.printStackTrace();
            }
        });

        schedModBtn.setOnAction(e -> {
            Schedule selectedAppt = scheduleTable.getSelectionModel().getSelectedItem();
            try {
                Controller.modAppt(UserIDRes.get(), scheduleTable, selectedAppt);
            } catch (SQLException | ParseException throwables) {
                throwables.printStackTrace();
            }

        });

        schedChangeTime.setOnAction(e -> {
            Schedule selectedAppt = scheduleTable.getSelectionModel().getSelectedItem();
            try {
                Controller.modApptTime(UserIDRes.get(), scheduleTable, selectedAppt);
            } catch (SQLException | ParseException throwables) {
                throwables.printStackTrace();
            }

        });

        btnSchedule.setOnAction(e -> {
            userScheduleStage.show();
        });
        Button custAddBtn = new Button("Add");
        Button custModBtn = new Button("Modify");
        Button custDelBtn = new Button("Delete");

        HBox custBtnBox = new HBox(5, custAddBtn, custModBtn, custDelBtn);

        AnchorPane partAnchor = new AnchorPane(customerTable, custBtnBox);
        AnchorPane.setBottomAnchor(custBtnBox, 10d);
        AnchorPane.setRightAnchor(custBtnBox, 10d);
        AnchorPane.setTopAnchor(customerTable, 40d);
        AnchorPane.setBottomAnchor(customerTable, 45d);
        AnchorPane.setRightAnchor(customerTable, 10d);
        AnchorPane.setLeftAnchor(customerTable, 10d);
        Stage customerStage = new Stage();
        Scene customerScene = new Scene(partAnchor, 850, 250);
        customerStage.setScene(customerScene);
        customerStage.setTitle("Customers");

        custAddBtn.setOnAction(e -> {
            try {
                Controller.addCustomer(customerTable, countryList, (Integer.valueOf(String.valueOf(UserIDRes))));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

        custModBtn.setOnAction(e -> {
            Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
            try {
                Controller.modCustomer(customerTable, countryList, (Integer.valueOf(String.valueOf(UserIDRes))), selectedCustomer);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

        custDelBtn.setOnAction(e -> {
            Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
            try {
                Controller.delCustomer(customerTable, selectedCustomer);
            } catch (SQLException | ParseException throwables) {
                throwables.printStackTrace();
            }
        });


        btnCustomers.setOnAction(e -> {
            ResultSet customerResult = Controller.getCustomers();
            try {
                ObservableList<Customer> customerList = Controller.generateCustomerList(customerResult);
                Controller.updateTable(customerTable, customerList);
            } catch (SQLException | ParseException throwables) {
                throwables.printStackTrace();
            }
            customerStage.show();
        });





        loginButton.setOnAction(e -> {
            PrintWriter printWriter = null;
            try {
                FileWriter fileWriter = new FileWriter("login_activity.txt", true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                printWriter = new PrintWriter(bufferedWriter);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            UserIDRes.set(Controller.loginButtonHandler(usernameField, passwordField, alert));
            if (UserIDRes.get() != -1){
                assert printWriter != null;
                printWriter.println("Login Attempt: " + Instant.now() + " " + usernameField.getText() + " SUCCESS");
                printWriter.close();
                try {
                    ObservableList<Schedule> userList = Controller.getScheduleByTime(UserIDRes.get(), "all");
                    Controller.updateTable(scheduleTable, userList);
                    final Boolean[] notifyAgent = {null};
                    DateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");

                    Timestamp now = Timestamp.from(Instant.now());
                    Timestamp soon = Timestamp.from(Instant.now().plusSeconds(900));

                    Timestamp localNow = new Timestamp(defaultFormat.parse(defaultFormat.format(now)).getTime());
                    Timestamp localSoon = new Timestamp(defaultFormat.parse(defaultFormat.format(soon)).getTime());

                    System.out.println(localNow);
                    System.out.println(localSoon);


                    Schedule upcomingAppt = null;
                    for (Schedule iAppt : userList) {
                        Timestamp iStart = new Timestamp(defaultFormat.parse(iAppt.getApptStart()).getTime());
                        if (iStart.after(localNow) && iStart.before(localSoon)){
                            upcomingAppt = iAppt;
                            notifyAgent[0] = true;
                        } else{
                            if (notifyAgent[0] == null){
                                notifyAgent[0] = false;

                            }
                        }
                    }

                    Alert apptAlert;
                    if(notifyAgent[0]){
                        assert upcomingAppt != null;
                        apptAlert = new Alert(Alert.AlertType.INFORMATION, "Appointment within 15 minutes!\n" +
                                "Appoinment ID: " + upcomingAppt.getApptID() + "\n"
                                + "Appontment Time: " + upcomingAppt.getApptStart(), ButtonType.OK);
                    } else {
                        apptAlert = new Alert(Alert.AlertType.INFORMATION, "No appointment within 15 minutes.", ButtonType.OK);
                    }
                    apptAlert.showAndWait();

                } catch (SQLException | ParseException throwables) {
                    throwables.printStackTrace();
                }
                primaryStage.close();
                mainMenu.show();
            } else {
                assert printWriter != null;
                printWriter.println("Login Attempt: " + Instant.now() + " " + usernameField.getText() + " FAILURE");
                printWriter.close();

            }
        });
        root.getChildren().addAll(usernameField, passwordField, loginButton, zoneLabel);
        AnchorPane.setRightAnchor(zoneLabel, 10d);
        AnchorPane.setBottomAnchor(zoneLabel, 10d);
        AnchorPane.setBottomAnchor(loginButton, 50d);
        AnchorPane.setTopAnchor(usernameField, 10d);
        AnchorPane.setTopAnchor(passwordField, 40d);
        AnchorPane.setLeftAnchor(usernameField, 20d);
        AnchorPane.setRightAnchor(usernameField, 20d);
        AnchorPane.setLeftAnchor(passwordField, 20d);
        AnchorPane.setRightAnchor(passwordField, 20d);
        AnchorPane.setLeftAnchor(loginButton, 30d);
        AnchorPane.setRightAnchor(loginButton, 30d);

        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        JDBC.makeConnection();
        launch(args);
    }
}
