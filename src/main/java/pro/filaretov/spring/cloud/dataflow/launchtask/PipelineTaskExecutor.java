package pro.filaretov.spring.cloud.dataflow.launchtask;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PipelineTaskExecutor {

    @Autowired
    private ScdfClient scdfClient;

    @Async
    public CompletableFuture<Void> run(MerchantPipeline pipeline, int salesPersonId) {
        try {
            String pipelineName = pipeline.getName();
            List<String> taskDefinitionNames = scdfClient.getTaskDefinitionNames(pipelineName);
            log.info("Pipeline '{}', sales person ID {}, tasks: {}", pipelineName, salesPersonId, taskDefinitionNames);

            int totalTasks = taskDefinitionNames.size();
            for (int i = 0; i < totalTasks; i++) {
                String task = taskDefinitionNames.get(i);

                log.info("Pipeline '{}', sales person ID {}, starting task '{}' ({} out of {} total)...", pipelineName,
                    salesPersonId, task, i + 1, totalTasks);
                String executionId = scdfClient.startTask(task);
                log.info("Pipeline '{}', sales person ID {}, task '{}' started, executionId={}", pipelineName,
                    salesPersonId, task, executionId);

                TaskExecutionStatus status;
                do {
                    log.info("Waiting...");
                    Thread.sleep(2000);
                    status = scdfClient.getTaskExecutionStatus(executionId);
                    log.info("Task execution status is '{}'", status.getTaskExecutionStatus());
                } while (!"COMPLETE".equals(status.getTaskExecutionStatus()));

                log.info("Task '{}' with executionId={} complete", task, executionId);
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }

        return CompletableFuture.completedFuture(null);
    }
}
