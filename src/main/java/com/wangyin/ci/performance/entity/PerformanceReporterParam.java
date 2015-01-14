/**
 * 
 */
package com.wangyin.ci.performance.entity;

/**
 * @author wyhubingyin
 * @date 2014年7月9日
 */
public class PerformanceReporterParam {
	private String runnerType;

	private boolean tps;

	private boolean responseTimesOverTime;

	private boolean responseTimesDistribution;
	
	
	public PerformanceReporterParam() {
	}
	
	public PerformanceReporterParam(String runnerType, boolean tps, boolean responseTimesOverTime, boolean responseTimesDistribution) {
		this.runnerType = runnerType;
		this.tps = tps;
		this.responseTimesOverTime = responseTimesOverTime;
		this.responseTimesDistribution = responseTimesDistribution;
	}

	public String getRunnerType() {
		return runnerType;
	}

	public void setRunnerType(String runnerType) {
		this.runnerType = runnerType;
	}

	public boolean isTps() {
		return tps;
	}

	public void setTps(boolean tps) {
		this.tps = tps;
	}

	public boolean isResponseTimesOverTime() {
		return responseTimesOverTime;
	}

	public void setResponseTimesOverTime(boolean responseTimesOverTime) {
		this.responseTimesOverTime = responseTimesOverTime;
	}

	public boolean isResponseTimesDistribution() {
		return responseTimesDistribution;
	}

	public void setResponseTimesDistribution(boolean responseTimesDistribution) {
		this.responseTimesDistribution = responseTimesDistribution;
	}
	
	
}
