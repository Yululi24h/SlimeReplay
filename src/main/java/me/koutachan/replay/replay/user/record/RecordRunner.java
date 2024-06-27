package me.koutachan.replay.replay.user.record;

import me.koutachan.replay.replay.packet.ReplayPacketContainer;
import me.koutachan.replay.replay.packet.in.ReplayWrapper;
import me.koutachan.replay.replay.user.ReplayUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public abstract class RecordRunner {
    private final ReplayUser user;
    private final long sleep;
    protected RecordHook hook;

    private boolean enabled;
    private Thread saveThread;
    private Long nextSave;

    public RecordRunner(ReplayUser user) {
        this(user, 1000L);
    }

    public RecordRunner(ReplayUser user, long sleep) {
        this(user, new RecordHookImpl(user), sleep);
    }

    public RecordRunner(ReplayUser user, RecordHook hook, long sleep) {
        this.user = user;
        this.hook = hook;
        this.sleep = sleep;
    }

    public void start() {
        this.enabled = true;
        calculateNextSleep();
        this.saveThread = new Thread(() -> {
            while (this.enabled) {
                try {
                    Thread.sleep(1L);
                    if (System.currentTimeMillis() > this.nextSave) {
                        save();
                        calculateNextSleep();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        this.saveThread.setName("SlimeReplay-Save-Thread");
        this.saveThread.start();
        this.hook.start();
    }

    private void calculateNextSleep() {
        this.nextSave = System.currentTimeMillis() + this.sleep;
    }

    public CompletableFuture<Void> stop() {
        if (!this.enabled)
            return null;
        this.enabled = false;
        if (this.saveThread != null) {
            this.saveThread.interrupt();
            this.saveThread = null;
        }
        return CompletableFuture.runAsync(this::save0);
    }

    public void save() {
        if (isRecording()) {
            save0();
        }
    }

    protected abstract void save0();

    public void onPacket(ReplayWrapper<?> packet) {
        if (isRecording()) {
            this.hook.onPacket(packet);
        }
    }

    public boolean isRecording() {
        return enabled;
    }

    public static RecordRunner ofFile(ReplayUser user, File file) {
        return new OptimizedFileRunner(user, file);
    }

    public static RecordRunner ofFile(ReplayUser user, File file, long sleep) {
        return new OptimizedFileRunner(user, file, sleep);
    }

    public static class OptimizedFileRunner extends RecordRunner {
        private final File to;

        public OptimizedFileRunner(ReplayUser user, File to) {
            super(user);
            this.to = to;
            if (to.exists() && !to.delete()) {
                throw new IllegalAccessError();
            }
        }

        public OptimizedFileRunner(ReplayUser user, File to, long sleep) {
            super(user, sleep);
            this.to = to;
            if (to.exists() && !to.delete()) {
                throw new IllegalAccessError();
            }
        }

        @Override
        protected void save0() {
            ReplayPacketContainer container = this.hook.getContainer();
            if (container != null && !container.isEmpty()) {
                ReplayPacketContainer copied = container.copy();
                container.clear();
                try (FileOutputStream stream = new FileOutputStream(to, true)) {
                    copied.write(stream);
                    if (!container.isVersionFlag() && copied.isVersionFlag()) {
                        container.setVersionFlag(true, copied.getServerVersion());
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}