package eddiellopez.com.asynccall;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//@RunWith(AndroidJUnit4.class)
public class AsyncInstrumentedTest {
//
//    private static final String TAG = "AsyncInstrumentedTest";
//    private Thread uiThread;
//    private ExecutorService mExecutorService;
//
//
//    @Before
//    public void setUp() {
//        uiThread = Looper.getMainLooper().getThread();
//        mExecutorService = Executors.newSingleThreadExecutor();
//    }
//
//    @After
//    public void tearDown() {
//        mExecutorService.shutdown();
//    }
//
//    @Test
//    public void rawVariant() {
//        Threader.OnResultListener<String> resultListener
//                = result -> assertThat(result, is(not(uiThread.getName())));
//        Callable<String> task = () -> Thread.currentThread().getName();
//
//        // Raw variant without builder
//        new Threader<>(resultListener).exec(task);
//    }
//
//    @Test
//    public void defaultPoolExecutor() {
//        final CountDownLatch latch = new CountDownLatch(1);
//
//        // The task to run
//        //noinspection Convert2MethodRef: Method Reference breaks the build
//        Callable<Thread> task = () -> Thread.currentThread();
//        // The result listener
//        Threader.OnResultListener<Thread> resultListener = threadName -> {
//            // Task should've been executed in a pool thread
//            assertThat("Thread can't be UI Thread",
//                    threadName, is(not(uiThread.getName())));
//            // Result should be delivered in the UI thread
//            assertThat("Results shall always be delivered on the UI Thread",
//                    Thread.currentThread(), is(uiThread));
//            latch.countDown();
//        };
//        // The call
//        new Builder<Thread>()
//                .async(task)
//                .post(resultListener)
//                .start()
//                .start();
//
//        try {
//            latch.await(2000, TimeUnit.MILLISECONDS);
//        } catch (InterruptedException e) {
//            // Nothing to do here
//        }
//        assertThat("Latch should have been counted down!", latch.getCount(), is(0L));
//    }
//
//    @Test
//    public void useRunnable() {
//        final CountDownLatch latch = new CountDownLatch(1);
//
//        // Use runnable without providing listeners
//        new Builder<Integer>()
//                .async(() -> {
//                    assertThat("A task should never run in the UI Thread!",
//                            Thread.currentThread(), is(not(uiThread)));
//                    latch.countDown();
//                })
//                .start()
//                .start();
//
//        try {
//            latch.await(2000, TimeUnit.MILLISECONDS);
//        } catch (InterruptedException e) {
//            // Nothing to do here
//        }
//        assertThat("Latch should have been counted down!", latch.getCount(), is(0L));
//    }
//
//    @Test
//    public void building() {
//        final CountDownLatch latch = new CountDownLatch(1);
//
//        // Use a runnable as task, yet provide a listener that expects results
//        new Builder<String>()
//                .async(() -> assertThat("A task should never run in the UI Thread!",
//                        Thread.currentThread(), is(not(uiThread))))
//                .post(result -> {
//                    assertThat("Null was expected here, yet we got: " + result,
//                            result, is(nullValue()));
//                    assertThat("Results shall always be delivered on the UI Thread",
//                            Thread.currentThread(), is(uiThread));
//                    latch.countDown();
//                })
//                .start()
//                .start();
//
//        try {
//            latch.await(2000, TimeUnit.MILLISECONDS);
//        } catch (InterruptedException e) {
//            // Nothing to do here
//        }
//        assertThat("Latch should have been counted down!", latch.getCount(), is(0L));
//    }
//
//    @Test
//    public void building3() {
//        final CountDownLatch latch = new CountDownLatch(1);
//
//        // Use a runnable as task, yet provide a listener that expects results
//        new Builder<String>()
//                .async(() -> {
//                    assertThat("A task should never run in the UI Thread!",
//                            Thread.currentThread(), is(not(uiThread)));
//                    return "OK";
//                })
//                .post(() -> {
//                    // Result ignored
//                    assertThat("Results shall always be delivered on the UI Thread",
//                            Thread.currentThread(), is(uiThread));
//                    latch.countDown();
//                })
//                .start()
//                .start();
//
//        try {
//            latch.await(2000, TimeUnit.MILLISECONDS);
//        } catch (InterruptedException e) {
//            // Nothing to do here
//        }
//        assertThat("Latch should have been counted down!", latch.getCount(), is(0L));
//    }
//
//    @Test
//    public void building2() {
//        final CountDownLatch latch = new CountDownLatch(1);
//
//        // Use a runnable as task, provide several actions and listeners
//        // We expect only the last pair is used.
//        //noinspection Convert2MethodRef: Method Reference breaks the build
//        new Builder<String>()
//                .async(() -> Thread.currentThread())
//                .async(() -> Log.d(TAG, Thread.currentThread().getName()))
//                .post(() -> {
//                    assertThat("This should've never been called!", 0, is(1));
//                    assertThat("Results shall always be delivered on the UI Thread",
//                            Thread.currentThread(), is(uiThread));
//                    latch.countDown();
//                })
//                .async(() -> Log.d("TEST", Thread.currentThread().getName()))
//                .async(() -> "OK")
//                .post(result -> {
//                    assertThat("Something NOT null was expected here!",
//                            result, is(not(nullValue())));
//                    assertThat("Results shall always be delivered on the UI Thread",
//                            Thread.currentThread(), is(uiThread));
//                    latch.countDown();
//                })
//                .start()
//                .start();
//
//        try {
//            latch.await(2000, TimeUnit.MILLISECONDS);
//        } catch (InterruptedException e) {
//            // Nothing to do here
//        }
//        assertThat("Latch should have been counted down!", latch.getCount(), is(0L));
//    }
//
//    @Test
//    public void executor() {
//        final CountDownLatch latch = new CountDownLatch(1);
//
//        //noinspection Convert2MethodRef: Method Reference breaks the build
//        new Builder<>()
//                .async(() -> Thread.currentThread())
//                .post(thread -> {
//                    assertThat("Results should always be delivered in the UI Thread!",
//                            uiThread, is(not(thread)));
//                    latch.countDown();
//                })
//                .withExecutor(mExecutorService)
//                .start()
//                .start();
//        try {
//            latch.await(2000, TimeUnit.MILLISECONDS);
//        } catch (InterruptedException e) {
//            // Nothing to do here
//        }
//        assertThat("Latch should have been counted down!", latch.getCount(), is(0L));
//    }
//
//    @Test
//    public void observer() {
//        TestLifecycleOwner testLifecycleOwner = new TestLifecycleOwner(Lifecycle.State.INITIALIZED);
//        testLifecycleOwner
//                .stateUp() // CREATED
//                .stateUp() // STARTED
//                .stateUp(); // RESUMED
//
//        assertThat("Test classes shouldn't fail tests!",
//                testLifecycleOwner.getLifecycle().getCurrentState(), is(Lifecycle.State.RESUMED));
//
//        // Hold this thought...
//        final CountDownLatch latch = new CountDownLatch(1);
//
//        new Builder<String>()
//                .async(() -> {
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        Log.e(TAG, "observer: Interrupted!", e);
//                    }
//                })
//                .post(result -> {
//                    // See await at the end of this method
//                    latch.countDown();
//                    assertThat("This thread should be the UI Thread!",
//                            uiThread, is(Thread.currentThread()));
//                    assertThat("Failed, this should never run since the " +
//                            "lifecycle owner is stopped!", 0, is(1));
//                })
//                .observe(testLifecycleOwner)
//                .start()
//                .start();
//
//        testLifecycleOwner
//                .stateDown(); // STARTED, shouldn't affect AsyncCall
//        assertThat("Test classes shouldn't fail tests!",
//                testLifecycleOwner.getLifecycle().getCurrentState(), is(Lifecycle.State.STARTED));
//        testLifecycleOwner.stateDown(); // STOPPED should prevent delivery.
//        assertThat("Test classes shouldn't fail tests!",
//                testLifecycleOwner.getLifecycle().getCurrentState(), is(Lifecycle.State.CREATED));
//
//        try {
//            latch.await(3000, TimeUnit.MILLISECONDS);
//        } catch (InterruptedException e) {
//            // Nothing to do here
//        }
//    }
//
//    /**
//     * A test lifecycle owner class.
//     */
//    static class TestLifecycleOwner implements LifecycleOwner {
//
//        final TestLifecycle mTestLifecycle;
//
//        @SuppressWarnings("SameParameterValue")
//        TestLifecycleOwner(Lifecycle.State initialState) {
//            mTestLifecycle = new TestLifecycle(initialState);
//        }
//
//        private final Set<LifecycleObserver> mObservers = new ArraySet<>();
//
//        @NonNull
//        @Override
//        public Lifecycle getLifecycle() {
//            return mTestLifecycle;
//        }
//
//        TestLifecycleOwner stateUp() {
//            switch (mTestLifecycle.getCurrentState()) {
//                case DESTROYED:
//                    // We revive this object, no event happens here
//                    mTestLifecycle.setState(Lifecycle.State.INITIALIZED);
//                    break;
//                case INITIALIZED:
//                    mTestLifecycle.setState(Lifecycle.State.CREATED);
//                    fireEvent(Lifecycle.Event.ON_CREATE);
//                    break;
//                case CREATED:
//                    mTestLifecycle.setState(Lifecycle.State.STARTED);
//                    fireEvent(Lifecycle.Event.ON_START);
//                    break;
//                case STARTED:
//                    mTestLifecycle.setState(Lifecycle.State.RESUMED);
//                    fireEvent(Lifecycle.Event.ON_RESUME);
//                    break;
//                case RESUMED:
//                    throw new IllegalArgumentException("Already RESUMED, can't go up!");
//            }
//            return this;
//        }
//
//        @SuppressWarnings("UnusedReturnValue")
//        TestLifecycleOwner stateDown() {
//            switch (mTestLifecycle.getCurrentState()) {
//                case DESTROYED:
//                case INITIALIZED:
//                    throw new IllegalArgumentException("Already DESTROYED, can't go down!");
//                    // This is been recalled, no event
//                case RESUMED:
//                    mTestLifecycle.setState(Lifecycle.State.STARTED);
//                    fireEvent(Lifecycle.Event.ON_PAUSE);
//                    break;
//                case STARTED:
//                    mTestLifecycle.setState(Lifecycle.State.CREATED);
//                    fireEvent(Lifecycle.Event.ON_STOP);
//                    break;
//                case CREATED:
//                    mTestLifecycle.setState(Lifecycle.State.DESTROYED);
//                    fireEvent(Lifecycle.Event.ON_DESTROY);
//                    break;
//            }
//            return this;
//        }
//
//        private void fireEvent(final Lifecycle.Event eventToFire) {
//            for (LifecycleObserver mObserver : mObservers) {
//                Method[] methods = mObserver.getClass().getDeclaredMethods();
//                for (Method method : methods) {
//                    method.setAccessible(true);
//                    Log.i(TAG, "fireEvent: Method: " + method.getName());
//                    OnLifecycleEvent annotation = method.getAnnotation(OnLifecycleEvent.class);
//                    if (annotation != null) {
//                        Lifecycle.Event event = annotation.value();
//                        if (event == eventToFire) {
//                            try {
//                                method.invoke(mObserver);
//                            } catch (IllegalAccessException | InvocationTargetException e) {
//                                fail("Couldn't get to method for event: " + eventToFire);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        private class TestLifecycle extends Lifecycle {
//            State mState;
//
//            TestLifecycle(State initialState) {
//                this.mState = initialState;
//            }
//
//            @Override
//            public void addObserver(@NonNull LifecycleObserver observer) {
//                mObservers.add(observer);
//            }
//
//            @Override
//            public void removeObserver(@NonNull LifecycleObserver observer) {
//                mObservers.remove(observer);
//            }
//
//            @NonNull
//            @Override
//            public State getCurrentState() {
//                return mState;
//            }
//
//            void setState(State state) {
//                this.mState = state;
//            }
//        }
//    }
}
