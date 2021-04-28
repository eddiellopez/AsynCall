package eddiellopez.com.asynccall;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

class EmptyThreader extends Threader<Void> {

    @NonNull
    private final Runnable runnable;

    @Nullable
    private final OnEmptyResultListener onEmptyResultListener;

    EmptyThreader(
            @NonNull ExecutorService executor,
            @Nullable OnExceptionHandler onExceptionHandler,
            @Nullable LifecycleOwner lifecycleOwner,
            @NonNull Runnable runnable,
            @Nullable OnEmptyResultListener onEmptyResultListener
    ) {
        super(executor, onExceptionHandler, lifecycleOwner);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(runnable,
                    "Threader cannot be constructed without a runnable task!");
        } else {
            throw new NullPointerException("Threader cannot be constructed without a runnable task!");
        }

        this.runnable = runnable;
        this.onEmptyResultListener = onEmptyResultListener;
    }

    @Override
    public void start() {
        // Wrap the runnable in a null returning callable.
        submit(() -> {
            runnable.run();
            return null;
        });
    }

    @NonNull
    @Override
    protected Runnable getDeliveryProcedure(Void aVoid) {
        return () -> {
            if (onEmptyResultListener != null) {
                onEmptyResultListener.onResult();
            }
        };
    }
}
