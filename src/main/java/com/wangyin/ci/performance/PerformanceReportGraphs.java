package com.wangyin.ci.performance;

import hudson.model.ModelObject;
import hudson.model.AbstractBuild;
import hudson.util.ChartUtil;

import java.util.Map;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import com.wangyin.ci.performance.entity.PerformanceGeneralParam;
import com.wangyin.ci.performance.entity.PerformanceReporterParam;
import com.wangyin.ci.performance.entity.ReportResult;
import com.wangyin.ci.performance.entity.ReportResultSummary;
import com.wangyin.ci.performance.util.ReportChartUtil;
import com.wangyin.ci.performance.util.StringConvertUtil;

/**
 * 
 * @author wyhubingyin
 * @date 2014年8月20日
 */
public class PerformanceReportGraphs implements ModelObject {

  private AbstractBuild<?, ?> build;
  private String filename;
  private Map<String, ReportResultSummary> listShow;
  private  PerformanceReporterParam performanceReporterParam;
   

  public PerformanceReportGraphs(AbstractBuild<?, ?> build,
      String filename , Map<String,ReportResultSummary> listShow, PerformanceReporterParam performanceReporterParam) {
    this.build = build;
    this.filename = filename;
    this.listShow = listShow;
    this.performanceReporterParam=performanceReporterParam;
  }


public String getDisplayName() {
	return "Performance Report";
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

public Map<String, ReportResult> getAggregate(String fileName){
	return listShow.get(fileName).getMapReportResult();
}

public ReportResultSummary getReportResultSummary(String fileName){
	return listShow.get(fileName);
}

public boolean isResponseTimesOverTime() {
	return performanceReporterParam.isResponseTimesOverTime();
}

public boolean isResponseTimesDistribution() {
	return performanceReporterParam.isResponseTimesDistribution();
}


public String getFilename() {
	return filename;
}


public AbstractBuild<?, ?> getBuild() {
	return build;
}





}