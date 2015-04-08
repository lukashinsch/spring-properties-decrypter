package eu.hinsch.spring.propertiesdecrypter;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.salt.ZeroSaltGenerator;
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
    public static final String DEFAULT_ALGORITHM = "PBEWithMD5AndDES";

    private String prefix;
    private StandardPBEStringEncryptor encrypter = new StandardPBEStringEncryptor();

    @Override
    public void setEnvironment(final Environment environment) {
        initializeEncrypter(environment);
        prefix = environment.getProperty("propertyDecryption.prefix", "{encrypted}");

        final ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) environment;
        final MutablePropertySources propertySources = configurableEnvironment.getPropertySources();

        List<String> encryptedKeys = getKeysOfEncryptedPropertyValues(environment, propertySources);
        addDecryptedValues(environment, propertySources, encryptedKeys);

        super.setEnvironment(environment);
    }

    private void initializeEncrypter(Environment environment) {
        encrypter.setAlgorithm(environment.getProperty("propertyDecryption.algorithm", DEFAULT_ALGORITHM));
        encrypter.setSaltGenerator(new ZeroSaltGenerator());
        encrypter.setPassword(environment.getRequiredProperty(PASSWORD_PROPERTY));
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
        return encrypter.decrypt(cypher);
    }

    private boolean isEncrypted(Object propertyValue) {
        return propertyValue != null && propertyValue instanceof String && ((String)propertyValue).startsWith(prefix);
    }

    private String getCypher(String encryptedPropertyValue) {
        return encryptedPropertyValue.substring(prefix.length());
    }
}
