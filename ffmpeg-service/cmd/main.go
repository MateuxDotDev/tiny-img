package main

import (
	"encoding/json"
	"fmt"
	"log"
	"mateux/dev/ffmpeg-service/internal/pkg/adapter/environment"
	rabbitmq "mateux/dev/ffmpeg-service/internal/pkg/adapter/messageQueue"
	queuePayload "mateux/dev/ffmpeg-service/internal/pkg/dto/queuePayload"
	"net/http"
	"os"
	"os/exec"
	"path/filepath"
	"strconv"
	"sync"
	"time"

	"github.com/prometheus/client_golang/prometheus/promhttp"
)

var rmqInstance *rabbitmq.RabbitMQ = nil
var envInstance *environment.Environment = nil

func main() {
	args := os.Args[1:]

	env, err := environment.NewEnvironment(args)
	if err != nil {
		log.Fatalf("Failed to load envs: %v", err)
	}
	envInstance = env

	go startMetrics(env)

	rmq, err := rabbitmq.NewRabbitMQ(env.RabbitMQHost, env.RabbitMQPort, env.RabbitMQUser, env.RabbitMQPassword)
	if err != nil {
		log.Fatalf("Failed to create RabbitMQ instance: %v", err)
	}
	defer rmq.Close()
	rmqInstance = rmq

	consumeMessages(rmq, env)
}

func startMetrics(env *environment.Environment) {
	http.Handle("/q/metrics", promhttp.Handler())
	http.ListenAndServe(":"+env.MetricsPort, nil)
}

func consumeMessages(rmq *rabbitmq.RabbitMQ, env *environment.Environment) {
	err := rmq.DeclareQueue(env.RabbitMQOptimizeQueueName, env.RabbitMQRoutingKey)
	if err != nil {
		log.Fatalf("Failed to declare queue: %v", err)
	}

	msgs, err := rmq.Consume(env.RabbitMQOptimizeQueueName, env.RabbitMQRoutingKey, env.RabbitMQConsumerName)
	if err != nil {
		log.Fatalf("Failed to consume messages: %v", err)
	}

	log.Printf("Consuming messages from queue %s", env.RabbitMQOptimizeQueueName)
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
	notifyQueue(fmt.Sprintf("Image %s processing started", payload.ImageID), payload.User)

	outputFolder := getFolderFromPath(payload.OriginalImagePath)
	if err := os.MkdirAll(outputFolder, 0755); err != nil {
		notifyQueue(fmt.Sprintf("Failed to create output folder %s", outputFolder), payload.User)
		log.Printf("Failed to create output folder %s: %v", outputFolder, err)
		return
	}

	ffmpegArgs := buildFfmpegOptions(payload)
	cmd := exec.Command("ffmpeg", ffmpegArgs...)
	notifyQueue(fmt.Sprintf("FFmpeg processing image %s started", payload.ImageID), payload.User)
	err := cmd.Run()
	if err != nil {
		log.Printf("Failed to process image %s: %v", payload.ImageID, err)
	}

	notifyQueue(fmt.Sprintf("Image %s processed", payload.ImageID), payload.User)
	log.Printf("Image %s processed in %v ", payload.ImageID, time.Since(initTime))
}

func getPayload(body []byte) *queuePayload.QueuePayload {
	var payload queuePayload.QueuePayload
	if err := json.Unmarshal(body, &payload); err != nil {
		log.Printf("Failed to unmarshal payload: %v", err)
		return nil
	}

	return &payload
}

func getNewFilePath(payload *queuePayload.QueuePayload) string {
	return filepath.Join(getFolderFromPath(payload.OriginalImagePath), payload.ImageID+"."+payload.Format)
}

func getFolderFromPath(path string) string {
	return filepath.Dir(path)
}

func buildFfmpegOptions(payload *queuePayload.QueuePayload) []string {
	return []string{
		"-i", payload.OriginalImagePath,
		"-q:v", strconv.Itoa(payload.Quality),
		"-vf", fmt.Sprintf("scale=iw*%d/100:ih*%d/100", payload.Size, payload.Size),
		getNewFilePath(payload),
	}
}

func notifyQueue(message string, user string) {
	err := rmqInstance.Publish(envInstance.RabbitMQNotificationQueueName, envInstance.RabbitMQRoutingKey, []byte(fmt.Sprintf(`{"message": "%s", "targetUser": "%s"}`, message, user)))
	if err != nil {
		log.Printf("Failed to notify queue: %v", err)
	}
}
