package org.openmhealth.data.generator.dto;

/**
 * @ClassName EcgRecordDTO
 * @Description 心电测量记录
 * @Author zws
 * @Date 2021/7/7 9:35
 * @Version 1.0
 */
public class EcgRecordDTO {

    /*
    心电
     */
    private Double ecg;

    /*
    时间戳
     */
    private Long timeStamp;

    /*
    心电类型
     */
    private Integer ecgType;

    /*
    心率类型
     */
    private Integer ecgArrhythmiaType;

    /*
    用户症状
     */
    private Integer userSymptom;

    /*
    示例频率
     */
    private Integer samplingFrequency;



}
