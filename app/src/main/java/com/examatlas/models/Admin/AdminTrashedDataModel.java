package com.examatlas.models.Admin;

public class AdminTrashedDataModel {
    String trashId,trashItemName,trashType;

    public AdminTrashedDataModel(String trashId, String trashItemName, String trashType) {
        this.trashId = trashId;
        this.trashItemName = trashItemName;
        this.trashType = trashType;
    }

    public String getTrashId() {
        return trashId;
    }

    public void setTrashId(String trashId) {
        this.trashId = trashId;
    }

    public String getTrashItemName() {
        return trashItemName;
    }

    public void setTrashItemName(String trashItemName) {
        this.trashItemName = trashItemName;
    }

    public String getTrashType() {
        return trashType;
    }

    public void setTrashType(String trashType) {
        this.trashType = trashType;
    }
}
