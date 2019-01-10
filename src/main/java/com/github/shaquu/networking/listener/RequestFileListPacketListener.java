package com.github.shaquu.networking.listener;

import com.github.shaquu.file.TorroFile;
import com.github.shaquu.networking.IpPort;
import com.github.shaquu.networking.NetworkNode;
import com.github.shaquu.networking.packets.FileListPacket;
import com.github.shaquu.networking.packets.Packet;
import com.github.shaquu.networking.tcp.TCPCLient;
import com.github.shaquu.networking.tcp.TCPServer;
import com.github.shaquu.networking.udp.UDPClientServer;
import com.github.shaquu.utils.PrimitiveObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RequestFileListPacketListener implements Listener {

    @Override
    public void call(UDPClientServer udpClientServer, IpPort ipPort, Packet packet) throws Exception {
        boolean received = udpClientServer.getPacketManager().add(packet);

        if (received) {
            Packet packetToSend = handler(udpClientServer);
            udpClientServer.addPacketToQueue(ipPort, packetToSend);
        }
    }

    @Override
    public void call(TCPServer tcpServer, TCPCLient tcpcLient, Packet packet) throws Exception {
        boolean received = tcpServer.getPacketManager().add(packet);

        if (received) {
            Packet packetToSend = handler(tcpServer);
            tcpServer.addPacketToQueue(packetToSend);
        }
    }

    private Packet handler(NetworkNode networkNode) throws IOException {
        networkNode.getFileManager().loadFiles();

        List<TorroFile> fileList = networkNode.getFileManager().getFileList();

        networkNode.getLogger().debug("Sending file list: " + Arrays.toString(fileList.toArray()));

        byte[] bytesToSend = Packet.toBytes(fileList);
        return new FileListPacket(System.currentTimeMillis(), 1, 1, PrimitiveObject.toByteArrObject(bytesToSend));
    }
}
