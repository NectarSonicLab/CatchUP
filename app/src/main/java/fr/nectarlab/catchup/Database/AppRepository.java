package fr.nectarlab.catchup.Database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by ThomasPiaczinski on 12/04/18.
 */

public class AppRepository {
    private RegisteredFriendsDAO mRegisteredFriendsDAO;
    private LiveData<List<RegisteredFriendsDB>> mAllFriends;

    public AppRepository(Application application){
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        mRegisteredFriendsDAO = appDatabase.mRegisteredFriendsDAO();
        mAllFriends = mRegisteredFriendsDAO.getAllFriends();
    }

    public LiveData<List<RegisteredFriendsDB>> getAllFriends(){
        return mAllFriends;
    }

    public void insert (RegisteredFriendsDB registeredFriendsDB){
        new insertAsyncTask(mRegisteredFriendsDAO).execute(registeredFriendsDB);
    }

    private static class insertAsyncTask extends AsyncTask<RegisteredFriendsDB, Void, Void>{
        private RegisteredFriendsDAO mAsyncTaskDAO;
        insertAsyncTask(RegisteredFriendsDAO dao){
            mAsyncTaskDAO = dao;
        }
        @Override
        protected Void doInBackground(final RegisteredFriendsDB...params){
            mAsyncTaskDAO.insert(params[0]);
            return null;
        }
    }
}
