package it.isw.cvmobile.widgets.scrollview.cards.accommodation.views;

import android.app.Activity;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.JsonObject;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import it.isw.cvmobile.R;
import it.isw.cvmobile.dao.DAOFactory;
import it.isw.cvmobile.dao.interfaces.AccommodationFacilityDAO;
import it.isw.cvmobile.models.AccommodationFacility;
import it.isw.cvmobile.models.User;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;
import it.isw.cvmobile.utils.aws.callbacks.LambdaResultsHandler;
import it.isw.cvmobile.views.activities.FullScreenActivity;
import it.isw.cvmobile.views.activities.NavigationActivity;
import it.isw.cvmobile.views.fragments.AccommodationFacilityFragment;
import it.isw.cvmobile.widgets.scrollview.cards.accommodation.AccommodationCardAdapter;
import it.isw.cvmobile.widgets.slider.SliderAdapter;
import it.isw.cvmobile.widgets.slider.SliderItem;
import it.isw.cvmobile.widgets.toast.MotionToast;
import it.isw.cvmobile.widgets.toast.MotionToastType;


@Completed
public class HeavyAccommodationCard extends AccommodationCardAdapter.CardViewHolder {

    private final AccommodationCardAdapter accommodationCardAdapter;
    private final Activity activity;
    private final MaterialCardView cardView;
    private final TextView textViewName;
    private final TextView textViewDescription;
    private final TextView textViewMatchedIn;
    private final TextView textViewTotalViews;
    private final TextView textViewTotalReviews;
    private final TextView textViewTotalFavorites;
    private final TextView textViewAddress;
    private final RatingBar ratingBar;
    private final ImageView typeFlag;
    private final SparkButton buttonFavorite;
    private final ChipGroup chipTagsGroup;
    private final HorizontalScrollView tagsScroll;
    private final SliderView imageSlider;
    private final String keywords;



    public HeavyAccommodationCard(@NonNull View itemView,
                                  Activity activity,
                                  String keywords,
                                  final AccommodationCardAdapter accommodationCardAdapter) {
        super(itemView);
        this.activity = activity;
        this.keywords = keywords;
        this.accommodationCardAdapter = accommodationCardAdapter;
        textViewName = itemView.findViewById(R.id.card_heavy_accommodation_text_view_name);
        textViewDescription = itemView.findViewById(R.id.card_heavy_accommodation_text_view_description);
        textViewTotalFavorites = itemView.findViewById(R.id.card_heavy_accommodation_text_view_favorites);
        textViewTotalViews = itemView.findViewById(R.id.card_heavy_accommodation_text_view_views);
        textViewTotalReviews = itemView.findViewById(R.id.card_heavy_accommodation_text_view_reviews);
        textViewAddress = itemView.findViewById(R.id.card_heavy_accommodation_text_view_address);
        ratingBar = itemView.findViewById(R.id.card_heavy_accommodation_rating_bar);
        tagsScroll = itemView.findViewById(R.id.card_heavy_accommodation_tags_horizontal_scroll_view);
        buttonFavorite = itemView.findViewById(R.id.card_heavy_accommodation_button_favorite);
        textViewMatchedIn = itemView.findViewById(R.id.card_heavy_accommodation_text_view_matched_in);
        imageSlider = itemView.findViewById(R.id.card_heavy_accommodation_image_slider);
        typeFlag = itemView.findViewById(R.id.card_heavy_accommodation_type_flag);
        cardView = itemView.findViewById(R.id.card_heavy_accommodation_container);
        chipTagsGroup = new ChipGroup(activity);
        chipTagsGroup.setSingleLine(true);
        tagsScroll.addView(chipTagsGroup);
    }

    @Override
    public void bindViewHolder(AccommodationCardAdapter.CardViewHolder cardViewHolder,
                               final AccommodationFacility accommodationFacility) {
        textViewName.setText(accommodationFacility.getName());
        textViewTotalFavorites.setText(String.valueOf(accommodationFacility.getTotalFavorites()));
        textViewTotalViews.setText(String.valueOf(accommodationFacility.getTotalViews()));
        textViewTotalReviews.setText(String.valueOf(accommodationFacility.getTotalReviews()));
        ratingBar.setRating(accommodationFacility.getRating());
        textViewAddress.setText(accommodationFacility.getAddress());
        setTypeFlag(accommodationFacility.getType());
        setTextDescription(accommodationFacility);
        if(!User.isLoggedIn()) {
            buttonFavorite.setVisibility(View.GONE);
        } else {
            if(accommodationFacility.isFavorite()) {
                buttonFavorite.setChecked(true);
            } else {
                buttonFavorite.setChecked(false);
            }
        }
        if(accommodationFacility.getTags() != null) {
            showTags(accommodationFacility.getTags());
        }
        if(accommodationFacility.getImages() != null) {
            initializeImageSlider(accommodationFacility);
        }
        listenToClickEvents(accommodationFacility);
        listenToSlideEvents(accommodationFacility);
        listenToTouchEvents();
    }

    private void listenToClickEvents(final AccommodationFacility accommodationFacility) {
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAccommodationFacilityFragment(accommodationFacility);
            }
        });
        buttonFavorite.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if(buttonState){
                    addFavorite(accommodationFacility);
                }else {
                    deleteFavorite(accommodationFacility);
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {
                //...
            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {
                //...
            }
        });

    }

    private void listenToSlideEvents(final AccommodationFacility accommodationFacility) {
        if(accommodationCardAdapter.getImageChangedListener() != null) {
            imageSlider.setCurrentPageListener(new SliderView.OnSliderPageListener() {
                @Override
                public void onSliderPageChanged(int position) {
                    accommodationCardAdapter.getImageChangedListener().onImageChanged(
                            accommodationFacility.getImages()[position]
                    );
                }
            });
        }
    }

    private void listenToTouchEvents() {
        tagsScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                itemView.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    private void openAccommodationFacilityFragment(AccommodationFacility accommodationFacility) {
        if(FullScreenActivity.isNetworkAvailable(activity)) {
            if(activity instanceof NavigationActivity){
                addToHistory(accommodationFacility);
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
        } else {
            MotionToast.display(
                    activity,
                    R.string.toast_warning_internet_connection,
                    MotionToastType.WARNING_MOTION_TOAST
            );
        }
    }

    private void showTags(@NotNull String tags) {
        chipTagsGroup.removeAllViews();
        int tagIndex = 0;
        for(String tag: tags.split(" ")) {
            if(tagIndex % 2 == 0) {
                addTag(R.color.darkBlue, tag);
            } else {
                addTag(R.color.peach, tag);
            }
            tagIndex++;
        }
    }

    private void addTag(int backgroundColor, String tag) {
        Chip chipTag = new Chip(activity);
        chipTag.setChipIconTintResource(R.color.white);
        chipTag.setClickable(false);
        chipTag.setCheckable(false);
        chipTag.setChipIcon(ContextCompat.getDrawable(activity, R.drawable.ic_tag));
        chipTag.setChipIconSize(45);
        chipTag.setIconStartPadding(9);
        chipTag.setChipBackgroundColorResource(backgroundColor);
        chipTag.setTextAppearance(R.style.TagTextAppearance);
        chipTag.setText(tag);
        chipTagsGroup.addView(chipTag);
    }

    private void initializeImageSlider(@NotNull AccommodationFacility accommodationFacility) {
        ArrayList<SliderItem> sliderItems = new ArrayList<>();
        for (String image : accommodationFacility.getImages()) {
            if(image != null) {
                sliderItems.add(new SliderItem(image));
            }
        }
        SliderAdapter sliderAdapter = new SliderAdapter(sliderItems);
        sliderAdapter.setTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                itemView.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        imageSlider.setSliderAdapter(sliderAdapter);
        imageSlider.setIndicatorAnimation(IndicatorAnimations.WORM);
        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        imageSlider.setScrollTimeInSec(3);
        imageSlider.setIndicatorSelectedColor(Color.WHITE);
        imageSlider.setIndicatorUnselectedColor(Color.GRAY);
    }

    private void highlightKeywordsTags(@NotNull AccommodationFacility accommodationFacility) {
        SpannableStringBuilder styledMatchedTags = new SpannableStringBuilder(
                activity.getResources().getString(R.string.heavy_accommodation_card_matched_tags) + " ");
        String formattedKeywords = " " + keywords.toLowerCase() + " ";
        for(String tag: accommodationFacility.getTags().split(" ")) {
            if(styledMatchedTags.length() + tag.length() <= 100) {
                String comparableTag = " " + tag.toLowerCase() + " ";
                if(formattedKeywords.contains(comparableTag)) {
                    SpannableString styledMatchedTag = new SpannableString(tag);
                    styledMatchedTag.setSpan(new BackgroundColorSpan(ContextCompat.getColor(activity, R.color.peach)),
                            0, tag.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    styledMatchedTag.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.white)),
                            0, tag.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    styledMatchedTags.append(styledMatchedTag);
                    styledMatchedTags.append(" ");
                }
            } else {
                styledMatchedTags.append("...");
                break;
            }
        }
        textViewMatchedIn.setVisibility(View.VISIBLE);
        textViewMatchedIn.setText(styledMatchedTags);
    }

    private void highlightKeywordsName(@NotNull AccommodationFacility accommodationFacility) {
        int indexCharacter = 0;
        String name = accommodationFacility.getName();
        SpannableString styledName = new SpannableString(name);
        String formattedKeywords = " " + keywords.toLowerCase() + " ";
        for(String word: name.split(" ")) {
            String comparableWord = " " + word.toLowerCase() + " ";
            if(formattedKeywords.contains(comparableWord)) {
                styledName.setSpan(new BackgroundColorSpan(ContextCompat.getColor(activity, R.color.peach)),
                        indexCharacter, (indexCharacter+word.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                styledName.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.white)),
                        indexCharacter, (indexCharacter+word.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            indexCharacter += (word.length()+1);
        }
        textViewName.setText(styledName);
    }

    private void highlightMatchedReviewSample(@NotNull AccommodationFacility accommodationFacility) {
        SpannableStringBuilder styledMatchedReviews = new SpannableStringBuilder(accommodationFacility.getTotalMatchedReviews() +
               " " + activity.getResources().getString(R.string.heavy_accommodation_card_matched_reviews));
        if(accommodationFacility.getMatchedReviewSample().length() >= 100) {
            String reviewSample = " " + accommodationFacility.getMatchedReviewSample().toLowerCase() + " ";
            for (String key : keywords.split(" ")) {
                String comparableKey = " " + key.toLowerCase() + " ";
                if (reviewSample.contains(comparableKey)) {
                    int keyIndex = reviewSample.indexOf(comparableKey.toLowerCase());
                    int leftPadding = keyIndex - (45-key.length()/2);
                    int rightPadding = keyIndex + (45-key.length()/2);
                    if (leftPadding < 0) {
                        rightPadding = - leftPadding + keyIndex + (key.length()-1);
                        leftPadding = 0;
                    } else if (rightPadding > reviewSample.length()) {
                        leftPadding = leftPadding - (rightPadding - reviewSample.length());
                        rightPadding = reviewSample.length();
                    }
                    String compressedReviewSample = " " + accommodationFacility.getMatchedReviewSample().substring(leftPadding, rightPadding) + " ";
                    int startKeyIndex = compressedReviewSample.toLowerCase().indexOf(comparableKey.toLowerCase())+1;
                    SpannableString styledCompressedReviewSample = new SpannableString(compressedReviewSample);
                    styledCompressedReviewSample.setSpan(new BackgroundColorSpan(ContextCompat.getColor(activity, R.color.peach)),
                            startKeyIndex, (startKeyIndex + key.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    styledCompressedReviewSample.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.white)),
                            startKeyIndex, (startKeyIndex + key.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    styledMatchedReviews.append(": ...");
                    styledMatchedReviews.append(styledCompressedReviewSample);
                    styledMatchedReviews.append("...");
                    break;
                }
            }
        }
        textViewMatchedIn.setVisibility(View.VISIBLE);
        textViewMatchedIn.setText(styledMatchedReviews);
    }

    private void setTextDescription(AccommodationFacility accommodationFacility) {
        int descriptionLength = setMatchedIn(accommodationFacility);
        if(accommodationFacility.getDescription() != null) {
            String description = accommodationFacility.getDescription();
            description = description.replace("\n", "");
            description = description.substring(0, Math.min(description.length(), descriptionLength)) + "...";
            textViewDescription.setText(description);
        }
    }

    private void setTypeFlag(String type) {
        if(type != null) {
            if(type.equals("lodging")) {
                typeFlag.setImageResource(R.drawable.flag_hotel);
            } else if(type.equals("restaurant")) {
                typeFlag.setImageResource(R.drawable.flag_restaurant);
            } else {
                typeFlag.setImageResource(R.drawable.flag_tourist_attraction);
            }
        }
    }

    private int setMatchedIn(@NotNull AccommodationFacility accommodationFacility) {
        int descriptionLength = 130;
        if(accommodationFacility.getMatchedIn() != null && keywords != null) {
            if(accommodationFacility.getMatchedIn().equals("name") && accommodationFacility.getName() != null) {
                highlightKeywordsName(accommodationFacility);
            } else if(accommodationFacility.getMatchedIn().equals("tags") && accommodationFacility.getTags() != null) {
                highlightKeywordsTags(accommodationFacility);
                descriptionLength = 80;
            } else if(accommodationFacility.getMatchedIn().equals("review") && accommodationFacility.getMatchedReviewSample() != null) {
                highlightMatchedReviewSample(accommodationFacility);
                descriptionLength = 80;
            }
        }
        return descriptionLength;
    }

    @RequiresInternetConnection
    private void addToHistory(@NotNull AccommodationFacility accommodationFacility) {
        accommodationFacility.setTotalViews(accommodationFacility.getTotalViews()+1);
        textViewTotalViews.setText(String.valueOf(accommodationFacility.getTotalViews()));
        AccommodationFacilityDAO accommodationFacilityDAO = DAOFactory.getAccommodationFacilityDAO(activity);
        String userId = null;
        if(User.isLoggedIn()) {
            userId = User.getInstance().getUserId();
        }
        accommodationFacilityDAO.addToHistory(accommodationFacility.getAccommodationFacilityId(), userId, null);
    }

    @RequiresInternetConnection
    private void deleteFavorite(final AccommodationFacility accommodationFacility) {
        if(FullScreenActivity.isNetworkAvailable(activity)) {
            AccommodationFacilityDAO accommodationFacilityDAO = DAOFactory.getAccommodationFacilityDAO(activity);
            accommodationFacilityDAO.deleteFromFavorites(
                    accommodationFacility.getAccommodationFacilityId(),
                    User.getInstance().getUserId(),
                    new LambdaResultsHandler() {

                        @Override
                        public void onSuccess(JsonObject results) {
                            User.getInstance().setTotalFavorites(User.getInstance().getTotalFavorites()-1);
                            accommodationFacility.setTotalFavorites(accommodationFacility.getTotalFavorites()-1);
                            textViewTotalFavorites.setText(String.valueOf(accommodationFacility.getTotalFavorites()));
                            accommodationFacility.setFavorite(false);
                            accommodationCardAdapter.removeFavorite(accommodationFacility);
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            buttonFavorite.setChecked(true);
                            MotionToast.display(
                                    activity,
                                    R.string.toast_error_unknown_error,
                                    MotionToastType.ERROR_MOTION_TOAST
                            );
                        }

                    });
        } else{
            buttonFavorite.setChecked(true);
            MotionToast.display(
                    activity,
                    R.string.toast_warning_internet_connection,
                    MotionToastType.WARNING_MOTION_TOAST
            );
        }
    }

    @RequiresInternetConnection
    private void addFavorite(final AccommodationFacility accommodationFacility) {
        if(FullScreenActivity.isNetworkAvailable(activity)) {
            AccommodationFacilityDAO accommodationFacilityDAO = DAOFactory.getAccommodationFacilityDAO(activity);
            accommodationFacilityDAO.addToFavorites(
                    accommodationFacility.getAccommodationFacilityId(),
                    User.getInstance().getUserId(),
                    new LambdaResultsHandler() {

                        @Override
                        public void onSuccess(JsonObject results) {
                            User.getInstance().setTotalFavorites(User.getInstance().getTotalFavorites()+1);
                            accommodationFacility.setTotalFavorites(accommodationFacility.getTotalFavorites()+1);
                            textViewTotalFavorites.setText(String.valueOf(accommodationFacility.getTotalFavorites()));
                            accommodationFacility.setFavorite(true);
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            buttonFavorite.setChecked(false);
                            MotionToast.display(
                                    activity,
                                    R.string.toast_error_unknown_error,
                                    MotionToastType.ERROR_MOTION_TOAST
                            );
                        }

                    });
        } else {
            buttonFavorite.setChecked(false);
            MotionToast.display(
                    activity,
                    R.string.toast_warning_internet_connection,
                    MotionToastType.WARNING_MOTION_TOAST
            );
        }
    }

}









