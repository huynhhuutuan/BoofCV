/*
 * Copyright (c) 2011-2012, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://www.boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.alg.feature.detect.line;


import boofcv.abst.feature.detect.line.DetectLine;
import boofcv.abst.feature.detect.line.DetectLineSegment;
import boofcv.alg.feature.detect.ImageCorruptPanel;
import boofcv.alg.filter.blur.GBlurImageOps;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.core.image.GeneralizedImageOps;
import boofcv.factory.feature.detect.line.FactoryDetectLineAlgs;
import boofcv.gui.ProcessInput;
import boofcv.gui.SelectAlgorithmImagePanel;
import boofcv.gui.feature.ImageLinePanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.ImageListManager;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Shows detected lines inside of different images.
 *
 * @author Peter Abeles
 */
// todo configure: blur, edge threshold, non-max radius,  min counts
// todo show binary image, transform
public class DetectLineApp<T extends ImageSingleBand, D extends ImageSingleBand>
		extends SelectAlgorithmImagePanel implements ProcessInput , ImageCorruptPanel.Listener
{
	Class<T> imageType;

	T input;
	T inputCorrupted;
	T blur;

	float edgeThreshold = 25;
	int maxLines = 10;
	int blurRadius = 2;

	ImageLinePanel gui = new ImageLinePanel();
	boolean processedImage = false;

	ImageCorruptPanel corruptPanel;

	public DetectLineApp( Class<T> imageType , Class<D> derivType ) {
		super(1);

		this.imageType = imageType;

		addAlgorithm(0,"Hough Polar", FactoryDetectLineAlgs.houghPolar(3, 30, 2, Math.PI / 180, edgeThreshold, maxLines, imageType, derivType));
		addAlgorithm(0,"Hough Foot", FactoryDetectLineAlgs.houghFoot(3, 8, 5, edgeThreshold, maxLines, imageType, derivType));
		addAlgorithm(0,"Hough Foot Sub Image", FactoryDetectLineAlgs.houghFootSub(3, 8, 5, edgeThreshold, maxLines, 2, 2, imageType, derivType));
		addAlgorithm(0,"Grid Line", FactoryDetectLineAlgs.lineRansac(40, 30, 2.36, true, imageType, derivType));

		input = GeneralizedImageOps.createSingleBand(imageType, 1, 1);
		inputCorrupted = GeneralizedImageOps.createSingleBand(imageType, 1, 1);
		blur = GeneralizedImageOps.createSingleBand(imageType, 1, 1);

		JPanel viewArea = new JPanel(new BorderLayout());
		corruptPanel = new ImageCorruptPanel();
		corruptPanel.setListener(this);

		viewArea.add(corruptPanel,BorderLayout.WEST);
		viewArea.add(gui,BorderLayout.CENTER);
		setMainGUI(viewArea);
	}

	public void process( final BufferedImage image ) {
		input.reshape(image.getWidth(),image.getHeight());
		inputCorrupted.reshape(image.getWidth(),image.getHeight());
		blur.reshape(image.getWidth(),image.getHeight());

		ConvertBufferedImage.convertFromSingle(image, input, imageType);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gui.setBackground(image);
				gui.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
				doRefreshAll();
			}
		});
	}

	@Override
	public boolean getHasProcessedImage() {
		return processedImage;
	}

	@Override
	public void refreshAll(Object[] cookies) {
		setActiveAlgorithm(0, null, getAlgorithmCookie(0));
	}

	@Override
	public synchronized void setActiveAlgorithm(int indexFamily, String name, Object cookie) {
		corruptPanel.corruptImage(input,inputCorrupted);
		GBlurImageOps.gaussian(inputCorrupted, blur, -1,blurRadius, null);

		if( cookie instanceof DetectLine ) {
			final DetectLine<T> detector = (DetectLine<T>) cookie;

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					ConvertBufferedImage.convertTo(inputCorrupted, gui.background);
					gui.setLines(detector.detect(blur));
					gui.repaint();
					processedImage = true;
				}
			});
		} else if( cookie instanceof DetectLineSegment) {
			final DetectLineSegment<T> detector = (DetectLineSegment<T>) cookie;

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					ConvertBufferedImage.convertTo(inputCorrupted,gui.background);
					gui.setLineSegments(detector.detect(blur));
					gui.repaint();
					processedImage = true;
				}
			});
		}
	}

	@Override
	public void changeImage(String name, int index) {
		ImageListManager m = getInputManager();
		BufferedImage image = m.loadImage(index,0);

		process(image);
	}

	@Override
	public synchronized void corruptImageChange() {
		doRefreshAll();
	}

	public static void main(String args[]) {
		Class imageType = ImageFloat32.class;
		Class derivType = ImageFloat32.class;

		DetectLineApp app = new DetectLineApp(imageType,derivType);

		ImageListManager manager = new ImageListManager();
		manager.add("Objects","../data/evaluation/simple_objects.jpg");
		manager.add("Indoors","../data/evaluation/lines_indoors.jpg");
		app.setInputManager(manager);

		// wait for it to process one image so that the size isn't all screwed up
		while( !app.getHasProcessedImage() ) {
			Thread.yield();
		}

		ShowImages.showWindow(app,"Line Detection");
	}

}