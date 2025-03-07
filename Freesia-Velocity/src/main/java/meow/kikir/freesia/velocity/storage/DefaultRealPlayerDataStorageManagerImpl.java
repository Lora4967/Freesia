package meow.kikir.freesia.velocity.storage;

import meow.kikir.freesia.velocity.Freesia;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DefaultRealPlayerDataStorageManagerImpl implements IDataStorageManager {
    private static final File PLUGIN_FOLDER = new File(new File("plugins"), "Freesia");
    private static final File PLAYER_DATA_FOLDER = new File(PLUGIN_FOLDER, "playerdata");

    static {
        PLAYER_DATA_FOLDER.mkdirs();
    }

    @Override
    public CompletableFuture<byte[]> loadPlayerData(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            final File targetFile = new File(PLAYER_DATA_FOLDER, playerUUID + ".nbt");

            if (!targetFile.exists()) {
                return null;
            }

            try {
                return Files.readAllBytes(targetFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, ioTask -> Freesia.PROXY_SERVER.getScheduler().buildTask(Freesia.INSTANCE, ioTask).schedule());
    }


    @Override
    public CompletableFuture<Void> save(UUID playerUUID, byte[] content) {
        return CompletableFuture.runAsync(() -> {
            final File targetFile = new File(PLAYER_DATA_FOLDER, playerUUID + ".nbt");

            try {
                if (!targetFile.exists()) {
                    targetFile.createNewFile();
                }

                Files.write(targetFile.toPath(), content);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, ioTask -> Freesia.PROXY_SERVER.getScheduler().buildTask(Freesia.INSTANCE, ioTask).schedule());
    }
}
