package fr.nectarlab.catchup.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by ThomasPiaczinski on 09/04/18.
 */
@Entity(foreignKeys = {
        @ForeignKey(entity = GroupDB.class,
        parentColumns = "groupID",
        childColumns = "ref_group_ID"),
        @ForeignKey(entity = FriendDB.class,
        parentColumns = "EMAIL",
        childColumns = "ref_friend_email")
})
public class Group_Friend_AssocDB {

    @PrimaryKey
    @NonNull
    private String ref_group_ID;


    @ColumnInfo(name="ref_friend_email")
    @NonNull
    private String ref_friend_email;

    @NonNull
    public String getRef_group_ID() {
        return ref_group_ID;
    }

    public void setRef_group_ID(@NonNull String ref_group_ID) {
        this.ref_group_ID = ref_group_ID;
    }

    public void setRef_friend_email(@NonNull String ref_friend_email) {
        this.ref_friend_email = ref_friend_email;
    }

    @NonNull
    public String getRef_friend_email() {
        return ref_friend_email;
    }


}
