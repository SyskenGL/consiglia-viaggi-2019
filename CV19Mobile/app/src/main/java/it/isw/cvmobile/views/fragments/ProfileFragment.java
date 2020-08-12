package it.isw.cvmobile.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wang.avi.AVLoadingIndicatorView;
import org.jetbrains.annotations.NotNull;
import de.hdodenhof.circleimageview.CircleImageView;
import it.isw.cvmobile.R;
import it.isw.cvmobile.presenters.fragments.ProfilePresenter;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.views.activities.NavigationActivity;


@Completed
public class ProfileFragment extends Fragment {

    private final NavigationActivity navigationActivity;
    private ProfilePresenter profilePresenter;
    private CircleImageView profilePicture;
    private TextView textViewGivenName;
    private TextView textViewFamilyName;
    private TextView textViewNickname;
    private TextView textViewAccountCreatedDate;
    private TextView textViewEmail;
    private TextView textViewTotalReviews;
    private TextView textViewTotalFavorites;
    private RadioButton radioButtonNickname;
    private RadioButton radioButtonRealName;
    private RadioGroup radioGroupPreferredUsername;
    private Button buttonChangePassword;
    private FloatingActionButton buttonChangeProfilePicture;
    private AVLoadingIndicatorView loadingSpinner;



    public ProfileFragment(NavigationActivity navigationActivity) {
        this.navigationActivity = navigationActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NotNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textViewGivenName = view.findViewById(R.id.fragment_profile_text_view_given_name);
        textViewFamilyName = view.findViewById(R.id.fragment_profile_text_view_family_name);
        textViewNickname = view.findViewById(R.id.fragment_profile_text_view_nickname);
        textViewAccountCreatedDate = view.findViewById(R.id.fragment_profile_text_view_account_created_value);
        textViewEmail = view.findViewById(R.id.fragment_profile_text_view_email_value);
        textViewTotalReviews = view.findViewById(R.id.fragment_profile_text_view_total_reviews_value);
        textViewTotalFavorites = view.findViewById(R.id.fragment_profile_text_view_total_favorites_value);
        radioGroupPreferredUsername = view.findViewById(R.id.fragment_profile_toggle_preferred_username);
        radioButtonNickname = view.findViewById(R.id.fragment_profile_toggle_nickname);
        radioButtonRealName = view.findViewById(R.id.fragment_profile_toggle_real_name);
        buttonChangePassword = view.findViewById(R.id.fragment_profile_button_change_password);
        buttonChangeProfilePicture = view.findViewById(R.id.fragment_profile_button_edit_image_profile);
        profilePicture = view.findViewById(R.id.fragment_profile_picture);
        loadingSpinner = view.findViewById(R.id.fragment_profile_loading_spinner);
        profilePresenter = new ProfilePresenter(this);
        listenToClickEvents();
        listenToCheckEvents();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        profilePresenter.onActivityResult(requestCode, resultCode, data);
    }

    private void listenToClickEvents() {
        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilePresenter.onButtonChangePasswordClicked();
            }
        });
        buttonChangeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilePresenter.onButtonChangeProfilePictureClicked();
            }
        });
    }

    private void listenToCheckEvents() {
        radioGroupPreferredUsername.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                profilePresenter.onRadioGroupPreferredUsernameChecked();
            }
        });
    }

    public CircleImageView getProfilePicture() {
        return profilePicture;
    }

    public int getRadioButtonNicknameId() {
        return radioButtonNickname.getId();
    }

    public int getRadioButtonRealNameId() {
        return radioButtonRealName.getId();
    }

    public String getCheckedPreferredUsernameValue() {
        if(radioButtonNickname.isChecked())
            return "nickname";
        return "real_name";
    }

    public NavigationActivity getNavigationActivity() {
        return navigationActivity;
    }

    public void setTextGivenName(String givenName) {
        textViewGivenName.setText(givenName);
    }

    public void setTextFamilyName(String familyName) {
        textViewFamilyName.setText(familyName);
    }

    public void setTextNickname(String nickname) {
        textViewNickname.setText(nickname);
    }

    public void setTextAccountCreatedDate(String accountCreatedDate) {
        textViewAccountCreatedDate.setText(accountCreatedDate);
    }

    public void setTextEmail(String email) {
        textViewEmail.setText(email);
    }

    public void setTextTotalReviews(String totalReviews) {
        textViewTotalReviews.setText(totalReviews);
    }

    public void setTextTotalFavorites(String totalFavorites) {
        textViewTotalFavorites.setText(totalFavorites);
    }

    public void setCheckedPreferredUsername(int buttonToCheck) {
        radioGroupPreferredUsername.setOnCheckedChangeListener(null);
        radioGroupPreferredUsername.check(buttonToCheck);
        radioGroupPreferredUsername.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                profilePresenter.onRadioGroupPreferredUsernameChecked();
            }
        });
    }

    public void setLoadingSpinnerVisibility(boolean visible) {
        if(visible) {
            loadingSpinner.setVisibility(View.VISIBLE);
        } else {
            loadingSpinner.setVisibility(View.INVISIBLE);
        }
    }

}