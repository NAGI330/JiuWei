package com.example.jiuwei.myActivity;

public class Activity {
    public String activityId;
    public String activityName;
    public String startDate;
    public String activityType;
    public String numMax;

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
                ", startDate='" + startDate + '\'' +
                ", activityType='" + activityType + '\'' +
                ", numMax='" + numMax + '\'' +
                '}';
    }
}
