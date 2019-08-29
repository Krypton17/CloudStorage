package com.krypton.cloud.controller;

import com.krypton.cloud.model.Folder;
import com.krypton.cloud.service.folder.FolderServiceImpl;
import com.krypton.cloud.service.folder.record.FolderRecordServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@RestController
@AllArgsConstructor
@RequestMapping("/folder")
public class FolderController {

	private final FolderServiceImpl folderService;

	private final FolderRecordServiceImpl folderRecordService;

	/**
	 * get total and free disk space in GB format
	 *
	 * @return hash map containing disk space info
	 */
	@GetMapping("/root/memory")
	public HashMap rootMemory() {
		return new HashMap<String, String>(){{
			put("total", String.valueOf(new File("/").getTotalSpace() / 1024 / 1024 / 1024));
			put("free", String.valueOf(new File("/").getFreeSpace() / 1024 / 1024 / 1024));
		}};
	}

	/**
	 * get {@link Folder} entity data
	 *
	 * @param id 	folder id
	 * @return {@link Folder}
	 */
	@GetMapping("/{id}/data")
	public Folder getFolderData(@PathVariable Long id) {
		return folderService.getFolderData(id);
	}

	/**
	 * get list of {@link Folder}'s inside a {@link Folder}
	 *
	 * @param id 	parent folder id
	 * @return {@link Folder}'s list
	 */
	@GetMapping("/{id}/folders")
	public Flux<Folder> getFolderFolders(@PathVariable Long id) {
		return folderRecordService.getFolderFolders(id);
	}

	/**
	 * get list of {@link com.krypton.cloud.model.File}'s inside a {@link Folder}
	 *
	 * @param id 	parent folder id
	 * @return {@link com.krypton.cloud.model.File}'s list
	 */
	@GetMapping("/{id}/files")
	public Flux<com.krypton.cloud.model.File> getFolderFiles(@PathVariable Long id) {
		return folderRecordService.getFolderFiles(id);
	}

	/**
	 * get information about inside content of folder
	 *
	 * @param id 	fodler id
	 * @return folder content information like inside files and folders number
	 */
	@GetMapping("/{id}/content_info")
	public HashMap<String, Integer> contentInfo(@PathVariable Long id) {
		return folderService.getItemsCount(id);
	}

	/**
	 * create new folder to specified path
	 *
	 * @param request 	new folder data
	 * @return http status
	 */
	@PostMapping("/create")
	public HttpStatus createFolder(@RequestBody HashMap<String, String> request) {
		return folderService.createFolder(request.get("name"), request.get("folderPath"));
	}

	/**
	 * move folder from one location to another
	 *
	 * @param request 	containing folder old and new path
	 * @return http status
	 */
	@PostMapping("/cut")
	public HttpStatus cutFolder(@RequestBody HashMap<String, String> request) {
		return folderService.cutFolder(request.get("oldPath"), request.get("newPath"));
	}

	/**
	 * copy folder to new path
	 *
	 * @param request 	request containing  original folder path and path for folder copy
	 * @return http status
	 */
	@PostMapping("/copy")
	public HttpStatus copyFolder(@RequestBody HashMap<String, String> request) {
		return folderService.copyFolder(request.get("oldPath"), request.get("newPath"));
	}

	/**
	 * rename folder
	 *
	 * @param request 	request containing folder path and new name
	 * @return http status
	 */
	@PostMapping("/rename")
	public HttpStatus renameFolder(@RequestBody HashMap<String, String> request) {
		return folderService.renameFolder(request.get("path"), request.get("newName"));
	}

	/**
	 * delete folder
	 *
	 * @param request 	request containing folder path
	 * @return http status
	 */
	@PostMapping("/delete")
	public HttpStatus deleteFolder(@RequestBody HashMap<String, String> request) {
		return folderService.deleteFolder(request.get("path"));
	}

	/**
	 *
	 *
	 * @param request 	request containing folder path
	 * @return http status
	 */
	@PostMapping("/delete-all")
	public HttpStatus deleteFolderContent(@RequestBody HashMap<String, String> request) {
		return folderService.deleteFolderContent(request.get("path"));
	}

	/**
	 * zip folder into temporary folder and return path to it
	 *
	 * @param request 	request with folder path
	 * @return path to zipped folder
	 */
	@PostMapping("/zip")
	public String zipFolder(@RequestBody HashMap<String, String> request) {
		return folderService.zipFolder(new File(request.get("path")));
	}
}