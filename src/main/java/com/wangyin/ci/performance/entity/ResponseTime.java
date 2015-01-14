/**
 * 
 */
package com.wangyin.ci.performance.entity;

/**
 * @author wyhubingyin
 * @date 2014年7月8日
 */
public class ResponseTime {
	private int number;
	private long sumElapse;
	
	
	public ResponseTime() {
	}
	
	public ResponseTime(int number, long sumElapse) {
		this.number = number;
		this.sumElapse = sumElapse;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public long getSumElapse() {
		return sumElapse;
	}

	public void setSumElapse(long sumElapse) {
		this.sumElapse = sumElapse;
	}
	
	
	
}
