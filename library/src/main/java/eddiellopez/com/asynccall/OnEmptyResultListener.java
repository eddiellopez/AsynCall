package eddiellopez.com.asynccall;


import androidx.annotation.UiThread;

/**
 * A convenience listener type to receive the result of the asynchronous operation, when
 * the action to be executed does not return any value.
 */
@FunctionalInterface
public interface OnEmptyResultListener extends OnResultListener {

    /**
     * Called in the UI Thread when the result is ready.
     */
    @UiThread
    void onResult();
}
