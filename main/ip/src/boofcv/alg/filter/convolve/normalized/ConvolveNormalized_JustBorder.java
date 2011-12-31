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

package boofcv.alg.filter.convolve.normalized;

import boofcv.struct.convolve.Kernel1D_F32;
import boofcv.struct.convolve.Kernel1D_I32;
import boofcv.struct.convolve.Kernel2D_F32;
import boofcv.struct.convolve.Kernel2D_I32;
import boofcv.struct.image.*;

/**
 * <p>
 * Covolves a 1D kernel in the horizontal or vertical direction across an image's border only, while re-normalizing the
 * kernel sum to one.  The kernel MUST be smaller than the image.
 * </p>
 * 
 * <p>
 * NOTE: Do not modify.  Automatically generated by {@link GenerateConvolveNormalized_JustBorder}.
 * </p>
 * 
 * @author Peter Abeles
 */
@SuppressWarnings({"ForLoopReplaceableByForEach"})
public class ConvolveNormalized_JustBorder {

	public static void horizontal(Kernel1D_F32 kernel, ImageFloat32 input, ImageFloat32 output ) {
		final float[] dataSrc = input.data;
		final float[] dataDst = output.data;
		final float[] dataKer = kernel.data;

		final int radius = kernel.getRadius();
		final int kernelWidth = kernel.getWidth();

		final int width = input.getWidth();
		final int height = input.getHeight();

		for (int i = 0; i < height; i++) {
			int indexDest = output.startIndex + i * output.stride;
			int j = input.startIndex + i * input.stride;
			final int jStart = j;
			int jEnd = j + radius;

			for (; j < jEnd; j++) {
				float total = 0;
				float totalWeight = 0;
				int indexSrc = jStart;
				for (int k = kernelWidth - (radius + 1 + j - jStart); k < kernelWidth; k++) {
					float w = dataKer[k];
					totalWeight += w;
					total += (dataSrc[indexSrc++]) * w;
				}
				dataDst[indexDest++] = (total / totalWeight);
			}

			j += width - 2*radius;
			indexDest += width - 2*radius;

			jEnd = jStart + width;
			for (; j < jEnd; j++) {
				float total = 0;
				float totalWeight = 0;
				int indexSrc = j - radius;
				final int kEnd = jEnd - indexSrc;

				for (int k = 0; k < kEnd; k++) {
					float w = dataKer[k];
					totalWeight += w;
					total += (dataSrc[indexSrc++]) * w;
				}
				dataDst[indexDest++] = (total / totalWeight);
			}
		}
	}

	public static void vertical(Kernel1D_F32 kernel, ImageFloat32 input, ImageFloat32 output ) {
		final float[] dataSrc = input.data;
		final float[] dataDst = output.data;
		final float[] dataKer = kernel.data;

		final int radius = kernel.getRadius();
		final int kernelWidth = kernel.getWidth();

		final int imgWidth = output.getWidth();
		final int imgHeight = output.getHeight();

		final int yEnd = imgHeight - radius;

		for (int y = 0; y < radius; y++) {
			int indexDst = output.startIndex + y * output.stride;
			int i = input.startIndex + y * input.stride;
			final int iEnd = i + imgWidth;

			int kStart = radius - y;

			float weight = 0;
			for (int k = kStart; k < kernelWidth; k++) {
				weight += dataKer[k];
			}

			for ( ; i < iEnd; i++) {
				float total = 0;
				int indexSrc = i - y * input.stride;
				for (int k = kStart; k < kernelWidth; k++, indexSrc += input.stride) {
					total += (dataSrc[indexSrc]) * dataKer[k];
				}
				dataDst[indexDst++] = (total / weight);
			}
		}

		for (int y = yEnd; y < imgHeight; y++) {
			int indexDst = output.startIndex + y * output.stride;
			int i = input.startIndex + y * input.stride;
			final int iEnd = i + imgWidth;

			int kEnd = imgHeight - (y - radius);

			float weight = 0;
			for (int k = 0; k < kEnd; k++) {
				weight += dataKer[k];
			}

			for ( ; i < iEnd; i++) {
				float total = 0;
				int indexSrc = i - radius * input.stride;
				for (int k = 0; k < kEnd; k++, indexSrc += input.stride) {
					total += (dataSrc[indexSrc]) * dataKer[k];
				}
				dataDst[indexDst++] = (total / weight);
			}
		}
	}

	public static void convolve(Kernel2D_F32 kernel, ImageFloat32 input, ImageFloat32 output ) {
		final float[] dataSrc = input.data;
		final float[] dataDst = output.data;
		final float[] dataKer = kernel.data;

		final int radius = kernel.getRadius();
		final int kernelWidth = kernel.getWidth();

		final int width = input.getWidth();
		final int height = input.getHeight();

		// convolve across the left and right borders
		for (int y = 0; y < height; y++) {

			int minI = y >= radius ? -radius : -y;
			int maxI = y < height - radius ?  radius : height - y - 1;

			int indexDst = output.startIndex + y* output.stride;

			for( int x = 0; x < radius; x++ ) {

				float total = 0;
				float totalWeight = 0;

				for( int i = minI; i <= maxI; i++ ) {
					int indexSrc = input.startIndex + (y+i)* input.stride+x;
					int indexKer = (i+radius)*kernelWidth;

					for( int j = -x; j <= radius; j++ ) {
						float w = dataKer[indexKer+j+radius];
						totalWeight += w;
						total += (dataSrc[indexSrc+j]) * w;
					}
				}

				dataDst[indexDst++] = (total / totalWeight);
			}

			indexDst = output.startIndex + y* output.stride + width-radius;
			for( int x = width-radius; x < width; x++ ) {

				int maxJ = width-x-1;

				float total = 0;
				float totalWeight = 0;

				for( int i = minI; i <= maxI; i++ ) {
					int indexSrc = input.startIndex + (y+i)* input.stride+x;
					int indexKer = (i+radius)*kernelWidth;

					for( int j = -radius; j <= maxJ; j++ ) {
						float w = dataKer[indexKer+j+radius];
						totalWeight += w;
						total += (dataSrc[indexSrc+j]) * w;
					}
				}

				dataDst[indexDst++] = (total / totalWeight);
			}
		}

		// convolve across the top border while avoiding convolving the corners again
		for (int y = 0; y < radius; y++) {

			int indexDst = output.startIndex + y* output.stride+radius;

			for( int x = radius; x < width-radius; x++ ) {

				float total = 0;
				float totalWeight = 0;

				for( int i = -y; i <= radius; i++ ) {
					int indexSrc = input.startIndex + (y+i)* input.stride+x;
					int indexKer = (i+radius)*kernelWidth;

					for( int j = -radius; j <= radius; j++ ) {
						float w = dataKer[indexKer+j+radius];
						totalWeight += w;
						total += (dataSrc[indexSrc + j]) * w;
					}
				}
				dataDst[indexDst++] = (total / totalWeight);
			}
		}

		// convolve across the bottom border
		for (int y = height-radius; y < height; y++) {

			int maxI = height - y - 1;
			int indexDst = output.startIndex + y* output.stride+radius;

			for( int x = radius; x < width-radius; x++ ) {

				float total = 0;
				float totalWeight = 0;

				for( int i = -radius; i <= maxI; i++ ) {
					int indexSrc = input.startIndex + (y+i)* input.stride+x;
					int indexKer = (i+radius)*kernelWidth;

					for( int j = -radius; j <= radius; j++ ) {
						float w = dataKer[indexKer+j+radius];
						totalWeight += w;
						total += (dataSrc[indexSrc + j]) * w;
					}
				}
				dataDst[indexDst++] = (total / totalWeight);
			}
		}
	}

	public static void horizontal(Kernel1D_I32 kernel, ImageUInt8 input, ImageInt8 output ) {
		final byte[] dataSrc = input.data;
		final byte[] dataDst = output.data;
		final int[] dataKer = kernel.data;

		final int radius = kernel.getRadius();
		final int kernelWidth = kernel.getWidth();

		final int width = input.getWidth();
		final int height = input.getHeight();

		for (int i = 0; i < height; i++) {
			int indexDest = output.startIndex + i * output.stride;
			int j = input.startIndex + i * input.stride;
			final int jStart = j;
			int jEnd = j + radius;

			for (; j < jEnd; j++) {
				int total = 0;
				int totalWeight = 0;
				int indexSrc = jStart;
				for (int k = kernelWidth - (radius + 1 + j - jStart); k < kernelWidth; k++) {
					int w = dataKer[k];
					totalWeight += w;
					total += (dataSrc[indexSrc++] & 0xFF) * w;
				}
				dataDst[indexDest++] = (byte)(total / totalWeight);
			}

			j += width - 2*radius;
			indexDest += width - 2*radius;

			jEnd = jStart + width;
			for (; j < jEnd; j++) {
				int total = 0;
				int totalWeight = 0;
				int indexSrc = j - radius;
				final int kEnd = jEnd - indexSrc;

				for (int k = 0; k < kEnd; k++) {
					int w = dataKer[k];
					totalWeight += w;
					total += (dataSrc[indexSrc++] & 0xFF) * w;
				}
				dataDst[indexDest++] = (byte)(total / totalWeight);
			}
		}
	}

	public static void vertical(Kernel1D_I32 kernel, ImageUInt8 input, ImageInt8 output ) {
		final byte[] dataSrc = input.data;
		final byte[] dataDst = output.data;
		final int[] dataKer = kernel.data;

		final int radius = kernel.getRadius();
		final int kernelWidth = kernel.getWidth();

		final int imgWidth = output.getWidth();
		final int imgHeight = output.getHeight();

		final int yEnd = imgHeight - radius;

		for (int y = 0; y < radius; y++) {
			int indexDst = output.startIndex + y * output.stride;
			int i = input.startIndex + y * input.stride;
			final int iEnd = i + imgWidth;

			int kStart = radius - y;

			int weight = 0;
			for (int k = kStart; k < kernelWidth; k++) {
				weight += dataKer[k];
			}

			for ( ; i < iEnd; i++) {
				int total = 0;
				int indexSrc = i - y * input.stride;
				for (int k = kStart; k < kernelWidth; k++, indexSrc += input.stride) {
					total += (dataSrc[indexSrc] & 0xFF) * dataKer[k];
				}
				dataDst[indexDst++] = (byte)(total / weight);
			}
		}

		for (int y = yEnd; y < imgHeight; y++) {
			int indexDst = output.startIndex + y * output.stride;
			int i = input.startIndex + y * input.stride;
			final int iEnd = i + imgWidth;

			int kEnd = imgHeight - (y - radius);

			int weight = 0;
			for (int k = 0; k < kEnd; k++) {
				weight += dataKer[k];
			}

			for ( ; i < iEnd; i++) {
				int total = 0;
				int indexSrc = i - radius * input.stride;
				for (int k = 0; k < kEnd; k++, indexSrc += input.stride) {
					total += (dataSrc[indexSrc] & 0xFF) * dataKer[k];
				}
				dataDst[indexDst++] = (byte)(total / weight);
			}
		}
	}

	public static void convolve(Kernel2D_I32 kernel, ImageUInt8 input, ImageInt8 output ) {
		final byte[] dataSrc = input.data;
		final byte[] dataDst = output.data;
		final int[] dataKer = kernel.data;

		final int radius = kernel.getRadius();
		final int kernelWidth = kernel.getWidth();

		final int width = input.getWidth();
		final int height = input.getHeight();

		// convolve across the left and right borders
		for (int y = 0; y < height; y++) {

			int minI = y >= radius ? -radius : -y;
			int maxI = y < height - radius ?  radius : height - y - 1;

			int indexDst = output.startIndex + y* output.stride;

			for( int x = 0; x < radius; x++ ) {

				int total = 0;
				int totalWeight = 0;

				for( int i = minI; i <= maxI; i++ ) {
					int indexSrc = input.startIndex + (y+i)* input.stride+x;
					int indexKer = (i+radius)*kernelWidth;

					for( int j = -x; j <= radius; j++ ) {
						int w = dataKer[indexKer+j+radius];
						totalWeight += w;
						total += (dataSrc[indexSrc+j] & 0xFF) * w;
					}
				}

				dataDst[indexDst++] = (byte)(total / totalWeight);
			}

			indexDst = output.startIndex + y* output.stride + width-radius;
			for( int x = width-radius; x < width; x++ ) {

				int maxJ = width-x-1;

				int total = 0;
				int totalWeight = 0;

				for( int i = minI; i <= maxI; i++ ) {
					int indexSrc = input.startIndex + (y+i)* input.stride+x;
					int indexKer = (i+radius)*kernelWidth;

					for( int j = -radius; j <= maxJ; j++ ) {
						int w = dataKer[indexKer+j+radius];
						totalWeight += w;
						total += (dataSrc[indexSrc+j] & 0xFF) * w;
					}
				}

				dataDst[indexDst++] = (byte)(total / totalWeight);
			}
		}

		// convolve across the top border while avoiding convolving the corners again
		for (int y = 0; y < radius; y++) {

			int indexDst = output.startIndex + y* output.stride+radius;

			for( int x = radius; x < width-radius; x++ ) {

				int total = 0;
				int totalWeight = 0;

				for( int i = -y; i <= radius; i++ ) {
					int indexSrc = input.startIndex + (y+i)* input.stride+x;
					int indexKer = (i+radius)*kernelWidth;

					for( int j = -radius; j <= radius; j++ ) {
						int w = dataKer[indexKer+j+radius];
						totalWeight += w;
						total += (dataSrc[indexSrc + j] & 0xFF) * w;
					}
				}
				dataDst[indexDst++] = (byte)(total / totalWeight);
			}
		}

		// convolve across the bottom border
		for (int y = height-radius; y < height; y++) {

			int maxI = height - y - 1;
			int indexDst = output.startIndex + y* output.stride+radius;

			for( int x = radius; x < width-radius; x++ ) {

				int total = 0;
				int totalWeight = 0;

				for( int i = -radius; i <= maxI; i++ ) {
					int indexSrc = input.startIndex + (y+i)* input.stride+x;
					int indexKer = (i+radius)*kernelWidth;

					for( int j = -radius; j <= radius; j++ ) {
						int w = dataKer[indexKer+j+radius];
						totalWeight += w;
						total += (dataSrc[indexSrc + j] & 0xFF) * w;
					}
				}
				dataDst[indexDst++] = (byte)(total / totalWeight);
			}
		}
	}

	public static void horizontal(Kernel1D_I32 kernel, ImageSInt16 input, ImageInt16 output ) {
		final short[] dataSrc = input.data;
		final short[] dataDst = output.data;
		final int[] dataKer = kernel.data;

		final int radius = kernel.getRadius();
		final int kernelWidth = kernel.getWidth();

		final int width = input.getWidth();
		final int height = input.getHeight();

		for (int i = 0; i < height; i++) {
			int indexDest = output.startIndex + i * output.stride;
			int j = input.startIndex + i * input.stride;
			final int jStart = j;
			int jEnd = j + radius;

			for (; j < jEnd; j++) {
				int total = 0;
				int totalWeight = 0;
				int indexSrc = jStart;
				for (int k = kernelWidth - (radius + 1 + j - jStart); k < kernelWidth; k++) {
					int w = dataKer[k];
					totalWeight += w;
					total += (dataSrc[indexSrc++]) * w;
				}
				dataDst[indexDest++] = (short)(total / totalWeight);
			}

			j += width - 2*radius;
			indexDest += width - 2*radius;

			jEnd = jStart + width;
			for (; j < jEnd; j++) {
				int total = 0;
				int totalWeight = 0;
				int indexSrc = j - radius;
				final int kEnd = jEnd - indexSrc;

				for (int k = 0; k < kEnd; k++) {
					int w = dataKer[k];
					totalWeight += w;
					total += (dataSrc[indexSrc++]) * w;
				}
				dataDst[indexDest++] = (short)(total / totalWeight);
			}
		}
	}

	public static void vertical(Kernel1D_I32 kernel, ImageSInt16 input, ImageInt16 output ) {
		final short[] dataSrc = input.data;
		final short[] dataDst = output.data;
		final int[] dataKer = kernel.data;

		final int radius = kernel.getRadius();
		final int kernelWidth = kernel.getWidth();

		final int imgWidth = output.getWidth();
		final int imgHeight = output.getHeight();

		final int yEnd = imgHeight - radius;

		for (int y = 0; y < radius; y++) {
			int indexDst = output.startIndex + y * output.stride;
			int i = input.startIndex + y * input.stride;
			final int iEnd = i + imgWidth;

			int kStart = radius - y;

			int weight = 0;
			for (int k = kStart; k < kernelWidth; k++) {
				weight += dataKer[k];
			}

			for ( ; i < iEnd; i++) {
				int total = 0;
				int indexSrc = i - y * input.stride;
				for (int k = kStart; k < kernelWidth; k++, indexSrc += input.stride) {
					total += (dataSrc[indexSrc]) * dataKer[k];
				}
				dataDst[indexDst++] = (short)(total / weight);
			}
		}

		for (int y = yEnd; y < imgHeight; y++) {
			int indexDst = output.startIndex + y * output.stride;
			int i = input.startIndex + y * input.stride;
			final int iEnd = i + imgWidth;

			int kEnd = imgHeight - (y - radius);

			int weight = 0;
			for (int k = 0; k < kEnd; k++) {
				weight += dataKer[k];
			}

			for ( ; i < iEnd; i++) {
				int total = 0;
				int indexSrc = i - radius * input.stride;
				for (int k = 0; k < kEnd; k++, indexSrc += input.stride) {
					total += (dataSrc[indexSrc]) * dataKer[k];
				}
				dataDst[indexDst++] = (short)(total / weight);
			}
		}
	}

	public static void convolve(Kernel2D_I32 kernel, ImageSInt16 input, ImageInt16 output ) {
		final short[] dataSrc = input.data;
		final short[] dataDst = output.data;
		final int[] dataKer = kernel.data;

		final int radius = kernel.getRadius();
		final int kernelWidth = kernel.getWidth();

		final int width = input.getWidth();
		final int height = input.getHeight();

		// convolve across the left and right borders
		for (int y = 0; y < height; y++) {

			int minI = y >= radius ? -radius : -y;
			int maxI = y < height - radius ?  radius : height - y - 1;

			int indexDst = output.startIndex + y* output.stride;

			for( int x = 0; x < radius; x++ ) {

				int total = 0;
				int totalWeight = 0;

				for( int i = minI; i <= maxI; i++ ) {
					int indexSrc = input.startIndex + (y+i)* input.stride+x;
					int indexKer = (i+radius)*kernelWidth;

					for( int j = -x; j <= radius; j++ ) {
						int w = dataKer[indexKer+j+radius];
						totalWeight += w;
						total += (dataSrc[indexSrc+j]) * w;
					}
				}

				dataDst[indexDst++] = (short)(total / totalWeight);
			}

			indexDst = output.startIndex + y* output.stride + width-radius;
			for( int x = width-radius; x < width; x++ ) {

				int maxJ = width-x-1;

				int total = 0;
				int totalWeight = 0;

				for( int i = minI; i <= maxI; i++ ) {
					int indexSrc = input.startIndex + (y+i)* input.stride+x;
					int indexKer = (i+radius)*kernelWidth;

					for( int j = -radius; j <= maxJ; j++ ) {
						int w = dataKer[indexKer+j+radius];
						totalWeight += w;
						total += (dataSrc[indexSrc+j]) * w;
					}
				}

				dataDst[indexDst++] = (short)(total / totalWeight);
			}
		}

		// convolve across the top border while avoiding convolving the corners again
		for (int y = 0; y < radius; y++) {

			int indexDst = output.startIndex + y* output.stride+radius;

			for( int x = radius; x < width-radius; x++ ) {

				int total = 0;
				int totalWeight = 0;

				for( int i = -y; i <= radius; i++ ) {
					int indexSrc = input.startIndex + (y+i)* input.stride+x;
					int indexKer = (i+radius)*kernelWidth;

					for( int j = -radius; j <= radius; j++ ) {
						int w = dataKer[indexKer+j+radius];
						totalWeight += w;
						total += (dataSrc[indexSrc + j]) * w;
					}
				}
				dataDst[indexDst++] = (short)(total / totalWeight);
			}
		}

		// convolve across the bottom border
		for (int y = height-radius; y < height; y++) {

			int maxI = height - y - 1;
			int indexDst = output.startIndex + y* output.stride+radius;

			for( int x = radius; x < width-radius; x++ ) {

				int total = 0;
				int totalWeight = 0;

				for( int i = -radius; i <= maxI; i++ ) {
					int indexSrc = input.startIndex + (y+i)* input.stride+x;
					int indexKer = (i+radius)*kernelWidth;

					for( int j = -radius; j <= radius; j++ ) {
						int w = dataKer[indexKer+j+radius];
						totalWeight += w;
						total += (dataSrc[indexSrc + j]) * w;
					}
				}
				dataDst[indexDst++] = (short)(total / totalWeight);
			}
		}
	}

}