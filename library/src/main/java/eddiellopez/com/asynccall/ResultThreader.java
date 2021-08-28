package eddiellopez.com.asynccall;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * A basic threader that runs an operation that always returns  a result.
 *
 * @param <T> The type of the result produced by the operation to run.
 */
class ResultThreader<T> extends Threader<T> {

    @NonNull
    private final Callable<T> callable;

    ResultThreader(
            @NonNull ExecutorService executor,
            @Nullable OnExceptionHandler onExceptionHandler,
            @Nullable LifecycleOwner lifecycleOwner,
            @NonNull Callable<T> callable,
            @Nullable OnConsumableResultListener<T> onConsumableResultListener
    ) {
        super(executor, onExceptionHandler, lifecycleOwner, (result) -> {
            if (onConsumableResultListener != null) {
                onConsumableResultListener.onResult(result);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(callable,
                    "Threader cannot be constructed without a callable task!");
        } else {
            throw new NullPointerException("Threader cannot be constructed without a callable task!");
        }
        this.callable = callable;
    }

    @Override
    public void start() {
        submit(callable);
    }
}
