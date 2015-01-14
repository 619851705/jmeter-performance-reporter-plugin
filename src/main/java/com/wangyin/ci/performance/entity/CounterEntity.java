/**
 * 
 */
package com.wangyin.ci.performance.entity;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wyhubingyin
 * @date 2014年9月10日
 */
public class CounterEntity {
	private AtomicInteger sumCount=new AtomicInteger(0);
	private AtomicInteger executeCount=new AtomicInteger(0);
	private AtomicInteger executeOverCount=new AtomicInteger(0);
	private CopyOnWriteArrayList<File> fileList=new CopyOnWriteArrayList<File>();
	private Map<String, ReportResultSummary> result;
	private boolean sendSummaryEmail;
	private boolean sendBaseLineSummaryEmail;
	
	
	public CounterEntity() {
	}


	public AtomicInteger getSumCount() {
		return sumCount;
	}


	public void setSumCount(AtomicInteger sumCount) {
		this.sumCount = sumCount;
	}


	public AtomicInteger getExecuteCount() {
		return executeCount;
	}


	public void setExecuteCount(AtomicInteger executeCount) {
		this.executeCount = executeCount;
	}


	public AtomicInteger getExecuteOverCount() {
		return executeOverCount;
	}


	public void setExecuteOverCount(AtomicInteger executeOverCount) {
		this.executeOverCount = executeOverCount;
	}


	


	

	public CopyOnWriteArrayList<File> getFileList() {
		return fileList;
	}


	public void setFileList(CopyOnWriteArrayList<File> fileList) {
		this.fileList = fileList;
	}


	public Map<String, ReportResultSummary> getResult() {
		return result;
	}


	public void setResult(Map<String, ReportResultSummary> result) {
		this.result = result;
	}


	public boolean isSendSummaryEmail() {
		return sendSummaryEmail;
	}


	public void setSendSummaryEmail(boolean sendSummaryEmail) {
		this.sendSummaryEmail = sendSummaryEmail;
	}


	public boolean isSendBaseLineSummaryEmail() {
		return sendBaseLineSummaryEmail;
	}


	public void setSendBaseLineSummaryEmail(boolean sendBaseLineSummaryEmail) {
		this.sendBaseLineSummaryEmail = sendBaseLineSummaryEmail;
	}
	
	
	
	
	
	
	
	
	
	
}
