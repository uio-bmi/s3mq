package no.uio.ifi.localega.s3mq

import com.rabbitmq.client.ConnectionFactory
import no.uio.ifi.localega.s3mq.consumers.S3MQConsumer

val INBOX_LOCATION: String = System.getenv("INBOX_LOCATION")
val MQ_CONNECTION: String = System.getenv("MQ_CONNECTION")
val QUEUE_FROM: String = System.getenv("QUEUE_FROM") ?: "inbox"
val EXCHANGE_TO: String = System.getenv("EXCHANGE_TO") ?: "cega"
val ROUTING_KEY: String = System.getenv("ROUTING_KEY") ?: "files.inbox"

fun main() {

    val factory = ConnectionFactory()
    factory.setUri(MQ_CONNECTION)
    val connection = factory.newConnection()
    val channel = connection.createChannel()
    channel.basicConsume(
        QUEUE_FROM, false,
        S3MQConsumer(
            channel,
            INBOX_LOCATION,
            EXCHANGE_TO,
            ROUTING_KEY
        )
    )

}
