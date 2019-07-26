package entity;

import java.io.Serializable;
import java.util.Date;

public class OrderItemChartParam implements Serializable {
    private Long parentId;
    private String sellerId;
    private Date startTime;
    private Date endTime;

    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getSellerId() {
        return this.sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
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
        return "OrderItemParam{" +
            "parentId=" + this.parentId +
            ", sellerId='" + this.sellerId + '\'' +
            ", startTime=" + this.startTime +
            ", endTime=" + this.endTime +
            '}';
    }
}
