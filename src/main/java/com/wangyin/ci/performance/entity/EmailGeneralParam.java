/**
 * 
 */
package com.wangyin.ci.performance.entity;

import java.util.Properties;


/**
 * @author wyhubingyin
 * @date 2014年7月15日
 */
public class EmailGeneralParam {
	public static final String EMAIL_CONTENT_HEAD="<html>"
			+"<head>"
			+"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
			+"<title>Insert title here</title>"
			+"</head>"
			+"<body>";
			 public static final String EMAIL_CONTENT_BODY_TABLE_HEAD="  <h1>Performance Report (${Report Name})</h1>                                              "
					 +"	<table cellspacing=\"0\" border=\"1\">"
					 +"		<caption style=\"font-size: x-large; color: deepblack\">Aggregate"
					 +"			Graph</caption>"
					 +"		<tr>"
					 +"			<th align=\"center\" style=\"width: 100px\" bgcolor=\"lightgrey\">Label</th>"
					 +"			<th align=\"center\" style=\"width: 100px\" bgcolor=\"lightgrey\">#"
					 +"				Samples</th>"
					 +"			<th align=\"center\" style=\"width: 100px\" bgcolor=\"lightgrey\">Average(ms)</th>"
					 +"			<th align=\"center\" style=\"width: 100px\" bgcolor=\"lightgrey\">Median(ms)</th>"
					 +"			<th align=\"center\" style=\"width: 100px\" bgcolor=\"lightgrey\">90%Line(ms)</th>"
					 +"			<th align=\"center\" style=\"width: 100px\" bgcolor=\"lightgrey\">Min(ms)</th>"
					 +"			<th align=\"center\" style=\"width: 100px\" bgcolor=\"lightgrey\">Max(ms)</th>"
					 +"			<th align=\"center\" style=\"width: 100px\" bgcolor=\"lightgrey\">Error(%)</th>"
					 +"			<th align=\"center\" style=\"width: 100px\" bgcolor=\"lightgrey\">Throughtput(/sec)</th>"
					 +"		</tr>";
			 public static final String EMAIL_CONTENT_BODY_TABLE_BODY="<tr>"
					 +"			<td align=\"left\" style=\"width: 100px; color: black\">${Label}</td>"
					 +"			<td align=\"center\" style=\"width: 100px; color: black\">${Samples}</td>"
					 +"			<td align=\"center\" style=\"width: 100px; color: black\">${Average}</td>"
					 +"			<td align=\"center\" style=\"width: 100px; color: black\">${Median}</td>"
					 +"			<td align=\"center\" style=\"width: 100px; color: black\">${90%Line}</td>"
					 +"			<td align=\"center\" style=\"width: 100px; color: black\">${Min}</td>"
					 +"			<td align=\"center\" style=\"width: 100px; color: black\">${Max}</td>"
					 +"			<td align=\"center\" style=\"width: 100px; color: black\">${Error%}</td>"
					 +"			<td align=\"center\" style=\"width: 100px; color: black\">${Throughtput}</td>"
					 +"		</tr>";
			 public static final	 String EMAIL_CONTENT_BODY_TABLE_END="	<tr>"
					 +"<td align=\"left\" style=\"width: 100px; color: black\">TOTAL</td>"
					 +"<td align=\"center\" style=\"width: 100px; color: black\">${SumSamples}</td>"
			+"<td align=\"center\" style=\"width: 100px; color: black\">${SumAverage}</td>"
			+"<td align=\"center\" style=\"width: 100px; color: black\">${SumMedian}</td>"
			+"<td align=\"center\" style=\"width: 100px; color: black\">${Sum90%Line}</td>"
			+"<td align=\"center\" style=\"width: 100px; color: black\">${SumMin}</td>"
			+"<td align=\"center\" style=\"width: 100px; color: black\">${SumMax}</td>"
			+"<td align=\"center\" style=\"width: 100px; color: black\">${SumError%}</td>"
			+"<td align=\"center\" style=\"width: 100px; color: black\">${SumThroughtput}</td>"
		+"</tr></table>";
			 public static final String EMAIL_CONTENT_BODYIMG="<img src = \"cid:${cid}\" width=\""+PerformanceGeneralParam.RESULT_IMAGE_WIDTH+"\" height=\""+PerformanceGeneralParam.RESULT_IMAGE_HEIGHT+"\">";
			  public static final String EMAIL_CONTENT_END="</body>"
			+"</html>";
			  
			  
			  public static final String BASELINE_EMAIL_CONTENT_BODY_TABLE_HEAD="<h1>Transaction Name : ${transName}</h1>"
					  +"<table cellspacing=\"0\" border=\"1\">"
					+"<caption style=\"font-size: x-large;color: deepblack\">Aggregate Graph</caption>"
    			+"<tr>"
    		+"<th align=\"center\" style=\"width: 100px\" bgcolor=\"lightgrey\">Concurrent Counts</th>"
    		+"<th align=\"center\" style=\"width: 100px\" bgcolor=\"lightgrey\">Average(ms)</th>"
					+"<th align=\"center\" style=\"width: 100px\" bgcolor=\"lightgrey\">Median(ms)</th>"
					+"<th align=\"center\" style=\"width: 100px\" bgcolor=\"lightgrey\">90%Line(ms)</th>"
					+"<th align=\"center\" style=\"width: 100px\" bgcolor=\"lightgrey\">Min(ms)</th>"
					+"<th align=\"center\" style=\"width: 100px\" bgcolor=\"lightgrey\">Max(ms)</th>"
					+"<th align=\"center\" style=\"width: 100px\" bgcolor=\"lightgrey\">Error(%)</th>"
					+"<th align=\"center\" style=\"width: 100px\" bgcolor=\"lightgrey\">Throughtput(/sec)</th>"
		+"</tr>";
			  
			  public static final String BASELINE_EMAIL_CONTENT_BODY_TABLE_BODY="<tr>"
					  +"<td align=\"center\" style=\"width: 100px; color: black\">${ConcurrentCounts}</td>"
			+"<td align=\"center\" style=\"width: 100px; color: black\">${Average}</td>"
			+"<td align=\"center\" style=\"width: 100px; color: black\">${Median}</td>"
			+"<td align=\"center\" style=\"width: 100px; color: black\">${90%Line}</td>"
			+"<td align=\"center\" style=\"width: 100px; color: black\">${Min}</td>"
			+"<td align=\"center\" style=\"width: 100px; color: black\">${Max}</td>"
			+"<td align=\"center\" style=\"width: 100px; color: black\">${Error%}</td>"
			+"<td align=\"center\" style=\"width: 100px; color: black\">${Throughtput}</td>"
		+"</tr>"; 
			  
			  public static final	 String BASELINE_EMAIL_CONTENT_BODY_TABLE_END="</table>";
			  
				public static String EMAIL_USER;
				public static String EMAIL_PASSWORD;
				public static String EMAIL_DEFAULT_SUFFIX;
				public static String EMAIL_ADDRESSER;
				public static String emailHost;
				public static String emailPort;
				
				
			  
				public  static Properties getEmailProperties(){
					Properties props = new Properties();
			        props.put("mail.smtp.auth", "true");
			        props.put("mail.smtp.host", emailHost);
			        props.put("mail.smtp.port", emailPort);
			        return props;
				}
				
				public static final  String EMAIL_SUBJECT="Performance Report";
				
				public static void init(String emailUser,String emailPassword,String emailDefaultSuffix,String emailAddresser,String emailHost,String emailPort){
					EmailGeneralParam.EMAIL_USER=emailUser;
					EmailGeneralParam.EMAIL_PASSWORD=emailPassword;
					EmailGeneralParam.EMAIL_DEFAULT_SUFFIX=emailDefaultSuffix;
					EmailGeneralParam.EMAIL_ADDRESSER=emailAddresser;
					EmailGeneralParam.emailHost=emailHost;
					EmailGeneralParam.emailPort=emailPort;
				}
				
}
