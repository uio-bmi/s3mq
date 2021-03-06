package no.uio.ifi.localega.s3mq.consumers

import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.rabbitmq.client.*
import mu.KotlinLogging
import no.uio.ifi.localega.s3mq.messages.S3ObjectDescriptor
import no.uio.ifi.localega.s3mq.messages.inbox.EncryptedChecksum
import no.uio.ifi.localega.s3mq.messages.inbox.InboxMessage
import no.uio.ifi.localega.s3mq.messages.s3.S3Message
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.codec.digest.MessageDigestAlgorithms
import org.apache.commons.io.FileUtils
import java.io.File
import java.net.URLDecoder
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class S3MQConsumer(
    channel: Channel,
    inboxLocation: String,
    private val exchangeTo: String,
    private val routingKey: String
) : DefaultConsumer(channel) {

    private val log = KotlinLogging.logger {}
    private val gson = Gson()

    private val inboxLocation = if (inboxLocation.endsWith("/")) inboxLocation else "$inboxLocation/"

    override fun handleDelivery(
        consumerTag: String,
        envelope: Envelope,
        properties: AMQP.BasicProperties,
        body: ByteArray
    ) {
        try {
            val s3ObjectDescriptor = parseMessage(body)
            if (s3ObjectDescriptor == null) {
                channel.basicAck(envelope.deliveryTag, false)
                return
            }
            val digest = calculateDigest(s3ObjectDescriptor)
            val encryptedChecksum =
                EncryptedChecksum(
                    MessageDigestAlgorithms.SHA_256.toLowerCase().replace(
                        "-",
                        ""
                    ), digest
                )
            val inboxMessage = InboxMessage(
                encryptedChecksums = listOf(encryptedChecksum),
                fileLastModified = s3ObjectDescriptor.time,
                filepath = s3ObjectDescriptor.key.substringAfter("/"),
                filesize = s3ObjectDescriptor.size,
                operation = "upload", // only uploads are supported by TSD S3 implementation
                user = s3ObjectDescriptor.key.substringBefore("/")
            )
            val inboxMessageJSON = gson.toJson(inboxMessage)
            log.info { "Publishing message: $inboxMessageJSON" }
            channel.basicPublish(
                exchangeTo,
                routingKey,
                MessageProperties.PERSISTENT_TEXT_PLAIN,
                inboxMessageJSON.toByteArray(Charsets.UTF_8)
            )
            channel.basicAck(envelope.deliveryTag, false)
        } catch (t: Throwable) {
            log.error(t) { t.message }
            channel.basicNack(envelope.deliveryTag, false, true)
        }
    }

    private fun parseMessage(byteBody: ByteArray): S3ObjectDescriptor? {
        val stringBody = String(byteBody, Charsets.UTF_8)
        log.info { "Message received: $stringBody" }
        val message = gson.fromJson(stringBody, S3Message::class.java)
        val record =
            message.records?.iterator()?.next() ?: throw JsonIOException("Message from S3 doesn't contain any records!")
        val eventName = record.eventName ?: throw JsonIOException("Message from S3 doesn't contain event name!")
        if (!eventName.startsWith("s3:ObjectCreated:")) {
            log.warn { "Not s3:ObjectCreated event, skipping..." }
            return null
        }
        val time = ZonedDateTime.parse(record.eventTime, DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault()))
            .toOffsetDateTime().toEpochSecond()
        val s3 = record.s3
        val bucketName = s3?.bucket?.name ?: throw JsonIOException("Message from S3 doesn't contain bucket name!")
        val key = URLDecoder.decode(
            s3.objectX?.key ?: throw JsonIOException("Message from S3 doesn't contain object key!"),
            Charsets.UTF_8.displayName()
        )
        val path = "$inboxLocation${bucketName}/${key}"
        val size = s3.objectX.size ?: throw JsonIOException("Message from S3 doesn't contain object size!")
        return S3ObjectDescriptor(bucketName, key, path, time, size)
    }

    private fun calculateDigest(s3ObjectDescriptor: S3ObjectDescriptor): String? {
        return DigestUtils.sha256Hex(FileUtils.openInputStream(File(s3ObjectDescriptor.path)))
    }

}
