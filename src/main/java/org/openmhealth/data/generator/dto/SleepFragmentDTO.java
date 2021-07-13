package org.openmhealth.data.generator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.openmhealth.schema.serializer.SerializationConstructor;

/**
 * @ClassName SleepFragmentDTO
 * @Description 睡眠时长
 * @Author zws
 * @Date 2021/7/7 9:32
 * @Version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public class SleepFragmentDTO extends MeasureDTO{

    /*
    睡眠类型
     */
    private Integer sleepFragment;

    /*
    必备指标
     */
    private Integer mField;

    /*
    睡眠时长
    */
    private Double sleepDuration;

    @SerializationConstructor
    protected SleepFragmentDTO(){
    }

    public static class Builder extends MeasureDTO.Builder<SleepFragmentDTO, SleepFragmentDTO.Builder>{
        private Integer sleepFragment;
        private Integer mField;
        private Double sleepDuration;

        public Builder (Double sleepDuration) {
            this.sleepDuration = sleepDuration;
        }

        public Builder setmField(Integer mField) {
            this.mField = mField;
            return this;
        }

        public Builder setSleepFragment(Integer sleepFragment){
            this.sleepFragment = sleepFragment;
            return this;
        }

        @Override
        public SleepFragmentDTO build() {
            return new SleepFragmentDTO(this);
        }
    }

    private SleepFragmentDTO(Builder builder){
        super(builder);
        this.mField = builder.mField;
        this.sleepFragment = builder.sleepFragment;
        this.sleepDuration = builder.sleepDuration;
    }

    public Integer getSleepFragment() {
        return sleepFragment;
    }

    public Double getSleepDuration() {
        return sleepDuration;
    }

    public Integer getmField() {
        return mField;
    }

    @Override
    public String toString() {
        return "SleepFragmentDTO{" +
                "sleepFragment=" + sleepFragment +
                ", mField=" + mField +
                '}';
    }
}
