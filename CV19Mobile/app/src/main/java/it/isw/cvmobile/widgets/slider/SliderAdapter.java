package it.isw.cvmobile.widgets.slider;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.wang.avi.AVLoadingIndicatorView;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import it.isw.cvmobile.R;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;


@Completed
public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderViewHolder> {

    private final List<SliderItem> sliderItems;
    private View.OnTouchListener touchListener;
    private View.OnClickListener clickListener;



    public SliderAdapter(List<SliderItem> sliderItems) {
        this.sliderItems = sliderItems;
    }

    @Override
    public SliderViewHolder onCreateViewHolder(@NotNull ViewGroup parent) {
        View view = View.inflate(parent.getContext(), R.layout.slider_image_item, null);
        SliderViewHolder sliderViewHolder = new SliderViewHolder(view);
        if(touchListener != null) {
            sliderViewHolder.itemView.setOnTouchListener(touchListener);
        }
        if(clickListener != null) {
            sliderViewHolder.itemView.setOnClickListener(clickListener);
        }
        return sliderViewHolder;
    }

    @Override
    @RequiresInternetConnection
    public void onBindViewHolder(@NotNull final SliderViewHolder viewHolder, int position) {
        SliderItem sliderItem = sliderItems.get(position);
        viewHolder.loadingSpinner.setVisibility(View.VISIBLE);
        viewHolder.image.setVisibility(View.INVISIBLE);
        RequestOptions requestOptions = RequestOptions.fitCenterTransform()
                .transform(new RoundedCorners(5))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(viewHolder.itemView.getWidth(), viewHolder.itemView.getHeight());
        RequestManager imageRequestManager = Glide.with(viewHolder.itemView);
        imageRequestManager.load(sliderItem.getImageUrl()).listener(new RequestListener<Drawable>() {

            @Override
            public boolean onLoadFailed(@Nullable GlideException e,
                                        Object model,
                                        Target<Drawable> target,
                                        boolean isFirstResource) {
                viewHolder.loadingSpinner.setVisibility(View.GONE);
                viewHolder.image.setImageResource(R.drawable.ic_error);
                viewHolder.image.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onResourceReady(final Drawable resource,
                                           Object model,
                                           Target<Drawable> target,
                                           DataSource dataSource,
                                           boolean isFirstResource) {
                viewHolder.loadingSpinner.setVisibility(View.GONE);
                viewHolder.image.setImageDrawable(resource);
                viewHolder.image.setVisibility(View.VISIBLE);
                return true;
            }

        }).apply(requestOptions).into(viewHolder.image);
    }

    @Override
    public int getCount() {
        return sliderItems.size();
    }

    public void setTouchListener(View.OnTouchListener touchListener) {
        this.touchListener = touchListener;
    }

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }



    static class SliderViewHolder extends SliderViewAdapter.ViewHolder {

        private ImageView image;
        private AVLoadingIndicatorView loadingSpinner;



        private SliderViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.slider_image_item_image_view);
            loadingSpinner = itemView.findViewById(R.id.slider_image_item_loading_spinner);
        }

    }

}
