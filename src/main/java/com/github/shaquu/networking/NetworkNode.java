package com.github.shaquu.networking;

import com.github.shaquu.controller.ConsoleController;
import com.github.shaquu.file.FileManager;
import com.github.shaquu.logger.Logger;
import com.github.shaquu.networking.listener.*;
import com.github.shaquu.networking.packets.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class NetworkNode extends ListenerManager {

    protected final ConcurrentLinkedQueue<IpPortPacket> packetQueue = new ConcurrentLinkedQueue<>();
    protected int port;
    protected Logger logger = new Logger();
    protected List<IpPort> ipPortList = new ArrayList<>();
    private Thread receiverThread;
    private Thread senderThread;
    private PacketManager packetManager;
    private FileManager fileManager;
    private String folderPath;
    private ConsoleController consoleController;

    protected NetworkNode(int port, String folderPath) {
        this.port = port;
        this.folderPath = folderPath;

        fileManager = new FileManager(this, folderPath);
        packetManager = new PacketManager();

        registerListener(new RequestFileListPacketListener());
        registerListener(new FileListPacketListener());
        registerListener(new PushFilePacketListener());
        registerListener(new PullFilePacketListener());
        registerListener(new PullFilePartsPacketListener());
        registerListener(new PushFilePartsPacketListener());
        registerListener(new LogOnPacketListener());

        receiverThread = new Thread(() -> {
            logger.log("Receiver started...");
            while (true) {
                try {
                    receiver();
                } catch (InterruptedException e) {
                    logger.log("Receiver stopped...");
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        senderThread = new Thread(() -> {
            logger.log("Sender started...");
            while (true) {
                try {
                    sender();
                } catch (InterruptedException e) {
                    logger.log("Sender stopped...");
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        consoleController = new ConsoleController(this) {
            @Override
            public void start() {
                super.start();
            }
        };
    }

    protected NetworkNode() {
        receiverThread = new Thread(() -> {
            while (true) {
                try {
                    receiver();
                } catch (InterruptedException e) {
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        senderThread = new Thread(() -> {
            while (true) {
                try {
                    sender();
                } catch (InterruptedException e) {
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void stop() {
        receiverThread.interrupt();
        senderThread.interrupt();

        if (consoleController != null) {
            logger.log("Stopping NetworkNode...");
        }
    }

    public void start() {
        receiverThread.start();
        senderThread.start();

        if (consoleController != null) {
            logger.log("Starting NetworkNode...");

            consoleController.start();
        }
    }

    protected abstract void receiver() throws Exception;

    protected abstract void sender() throws Exception;

    public abstract void addPacketToQueue(IpPort ipPort, Packet packet) throws Exception;

    public abstract void addPacketToQueue(Packet packet) throws Exception;

    public FileManager getFileManager() {
        return fileManager;
    }

    public PacketManager getPacketManager() {
        return packetManager;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public Logger getLogger() {
        return logger;
    }


    protected Packet createPacketChunk(Packet packet, int part, int maxPart, Byte[] data) {
        if (packet instanceof RequestFileListPacket) {
            return new RequestFileListPacket(packet.getId());
        } else if (packet instanceof FileListPacket) {
            return new FileListPacket(packet.getId(), part, maxPart, data);
        } else if (packet instanceof PushFilePacket) {
            return new PushFilePacket(packet.getId(), part, maxPart, data, ((PushFilePacket) packet).getTorroFile());
        } else if (packet instanceof PullFilePacket) {
            return new PullFilePacket(packet.getId(), part, maxPart, data, ((PullFilePacket) packet).getTorroFile());
        } else if (packet instanceof LogOnPacket) {
            logger.debug("Packet LogOn chunked. Something is wrong.");
            return new LogOnPacket(packet.getId());
        } else if (packet instanceof PullFilePartsPacket) {
            return new PullFilePartsPacket(packet.getId(), part, maxPart, data, ((PullFilePartsPacket) packet).getParts());
        }
        logger.log("Packet type not supported in createPacketChunk.");
        return null;
    }

    public void addClient(IpPort ipPort) {
        ipPortList.add(ipPort);
    }
}
