package fr.nectarlab.catchup.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by ThomasPiaczinski on 12/04/18.
 */
@Entity
public class RegisteredFriendsDB {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "EMAIL")
    private String EMAIL;

    @ColumnInfo (name="USERNAME")
    private String USERNAME;

    public RegisteredFriendsDB(){}
    public RegisteredFriendsDB(String EMAIL, String USERNAME){
        this.EMAIL=EMAIL;
        this.USERNAME = USERNAME;
    }


    @NonNull
    public String getEMAIL() {
        return this.EMAIL;
    }
    public void setEMAIL(@NonNull String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }
}
