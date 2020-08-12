package it.isw.cvmobile.widgets.scrollview.cards.accommodation;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import it.isw.cvmobile.R;
import it.isw.cvmobile.models.AccommodationFacility;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.widgets.scrollview.cards.accommodation.views.HeavyAccommodationCard;
import it.isw.cvmobile.widgets.scrollview.cards.accommodation.views.SoftAccommodationCard;
import it.isw.cvmobile.widgets.scrollview.cards.accommodation.enumerations.AccommodationCardAdapterMode;
import it.isw.cvmobile.widgets.scrollview.cards.listeners.OnItemRemovedListener;
import it.isw.cvmobile.widgets.slider.listeners.OnImageChangedListener;


@Completed
public class AccommodationCardAdapter extends RecyclerView.Adapter<AccommodationCardAdapter.CardViewHolder>{

    private final Activity activity;
    private final AccommodationCardAdapterMode mode;
    private final List<AccommodationFacility> accommodationFacilities;
    private String keywords;
    private OnItemRemovedListener itemRemovedListener;
    private OnImageChangedListener imageChangedListener;



    public AccommodationCardAdapter(Activity activity,
                                    List<AccommodationFacility> accommodationFacilities,
                                    AccommodationCardAdapterMode mode) {
        this.accommodationFacilities = accommodationFacilities;
        this.mode = mode;
        this.activity = activity;
    }

    public AccommodationCardAdapter(Activity activity,
                                    List<AccommodationFacility> accommodationFacilities,
                                    AccommodationCardAdapterMode mode,
                                    String keywords) {
        this.accommodationFacilities = accommodationFacilities;
        this.mode = mode;
        this.activity = activity;
        this.keywords = keywords;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mode == AccommodationCardAdapterMode.HEAVY_ADAPTER ||
           mode == AccommodationCardAdapterMode.HEAVY_ADAPTER_FAVORITES) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.card_heavy_accommodation, parent, false);
            return new HeavyAccommodationCard(view, activity, keywords, this);
        } else {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.card_soft_accommodation, parent, false);
            return new SoftAccommodationCard(view, activity);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.bindViewHolder(holder, accommodationFacilities.get(position));
    }

    @Override
    public int getItemCount() {
        return accommodationFacilities.size();
    }

    public void removeFavorite(AccommodationFacility accommodationFacility) {
        if(mode == AccommodationCardAdapterMode.HEAVY_ADAPTER_FAVORITES) {
            accommodationFacilities.remove(accommodationFacility);
            notifyDataSetChanged();
            itemRemovedListener.onItemRemoved();
        }
    }

    public void setItemRemovedListener(OnItemRemovedListener itemRemovedListener) {
        this.itemRemovedListener = itemRemovedListener;
    }

    public void setImageChangedListener(OnImageChangedListener imageChangedListener) {
        this.imageChangedListener = imageChangedListener;
    }

    public OnImageChangedListener getImageChangedListener() {
        return imageChangedListener;
    }



    public abstract static class CardViewHolder extends RecyclerView.ViewHolder {

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void bindViewHolder(CardViewHolder cardViewHolder, AccommodationFacility accommodationFacility);

    }

}