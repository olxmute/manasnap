# Manasnap

A Spring Boot application that asynchronously fetches PNG image URLs for Magic: The Gathering cards using the Scryfall
API.

The application processes card name requests in the background and provides status tracking for each operation.

## Features

- Asynchronous processing of card name requests
- Non-blocking REST API endpoints
- Background fetching of card PNG URLs from Scryfall API
- Operation status tracking
- H2 database for storing operation results
- Docker support for easy deployment

## Prerequisites

- Java 21 or higher
- Gradle 8.x or higher
- Docker (optional, for containerized deployment)

## Building the Project

### Using Gradle

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

### Using Docker

```bash
# Build the project first
./gradlew build

# Build the Docker image
docker build -t manasnap .

# Run the container
docker run -p 8080:8080 manasnap
```

## API Documentation

### 1. Submit Card Names for Processing

**Endpoint:** `POST /cards`

**Request Body:**

```json
{
  "cardNames": [
    "Lightning Bolt",
    "Black Lotus",
    "Counterspell"
  ]
}
```

**Response:**

```json
{
  "operationId": "some-generated-id"
}
```

### 2. Check Operation Status

**Endpoint:** `GET /cards/{operationId}`

**Possible Responses:**

1. Processing:

```json
{
  "operationId": "some-generated-id",
  "status": "PROCESSING"
}
```

2. Success:

```json
{
  "operationId": "some-generated-id",
  "status": "SUCCESS",
  "results": [
    {
      "cardName": "Lightning Bolt",
      "pngUrl": "https://..."
    }
  ]
}
```

3. Partial Success:

```json
{
  "operationId": "some-generated-id",
  "status": "PARTIAL_SUCCESS",
  "results": [
    {
      "cardName": "Lightning Bolt",
      "pngUrl": "https://..."
    }
  ],
  "failures": [
    {
      "cardName": "Black Lotus",
      "error": "Card not found"
    }
  ]
}
```

4. Failure:

```json
{
  "operationId": "some-generated-id",
  "status": "FAILURE",
  "error": "All requests failed"
}
```
