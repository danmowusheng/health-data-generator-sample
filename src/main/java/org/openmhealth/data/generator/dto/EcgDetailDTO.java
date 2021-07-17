package org.openmhealth.data.generator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.openmhealth.schema.serializer.SerializationConstructor;

/**
 * @ClassName EcgDetailDTO
 * @Description 心电测量明细
 * @Author zws
 * @Date 2021/7/7 9:35
 * @Version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public class EcgDetailDTO extends MeasureDTO {

    /*
    ecg类型
     */
    private Integer ecgLead;

    /*
    电压值
     */
    private Double ecgDetail;

    /*
    标识（所属ecg_record的开始时间）
    */
    private Long identifier;

    @SerializationConstructor
    protected EcgDetailDTO(){
    }

    public static class Builder extends MeasureDTO.Builder<EcgDetailDTO, Builder>{

        private Integer ecgLead;
        private Double ecgDetail;
        private Long identifier;

        public Builder (Double ecgDetail, Long identifier) {
            this.ecgDetail = ecgDetail;
            this.identifier = identifier;
        }

        public Builder setEcgLead(Integer ecgLead) {
            this.ecgLead = ecgLead;
            return this;
        }


        @Override
        public EcgDetailDTO build() {
            return new EcgDetailDTO(this);
        }
    }

    private EcgDetailDTO(Builder builder) {
        super(builder);
        this.ecgLead = builder.ecgLead;
        this.ecgDetail = builder.ecgDetail;
        this.identifier = builder.identifier;
    }

    public Integer getEcgLead() {
        return ecgLead;
    }

    public Double getEcgDetail() {
        return ecgDetail;
    }

    public Long getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "EcgDetailDTO{" +
                "ecgLead=" + ecgLead +
                ", ecgDetail=" + ecgDetail +
                ", identifier=" + identifier +
                '}';
    }
}
