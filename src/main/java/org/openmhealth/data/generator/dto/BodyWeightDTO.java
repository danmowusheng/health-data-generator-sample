package org.openmhealth.data.generator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonNaming(PropertyNamingStrategy.LOWER_CAMEL_CASE)
public class BodyWeightDTO extends MeasureDTO{

    /*
    体重值
     */
    private Double bodyWeight;

    /*
    必需label,体重的类型
     */
    private Integer mField;

    @SerializationConstructor
    protected BodyWeightDTO(){
    }

    public static class Builder extends MeasureDTO.Builder<BodyWeightDTO, BodyWeightDTO.Builder>{
        private Double bodyWeight;
        private Integer mField;

        public Builder (Double bodyWeight) {
            this.bodyWeight = bodyWeight;
        }

        public Builder setmField(Integer mField) {
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

    public Double getBodyWeight() {
        return bodyWeight;
    }

    public Integer getmField() {
        return mField;
    }

    @Override
    public String toString() {
        return "BodyWeightDTO{" +
                "bodyWeight=" + bodyWeight +
                ", mField=" + mField +
                '}';
    }
}
