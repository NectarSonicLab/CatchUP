package fr.nectarlab.catchup.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

import fr.nectarlab.catchup.Database.AppRepository;
import fr.nectarlab.catchup.Database.Media;

/**
 * MediaModel
 * Objet java faisant le lien entre la DB et l'affichage
 */

public class MediaModel extends AndroidViewModel{
    private AppRepository mRepository;
    private LiveData<List<Media>> allMedias;

    public MediaModel (Application application){
        super(application);
        mRepository = new AppRepository(application);
        allMedias = mRepository.getAllMedias();
    }

    public void insert(Media media){mRepository.insertMedia(media);}

    public LiveData<List<Media>> getAllMedias(){return allMedias;}

    public AppRepository getmRepository() {
        return mRepository;
    }
}
