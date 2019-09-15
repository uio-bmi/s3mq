package no.uio.ifi.localega.s3mq.messages.s3


import com.google.gson.annotations.SerializedName

data class Bucket(
    @SerializedName("arn")
    val arn: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("ownerIdentity")
    val ownerIdentity: OwnerIdentity?
)
