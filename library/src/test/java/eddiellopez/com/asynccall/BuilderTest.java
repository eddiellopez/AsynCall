package eddiellopez.com.asynccall;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import androidx.lifecycle.LifecycleOwner;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class BuilderTest {

    @Mock
    private ThreaderFactory threaderFactory;

    @Mock
    private ExecutorService executorService;

    @Mock
    private Threader<Object> threader;

    @Mock
    private LifecycleOwner lifecycleOwner;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void async() {
        // Given a threader factory that returns the appropriate object:
        Mockito.when(threaderFactory.from(any(), any(), any(), any(), any()))
                .thenReturn(threader);

        // Considering a builder:
        final Builder<String> builder = new Builder<>();
        // <FOR-TEST-ONLY>
        builder.setThreaderFactory(threaderFactory);

        // Configured as follows:
        final Callable<String> callable = () -> "Result";
        builder.async(callable);

        // When started...
        builder.start();

        //noinspection unchecked
        final ArgumentCaptor<Callable<Object>> argument = ArgumentCaptor.forClass(Callable.class);

        // We expect the threader to be built.
        verify(threaderFactory).from(any(), any(), any(), argument.capture(), any());
        // And the callable used is the supplied.
        assertThat(argument.getValue(), is(callable));
        // And we expect the threader to be started.
        verify(threader).start();
    }

    @Test(expected = NullPointerException.class)
    public void asyncNoCallable() {
        // Given a threader factory that returns the appropriate object:
        Mockito.when(threaderFactory.from(any(), any(), any(), any(), any()))
                .thenReturn(threader);

        // Considering a builder:
        final Builder<String> builder = new Builder<>();
        // <FOR-TEST-ONLY>
        builder.setThreaderFactory(threaderFactory);

        // Configured with a null callable:
        final Callable<String> callable = null;
        //noinspection ConstantConditions
        builder.async(callable);

        // When started... we expect an exception.
        builder.start();
    }

    @Test
    public void post() {
        // Given a threader factory that returns the appropriate object:
        Mockito.when(threaderFactory.from(any(), any(), any(), any(), any()))
                .thenReturn(threader);

        // Considering a builder:
        final Builder<String> builder = new Builder<>();
        // <FOR-TEST-ONLY>
        builder.setThreaderFactory(threaderFactory);

        // Configured as follows:
        final OnConsumableResultListener<String> resultListener = result -> assertThat(result, is("Result"));
        builder.async(() -> "Result")
                .onResult(resultListener);

        // When started...
        builder.start();

        //noinspection unchecked
        final ArgumentCaptor<OnConsumableResultListener<String>> argument =
                ArgumentCaptor.forClass(OnConsumableResultListener.class);

        // We expect the threader to be built.
        verify(threaderFactory).from(any(), any(), any(), any(), argument.capture());
        // And the result listener used is the supplied.
        assertThat(argument.getValue(), is(resultListener));
        // And we expect the threader to be started.
        verify(threader).start();
    }

    @Test
    public void withExecutorService() {
        // Given a threader factory that returns the appropriate object:
        Mockito.when(threaderFactory.from(any(), any(), any(), any(), any()))
                .thenReturn(threader);

        // Considering a builder:
        final Builder<String> builder = new Builder<>();
        // <FOR-TEST-ONLY>
        builder.setThreaderFactory(threaderFactory);

        // Configured as follows:
        builder.async(() -> "Result")
                .onResult(result -> assertThat(result, is("Result")))
                .withExecutorService(executorService);

        // When started...
        builder.start();

        final ArgumentCaptor<ExecutorService> argument = ArgumentCaptor.forClass(ExecutorService.class);

        // We expect the threader to be built.
        verify(threaderFactory).from(argument.capture(), any(), any(), any(), any());
        // And the executor used is the supplied.
        assertThat(argument.getValue(), is(executorService));
        // And we expect the threader to be started.
        verify(threader).start();
    }

    @Test
    public void observe() {
        // Given a threader factory that returns the appropriate object:
        Mockito.when(threaderFactory.from(any(), any(), any(), any(), any()))
                .thenReturn(threader);

        // Considering a builder:
        final Builder<String> builder = new Builder<>();
        // <FOR-TEST-ONLY>
        builder.setThreaderFactory(threaderFactory);

        // Configured as follows:
        builder.async(() -> "Result")
                .onResult(result -> assertThat(result, is("Result")))
                .withExecutorService(executorService)
                .observe(lifecycleOwner);

        // When started...
        builder.start();

        final ArgumentCaptor<LifecycleOwner> argument = ArgumentCaptor.forClass(LifecycleOwner.class);

        // We expect the threader to be built.
        verify(threaderFactory).from(any(), any(), argument.capture(), any(), any());
        // And the lifecycle owner used is the supplied.
        assertThat(argument.getValue(), is(lifecycleOwner));
        // And we expect the threader to be started.
        verify(threader).start();
    }

    @Test
    public void except() {
        // Given a threader factory that returns the appropriate object:
        Mockito.when(threaderFactory.from(any(), any(), any(), any(), any()))
                .thenReturn(threader);

        // Considering a builder:
        final Builder<String> builder = new Builder<>();
        // <FOR-TEST-ONLY>
        builder.setThreaderFactory(threaderFactory);

        // Configured as follows:
        final OnExceptionHandler onExceptionHandler = Throwable::printStackTrace;

        builder.async(() -> "Result")
                .onResult(result -> assertThat(result, is("Result")))
                .withExecutorService(executorService)
                .observe(lifecycleOwner)
                .except(onExceptionHandler);

        // When started...
        builder.start();

        final ArgumentCaptor<OnExceptionHandler> argument = ArgumentCaptor.forClass(OnExceptionHandler.class);

        // We expect the threader to be built.
        verify(threaderFactory).from(any(), argument.capture(), any(), any(), any());
        // And the exception handler used is the supplied.
        assertThat(argument.getValue(), is(onExceptionHandler));
        // And we expect the threader to be started.
        verify(threader).start();
    }

    @Test
    public void start() {
    }
}