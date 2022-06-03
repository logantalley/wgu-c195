package scheduler;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * This is the main function
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws SQLException {
        AtomicInteger UserIDRes = new AtomicInteger(-1);
        AnchorPane root = new AnchorPane();
        PasswordField passwordField = new PasswordField();
        TextField usernameField = new TextField();
        Button loginButton = new Button();
        Alert alert = new Alert(Alert.AlertType.ERROR, "Bad username/password combo!", ButtonType.OK);
        String userLang = Controller.getUserLang();
        ObservableList<Country> countryList = Controller.getCountries();



        Label zoneLabel = new Label(userLang);


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
        AnchorPane mainAnchor = new AnchorPane();
        mainAnchor.getChildren().addAll(btnSchedule, btnCustomers);
        AnchorPane.setRightAnchor(btnCustomers, 10d);
        AnchorPane.setLeftAnchor(btnSchedule, 10d);
        Scene mainScene = new Scene(mainAnchor, 850, 250);
        mainMenu.setScene(mainScene);

        TableView<Schedule> scheduleTable = Controller.generateScheduleTable();
        TableView<Customer> customerTable = Controller.generateCustomerTable();


        Stage userScheduleStage = new Stage();


        Button schedAddBtn = new Button("Add");
        Button schedModBtn = new Button("Modify");
        Button schedDelBtn = new Button("Delete");

        HBox schedBtnBox = new HBox(5, schedAddBtn, schedModBtn, schedDelBtn);
        AnchorPane userSchedPane = new AnchorPane(scheduleTable, schedBtnBox);
        AnchorPane.setBottomAnchor(schedBtnBox, 10d);
        AnchorPane.setRightAnchor(schedBtnBox, 10d);
        AnchorPane.setTopAnchor(scheduleTable, 40d);
        AnchorPane.setBottomAnchor(scheduleTable, 45d);
        AnchorPane.setRightAnchor(scheduleTable, 10d);
        AnchorPane.setLeftAnchor(scheduleTable, 10d);
        Scene userSchedScene = new Scene(userSchedPane, 850, 250);
        userScheduleStage.setScene(userSchedScene);
        userScheduleStage.setTitle("Schedule");

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
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });


        btnCustomers.setOnAction(e -> {
            ResultSet customerResult = Controller.getCustomers();
            try {
                ObservableList<Customer> customerList = Controller.generateCustomerList(customerResult);
                Controller.updateTable(customerTable, customerList);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            customerStage.show();
        });





        loginButton.setOnAction(e -> {
            UserIDRes.set(Controller.loginButtonHandler(usernameField, passwordField, alert));
            if (UserIDRes.get() != -1){
                ResultSet userSchedule = Controller.getUserSchedule(UserIDRes.get());
                try {
                    ObservableList<Schedule> userList = Controller.generateScheduleList(userSchedule);
                    Controller.updateTable(scheduleTable, userList);

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                primaryStage.close();
                mainMenu.show();
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
