package entity;

import java.io.Serializable;
import java.util.List;

public class ScatterChartData implements Serializable {
    List<Object[]> dataList;

    public List<Object[]> getDataList() {
        return this.dataList;
    }

    public void setDataList(List<Object[]> dataList) {
        this.dataList = dataList;
    }

    @Override
    public String toString() {
        return "ScatterChartData{" +
            "dataList=" + this.dataList +
            '}';
    }
}
