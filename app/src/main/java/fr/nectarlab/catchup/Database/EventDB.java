package fr.nectarlab.catchup.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import java.util.Date;

/**
 * Created by ThomasPiaczinski on 09/04/18.
 */
@Entity (foreignKeys = {
        @ForeignKey(entity = GroupDB.class,
        parentColumns = "groupID",
        childColumns = "ref_groupID")
})
public class EventDB {
    @PrimaryKey
    @NonNull
    private String eventID;

    @ColumnInfo
    private String eventType;

    @ColumnInfo(name="ref_groupID")
    private String ref_groupeID;

    //private Date startTime;



    //Getters qnd Setters
    @NonNull
  public String getEventID() {
        return eventID;
    }

    public void setEventID(@NonNull String eventID) {
        this.eventID = eventID;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getRef_groupeID() {
        return ref_groupeID;
    }

    public void setRef_groupeID(String ref_groupeID) {
        this.ref_groupeID = ref_groupeID;
    }

    /*public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }*/
}
