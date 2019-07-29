package entity;

import java.io.Serializable;
import java.util.Date;

public class UserActivityResult implements Serializable {
    private String username;
    private String phone;
    private Date lastLoginTime;
    private Integer activityRate;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getLastLoginTime() {
        return this.lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Integer getActivityRate() {
        return this.activityRate;
    }

    public void setActivityRate(Integer activityRate) {
        this.activityRate = activityRate;
    }

    @Override
    public String toString() {
        return "UserActivityResult{" +
            "username='" + this.username + '\'' +
            ", phone='" + this.phone + '\'' +
            ", lastLoginTime=" + this.lastLoginTime +
            ", activityRate=" + this.activityRate +
            '}';
    }
}
