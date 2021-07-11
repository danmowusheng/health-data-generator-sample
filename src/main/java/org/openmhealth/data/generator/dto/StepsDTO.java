package org.openmhealth.data.generator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.openmhealth.schema.serializer.SerializationConstructor;

/**
 * @ClassName StepsDTO
 * @Description 步数
 * @Author zws
 * @Date 2021/7/7 9:20
 * @Version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StepsDTO extends MeasureDTO{

    /*
    步数
     */
    private Integer stepsCount;

    @SerializationConstructor
    protected StepsDTO() {
    }

    public static class Builder extends MeasureDTO.Builder<StepsDTO, StepsDTO.Builder>{

        private Integer stepsCount;

        public Builder (Integer stepsCount) {
            this.stepsCount = stepsCount;
        }

        @Override
        public StepsDTO build() {
            return new StepsDTO(this);
        }
    }

    public Integer getStepsCount() {
        return stepsCount;
    }

    private StepsDTO(Builder builder) {
        super(builder);
        this.stepsCount = builder.stepsCount;
    }

    @Override
    public String toString() {
        return "StepsDTO{" +
                "stepsCount=" + stepsCount +
                '}';
    }
}
