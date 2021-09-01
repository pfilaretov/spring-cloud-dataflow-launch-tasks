package pro.filaretov.spring.cloud.dataflow.launchtask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

@Slf4j
public class LaunchTasksPocApplicationRunner implements ApplicationRunner {

    public static final String TASK_MAVEN_URI = "maven://pro.filaretov.spring.cloud.task.sample:spring-cloud-task-poc:2.0.0-SNAPSHOT";
    public static final String TASK_NAME = "task-poc";
    public static final int MERCHANT_COUNT = 1;
    public static final int PIPELINE_COUNT = 2;
    public static final int SALES_PERSON_COUNT = 2;

    @Autowired
    private ScdfClient scdfClient;

    @Autowired
    private PipelineTaskExecutor pipelineTaskExecutor;

    @Override
    public void run(ApplicationArguments args) {
        cleanup();

        registerTaskApp();

        List<MerchantPipelineExecution> pipelineExecutions = createPipelineExecutions();
        List<CompletableFuture<Void>> myJobs = new ArrayList<>();
        for (MerchantPipelineExecution pipelineExecution : pipelineExecutions) {
            for (int j = 0; j < pipelineExecution.getSalesPersonCount(); j++) {
                CompletableFuture<Void> future = pipelineTaskExecutor.run(pipelineExecution.getMerchantPipeline(), j + 1);
                myJobs.add(future);
            }
        }

        CompletableFuture.allOf(myJobs.toArray(new CompletableFuture[0])).join();
    }

    private void cleanup() {
        log.info("Cleaning up...");

        // to work around some kind of bug with destroy all in SCDF
        while (true) {
            try {
                scdfClient.destroyAllTaskDefinitions();
                break;
            } catch (Exception e) {
                log.warn("Exception caught during cleanup: {}", e.getMessage());
            }
        }

        log.info("Cleanup complete");
    }

    private void registerTaskApp() {
        log.info("Registering task app..");
        scdfClient.registerTaskApp(TASK_NAME, TASK_MAVEN_URI);
        log.info("Registered");
    }

    private List<MerchantPipelineExecution> createPipelineExecutions() {
        List<MerchantPipelineExecution> pipelineExecutions = new ArrayList<>();

        for (int merchCount = 1; merchCount <= MERCHANT_COUNT; merchCount++) {
            for (int pipelineCount = 1; pipelineCount <= PIPELINE_COUNT; pipelineCount++) {
                MerchantPipeline pipeline = MerchantPipeline.builder()
                    .name("m" + merchCount + "-p" + pipelineCount)
                    .definition("t1: " + TASK_NAME + " && t2: " + TASK_NAME)
                    .description("Merchant " + merchCount + ", Pipeline " + pipelineCount)
                    .build();

                log.info("Creating pipeline '{}' defined as '{}'...", pipeline.getName(), pipeline.getDefinition());
                scdfClient.createTask(pipeline);
                log.info("Created");

                pipelineExecutions.add(new MerchantPipelineExecution(pipeline, SALES_PERSON_COUNT));
            }
        }

        return pipelineExecutions;
    }
}
