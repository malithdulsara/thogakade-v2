package controller;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.dto.ItemInfoDto;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class itemInfoController implements Initializable {
    ObservableList<ItemInfoDto> itemInfoDtos = FXCollections.observableArrayList();

    @FXML
    private TableColumn<?, ?> colDescription;

    @FXML
    private TableColumn<?, ?> colItemCode;

    @FXML
    private TableColumn<?, ?> colPackSize;

    @FXML
    private TableColumn<?, ?> colQuantity;

    @FXML
    private TableColumn<?, ?> colUnitPrize;

    @FXML
    private TableView<ItemInfoDto> tblItemInfo;

    @FXML
    private TextField txtDescription;

    @FXML
    private TextField txtItemCode;

    @FXML
    private TextField txtPackSize;

    @FXML
    private TextField txtQuantity;

    @FXML
    private TextField txtSearch;

    @FXML
    private TextField txtUnitPrize;

    @FXML
    void btnAddOnAction(ActionEvent event) {

        try {
            Connection connection = DBConnection.getInstance().getConnection();

            String sql = "INSERT INTO item (ItemCode,Description,PackSize,UnitPrice,QtyOnHand)" + "VALUES (?,?,?,?,?)";

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, txtItemCode.getText());
            statement.setString(2, txtDescription.getText());
            statement.setString(3, txtPackSize.getText());
            statement.setString(4, txtUnitPrize.getText());
            statement.setString(5, txtQuantity.getText());

            int rowAffected = statement.executeUpdate();

            if (rowAffected > 0) {
                System.out.println("Successfully Added !");
                loadTable();
                clearFields();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "DB Error: " + e.getMessage()).show();
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();

            String sql = "DELETE FROM Item WHERE ItemCode = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, txtItemCode.getText());

            int rowAffected = statement.executeUpdate();

            if (rowAffected > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Successfully Deleted!").show();
                itemInfoDtos.clear();
                loadTable();
                clearFields();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnReloadOnAction(ActionEvent event) {
        loadTable();
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "UPDATE item SET Description = ? , PackSize =?,UnitPrice = ? ,QtyOnHand = ? WHERE ItemCode = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, txtDescription.getText());
            statement.setString(2, txtPackSize.getText());
            statement.setString(3, txtUnitPrize.getText());
            statement.setString(4, txtQuantity.getText());
            statement.setString(5, txtItemCode.getText());


            int rowAffected = statement.executeUpdate();
            if (rowAffected > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Successfully Updated!").show();
                loadTable();
                clearFields();
            } else {
                new Alert(Alert.AlertType.WARNING, "Update Failed: Code not found").show();
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "DB Error: " + e.getMessage()).show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTable();

        tblItemInfo.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            txtItemCode.setText(newValue.getItemCode());
            txtDescription.setText(newValue.getDescription());
            txtPackSize.setText(newValue.getPackSize());
            txtQuantity.setText(String.valueOf(newValue.getQuantity()));
            txtUnitPrize.setText(String.valueOf(newValue.getUnitPrize()));
        });

        FilteredList<ItemInfoDto> filteredData = new FilteredList<>(itemInfoDtos, b -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if (item.getItemCode().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                else if (item.getDescription().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }


                return false; // Does not match.
            });
        });

        SortedList<ItemInfoDto> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(tblItemInfo.comparatorProperty());

        tblItemInfo.setItems(sortedData);


    }

    private void loadTable() {
        itemInfoDtos.clear();
        try {
            Connection connection = DBConnection.getInstance().getConnection();

            String sql = "SELECT * FROM item";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ItemInfoDto itemInfoDto = new ItemInfoDto(
                        resultSet.getString("ItemCode"),
                        resultSet.getString("Description"),
                        resultSet.getString("PackSize"),
                        resultSet.getDouble("UnitPrice"),
                        resultSet.getInt("QtyOnHand")
                );
                itemInfoDtos.add(itemInfoDto);
            }
            tblItemInfo.setItems(itemInfoDtos);

            colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
            colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
            colPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
            colUnitPrize.setCellValueFactory(new PropertyValueFactory<>("unitPrize"));
            colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearFields() {
        txtItemCode.setText("");
        txtDescription.setText("");
        txtPackSize.setText("");
        txtUnitPrize.setText("");
        txtQuantity.setText("");
    }
}
