package otus.model.dto;

import lombok.Data;

@Data
public class UploadProductDto {

    private String name;
    private Double price;
    private int quantity;
}
