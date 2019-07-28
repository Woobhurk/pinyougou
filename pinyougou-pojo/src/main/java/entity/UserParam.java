package entity;

import java.io.Serializable;
import java.util.Date;

public class UserParam implements Serializable {
    private String username;
    private Date startTime;
    private Date endTime;
    private String status;
    private Boolean unfrozen;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getUnfrozen() {
        return this.unfrozen;
    }

    public void setUnfrozen(Boolean unfrozen) {
        this.unfrozen = unfrozen;
    }

    @Override
    public String toString() {
        return "UserParam{" +
            "username='" + this.username + '\'' +
            ", startTime=" + this.startTime +
            ", endTime=" + this.endTime +
            ", status='" + this.status + '\'' +
            ", unfrozen=" + this.unfrozen +
            '}';
    }
}
