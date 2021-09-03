package eddiellopez.com.asynccall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

class ThreaderFactory {

    public <T> Threader<T> from(
            @NonNull ExecutorService executor,
            @Nullable OnExceptionHandler onExceptionHandler,
            @Nullable LifecycleOwner lifecycleOwner,
            @NonNull Callable<T> callable,
            @Nullable OnConsumableResultListener<T> onConsumableResultListener
    ) {
        return new ResultThreader<>(
                executor,
                onExceptionHandler,
                lifecycleOwner,
                callable,
                onConsumableResultListener
        );
    }
}
