package otus.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "storage_table")
public class Product {

    @Id
    private UUID productUUID;
    private int availableQuantity;
    private int reservedQuantity;
}
