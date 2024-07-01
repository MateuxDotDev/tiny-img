package rabbitmq

import (
	"fmt"
	"net/url"

	"github.com/rabbitmq/amqp091-go"
)

type RabbitMQ struct {
	conn *amqp091.Connection
}

func NewRabbitMQ(host string, port string, user string, password string) (*RabbitMQ, error) {
	connectionUrl := fmt.Sprintf("amqp://%s:%s@%s:%s/", url.QueryEscape(user), url.QueryEscape(password), host, port)
	conn, err := amqp091.Dial(connectionUrl)

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

func (r *RabbitMQ) Consume(queueName string, consumerId string) (<-chan amqp091.Delivery, error) {
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

	return msgs, nil
}
