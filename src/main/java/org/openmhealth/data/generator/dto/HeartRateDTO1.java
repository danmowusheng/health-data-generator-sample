package org.openmhealth.data.generator.dto;

import java.io.Serializable;

/**
 * @ClassName HeartRateDTO1
 * @Description 心率数据
 * @Author zws
 * @Date 2021/6/30 9:29
 * @Version 1.0
 */
public class HeartRateDTO1 implements Serializable {
    private static final long serialVersionUID = -5809782578272943999L;

    //记录Id
    private String id;
    //用户Id
    private String userId;
    //心率
    private Double heartRate;
    //时间
    private Double hRTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Double heartRate) {
        this.heartRate = heartRate;
    }

    public Double getHRTime() {
        return hRTime;
    }

    public void setHRTime(Double hRTime) {
        this.hRTime = hRTime;
    }
}
