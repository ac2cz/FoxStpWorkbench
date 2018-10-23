package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

import common.Log;
import data.Stp;
import data.StpTable;

@SuppressWarnings("serial")
public class EarthPlotPanel extends MapPanel {

	public static final double MAX_LATITUDE = 81;
	public static final double MIN_LATITUDE = 85;
	public static final int RECTANGULAR_PROJECTION = 0;
	public static final int MERCATOR_PROJECTION = 1;
	public int mapProjection = RECTANGULAR_PROJECTION;

	int topBorder;
	int bottomBorder;
	int sideBorder;
	Color graphAxisColor = Color.BLACK;
	Color graphTextColor = Color.DARK_GRAY;
	int labelHeight = 20;
	int labelWidth = 30;
	int sideLabelOffset = 10;

	static int MAX_TICKS = 30;
	
	StpTable stpTable;
	
	EarthPlotPanel() {
		repaint();
	}
	
	public void setData(StpTable table) {
		stpTable = table;
		repaint();
	}
	
	private void setImage() {
		try {
//		if (graphFrame.mapType == GraphFrame.COLOR_MAP_EQUIRECTANGULAR) {
//			mapProjection = RECTANGULAR_PROJECTION;
//				image = ImageIO.read(this.getClass().getResource("/images/Equirectangular_projection_SW.jpg"));
//			
//		} else if (graphFrame.mapType == GraphFrame.LINE_MAP_EQUIRECTANGULAR) {
			mapProjection = RECTANGULAR_PROJECTION;
			image = ImageIO.read(this.getClass().getResource("/images/map_outline.jpg"));
			
//		}
		} catch (IOException e) {
			 Log.errorDialog("ERROR", "Could not set map image: " + "\n" + e.getMessage());
		}
	}
	
	
    /**
     * We plot lat lon on a map projection.
     * lat is stored in degrees from -90 to +90
     * lon is stored in degrees from -180 to +180
     * 
     * The values are then plotted on a projection of the earth
     * The map is defined by graphHeight and graphWidth.  
     * x is plotted left to right from 0 to graphWidth.  0 is the sideBorder
     * y is plotted vertically with 0 at the top and graphHeight at the bottom.  0 is the topBorder
     * 
     * 
     */
	public void paintComponent(Graphics gr) {
		super.paintComponent( gr ); // call superclass's paintComponent  
		
		topBorder = 0;
		bottomBorder = 0;
		
		int graphHeight = getHeight() - topBorder - bottomBorder;
		int graphWidth = getWidth() - sideBorder*2; // width of entire graph
		
		
//		g.setFont(new Font("SansSerif", Font.PLAIN, Config.graphAxisFontSize));
		
		setImage();
		paintMap(gr, sideBorder, 0, graphHeight, graphWidth);



//		drawLegend(graphHeight, graphWidth, minValue, maxValue, graphFrame.fieldUnits);
		
//		g.setFont(new Font("SansSerif", Font.PLAIN, Config.graphAxisFontSize));
		
		// Draw vertical axis - always in the same place
		gr.setColor(graphAxisColor);
		gr.drawLine(sideBorder, getHeight()-bottomBorder, sideBorder, topBorder);
		int numberOfLabels = (graphHeight)/labelHeight;
		double[] labels = calcAxisInterval(-180, 180, numberOfLabels, false);
		numberOfLabels = labels.length;
		gr.setColor(graphTextColor);
		//g2.drawString("Latitude", sideLabelOffset, topBorder -(int)(Config.graphAxisFontSize/2)); 
		int zeroPoint = getHeight() - topBorder - bottomBorder; //graphHeight + topBorder;
		
		DecimalFormat f2 = new DecimalFormat("0");
		for (int v=1; v < numberOfLabels; v++) {
			
			//int pos = getRatioPosition(minVert, maxVert, labels[v], graphHeight);
			int pos = latToY(labels[v], graphWidth, graphHeight);
			pos = graphHeight-pos+topBorder;
			if (labels[v] == 0) zeroPoint = pos+topBorder;
		//	pos = graphHeight-pos;
			String s = f2.format(labels[v]);

			gr.drawString(s, sideLabelOffset, pos+(int)(20)); 
			
//			if (graphFrame.showHorizontalLines) {
//				g2.setColor(Color.GRAY);
//				g2.drawLine(sideBorder-5, pos+topBorder, graphWidth+sideBorder, pos+topBorder);
//				g2.setColor(graphTextColor);
//			} else
//				g.drawLine(sideBorder-5, pos+topBorder, sideBorder+5, pos+topBorder);
				
			
		}
		gr.setColor(graphAxisColor);
		
//		gr.setFont(new Font("SansSerif", Font.PLAIN, Config.graphAxisFontSize));

		int titleHeight = 20;

		// Draw the title
		gr.setColor(Color.BLACK);
		gr.setFont(new Font("SansSerif", Font.BOLD, 14));
//		String title = graphFrame.displayTitle + " (Mercator Projection)";
		String title = "Ground Station Positions (Equirectangular Projection)";
		
		gr.drawString(title, sideBorder/2 + graphWidth/2 - 150 , titleHeight-10);

		gr.setFont(new Font("SansSerif", Font.PLAIN, 12));
		
		// Draw baseline at the zero point
		gr.setColor(graphAxisColor);
		gr.drawLine(sideLabelOffset+sideBorder, zeroPoint, graphWidth+sideBorder, zeroPoint);
		gr.setColor(graphTextColor);
		int offset = 0;
		//g2.drawString("Longitude", sideLabelOffset, zeroPoint+1*Config.graphAxisFontSize + offset );

		// Plot the labels for the horizontal axis
		int numberOfTimeLabels = graphWidth/(labelWidth/2);
		double[] timelabels = calcAxisInterval(-180, 180, numberOfTimeLabels, true);
		numberOfTimeLabels = timelabels.length;
		
		for (int h=0; h < numberOfTimeLabels; h++) {
			int timepos = getRatioPosition(-180, 180, timelabels[h], graphWidth);
			gr.setColor(graphTextColor);
			gr.drawString(""+(long)timelabels[h], timepos+sideBorder+2, zeroPoint+1*12 + offset);
//			if (graphFrame.showVerticalLines) {
//				g2.setColor(Color.GRAY);
//				g.drawLine(timepos+sideBorder, graphHeight + topBorder+5, timepos+sideBorder, topBorder);
//			}
		}
		
//		if (noLatLonReadings) {
//			g2.setColor(Color.BLACK);
//			g2.drawString("No Latitude and Longitude Data Available for plot", graphWidth/2-50, graphHeight/3);
//			return;
//		}
//
		// Now we plot the data
		// We have to remember that the latitude boxes run 0 - 180 but mean -90 to +90
		//
		int boxHeight = 7;
		int boxWidth = 7;
		gr.setFont(new Font("SansSerif", Font.PLAIN, 12));
		if (stpTable != null)
		for (Stp stp : stpTable.records) {

			double lat = stp.lat; // (180.0*(v)/maxVertBoxes) -90;
			double lon = stp.lon+180; //360.0*h/maxHorBoxes;
			int x = lonToX(lon, graphWidth) +sideBorder+1;
			int y = latToY(lat, graphWidth, graphHeight);
			gr.setColor(Color.RED);
			gr.fillRect(x, graphHeight-y+topBorder-boxHeight, boxWidth, boxHeight);
			//gr.setColor(Color.BLUE);
			//gr.drawString(stp.receiver, x, graphHeight-y+topBorder-boxHeight);
		}
	}

	private Color getColorGradient(double minValue, double maxValue, double val, int range) {
		int alpha = 160; // 50% transparent
		int shade = getRatioPosition(minValue, maxValue, val, 255);
		if (shade > 255) shade = 255;
		if (shade <0) shade = 0;
		shade = 255-shade; // we want min signal white and max black

		/*int r1 = 200;
		int r2 = 100;
		int grn1=0;
		int grn2 = 0;
		int b1=0;
		int b2 = 100;
		int p = shade/255;
		// TODO - why does this linear interpolation not work?
		int r = (int) ((1.0-p) * r1 + p * r2 + 0.5);
		int g = (int) ((1.0-p) * grn1 + p * grn2 + 0.5);
		int b = (int) ((1.0-p) * b1 + p * b2 + 0.5);
		 *
		 */
		
		//g2.setColor(new Color(shade,shade,shade));
		return new Color(255-shade,0,shade, alpha);
		
	}
	
	int lonToX(double lon, int mapWidth) {
		return mercatorLonToX(lon,mapWidth);
	}

	int latToY(double lat, int mapWidth, int mapHeight) {
		if (mapProjection == RECTANGULAR_PROJECTION)
			return rectangularLatToY(lat, mapWidth, mapHeight);
		else
			return mercatorLatToY(lat,mapWidth, mapHeight);
	}

	int rectangularLatToY(double lat, int mapWidth, int mapHeight) {
		int y = (int)(lat*mapHeight/180);
		return mapHeight/2+y;
	}

	
	   /**
     * Convert the longitude to the x coordinate of the Mercator projection
     * 0 is in the center 180 is the mapWidth. -180 is at the left edge of the map
     * 
     * @param lon
     * @param mapWidth
     * @return
     */
    int mercatorLonToX(double lon, int mapWidth) {
		int x = 0;
	
		x = (int) (lon*mapWidth/360);
		
		return x;
    }
    
    /**
     * Convert the latitude to the y coordinate of the Mercator projection
     * 0 is the center of the map vertically
     * @param lat
     * @param mapWidth
     * @param mapHeight
     * @return
     */
    int mercatorLatToY(double lat, int mapWidth, int mapHeight) {
    	// squash vertically to meet the map projection
/*
    	if (lat > 0)
    		lat = MAX_LATITUDE*lat / 90;
    	else
    		lat = MIN_LATITUDE*lat / 90;
 */
    	// convert from degrees to radians because Math functions are in radians
		double latRad = lat * (Math.PI/180);

		// get y value
		double mercN = Math.log(Math.tan((Math.PI/4)+(latRad/2)));
		int y = (int) ((mapWidth/(2*Math.PI))*mercN);
		//return mapHeight/2-y;
		return mapHeight/2+y;
    }
    
    /**
	 * Given a number of ticks across a window and the range, calculate the tick size
	 * and return an array of tick values to use on an axis.  It will have one of the step sizes
	 * calculated by the stepFunction
	 * 
	 * @param range
	 * @param ticks
	 * @return
	 */
	public static double[] calcAxisInterval(double min, double max, int ticks, boolean intStep) {
		double range = max - min;
		if (ticks ==0) ticks = 1;
		double step = 0.0;

		// From the range and the number of ticks, work out a suitable tick size
		step = getStep(range, ticks, intStep);
		
		// We don't want labels that plot off the end of the graph, so reduce the ticks if needed
		ticks = (int) Math.ceil(range / step);
		// Now find the first value before the minimum.
		double startValue = roundToSignificantFigures(Math.round(min/step) * step, 6);

		// We want ticks that go all the way to the end, so resize the tick list if needed

//		int newticks = (int)((max - startValue) / step + 1);
//		if (newticks < ticks * 3 && newticks > ticks) {
//			ticks = newticks;
//			step = getStep(range, ticks);
//		}
		if (ticks > MAX_TICKS) {
			ticks = MAX_TICKS;  // safety check
			step = getStep(range, ticks, intStep);
		}
		if (ticks < 0) {
			ticks = 1;
			step = getStep(range, ticks, intStep);
		}
		
		double[] tickList = new double[ticks];

		if (ticks == 1) { // special case where we do not have room for a label so only some labels are plotted
			//double midValue = roundToSignificantFigures(Math.round(range/2/step) * step, 6);
			tickList[0] = startValue;
		} else 	if (ticks == 2) {
			double midValue = roundToSignificantFigures(startValue + step, 6);
			tickList[0] = startValue;
			tickList[1] = midValue;
		} else
		if (ticks > 0)
			tickList[0] = startValue;
		for (int i=1; i< ticks; i++) {
			startValue = roundToSignificantFigures(startValue + step, 6);
			//val = Math.round(val/step) * step;
			tickList[i] = startValue;
		}
		
		return tickList;
	}

	private static double getStep(double range, int ticks, boolean intStep) {
		double step = 0;
		
		if (!intStep && range/ticks <= 0.01) step = 0.01d;
		else if (!intStep && range/ticks <= 0.1) step = 0.10d;
		else if (!intStep && range/ticks <= 0.2) step = 0.20d;
		else if (!intStep && range/ticks <= 0.25) step = 0.25d;
		else if (!intStep && range/ticks <= 0.33) step = 0.33d;
		else if (!intStep && range/ticks <= 0.5) step = 0.50d;
		else if (range/ticks <= 1) step = 1.00d;
		else if (range/ticks <= 2) step = 2.00d;
		else if (!intStep && range/ticks <= 2.5) step = 2.50d;
		else if (!intStep && range/ticks <= 3.3) step = 3.33d;
		else if (range/ticks <= 5) step = 5.00d;
		else if (range/ticks <= 10) step = 10.00d;
		else if (range/ticks <= 25) step = 25.00d;
		else if (!intStep && range/ticks <= 33) step = 33.33d;
		else if (range/ticks <= 50) step = 50.00d;
		else if (range/ticks <= 100) step = 100.00d;
		else if (range/ticks <= 200) step = 200.00d;
		else if (range/ticks <= 250) step = 250.00d;
		else if (!intStep && range/ticks <= 333) step = 333.33d;
		else if (range/ticks <= 500) step = 500.00d;
		else if (range/ticks <= 1000) step = 1000.00d;
		else if (range/ticks <= 2000) step = 2000.00d;
		else if (range/ticks <= 2500) step = 2500.00d;
		else if (!intStep && range/ticks <= 3333) step = 3333.33d;
		else if (range/ticks <= 5000) step = 5000.00d;
		else if (range/ticks <= 10000) step = 10000.00d;
		else if (range/ticks <= 20000) step = 20000.00d;
		else if (range/ticks <= 25000) step = 25000.00d;
		else if (!intStep && range/ticks <= 33333) step = 33333.33d;
		else if (range/ticks <= 50000) step = 50000.00d;
		else if (range/ticks <= 100000) step = 100000.00d;
		else if (range/ticks <= 250000) step = 250000.00d;
		else if (!intStep && range/ticks <= 333333) step = 333333.33d;
		else if (range/ticks <= 500000) step = 500000.00d;
		else if (range/ticks <= 1000000) step = 1000000.00d;
		else if (range/ticks <= 2000000) step = 2000000.00d;
		else if (range/ticks <= 2500000) step = 2500000.00d;
		else if (!intStep && range/ticks <= 3333333) step = 3333333.33d;
		else if (range/ticks <= 5000000) step = 5000000.00d;
		else if (range/ticks <= 10000000) step = 10000000.00d;
		return step;
	}

	
	
	public static double roundToSignificantFigures(double num, int n) {
	    if(num == 0) {
	        return 0;
	    }

	    final double d = Math.ceil(Math.log10(num < 0 ? -num: num));
	    final int power = n - (int) d;

	    final double magnitude = Math.pow(10, power);
	    final long shifted = Math.round(num*magnitude);
	    return shifted/magnitude;
	}
	
	public static int getRatioPosition(double min, double max, double value, int dimension) {
		if (max == min) return 0;
		double ratio = (max - value) / (max - min);
		int position = (int)Math.round(dimension * ratio);
		return dimension-position;
	}

}
