package scheduler;

import com.mysql.cj.x.protobuf.MysqlxPrepare;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * This is the main function
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
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

        TableView scheduleTable = Controller.generateScheduleTable();





        /*ResultSet customerResult = Controller.getCustomers();
        TableView customerTable = Controller.generateTable(customerResult);*/


        loginButton.setOnAction(e -> {
            int UserIDRes = Controller.loginButtonHandler(usernameField, passwordField, alert);
            if (UserIDRes != -1){
                ResultSet userSchedule = Controller.getSchedule(UserIDRes);
                try {
                    ObservableList<Schedule> userList = Controller.generateScheduleList(userSchedule);
                    Controller.updateScheduleTable(scheduleTable, userList);
                    Stage userScheduleStage = new Stage();
                    AnchorPane userSchedPane = new AnchorPane();
                    userSchedPane.getChildren().add(scheduleTable);
                    Scene userSchedScene = new Scene(userSchedPane, 850, 250);
                    userScheduleStage.setScene(userSchedScene);
                    userScheduleStage.setTitle("Schedule");
                    userScheduleStage.show();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
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
