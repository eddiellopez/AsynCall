package eddiellopez.com.asynccall;

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
 * delivers the result in the UI thread. Use the {@link Builder} helper to parametrize
 * the execution.
 */
public class AsyncCall<T> extends AsyncTask<Callable<T>, Void, T> implements LifecycleObserver {

    private static final String TAG = "AsyncCall";
    private Executor executor = THREAD_POOL_EXECUTOR;
    @Nullable
    private Callable<T> what;
    private final OnEmptyResultListener mOnEmptyResultListener;
    private final OnResultListener<T> mOnResultListener;
    private Runnable runnable;
    private boolean deliver = true;

    /**
     * Builds an instance of this class.
     *
     * @param result A {@link OnResultListener}, whose accept will be called to deliver the result,
     *               in the UI thread.
     */
    @SuppressWarnings("WeakerAccess")
    public AsyncCall(@NonNull OnResultListener<T> result) {
        mOnResultListener = result;
        mOnEmptyResultListener = null;
    }

    private AsyncCall(Builder<T> builder) {
        if (builder.executor != null) {
            this.executor = builder.executor;
        }
        this.mOnResultListener = builder.onResultListener;
        this.mOnEmptyResultListener = builder.onEmptyResultListener;
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
        if (deliver) {
            if (mOnResultListener != null) {
                mOnResultListener.onResult(t);

            } else if (mOnEmptyResultListener != null) {
                mOnEmptyResultListener.onResult();
            } else {
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, "onPostExecute: No listener was set for this task.");
                }
            }
        }
    }

    /**
     * Starts executing the task.
     *
     * @throws IllegalStateException If no task was specified.
     */
    @SuppressWarnings("WeakerAccess")
    @UiThread
    public void start() {
        if (what != null) {
            //noinspection unchecked
            executeOnExecutor(executor, what);
        } else if (runnable != null) {
            //noinspection unchecked
            executeOnExecutor(executor, (Callable<T>) () -> {
                runnable.run();
                return null;
            });
        } else {
            throw new IllegalArgumentException("No task was specified!");
        }
    }

    /**
     * Executes the callable task in the default pool executor.
     *
     * @param task The task
     */
    @SuppressWarnings("WeakerAccess")
    public void exec(@NonNull Callable<T> task) {
        //noinspection unchecked
        executeOnExecutor(THREAD_POOL_EXECUTOR, task);
    }

    @OnLifecycleEvent(ON_STOP)
    void onStopped() {
        deliver = false;
    }

    /**
     * Builds {@link AsyncCall} objects.
     */
    @SuppressWarnings("WeakerAccess")
    public static class Builder<T> {
        private Executor executor;
        private OnResultListener<T> onResultListener;
        private Callable<T> task;
        private Runnable runnable;
        private LifecycleOwner lifecycleOwner;
        private OnEmptyResultListener onEmptyResultListener;

        /**
         * Specifies the task to run asynchronously.
         * Overrides any previously configured task.
         *
         * @param task The callable task.
         * @return This builder.
         */
        public Builder<T> withTask(Callable<T> task) {
            this.task = task;
            this.runnable = null;
            return this;
        }

        /**
         * A convenience method to specify a task for which a result is not expected.
         * Overrides any previously configured task.
         *
         * @param task The task as a runnable.
         * @return This builder.
         */
        public Builder<T> withTask(Runnable task) {
            this.runnable = task;
            this.task = null;
            return this;
        }

        /**
         * Specifies a result listener that delivers a result.
         * See also {@link #withEmptyResultListener(OnEmptyResultListener)}.
         *
         * @param listener The result listener.
         * @return This builder.
         */
        public Builder<T> withResultListener(@Nullable OnResultListener<T> listener) {
            this.onResultListener = listener;
            return this;
        }

        /**
         * Specifies a result listener that does not expects a result.
         * Note that a single listener will be called. See also {@link #withResultListener(OnResultListener)}.
         *
         * @param listener The result listener.
         * @return This builder.
         */
        public Builder<T> withEmptyResultListener(@Nullable OnEmptyResultListener listener) {
            this.onEmptyResultListener = listener;
            return this;
        }

        /**
         * Specifies an {@link Executor}. The default {@link AsyncTask#THREAD_POOL_EXECUTOR}
         * will be used if none specified.
         *
         * @param executor The executor.
         * @return This builder.
         */
        public Builder<T> withExecutor(Executor executor) {
            this.executor = executor;
            return this;
        }

        /**
         * Observes a lifecycle component to determine if the result should be delivered.
         * If the owner is STOPPED, the result won't be delivered.
         *
         * @param lifecycleOwner The lifecycle owner
         * @return This builder.
         */
        public Builder<T> observe(LifecycleOwner lifecycleOwner) {
            this.lifecycleOwner = lifecycleOwner;
            return this;
        }

        /**
         * Builds the AsyncCall.
         *
         * @return The new object
         */
        public AsyncCall<T> build() {
            return new AsyncCall<>(this);
        }
    }

    /**
     * A listener to receive the result of the asynchronous operation.
     *
     * @param <T> The type of the result.
     */
    @FunctionalInterface
    public interface OnResultListener<T> {
        /**
         * Called in the UI Thread when the result is ready.
         *
         * @param result The result, if any, otherwise null.
         */
        @UiThread
        void onResult(@Nullable T result);
    }

    /**
     * A convenience listener type to receive the result of the asynchronous operation, when
     * the action to be executed does not return any value.
     */
    @FunctionalInterface
    public interface OnEmptyResultListener {
        /**
         * Called in the UI Thread when the result is ready.
         */
        @UiThread
        void onResult();
    }
}
