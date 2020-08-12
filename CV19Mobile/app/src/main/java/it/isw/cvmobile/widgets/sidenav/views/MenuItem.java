package it.isw.cvmobile.widgets.sidenav.views;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;
import it.isw.cvmobile.R;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.widgets.sidenav.MenuAdapter;


@Completed
public class MenuItem extends ItemView <MenuItem.ViewHolder> {

    private int selectedItemIconTint;
    private int selectedItemTextTint;
    private Typeface selectedFontFamily;
    private int defaultItemIconTint;
    private int defaultItemTextTint;
    private Typeface defaultFontFamily;
    private ViewHolder viewHolder;
    private Drawable icon;
    private String title;



    public MenuItem(Drawable icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    @Override
    public ViewHolder createViewHolder(@NotNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.side_nav_menu_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindViewHolder(@NotNull ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
        viewHolder.title.setText(title);
        viewHolder.icon.setImageDrawable(icon);
        if(isSelected()) {
            viewHolder.title.setTextColor(selectedItemTextTint);
            viewHolder.title.setTypeface(selectedFontFamily);
            viewHolder.icon.setColorFilter(selectedItemIconTint);
        } else {
            viewHolder.title.setTextColor(defaultItemTextTint);
            viewHolder.title.setTypeface(defaultFontFamily);
            viewHolder.icon.setColorFilter(defaultItemIconTint);
        }
    }

    public MenuItem setSelectedItemIconTint(int selectedItemIconTint) {
        this.selectedItemIconTint = selectedItemIconTint;
        return this;
    }

    public MenuItem setSelectedItemTextTint(int selectedItemTextTint) {
        this.selectedItemTextTint = selectedItemTextTint;
        return this;
    }

    public MenuItem setSelectedFontFamily(Typeface selectedFontFamily) {
        this.selectedFontFamily = selectedFontFamily;
        return this;
    }

    public MenuItem setDefaultItemIconTint(int defaultItemIconTint) {
        this.defaultItemIconTint = defaultItemIconTint;
        return this;
    }

    public MenuItem setDefaultItemTextTint(int defaultItemTextTint) {
        this.defaultItemTextTint = defaultItemTextTint;
        return this;
    }

    public MenuItem setDefaultFontFamily(Typeface defaultFontFamily) {
        this.defaultFontFamily = defaultFontFamily;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public int getSelectedItemTextTint() {
        return selectedItemTextTint;
    }

    public int getSelectedItemIconTint() {
        return selectedItemIconTint;
    }

    public Typeface getSelectedFontFamily() {
        return selectedFontFamily;
    }

    public int getDefaultItemIconTint() {
        return defaultItemIconTint;
    }

    public int getDefaultItemTextTint() {
        return defaultItemTextTint;
    }

    public Typeface getDefaultFontFamily() {
        return defaultFontFamily;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public void drawIcon() {
        viewHolder.icon.setImageDrawable(icon);
    }

    public void drawText() {
        viewHolder.title.setText(title);
    }



    static class ViewHolder extends MenuAdapter.ViewHolder {

        private ImageView icon;
        private TextView title;



        ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.side_nav_menu_item_icon);
            title = itemView.findViewById(R.id.side_nav_menu_item_title);
        }
    }

}
