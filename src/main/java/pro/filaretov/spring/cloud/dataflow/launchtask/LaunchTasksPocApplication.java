package pro.filaretov.spring.cloud.dataflow.launchtask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAsync
public class LaunchTasksPocApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaunchTasksPocApplication.class, args).close();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.rootUri("http://localhost:9393").build();
    }

    @Bean
    public LaunchTasksPocApplicationRunner myTask() {
        return new LaunchTasksPocApplicationRunner();
    }

    @Bean
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(20);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setKeepAliveSeconds(10);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setThreadNamePrefix("exec-");
        taskExecutor.initialize();
        return taskExecutor;
    }
}
