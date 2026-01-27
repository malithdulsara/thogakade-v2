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

public class CustomerInfoFormController implements Initializable {
    CustomerInfoService customerInfoService = new CustomerInfoController();
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
            int rowAffected = customerInfoService.addCustomerInfo(
                    txtId.getText(),
                    cmbTitle.getValue(),
                    txtName.getText(),
                    LocalDate.parse(dpDob.getValue().toString()),
                    Double.parseDouble(txtSalary.getText()),
                    txtAddress.getText(),
                    txtCity.getText(),
                    txtProvince.getText(),
                    txtPostalCode.getText()
            );

            if (rowAffected > 0) {
                System.out.println("Successfully Added !");
                loadCustomerData();
                clearFields();
            }
        }

    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        int rowAffected = customerInfoService.deleteCustomerInfo(txtId.getText());

        if (rowAffected > 0) {
            new Alert(Alert.AlertType.INFORMATION, "Successfully Deleted!").show();
            customerInfoDtos.clear();
            loadCustomerData();
            clearFields();
        }

    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        if(validateCustomer()){
            String title = cmbTitle.getValue();
            String name = txtName.getText();
            LocalDate dob = dpDob.getValue();
            String salary = txtSalary.getText();
            String address = txtAddress.getText();
            String city = txtCity.getText();
            String province = txtProvince.getText();
            String postalCode = txtPostalCode.getText();
            String id = txtId.getText();
            int rowAffected = customerInfoService.updateCustomerInfo(id, title, name, LocalDate.parse(dob.toString()), Double.parseDouble(salary), address, city, province, postalCode);

            if (rowAffected > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Successfully Updated!").show();
                loadCustomerData();
                clearFields();
            } else {
                new Alert(Alert.AlertType.WARNING, "Update Failed: ID not found").show();
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
        ObservableList<CustomerInfoDto> newData = customerInfoService.getAllCustomerInfo();

        // 2. Clear the CURRENT list (which the table is watching)
        customerInfoDtos.clear();

        // 3. Add all new items to the CURRENT list
        // This notifies the FilteredList -> SortedList -> TableView to update!
        customerInfoDtos.addAll(newData);


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colBirthday.setCellValueFactory(new PropertyValueFactory<>("dob"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        colProvince.setCellValueFactory(new PropertyValueFactory<>("province"));
        colPostalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));

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
