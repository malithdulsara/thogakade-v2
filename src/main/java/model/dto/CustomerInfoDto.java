package model.dto;


import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CustomerInfoDto {
    private String Id;
    private String Title;
    private String name;
    private String dob;
    private Double Salary;
    private String address;
    private String city;
    private String province;
    private String postalCode;

}
