/**
 * 
 */
package com.wangyin.ci.performance;

import hudson.model.AbstractBuild;
import hudson.model.Action;

import java.io.File;
import java.lang.ref.WeakReference;

import org.kohsuke.stapler.StaplerProxy;

import com.wangyin.ci.performance.util.BaseTestLineUtil;
import com.wangyin.ci.performance.util.SummaryReportUtil;

/**
 * @author wyhubingyin
 * @date 2014年9月3日
 */
public class BaseLineTestSummaryReporterBuildAction  implements Action,StaplerProxy{
	private transient WeakReference<BaseLineTestReportMap> baseLineTestReportMap;
	private AbstractBuild<?, ?> build;

	private File[] listFiles;
	


	public BaseLineTestSummaryReporterBuildAction(AbstractBuild<?, ?> build,File[] listFiles) {
		this.build = build;
		this.listFiles=listFiles;
	}

	public Object getTarget() {
		return getBaseLineTestReportMap();
	}

	public String getIconFileName() {
		return "graph.gif";
	}

	public String getDisplayName() {
		return "Performance Summary BaseLine Reporter";
	}

	public String getUrlName() {
		return "performanceSummaryBaseLineReporter";
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
		reportMap = new BaseLineTestReportMap(BaseTestLineUtil.getBaseTest(SummaryReportUtil.getSummaryMap(listFiles)),getBuild());
		baseLineTestReportMap = new WeakReference<BaseLineTestReportMap>(reportMap);
		return reportMap;
	}

	/**
	 * @return
	 */
	public AbstractBuild<?, ?> getBuild() {
		return build;
	}

	public File[] getListFiles() {
		return listFiles;
	}

	public void setListFiles(File[] listFiles) {
		this.listFiles = listFiles;
	}

}
