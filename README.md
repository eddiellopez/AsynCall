# AsyncCall
Asynchronous and concurrent execution tools for Android.  

AsyncCall will execute a task in a worker thread and deliver the result in the UI Thread. The `Executor` to use will be the default  `AsyncTask` pool executor unless other is specified.

## Usage examples

Use the `Builder` class to configure the `AsincCall` as follows:
```
  // The task to run in the form of a Callable
  Callable<Thread> task = Thread::currentThread;
  // The result listener
  AsyncCall.OnResultListener<Thread> resultListener = threadName -> {
      // Task will be executed in a pool thread
      assertThat("Thread can't be UI Thread",
              threadName, is(not(uiThread.getName())));
      // Result will be delivered in the UI thread
      assertThat(Thread.currentThread(), is(uiThread));
  };
  
  // Configure, build and start
  new AsyncCall.Builder<Thread>()
          .withTask(task)
          .withResultListener(resultListener)
          .build()
          .start();
```
Also a custom `Executor` can be used:
```
  mExecutorService = Executors.newSingleThreadExecutor();

  new AsyncCall.Builder<>()
          .withTask(Thread::currentThread)
          .withResultListener(thread -> assertThat(uiThread, is(not(thread))))
          .withExecutor(mExecutorService)
          .build()
          .start();
```
From the above snippets, `start` should always be called from the UI Thread.  

There's also a direct, less recommended usage mode without using the builder:
```
  AsyncCall.OnResultListener<String> resultListener
          = result -> assertThat(result, is(not(uiThread.getName())));
  Callable<String> task = () -> Thread.currentThread().getName();

  // Raw variant without builder
  new AsyncCall<>(resultListener).exec(task);
```

## Add to project
[![](https://jitpack.io/v/eddiellopez/asynccall.svg)](https://jitpack.io/#eddiellopez/asynccall)

Add jitpack to your project repositores:
```gradle
allprojects {
    repositories {
    ...
    maven { url 'https://jitpack.io' }
    }
  }
```
Add the dependency:
```gradle
dependencies {
    implementation 'com.github.eddiellopez:AsyncCall:tag'
  }
```
See the [Latest Release](https://github.com/eddiellopez/AsyncCall/releases/latest) for the tag.

