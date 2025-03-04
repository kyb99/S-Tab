package com.sixb.stab.s3.api.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class S3Service {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public String getPresignedUrl(long userId, String mimeType, String type, String filename) {
		return amazonS3.generatePresignedUrl(getGeneratePresignedUrlRequest(bucket, mimeType, createPath(userId, type, filename))).toString();
	}

	private GeneratePresignedUrlRequest getGeneratePresignedUrlRequest(String bucket, String mimeType, String filename) {
		GeneratePresignedUrlRequest generatePresignedUrlRequest =
				new GeneratePresignedUrlRequest(bucket, filename)
						.withMethod(HttpMethod.PUT)
						.withExpiration(getPresignedUrlExpiration());

		generatePresignedUrlRequest.addRequestParameter("Content-type", mimeType);

		return generatePresignedUrlRequest;
	}

	private Date getPresignedUrlExpiration() {
		return new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
	}

	private String createFileId() {
		return UUID.randomUUID().toString();
	}

	private String createPath(long userId, String type, String filename) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		String now = format.format(new Date());
		return type + "/" + now + "/" + userId + "/" + createFileId() + "_" + filename;
	}

}
