package org.openmhealth.data.generator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.openmhealth.data.generator.constant.HeartRate;
import org.openmhealth.schema.serializer.SerializationConstructor;

/**
 * @ClassName EcgDetailDTO
 * @Description 心电测量明细
 * @Author zws
 * @Date 2021/7/7 9:35
 * @Version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public class EcgDetailDTO extends MeasureDTO {

    /*
    ecg类型
     */
    private Integer mField;

    /*
    电压值
     */
    private Double voltageDatas;

    @SerializationConstructor
    protected EcgDetailDTO(){
    }

    public static class Builder extends MeasureDTO.Builder<EcgDetailDTO, Builder>{

        private Integer mField;
        private Double voltageDatas;

        public Builder (Double voltageDatas) {
            this.voltageDatas = voltageDatas;
        }

        public Builder setmField(Integer mField) {
            this.mField = mField;
            return this;
        }


        @Override
        public EcgDetailDTO build() {
            return new EcgDetailDTO(this);
        }
    }

    private EcgDetailDTO(Builder builder) {
        super(builder);
        this.mField = builder.mField;
        this.voltageDatas = builder.voltageDatas;
    }

    public Integer getmField() {
        return mField;
    }

    public Double getVoltageDatas() {
        return voltageDatas;
    }

    @Override
    public String toString() {
        return "EcgDetailDTO{" +
                "mField=" + mField +
                ", voltageDatas=" + voltageDatas +
                '}';
    }
}
