/**
 * 
 */
package com.wangyin.ci.performance;

import java.io.File;
import java.lang.ref.WeakReference;

import hudson.model.Action;
import hudson.model.AbstractBuild;

import org.kohsuke.stapler.StaplerProxy;

import com.wangyin.ci.performance.entity.PerformanceReporterParam;
import com.wangyin.ci.performance.util.SummaryReportUtil;

/**
 * @author wyhubingyin
 * @date 2014年9月1日
 */
public class PerformanceSummaryReportProjectAction implements Action ,StaplerProxy{
	private transient WeakReference<PerformanceReportMap> performanceSummaryReportMap;
	private AbstractBuild<?, ?> build;
		
	private PerformanceReporterParam performanceReporterParam;
	private File[] listFiles;
	/**
	 * @param listFiles 
	 * @param param 
	 * @param ps 
	 * @param build 
	 */
	public PerformanceSummaryReportProjectAction(AbstractBuild<?, ?> build,  PerformanceReporterParam performanceReporterParam, File[] listFiles) {
		this.build = build;
		this.performanceReporterParam = performanceReporterParam;
		this.listFiles=listFiles;
	}


	public String getIconFileName() {
		return "graph.gif";
	}

	
	public String getDisplayName() {
		return "Performance Summary Reporter";
	}

	
	public String getUrlName() {
		return "performanceSummaryReporter";
	}

	public Object getTarget() {
		return getPerformanceReportMap();
	}
	
	private Object getPerformanceReportMap() {
		PerformanceReportMap reportMap = null;
		WeakReference<PerformanceReportMap> wr = performanceSummaryReportMap;
		if (wr != null) {
			reportMap = wr.get();
			if (reportMap != null) {
				return reportMap;
			}
		}
		reportMap = new PerformanceReportMap(SummaryReportUtil.getSummaryMap(listFiles),getBuild(),getPerformanceReporterParam());
		performanceSummaryReportMap = new WeakReference<PerformanceReportMap>(reportMap);
		return reportMap;
	}


	public AbstractBuild<?, ?> getBuild() {
		return build;
	}


	public void setBuild(AbstractBuild<?, ?> build) {
		this.build = build;
	}


	


	public PerformanceReporterParam getPerformanceReporterParam() {
		return performanceReporterParam;
	}


	public void setPerformanceReporterParam(PerformanceReporterParam performanceReporterParam) {
		this.performanceReporterParam = performanceReporterParam;
	}


	public File[] getListFiles() {
		return listFiles;
	}


	public void setListFiles(File[] listFiles) {
		this.listFiles = listFiles;
	}

}
