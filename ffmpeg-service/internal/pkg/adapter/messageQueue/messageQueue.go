package messageQueue

type Message struct {
	Body    []byte
	Headers map[string]interface{}
}

type MessageBroker interface {
	Consume(queueName string, consumerId string) (<-chan Message, error)
}
