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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileListPacketListener implements Listener {

    @Override
    public void call(UDPClientServer udpClientServer, IpPort ipPort, Packet packet) throws Exception {
        List<TorroFile> fileList = (List<TorroFile>) handler(udpClientServer, packet);

        if (fileList != null) {
            udpClientServer.addClienFileList(ipPort, fileList);
        }
    }

    @Override
    public void call(TCPServer tcpServer, TCPCLient tcpcLient, Packet packet) throws Exception {
        List<TorroFile> fileList = (List<TorroFile>) handler(tcpServer, packet);

        if (fileList != null) {
            tcpServer.addClienFileList(tcpcLient.getId(), fileList);
        }
    }

    private Object handler(NetworkNode networkNode, Packet packet) throws IOException, ClassNotFoundException {
        boolean received = networkNode.getPacketManager().add(packet);

        if (received) {
            FileListPacket fileListPacket = (FileListPacket)
                    networkNode.getPacketManager().getPacket(FileListPacket.class, packet.getId());

            byte[] allBytes = PrimitiveObject.toByteArrPrimitive(fileListPacket.getData());

            List<TorroFile> fileList = (ArrayList<TorroFile>) Packet.fromBytes(allBytes);

            networkNode.getLogger().debug(Arrays.toString(fileList.toArray()));

            return fileList;
        }

        return null;
    }
}