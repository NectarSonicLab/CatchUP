package fr.nectarlab.catchup;

/**
 * Created by ThomasBene on 5/20/2018.
 */

public class IntentUtils {
    private static String EventAdapter_CurrentObject = "CURRENT_EVENT";
    private static String EventAdapter_displayName = "DISPLAY_NAME";
    private static String EventAdapter_displayDate = "DISPLAY_DATE";
    private static String EventAdapter_DisplayEventType = "DISPLAY_EVENT_TYPE";
    private final static int NOTIFICATION_SENDER_REQ_CODE=0;
    private final static String NOTIFICATION_SENDER_INTENT_KEY="INVITATION";
    private final static String PENDING_INTENT_EVENT_KEY="PENDING_INTENT_EVENT_KEY";
    public final static int PICK_CONTACT_PHOTO= 1;

    public static String getEventAdapter_displayName() {
        return EventAdapter_displayName;
    }

    public static String getEventAdapter_displayDate() {
        return EventAdapter_displayDate;
    }

    public static String getEventAdapter_DisplayEventType() {
        return EventAdapter_DisplayEventType;
    }

    public static String getEventAdapter_CurrentObject() {
        return EventAdapter_CurrentObject;
    }

    public static int getNOTIFICATION_SENDER_REQ_CODE() {
        return NOTIFICATION_SENDER_REQ_CODE;
    }

    public static String getNotificationSenderIntentKey() {
        return NOTIFICATION_SENDER_INTENT_KEY;
    }

    public static String getPendingIntentEventKey() {
        return PENDING_INTENT_EVENT_KEY;
    }

    public static int getPickContactPhoto() {
        return PICK_CONTACT_PHOTO;
    }
}
