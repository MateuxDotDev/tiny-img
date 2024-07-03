FROM golang:1.22

RUN apt-get update && apt-get install -y ffmpeg

WORKDIR /usr/src/app

COPY go.mod go.sum ./
RUN go mod download && go mod verify

COPY . .

RUN go build -v -o /usr/local/bin/app ./cmd

CMD ["app", "os"]
