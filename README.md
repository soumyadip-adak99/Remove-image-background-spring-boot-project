Here's an updated professional README.md file tailored specifically for your GitHub repository structure and project organization:

# Remove Image Background - Full Stack Application

A comprehensive full-stack solution for automatic image background removal, featuring a React frontend, Spring Boot backend, and Flask-based ML service.

## Table of Contents

- [Features](#features)
- [Project Structure](#project-structure)
- [Tech Stack](#tech-stack)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Payment Flow](#payment-flow)
- [Contributing](#contributing)
- [License](#license)

## Features

- 🖼️ AI-powered background removal with custom Flask ML service
- 👤 User authentication via Clerk
- 💳 Premium features with Razorpay integration
- 📁 Image history tracking
- ⚡ Responsive UI with React and Tailwind CSS
- 🔄 RESTful API with Spring Boot
- 🗄️ PostgreSQL database for data persistence

## Project Structure

The repository is organized into three main branches:

### 1. [Main Branch](https://github.com/soumyadip-adak99/Remove-image-background-spring-boot-project)
- Contains core Spring Boot backend
- Handles authentication, payment processing, and API endpoints
- Manages database operations with PostgreSQL

### 2. [Front-end Branch](https://github.com/soumyadip-adak99/Remove-image-background-spring-boot-project/tree/Font-end)
- React application with Tailwind CSS
- Includes pages for:
    - Image upload and processing
    - User authentication flows
    - Payment integration
    - Image history display

### 3. [ML-branch](https://github.com/soumyadip-adak99/Remove-image-background-spring-boot-project/tree/ML-branch)
- Flask-based microservice for image processing
- Implements background removal algorithm
- Communicates with Spring Boot backend

## Tech Stack

| Component        | Technology |
|-----------------|------------|
| Frontend        | React, Tailwind CSS, Axios |
| Backend         | Spring Boot, Spring Security, Spring Data JPA |
| ML Service      | Flask, OpenCV, Pillow |
| Database        | PostgreSQL |
| Authentication  | Clerk |
| Payment         | Razorpay |

## Installation

### Prerequisites

- Java JDK 17+
- Node.js 16+
- Python 3.8+
- PostgreSQL 12+

### Clone the Repository

```bash
git clone https://github.com/soumyadip-adak99/Remove-image-background-spring-boot-project.git
cd Remove-image-background-spring-boot-project
```

### Frontend Setup

```bash
git checkout Font-end
cd frontend
npm install
```

### Backend Setup

```bash
git checkout main
cd backend
./mvnw clean install
```

### ML Service Setup

```bash
git checkout ML-branch
cd ml_service
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate
pip install -r requirements.txt
```

## Configuration

### Backend (Spring Boot)

Create `application.properties` in `backend/src/main/resources`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/bg_removal_db
spring.datasource.username=postgres
spring.datasource.password=yourpassword

# Clerk
clerk.publishable-key=your_clerk_key
clerk.secret-key=your_clerk_secret

# Razorpay
razorpay.key-id=your_razorpay_id
razorpay.key-secret=your_razorpay_secret

# ML Service
ml.service.url=http://localhost:5000
```

### Frontend (React)

Create `.env` in `frontend`:

```env
REACT_APP_API_BASE_URL=http://localhost:8080/api
REACT_APP_CLERK_PUBLISHABLE_KEY=your_clerk_pub_key
```

### ML Service (Flask)

Create `.env` in `ml_service`:

```env
FLASK_ENV=development
UPLOAD_FOLDER=./uploads
ALLOWED_EXTENSIONS={'png', 'jpg', 'jpeg'}
```

## Running the Application

1. **Start ML Service**:
   ```bash
   cd ml_service
   flask run
   ```

2. **Start Spring Boot Backend**:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

3. **Start React Frontend**:
   ```bash
   cd frontend
   npm start
   ```

Access the application at `http://localhost:3000`

## API Documentation

Key Endpoints:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/images/upload` | POST | Upload image for processing |
| `/api/images/{id}` | GET | Retrieve processed image |
| `/api/images/user` | GET | Get user's image history |
| `/api/payment/create-order` | POST | Create Razorpay order |
| `/api/payment/verify` | POST | Verify payment |

## Payment Flow

1. User selects premium plan
2. Frontend requests order creation from backend
3. Razorpay checkout initialized
4. Backend verifies payment upon completion
5. User account upgraded upon successful verification

## License

MIT License - See [LICENSE](LICENSE) for details.
