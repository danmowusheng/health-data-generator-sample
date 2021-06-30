package org.openmhealth.data.generator.domain;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-06-20 21:37
 **/
public class DataGenerationRequest {
    //开始时间
    private OffsetDateTime startDateTime;
    //结束时间
    private OffsetDateTime endDateTime;
    //时间间隔
    private Duration meanInterPointDuration;
    //是否检测夜间
    private Boolean suppressNightTimeMeasures = false;
    List<MeasureGenerationRequest> measureGenerationRequests;

    public OffsetDateTime getStartDateTime() {
        return startDateTime;
    }

    public OffsetDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setStartDateTime(OffsetDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setEndDateTime(OffsetDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setMeanInterPointDuration(Duration meanInterPointDuration) {
        this.meanInterPointDuration = meanInterPointDuration;
    }

    public void setSuppressNightTimeMeasures(Boolean suppressNightTimeMeasures) {
        this.suppressNightTimeMeasures = suppressNightTimeMeasures;
    }

    public List<MeasureGenerationRequest> getMeasureGenerationRequests() {
        return measureGenerationRequests;
    }

    public void setMeasureGenerationRequests(List<MeasureGenerationRequest> measureGenerationRequests) {
        this.measureGenerationRequests = measureGenerationRequests;
    }
}
