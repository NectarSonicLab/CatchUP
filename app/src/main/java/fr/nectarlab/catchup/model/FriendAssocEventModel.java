package fr.nectarlab.catchup.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

import fr.nectarlab.catchup.Database.AppRepository;
import fr.nectarlab.catchup.Database.Event_Friend_AssocDB;

/**
 * Created by ThomasBene on 6/12/2018.
 */

public class FriendAssocEventModel extends AndroidViewModel {
    private AppRepository mRepository;
    private LiveData<List<Event_Friend_AssocDB>> allFriendsByEvent;
    public FriendAssocEventModel (Application application){
        super(application);
        this.mRepository = new AppRepository(application);
        allFriendsByEvent = mRepository.getmAllFriendsByEvent();
    }

    public void insert (Event_Friend_AssocDB eventFriendAssocDB){mRepository.insertFriendEvent(eventFriendAssocDB);}

    public AppRepository getmRepository() {
        return mRepository;
    }

    public LiveData<List<Event_Friend_AssocDB>> getAllFriendsByEvent() {
        return allFriendsByEvent;
    }
}
