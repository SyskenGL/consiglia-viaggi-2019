package it.isw.cvmobile.widgets.scrollview.cards.notification;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import it.isw.cvmobile.R;
import it.isw.cvmobile.models.Notification;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.widgets.scrollview.cards.notification.views.NotificationCardView;


@Completed
public class NotificationCardAdapter extends RecyclerView.Adapter<NotificationCardAdapter.CardViewHolder> {

    private final List<Notification> notifications;
    private final Activity activity;



    public NotificationCardAdapter(Activity activity, List<Notification> notifications) {
        this.notifications = notifications;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_notification, parent, false);
        return new NotificationCardView(view, this, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.bindViewHolder(holder, notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void removeNotification(Notification notification) {
        notifications.remove(notification);
        notifyDataSetChanged();
    }



    public abstract static class CardViewHolder extends RecyclerView.ViewHolder {

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void bindViewHolder(CardViewHolder cardViewHolder, Notification notification);

    }

}