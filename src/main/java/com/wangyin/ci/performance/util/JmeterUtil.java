/**
 * 
 */
package com.wangyin.ci.performance.util;


import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.wangyin.ci.performance.entity.JmeterCSVParam;
import com.wangyin.ci.performance.entity.ReportResult;
import com.wangyin.ci.performance.entity.ReportResultSummary;
import com.wangyin.ci.performance.entity.ResponseTime;

/**
 * @author wyhubingyin
 * @date 2014年7月7日
 */
public class JmeterUtil {
	
	@SuppressWarnings({ "resource" })
	public static ReportResultSummary read(File... files) throws NumberFormatException, IOException, DocumentException{
		 Map<String,ReportResult> resultMap=new TreeMap<String, ReportResult>();
  		 ReportResultSummary summary=new ReportResultSummary();
  		double errorNumbers=0;
  		for(File file:files){
  			if(file!=null){
  				List<JmeterCSVParam> lJmeterCSVParams=new ArrayList<JmeterCSVParam>();
  				if(file.getName().endsWith(".csv")){
  					InputStreamReader read= new InputStreamReader(
  					        new FileInputStream(file),"UTF-8");
  					BufferedReader bufferedReader = new BufferedReader(read);
  					String[] params;
  					while ((params = csvReadFile(bufferedReader, ',')).length != 0) {
  						JmeterCSVParam csv=new JmeterCSVParam(Long.parseLong(params[0]), Long.parseLong(params[1]),params[2], params[3], params[4], params[5], params[6], Boolean.parseBoolean(params[7]), Long.parseLong(params[8]), Long.parseLong(params[9]));
  			        	lJmeterCSVParams.add(csv);
  					}
  				}else if(file.getName().endsWith(".jtl")){
  					SAXReader reader=new SAXReader();
  					Document doc=reader.read(file);
  					List<Element> lElements=doc.getRootElement().elements("sample");
  					for(Element e:lElements){
  						JmeterCSVParam csv=new JmeterCSVParam(Long.parseLong(e.attributeValue("ts")), Long.parseLong(e.attributeValue("t")),e.attributeValue("lb"), e.attributeValue("rc"), e.attributeValue("rm"), e.attributeValue("tn"), e.attributeValue("dt"), Boolean.parseBoolean(e.attributeValue("s")), Long.parseLong(e.attributeValue("by")), Long.parseLong(e.attributeValue("lt")));
  						lJmeterCSVParams.add(csv);
  					}
  				}
  				
  		for(JmeterCSVParam csv:lJmeterCSVParams){	
        	long timeStamp=csv.getTimeStamp();
        	Date date=new Date((timeStamp+csv.getElapsed())/1000*1000);
        	ReportResult result=null;
        	String label=csv.getLabel();
			if(resultMap.containsKey(label)){
				result=resultMap.get(label);
			}else{
				result=new ReportResult();
			}
        	if(csv.isSuccess()){
				if(result.getSuccessTpsMap().containsKey(date)){
					result.getSuccessTpsMap().put(date, result.getSuccessTpsMap().get(date)+1);
				}else{
					result.getSuccessTpsMap().put(date, 1);
				}
				
			}else{
				errorNumbers++;
				result.increaseErrorNumber();
				if(result.getErrorTpsMap().containsKey(date)){
					result.getErrorTpsMap().put(date, result.getErrorTpsMap().get(date)+1);
				}else{
					result.getErrorTpsMap().put(date, 1);
				}
			}
        	long time=timeStamp+csv.getElapsed();
			Date responseOverDate=new Date(time-time%500);
			if(result.getResponseMap().containsKey(responseOverDate)){
				ResponseTime tmpResponseTime=result.getResponseMap().get(responseOverDate);
				result.getResponseMap().put(responseOverDate,  new ResponseTime(tmpResponseTime.getNumber()+1, tmpResponseTime.getSumElapse()+csv.getElapsed()) );
			}else{
				result.getResponseMap().put(responseOverDate, new ResponseTime(1, csv.getElapsed()));
			}
			if(result.getMin()==0||csv.getElapsed()<result.getMin()){
				result.setMin(csv.getElapsed());
			}
			if(csv.getElapsed()>result.getMax()){
				result.setMax(csv.getElapsed());
			}
			if(result.getStartTime()==0||timeStamp<result.getStartTime()){
				result.setStartTime(timeStamp);
			}
			long endTime=timeStamp+csv.getElapsed();
			if(endTime>result.getEndTime()){
				result.setEndTime(endTime);
			}
			result.getListElapsed().add(csv.getElapsed());
			int n=(int) (csv.getElapsed()/100);
			if(result.getResponseDistributionMap().containsKey(n)){
				result.getResponseDistributionMap().put(n, result.getResponseDistributionMap().get(n)+1);
			}else{
				result.getResponseDistributionMap().put(n, 1);
			}
			
			resultMap.put(label, result);
			if(summary.getMin()==0||csv.getElapsed()<summary.getMin()){
				summary.setMin(csv.getElapsed());
			}
			if(csv.getElapsed()>summary.getMax()){
				summary.setMax(csv.getElapsed());
			}
			summary.getListElapsed().add(csv.getElapsed());
			if(summary.getStartTime()==0||timeStamp<summary.getStartTime()){
				summary.setStartTime(timeStamp);
			}
			if(endTime>summary.getEndTime()){
				summary.setEndTime(endTime);
			}
        }
  		}}
  		DecimalFormat   errorFormat   =   new   DecimalFormat("#####0.00");   
        DecimalFormat   averageTpsFormat   =   new   DecimalFormat("#####0.0");
        for(Entry<String,ReportResult> set:resultMap.entrySet()){
        	ReportResult result=set.getValue();
            int samples=result.getListElapsed().size();
            double runtime=result.getEndTime()-result.getStartTime();
            double averageTps=samples>0?samples/runtime*1000:0.0;
            double errorPer=samples>0?result.getErrorNumbers()/samples*100:100.0;
              
           result.setAverageTpsString(averageTpsFormat.format(averageTps));
            
            result.setSamples(samples);
            Collections.sort(result.getListElapsed());
            if(samples>1){
            	result.setLine90(result.getListElapsed().get((int) (samples*0.9-1)));
            	result.setMedian(result.getListElapsed().get(samples/2-1));
            }
            else{
            	result.setLine90(result.getListElapsed().get(0));
            	result.setMedian(result.getListElapsed().get(0));
            }
            result.setAverage(result.getSummuryElapsed()/samples);
            result.setErrorPerString(errorFormat.format(errorPer));
        }
        int samples=summary.getListElapsed().size();
        double runtime=summary.getEndTime()-summary.getStartTime();
        double averageTps=samples>0?samples/runtime*1000:0.0;
        double errorPer=samples>0?errorNumbers/samples*100:100.0;
        summary.setAverageTpsString(averageTpsFormat.format(averageTps));
        
        summary.setSamples(samples);
        Collections.sort(summary.getListElapsed());
        	 if(samples>1){
             	summary.setLine90(summary.getListElapsed().get((int) (samples*0.9-1)));
             	summary.setMedian(summary.getListElapsed().get(samples/2-1));
             }
             else if(samples==1){
             	summary.setLine90(summary.getListElapsed().get(0));
             	summary.setMedian(summary.getListElapsed().get(0));
             }
        	 if(summary.getSummuryElapsed()==0){
        		 summary.setAverage(0);
        	 }
        	 else{summary.setAverage(summary.getSummuryElapsed()/samples);}
             summary.setErrorPerString(errorFormat.format(errorPer));
             summary.setMapReportResult(resultMap);
        
		return summary;
	}
	
	
	private enum ParserState {INITIAL, PLAIN, QUOTED, EMBEDDEDQUOTE}

	public static final char QUOTING_CHAR = '"';

	/**
	 * Reads from file and splits input into strings according to the delimiter,
	 * taking note of quoted strings.
	 * <p>
	 * Handles DOS (CRLF), Unix (LF), and Mac (CR) line-endings equally.
	 * <p>
	 * A blank line - or a quoted blank line - both return an array containing a
	 * single empty String.
	 * 
	 * @param infile
	 *            input file - must support mark(1)
	 * @param delim
	 *            delimiter (e.g. comma)
	 * @return array of strings, will be empty if there is no data, i.e. if the
	 *         input is at EOF.
	 * @throws IOException
	 *             also for unexpected quote characters
	 */
	public static String[] csvReadFile(BufferedReader infile, char delim) throws IOException {
		int ch;
		ParserState state = ParserState.INITIAL;
		List<String> list = new ArrayList<String>();
		CharArrayWriter baos = new CharArrayWriter(200);
		boolean push = false;
		while (-1 != (ch = infile.read())) {
			push = false;
			switch (state) {
			case INITIAL:
				if (ch == QUOTING_CHAR) {
					state = ParserState.QUOTED;
				} else if (isDelimOrEOL(delim, ch)) {
					push = true;
				} else {
					baos.write(ch);
					state = ParserState.PLAIN;
				}
				break;
			case PLAIN:
				if (ch == QUOTING_CHAR) {
					baos.write(ch);
					throw new IOException("Cannot have quote-char in plain field:[" + baos.toString() + "]");
				} else if (isDelimOrEOL(delim, ch)) {
					push = true;
					state = ParserState.INITIAL;
				} else {
					baos.write(ch);
				}
				break;
			case QUOTED:
				if (ch == QUOTING_CHAR) {
					state = ParserState.EMBEDDEDQUOTE;
				} else {
					baos.write(ch);
				}
				break;
			case EMBEDDEDQUOTE:
				if (ch == QUOTING_CHAR) {
					baos.write(QUOTING_CHAR); // doubled quote => quote
					state = ParserState.QUOTED;
				} else if (isDelimOrEOL(delim, ch)) {
					push = true;
					state = ParserState.INITIAL;
				} else {
					baos.write(QUOTING_CHAR);
					throw new IOException("Cannot have single quote-char in quoted field:[" + baos.toString() + "]");
				}
				break;
			} // switch(state)
			if (push) {
				if (ch == '\r') {// Remove following \n if present
					infile.mark(1);
					if (infile.read() != '\n') {
						infile.reset(); // did not find \n, put the character
										// back
					}
				}
				String s = baos.toString();
				list.add(s);
				baos.reset();
			}
			if ((ch == '\n' || ch == '\r') && state != ParserState.QUOTED) {
				break;
			}
		} // while not EOF
		if (ch == -1) {// EOF (or end of string) so collect any remaining data
			if (state == ParserState.QUOTED) {
				throw new IOException("Missing trailing quote-char in quoted field:[\"" + baos.toString() + "]");
			}
			// Do we have some data, or a trailing empty field?
			if (baos.size() > 0 // we have some data
					|| push // we've started a field
					|| state == ParserState.EMBEDDEDQUOTE // Just seen ""
			) {
				list.add(baos.toString());
			}
		}
		return list.toArray(new String[list.size()]);
	}

	private static boolean isDelimOrEOL(char delim, int ch) {
		return ch == delim || ch == '\n' || ch == '\r';
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException, DocumentException {
		//File file=new File("C:/Users/Administrator/Desktop/性能平台资料/jtl-posp-void/20140820-Test-100T-100L.csv");
		//File file=new File("C:/Users/Administrator/Desktop/性能平台资料/jtl-posp-void/20141023110207-Test.csv");
		File file=new File("C:\\Users\\Administrator\\Desktop\\性能平台资料\\jtl-posp-void\\20141201184412-Test.csv");
		List<File> files=new ArrayList<File>();
		files.add(file);
		ReportResultSummary result2=read(files.toArray(new File[]{}));
		System.out.println("tps:"+result2.getAverageTpsString());
		System.out.println("min: "+result2.getMin());
		System.out.println("max :"+result2.getMax());
		System.out.println("average :"+result2.getAverage());
		System.out.println("error :"+result2.getErrorPerString());
		System.out.println("line90: :"+result2.getLine90());
		System.out.println("median:"+result2.getMedian());
		System.out.println("samples:"+result2.getSamples());
		for(Entry<String,ReportResult> entry:result2.getMapReportResult().entrySet()){
			System.out.println("---------------------------------------"); 
			System.out.println("label:" +entry.getKey());
			 ReportResult result=entry.getValue();
			 System.out.println(result.getSamples());
			 System.out.println(result.getMin());
				System.out.println(result.getMax());
				System.out.println(result.getLine90());
				System.out.println(result.getAverage());
				System.out.println(result.getMedian());
				System.out.println(result.getErrorPerString());
			 System.out.println(result.getErrorPerString());
			 System.out.println(result.getAverage());
				System.out.println(result.getAverageTpsString());
				//System.out.println(result.getResponseDistributionMap().get(1));
		 }
		
	}
}
