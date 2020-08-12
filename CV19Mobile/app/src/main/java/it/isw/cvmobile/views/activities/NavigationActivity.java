package it.isw.cvmobile.views.activities;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.widgets.sidenav.views.ItemView;
import it.isw.cvmobile.widgets.sidenav.MenuAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.scwang.wave.MultiWaveHeader;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import com.yarolegovich.slidingrootnav.callback.DragStateListener;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.List;
import it.isw.cvmobile.R;
import it.isw.cvmobile.presenters.activities.NavigationPresenter;
import it.isw.cvmobile.widgets.sidenav.views.MenuItem;
import it.isw.cvmobile.widgets.sidenav.listeners.OnItemSelectedListener;
import it.isw.cvmobile.widgets.sidenav.views.SpacingItem;
import jp.wasabeef.blurry.Blurry;
import pub.devrel.easypermissions.EasyPermissions;


@Completed
public class NavigationActivity extends FullScreenActivity implements EasyPermissions.PermissionCallbacks {

    public final static int BUTTON_HOME = 0;
    public final static int BUTTON_MY_PROFILE = 1;
    public final static int BUTTON_MY_FAVORITES = 2;
    public final static int BUTTON_MY_REVIEWS = 3;
    public final static int BUTTON_MY_NOTIFICATIONS = 4;
    public final static int BUTTON_MY_HISTORY = 5;
    public final static int BUTTON_SIGN_OUT = 6;
    public final static int BUTTON_SIGN_IN = 1;

    private NavigationPresenter navigationPresenter;
    private SlidingRootNav sideNavigationMenu;
    private ImageView sideNavigationBackground;
    private MenuAdapter sideNavigationMenuAdapter;
    private RecyclerView sideNavigationMenuRecycler;
    private MultiWaveHeader waves;
    private MenuItem notificationLabel;
    private Toolbar toolbar;
    private String[] titles;
    private Drawable[] icons;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        toolbar = findViewById(R.id.activity_navigation_toolbar);
        waves = findViewById(R.id.activity_navigation_waves);
        sideNavigationMenu = new SlidingRootNavBuilder(this)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withToolbarMenuToggle(toolbar)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.side_nav_menu)
                .inject();
        sideNavigationBackground = findViewById(R.id.side_navigation_menu_background);
        sideNavigationMenuRecycler = findViewById(R.id.side_navigation_menu_recycle_view);
        sideNavigationMenuRecycler.setNestedScrollingEnabled(false);
        sideNavigationMenuRecycler.setLayoutManager(new LinearLayoutManager(this));
        navigationPresenter = new NavigationPresenter(this);
        initializeUserInterface();
    }

    @Override
    public void onStart() {
        super.onStart();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        navigationPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] perms, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, perms, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, perms, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        navigationPresenter.onPermissionsGranted(requestCode, perms);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        navigationPresenter.onPermissionsDenied(requestCode, perms);
    }

    private void initializeUserInterface() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(FullScreenActivity.SYSTEM_UI_FLAG);
        setViewHeight(waves, getScreenHeight()+200);
        if(toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setAlpha(255);
        }
        waves.animate().translationY(getScreenHeight()+200).setDuration(1500);
        navigationPresenter.onUserInterfaceInitialization();
        listenToSelectedEvents();
        listenToDragEvents();
    }

    private void listenToSelectedEvents() {
        sideNavigationMenuAdapter.setSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                navigationPresenter.onSideNavigationMenuAdapterItemSelected(position);
            }
        });
    }

    private void listenToDragEvents() {
        sideNavigationMenu.getLayout().addDragStateListener(new DragStateListener() {

            @Override
            public void onDragStart() {
                navigationPresenter.onMenuDragged(sideNavigationMenu.isMenuOpened());
            }

            @Override
            public void onDragEnd(boolean isMenuOpened) {}

        });
    }

    public void closeSideNavigationMenu() {
        sideNavigationMenu.closeMenu();
    }

    private ItemView createMenuLabel(int position) {
        return new MenuItem(icons[position], titles[position])
                .setDefaultItemIconTint(getApplicationContext().getColor(R.color.darkBlue))
                .setDefaultItemTextTint(getApplicationContext().getColor(R.color.darkBlue))
                .setDefaultFontFamily(ResourcesCompat.getFont(this, R.font.montserrat_bold))
                .setSelectedItemIconTint(getApplicationContext().getColor(R.color.peach))
                .setSelectedItemTextTint(getApplicationContext().getColor(R.color.peach))
                .setSelectedFontFamily(ResourcesCompat.getFont(this, R.font.montserrat_bold));
    }

    public void setSideNavigationBlurryBackground(View view, int radius, int sampling, int color) {
        Blurry.Composer composer = Blurry.with(this);
        composer.radius(radius);
        composer.sampling(sampling);
        composer.color(color);
        composer.async().capture(view).into(sideNavigationBackground);
    }

    public void setUserLoggedInNavigationMode() {
        setIcons(R.array.activity_navigation_icons_array_logged_in);
        setTitles(R.array.activity_navigation_titles_array_logged_in);
        sideNavigationMenuAdapter = new MenuAdapter(Arrays.asList(
                createMenuLabel(BUTTON_HOME).setSelected(true),
                createMenuLabel(BUTTON_MY_PROFILE),
                createMenuLabel(BUTTON_MY_FAVORITES),
                createMenuLabel(BUTTON_MY_REVIEWS),
                (notificationLabel = (MenuItem) createMenuLabel(BUTTON_MY_NOTIFICATIONS)),
                createMenuLabel(BUTTON_MY_HISTORY),
                new SpacingItem(50),
                createMenuLabel(BUTTON_SIGN_OUT)));
        sideNavigationMenuRecycler.setAdapter(sideNavigationMenuAdapter);
        sideNavigationMenuAdapter.setSelected(BUTTON_HOME);
    }

    public void setUserNotLoggedInNavigationMode() {
        setIcons(R.array.activity_navigation_icons_array_not_logged_in);
        setTitles(R.array.activity_navigation_titles_array_not_logged_in);
        sideNavigationMenuAdapter = new MenuAdapter(Arrays.asList(
                createMenuLabel(BUTTON_HOME).setSelected(true),
                new SpacingItem(220),
                createMenuLabel(BUTTON_SIGN_IN)));
        sideNavigationMenuRecycler.setAdapter(sideNavigationMenuAdapter);
        sideNavigationMenuAdapter.setSelected(BUTTON_HOME);
    }

    public void setNotificationAlertIcon() {
        notificationLabel.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_notification_alert));
        notificationLabel.drawIcon();
    }

    public void setNotificationDefaultIcon() {
        notificationLabel.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_notification));
        notificationLabel.drawIcon();
    }

    private void setTitles(int titlesId) {
        titles = getResources().getStringArray(titlesId);
    }

    private void setIcons(int iconsId) {
        TypedArray arrayIcons = getResources().obtainTypedArray(iconsId);
        icons = new Drawable[arrayIcons.length()];
        for (int iconIndex = 0; iconIndex < arrayIcons.length(); iconIndex++) {
            int resourceId = arrayIcons.getResourceId(iconIndex, 0);
            if (resourceId != 0) {
                icons[iconIndex] = ContextCompat.getDrawable(this, resourceId);
            }
        }
        arrayIcons.recycle();
    }

    public void setToolbarVisibility(boolean visibility){
        if(visibility) {
            toolbar.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.GONE);
        }
    }

    public void setLockedNavigationMenu(boolean locked) {
        sideNavigationMenu.setMenuLocked(locked);
    }

}