package pro.filaretov.spring.cloud.dataflow.launchtask;

import lombok.Data;

@Data
public class TaskDefinition {

    private String name;
    private String dslText;
    private String description;
    private String status;
    private boolean composed;
    private boolean composedTaskElement;

}
