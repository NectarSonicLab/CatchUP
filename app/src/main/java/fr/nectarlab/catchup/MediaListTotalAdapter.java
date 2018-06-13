package fr.nectarlab.catchup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import fr.nectarlab.catchup.Database.Media;

/**
 * MediaListTotalAdapter
 * Presente tous les medias enregistres dans l'activite MemoryBox
 */

public class MediaListTotalAdapter extends RecyclerView.Adapter<MediaListTotalAdapter.MediaViewHolder> {
    private final String TAG = "MediaListTotalAdapter";
    private List<Media> mMedia;
    private final LayoutInflater mInflater;
    Context context;


    class MediaViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        private MediaViewHolder(View itemView){
            super(itemView);
            imageView =  itemView.findViewById(R.id.memoryBoxItem_img);
        }
    }

    MediaListTotalAdapter (Context context){
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public MediaListTotalAdapter.MediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = mInflater.inflate(R.layout.memory_box_items, parent, false);
        context = mInflater.getContext();
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MediaListTotalAdapter.MediaViewHolder holder, int position) {
        if(mMedia!=null){
            final Media current = mMedia.get(position);
            GlideApp.with(context)
                    .load(current.getContenu())
                    .placeholder(R.drawable.catchup_splashscreen_small)
                    .into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        if(mMedia!=null){
            Log.i(TAG, "MediaTotal getItemCount()" +mMedia.size());
            return mMedia.size();
        }
        else{
            return 0;
        }
    }
    public void setMediaTotal(List<Media>listedMedia){
        this.mMedia=listedMedia;
        notifyDataSetChanged();
    }
}
