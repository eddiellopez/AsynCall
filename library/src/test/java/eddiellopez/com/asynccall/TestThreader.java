package eddiellopez.com.asynccall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import java.util.concurrent.ExecutorService;

class TestThreader extends Threader<String> {

    protected TestThreader(
            @NonNull ExecutorService executor,
            @Nullable OnExceptionHandler onExceptionHandler,
            @Nullable LifecycleOwner lifecycleOwner,
            DeliveryProcedure<String> deliveryProcedure
    ) {
        super(executor, onExceptionHandler, lifecycleOwner, deliveryProcedure);
    }

    @Override
    public void start() {
        throw new UnsupportedOperationException();
    }
}
