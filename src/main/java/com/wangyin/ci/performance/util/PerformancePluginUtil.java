/**
 * 
 */
package com.wangyin.ci.performance.util;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;




import org.dom4j.DocumentException;

import com.wangyin.ci.performance.entity.PerformanceGeneralParam;
import com.wangyin.ci.performance.entity.ReportResultSummary;

/**
 * @author wyhubingyin
 * @date 2014年9月16日
 */
public class PerformancePluginUtil {
	public static Map<String, ReportResultSummary>  getListShowByFilePaths(File[] files,String runnerType) {
		Map<String, ReportResultSummary> listShow=new TreeMap<String, ReportResultSummary>();
		for(File f: files){
			String fileName=f.getName();
			ReportResultSummary reportResultSummary = null;
			if (PerformanceGeneralParam.JMETER_TYPE.equals(runnerType)) {
				try {
					reportResultSummary = JmeterUtil.read(f);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (DocumentException e) {
					e.printStackTrace();
				}
			}
			listShow.put(fileName, reportResultSummary);
		}
		return listShow;
	}
}
