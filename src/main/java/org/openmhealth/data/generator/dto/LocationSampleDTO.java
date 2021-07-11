package org.openmhealth.data.generator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.openmhealth.schema.serializer.SerializationConstructor;

/**
 * @ClassName LocationSampleDTO
 * @Description 地理位置
 * @Author zws
 * @Date 2021/7/7 9:34
 * @Version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LocationSampleDTO extends MeasureDTO{

    /*
    位置数值
     */
    private Double locationSample;

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
        this.locationSample = builder.locationSample;
    }

    @SerializationConstructor
    protected LocationSampleDTO(){
    }

    public Double getLocationSample() {
        return locationSample;
    }

    public Integer getmField() {
        return mField;
    }

    @Override
    public String toString() {
        return "LocationSampleDTO{" +
                "locationSample=" + locationSample +
                ", mField=" + mField +
                '}';
    }
}
