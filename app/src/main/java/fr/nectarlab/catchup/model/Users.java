package fr.nectarlab.catchup.model;

/**
 * Created by ThomasPiaczinski on 03/04/18.
 */

public class Users {
    private String Id;
    private String email;
    private String username;

    public Users (String mId, String mEmail, String mUsername){
        this.Id = mId;
        this.email = mEmail;
        this.username = mUsername;
    }

    public String getEmail (){
        return this.email;
    }
}
