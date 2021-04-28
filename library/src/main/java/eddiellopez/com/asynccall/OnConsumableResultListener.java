package eddiellopez.com.asynccall;


import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

/**
 * A listener to receive the result of the asynchronous operation.
 *
 * @param <T> The type of the result.
 */
@FunctionalInterface
public interface OnConsumableResultListener<T> extends OnResultListener {
    /**
     * Called in the UI Thread when the result is ready.
     *
     * @param result The result, if any, otherwise null.
     */
    @UiThread
    void onResult(@Nullable T result);
}
