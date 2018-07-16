package com.ipph.migratecore.quartz;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ipph.migratecore.enumeration.BatchStatusEnum;
import com.ipph.migratecore.model.BatchLogModel;
import com.ipph.migratecore.service.BatchLogService;
import com.ipph.migratecore.service.LogService;

/**
 * 该定时器用于更新数据执行进度
 */
@Component
@EnableScheduling
public class BatchLogProcessingTask {
	
	private Logger logger=LoggerFactory.getLogger(BatchLogProcessingTask.class);
	
	@Resource
	private BatchLogService batchLogService;
	@Resource
	private LogService logService;
	
	//延迟3秒执行，指方法执行完成后过3秒再次执行
	@Scheduled(fixedDelay=5*1000)
	public void execute() {
		
		List<BatchLogModel> batchLogList=batchLogService.getList(true);//获取正在执行的批次
		
		if(null!=batchLogList&&batchLogList.size()>0) {
			for(BatchLogModel batchLog:batchLogList) {
				updateBatchLogSize(batchLog);
			}
		}
	}
	/**
	 * 更新批次执行数量
	 * @param batchLog
	 */
	private void updateBatchLogSize(BatchLogModel batchLog) {
		
		logger.info("processing "+batchLog.getBatchName()+"....");
		
		if(null==batchLog||null==batchLog.getTotal()) {
			return;
		}
		
		long size=0L;
		long num=0L;
		long dbSize=0L;
		
		//更新size字段
		if(null==batchLog.getSize()||batchLog.getTotal().longValue()!=batchLog.getSize().longValue()) {
			
			size=logService.countByBatchLogId(batchLog.getId());
			
			num=null==batchLog.getNum()?0L:batchLog.getNum().longValue();
			
			dbSize=null==batchLog.getSize()?0L:batchLog.getSize().longValue();
			
			if(dbSize==size) {
				if(num>10) {
					batchLogService.updateStatus(batchLog.getId(), BatchStatusEnum.FAIL);
					return;
				}else {
					batchLogService.updateSize(batchLog.getId(), size,++num);
					return;
				}
			}
			
			batchLogService.updateSize(batchLog.getId(), size,0);
			return;
		}
		
		
		batchLogService.updateStatus(batchLog.getId(), BatchStatusEnum.SUCCESS);
		
	}
}
