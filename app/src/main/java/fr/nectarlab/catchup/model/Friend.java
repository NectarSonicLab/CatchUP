package fr.nectarlab.catchup.model;

/**
 * Created by ThomasPiaczinski on 03/04/18.
 */

public class Friend extends Users {
    private String Id;
    private String EMAIL;

    public Friend(){}

    public Friend(String mId, String mEmail) {
        super(mId, mEmail);
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
