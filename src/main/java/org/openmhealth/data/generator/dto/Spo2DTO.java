package org.openmhealth.data.generator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.openmhealth.schema.serializer.SerializationConstructor;

/**
 * @ClassName Spo2DTO
 * @Description 血氧饱和
 * @Author zws
 * @Date 2021/7/7 9:34
 * @Version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public class Spo2DTO extends MeasureDTO{

    /*
    血氧饱和值
     */
    private Double spo2;

    /*
    必备指标
     */
    private Integer spo2Type;

    /*
    是否进行氧疗
     */
    private Boolean oxygenTherapy;

    /*
    spo2测量
     */
    private Integer spo2Measurement;

    @SerializationConstructor
    protected Spo2DTO() {
    }


    public static class Builder extends MeasureDTO.Builder<Spo2DTO, Spo2DTO.Builder>{

        private Double spo2;
        private Integer spo2Type;
        private Boolean oxygenTherapy;
        private Integer spo2Measurement;

        public Builder (Double spo2) {
            this.spo2 = spo2;
        }

        public Builder setSpo2Type(Integer spo2Type) {
            this.spo2Type = spo2Type;
            return this;
        }

        public Builder setOxygenTherapy(Boolean oxygenTherapy) {
            this.oxygenTherapy = oxygenTherapy;
            return this;
        }

        public Builder setSpo2Measurement(Integer spo2Measurement) {
            this.spo2Measurement = spo2Measurement;
            return this;
        }

        @Override
        public Spo2DTO build() {
            return new Spo2DTO(this);
        }
    }

    private Spo2DTO(Builder builder) {
        super(builder);
        this.spo2Type = builder.spo2Type;
        this.oxygenTherapy = builder.oxygenTherapy;
        this.spo2 = builder.spo2;
        this.spo2Measurement = builder.spo2Measurement;
    }

    public Double getSpo2() {
        return spo2;
    }

    public Integer getSpo2Type() {
        return spo2Type;
    }

    public Boolean getOxygenTherapy() {
        return oxygenTherapy;
    }

    public Integer getSpo2Measurement() {
        return spo2Measurement;
    }

    @Override
    public String toString() {
        return "Spo2DTO{" +
                "spo2=" + spo2 +
                ", spo2Type=" + spo2Type +
                ", oxygenTherapy=" + oxygenTherapy +
                ", spo2Measurement=" + spo2Measurement +
                '}';
    }
}
