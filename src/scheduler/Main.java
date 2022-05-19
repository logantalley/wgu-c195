package scheduler;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
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
        AtomicBoolean signedInStatus = new AtomicBoolean(false);
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

        TableView<Schedule> scheduleTable = Controller.generateScheduleTable();
        TableView<Customer> customerTable = Controller.generateCustomerTable();





        /*ResultSet customerResult = Controller.getCustomers();
        TableView customerTable = Controller.generateTable(customerResult);*/

//        if (UserIDRes != -1){
//            ResultSet userSchedule = Controller.getSchedule(UserIDRes);
//            try {
//                ObservableList<Schedule> userList = Controller.generateScheduleList(userSchedule);
//                Controller.updateTable(scheduleTable, userList);
//                Stage userScheduleStage = new Stage();
//                AnchorPane userSchedPane = new AnchorPane();
//                userSchedPane.getChildren().add(scheduleTable);
//                Scene userSchedScene = new Scene(userSchedPane, 850, 250);
//                userScheduleStage.setScene(userSchedScene);
//                userScheduleStage.setTitle("Schedule");
//                userScheduleStage.show();
//            } catch (SQLException throwables) {
//                throwables.printStackTrace();
//            }
//        }
        loginButton.setOnAction(e -> {
            UserIDRes.set(Controller.loginButtonHandler(usernameField, passwordField, alert));
            signedInStatus.set(true);
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
