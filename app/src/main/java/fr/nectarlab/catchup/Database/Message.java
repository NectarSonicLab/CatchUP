package fr.nectarlab.catchup.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Message
 * Schema SQL representant un message echange entre utilisateurs au sein d'un meme evenement
 */
@Entity(indices = {@Index("ref_user_EMAIL"), @Index("ref_event_ID")},foreignKeys = {
        @ForeignKey(entity = UserDB.class,
                parentColumns = "EMAIL",
                childColumns = "ref_user_EMAIL"),
        @ForeignKey(entity = EventDB.class,
                parentColumns = "eventID",
                childColumns = "ref_event_ID")
})

public class Message {
    @PrimaryKey
    @NonNull
    @ColumnInfo
    private String messageID;

    @ColumnInfo
    private String timeStamp;

    @ColumnInfo
    private String contenu;

    @ColumnInfo (name = "ref_user_EMAIL")
    private String ref_user_EMAIL;

    @ColumnInfo (name = "ref_event_ID")
    private String ref_event_ID;


    //Constructeurs
    public Message(){}
    public Message (String mediaID, String timeStamp, String contenu, String userPK, String eventID){
        this.messageID = mediaID;
        this.timeStamp = timeStamp;
        this.contenu = contenu;
        this.ref_user_EMAIL = userPK;
        this.ref_event_ID = eventID;
    }

    //Getters and Setters

    @NonNull
    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(@NonNull String messageID) {
        this.messageID = messageID;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getRef_user_EMAIL() {
        return ref_user_EMAIL;
    }

    public void setRef_user_EMAIL(String ref_user_EMAIL) {
        this.ref_user_EMAIL = ref_user_EMAIL;
    }

    public String getRef_event_ID() {
        return ref_event_ID;
    }

    public void setRef_event_ID(String ref_event_ID) {
        this.ref_event_ID = ref_event_ID;
    }
}
