package fr.nectarlab.catchup.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by ThomasPiaczinski on 06/04/18.
 */
@Entity (foreignKeys = {
        @ForeignKey(entity = UserDB.class,
        parentColumns = "EMAIL",
        childColumns = "user_email"),
        @ForeignKey(entity = GroupDB.class,
        parentColumns = "groupID",
        childColumns = "ref_group_ID")
})
public class FriendDB {

    @PrimaryKey @NonNull
    private String EMAIL;

    @ColumnInfo(name="ID")
    private String ID;

    @ColumnInfo (name="USERNAME")
    private String USERNAME;

    @ColumnInfo(name="user_email")
    private String userEMAIL;

    @ColumnInfo(name="ref_group_ID")
    private String refGroupID;

    public FriendDB(){}

    //POJO
    public FriendDB (String EMAIL,String ID, String USERNAME, String userEMAIL, String refGroupID){
        this.EMAIL = EMAIL;
        this.ID = ID;
        this.USERNAME = USERNAME;
        this.userEMAIL = userEMAIL;
        this.refGroupID = refGroupID;
    }
    //getters and setters
    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getUserEMAIL() {
        return userEMAIL;
    }

    public String getRefGroupID() {
        return refGroupID;
    }

    public void setUserEMAIL(String userEMAIL) {
        this.userEMAIL = userEMAIL;
    }

    public void setRefGroupID(String refGroupID) {
        this.refGroupID = refGroupID;
    }
}
