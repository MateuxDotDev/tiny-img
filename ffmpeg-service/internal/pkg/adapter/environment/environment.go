package environment

import (
	"fmt"
	"os"
	"reflect"
	"sync"

	"github.com/joho/godotenv"
)

type Environment struct {
	RabbitMQHost         string `env:"RABBITMQ_HOST"`
	RabbitMQPort         string `env:"RABBITMQ_PORT"`
	RabbitMQUser         string `env:"RABBITMQ_USER"`
	RabbitMQPassword     string `env:"RABBITMQ_PASSWORD"`
	RabbitMQQueueName    string `env:"RABBITMQ_QUEUE_NAME"`
	RabbitMQConsumerName string `env:"RABBITMQ_CONSUMER_NAME"`
}

var (
	instance *Environment
	once     sync.Once
)

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
	var err error
	once.Do(func() {
		godotenv.Load()

		instance = &Environment{}
		err = loadEnvironmentVars(instance)
		if err != nil {
			instance = nil
		}
	})

	if err != nil {
		return nil, err
	}

	return instance, nil
}

func GetInstance() (*Environment, error) {
	return NewEnvironment()
}
