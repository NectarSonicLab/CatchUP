package fr.nectarlab.catchup.Database;

import android.database.DatabaseUtils;

/**
 * Created by ThomasPiaczinski on 09/04/18.
 */

public class UtilDB {
    public static void addFriend(final AppDatabase db, final String email, final String friendID,
                                 String friendUsername, String fUsername){
        FriendDB fDB = new FriendDB(email, friendID, friendUsername, fUsername);
        db.friendDao().insertNewFriend(fDB);
    }

    public long getRowCount(final AppDatabase db, String TABLE_NAME){
        return -1;
    }
}
