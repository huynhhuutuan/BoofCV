/*
 * Copyright (c) 2011-2016, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
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

package boofcv.examples.calibration;

import boofcv.alg.distort.ImageDistort;
import boofcv.alg.distort.spherical.EquirectangularToPinhole_F32;
import boofcv.alg.interpolate.InterpolatePixel;
import boofcv.alg.interpolate.TypeInterpolate;
import boofcv.core.image.border.BorderType;
import boofcv.factory.distort.FactoryDistort;
import boofcv.factory.interpolate.FactoryInterpolation;
import boofcv.gui.ListDisplayPanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.UtilIO;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.calib.CameraPinhole;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageType;
import boofcv.struct.image.Planar;
import georegression.geometry.ConvertRotation3D_F64;
import georegression.struct.EulerType;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Demonstration for how to project a synthetic pinhole camera view given an equirectangular image. To
 * specify the projection the camera model needs to be configured and the orientation of the view needs to bet set.
 *
 * @author Peter Abeles
 */
public class ExampleEquirectangularToPinhole {
	public static void main(String[] args) {

		// Specify what the pinhole camera should look like
		CameraPinhole pinholeModel = new CameraPinhole(250,250,0,250,250,500,500);

		// Load equirectangular RGB image
		BufferedImage bufferedEqui =
				UtilImageIO.loadImage(UtilIO.pathExample("spherical/equirectangular_half_dome_01.jpg"));
		Planar<GrayU8> equiImage =
				ConvertBufferedImage.convertFrom(bufferedEqui, true, ImageType.pl(3,GrayU8.class));

		// Declare storage for pinhole camera image
		Planar<GrayU8> pinholeImage = equiImage.createNew(pinholeModel.width, pinholeModel.height);

		// Create the image distorter which will render the image
		InterpolatePixel<Planar<GrayU8>> interp = FactoryInterpolation.
				createPixel(0, 255, TypeInterpolate.BILINEAR, BorderType.EXTENDED, equiImage.getImageType());
		ImageDistort<Planar<GrayU8>,Planar<GrayU8>> distorter =
				FactoryDistort.distort(false,interp,equiImage.getImageType());

		// This is where the magic is done.  It defines the transform rfom equirectangular to pinhole
		EquirectangularToPinhole_F32 equiToPinhole = new EquirectangularToPinhole_F32();
		equiToPinhole.setEquirectangularShape(equiImage.width,equiImage.height);
		equiToPinhole.setPinhole(pinholeModel);

		// Pass in the transform to the image distorter
		distorter.setModel(equiToPinhole);

		// change the orientation of the camera to make the view better
		ConvertRotation3D_F64.eulerToMatrix(EulerType.YXZ,0, 1.45, 2.2,equiToPinhole.getRotation());

		// Render the image
		distorter.apply(equiImage,pinholeImage);
		BufferedImage bufferedPinhole0 = ConvertBufferedImage.convertTo(pinholeImage,null,true);

		// Let's look at another view
		ConvertRotation3D_F64.eulerToMatrix(EulerType.YXZ,0, 1.25, -1.25,equiToPinhole.getRotation());

		distorter.apply(equiImage,pinholeImage);
		BufferedImage bufferedPinhole1 = ConvertBufferedImage.convertTo(pinholeImage,null,true);

		// Display the results
		ListDisplayPanel panel = new ListDisplayPanel();
		panel.addImage(bufferedPinhole0,"Pinehole View 0");
		panel.addImage(bufferedPinhole1,"Pinehole View 1");
		panel.addImage(bufferedEqui,"Equirectangular");
		panel.setPreferredSize(new Dimension(equiImage.width,equiImage.height));

		ShowImages.showWindow(panel, "Equirectangular to Pinhole", true);
	}
}
