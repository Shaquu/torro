package com.github.shaquu.controller;

import com.github.shaquu.file.TorroFile;
import com.github.shaquu.file.TorroFileWithContent;
import com.github.shaquu.networking.NetworkNode;
import com.github.shaquu.networking.packets.Packet;
import com.github.shaquu.networking.packets.PushFilePacket;
import com.github.shaquu.networking.packets.RequestFileListPacket;
import com.github.shaquu.networking.udp.IpPort;
import com.github.shaquu.networking.udp.UDPClientServer;
import com.github.shaquu.utils.PrimitiveObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleController {

    private final NetworkNode networkNode;
    private final Scanner scanner = new Scanner(System.in);

    private List<Command> commandList = new ArrayList<>();

    private int commandNumber = 0;

    public ConsoleController(NetworkNode networkNode) {
        this.networkNode = networkNode;

        commandList.add(new Command() {
            @Override
            public String description() {
                return "Quit [" + (commandNumber++) + "]";
            }

            @Override
            public void execute() {
            }
        });

        commandList.add(new Command() {
            @Override
            public String description() {
                return "Request file list [" + (commandNumber++) + "]";
            }

            @Override
            public void execute() {
                try {
                    requestFileList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        commandList.add(new Command() {
            @Override
            public String description() {
                return "Print my file list [" + (commandNumber++) + "]";
            }

            @Override
            public void execute() {
                printMyFileList();
            }
        });

        commandList.add(new Command() {
            @Override
            public String description() {
                return "Print my client list [" + (commandNumber++) + "]";
            }

            @Override
            public void execute() {
                printMyClients();
            }
        });

        commandList.add(new Command() {
            @Override
            public String description() {
                return "Push file [" + (commandNumber++) + "]";
            }

            @Override
            public void execute() {
                try {
                    pushFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void start() {
        StringBuilder stringBuilder = new StringBuilder("Available commands:");
        commandList.forEach(c -> stringBuilder.append(" | ").append(c.description()));

        int command;
        do {
            networkNode.getLogger().log(stringBuilder.toString());
            command = scanner.nextInt();

            if (command == 0) {
                break;
            } else if (command >= 0 && command < commandList.size()) {
                commandList.get(command).execute();
            } else {
                networkNode.getLogger().log("Unknown command.");
            }
        } while (true);
    }

    private void requestFileList() throws Exception {
        networkNode.getLogger().log("Requesting file list...");

        Packet packet = new RequestFileListPacket(System.currentTimeMillis());
        networkNode.addPacketToQueue(packet);
    }

    private void printMyFileList() {
        StringBuilder stringBuilder = new StringBuilder();

        int i = 0;

        for (TorroFile torroFile : networkNode.getFileManager().getFileList()) {
            stringBuilder.append("\n[").append(i++).append("] ").append(torroFile.toString());
        }

        networkNode.getLogger().log("My file list:" + stringBuilder.toString());
    }

    private void printMyClients() {
        StringBuilder stringBuilder = new StringBuilder();

        int i = 0;

        if (networkNode instanceof UDPClientServer) {
            UDPClientServer udpClientServer = (UDPClientServer) networkNode;
            for (IpPort ipPort : udpClientServer.getIpPortList()) {
                stringBuilder.append("\n[").append(i++).append("] ").append(ipPort.toString());
            }

            networkNode.getLogger().log("My client list:" + stringBuilder.toString());
        } else {
            networkNode.getLogger().log("My client list:" + stringBuilder.toString());
        }
    }

    private void pushFile() throws Exception {
        printMyFileList();
        networkNode.getLogger().log("Select file number to push:");

        List<TorroFile> fileList = networkNode.getFileManager().getFileList();

        int fileNumber = scanner.nextInt();

        if (fileNumber < 0 || fileNumber >= fileList.size()) {
            networkNode.getLogger().log("Wrong file number;");
            return;
        }

        TorroFile torroFile = fileList.get(fileNumber);
        TorroFileWithContent torroFileWithContent =
                new TorroFileWithContent(
                        torroFile.getFileName(),
                        torroFile.getFileSize(),
                        torroFile.getMd5(),
                        networkNode.getFileManager().getFileContent(torroFile.getMd5())
                );

        printMyClients();
        networkNode.getLogger().log("Select client number who will be pushed with file:");

        int clientNumber = scanner.nextInt();

        if (networkNode instanceof UDPClientServer) {
            UDPClientServer udpClientServer = (UDPClientServer) networkNode;

            if (clientNumber < 0 || clientNumber >= udpClientServer.getIpPortList().size()) {
                networkNode.getLogger().log("Wrong client number;");
                return;
            }

            networkNode.getLogger().log("Pushing file to client");

            Byte[] data = PrimitiveObject.toByteArrObject(Packet.toBytes(torroFileWithContent));

            Packet packet = new PushFilePacket(System.currentTimeMillis(), 1, 1, data);
            networkNode.addPacketToQueue(udpClientServer.getIpPortList().get(clientNumber), packet);
        }
    }

}
