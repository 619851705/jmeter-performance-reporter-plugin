/**
 * 
 */
package com.wangyin.ci.performance.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jfree.chart.ChartFrame;

import com.wangyin.ci.performance.entity.ReportResult;
import com.wangyin.ci.performance.entity.ReportResultSummary;

/**
 * @author wyhubingyin
 * @date 2014年8月20日
 */
public class BaseTestLineUtil {
	public static Map<String,List<ReportResult>> getBaseTest(Map<String,ReportResultSummary> map){
		Map<String,List<ReportResult>> result=new TreeMap<String,List<ReportResult>>();
		for(Entry<String,ReportResultSummary> e:map.entrySet()){
			int threads=0;
			for(String string:e.getKey().split("-")){
				if(string.matches("^\\d+T$")){
					threads=Integer.parseInt(string.replace("T",""));
					break;
				}
			}
			for(Entry<String,ReportResult> entry:e.getValue().getMapReportResult().entrySet()){
				String sceneName=entry.getKey();
				List<ReportResult> list=null;
				ReportResult reportResult=entry.getValue();
				reportResult.setThreads(threads);
				if(result.containsKey(sceneName)){
					list=result.get(sceneName);
					list.add(reportResult);
				}else{
					list=new ArrayList<ReportResult>();
					list.add(reportResult);
					result.put(sceneName, list);
				}
			}
			if(e.getValue().getMapReportResult().size()>1){
				ReportResultSummary summary=e.getValue();
				ReportResult totalReportResult=new ReportResult(summary.getAverage(), summary.getMin(), summary.getMax(), summary.getAverageTpsString(), summary.getErrorPerString(), threads);
				String total="TOTAL";
				List<ReportResult> totalList=null;
				if(result.containsKey(total)){
					totalList=result.get(total);
					totalList.add(totalReportResult);
				}else{
					totalList=new ArrayList<ReportResult>();
					totalList.add(totalReportResult);
					result.put(total, totalList);
				}
			}
		}
		
		for(Entry<String,List<ReportResult>> e:result.entrySet()){
			Collections.sort(e.getValue(),new Comparator<ReportResult>(){

				public int compare(ReportResult o1, ReportResult o2) {
					return compare(o1.getThreads(), o2.getThreads());
					
				}
				public int compare(Integer o1, Integer o2) {  
			        int val1 = o1.intValue();  
			        int val2 = o2.intValue();  
			        return (val1 < val2 ? -1 : (val1 == val2 ? 0 : 1));  
			      }
				
			});
		}
		
		return result;
	}
	
	public static void main(String[] args) throws Exception {
		File file=new File("C:/Users/Administrator/.jenkins/jobs/test_new2/workspace/target/jmeter/results");
		
		File[] files=file.listFiles();
		Map<String, ReportResultSummary> map=new HashMap<String, ReportResultSummary>();
		for(File f:files){
			map.put(f.getName(), JmeterUtil.read(f));
		}
		Map<String,List<ReportResult>> map2=getBaseTest(map);
		for(Entry<String,List<ReportResult>> e:map2.entrySet()){
			 ChartFrame chartFrame = new ChartFrame("Transactions per Second",ReportChartUtil.getBaseTestGraphByJmeter(e.getValue()));
			 chartFrame.pack(); // 以合适的大小展现图形
			chartFrame.setVisible(true);// 
		}
	}
}
