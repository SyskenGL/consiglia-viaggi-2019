package it.isw.cvmobile.widgets.sidenav;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.widgets.sidenav.listeners.OnItemSelectedListener;
import it.isw.cvmobile.widgets.sidenav.views.ItemView;


@Completed
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private final List<ItemView> itemViews;
    private final Map<Class<? extends ItemView>, Integer> itemViewTypes;
    private final SparseArray<ItemView> sparseItemViews;
    private OnItemSelectedListener selectedListener;



    public MenuAdapter(List<ItemView> itemViews) {
        this.itemViews = itemViews;
        this.itemViewTypes = new HashMap<>();
        this.sparseItemViews = new SparseArray<>();
        processItemViewTypes();
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int viewType) {
        ViewHolder viewHolder = sparseItemViews.get(viewType).createViewHolder(viewGroup);
        viewHolder.menuAdapter = this;
        return viewHolder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        itemViews.get(position).bindViewHolder(viewHolder);
    }

    @Override
    public int getItemCount() {
        return itemViews.size();
    }

    @Override
    public int getItemViewType(int position) {
        Integer type = itemViewTypes.get(itemViews.get(position).getClass());
        return (type != null) ? type : 0;
    }

    private void processItemViewTypes() {
        int key = 0;
        for (ItemView itemView : itemViews) {
            if (!itemViewTypes.containsKey(itemView.getClass())) {
                itemViewTypes.put(itemView.getClass(), key);
                sparseItemViews.put(key, itemView);
                key++;
            }
        }
    }

    public void setSelected(int position) {
        ItemView itemViewSelected = itemViews.get(position);
        if (!itemViewSelected.isSelectable()) {
            return;
        }
        for (int itemViewIndex = 0; itemViewIndex < itemViews.size(); itemViewIndex++) {
            ItemView item = itemViews.get(itemViewIndex);
            if (item.isSelected()) {
                item.setSelected(false);
                notifyItemChanged(itemViewIndex);
                break;
            }
        }
        itemViewSelected.setSelected(true);
        notifyItemChanged(position);
        if (selectedListener != null) {
            selectedListener.onItemSelected(position);
        }
    }

    public void setSelectedListener(OnItemSelectedListener selectedListener) {
        this.selectedListener = selectedListener;
    }



    public static abstract class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private MenuAdapter menuAdapter;



        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            menuAdapter.setSelected(getAdapterPosition());
        }
    }

}