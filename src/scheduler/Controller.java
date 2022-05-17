package scheduler;

import com.mysql.cj.xdevapi.Table;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Controller {
    /**
     *
     * @param usernameField passed as an input from the Login screen
     * @param passwordField passed as an input from the Login screen
     * @return An integer, either the matching UserID, or else -1, for error catching
     */
    public static int loginButtonHandler(TextField usernameField, PasswordField passwordField, Alert alert){
        String loginQuery = "select User_ID from users where User_Name = ? and Password = ?;";
        try {
            JDBC.makePreparedStatement(loginQuery, JDBC.getConnection());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        PreparedStatement loginStmt = null;
        try {
            loginStmt = JDBC.getPreparedStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        assert loginStmt != null;
        try {
            loginStmt.setString(1, usernameField.getText());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            loginStmt.setString(2, passwordField.getText());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        //System.out.println(loginStmt);
        ResultSet loginResult = null;
        try {
            loginResult = loginStmt.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            if (loginResult.next()){
                int UserID = Integer.parseInt(loginResult.getString(1));
                return UserID;


            }else{
                alert.showAndWait();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return -1;
    }

    /**
     *
     * @param UserID an Integer to look up the schedule with
     * @return A ResultSet with the User's schedule
     */
    public static ResultSet getSchedule (int UserID){
        String scheduleQuery = """
                select * from appointments where User_ID = ?;
                """;
        try {
            JDBC.makePreparedStatement(scheduleQuery, JDBC.getConnection());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        PreparedStatement scheduleStmt = null;
        try {
            scheduleStmt = JDBC.getPreparedStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            assert scheduleStmt != null;
            scheduleStmt.setInt(1, UserID);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        ResultSet scheduleResult = null;
        try {
            scheduleResult = scheduleStmt.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return scheduleResult;
    }
    public static String getUserLocale(){
        String userCountry = System.getProperty("user.country");
        String userLang = System.getProperty("user.language");
        String userLocale = userCountry + userLang;
        return userLocale;
    }
    public static ObservableList<Schedule> generateScheduleList(ResultSet resultSet) throws SQLException {
        ObservableList<Schedule> scheduleList = FXCollections.observableArrayList();
        while(resultSet.next()){
            Schedule scheduleRow = new Schedule(
                    resultSet.getInt("Appointment_ID"),
                    resultSet.getString("Appointment_Title"),
                    resultSet.getString("Appointment_Description"),
                    resultSet.getString("Appointment_Location"),
                    resultSet.getString("Contact"),
                    resultSet.getString("Appointment_Type"),
                    resultSet.getString("Appointment_Start"),
                    resultSet.getString("Appointment_End"),
                    resultSet.getInt("Customer_ID"),
                    resultSet.getInt("User_ID")
            );
            scheduleList.add(scheduleRow);
        }
        return scheduleList;
    }
    public static TableView<Schedule> generateScheduleTable(){
        TableView <Schedule> apptTable = new TableView<>();

        TableColumn apptID = new TableColumn("Appointment ID");
        apptID.setCellValueFactory(new PropertyValueFactory<Schedule, String>("apptID"));

        TableColumn apptTitle = new TableColumn("Appointment Title");
        apptTitle.setCellValueFactory(new PropertyValueFactory<Schedule, String>("apptTitle"));

        TableColumn apptDescription = new TableColumn("Appointment Description");
        apptDescription.setCellValueFactory(new PropertyValueFactory<Schedule, String>("apptDescription"));

        TableColumn apptLocation = new TableColumn("Appointment Location");
        apptLocation.setCellValueFactory(new PropertyValueFactory<Schedule, String>("apptLocation"));

        TableColumn apptContact = new TableColumn("Appointment Contact");
        apptContact.setCellValueFactory(new PropertyValueFactory<Schedule, String>("apptContact"));

        TableColumn apptType = new TableColumn("Appointment Type");
        apptType.setCellValueFactory(new PropertyValueFactory<Schedule, String>("apptType"));

        TableColumn apptStart = new TableColumn("Appointment Start");
        apptStart.setCellValueFactory(new PropertyValueFactory<Schedule, String>("apptStart"));

        TableColumn apptEnd = new TableColumn("Appointment End");
        apptEnd.setCellValueFactory(new PropertyValueFactory<Schedule, String>("apptEnd"));

        /* Dog, we might need to change these!

         */
        TableColumn customerID = new TableColumn("Customer ID");
        customerID.setCellValueFactory(new PropertyValueFactory<Schedule, String>("customerID"));

        TableColumn userID = new TableColumn("User ID");
        customerID.setCellValueFactory(new PropertyValueFactory<Schedule, String>("userID"));

        apptTable.getColumns().addAll(
                apptID,
                apptTitle,
                apptDescription,
                apptLocation,
                apptContact,
                apptType,
                apptStart,
                apptEnd,
                customerID,
                userID);

        return apptTable;
    }
   public static void updateScheduleTable(TableView tableView, ObservableList obsList){
        tableView.setItems(obsList);
    }
    public static ResultSet getCustomers(){
        String customerQuery =
        """
        SELECT
            customers.Customer_ID,
            customers.Customer_Name,
            customers.Phone,
            customers.Address,
            first_level_divisions.Division,
            countries.Country,
            customers.Postal_Code
        FROM
            customers
            INNER JOIN first_level_divisions on customers.Division_ID = first_level_divisions.Division_ID
                INNER JOIN countries on first_level_divisions.Country_ID = countries.Country_ID;
        """;

        try {
            JDBC.makePreparedStatement(customerQuery, JDBC.getConnection());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        PreparedStatement customerStmt = null;
        try {
            customerStmt = JDBC.getPreparedStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        ResultSet customerResult = null;
        try {
            assert customerStmt != null;
            customerResult = customerStmt.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return customerResult;
    }

    public static void fillCustomers(ResultSet resultSet, TableView tableView) throws SQLException {
        while(resultSet.next()){
            TableRow row = new TableRow();
            tableView.getItems().add(row);

        }
    }
}
