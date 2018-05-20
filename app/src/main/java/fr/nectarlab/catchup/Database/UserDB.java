package fr.nectarlab.catchup.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * UserDB
 * Schema representant l'utilisateur
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

    //Constructeurs
    UserDB(){}
    UserDB(String email, String ID, String username){
        this.EMAIL = email;
        this.ID = ID;
        this.USERNAME=username;
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

}
