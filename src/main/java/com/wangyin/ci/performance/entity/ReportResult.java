/**
 * 
 */
package com.wangyin.ci.performance.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author wyhubingyin
 * @date 2014年7月7日
 */
public class ReportResult{
	
	//Aggregate Graph
	private int samples;
	private long average;
	private long median;
	private long line90;
	private long min;
	private long max;
	//private double errorPer;
	//private double averageTps;
	private double kbPer;
	private long startTime;
	private long endTime;
	
	private double errorNumbers;
	
	private String averageTpsString;
	private String errorPerString;
	Map<Date, Integer> successTpsMap=new HashMap<Date, Integer>();
	Map<Date, Integer> errorTpsMap=new HashMap<Date, Integer>();
	
	Map<Date, ResponseTime> responseMap=new HashMap<Date, ResponseTime>();
	//所有响应时间集合
	 List<Long> listElapsed=new ArrayList<Long>();
	 
	 //responseDistribution 柱状图 map
	 Map<Integer, Integer> responseDistributionMap=new HashMap<Integer, Integer>();
	 
	 private int threads;
	/**
	 * 
	 */
	public ReportResult() {
	}
	
	public ReportResult(long average, long min, long max, String averageTpsString, String errorPerString, int threads) {
		this.average = average;
		this.min = min;
		this.max = max;
		this.averageTpsString = averageTpsString;
		this.errorPerString = errorPerString;
		this.threads = threads;
	}
	
	
	public int getSamples() {
		return samples;
	}


	

	public void setSamples(int samples) {
		this.samples = samples;
	}


	public long getAverage() {
		return average;
	}
	public void setAverage(long average) {
		this.average = average;
	}
	public long getMedian() {
		return median;
	}
	public void setMedian(long median) {
		this.median = median;
	}
	public long getLine90() {
		return line90;
	}
	public void setLine90(long line90) {
		this.line90 = line90;
	}
	public long getMin() {
		return min;
	}
	public void setMin(long min) {
		this.min = min;
	}
	public long getMax() {
		return max;
	}
	public void setMax(long max) {
		this.max = max;
	}
	
	public double getKbPer() {
		return kbPer;
	}
	public void setKbPer(double kbPer) {
		this.kbPer = kbPer;
	}
	public Map<Date, Integer> getSuccessTpsMap() {
		return successTpsMap;
	}
	public void setSuccessTpsMap(Map<Date, Integer> successTpsMap) {
		this.successTpsMap = successTpsMap;
	}
	public Map<Date, Integer> getErrorTpsMap() {
		return errorTpsMap;
	}
	public void setErrorTpsMap(Map<Date, Integer> errorTpsMap) {
		this.errorTpsMap = errorTpsMap;
	}


	public Map<Date, ResponseTime> getResponseMap() {
		return responseMap;
	}


	public void setResponseMap(Map<Date, ResponseTime> responseMap) {
		this.responseMap = responseMap;
	}


	public List<Long> getListElapsed() {
		return listElapsed;
	}


	public void setListElapsed(List<Long> listElapsed) {
		this.listElapsed = listElapsed;
	}


	public Map<Integer, Integer> getResponseDistributionMap() {
		return responseDistributionMap;
	}


	public void setResponseDistributionMap(Map<Integer, Integer> responseDistributionMap) {
		this.responseDistributionMap = responseDistributionMap;
	}


	public long getStartTime() {
		return startTime;
	}


	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}


	public long getEndTime() {
		return endTime;
	}


	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}


	public String getAverageTpsString() {
		return averageTpsString;
	}


	public void setAverageTpsString(String averageTpsString) {
		this.averageTpsString = averageTpsString+"/sec";
	}


	public String getErrorPerString() {
		return errorPerString;
	}


	public void setErrorPerString(String errorPerString) {
		this.errorPerString = errorPerString+"%";
	}



	/**
	 * @return
	 */
	public long getSummuryElapsed() {
		long sum=0;
		for(long i:listElapsed){
			sum+=i;
		}
		return sum;
	}


	


	public double getErrorNumbers() {
		return errorNumbers;
	}


	/**
	 * 
	 */
	public void increaseErrorNumber() {
		errorNumbers++;
	}


	public int getThreads() {
		return threads;
	}


	public void setThreads(int threads) {
		this.threads = threads;
	}




	
	
	
	
	
}
