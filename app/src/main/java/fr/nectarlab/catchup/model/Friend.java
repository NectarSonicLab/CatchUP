package fr.nectarlab.catchup.model;

/**
 * non implante
 */

public class Friend extends Users {
    private String EMAIL;
    private String USERNAME;
    private boolean IS_NOTIFICATION_CONSUMED;
    private boolean IS_INVITE_PENDING;

    public Friend(){}

    public Friend(String email, String username, boolean isNotificationConsumed, boolean isInvitePending) {
        super(email, username);
        this.EMAIL = email;
        this.USERNAME = username;
        this.IS_NOTIFICATION_CONSUMED = isNotificationConsumed;
        this.IS_INVITE_PENDING = isInvitePending;
    }



    @Override
    public String getEMAIL() {
        return this.EMAIL;
    }

    @Override
    public String getUSERNAME() {
        return this.USERNAME;
    }

    public boolean getIS_NOTIFICATION_CONSUMED() {
        return this.IS_NOTIFICATION_CONSUMED;
    }

    public boolean getIS_INVITE_PENDING() {
        return this.IS_INVITE_PENDING;
    }
}
