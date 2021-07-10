package org.openmhealth.data.generator.dto;

import org.openmhealth.schema.serializer.SerializationConstructor;

/**
 * @ClassName StressDTO
 * @Description 压力
 * @Author zws
 * @Date 2021/7/7 9:35
 * @Version 1.0
 */
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

        public Builder setStress(Integer stress) {
            this.stress = stress;
            return this;
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

    @Override
    public String toString() {
        return "StressDTO{" +
                "stress=" + stress +
                ", grade=" + grade +
                ", measureType=" + measureType +
                '}';
    }
}
