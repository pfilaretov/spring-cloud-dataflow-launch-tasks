package pro.filaretov.spring.cloud.dataflow.launchtask;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MerchantPipelineExecution {

    private final MerchantPipeline merchantPipeline;
    private final int salesPersonCount;
}
