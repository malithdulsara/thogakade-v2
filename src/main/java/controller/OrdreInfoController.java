package controller;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.dto.OrderInfoDto;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class OrdreInfoController implements Initializable {
    ObservableList<OrderInfoDto> orderInfoDtos = FXCollections.observableArrayList();

    @FXML
    private TableColumn<?, ?> colCustomerId;

    @FXML
    private TableColumn<?, ?> colOrderDate;

    @FXML
    private TableColumn<?, ?> colOrderId;

    @FXML
    private DatePicker dpOrderDate;

    @FXML
    private TableView<OrderInfoDto> tblOrderInfo;

    @FXML
    private TextField txtCustomerId;

    @FXML
    private TextField txtOrderId;

    @FXML
    private TextField txtSearch;

    @FXML
    void btnAddOnAction(ActionEvent event) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "INSERT INTO orders (OrderID,OrderDate,CustID)values(?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, txtOrderId.getText());
            statement.setDate(2, Date.valueOf(dpOrderDate.getValue()));
            statement.setString(3, txtCustomerId.getText());

            int rowAffected = statement.executeUpdate();

            if (rowAffected > 0) {
                System.out.println("Successfully Added !");
                loadOrderDetails();
                clearFields();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearFields() {
        txtOrderId.setText("");
        txtCustomerId.setText("");
        dpOrderDate.setValue(null);
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "DELETE FROM orders WHERE OrderID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, txtOrderId.getText());


            int rowAffected = statement.executeUpdate();

            if (rowAffected > 0) {
                System.out.println("Successfully Deleted !");
                loadOrderDetails();
                clearFields();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnReloadOnAction(ActionEvent event) {
        loadOrderDetails();
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "UPDATE orders SET OrderDate=?,CustID=? WHERE OrderID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(3, txtOrderId.getText());
            statement.setDate(1, Date.valueOf(dpOrderDate.getValue()));
            statement.setString(2, txtCustomerId.getText());

            int rowAffected = statement.executeUpdate();

            if (rowAffected > 0) {
                System.out.println("Successfully Updated !");
                loadOrderDetails();
                clearFields();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadOrderDetails();

        tblOrderInfo.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue != null) {
                txtOrderId.setText(newValue.getOrderId());

                if (newValue.getOrderDate() != null) {
                    dpOrderDate.setValue(newValue.getOrderDate().toLocalDate());
                }
                txtCustomerId.setText(newValue.getCustomerId());
            }
        });

        FilteredList<OrderInfoDto> filteredData = new FilteredList<>(orderInfoDtos, b -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(order -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if (order.getOrderId().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                else if (order.getCustomerId().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }


                return false;
            });
        });

        SortedList<OrderInfoDto> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(tblOrderInfo.comparatorProperty());

        tblOrderInfo.setItems(sortedData);


    }

    private void loadOrderDetails() {
        orderInfoDtos.clear();

        try {
            Connection connection = DBConnection.getInstance().getConnection();

            String sql = "SELECT * FROM orders";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                OrderInfoDto orderInfoDto = new OrderInfoDto(
                        resultSet.getString("OrderID"),
                        resultSet.getDate("OrderDate"),
                        resultSet.getString("CustID")
                );
                orderInfoDtos.add(orderInfoDto);
            }
            tblOrderInfo.setItems(orderInfoDtos);

            colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            colOrderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
            colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
