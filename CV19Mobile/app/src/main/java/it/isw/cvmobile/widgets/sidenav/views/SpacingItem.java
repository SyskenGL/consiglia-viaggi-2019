package it.isw.cvmobile.widgets.sidenav.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import org.jetbrains.annotations.NotNull;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.widgets.sidenav.MenuAdapter;


@Completed
public class SpacingItem extends ItemView <SpacingItem.ViewHolder> {

    private int spaceValueInDp;



    public SpacingItem(int spaceDp) {
        this.spaceValueInDp = spaceDp;
    }

    @Override
    public ViewHolder createViewHolder(@NotNull ViewGroup viewGroup) {
        Context context = viewGroup.getContext();
        View view = new View(context);
        int height = (int) (context.getResources().getDisplayMetrics().density * spaceValueInDp);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        return new ViewHolder(view);
    }

    @Override
    public void bindViewHolder(ViewHolder ViewHolder) {
        // ...
    }

    @Override
    public boolean isSelectable() {
        return false;
    }



    static class ViewHolder extends MenuAdapter.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
        }

    }

}