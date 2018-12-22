package com.agh.bsct.algorithm.algorithms.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@ConfigurationProperties(prefix = "algorithm-app")
@PropertySource("classpath:algorithm-app.properties")
public class AlgorithmProperties {

    /**
     * If set to true, saving some calculation values
     * to file will be possible
     */
    private Boolean isWritingValuesToFileEnabled;

}
