package org.openmhealth.data.generator.dto;

/**
 * @ClassName Spo2DTO
 * @Description 血氧饱和
 * @Author zws
 * @Date 2021/7/7 9:34
 * @Version 1.0
 */
public class Spo2DTO {

    /*
    血氧饱和值
     */
    private Double spo2;

    /*
    时间戳
     */
    private Long timeStamp;

    /*
    必备指标
     */
    private String mField;

    /*
    是否进行氧疗
     */
    private Boolean oxygenTherapy;

    /*
    spo2测量
     */
    private String spo2Measurement;
}
