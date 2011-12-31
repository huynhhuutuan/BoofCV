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

package boofcv.abst.feature.detect.edge;

import boofcv.abst.filter.blur.BlurFilter;
import boofcv.abst.filter.derivative.ImageGradient;
import boofcv.struct.image.ImageSingleBand;


/**
 * Canny edge detector where the thresholds are computed dynamically based upon the magnitude of the largest edge
 *
 * @author Peter Abeles
 */
public class CannyEdgeContourDynamic<T extends ImageSingleBand, D extends ImageSingleBand> extends CannyEdgeContour<T,D>
{
	// threshold is specified to be
	float fractionLow, fractionHigh;

	public CannyEdgeContourDynamic(BlurFilter<T> blur, ImageGradient<T, D> gradient ,
								   float fractionLow , float fractionHigh  ) {
		super(blur, gradient, 0, 0);
		if( fractionLow >= fractionHigh )
			throw new IllegalArgumentException("low must be lower than high");
		this.fractionLow  = fractionLow;
		this.fractionHigh  = fractionHigh;
	}

	@Override
	protected void updateThresholds() {
		// find the largest intensity value
		float max = 0;

		for( int y = 0; y < suppressed.height; y++ ) {
			for( int x = 0; x < suppressed.width; x++ ) {
//				if( label.get(x,y) != 0 ) {
					float v = suppressed.get(x,y);
					if( v > max )
						max = v;
//				}
			}
		}

		// set the threshold using that
		threshLow = max*fractionLow;
		threshLow = max*fractionHigh;
	}
}