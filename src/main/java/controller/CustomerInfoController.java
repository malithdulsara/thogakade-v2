package controller;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.dto.CustomerInfoDto;

import java.sql.*;
import java.time.LocalDate;

public class CustomerInfoController implements CustomerInfoService{
    @Override
    public ObservableList<CustomerInfoDto> getAllCustomerInfo() {
        ObservableList<CustomerInfoDto>customerInfoDtos= FXCollections.observableArrayList();

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

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return customerInfoDtos;
    }

    @Override
    public int addCustomerInfo(String id, String title, String name, LocalDate dob, double salary, String address, String city, String province, String postalCode) {
        int rowAffected =0;
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade", "root", "1234");

            String sql = "INSERT INTO customer (CustID,CustTitle,CustName,DOB,salary,CustAddress,City,Province,PostalCode)" + "VALUES (?,?,?,?,?,?,?,?,?)";

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, id);
            statement.setString(2, title);
            statement.setString(3, name);
            statement.setDate(4, Date.valueOf(dob));
            statement.setString(5, String.valueOf(salary));
            statement.setString(6,address);
            statement.setString(7, city);
            statement.setString(8, province);
            statement.setString(9, postalCode);

            rowAffected = statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return rowAffected;
    }

    @Override
    public int updateCustomerInfo(String id, String title, String name, LocalDate dob, double salary, String address, String city, String province, String postalCode) {
        int rowAffected =0;
        try {
            Connection connection = DBConnection.getInstance().getConnection();

            String sql ="UPDATE Customer SET CustTitle = ? , CustName =?,DOB = ? ,salary = ?,CustAddress = ?,City =?,Province=?,PostalCode=? WHERE CustID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1,title);
            statement.setString(2,name);
            statement.setDate(3, Date.valueOf(dob));
            statement.setString(4, String.valueOf(salary));
            statement.setString(5,address);
            statement.setString(6,city);
            statement.setString(7,province);
            statement.setString(8,postalCode);
            statement.setString(9,id);

            rowAffected =statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
       return rowAffected;
    }

    @Override
    public int deleteCustomerInfo(String id) {
        int rowAffected =0;
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade","root","1234");
            String sql = "DELETE FROM Customer WHERE CustID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,id);
            rowAffected = statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rowAffected;
    }
}
