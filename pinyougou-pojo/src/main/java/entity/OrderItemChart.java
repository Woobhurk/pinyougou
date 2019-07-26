package entity;

import java.io.Serializable;
import java.util.List;

public class OrderItemChart implements Serializable {
    private Long itemCatId;
    private String name;
    private Double value;
    private List<OrderItemChart> children;

    public Long getItemCatId() {
        return this.itemCatId;
    }

    public void setItemCatId(Long itemCatId) {
        this.itemCatId = itemCatId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return this.value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public List<OrderItemChart> getChildren() {
        return this.children;
    }

    public void setChildren(List<OrderItemChart> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "OrderItemChart{" +
            "itemCatId=" + this.itemCatId +
            ", name='" + this.name + '\'' +
            ", value=" + this.value +
            ", children=" + this.children +
            '}';
    }
}
