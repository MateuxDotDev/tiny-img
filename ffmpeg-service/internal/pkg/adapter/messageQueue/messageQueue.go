package messageQueue

type Message struct {
    Body []byte
}

type MessageBroker interface {
    Consume(queueName string, consumerId string) (<-chan Message, error)
    Close()
}