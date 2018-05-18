package com.ipph.migratecore.xml;

import java.util.ArrayList;
import java.util.List;

import com.ipph.migratecore.enumeration.ApplyTypeEnum;
import com.ipph.migratecore.enumeration.FieldConditionTypeEnum;
import com.ipph.migratecore.enumeration.FieldConstraintEnum;
import com.ipph.migratecore.enumeration.FieldValueTypeEnum;
import com.ipph.migratecore.enumeration.TableOperationEnum;
import com.ipph.migratecore.model.ConditionModel;
import com.ipph.migratecore.model.ConstraintModel;
import com.ipph.migratecore.model.FieldModel;
import com.ipph.migratecore.model.FormatModel;
import com.ipph.migratecore.model.SubtableModel;
import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.model.WhereModel;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 解析时保证标签的顺序是正确解析配置的关键
 * 如table标签中依次是：fields,where,formats和constraints标签
 */
public class TransferTableHandler extends DefaultHandler {
	List<TableModel> tableList=null;
    private TableModel table=null;
    private FieldModel fieldModel=null;
    private FormatModel formatModel=null;
    private ConditionModel conditionModel=null;
    private SubtableModel subTableModel=null;
    private WhereModel whereModel=null;
    private ConstraintModel constraintModel=null;
    private StringBuffer temp=new StringBuffer();
    
    @Override
    public void startDocument () {
        //开始解析文档  
    	tableList=new ArrayList<>();
    }
    @Override
    public void endDocument () {
    }
    
    public List<TableModel> getResult(){
    	return this.tableList;
    }
    
    /**
     * 标签开始前初始化对象
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    	//开始解析节点，设置当前解析节点的标签名称
    	processElement(qName, attributes, false, null);
    };
    /**
     * 获取标签体中的值
     */
    @Override  
    public void characters(char[] ch, int start, int length)throws SAXException {
    	if(length<=0) return;
    	temp.append(new String(ch,start,length));
    }
    /**
     * 标签结束时赋值对象并重置数据
     */
    @Override  
    public void endElement(String uri, String localName, String qName)throws SAXException {
    	String text=temp.toString().replaceAll("\\s*|\t|\r|\n", "");
    	//解析Field内的节点内容
    	processElement(qName, null, true, text);
    	temp.setLength(0);
    }
    /**
     * 处理xml标签
     * @param qName
     * @param attributes
     * @param isEnd
     * @param text
     */
    private void processElement(String qName, Attributes attributes,boolean isEnd,String text){
    	if(XmlElement.table.equals(qName)) {//表
    		processTableElement(attributes,isEnd,text);
        }else if(XmlElement.subTable.equals(qName)){//子表
        	processSubTableElement(attributes,isEnd,text);
        }else if(XmlElement.field.equals(qName)){//字段
        	processFieldElement(attributes,isEnd,text);
        }else if(XmlElement.where.equals(qName)){//where条件
        	processWhereElement(attributes,isEnd,text);
        }else if(XmlElement.format.equals(qName)){//格式化
        	processFormatElement(attributes,isEnd,text);
        }else if(XmlElement.field_condition.equals(qName)){//条件
        	processConditionElement(attributes,isEnd,text);
        }else if(XmlElement.constraint.equals(qName)) {//字段限定
        	processConstraintElement(attributes,isEnd,text);
        }
    }
  
    
    /**
     * 处理table标签
     * @param attributes
     */
    private void processTableElement(Attributes attributes,boolean isEnd,String text){
    	if(!isEnd){
    		table=new TableModel();
    		table.setType(TableOperationEnum.valueOf(attributes.getValue("type")));
    		table.setFrom(attributes.getValue("from"));
    		table.setTo(attributes.getValue("to"));
    		if(null!=attributes.getValue("skip")&&!"".equals(attributes.getValue("skip"))){
    			table.setSkip(Boolean.parseBoolean(attributes.getValue("skip").trim()));
    		}
    		table.setNote(attributes.getValue("note"));
    		table.setFiledList(new ArrayList<FieldModel>());
    		table.setSubTableList(new ArrayList<SubtableModel>());
    		table.setFormatFieldList(new ArrayList<FormatModel>());
    		table.setConstraintList(new ArrayList<ConstraintModel>());
    	}else{
    		if(null!=table){
    			tableList.add(table);
    		}
    	}
    }
    /**
     * 处理subtable标签
     * @param attributes
     */
    private void processSubTableElement(Attributes attributes,boolean isEnd,String text){
    	if(!isEnd){
    		subTableModel=new SubtableModel();
    		if(null!=table){
    			subTableModel.setFrom(table.getFrom());
    		}
    		subTableModel.setTo(attributes.getValue("name"));
    		if(null!=attributes.getValue("skip")&&!"".equals(attributes.getValue("skip"))){
    			subTableModel.setSkip(Boolean.parseBoolean(attributes.getValue("skip").trim()));
    		}
    		subTableModel.setFiledList(new ArrayList<FieldModel>());
    		table.setFormatFieldList(new ArrayList<FormatModel>());
    		table.setConstraintList(new ArrayList<ConstraintModel>());
    	}else{
    		if(null!=subTableModel){
    			if(null!=table){
    				table.getSubTableList().add(subTableModel);
    			}
    		}
    	}
    }
    /**
     * 处理field标签
     * @param attributes
     */
    private void processFieldElement(Attributes attributes,boolean isEnd,String text){
    	if(!isEnd){
    		fieldModel=new FieldModel();
    		fieldModel.setName(attributes.getValue("name"));
    		
    		fieldModel.setValue(null!=attributes.getValue("value")?attributes.getValue("value"):"");
    		
    		fieldModel.setNote(null!=attributes.getValue("note")?attributes.getValue("note"):"");
    		
    		if(null!=attributes.getValue("applyType")&&!"".equals(attributes.getValue("applyType"))){
    			fieldModel.setApplyType(ApplyTypeEnum.valueOf(attributes.getValue("applyType")));
    		}
    		if(null!=attributes.getValue("valueType")&&!"".equals(attributes.getValue("valueType"))){
    			fieldModel.setValueType(FieldValueTypeEnum.valueOf(attributes.getValue("valueType")));
    		}
    		if(null!=attributes.getValue("forLog")&&!"".equals(attributes.getValue("forLog"))){
    			fieldModel.setForLog(Boolean.parseBoolean(attributes.getValue("forLog")));
    		}
    	}else{//field标签可能处于condition标签，subtable标签或table标签内
    		if(null!=fieldModel){
    			if(constraintModel!=null){
    				constraintModel.setField(fieldModel);
    			}
    			else if(conditionModel!=null){
    				conditionModel.setField(fieldModel);
    			}else if(subTableModel!=null){
    				subTableModel.getFiledList().add(fieldModel);
    			}else if(table!=null){
    				table.getFiledList().add(fieldModel);
    			}
    		}
    	}
    }
    /**
     * 处理where标签
     */
    private void processWhereElement(Attributes attributes,boolean isEnd,String text){
    	if(!isEnd){
    		whereModel=new WhereModel();
    		whereModel.setConditionList(new ArrayList<ConditionModel>());
    	}else{//where标签可能存在于table或subtable标签中
    		if(null!=whereModel){
    			if(null!=subTableModel){
    				subTableModel.setWhereModel(whereModel);
    			}else if(null!=table){
    				table.setWhereModel(whereModel);
    			}
    		}
    	}
    }
    /**
     * 处理格式标签
     * @param attributes
     */
    private void processFormatElement(Attributes attributes,boolean isEnd,String text){
    	if(!isEnd){
    		formatModel=new FormatModel();
    		formatModel.setFiledName(null!=attributes.getValue("fieldName")?attributes.getValue("fieldName"):"");
    		try {
    			formatModel.setClazz(null!=attributes.getValue("clazz")?Class.forName(attributes.getValue("clazz")):null);
    		} catch (ClassNotFoundException e) {
    			formatModel.setClazz(null);
    		}
    	}else{//format标签可能存在于table或subtable标签中
    		if(null!=formatModel){
    			formatModel.setFormatParameter(text);
    			
    			if(null!=subTableModel){
    				subTableModel.getFormatFieldList().add(formatModel);
    			}else if(null!=table){
    				table.getFormatFieldList().add(formatModel);
    			}
    		}
    	}
    }
    /**
     * 处理条件标签
     * @param attributes
     */
    private void processConditionElement(Attributes attributes,boolean isEnd,String text){
    	if(!isEnd){
    		conditionModel=new ConditionModel();
    		if(null!=attributes.getValue("type")&&!"".equals(attributes.getValue("type"))){
    			conditionModel.setType(FieldConditionTypeEnum.valueOf(attributes.getValue("type")));
    		}
    		if(null!=attributes.getValue("applyType")&&!"".equals(attributes.getValue("applyType"))){
    			conditionModel.setApplyType(ApplyTypeEnum.valueOf(attributes.getValue("applyType")));
    		}
    	}else{//condition标签处于where标签内
    		if(null!=whereModel){
    			whereModel.getConditionList().add(conditionModel);
    		}
    	}
    	
    }
    
    private void processConstraintElement(Attributes attributes,boolean isEnd,String text) {
    	if(!isEnd){
    		constraintModel=new ConstraintModel();
    		if(null!=attributes.getValue("type")&&!"".equals(attributes.getValue("type"))){
    			constraintModel.setType(FieldConstraintEnum.valueOf(attributes.getValue("type")));
    		}
    		if(null!=attributes.getValue("applyType")&&!"".equals(attributes.getValue("applyType"))){
    			constraintModel.setApplyType(ApplyTypeEnum.valueOf(attributes.getValue("applyType")));
    		}
    	}else{//constraint标签处于table或subtable标签内
    		if(null!=constraintModel){
    			if(null!=subTableModel){
    				subTableModel.getConstraintList().add(constraintModel);
    			}else if(null!=table){
    				table.getConstraintList().add(constraintModel);
    			}
    		}
    	}
    }
    
}