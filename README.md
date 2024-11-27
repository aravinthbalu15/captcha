# Simple Captcha

A robust and secure session-based CAPTCHA application built with Java Spring Boot. This application provides a modern, efficient way to protect your web forms from automated submissions and spam bots.

## Features

- Session-based CAPTCHA validation
- Secure random code generation
- Customizable CAPTCHA image generation
- Admin panel for CAPTCHA management
- Rate limiting for CAPTCHA requests
- RESTful API endpoints
- Modern and responsive UI

## Technology Stack

- **Backend**: Java Spring Boot
- **Frontend**: JSP, JavaScript
- **Database**: PostgreSQL
- **Build Tool**: Maven
- **Container**: Docker

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- PostgreSQL 12+

## Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/simple-captcha.git
cd simple-captcha
```

2. Build the project:
```bash
mvn clean install
```

3. Start the application using Docker Compose:
```bash
docker-compose up -d
```

The application will be available at `http://localhost:8080`

## Usage

### User Interface

1. Access the main page at `http://localhost:8080`
2. A CAPTCHA image will be displayed with a text input field
3. Enter the code shown in the image
4. Submit the form to validate the CAPTCHA

### Admin Interface

1. Access the admin panel at `http://localhost:8080/admin`
2. View all generated CAPTCHAs
3. Add new CAPTCHAs
4. Delete existing CAPTCHAs
5. Monitor CAPTCHA usage

## API Endpoints

- `GET /captcha/image` - Get a new CAPTCHA image
- `POST /captcha/validate` - Validate a CAPTCHA code
- `GET /admin/captcha` - List all CAPTCHAs (Admin only)
- `PUT /admin/captcha` - Create a new CAPTCHA (Admin only)
- `DELETE /admin/captcha` - Delete a CAPTCHA (Admin only)

## Security Features

- Session-based validation
- Rate limiting (maximum 4 CAPTCHA changes per session)
- 30-second expiration time for each CAPTCHA
- Secure random code generation
- Protection against brute force attacks