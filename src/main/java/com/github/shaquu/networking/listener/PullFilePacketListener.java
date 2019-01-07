package com.github.shaquu.networking.listener;

import com.github.shaquu.file.TorroFile;
import com.github.shaquu.file.TorroFileWithContent;
import com.github.shaquu.networking.IpPort;
import com.github.shaquu.networking.NetworkNode;
import com.github.shaquu.networking.packets.Packet;
import com.github.shaquu.networking.packets.PullFilePacket;
import com.github.shaquu.networking.packets.PushFilePacket;
import com.github.shaquu.networking.tcp.TCPCLient;
import com.github.shaquu.networking.tcp.TCPServer;
import com.github.shaquu.networking.udp.UDPClientServer;
import com.github.shaquu.utils.PrimitiveObject;

import java.io.IOException;

public class PullFilePacketListener implements Listener {

    @Override
    public void call(UDPClientServer udpClientServer, IpPort ipPort, Packet packet) throws Exception {
        TorroFile file = handler(udpClientServer, packet);

        if (file == null) {
            return;
        }

        TorroFileWithContent torroFileWithContent =
                new TorroFileWithContent(
                        file.getFileName(),
                        file.getFileSize(),
                        file.getMd5(),
                        udpClientServer.getFileManager().getFileContent(file.getMd5())
                );

        udpClientServer.getLogger().log("Pushing file to client");

        Byte[] data = PrimitiveObject.toByteArrObject(Packet.toBytes(torroFileWithContent));

        Packet packetToSend = new PushFilePacket(System.currentTimeMillis(), 1, 1, data);
        udpClientServer.addPacketToQueue(ipPort, packetToSend);
    }

    @Override
    public void call(TCPServer tcpServer, TCPCLient tcpcLient, Packet packet) throws Exception {
        TorroFile file = handler(tcpServer, packet);

        if (file == null) {
            return;
        }

        TorroFileWithContent torroFileWithContent =
                new TorroFileWithContent(
                        file.getFileName(),
                        file.getFileSize(),
                        file.getMd5(),
                        tcpServer.getFileManager().getFileContent(file.getMd5())
                );

        tcpServer.getLogger().log("Pushing file to client");

        Byte[] data = PrimitiveObject.toByteArrObject(Packet.toBytes(torroFileWithContent));

        Packet packetToSend = new PushFilePacket(System.currentTimeMillis(), 1, 1, data);
        tcpcLient.addPacketToQueue(packetToSend);
    }

    private TorroFile handler(NetworkNode networkNode, Packet packet) throws IOException, ClassNotFoundException {
        boolean received = networkNode.getPacketManager().add(packet);

        if (received) {
            Packet fullPacket = networkNode.getPacketManager().getPacket(PullFilePacket.class, packet.getId());

            byte[] allBytes = PrimitiveObject.toByteArrPrimitive(fullPacket.getData());

            TorroFile file = (TorroFile) Packet.fromBytes(allBytes);

            if (networkNode.getFileManager().getFileContent(file.getMd5()) == null) {
                networkNode.getLogger().log("Someone wants file you dont have  " + file.getFileName() + " " + file.getMd5());
                return null;
            }

            return file;
        }

        return null;
    }
}
