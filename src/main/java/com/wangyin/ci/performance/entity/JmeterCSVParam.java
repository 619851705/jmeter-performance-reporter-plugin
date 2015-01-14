/**
 * 
 */
package com.wangyin.ci.performance.entity;

/**
 * @author wyhubingyin
 * @date 2014年7月7日
 */
public class JmeterCSVParam {
	/**
	 * timeStamp,elapsed,label,responseCode,responseMessage,threadName,dataType,success,failureMessage,bytes,Latency

请求发出的绝对时间，响应时间，请求的标签，返回码，返回消息，请求所属的线程，数据类型，是否成功，失败信息，字节，
elapsed Latency timeStamp success label responseCode responseMessage threadName dataType bytes
	 */
	
	private long timeStamp;
	private long elapsed;
	private String label;
	private String responseCode;
	private String responseMessage;
	private String threadName;
	private String dataType;
	private boolean success;
	private long bytes;
	private long latency;
	
	public JmeterCSVParam() {
	}
	
	public JmeterCSVParam(long timeStamp, long elapsed, String label, String responseCode, String responseMessage, String threadName, String dataType, boolean success, long bytes, long latency) {
		this.timeStamp = timeStamp;
		this.elapsed = elapsed;
		this.label = label;
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
		this.threadName = threadName;
		this.dataType = dataType;
		this.success = success;
		this.bytes = bytes;
		this.latency = latency;
	}
	
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public long getElapsed() {
		return elapsed;
	}
	public void setElapsed(long elapsed) {
		this.elapsed = elapsed;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	public String getThreadName() {
		return threadName;
	}
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public long getBytes() {
		return bytes;
	}
	public void setBytes(long bytes) {
		this.bytes = bytes;
	}
	public long getLatency() {
		return latency;
	}
	public void setLatency(long latency) {
		this.latency = latency;
	}
	
	
	
	
}
