package fr.nectarlab.catchup.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by ThomasPiaczinski on 09/04/18.
 */
@Entity
public class UserDB {
    @PrimaryKey
    @NonNull
    private String EMAIL;

    @ColumnInfo(name="ID")
    private String ID;

    @ColumnInfo (name="USERNAME")
    private String USERNAME;
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

}
