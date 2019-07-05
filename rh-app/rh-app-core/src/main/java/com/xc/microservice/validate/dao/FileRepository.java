package com.xc.microservice.validate.dao;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.xc.microservice.validate.model.entity.TFile;

/**
 * 文件管理--图片上传
 * @author zk
 *
 */
public interface FileRepository extends MongoRepository<TFile, String> {
	
	List<TFile> findByImgId(String imgId);
	
	
}
