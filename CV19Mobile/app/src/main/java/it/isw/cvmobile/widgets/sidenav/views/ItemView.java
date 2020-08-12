package it.isw.cvmobile.widgets.sidenav.views;

import android.view.ViewGroup;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.widgets.sidenav.MenuAdapter;


@Completed
public abstract class ItemView <T extends MenuAdapter.ViewHolder> {

    private boolean selected;



    public abstract T createViewHolder(ViewGroup parent);

    public abstract void bindViewHolder(T ViewHolder);

    public ItemView setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isSelectable() {
        return !selected;
    }

}
