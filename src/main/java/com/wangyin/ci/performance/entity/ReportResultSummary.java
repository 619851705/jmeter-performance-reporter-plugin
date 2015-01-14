/**
 * 
 */
package com.wangyin.ci.performance.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wyhubingyin
 * @date 2014年8月13日
 */
public class ReportResultSummary {
	private Map<String,ReportResult> mapReportResult;
	private int samples;
	private long average;
	private long median;
	private long line90;
	private long min;
	private long max;
	private long startTime;
	private long endTime;
	
	private String averageTpsString;
	private String errorPerString;
	private List<Long> listElapsed=new ArrayList<Long>();
	public Map<String, ReportResult> getMapReportResult() {
		return mapReportResult;
	}
	public void setMapReportResult(Map<String, ReportResult> mapReportResult) {
		this.mapReportResult = mapReportResult;
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
	public List<Long> getListElapsed() {
		return listElapsed;
	}
	public void setListElapsed(List<Long> listElapsed) {
		this.listElapsed = listElapsed;
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
	
	
}
