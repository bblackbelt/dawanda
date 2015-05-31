package de.dawanda.dawandaclient.networking;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CommandExecutor<T> {


    private static CommandExecutor mCommandExecutor;

    private final ExecutorService mExecutor;

    private CommandExecutor() {
        mExecutor = Executors.newFixedThreadPool(1);
    }

    public static synchronized CommandExecutor getInstance() {
        if (mCommandExecutor == null) {
            mCommandExecutor = new CommandExecutor();
        }
        return mCommandExecutor;
    }

    public <T extends BaseCommand> void addCommand(T command) {
        mExecutor.execute(command);
    }

}
