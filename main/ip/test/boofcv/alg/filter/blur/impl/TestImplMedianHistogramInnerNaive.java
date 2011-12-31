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

package boofcv.alg.filter.blur.impl;

import boofcv.alg.misc.ImageTestingOps;
import boofcv.struct.image.ImageUInt8;
import boofcv.testing.BoofTesting;
import org.junit.Test;

import java.util.Random;

/**
 * @author Peter Abeles
 */
public class TestImplMedianHistogramInnerNaive {

	@Test
	public void compareToSort() {
		ImageUInt8 image = new ImageUInt8(20,30);
		ImageTestingOps.randomize(image,new Random(234), 0, 100);

		ImageUInt8 found = new ImageUInt8( image.width , image.height );
		ImageUInt8 expected = new ImageUInt8( image.width , image.height );

		BoofTesting.checkSubImage(this, "compareToSort", true, image, found, expected);
	}

	public void compareToSort(ImageUInt8 image, ImageUInt8 found, ImageUInt8 expected) {
		for( int radius = 1; radius <= 3; radius++ ) 
		{
			ImplMedianHistogramInnerNaive.process(image,found,radius,null,null);
			ImplMedianSortNaive.process(image,expected,radius,null);

			BoofTesting.assertEquals(expected,found,radius);
		}
	}
}