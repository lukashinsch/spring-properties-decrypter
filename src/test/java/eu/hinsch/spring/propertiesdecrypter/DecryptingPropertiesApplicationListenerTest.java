package eu.hinsch.spring.propertiesdecrypter;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.util.Properties;

import static org.hamcrest.Matchers.isA;

/**
 * Created by lh on 27/06/15.
 */
public class DecryptingPropertiesApplicationListenerTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private final DecryptingPropertiesApplicationListener listener = new DecryptingPropertiesApplicationListener();

    @Test
    public void shouldWrapExceptionOnEncryptionError() {
        // given
        StandardEnvironment environment = new StandardEnvironment();
        Properties props = new Properties();
        props.put("propertyDecryption.password", "NOT-A-PASSWORD");
        props.put("someProperty", "{encrypted}NOT-ENCRYPTED");
        environment.getPropertySources().addFirst(new PropertiesPropertySource("test-env", props));

        exception.expect(RuntimeException.class);
        exception.expectMessage("Unable to decrypt property value '{encrypted}NOT-ENCRYPTED'");
        exception.expectCause(isA(EncryptionOperationNotPossibleException.class));

        ApplicationEnvironmentPreparedEvent event = new ApplicationEnvironmentPreparedEvent(new SpringApplication(), new String[0], environment);

        // when
        listener.onApplicationEvent(event);

        // then -> exception
    }
}
