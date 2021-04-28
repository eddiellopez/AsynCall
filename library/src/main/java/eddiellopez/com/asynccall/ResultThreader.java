package eddiellopez.com.asynccall;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

// TODO: 3/9/21 Docs
class ResultThreader<T> extends Threader<T> {

    @NonNull
    private final Callable<T> callable;

    @Nullable
    private final OnConsumableResultListener<T> onConsumableResultListener;

    ResultThreader(
            @NonNull ExecutorService executor,
            @Nullable OnExceptionHandler onExceptionHandler,
            @Nullable LifecycleOwner lifecycleOwner,
            @NonNull Callable<T> callable,
            @Nullable OnConsumableResultListener<T> onConsumableResultListener
    ) {
        super(executor, onExceptionHandler, lifecycleOwner);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(callable,
                    "Threader cannot be constructed without a callable task!");
        } else {
            throw new NullPointerException("Threader cannot be constructed without a callable task!");
        }
        this.callable = callable;
        this.onConsumableResultListener = onConsumableResultListener;
    }

    @Override
    public void start() {
        submit(callable);
    }

    @Override
    @NonNull
    protected Runnable getDeliveryProcedure(final @Nullable T t) {
        return () -> {
            if (onConsumableResultListener != null) {
                onConsumableResultListener.onResult(t);
            }
        };
    }
}
