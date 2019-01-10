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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FileListPacketListener implements Listener {

    @Override
    public void call(UDPClientServer udpClientServer, IpPort ipPort, Packet packet) throws Exception {
        List<TorroFile> fileList = (List<TorroFile>) handler(udpClientServer, packet);

        if (fileList != null) {
            udpClientServer.getLogger().log("Files of client " + ipPort);
            udpClientServer.addClienFileList(ipPort, fileList);
        }
    }

    @Override
    public void call(TCPServer tcpServer, TCPCLient tcpcLient, Packet packet) throws Exception {
        List<TorroFile> fileList = (List<TorroFile>) handler(tcpServer, packet);

        if (fileList != null) {
            tcpServer.getLogger().log("Files of client " + tcpcLient);
            tcpServer.addClienFileList(tcpcLient.getId(), fileList);
        }
    }

    private Object handler(NetworkNode networkNode, Packet packet) throws ClassNotFoundException {
        boolean received = networkNode.getPacketManager().add(packet);

        if (received) {
            FileListPacket fileListPacket = (FileListPacket)
                    networkNode.getPacketManager().getPacket(FileListPacket.class, packet.getId());

            byte[] allBytes = PrimitiveObject.toByteArrPrimitive(fileListPacket.getData());

            List<TorroFile> fileList = (ArrayList<TorroFile>) Packet.fromBytes(allBytes);

            networkNode.getLogger().log(Arrays.toString(Objects.requireNonNull(fileList).toArray()));

            return fileList;
        }

        return null;
    }
}