/**
 * 
 */
package com.wangyin.ci.performance;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.jfree.chart.ChartUtilities;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import com.wangyin.ci.performance.entity.EmailGeneralParam;
import com.wangyin.ci.performance.entity.PerformanceGeneralParam;
import com.wangyin.ci.performance.entity.PerformanceReporterParam;
import com.wangyin.ci.performance.entity.ReportResult;
import com.wangyin.ci.performance.entity.ReportResultSummary;
import com.wangyin.ci.performance.util.BaseTestLineUtil;
import com.wangyin.ci.performance.util.ExceptionUtil;
import com.wangyin.ci.performance.util.JmeterUtil;
import com.wangyin.ci.performance.util.PerformanceEmailSender;
import com.wangyin.ci.performance.util.ReportChartUtil;
import com.wangyin.ci.performance.util.SummaryReportUtil;

/** Performance Reporter Main Class
 * @author wyhubingyin
 * @date 2014年7月2日
 */
public class PerformanceReporterPublisher extends Recorder {
	private String runnerType;
	
	private String filePath;
	
	private String recipients;
	
	private boolean tps;
	
	private boolean responseTimesOverTime;
	
	private boolean responseTimesDistribution;
	
	private String tpsString;
	
	private String responseTimesOverTimeString;
	
	private String responseTimesDistributionString;
	
	public static final String PERFORMANCE_REPORTS_DIRECTORY = "performance-reports";
	
	
	public String getRecipients() {
		return recipients;
	}

	public String getRunnerType() {
		return runnerType;
	}

	public String getFilePath() {
		return filePath;
	}

	public boolean isTps() {
		return tps;
	}

	public boolean isResponseTimesOverTime() {
		return responseTimesOverTime;
	}

	public boolean isResponseTimesDistribution() {
		return responseTimesDistribution;
	}
	
	
	public String getTpsString() {
		return tpsString;
	}
	
	

	public String getResponseTimesOverTimeString() {
		return responseTimesOverTimeString;
	}

	public String getResponseTimesDistributionString() {
		return responseTimesDistributionString;
	}

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}
	
	

	
	
	@DataBoundConstructor
	public PerformanceReporterPublisher(String runnerType, String filePath, String recipients, boolean tps, boolean responseTimesOverTime, boolean responseTimesDistribution) {
		this.runnerType = runnerType;
		this.filePath = filePath;
		this.recipients = recipients;
		this.tps = tps;
		this.responseTimesOverTime = responseTimesOverTime;
		this.responseTimesDistribution = responseTimesDistribution;
		this.tpsString=tps?"true":"false";
		this.responseTimesOverTimeString=responseTimesOverTime?"true":"false";
		this.responseTimesDistributionString=responseTimesDistribution?"true":"false";
	}

	

	@Extension
	public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
		private String emailUser;
		private String emailPassword;
		private String emailAddresser;
		private String emailDefaultSuffix;
		private String emailHost;
		private String emailPort;
		
		public DescriptorImpl() {
			load();
		}
		
		@Override
		public String getDisplayName() {
			return "Performance Reporter";
		}

		@Override
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}

		public String getEmailAddresser() {
			return emailAddresser;
		}

		public void setEmailAddresser(String emailAddresser) {
			this.emailAddresser = emailAddresser;
		}

		public String getEmailPassword() {
			return emailPassword;
		}

		public void setEmailPassword(String emailPassword) {
			this.emailPassword = emailPassword;
		}

		public String getEmailDefaultSuffix() {
			return emailDefaultSuffix;
		}

		public void setEmailDefaultSuffix(String emailDefaultSuffix) {
			this.emailDefaultSuffix = emailDefaultSuffix;
		}

		public String getEmailHost() {
			return emailHost;
		}

		public void setEmailHost(String emailHost) {
			this.emailHost = emailHost;
		}

		public String getEmailPort() {
			return emailPort;
		}

		public void setEmailPort(String emailPort) {
			this.emailPort = emailPort;
		}
		
		
		
		 public String getEmailUser() {
			return emailUser;
		}

		public void setEmailUser(String emailUser) {
			this.emailUser = emailUser;
		}

		@Override
	        public boolean configure(StaplerRequest req, JSONObject formData) throws hudson.model.Descriptor.FormException {
			 this.setEmailUser(formData.getString("emailUser"));       
			this.setEmailAddresser(formData.getString("emailAddresser"));
	                 this.setEmailPassword(formData.getString("emailPassword"));
	                 this.setEmailDefaultSuffix(formData.getString("emailDefaultSuffix"));
	                 this.setEmailHost(formData.getString("emailHost"));
	                 this.setEmailPort(formData.getString("emailPort"));
	        	 save();
	        	return super.configure(req, formData);
	        }
		
	}
	
	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}
	
	public String getEmailUser() {
		return getDescriptor().getEmailUser();
	}
	
	public String getEmailAddresser() {
		return getDescriptor().getEmailAddresser();
	}

	public String getEmailPassword() {
		return getDescriptor().getEmailPassword();
	}

	public String getEmailDefaultSuffix() {
		return getDescriptor().getEmailDefaultSuffix();
	}

	public String getEmailHost() {
		return getDescriptor().getEmailHost();
	}

	public String getEmailPort() {
		if(getDescriptor().getEmailPort() == null || getDescriptor().getEmailPort().length() == 0){
			return "25";
		}
		return getDescriptor().getEmailPort();
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)  {
		String jobName="";
    	String rootDir=build.getRootDir().getPath();
    	
        jobName=rootDir.substring(rootDir.indexOf("jobs")+"jobs".length()+1);
		jobName=jobName.substring(0,jobName.indexOf(File.separator));
		try {
		PrintStream ps = listener.getLogger();
		SummaryReportUtil.incrementSum(jobName);
    	EmailGeneralParam.init(getEmailUser(), getEmailPassword(), getEmailDefaultSuffix(), getEmailAddresser(), getEmailHost()	, getEmailPort());
    	ps.println("EmailGeneralParam init complete");
		if(build.getResult()==Result.FAILURE){
        	return false;
        }
		ps.println("tps :" + tps);
		ps.println("runnerType :" + runnerType);
		ps.println("responseTimesOverTime: " + responseTimesOverTime);
		ps.println("responseTimesDistribution: " + responseTimesDistribution);
		ps.println("Report files :"+filePath);
		ps.println("recipients :"+recipients);
			
		FilePath[]  listfilFilePaths=build.getModuleRoot().list(filePath);
		ps.println("locatePerformanceReports complete");
		 copyReportsToMaster(build, ps, listfilFilePaths,runnerType);
		 ps.println("copyReportsToMaster complete...");
		 Map<String, ReportResultSummary> listShow=new TreeMap<String, ReportResultSummary>();
		Map<String, List<File>> emailMapFile=new TreeMap<String, List<File>>();
		Map<String, List<File>> nmonMap=new HashMap<String, List<File>>(); 
		File dirFile = new File(build.getRootDir() + File.separator + PerformanceReporterPublisher.PERFORMANCE_REPORTS_DIRECTORY + File.separator + runnerType);
			File[] files = null;
			if (PerformanceGeneralParam.JMETER_TYPE.equals(runnerType)) {
				files = dirFile.listFiles(new FilenameFilter() {

					public boolean accept(File dir, String name) {
						return name.endsWith(".jtl") || name.endsWith(".csv");
					}
				});
			//TODO AK47
			}
			
			ps.println("report files size :" +files.length);
			
			String jobdir=rootDir.substring(0,rootDir.indexOf("jobs")+"jobs".length());
			String nmonDir=jobdir+File.separator+jobName+File.separator+"workspace"+File.separator+"nmon";
			ps.println("nmondir : "+nmonDir);
			File nmonFile=new File(nmonDir);
			File[] nmonList=new File[]{};
			if(nmonFile!=null&&nmonFile.exists()){
			nmonList=nmonFile.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".nmon");
				}
			});}

			for(File nmon:nmonList){
				String name=nmon.getName().substring(0,nmon.getName().indexOf("."));
				String[] strings=name.split("_");
				int len=strings.length;
				String key=strings[len-2]+"-"+strings[len-1];
				if(nmonMap.containsKey(key)){
					nmonMap.get(key).add(nmon);
				}else{
					List<File> lFiles=new ArrayList<File>();
					lFiles.add(nmon);
					nmonMap.put(key, lFiles);
				}
				
			}
			for(File f: files){
				List<File> fileList=new ArrayList<File>();
				String fileName=f.getName();
				ps.println(fileName);
				String baseName=fileName.substring(0,fileName.indexOf("."));
				String[] strings=baseName.split("-");
				int len=strings.length;
				String key=strings[len-2]+"-"+strings[len-1];
				if (PerformanceGeneralParam.JMETER_TYPE.equals(runnerType)) {
					ReportResultSummary reportResultSummary;
						ps.println("Jmeter readCsv start");
						reportResultSummary = JmeterUtil.read(f);
						ps.println("Jmeter readCsv complete");
						String fileDir=rootDir + File.separator + PerformanceReporterPublisher.PERFORMANCE_REPORTS_DIRECTORY + File.separator + fileName.substring(0, fileName.indexOf("."));
						if(tps){
							String tmpString=fileDir+"_TPS.png";
							File tmp=new File(tmpString);
							ChartUtilities.saveChartAsPNG(tmp, ReportChartUtil.getTpsGraphByJmeter(reportResultSummary.getMapReportResult()), PerformanceGeneralParam.RESULT_IMAGE_WIDTH, PerformanceGeneralParam.RESULT_IMAGE_HEIGHT);
							fileList.add(tmp);
							ps.println("save tps image complete");
						}
						if (responseTimesOverTime) {
							String tmpString=fileDir+"_ResponseTimesOverTime.png";
							File tmp=new File(tmpString);
							ChartUtilities.saveChartAsPNG(tmp, ReportChartUtil.getResponseTimesOverTimeGraphByJmeter(reportResultSummary.getMapReportResult()), PerformanceGeneralParam.RESULT_IMAGE_WIDTH, PerformanceGeneralParam.RESULT_IMAGE_HEIGHT);
							fileList.add(tmp);
							ps.println("save responseTimesOverTime image complete");
						}
						if(responseTimesDistribution){
							String tmpString=fileDir+"_ResponseTimesDistribution.png";
							File tmp=new File(tmpString);
							ChartUtilities.saveChartAsPNG(tmp, ReportChartUtil.getResponseTimesDistributionGraphByJmeter(reportResultSummary.getMapReportResult()), PerformanceGeneralParam.RESULT_IMAGE_WIDTH, PerformanceGeneralParam.RESULT_IMAGE_HEIGHT);
							fileList.add(tmp);
							ps.println("save responseTimesDistribution image complete");
						}
						
						

					listShow.put(fileName, reportResultSummary);
			}
				
			//nmon add
				if(nmonMap.containsKey(key)){
					for(File nmonF:nmonMap.get(key)){
						fileList.add(nmonF);
					}
				}
				emailMapFile.put(fileName, fileList);
				
				
			}
			File[] listFiles=null;
			Map<String, ReportResultSummary> summaryReportlistShow=null;
			if(SummaryReportUtil.getSumCount(jobName)>1){
				ps.println("Merge Report ....");
				ps.println("addReports start!");
				SummaryReportUtil.addReports(files,jobName);
				ps.println("addReports complete!");
				summaryReportlistShow=SummaryReportUtil.getSummaryMap(ps,jobName);
				listFiles=SummaryReportUtil.getFileList(jobName);
				ps.println("Merge Report complete");
			}	
		if(recipients!=null&&!recipients.isEmpty()){
				ps.println("send Email start...");
				sendEmail(listShow,emailMapFile,build.getFullDisplayName(),ps);
				ps.println("send Email complete...");
		}
		PerformanceReporterParam param=new PerformanceReporterParam(runnerType, tps, responseTimesOverTime, responseTimesDistribution);
		PerformanceReporterBuildAction buildAction = new PerformanceReporterBuildAction(build, param,files);
		build.addAction(buildAction);
		//add BestLine Test
		if(listShow.size()>1){
			Map<String,List<ReportResult>> map=BaseTestLineUtil.getBaseTest(listShow);
			if(recipients!=null&&!recipients.isEmpty()){
				ps.println("send BaseLine Email start...");
				for(Entry<String,List<ReportResult>> entry:map.entrySet()){
					String fileName=rootDir+ File.separator + PerformanceReporterPublisher.PERFORMANCE_REPORTS_DIRECTORY + File.separator +entry.getKey().replaceAll("[\\\\/:*?\"<>\\|]", "_")+"_BaseLine.png";
					File tmp=new File(fileName);
					ChartUtilities.saveChartAsPNG(tmp, ReportChartUtil.getBaseTestGraphByJmeter(entry.getValue()), PerformanceGeneralParam.RESULT_IMAGE_WIDTH, PerformanceGeneralParam.RESULT_IMAGE_HEIGHT);
					sendBaseLineEmail(tmp,entry,build.getFullDisplayName());
					ps.println("Transaction name : "+entry.getKey()+" send Email complete");
				}
				ps.println("send BaseLine Email complete...");
			}
			
			BaseLineTestReporterBuildAction baseLineTestReporterBuildAction=new BaseLineTestReporterBuildAction(build,  files,runnerType);
			build.addAction(baseLineTestReporterBuildAction);
			
		}
		//add ReportSummary
		if(summaryReportlistShow!=null){
		build.addAction(new PerformanceSummaryReportProjectAction(build, param,listFiles));
		//sendEmail ReportSummary
		if(recipients!=null&&!recipients.isEmpty()){
			ps.println("send ReportSummary Email start...");
			SummaryReportUtil.sendSummaryEmail(summaryReportlistShow,tps,responseTimesOverTime,responseTimesDistribution,rootDir,jobName,recipients,ps);
			ps.println("send ReportSummary Email complete...");
		}
		if(summaryReportlistShow.size()>1){
			Map<String,List<ReportResult>> map=BaseTestLineUtil.getBaseTest(summaryReportlistShow);
			//add ReportSummary BaseLine report
			if(recipients!=null&&!recipients.isEmpty()){
				ps.println("send BaseLine Summary Email start...");
				SummaryReportUtil.sendBaseLineSummaryEmail(map,rootDir,recipients,build.getFullDisplayName(),jobName,ps);
				ps.println("send BaseLine Summary Email complete...");
			}
			//add ReportSummary BaseLine action
			BaseLineTestSummaryReporterBuildAction baseLineTestReporterBuildAction=new BaseLineTestSummaryReporterBuildAction(build, listFiles);
			build.addAction(baseLineTestReporterBuildAction);
		}
		int n=0;
		while(true){
			int sum=SummaryReportUtil.getSumCount(jobName);
			int execute=SummaryReportUtil.getExecuteOverCount(jobName);
			ps.println("executers sum counts: "+ sum);
			ps.println("executer completed counts : "+ execute);
			if(sum==execute){
				ps.println("All executers run complete!");
				break;
			}
			ps.println("Wait for the other executers is running over...");
			Thread.sleep(1000);
			if(n++==120){
				break;
			}
		}
		}
		Thread.sleep(3000);
		
		}  catch (Exception e) {
			 listener.getLogger().println(ExceptionUtil.exception2String(e));
			 build.setResult(Result.FAILURE);
		}finally{
			 listener.getLogger().println("reset ...");
			SummaryReportUtil.reset(jobName);
		}
		
		return true;
	}
	
	

	/**
	 * @param tmp
	 * @param entry
	 * @param fullDisplayName
	 * @throws Exception 
	 */
	private void sendBaseLineEmail(File tmp, Entry<String, List<ReportResult>> entry, String fullDisplayName) throws Exception {
			StringBuilder sBuilder=new StringBuilder();
			sBuilder.append(EmailGeneralParam.EMAIL_CONTENT_HEAD);
			sBuilder.append(EmailGeneralParam.BASELINE_EMAIL_CONTENT_BODY_TABLE_HEAD.replace("${transName}", entry.getKey()));
			for(ReportResult result:entry.getValue()){
				String body=EmailGeneralParam.BASELINE_EMAIL_CONTENT_BODY_TABLE_BODY;
				body=body.replace("${ConcurrentCounts}", String.valueOf(result.getThreads())).
				replace("${Samples}", String.valueOf(result.getSamples())).
				replace("${Average}",String.valueOf(result.getAverage())).
				replace("${Median}", String.valueOf(result.getMedian())).
				replace("${90%Line}", String.valueOf(result.getLine90())).
				replace("${Min}", String.valueOf(result.getMin())).
				replace("${Max}", String.valueOf(result.getMax())).
				replace("${Error%}", result.getErrorPerString()).
				replace("${Throughtput}", result.getAverageTpsString());
				sBuilder.append(body);
			}
			sBuilder.append(EmailGeneralParam.BASELINE_EMAIL_CONTENT_BODY_TABLE_END);
				sBuilder.append(EmailGeneralParam.EMAIL_CONTENT_BODYIMG.replace("${cid}", tmp.getName()));
			sBuilder.append(EmailGeneralParam.EMAIL_CONTENT_END);
			PerformanceEmailSender emailSender=new PerformanceEmailSender(EmailGeneralParam.EMAIL_USER, EmailGeneralParam.EMAIL_PASSWORD,EmailGeneralParam.EMAIL_ADDRESSER, EmailGeneralParam.getEmailProperties());
			List<File> lFiles=new ArrayList<File>();
			lFiles.add(tmp);
			emailSender.sendMail(recipients, EmailGeneralParam.EMAIL_SUBJECT+"(JobName : "+fullDisplayName+" Transaction Name : "+entry.getKey()+" )", sBuilder.toString(), lFiles);
			
	}

	/**
	 * @param listShow
	 * @param emailMapFile
	 * @param string 
	 * @throws Exception 
	 */
	private void sendEmail(Map<String, ReportResultSummary> listShow, Map<String, List<File>> emailMapFile, String jobName,PrintStream ps) throws Exception {
		for(Entry<String,ReportResultSummary> entry:listShow.entrySet()){
			String title=entry.getKey();
			StringBuilder sBuilder=new StringBuilder();
			ReportResultSummary reportResultSummary=entry.getValue();
			sBuilder.append(EmailGeneralParam.EMAIL_CONTENT_HEAD);
			sBuilder.append(EmailGeneralParam.EMAIL_CONTENT_BODY_TABLE_HEAD.replace("${Report Name}", title));
			for(Entry<String, ReportResult> e:reportResultSummary.getMapReportResult().entrySet()){
				String body=EmailGeneralParam.EMAIL_CONTENT_BODY_TABLE_BODY;
				ReportResult result=e.getValue();
				body=body.replace("${Label}", e.getKey()).
				replace("${Samples}", String.valueOf(result.getSamples())).
				replace("${Average}",String.valueOf(result.getAverage())).
				replace("${Median}", String.valueOf(result.getMedian())).
				replace("${90%Line}", String.valueOf(result.getLine90())).
				replace("${Min}", String.valueOf(result.getMin())).
				replace("${Max}", String.valueOf(result.getMax())).
				replace("${Error%}", result.getErrorPerString()).
				replace("${Throughtput}", result.getAverageTpsString());
				sBuilder.append(body);
			}
			String tableEnd=EmailGeneralParam.EMAIL_CONTENT_BODY_TABLE_END;
			tableEnd=tableEnd.replace("${SumSamples}",String.valueOf( reportResultSummary.getSamples())).
			replace("${SumAverage}", String.valueOf( reportResultSummary.getAverage())).
			replace("${SumMedian}",String.valueOf( reportResultSummary.getMedian())).
			replace("${Sum90%Line}", String.valueOf(reportResultSummary.getLine90())).
			replace("${SumMin}", String.valueOf(reportResultSummary.getMin())).
			replace("${SumMax}", String.valueOf(reportResultSummary.getMax())).
			replace("${SumError%}", reportResultSummary.getErrorPerString()).
			replace("${SumThroughtput}", reportResultSummary.getAverageTpsString());
			sBuilder.append(tableEnd);
			List<File> lFiles=emailMapFile.get(title);
			for(File file:lFiles){
				if(file.getName().endsWith(".png")){
				sBuilder.append(EmailGeneralParam.EMAIL_CONTENT_BODYIMG.replace("${cid}", file.getName()));
				}
			}
			sBuilder.append(EmailGeneralParam.EMAIL_CONTENT_END);
			PerformanceEmailSender emailSender=new PerformanceEmailSender(EmailGeneralParam.EMAIL_USER, EmailGeneralParam.EMAIL_PASSWORD,EmailGeneralParam.EMAIL_ADDRESSER, EmailGeneralParam.getEmailProperties());
			String emailTitle=EmailGeneralParam.EMAIL_SUBJECT+"(JobName : "+jobName+" Report File : "+title+" )";
			emailSender.sendMail(recipients, emailTitle, sBuilder.toString(), lFiles);
			ps.println("Email Title : "+emailTitle+" , send complete");
			
		}
	}

	private void copyReportsToMaster(AbstractBuild<?, ?> build,
		      PrintStream logger, FilePath[] listfilFilePaths, String parserDisplayName)
		      throws IOException, InterruptedException {
		FilePath remoteZipDir=null;
		FilePath remoteZip=null;
		try{
			String zipString= "report-result.zip";
			String zipDirString= "report-result";
			remoteZipDir=new FilePath(build.getModuleRoot(),zipDirString);
			remoteZipDir.mkdirs();
			logger.println("copy remote report to remote report-result dir....");
		    for (FilePath src : listfilFilePaths) {
		    	src.copyTo(new FilePath(remoteZipDir,src.getName()));
		    }
		    remoteZip=new FilePath(remoteZipDir.getParent(),zipString);
		    logger.println("compress remote report-result dir ....");
		    remoteZipDir.zip(remoteZip);
		    File localReport = getPerformanceReport(build, parserDisplayName,
		    		zipString);
		   
	      FilePath localReportFilePath=new FilePath(localReport);
	      logger.println("copy report-result zip to locate....");
	      remoteZip.copyTo(localReportFilePath);
	      FilePath resultDir=new FilePath(localReportFilePath.getParent(),zipDirString);
	      localReportFilePath.unzip(resultDir);
	      FilePath tmpFilePath=new FilePath(resultDir, zipDirString);
	      List<FilePath> filePaths=tmpFilePath.list();
	      logger.println("copy report to locate dir ...");
	      for(FilePath filePath:filePaths){
	    	  filePath.copyTo(new FilePath(localReportFilePath.getParent(),filePath.getName()));
	      }
	      resultDir.deleteRecursive();
		}finally{
			if(remoteZip!=null){
				 remoteZip.delete();
				 logger.println("delete remoteZip complete");
			}
			if(remoteZipDir!=null){
				 remoteZipDir.deleteRecursive();
				 logger.println("delete remoteZipDir complete");
			}
	      }
		  }
	
	 private static String getRelativePath(String... suffixes) {
		    StringBuilder sb = new StringBuilder(100);
		    sb.append(PERFORMANCE_REPORTS_DIRECTORY);
		    for (String suffix : suffixes) {
		      sb.append(File.separator).append(suffix);
		    }
		    return sb.toString();
		  }
	 
	 
	 public static File getPerformanceReport(AbstractBuild<?, ?> build,
		      String parserDisplayName, String performanceReportName) {
		    return new File(build.getRootDir(),
		        getPerformanceReportFileRelativePath(
		            parserDisplayName,
		            getPerformanceReportBuildFileName(performanceReportName)));
		  }
	 
	 
	 
	 public static String getPerformanceReportBuildFileName(
		      String performanceReportWorkspaceName) {
		    String result = performanceReportWorkspaceName;
		    if (performanceReportWorkspaceName != null) {
		      Pattern p = Pattern.compile("-[0-9]*\\.xml");
		      Matcher matcher = p.matcher(performanceReportWorkspaceName);
		      if (matcher.find()) {
		        result = matcher.replaceAll(".xml");
		      }
		    }
		    return result;
		  }
	 
	 public static String getPerformanceReportFileRelativePath(
		      String parserDisplayName, String reportFileName) {
		    return getRelativePath(parserDisplayName, reportFileName);
		  }
	 
	

	 
}
