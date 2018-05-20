package fr.nectarlab.catchup.model;

/**
 * Non implante
 */

public class Users {
    private String Id;
    private String EMAIL;
    private String username;

    public Users (){}

    public Users (String mId, String mEmail){
        this.Id = mId;
        this.EMAIL = mEmail;
    }

    public String getEmail (){
        return this.EMAIL;
    }

    public String getId(){
        return this.Id;
    }
}
