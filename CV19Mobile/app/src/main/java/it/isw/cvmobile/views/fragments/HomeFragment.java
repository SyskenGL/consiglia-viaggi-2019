package it.isw.cvmobile.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.material.textfield.TextInputEditText;
import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;
import com.wang.avi.AVLoadingIndicatorView;
import org.jetbrains.annotations.NotNull;
import java.util.Random;
import it.isw.cvmobile.R;
import it.isw.cvmobile.presenters.fragments.HomePresenter;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.views.activities.NavigationActivity;


@Completed
public class HomeFragment extends Fragment {

    private final NavigationActivity navigationActivity;
    private HomePresenter homePresenter;
    private KenBurnsView background;
    private TextInputEditText editTextResearch;
    private RecyclerView bestDestinationsRecyclerView;
    private ImageView buttonHotel;
    private ImageView buttonRestaurants;
    private ImageView buttonTouristAttractions;
    private ImageView buttonMaps;
    private ImageView buttonRefresh;
    private View bestDestinationsContainer;
    private View errorView;
    private AVLoadingIndicatorView loadingSpinner;
    private final static int[] backgrounds = {
            R.drawable.background_home_one,
            R.drawable.background_home_two,
            R.drawable.background_home_three,
            R.drawable.background_home_four
    };



    public HomeFragment(NavigationActivity navigationActivity) {
        this.navigationActivity = navigationActivity;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        background = view.findViewById(R.id.fragment_home_background);
        editTextResearch = view.findViewById(R.id.fragment_home_text_input_research);
        bestDestinationsRecyclerView = view.findViewById(R.id.fragment_home_horizontal_best_destination_recycler_view);
        bestDestinationsRecyclerView.setLayoutManager(new CardSliderLayoutManager(200, 600, 0));
        new CardSnapHelper().attachToRecyclerView(bestDestinationsRecyclerView);
        buttonHotel = view.findViewById(R.id.fragment_home_button_hotel);
        loadingSpinner = view.findViewById(R.id.fragment_home_best_destinations_loading_spinner);
        buttonRestaurants = view.findViewById(R.id.fragment_home_button_restaurants);
        buttonTouristAttractions = view.findViewById(R.id.fragment_home_button_tourist_attractions);
        bestDestinationsContainer = view.findViewById(R.id.fragment_home_best_destination);
        buttonMaps = view.findViewById(R.id.fragment_home_button_maps);
        buttonRefresh = view.findViewById(R.id.fragment_home_refresh_button);
        errorView = view.findViewById(R.id.fragment_home_error);
        homePresenter = new HomePresenter(this);
        listenToClickEvents();
        listenToActionDoneEvents();
    }

    @Override
    public void onResume() {
        super.onResume();
        background.resume();
        background.setImageResource(backgrounds[new Random().nextInt(backgrounds.length)]);
    }

    @Override
    public void onStop() {
        super.onStop();
        background.pause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        homePresenter.onActivityResult(requestCode, resultCode, data);
    }

    private void listenToClickEvents() {
        buttonHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homePresenter.onButtonHotelClicked();
            }
        });
        buttonRestaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homePresenter.onButtonRestaurantsClicked();
            }
        });
        buttonTouristAttractions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homePresenter.onButtonTouristAttractionsClicked();
            }
        });
        buttonMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homePresenter.onButtonMapsClicked();
            }
        });
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homePresenter.onButtonRefreshClicked();
            }
        });
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homePresenter.onBackgroundClicked();
            }
        });
    }

    private void listenToActionDoneEvents() {
        editTextResearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return homePresenter.onResearchDone(actionId);
            }
        });
    }

    public void cleanEditTextResearch() {
        editTextResearch.setText("");
    }

    public void startButtonRefreshSpinAnimation() {
        RotateAnimation rotate = new RotateAnimation(
                0,
                360,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
        );
        rotate.setDuration(900);
        rotate.setRepeatCount(Animation.INFINITE);
        buttonRefresh.startAnimation(rotate);
    }

    public void clearButtonRefreshAnimation() {
        buttonRefresh.clearAnimation();
    }

    public NavigationActivity getNavigationActivity() {
        return navigationActivity;
    }

    public String getKeywords() {
        if(editTextResearch.getText() == null) return "";
        return editTextResearch.getText().toString();
    }

    public void setLoadingSpinnerVisibility(boolean isVisible) {
        if(isVisible) {
            loadingSpinner.setVisibility(View.VISIBLE);
        } else {
            loadingSpinner.setVisibility(View.GONE);
        }
    }

    public void setBestDestinationsRecyclerViewVisibility(boolean isVisible) {
        if(isVisible) {
            bestDestinationsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            bestDestinationsRecyclerView.setVisibility(View.GONE);
        }
    }

    public void setBestDestinationsContainerVisibility(boolean visibility) {
        if(visibility) {
            bestDestinationsContainer.setVisibility(View.VISIBLE);
        } else {
            bestDestinationsContainer.setVisibility(View.GONE);
        }
    }

    public void setErrorViewVisibility(boolean visibility) {
        if(visibility) {
            errorView.setVisibility(View.VISIBLE);
        } else {
            errorView.setVisibility(View.GONE);
        }
    }

    public void setBestDestinationsRecyclerViewAdapter(RecyclerView.Adapter adapter) {
        bestDestinationsRecyclerView.setAdapter(adapter);
    }

}
