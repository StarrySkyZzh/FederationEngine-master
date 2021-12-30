package au.com.d2dcrc;

import au.com.d2dcrc.domain.Greeting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Example controller
 */
@RestController
public class GreetingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GreetingController.class);

    /**
     * Example call returning a greeting
     * @return the greeting
     */
    @RequestMapping("/api/greeting")
    Greeting greeting() {
        String greeting = "Hello World";
        LOGGER.debug("Greeting is: " + greeting);
        return new Greeting(greeting);
    }
}