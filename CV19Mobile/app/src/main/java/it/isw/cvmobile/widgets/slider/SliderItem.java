package it.isw.cvmobile.widgets.slider;

import it.isw.cvmobile.utils.annotations.Completed;


@Completed
public class SliderItem {

    private String imageUrl;


    public SliderItem(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
