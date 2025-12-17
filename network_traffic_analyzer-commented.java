package com.alok.trafficanalyzer;

// Import statements - these are the libraries we need for packet capture
import org.pcap4j.core.NotOpenException; // Exception when packet capture handle is not open
import org.pcap4j.core.PcapHandle; // Main class for capturing packets
import org.pcap4j.core.Pcaps; // Utility class for finding network devices
import org.pcap4j.packet.Packet; // Represents a captured network packet
import org.pcap4j.core.PcapNetworkInterface; // Represents a network interface (Wi-Fi, Ethernet, etc.)
import org.pcap4j.core.PcapNativeException; // Exception for native pcap library errors
import java.util.List; // For storing list of network devices
import java.util.Scanner; // For reading user input

public class PacketCapture {

    public static void main(String[] args) {
        try {
            // STEP 1: DISCOVER NETWORK DEVICES
            // ================================
            // Find all available network interfaces on the system
            // This includes Wi-Fi, Ethernet, VPN adapters, virtual adapters, etc.
            List<PcapNetworkInterface> allDevs = Pcaps.findAllDevs();

            // Check if any network devices were found
            if (allDevs == null || allDevs.isEmpty()) {
                System.out.println("No devices found!");
                return; // Exit if no devices available
            }

            // STEP 2: DISPLAY AVAILABLE DEVICES
            // =================================
            // Show all available network interfaces to the user
            // Each device has an index (0, 1, 2...), name, and description
            System.out.println("Available network devices:");
            for (int i = 0; i < allDevs.size(); i++) {
                // Print: Index: DeviceName (Description)
                // Example: 3: \Device\NPF_{...} (Realtek RTL8822CE 802.11ac PCIe Adapter)
                System.out.println(i + ": " + allDevs.get(i).getName() +
                        " (" + allDevs.get(i).getDescription() + ")");
            }

            // STEP 3: GET USER SELECTION
            // ==========================
            // Ask user to choose which network interface to monitor
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the device number to capture: ");
            int deviceIndex = scanner.nextInt(); // Read the device number
            scanner.close(); // Close the scanner to free resources

            // STEP 4: VALIDATE USER INPUT
            // ===========================
            // Check if the selected device index is valid
            if (deviceIndex < 0 || deviceIndex >= allDevs.size()) {
                System.out.println("Invalid device index!");
                return; // Exit if invalid selection
            }

            // STEP 5: OPEN THE SELECTED DEVICE FOR PACKET CAPTURE
            // ===================================================
            // Get the selected network interface
            PcapNetworkInterface device = allDevs.get(deviceIndex);

            // Open the device for live packet capture
            // Parameters explained:
            // - 65536: Maximum bytes to capture per packet (64KB - captures full packets)
            // - PROMISCUOUS: Capture all packets on network, not just those destined for
            // this machine
            // - 10000: Read timeout in milliseconds (10 seconds)
            PcapHandle handle = device.openLive(
                    65536, // snaplen - maximum packet size to capture
                    PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, // capture mode
                    10000 // timeout in milliseconds
            );

            // Confirm capture has started
            System.out.println("Starting capture on device: " + device.getName());

            // STEP 6: PACKET CAPTURE LOOP
            // ===========================
            // Initialize counters for packet capture
            int packetCount = 0; // Current number of packets captured
            int maxPackets = 10; // Maximum packets to capture (configurable)

            // Main capture loop - continue until we reach maxPackets
            while (packetCount < maxPackets) {

                // STEP 6A: CAPTURE NEXT PACKET
                // ============================
                // getNextPacket() blocks until a packet is received or timeout occurs
                Packet packet = handle.getNextPacket();

                if (packet != null) {
                    // STEP 6B: PROCESS CAPTURED PACKET
                    // ================================
                    // A packet was successfully captured
                    System.out.println("Packet #" + (packetCount + 1) + ":");

                    // Print the packet details
                    // This shows the raw packet data in hexadecimal format
                    // Contains: Ethernet header + IP header + TCP/UDP header + Data
                    System.out.println(packet);

                    packetCount++; // Increment packet counter
                } else {
                    // STEP 6C:111111``11 HANDLE NO PACKET SCENARIO
                    // ==================================
                    // No packet received within timeout period
                    System.out.println("No packet captured at this moment...");
                    // Note: This continues the loop, doesn't exit
                }
            }

            // STEP 7: CLEANUP AND SUMMARY
            // ===========================
            // Close the packet capture handle to free system resources
            handle.close();

            // Display capture summary
            System.out.println("Capture finished. Total packets captured: " + packetCount);

        }
        // STEP 8: ERROR HANDLING
        // ======================
        // Handle different types of exceptions that might occur

        catch (PcapNativeException e) {
            // Occurs when there's an issue with the native pcap library
            // Common causes: insufficient privileges, device not available, driver issues
            System.out.println("Error opening device: " + e.getMessage());
            e.printStackTrace(); // Print full error details for debugging

        } catch (NotOpenException e) {
            // Occurs when trying to capture packets on a closed handle
            System.out.println("Error capturing packet: " + e.getMessage());
            e.printStackTrace();

        } catch (Exception e) {
            // Catch any other unexpected exceptions
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

/*
 * PROGRAM FLOW SUMMARY:
 * =====================
 * 
 * 1. INITIALIZATION
 * - Import required pcap4j libraries
 * - Set up exception handling
 * 
 * 2. DEVICE DISCOVERY
 * - Scan system for all network interfaces
 * - Display list with descriptions
 * 
 * 3. USER INTERACTION
 * - Prompt user to select network interface
 * - Validate selection
 * 
 * 4. DEVICE CONFIGURATION
 * - Open selected device in promiscuous mode
 * - Configure capture parameters (packet size, timeout)
 * 
 * 5. PACKET CAPTURE
 * - Loop to capture specified number of packets
 * - Process and display each packet
 * - Handle timeouts gracefully
 * 
 * 6. CLEANUP
 * - Close capture handle
 * - Display summary statistics
 * - Handle any errors that occurred
 * 
 * TECHNICAL CONCEPTS:
 * ==================
 * 
 * • PROMISCUOUS MODE: Captures all network traffic, not just traffic
 * destined for this computer
 * 
 * • PACKET STRUCTURE: Each packet contains multiple layers:
 * - Ethernet Header (MAC addresses)
 * - IP Header (source/destination IP addresses)
 * - TCP/UDP Header (port numbers, flags)
 * - Application Data (HTTP, DNS, etc.)
 * 
 * • HEXADECIMAL OUTPUT: Raw packet bytes shown in hex format
 * - Allows low-level analysis of network protocols
 * - Can be decoded to understand communication details
 * 
 * • TIMEOUT HANDLING: Prevents program from hanging if no traffic
 * 
 * • RESOURCE MANAGEMENT: Proper cleanup prevents system resource leaks
 */