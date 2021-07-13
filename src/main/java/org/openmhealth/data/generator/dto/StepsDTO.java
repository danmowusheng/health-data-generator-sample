package org.openmhealth.data.generator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.openmhealth.schema.serializer.SerializationConstructor;

/**
 * @ClassName StepsDTO
 * @Description 步数
 * @Author zws
 * @Date 2021/7/7 9:20
 * @Version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public class StepsDTO extends MeasureDTO{

    /*
    步数
     */
    private Integer steps;

    @SerializationConstructor
    protected StepsDTO() {
    }

    public static class Builder extends MeasureDTO.Builder<StepsDTO, StepsDTO.Builder>{

        private Integer steps;

        public Builder (Integer steps) {
            this.steps = steps;
        }

        @Override
        public StepsDTO build() {
            return new StepsDTO(this);
        }
    }

    public Integer getSteps() {
        return steps;
    }

    private StepsDTO(Builder builder) {
        super(builder);
        this.steps = builder.steps;
    }

    @Override
    public String toString() {
        return "StepsDTO{" +
                "stepsCount=" + steps +
                '}';
    }
}
