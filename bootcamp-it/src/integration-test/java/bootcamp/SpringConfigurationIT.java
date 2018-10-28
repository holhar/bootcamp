package bootcamp;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringConfigurationIT.Config.class)
public class SpringConfigurationIT {

    @Autowired
    private ApplicationDeployer applicationDeployer;

    @Autowired
    private ServicesDeployer servicesDeployer;

    @Test
    public void deploy() throws Throwable {

        File projectFolder = new File(new File("."), "../spring-configuration");
        File jar = new File(projectFolder, "target/configuration.jar");

        String applicationName = "bootcamp-customers";
        String mysqlSvc = "bootcamp-customers-mysql";

        Map<String, String> env = new HashMap<>();
        env.put("SPRING_PROFILES_ACTIVE", "cloud");

        Duration timeout = Duration.ofMinutes(5);
        servicesDeployer.deployService(applicationName, mysqlSvc, "cleardb", "spark")
                // <1>
                .then(
                        applicationDeployer.deployApplication(jar, applicationName, env, timeout,
                                mysqlSvc)) // <2>
                .block(); // <3>

    }

    @SpringBootApplication
    public static class Config {

        // cf config will be pulled from spring-configuration
        @Bean
        ApplicationDeployer applications(CloudFoundryOperations cf) {
            return new ApplicationDeployer(cf);
        }

        // cf config will be pulled from spring-configuration
        @Bean
        ServicesDeployer services(CloudFoundryOperations cf) {
            return new ServicesDeployer(cf);
        }
    }

    // see: https://www.programcreek.com/java-api-examples/?api=org.cloudfoundry.operations.CloudFoundryOperations
    @Bean
    CloudFoundryOperations cloudFoundryOperations() {
        DefaultConnectionContext connectionContext = DefaultConnectionContext.builder()
                .apiHost("https://api.run.pivotal.io")
                .build();

        TokenProvider tokenProvider = PasswordGrantTokenProvider.builder()
                .password("<CF-PASSWORD-HERE>")
                .username("<CF-USERNAME-HERE>")
                .build();

        ReactorCloudFoundryClient reactorClient = ReactorCloudFoundryClient.builder()
                .connectionContext(connectionContext)
                .tokenProvider(tokenProvider)
                .build();

        CloudFoundryOperations cloudFoundryOperations = DefaultCloudFoundryOperations.builder()
                .cloudFoundryClient(reactorClient)
                .organization("holhar")
                .space("development")
                .build();

        return cloudFoundryOperations;
    }

}
