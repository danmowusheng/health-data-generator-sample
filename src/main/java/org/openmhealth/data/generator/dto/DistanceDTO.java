package org.openmhealth.data.generator.dto;

import org.openmhealth.schema.serializer.SerializationConstructor;

/**
 * @ClassName DistanceDTO
 * @Description 距离
 * @Author zws
 * @Date 2021/7/7 9:30
 * @Version 1.0
 */
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

        public Builder setDistanceCount(Double caloriesCount) {
            this.distanceCount = caloriesCount;
            return this;
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

    @Override
    public String toString() {
        return "DistanceDTO{" +
                "distanceCount=" + distanceCount +
                '}';
    }
}
