package eddiellopez.com.asynccall;


import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

/**
 * A listener to receive the result of the asynchronous operation.
 */
@FunctionalInterface
public interface OnExceptionHandler {
    /**
     * Called in the UI Thread when the result is ready.
     *
     * @param exception The exception thrown by the action.
     */
    @UiThread
    void onFailure(@Nullable Exception exception);
}
