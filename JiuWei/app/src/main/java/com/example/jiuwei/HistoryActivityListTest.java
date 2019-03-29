package com.example.jiuwei;


/*
 * 用来模拟数据库 给 myactivity History 提供数据
 * 后续建表完成后删除即可
 */




public class HistoryActivityListTest {
    private String activityName;
    private String startDate;

    public String getActivityName() {
        return activityName;
    }
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    @Override
    public String toString() {
        return "Activity [activityName=" + activityName + ", startDate=" + startDate + "]";
    }


}
