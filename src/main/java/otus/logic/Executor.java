package otus.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import otus.config.yaml.KafkaProducerYamlProperties;
import otus.model.dto.OrderDto;
import otus.model.entity.Product;
import otus.repository.ProductRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class Executor {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;
    private final ProductRepository repository;
    private final KafkaProducerYamlProperties producerYamlProperties;

    @KafkaListener(topics = "newOrder")
    public void listenNew(String message) throws JsonProcessingException {
        OrderDto dto = mapper.readValue(message, OrderDto.class);
        Product product = repository.findById(dto.getProductUUID()).orElseThrow();
        int available = product.getAvailableQuantity();
        int need = dto.getQuantity();
        if (available >= need) {
            kafkaTemplate.send(producerYamlProperties.getSuccessTopic(), String.valueOf(dto.getOrderUUID()));
            log.info("Зарезервировано {} из {}.", need, available);
        } else {
            kafkaTemplate.send(producerYamlProperties.getFailTopic(), String.valueOf(dto.getOrderUUID()));
            log.info("Невозможно зарезервировать {} из {}.", need, available);
        }
    }

    @KafkaListener(topics = "executeOrder")
    public void executeOrder(String message) throws JsonProcessingException {
        OrderDto dto = mapper.readValue(message, OrderDto.class);
        Product product = repository.findById(dto.getProductUUID()).orElseThrow();
        product.setReservedQuantity(0);
        log.info("Списание товара со склада.");
        repository.save(product);
    }

    @KafkaListener(topics = "cancel")
    public void listenRollback(String message) throws JsonProcessingException {
        OrderDto dto = mapper.readValue(message, OrderDto.class);
        Product product = repository.findById(dto.getProductUUID()).orElseThrow();
        product.setAvailableQuantity(product.getAvailableQuantity() + product.getReservedQuantity());
        product.setReservedQuantity(0);
        log.info("Отмена резервирования.");
        repository.save(product);
    }
}

