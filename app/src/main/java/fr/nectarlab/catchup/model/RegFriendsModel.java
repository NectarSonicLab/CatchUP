package fr.nectarlab.catchup.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.util.List;

import fr.nectarlab.catchup.Database.AppRepository;
import fr.nectarlab.catchup.Database.RegisteredFriendsDB;
import fr.nectarlab.catchup.RegisteredUsersActivity_test;

/**
 * RegFriendModel
 * Objet Java faisant le lien entre la DB et l'affichage
 */

public class RegFriendsModel extends AndroidViewModel {
    private static final String TAG = "RegFriendsModel";
    private AppRepository mRepository;
    private LiveData<List<RegisteredFriendsDB>> allFriends;

    public RegFriendsModel (Application application){
        super(application);
        mRepository = new AppRepository(application);
        allFriends = mRepository.getAllFriends();
    }
    /*
     * Methode pour recuperer tous les amis enregistres dans la DB
     * sert pour l'affichage via FriendsListAdapter
     */
    public LiveData<List<RegisteredFriendsDB>> getAllFriends(){
        return allFriends;
    }

    /*
     * Methode pour inserer un nouvel ami
     */
    public void insert (RegisteredFriendsDB registeredFriendsDB){mRepository.insert(registeredFriendsDB);}

    public void getNumFriends(){
        mRepository.getNumFriendsQuery();
        //return mRepository.callBack();
    }
    public void test(){
       // mRepository.getExecuteResult(TAG);
    }
    public RegisteredUsersActivity_test getReference(){
       return mRepository.getmRegisteredUsersActivity_test();
    }

    public AppRepository getmRepository() {
        return mRepository;
    }
}
