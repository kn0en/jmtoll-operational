package com.kn0en.jmoperational.officerAvailableRecyclerView;

public class OfficerAvailObject {
    private String officerAvailableId,officerName,officerRuas,officerDistance;

    public OfficerAvailObject(String officerAvailableId, String officerName, String officerRuas, String officerDistance) {
        this.officerAvailableId = officerAvailableId;
        this.officerName = officerName;
        this.officerRuas = officerRuas;
        this.officerDistance = officerDistance;
    }

    public String getOfficerAvailableId() {
        return officerAvailableId;
    }

    public void setOfficerAvailableId(String officerAvailableId) {
        this.officerAvailableId = officerAvailableId;
    }

    public String getOfficerName() {
        return officerName;
    }

    public void setOfficerName(String officerName) {
        this.officerName = officerName;
    }

    public String getOfficerRuas() {
        return officerRuas;
    }

    public void setOfficerRuas(String officerRuas) {
        this.officerRuas = officerRuas;
    }

    public String getOfficerDistance() {
        return officerDistance;
    }

    public void setOfficerDistance(String officerDistance) {
        this.officerDistance = officerDistance;
    }
}
