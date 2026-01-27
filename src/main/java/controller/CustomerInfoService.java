package controller;

import javafx.collections.ObservableList;
import model.dto.CustomerInfoDto;

import java.time.LocalDate;

public interface CustomerInfoService {
ObservableList<CustomerInfoDto> getAllCustomerInfo();
public int addCustomerInfo(String id, String title, String name, LocalDate dob, double salary, String address, String city, String province, String postalCode);
public int updateCustomerInfo(String id, String title, String name, LocalDate dob, double salary, String address, String city, String province, String postalCode);
public int deleteCustomerInfo(String id);
}
