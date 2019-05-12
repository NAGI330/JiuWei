package com.example.jiuwei.myActivity;

public class Activity {
    public String activityId;
    public String activityName;
    public String activityDescribe;
    public String activityPlace;
    public String startDate;
    public String activityType;
    public String numMax;
    public String ownId;
    public String ownName;
    public Boolean activityState;

    public String getActivityDescribe() {
        return activityDescribe;
    }

    public void setActivityDescribe(String activityDescribe) {
        this.activityDescribe = activityDescribe;
    }

    public Boolean getActivityState() {
        return activityState;
    }

    public void setActivityState(Boolean activityState) {
        this.activityState = activityState;
    }

    public String getActivityPlace() {
        return activityPlace;
    }

    public void setActivityPlace(String activityPlace) {
        this.activityPlace = activityPlace;
    }

    public String getOwnId() {
        return ownId;
    }

    public void setOwnId(String ownId) {
        this.ownId = ownId;
    }

    public String getOwnName() {
        return ownName;
    }

    public void setOwnName(String ownName) {
        this.ownName = ownName;
    }
    public String getActivityId() {
        return activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getActivityType() {
        return activityType;
    }

    public String getNumMax() {
        return numMax;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public void setNumMax(String numMax) {
        this.numMax = numMax;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "activityId='" + activityId + '\'' +
                ", activityName='" + activityName + '\'' +
                ", activityDescribe='" + activityDescribe + '\'' +
                ", activityPlace='" + activityPlace + '\'' +
                ", startDate='" + startDate + '\'' +
                ", activityType='" + activityType + '\'' +
                ", numMax='" + numMax + '\'' +
                ", ownId='" + ownId + '\'' +
                ", ownName='" + ownName + '\'' +
                '}';
    }


}
