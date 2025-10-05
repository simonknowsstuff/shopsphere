# ShopSphere

ShopSphere is a proof of concept e-commerce platform built with Spring Boot, demonstrating a comprehensive implementation of a modern online shopping system.

## Overview

This project was developed as part of an Object Oriented Programming class, showcasing best practices in software architecture and design patterns.

## Features

### Core Functionality
- Complete product catalog management
- Shopping cart functionality
- Order processing system
- Customer review system
- Vendor management
- Administrative controls
- Mock payment system integration

### Technical Features
- JWT-based authentication and authorization
- RESTful API architecture
- Model-View-Controller (MVC) design pattern
- Secure user management

## Architecture

The application follows a standard MVC architecture with the following controllers:
- Admin Controller: System administration and management
- Auth Controller: Authentication and authorization handling
- Cart Controller: Shopping cart operations
- Order Controller: Order processing and management
- Payment Controller: Payment processing (mock system)
- Product Controller: Product catalog management
- Review Controller: Customer review handling
- Vendor Controller: Vendor operations and management

## Technologies

- Spring Boot
- Spring Data JDBC
- Spring MVC
- Jakarta EE
- Java 21
- JWT for authentication

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 21
- Maven for dependency management

### Installation
1. Clone the repository
```
bash git clone https://github.com/simonknowsstuff/samplecommerce.git
``` 

2. Navigate to the project directory
```
bash cd shopsphere
``` 

3. Build the project
```
bash mvn clean install
``` 

4. Run the application
```
bash mvn spring-boot:run
``` 

## Security

The application implements security best practices including:
- JWT-based authentication
- Secure password handling
- Role-based access control

## Contributors
- Simon Binu
- Seraphin J Raphy

## Contributing

This is a proof-of-concept project created for educational purposes. While it's not actively maintained for production use, feedback and suggestions are welcome.

## License

[GNU General Public License](LICENSE)

## Acknowledgments

This project was developed as an educational initiative, putting Object-Oriented Programming principles into practice through a real-world e-commerce application. Special thanks to:

- The Spring Boot team for their excellent documentation and framework
- The Spring Framework community for their comprehensive guides and resources
- The faculty and peers who provided valuable feedback and insights throughout the development process

The project's architecture and implementation were guided by industry best practices and official Spring documentation.

