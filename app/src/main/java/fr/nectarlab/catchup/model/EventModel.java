package fr.nectarlab.catchup.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

import fr.nectarlab.catchup.Database.AppRepository;
import fr.nectarlab.catchup.Database.EventDB;

/**
 * EventModel
 * Objet Java faisant le lien entre la DB et l'affichage
 */
public class EventModel extends AndroidViewModel {
    private AppRepository mRepository;
    private LiveData<List<EventDB>> allEvents;

    public EventModel (Application application){
        super(application);
        mRepository = new AppRepository(application);
        allEvents = mRepository.getAllEvents();
    }
    /*
     * Methode pour inserer un nouvel evenement dans la BD via AppRepository
     */
    public void insert(EventDB eventDB){
        mRepository.insertEvent(eventDB);
    }
    /*
     *Methode pour recuperer tous les evenements enregistres dans la BD
     */
    public LiveData<List<EventDB>> getAllEvents(){
        return allEvents;
    }
}
