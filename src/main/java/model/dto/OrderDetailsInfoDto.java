package model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDetailsInfoDto {
    private String orderId;
    private String itemCode;
    private int quantity;
    private int discount;
}
