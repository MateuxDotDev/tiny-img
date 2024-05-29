

### RabbitMQ

You can use the following command to run RabbitMQ using docker:

```bash
docker run -p 15672:15672 -p 5672:5672 -p 25676:25676 -d --name ffmpeg-processor-queue --restart unless-stopped rabbitmq:3-management
```

https://chemidy.medium.com/create-the-smallest-and-secured-golang-docker-image-based-on-scratch-4752223b7324