package scheduler;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import javax.xml.transform.Result;
import java.sql.*;
import java.time.Instant;

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
    public static String getUserLang(){
        String userLang = System.getProperty("user.language");
        return userLang;
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

            } catch (SQLException throwables) {
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
    public static void delCustomer(TableView customerTable, Customer selectedCustomer) throws SQLException {
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

            } catch (SQLException throwables) {
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

    public static ObservableList<Schedule> getScheduleByTime(int userID, String queryType) throws SQLException {
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
        while(scheduleRes.next()){
            Schedule scheduleRow = new Schedule(
                    scheduleRes.getInt("Appointment_ID"),
                    scheduleRes.getString("Title"),
                    scheduleRes.getString("Description"),
                    scheduleRes.getString("Location"),
                    scheduleRes.getString("Contact"),
                    scheduleRes.getString("Type"),
                    scheduleRes.getString("Start"),
                    scheduleRes.getString("End"),
                    scheduleRes.getInt("Customer_ID"),
                    scheduleRes.getInt("User_ID")
            );
            scheduleList.add(scheduleRow);
        }
        return scheduleList;

    }

    public static void addAppt(Integer userID) throws SQLException {
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
        
        Label startLabel = new Label("Start Date/Time");
        TextField startField = new TextField();
        
        Label endLabel = new Label("End Date/Time");
        TextField endField = new TextField();
        
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
                /* TODO: Fix the start and end fields to be Timestamp objects */
                addStmt.setString(5, startField.getText());
                addStmt.setString(6, endField.getText());
                addStmt.setTimestamp(7, createDateTime);
                addStmt.setInt(8, userID);
                addStmt.setTimestamp(9, createDateTime);
                addStmt.setInt(10, userID);
                addStmt.setInt(11, selectedCustomer[0].getCustomerID());
                addStmt.setInt(12, userID);
                addStmt.setInt(13, selectedContact[0].getContactID());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
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
        apptGrid.add(startLabel, 0, 6, 1, 1);
        apptGrid.add(startField, 1, 6, 1, 1);
        apptGrid.add(endLabel, 0, 7, 1, 1);
        apptGrid.add(endField, 1, 7, 1, 1);
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
}
