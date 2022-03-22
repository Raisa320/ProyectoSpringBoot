package pds.wabbit;

import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WabbitApplication {

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-5"));
    }

    public static void main(String[] args) {
        SpringApplication.run(WabbitApplication.class, args);
    }

}
