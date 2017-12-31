
package com.nms.model.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.statistics.xmlanalysis.ReadTableAttrsXml;

/**
 * 将页面信息导出成Excel文件
 * @author sy
 *
 */
public class ExportExcel {
	
	/**
	 * 根据表名，路径读取Xml文件
	 * @param tableName
	 * 	  表头名称
	 * @param path
	 *   Xml文件路径
	 *     
	 *     注意：传人的path路径区分中英文
	 *     
	 * @return headList  
	 *      表头信息
	 */
	private List<String> ReadXml(String tableName){
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document document = null;
		Element root = null;
		List<String> headList=null;
		try {
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			String path = "config/tableAttrs.xml";
			if ("en_US".equals(ResourceUtil.language)) {
				path = "config/tableAttrs_en.xml";
			}
			document = builder.parse(ExportExcel.class.getClassLoader().getResource(path).toString());
			root = document.getDocumentElement();
			NodeList nodeList = root.getChildNodes();
			headList=new ArrayList<String>();
			if(nodeList!=null&&nodeList.getLength()>0){
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					// 在XML文件中找到  表头为  tableName的 节点
					if (node.getNodeType() == Node.ELEMENT_NODE && node.getAttributes().getNamedItem("name").getNodeValue().equals(tableName)) {
						NodeList childNode = node.getChildNodes(); //attribute
						for (int j = 0; j < childNode.getLength(); j++) {
							//取出 表头  节点  下  的各个属性（既表头的子节点）
							if (childNode.item(j).getNodeType() == Node.ELEMENT_NODE && childNode.item(j).getNodeName().equals("attribute")) {
								Node child = childNode.item(j);  //attribute
								//  判断：   取出 XML文件中  ，表头的 属性设置为true的  列明   ,并且  ID（序列号除外）
								if(child.getAttributes().getNamedItem("name").getNodeValue().equals("ID")||child.getAttributes().getNamedItem("name").getNodeValue().equals(ResourceUtil.srcStr(StringKeysObj.ORDER_NUM))){
									headList.add(ResourceUtil.srcStr(StringKeysObj.ORDER_NUM));
								}
								if(child.getAttributes().getNamedItem("visible").getNodeValue().equals("true")){
									String displayName = child.getAttributes().getNamedItem("name").getNodeValue();
									headList.add(displayName);
								}																
							}
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,ReadTableAttrsXml.class);
		} finally {
			factory = null;
			builder = null;
			document = null;
			root = null;
		}
		return headList;
	}
	/**
	 * 导出 Excel文件
	 * @param list
	 *    String []（List<bean>转化来的）List集合
	 * @param name
	 *      表头名称
	 * @param path
	 *     路径
	 * @return   操作日志的结果
	 *     null  成功
	 *       fail  失败
	 */
	public String exportExcel(List<String[]> list,String name)throws Exception{
		JFileChooser fileChooser=null;
		File file=null;
		FileOutputStream fileOut=null;
		String result="fail";
		Workbook workbook=null;
		UiUtil uiUtil = new UiUtil();
		int reult = 0;
		List<String> headName = null;;//表头
		try{
			fileChooser=new JFileChooser();
			FileNameExtensionFilter filter=new FileNameExtensionFilter("Excel", "xls");
			fileChooser.setFileFilter(filter);
			fileChooser.showSaveDialog(null);
			file = fileChooser.getSelectedFile();
			if(file!=null){
				String filepath = file.toString();
				if(!file.exists()){
					filepath=file.getAbsoluteFile()+".xls";
				}
				
				//是 0: 否 1 
				if(uiUtil.isExistAlikeFileName(filepath)){
				  reult = DialogBoxUtil.confirmDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_FILEPATHHASEXIT));
				  if(reult == 1){
 					  return "fail";
				  }
				}
				fileOut=new FileOutputStream(filepath);
//				if(fileOut.getChannel().tryLock()==null){
//					System.out.println("文件已经被打开");
				
				headName = ReadXml(name);
				
				workbook= exportData(list, headName);
				if(workbook!=null){
					workbook.write(fileOut);
					result=null;//导出成功
				}
			}else{
				//选择路径为空
//				DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_FILEPATH));
			}	
			
		}catch(Exception e){
			DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_FILE_USERED));
		}finally{
			if(result!=null){
				//导出失败
				//DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysBtn.BTN_EXPORT)+ResourceUtil.srcStr(StringKeysBtn.BTN_EXPORT_FALSE));
			}else{
				//导出成功
				DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysBtn.BTN_EXPORT)+ResourceUtil.srcStr(StringKeysBtn.BTN_EXPORT_ISUCCESS));
			}
			 fileChooser=null;
			 file=null;
			 fileOut.close();
			 fileOut = null;
			 workbook=null;
		}
		return result;
	}
	
	public String exportExcel(String filePath, List<String[]> list,String name)throws Exception{
		File file=null;
		FileOutputStream fileOut=null;
		String result="fail";
		Workbook workbook=null;
		List<String> headName = null;;//表头
		try{
			file = new File(filePath);
			if(file != null){
				String filepath = file.toString();
				if(!file.exists()){
					filepath=file.getAbsoluteFile()+".xls";
				}
				fileOut=new FileOutputStream(filepath);
				headName = ReadXml(name);
				workbook= exportData(list, headName);
				if(workbook!=null){
					workbook.write(fileOut);
					result=null;//导出成功
				}
			}	
			
		}catch(Exception e){
		}finally{
			 file=null;
			 fileOut.close();
			 fileOut = null;
			 workbook=null;
		}
		return result;
	}
	
	public String exportExcel(List<String[]> list, String name, String fileType)throws Exception{
		JFileChooser fileChooser=null;
		File file=null;
		FileOutputStream fileOut=null;
		String result="fail";
		HSSFWorkbook workbook=null;
		UiUtil uiUtil = new UiUtil();
		int reult = 0;
		List<String> headName = null;;//表头
		try{
			fileChooser=new JFileChooser();
			FileNameExtensionFilter filter=new FileNameExtensionFilter(fileType, "Excel".equals(fileType) ? "xls":"txt");
			fileChooser.setFileFilter(filter);
			fileChooser.showSaveDialog(null);
			file = fileChooser.getSelectedFile();
			if(file!=null){
				String filepath = file.toString();
				if(!file.exists()){
					filepath = file.getAbsoluteFile()+"."+("Excel".equals(fileType) ? "xls":"txt");
				}
				
				//是 0: 否 1 
				if(uiUtil.isExistAlikeFileName(filepath)){
				  reult = DialogBoxUtil.confirmDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_FILEPATHHASEXIT));
				  if(reult == 1){
 					  return "fail";
				  }
				}
				fileOut = new FileOutputStream(filepath);
//				if(fileOut.getChannel().tryLock()==null){
//					System.out.println("文件已经被打开");
				
				headName = ReadXml(name);
				
				workbook = exportData(list, headName);
				if(workbook != null){
					if("Excel".equals(fileType)){
						workbook.write(fileOut);
					}else{
						convertXlsToTxt(null, filepath, workbook);
					}
					result = null;//导出成功
				}
			}else{
				//选择路径为空
//				DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_FILEPATH));
			}	
			
		}catch(Exception e){
			DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_FILE_USERED));
		}finally{
			if(result!=null){
				//导出失败
				//DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysBtn.BTN_EXPORT)+ResourceUtil.srcStr(StringKeysBtn.BTN_EXPORT_FALSE));
			}else{
				//导出成功
				DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysBtn.BTN_EXPORT)+ResourceUtil.srcStr(StringKeysBtn.BTN_EXPORT_ISUCCESS));
			}
			 fileChooser=null;
			 file=null;
			 fileOut.close();
			 fileOut = null;
			 workbook=null;
		}
		return result;
	}
	
	/**
	 * 
	 * 根据表名和数据导出
	 * @param list 表内所有数据
	 * @param headName 表头
	 * @return
	 * @throws Exception
	 */
	public String exportExcel(List<String[]> list,List<String> headName)throws Exception{
		JFileChooser fileChooser=null;
		File file=null;
		FileOutputStream fileOut=null;
		String result="fail";
		Workbook workbook=null;
		UiUtil uiUtil = new UiUtil();
		int reult = 0;
		try{
			fileChooser=new JFileChooser();
			FileNameExtensionFilter filter=new FileNameExtensionFilter("Excel", "xls");
			fileChooser.setFileFilter(filter);
			fileChooser.showSaveDialog(null);
			file = fileChooser.getSelectedFile();
			if(file!=null){
				String filepath = file.toString();
				if(!file.exists()){
					filepath=file.getAbsoluteFile()+".xls";
				}
				
				//是 0: 否 1 
				if(uiUtil.isExistAlikeFileName(filepath)){
				  reult = DialogBoxUtil.confirmDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_FILEPATHHASEXIT));
				  if(reult == 1){
 					  return "fail";
				  }
				}
				fileOut = new FileOutputStream(filepath);
				
				workbook= exportData(list, headName);
				
				if(workbook != null){
					workbook.write(fileOut);
					result = null;//导出成功
				}
			}else{
				//选择路径为空
//				DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_FILEPATH));
			}	
			
		}catch(Exception e){
			DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_FILE_USERED));
		}finally{
			if(result!=null){
				//导出失败
				//DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysBtn.BTN_EXPORT)+ResourceUtil.srcStr(StringKeysBtn.BTN_EXPORT_FALSE));
			}else{
				//导出成功
				DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysBtn.BTN_EXPORT)+ResourceUtil.srcStr(StringKeysBtn.BTN_EXPORT_ISUCCESS));
			}
			 fileChooser=null;
			 file=null;
			 fileOut=null;
			 workbook=null;
		}
		return result;
	}
	
	private void convertXlsToTxt(String readinFile, String outputFile, HSSFWorkbook readWorkbook) {
		  int i = 0;
		  int j = 0;
		  FileWriter fw = null;
		  try {
		   //源文件
//		   HSSFWorkbook readWorkbook = new HSSFWorkbook(new FileInputStream(readinFile));
		   HSSFSheet sourceSheet = readWorkbook.getSheetAt(0);
		   HSSFRow sourceRow = null;
		   HSSFCell sourceCell = null;
		   fw = new FileWriter(outputFile);
		   //读取行和列
		   for (i = 0; i <= sourceSheet.getLastRowNum(); i++) {
		    String content = "";
		    sourceRow = sourceSheet.getRow(i);
		    for (j = 0; j < sourceRow.getLastCellNum(); j++) {
		     sourceCell = sourceRow.getCell(j);
		     if (j == sourceRow.getLastCellNum()-1) {  //若是最后一列
		    	if(sourceCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
		    		content += sourceCell.toString().split("\\.")[0]+" ";
		     	}else{
		     		content += sourceCell.getRichStringCellValue();
		     	}
		     } else {
		    	if(sourceCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
		    		content += sourceCell.toString().split("\\.")[0]+" ";
		     	}else{
		     		content += sourceCell.getRichStringCellValue() + ",  ";
		     	}
		     }     
		    }
		    content += "\r\n";  //换行
		    fw.write(content);  //写入文件
		    fw.flush();
		   }
		   if(fw != null) {  //关闭文件流
		    fw.close();
		   }
		  } catch (Exception e) {
			  ExceptionManage.dispose(e, this.getClass());
		  }
		 }
	
	/**
	 * 将 页面数据转为 Excel 表中
	 * @param list
	 * @return
	 * @throws Exception
	 */
	private HSSFWorkbook exportData(List<String[]> list,List<String> headName) throws Exception{
		HSSFWorkbook workbook = null;
		HSSFSheet sheet_1 = null;
		HSSFRow row_0 = null;
//		List<String> headName
		try{
			workbook = new HSSFWorkbook();
			sheet_1 = workbook.createSheet();
			row_0 = sheet_1.createRow(0);
//			headName = ReadXml(name);
			if(headName != null && headName.size()>0){
				for(int i=0;i<headName.size();i++){
					//表头 信息
					row_0.createCell(i).setCellValue(headName.get(i).toString());
				}
				if(list != null && list.size()>0 ){		
					//循环，有多少组数据
					for(int i = 0; i<list.size(); i++){
						HSSFRow row = sheet_1.createRow(i+1);
						row.createCell(0).setCellValue(i+1);
						String[] beanLine = list.get(i);
						/**
						 * 取出 （ String[]） beanLine的值
						 *       每一行（row）复制
						 */
						for(int j = 0; j<beanLine.length; j++){							
							if(beanLine[j]!=null&&!beanLine[j].equals("")&&!beanLine[j].equals("null")){
								HSSFRichTextString hrts = new HSSFRichTextString(beanLine[j].toString());
								row.createCell(j+1).setCellValue(hrts);
							}else{
								HSSFRichTextString hrts = new HSSFRichTextString("");
								row.createCell(j+1).setCellValue(hrts);
							}	
						}
						beanLine = null;
						row = null;
					}
				}
			}else{
				//表头为空
				DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CREATE_TABLENAME));
			}
		}catch(Exception e){
			ExceptionManage.dispose(e, ExportExcel.class);
		}finally{		
			sheet_1 = null;
			row_0 = null;
			headName = null;
		}
		return workbook;
	}
	/**
	 * 将 传人的 List集合（bean） 转为  List(String[])
	 *     作为导出时传人的参数之一
	 * @param list
	 *     传人的 bean对象的 集合
	 * @param tableName
	 *       当多个页面公用一个Bean时
	 *           根据tabelName 判定具体是哪个界面
	 * @return  List<String[]>
	 *     返回：String[]的集合
	 * @throws Exception
	 */
	public List<String[]> tranListString(List list,String tableName) throws Exception{
		ListString listString=new ListString();
		return listString.tranListString(list, tableName);
		
	}
}
