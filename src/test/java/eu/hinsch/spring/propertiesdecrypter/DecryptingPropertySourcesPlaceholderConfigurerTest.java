package eu.hinsch.spring.propertiesdecrypter;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.ThrowableCauseMatcher.hasCause;

/**
 * Created by lh on 04/04/15.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringApplicationConfiguration(classes = DecryptingPropertySourcesPlaceholderConfigurerTest.TestConfig.class)
public class DecryptingPropertySourcesPlaceholderConfigurerTest {

    private static final String CODE = "Cn0fGsAEINM1+FsMf0evHob485wqmPwT";
    private static final String ENCRYPTED_VALUE = "{cypher}" + CODE;
    private static final String SECRET = "MY-SECRET";
    private List<String> systemProperties = new ArrayList<>();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Autowired
    private Environment environment;
    @Autowired
    private ConfigurableApplicationContext applicationContext;

    private TestContextManager testContextManager;

    @SpringBootApplication
    static class TestConfig {
    }

    @Test
    public void shouldDecodeSystemProperty() throws Exception {
        // given
        setSystemProperty("secretProperty", ENCRYPTED_VALUE);

        // when
        startupContext();

        // then
        assertThat(environment.getProperty("secretProperty"), is(SECRET));
    }

    @Test
    public void shouldDecryptCorrectPropertyOnConflict() throws Exception {
        // given
        setSystemProperty("conflictingProperty", ENCRYPTED_VALUE);

        // when
        startupContext();

        //
        assertThat(environment.getProperty("conflictingProperty"), is(SECRET));
    }

    @Test
    public void shouldThrowExceptionIfPasswordIsIncorrect() throws Exception {
        // given
        setSystemProperty("propertyEncryption.password", "NOT-THE-CORRECT-PASSWORD");
        setSystemProperty("somethingToDecrypt", ENCRYPTED_VALUE);
        exception.expect(IllegalStateException.class);
        // meaningful exception is wrapped twice inside spring errors
        exception.expectCause(hasCause(isA(EncryptionOperationNotPossibleException.class)));

        // when
        startupContext();

        // then -> exception
    }

    @Test
    @Ignore("TODO not yet working")
    public void shouldUsePrefixOverride() throws Exception {
        // given
        setSystemProperty("propertyDecryption.prefix", "{cypher}");
        setSystemProperty("secretProperty", ENCRYPTED_VALUE);

        // when
        startupContext();

        // then
        assertThat(environment.getProperty("secretProperty"), is(SECRET));
    }

    private void setSystemProperty(String property, String value) {
        System.setProperty(property, value);
        systemProperties.add(property);
    }

    // manual app startup because we want to set properties before
    private void startupContext() throws Exception {
        testContextManager = new TestContextManager(this.getClass());
        testContextManager.prepareTestInstance(this);
        testContextManager.registerTestExecutionListeners(new DirtiesContextTestExecutionListener());
    }

    @After
    public void cleanUp() {
        systemProperties.stream().forEach(System::clearProperty);
    }
}