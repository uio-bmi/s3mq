package no.uio.ifi.localega.s3mq.messages.s3


import com.google.gson.annotations.SerializedName

data class OwnerIdentity(
    @SerializedName("principalId")
    val principalId: String?
)
