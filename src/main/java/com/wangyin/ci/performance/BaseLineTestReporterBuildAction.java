/**
 * 
 */
package com.wangyin.ci.performance;

import hudson.model.Action;
import hudson.model.AbstractBuild;

import java.io.File;
import java.lang.ref.WeakReference;

import org.kohsuke.stapler.StaplerProxy;

import com.wangyin.ci.performance.util.BaseTestLineUtil;
import com.wangyin.ci.performance.util.PerformancePluginUtil;

/**
 * @author wyhubingyin
 * @date 2014年7月3日
 */
public class BaseLineTestReporterBuildAction implements Action,StaplerProxy{
	private transient WeakReference<BaseLineTestReportMap> baseLineTestReportMap;
	private AbstractBuild<?, ?> build;

	

	private File[] files;
    private String runnerType;
    
	public BaseLineTestReporterBuildAction(AbstractBuild<?, ?> build,  File[] files,String runnerType) {
		this.build = build;
		this.files=files;
		this.runnerType=runnerType;
	}

	public Object getTarget() {
		return getBaseLineTestReportMap();
	}

	public String getIconFileName() {
		return "graph.gif";
	}

	public String getDisplayName() {
		return "Performance BaseLine Reporter";
	}

	public String getUrlName() {
		return "performanceBaseLineReporter";
	}
	
	private BaseLineTestReportMap getBaseLineTestReportMap()
	{	
		BaseLineTestReportMap reportMap = null;
		WeakReference<BaseLineTestReportMap> wr = baseLineTestReportMap;
		if (wr != null) {
			reportMap = wr.get();
			if (reportMap != null) {
				return reportMap;
			}
		}
		reportMap = new BaseLineTestReportMap(BaseTestLineUtil.getBaseTest(PerformancePluginUtil.getListShowByFilePaths(files,runnerType)),getBuild());
		baseLineTestReportMap = new WeakReference<BaseLineTestReportMap>(reportMap);
		return reportMap;
	}

	/**
	 * @return
	 */
	public AbstractBuild<?, ?> getBuild() {
		return build;
	}


}
