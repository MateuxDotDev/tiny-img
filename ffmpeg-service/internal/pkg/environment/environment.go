package environment

import (
	"fmt"
	"os"
	"reflect"

	"github.com/joho/godotenv"
)

type Environment struct {
	RabbitMQURL          string `env:"RABBITMQ_URL"`
	RabbitMQQueueName    string `env:"RABBITMQ_QUEUE_NAME"`
	RabbitMQConsumerName string `env:"RABBITMQ_CONSUMER_NAME"`
	BasePath             string `env:"BASE_PATH"`
	SupportedExtensions  string `env:"SUPPORTED_EXTENSIONS"`
	DownscaleSizes       string `env:"DOWNSCALE_SIZES"`
}

func loadEnvironmentVars(cfg *Environment) error {
	val := reflect.ValueOf(cfg).Elem()
	typ := val.Type()

	for i := 0; i < val.NumField(); i++ {
		field := val.Field(i)
		tag := typ.Field(i).Tag.Get("env")

		if envValue := os.Getenv(tag); envValue != "" {
			field.SetString(envValue)
		} else {
			return fmt.Errorf("%s environment variable not set", tag)
		}
	}

	return nil
}

func NewEnvironment() (*Environment, error) {
	godotenv.Load()

	cfg := &Environment{}
	if err := loadEnvironmentVars(cfg); err != nil {
		return nil, err
	}

	return cfg, nil
}
