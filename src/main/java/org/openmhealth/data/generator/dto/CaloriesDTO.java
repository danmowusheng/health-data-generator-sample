package org.openmhealth.data.generator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.openmhealth.schema.serializer.SerializationConstructor;

/**
 * @ClassName CaloriesDTO
 * @Description 卡路里
 * @Author zws
 * @Date 2021/7/7 9:30
 * @Version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public class CaloriesDTO extends MeasureDTO{

    /*
    卡路里
     */
    private Double calories;

    @SerializationConstructor
    protected CaloriesDTO() {
    }

    public static class Builder extends MeasureDTO.Builder<CaloriesDTO, Builder>{

        private Double caloriesCount;
        /*
        public Builder(double caloriesCount){
            this.caloriesCount = caloriesCount;
        }
        */

        public Builder (Double caloriesCount) {
            this.caloriesCount = caloriesCount;
        }


        @Override
        public CaloriesDTO build() {
            return new CaloriesDTO(this);
        }
    }

    private CaloriesDTO(Builder builder) {
        super(builder);
        this.calories = builder.caloriesCount;
    }

    public Double getCalories() {
        return calories;
    }

    @Override
    public String toString() {
        return "CaloriesDTO{" +
                "caloriesCount=" + calories +
                '}';
    }
}
