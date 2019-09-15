package no.uio.ifi.localega.s3mq.messages.s3


import com.google.gson.annotations.SerializedName

data class S3(
    @SerializedName("bucket")
    val bucket: Bucket?,
    @SerializedName("configurationId")
    val configurationId: String?,
    @SerializedName("object")
    val objectX: Object?,
    @SerializedName("s3SchemaVersion")
    val s3SchemaVersion: String?
)
