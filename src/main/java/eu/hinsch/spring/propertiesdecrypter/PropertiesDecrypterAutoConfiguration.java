package eu.hinsch.spring.propertiesdecrypter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Created by lh on 05/04/15.
 */
@Configuration
public class PropertiesDecrypterAutoConfiguration {

    @ConditionalOnProperty(DecryptingPropertySourcesPlaceholderConfigurer.PASSWORD_PROPERTY)
    @Bean
    public PropertySourcesPlaceholderConfigurer decryptingPropertySourcesPlaceholderConfigurer() {
        return new DecryptingPropertySourcesPlaceholderConfigurer();
    }
}
