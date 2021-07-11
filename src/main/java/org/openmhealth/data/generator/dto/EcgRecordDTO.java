package org.openmhealth.data.generator.dto;

import org.openmhealth.schema.serializer.SerializationConstructor;

/**
 * @ClassName EcgRecordDTO
 * @Description 心电测量记录
 * @Author zws
 * @Date 2021/7/7 9:35
 * @Version 1.0
 */
public class EcgRecordDTO extends MeasureDTO{

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
        private Integer ecgType;
        private Double ecg;
        private Integer ecgArrhythmiaType;
        private Integer userSymptom;
        private Integer samplingFrequency;

        public Builder (Double ecg, Integer samplingFrequency) {
            this.ecg = ecg;
            this.samplingFrequency = samplingFrequency;
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
    }

}
