package org.openmhealth.data.generator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.openmhealth.schema.serializer.SerializationConstructor;

import java.util.Objects;

/**
 * @ClassName CaloriesDTO
 * @Description 卡路里
 * @Author zws
 * @Date 2021/7/7 9:30
 * @Version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CaloriesDTO extends MeasureDTO{

    /*
    卡路里
     */
    private Double caloriesCount;

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
        this.caloriesCount = builder.caloriesCount;
    }

    public Double getCaloriesCount() {
        return caloriesCount;
    }

    @Override
    public String toString() {
        return "CaloriesDTO{" +
                "caloriesCount=" + caloriesCount +
                '}';
    }
}
