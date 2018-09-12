package eddiellopez.com.asyncall;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/**
 * Runs a {@link Callable} in a worker thread and
 * delivers the result in the UI thread.
 */
public class AsynCall<T> extends AsyncTask<Callable<T>, Void, T> {

    private static final String TAG = "AsynCall";
    private Executor executor = THREAD_POOL_EXECUTOR;
    @Nullable
    private Callable<T> what;
    private OnResultListener<T> mOnResultListener;
    private Runnable runnable;

    /**
     * Builds an instance of this class.
     *
     * @param result A {@link OnResultListener}, whose accept will be called to deliver the result, in the UI thread
     */
    public AsynCall(@NonNull OnResultListener<T> result) {
        mOnResultListener = result;
    }

    private AsynCall(Builder builder) {
        this.what = builder.task;
        this.executor = builder.executor;
        this.mOnResultListener = builder.onResultListener;
        this.runnable = builder.runnable;
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
        if (mOnResultListener != null) {
            mOnResultListener.onResult(t);
        }
    }

    /**
     * Starts executing the task.
     */
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
     * Builds {@link AsynCall} objects.
     */
    public class Builder {
        private Executor executor;
        private OnResultListener<T> onResultListener;
        private Callable<T> task;
        private Runnable runnable;

        /**
         * Specifies the task to run asynchronously.
         * Overwrites any previously configured task.
         *
         * @param task The task
         * @return This builder
         */
        public Builder withTask(Callable<T> task) {
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
        public Builder withTask(Runnable task) {
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
        public Builder withResultListener(@Nullable OnResultListener<T> listener) {
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
        public Builder withExecutor(Executor executor) {
            this.executor = executor;
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
        void onResult(T result);
    }
}
