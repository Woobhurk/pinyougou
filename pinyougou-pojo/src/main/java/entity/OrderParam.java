package entity;

import java.io.Serializable;
import java.util.Date;

public class OrderParam implements Serializable {
    private Long categoryId;
    private String title;
    private String status;
    private Date startTime;
    private Date endTime;

    public Long getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    @Override
    public String toString() {
        return "OrderParam{" +
            "categoryId=" + this.categoryId +
            ", title='" + this.title + '\'' +
            ", status='" + this.status + '\'' +
            ", startTime=" + this.startTime +
            ", endTime=" + this.endTime +
            '}';
    }
}
