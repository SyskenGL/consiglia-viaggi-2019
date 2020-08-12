package it.isw.cvmobile.widgets.scrollview.cards.review;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import it.isw.cvmobile.R;
import it.isw.cvmobile.models.Review;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.widgets.scrollview.cards.review.enumerations.ReviewCardAdapterMode;
import it.isw.cvmobile.widgets.scrollview.cards.review.views.ReviewCardView;


@Completed
public class ReviewCardAdapter extends RecyclerView.Adapter<ReviewCardAdapter.CardViewHolder> {

    private final List<Review> reviews;
    private final Activity activity;
    private final ReviewCardAdapterMode mode;



    public ReviewCardAdapter(Activity activity, List<Review> reviews, ReviewCardAdapterMode mode) {
        this.reviews = reviews;
        this.activity = activity;
        this.mode = mode;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_review, parent, false);
        return new ReviewCardView(view, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull final CardViewHolder holder, int position) {
        holder.bindViewHolder(holder, reviews.get(position), mode);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }



    public abstract static class CardViewHolder extends RecyclerView.ViewHolder {

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        protected abstract void bindViewHolder(CardViewHolder cardViewHolder,
                                               Review review,
                                               ReviewCardAdapterMode mode);

    }

}