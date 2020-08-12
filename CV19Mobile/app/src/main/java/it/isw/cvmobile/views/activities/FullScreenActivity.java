package it.isw.cvmobile.views.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import it.isw.cvmobile.utils.annotations.Completed;


@Completed
public abstract class FullScreenActivity extends AppCompatActivity {

    public static final int SYSTEM_UI_FLAG;
    public static final RecyclerView.OnItemTouchListener ITEM_TOUCH_MASKER;


    private int screenHeight;
    private int screenWidth;



    static {
        ITEM_TOUCH_MASKER = new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                return true;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                // ...
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                // ...
            }
        };
        SYSTEM_UI_FLAG =  View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calculateScreenSize();
        setOnSystemUiVisibilityChangeListener();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(SYSTEM_UI_FLAG);
        }
    }

    private void calculateScreenSize() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(size);
        screenHeight = size.y;
        screenWidth = size.x;
    }

    public static void cleanFocus(@NotNull Activity activity) {
        View currentFocus = activity.getCurrentFocus();
        if(currentFocus != null) {
            currentFocus.clearFocus();
        }
    }

    public static void hideKeyboard(@NotNull Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null && inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    public static boolean isNetworkAvailable(@NotNull Activity activity) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    public static void setViewMargins(@NotNull View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            layoutParams.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public static void setViewHeight(@NotNull View view, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
    }

    public static void setViewWidth(@NotNull View view, int width) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = width;
        view.setLayoutParams(layoutParams);
    }

    public void setOnSystemUiVisibilityChangeListener() {
        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    View decorView = getWindow().getDecorView();
                    decorView.setSystemUiVisibility(SYSTEM_UI_FLAG);
                }
            }
        });
    }

    public int getScreenHeight(){
        return screenHeight;
    }

    public int getScreenWidth(){
        return screenWidth;
    }

}
