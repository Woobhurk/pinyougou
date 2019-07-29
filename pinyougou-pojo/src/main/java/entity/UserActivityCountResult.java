package entity;

import java.io.Serializable;
import java.util.List;

public class UserActivityCountResult implements Serializable {
    private List<String> activityTime;
    private List<Integer> activityCount;

    public List<String> getActivityTime() {
        return this.activityTime;
    }

    public void setActivityTime(List<String> activityTime) {
        this.activityTime = activityTime;
    }

    public List<Integer> getActivityCount() {
        return this.activityCount;
    }

    public void setActivityCount(List<Integer> activityCount) {
        this.activityCount = activityCount;
    }

    @Override
    public String toString() {
        return "UserActivityCountResult{" +
            "activityTime=" + this.activityTime +
            ", activityCount=" + this.activityCount +
            '}';
    }
}
