package otus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import otus.config.yaml.KafkaProducerYamlProperties;
import otus.model.dto.OrderDto;
import otus.model.entity.Product;

@Service
@RequiredArgsConstructor
public class Executor {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;
    private final ProductRepository repository;
    private final KafkaProducerYamlProperties properties;

    @KafkaListener(topics = "newOrder")
    public void listener(String message) throws JsonProcessingException {
        OrderDto dto = mapper.readValue(message, OrderDto.class);
        Product product = repository.findById(dto.getProductUUID()).orElseThrow();
        if (product.getAvailableQuantity() >= dto.getQuantity()) {
            kafkaTemplate.send(properties.getSuccessTopic(), String.valueOf(dto.getOrderUUID()));
        } else {

        }
    }
    }

