package otus.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import otus.model.dto.ProcessOrderDto;
import otus.model.entity.Product;
import otus.repository.ProductRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class Executor {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;
    private final ProductRepository repository;

    @KafkaListener(topics = "newOrder")
    public void listenNew(String message) throws JsonProcessingException {
        ProcessOrderDto dto = mapper.readValue(message, ProcessOrderDto.class);
        Product product = repository.findById(dto.getProductUUID()).orElseThrow();
        if (dto.isNew()) {
            int available = product.getAvailableQuantity();
            int need = dto.getQuantity();
            if (available >= need) {
                product.setReservedQuantity(need);
                product.setAvailableQuantity(available - need);
                dto.setAllowReservation(true);
                log.info("Зарезервировано {} из {}.", need, available);
            } else {
                dto.setAllowReservation(false);
                dto.setServiceName("storage");
                log.info("Невозможно зарезервировать {} из {}.", need, available);
            }
            kafkaTemplate.send("processOrder", mapper.writeValueAsString(dto));
        } else {
            if (dto.isReservedByAllServices()) {
                product.setReservedQuantity(0);
                log.info("Списание товара со склада по заказу {}.", dto.getOrderUUID());
            } else {
                product.setAvailableQuantity(product.getAvailableQuantity() + product.getReservedQuantity());
                product.setReservedQuantity(0);
                log.info("Отмена резервирования.");
            }
        }
        repository.save(product);
    }
}

