package com.github.shaquu.networking.listener;

import com.github.shaquu.file.TorroFileWithContent;
import com.github.shaquu.networking.IpPort;
import com.github.shaquu.networking.NetworkNode;
import com.github.shaquu.networking.packets.Packet;
import com.github.shaquu.networking.packets.PushFilePacket;
import com.github.shaquu.networking.tcp.TCPCLient;
import com.github.shaquu.networking.tcp.TCPServer;
import com.github.shaquu.networking.udp.UDPClientServer;
import com.github.shaquu.utils.PrimitiveObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class PushFilePartsPacketListener implements Listener {

    @Override
    public void call(UDPClientServer udpClientServer, IpPort ipPort, Packet packet) throws Exception {
        handler(udpClientServer, (PushFilePacket) packet);
    }

    @Override
    public void call(TCPServer tcpServer, TCPCLient tcpcLient, Packet packet) throws Exception {
        handler(tcpServer, (PushFilePacket) packet);
    }

    private void handler(NetworkNode networkNode, PushFilePacket packet) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        boolean received = networkNode.getPacketManager().add(packet);

        if (received) {
            Packet fullPacket = networkNode.getPacketManager().getPushFilePacket(packet.getTorroFile());

            byte[] allBytes = PrimitiveObject.toByteArrPrimitive(fullPacket.getData());

            TorroFileWithContent file = (TorroFileWithContent) Packet.fromBytes(allBytes);

            if (networkNode.getFileManager().getFileContent(Objects.requireNonNull(file).getMd5()) == null) {
                networkNode.getLogger().log("Received file " + file.getFileName() + " " + file.getMd5());

                String filePath = networkNode.getFolderPath() + "/" + file.getFileName();

                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    fos.write(PrimitiveObject.toByteArrPrimitive(file.getContent()));
                }

                networkNode.getFileManager().add(new File(filePath));
            }  //networkNode.getLogger().log("Received file that already is here " + file.getFileName() + " " + file.getMd5());

        }
    }
}
