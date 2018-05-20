package fr.nectarlab.catchup.model;

/**
 * non implante
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
