package eddiellopez.com.asynccall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import java.util.concurrent.ExecutorService;

class TestThreader extends Threader<String> {

    protected TestThreader(
            @NonNull ExecutorService executor,
            @Nullable OnExceptionHandler onExceptionHandler,
            @Nullable LifecycleOwner lifecycleOwner
    ) {
        super(executor, onExceptionHandler, lifecycleOwner);
    }

    @Override
    public void start() {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    protected Runnable getDeliveryProcedure(String string) {
        throw new UnsupportedOperationException();
    }
}
