package fr.nectarlab.catchup.model;

/**
 * Created by ThomasPiaczinski on 03/04/18.
 */

public class Users {
    private String Id;
    private String EMAIL;
    private String username;

    public Users (String mId, String mEmail){
        this.Id = mId;
        this.EMAIL = mEmail;
    }

    public String getEmail (){
        return this.EMAIL;
    }
}
