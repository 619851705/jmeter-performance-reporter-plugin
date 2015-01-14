/**
 * 
 */
package com.wangyin.ci.performance;

import hudson.model.Action;
import hudson.model.AbstractBuild;

import java.io.File;
import java.lang.ref.WeakReference;

import org.kohsuke.stapler.StaplerProxy;

import com.wangyin.ci.performance.entity.PerformanceReporterParam;
import com.wangyin.ci.performance.util.PerformancePluginUtil;

/**
 * @author wyhubingyin
 * @date 2014年7月3日
 */
public class PerformanceReporterBuildAction implements Action,StaplerProxy{
	private transient WeakReference<PerformanceReportMap> performanceReportMap;
	private AbstractBuild<?, ?> build;

	
	private PerformanceReporterParam performanceReporterParam;

	private File[] files;

	public PerformanceReporterBuildAction(AbstractBuild<?, ?> build, PerformanceReporterParam performanceReporterParam,File[] files) {
		this.build = build;
		this.performanceReporterParam = performanceReporterParam;
		this.files=files;
	}

	public Object getTarget() {
		return getPerformanceReportMap();
	}

	public String getIconFileName() {
		return "graph.gif";
	}

	public String getDisplayName() {
		return "Performance Reporter";
	}

	public String getUrlName() {
		return "performanceReporter";
	}
	
	private PerformanceReportMap getPerformanceReportMap()
	{	
		PerformanceReportMap reportMap = null;
		WeakReference<PerformanceReportMap> wr = performanceReportMap;
		if (wr != null) {
			reportMap = wr.get();
			if (reportMap != null) {
				return reportMap;
			}
		}
		reportMap = new PerformanceReportMap(PerformancePluginUtil.getListShowByFilePaths(files,performanceReporterParam.getRunnerType()),getBuild(),getPerformanceReporterParam());
		performanceReportMap = new WeakReference<PerformanceReportMap>(reportMap);
		return reportMap;
	}


	public AbstractBuild<?, ?> getBuild() {
		return build;
	}



	public PerformanceReporterParam getPerformanceReporterParam() {
		return performanceReporterParam;
	}

	public void setPerformanceReporterParam(PerformanceReporterParam performanceReporterParam) {
		this.performanceReporterParam = performanceReporterParam;
	}

	public File[] getFiles() {
		return files;
	}

	public void setFiles(File[] files) {
		this.files = files;
	}

	
	
	
	
	

}
