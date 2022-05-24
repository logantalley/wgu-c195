package scheduler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

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
    public static ObservableList<Country> getCountries() throws SQLException {
        String countryQuery = """
                SELECT
                    Country,
                    Country_ID
                FROM
                    countries;
                """;
        JDBC.makePreparedStatement(countryQuery, JDBC.getConnection());
        PreparedStatement countryStmt = JDBC.getPreparedStatement();
        assert countryStmt != null;
        ResultSet countryRes = countryStmt.executeQuery();
        assert countryRes != null;
        ObservableList<Country> countryList = FXCollections.observableArrayList();
        while (countryRes.next()){
            Country countryRow = new Country(
                    countryRes.getString("Country"),
                    countryRes.getInt("Country_ID")
            );
            countryList.add(countryRow);
        }
        return countryList;
    }

    public static ObservableList<Division> getDivisions(Integer countryID) throws SQLException {
        String divisionQuery = """
                SELECT
                    Division,
                    Division_ID
                FROM
                    first_level_divisions
                WHERE
                    Country_ID = ?;
                """;
        JDBC.makePreparedStatement(divisionQuery, JDBC.getConnection());
        PreparedStatement divisionStmt = JDBC.getPreparedStatement();
        assert divisionStmt != null;
        divisionStmt.setInt(1, countryID);
        ResultSet divisionRes = divisionStmt.executeQuery();
        assert divisionRes != null;
        ObservableList<Division> divisionList = FXCollections.observableArrayList();
        while (divisionRes.next()){
            Division divisionRow = new Division(
                    divisionRes.getString("Division"),
                    divisionRes.getInt("Division_ID")
            );
            divisionList.add(divisionRow);
        }
        return divisionList;
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

    public static void addCustomer() throws SQLException {
        Stage addCustStage = new Stage();
        GridPane addCustGrid = new GridPane();
        Label sceneLabel = new Label("Add Customer");
        sceneLabel.setStyle("-fx-font-weight: bold;");


        Label custIDLabel = new Label("ID");
        TextField custIDField = new TextField("Autogenerated");
        custIDField.setDisable(true);

        Label custNameLabel = new Label("Name");
        TextField custNameField = new TextField();

        Label custAddressLabel = new Label("Address");
        TextField custAddressField = new TextField();

        Label custPostalLabel = new Label("Postal Code");
        TextField custPostalField = new TextField();

        Label custPhoneLabel = new Label("Phone Number");
        TextField custPhoneField = new TextField();
        ObservableList<Country> countryList = getCountries();

        ComboBox custCountryBox = new ComboBox(countryList);
        custCountryBox.setOnAction(p -> {
            System.out.println(custCountryBox.getValue());
        });

        ComboBox custDivision = new ComboBox(getDivisions(1));

        Button saveBtn = new Button("Save");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(x -> {
            addCustStage.close();
        });

        addCustGrid.add(sceneLabel, 0, 0, 1, 1);
        addCustGrid.add(custIDLabel, 0, 1, 1, 1);
        addCustGrid.add(custIDField, 1, 1, 1, 1);
        addCustGrid.add(custNameLabel, 0, 2, 1, 1);
        addCustGrid.add(custNameField, 1, 2, 1, 1);
        addCustGrid.add(custAddressLabel, 0, 3, 1, 1);
        addCustGrid.add(custAddressField, 1, 3, 1, 1);
        addCustGrid.add(custPhoneLabel, 0, 4, 1, 1);
        addCustGrid.add(custPhoneField, 1, 4, 1, 1);
        addCustGrid.add(custPostalLabel, 0, 5, 1, 1);
        addCustGrid.add(custPostalField, 1, 5, 1, 1);
        addCustGrid.add(custCountryBox, 2, 5, 1, 1);
        addCustGrid.add(custDivision, 3, 5, 1, 1);

        addCustGrid.add(saveBtn, 2, 7, 1, 1);
        addCustGrid.add(cancelBtn, 3, 7, 1, 1);


        Scene addCustScene = new Scene(addCustGrid, 850, 250);

        addCustStage.setTitle("Add Customer");
        addCustStage.setScene(addCustScene);
        addCustStage.show();
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
