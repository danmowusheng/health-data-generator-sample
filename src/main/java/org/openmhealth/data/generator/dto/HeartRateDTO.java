package org.openmhealth.data.generator.dto;

import org.openmhealth.data.generator.constant.HeartRate;
import org.openmhealth.schema.serializer.SerializationConstructor;

/**
 * @ClassName HeartRateDTO
 * @Description 心率
 * @Author zws
 * @Date 2021/7/7 9:33
 * @Version 1.0
 */
public class HeartRateDTO extends MeasureDTO{

    /*
    心率
     */
    private Integer heartRate;

    /*
    测量类型
     */
    private HeartRate mField;

    @SerializationConstructor
    protected HeartRateDTO() {
    }

    public static class Builder extends MeasureDTO.Builder<HeartRateDTO, Builder>{

        private Integer heartRate;
        private HeartRate mField;

        public Builder setHeartRate(Integer heartRate) {
            this.heartRate = heartRate;
            return this;
        }

        public Builder setmField(HeartRate mField) {
            this.mField = mField;
            return this;
        }

        @Override
        public HeartRateDTO build() {
            return new HeartRateDTO(this);
        }
    }

    private HeartRateDTO(Builder builder) {
        super(builder);
        this.mField = builder.mField;
        this.heartRate = builder.heartRate;
    }

    @Override
    public String toString() {
        return "HeartRateDTO{" +
                "heartRate=" + heartRate +
                ", mField=" + mField +
                '}';
    }
}
