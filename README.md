# 🚀 Symptom Checker Microservice - Docker Setup

This project is a **Spring Boot microservice** for a symptom checker. It is containerized using **Docker** and can be started using **Docker Compose**.

---

## 📌 Prerequisites
Before starting, ensure you have installed:
- [Docker](https://www.docker.com/get-started) 🐳
- [Docker Compose](https://docs.docker.com/compose/) 🛠️
- [Maven](https://maven.apache.org/) 📦

---

## 🚀 Build & Run the Application

### **1️⃣ Build the JAR**
Run the following command to package the Spring Boot application:

```sh
mvn clean install -DskipTests
```

### **1️⃣ Start the application to docker compose**
```sh
docker-compose up -d
```

### **1️⃣ See containers**
```sh
docker ps
```

### **1️⃣ Stop the containers**
```sh
docker-compose down
```
