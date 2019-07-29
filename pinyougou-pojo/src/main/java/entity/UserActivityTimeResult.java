package entity;

import java.io.Serializable;

public class UserActivityTimeResult implements Serializable {
    private Long date;
    private Long hour;

    public Long getDate() {
        return this.date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getHour() {
        return this.hour;
    }

    public void setHour(Long hour) {
        this.hour = hour;
    }

    @Override
    public String toString() {
        return "UserActivityTimeResult{" +
            "date=" + this.date +
            ", hour=" + this.hour +
            '}';
    }
}
