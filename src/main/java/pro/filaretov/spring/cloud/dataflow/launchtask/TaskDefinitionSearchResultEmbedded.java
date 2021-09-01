package pro.filaretov.spring.cloud.dataflow.launchtask;

import java.util.List;
import lombok.Data;

@Data
public class TaskDefinitionSearchResultEmbedded {

    private List<TaskDefinition> taskDefinitionResourceList;
}
