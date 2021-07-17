package org.openmhealth.data.generator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.openmhealth.schema.serializer.SerializationConstructor;

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
    private Integer weightType;

    @SerializationConstructor
    protected BodyWeightDTO(){
    }

    public static class Builder extends MeasureDTO.Builder<BodyWeightDTO, BodyWeightDTO.Builder>{
        private Double bodyWeight;
        private Integer weightType;

        public Builder (Double bodyWeight) {
            this.bodyWeight = bodyWeight;
        }

        public Builder setWeightType(Integer weightType) {
            this.weightType = weightType;
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
        this.weightType = builder.weightType;
    }

    public Double getBodyWeight() {
        return bodyWeight;
    }

    public Integer getWeightType() {
        return weightType;
    }

    @Override
    public String toString() {
        return "BodyWeightDTO{" +
                "bodyWeight=" + bodyWeight +
                ", weightType=" + weightType +
                '}';
    }
}
