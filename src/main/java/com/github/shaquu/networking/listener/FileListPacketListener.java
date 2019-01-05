package com.github.shaquu.networking.listener;

import com.github.shaquu.file.TorroFile;
import com.github.shaquu.networking.NetworkNode;
import com.github.shaquu.networking.packets.FileListPacket;
import com.github.shaquu.networking.packets.Packet;
import com.github.shaquu.networking.packets.RequestFileListPacket;
import com.github.shaquu.networking.udp.IpPort;
import com.github.shaquu.networking.udp.UDPClientServer;
import com.github.shaquu.utils.PrimitiveObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileListPacketListener implements Listener {

    @Override
    public void call(UDPClientServer udpClientServer, IpPort ipPort, Packet packet) throws Exception {
        boolean received = udpClientServer.getPacketManager().add(packet);

        if (received) {
            FileListPacket fileListPacket = (FileListPacket)
                    udpClientServer.getPacketManager().getPacket(FileListPacket.class, packet.getId());

            byte[] allBytes = PrimitiveObject.toByteArrPrimitive(fileListPacket.getData());

            List<TorroFile> fileList = (ArrayList<TorroFile>) Packet.fromBytes(allBytes);

            udpClientServer.getLogger().log(Arrays.toString(fileList.toArray()));
        }
    }

    @Override
    public void call(NetworkNode networkNode, Packet packet) throws Exception {

    }
}