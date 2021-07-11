package org.openmhealth.data.generator.domain;

import org.openmhealth.data.generator.constant.HeartRate;
import org.openmhealth.data.generator.dto.*;
import org.openmhealth.data.generator.service.ECGRecordDataPointGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    String userID;
    /**
     * 健康数据
     */
    List<BodyWeightDTO> bodyWeightDTOS;
    List<CaloriesDTO> caloriesDTOS;
    List<DistanceDTO> distanceDTOS;
    List<EcgDetailDTO> ecgDetailDTOS;
    List<EcgRecordDTO> ecgRecordDTOS;
    List<HeartRateDTO> heartRateDTOS;
    List<LocationSampleDTO> locationSampleDTOS;
    List<SleepFragmentDTO> sleepFragmentDTOS;
    List<Spo2DTO> spo2DTOS;
    List<StepsDTO> stepsDTOS;
    List<StressDTO> stressDTOS;


    public JsonObject(String userID) {
        this.userID = userID;

        bodyWeightDTOS = new ArrayList<>();
        caloriesDTOS = new ArrayList<>();
        distanceDTOS = new ArrayList<>();
        ecgDetailDTOS = new ArrayList<>();
        ecgRecordDTOS = new ArrayList<>();
        heartRateDTOS = new ArrayList<>();
        locationSampleDTOS = new ArrayList<>();
        sleepFragmentDTOS = new ArrayList<>();
        spo2DTOS = new ArrayList<>();
        stepsDTOS = new ArrayList<>();
        stressDTOS = new ArrayList<>();
    }

    public void setList(String name, List<? extends MeasureDTO> DTOlist){
        switch (name){
            case "body-weight":
                this.bodyWeightDTOS = (List<BodyWeightDTO>) DTOlist;
                break;
            case "calorie-count":
                this.caloriesDTOS = (List<CaloriesDTO>) DTOlist;
                break;
            case "distance":
                this.distanceDTOS = (List<DistanceDTO>) DTOlist;
                break;
            case "ECG-detail":
                this.ecgDetailDTOS = (List<EcgDetailDTO>) DTOlist;
                break;
            case "ECG-record":
                this.ecgRecordDTOS = (List<EcgRecordDTO>) DTOlist;
                break;
            case "heart-rate":
                this.heartRateDTOS = (List<HeartRateDTO>) DTOlist;
                break;
            case "geo-position":
                this.locationSampleDTOS = (List<LocationSampleDTO>) DTOlist;
                break;
            case "sleep-duration":
                this.sleepFragmentDTOS = (List<SleepFragmentDTO>) DTOlist;
                break;
            case "step-count":
                this.stepsDTOS = (List<StepsDTO>) DTOlist;
                break;
            case "stress-detail":
                this.stressDTOS = (List<StressDTO>) DTOlist;
                break;
        }
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public List<BodyWeightDTO> getBodyWeightDTOS() {
        return bodyWeightDTOS;
    }

    public void setBodyWeightDTOS(List<BodyWeightDTO> bodyWeightDTOS) {
        this.bodyWeightDTOS = bodyWeightDTOS;
    }

    public List<CaloriesDTO> getCaloriesDTOS() {
        return caloriesDTOS;
    }

    public void setCaloriesDTOS(List<CaloriesDTO> caloriesDTOS) {
        this.caloriesDTOS = caloriesDTOS;
    }

    public List<DistanceDTO> getDistanceDTOS() {
        return distanceDTOS;
    }

    public void setDistanceDTOS(List<DistanceDTO> distanceDTOS) {
        this.distanceDTOS = distanceDTOS;
    }

    public List<EcgDetailDTO> getEcgDetailDTOS() {
        return ecgDetailDTOS;
    }

    public void setEcgDetailDTOS(List<EcgDetailDTO> ecgDetailDTOS) {
        this.ecgDetailDTOS = ecgDetailDTOS;
    }

    public List<EcgRecordDTO> getEcgRecordDTOS() {
        return ecgRecordDTOS;
    }

    public void setEcgRecordDTOS(List<EcgRecordDTO> ecgRecordDTOS) {
        this.ecgRecordDTOS = ecgRecordDTOS;
    }

    public List<HeartRateDTO> getHeartRateDTOS() {
        return heartRateDTOS;
    }

    public void setHeartRateDTOS(List<HeartRateDTO> heartRateDTOS) {
        this.heartRateDTOS = heartRateDTOS;
    }

    public List<LocationSampleDTO> getLocationSampleDTOS() {
        return locationSampleDTOS;
    }

    public void setLocationSampleDTOS(List<LocationSampleDTO> locationSampleDTOS) {
        this.locationSampleDTOS = locationSampleDTOS;
    }

    public List<SleepFragmentDTO> getSleepFragmentDTOS() {
        return sleepFragmentDTOS;
    }

    public void setSleepFragmentDTOS(List<SleepFragmentDTO> sleepFragmentDTOS) {
        this.sleepFragmentDTOS = sleepFragmentDTOS;
    }

    public List<Spo2DTO> getSpo2DTOS() {
        return spo2DTOS;
    }

    public void setSpo2DTOS(List<Spo2DTO> spo2DTOS) {
        this.spo2DTOS = spo2DTOS;
    }

    public List<StepsDTO> getStepsDTOS() {
        return stepsDTOS;
    }

    public void setStepsDTOS(List<StepsDTO> stepsDTOS) {
        this.stepsDTOS = stepsDTOS;
    }

    public List<StressDTO> getStressDTOS() {
        return stressDTOS;
    }

    public void setStressDTOS(List<StressDTO> stressDTOS) {
        this.stressDTOS = stressDTOS;
    }

    /**
     * 想捕获泛型失败
     */
    public <T> void helper(List<T> list, List<T> DTOlist){
        list.addAll(DTOlist);
    }
}
