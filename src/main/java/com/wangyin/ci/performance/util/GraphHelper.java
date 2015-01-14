package com.wangyin.ci.performance.util;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;


import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.JFreeChart;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;


/**
 * @author wilson.wu
 */
public final class GraphHelper
{
	

	/**
	 * Exports a JFreeChart to a SVG file.
	 * 
	 * @param chart
	 *            JFreeChart to export
	 * @param bounds
	 *            the dimensions of the viewport
	 * @param svgFile
	 *            the output file.
	 * @throws IOException
	 *             if writing the svgFile fails.
	 */
	public static void exportChartAsSVG(JFreeChart chart, int width, int height, StaplerRequest req, StaplerResponse rsp)
			throws IOException
	{
		int w = width, h = height;

		String widthStr = req.getParameter("width");
		try {
			w = Integer.valueOf(widthStr);
		}
		catch (Exception e) {
			w = width;
		}
		String heightStr = req.getParameter("height");
		try {
			h = Integer.valueOf(heightStr);
		}
		catch (Exception e) {
			h = height;
		}

		h = h <= 0 ? Toolkit.getDefaultToolkit().getScreenSize().height - 260 : h;
		w = w <= 0 ? Toolkit.getDefaultToolkit().getScreenSize().width - 280 : w;

		Rectangle bounds = new Rectangle(w, h);
		// Get a DOMImplementation and create an XML document
		DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
		Document document = domImpl.createDocument(null, "svg", null);

		// Create an instance of the SVG Generator
		SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

		// draw the chart in the SVG generator
		chart.draw(svgGenerator, bounds);

		// Write svg file
		OutputStream outputStream = rsp.getOutputStream();
		Writer out = new OutputStreamWriter(outputStream, "UTF-8");
		svgGenerator.stream(out, true);
		outputStream.flush();
		out.flush();
	}

	
}
