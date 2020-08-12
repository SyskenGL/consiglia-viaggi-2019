package it.isw.cvmobile.views.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import org.jetbrains.annotations.NotNull;
import java.util.Calendar;
import it.isw.cvmobile.R;
import it.isw.cvmobile.models.AccommodationFacility;
import it.isw.cvmobile.presenters.fragments.PostReviewPresenter;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.views.activities.NavigationActivity;


@Completed
public class PostReviewFragment extends Fragment {

    private NavigationActivity navigationActivity;
    private AccommodationFacilityFragment accommodationFacilityFragment;
    private PostReviewPresenter postReviewPresenter;
    private TextInputLayout textInputTitle;
    private TextInputLayout textInputDescription;
    private TextInputLayout textInputDateOfStay;
    private RatingBar ratingBar;
    private RoundCornerProgressBar uploadingProgressBar;
    private FloatingActionButton buttonCalendar;
    private ImageView buttonBack;
    private ImageView buttonFirstImage;
    private ImageView buttonSecondImage;
    private ImageView buttonThirdImage;
    private View layout;
    private View postReviewContainer;
    private View thanksContainer;
    private Button buttonShare;
    private Button buttonExit;



    public PostReviewFragment(NavigationActivity startActivity,
                              AccommodationFacilityFragment accommodationFacilityFragment) {
        this.navigationActivity = startActivity;
        this.accommodationFacilityFragment = accommodationFacilityFragment;
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        layout = view.findViewById(R.id.fragment_post_review);
        postReviewContainer = view.findViewById(R.id.fragment_post_review_post_container);
        thanksContainer = view.findViewById(R.id.fragment_post_review_thank_you_container);
        ratingBar = view.findViewById(R.id.fragment_post_review_rating);
        textInputTitle = view.findViewById(R.id.fragment_post_review_text_input_title_layout);
        textInputDescription = view.findViewById(R.id.fragment_post_review_text_input_description_layout);
        textInputDateOfStay = view.findViewById(R.id.fragment_post_review_text_input_date_of_stay_layout);
        buttonBack = view.findViewById(R.id.fragment_post_review_button_back);
        buttonFirstImage = view.findViewById(R.id.fragment_post_review_first_image_button);
        buttonSecondImage = view.findViewById(R.id.fragment_post_review_second_image_button);
        buttonThirdImage = view.findViewById(R.id.fragment_post_review_third_image_button);
        buttonCalendar = view.findViewById(R.id.fragment_post_review_date_of_stay_button);
        buttonShare = view.findViewById(R.id.fragment_post_review_button_share);
        buttonExit = view.findViewById(R.id.fragment_post_review_button_exit);
        uploadingProgressBar = view.findViewById(R.id.fragment_post_review_uploading_progress_bar);
        postReviewPresenter = new PostReviewPresenter(this);
        listenToRatingBarChangeEvents();
        listenToClickEvents();
        listenToTextChangedEvents();
    }

    private void listenToClickEvents() {
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postReviewPresenter.onLayoutClicked();
            }
        });
        buttonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postReviewPresenter.onButtonCalendarClicked();
            }
        });
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postReviewPresenter.onButtonShareClicked();
            }
        });
        buttonFirstImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postReviewPresenter.onButtonFirstImageClicked();
            }
        });
        buttonSecondImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postReviewPresenter.onButtonSecondImageClicked();
            }
        });
        buttonThirdImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postReviewPresenter.onButtonThirdImageClicked();
            }
        });
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postReviewPresenter.onButtonBackClicked();
            }
        });
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postReviewPresenter.onButtonExitClicked();
            }
        });
    }

    private void listenToRatingBarChangeEvents() {
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                postReviewPresenter.onRatingChanged(rating);
            }
        });
    }

    private void listenToTextChangedEvents() {
        EditText titleEditText = textInputTitle.getEditText();
        EditText descriptionEditText = textInputDescription.getEditText();
        if(titleEditText != null) {
            titleEditText.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    postReviewPresenter.onTitleChanged();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }

            });
        }
        if(descriptionEditText != null) {
            descriptionEditText.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    postReviewPresenter.onDescriptionChanged();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }

            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        postReviewPresenter.onActivityResult(requestCode, resultCode, data);
    }

    public void cleanTextInputErrors() {
        textInputTitle.setErrorEnabled(false);
        textInputDescription.setErrorEnabled(false);
        textInputDateOfStay.setErrorEnabled(false);
    }

    public void showThanksView() {
        buttonBack.setVisibility(View.GONE);
        postReviewContainer.animate().alpha(0).setDuration(1000).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                postReviewContainer.setVisibility(View.GONE);
                thanksContainer.setVisibility(View.VISIBLE);
                thanksContainer.animate().alpha(1).setDuration(1000);
            }
        });
    }

    public NavigationActivity getNavigationActivity() {
        return navigationActivity;
    }

    public AccommodationFacility getAccommodationFacility() {
        return accommodationFacilityFragment.getAccommodationFacility();
    }

    public AccommodationFacilityFragment getAccommodationFacilityFragment() {
        return accommodationFacilityFragment;
    }

    public String getTitle() {
        EditText titleEditText = textInputTitle.getEditText();
        if(titleEditText == null) return "";
        return titleEditText.getText().toString();
    }

    public String getDescription() {
        EditText descriptionEditText = textInputDescription.getEditText();
        if(descriptionEditText == null) return "";
        return descriptionEditText.getText().toString();
    }

    public String getDateOfStay() {
        EditText dateOfStayEditText = textInputDateOfStay.getEditText();
        if(dateOfStayEditText == null) return "";
        return dateOfStayEditText.getText().toString();
    }

    public int getRating() {
        return (int) ratingBar.getRating();
    }

    public void setTextInputTitleError(String error) {
        textInputTitle.setErrorEnabled(true);
        textInputTitle.setError(error);
    }

    public void setTextInputDescriptionError(String error) {
        textInputDescription.setErrorEnabled(true);
        textInputDescription.setError(error);
    }

    public void setTextInputDateOfStayError(String error) {
        textInputDateOfStay.setErrorEnabled(true);
        textInputDateOfStay.setError(error);
    }

    public void setVisibilityUploadingProgressBar(boolean visibility) {
        if(visibility) {
            uploadingProgressBar.setVisibility(View.VISIBLE);
        } else {
            uploadingProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void addProgressUploadingProgressBar(int progress) {
        uploadingProgressBar.setProgress(uploadingProgressBar.getProgress()+progress);
    }

    public void setMaxProgressUploadingProgressBar(int maxProgress) {
        uploadingProgressBar.setMax(maxProgress);
    }

    public void setRatingBarRating(float rating) {
        ratingBar.setRating(rating);
    }

    public void setTextDateOfStay(String text) {
        EditText dateOfStayEditText = textInputDateOfStay.getEditText();
        if(dateOfStayEditText != null) {
            dateOfStayEditText.setText(text);
        }
    }

    public void setEnabledButtonShare(boolean enabled){
        buttonShare.setEnabled(enabled);
    }

    public void setFirstImagePreview(Drawable drawable){
        buttonFirstImage.setBackground(drawable);
    }

    public void setSecondImagePreview(Drawable drawable){
        buttonSecondImage.setBackground(drawable);
    }

    public void setThirdImagePreview(Drawable drawable){
        buttonThirdImage.setBackground(drawable);
    }

    public void setCounterEnabledTextInputTitle(boolean enabled) {
        textInputTitle.setCounterEnabled(enabled);
    }

    public void setCounterEnabledTextInputDescription(boolean enabled) {
        textInputDescription.setCounterEnabled(enabled);
    }

    public void setVisibilityFirstImageBox(boolean visibility) {
        if(visibility) {
            buttonFirstImage.setImageDrawable(ContextCompat.getDrawable(
                        navigationActivity,
                        R.drawable.ic_image_box
                    )
            );
        } else {
            buttonFirstImage.setImageDrawable(null);
        }
    }

    public void setVisibilitySecondImageBox(boolean visibility) {
        if(visibility) {
            buttonSecondImage.setImageDrawable(ContextCompat.getDrawable(
                        navigationActivity,
                        R.drawable.ic_image_box
                    )
            );
        } else {
            buttonSecondImage.setImageDrawable(null);
        }
    }

    public void setVisibilityThirdImageBox(boolean visibility) {
        if(visibility) {
            buttonThirdImage.setImageDrawable(ContextCompat.getDrawable(
                        navigationActivity,
                        R.drawable.ic_image_box
                    )
            );
        } else {
            buttonThirdImage.setImageDrawable(null);
        }
    }

}