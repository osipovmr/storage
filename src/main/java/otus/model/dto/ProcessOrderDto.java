package otus.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ProcessOrderDto {

    private UUID orderUUID;
    private UUID productUUID;
    private int quantity;
    private int cost;
    private UUID userUUID;
    private String userEmail;
    private boolean isNew;
    private boolean isReservedByAllServices;
    private boolean allowReservation;
    private String serviceName;
    private LocalDate deliveryDate;
    private String message;

    @Override
    public String toString() {
        return "Order{" +
                "orderUUID=" + orderUUID +
                ", productUUID=" + productUUID +
                ", quantity=" + quantity +
                ", cost=" + cost +
                ", userUUID=" + userUUID +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}
