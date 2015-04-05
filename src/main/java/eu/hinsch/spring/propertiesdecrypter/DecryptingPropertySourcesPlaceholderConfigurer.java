package eu.hinsch.spring.propertiesdecrypter;

import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

/**
 * Created by lh on 02/04/15.
 */
public class DecryptingPropertySourcesPlaceholderConfigurer extends PropertySourcesPlaceholderConfigurer {
    static final String PASSWORD_PROPERTY = "propertyDecryption.password";

    private String prefix;
    private BasicTextEncryptor encryptor = new BasicTextEncryptor();

    @Override
    public void setEnvironment(final Environment environment) {
        encryptor.setPassword(environment.getProperty(PASSWORD_PROPERTY));
        prefix = environment.getProperty("propertyDecryption.prefix", "{cypher}");

        final ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) environment;
        final MutablePropertySources propertySources = configurableEnvironment.getPropertySources();

        List<String> encryptedKeys = getKeysOfEncryptedPropertyValues(environment, propertySources);
        addDecryptedValues(environment, propertySources, encryptedKeys);

        super.setEnvironment(environment);
    }

    private List<String> getKeysOfEncryptedPropertyValues(Environment environment, MutablePropertySources propertySources) {
        Stream<PropertySource<?>> stream = getPropertySourceStream(propertySources);
        return stream.filter(source -> source instanceof MapPropertySource)
                .map(source -> (MapPropertySource) source)
                .map(PropertySource::getSource)
                .flatMap(map -> map.entrySet().stream())
                .map(Map.Entry::getKey)
                .filter(key -> isEncrypted(environment.getProperty(key)))
                .collect(toList());
    }

    private Stream<PropertySource<?>> getPropertySourceStream(final MutablePropertySources propertySources) {
        final Iterator<PropertySource<?>> iterator = propertySources.iterator();
        Iterable<PropertySource<?>> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    private void addDecryptedValues(Environment environment, MutablePropertySources propertySources, List<String> encryptedKeys) {

        // TODO collect to properties?
        final Properties decryptedValues = new Properties();
        encryptedKeys.stream().forEach(
                key -> {
                    String value = environment.getProperty(key);
                    decryptedValues.put(key, decryptPropertyValue(value));
                }
        );
        propertySources.addFirst(new PropertiesPropertySource("decryptedValues", decryptedValues));
    }

    private String decryptPropertyValue(String encryptedPropertyValue) {
        String cypher = getCypher(encryptedPropertyValue);
        return encryptor.decrypt(cypher);
    }

    private boolean isEncrypted(Object propertyValue) {
        return propertyValue != null && propertyValue instanceof String && ((String)propertyValue).startsWith(prefix);
    }

    private String getCypher(String encryptedPropertyValue) {
        return encryptedPropertyValue.substring(prefix.length());
    }
}
