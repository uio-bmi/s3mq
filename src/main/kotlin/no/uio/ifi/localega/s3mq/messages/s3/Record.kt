package no.uio.ifi.localega.s3mq.messages.s3


import com.google.gson.annotations.SerializedName

data class Record(
    @SerializedName("awsRegion")
    val awsRegion: String?,
    @SerializedName("eventName")
    val eventName: String?,
    @SerializedName("eventSource")
    val eventSource: String?,
    @SerializedName("eventTime")
    val eventTime: String?,
    @SerializedName("eventVersion")
    val eventVersion: String?,
    @SerializedName("requestParameters")
    val requestParameters: RequestParameters?,
    @SerializedName("responseElements")
    val responseElements: ResponseElements?,
    @SerializedName("s3")
    val s3: S3?,
    @SerializedName("userIdentity")
    val userIdentity: UserIdentity?
)
