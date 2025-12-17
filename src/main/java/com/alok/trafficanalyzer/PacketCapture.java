package com.alok.trafficanalyzer;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class PacketCapture {
	private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
			.withZone(ZoneOffset.UTC);
	private BufferedWriter writer;

	public PacketCapture(String outputCsv) throws IOException {
		File f = new File(outputCsv);
		boolean exists = f.exists();
		writer = new BufferedWriter(new FileWriter(f, true));
		if (!exists) {
			writer.write("timestamp,src_ip,dst_ip,protocol,length");
			writer.newLine();
		}
	}

	public void logPacket(String srcIp, String dstIp, String protocol, int length) throws IOException {
		Instant now = Instant.now();
		String ts = fmt.format(now);
		writer.write(String.format("%s,%s,%s,%s,%d", ts, srcIp, dstIp, protocol, length));
		writer.newLine();
		writer.flush();
	}

	public void close() throws IOException {
		writer.close();
	}

	public static void main(String[] args) {
		try {
			PacketCapture pc = new PacketCapture("data/packets.csv");
			pc.logPacket("192.168.0.2", "8.8.8.8", "TCP", 1500);
			pc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
