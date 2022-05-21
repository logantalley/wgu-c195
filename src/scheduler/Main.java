package scheduler;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * This is the main function
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        AtomicInteger UserIDRes = new AtomicInteger(-1);
        AnchorPane root = new AnchorPane();
        PasswordField passwordField = new PasswordField();
        TextField usernameField = new TextField();
        Button loginButton = new Button();
        Alert alert = new Alert(Alert.AlertType.ERROR, "Bad username/password combo!", ButtonType.OK);
        String userLocale = Controller.getUserLocale();

        Label zoneLabel = new Label(userLocale);


/*        String userLocale = "CAfr";*/
        if (userLocale.equals("CAfr")) {
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

        System.out.println(userLocale);
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
        AnchorPane userSchedPane = new AnchorPane();
        userSchedPane.getChildren().add(scheduleTable);
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
