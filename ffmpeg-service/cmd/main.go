package main

import (
	"log"
	"mateux/dev/ffmpeg-processor/internal/pkg/environment"
	"mateux/dev/ffmpeg-processor/internal/pkg/rabbitmq"
	"os"
	"os/exec"
	"path/filepath"
	"strings"
	"sync"
	"time"

	"github.com/rabbitmq/amqp091-go"
)

func main() {
	env, err := environment.NewEnvironment()
	if err != nil {
		log.Fatalf("Failed to load envs: %v", err)
	}

	rmq, err := rabbitmq.NewRabbitMQ(env.RabbitMQURL)
	if err != nil {
		log.Fatalf("Failed to create RabbitMQ instance: %v", err)
	}
	defer rmq.Close()

	msgs, err := rmq.Consume(env.RabbitMQQueueName, env.RabbitMQConsumerName)
	if err != nil {
		log.Fatalf("Failed to consume messages: %v", err)
	}

	var wg sync.WaitGroup
	for msg := range msgs {
		wg.Add(1)
		go func(body []byte, headers amqp091.Table) {
			defer wg.Done()

			correlationId := headers["X-Correlation-Id"].(string)
			log.Printf("New message received %s", correlationId)
			processMessage(body, env.BasePath, env.DownscaleSizes, env.SupportedExtensions)
		}(msg.Body, msg.Headers)
	}
	wg.Wait()
}

func processMessage(body []byte, basePath string, downscaleSizes string, supportedExtensions string) {
	initTime := time.Now()
	imagePath := string(body)

	userUUID := strings.Split(filepath.Dir(imagePath), "/")
	imageName := filepath.Base(imagePath)
	imageNameWithoutExt := strings.TrimSuffix(imageName, filepath.Ext(imageName))
	imageExt := filepath.Ext(imageName)[1:]
	if imageExt == "" {
		log.Printf("No file extension found for image %s", imageName)
		return
	}

	for _, size := range strings.Split(downscaleSizes, ",") {
		outputFolder := filepath.Join(basePath, userUUID[0], userUUID[1], size)
		if err := os.MkdirAll(outputFolder, 0755); err != nil {
			log.Printf("Failed to create output folder %s: %v", outputFolder, err)
			continue
		}

		for _, ext := range strings.Split(supportedExtensions, ",") {
			if imageExt == ext {
				continue
			}

			outputFile := filepath.Join(outputFolder, imageNameWithoutExt+"."+ext)
			sourceFile := filepath.Join(basePath, imagePath)
			cmd := exec.Command("ffmpeg", "-i", sourceFile, "-vf", "scale="+size+":-1", outputFile)
			err := cmd.Run()
			if err != nil {
				log.Printf("Failed to process image %s: %v", imageName, err)
				continue
			}
		}
	}

	log.Printf("Image %s processed in %v ", imageName, time.Since(initTime))
}
