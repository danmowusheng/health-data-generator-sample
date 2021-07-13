package org.openmhealth.data.generator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.openmhealth.schema.serializer.SerializationConstructor;

/**
 * @ClassName LocationSampleDTO
 * @Description 地理位置
 * @Author zws
 * @Date 2021/7/7 9:34
 * @Version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public class LocationSampleDTO extends MeasureDTO{

    /*
    位置数值
     */
    private Double location;

    /*
    必备指标
     */
    private Integer mField;



    public static class Builder extends MeasureDTO.Builder<LocationSampleDTO, LocationSampleDTO.Builder>{
        private Double locationSample;
        private Integer mField;

        public Builder (Double locationSample) {
            this.locationSample = locationSample;
        }

        public Builder setmField(Integer mField) {
            this.mField = mField;
            return this;
        }

        @Override
        public LocationSampleDTO build() {
            return new LocationSampleDTO(this);
        }
    }

    private LocationSampleDTO(Builder builder){
        super(builder);
        this.mField = builder.mField;
        this.location = builder.locationSample;
    }

    @SerializationConstructor
    protected LocationSampleDTO(){
    }

    public Double getLocation() {
        return location;
    }

    public Integer getmField() {
        return mField;
    }

    @Override
    public String toString() {
        return "LocationSampleDTO{" +
                "locationSample=" + location +
                ", mField=" + mField +
                '}';
    }
}
