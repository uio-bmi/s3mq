package no.uio.ifi.localega.s3mq.messages.s3


import com.google.gson.annotations.SerializedName

data class Object(
    @SerializedName("key")
    val key: String?,
    @SerializedName("sequencer")
    val sequencer: String?,
    @SerializedName("size")
    val size: Long?
)
