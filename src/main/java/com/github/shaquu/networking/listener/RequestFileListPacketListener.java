package com.github.shaquu.networking.listener;

import com.github.shaquu.file.TorroFile;
import com.github.shaquu.networking.NetworkNode;
import com.github.shaquu.networking.packets.FileListPacket;
import com.github.shaquu.networking.packets.Packet;
import com.github.shaquu.networking.udp.IpPort;
import com.github.shaquu.networking.udp.UDPClientServer;
import com.github.shaquu.utils.PrimitiveObject;

import java.util.Arrays;
import java.util.List;

public class RequestFileListPacketListener implements Listener {
    
    @Override
    public void call(UDPClientServer udpClientServer, IpPort ipPort, Packet packet) throws Exception {
       boolean received = udpClientServer.getPacketManager().add(packet);

        if (received) {
            List<TorroFile> fileList = udpClientServer.getFileManager().getFileList();

            udpClientServer.getLogger().log("Sending file list: " + Arrays.toString(fileList.toArray()));

            byte[] bytesToSend = Packet.toBytes(fileList);
            Packet packetToSend = new FileListPacket(System.currentTimeMillis(), 1, 1, PrimitiveObject.toByteArrObject(bytesToSend));

            udpClientServer.addPacketToQueue(ipPort, packetToSend);
        }
    }

    @Override
    public void call(NetworkNode networkNode, Packet packet) throws Exception {

    }
}
