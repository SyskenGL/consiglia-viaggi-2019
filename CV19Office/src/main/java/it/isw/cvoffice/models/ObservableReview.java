package it.isw.cvoffice.models;


public class ObservableReview implements ObservableValue<Review> {

    private Review value;
    private OnObservableValueChangedListener<? super Review> listener;


    @Override
    public void setValue(Review value) {
        this.value = value;
        if(listener != null) {
            listener.onObservableValueChanged(value);
        }
    }

    @Override
    public void setOnObservableValueChangedListener(OnObservableValueChangedListener<? super Review> listener) {
        this.listener = listener;
    }

    @Override
    public Review getValue() {
        return value;
    }

}