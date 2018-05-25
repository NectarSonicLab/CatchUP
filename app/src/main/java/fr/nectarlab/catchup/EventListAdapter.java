package fr.nectarlab.catchup;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import fr.nectarlab.catchup.Database.EventDB;

/**
 * EventListAdapter
 * Gere l'affichage des objets EventDB enregistres dans la BD
 */

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventsViewHolder> {
    private final String TAG = "EventListAdapter";
    private List<EventDB> mEvents;
    private final LayoutInflater mInflater;
    Context context;

    /*
     * Classe interne gardant en reference les itemsView
     * necessaires a l'affichage
     */
    class EventsViewHolder extends RecyclerView.ViewHolder implements Serializable {
        TextView displayName, displayDate, displayNbFriends;
        ImageButton dots;

        private EventsViewHolder(View itemView) {
            super(itemView);
            this.displayName = itemView.findViewById(R.id.eventItem_name);
            this.displayDate = itemView.findViewById(R.id.eventItem_date);
            this.displayNbFriends = itemView.findViewById(R.id.eventItem_nbFriends);//a modifier, initialement pour montrer le nb d'amis
            this.dots = itemView.findViewById(R.id.eventItem_dots_iv);
        }
    }

    EventListAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    /*
     * Creation de la View (item)
     */
    @Override
    public EventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = mInflater.inflate(R.layout.event_recyclerview_item, parent, false);
        context = mInflater.getContext();
        return new EventsViewHolder(view);
    }

    /*
     * Affichage des elements tires de l'objet dans des Views
     */
    @Override
    public void onBindViewHolder(EventsViewHolder holder, int position) {
        if (mEvents != null) {
            final EventDB current = mEvents.get(position);

            Log.i(TAG, current.toString() );
            holder.displayName.setText(current.getEventName());
            holder.displayDate.setText(current.getDate());
            holder.displayNbFriends.setText(current.getEventType());
            holder.dots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent (context, Insights.class);
                    //.putExtra(IntentUtils.getEventAdapter_displayName(), current.getEventName());
                    //i.putExtra(IntentUtils.getEventAdapter_displayDate(), current.getDate());
                   // i.putExtra(IntentUtils.getEventAdapter_DisplayEventType(), current.getEventType());
                    i.putExtra(IntentUtils.getEventAdapter_CurrentObject(), current);
                    context.startActivity(i);
                }
            });
        }

    }

    /*
     * Retourne le nombre d'objets a inserer dans l'adapter
     */
    @Override
    public int getItemCount() {
        if (mEvents != null) {
            Log.i(TAG, "eventSize: "+mEvents.size());
            return mEvents.size();
        } else {
            return 0;
        }
    }

    /*
     * Sert a finaliser l'affichage
     */
    public void setEvents(List<EventDB>listedEvents){
        this.mEvents=listedEvents;
        notifyDataSetChanged();
    }


}

