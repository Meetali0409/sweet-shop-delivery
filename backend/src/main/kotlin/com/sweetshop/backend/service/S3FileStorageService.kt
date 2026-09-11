package com.sweetshop.backend.service

import com.sweetshop.backend.exception.BadRequestException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.net.URI
import java.util.UUID

@Service
@ConditionalOnProperty(name = ["app.storage.type"], havingValue = "s3")
class S3FileStorageService(
    @Value("\${app.storage.s3-bucket}")
    private val bucket: String,
    @Value("\${app.storage.s3-access-key}")
    private val accessKey: String,
    @Value("\${app.storage.s3-secret-key}")
    private val secretKey: String,
    @Value("\${app.storage.s3-endpoint:}")
    private val endpoint: String,
    @Value("\${app.storage.s3-region:auto}")
    private val region: String,
    @Value("\${app.storage.base-url:}")
    private val baseUrl: String,
    @Value("\${app.file.allowed-types:image/jpeg,image/png,image/webp,image/gif}")
    private val allowedTypes: String,
    @Value("\${app.file.max-file-size-mb:10}")
    private val maxFileSizeMb: Long
) : FileStorageService {

    private val logger = LoggerFactory.getLogger(S3FileStorageService::class.java)
    private val allowedMimeTypes by lazy { allowedTypes.split(",").map { it.trim().lowercase() }.toSet() }
    private val allowedExtensions = setOf("jpg", "jpeg", "png", "webp", "gif")

    private val s3Client: S3Client by lazy {
        val builder = S3Client.builder()
            .credentialsProvider(
                StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
            )
            .region(Region.of(region))

        if (endpoint.isNotBlank()) {
            builder.endpointOverride(URI.create(endpoint))
            builder.forcePathStyle(true)
        }

        builder.build()
    }

    private fun validateFile(file: MultipartFile) {
        if (file.isEmpty) {
            throw BadRequestException("File is empty")
        }
        if (file.size > maxFileSizeMb * 1024 * 1024) {
            throw BadRequestException("File size exceeds maximum of ${maxFileSizeMb}MB")
        }
        val contentType = file.contentType?.lowercase()
        if (contentType == null || contentType !in allowedMimeTypes) {
            throw BadRequestException("File type not allowed. Accepted types: ${allowedMimeTypes.joinToString(", ")}")
        }
        val extension = file.originalFilename?.substringAfterLast(".", "")?.lowercase() ?: ""
        if (extension !in allowedExtensions) {
            throw BadRequestException("File extension not allowed. Accepted: ${allowedExtensions.joinToString(", ")}")
        }
    }

    override fun storeFile(file: MultipartFile): String {
        validateFile(file)

        val extension = file.originalFilename?.substringAfterLast(".", "jpg")?.lowercase() ?: "jpg"
        val key = "${UUID.randomUUID()}.$extension"

        val putRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(file.contentType)
            .build()

        s3Client.putObject(putRequest, RequestBody.fromInputStream(file.inputStream, file.size))
        logger.info("File uploaded to S3: {}", key)
        return key
    }

    override fun getFileUrl(fileName: String): String {
        return if (baseUrl.isNotBlank()) {
            "${baseUrl.trimEnd('/')}/$fileName"
        } else {
            "https://$bucket.s3.amazonaws.com/$fileName"
        }
    }

    override fun deleteFile(fileName: String) {
        try {
            val deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build()
            s3Client.deleteObject(deleteRequest)
            logger.info("File deleted from S3: {}", fileName)
        } catch (ex: Exception) {
            logger.error("Could not delete file from S3: {}", fileName, ex)
        }
    }
}
