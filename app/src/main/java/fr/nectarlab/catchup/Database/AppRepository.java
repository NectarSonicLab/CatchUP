package fr.nectarlab.catchup.Database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Query;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutionException;

import fr.nectarlab.catchup.RegisteredUsersActivity_test;


/**
 * Class AppRepository fait le lien entre la base de donnees, les entites
 * et les differentes interfaces qui effectuent les requetes.
 * Celles-ci sont de maniere generale gerees par des asyncTask
 * Created by ThomasPiaczinski on 12/04/18.
 */

public class AppRepository {
    private static final String TAG = "CatchUP_AppRepository";
    /*
     * Reference aux amis de l'utilisateur qui ont l'app
     * installee sur leur terminal
     * mRegisteredFriendsDAO est une interface, mAllFriends
     * represente la liste des objets enregistres
     */
    private RegisteredFriendsDAO mRegisteredFriendsDAO;
    private LiveData<List<RegisteredFriendsDB>> mAllFriends;
    /*
     * Reference aux evenements enregistres
     * mEventDAO est l'interface, mAllEvents
     * represente la liste des objets enregistres
     */
    private EventDAO mEventDAO;
    private LiveData<List<EventDB>> mAllEvents;
    /*
     * Reference aux medias enregistres
     * mMediaDAO est l'interface
     * mAllMedias represente la liste des Medias
     */
    private MediaDAO mMediaDAO;
    private LiveData<List<Media>> mAllMedias;

    /*
     * Reference aux medias enregistres
     * mMessageDAO est l'interface
     * mAllMessages represente la liste des messages
     */
    private MessageDAO mMessageDAO;
    private LiveData<List<Message>> mAllMessages;
    private RegisteredUsersActivity_test mRegisteredUsersActivity_test;// = new RegisteredUsersActivity_test();
    static int numFriends;
    private getNumFriendsAsyncTask.ResponseListener mRresponseListener;

    /*
     * Reference aux amis par event
     * mFriendAssoDAO est l'interface;
     */
    private Event_Friend_AssocDAO mFriendAssoDAO;
    private LiveData<List<Event_Friend_AssocDB>> mAllFriendsByEvent;



    /*
     * Constructeur public de la classe
     */

    public AppRepository(Application application){
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        this.mRegisteredFriendsDAO = appDatabase.mRegisteredFriendsDAO();
        this.mAllFriends = mRegisteredFriendsDAO.getAllFriends();
        this.mEventDAO = appDatabase.mEventDAO();
        this.mAllEvents = mEventDAO.getAllEvent();
        this.mMediaDAO = appDatabase.mMediaDAO();
        this.mAllMedias = mMediaDAO.getAllMedias();
        this.mMessageDAO = appDatabase.mMessageDAO();
        this.mAllMessages = mMessageDAO.getAllMessages();
        this.mFriendAssoDAO = appDatabase.mEvent_Friend_AssocDAO();
        this.mAllFriendsByEvent = mFriendAssoDAO.getAllFriendsInGroup();
    }

    /*
     *Inutile reference a l'interface RegisteredFriendsDAO
     */
    public RegisteredFriendsDAO getmRegisteredFriendsDAO() {
        return this.mRegisteredFriendsDAO;
    }

    /*
     * Getters pour recuperer la liste des objets amis, medias et evenements
     */

    public LiveData<List<RegisteredFriendsDB>> getAllFriends(){
        return this.mAllFriends;
    }
    public LiveData<List<EventDB>> getAllEvents(){return this.mAllEvents;}
    public LiveData<List<Media>> getAllMedias(){return this.mAllMedias;}
    public LiveData<List<Message>> getAllMessages() {return this.mAllMessages;}
    public LiveData<List<Event_Friend_AssocDB>> getmAllFriendsByEvent() {return this.mAllFriendsByEvent;}


    /*
     * Methode appelant une asynTask pour inserer un nouvel ami dans la BD
     */
    public void insert (RegisteredFriendsDB registeredFriendsDB){
        new insertAsyncTask(mRegisteredFriendsDAO).execute(registeredFriendsDB);
    }

    /*
     * Methode appelant une asynTask pour inserer un nouvel evenement
     */
    public void insertEvent(EventDB eventDB){
        new insertEventAsyncTask(mEventDAO).execute(eventDB);
    }

    /*
     *Methode appelant une AsynkTask pour inserer un nouveau Media
     */
    public void insertMedia(Media media){
        new insertMediaAsyncTask(mMediaDAO).execute(media);
    }

    /*
     * Methode appelant une AsyncTask pour inserer un nouveau Message
     */
    public void insertMessage(Message message){
        new insertMessageAsyncTask(mMessageDAO).execute(message);
    }

   /*
    * Methode appelant une AsyncTask pour inserer un nouvelle association Friend/Event
    */
   public void insertFriendEvent (Event_Friend_AssocDB eventFriendAssocDB){
       new insertAssoc(mFriendAssoDAO).execute(eventFriendAssocDB);
   }

    /*
     * Methode invoquant une asynkTask pour recuperer le nombre d'amis enregistres
     * sous forme d'un entier et le passer via une interface a l'activite appelante (RegisteredUsersActivity)
     */
    public void getNumFriendsQuery (){
       final getNumFriendsAsyncTask mGetNumFriendsAsyncTask = new getNumFriendsAsyncTask(mRegisteredFriendsDAO, mRresponseListener);
       mGetNumFriendsAsyncTask.execute();
       Log.i("getNumFriendsQuery", "numFriends "+numFriends);
    }

    /*
     * AsyncTask appelee par la methode insert()
     */
    private static class insertAsyncTask extends AsyncTask<RegisteredFriendsDB, Void, Void>{
        private RegisteredFriendsDAO mAsyncTaskDAO;
        insertAsyncTask(RegisteredFriendsDAO dao){
            this.mAsyncTaskDAO = dao;
        }
        @Override
        protected Void doInBackground(final RegisteredFriendsDB...params){
            Log.i(TAG, "insertAsyncTask doInBackground "+params[0]);
            mAsyncTaskDAO.insert(params[0]);
            return null;
        }
    }

    /*
     * AsyncTask appelee par la methode insertEvent()
     */
    private static class insertEventAsyncTask extends AsyncTask<EventDB, Void, Void>{
        private EventDAO eventDAO;
        insertEventAsyncTask(EventDAO dao){this.eventDAO = dao;}
        @Override
        protected Void doInBackground (final EventDB...params){//...params
            Log.i(TAG, "insertEventAsyncTask: Insert()" );
            eventDAO.insert(params[0]);
            return null;
        }
    }

    /*
     * AsyncTask appelee par la methode insertMedia()
     */
    private static class insertMediaAsyncTask extends AsyncTask<Media, Void, Void>{
        private MediaDAO mMediaDAO;
        insertMediaAsyncTask(MediaDAO dao){this.mMediaDAO = dao;}
        @Override
        protected Void doInBackground (final Media...params){
            mMediaDAO.insert(params[0]);
            return null;
        }
    }

    /*
     *AsyncTask appelee par la methode insertMessage()
     */
    private static class insertMessageAsyncTask extends  AsyncTask<Message, Void, Void>{
        private MessageDAO mMessageDAO;
        insertMessageAsyncTask(MessageDAO dao){this.mMessageDAO = dao;}
        @Override
        protected Void doInBackground (final Message ... params){
            mMessageDAO.insert(params[0]);
            return null;
        }
    }

    /*
     *
     */
    private static class insertAssoc extends  AsyncTask<Event_Friend_AssocDB, Void, Void>{
        private Event_Friend_AssocDAO assocDAO;
        insertAssoc (Event_Friend_AssocDAO dao){this.assocDAO = dao;}
        @Override
        protected Void doInBackground (final Event_Friend_AssocDB ... params){
            assocDAO.insert(params[0]);
            Log.i(TAG, "insertAssoc: Insert()" );
            return null;
        }
    }

    /*
     * AsyncTask appelee par la methode getNumFriendsQuery implantant l'interface
     * ResponseListener qui va passer le resultat de l'asyncTask a une activite appelante
     */
    public static class getNumFriendsAsyncTask extends AsyncTask <Integer, Void, Integer> {
        private ResponseListener listener;
        public interface ResponseListener{
            void onResponseReceive (int result);
        }

        public void recordingEvent(int result){
            Log.i(TAG, "recordingEvent "+result);
            listener.onResponseReceive(result);
        }
        private int res;
        private RegisteredFriendsDAO mAsyncTaskDAO;
        public getNumFriendsAsyncTask(RegisteredFriendsDAO dao, ResponseListener listener) {
            this.listener = listener;
            this.mAsyncTaskDAO = dao;
        }

        @Override
        protected Integer doInBackground(Integer...params) {
           int result = mAsyncTaskDAO.getNumberOfFriends();
            if (this.getStatus()== Status.RUNNING){
                Log.i(TAG, "Running");
            }
           return result;
        }
        @Override
        protected void onPostExecute(Integer result){
            res=result;
            recordingEvent(res);
            Log.i("GetNumFriendsAsyncTask", "onPostexecute: resultat: "+res);
        }
    }


    /*
     * Inutile
     */
    public RegisteredUsersActivity_test getmRegisteredUsersActivity_test() {
        return mRegisteredUsersActivity_test;
       // return RUA;
    }

    public MessageDAO getmMessageDAO() {
        return mMessageDAO;
    }

    public MediaDAO getmMediaDAO() {
        return mMediaDAO;
    }

    public Event_Friend_AssocDAO getmFriendAssoDAO() {
        return mFriendAssoDAO;
    }

    public EventDAO getmEventDAO() {
        return mEventDAO;
    }
}
