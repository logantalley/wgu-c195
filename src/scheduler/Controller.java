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
    public static ResultSet getUserSchedule (int UserID){
        String scheduleQuery = """
                SELECT
                    Appointment_ID,
                    Title,
                    Description,
                    Location,
                    contacts.Contact_Name as Contact,
                    Type,
                    Start,
                    End,
                    Customer_ID,
                    User_ID
                FROM
                    appointments
                    INNER JOIN contacts on appointments.Contact_ID = contacts.Contact_ID
                WHERE
                    User_ID = ?;
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
        assert scheduleResult != null;
        //System.out.println(scheduleStmt);
        return scheduleResult;
    }

    public static ResultSet getCustomerSchedule (int CustomerID){
        String scheduleQuery = """
                SELECT
                    Appointment_ID,
                    Title,
                    Description,
                    Location,
                    contacts.Contact_Name as Contact,
                    Type,
                    Start,
                    End,
                    Customer_ID,
                    User_ID
                FROM
                    appointments
                    INNER JOIN contacts on appointments.Contact_ID = contacts.Contact_ID
                WHERE
                    Customer_ID = ?;
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
            scheduleStmt.setInt(1, CustomerID);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        ResultSet scheduleResult = null;
        try {
            scheduleResult = scheduleStmt.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        assert scheduleResult != null;
        //System.out.println(scheduleStmt);
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
                    resultSet.getString("Title"),
                    resultSet.getString("Description"),
                    resultSet.getString("Location"),
                    resultSet.getString("Contact"),
                    resultSet.getString("Type"),
                    resultSet.getString("Start"),
                    resultSet.getString("End"),
                    resultSet.getInt("Customer_ID"),
                    resultSet.getInt("User_ID")
            );
            scheduleList.add(scheduleRow);
        }
        return scheduleList;
    }
    public static TableView<Schedule> generateScheduleTable(){
        TableView <Schedule> apptTable = new TableView<>();

        TableColumn apptID = new TableColumn("ID");
        apptID.setCellValueFactory(new PropertyValueFactory<Schedule, Integer>("apptID"));

        TableColumn apptTitle = new TableColumn("Title");
        apptTitle.setCellValueFactory(new PropertyValueFactory<Schedule, String>("apptTitle"));

        TableColumn apptDescription = new TableColumn("Description");
        apptDescription.setCellValueFactory(new PropertyValueFactory<Schedule, String>("apptDescription"));

        TableColumn apptLocation = new TableColumn("Location");
        apptLocation.setCellValueFactory(new PropertyValueFactory<Schedule, String>("apptLocation"));

        TableColumn apptContact = new TableColumn("Contact");
        apptContact.setCellValueFactory(new PropertyValueFactory<Schedule, String>("apptContact"));

        TableColumn apptType = new TableColumn("Type");
        apptType.setCellValueFactory(new PropertyValueFactory<Schedule, String>("apptType"));

        TableColumn apptStart = new TableColumn("Start");
        apptStart.setCellValueFactory(new PropertyValueFactory<Schedule, String>("apptStart"));

        TableColumn apptEnd = new TableColumn("End");
        apptEnd.setCellValueFactory(new PropertyValueFactory<Schedule, String>("apptEnd"));

        TableColumn customerID = new TableColumn("Customer ID");
        customerID.setCellValueFactory(new PropertyValueFactory<Schedule, Integer>("customerID"));

        TableColumn userID = new TableColumn("User ID");
        userID.setCellValueFactory(new PropertyValueFactory<Schedule, Integer>("userID"));


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
                userID
                );

        return apptTable;
    }
   public static void updateTable(TableView tableView, ObservableList obsList){
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


    public static ObservableList<Customer> generateCustomerList(ResultSet resultSet) throws SQLException {
        ObservableList<Customer> customerList = FXCollections.observableArrayList();

        while(resultSet.next()){
            int CustomerID = resultSet.getInt("Customer_ID");
            ResultSet customerApptQuery = getCustomerSchedule(CustomerID);
            ObservableList<Schedule> customerApptList = generateScheduleList(customerApptQuery);
            Customer customerRow = new Customer(
                    resultSet.getInt("Customer_ID"),
                    resultSet.getString("Customer_Name"),
                    resultSet.getString("Phone"),
                    resultSet.getString("Address"),
                    resultSet.getString("Division"),
                    resultSet.getString("Country"),
                    resultSet.getString("Postal_Code"),
                    customerApptList
            );
            customerList.add(customerRow);
        }
        return customerList;
    }
    public static TableView<Customer> generateCustomerTable(){
        TableView <Customer> customerTable = new TableView<>();

        TableColumn customerID = new TableColumn("Customer ID");
        customerID.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("customerID"));

        TableColumn customerName = new TableColumn("Customer Name");
        customerName.setCellValueFactory(new PropertyValueFactory<Customer, String>("customerName"));

        TableColumn customerPhone = new TableColumn("Customer Phone");
        customerPhone.setCellValueFactory(new PropertyValueFactory<Customer, String>("customerPhone"));

        TableColumn customerAddress = new TableColumn("Customer Address");
        customerAddress.setCellValueFactory(new PropertyValueFactory<Customer, String>("customerAddress"));

        TableColumn customerDivision = new TableColumn("Customer Division");
        customerDivision.setCellValueFactory(new PropertyValueFactory<Customer, String>("customerDivision"));

        TableColumn customerCountry = new TableColumn("Customer Country");
        customerCountry.setCellValueFactory(new PropertyValueFactory<Customer, String>("customerCountry"));

        TableColumn customerPostal = new TableColumn("Customer Postal");
        customerPostal.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("customerPostal"));


        customerTable.getColumns().addAll(
                customerID,
                customerName,
                customerPhone,
                customerAddress,
                customerDivision,
                customerCountry,
                customerPostal
        );

        return customerTable;
    }
}
