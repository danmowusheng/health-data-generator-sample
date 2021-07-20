package org.openmhealth.data.generator.domain;

import org.openmhealth.data.generator.dto.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-11 10:53
 * @description：
 **/
public class JsonObject {
    /**
     * 用户名
     */
    String userId;
    /**
     * 健康数据
     */
    List<BodyWeightDTO> bodyWeight;
    List<CaloriesDTO> calories;
    List<DistanceDTO> distance;
    List<EcgDetailDTO> ecgDetail;
    List<EcgRecordDTO> ecgRecord;
    List<HeartRateDTO> heartRate;
    List<LocationSampleDTO> locationSample;
    List<SleepDurationDTO> sleepFragment;
    List<Spo2DTO> spo2;
    List<StepsDTO> steps;
    List<StressDTO> stress;


    public JsonObject(String userId) {
        this.userId = userId;

        bodyWeight = new ArrayList<>();
        calories = new ArrayList<>();
        distance = new ArrayList<>();
        ecgDetail = new ArrayList<>();
        ecgRecord = new ArrayList<>();
        heartRate = new ArrayList<>();
        locationSample = new ArrayList<>();
        sleepFragment = new ArrayList<>();
        spo2 = new ArrayList<>();
        steps = new ArrayList<>();
        stress = new ArrayList<>();
    }

    public void setList(String name, List<? extends MeasureDTO> DTOlist){
        switch (name){
            case "body-weight":
                this.bodyWeight = (List<BodyWeightDTO>) DTOlist;
                break;
            case "calorie-count":
                this.calories = (List<CaloriesDTO>) DTOlist;
                break;
            case "distance":
                this.distance = (List<DistanceDTO>) DTOlist;
                break;
            case "ECG-detail":
                this.ecgDetail = (List<EcgDetailDTO>) DTOlist;
                break;
            case "ECG-record":
                this.ecgRecord = (List<EcgRecordDTO>) DTOlist;
                break;
            case "heart-rate":
                this.heartRate = (List<HeartRateDTO>) DTOlist;
                break;
            case "geo-detail":
                this.locationSample = (List<LocationSampleDTO>) DTOlist;
                break;
            case "sleep-duration":
                this.sleepFragment = (List<SleepDurationDTO>) DTOlist;
                break;
            case "step-count":
                this.steps = (List<StepsDTO>) DTOlist;
                break;
            case "stress-detail":
                this.stress = (List<StressDTO>) DTOlist;
                break;
            case "oxygen-saturation":
                this.spo2 = (List<Spo2DTO>) DTOlist;
                break;
            default:
                throw new RuntimeException("没有这个name对应列表！！！！");
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<BodyWeightDTO> getBodyWeight() {
        return bodyWeight;
    }

    public void setBodyWeight(List<BodyWeightDTO> bodyWeight) {
        this.bodyWeight = bodyWeight;
    }

    public List<CaloriesDTO> getCalories() {
        return calories;
    }

    public void setCalories(List<CaloriesDTO> calories) {
        this.calories = calories;
    }

    public List<DistanceDTO> getDistance() {
        return distance;
    }

    public void setDistance(List<DistanceDTO> distance) {
        this.distance = distance;
    }

    public List<EcgDetailDTO> getEcgDetail() {
        return ecgDetail;
    }

    public void setEcgDetail(List<EcgDetailDTO> ecgDetail) {
        this.ecgDetail = ecgDetail;
    }

    public List<EcgRecordDTO> getEcgRecord() {
        return ecgRecord;
    }

    public void setEcgRecord(List<EcgRecordDTO> ecgRecord) {
        this.ecgRecord = ecgRecord;
    }

    public List<HeartRateDTO> getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(List<HeartRateDTO> heartRate) {
        this.heartRate = heartRate;
    }

    public List<LocationSampleDTO> getLocationSample() {
        return locationSample;
    }

    public void setLocationSample(List<LocationSampleDTO> locationSample) {
        this.locationSample = locationSample;
    }

    public List<SleepDurationDTO> getSleepFragment() {
        return sleepFragment;
    }

    public void setSleepFragment(List<SleepDurationDTO> sleepFragment) {
        this.sleepFragment = sleepFragment;
    }

    public List<Spo2DTO> getSpo2() {
        return spo2;
    }

    public void setSpo2(List<Spo2DTO> spo2) {
        this.spo2 = spo2;
    }

    public List<StepsDTO> getSteps() {
        return steps;
    }

    public void setSteps(List<StepsDTO> steps) {
        this.steps = steps;
    }

    public List<StressDTO> getStress() {
        return stress;
    }

    public void setStress(List<StressDTO> stress) {
        this.stress = stress;
    }

    /**
     * 想捕获泛型失败
     */
    public <T> void helper(List<T> list, List<T> DTOlist){
        list.addAll(DTOlist);
    }
}
