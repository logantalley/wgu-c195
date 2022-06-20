package scheduler;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.TimeZone;

public class Controller {
    /**
     *  This function returns a UserID integer for use in queries later.
     * @param usernameField this is supplied via the userNameField TextField
     * @param passwordField this is supplied via the passwordField PasswordField
     * @param alert this is supplied so that we can return the appropriate error message
     * @return UserID integer
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

    /**
     * Gets a list of all Countries
     * @return an ObservableList with all Countries for populating ComboBoxes
     * @throws SQLException on SQL error
     */
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

    /**
     * Gives us a list of divisions for use in ComboBoxes
     * @param countryID the countryID for the list of Divisions
     * @return an ObservableList of divisions for a particular Country
     * @throws SQLException on SQL error
     */
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

    /**
     * Gives us the SQL results for a Customer's schedule
     * This logic was actually improved upon as I continued
     * the project, as you will see.
     * @param CustomerID the ID of the Customer for whom you would like to see a Schedule
     * @return ResultSet for compiling into an ObservableList
     */
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

    /**
     * Simple function to return userLang
     * @return the String of the System's user.language
     */
    public static String getUserLang(){
        return System.getProperty("user.language");
    }

    /**
     * Simple function to return userTimeZone
     * @return TimeZone of systemDefault
     */
    public static TimeZone getUserTimeZone(){
        return TimeZone.getTimeZone(ZoneId.systemDefault());

    }

    /**
     * Consumes a SQL ResultSet to generate an ObservableList
     * @param resultSet the SQL ResultSet of which you would like to generate an ObservableList
     * @return an ObservableList for a Schedule from a ResultSet
     * @throws SQLException on SQL error
     * @throws ParseException on attempting to parse the dates
     */
    public static ObservableList<Schedule> generateScheduleList(ResultSet resultSet) throws SQLException, ParseException {
        ObservableList<Schedule> scheduleList = FXCollections.observableArrayList();
        SimpleDateFormat initialFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
        defaultFormat.setTimeZone(getUserTimeZone());
        while(resultSet.next()){
            Schedule scheduleRow = new Schedule(
                    resultSet.getInt("Appointment_ID"),
                    resultSet.getString("Title"),
                    resultSet.getString("Description"),
                    resultSet.getString("Location"),
                    resultSet.getString("Contact"),
                    resultSet.getString("Type"),
                    defaultFormat.format(initialFormat.parse(String.valueOf(resultSet.getTimestamp("Start")))),
                    defaultFormat.format(initialFormat.parse(String.valueOf(resultSet.getTimestamp("End")))),
                    resultSet.getInt("Customer_ID"),
                    resultSet.getInt("User_ID")
            );
            scheduleList.add(scheduleRow);
        }
        return scheduleList;
    }

    /**
     * Simple function to generate a TableView for Appointments
     * @return Empty TableView with Rows for Appointments
     */
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

    /**
     * Simple function to set the Items in a TableView
     * @param tableView the TableView you would like to update
     * @param obsList the ObservableList you would like to push to the TableView
     */
    public static void updateTable(TableView tableView, ObservableList obsList){
        tableView.setItems(obsList);
    }

    /**
     * Simple function to obtain SQL ResultSet of all Customers for ComboBox
     * @return SQL ResultSet for consumption into ObservableList
     */
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

    /**
     * Function to add a new Customer
     * @param customerTable TableView CustomerTable which needs updating
     * @param countryList the ObservableList of all Countries
     * @param userID Integer userID who is creating a new Customer
     * @throws SQLException on SQL Errors
     */
    public static void addCustomer(TableView customerTable, ObservableList<Country> countryList, Integer userID) throws SQLException {
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
        Label custCountryLabel = new Label("Country");
        Label custDivisionLabel = new Label("Division");
        final Country[] selectedCountry = {null};
        ComboBox<Division> custDivision = new ComboBox<>();
        custDivision.setDisable(true);
        ComboBox<Country> custCountryBox = new ComboBox<>(countryList);
        custCountryBox.setOnAction(x ->{
            selectedCountry[0] = custCountryBox.getValue();
            custDivision.setDisable(false);
            try {
                custDivision.setItems(getDivisions(selectedCountry[0].getCountryID()));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        });
        final Division[] selectedDivision = {null};
        custDivision.setOnAction(x -> {
            selectedDivision[0] = custDivision.getValue();

        });



        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(x -> {
            Timestamp createDate = Timestamp.from(Instant.now());


            assert custNameField.getText() != null;
            assert custAddressField.getText() != null;
            assert custPostalField.getText() != null;
            assert custPhoneField.getText() != null;
            assert selectedDivision[0].getDivisionID() != 0;
            String customerAddQuery = """
                    INSERT INTO customers(Customer_Name, Phone, Address, Division_ID, Postal_Code, Create_Date, Created_By, Last_Update, Last_Updated_By)
                    VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;
            try {
                JDBC.makePreparedStatement(customerAddQuery, JDBC.getConnection());
                PreparedStatement customerAddStmt = JDBC.getPreparedStatement();
                assert customerAddStmt != null;
                customerAddStmt.setString(1, custNameField.getText());
                customerAddStmt.setString(2, custPhoneField.getText());
                customerAddStmt.setString(3, custAddressField.getText());
                customerAddStmt.setInt(4, selectedDivision[0].getDivisionID());
                customerAddStmt.setString(5, custPostalField.getText());
                customerAddStmt.setInt(7, userID);
                customerAddStmt.setTimestamp(6, createDate);
                customerAddStmt.setInt(9, userID);
                customerAddStmt.setTimestamp(8, createDate);
                customerAddStmt.executeUpdate();
                ResultSet customerResult = getCustomers();
                ObservableList<Customer> customerList = generateCustomerList(customerResult);
                updateTable(customerTable, customerList);
                addCustStage.close();

            } catch (SQLException | ParseException throwables) {
                throwables.printStackTrace();
            }
        });

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
        addCustGrid.add(custCountryLabel, 2, 4, 1, 1);
        addCustGrid.add(custCountryBox, 2, 5, 1, 1);
        addCustGrid.add(custDivisionLabel, 3, 4, 1, 1);
        addCustGrid.add(custDivision, 3, 5, 1, 1);

        addCustGrid.add(saveBtn, 2, 7, 1, 1);
        addCustGrid.add(cancelBtn, 3, 7, 1, 1);


        Scene addCustScene = new Scene(addCustGrid, 850, 250);

        addCustStage.setTitle("Add Customer");
        addCustStage.setScene(addCustScene);
        addCustStage.show();
    }

    /**
     * Simple function to return Country object based upon countryName
     * @param countryName String name of Country
     * @return Country object
     * @throws SQLException on SQL error
     */
    public static Country lookupCountry(String countryName) throws SQLException {
        String lookupQuery = """
                SELECT
                    Country_ID
                FROM
                    countries
                WHERE
                    Country = ?;
                """;
        JDBC.makePreparedStatement(lookupQuery, JDBC.getConnection());
        PreparedStatement countryStmt = JDBC.getPreparedStatement();
        assert countryStmt != null;
        countryStmt.setString(1, countryName);
        ResultSet countryRes = countryStmt.executeQuery();
        if (countryRes.next()){
            return new Country(countryName, countryRes.getInt(1));
        }
        return null;
    }

    /**
     * Simple function to get a Divison based on divisionName
     * @param divisionName String of division's name
     * @return Division object
     * @throws SQLException on SQL error
     */
    public static Division lookupDivision(String divisionName) throws SQLException {
        String lookupQuery = """
                SELECT
                    Division_ID
                FROM
                    first_level_divisions
                WHERE
                    Division = ?;
                """;
        JDBC.makePreparedStatement(lookupQuery, JDBC.getConnection());
        PreparedStatement divisionStmt = JDBC.getPreparedStatement();
        assert divisionStmt != null;
        divisionStmt.setString(1, divisionName);
        ResultSet divisionRes = divisionStmt.executeQuery();
        if (divisionRes.next()){
            return new Division(divisionName, divisionRes.getInt(1));
        }
        return null;
    }

    /**
     * Function to delete a Customer
     * @param customerTable TableView customerTable to be updated
     * @param selectedCustomer Customer selectedCustomer based upon what is currently selected
     * @throws SQLException on SQL error
     * @throws ParseException on Date parsing
     */
    public static void delCustomer(TableView customerTable, Customer selectedCustomer) throws SQLException, ParseException {
        if(selectedCustomer.getCustomerAppts().size() > 0){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot delete customer with appointments!", ButtonType.OK);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + selectedCustomer.getCustomerName() + "?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES){
                String custDelQuery = """
                    DELETE FROM
                        customers
                    WHERE
                        Customer_ID = ?
                    """;
                JDBC.makePreparedStatement(custDelQuery, JDBC.getConnection());
                PreparedStatement custDelStmt = JDBC.getPreparedStatement();
                custDelStmt.setInt(1, selectedCustomer.getCustomerID());
                custDelStmt.executeUpdate();
                ResultSet customerResult = getCustomers();
                ObservableList<Customer> customerList = generateCustomerList(customerResult);
                updateTable(customerTable, customerList);
                Alert delAlert = new Alert(Alert.AlertType.INFORMATION, "Customer deleted!", ButtonType.OK);
                delAlert.showAndWait();
            }


        }
    }

    /**
     * Function to modify existing Customer
     * @param customerTable TableView customerTable to be updated
     * @param countryList ObservableList of all Countries
     * @param userID Integer userID of user updating customer
     * @param selectedCustomer Customer selectedCustomer based upon Customer selected on TableView
     * @throws SQLException on SQL error
     */
    public static void modCustomer(TableView customerTable, ObservableList<Country> countryList, Integer userID, Customer selectedCustomer) throws SQLException {
        Stage modCustStage = new Stage();
        GridPane modCustGrid = new GridPane();
        Label sceneLabel = new Label("Modify Customer");
        sceneLabel.setStyle("-fx-font-weight: bold;");


        Label custIDLabel = new Label("ID");
        TextField custIDField = new TextField(String.valueOf(selectedCustomer.getCustomerID()));
        custIDField.setDisable(true);

        Label custNameLabel = new Label("Name");
        TextField custNameField = new TextField(selectedCustomer.getCustomerName());

        Label custAddressLabel = new Label("Address");
        TextField custAddressField = new TextField(selectedCustomer.getCustomerAddress());

        Label custPostalLabel = new Label("Postal Code");
        TextField custPostalField = new TextField(selectedCustomer.getCustomerPostal());

        Label custPhoneLabel = new Label("Phone Number");
        TextField custPhoneField = new TextField(selectedCustomer.getCustomerPhone());
        Label custCountryLabel = new Label("Country");
        Label custDivisionLabel = new Label("Division");
        final Country[] selectedCountry = {null};
        selectedCountry[0] = lookupCountry(selectedCustomer.getCustomerCountry());
        final Division[] selectedDivision = {null};
        selectedDivision[0] = lookupDivision(selectedCustomer.getCustomerDivision());
        ComboBox<Division> custDivision = new ComboBox<>();
        ComboBox<Country> custCountryBox = new ComboBox<>(countryList);
        custCountryBox.setValue(selectedCountry[0]);
        custDivision.setValue(selectedDivision[0]);
        custDivision.setItems(getDivisions(selectedCountry[0].getCountryID()));
        custCountryBox.setOnAction(x ->{
            selectedCountry[0] = custCountryBox.getValue();
            try {
                custDivision.setItems(getDivisions(selectedCountry[0].getCountryID()));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        });

        custDivision.setOnAction(x -> {
            selectedDivision[0] = custDivision.getValue();

        });



        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(x -> {
            Timestamp createDate = Timestamp.from(Instant.now());


            assert custNameField.getText() != null;
            assert custAddressField.getText() != null;
            assert custPostalField.getText() != null;
            assert custPhoneField.getText() != null;
            assert selectedDivision[0].getDivisionID() != 0;
            String customermodQuery = """
                    UPDATE
                        customers
                    SET
                        Customer_Name = ?,
                        Phone = ?,
                        Address = ?,
                        Division_ID = ?,
                        Postal_Code = ?,
                        Last_Update = ?,
                        Last_Updated_By = ?
                    WHERE
                        Customer_ID = ?;
                    """;
            try {
                JDBC.makePreparedStatement(customermodQuery, JDBC.getConnection());
                PreparedStatement customermodStmt = JDBC.getPreparedStatement();
                assert customermodStmt != null;
                customermodStmt.setString(1, custNameField.getText());
                customermodStmt.setString(2, custPhoneField.getText());
                customermodStmt.setString(3, custAddressField.getText());
                customermodStmt.setInt(4, selectedDivision[0].getDivisionID());
                customermodStmt.setString(5, custPostalField.getText());
                customermodStmt.setTimestamp(6, createDate);
                customermodStmt.setInt(7, userID);
                customermodStmt.setInt(8, selectedCustomer.getCustomerID());
                customermodStmt.executeUpdate();
                ResultSet customerResult = getCustomers();
                ObservableList<Customer> customerList = generateCustomerList(customerResult);
                updateTable(customerTable, customerList);
                modCustStage.close();

            } catch (SQLException | ParseException throwables) {
                throwables.printStackTrace();
            }
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(x -> {
            modCustStage.close();
        });

        modCustGrid.add(sceneLabel, 0, 0, 1, 1);
        modCustGrid.add(custIDLabel, 0, 1, 1, 1);
        modCustGrid.add(custIDField, 1, 1, 1, 1);
        modCustGrid.add(custNameLabel, 0, 2, 1, 1);
        modCustGrid.add(custNameField, 1, 2, 1, 1);
        modCustGrid.add(custAddressLabel, 0, 3, 1, 1);
        modCustGrid.add(custAddressField, 1, 3, 1, 1);
        modCustGrid.add(custPhoneLabel, 0, 4, 1, 1);
        modCustGrid.add(custPhoneField, 1, 4, 1, 1);
        modCustGrid.add(custPostalLabel, 0, 5, 1, 1);
        modCustGrid.add(custPostalField, 1, 5, 1, 1);
        modCustGrid.add(custCountryLabel, 2, 4, 1, 1);
        modCustGrid.add(custCountryBox, 2, 5, 1, 1);
        modCustGrid.add(custDivisionLabel, 3, 4, 1, 1);
        modCustGrid.add(custDivision, 3, 5, 1, 1);

        modCustGrid.add(saveBtn, 2, 7, 1, 1);
        modCustGrid.add(cancelBtn, 3, 7, 1, 1);


        Scene modCustScene = new Scene(modCustGrid, 850, 250);

        modCustStage.setTitle("Modify Customer");
        modCustStage.setScene(modCustScene);
        modCustStage.show();
    }

    /**
     * Function to retrieve a list of Customers
     * @param resultSet ResultSet resultSet to be consumed by function
     * @return ObservableList of Customers
     * @throws SQLException on SQL error
     * @throws ParseException on Date parsing
     */
    public static ObservableList<Customer> generateCustomerList(ResultSet resultSet) throws SQLException, ParseException {
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

    /**
     * Simple function to generate a TableView for displaying a list of Customers
     * @return Empty TableView for Customers
     */
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

    /**
     * Simple function to create list of Contacts for ComboBox
     * @return ObservableList contactList of all Contacts
     * @throws SQLException on SQL error
     */
    public static ObservableList<Contact> getContactList() throws SQLException {
        ObservableList<Contact> contactList = FXCollections.observableArrayList();
        String allContactQuery = """
            SELECT *
            FROM contacts;
            """;
        JDBC.makePreparedStatement(allContactQuery, JDBC.getConnection());
        PreparedStatement contactStmt = JDBC.getPreparedStatement();
        assert contactStmt != null;
        ResultSet contactRes = contactStmt.executeQuery();

        while(contactRes.next()){
            Contact contactRow =  new Contact(
                    contactRes.getInt("Contact_ID"),
                    contactRes.getString("Contact_Name"),
                    contactRes.getString("Email")
            );
            contactList.add(contactRow);
        }


        return contactList;
    }

    /**
     * Simple function to look up a Contact by name
     * @param contactName String name of Contact
     * @return Contact object based on their Contact_Name
     * @throws SQLException on SQL error
     */
    public static Contact lookupContact(String contactName) throws SQLException {

        String lookupContactQuery = """
            SELECT *
            FROM contacts
            WHERE Contact_Name = ?;
            """;
        JDBC.makePreparedStatement(lookupContactQuery, JDBC.getConnection());
        PreparedStatement contactStmt = JDBC.getPreparedStatement();
        assert contactStmt != null;
        contactStmt.setString(1, contactName);
        ResultSet contactRes = contactStmt.executeQuery();

        if(contactRes.next()){
             return new Contact(
                    contactRes.getInt("Contact_ID"),
                    contactRes.getString("Contact_Name"),
                    contactRes.getString("Email")
            );
        }


        return null;
    }

    /**
     * getScheduleByTime was my favorite function to write. In many ways,
     * it is the culmination of my Java learning.
     * @param userID Integer of user for whom a Schedule is needed
     * @param queryType String to specify what grouping of time is needed
     * @return ObservableList scheduleList based upon the queryType
     * @throws SQLException on SQL error
     * @throws ParseException on parsing Dates
     */
    public static ObservableList<Schedule> getScheduleByTime(int userID, String queryType) throws SQLException, ParseException {
        ObservableList<Schedule> scheduleList = FXCollections.observableArrayList();
        String scheduleQuery = null;
        if (queryType.equals("byWeek")){
            scheduleQuery = """
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
                    	YEARWEEK(Start) = YEARWEEK(curdate()) AND
                       	User_ID = ?;
                    """;
        }else if (queryType.equals("byMonth")) {
            scheduleQuery = """
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
                        CONCAT(YEAR(Start), '-', MONTH(Start)) = CONCAT(YEAR(curdate()), '-', MONTH(curdate())) AND
                    	User_ID = ?;
                    """;

        }else{
            scheduleQuery = """
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
        }
        JDBC.makePreparedStatement(scheduleQuery, JDBC.getConnection());
        PreparedStatement scheduleStmt = JDBC.getPreparedStatement();
        assert scheduleStmt != null;
        scheduleStmt.setInt(1, userID);
        ResultSet scheduleRes = scheduleStmt.executeQuery();
        SimpleDateFormat initialFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
        defaultFormat.setTimeZone(getUserTimeZone());

        while(scheduleRes.next()){
            Schedule scheduleRow = new Schedule(
                    scheduleRes.getInt("Appointment_ID"),
                    scheduleRes.getString("Title"),
                    scheduleRes.getString("Description"),
                    scheduleRes.getString("Location"),
                    scheduleRes.getString("Contact"),
                    scheduleRes.getString("Type"),
                    defaultFormat.format(initialFormat.parse(String.valueOf(scheduleRes.getTimestamp("Start")))),
                    defaultFormat.format(initialFormat.parse(String.valueOf(scheduleRes.getTimestamp("End")))),
                    scheduleRes.getInt("Customer_ID"),
                    scheduleRes.getInt("User_ID")
            );
            scheduleList.add(scheduleRow);
        }
        return scheduleList;

    }

    /**
     * Function to add an Appointment.
     * LAMBDA EXPRESSION in cancelBtn.setOnAction():
     * This lambda expression is very simple and allows me
     * to immediately close my Stage on the cancelBtn being
     * clicked, rather than having to create an EventHandler.
     * @param userID Integer ID of user who is adding Appt
     * @param scheduleTable TableView scheduleTable for updating
     * @throws SQLException on SQL error
     * @throws ParseException on parsing Dates
     */
    public static void addAppt(Integer userID, TableView<Schedule> scheduleTable) throws SQLException, ParseException {
        Stage apptStage = new Stage();
        GridPane apptGrid = new GridPane();
        
        Label sceneLabel = new Label("Add Appointment");

        Label idLabel = new Label("ID");
        TextField idField = new TextField("Autogenerated");
        idField.setDisable(true);
        
        Label titleLabel = new Label("Title");
        TextField titleField = new TextField();
        
        Label descLabel = new Label("Description");
        TextField descField = new TextField();
        
        Label locLabel = new Label("Location");
        TextField locField = new TextField();
        
        Label contactLabel = new Label("Contact");
        ComboBox<Contact> contactBox = new ComboBox<>(getContactList());
        
        Label typeLabel = new Label("Type");
        TextField typeField = new TextField();
        final Date[] selectedDate = {null};
        Label dateLabel = new Label("Date");
        final DatePicker dateField = new DatePicker();
        dateField.setOnAction(e -> {
                selectedDate[0] = Date.valueOf(dateField.getValue());
            });

        Label startLabel = new Label("Start Time (HH:MM AM/PM format)");
        TextField startField = new TextField();
        
        Label endLabel = new Label("End Time (HH:MM AM/PM format)");
        TextField endField = new TextField();
        DateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");

        Label customerLabel = new Label("Customer");
        ResultSet customerRes = getCustomers();

        ObservableList<Customer> customerList = generateCustomerList(customerRes);
        ComboBox<Customer> customerBox = new ComboBox<>(customerList);

        final Customer[] selectedCustomer = {null};
        final Contact[] selectedContact = {null};

        contactBox.setOnAction(e -> {
            selectedContact[0] = contactBox.getValue();
        });

        customerBox.setOnAction(e -> {
            selectedCustomer[0] = customerBox.getValue();
        });

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            Timestamp createDateTime = Timestamp.from(Instant.now());
            Timestamp startTimeStamp = null;
            Timestamp endTimeStamp = null;
            try {
                startTimeStamp = new Timestamp(defaultFormat.parse(selectedDate[0] + " " + startField.getText()).getTime());
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }
            try {
                endTimeStamp = new Timestamp(defaultFormat.parse(selectedDate[0] + " " + endField.getText()).getTime());
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }

            assert startTimeStamp != null;
            assert endTimeStamp != null;
            //System.out.println(startTimeStamp);
            java.util.Date estStart = null;
            java.util.Date estEnd = null;

            SimpleDateFormat estDF = new SimpleDateFormat("HH:mm:ss");
            estDF.setTimeZone(TimeZone.getTimeZone(ZoneId.of("America/New_York")));
            try {
                estStart = estDF.parse("07:00:00");
                estEnd = estDF.parse("21:00:00");

            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }

            try {
                java.util.Date startCompare = estDF.parse(estDF.format(startTimeStamp));
                java.util.Date endCompare = estDF.parse(estDF.format(endTimeStamp));

                if (startCompare.after(estStart) && startCompare.before(estEnd)) {
                    if (endCompare.after(estStart) && endCompare.before(estEnd)) {
                        ResultSet customerApptQuery = getCustomerSchedule(selectedCustomer[0].getCustomerID());
                        ObservableList<Schedule> customerApptList = generateScheduleList(customerApptQuery);
                        final Boolean[] breakLoop = {null};
                        breakLoop[0] = true;
                        for (Schedule iSchedule : customerApptList) {
                            Timestamp iStart = new Timestamp(defaultFormat.parse(iSchedule.getApptStart()).getTime());
                            Timestamp iStop = new Timestamp(defaultFormat.parse(iSchedule.getApptEnd()).getTime());
                            if (startTimeStamp.before(iStop) && iStart.before(endTimeStamp)){
                                Alert alert = new Alert(Alert.AlertType.ERROR, "Overlapping Appointments!", ButtonType.OK);
                                alert.showAndWait();
                                breakLoop[0] = false;

                                break;

                            } else {
                                breakLoop[0] = true;
                            }
                        }
                        if (breakLoop[0]){
                            String addQuery = """
                                        INSERT INTO appointments(Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID)
                                        VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                                        """;
                            try {
                                JDBC.makePreparedStatement(addQuery, JDBC.getConnection());
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            PreparedStatement addStmt = null;
                            try {
                                addStmt = JDBC.getPreparedStatement();
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            assert addStmt != null;
                            try {
                                addStmt.setString(1, titleField.getText());
                                addStmt.setString(2, descField.getText());
                                addStmt.setString(3, locField.getText());
                                addStmt.setString(4, typeField.getText());
                                addStmt.setTimestamp(5, startTimeStamp);
                                addStmt.setTimestamp(6, endTimeStamp);
                                addStmt.setTimestamp(7, createDateTime);
                                addStmt.setInt(8, userID);
                                addStmt.setTimestamp(9, createDateTime);
                                addStmt.setInt(10, userID);
                                addStmt.setInt(11, selectedCustomer[0].getCustomerID());
                                addStmt.setInt(12, userID);
                                addStmt.setInt(13, selectedContact[0].getContactID());
                                addStmt.executeUpdate();

                                ObservableList<Schedule> scheduleList = Controller.getScheduleByTime(userID, "all");
                                updateTable(scheduleTable, scheduleList);
                                apptStage.close();


                            } catch (SQLException | ParseException throwables) {
                                throwables.printStackTrace();
                            }
                        }




                    }else{
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Appointment needs to be within EST business hours!", ButtonType.OK);
                        alert.showAndWait();
                    }
                }else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Appointment needs to be within EST business hours!", ButtonType.OK);
                    alert.showAndWait();
                }

            } catch (ParseException | SQLException parseException) {
                parseException.printStackTrace();
            }





        });
        Button cancelBtn = new Button("Cancel");

        cancelBtn.setOnAction(e -> {
            apptStage.close();
        });


        apptGrid.add(sceneLabel, 0, 0, 1, 1);
        apptGrid.add(idLabel, 0, 1, 1, 1);
        apptGrid.add(idField, 1, 1, 1, 1);
        apptGrid.add(titleLabel, 0, 2, 1, 1);
        apptGrid.add(titleField, 1, 2, 1, 1);
        apptGrid.add(descLabel, 0, 3, 1, 1);
        apptGrid.add(descField, 1, 3, 1, 1);
        apptGrid.add(locLabel, 0, 4, 1, 1);
        apptGrid.add(locField, 1, 4, 1, 1);
        apptGrid.add(typeLabel, 0, 5, 1, 1);
        apptGrid.add(typeField, 1, 5, 1, 1);
        apptGrid.add(dateLabel, 0, 6, 1, 1);
        apptGrid.add(dateField, 1, 6, 1, 1);
        apptGrid.add(startLabel, 0, 7, 1, 1);
        apptGrid.add(startField, 1, 7, 1, 1);
        apptGrid.add(endLabel, 0, 8, 1, 1);
        apptGrid.add(endField, 1, 8, 1, 1);
        apptGrid.add(contactLabel, 2, 4, 1, 1);
        apptGrid.add(contactBox, 2, 5, 1, 1);
        apptGrid.add(customerLabel, 3, 4, 1, 1);
        apptGrid.add(customerBox, 3, 5, 1, 1);

        apptGrid.add(saveBtn, 2, 7, 1, 1);
        apptGrid.add(cancelBtn, 3, 7, 1, 1);


        Scene apptScene = new Scene(apptGrid, 850, 250);

        apptStage.setTitle("Add Appointment");
        apptStage.setScene(apptScene);
        apptStage.show();
    }

    /**
     * Function to delete an Appointment
     * @param userID Integer ID of user who is deleting Appointment
     * @param schedTable TableView schedTable for updating
     * @param selectedAppt Schedule selectedAppt from the selected item on the TableView
     * @throws SQLException on SQL error
     * @throws ParseException on parsing Dates
     */
    public static void delAppt(Integer userID, TableView<Schedule> schedTable, Schedule selectedAppt) throws SQLException, ParseException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this appointment?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES){
            String apptDelQuery = """
                DELETE FROM
                    appointments
                WHERE
                    Appointment_ID = ?
                """;
            JDBC.makePreparedStatement(apptDelQuery, JDBC.getConnection());
            PreparedStatement apptDelStmt = JDBC.getPreparedStatement();
            assert apptDelStmt != null;
            apptDelStmt.setInt(1, selectedAppt.getApptID());
            apptDelStmt.executeUpdate();
            ObservableList<Schedule> scheduleList = Controller.getScheduleByTime(userID, "all");
            updateTable(schedTable, scheduleList);
            Alert delAlert = new Alert(Alert.AlertType.INFORMATION, "Appointment deleted! \n" + "Appointment ID: " + selectedAppt.getApptID() + "\n" + "Appointment Title: " + selectedAppt.getApptTitle() + "\n" + "Appointment Type: " + selectedAppt.getApptType(), ButtonType.OK);
            delAlert.showAndWait();
        }


        }

    /**
     * Function to return Customer based upon ID
     * @param customerID Integer of Customer to look up
     * @return Customer object based upon their ID
     * @throws SQLException on SQL error
     * @throws ParseException on parsing Dates
     */
    public static Customer lookupCustomer(Integer customerID) throws SQLException, ParseException {
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
                        INNER JOIN countries on first_level_divisions.Country_ID = countries.Country_ID
                WHERE
                    Customer_ID = ?;
                """;
        JDBC.makePreparedStatement(customerQuery, JDBC.getConnection());
        PreparedStatement customerStmt = JDBC.getPreparedStatement();
        assert customerStmt != null;
        customerStmt.setInt(1, customerID);
        ResultSet customerResult = customerStmt.executeQuery();
        if (customerResult.next()){
            ResultSet customerApptQuery = getCustomerSchedule(customerID);
            ObservableList<Schedule> customerApptList = generateScheduleList(customerApptQuery);
            return new Customer(
                    customerResult.getInt("Customer_ID"),
                    customerResult.getString("Customer_Name"),
                    customerResult.getString("Phone"),
                    customerResult.getString("Address"),
                    customerResult.getString("Division"),
                    customerResult.getString("Country"),
                    customerResult.getString("Postal_Code"),
                    customerApptList
            );
        }
        return null;
    }

    /**
     * Function to modify an Appointment
     * LAMBDA EXPRESSION in dateField.setOnAction():
     * I heavily utilize lambdas in my code, but this is a clear
     * and succinct example of how useful they are. Rather than
     * creating an EventHandler, I can simply use a lambda to immediately
     * set my value on the setOnAction listener.
     * @param userID Integer ID of User who is modifying Appointment
     * @param scheduleTable TableView scheduleTable for updating
     * @param selectedAppt Schedule selectedAppt from selected item on TableView
     * @throws SQLException on SQL error
     * @throws ParseException on parsing Dates
     */
    public static void modAppt(Integer userID, TableView<Schedule> scheduleTable, Schedule selectedAppt) throws SQLException, ParseException {
        Stage apptStage = new Stage();
        GridPane apptGrid = new GridPane();

        Label sceneLabel = new Label("Modify Appointment");

        Label idLabel = new Label("ID");
        TextField idField = new TextField(String.valueOf(selectedAppt.getApptID()));
        idField.setDisable(true);

        Label titleLabel = new Label("Title");
        TextField titleField = new TextField(selectedAppt.getApptTitle());

        Label descLabel = new Label("Description");
        TextField descField = new TextField(selectedAppt.getApptDescription());

        Label locLabel = new Label("Location");
        TextField locField = new TextField(selectedAppt.getApptLocation());

        Label contactLabel = new Label("Contact");
        ComboBox<Contact> contactBox = new ComboBox<>(getContactList());

        contactBox.setValue(lookupContact(selectedAppt.getApptContact()));

        Label typeLabel = new Label("Type");
        TextField typeField = new TextField(selectedAppt.getApptType());
        final Date[] selectedDate = {null};
        Label dateLabel = new Label("Date");
        final DatePicker dateField = new DatePicker();

        dateField.setOnAction(e -> {
            selectedDate[0] = Date.valueOf(dateField.getValue());
        });

        Label startLabel = new Label("Start Time (HH:MM AM/PM format)");
        TextField startField = new TextField();

        Label endLabel = new Label("End Time (HH:MM AM/PM format)");
        TextField endField = new TextField();
        DateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(getUserTimeZone());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
        timeFormat.setTimeZone(getUserTimeZone());
        startField.setText(timeFormat.format(defaultFormat.parse(selectedAppt.getApptStart())));
        endField.setText(timeFormat.format(defaultFormat.parse(selectedAppt.getApptEnd())));
        dateField.setValue(LocalDate.parse(dateFormat.format(defaultFormat.parse(selectedAppt.getApptStart()))));
        selectedDate[0] = Date.valueOf(dateField.getValue());


        Label customerLabel = new Label("Customer");
        ResultSet customerRes = getCustomers();

        ObservableList<Customer> customerList = generateCustomerList(customerRes);
        ComboBox<Customer> customerBox = new ComboBox<>(customerList);
        customerBox.setValue(lookupCustomer(selectedAppt.getCustomerID()));

        final Customer[] selectedCustomer = {null};
        final Contact[] selectedContact = {null};

        selectedContact[0] = contactBox.getValue();
        selectedCustomer[0] = customerBox.getValue();
        contactBox.setOnAction(e -> {
            selectedContact[0] = contactBox.getValue();
        });

        customerBox.setOnAction(e -> {
            selectedCustomer[0] = customerBox.getValue();
        });

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            Timestamp createDateTime = Timestamp.from(Instant.now());
            Timestamp startTimeStamp = null;
            Timestamp endTimeStamp = null;
            try {
                startTimeStamp = new Timestamp(defaultFormat.parse(selectedDate[0] + " " + startField.getText()).getTime());
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }
            try {
                endTimeStamp = new Timestamp(defaultFormat.parse(selectedDate[0] + " " + endField.getText()).getTime());
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }

            java.util.Date estStart = null;
            java.util.Date estEnd = null;

            SimpleDateFormat estDF = new SimpleDateFormat("HH:mm:ss");
            estDF.setTimeZone(TimeZone.getTimeZone(ZoneId.of("America/New_York")));
            try {
                estStart = estDF.parse("07:00:00");
                estEnd = estDF.parse("21:00:00");

            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }


            try {
                java.util.Date startCompare = estDF.parse(estDF.format(startTimeStamp));
                java.util.Date endCompare = estDF.parse(estDF.format(endTimeStamp));

                if (startCompare.after(estStart) && startCompare.before(estEnd)) {
                    if (endCompare.after(estStart) && endCompare.before(estEnd)) {
                        assert startTimeStamp != null;
                        assert endTimeStamp != null;
                        ResultSet customerApptQuery = getCustomerSchedule(selectedAppt.getCustomerID());
                        ObservableList<Schedule> customerApptList = generateScheduleList(customerApptQuery);
                        final Boolean[] breakLoop = {null};
                        breakLoop[0] = true;
                        for (Schedule iSchedule : customerApptList) {
                            Timestamp iStart = new Timestamp(defaultFormat.parse(iSchedule.getApptStart()).getTime());
                            System.out.println(iStart);
                            Timestamp iStop = new Timestamp(defaultFormat.parse(iSchedule.getApptEnd()).getTime());
                            System.out.println(iStop);
                            if (startTimeStamp.before(iStop) && iStart.before(endTimeStamp)){
                                if (startTimeStamp.equals(iStart)){
                                    breakLoop[0] = selectedAppt.getApptID() == iSchedule.getApptID();
                                }
                                break;

                            } else {
                                breakLoop[0] = true;
                            }
                        }
                        if (breakLoop[0]) {
                            String modQuery = """
                    UPDATE appointments
                    SET
                        Title = ?,
                        Description = ?,
                        Location = ? ,
                        Type = ?,
                        Start = ?,
                        End = ?,
                        Last_Update = ?,
                        Last_Updated_By = ?,
                        Customer_ID = ?,
                        User_ID = ?,
                        Contact_ID = ?
                    WHERE Appointment_ID = ?
                    """;
                            try {
                                JDBC.makePreparedStatement(modQuery, JDBC.getConnection());
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            PreparedStatement modStmt = null;
                            try {
                                modStmt = JDBC.getPreparedStatement();
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            assert modStmt != null;
                            try {
                                modStmt.setString(1, titleField.getText());
                                modStmt.setString(2, descField.getText());
                                modStmt.setString(3, locField.getText());
                                modStmt.setString(4, typeField.getText());
                                modStmt.setTimestamp(5, startTimeStamp);
                                modStmt.setTimestamp(6, endTimeStamp);
                                modStmt.setTimestamp(7, createDateTime);
                                modStmt.setInt(8, userID);
                                modStmt.setInt(9, selectedCustomer[0].getCustomerID());
                                modStmt.setInt(10, userID);
                                modStmt.setInt(11, selectedContact[0].getContactID());
                                modStmt.setInt(12, selectedAppt.getApptID());
                                modStmt.executeUpdate();

                                ObservableList<Schedule> scheduleList = Controller.getScheduleByTime(userID, "all");
                                updateTable(scheduleTable, scheduleList);
                                apptStage.close();


                            } catch (SQLException | ParseException throwables) {
                                throwables.printStackTrace();
                            }

                        }else{
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Overlapping Appointments!", ButtonType.OK);
                            alert.showAndWait();
                        }




                    }else{
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Appointment needs to be within EST business hours!", ButtonType.OK);
                        alert.showAndWait();
                    }
                }else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Appointment needs to be within EST business hours!", ButtonType.OK);
                    alert.showAndWait();
                }

            } catch (ParseException | SQLException parseException) {
                parseException.printStackTrace();
            }





        });
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> {
            apptStage.close();
        });


        apptGrid.add(sceneLabel, 0, 0, 1, 1);
        apptGrid.add(idLabel, 0, 1, 1, 1);
        apptGrid.add(idField, 1, 1, 1, 1);
        apptGrid.add(titleLabel, 0, 2, 1, 1);
        apptGrid.add(titleField, 1, 2, 1, 1);
        apptGrid.add(descLabel, 0, 3, 1, 1);
        apptGrid.add(descField, 1, 3, 1, 1);
        apptGrid.add(locLabel, 0, 4, 1, 1);
        apptGrid.add(locField, 1, 4, 1, 1);
        apptGrid.add(typeLabel, 0, 5, 1, 1);
        apptGrid.add(typeField, 1, 5, 1, 1);
        apptGrid.add(dateLabel, 0, 6, 1, 1);
        apptGrid.add(dateField, 1, 6, 1, 1);
        apptGrid.add(startLabel, 0, 7, 1, 1);
        apptGrid.add(startField, 1, 7, 1, 1);
        apptGrid.add(endLabel, 0, 8, 1, 1);
        apptGrid.add(endField, 1, 8, 1, 1);
        apptGrid.add(contactLabel, 2, 4, 1, 1);
        apptGrid.add(contactBox, 2, 5, 1, 1);
        apptGrid.add(customerLabel, 3, 4, 1, 1);
        apptGrid.add(customerBox, 3, 5, 1, 1);

        apptGrid.add(saveBtn, 2, 7, 1, 1);
        apptGrid.add(cancelBtn, 3, 7, 1, 1);


        Scene apptScene = new Scene(apptGrid, 850, 250);

        apptStage.setTitle("Modify Appointment");
        apptStage.setScene(apptScene);
        apptStage.show();
    }

    /**
     * Function to modify only the time of an Appt
     * @param userID Integer ID of User modifying  Appt
     * @param scheduleTable TableView scheduleTable for updating
     * @param selectedAppt Schedule selectedAppt from object selected on TableView
     * @throws SQLException on SQL error
     * @throws ParseException on parsing Dates
     */
    public static void modApptTime(Integer userID, TableView<Schedule> scheduleTable, Schedule selectedAppt) throws SQLException, ParseException {
        Stage apptStage = new Stage();
        GridPane apptGrid = new GridPane();

        Label sceneLabel = new Label("Change Appointment Time");

        Label idLabel = new Label("ID");
        TextField idField = new TextField(String.valueOf(selectedAppt.getApptID()));
        idField.setDisable(true);

        Label titleLabel = new Label("Title");
        TextField titleField = new TextField(selectedAppt.getApptTitle());
        titleField.setDisable(true);

        Label descLabel = new Label("Description");
        TextField descField = new TextField(selectedAppt.getApptDescription());
        descField.setDisable(true);

        Label locLabel = new Label("Location");
        TextField locField = new TextField(selectedAppt.getApptLocation());
        locField.setDisable(true);

        Label contactLabel = new Label("Contact");
        ComboBox<Contact> contactBox = new ComboBox<>(getContactList());

        contactBox.setValue(lookupContact(selectedAppt.getApptContact()));
        contactBox.setDisable(true);

        Label typeLabel = new Label("Type");
        TextField typeField = new TextField(selectedAppt.getApptType());
        typeField.setDisable(true);
        final Date[] selectedDate = {null};
        Label dateLabel = new Label("Date");
        final DatePicker dateField = new DatePicker();
        dateField.setDisable(true);
        Label startLabel = new Label("Start Time (HH:MM AM/PM format)");
        TextField startField = new TextField();

        Label endLabel = new Label("End Time (HH:MM AM/PM format)");
        TextField endField = new TextField();
        DateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(getUserTimeZone());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
        timeFormat.setTimeZone(getUserTimeZone());
        startField.setText(timeFormat.format(defaultFormat.parse(selectedAppt.getApptStart())));
        endField.setText(timeFormat.format(defaultFormat.parse(selectedAppt.getApptEnd())));
        dateField.setValue(LocalDate.parse(dateFormat.format(defaultFormat.parse(selectedAppt.getApptStart()))));
        selectedDate[0] = Date.valueOf(dateField.getValue());


        Label customerLabel = new Label("Customer");
        ResultSet customerRes = getCustomers();

        ObservableList<Customer> customerList = generateCustomerList(customerRes);
        ComboBox<Customer> customerBox = new ComboBox<>(customerList);
        customerBox.setValue(lookupCustomer(selectedAppt.getCustomerID()));
        customerBox.setDisable(true);
        contactBox.setDisable(true);

        final Customer[] selectedCustomer = {null};
        final Contact[] selectedContact = {null};

        selectedContact[0] = contactBox.getValue();
        selectedCustomer[0] = customerBox.getValue();


        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            Timestamp createDateTime = Timestamp.from(Instant.now());
            Timestamp startTimeStamp = null;
            Timestamp endTimeStamp = null;
            try {
                startTimeStamp = new Timestamp(defaultFormat.parse(selectedDate[0] + " " + startField.getText()).getTime());
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }
            try {
                endTimeStamp = new Timestamp(defaultFormat.parse(selectedDate[0] + " " + endField.getText()).getTime());
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }

            java.util.Date estStart = null;
            java.util.Date estEnd = null;

            SimpleDateFormat estDF = new SimpleDateFormat("HH:mm:ss");
            estDF.setTimeZone(TimeZone.getTimeZone(ZoneId.of("America/New_York")));
            try {
                estStart = estDF.parse("07:00:00");
                estEnd = estDF.parse("21:00:00");

            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }


            try {
                java.util.Date startCompare = estDF.parse(estDF.format(startTimeStamp));
                java.util.Date endCompare = estDF.parse(estDF.format(endTimeStamp));

                if (startCompare.after(estStart) && startCompare.before(estEnd)) {
                    if (endCompare.after(estStart) && endCompare.before(estEnd)) {
                        System.out.println(startCompare);
                        System.out.println(endCompare);
                        assert startTimeStamp != null;
                        assert endTimeStamp != null;
                        ResultSet customerApptQuery = getCustomerSchedule(selectedAppt.getCustomerID());
                        System.out.println(selectedAppt.getCustomerID());
                        ObservableList<Schedule> customerApptList = generateScheduleList(customerApptQuery);
                        final Boolean[] breakLoop = {null};
                        breakLoop[0] = true;
                        for (Schedule iSchedule : customerApptList) {
                            Timestamp iStart = new Timestamp(defaultFormat.parse(iSchedule.getApptStart()).getTime());
                            Timestamp iStop = new Timestamp(defaultFormat.parse(iSchedule.getApptEnd()).getTime());
                            if (startTimeStamp.before(iStop) && iStart.before(endTimeStamp)){
                                if (startTimeStamp.equals(iStart)){
                                    if (selectedAppt.getApptID() == iSchedule.getApptID()){
                                        breakLoop[0] = true;
                                    }
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.ERROR, "Overlapping Appointments!", ButtonType.OK);
                                    alert.showAndWait();
                                    breakLoop[0] = false;
                                }
                                break;

                            } else {
                                breakLoop[0] = true;
                            }
                        }
                        if (breakLoop[0]) {
                            String modQuery = """
                    UPDATE appointments
                    SET
                        Title = ?,
                        Description = ?,
                        Location = ? ,
                        Type = ?,
                        Start = ?,
                        End = ?,
                        Last_Update = ?,
                        Last_Updated_By = ?,
                        Customer_ID = ?,
                        User_ID = ?,
                        Contact_ID = ?
                    WHERE Appointment_ID = ?
                    """;
                            try {
                                JDBC.makePreparedStatement(modQuery, JDBC.getConnection());
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            PreparedStatement modStmt = null;
                            try {
                                modStmt = JDBC.getPreparedStatement();
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            assert modStmt != null;
                            try {
                                modStmt.setString(1, titleField.getText());
                                modStmt.setString(2, descField.getText());
                                modStmt.setString(3, locField.getText());
                                modStmt.setString(4, typeField.getText());
                                modStmt.setTimestamp(5, startTimeStamp);
                                modStmt.setTimestamp(6, endTimeStamp);
                                modStmt.setTimestamp(7, createDateTime);
                                modStmt.setInt(8, userID);
                                modStmt.setInt(9, selectedCustomer[0].getCustomerID());
                                modStmt.setInt(10, userID);
                                modStmt.setInt(11, selectedContact[0].getContactID());
                                modStmt.setInt(12, selectedAppt.getApptID());
                                modStmt.executeUpdate();

                                ObservableList<Schedule> scheduleList = Controller.getScheduleByTime(userID, "all");
                                updateTable(scheduleTable, scheduleList);
                                apptStage.close();


                            } catch (SQLException | ParseException throwables) {
                                throwables.printStackTrace();
                            }

                        }




                    }else{
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Appointment needs to be within EST business hours!", ButtonType.OK);
                        alert.showAndWait();
                    }
                }else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Appointment needs to be within EST business hours!", ButtonType.OK);
                    alert.showAndWait();
                }

            } catch (ParseException | SQLException parseException) {
                parseException.printStackTrace();
            }





        });
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> {
            apptStage.close();
        });


        apptGrid.add(sceneLabel, 0, 0, 1, 1);
        apptGrid.add(idLabel, 0, 1, 1, 1);
        apptGrid.add(idField, 1, 1, 1, 1);
        apptGrid.add(titleLabel, 0, 2, 1, 1);
        apptGrid.add(titleField, 1, 2, 1, 1);
        apptGrid.add(descLabel, 0, 3, 1, 1);
        apptGrid.add(descField, 1, 3, 1, 1);
        apptGrid.add(locLabel, 0, 4, 1, 1);
        apptGrid.add(locField, 1, 4, 1, 1);
        apptGrid.add(typeLabel, 0, 5, 1, 1);
        apptGrid.add(typeField, 1, 5, 1, 1);
        apptGrid.add(dateLabel, 0, 6, 1, 1);
        apptGrid.add(dateField, 1, 6, 1, 1);
        apptGrid.add(startLabel, 0, 7, 1, 1);
        apptGrid.add(startField, 1, 7, 1, 1);
        apptGrid.add(endLabel, 0, 8, 1, 1);
        apptGrid.add(endField, 1, 8, 1, 1);
        apptGrid.add(contactLabel, 2, 4, 1, 1);
        apptGrid.add(contactBox, 2, 5, 1, 1);
        apptGrid.add(customerLabel, 3, 4, 1, 1);
        apptGrid.add(customerBox, 3, 5, 1, 1);

        apptGrid.add(saveBtn, 2, 7, 1, 1);
        apptGrid.add(cancelBtn, 3, 7, 1, 1);


        Scene apptScene = new Scene(apptGrid, 850, 250);
        apptStage.setTitle("Change Appointment Time");
        apptStage.setScene(apptScene);
        apptStage.show();
    }

    /**
     * Function to show the report for count of Appointments by Type
     * @throws SQLException on SQL error
     */
    public static void showApptReport() throws SQLException {
        TableView <apptReport> apptReportTable = new TableView<>();

        TableColumn timeFrame = new TableColumn("Timeframe");
        timeFrame.setCellValueFactory(new PropertyValueFactory<apptReport, String>("timeFrame"));

        TableColumn apptType = new TableColumn("Type");
        apptType.setCellValueFactory(new PropertyValueFactory<apptReport, String>("apptType"));

        TableColumn apptCount = new TableColumn("Count");
        apptCount.setCellValueFactory(new PropertyValueFactory<apptReport, Integer>("apptCount"));

        apptReportTable.getColumns().addAll(timeFrame, apptType, apptCount);

        String apptReportQuery = """
                SELECT DATE_FORMAT(Start, "%Y-%m") as Timeframe, Type, count(Appointment_ID) as Count
                FROM appointments
                GROUP BY Type, DATE_FORMAT(Start, "%Y%m");
                """;

        JDBC.makePreparedStatement(apptReportQuery, JDBC.getConnection());
        PreparedStatement apptReportStmt = JDBC.getPreparedStatement();
        ResultSet apptReportRes = apptReportStmt.executeQuery();
        ObservableList<apptReport> apptReportObservableList = FXCollections.observableArrayList();
        while (apptReportRes.next()){
            apptReport apptReportRow = new apptReport(
                    apptReportRes.getString("Timeframe"),
                    apptReportRes.getString("Type"),
                    apptReportRes.getInt("Count")
            );
            apptReportObservableList.add(apptReportRow);
        }
        apptReportTable.setItems(apptReportObservableList);

        AnchorPane apptReportAnchor = new AnchorPane(apptReportTable);
        AnchorPane.setTopAnchor(apptReportTable, 40d);
        AnchorPane.setBottomAnchor(apptReportTable, 45d);
        AnchorPane.setRightAnchor(apptReportTable, 10d);
        AnchorPane.setLeftAnchor(apptReportTable, 10d);
        Stage apptReportStage = new Stage();
        Scene apptReportScene = new Scene(apptReportAnchor, 850, 250);
        apptReportStage.setScene(apptReportScene);
        apptReportStage.setTitle("Appointments by Type");
        apptReportStage.show();

    }

    /**
     * Function to grab list of Contact Appointments
     * @param contactID Integer ID of Contact for whom to find Appointments
     * @return ObservableList contactApptList of Appointments corresponding to a Contact
     * @throws SQLException on SQL error
     * @throws ParseException on parsing Dates
     */
    public static ObservableList<Schedule> getContactAppts(int contactID) throws SQLException, ParseException {
        ObservableList<Schedule> contactApptList = FXCollections.observableArrayList();
        String contactApptQuery = """
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
                    	appointments.Contact_ID = ?;
                """;
        JDBC.makePreparedStatement(contactApptQuery, JDBC.getConnection());
        PreparedStatement contactApptStmt = JDBC.getPreparedStatement();
        assert contactApptStmt != null;
        contactApptStmt.setInt(1, contactID);
        SimpleDateFormat initialFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
        defaultFormat.setTimeZone(getUserTimeZone());
        ResultSet contactApptRes = contactApptStmt.executeQuery();
        while(contactApptRes.next()){
            Schedule scheduleRow = new Schedule(
                    contactApptRes.getInt("Appointment_ID"),
                    contactApptRes.getString("Title"),
                    contactApptRes.getString("Description"),
                    contactApptRes.getString("Location"),
                    contactApptRes.getString("Contact"),
                    contactApptRes.getString("Type"),
                    defaultFormat.format(initialFormat.parse(String.valueOf(contactApptRes.getTimestamp("Start")))),
                    defaultFormat.format(initialFormat.parse(String.valueOf(contactApptRes.getTimestamp("End")))),
                    contactApptRes.getInt("Customer_ID"),
                    contactApptRes.getInt("User_ID")
            );
            contactApptList.add(scheduleRow);
        }
        return contactApptList;


    }

    /**
     * Function to show report of list of Appointments by Contact
     * @throws SQLException on SQL error
     * @throws ParseException on parsing Dates
     */
    public static void showContactReport() throws SQLException, ParseException {
        TableView<Schedule> contactReportTable = generateScheduleTable();
        ObservableList<Contact> contactList = getContactList();
        Contact[] selectedContact = {null};
        ComboBox<Contact> contactBox = new ComboBox<>(contactList);
        contactBox.setValue(contactList.get(0));
        selectedContact[0] = contactBox.getValue();

        updateTable(contactReportTable, getContactAppts(selectedContact[0].getContactID()));
        contactBox.setOnAction(e -> {
            selectedContact[0] = contactBox.getValue();
            try {
                updateTable(contactReportTable, getContactAppts(selectedContact[0].getContactID()));
            } catch (SQLException | ParseException throwables) {
                throwables.printStackTrace();
            }
        });

        AnchorPane contactReportAnchor = new AnchorPane(contactReportTable, contactBox);
        AnchorPane.setTopAnchor(contactReportTable, 40d);
        AnchorPane.setBottomAnchor(contactReportTable, 45d);
        AnchorPane.setRightAnchor(contactReportTable, 10d);
        AnchorPane.setLeftAnchor(contactReportTable, 10d);
        AnchorPane.setTopAnchor(contactBox, 10d);
        AnchorPane.setRightAnchor(contactBox, 10d);
        Stage contactReportStage = new Stage();
        Scene contactReportScene = new Scene(contactReportAnchor, 850, 250);
        contactReportStage.setScene(contactReportScene);
        contactReportStage.setTitle("Appointments by Contact");
        contactReportStage.show();

    }

    /**
     * Function to show report of Customers by Country
     * @throws SQLException on SQL error
     */
    public static void showCustomerReport() throws SQLException {
        TableView <customerReport> customerReportTable = new TableView<>();

        TableColumn countryName = new TableColumn("Country");
        countryName.setCellValueFactory(new PropertyValueFactory<apptReport, String>("countryName"));

        TableColumn customerCount = new TableColumn("Count");
        customerCount.setCellValueFactory(new PropertyValueFactory<apptReport, Integer>("customerCount"));

        customerReportTable.getColumns().addAll(countryName, customerCount);

        String custReportQuery = """
                SELECT
                    countries.Country,
                    count(customers.Customer_ID) as Count
                FROM
                    customers
                        INNER JOIN first_level_divisions on customers.Division_ID = first_level_divisions.Division_ID
                            INNER JOIN countries on first_level_divisions.Country_ID = countries.Country_ID
                GROUP BY
                    countries.Country;
                """;

        JDBC.makePreparedStatement(custReportQuery, JDBC.getConnection());
        PreparedStatement custReportStmt = JDBC.getPreparedStatement();
        ResultSet custReportRes = custReportStmt.executeQuery();
        ObservableList<customerReport> custReportObservableList = FXCollections.observableArrayList();
        while (custReportRes.next()){
            customerReport custReportRow = new customerReport(
                    custReportRes.getString("Country"),
                    custReportRes.getInt("Count")
            );
            custReportObservableList.add(custReportRow);
        }
        customerReportTable.setItems(custReportObservableList);

        AnchorPane custReportAnchor = new AnchorPane(customerReportTable);
        AnchorPane.setTopAnchor(customerReportTable, 40d);
        AnchorPane.setBottomAnchor(customerReportTable, 45d);
        AnchorPane.setRightAnchor(customerReportTable, 10d);
        AnchorPane.setLeftAnchor(customerReportTable, 10d);
        Stage custReportStage = new Stage();
        Scene custReportScene = new Scene(custReportAnchor, 850, 250);
        custReportStage.setScene(custReportScene);
        custReportStage.setTitle("Count of Customers by Country");
        custReportStage.show();

    }
}
