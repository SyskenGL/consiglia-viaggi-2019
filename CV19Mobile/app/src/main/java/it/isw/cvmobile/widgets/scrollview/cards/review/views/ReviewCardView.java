package it.isw.cvmobile.widgets.scrollview.cards.review.views;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.JsonObject;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;
import com.wang.avi.AVLoadingIndicatorView;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;
import it.isw.cvmobile.R;
import it.isw.cvmobile.dao.DAOFactory;
import it.isw.cvmobile.dao.interfaces.AccommodationFacilityDAO;
import it.isw.cvmobile.dao.interfaces.ReviewDAO;
import it.isw.cvmobile.models.AccommodationFacility;
import it.isw.cvmobile.models.Review;
import it.isw.cvmobile.models.User;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;
import it.isw.cvmobile.utils.aws.callbacks.LambdaResultsHandler;
import it.isw.cvmobile.views.activities.FullScreenActivity;
import it.isw.cvmobile.views.activities.NavigationActivity;
import it.isw.cvmobile.views.fragments.AccommodationFacilityFragment;
import it.isw.cvmobile.widgets.scrollview.cards.review.ReviewCardAdapter;
import it.isw.cvmobile.widgets.scrollview.cards.review.enumerations.ReviewCardAdapterMode;
import it.isw.cvmobile.widgets.slider.SliderAdapter;
import it.isw.cvmobile.widgets.slider.SliderItem;
import it.isw.cvmobile.widgets.toast.MotionToast;
import it.isw.cvmobile.widgets.toast.MotionToastType;


@Completed
public class ReviewCardView extends ReviewCardAdapter.CardViewHolder {

    private MaterialCardView imageSliderContainer;
    private MaterialCardView reviewCard;
    private final Activity activity;
    private final RatingBar ratingBar;
    private final CircleImageView reviewerProfilePicture;
    private final TextView textViewReviewerUsername;
    private final TextView textViewTitle;
    private final TextView textViewDateOfStay;
    private final TextView textViewPublicationDate;
    private final TextView textViewDescription;
    private final TextView textViewTotalLikes;
    private final TextView textViewTotalDislikes;
    private final TextView textViewStatus;
    private final TextView textViewAccommodationFacilityName;
    private final SliderView imageSlider;
    private final SparkButton buttonLike;
    private final SparkButton buttonDislike;
    private final AVLoadingIndicatorView loadingSpinner;



    public ReviewCardView(@NonNull View itemView, Activity activity) {
        super(itemView);
        this.activity = activity;
        reviewCard = itemView.findViewById(R.id.card_review_container);
        ratingBar = itemView.findViewById(R.id.card_review_rating_bar);
        reviewerProfilePicture = itemView.findViewById(R.id.card_review_reviewer_profile_picture);
        textViewReviewerUsername = itemView.findViewById(R.id.card_review_reviewer_username);
        textViewTitle = itemView.findViewById(R.id.card_review_title);
        textViewDateOfStay = itemView.findViewById(R.id.card_review_date_of_stay);
        textViewPublicationDate = itemView.findViewById(R.id.card_review_publication_date);
        textViewDescription = itemView.findViewById(R.id.card_review_description);
        textViewStatus = itemView.findViewById(R.id.card_review_status);
        textViewAccommodationFacilityName = itemView.findViewById(R.id.card_review_accommodation_facility_name);
        imageSlider = itemView.findViewById(R.id.card_review_slider_view);
        imageSliderContainer = itemView.findViewById(R.id.card_review_slider_view_container);
        buttonLike = itemView.findViewById(R.id.card_review_thumb_up);
        buttonDislike = itemView.findViewById(R.id.card_review_thumb_down);
        textViewTotalLikes = itemView.findViewById(R.id.card_review_total_thumb_up);
        textViewTotalDislikes = itemView.findViewById(R.id.card_review_total_thumb_down);
        loadingSpinner = itemView.findViewById(R.id.card_review_loading_spinner);
    }

    @Override
    protected void bindViewHolder(ReviewCardAdapter.CardViewHolder cardViewHolder,
                                  @NotNull Review review,
                                  ReviewCardAdapterMode mode) {
        ratingBar.setRating(review.getRating());
        textViewReviewerUsername.setText(review.getReviewerUsername());
        textViewTitle.setText(review.getTitle());
        textViewDateOfStay.setText(review.getDateOfStay());
        textViewPublicationDate.setText(review.getPublicationDate());
        textViewDescription.setText(review.getDescription());
        textViewTotalLikes.setText(String.valueOf(review.getTotalLikes()));
        textViewTotalDislikes.setText(String.valueOf(review.getTotalDislikes()));
        showProfilePicture(review.getReviewerProfilePicture());
        initializeFeedbackButtons(review);
        if(review.getImages() != null && review.getImages().length > 0) {
            imageSliderContainer.setVisibility(View.VISIBLE);
            initializeImageSlider(review);
        }
        if(mode == ReviewCardAdapterMode.EXTRA_INFO_CARD) {
            reviewCard.setClickable(true);
            textViewAccommodationFacilityName.setVisibility(View.VISIBLE);
            textViewAccommodationFacilityName.setText(review.getAccommodationFacilityName());
        } else {
            reviewCard.setClickable(false);
        }
        showStatus(review, mode);
        listenToClickEvents(review, mode);
    }

    private void initializeImageSlider(@NotNull Review review) {
        ArrayList<SliderItem> sliderItems = new ArrayList<>();
        for (String image : review.getImages()) {
            if(image != null) {
                sliderItems.add(new SliderItem(image));
            }
        }
        SliderAdapter sliderAdapter = new SliderAdapter(sliderItems);
        imageSlider.setSliderAdapter(sliderAdapter);
        imageSlider.setIndicatorAnimation(IndicatorAnimations.WORM);
        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        imageSlider.setScrollTimeInSec(3);
        imageSlider.setIndicatorSelectedColor(Color.WHITE);
        imageSlider.setIndicatorUnselectedColor(Color.GRAY);
    }

    private void listenToClickEvents(final Review review, ReviewCardAdapterMode mode) {
        buttonLike.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if(User.isLoggedIn()) {
                    if(buttonState){
                        addFeedback(review, true);
                    }else {
                        deleteFeedback(review, true);
                    }
                } else {
                    buttonLike.setChecked(false);
                    MotionToast.display(
                            activity,
                            R.string.toast_info_not_logged,
                            MotionToastType.INFO_MOTION_TOAST
                    );
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {
                // ...
            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {
                // ...
            }
        });
        buttonDislike.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if(User.isLoggedIn()) {
                    if(buttonState){
                        addFeedback(review, false);
                    }else {
                        deleteFeedback(review, false);
                    }
                } else {
                    buttonDislike.setChecked(false);
                    MotionToast.display(
                            activity,
                            R.string.toast_info_not_logged,
                            MotionToastType.INFO_MOTION_TOAST
                    );
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {
                // ...
            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {
                // ...
            }

        });
        if(mode == ReviewCardAdapterMode.EXTRA_INFO_CARD) {
            reviewCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onReviewCardClicked(review);
                }
            });
        }
    }

    private void initializeFeedbackButtons(Review review) {
        if(User.isLoggedIn()) {
            if(review.getFeedback() != null) {
                if(review.getFeedback().equals("thumb_up")) {
                    buttonLike.setChecked(true);
                    buttonDislike.setChecked(false);
                } else {
                    buttonDislike.setChecked(true);
                    buttonLike.setChecked(false);
                }
            } else {
                buttonDislike.setChecked(false);
                buttonLike.setChecked(false);
            }
        }
    }

    private void showStatus(Review review, ReviewCardAdapterMode mode) {
        if(mode == ReviewCardAdapterMode.EXTRA_INFO_CARD) {
            textViewStatus.setVisibility(View.VISIBLE);
            if(review.getStatus().equals("pending")) {
                textViewStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null,
                        ContextCompat.getDrawable(activity, R.drawable.ic_pending),
                        null
                );
            } else if(review.getStatus().equals("approved")) {
                textViewStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null,
                        ContextCompat.getDrawable(activity, R.drawable.ic_approved),
                        null
                );
            } else {
                textViewStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null,
                        ContextCompat.getDrawable(activity, R.drawable.ic_disapproved),
                        null
                );
            }
        }
    }

    private void openAccommodationFacilityFragment(AccommodationFacility accommodationFacility) {
        if(activity instanceof NavigationActivity){
            NavigationActivity navigationActivity = (NavigationActivity) activity;
            AccommodationFacilityFragment accommodationFacilityFragment =
                    new AccommodationFacilityFragment(navigationActivity, accommodationFacility);
            FragmentTransaction fragmentTransaction = navigationActivity.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_right
            );
            fragmentTransaction.add(R.id.activity_navigation_fragment_container, accommodationFacilityFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    private void handleAddFeedbackSuccess(Review review, boolean isLike) {
        if(isLike) {
            review.setTotalLikes(review.getTotalLikes()+1);
            textViewTotalLikes.setText(String.valueOf(review.getTotalLikes()));
            review.setFeedback("thumb_up");
            if(buttonDislike.isChecked()){
                review.setTotalDislikes(review.getTotalDislikes()-1);
                textViewTotalDislikes.setText(String.valueOf(review.getTotalDislikes()));
                buttonDislike.setChecked(false);
            }
        } else {
            review.setTotalDislikes(review.getTotalDislikes()+1);
            textViewTotalDislikes.setText(String.valueOf(review.getTotalDislikes()));
            review.setFeedback("thumb_down");
            if(buttonLike.isChecked()){
                review.setTotalLikes(review.getTotalLikes()-1);
                textViewTotalLikes.setText(String.valueOf(review.getTotalLikes()));
                buttonLike.setChecked(false);
            }
        }
    }

    @RequiresInternetConnection
    private void onReviewCardClicked(Review review) {
        if(FullScreenActivity.isNetworkAvailable(activity)) {
            final AccommodationFacilityDAO accommodationFacilityDAO = DAOFactory.getAccommodationFacilityDAO(activity);
            Map<String, String> filters = new HashMap<>();
            filters.put("accommodation_facility_id", review.getAccommodationFacilityId());
            if(User.isLoggedIn()) {
                filters.put("user_id", User.getInstance().getUserId());
            }
            accommodationFacilityDAO.getAccommodationFacilities(filters, null, null, 0, 1, new LambdaResultsHandler() {

                @Override
                public void onSuccess(JsonObject results) {
                    List<AccommodationFacility> accommodationFacilities = accommodationFacilityDAO.parseResults(results);
                    openAccommodationFacilityFragment(accommodationFacilities.get(0));
                }

                @Override
                public void onFailure(Exception exception) {
                    MotionToast.display(
                            activity,
                            R.string.toast_error_unknown_error,
                            MotionToastType.ERROR_MOTION_TOAST
                    );
                }

            });
        } else {
            MotionToast.display(
                    activity,
                    R.string.toast_warning_internet_connection,
                    MotionToastType.WARNING_MOTION_TOAST
            );
        }
    }

    @RequiresInternetConnection
    private void addFeedback(final Review review, final boolean isLike) {
        if(FullScreenActivity.isNetworkAvailable(activity)) {
            ReviewDAO reviewDAO = DAOFactory.getReviewDAO(activity);
            reviewDAO.addFeedback(isLike, review.getReviewId(), User.getInstance().getUserId(),
                    new LambdaResultsHandler() {

                        @Override
                        public void onSuccess(JsonObject results) {
                            handleAddFeedbackSuccess(review, isLike);
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            if(isLike) buttonLike.setChecked(false);
                            else buttonDislike.setChecked(false);
                            MotionToast.display(
                                    activity,
                                    R.string.toast_error_unknown_error,
                                    MotionToastType.ERROR_MOTION_TOAST
                            );
                        }

                    });
        } else {
            if(isLike) buttonLike.setChecked(false);
            else buttonDislike.setChecked(false);
            MotionToast.display(
                    activity,
                    R.string.toast_warning_internet_connection,
                    MotionToastType.WARNING_MOTION_TOAST
            );
        }
    }

    @RequiresInternetConnection
    private void deleteFeedback(final Review review, final boolean isLike) {
        if(FullScreenActivity.isNetworkAvailable(activity)) {
            ReviewDAO reviewDAO = DAOFactory.getReviewDAO(activity);
            reviewDAO.deleteFeedback(review.getReviewId(), User.getInstance().getUserId(),
                    new LambdaResultsHandler() {

                        @Override
                        public void onSuccess(JsonObject results) {
                            if(isLike) {
                                review.setTotalLikes(review.getTotalLikes()-1);
                                textViewTotalLikes.setText(String.valueOf(review.getTotalLikes()));
                                review.setFeedback(null);
                            } else {
                                review.setTotalDislikes(review.getTotalDislikes()-1);
                                textViewTotalDislikes.setText(String.valueOf(review.getTotalDislikes()));
                                review.setFeedback(null);
                            }
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            if(isLike) buttonLike.setChecked(true);
                            else buttonDislike.setChecked(true);
                            buttonDislike.setChecked(true);
                            MotionToast.display(
                                    activity,
                                    R.string.toast_error_unknown_error,
                                    MotionToastType.ERROR_MOTION_TOAST
                            );
                        }

                    });
        } else {
            if(isLike) buttonLike.setChecked(true);
            else buttonDislike.setChecked(true);
            MotionToast.display(
                    activity,
                    R.string.toast_warning_internet_connection,
                    MotionToastType.WARNING_MOTION_TOAST
            );
        }
    }

    @RequiresInternetConnection
    private void showProfilePicture(String url) {
        loadingSpinner.setVisibility(View.VISIBLE);
        reviewerProfilePicture.setVisibility(View.INVISIBLE);
        Glide.with(activity)
                .load(url)
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .listener(new RequestListener<Drawable>() {

                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e,
                                                Object model,
                                                Target<Drawable> target,
                                                boolean isFirstResource) {
                        loadingSpinner.setVisibility(View.GONE);
                        reviewerProfilePicture.setImageResource(R.drawable.ic_error_sad);
                        reviewerProfilePicture.setVisibility(View.VISIBLE);
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource,
                                                   Object model,
                                                   Target<Drawable> target,
                                                   DataSource dataSource,
                                                   boolean isFirstResource) {
                        loadingSpinner.setVisibility(View.GONE);
                        reviewerProfilePicture.setImageDrawable(resource);
                        reviewerProfilePicture.setVisibility(View.VISIBLE);
                        return true;
                    }

                }).into(reviewerProfilePicture);
    }

}














