package it.isw.cvoffice.models;

public interface ObservableValue<T> {

    void setValue(T value);

    void setOnObservableValueChangedListener(OnObservableValueChangedListener<? super T> listener);

    T getValue();


    interface OnObservableValueChangedListener<U> {
        void onObservableValueChanged(U newValue);
    }

}