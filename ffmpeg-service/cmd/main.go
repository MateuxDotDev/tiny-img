package main

import (
	"encoding/json"
	"fmt"
	"log"
	"mateux/dev/ffmpeg-service/internal/pkg/adapter/environment"
	rabbitmq "mateux/dev/ffmpeg-service/internal/pkg/adapter/messageQueue"
	options "mateux/dev/ffmpeg-service/internal/pkg/queuePayload"
	"os"
	"os/exec"
	"path/filepath"
	"strconv"
	"sync"
	"time"
)

func main() {
	env, err := environment.NewEnvironment()
	if err != nil {
		log.Fatalf("Failed to load envs: %v", err)
	}

	rmq, err := rabbitmq.NewRabbitMQ(env.RabbitMQHost, env.RabbitMQPort, env.RabbitMQUser, env.RabbitMQPassword)
	if err != nil {
		log.Fatalf("Failed to create RabbitMQ instance: %v", err)
	}
	defer rmq.Close()

	consumeMessages(rmq, env)
}

func consumeMessages(rmq *rabbitmq.RabbitMQ, env *environment.Environment) {
	msgs, err := rmq.Consume(env.RabbitMQQueueName, env.RabbitMQConsumerName)
	if err != nil {
		log.Fatalf("Failed to consume messages: %v", err)
	}

	log.Printf("Consuming messages from queue %s", env.RabbitMQQueueName)
	var wg sync.WaitGroup
	for msg := range msgs {
		wg.Add(1)
		go func(body []byte) {
			defer wg.Done()

			processMessage(body)
		}(msg.Body)
	}
	wg.Wait()
}

func processMessage(body []byte) {
	initTime := time.Now()

	payload := getPayload(body)
	if payload == nil {
		return
	}

	log.Printf("Processing image %s for user %s", payload.ImageID, payload.User)

	outputFolder := getFolderFromPath(payload.OriginalImagePath)
	if err := os.MkdirAll(outputFolder, 0755); err != nil {
		log.Printf("Failed to create output folder %s: %v", outputFolder, err)
		return
	}

	ffmpegArgs := buildFfmpegOptions(payload)
	cmd := exec.Command("ffmpeg", ffmpegArgs...)
	err := cmd.Run()
	if err != nil {
		log.Printf("Failed to process image %s: %v", payload.ImageID, err)
	}

	log.Printf("Image %s processed in %v ", payload.ImageID, time.Since(initTime))
}

func getPayload(body []byte) *options.QueuePayload {
	var payload options.QueuePayload
	if err := json.Unmarshal(body, &payload); err != nil {
		log.Printf("Failed to unmarshal payload: %v", err)
		return nil
	}

	return &payload
}

func getNewFilePath(payload *options.QueuePayload) string {
	return filepath.Join(getFolderFromPath(payload.OriginalImagePath), payload.ImageID+"."+payload.Format)
}

func getFolderFromPath(path string) string {
	return filepath.Dir(path)
}

func buildFfmpegOptions(payload *options.QueuePayload) []string {
	return []string{
		"-i", payload.OriginalImagePath,
		"-q:v", strconv.Itoa(payload.Quality),
		"-vf", fmt.Sprintf("scale=iw*%d/100:ih*%d/100", payload.Size, payload.Size),
		getNewFilePath(payload),
	}
}
