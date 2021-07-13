package org.openmhealth.data.generator.additionalMeasure;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.openmhealth.schema.domain.omh.Measure;
import org.openmhealth.schema.domain.omh.SchemaId;
import org.openmhealth.schema.serializer.SerializationConstructor;

import java.util.Optional;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-12 09:09
 * @description：
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ECGDetail extends Measure {
    public static final SchemaId SCHEMA_ID = new SchemaId(OMH_NAMESPACE, "ECG-detail", "1.0");

    private Integer mField;

    private Double voltageDatas;

    @SerializationConstructor
    protected ECGDetail(){
    }

    public static class Builder extends Measure.Builder<ECGDetail, Builder>{
        private Integer mField;

        private Double voltageDatas;

        public Builder(Double voltageDatas){
            this.voltageDatas = voltageDatas;
        }

        public Builder setmField(Integer mField){
            this.mField = mField;
            return this;
        }

        @Override
        public ECGDetail build() {
            return new ECGDetail(this);
        }
    }

    private ECGDetail(Builder builder){
        super(builder);
        this.mField = builder.mField;
        this.voltageDatas = builder.voltageDatas;
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
