package entity;

import java.io.Serializable;
import java.util.List;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;

public class OrderResult implements Serializable {
    private TbOrder order;
    private List<TbOrderItem> orderItemList;

    public TbOrder getOrder() {
        return this.order;
    }

    public void setOrder(TbOrder order) {
        this.order = order;
    }

    public List<TbOrderItem> getOrderItemList() {
        return this.orderItemList;
    }

    public void setOrderItemList(List<TbOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    @Override
    public String toString() {
        return "OrderResult{" +
            "order=" + this.order +
            ", orderItemList=" + this.orderItemList +
            '}';
    }
}
