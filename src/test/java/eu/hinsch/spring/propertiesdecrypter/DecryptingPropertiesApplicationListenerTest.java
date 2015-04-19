package eu.hinsch.spring.propertiesdecrypter;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by lh on 04/04/15.
 */
@RunWith(Enclosed.class)
public class DecryptingPropertiesApplicationListenerTest {

    private static final String CODE = "7M2qVa5OHzn43YWGUE6R2Q==";
    private static final String ENCRYPTED_VALUE = "{encrypted}" + CODE;
    private static final String SECRET = "MY-SECRET";

    @SpringBootApplication
    static class TestAppConfig {
    }

    @TestPropertySource(properties = "secretProperty = " + ENCRYPTED_VALUE)
    public static class WithSecretProperty extends TestConfig {
        @Test
        public void shouldDecodeSystemProperty() throws Exception {
            assertThat(environment.getProperty("secretProperty"), is(SECRET));
        }
    }

    @TestPropertySource(properties = "conflictingProperty = " + ENCRYPTED_VALUE)
    public static class WithConflictingProperty extends TestConfig {
        @Test
        public void shouldDecryptCorrectPropertyOnConflict() throws Exception {
            assertThat(environment.getProperty("conflictingProperty"), is(SECRET));
        }
    }

    @TestPropertySource(properties = {
            "propertyDecryption.prefix = ---SOME-PREFIX---",
            "secretProperty = ---SOME-PREFIX---" + CODE
    })
    public static class WithPrefix extends TestConfig {
        @Test
        public void shouldUsePrefixOverride() {
            assertThat(environment.getProperty("secretProperty"), is(SECRET));
        }

        @Test
        public void shouldNotChangePrefixProperty() {
            assertThat(environment.getProperty("propertyDecryption.prefix"), is("---SOME-PREFIX---"));
        }
    }

    public static class WithDefaults extends TestConfig {
        @Test
        public void shouldDecryptPropertyFromApplicationProperties() {
            assertThat(environment.getProperty("secretInApplicationProperties"), is(SECRET));
        }
    }

    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    @SpringApplicationConfiguration(classes = DecryptingPropertiesApplicationListenerTest.TestAppConfig.class)
    @RunWith(SpringJUnit4ClassRunner.class)
    @TestPropertySource(properties = "propertyDecryption.password = MYPASSWORD")
    static abstract class TestConfig {
        @Autowired
        protected Environment environment;
    }
}