package org.openmhealth.data.generator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.openmhealth.schema.serializer.SerializationConstructor;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-16 10:01
 * @description：
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeoPositionDTO extends MeasureDTO{
    /*
    经度
     */
    private double latitude;

    /*
    纬度
     */
    private double longitude;

    /*
    精度
     */
    private Integer precision;

    @SerializationConstructor
    protected GeoPositionDTO(){
    }

    public static class Builder extends MeasureDTO.Builder<GeoPositionDTO, Builder>{
        private double latitude;
        private double longitude;
        private Integer precision;

        public Builder (double latitude, double longitude){
            this.latitude = latitude;
            this.longitude = longitude;
        }


        public Builder setPrecision(Integer precision){
            this.precision = precision;
            return this;
        }

        @Override
        public GeoPositionDTO build() {
            return new GeoPositionDTO(this);
        }
    }

    private GeoPositionDTO(Builder builder){
        super(builder);
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.precision = builder.precision;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Integer getPrecision() {
        return precision;
    }

    @Override
    public String toString() {
        return "GeoPositionDTO{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", precision=" + precision +
                '}';
    }
}
