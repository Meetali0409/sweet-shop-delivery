package com.sweetshop.backend.service

import com.sweetshop.backend.exception.BadRequestException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.UUID

interface FileStorageService {
    fun storeFile(file: MultipartFile): String
    fun getFileUrl(fileName: String): String
    fun deleteFile(fileName: String)
}

@Service
@ConditionalOnProperty(name = ["app.storage.type"], havingValue = "local", matchIfMissing = true)
class LocalFileStorageService(
    @Value("\${app.storage.local-path:./uploads}")
    private val uploadDir: String,
    @Value("\${app.file.allowed-types:image/jpeg,image/png,image/webp,image/gif}")
    private val allowedTypes: String,
    @Value("\${app.file.max-file-size-mb:10}")
    private val maxFileSizeMb: Long
) : FileStorageService {

    private val logger = LoggerFactory.getLogger(LocalFileStorageService::class.java)
    private val fileStoragePath: Path = Paths.get(uploadDir).toAbsolutePath().normalize()
    private val allowedMimeTypes by lazy { allowedTypes.split(",").map { it.trim().lowercase() }.toSet() }
    private val allowedExtensions = setOf("jpg", "jpeg", "png", "webp", "gif")

    init {
        try {
            Files.createDirectories(fileStoragePath)
        } catch (ex: IOException) {
            throw RuntimeException("Could not create the directory for file uploads: $uploadDir", ex)
        }
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

        val originalFileName = file.originalFilename
            ?: throw BadRequestException("File name is missing")

        if (originalFileName.contains("..")) {
            throw BadRequestException("Invalid file path")
        }

        val fileExtension = originalFileName.substringAfterLast(".", "").lowercase()
        val uniqueFileName = "${UUID.randomUUID()}.$fileExtension"

        try {
            val targetLocation = fileStoragePath.resolve(uniqueFileName)
            Files.copy(file.inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)
            logger.info("File stored: {}", uniqueFileName)
            return uniqueFileName
        } catch (ex: IOException) {
            throw RuntimeException("Could not store file $uniqueFileName", ex)
        }
    }

    override fun getFileUrl(fileName: String): String {
        return "/api/v1/files/$fileName"
    }

    override fun deleteFile(fileName: String) {
        try {
            val filePath = fileStoragePath.resolve(fileName).normalize()
            if (!filePath.startsWith(fileStoragePath)) {
                logger.warn("Path traversal attempt detected: {}", fileName)
                return
            }
            Files.deleteIfExists(filePath)
            logger.info("File deleted: {}", fileName)
        } catch (ex: IOException) {
            logger.error("Could not delete file: {}", fileName, ex)
        }
    }
}
