package org.openmhealth.data.generator.additionalMeasure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.openmhealth.data.generator.constant.StressMeasureType;
import org.openmhealth.schema.domain.omh.Measure;
import org.openmhealth.schema.domain.omh.SchemaId;
import org.openmhealth.schema.serializer.SerializationConstructor;


import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-10 10:38
 * @description：
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Stress extends Measure {

    public static final SchemaId SCHEMA_ID = new SchemaId(OMH_NAMESPACE, "body-height", "1.0");

    private Integer stress;
    private Integer grade;
    private StressMeasureType stressMeasureType;

    @SerializationConstructor
    protected Stress(){
    }

    public static class Builder extends Measure.Builder<Stress, Stress.Builder>{

        private Integer stress;
        private Integer grade;
        private StressMeasureType stressMeasureType;

        public Builder setStress(int stress){
            checkNotNull(stress, "A stress hasn't been specified.");
            this.stress = stress;
            return this;
        }

        public Builder setGrade(int grade){
            checkNotNull(grade, "A stress grade hasn't been specified.");
            this.grade = grade;
            return this;
        }

        public Builder setStressMeasureType(StressMeasureType stressMeasureType){
            checkNotNull(stressMeasureType, "A stress measureType hasn't been specified.");
            this.stressMeasureType = stressMeasureType;
            return this;
        }

        @Override
        public Stress build() {
            return new Stress(this);
        }
    }

    private Stress(Builder builder){
        super(builder);
        this.stress = builder.stress;
        this.grade = builder.grade;
        this.stressMeasureType = builder.stressMeasureType;
    }

    @Override
    public SchemaId getSchemaId() {
        return SCHEMA_ID;
    }


    @Override
    public Optional<Object> getAdditionalProperty(String name) {
        return Optional.empty();
    }
}
