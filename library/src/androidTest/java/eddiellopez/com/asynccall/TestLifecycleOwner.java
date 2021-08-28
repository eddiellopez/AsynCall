package eddiellopez.com.asynccall;

import static org.junit.Assert.fail;

import android.util.ArraySet;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

/**
 * A test lifecycle owner class.
 */
class TestLifecycleOwner implements LifecycleOwner {

    final TestLifecycle mTestLifecycle;

    @SuppressWarnings("SameParameterValue")
    TestLifecycleOwner(Lifecycle.State initialState) {
        mTestLifecycle = new TestLifecycle(initialState);
    }

    private final Set<LifecycleObserver> mObservers = new ArraySet<>();

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mTestLifecycle;
    }

    TestLifecycleOwner stateUp() {
        switch (mTestLifecycle.getCurrentState()) {
            case DESTROYED:
                // We revive this object, no event happens here
                mTestLifecycle.setState(Lifecycle.State.INITIALIZED);
                break;
            case INITIALIZED:
                mTestLifecycle.setState(Lifecycle.State.CREATED);
                fireEvent(Lifecycle.Event.ON_CREATE);
                break;
            case CREATED:
                mTestLifecycle.setState(Lifecycle.State.STARTED);
                fireEvent(Lifecycle.Event.ON_START);
                break;
            case STARTED:
                mTestLifecycle.setState(Lifecycle.State.RESUMED);
                fireEvent(Lifecycle.Event.ON_RESUME);
                break;
            case RESUMED:
                throw new IllegalArgumentException("Already RESUMED, can't go up!");
        }
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    TestLifecycleOwner stateDown() {
        switch (mTestLifecycle.getCurrentState()) {
            case DESTROYED:
            case INITIALIZED:
                throw new IllegalArgumentException("Already DESTROYED, can't go down!");
                // This is been recalled, no event
            case RESUMED:
                mTestLifecycle.setState(Lifecycle.State.STARTED);
                fireEvent(Lifecycle.Event.ON_PAUSE);
                break;
            case STARTED:
                mTestLifecycle.setState(Lifecycle.State.CREATED);
                fireEvent(Lifecycle.Event.ON_STOP);
                break;
            case CREATED:
                mTestLifecycle.setState(Lifecycle.State.DESTROYED);
                fireEvent(Lifecycle.Event.ON_DESTROY);
                break;
        }
        return this;
    }

    private void fireEvent(final Lifecycle.Event eventToFire) {
        for (LifecycleObserver mObserver : mObservers) {
            Arrays.stream(mObserver.getClass().getDeclaredMethods())
                    .peek(method -> method.setAccessible(true))
                    .filter(method -> {
                        final OnLifecycleEvent annotation = method.getAnnotation(OnLifecycleEvent.class);
                        return annotation != null && annotation.value() == eventToFire;
                    }).forEach(method -> fireEvent(eventToFire, mObserver, method));
        }
    }

    private void fireEvent(Lifecycle.Event eventToFire, LifecycleObserver mObserver, Method method) {
        try {
            method.invoke(mObserver);
        } catch (IllegalAccessException | InvocationTargetException e) {
            fail("Couldn't get to method for event: " + eventToFire);
        }
    }

    private class TestLifecycle extends Lifecycle {
        State mState;

        TestLifecycle(State initialState) {
            this.mState = initialState;
        }

        @Override
        public void addObserver(@NonNull LifecycleObserver observer) {
            mObservers.add(observer);
        }

        @Override
        public void removeObserver(@NonNull LifecycleObserver observer) {
            mObservers.remove(observer);
        }

        @NonNull
        @Override
        public State getCurrentState() {
            return mState;
        }

        void setState(State state) {
            this.mState = state;
        }
    }
}
