package fr.nectarlab.catchup;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import fr.nectarlab.catchup.Database.Media;
import fr.nectarlab.catchup.model.MediaModel;

/**
 * Created by ThomasBene on 6/12/2018.
 */

public class MemoryBox extends AppCompatActivity {
    private final String TAG = "MemoryBox";
    private MediaModel mMediaModel;
    private Observer observer;
    @Override
    public void onCreate (Bundle b){
        super.onCreate(b);
        this.setContentView(R.layout.memory_box);
    }

    @Override
    public void onResume(){
        super.onResume();
        this.mMediaModel = ViewModelProviders.of(this).get(MediaModel.class);
        RecyclerView recyclerView = findViewById(R.id.memoryBox_recycler_rv);
        final MediaListTotalAdapter adapter = new MediaListTotalAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mMediaModel.getAllMedias().observe(this, observer =new Observer<List<Media>>() {
            @Override
            public void onChanged(@Nullable List<Media> media) {
                adapter.setMediaTotal(media);
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        if(observer!=null){
            mMediaModel.getAllMedias().removeObserver(observer);
        }
    }
}
