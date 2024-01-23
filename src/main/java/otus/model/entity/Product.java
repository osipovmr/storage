package otus.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "storage")
public class Product {

    @Id
    @Column(name = "product_uuid")
    private UUID productUUID;
    private int availableQuantity;
    private int reservedQuantity;
}
