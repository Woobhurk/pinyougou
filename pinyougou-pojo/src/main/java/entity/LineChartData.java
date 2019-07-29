package entity;

import java.io.Serializable;
import java.util.List;

public class LineChartData<H, V> implements Serializable {
    private List<H> horizontalDataList;
    private List<V> verticalDataList;

    public List<H> getHorizontalDataList() {
        return this.horizontalDataList;
    }

    public void setHorizontalDataList(List<H> horizontalDataList) {
        this.horizontalDataList = horizontalDataList;
    }

    public List<V> getVerticalDataList() {
        return this.verticalDataList;
    }

    public void setVerticalDataList(List<V> verticalDataList) {
        this.verticalDataList = verticalDataList;
    }

    @Override
    public String toString() {
        return "LineChartData{" +
            "horizontalDataList=" + this.horizontalDataList +
            ", verticalDataList=" + this.verticalDataList +
            '}';
    }
}
