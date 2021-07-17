package org.openmhealth.data.generator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class SleepDurationDTO extends MeasureDTO{

    /*
    睡眠时长
    */
    private Double sleepDuration;

    /*
    睡眠类型
     */
    private Integer sleepType;



    @SerializationConstructor
    protected SleepDurationDTO(){
    }

    public static class Builder extends MeasureDTO.Builder<SleepDurationDTO, SleepDurationDTO.Builder>{
        private Integer sleepType;
        private Double sleepDuration;

        public Builder (Double sleepDuration) {
            this.sleepDuration = sleepDuration;
        }


        public Builder setSleepType(Integer sleepType){
            this.sleepType = sleepType;
            return this;
        }

        @Override
        public SleepDurationDTO build() {
            return new SleepDurationDTO(this);
        }
    }

    private SleepDurationDTO(Builder builder){
        super(builder);
        this.sleepType = builder.sleepType;
        this.sleepDuration = builder.sleepDuration;
    }

    public Integer getSleepType() {
        return sleepType;
    }

    public Double getSleepDuration() {
        return sleepDuration;
    }


    @Override
    public String toString() {
        return "SleepDurationDTO{" +
                "sleepDuration=" + sleepDuration +
                ", sleepType=" + sleepType +
                '}';
    }
}
