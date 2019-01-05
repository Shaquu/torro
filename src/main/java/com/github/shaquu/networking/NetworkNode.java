package com.github.shaquu.networking;

import com.github.shaquu.file.FileManager;
import com.github.shaquu.logger.Logger;
import com.github.shaquu.networking.listener.ListenerManager;
import com.github.shaquu.networking.packets.Packet;
import com.github.shaquu.networking.packets.PacketManager;
import com.github.shaquu.networking.udp.IpPort;

public abstract class NetworkNode extends ListenerManager {

    protected Logger logger = new Logger();

    private Thread receiverThread;
    private Thread senderThread;
    private PacketManager packetManager;
    private FileManager fileManager;

    protected NetworkNode() {
        fileManager = new FileManager();
        packetManager = new PacketManager();

        receiverThread = new Thread(() -> {
            while (true) {
                try {
                    receiver();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        senderThread = new Thread(() -> {
            while (true) {
                try {
                    sender();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void start() {
        logger.log("Starting NetworkNode...");

        receiverThread.start();
        senderThread.start();
    }

    protected abstract void receiver() throws Exception;

    protected abstract void sender() throws Exception;

    public abstract void addPacketToQueue(IpPort ipPort, Packet packet) throws Exception;

    public abstract void addPacketToQueue(Packet packet);

    public FileManager getFileManager() {
        return fileManager;
    }

    public PacketManager getPacketManager() {
        return packetManager;
    }

    public Logger getLogger() {
        return logger;
    }
}
