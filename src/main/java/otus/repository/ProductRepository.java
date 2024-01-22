package otus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import otus.model.entity.Product;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
