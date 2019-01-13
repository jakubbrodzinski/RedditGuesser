package bach.project.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(scanBasePackages = {"bach.project.configuration","bach.project.dao","bach.project.service","bach.project.controller"})
@PropertySource(value={"classpath:/mailsender.properties","classpath:/reddit.properties"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
