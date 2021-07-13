package org.openmhealth.data.generator.additionalMeasure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.openmhealth.schema.domain.omh.Measure;
import org.openmhealth.schema.domain.omh.SchemaId;
import org.openmhealth.schema.serializer.SerializationConstructor;

import java.util.Optional;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-12 09:15
 * @description：
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ECGRecord extends Measure {
    private Double ecg;

    private Integer ecgType;

    private Integer ecgArrhythmiaType;

    private Integer userSymptom;

    private Integer samplingFrequency;

    public static final SchemaId SCHEMA_ID = new SchemaId(OMH_NAMESPACE, "ECG-record", "1.0");

    @SerializationConstructor
    protected ECGRecord(){
    }

    public static class Builder extends Measure.Builder<ECGRecord, Builder>{
        private Double ecg;
        private Integer ecgType;
        private Integer ecgArrhythmiaType;
        private Integer userSymptom;
        private Integer samplingFrequency;

        public Builder(Double ecg, Integer samplingFrequency){
            this.ecg = ecg;
            this.samplingFrequency = samplingFrequency;
        }

        public Builder setEcgType(Integer ecgType){
            this.ecgType = ecgType;
            return this;
        }

        public Builder setEcgArrhythmiaType(Integer ecgArrhythmiaType){
            this.ecgArrhythmiaType = ecgArrhythmiaType;
            return this;
        }

        public Builder setUserSymptom(Integer userSymptom){
            this.userSymptom = userSymptom;
            return this;
        }

        @Override
        public ECGRecord build() {
            return new ECGRecord(this);
        }
    }

    private ECGRecord(Builder builder){
        super(builder);
        this.ecg = builder.ecg;
        this.ecgArrhythmiaType = builder.ecgArrhythmiaType;
        this.ecgType = builder.ecgType;
        this.samplingFrequency = builder.samplingFrequency;
        this.userSymptom = builder.userSymptom;
    }

    @Override
    public SchemaId getSchemaId() {
        return SCHEMA_ID;
    }

    @Override
    public void setAdditionalProperty(String path, Object value) {

    }

    @Override
    public Optional<Object> getAdditionalProperty(String name) {
        return Optional.empty();
    }
}
