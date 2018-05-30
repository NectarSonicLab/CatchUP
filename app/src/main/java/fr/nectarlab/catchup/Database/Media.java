package fr.nectarlab.catchup.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

/**
 * Media
 * Schema representant un objet Media (photo, fichier audio...) partage entre les utilisateurs
 * du meme evenement
 */
@Entity (indices = {@Index("ref_user_EMAIL")/*, @Index("ref_event_ID")},foreignKeys = {
        @ForeignKey(entity = UserDB.class,
                parentColumns = "EMAIL",
                childColumns = "ref_user_EMAIL"),
        @ForeignKey(entity = EventDB.class,
                parentColumns = "eventID",
                childColumns = "ref_event_ID")*/
            })
//indices = {@Index("ref_user_EMAIL")})

public class Media {
    @PrimaryKey
    @NonNull
    @ColumnInfo
    private String mediaID;

    @ColumnInfo
    private String timeStamp;

    @ColumnInfo
    private String contenu;

    @ColumnInfo (name = "ref_user_EMAIL")
    private String ref_user_EMAIL;

    @ColumnInfo (name = "ref_event_ID")
    private String ref_event_ID;

    //Constructeurs
    public Media(){}
    public Media(String mediaID, String timeStamp, String contenu, String usernamePK, String eventID){
        this.mediaID = mediaID;
        this.timeStamp = timeStamp;
        this.contenu = contenu;
        this.ref_user_EMAIL = usernamePK;
        this.ref_event_ID = eventID;
    }

    //Getters and Setters

    @NonNull
    public String getMediaID() {
        return mediaID;
    }

    public void setMediaID(@NonNull String mediaID) {
        this.mediaID = mediaID;
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

    public void setRef_user_EMAIL(String ref_user_USERNAME) {
        this.ref_user_EMAIL = ref_user_USERNAME;
    }

    public String getRef_event_ID() {
        return ref_event_ID;
    }

    public void setRef_event_ID(String ref_event_ID) {
        this.ref_event_ID = ref_event_ID;
    }
}
