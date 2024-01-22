package otus.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDto {

    private UUID orderUUID;
    private UUID productUUID;
    private int quantity;
}
