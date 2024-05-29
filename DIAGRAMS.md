# Sequence Diagrams

## Table of Contents

- [Successful sign up](#successful-sign-up)
- [Unsuccessful sign up - invalid form](#unsuccessful-sign-up---invalid-form)
- [Unsuccessful sign up - username already taken](#unsuccessful-sign-up---username-already-taken)
- [Successful sign in](#successful-sign-in)
- [Unsuccessful sign in - invalid form](#unsuccessful-sign-in---invalid-form)
- [Unsuccessful sign in - user not found](#unsuccessful-sign-in---user-not-found)
- [Unsuccessful sign in - invalid password](#unsuccessful-sign-in---invalid-password)
- [Successful image processing](#successful-image-processing)
- [Unsuccessful image processing - image too big](#unsuccessful-image-processing---image-too-big)
- [Unsuccessful image processing - too many images](#unsuccessful-image-processing---too-many-images)
- [Unsuccessful image processing - image processing error](#unsuccessful-image-processing---image-processing-error)
- [Successful image download](#successful-image-download)
- [Unsuccessful image download - too many downloads](#unsuccessful-image-download---too-many-downloads)
- [Successful image share](#successful-image-share)
- [Unsuccessful image share - too many shares](#unsuccessful-image-share---too-many-shares)

### Successful sign up

Expected payload sent by the user should be:

```json
{
  "username": "joseph.cooper",
  "password": "12345678",
  "email": "joseph.cooper@mail.com"
}
```

```mermaid
sequenceDiagram
    User->>Auth Service: send sign up form
    Auth Service->>Auth Service: validate form
    Auth Service->>Database: check username availability
    Database->>Auth Service: username available
    Auth Service->>Database: insert user
    Database->>Auth Service: user successfully inserted
    Auth Service->>Auth Service: generate auth and refresh tokens
    Auth Service->>User: auth + refresh tokens
```

### Unsuccessful sign up - invalid form

```mermaid
sequenceDiagram
    User->>Auth Service: send sign up form
    Auth Service->>Auth Service: validate form
    Auth Service->>User: invalid form
```

### Unsuccessful sign up - username already taken

```mermaid
sequenceDiagram
    User->>Auth Service: send sign up form
    Auth Service->>Auth Service: validate form
    Auth Service->>Database: check username availability
    Database->>Auth Service: username already taken
    Auth Service->>User: username already taken
```

---

### Successful sign in

Expected payload sent by the user:

```json
{
  "username": "joseph.cooper",
  "password": "12345678",
}
```

```mermaid
sequenceDiagram
    User->>Auth Service: send sign in form
    Auth Service->>Auth Service: validate form
    Auth Service->>Database: check user
    Database->>Auth Service: user found
    Auth Service->>Auth Service: validate password
    Auth Service->>Auth Service: generate auth and refresh tokens
    Auth Service->>User: auth + refresh tokens
```

### Unsuccessful sign in - invalid form

```mermaid
sequenceDiagram
    User->>Auth Service: send sign in form
    Auth Service->>Auth Service: validate form
    Auth Service->>User: invalid form
```

### Unsuccessful sign in - user not found

```mermaid
sequenceDiagram
    User->>Auth Service: send sign in form
    Auth Service->>Auth Service: validate form
    Auth Service->>Database: check user
    Database->>Auth Service: user not found
    Auth Service->>User: failed to sign in
```

### Unsuccessful sign in - invalid password

```mermaid
sequenceDiagram
    User->>Auth Service: send sign in form
    Auth Service->>Auth Service: validate form
    Auth Service->>Database: check user
    Database->>Auth Service: user found
    Auth Service->>Auth Service: validate password
    Auth Service->>User: failed to sign in
```

---

### Successful image processing

Expected payload sent by the user:

First, should upload the image as a file. The user will receive an `imageId` as a response.

Then, send the following payload:

```json
{
  "image": "imageId",
  "size": "50%",
  "format": "jpeg",
  "quality": "50"
}
```

```mermaid
sequenceDiagram
    User->>TinyImg Service: send image
    TinyImg Service->>TinyImg Service: validate image
    TinyImg Service->>TinyImg Service: store image
    TinyImg Service->>File Storage: store image
    File Storage->>TinyImg Service: image stored
    TinyImg Service->>Database: store image
    Database->>TinyImg Service: image stored
    TinyImg Service--)Message Queue: send image to be processed
    TinyImg Service->>User: image processing started
    Message Queue->>FFmpeg Service: message received
    FFmpeg Service--)Message Queue: notify image received
    Message Queue--)Notification Service: notify image received
    Notification Service->>User: image processing started
    FFmpeg Service->>FFmpeg Service: load image
    FFmpeg Service->>Message Queue: notify image loaded
    Message Queue--)Notification Service: notify image loaded
    FFmpeg Service->>FFmpeg Service: process image
    FFmpeg Service--)Message Queue: notify image processed
    Message Queue--)Notification Service: notify image processed
    Notification Service->>User: image processed
    FFmpeg Service->>FFmpeg Service: store processed image
    FFmpeg Service--)Message Queue: notify image stored
    Message Queue--)Notification Service: notify image stored
    Notification Service->>User: image stored and ready
```

### Unsuccessful image processing - image too big

```mermaid
sequenceDiagram
    User->>TinyImg Service: send image
    TinyImg Service->>TinyImg Service: validate image
    TinyImg Service->>User: image too big
```

### Unsuccessful image processing - too many images

```mermaid
sequenceDiagram
    User->>TinyImg Service: send image
    TinyImg Service->>TinyImg Service: validate image
    TinyImg Service->>User: too many images
```

### Unsuccessful image processing - image processing error

```mermaid
sequenceDiagram
    User->>TinyImg Service: send image
    TinyImg Service->>TinyImg Service: validate image
    TinyImg Service->>TinyImg Service: store image
    TinyImg Service--)Message Queue: send image to be processed
    TinyImg Service->>User: image processing started
    Message Queue->>FFmpeg Service: message received
    FFmpeg Service--)Message Queue: notify image received
    Message Queue--)Notification Service: notify image received
    Notification Service->>User: image processing started
    FFmpeg Service->>FFmpeg Service: load image
    FFmpeg Service->>Message Queue: notify image loaded
    Message Queue--)Notification Service: notify image loaded
    FFmpeg Service->>FFmpeg Service: process image
    FFmpeg Service--)Message Queue: notify image processed
    Message Queue--)Notification Service: notify image processed
    Notification Service->>User: image processing error
```

---

### Successful image download

```mermaid
sequenceDiagram
    User->>TinyImg Service: request image download
    TinyImg Service->>TinyImg Service: validate download
    TinyImg Service->>TinyImg Service: download image
    TinyImg Service->>User: image downloaded
```

### Unsuccessful image download - too many downloads

```mermaid
sequenceDiagram
    User->>TinyImg Service: request image download
    TinyImg Service->>TinyImg Service: validate download
    TinyImg Service->>User: too many downloads
```

---

### Successful image share

```mermaid
sequenceDiagram
    User->>TinyImg Service: request image share
    TinyImg Service->>TinyImg Service: validate share
    TinyImg Service->>TinyImg Service: share image
    TinyImg Service->>User: image shared
```

### Unsuccessful image share - too many shares

```mermaid
sequenceDiagram
    User->>TinyImg Service: request image share
    TinyImg Service->>TinyImg Service: validate share
    TinyImg Service->>User: too many shares
```
x