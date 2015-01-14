package com.wangyin.ci.performance;

import hudson.model.ModelObject;
import hudson.model.AbstractBuild;
import hudson.util.ChartUtil;

import java.util.List;
import java.util.Map;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import com.wangyin.ci.performance.entity.PerformanceGeneralParam;
import com.wangyin.ci.performance.entity.ReportResult;
import com.wangyin.ci.performance.util.ReportChartUtil;
import com.wangyin.ci.performance.util.StringConvertUtil;
/**
 * 
 * @author wyhubingyin
 * @date 2014年8月20日
 */
public class BaseLineTestReportGraphs implements ModelObject {

  private AbstractBuild<?, ?> build;
  private String filename;
  private Map<String, List<ReportResult>> listShow;
   



public BaseLineTestReportGraphs(AbstractBuild<?, ?> build, String filename,Map<String, List<ReportResult>> listShow) {
	this.build = build;
    this.filename = filename;
    this.listShow = listShow;
}


public String getDisplayName() {
	return "BaseLine Report";
}




public List<ReportResult> getReportResultList(String fileName){
	return listShow.get(fileName);
}


public String getFilename() {
	return filename;
}


public AbstractBuild<?, ?> getBuild() {
	return build;
}


public void doGetBaseTestGraph(StaplerRequest request, StaplerResponse response) throws Exception {
	ChartUtil.generateGraph(request, response, ReportChartUtil.getBaseTestGraphByJmeter(listShow.get(StringConvertUtil.convert2utf8(request.getParameter("fileName")))), PerformanceGeneralParam.RESULT_IMAGE_WIDTH, PerformanceGeneralParam.RESULT_IMAGE_HEIGHT);
}


}