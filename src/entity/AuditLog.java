package entity;

import java.sql.Timestamp;

public class AuditLog {
    private int id;
    private String actorInfo;
    private String actionType;
    private String targetTable;
    private int targetId;
    private String actionDetails;
    private Timestamp createdAt;

    public AuditLog() {
    }

    public AuditLog(String actorInfo, String actionType, String targetTable, int targetId, String actionDetails) {
        this.actorInfo = actorInfo;
        this.actionType = actionType;
        this.targetTable = targetTable;
        this.targetId = targetId;
        this.actionDetails = actionDetails;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getActorInfo() {
        return actorInfo;
    }

    public void setActorInfo(String actorInfo) {
        this.actorInfo = actorInfo;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public String getActionDetails() {
        return actionDetails;
    }

    public void setActionDetails(String actionDetails) {
        this.actionDetails = actionDetails;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }


}
