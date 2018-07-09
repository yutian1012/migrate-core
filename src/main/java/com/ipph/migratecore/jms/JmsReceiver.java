package com.ipph.migratecore.jms;

import java.sql.SQLException;

import javax.annotation.Resource;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.ipph.migratecore.config.JmsConfirguration;
import com.ipph.migratecore.deal.exception.ConfigException;
import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.service.TableService;

@Component
public class JmsReceiver {
	@Resource
	private TableService tableService;
	/**
	 * @param messageModel
	 */
    @JmsListener(destination =JmsConfirguration.QUEUE_MESSAGEMODEL)
    public void receiveMessageModelByQueue(MigrateMessageModel messageModel) {
    	if(null!=messageModel) {
    		try {
				handleMessagModel(messageModel);
			} catch (ConfigException | SQLException e) {
				e.printStackTrace();
			}
    	}
    }
    
    private void handleMessagModel(MigrateMessageModel messageModel) throws ConfigException, SQLException {
    	if(null!=messageModel.getTableId()) {
    		TableModel table=tableService.getById(messageModel.getTableId());
    		tableService.migrateTable(table,messageModel.getBatchLogId(),messageModel.getParentId());
    	}
    }
    
}
