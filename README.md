Here’s your updated **professional README** with **tech stack logos from [Icons8](https://icons8.com/icons)** embedded for each technology using markdown image links. These icons are optimized for GitHub rendering.

---

# 🖼️ Remove Image Background – Full Stack AI Application

A comprehensive full-stack solution for **automatic image background removal**, built with a **React frontend**, **Spring Boot backend**, and a **Flask-based ML microservice**.

---

## 📚 Table of Contents

* [✨ Features](#-features)
* [📁 Project Structure](#-project-structure)
* [🧰 Tech Stack](#-tech-stack)
* [⚙️ Installation](#-installation)
* [🔧 Configuration](#-configuration)
* [🚀 Running the Application](#-running-the-application)
* [📡 API Documentation](#-api-documentation)
* [💰 Payment Flow](#-payment-flow)
* [🤝 Contributing](#-contributing)
* [📄 License](#-license)

---

## ✨ Features

* 🧠 **AI-powered background removal** using a custom Python ML service (`rembg`)
* 🔐 **User authentication** via Clerk
* 💳 **Premium subscription** support via Razorpay
* 🎨 **Responsive frontend UI** with React and Tailwind CSS
* 🔄 **RESTful backend** powered by Spring Boot
* 🗄️ **PostgreSQL** database for persistent storage

---

## 📁 Project Structure

```text
├── backend/         → Spring Boot application
├── frontend/        → React + Tailwind CSS frontend
└── ml_service/      → Flask ML microservice using rembg
```

| Branch                                                                                                        | Description                                       |
| ------------------------------------------------------------------------------------------------------------- | ------------------------------------------------- |
| [`main`](https://github.com/soumyadip-adak99/Remove-image-background-spring-boot-project)                     | Core backend with Spring Boot, DB, payment, Clerk |
| [`Font-end`](https://github.com/soumyadip-adak99/Remove-image-background-spring-boot-project/tree/Font-end)   | React frontend (image UI, auth, payment UI)       |
| [`ML-branch`](https://github.com/soumyadip-adak99/Remove-image-background-spring-boot-project/tree/ML-branch) | Flask microservice with AI model                  |

---

## 🧰 Tech Stack

| Component                                                                                                                   | Technology                           |
|-----------------------------------------------------------------------------------------------------------------------------|--------------------------------------|
| <img src="https://img.icons8.com/?size=100&id=NfbyHexzVEDk&format=png" alt="React" height=35 width=35/>                     | **React** - Frontend framework       |
| <img src="https://img.icons8.com/?size=100&id=x7XMNGh2vdqA&format=png" alt="Tailwind CSS" height=35 width=35/>              | **Tailwind CSS** - Utility-first CSS |
| <img src="https://img.icons8.com/?size=100&id=H0YdC9fN7UOE&format=png" alt="Spring Boot" height=35 width=35/>              | **Spring Boot** - Backend framework  |
| <img src="https://img.icons8.com/?size=100&id=1Y34I85zxcjS&format=png" alt="Flask" height=35 width=35/>                     | **Flask** - Python ML microservice   |
| <img src="https://img.icons8.com/?size=100&id=38561&format=png" alt="PostgreSQL" height=35 width=35/>                       | **PostgreSQL** - Relational database |
| <img src="https://img.icons8.com/?size=100&id=RSkSAisj8s13&format=png" alt="Clerk" height=35 width=35/>                     | **Clerk** - Authentication           |
| <img src="https://img.icons8.com/?size=100&id=9uEfV0N3Nt8l&format=png" alt="Razorpay" height=35 width=35/>                  | **Razorpay** - Payment gateway       |

> Icons by [Icons8](https://icons8.com)

---

## ⚙️ Installation

### 📌 Prerequisites

* Java 17+
* Node.js 16+
* Python 3.8+
* PostgreSQL 12+

---

### 🔽 Clone the Repository

```bash
git clone https://github.com/soumyadip-adak99/Remove-image-background-spring-boot-project.git
cd Remove-image-background-spring-boot-project
```

---

### 🌐 Frontend Setup

```bash
git checkout Font-end
cd frontend
npm install
```

### 🖥 Backend Setup

```bash
git checkout main
cd backend
./mvnw clean install
```

### 🧠 ML Service Setup

```bash
git checkout ML-branch
cd ml_service
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate
pip install -r requirements.txt
```

---

## 🔧 Configuration

### ☕ Spring Boot (`backend/src/main/resources/application.properties`)

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bg_removal_db
spring.datasource.username=postgres
spring.datasource.password=yourpassword

clerk.publishable-key=your_clerk_pub_key
clerk.secret-key=your_clerk_secret

razorpay.key-id=your_razorpay_id
razorpay.key-secret=your_razorpay_secret

ml.service.url=http://localhost:5000
```

---

### ⚛️ React Frontend (`frontend/.env`)

```env
REACT_APP_API_BASE_URL=http://localhost:8080/api
REACT_APP_CLERK_PUBLISHABLE_KEY=your_clerk_pub_key
```

---

### 🧬 ML Service (`ml_service/.env`)

```env
FLASK_ENV=development
UPLOAD_FOLDER=./uploads
ALLOWED_EXTENSIONS={'png', 'jpg', 'jpeg'}
```

---

## 🚀 Running the Application

### 1️⃣ Start Flask ML Service

```bash
cd ml_service
flask run
```

### 2️⃣ Start Spring Boot Backend

```bash
cd backend
./mvnw spring-boot:run
```

### 3️⃣ Start React Frontend

```bash
cd frontend
npm start
```

➡️ Access the app at [http://localhost:3000](http://localhost:3000)

---

## 📡 API Documentation

| Endpoint                    | Method | Description                         |
| --------------------------- | ------ | ----------------------------------- |
| `/api/images/upload`        | `POST` | Upload image for background removal |
| `/api/images/{id}`          | `GET`  | Retrieve processed image            |
| `/api/images/user`          | `GET`  | Get all images uploaded by user     |
| `/api/payment/create-order` | `POST` | Create Razorpay order               |
| `/api/payment/verify`       | `POST` | Verify Razorpay payment             |

---

## 💰 Payment Flow

1. User selects a premium plan on the frontend
2. Frontend sends a request to the backend to create a Razorpay order
3. Razorpay Checkout is initialized
4. Upon successful payment, backend verifies the order
5. User account is upgraded

---

## 🤝 Contributing

Contributions are welcome!
Fork the repo → create a new branch → submit a pull request ✅

---

## 📄 License

Licensed under the [MIT License](LICENSE)

---

Would you like this exported as a downloadable `README.md` file or embedded in your GitHub project directly?
