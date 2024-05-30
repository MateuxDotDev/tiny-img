package messageQueue

import (
	"log"
	"sync"

	"mateux/dev/ffmpeg-service/internal/pkg/adapter/environment"

	"github.com/rabbitmq/amqp091-go"
)

type RabbitMQ struct {
	conn *amqp091.Connection
}

func NewRabbitMQ(url string) (MessageBroker, error) {
	conn, err := amqp091.Dial(url)
	if err != nil {
		return nil, err
	}
	return &RabbitMQ{conn: conn}, nil
}

func (r *RabbitMQ) Close() {
	if r.conn != nil {
		r.conn.Close()
	}
}

func (r *RabbitMQ) Consume(queueName string, consumerId string) (<-chan Message, error) {
	ch, err := r.conn.Channel()
	if err != nil {
		return nil, err
	}

	_, err = ch.QueueDeclare(
		queueName,
		false,
		false,
		false,
		false,
		nil,
	)
	if err != nil {
		return nil, err
	}

	msgs, err := ch.Consume(
		queueName,
		consumerId,
		true,
		false,
		false,
		false,
		nil,
	)
	if err != nil {
		return nil, err
	}

	messageChannel := make(chan Message)
	go func() {
		for d := range msgs {
			messageChannel <- Message{
				Body:    d.Body,
				Headers: d.Headers,
			}
		}
		close(messageChannel)
	}()

	return messageChannel, nil
}

var (
	instance MessageBroker
	once     sync.Once
)

func NewMessageBroker() (MessageBroker, error) {
	var err error
	once.Do(func() {
		env, loadErr := environment.GetInstance()
		if loadErr != nil {
			log.Fatalf("Failed to load envs: %v", loadErr)
			return
		}

		instance, err = NewRabbitMQ(env.RabbitMQURL)
		if err != nil {
			log.Fatalf("Failed to create RabbitMQ instance: %v", err)
			instance = nil
		}

	})

	if err != nil {
		return nil, err
	}

	return instance, nil
}
