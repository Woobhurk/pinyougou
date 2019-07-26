package entity;

import java.io.Serializable;

public class ResultInfo implements Serializable {
    private Boolean success;
    private String message;
    private Object data;

    public Boolean getSuccess() {
        return this.success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultInfo{" +
            "success=" + this.success +
            ", message='" + this.message + '\'' +
            ", data=" + this.data +
            '}';
    }
}
