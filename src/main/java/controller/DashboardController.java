package controller;

import db.DBConnection;
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
        openWindow("/view/customer_info.fxml", "Customer Information");
    }

    @FXML
    void btnOrderOnAction(ActionEvent event) {
        openWindow("/view/order_info.fxml", "Order Information");
    }


    @FXML
    void btnItemOnAction(ActionEvent event) {
        openWindow("/view/item_info.fxml", "Item Information");

    }

    @FXML
    void btnOrderDetailsOnAction(ActionEvent event) {
        openWindow("/view/orderDetails_info.fxml", "Order Details");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadCount();
    }


    private void loadCount() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
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


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void openWindow(String fxmlPath, String title) {
        try {
            Stage stage = new Stage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource(fxmlPath))));
            stage.setTitle(title);
            stage.showAndWait();


            loadCount();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

}
