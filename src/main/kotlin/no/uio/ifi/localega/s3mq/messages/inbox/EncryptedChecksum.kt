package no.uio.ifi.localega.s3mq.messages.inbox


import com.google.gson.annotations.SerializedName

data class EncryptedChecksum(
    @SerializedName("type")
    val type: String?,
    @SerializedName("value")
    val value: String?
)
