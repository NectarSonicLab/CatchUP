package fr.nectarlab.catchup.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;

import java.util.List;

import fr.nectarlab.catchup.Database.AppRepository;
import fr.nectarlab.catchup.Database.Message;

/**
 * Created by ThomasBene on 5/28/2018.
 */

public class MessageModel extends AndroidViewModel {
    private AppRepository mRepository;
    private LiveData<List<Message>> allMessages;

    public MessageModel(Application application) {
        super(application);
        mRepository = new AppRepository(application);
        allMessages = mRepository.getAllMessages();
    }

    public void insert(Message message) {
        mRepository.insertMessage(message);
    }

    public LiveData<List<Message>> getAllMessages() {
        return allMessages;
    }

    public AppRepository getmRepository() {
        return mRepository;
    }

}
