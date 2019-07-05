package com.xc.microservice.validate.service;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.WriteResult;
import com.xc.microservice.validate.dao.FileRepository;
import com.xc.microservice.validate.model.entity.TFile;


/**
 * 文件的curd操作
 * @author zk
 *
 */

@Service
public class FileService {
	
	@Autowired
	private FileRepository fileDao;
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	/**
	 * 保存文件
	 * @param file
	 * @return
	 */
	public String save(TFile file){
		fileDao.save(file);
		return file.get_id();
	}
	
	/**
	 * 获取图片
	 * @param id
	 * @return
	 */
	public TFile getFile(String id){
		List<TFile> files = fileDao.findByImgId(id);
		if(files!=null&&files.size()>0){
			return files.get(0);
		}
		return null;
	}
	
	/**
	 * 删除图片、、更新图片需要删除重新添加
	 * @param imgId
	 * @return
	 */
	public boolean delete(String imgId){
		WriteResult res = mongoTemplate.remove(query(where("imgId").is(imgId)), TFile.class);
		if(res.getN() > 0){
			return true;
		}else{
			return false;
		}
	}
	
	
	
}
