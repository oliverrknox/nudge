# Nudge

<details>
    <summary>
        <strong>Table of contents</strong>
    </summary>
    <ul>
        <li>
            <a href="#description">Description</a>
            <ul>
                <li><a href="#built-with">Built with</a></li>
                <li><a href="#integrated-with">Integrated with</a></li>
            </ul>
        </li>
        <li>
            <a href="#getting-started">Getting started</a>
            <ul>
                <li><a href="#installation">Installation</a></li>
                <li><a href="#set-up">Set up</a></li>
            </ul>
        </li>
        <li><a href="#usage">Usage</a></li>
        <li><a href="#documentation">Documentation</a></li>
    </ul>
</details>

## Description

Nudge is a REST API to keep track of events. _"Nudges"_ are reminders of an event which can contain any number of _"Triggers"_.
These tell the system at what intervals to notify you of the event.

The application supports email notifications. Providing an email is a system requirement when creating user accounts in [Keycloak](https://keycloak.org).

### Built with

- [Spring](https://spring.io)

### Integrated with
- [Keycloak](https://keycloak.org)
- [Postgres](https://postgresql.org)
- [Send Grid](https://sendgrid.com)

## Getting started

### Installation

To install the application

1. Navigate to the root of this project.
2. Run the command:
    ```bash
    ./mvnw clean install
    ```

### Set up

To set up the application locally

- Set the following environment variables:
    
    | Variable         | Example | Info                                                  |
    |------------------| --- |-------------------------------------------------------|
    | DB_USERNAME      | postgres | The username of the user set in the database |
    | DB_PASSWORD      | ***** | The password of the user set in the database |
    | DB_URL           | jdbc:postgresql://localhost:5432/nudge | The JDBC URL of the database                          |
    | SENDGRID_API_KEY | ***** | The API key created within SendGrid's admin dashboard |
    

## Usage

To run the application

- Run the command:
    ```bash
    ./mvnw clean package
    java -jar target/nudge-{version}.jar
    ```

## Documentation

This application is documented using Open API. 

Visit the [Swagger UI](https://api.oliverknox.io/nudge-gateway/swagger-ui/index.html).