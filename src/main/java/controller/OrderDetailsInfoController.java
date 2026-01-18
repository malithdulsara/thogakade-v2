package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.dto.OrderDetailsInfoDto;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class OrderDetailsInfoController implements Initializable {

    ObservableList<OrderDetailsInfoDto> orderDetailsInfoDtos = FXCollections.observableArrayList();

    @FXML private TableColumn<?, ?> colDiscount;
    @FXML private TableColumn<?, ?> colItemCode;
    @FXML private TableColumn<?, ?> colOrderId;
    @FXML private TableColumn<?, ?> colQuantity;
    @FXML private TableView<OrderDetailsInfoDto> tblOrderInfo;
    @FXML private TextField txtDiscount;
    @FXML private TextField txtItemCode;
    @FXML private TextField txtOrderId;
    @FXML private TextField txtQuantity;
    @FXML private TextField txtSearch;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));

        loadTable();

        tblOrderInfo.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                txtOrderId.setText(newValue.getOrderId());
                txtItemCode.setText(newValue.getItemCode());
                // Must convert int to String
                txtQuantity.setText(String.valueOf(newValue.getQuantity()));
                txtDiscount.setText(String.valueOf(newValue.getDiscount()));
            }
        });

        FilteredList<OrderDetailsInfoDto> filteredData = new FilteredList<>(orderDetailsInfoDtos, b -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(detail -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                if (detail == null) return false;

                String lowerCaseFilter = newValue.toLowerCase();

                if (detail.getOrderId().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                // Search by Item Code
                else if (detail.getItemCode().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<OrderDetailsInfoDto> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblOrderInfo.comparatorProperty());
        tblOrderInfo.setItems(sortedData);
    }

    private void loadTable(){
        orderDetailsInfoDtos.clear();
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade", "root", "1234");
            String sql = "SELECT * FROM orderdetail";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                OrderDetailsInfoDto dto = new OrderDetailsInfoDto(
                        resultSet.getString("OrderID"),
                        resultSet.getString("ItemCode"),
                        resultSet.getInt("OrderQTY"),
                        resultSet.getInt("Discount")
                );
                orderDetailsInfoDtos.add(dto);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade","root","1234");
            String sql = "INSERT INTO orderdetail (OrderID,ItemCode,OrderQTY,Discount) VALUES (?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1,txtOrderId.getText());
            statement.setString(2,txtItemCode.getText());
            statement.setInt(3, Integer.parseInt(txtQuantity.getText()));
            statement.setInt(4, Integer.parseInt(txtDiscount.getText()));

            int rowAffected = statement.executeUpdate();

            if (rowAffected > 0) {
                System.out.println("Successfully Added !");
                loadTable();
                clearFields();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade","root","1234");
            // To update a specific item in an order, we need to match BOTH OrderID and ItemCode
            String sql = "UPDATE orderdetail SET OrderQTY = ?, Discount = ? WHERE OrderID = ? AND ItemCode = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, Integer.parseInt(txtQuantity.getText()));
            statement.setInt(2, Integer.parseInt(txtDiscount.getText()));
            statement.setString(3, txtOrderId.getText());
            statement.setString(4, txtItemCode.getText());

            int rowAffected = statement.executeUpdate();

            if (rowAffected > 0) {
                System.out.println("Successfully Updated !");
                loadTable();
                clearFields();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade","root","1234");
            // Fixed SQL: You must delete based on OrderID AND ItemCode to be safe
            String sql = "DELETE FROM orderdetail WHERE OrderID = ? AND ItemCode = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1,txtOrderId.getText());
            statement.setString(2,txtItemCode.getText());

            int rowAffected = statement.executeUpdate();

            if (rowAffected > 0) {
                System.out.println("Successfully Deleted !");
                loadTable();
                clearFields();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnReloadOnAction(ActionEvent event) {
        loadTable();
        clearFields();
    }

    private void clearFields() {
        txtOrderId.setText("");
        txtItemCode.setText("");
        txtQuantity.setText("");
        txtDiscount.setText("");
    }
}