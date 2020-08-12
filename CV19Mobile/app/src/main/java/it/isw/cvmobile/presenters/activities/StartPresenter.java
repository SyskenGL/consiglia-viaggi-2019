package it.isw.cvmobile.presenters.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import it.isw.cvmobile.R;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.aws.Cognito;
import it.isw.cvmobile.views.activities.NavigationActivity;
import it.isw.cvmobile.views.activities.StartActivity;
import it.isw.cvmobile.views.fragments.SignInFragment;
import it.isw.cvmobile.views.fragments.SignUpFragment;


@Completed
public class StartPresenter {

    private final StartActivity startActivity;



    public StartPresenter(final StartActivity startActivity, boolean openSignIn) {
        this.startActivity = startActivity;
        if(openSignIn) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openFragment(new SignInFragment(startActivity));
                }
            }, 500);
        }
    }

    public void onButtonSignInClicked() {
        startActivity.startClosingTransition(false, null);
        openFragment(new SignInFragment(startActivity));
    }

    public void onButtonSignUpClicked() {
        startActivity.startClosingTransition(false, null);
        openFragment(new SignUpFragment(startActivity));
    }

    public void onButtonSkipClicked() {
        Cognito cognito = Cognito.getInstance(startActivity);
        CognitoUserPool cognitoUserPool = cognito.getCognitoUserPool();
        cognitoUserPool.getCurrentUser().signOut();
        startActivity.startClosingTransition(true, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Intent intent = new Intent(startActivity, NavigationActivity.class);
                startActivity.startActivity(intent);
                startActivity.finish();
            }
        });
    }

    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = startActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.add(R.id.activity_start_fragment_container, fragment).commit();
    }

}