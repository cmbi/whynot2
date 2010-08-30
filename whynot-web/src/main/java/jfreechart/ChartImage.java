package jfreechart;

import java.awt.image.BufferedImage;

import org.apache.wicket.Resource;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.DynamicImageResource;
import org.apache.wicket.protocol.http.WebResponse;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wicket Image constructed from a JFreeChart and exposing the
 * rendering information to allow image map creation
 * 
 * @author Jonny Wray
 */
public class ChartImage extends Image {
	private static final Logger				log	= LoggerFactory.getLogger(ChartImage.class);

	private int								width;
	private int								height;
	private JFreeChart						chart;
	private transient BufferedImage			image;
	private transient ChartRenderingInfo	renderingInfo;

	public ChartImage(String id, JFreeChart chart, int width, int height) {
		super(id);
		this.width = width;
		this.height = height;
		this.chart = chart;
	}

	private BufferedImage createBufferedImage() {
		if (image == null) {
			renderingInfo = new ChartRenderingInfo();
			try {
				image = chart.createBufferedImage(width, height, renderingInfo);
			}
			catch (NoClassDefFoundError e) {
				log.error(e.getMessage(), e);
				image = null;
			}
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
			protected void setHeaders(WebResponse response) {
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
