package fr.nectarlab.catchup.Database;

/**
 * Non implante
 * Created by ThomasPiaczinski on 09/04/18.
 */

public class DB {
    private static final DB ourInstance = new DB();

    public static DB getInstance() {
        return ourInstance;
    }

    private DB() {
    }
}
