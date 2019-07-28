package entity;

import java.io.Serializable;
import java.util.Date;

public class UserStatisticsParam implements Serializable {
    private Date startTime;
    private Date endTime;
    private Integer timeDelta;

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

    public Integer getTimeDelta() {
        return this.timeDelta;
    }

    public void setTimeDelta(Integer timeDelta) {
        this.timeDelta = timeDelta;
    }

    @Override
    public String toString() {
        return "UserStatisticsParam{" +
            "startTime=" + this.startTime +
            ", endTime=" + this.endTime +
            ", timeDelta=" + this.timeDelta +
            '}';
    }
}
