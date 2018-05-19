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
 * Created by ThomasPiaczinski on 12/04/18.
 */

public class AppRepository {
    private static final String TAG = "CatchUP_AppRepository";
    private RegisteredFriendsDAO mRegisteredFriendsDAO;
    private RegisteredUsersActivity_test mRegisteredUsersActivity_test;// = new RegisteredUsersActivity_test();
    private LiveData<List<RegisteredFriendsDB>> mAllFriends;
    static int numFriends;
    private getNumFriendsAsyncTask.ResponseListener mRresponseListener;


    public AppRepository(Application application){
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        mRegisteredFriendsDAO = appDatabase.mRegisteredFriendsDAO();
        mAllFriends = mRegisteredFriendsDAO.getAllFriends();
    }

    public RegisteredFriendsDAO getmRegisteredFriendsDAO() {
        return mRegisteredFriendsDAO;
    }

    public LiveData<List<RegisteredFriendsDB>> getAllFriends(){
        return mAllFriends;
    }

    public void insert (RegisteredFriendsDB registeredFriendsDB){
        new insertAsyncTask(mRegisteredFriendsDAO).execute(registeredFriendsDB);
    }


    public void getNumFriendsQuery (){
       final getNumFriendsAsyncTask mGetNumFriendsAsyncTask = new getNumFriendsAsyncTask(mRegisteredFriendsDAO, mRresponseListener);
       mGetNumFriendsAsyncTask.execute();
       Log.i("getNumFriendsQuery", "numFriends "+numFriends);
    }

    private static class insertAsyncTask extends AsyncTask<RegisteredFriendsDB, Void, Void>{
        private RegisteredFriendsDAO mAsyncTaskDAO;
        insertAsyncTask(RegisteredFriendsDAO dao){
            mAsyncTaskDAO = dao;
        }
        @Override
        protected Void doInBackground(final RegisteredFriendsDB...params){
            Log.i(TAG, "insertAsyncTask doInBackground "+params[0]);
            mAsyncTaskDAO.insert(params[0]);
            return null;
        }
    }

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
    public RegisteredUsersActivity_test getmRegisteredUsersActivity_test() {
        return mRegisteredUsersActivity_test;
       // return RUA;
    }
}
