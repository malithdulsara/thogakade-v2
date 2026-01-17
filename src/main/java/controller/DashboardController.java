package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Label lblCustomerCount;

    @FXML
    private Label lblOrderCount;

    @FXML
    private Label lblItemCount;

    @FXML
    void btnCustomerOnAction(ActionEvent event) {
        Stage stage = new Stage();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/customer_info.fxml"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setTitle("Customer Information");
        stage.showAndWait();
        loadCount();


    }

    @FXML
    void btnOrderOnAction(ActionEvent event) {
        Stage stage = new Stage();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/order_info.fxml"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setTitle("Order Information");
        stage.showAndWait();
        loadCount();
    }


    @FXML
    void btnItemOnAction(ActionEvent event) {
        Stage stage = new Stage();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/item_info.fxml"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setTitle("Item Information");
        stage.showAndWait();
        loadCount();

    }

    @FXML
    void btnSupplierOnAction(ActionEvent event) {
        Stage stage = new Stage();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/supplier_info.fxml"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setTitle("Supplier Information");
        stage.showAndWait();
        loadCount();


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadCount();
    }

    private void loadCount(){
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade", "root", "1234");
            PreparedStatement stmCustomer = connection.prepareStatement("SELECT COUNT(*) FROM Customer");
            ResultSet rstCustomer = stmCustomer.executeQuery();
            if (rstCustomer.next()) {
                int count = rstCustomer.getInt(1);
                lblCustomerCount.setText(String.valueOf(count));
            }

            PreparedStatement stmItem = connection.prepareStatement("SELECT COUNT(*) FROM Item");
            ResultSet rstItem = stmItem.executeQuery();
            if (rstItem.next()) {
                int count = rstItem.getInt(1);
                lblItemCount.setText(String.valueOf(count));
            }

            PreparedStatement stmOrders = connection.prepareStatement("SELECT COUNT(DISTINCT OrderID) FROM orders");
            ResultSet rstOrders = stmOrders.executeQuery();
            if (rstOrders.next()) {
                lblOrderCount.setText(String.valueOf(rstOrders.getInt(1)));
            }

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
