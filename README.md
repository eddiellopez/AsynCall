# AsyncCall

Simple concurrent execution tools for Android in Java.

AsyncCall will execute a task in a worker thread and deliver the result when done. The client should
provide an Executor and the corresponding listeners.

## Usage examples

Use the `Builder` class as follows:

```
new Builder<List<Post>>()
        .withExecutorService(executorService)
        // This is the task, which assumes the form of a Callable:
        .async(postRepository::getPosts)
        // The result is obtained here:
        .onResult(posts->{
        // Note the result can be null.
        assert posts!=null;

        // Update the UI.
        posts.stream()
        .findFirst()
        .ifPresent(post->binding.textviewFirst.setText(post.getTitle()));

        })
        // Since this example is called from a Fragment, observe its lifecycle:
        .observe(this)
        // Any exception will be received here:
        .except(e->Log.e(TAG,"onViewCreated: ",e))
        .start();
```

Calling `start` from the UI thread, guarantees delivery on the UI Thread. Calling it from any other
thread delivers the result in a worked thread. This applies to exceptions as well.


## Add to project

[![](https://jitpack.io/v/eddiellopez/asynccall.svg)](https://jitpack.io/#eddiellopez/asynccall)

Add jitpack to your project repositories:

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

