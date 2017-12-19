package com.nms.ui.ptn.systemManage.monitor.view;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.keys.StringKeysTip;

/**
 * 文件名称:CpuAndMemoryJpanel.java
 * 文件描述:监控数据库内存和客户机的内存情况
 * 
 */
public class CpuAndMemoryJpanel extends DataBasePanel{
	private static final long serialVersionUID = 6952457888210507243L;
	/************用来标记是CPU=1/内存=2/硬盘=3/网卡=4**********************/
	private int label;
	private Object object;
	
	public CpuAndMemoryJpanel(int label,Object object)
	{
		this.object = object;
		this.label = label;
		initTitle();
		refresh();
	}

	/**
	 * 创建表格头信息
	 * 
	 */
	private void initTitle() {
		String st1 = "";
		String st2 = "";
		String st3 = "";
		String st4 = "";
		String st5 = "";
		if(label == 1){
			 st1 = ResourceUtil.srcStr(StringKeysTip.COURSEINFOTABLENUMBER);
			 st2 = ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_NAME);
			 st3 = ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_CPURATE);
			 st4 = ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_CPUFREE);
			 st5 = ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_CPUTIME);
		}else if(label == 2 || label == 3){
			 st1 = ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_NAME);
			 st2 = ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_TOTALMEMORY);
			 st3 = ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_USERMEMORY);
			 st4 = ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_USERRATE)+"(G)%";
			 st5 = ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_FREEMEMORY);
		}else if(label == 4){
			// 网卡信息
			 st1 = ResourceUtil.srcStr(StringKeysTip.COURSEINFOTABLENUMBER);
			 st2 = ResourceUtil.srcStr(StringKeysTip.TIP_DISPLAY_NAME);
			 st3 = ResourceUtil.srcStr(StringKeysTip.TIP_NETWORK_CARD_NAME);
			 st4 = ResourceUtil.srcStr(StringKeysTip.TIP_NET_ADDRESS);
		}
		
		if(label == 4){
			 super.table.setModel(new DefaultTableModel(new Object[][]{},new String[]{st1
		        		,st2
		        		,st3
		        		,st4
		        }){
				@SuppressWarnings("rawtypes")
				Class[] types = new Class[]{java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,java.lang.Object.class};
				@Override
				public Class getColumnClass(int columnIndex){
					return types[columnIndex];
				}
				@Override
				public boolean isCellEditable(int rowIndex,int columnIndex){
					if(rowIndex == 1){
						return false;
					}
					return false;
				}
			});
		}else{
			 super.table.setModel(new DefaultTableModel(new Object[][]{},new String[]{st1
		        		,st2
		        		,st3
		        		,st4
		        		,st5
		        }){
				@SuppressWarnings("rawtypes")
				Class[] types = new Class[]{java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,java.lang.Object.class};
				@Override
				public Class getColumnClass(int columnIndex){
					return types[columnIndex];
				}
				@Override
				public boolean isCellEditable(int rowIndex,int columnIndex){
					if(rowIndex == 1){
						return false;
					}
					return false;
				}
			});
		}
	}
	
	public void refresh()
	{
		 ((DefaultTableModel)this.table.getModel()).getDataVector().clear();
		 ((DefaultTableModel)this.table.getModel()).fireTableDataChanged();
		 Object data[] = new Object[] {};
		 int rowCount = 0;
		 if(object instanceof Double)
		 {
			double cpuRate = (Double)object;
			 if(cpuRate <0){
				 cpuRate = -cpuRate;
			 }
			 data = new Object[] {++rowCount,
					              "CPU", 
					              cpuRate+"%", 
							      (100-cpuRate)+"%",
							      DateUtil.getDate(DateUtil.FULLTIME)
							    };
				((DefaultTableModel)this.table.getModel()).addRow(data);
		 }else if(object instanceof List &&((List)object) != null && ((List)object).size() >0 && label == 2)
		 {
			 DecimalFormat df = new DecimalFormat("######0.00"); 
			 double totalMemorySize =  Double.parseDouble((((List)object).get(0)).toString())/1048576;
			 double usedMemory =  Double.parseDouble((((List)object).get(1)).toString())/1048576;
			 double fireMemory =  Double.parseDouble((((List)object).get(2)).toString())/1048576;
			 
			 data = new Object[] {
					  ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_MEMORY), 
					  Math.rint(totalMemorySize), 
					  df.format(usedMemory),
		              df.format((usedMemory/totalMemorySize)*100)+"%",
		              df.format(fireMemory)
				    };
	            ((DefaultTableModel)this.table.getModel()).addRow(data);
		 }
		 else if(object instanceof File[] &&((File[])object) != null && ((File[])object).length >0)
		 {
			 DecimalFormat df = new DecimalFormat("######0.00"); 
			  File[] roots = (File[]) object;  
	            for (File file : roots) 
	            {  
	            	if(file.getTotalSpace() > 0)
	            	{
		                long userSpace = file.getTotalSpace() - file.getFreeSpace();
		                data = new Object[] {
		                		file.getPath(), 
		                		Math.rint((file.getTotalSpace()/1048576/1024)), 
		                		df.format(userSpace/1048576/1024),
		                		df.format(((double)userSpace/(double)file.getTotalSpace())*100),
		                		df.format(file.getFreeSpace()/1048576/1024)
		                };
		                ((DefaultTableModel)this.table.getModel()).addRow(data);

	            	}
	            }  
		 } else if(object instanceof List && label == 4) {
			 List<String> netList = (List<String>) object;
			 for (String str : netList) {
				String[] infoArr = str.split("@");
				data = new Object[] {
					 	++rowCount,
					 	infoArr[0],
					 	infoArr[1],
					 	infoArr[2]
                };
                ((DefaultTableModel)this.table.getModel()).addRow(data);
			 }
		 }
	}
}
