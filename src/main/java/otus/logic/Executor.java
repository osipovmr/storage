package otus.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import otus.model.dto.ProcessOrderDto;
import otus.model.dto.UploadProductDto;
import otus.model.entity.Product;
import otus.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/storage")
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

    @PostMapping("/upload")
    public ResponseEntity<?> checkAccount(@RequestBody List<UploadProductDto> uploadProductDtoList) {
        List<Product> list = new ArrayList<>();
        for (UploadProductDto dto : uploadProductDtoList) {
            Product product = Product.builder()
                    .name(dto.getName())
                    .price(dto.getPrice())
                    .availableQuantity(dto.getQuantity())
                    .build();
            list.add(product);
        }
        repository.saveAll(list);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}

