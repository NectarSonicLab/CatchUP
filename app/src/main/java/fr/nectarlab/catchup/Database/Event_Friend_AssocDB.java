package fr.nectarlab.catchup.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Event_Friend_AssocDB
 * Schema SQL representant une association
 * entre deux autres entites: Event et RegisteredFriendsDB
 */
@Entity(indices = {@Index("ref_event_ID"), @Index("ref_friend_email")}/*,foreignKeys = {
        @ForeignKey(entity = EventDB.class,
        parentColumns = "eventID",
        childColumns = "ref_event_ID"),
        @ForeignKey(entity = RegisteredFriendsDB.class,
        parentColumns = "EMAIL",
        childColumns = "ref_friend_email")
}*/)
public class Event_Friend_AssocDB {

    @PrimaryKey
    @NonNull
    private String ref_event_ID;


    @ColumnInfo(name="ref_friend_email")
    @NonNull
    private String ref_friend_email;


    //Constructeurs
    public Event_Friend_AssocDB(){}
    public Event_Friend_AssocDB(String eventID, String Friend_PK){
        this.ref_event_ID = eventID;
        this.ref_friend_email = Friend_PK;
    }

   //Getters and Setters


    @NonNull
    public String getRef_event_ID() {
        return ref_event_ID;
    }

    public void setRef_event_ID(@NonNull String ref_event_ID) {
        this.ref_event_ID = ref_event_ID;
    }

    @NonNull
    public String getRef_friend_email() {
        return ref_friend_email;
    }

    public void setRef_friend_email(@NonNull String ref_friend_email) {
        this.ref_friend_email = ref_friend_email;
    }
}
