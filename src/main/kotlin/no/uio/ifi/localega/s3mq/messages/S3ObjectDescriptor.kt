package no.uio.ifi.localega.s3mq.messages

data class S3ObjectDescriptor(
    val bucket: String,
    val key: String,
    val path: String,
    val time: Long,
    val size: Long
)
