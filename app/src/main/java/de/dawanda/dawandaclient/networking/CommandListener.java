package de.dawanda.dawandaclient.networking;


public interface CommandListener<T> {
    void onCommandFinished(T result);

    void onCommandFailed(String message, Throwable throwable);
}
