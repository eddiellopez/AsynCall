# AsynCall
Asynchronous and concurrent execution tools for Android.  

AsynCall will execute your task in a worker thread and deliver the result in the UI Thread. The `Executor` to use will be the default  `AsyncTask` pool executor unless other is specified.

## Usage examples

Use the `Builder` class to configure the `AsinCall` as follows:
```java
  // The task to run in the form of a Callable
  Callable<Thread> task = Thread::currentThread;
  // The result listener
  AsynCall.OnResultListener<Thread> resultListener = threadName -> {
      // Task will be executed in a pool thread
      assertThat("Thread can't be UI Thread",
              threadName, is(not(uiThread.getName())));
      // Result will be delivered in the UI thread
      assertThat(Thread.currentThread(), is(uiThread));
  };
  
  new AsynCall.Builder<Thread>()
          .withTask(task)
          .withResultListener(resultListener)
          .build()
          .start();
```
Also a custom `Executor` can be used:
```java
  mExecutorService = Executors.newSingleThreadExecutor();

  new AsynCall.Builder<>()
          .withTask(Thread::currentThread)
          .withResultListener(thread -> assertThat(uiThread, is(not(thread))))
          .withExecutor(mExecutorService)
          .build()
          .start();
```
From the above snippets, `start` should always be called from the UI Thread.  

There's also a less recommended usage mode without using the builder:
```java
  AsynCall.OnResultListener<String> resultListener
          = result -> assertThat(result, is(not(uiThread.getName())));
  Callable<String> task = () -> Thread.currentThread().getName();

  // Raw variant without builder
  new AsynCall<>(resultListener).exec(task);
```

## Add to project
[![](https://jitpack.io/v/eddiellopez/AsynCall.svg)](https://jitpack.io/#eddiellopez/AsynCall)

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
    implementation 'com.github.eddiellopez:AsynCall:tag'
  }
```
See the [Latest Release](https://github.com/eddiellopez/AsynCall/releases/latest) for the tag.

