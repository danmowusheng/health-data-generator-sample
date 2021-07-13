package org.openmhealth.data.generator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.openmhealth.schema.serializer.SerializationConstructor;

/**
 * @ClassName EcgRecordDTO
 * @Description 心电测量记录
 * @Author zws
 * @Date 2021/7/7 9:35
 * @Version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public class EcgRecordDTO extends MeasureDTO{

    /*
    平均心率
    */
    private Integer avgHeartRate;

    /*
    心电
     */
    private Double ecg;

    /*
    心电类型
     */
    private Integer ecgType;

    /*
    心率类型
     */
    private Integer ecgArrhythmiaType;

    /*
    用户症状
     */
    private Integer userSymptom;

    /*
    示例频率
     */
    private Integer samplingFrequency;

    @SerializationConstructor
    EcgRecordDTO(){

    }

    public static class Builder extends MeasureDTO.Builder<EcgRecordDTO, Builder>{
        private Integer avgHeartRate;
        private Integer ecgType;
        private Double ecg;
        private Integer ecgArrhythmiaType;
        private Integer userSymptom;
        private Integer samplingFrequency;

        public Builder (Double ecg, Integer avgHeartRate) {
            this.ecg = ecg;
            this.avgHeartRate = avgHeartRate;
        }

        public Builder setSamplingFrequency(Integer samplingFrequency){
            this.samplingFrequency = samplingFrequency;
            return this;
        }

        public Builder setEcgType(Integer ecgType){
            this.ecgType = ecgType;
            return this;
        }

        public Builder setEcgArrhythmiaType(Integer ecgArrhythmiaType) {
            this.ecgArrhythmiaType = ecgArrhythmiaType;
            return this;
        }

        public Builder setUserSymptom(Integer userSymptom) {
            this.userSymptom = userSymptom;
            return this;
        }


        @Override
        public EcgRecordDTO build() {
            return new EcgRecordDTO(this);
        }
    }

    public Double getEcg() {
        return ecg;
    }

    public Integer getEcgType() {
        return ecgType;
    }

    public Integer getEcgArrhythmiaType() {
        return ecgArrhythmiaType;
    }

    public Integer getUserSymptom() {
        return userSymptom;
    }

    public Integer getSamplingFrequency() {
        return samplingFrequency;
    }

    private EcgRecordDTO(Builder builder){
        super(builder);
        this.ecg = builder.ecg;
        this.ecgArrhythmiaType = builder.ecgArrhythmiaType;
        this.ecgType = builder.ecgType;
        this.samplingFrequency = builder.samplingFrequency;
        this.userSymptom = builder.userSymptom;
        this.avgHeartRate = builder.avgHeartRate;
    }

}
