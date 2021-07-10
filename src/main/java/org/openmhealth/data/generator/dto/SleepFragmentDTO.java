package org.openmhealth.data.generator.dto;

import org.openmhealth.schema.serializer.SerializationConstructor;

/**
 * @ClassName SleepFragmentDTO
 * @Description 睡眠时长
 * @Author zws
 * @Date 2021/7/7 9:32
 * @Version 1.0
 */
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

        public Builder setSleepFragment(Double sleepFragment) {
            this.sleepFragment = sleepFragment;
            return this;
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

    @Override
    public String toString() {
        return "SleepFragmentDTO{" +
                "sleepFragment=" + sleepFragment +
                ", mField=" + mField +
                '}';
    }
}
