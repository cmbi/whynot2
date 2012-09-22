package jfreechart;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.apache.wicket.Resource;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.DynamicImageResource;
import org.apache.wicket.protocol.http.WebResponse;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;

/**
 * Wicket Image constructed from a JFreeChart and exposing the rendering information to allow image map creation
 * 
 * @author Jonny Wray
 */
public class ChartImage extends Image {
	private final int						width;
	private final int						height;
	private final JFreeChart				chart;
	private transient BufferedImage			image;
	private transient ChartRenderingInfo	renderingInfo;

	public ChartImage(final String id, final JFreeChart chart, final int width, final int height) {
		super(id);
		this.width = width;
		this.height = height;
		this.chart = chart;
	}

	BufferedImage createBufferedImage() {
		if (image == null) {
			renderingInfo = new ChartRenderingInfo();
			// Previously we used this, but it did not play well with headless servers because of the Swing component
			// image = chart.createBufferedImage(width, height, renderingInfo);
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = (Graphics2D) image.getGraphics();
			chart.draw(graphics, new Rectangle(width, height), renderingInfo);
		}
		return image;
	}

	public ChartRenderingInfo getRenderingInfo() {
		if (renderingInfo == null)
			createBufferedImage();
		return renderingInfo;
	}

	@Override
	protected Resource getImageResource() {
		return new DynamicImageResource() {
			@Override
			protected byte[] getImageData() {
				BufferedImage bufferedImage = createBufferedImage();
				if (bufferedImage == null)
					return new byte[0];
				return toImageData(bufferedImage);
			}

			@Override
			protected void setHeaders(final WebResponse response) {
				if (isCacheable())
					super.setHeaders(response);
				else {
					response.setHeader("Pragma", "no-cache");
					response.setHeader("Cache-Control", "no-cache");
					response.setDateHeader("Expires", 0);
				}
			}
		};
	}
}
