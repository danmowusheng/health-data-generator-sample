package org.openmhealth.data.generator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.openmhealth.schema.serializer.SerializationConstructor;

/**
 * @ClassName StressDTO
 * @Description 压力
 * @Author zws
 * @Date 2021/7/7 9:35
 * @Version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StressDTO extends MeasureDTO{

    /*
    压力
     */
    private Integer stress;

    /*
    等级
     */
    private Integer grade;

    /*
    测量类型
     */
    private Integer measureType;

    @SerializationConstructor
    protected StressDTO() {
    }

    public static class Builder extends MeasureDTO.Builder<StressDTO, StressDTO.Builder>{

        private Integer stress;

        private Integer grade;

        private Integer measureType;

        public Builder (Integer stress) {
            this.stress = stress;
        }

        public Builder setGrade(Integer grade) {
            this.grade = grade;
            return this;
        }

        public Builder setMeasureType(Integer measureType) {
            this.measureType = measureType;
            return this;
        }

        @Override
        public StressDTO build() {
            return new StressDTO(this);
        }
    }

    private StressDTO(Builder builder) {
        super(builder);
        this.grade = builder.grade;
        this.measureType = builder.measureType;
        this.stress = builder.stress;
    }

    public Integer getStress() {
        return stress;
    }

    public Integer getGrade() {
        return grade;
    }

    public Integer getMeasureType() {
        return measureType;
    }

    @Override
    public String toString() {
        return "StressDTO{" +
                "stress=" + stress +
                ", grade=" + grade +
                ", measureType=" + measureType +
                '}';
    }
}
