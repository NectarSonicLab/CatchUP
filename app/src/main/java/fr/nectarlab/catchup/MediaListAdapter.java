package fr.nectarlab.catchup;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.nectarlab.catchup.Database.Media;

/**
 * Created by ThomasBene on 5/21/2018.
 */

public class MediaListAdapter extends RecyclerView.Adapter<MediaListAdapter.MediaViewHolder> {
    private final String TAG = "MediaListAdapter";
    private List<Media> mMedia;
    private final LayoutInflater mInflater;



    class MediaViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        private MediaViewHolder (View itemView){
            super(itemView);
            this.imageView= itemView.findViewById(R.id.Media_photo_iv);
            this.textView=itemView.findViewById(R.id.Media_item_tv);
        }
    }


    MediaListAdapter(Context context){
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public MediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = mInflater.inflate(R.layout.media_recyclerview_item, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MediaViewHolder holder, int position) {
        if(mMedia!=null){
            final Media current = mMedia.get(position);
            Uri content = Uri.parse(current.getContenu());
            //holder.imageView.setImageURI(content);
            holder.imageView.setImageBitmap(Insights.decodeSampledBitmapFromResource(Insights.getSelectedImgUri(), 500, 500));
            //holder.imageView.setImageBitmap(Insights.getBitmap());//possible si current a un champ bitmap!
        }
    }

    @Override
    public void onViewDetachedFromWindow(MediaViewHolder holder){
        //testing
        holder.imageView.setVisibility(View.GONE);
    }

    @Override
    public void onViewAttachedToWindow(MediaViewHolder holder){
        //testing
        holder.imageView.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        if(mMedia!=null){
            return mMedia.size();
        }
        else{
        return 0;
        }
    }
    public void setMedia(List<Media>listedMedia){
        this.mMedia=listedMedia;
        notifyDataSetChanged();
    }
}
