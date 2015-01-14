/**
 * 
 */
package com.wangyin.ci.performance;

import hudson.model.ModelObject;
import hudson.model.AbstractBuild;
import hudson.util.ChartUtil;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import com.wangyin.ci.performance.entity.PerformanceGeneralParam;
import com.wangyin.ci.performance.entity.ReportResult;
import com.wangyin.ci.performance.util.ReportChartUtil;
import com.wangyin.ci.performance.util.StringConvertUtil;

/**
 * @author wyhubingyin
 * @date 2014年7月3日
 */
public class BaseLineTestReportMap implements ModelObject {
	
	private static final String REPORT_LINK = "baseLine";
	private AbstractBuild<?, ?> build;

	private Map<String, List<ReportResult>> listShow;
	
	public BaseLineTestReportMap(Map<String, List<ReportResult>> listShow, AbstractBuild<?, ?> build) {
		this.listShow = new TreeMap<String,List<ReportResult>>(listShow);
		this.build=build;
	}
	

	public String getDisplayName() {
		return "BaseLine";
	}

	public AbstractBuild<?, ?> getBuild() {
		return build;
	}




	
	public void doGetBaseTestGraph(StaplerRequest request, StaplerResponse response) throws Exception {
		ChartUtil.generateGraph(request, response, ReportChartUtil.getBaseTestGraphByJmeter(listShow.get(StringConvertUtil.convert2utf8(request.getParameter("fileName")))), PerformanceGeneralParam.RESULT_IMAGE_WIDTH, PerformanceGeneralParam.RESULT_IMAGE_HEIGHT);
	}


	public Map<String, List<ReportResult>> getListShow() {
		return listShow;
	}
	
	
	public Object getDynamic(final String link, final StaplerRequest request,
		      final StaplerRequest response) throws UnsupportedEncodingException {
		    if (REPORT_LINK.equals(link)) {
		      return createBaseLineTestReportGraphs(request);
		    } else {
		      return null;
		    }
		  }
	
	

		  public Object createBaseLineTestReportGraphs(final StaplerRequest request) throws UnsupportedEncodingException {
			  String filename =StringConvertUtil.convert2utf8(request.getParameter("fileName"));
		    
		   BaseLineTestReportGraphs reportGraphs = new BaseLineTestReportGraphs( getBuild(),filename, listShow);

		    return reportGraphs;
		  }

	
	

	
}
