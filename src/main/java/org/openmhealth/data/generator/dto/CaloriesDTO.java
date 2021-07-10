package org.openmhealth.data.generator.dto;

import org.openmhealth.schema.serializer.SerializationConstructor;

/**
 * @ClassName CaloriesDTO
 * @Description 卡路里
 * @Author zws
 * @Date 2021/7/7 9:30
 * @Version 1.0
 */
public class CaloriesDTO extends MeasureDTO{

    /*
    卡路里
     */
    private Double caloriesCount;

    @SerializationConstructor
    protected CaloriesDTO() {
    }

    public static class Builder extends MeasureDTO.Builder<CaloriesDTO, CaloriesDTO.Builder>{

        private Double caloriesCount;

        public Builder setCaloriesCount(Double caloriesCount) {
            this.caloriesCount = caloriesCount;
            return this;
        }


        @Override
        public CaloriesDTO build() {
            return new CaloriesDTO(this);
        }
    }

    private CaloriesDTO(Builder builder) {
        super(builder);
        this.caloriesCount = builder.caloriesCount;
    }

    @Override
    public String toString() {
        return "CaloriesDTO{" +
                "caloriesCount=" + caloriesCount +
                '}';
    }
}
