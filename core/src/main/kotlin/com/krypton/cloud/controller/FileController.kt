package com.krypton.cloud.controller

import com.krypton.cloud.model.File
import lombok.AllArgsConstructor
import com.krypton.cloud.service.file.FileServiceImpl
import com.krypton.cloud.service.file.record.FileRecordServiceImpl
import com.krypton.cloud.service.trash.TrashService
import org.springframework.http.*
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.FormFieldPart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@AllArgsConstructor
@RestController
@RequestMapping("/file")
class FileController(
        private val fileService : FileServiceImpl,
        private val fileRecordService : FileRecordServiceImpl,
        private val trashService: TrashService<File>
) {

    /**
     * upload one file to specified folder path
     *
     * @param filePart  file for save
     * @param path      path to folder where to save file
     * @return http status
     */
    @PostMapping("/upload/one")
    fun uploadFile(
            @RequestPart("file") filePart : Mono<FilePart>,
            @RequestPart("path") path : FormFieldPart
    ) : Mono<HttpStatus> = filePart.flatMap { fileService.saveFile(it, path.value()) }

    @GetMapping("/{id}/data")
    fun getData(@PathVariable id : String) : File = fileRecordService.getById(UUID.fromString(id))

    /**
     * move folder from one location to another
     *
     * @param request   containing folder old and new path
     * @return http status
     */
    @PostMapping("/move")
    fun moveFile(@RequestBody request : HashMap<String, String>) : HttpStatus = fileService.move(request["oldPath"]!!, request["newPath"]!!)

    /**
     * copy folder to new path
     *
     * @param request   request containing  original folder path and path for folder copy
     * @return http status
     */
    @PostMapping("/copy")
    fun copyFile(@RequestBody request : HashMap<String, String>) : HttpStatus = fileService.copy(request["oldPath"]!!, request["newPath"]!!)

    /**
     * @param request   file path and new name
     * @return http status
     */
    @PostMapping("/rename")
    fun renameFile(@RequestBody request : HashMap<String, String>) : HttpStatus = fileService.rename(request["path"]!!, request["newName"]!!)

    /**
     * @param request   file path
     * @return http status
     */
    @PostMapping("/delete")
    fun deleteFile(@RequestBody request : HashMap<String, String>) : HttpStatus = fileService.delete(request["path"]!!)

    @PostMapping("/move-to-trash")
    fun moveToTrash(@RequestBody request : HashMap<String, String>) : HttpStatus {
        val file = fileRecordService.getById(UUID.fromString(request["id"]))

        return if (trashService.moveToTrash(file)) {
            HttpStatus.OK
        } else HttpStatus.INTERNAL_SERVER_ERROR
    }

    @PostMapping("/delete-from-trash")
    fun deleteFromTrash(@RequestBody request : HashMap<String, String>) : HttpStatus {
        return if (trashService.deleteFromTrash(UUID.fromString(request["id"]))) {
            HttpStatus.OK
        } else HttpStatus.INTERNAL_SERVER_ERROR
    }

    @PostMapping("/restore-from-trash")
	fun restoreFromTrash(@RequestBody request : HashMap<String, String>) : HttpStatus {
		return if (trashService.restoreFromTrash(UUID.fromString(request["id"]))) {
			HttpStatus.OK
		} else {
			HttpStatus.INTERNAL_SERVER_ERROR
		}
	}
}
