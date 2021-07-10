package org.openmhealth.data.generator.dto;

import org.openmhealth.data.generator.constant.BodyWeight;
import org.openmhealth.schema.serializer.SerializationConstructor;

import java.sql.BatchUpdateException;

/**
 * @ClassName BodyWeightDTO
 * @Description 体重数据
 * @Author zws
 * @Date 2021/7/7 9:34
 * @Version 1.0
 */
public class BodyWeightDTO extends MeasureDTO{

    /*
    体重值
     */
    private Double bodyWeight;

    /*
    必需label,体重的类型
     */
    private BodyWeight mField;

    @SerializationConstructor
    protected BodyWeightDTO(){
    }
    public static class Builder extends MeasureDTO.Builder<BodyWeightDTO, BodyWeightDTO.Builder>{
        private Double bodyWeight;
        private BodyWeight mField;

        public Builder setBodyWeight(Double bodyWeight) {
            this.bodyWeight = bodyWeight;
            return this;
        }

        public Builder setmField(BodyWeight mField) {
            this.mField = mField;
            return this;
        }

        @Override
        public BodyWeightDTO build() {
            return new BodyWeightDTO(this);
        }
    }

    public BodyWeightDTO(Builder builder) {
        super(builder);
        this.bodyWeight = builder.bodyWeight;
        this.mField = builder.mField;
    }

    @Override
    public String toString() {
        return "BodyWeightDTO{" +
                "bodyWeight=" + bodyWeight +
                ", mField=" + mField +
                '}';
    }
}
