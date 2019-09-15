package no.uio.ifi.localega.s3mq

import com.github.fridujo.rabbitmq.mock.MockConnectionFactory
import com.rabbitmq.client.*
import no.uio.ifi.localega.s3mq.consumers.S3MQConsumer
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.test.*

const val EXCHANGE_FROM = "lega"
const val QUEUE_FROM = "lega_inbox"
const val EXCHANGE_TO = "cega"
const val QUEUE_TO = "cega_inbox"
const val ROUTING_KEY = "files.inbox"

const val MESSAGE_IN = """
    {
   "Records":[
      {
         "eventVersion":"2.0",
         "eventSource":"aws:s3",
         "awsRegion":"",
         "eventTime":"2016-09-08T22:34:38.226Z",
         "eventName":"s3:ObjectCreated:Put",
         "userIdentity":{
            "principalId":"minio"
         },
         "requestParameters":{
            "sourceIPAddress":"10.1.10.150:44576"
         },
         "responseElements":{

         },
         "s3":{
            "s3SchemaVersion":"1.0",
            "configurationId":"Config",
            "bucket":{
               "name":"BUCKET_NAME",
               "ownerIdentity":{
                  "principalId":"minio"
               },
               "arn":"arn:aws:s3:::images"
            },
            "object":{
               "key":"FILE_NAME",
               "size":200436,
               "sequencer":"147279EAF9F40933"
            }
         }
      }
   ],
   "level":"info",
   "msg":"",
   "time":"2016-09-08T15:34:38-07:00"
}
"""

const val MESSAGE_OUT =
    "{\"encrypted_checksums\":[{\"type\":\"sha256\",\"value\":\"e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\"}],\"file_last_modified\":1473374078,\"filepath\":\"FILE_PATH\",\"filesize\":200436,\"operation\":\"upload\",\"user\":\"T\"}"

class S3MQConsumerTests {

    private val factory: ConnectionFactory = MockConnectionFactory()
    private val connection = factory.newConnection()
    private val channel = connection.createChannel()

    private lateinit var inboxLocation: String
    private lateinit var bucket: String

    private lateinit var tempFile: File

    @BeforeTest
    fun setup() {
        channel.exchangeDeclare(EXCHANGE_FROM, BuiltinExchangeType.TOPIC)
        channel.queueDeclare(QUEUE_FROM, true, true, false, null)
        channel.queueBind(
            QUEUE_FROM,
            EXCHANGE_FROM,
            ROUTING_KEY
        )

        channel.exchangeDeclare(EXCHANGE_TO, BuiltinExchangeType.TOPIC)
        channel.queueDeclare(QUEUE_TO, true, true, false, null)
        channel.queueBind(
            QUEUE_TO,
            EXCHANGE_TO,
            ROUTING_KEY
        )

        tempFile = createTempFile()
        inboxLocation = tempFile.parentFile.parentFile.absolutePath + "/"
        bucket = tempFile.parentFile.name
    }

    @Test
    fun test() {
        val messages = mutableListOf<String>()
        channel.basicConsume(
            QUEUE_FROM, false,
            S3MQConsumer(
                channel,
                inboxLocation,
                EXCHANGE_TO,
                ROUTING_KEY
            )
        )
        channel.basicConsume(QUEUE_TO, object : DefaultConsumer(channel) {
            override fun handleDelivery(
                consumerTag: String,
                envelope: Envelope,
                properties: AMQP.BasicProperties,
                body: ByteArray
            ) {
                messages.add(String(body, Charsets.UTF_8))
            }
        })
        channel.basicPublish(
            EXCHANGE_FROM,
            ROUTING_KEY,
            null,
            MESSAGE_IN.replace("BUCKET_NAME", bucket).replace("FILE_NAME", tempFile.name).toByteArray(Charsets.UTF_8)
        )
        TimeUnit.MILLISECONDS.sleep(1000L)
        assertTrue(messages.isNotEmpty())
        val message = messages.iterator().next()
        assertEquals(MESSAGE_OUT.replace("FILE_PATH", tempFile.absolutePath), message)
    }

    @AfterTest
    fun tearDown() {
        tempFile.delete()
    }

}
