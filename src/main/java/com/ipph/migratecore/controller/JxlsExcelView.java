package com.ipph.migratecore.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.springframework.web.servlet.view.AbstractView;

public class JxlsExcelView extends AbstractView{
	
	private static final String CONTENT_TYPE = "application/vnd.ms-excel";

    private String templatePath;
    private String exportFileName;

    /**
     * @param templatePath   模版相对于当前classpath路径
     * @param exportFileName 导出文件名
     */
    public JxlsExcelView(String templatePath, String exportFileName) {
        this.templatePath = templatePath;
        this.exportFileName = exportFileName;
        //设置文档内容
        setContentType(CONTENT_TYPE);
    }
    /**
     * 输出文档
     * @throws Exception 
     */
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Context context = new Context(model);
		OutputStream os=getOutputStream(request,response,exportFileName);
        //try-with resource方式处理流资源
        try( InputStream is = this.getClass().getResourceAsStream(templatePath)){
        	//到处excel文档
    		JxlsHelper.getInstance().processTemplate(is, os, context);
    	}
	}
	
	/**
	 * 获取输出流
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public OutputStream getOutputStream(HttpServletRequest request,HttpServletResponse response,String filename) throws Exception {
		response.setCharacterEncoding("utf-8");
		response.setContentType(getContentType());
        response.setHeader("content-disposition","attachment;filename=" + getFileName(request.getHeader("User-Agent").toUpperCase(), filename));
        return response.getOutputStream();
	}
	
	/**
	 * 获取文件名
	 * @param browser
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static String getFileName(String browser,String fileName)throws Exception{
        if(browser.indexOf("MSIE")!=-1){//IE浏览器
            fileName = new String((fileName+".xls").getBytes("gb2312"), "ISO8859-1");
        }else if(browser.indexOf("FIREFOX")!=-1){//google,火狐浏览器，会自动添加文件扩展名。
            fileName = "=?UTF-8?B?" + (new String (Base64.getEncoder().encode((fileName+".xls").getBytes("UTF-8")))) + "?=";  //火狐文件名空格被截断问题
        }else{
            fileName = new String((fileName+".xls").getBytes("gb2312"), "ISO8859-1");
        }
        return fileName;
	}
	
}
