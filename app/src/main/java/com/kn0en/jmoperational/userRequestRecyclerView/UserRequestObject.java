package com.kn0en.jmoperational.userRequestRecyclerView;

public class UserRequestObject {
    private String riderRequestId;
    private String riderName;
    private String riderRuas;


    public UserRequestObject(String riderRequestId, String riderName, String riderRuas) {
        this.riderRequestId = riderRequestId;
        this.riderName = riderName;
        this.riderRuas = riderRuas;
    }

    public String getRiderRequestId() {
        return riderRequestId;
    }

    public void setRiderRequestId(String riderRequestId) {
        this.riderRequestId = riderRequestId;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }

    public String getRiderRuas() {
        return riderRuas;
    }

    public void setRiderRuas(String riderRuas) {
        this.riderRuas = riderRuas;
    }
}
