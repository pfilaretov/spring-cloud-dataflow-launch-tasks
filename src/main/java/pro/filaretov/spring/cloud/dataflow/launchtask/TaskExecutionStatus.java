package pro.filaretov.spring.cloud.dataflow.launchtask;

import lombok.Data;

@Data
public class TaskExecutionStatus {

    private Long executionId;
    private Integer exitCode;
    private String taskName;
    private String taskExecutionStatus;
}
