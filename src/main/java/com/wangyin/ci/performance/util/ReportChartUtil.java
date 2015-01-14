/**
 * 
 */
package com.wangyin.ci.performance.util;


import java.awt.Color;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.EmptyBlock;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.CompositeTitle;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.CategoryTableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

import com.wangyin.ci.performance.entity.ReportResult;
import com.wangyin.ci.performance.entity.ReportResultSummary;
import com.wangyin.ci.performance.entity.ResponseTime;

/**
 * @author wyhubingyin
 * @date 2014年7月7日
 */
public class ReportChartUtil {

	public static JFreeChart getTpsGraphByJmeter(Map<String, ReportResult> resultMap) {
		ArrayList<XYDataset> dataset = new ArrayList<XYDataset>();
		TimeSeriesCollection resp = new TimeSeriesCollection();
		for (Entry<String, ReportResult> entry : resultMap.entrySet()) {
			ReportResult result = entry.getValue();
			String label = entry.getKey();
			TimeSeries successTps = new TimeSeries(label + "(success)", FixedMillisecond.class);
			TimeSeries failTps = new TimeSeries(label + "(failure)", FixedMillisecond.class);
			
			for (Entry<Date, Integer> set : result.getSuccessTpsMap().entrySet()) {
				
				RegularTimePeriod current = new FixedMillisecond(set.getKey());
				successTps.addOrUpdate(current, set.getValue());
			}
			for (Entry<Date, Integer> set : result.getErrorTpsMap().entrySet()) {
				RegularTimePeriod current = new FixedMillisecond(set.getKey());
				failTps.addOrUpdate(current, set.getValue());
			}
			resp.addSeries(successTps);
			resp.addSeries(failTps);
		}
		dataset.add(resp);
		
		JFreeChart chart = ChartFactory.createTimeSeriesChart("Transactions per Second", "Elapsed time(granularity: 1 sec)", "Number of transactions /sec", resp, true, true, false);
		chart.setBackgroundPaint(Color.white);
		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.black);
		plot.setRangeGridlinePaint(Color.black);
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
		return chart;
	}
	
	
	
	public static JFreeChart getResponseTimesOverTimeGraphByJmeter(Map<String, ReportResult> resultMap) {
		ArrayList<XYDataset> dataset = new ArrayList<XYDataset>();
		TimeSeriesCollection resp = new TimeSeriesCollection();
		for (Entry<String, ReportResult> entry : resultMap.entrySet()) {
			ReportResult result = entry.getValue();
			String label = entry.getKey();
			
			TimeSeries response = new TimeSeries(label, FixedMillisecond.class);
			for (Entry<Date, ResponseTime> set : result.getResponseMap().entrySet()) {
				RegularTimePeriod current = new FixedMillisecond(set.getKey());
				ResponseTime responseTime = set.getValue();
				response.addOrUpdate(current, responseTime.getSumElapse() / responseTime.getNumber());
			}
			resp.addSeries(response);
		}
		dataset.add(resp);
		JFreeChart chart = ChartFactory.createTimeSeriesChart("Response Times Over Time", "Elapsed time(granularity: 500 ms)", "Response times in ms", resp, true, true, false);
		chart.setBackgroundPaint(Color.white);

		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.black);
		plot.setRangeGridlinePaint(Color.black);

		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
		return chart;
	}

	public static JFreeChart getResponseTimesDistributionGraphByJmeter(Map<String, ReportResult> resultMap) {
		 CategoryTableXYDataset dataset=new CategoryTableXYDataset();
		 dataset.setIntervalWidth(100);
		int maxDomain=0;
		for (Entry<String, ReportResult> entry : resultMap.entrySet()) {
			ReportResult result = entry.getValue();
			String label = entry.getKey();
			int maxNumber=0;
			for (Entry<Integer, Integer> set : result.getResponseDistributionMap().entrySet()) {
				if(set.getKey()>maxDomain){
					maxDomain=set.getKey();
				}
				if(set.getValue()>maxNumber){
					maxNumber=set.getValue();
				}
			}
			for (Entry<Integer, Integer> set : result.getResponseDistributionMap().entrySet()) {
				dataset.add(set.getKey()*100+50, set.getValue()<(maxNumber/300)?maxNumber/300:set.getValue(), label);
				
			}
		}
		
		JFreeChart chart = ChartFactory.createXYBarChart("Response Times Distribution", "Response times in ms", false, "Number of responses",dataset , PlotOrientation.VERTICAL, true, true, false);
		chart.setBackgroundPaint(Color.white);
		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.black);
		plot.setRangeGridlinePaint(Color.black);
		XYBarRenderer renderer=(XYBarRenderer) plot.getRenderer();
		renderer.setMargin(0.01);
		plot.setRenderer(renderer);
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		NumberAxis axis=(NumberAxis)plot.getDomainAxis();
		axis.setAutoRange(false);
		axis.setLowerBound(0);
		if(maxDomain<10){
			axis.setUpperBound(1000);
		}else{
			axis.setUpperBound((maxDomain+5)*100);
		}
		return chart;
	}
	
	public static JFreeChart getBaseTestGraphByJmeter(List<ReportResult> lr) {
		XYDataset xydataset = createTpsDate(lr);
        JFreeChart jfreechart = ChartFactory.createXYLineChart("Performance Baseline Graphic", "Concurrent Counts", "Transactions per Second(/sec)", xydataset, PlotOrientation.VERTICAL, false, true, false);
        jfreechart.setBackgroundPaint(Color.white);
        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
        NumberAxis numberaxis = (NumberAxis)xyplot.getRangeAxis();
        numberaxis.setAutoRangeIncludesZero(false);
        NumberAxis numberaxis1 = new NumberAxis("Average Response Time(ms)");
        numberaxis1.setAutoRangeIncludesZero(false);
        xyplot.setRangeAxis(1, numberaxis1);
        xyplot.setDataset(1, createResponseAvg(lr));
        xyplot.mapDatasetToRangeAxis(1, 1);
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)xyplot.getRenderer();
        xylineandshaperenderer.setToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
        xylineandshaperenderer.setShapesVisible(true);
        xylineandshaperenderer.setShapesFilled(true);
        XYLineAndShapeRenderer xylineandshaperenderer1 = new XYLineAndShapeRenderer(true, true);
        xylineandshaperenderer1.setSeriesPaint(0, Color.black);
        xylineandshaperenderer.setToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
        xyplot.setRenderer(1, xylineandshaperenderer1);
        LegendTitle legendtitle = new LegendTitle(xylineandshaperenderer);
        LegendTitle legendtitle1 = new LegendTitle(xylineandshaperenderer1);
        BlockContainer blockcontainer = new BlockContainer(new BorderArrangement());
        blockcontainer.add(legendtitle, RectangleEdge.LEFT);
        blockcontainer.add(legendtitle1, RectangleEdge.RIGHT);
        blockcontainer.add(new EmptyBlock(2D, 0.0D));
        CompositeTitle compositetitle = new CompositeTitle(blockcontainer);
        compositetitle.setPosition(RectangleEdge.BOTTOM);
        jfreechart.addSubtitle(compositetitle);
        return jfreechart;
	}
	
	private static XYDataset createTpsDate(List<ReportResult> lr)
    {
        XYSeries xyseries = new XYSeries("Transactions per Second(/sec)");
        for(ReportResult reportResult:lr){
        	xyseries.add(reportResult.getThreads(),Double.parseDouble(reportResult.getAverageTpsString().replace("/sec", "")));
        }
        XYSeriesCollection xyseriescollection = new XYSeriesCollection();
        xyseriescollection.addSeries(xyseries);
        return xyseriescollection;
    }
	
	private static XYDataset createResponseAvg(List<ReportResult> lr)
    {
        XYSeries xyseries = new XYSeries("Average Response Time(ms)");
        for(ReportResult reportResult:lr){
        	xyseries.add(reportResult.getThreads(),reportResult.getAverage());
        }
      
        XYSeriesCollection xyseriescollection = new XYSeriesCollection();
        xyseriescollection.addSeries(xyseries);
        return xyseriescollection;
    }
	

	public static void main(String[] args) throws Exception {
		File[] files=new File[2];
		files[0]=new File("C:\\Users\\Administrator\\Desktop\\性能平台资料\\jtl-posp-void/20140903170526-Test-30T-10D.csv");
		files[1]=new File("C:\\Users\\Administrator\\Desktop\\性能平台资料\\jtl-posp-void/20140903170538-Test-30T-10D.csv");
		ReportResultSummary result = JmeterUtil.read(files);
		Map<String, ReportResult> resultMap=result.getMapReportResult();
		ChartFrame chartFrame = new ChartFrame("Transactions per Second", getResponseTimesOverTimeGraphByJmeter(resultMap));
		// ChartFrame chartFrame = new ChartFrame("Transactions per Second", getTpsGraphByJmeter(resultMap));
		 //ChartUtilities.saveChartAsPNG(new File("d:/tps.png"), getTpsGraphByJmeter(resultMap),1000, 600);
		//ChartFrame chartFrame = new ChartFrame("Transactions per Second", getResponseTimesDistributionGraphByJmeter(resultMap));
		// chart要放在Java容器组件中，ChartFrame继承自java的Jframe类。该第一个参数的数据是放在窗口左上角的，不是正中间的标题。
		 //ChartFrame chartFrame = new ChartFrame("Transactions per Second", getBaseTestGraphByJmeter(resultMap));
		 chartFrame.pack(); // 以合适的大小展现图形
		chartFrame.setVisible(true);// 图形是否可见

	}
}
