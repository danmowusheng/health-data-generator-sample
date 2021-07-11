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
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SleepFragmentDTO extends MeasureDTO{

    /*
    睡眠时长
     */
    private Double sleepFragment;

    /*
    必备指标
     */
    private Integer mField;

    @SerializationConstructor
    protected SleepFragmentDTO(){
    }

    public static class Builder extends MeasureDTO.Builder<SleepFragmentDTO, SleepFragmentDTO.Builder>{
        private Double sleepFragment;
        private Integer mField;

        public Builder (Double sleepFragment) {
            this.sleepFragment = sleepFragment;
        }

        public Builder setmField(Integer mField) {
            this.mField = mField;
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
    }

    public Double getSleepFragment() {
        return sleepFragment;
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
