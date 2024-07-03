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

func (r *RabbitMQ) DeclareQueue(queueName string, routingKey string) error {
	ch, err := r.conn.Channel()
	if err != nil {
		return err
	}
	defer ch.Close()

	_, err = ch.QueueDeclare(
		queueName,
		true,
		false,
		false,
		false,
		nil,
	)
	if err != nil {
		return err
	}

	err = ch.QueueBind(
		queueName,
		routingKey,
		queueName,
		false,
		nil,
	)
	if err != nil {
		return err
	}

	return nil
}

func (r *RabbitMQ) Consume(queueName string, routingKey string, consumerId string) (<-chan amqp091.Delivery, error) {
	ch, err := r.conn.Channel()
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

func (r *RabbitMQ) Publish(exchangeName string, routingKey string, body []byte) error {
	ch, err := r.conn.Channel()
	if err != nil {
		return err
	}

	err = ch.Publish(
		exchangeName,
		routingKey,
		false,
		false,
		amqp091.Publishing{
			ContentType: "application/json",
			Body:        body,
		},
	)
	if err != nil {
		return err
	}

	return nil
}
