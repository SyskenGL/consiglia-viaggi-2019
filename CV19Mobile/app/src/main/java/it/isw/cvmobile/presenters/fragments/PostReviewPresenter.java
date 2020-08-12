package it.isw.cvmobile.presenters.fragments;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.DatePicker;
import androidx.fragment.app.FragmentManager;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.google.gson.JsonObject;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import it.isw.cvmobile.R;
import it.isw.cvmobile.dao.DAOFactory;
import it.isw.cvmobile.dao.interfaces.ReviewDAO;
import it.isw.cvmobile.models.AccommodationFacility;
import it.isw.cvmobile.models.User;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;
import it.isw.cvmobile.utils.aws.callbacks.LambdaResultsHandler;
import it.isw.cvmobile.views.activities.FullScreenActivity;
import it.isw.cvmobile.views.fragments.PostReviewFragment;
import it.isw.cvmobile.widgets.toast.MotionToast;
import it.isw.cvmobile.widgets.toast.MotionToastType;
import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;


public class PostReviewPresenter {

    private static final int MEDIA_FIRST_IMAGE_REQUEST_CODE = 6;
    private static final int MEDIA_SECOND_IMAGE_REQUEST_CODE = 7;
    private static final int MEDIA_THIRD_IMAGE_REQUEST_CODE = 8;
    private static final int STARTING_CALENDAR_YEAR = 2019;
    private static final int STARTING_CALENDAR_MONTH = Calendar.JANUARY;
    private static final int STARTING_CALENDAR_DAY = 1;

    private PostReviewFragment postReviewFragment;
    private Uri[] images;



    public PostReviewPresenter(PostReviewFragment postReviewFragment) {
        this.postReviewFragment = postReviewFragment;
        images = new Uri[3];
    }

    public void onButtonCalendarClicked() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                postReviewFragment.getNavigationActivity(), R.style.date_picker,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        if(month < 9){
                            postReviewFragment.setTextDateOfStay(
                                    year + "-0" + (month + 1) + "-" + dayOfMonth
                            );
                        }else{
                            postReviewFragment.setTextDateOfStay(
                                    year + "-" + (month + 1) + "-" + dayOfMonth
                            );
                        }
                    }
                },
                STARTING_CALENDAR_YEAR,
                STARTING_CALENDAR_MONTH,
                STARTING_CALENDAR_DAY
        );
        datePickerDialog.show();
    }

    public void onButtonShareClicked() {
        Resources resources = postReviewFragment.getNavigationActivity().getResources();
        postReviewFragment.cleanTextInputErrors();
        postReviewFragment.setEnabledButtonShare(false);
        String title = postReviewFragment.getTitle();
        String description = postReviewFragment.getDescription();
        String dateOfStay = postReviewFragment.getDateOfStay();
        String rating = String.valueOf(postReviewFragment.getRating());
        if(title.length() == 0) {
            postReviewFragment.setTextInputTitleError(
                    resources.getString(R.string.fragment_post_review_enter_title)
            );
            postReviewFragment.setEnabledButtonShare(true);
        } else if(title.length() < 30) {
            postReviewFragment.setTextInputTitleError(
                    resources.getString(R.string.fragment_post_review_invalid_title)
            );
            postReviewFragment.setEnabledButtonShare(true);
        } else if(description.length() == 0) {
            postReviewFragment.setTextInputDescriptionError(
                    resources.getString(R.string.fragment_post_review_enter_description)
            );
            postReviewFragment.setEnabledButtonShare(true);
        } else if(description.length() < 150) {
            postReviewFragment.setTextInputDescriptionError(
                    resources.getString(R.string.fragment_post_review_enter_invalid_description)
            );
            postReviewFragment.setEnabledButtonShare(true);
        } else if(dateOfStay.length() == 0) {
            postReviewFragment.setTextInputDateOfStayError(
                    resources.getString(R.string.fragment_post_review_enter_date_of_stay)
            );
            postReviewFragment.setEnabledButtonShare(true);
        } else {
            addReview(title, description, dateOfStay, rating);
        }
    }

    public void onButtonExitClicked() {
        FragmentManager fragmentManager = postReviewFragment.getFragmentManager();
        if(fragmentManager != null) {
            fragmentManager.popBackStackImmediate();
        }
    }

    public void onLayoutClicked() {
        FullScreenActivity.hideKeyboard(postReviewFragment.getNavigationActivity());
        FullScreenActivity.cleanFocus(postReviewFragment.getNavigationActivity());
    }

    public void onTitleChanged() {
        if(postReviewFragment.getTitle().length() >= 30) {
            postReviewFragment.setCounterEnabledTextInputTitle(false);
        } else {
            postReviewFragment.setCounterEnabledTextInputTitle(true);
        }
    }

    public void onDescriptionChanged() {
        if(postReviewFragment.getDescription().length() >= 150) {
            postReviewFragment.setCounterEnabledTextInputDescription(false);
        } else {
            postReviewFragment.setCounterEnabledTextInputDescription(true);
        }
    }

    public void onButtonBackClicked() {
        FragmentManager fragmentManager = postReviewFragment.getFragmentManager();
        if(fragmentManager != null) {
            fragmentManager.popBackStackImmediate();
            if(fragmentManager.getBackStackEntryCount() == 0) {
                postReviewFragment.getNavigationActivity().setLockedNavigationMenu(false);
                postReviewFragment.getNavigationActivity().setToolbarVisibility(true);
            }
        }
    }

    public void onButtonFirstImageClicked() {
        if(images[0] == null) {
            openGallery(MEDIA_FIRST_IMAGE_REQUEST_CODE);
        } else {
            images[0] = null;
            postReviewFragment.setFirstImagePreview(null);
            postReviewFragment.setVisibilityFirstImageBox(true);
        }
    }

    public void onButtonSecondImageClicked() {
        if(images[1] == null) {
            openGallery(MEDIA_SECOND_IMAGE_REQUEST_CODE);
        } else {
            images[1] = null;
            postReviewFragment.setSecondImagePreview(null);
            postReviewFragment.setVisibilitySecondImageBox(true);
        }
    }

    public void onButtonThirdImageClicked() {
        if(images[2] == null) {
            openGallery(MEDIA_THIRD_IMAGE_REQUEST_CODE);
        } else {
            images[2] = null;
            postReviewFragment.setThirdImagePreview(null);
            postReviewFragment.setVisibilityThirdImageBox(true);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            Drawable preview = getPreview(imageUri);
            if(preview == null) {
                MotionToast.display(
                        postReviewFragment.getNavigationActivity(),
                        R.string.toast_error_unknown_error,
                        MotionToastType.ERROR_MOTION_TOAST
                );
            } else if(isUriDuplicated(imageUri)) {
                MotionToast.display(
                        postReviewFragment.getNavigationActivity(),
                        R.string.toast_warning_duplicate_images,
                        MotionToastType.WARNING_MOTION_TOAST
                );
            } else {
                switch (requestCode) {
                    case MEDIA_FIRST_IMAGE_REQUEST_CODE:
                        images[0] = imageUri;
                        postReviewFragment.setVisibilityFirstImageBox(false);
                        postReviewFragment.setFirstImagePreview(preview);
                        break;
                    case MEDIA_SECOND_IMAGE_REQUEST_CODE:
                        images[1] = imageUri;
                        postReviewFragment.setVisibilitySecondImageBox(false);
                        postReviewFragment.setSecondImagePreview(preview);
                        break;
                    case MEDIA_THIRD_IMAGE_REQUEST_CODE:
                        images[2] = imageUri;
                        postReviewFragment.setVisibilityThirdImageBox(false);
                        postReviewFragment.setThirdImagePreview(preview);
                        break;
                }
            }
        }
    }

    private void openGallery(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(EXTERNAL_CONTENT_URI, "image/*");
        postReviewFragment.startActivityForResult(intent, requestCode);
    }

    public void onRatingChanged(float rating) {
        if(rating == 0) {
            postReviewFragment.setRatingBarRating(1);
        }
    }

    private boolean isUriDuplicated(Uri uri) {
        for(Uri image: images) {
            if(image != null && image.equals(uri)) {
                return true;
            }
        }
        return false;
    }

    private Drawable getPreview(Uri uri) {
        try {
            ContentResolver contentResolver = postReviewFragment.getNavigationActivity().getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);
            return Drawable.createFromStream(inputStream, uri.toString());
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    private int getImagesCount() {
        int imagesCount = 0;
        for(Uri uri: images) {
            if(uri != null) {
                imagesCount += 1;
            }
         }
        return imagesCount;
    }

    private void handleAddReviewSuccess(String reviewId, String accommodationFacilityId) {
        if(getImagesCount() > 0) {
            postReviewFragment.setVisibilityUploadingProgressBar(true);
            postReviewFragment.setMaxProgressUploadingProgressBar(getImagesCount());
            int imageIndex = 1;
            for(Uri image: images) {
                if(image != null) {
                    addReviewImage(image, reviewId, accommodationFacilityId, imageIndex);
                    imageIndex+=1;
                }
            }
        } else {
            postReviewFragment.showThanksView();
        }
    }

    @RequiresInternetConnection
    private void addReviewImage(Uri image,
                                String reviewId,
                                String accommodationFacilityId,
                                final int imageIndex) {
        final ReviewDAO reviewDAO = DAOFactory.getReviewDAO(postReviewFragment.getNavigationActivity());
        reviewDAO.addReviewImage(image, reviewId, accommodationFacilityId, imageIndex, new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                postReviewFragment.addProgressUploadingProgressBar(1);
                System.out.println(imageIndex + "   " +getImagesCount());
                if(imageIndex == getImagesCount()) {
                    postReviewFragment.showThanksView();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                // ...
            }

            @Override
            public void onError(int id, Exception ex) {
                postReviewFragment.addProgressUploadingProgressBar(1);
                System.out.println(imageIndex + "   " +getImagesCount());
                if(imageIndex == getImagesCount()) {
                    postReviewFragment.showThanksView();
                }
            }

        });
    }

    @RequiresInternetConnection
    private void addReview(String title, String description, String dateOfStay, String rating) {
        if(FullScreenActivity.isNetworkAvailable(postReviewFragment.getNavigationActivity())) {
            final AccommodationFacility accommodationFacility = postReviewFragment.getAccommodationFacility();
            final ReviewDAO reviewDAO = DAOFactory.getReviewDAO(postReviewFragment.getNavigationActivity());
            final String accommodationFacilityId = accommodationFacility.getAccommodationFacilityId();
            User user = User.getInstance();
            reviewDAO.addReview(
                    title,
                    description,
                    rating,
                    dateOfStay,
                    getImagesCount(),
                    accommodationFacilityId,
                    user.getUserId(),
                    new LambdaResultsHandler() {

                        @Override
                        public void onSuccess(JsonObject results) {
                            postReviewFragment.getAccommodationFacilityFragment().setButtonWriteReviewVisibility(false);
                            postReviewFragment.getAccommodationFacility().setCommented(true);
                            String reviewId = results.get("id").getAsString();
                            handleAddReviewSuccess(reviewId, accommodationFacilityId);
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            MotionToast.display(
                                    postReviewFragment.getNavigationActivity(),
                                    R.string.toast_error_unknown_error,
                                    MotionToastType.ERROR_MOTION_TOAST
                            );
                            postReviewFragment.setEnabledButtonShare(true);
                        }

                    });
        } else {
            MotionToast.display(
                    postReviewFragment.getNavigationActivity(),
                    R.string.toast_warning_internet_connection,
                    MotionToastType.WARNING_MOTION_TOAST
            );
            postReviewFragment.setEnabledButtonShare(true);
        }
    }

}