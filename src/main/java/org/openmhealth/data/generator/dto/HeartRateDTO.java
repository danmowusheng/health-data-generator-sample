package org.openmhealth.data.generator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.openmhealth.schema.serializer.SerializationConstructor;

/**
 * @ClassName HeartRateDTO
 * @Description 心率
 * @Author zws
 * @Date 2021/7/7 9:33
 * @Version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public class HeartRateDTO extends MeasureDTO{

    /*
    心率
     */
    private Integer heartRate;

    /*
    测量类型
     */
    private Integer bpmMode;

    @SerializationConstructor
    protected HeartRateDTO() {
    }

    public static class Builder extends MeasureDTO.Builder<HeartRateDTO, Builder>{

        private Integer heartRate;
        private Integer bpmMode;

        public Builder (Integer heartRate) {
            this.heartRate = heartRate;
        }

        public Builder setBpmMode(Integer bpmMode) {
            this.bpmMode = bpmMode;
            return this;
        }

        @Override
        public HeartRateDTO build() {
            return new HeartRateDTO(this);
        }
    }

    private HeartRateDTO(Builder builder) {
        super(builder);
        this.bpmMode = builder.bpmMode;
        this.heartRate = builder.heartRate;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public Integer getBpmMode() {
        return bpmMode;
    }

    @Override
    public String toString() {
        return "HeartRateDTO{" +
                "heartRate=" + heartRate +
                ", bpmMode=" + bpmMode +
                '}';
    }
}
