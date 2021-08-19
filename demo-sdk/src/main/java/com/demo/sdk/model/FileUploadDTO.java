package com.demo.sdk.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 文件上传
 */
@Getter
@Setter
@ToString(callSuper = true)
public class FileUploadDTO {

	/**
	 * 文件目录
	 */
	private String dir;

	/**
	 * 文件名
	 */
	private String fileName;
	
}
