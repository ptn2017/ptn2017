package com.nms.db.bean.system;

import java.io.Serializable;

/**
 * 常量数字
 * @author Administrator
 *
 */
public class UnLoads implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int SITE = 1;//历史告警
	public static final int SLOT = 2;//历史性能-15分钟
	public static final int CARD = 3;	//操作日志
	public static final int FOUR = 4;//历史性能-24小时
	public static final int LOGIN = 5;//登录日志
	public static final int SYSTEM= 6;//系统日志
	public static final int SITEEVENT = 7;// 网元事件日志
	public static final int PERFORMANCE15 = 8;
	public static final int PERFORMANCE24 = 9;
}
