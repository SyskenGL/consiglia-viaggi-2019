package it.isw.cvmobile.presenters.activities;

import android.content.Intent;
import android.os.Handler;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.views.activities.OpeningActivity;
import it.isw.cvmobile.views.activities.StartActivity;


@Completed
public class OpeningPresenter {

    private final OpeningActivity openingActivity;



    public OpeningPresenter(OpeningActivity openingActivity) {
        this.openingActivity = openingActivity;
    }

    public void onPresentationEnd() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(openingActivity, StartActivity.class);
                openingActivity.startActivity(intent);
                openingActivity.finish();
            }
        }, 1200);
    }

}
