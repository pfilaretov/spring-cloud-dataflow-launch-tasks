package pro.filaretov.spring.cloud.dataflow.launchtask;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MerchantPipeline {

    private final String name;
    private final String definition;
    private final String description;
}
