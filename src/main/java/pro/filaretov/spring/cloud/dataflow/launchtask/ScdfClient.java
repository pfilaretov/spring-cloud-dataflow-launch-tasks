package pro.filaretov.spring.cloud.dataflow.launchtask;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ScdfClient {

    @Autowired
    private RestTemplate restTemplate;

    public void createTask(MerchantPipeline merchantPipeline) {
        Map<String, String> uriVars = Map.of(
            "name", merchantPipeline.getName(),
            "definition", merchantPipeline.getDefinition(),
            "description", merchantPipeline.getDescription()
        );

        restTemplate.postForEntity("/tasks/definitions?name={name}&definition={definition}&description={description}",
            null, Object.class, uriVars);
    }

    public String startTask(String task) {
        return restTemplate.postForEntity("/tasks/executions?name=" + task, null, String.class).getBody();
    }

    public List<String> getTaskDefinitionNames(String pipelineName) {

        TaskDefinitionSearchResult searchResult = restTemplate.getForEntity(
            "/tasks/definitions?search=" + pipelineName, TaskDefinitionSearchResult.class).getBody();

        List<TaskDefinition> taskDefinitionResourceList = searchResult.get_embedded().getTaskDefinitionResourceList();
        return taskDefinitionResourceList.stream()
            .filter(taskDefinition -> !taskDefinition.isComposed())
            .filter(TaskDefinition::isComposedTaskElement)
            .map(TaskDefinition::getName)
            .filter(name -> name.startsWith(pipelineName + "-"))
            .collect(Collectors.toList());
    }

    public TaskExecutionStatus getTaskExecutionStatus(String executionId) {
        return restTemplate.getForEntity("/tasks/executions/" + executionId, TaskExecutionStatus.class).getBody();
    }

    public void destroyAllTaskDefinitions() {
        restTemplate.delete("/tasks/definitions");
    }

    public void registerTaskApp(String name, String uri) {
        Map<String, String> uriVars = Map.of(
            "name", name,
            "uri", uri
        );

        restTemplate.postForEntity("/apps/task/{name}?uri={uri}&force=true", null, Object.class, uriVars);
    }
}
