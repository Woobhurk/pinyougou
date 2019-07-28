package entity;

import java.io.Serializable;
import java.util.List;

public class UserStatisticsResult implements Serializable {
    private List<String> horizontalDataList;
    private List<Integer> verticalDataList;

    public List<String> getHorizontalDataList() {
        return this.horizontalDataList;
    }

    public void setHorizontalDataList(List<String> horizontalDataList) {
        this.horizontalDataList = horizontalDataList;
    }

    public List<Integer> getVerticalDataList() {
        return this.verticalDataList;
    }

    public void setVerticalDataList(List<Integer> verticalDataList) {
        this.verticalDataList = verticalDataList;
    }

    @Override
    public String toString() {
        return "UserStatisticsResult{" +
            "horizontalDataList=" + this.horizontalDataList +
            ", verticalDataList=" + this.verticalDataList +
            '}';
    }
}
