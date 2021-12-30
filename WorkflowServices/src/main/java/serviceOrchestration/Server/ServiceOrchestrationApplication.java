package serviceOrchestration.Server;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication
public class ServiceOrchestrationApplication {


    public static final String FDEURL = "http://103.61.226.11:8091/query";

    public static void main(String... args) {
        SpringApplication.run(ServiceOrchestrationApplication.class, args);
    }

//    @EventListener
//    private void processPostDeploy(PostDeployEvent event) {
//        runtimeService.startProcessInstanceByKey("loanApproval");
//    }

}
