package fr.nectarlab.catchup.server_side;

/**
 * ServerUtil
 */

public class ServerUtil {
    private static final String FirebaseServer_Event = "EVENT";
    private static final String FirebaseServer_Event_Friend_Asso="FRIENDS LISTED BY EVENTS";
    private static final String MEDIA ="MEDIA";
    private static final String ref_event_ID="ref_event_ID";
    private static final String ref_friend_email= "ref_friend_email";
    private static final String EVENT_ID="eventID";
    private static final String EMAIL="admin";
    private static final String FRIEND_EMAIL="FRIEND_EMAIL";
    private static final String EVENT_TYPE="eventType";
    private static final String EVENT_NAME="eventName";
    private static final String DATE="date";
    private static final String DEBUT_TIME="debutTime";
    private static final String LOCATION="location";

    public static String getFirebaseServer_Event() {
        return FirebaseServer_Event;
    }

    public static String getEventId() {
        return EVENT_ID;
    }

    public static String getEMAIL() {
        return EMAIL;
    }

    public static String getEventType() {
        return EVENT_TYPE;
    }

    public static String getEventName() {
        return EVENT_NAME;
    }

    public static String getDATE() {
        return DATE;
    }

    public static String getDebutTime() {
        return DEBUT_TIME;
    }

    public static String getLOCATION() {
        return LOCATION;
    }

    public static String getFirebaseServer_Event_Friend_Asso() {
        return FirebaseServer_Event_Friend_Asso;
    }

    public static String getFriendEmail() {
        return FRIEND_EMAIL;
    }

    public static String getRef_event_ID() {
        return ref_event_ID;
    }

    public static String getRef_friend_email() {
        return ref_friend_email;
    }

    public static String getMEDIA() {
        return MEDIA;
    }
}
