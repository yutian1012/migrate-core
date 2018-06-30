package com.ipph.migratecore.transformer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ipph.migratecore.enumeration.ApplyTypeEnum;
import com.ipph.migratecore.enumeration.FieldValueTypeEnum;
import com.ipph.migratecore.model.FieldModel;
import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.model.view.FieldViewModel;
import com.ipph.migratecore.model.view.TableViewModel;

@Component
public class TableTransformer {
	
	public TableModel transform(TableViewModel tableViewModel) {
		
		TableModel table=new TableModel();
		//BeanUtils.copyProperties(tableViewModel,table );
		table.setFrom(tableViewModel.getFrom());
		table.setTo(tableViewModel.getTo());
		table.setNote(tableViewModel.getNote());
		table.setType(tableViewModel.getType());
		//table.setMain(main);
		table.setSkip(tableViewModel.isSkip());
		//table.setSourcePkName(sourcePkName);
		
		//解析字段设置
		transformField(tableViewModel, table);
		return table;
	}
	
	/**
	 * 解析数据显示到页面上
	 * @param tableModel
	 * @return
	 */
	public TableViewModel parseTable(TableModel tableModel) {
		TableViewModel tableViewModel=new TableViewModel();
		
		tableViewModel.setId(tableModel.getId());
		tableViewModel.setFrom(tableModel.getFrom());
		tableViewModel.setTo(tableModel.getTo());
		tableViewModel.setNote(tableModel.getNote());
		tableViewModel.setType(tableModel.getType());
		tableViewModel.setSkip(tableModel.isSkip());
		
		//处理字段设置
		parseField(tableModel,tableViewModel);
		
		return tableViewModel;
	}
	
	/**
	 * 转换字段设置集合
	 * @param tableViewModel
	 * @param table
	 */
	public void transformField(TableViewModel tableViewModel,TableModel table) {
		if(null!=tableViewModel.getFieldList()) {
			List<FieldModel> fieldList=new ArrayList<>(tableViewModel.getFieldList().size());
			Iterator<FieldViewModel> iter=tableViewModel.getFieldList().iterator();
			while(iter.hasNext()) {
				FieldViewModel fieldViewModel=iter.next();
				FieldModel fieldModel=new FieldModel();
				
				fieldModel.setName(fieldViewModel.getFieldName());
				fieldModel.setValueType(fieldViewModel.getValueType());
				switch (fieldViewModel.getValueType()) {
				case FIELD:
					fieldModel.setValue(fieldViewModel.getFieldValue());
					break;
				default:
					fieldModel.setValue(fieldViewModel.getTextValue());
					break;
				}
				fieldModel.setApplyType(ApplyTypeEnum.TARGET);//默认值为target
				fieldModel.setForLog(fieldViewModel.isForLog());
				fieldModel.setNote(fieldViewModel.getNote());
				fieldList.add(fieldModel);
			}
			if(fieldList.size()>0) {
				table.setFieldList(fieldList);
			}
			
			//条件source字段信息
			List<FieldModel> sourceFieldList=new ArrayList<>(table.getFieldList().size());
			for(FieldModel fieldModel:table.getFieldList()) {
				if(null!=fieldModel&&FieldValueTypeEnum.FIELD==fieldModel.getValueType()) {
					FieldModel sourceFieldModel=new FieldModel();
					sourceFieldModel.setName(fieldModel.getValue());
					sourceFieldModel.setApplyType(ApplyTypeEnum.SOURCE);
					sourceFieldList.add(sourceFieldModel);
				}
			}
			
			if(sourceFieldList.size()>0) {
				table.getFieldList().addAll(sourceFieldList);
			}
		}
	}
	/**
	 * 解析数据到tableViewModel中
	 * @param tableModel
	 * @param tableViewModel
	 */
	public void parseField(TableModel tableModel,TableViewModel tableViewModel) {
		if(null!=tableModel.getFieldList()) {
			List<FieldViewModel> fieldList=new ArrayList<>(tableModel.getFieldList().size());
			Iterator<FieldModel> iter=tableModel.getFieldList().iterator();
			while(iter.hasNext()) {
				FieldModel fieldModel=iter.next();
				FieldViewModel fieldViewModel=new FieldViewModel();
				fieldViewModel.setFieldName(fieldModel.getName());
				fieldViewModel.setValueType(fieldModel.getValueType());
				switch (fieldModel.getValueType()) {
				case FIELD:
					fieldViewModel.setFieldValue(fieldModel.getValue());
					fieldViewModel.setShowSelect(true);
					break;

				default:
					fieldViewModel.setTextValue(fieldModel.getValue());
					fieldViewModel.setShowText(true);
					break;
				}
			}
		}
	}
	
	public void transFormFormatField(TableViewModel tableViewModel,TableModel table) {
		
	}
	
	public void transFormConditionField(TableViewModel tableViewModel,TableModel table) {
		
	}
}
