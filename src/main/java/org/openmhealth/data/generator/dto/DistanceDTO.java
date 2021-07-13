package org.openmhealth.data.generator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.openmhealth.schema.serializer.SerializationConstructor;

/**
 * @ClassName DistanceDTO
 * @Description 距离
 * @Author zws
 * @Date 2021/7/7 9:30
 * @Version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public class DistanceDTO extends MeasureDTO{

    /*
    距离
     */
    private Double distance;

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
        this.distance = builder.distanceCount;
    }

    public Double getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "DistanceDTO{" +
                "distanceCount=" + distance +
                '}';
    }
}
