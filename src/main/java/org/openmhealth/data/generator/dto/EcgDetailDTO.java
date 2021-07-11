package org.openmhealth.data.generator.dto;

import org.openmhealth.data.generator.constant.HeartRate;
import org.openmhealth.schema.serializer.SerializationConstructor;

/**
 * @ClassName EcgDetailDTO
 * @Description 心电测量明细
 * @Author zws
 * @Date 2021/7/7 9:35
 * @Version 1.0
 */
public class EcgDetailDTO extends MeasureDTO {

    /*
    ecg类型
     */
    private Integer ecgType;


    /*
    电压值
     */
    private Double voltageDatas;

    @SerializationConstructor
    protected EcgDetailDTO(){
    }

    public static class Builder extends MeasureDTO.Builder<EcgDetailDTO, Builder>{

        private Integer ecgType;
        private Double voltageDatas;

        public Builder (Double voltageDatas) {
            this.voltageDatas = voltageDatas;
        }

        public Builder setECGType(Integer ecgType) {
            this.ecgType = ecgType;
            return this;
        }


        @Override
        public EcgDetailDTO build() {
            return new EcgDetailDTO(this);
        }
    }

    private EcgDetailDTO(Builder builder) {
        super(builder);
        this.ecgType = builder.ecgType;
        this.voltageDatas = builder.voltageDatas;
    }

    public Integer getEcgType() {
        return ecgType;
    }

    public Double getVoltageDatas() {
        return voltageDatas;
    }

    @Override
    public String toString() {
        return "EcgDetailDTO{" +
                "ecgType=" + ecgType +
                ", voltageDatas=" + voltageDatas +
                '}';
    }
}
