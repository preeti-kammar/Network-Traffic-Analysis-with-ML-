package com.alok.trafficanalyzer;

import org.pcap4j.packet.Packet;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

public class PacketDecoder {

    public LocalPacket decode(Packet packet) {
        if (packet == null)
            return null;

        String srcIP = "", dstIP = "", protocol = "";
        int length = 0;
        String srcPort = "", dstPort = "";

        if (packet.contains(IpV4Packet.class)) {
            IpV4Packet ipv4 = packet.get(IpV4Packet.class);
            srcIP = ipv4.getHeader().getSrcAddr().getHostAddress();
            dstIP = ipv4.getHeader().getDstAddr().getHostAddress();
            protocol = ipv4.getHeader().getProtocol().name();
            length = ipv4.getHeader().getTotalLengthAsInt();
        }

        if (packet.contains(TcpPacket.class)) {
            TcpPacket tcp = packet.get(TcpPacket.class);
            srcPort = tcp.getHeader().getSrcPort().valueAsString();
            dstPort = tcp.getHeader().getDstPort().valueAsString();
        }

        if (packet.contains(UdpPacket.class)) {
            UdpPacket udp = packet.get(UdpPacket.class);
            srcPort = udp.getHeader().getSrcPort().valueAsString();
            dstPort = udp.getHeader().getDstPort().valueAsString();
        }

        return new LocalPacket(srcIP, dstIP, srcPort, dstPort, protocol, length);
    }

    public static class LocalPacket {
        public String srcIP, dstIP, srcPort, dstPort, protocol;
        public int length;

        public LocalPacket(String srcIP, String dstIP, String srcPort, String dstPort, String protocol, int length) {
            this.srcIP = srcIP;
            this.dstIP = dstIP;
            this.srcPort = srcPort;
            this.dstPort = dstPort;
            this.protocol = protocol;
            this.length = length;
        }
    }
}
