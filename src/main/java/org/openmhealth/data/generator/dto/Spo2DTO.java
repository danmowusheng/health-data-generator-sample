package org.openmhealth.data.generator.dto;

import org.openmhealth.schema.serializer.SerializationConstructor;

/**
 * @ClassName Spo2DTO
 * @Description 血氧饱和
 * @Author zws
 * @Date 2021/7/7 9:34
 * @Version 1.0
 */
public class Spo2DTO extends MeasureDTO{

    /*
    血氧饱和值
     */
    private Double spo2;


    /*
    必备指标
     */
    private String mField;

    /*
    是否进行氧疗
     */
    private Boolean oxygenTherapy;

    /*
    spo2测量
     */
    private String spo2Measurement;

    @SerializationConstructor
    protected Spo2DTO() {
    }

    public static class Builder extends MeasureDTO.Builder<Spo2DTO, Spo2DTO.Builder>{

        private Double spo2;
        private String mField;
        private Boolean oxygenTherapy;
        private String spo2Measurement;

        public Builder setSpo2(Double spo2) {
            this.spo2 = spo2;
            return this;
        }

        public Builder setmField(String mField) {
            this.mField = mField;
            return this;
        }

        public Builder setOxygenTherapy(Boolean oxygenTherapy) {
            this.oxygenTherapy = oxygenTherapy;
            return this;
        }

        public Builder setSpo2Measurement(String spo2Measurement) {
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
        this.mField = builder.mField;
        this.oxygenTherapy = builder.oxygenTherapy;
        this.spo2 = builder.spo2;
        this.spo2Measurement = builder.spo2Measurement;
    }

    @Override
    public String toString() {
        return "Spo2DTO{" +
                "spo2=" + spo2 +
                ", mField='" + mField + '\'' +
                ", oxygenTherapy=" + oxygenTherapy +
                ", spo2Measurement='" + spo2Measurement + '\'' +
                '}';
    }
}
