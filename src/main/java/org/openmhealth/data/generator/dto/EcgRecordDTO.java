package org.openmhealth.data.generator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    private Integer ecgRecord;

    /*
    心电类型
     */
    private Integer ecgLead;

    /*
    标识（ecg_record的开始时间）
    */
    private Long identifier;

    /*
     心率失常类型
     */
    private Integer ecgArrhythmiaType;

    /*
    用户症状
     */
    private Integer userSymptom;

    /*
    取样频率
     */
    private Integer samplingFrequency;

    @SerializationConstructor
    EcgRecordDTO(){

    }

    public static class Builder extends MeasureDTO.Builder<EcgRecordDTO, Builder>{
        private Integer ecgRecord;
        private Integer ecgLead;
        private Integer ecgArrhythmiaType;
        private Integer userSymptom;
        private Integer samplingFrequency;
        private Long identifier;

        public Builder (Integer ecgRecord) {
            this.ecgRecord = ecgRecord;
        }

        public Builder setIdentifier(Long identifier){
            this.identifier = identifier;
            return this;
        }

        public Builder setSamplingFrequency(Integer samplingFrequency){
            this.samplingFrequency = samplingFrequency;
            return this;
        }

        public Builder setEcgLead(Integer ecgLead){
            this.ecgLead = ecgLead;
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


    public Integer getEcgLead() {
        return ecgLead;
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

    public Integer getEcgRecord() {
        return ecgRecord;
    }

    public Long getIdentifier() {
        return identifier;
    }

    public void setEcgRecord(Integer ecgRecord) {
        this.ecgRecord = ecgRecord;
    }

    private EcgRecordDTO(Builder builder){
        super(builder);
        this.identifier = builder.identifier;
        this.ecgArrhythmiaType = builder.ecgArrhythmiaType;
        this.ecgLead = builder.ecgLead;
        this.samplingFrequency = builder.samplingFrequency;
        this.userSymptom = builder.userSymptom;
        this.ecgRecord = builder.ecgRecord;
    }

    @Override
    public String toString() {
        return "EcgRecordDTO{" +
                "ecgRecord=" + ecgRecord +
                ", ecgLead=" + ecgLead +
                ", identifier=" + identifier +
                ", ecgArrhythmiaType=" + ecgArrhythmiaType +
                ", userSymptom=" + userSymptom +
                ", samplingFrequency=" + samplingFrequency +
                '}';
    }
}
