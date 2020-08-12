package it.isw.cvmobile.widgets.scrollview.cards.accommodation.views;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import it.isw.cvmobile.R;
import it.isw.cvmobile.dao.DAOFactory;
import it.isw.cvmobile.dao.interfaces.AccommodationFacilityDAO;
import it.isw.cvmobile.models.AccommodationFacility;
import it.isw.cvmobile.models.User;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;
import it.isw.cvmobile.views.activities.FullScreenActivity;
import it.isw.cvmobile.views.activities.NavigationActivity;
import it.isw.cvmobile.views.fragments.AccommodationFacilityFragment;
import it.isw.cvmobile.widgets.scrollview.cards.accommodation.AccommodationCardAdapter;
import it.isw.cvmobile.widgets.slider.SliderAdapter;
import it.isw.cvmobile.widgets.slider.SliderItem;
import it.isw.cvmobile.widgets.toast.MotionToast;
import it.isw.cvmobile.widgets.toast.MotionToastType;


@Completed
public class SoftAccommodationCard extends AccommodationCardAdapter.CardViewHolder {

    private final Activity activity;
    private final RatingBar ratingBar;
    private final ImageView typeFlag;
    private final SliderView imageSlider;



    public SoftAccommodationCard(@NonNull View itemView, Activity activity) {
        super(itemView);
        this.activity = activity;
        imageSlider = itemView.findViewById(R.id.card_soft_accommodation_image_slider);
        typeFlag = itemView.findViewById(R.id.card_soft_accommodation_type_flag);
        ratingBar = itemView.findViewById(R.id.card_soft_accommodation_rating_bar);
    }

    @Override
    public void bindViewHolder(AccommodationCardAdapter.CardViewHolder cardViewHolder,
                               @NotNull AccommodationFacility accommodationFacility) {
        if(accommodationFacility.getType() != null) {
            if(accommodationFacility.getType().equals("lodging")) {
                typeFlag.setImageResource(R.drawable.flag_hotel);
            } else if(accommodationFacility.getType().equals("restaurant")) {
                typeFlag.setImageResource(R.drawable.flag_restaurant);
            } else {
                typeFlag.setImageResource(R.drawable.flag_tourist_attraction);
            }
        }
        ratingBar.setRating(accommodationFacility.getRating());
        if(accommodationFacility.getImages() != null) {
            initializeImageSlider(accommodationFacility);
        }
    }

    private void initializeImageSlider(@NotNull final AccommodationFacility accommodationFacility) {
        ArrayList<SliderItem> sliderItems = new ArrayList<>();
        for (String image : accommodationFacility.getImages()) {
            if(image != null) {
                sliderItems.add(new SliderItem(image));
            }
        }
        SliderAdapter sliderAdapter = new SliderAdapter(sliderItems);
        sliderAdapter.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAccommodationFacilityFragment(accommodationFacility);
            }
        });
        imageSlider.setSliderAdapter(sliderAdapter);
        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        imageSlider.setScrollTimeInSec(5);
        imageSlider.startAutoCycle();
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

    @RequiresInternetConnection
    private void addToHistory(@NotNull AccommodationFacility accommodationFacility) {
        accommodationFacility.setTotalViews(accommodationFacility.getTotalViews()+1);
        AccommodationFacilityDAO accommodationFacilityDAO = DAOFactory.getAccommodationFacilityDAO(activity);
        String userId = null;
        if(User.isLoggedIn()) {
            userId = User.getInstance().getUserId();
        }
        accommodationFacilityDAO.addToHistory(accommodationFacility.getAccommodationFacilityId(), userId, null);
    }

}
