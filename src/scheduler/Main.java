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
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        Button loginButton = new Button("Login");
        TableView scheduleTable = Controller.generateScheduleTable();





        /*ResultSet customerResult = Controller.getCustomers();
        TableView customerTable = Controller.generateTable(customerResult);*/


        loginButton.setOnAction(e -> {
            int UserIDRes = Controller.loginButtonHandler(usernameField, passwordField);
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
        root.getChildren().addAll(usernameField, passwordField, loginButton);
        AnchorPane.setBottomAnchor(loginButton, 50d);
        AnchorPane.setTopAnchor(usernameField, 10d);
        AnchorPane.setTopAnchor(passwordField, 40d);
        AnchorPane.setLeftAnchor(usernameField, 20d);
        AnchorPane.setRightAnchor(usernameField, 20d);
        AnchorPane.setLeftAnchor(passwordField, 20d);
        AnchorPane.setRightAnchor(passwordField, 20d);
        AnchorPane.setLeftAnchor(loginButton, 30d);
        AnchorPane.setRightAnchor(loginButton, 30d);
        primaryStage.setTitle("Scheduler");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        JDBC.makeConnection();
        launch(args);
    }
}
