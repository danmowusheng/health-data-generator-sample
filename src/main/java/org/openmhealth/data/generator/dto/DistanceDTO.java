package org.openmhealth.data.generator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.openmhealth.schema.serializer.SerializationConstructor;

/**
 * @ClassName DistanceDTO
 * @Description 距离
 * @Author zws
 * @Date 2021/7/7 9:30
 * @Version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DistanceDTO extends MeasureDTO{

    /*
    距离
     */
    private Double distanceCount;

    @SerializationConstructor
    protected DistanceDTO() {
    }

    public static class Builder extends MeasureDTO.Builder<DistanceDTO, DistanceDTO.Builder>{

        private Double distanceCount;

        public Builder (Double caloriesCount) {
            this.distanceCount = caloriesCount;
        }


        @Override
        public DistanceDTO build() {
            return new DistanceDTO(this);
        }
    }

    private DistanceDTO(Builder builder) {
        super(builder);
        this.distanceCount = builder.distanceCount;
    }

    public Double getDistanceCount() {
        return distanceCount;
    }

    @Override
    public String toString() {
        return "DistanceDTO{" +
                "distanceCount=" + distanceCount +
                '}';
    }
}
