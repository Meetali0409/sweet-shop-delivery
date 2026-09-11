package com.sweetshop.backend.controller

import com.sweetshop.backend.dto.ApiResponse
import com.sweetshop.backend.service.FileStorageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths

@RestController
@RequestMapping("/api/v1/files")
class FileController(
    private val fileStorageService: FileStorageService,
    @Value("\${app.file.upload-dir:./uploads}")
    private val uploadDir: String
) {

    @PostMapping("/upload")
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<ApiResponse<Map<String, String>>> {
        val fileName = fileStorageService.storeFile(file)
        val fileUrl = fileStorageService.getFileUrl(fileName)
        val response = mapOf("fileName" to fileName, "fileUrl" to fileUrl)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse(success = true, data = response, message = "File uploaded successfully")
        )
    }

    @GetMapping("/{fileName}")
    fun getFile(@PathVariable fileName: String): ResponseEntity<Resource> {
        val filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(fileName)
        val resource = UrlResource(filePath.toUri())

        if (!resource.exists()) {
            return ResponseEntity.notFound().build()
        }

        val contentType = try {
            Files.probeContentType(filePath) ?: "application/octet-stream"
        } catch (ex: Exception) {
            "application/octet-stream"
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"${resource.filename}\"")
            .body(resource)
    }
}
