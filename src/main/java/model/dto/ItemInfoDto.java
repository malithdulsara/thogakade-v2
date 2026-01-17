package model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class ItemInfoDto {
    private String itemCode;
    private String description;
    private String packSize;
    private double unitPrize;
    private int quantity;
}
