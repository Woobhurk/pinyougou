package entity;

import java.io.Serializable;
import java.util.Date;

public class UserActivityParam implements Serializable {
    private Date startTime;
    private Date endTime;
    private Integer timeUnit;

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getTimeUnit() {
        return this.timeUnit;
    }

    public void setTimeUnit(Integer timeUnit) {
        this.timeUnit = timeUnit;
    }

    @Override
    public String toString() {
        return "UserActivityParam{" +
            "startTime=" + this.startTime +
            ", endTime=" + this.endTime +
            ", timeUnit=" + this.timeUnit +
            '}';
    }
}
