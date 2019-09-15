package no.uio.ifi.localega.s3mq.messages.s3


import com.google.gson.annotations.SerializedName

data class S3Message(
    @SerializedName("level")
    val level: String?,
    @SerializedName("msg")
    val msg: String?,
    @SerializedName("Records")
    val records: List<Record?>?,
    @SerializedName("time")
    val time: String?
)
