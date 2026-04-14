


💬 Real-Time Chat Application (Java)

<p align="center">
  <b>A multi-user real-time chat system built using Java Socket Programming & Swing GUI</b>
</p>


<p align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white"/>
  <img src="https://img.shields.io/badge/Sockets-Networking-blue?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/GUI-Swing-green?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Multithreading-Concurrency-orange?style=for-the-badge"/>
</p>





🚀 Overview

This project is a real-time chat application that enables multiple users to communicate simultaneously using a client-server architecture.

It simulates how real-world messaging systems like WhatsApp work at a fundamental level using Java networking concepts.


✨ Features
	•	💬 Real-time communication between multiple clients
	•	🔒 Private messaging (user-to-user)
	•	📢 Broadcast messaging (send to all users)
	•	👥 Dynamic user list updates
	•	⌨️ Typing indicator (live feedback)
	•	🎨 WhatsApp-style UI with chat bubbles
	•	⚡ Multithreaded server for handling multiple clients



🛠️ Tech Stack

Technology	Purpose
Java	Core programming language
Socket Programming	Communication between client & server
Swing	GUI (User Interface)
Multithreading	Handle multiple users simultaneously




## 🏗 Project Architecture

- ChatServer handles all client connections
- Each client connects via socket
- Every client runs in a separate thread
- Server manages user list and message routing


	•	Server listens on a port
	•	Each client connects via socket
	•	Each connection handled in a separate thread

⸻

⚙️ How It Works
	1.	Server starts and waits for connections
	2.	Clients connect and send their username
	3.	Server maintains active user list
	4.	Messages are handled as:
	•	PRIVATE:user:message → Direct message
	•	Normal message → Broadcast
	5.	Server routes messages to appropriate clients

⸻




▶️ How to Run

1️⃣ Start Server

Run ChatServer.java


⸻

2️⃣ Start Clients

Run LoginGUI.java multiple times


⸻

3️⃣ Start Chatting 🎉
	•	Select a user → Private message
	•	Click Send All → Broadcast
	•	See real-time updates across windows

⸻

📸 Demo

Real-time messaging with multiple users

<img width="2880" height="1800" alt="image" src="https://github.com/user-attachments/assets/451d55b7-2f91-4f9c-926b-4de290bc1e09" />


⸻

📚 Concepts Covered
	•	Client-Server Architecture
	•	Socket Programming
	•	Multithreading
	•	Event Handling
	•	GUI Design

⸻

🔥 Key Highlights
	•	Built from scratch without frameworks
	•	Handles multiple clients concurrently
	•	Clean UI with message bubbles
	•	Simulates real-world chat systems

⸻

🚀 Future Enhancements
	•	💾 Chat history using database (MySQL / Firebase)
	•	🔐 User authentication system
	•	📁 File sharing feature
	•	🌐 LAN / Internet-based communication
	•	📱 Android App version

⸻

🤝 Contribution

Feel free to fork this repo and enhance it!

⸻

👨‍💻 Author

Aditya Malode
	•	🔗 GitHub: https://github.com/Aditya02134

⸻

⭐ Support

If you like this project:

👉 Give it a ⭐ on GitHub
👉 Share your feedback

⸻


<p align="center">
  <b>🚀 Built with passion for learning & development</b>
</p>
:::



