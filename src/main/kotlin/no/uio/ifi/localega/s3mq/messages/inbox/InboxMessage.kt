package no.uio.ifi.localega.s3mq.messages.inbox


import com.google.gson.annotations.SerializedName

data class InboxMessage(
    @SerializedName("encrypted_checksums")
    val encryptedChecksums: List<EncryptedChecksum?>?,
    @SerializedName("file_last_modified")
    val fileLastModified: Long?,
    @SerializedName("filepath")
    val filepath: String?,
    @SerializedName("filesize")
    val filesize: Long?,
    @SerializedName("operation")
    val operation: String?,
    @SerializedName("user")
    val user: String?
)
