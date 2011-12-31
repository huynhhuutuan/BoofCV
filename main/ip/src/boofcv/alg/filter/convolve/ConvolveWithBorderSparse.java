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

package boofcv.alg.filter.convolve;

import boofcv.core.image.border.ImageBorder_F32;
import boofcv.core.image.border.ImageBorder_I32;
import boofcv.struct.convolve.Kernel1D_F32;
import boofcv.struct.convolve.Kernel1D_I32;
import boofcv.struct.convolve.Kernel2D_F32;
import boofcv.struct.convolve.Kernel2D_I32;

/**
 * <p>
 * Implementations of sparse convolve using image border.
 * </p>
 * 
 * <p>
 * DO NOT MODIFY: Generated by {@link boofcv.alg.filter.convolve.border.GenerateConvolveWithBorderSparse}.
 * </p>
 * 
 * @author Peter Abeles
 */
public class ConvolveWithBorderSparse {

	public static float horizontal( Kernel1D_F32 kernel, ImageBorder_F32 input , int x , int y ) {
		final int r = kernel.getRadius();
		final int w = kernel.getWidth();

		float total = 0;

		for( int i = 0; i < w; i++ ) {
			total += input.get(x+i-r,y)*kernel.get(i);
		}

		return total;
	}

	public static float vertical( Kernel1D_F32 kernel, ImageBorder_F32 input , int x , int y ) {
		final int r = kernel.getRadius();
		final int w = kernel.getWidth();

		float total = 0;

		for( int i = 0; i < w; i++ ) {
			total += input.get(x,y+i-r)*kernel.get(i);
		}

		return total;
	}

	public static float convolve( Kernel2D_F32 kernel, ImageBorder_F32 input , int x , int y ) {
		final int r = kernel.getRadius();
		final int w = kernel.getWidth();

		float total = 0;

		for( int i = 0; i < w; i++ ) {
			for( int j = 0; j < w; j++ ) {
				total += input.get(x+j-r,y+i-r)*kernel.get(i,j);
			}
		}

		return total;
	}

	public static int horizontal( Kernel1D_I32 kernel, ImageBorder_I32 input , int x , int y ) {
		final int r = kernel.getRadius();
		final int w = kernel.getWidth();

		int total = 0;

		for( int i = 0; i < w; i++ ) {
			total += input.get(x+i-r,y)*kernel.get(i);
		}

		return total;
	}

	public static int vertical( Kernel1D_I32 kernel, ImageBorder_I32 input , int x , int y ) {
		final int r = kernel.getRadius();
		final int w = kernel.getWidth();

		int total = 0;

		for( int i = 0; i < w; i++ ) {
			total += input.get(x,y+i-r)*kernel.get(i);
		}

		return total;
	}

	public static int convolve( Kernel2D_I32 kernel, ImageBorder_I32 input , int x , int y ) {
		final int r = kernel.getRadius();
		final int w = kernel.getWidth();

		int total = 0;

		for( int i = 0; i < w; i++ ) {
			for( int j = 0; j < w; j++ ) {
				total += input.get(x+j-r,y+i-r)*kernel.get(i,j);
			}
		}

		return total;
	}


}