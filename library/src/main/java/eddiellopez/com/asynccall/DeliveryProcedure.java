package eddiellopez.com.asynccall;

/**
 * The code to execute for delivering the result.
 * For example, the callback to call.
 * <p>
 * Note this will only be called, when a lifecycle owner is under observation, if
 * in a resumed state.
 */
@FunctionalInterface
public interface DeliveryProcedure<T> {

    /**
     * Delivers the result.
     *
     * @param result The result.
     */
    void deliver(T result);
}
