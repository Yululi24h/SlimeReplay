package me.koutachan.replay.replay.user.record;

import me.koutachan.replay.replay.packet.ReplayPacketContainer;
import me.koutachan.replay.replay.packet.in.ReplayWrapper;
import me.koutachan.replay.replay.user.ReplayUser;

import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.GZIPOutputStream;

public class RecordRunner {
    private final ReplayUser user;
    private final long sleep;
    protected RecordHook hook;

    private boolean enabled;
    private Thread saveThread;

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
        this.saveThread = new Thread(() -> {
            while (enabled) {
                try {
                    Thread.sleep(sleep);
                    save();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        this.saveThread.setName("SlimeReplay-Save-Thread");
        this.saveThread.start();
        this.hook.start();
    }

    public void stop() {
        try {
            //this.onSave();
            /* this.onSave(); */
            this.enabled = false;
            if (saveThread != null) {
                saveThread = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void save() throws Exception {
    }

    public void onPacket(ReplayWrapper<?> packet) {
        if (isRecording()) {
            hook.onPacket(packet);
        }
    }

    public boolean isRecording() {
        return enabled;
    }

    public static RecordRunner ofFile(ReplayUser user, File file) {
        return new FileRunner(user, file);
    }

    public static RecordRunner ofFile(ReplayUser user, File file, long sleep) {
        return new FileRunner(user, file, sleep);
    }

    public static class FileRunner extends RecordRunner {
        private final File to;

        public FileRunner(ReplayUser user, File to) {
            super(user);
            this.to = to;
            if (to.exists() && !to.delete()) {
                throw new IllegalAccessError();
            }
        }

        public FileRunner(ReplayUser user, File to, long sleep) {
            super(user, sleep);
            this.to = to;
            if (to.exists() && !to.delete()) {
                throw new IllegalAccessError();
            }
        }

        @Override
        public synchronized void save() throws Exception {
            if (!isRecording())
                return;
            ReplayPacketContainer container = hook.getContainer();
            if (container != null) {
                ReplayPacketContainer copied = container.copy();
                container.clear();
                try (GZIPOutputStream stream = new GZIPOutputStream(new FileOutputStream(to, true))) {
                    copied.write(stream);
                }
            }
        }
    }
}