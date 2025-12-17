# Network Traffic Analysis with Machine Learning

##  Abstract

Modern computer networks generate massive volumes of traffic, making manual monitoring and analysis inefficient and error-prone. This project presents a **Network Traffic Analysis System** that processes packet-level network data to extract meaningful insights such as protocol usage, traffic trends, packet size distribution, and top communicating hosts.

The system focuses on **data analysis and visualization**, forming a strong foundation for future **machine learning–based anomaly detection and cybersecurity applications**. By transforming raw packet data into structured features and visual representations, the project enables better understanding of network behavior and potential security risks.

---

##  Project Overview

The **Network Traffic Analysis with Machine Learning** project analyzes captured network packet data stored in CSV format and performs structured traffic analysis. The system decodes packet attributes such as protocol type, packet size, timestamps, and source information, and generates analytical insights along with visual outputs.

This project was developed as an **academic final-year networking and data analysis project**, demonstrating how packet-level traffic data can be processed, analyzed, and prepared for ML-based security solutions.

---

##  Objectives

* Analyze network traffic using packet-level data
* Identify protocol usage and traffic distribution patterns
* Visualize traffic trends over time
* Detect top communicating hosts (top talkers)
* Prepare meaningful features for future ML-based anomaly detection
* Understand practical applications of network traffic analysis in cybersecurity

---

##  System Architecture

The system follows a modular architecture with the following components:

* **Packet Capture Module:** Handles packet data input from CSV files
* **Packet Decoder Module:** Extracts relevant packet attributes such as protocol, size, and timestamps
* **Traffic Analyzer Module:** Performs statistical analysis on decoded data
* **Visualization Layer:** Generates graphical outputs for easy interpretation

This modular separation improves **scalability, maintainability, and readability** of the system.

---

##  Functional Features

* Packet-level traffic analysis
* Protocol distribution analysis
* Traffic volume analysis over time
* Packet size variation analysis
* Identification of top network talkers
* Graphical visualization of analysis results
* Maven-based project structure

---

##  Technology Stack

### Programming & Backend

* **Java**
* **Maven**

### Data & Analysis

* CSV-based packet datasets
* Statistical traffic analysis techniques

### Visualization

* PNG-based graph outputs

### Tools & Environment

* Eclipse / IntelliJ IDEA
* Git & GitHub

---

##  Project Structure

```
NetworkTrafficAnalyzer/
│
├── src/
│   └── main/java/com/alok/trafficanalyzer/
│       ├── Analyzer.java
│       ├── PacketCapture.java
│       └── PacketDecoder.java
│
├── data/
│   ├── packets.csv
│   └── captured_packets.csv
│
├── network_traffic_analyzer-commented.java
├── pom.xml
├── .gitignore
└── README.md
```

---

##  Sample Output & Visualizations

The system generates multiple visual outputs to represent network traffic behavior, including:

* Protocol Distribution Graph
* Packets vs Time Analysis
* Packet Size vs Time Analysis
* Top Network Talkers Visualization

These visualizations assist in quickly understanding traffic patterns and identifying anomalies.

---

##  Installation & Setup

### Step 1: Clone the Repository

```bash
git clone https://github.com/preeti-kammar/Network-Traffic-Analysis-with-ML-.git
cd NetworkTrafficAnalyzer
```

### Step 2: Build the Project

```bash
dir pom.xml
mvn clean install
```

### Step 3: Run the Application

```bash
mvn exec:java -Dexec.mainClass="com.alok.trafficanalyzer.PacketCapture"
```

---

##  Results & Outcomes

* Successful analysis of packet-level network data
* Clear visualization of protocol usage and traffic trends
* Identification of top communicating hosts
* Structured feature extraction suitable for ML models
* Demonstration of practical network traffic analysis techniques
  
* Output Details :
  
Simple explanations for each of the network analysis graphs shown as the outputs:

1.Packets vs Size
This graph shows how many packets were observed for different packet sizes. 
The tall, thin bars show that packets of specific sizes (such as 300, 400, 1200, and 1500 bytes) appeared, 
with each size occurring about equally often.

2.Packets vs Time 
This graph shows how many network packets are sent or received over time, measured per second. 
The flat, straight line indicates that packets were transmitted at a constant rate during the captured period.

3.Protocol Distribution
This bar chart shows which network protocols (like UDP, TCP, and ICMP) were used the most. 
UDP and TCP were observed the most frequently and equally, while ICMP appeared less often during the capture.

4.Size vs Time
This graph shows the total size of network traffic (in bytes) over time. 
The line goes up and down, meaning the amount of data transferred varied at each moment. 
Sometimes there was less traffic, other times it spiked quickly.

5.Top Source IPs
This bar chart displays which devices (source IP addresses) sent the most packets in the network. 
Each bar represents a different IP, and similar heights mean each device contributed nearly the same amount of traffic.

---

##  Future Enhancements

* Integration of machine learning models for anomaly detection
* Real-time packet capture and live traffic monitoring
* Dashboard-based visualization using web technologies
* Support for large-scale network datasets
* Extension to intrusion detection systems (IDS)

---

##  Learning Outcomes

* Understanding of packet-level network traffic behavior
* Feature extraction from raw network data
* Data visualization techniques for traffic analysis
* Modular Java application design
* Strong foundation for ML-based network security solutions

---
