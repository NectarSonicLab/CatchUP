package fr.nectarlab.catchup.Database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
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
    private RegisteredUsersActivity_test mRegisteredUsersActivity_test;// = new RegisteredUsersActivity_test();
    static int numFriends;
    private getNumFriendsAsyncTask.ResponseListener mRresponseListener;

    /*
     * Constructeur public de la classe
     */

    public AppRepository(Application application){
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        mRegisteredFriendsDAO = appDatabase.mRegisteredFriendsDAO();
        mAllFriends = mRegisteredFriendsDAO.getAllFriends();
        mEventDAO = appDatabase.mEventDAO();
        mAllEvents = mEventDAO.getAllEvent();
    }

    /*
     *Inutile reference a l'interface RegisteredFriendsDAO
     */
    public RegisteredFriendsDAO getmRegisteredFriendsDAO() {
        return this.mRegisteredFriendsDAO;
    }

    /*
     * Getters pour recuperer la liste des objets amis et evenements
     */

    public LiveData<List<RegisteredFriendsDB>> getAllFriends(){
        return this.mAllFriends;
    }
    public LiveData<List<EventDB>> getAllEvents(){return this.mAllEvents;}

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
            eventDAO.insert(params[0]);
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
}
