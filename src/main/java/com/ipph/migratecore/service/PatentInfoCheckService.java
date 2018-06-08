package com.ipph.migratecore.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipph.migratecore.dao.PatentInfoRepository;
import com.ipph.migratecore.enumeration.LogMessageEnum;
import com.ipph.migratecore.model.PatentInfoCheck;

@Service
public class PatentInfoCheckService {

	@Resource
	private PatentInfoRepository patentInfoRepository;
	@Resource
	private PatentService patentService;
	
	public void check(Long batchLogId) {
		patentService.processFormatExceptionByThread(batchLogId);
		patentService.processNotFoundExceptionByThread(batchLogId);
	}
	
	public List<PatentInfoCheck> getList(Long batchLogId,Pageable page){
		return patentInfoRepository.findByBatchLogId(batchLogId,page);
	}
	
	public List<PatentInfoCheck> getListByBatchLogIdAndTypeAndCostTypeIn(Long batchLogId,LogMessageEnum type,String[] costType,Pageable page){
		return patentInfoRepository.findByBatchLogIdAndTypeAndCostTypeIn(batchLogId,type,costType,page);
	}
	
	@Transactional
	public PatentInfoCheck save(Long oId,LogMessageEnum type,String appNumbers,Long batchLogId,String oAppNumber,String costType) {
		PatentInfoCheck check=new PatentInfoCheck();
		check.setOId(oId);
		if(null==appNumbers) {
			check.setStatus("接口数据未匹配成功");
		}else {
			if(appNumbers.endsWith(",")) {
				appNumbers=appNumbers.substring(0,appNumbers.length()-1);//去掉末尾的逗号
			}
			check.setStatus("确认匹配的申请号是否正确");
		}
		
		check.setAppNumbers(appNumbers);
		check.setType(type);
		check.setBatchLogId(batchLogId);
		check.setOAppNumber(oAppNumber);
		check.setCostType(costType);
		return patentInfoRepository.save(check);
	}
}
