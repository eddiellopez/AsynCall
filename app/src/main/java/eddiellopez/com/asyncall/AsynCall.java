package eddiellopez.com.asyncall;

import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import static android.arch.lifecycle.Lifecycle.Event.ON_STOP;

/**
 * Runs a {@link Callable} in a worker thread and
 * delivers the result in the UI thread.
 */
public class AsynCall<T> extends AsyncTask<Callable<T>, Void, T> implements LifecycleObserver {

    private static final String TAG = "AsynCall";
    private Executor executor = THREAD_POOL_EXECUTOR;
    @Nullable
    private Callable<T> what;
    private OnResultListener<T> mOnResultListener;
    private Runnable runnable;
    private boolean deliver = true;

    /**
     * Builds an instance of this class.
     *
     * @param result A {@link OnResultListener}, whose accept will be called to deliver the result, in the UI thread
     */
    public AsynCall(@NonNull OnResultListener<T> result) {
        mOnResultListener = result;
    }

    private AsynCall(Builder<T> builder) {
        if (builder.executor != null) {
            this.executor = builder.executor;
        }
        this.mOnResultListener = builder.onResultListener;
        this.what = builder.task;
        this.runnable = builder.runnable;

        if (builder.lifecycleOwner != null) {
            builder.lifecycleOwner.getLifecycle().addObserver(this);
        }
    }

    @Override
    protected T doInBackground(Callable<T>[] calls) {
        try {
            return calls[0].call();
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(T t) {
        if (mOnResultListener != null && deliver) {
            mOnResultListener.onResult(t);
        }
    }

    /**
     * Starts executing the task.
     */
    @UiThread
    public void start() {
        if (what != null) {
            //noinspection unchecked
            executeOnExecutor(executor, what);
        } else if (runnable != null) {
            executor.execute(runnable);
        } else {
            throw new IllegalArgumentException("No task was specified!");
        }
    }

    /**
     * Executes the callable task in the default pool executor.
     *
     * @param task The task
     */
    public void exec(@NonNull Callable<T> task) {
        //noinspection unchecked
        executeOnExecutor(THREAD_POOL_EXECUTOR, task);
    }

    @OnLifecycleEvent(ON_STOP)
    void onStopped() {
        deliver = false;
    }

    /**
     * Builds {@link AsynCall} objects.
     */
    public static class Builder<T> {
        private Executor executor;
        private OnResultListener<T> onResultListener;
        private Callable<T> task;
        private Runnable runnable;
        private LifecycleOwner lifecycleOwner;

        /**
         * Specifies the task to run asynchronously.
         * Overwrites any previously configured task.
         *
         * @param task The task
         * @return This builder
         */
        public Builder<T> withTask(Callable<T> task) {
            this.task = task;
            this.runnable = null;
            return this;
        }

        /**
         * A convenience method to specify a task for which a result is not expected.
         * Overwrites any previously configured task.
         *
         * @param task The task as a runnable
         * @return This builder
         */
        public Builder<T> withTask(Runnable task) {
            this.runnable = task;
            this.task = null;
            return this;
        }

        /**
         * Specifies the result listener.
         *
         * @param listener The result listener
         * @return This builder
         */
        public Builder<T> withResultListener(@Nullable OnResultListener<T> listener) {
            this.onResultListener = listener;
            return this;
        }

        /**
         * Specifies an {@link Executor}. The default {@link AsyncTask#THREAD_POOL_EXECUTOR}
         * will be used if none specified.
         *
         * @param executor The executor
         * @return This builder
         */
        public Builder<T> withExecutor(Executor executor) {
            this.executor = executor;
            return this;
        }

        /**
         * Observes a lifecycle component to determine if the result should be delivered.
         * If the owner is STOPPED, the result won't be delivered.
         *
         * @param lifecycleOwner The owner
         * @return This builder
         */
        private Builder<T> observe(LifecycleOwner lifecycleOwner) {
            this.lifecycleOwner = lifecycleOwner;
            return this;
        }

        /**
         * Builds an AsynCall.
         *
         * @return The new object
         */
        public AsynCall<T> build() {
            return new AsynCall<>(this);
        }
    }

    /**
     * A listener to receive the result of the asynchronous operation.
     *
     * @param <T> The type of the result
     */
    public interface OnResultListener<T> {
        /**
         * Called in the UI Thread when the result is ready.
         *
         * @param result The result
         */
        @UiThread
        void onResult(T result);
    }
}
