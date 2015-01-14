/**
 * 
 */
package com.wangyin.ci.performance.util;


import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dom4j.DocumentException;
import org.jfree.chart.ChartUtilities;

import com.wangyin.ci.performance.PerformanceReporterPublisher;
import com.wangyin.ci.performance.entity.CounterEntity;
import com.wangyin.ci.performance.entity.EmailGeneralParam;
import com.wangyin.ci.performance.entity.PerformanceGeneralParam;
import com.wangyin.ci.performance.entity.ReportResult;
import com.wangyin.ci.performance.entity.ReportResultSummary;

/**
 * @author wyhubingyin
 * @date 2014年9月1日
 */
public class SummaryReportUtil {
	
	private static ConcurrentHashMap<String, CounterEntity> map=new ConcurrentHashMap<String, CounterEntity>();
	
	public static void incrementSum(String name){
		if(!map.containsKey(name)){
			map.putIfAbsent(name, new CounterEntity());
		}
		map.get(name).getSumCount().incrementAndGet();
	}
	

	public static int getSumCount(String name) {
		return map.get(name).getSumCount().get();
	}
	
	

	/**
	 * @param files
	 */
	public static void addReports(File[] files,String name) {
		CopyOnWriteArrayList<File> fileList=map.get(name).getFileList();
		for(File f:files){
			fileList.addIfAbsent(f);
		}
		map.get(name).getExecuteCount().incrementAndGet();
	}
	
	public static File[] getFileList(String jobName) {
		CounterEntity counterEntity=map.get(jobName);
		return counterEntity.getFileList().toArray(new File[]{});
	}
	
	public synchronized static Map<String, ReportResultSummary> getSummaryMap(PrintStream ps,String jobName) throws Exception{
		CounterEntity counterEntity=map.get(jobName);
		Map<String, ReportResultSummary> result=counterEntity.getResult();
		if(result==null){
			int n=0;
			while(true){
				int sum=counterEntity.getSumCount().get();
				int execute=counterEntity.getExecuteCount().get();
				ps.println("executers sum counts: "+sum);
				ps.println("executer add Report completed counts : "+ execute);
				if(sum==execute){
					ps.println("Merge Reports Complete!");
					break;
				}
				ps.println("Merge Reports!Wait other executer add Report");
				Thread.sleep(1000);
				if(n++==60){
					throw new Exception("cant get multi report!");
				}
			}
			Map<String, List<File>> hashMap=new HashMap<String, List<File>>();
		 result=new TreeMap<String, ReportResultSummary>();
		for(File f:counterEntity.getFileList()){
			String name=f.getName();
			name=name.substring(name.indexOf("-")+1);
			if(hashMap.containsKey(name)){
				hashMap.get(name).add(f);
			}else{
				List<File> lFiles=new ArrayList<File>();
				lFiles.add(f);
				hashMap.put(name, lFiles);
			}
		}
		for(Entry<String, List<File>> entry:hashMap.entrySet()){
			result.put(entry.getKey(), JmeterUtil.read(entry.getValue().toArray(new File[]{})));
		}
		counterEntity.setResult(result);
		}
		counterEntity.getExecuteOverCount().incrementAndGet();
		
		return result;
	}
	
	public static Map<String, ReportResultSummary> getSummaryMap(File[] listFiles) {
		
			Map<String, List<File>> hashMap=new HashMap<String, List<File>>();
			Map<String, ReportResultSummary> result=new TreeMap<String, ReportResultSummary>();
		for(File f:listFiles){
			String name=f.getName();
			name=name.substring(name.indexOf("-")+1);
			if(hashMap.containsKey(name)){
				hashMap.get(name).add(f);
			}else{
				List<File> lFiles=new ArrayList<File>();
				lFiles.add(f);
				hashMap.put(name, lFiles);
			}
		}
		for(Entry<String, List<File>> entry:hashMap.entrySet()){
			try {
				result.put(entry.getKey(), JmeterUtil.read(entry.getValue().toArray(new File[]{})));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}

		return result;
	}


	/**
	 * 
	 */
	public static void reset(String name) {
		if(map.containsKey(name)){

			CounterEntity counterEntity=map.get(name);
			counterEntity.getFileList().clear();
			counterEntity.getSumCount().set(0);
			counterEntity.getExecuteCount().set(0);
			counterEntity.getExecuteOverCount().set(0);
			counterEntity.setResult(null);
			counterEntity.setSendBaseLineSummaryEmail(false);
			counterEntity.setSendSummaryEmail(false);
		}
		
	}


	public static int getExecuteOverCount(String name) {
		return map.get(name).getExecuteOverCount().get();
	}


	/**
	 * @param summaryReportlistShow
	 * @param fullDisplayName
	 * @param ps
	 * @throws Exception 
	 */
	public synchronized static void sendSummaryEmail(Map<String, ReportResultSummary> listShow,boolean tps,boolean responseTimesOverTime,boolean responseTimesDistribution,String rootDir,String jobName,String recipients,PrintStream ps) throws Exception {
		CounterEntity counterEntity=map.get(jobName);
			if(!counterEntity.isSendSummaryEmail()){
				for(Entry<String,ReportResultSummary> entry:listShow.entrySet()){
					List<File> fileList=new ArrayList<File>();
					ReportResultSummary reportResultSummary=entry.getValue();
					String fileName=entry.getKey();
					String fileDir=rootDir + File.separator + PerformanceReporterPublisher.PERFORMANCE_REPORTS_DIRECTORY + File.separator + fileName.substring(0, fileName.indexOf("."));
					if(tps){
						String tmpString=fileDir+"_TPS.png";
						File tmp=new File(tmpString);
						ChartUtilities.saveChartAsPNG(tmp, ReportChartUtil.getTpsGraphByJmeter(reportResultSummary.getMapReportResult()), PerformanceGeneralParam.RESULT_IMAGE_WIDTH, PerformanceGeneralParam.RESULT_IMAGE_HEIGHT);
						fileList.add(tmp);
						ps.println("save tps image complete");
					}
					if (responseTimesOverTime) {
						String tmpString=fileDir+"_ResponseTimesOverTime.png";
						File tmp=new File(tmpString);
						ChartUtilities.saveChartAsPNG(tmp, ReportChartUtil.getResponseTimesOverTimeGraphByJmeter(reportResultSummary.getMapReportResult()), PerformanceGeneralParam.RESULT_IMAGE_WIDTH, PerformanceGeneralParam.RESULT_IMAGE_HEIGHT);
						fileList.add(tmp);
						ps.println("save responseTimesOverTime image complete");
					}
					if(responseTimesDistribution){
						String tmpString=fileDir+"_ResponseTimesDistribution.png";
						File tmp=new File(tmpString);
						ChartUtilities.saveChartAsPNG(tmp, ReportChartUtil.getResponseTimesDistributionGraphByJmeter(reportResultSummary.getMapReportResult()), PerformanceGeneralParam.RESULT_IMAGE_WIDTH, PerformanceGeneralParam.RESULT_IMAGE_HEIGHT);
						fileList.add(tmp);
						ps.println("save responseTimesDistribution image complete");
					}
					StringBuilder sBuilder=new StringBuilder();
					sBuilder.append(EmailGeneralParam.EMAIL_CONTENT_HEAD);
					sBuilder.append(EmailGeneralParam.EMAIL_CONTENT_BODY_TABLE_HEAD.replace("${Report Name}", fileName));
					for(Entry<String, ReportResult> e:reportResultSummary.getMapReportResult().entrySet()){
						String body=EmailGeneralParam.EMAIL_CONTENT_BODY_TABLE_BODY;
						ReportResult result=e.getValue();
						body=body.replace("${Label}", e.getKey()).
						replace("${Samples}", String.valueOf(result.getSamples())).
						replace("${Average}",String.valueOf(result.getAverage())).
						replace("${Median}", String.valueOf(result.getMedian())).
						replace("${90%Line}", String.valueOf(result.getLine90())).
						replace("${Min}", String.valueOf(result.getMin())).
						replace("${Max}", String.valueOf(result.getMax())).
						replace("${Error%}", result.getErrorPerString()).
						replace("${Throughtput}", result.getAverageTpsString());
						sBuilder.append(body);
					}
					String tableEnd=EmailGeneralParam.EMAIL_CONTENT_BODY_TABLE_END;
					tableEnd=tableEnd.replace("${SumSamples}",String.valueOf( reportResultSummary.getSamples())).
					replace("${SumAverage}", String.valueOf( reportResultSummary.getAverage())).
					replace("${SumMedian}",String.valueOf( reportResultSummary.getMedian())).
					replace("${Sum90%Line}", String.valueOf(reportResultSummary.getLine90())).
					replace("${SumMin}", String.valueOf(reportResultSummary.getMin())).
					replace("${SumMax}", String.valueOf(reportResultSummary.getMax())).
					replace("${SumError%}", reportResultSummary.getErrorPerString()).
					replace("${SumThroughtput}", reportResultSummary.getAverageTpsString());
					sBuilder.append(tableEnd);
					for(File file:fileList){
						if(file.getName().endsWith(".png")){
						sBuilder.append(EmailGeneralParam.EMAIL_CONTENT_BODYIMG.replace("${cid}", file.getName()));
						}
					}
					sBuilder.append(EmailGeneralParam.EMAIL_CONTENT_END);
					PerformanceEmailSender emailSender=new PerformanceEmailSender(EmailGeneralParam.EMAIL_USER, EmailGeneralParam.EMAIL_PASSWORD,EmailGeneralParam.EMAIL_ADDRESSER, EmailGeneralParam.getEmailProperties());
					String emailTitle="Summary "+EmailGeneralParam.EMAIL_SUBJECT+"(JobName : "+jobName+" Report File : "+fileName+" )";
					emailSender.sendMail(recipients, emailTitle, sBuilder.toString(), fileList);
					ps.println("Email Title : "+emailTitle+" , send complete");
				}
				counterEntity.setSendSummaryEmail(true);
				}
		
		
	}


	/**
	 * @throws Exception 
	 * 
	 */
	public synchronized static void sendBaseLineSummaryEmail(Map<String,List<ReportResult>> reportMap,String rootDir,String recipients,String fullName,String jobName,PrintStream ps) throws Exception {
		CounterEntity counterEntity=map.get(jobName);
			if(!counterEntity.isSendBaseLineSummaryEmail()){
		for(Entry<String,List<ReportResult>> entry:reportMap.entrySet()){
			String fileName=rootDir+ File.separator + PerformanceReporterPublisher.PERFORMANCE_REPORTS_DIRECTORY + File.separator +entry.getKey().replaceAll("[\\\\/:*?\"<>\\|]", "_")+"_BaseLine.png";
			File tmp=new File(fileName);
			ChartUtilities.saveChartAsPNG(tmp, ReportChartUtil.getBaseTestGraphByJmeter(entry.getValue()), PerformanceGeneralParam.RESULT_IMAGE_WIDTH, PerformanceGeneralParam.RESULT_IMAGE_HEIGHT);
			sendBaseLineEmail(tmp,entry,fullName,recipients);
			ps.println("Transaction name : "+entry.getKey()+" send Summary BaseLine Email complete");
		} counterEntity.setSendBaseLineSummaryEmail(true);
			}
	}
	
	
	
	private static void sendBaseLineEmail(File tmp, Entry<String, List<ReportResult>> entry, String fullDisplayName,String recipients) throws Exception {
		StringBuilder sBuilder=new StringBuilder();
		sBuilder.append(EmailGeneralParam.EMAIL_CONTENT_HEAD);
		sBuilder.append(EmailGeneralParam.BASELINE_EMAIL_CONTENT_BODY_TABLE_HEAD.replace("${transName}", entry.getKey()));
		for(ReportResult result:entry.getValue()){
			String body=EmailGeneralParam.BASELINE_EMAIL_CONTENT_BODY_TABLE_BODY;
			body=body.replace("${ConcurrentCounts}", String.valueOf(result.getThreads())).
			replace("${Samples}", String.valueOf(result.getSamples())).
			replace("${Average}",String.valueOf(result.getAverage())).
			replace("${Median}", String.valueOf(result.getMedian())).
			replace("${90%Line}", String.valueOf(result.getLine90())).
			replace("${Min}", String.valueOf(result.getMin())).
			replace("${Max}", String.valueOf(result.getMax())).
			replace("${Error%}", result.getErrorPerString()).
			replace("${Throughtput}", result.getAverageTpsString());
			sBuilder.append(body);
		}
		sBuilder.append(EmailGeneralParam.BASELINE_EMAIL_CONTENT_BODY_TABLE_END);
			sBuilder.append(EmailGeneralParam.EMAIL_CONTENT_BODYIMG.replace("${cid}", tmp.getName()));
		sBuilder.append(EmailGeneralParam.EMAIL_CONTENT_END);
		PerformanceEmailSender emailSender=new PerformanceEmailSender(EmailGeneralParam.EMAIL_USER, EmailGeneralParam.EMAIL_PASSWORD,EmailGeneralParam.EMAIL_ADDRESSER, EmailGeneralParam.getEmailProperties());
		List<File> lFiles=new ArrayList<File>();
		lFiles.add(tmp);
		emailSender.sendMail(recipients, "Summary Base Line "+EmailGeneralParam.EMAIL_SUBJECT+"(JobName : "+fullDisplayName+" Transaction Name : "+entry.getKey()+" )", sBuilder.toString(), lFiles);
}




	
	
	
}
