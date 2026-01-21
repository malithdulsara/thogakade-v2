package controller;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.dto.CustomerInfoDto;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import java.util.HashMap;
import java.util.Map;
import java.io.InputStream;

public class CustomerInfoController implements Initializable {
    ObservableList<CustomerInfoDto> customerInfoDtos = FXCollections.observableArrayList();

    @FXML
    private TableColumn<?, ?> colAddress;

    @FXML
    private TableColumn<?, ?> colBirthday;

    @FXML
    private TableColumn<?, ?> colCity;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colName;

    @FXML
    private TableColumn<?, ?> colPostalCode;

    @FXML
    private TableColumn<?, ?> colProvince;

    @FXML
    private TableColumn<?, ?> colSalary;

    @FXML
    private TableColumn<?, ?> colTitle;

    @FXML
    private TableView<CustomerInfoDto> tblCustomerInfo;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtCity;

    @FXML
    private DatePicker dpDob;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPostalCode;

    @FXML
    private TextField txtProvince;

    @FXML
    private TextField txtSalary;

    @FXML
    private ComboBox<String> cmbTitle;

    @FXML
    private TextField txtSearch;


    @FXML
    void btnAddOnAction(ActionEvent event) {
        if(validateCustomer()){
            try {
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade", "root", "1234");

                String sql = "INSERT INTO customer (CustID,CustTitle,CustName,DOB,salary,CustAddress,City,Province,PostalCode)" + "VALUES (?,?,?,?,?,?,?,?,?)";

                PreparedStatement statement = connection.prepareStatement(sql);

                statement.setString(1, txtId.getText());
                statement.setString(2, cmbTitle.getValue());
                statement.setString(3, txtName.getText());
                statement.setDate(4, java.sql.Date.valueOf(dpDob.getValue()));
                statement.setString(5, txtSalary.getText());
                statement.setString(6, txtAddress.getText());
                statement.setString(7, txtCity.getText());
                statement.setString(8, txtProvince.getText());
                statement.setString(9, txtPostalCode.getText());

                int rowAffected = statement.executeUpdate();

                if (rowAffected > 0) {
                    System.out.println("Successfully Added !");
                    loadCustomerData();
                    clearFields();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "DB Error: " + e.getMessage()).show();
            }
        }

    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade","root","1234");

            String sql = "DELETE FROM Customer WHERE CustID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1,txtId.getText());

            int rowAffected = statement.executeUpdate();

            if (rowAffected > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Successfully Deleted!").show();
                customerInfoDtos.clear();
                loadCustomerData();
                clearFields();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        if(validateCustomer()){
            try {
                Connection connection = DBConnection.getInstance().getConnection();
                String sql ="UPDATE Customer SET CustTitle = ? , CustName =?,DOB = ? ,salary = ?,CustAddress = ?,City =?,Province=?,PostalCode=? WHERE CustID = ?";
                PreparedStatement statement = connection.prepareStatement(sql);

                statement.setString(1,cmbTitle.getValue());
                statement.setString(2,txtName.getText());
                statement.setDate(3, java.sql.Date.valueOf(dpDob.getValue()));
                statement.setString(4,txtSalary.getText());
                statement.setString(5,txtAddress.getText());
                statement.setString(6,txtCity.getText());
                statement.setString(7,txtProvince.getText());
                statement.setString(8,txtPostalCode.getText());
                statement.setString(9,txtId.getText());

                int rowAffected =statement.executeUpdate();
                if (rowAffected > 0) {
                    new Alert(Alert.AlertType.INFORMATION, "Successfully Updated!").show();
                    loadCustomerData();
                    clearFields();
                } else {
                    new Alert(Alert.AlertType.WARNING, "Update Failed: ID not found").show();
                }

            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "DB Error: " + e.getMessage()).show();
            }
        }



    }
    @FXML
    void btnReloadOnAction(ActionEvent event) {
        loadCustomerData();
    }

    @FXML
    void btnPrintOnAction(ActionEvent event) {
        try {
            InputStream reportStream = getClass().getResourceAsStream("/reports/customer_report.jrxml");

            JasperDesign jasperDesign = JRXmlLoader.load(reportStream);
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

            Connection connection = DBConnection.getInstance().getConnection();

            Map<String, Object> parameters = new HashMap<>();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            JasperViewer.viewReport(jasperPrint, false);

            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Report Error: " + e.getMessage()).show();
        }
    }

    private void loadCustomerData() {
        customerInfoDtos.clear();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "SELECT * FROM customer";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                CustomerInfoDto customerInfoDto = new CustomerInfoDto(
                        resultSet.getString("CustId"),
                        resultSet.getString("CustTitle"),
                        resultSet.getString("CustName"),
                        resultSet.getString("DOB"),
                        resultSet.getDouble("salary"),
                        resultSet.getString("CustAddress"),
                        resultSet.getString("City"),
                        resultSet.getString("Province"),
                        resultSet.getString("PostalCode")

                );
                customerInfoDtos.add(customerInfoDto);
            }

            //tblCustomerInfo.setItems(customerInfoDtos);

            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            colName.setCellValueFactory(new PropertyValueFactory<>("name"));
            colBirthday.setCellValueFactory(new PropertyValueFactory<>("dob"));
            colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
            colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
            colCity.setCellValueFactory(new PropertyValueFactory<>("city"));
            colProvince.setCellValueFactory(new PropertyValueFactory<>("province"));
            colPostalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadCustomerData();

        // ----------------- LIVE VALIDATION START -----------------
        txtSalary.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                txtSalary.setText(oldValue);
            }
        });

        txtPostalCode.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtPostalCode.setText(oldValue);
                return;
            }

            if (newValue.length() > 5) {
                txtPostalCode.setText(oldValue); // Reject if longer than 5
            }
        });

        txtName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[A-Za-z ]*")) {
                txtName.setText(oldValue);
            }
        });

        // ----------------- LIVE VALIDATION END -----------------

        // ----------------- SEARCH LOGIC START -----------------

        FilteredList<CustomerInfoDto> filteredData = new FilteredList<>(customerInfoDtos, b -> true);


        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(customer -> {
                // If filter text is empty, display all customers.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and ID field in your object with filter.
                String lowerCaseFilter = newValue.toLowerCase();

                if (customer.getId().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches ID.
                } else if (customer.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches Name.
                }

                return false;
            });
        });
        SortedList<CustomerInfoDto> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(tblCustomerInfo.comparatorProperty());

        tblCustomerInfo.setItems(sortedData);

        // ----------------- SEARCH LOGIC END -----------------

        ObservableList <String> titleList = FXCollections.observableArrayList("Mr","Ms","Mrs","Miss");
        cmbTitle.setItems(titleList);
        tblCustomerInfo.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) ->{
            if(newValue != null){
                txtId.setText(newValue.getId());
                cmbTitle.setValue(newValue.getTitle());
                txtName.setText(newValue.getName());
                try {
                    dpDob.setValue(LocalDate.parse(newValue.getDob()));
                } catch (Exception e) {}
                txtSalary.setText(String.valueOf(newValue.getSalary()));
                txtAddress.setText(newValue.getAddress());
                txtCity.setText(newValue.getCity());
                txtProvince.setText(newValue.getProvince());
                txtPostalCode.setText(newValue.getPostalCode());
            }
        } );

    }

    private boolean validateCustomer() {
        if (!Pattern.matches("^C[0-9]{3}$", txtId.getText())) {
            new Alert(Alert.AlertType.ERROR, "Invalid ID! Format must be C001").show();
            txtId.requestFocus();
            return false;
        }

        if (cmbTitle.getValue() == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a Title").show();
            cmbTitle.requestFocus();
            return false;
        }

        if (!Pattern.matches("^[A-z ]{3,}$", txtName.getText())) {
            new Alert(Alert.AlertType.ERROR, "Invalid Name (At least 3 letters)").show();
            txtName.requestFocus();
            return false;
        }

        if (dpDob.getValue() == null) {
            new Alert(Alert.AlertType.ERROR, "Please select Date of Birth").show();
            dpDob.requestFocus();
            return false;
        }

        if (!Pattern.matches("^[0-9]+[.]?[0-9]*$", txtSalary.getText())) {
            new Alert(Alert.AlertType.ERROR, "Invalid Salary (Numbers only)").show();
            txtSalary.requestFocus();
            return false;
        }

        if (txtAddress.getText().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Address cannot be empty").show();
            txtAddress.requestFocus();
            return false;
        }

        if (txtCity.getText().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "City cannot be empty").show();
            txtCity.requestFocus();
            return false;
        }

        if (!Pattern.matches("^[0-9]{4,5}$", txtPostalCode.getText())) {
            new Alert(Alert.AlertType.ERROR, "Invalid Postal Code").show();
            txtPostalCode.requestFocus();
            return false;
        }

        return true;
    }

    private void clearFields() {
        txtId.setText("");
        cmbTitle.setValue(null);
        txtName.setText("");
        dpDob.setValue(null);
        txtSalary.setText("");
        txtAddress.setText("");
        txtCity.setText("");
        txtProvince.setText("");
        txtPostalCode.setText("");
    }
}
