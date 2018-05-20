package fr.nectarlab.catchup.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Schema representant un groupe, finalement remplace par event, peut etre efface
 */
@Entity (foreignKeys = {
        @ForeignKey(entity = UserDB.class,
        parentColumns = "EMAIL",
        childColumns = "adminEMAIL"),
        @ForeignKey(entity = EventDB.class,
        parentColumns = "eventID",
        childColumns = "ref_eventID")
})
public class GroupDB {

    @PrimaryKey
    @NonNull
    private String groupID;

    @ColumnInfo
    private String adminEMAIL;

    @ColumnInfo
    private String groupName;

    @ColumnInfo
    private String ref_eventID;

    @NonNull
    public String getGroupID() {
        return groupID;
    }

    public String getAdminEMAIL() {
        return adminEMAIL;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getRef_eventID() {
        return ref_eventID;
    }

    public void setGroupID(@NonNull String groupID) {
        this.groupID = groupID;
    }

    public void setAdminEMAIL(String adminEMAIL) {
        this.adminEMAIL = adminEMAIL;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setRef_eventID(String ref_eventID) {
        this.ref_eventID = ref_eventID;
    }
}
