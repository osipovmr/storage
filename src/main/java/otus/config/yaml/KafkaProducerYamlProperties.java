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
@ConfigurationProperties(prefix = "producer")
@PropertySource(value = "${properties.classpath}kafka_properties.yml", factory = YamlPropertySourceFactory.class)
public class KafkaProducerYamlProperties {

    private Map<String, String> propertiesMap;
    private String successTopic;
    private String failTopic;
}
