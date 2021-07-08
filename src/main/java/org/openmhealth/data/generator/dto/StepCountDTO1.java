package org.openmhealth.data.generator.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName t
 * @Description 计步数据
 * @Author zws
 * @Date 2021/6/29 9:59
 * @Version 1.0
 */
public class StepCountDTO1 implements Serializable {
    
    private static final long serialVersionUID = -5809782578272943999L;

    //记录Id
    private String id;
    //用户Id
    private String userId;
    //步数
    private Integer stepCount;
    //开始时间
    private Date startDateTime;
    //持续时间（单位：m)
    private Double duration;

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public Integer getStepCount() {
        return stepCount;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public double getDuration() {
        return duration;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setStepCount(Integer stepCount) {
        this.stepCount = stepCount;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }


}
