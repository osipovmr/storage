package otus.config.yaml;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "consumer")
@PropertySource(value = "${properties.classpath}kafka_properties.yml", factory = YamlPropertySourceFactory.class)
public class KafkaConsumerYamlProperties {

    private Map<String, String> propertiesMap;
}
