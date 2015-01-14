/**
 * 
 */
package com.wangyin.ci.performance;

import hudson.model.ModelObject;
import hudson.model.AbstractBuild;
import hudson.util.ChartUtil;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.TreeMap;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import com.wangyin.ci.performance.entity.PerformanceGeneralParam;
import com.wangyin.ci.performance.entity.PerformanceReporterParam;
import com.wangyin.ci.performance.entity.ReportResult;
import com.wangyin.ci.performance.entity.ReportResultSummary;
import com.wangyin.ci.performance.util.ReportChartUtil;
import com.wangyin.ci.performance.util.StringConvertUtil;

/**
 * @author wyhubingyin
 * @date 2014年7月3日
 */
public class PerformanceReportMap implements ModelObject {

	private static final String REPORT_LINK = "report";
	private PerformanceReporterParam performanceReporterParam;

	private Map<String, ReportResultSummary> listShow;
	private AbstractBuild<?, ?> build;

	public PerformanceReportMap(Map<String, ReportResultSummary> listShow, AbstractBuild<?, ?> build, PerformanceReporterParam performanceReporterParam) {
		this.listShow = new TreeMap<String, ReportResultSummary>(listShow);
		this.build = build;
		this.performanceReporterParam = performanceReporterParam;
	}

	public String getDisplayName() {
		return "Performance";
	}

	public AbstractBuild<?, ?> getBuild() {
		return build;
	}

	public boolean isTps() {
		return performanceReporterParam.isTps();
	}

	public void doGetTpsGraph(StaplerRequest request, StaplerResponse response) throws Exception {
		ChartUtil.generateGraph(request, response, ReportChartUtil.getTpsGraphByJmeter(listShow.get(StringConvertUtil.convert2utf8(request.getParameter("fileName"))).getMapReportResult()), PerformanceGeneralParam.RESULT_IMAGE_WIDTH, PerformanceGeneralParam.RESULT_IMAGE_HEIGHT);
	}

	public void doGetResponseTimesOverTimeGraph(StaplerRequest request, StaplerResponse response) throws Exception {
		ChartUtil.generateGraph(request, response, ReportChartUtil.getResponseTimesOverTimeGraphByJmeter(listShow.get(StringConvertUtil.convert2utf8(request.getParameter("fileName"))).getMapReportResult()), PerformanceGeneralParam.RESULT_IMAGE_WIDTH, PerformanceGeneralParam.RESULT_IMAGE_HEIGHT);
	}

	public void doGetResponseTimesDistributionGraph(StaplerRequest request, StaplerResponse response) throws Exception {
		ChartUtil.generateGraph(request, response, ReportChartUtil.getResponseTimesDistributionGraphByJmeter(listShow.get(StringConvertUtil.convert2utf8(request.getParameter("fileName"))).getMapReportResult()), PerformanceGeneralParam.RESULT_IMAGE_WIDTH, PerformanceGeneralParam.RESULT_IMAGE_HEIGHT);
	}

	public Map<String, ReportResult> getAggregate(String fileName) {
		return listShow.get(fileName).getMapReportResult();
	}

	public boolean isResponseTimesOverTime() {
		return performanceReporterParam.isResponseTimesOverTime();
	}

	public boolean isResponseTimesDistribution() {
		return performanceReporterParam.isResponseTimesDistribution();
	}

	public Map<String, ReportResultSummary> getListShow() {
		return listShow;
	}

	public Object getDynamic(final String link, final StaplerRequest request, final StaplerRequest response) throws UnsupportedEncodingException {
		if (REPORT_LINK.equals(link)) {
			return createPerformanceReportGraphs(request);
		} else {
			return null;
		}
	}

	public Object createPerformanceReportGraphs(final StaplerRequest request) throws UnsupportedEncodingException {
		String filename = StringConvertUtil.convert2utf8(request.getParameter("fileName"));

		PerformanceReportGraphs reportGraphs = new PerformanceReportGraphs(getBuild(), filename, listShow, performanceReporterParam);

		return reportGraphs;
	}

}
