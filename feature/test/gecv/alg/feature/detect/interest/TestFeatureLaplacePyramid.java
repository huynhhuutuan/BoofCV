/*
 * Copyright 2011 Peter Abeles
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package gecv.alg.feature.detect.interest;

import gecv.abst.filter.ImageFunctionSparse;
import gecv.abst.filter.derivative.AnyImageDerivative;
import gecv.alg.transform.gss.ScaleSpacePyramid;
import gecv.alg.transform.gss.UtilScaleSpace;
import gecv.core.image.inst.FactoryImageGenerator;
import gecv.factory.filter.derivative.FactoryDerivativeSparse;
import gecv.struct.image.ImageFloat32;
import jgrl.struct.point.Point2D_I32;

import java.util.List;


/**
 * @author Peter Abeles
 */
public class TestFeatureLaplacePyramid extends GenericFeatureScaleDetector {

	public TestFeatureLaplacePyramid() {
		// just make one of these tests work
		scaleTolerance = 2.5;
	}

	@Override
	protected Object createDetector( GeneralFeatureDetector<ImageFloat32, ImageFloat32> detector ) {

		ImageFunctionSparse<ImageFloat32> sparseLaplace = FactoryDerivativeSparse.createLaplacian(ImageFloat32.class,null);
		AnyImageDerivative<ImageFloat32,ImageFloat32> deriv = UtilScaleSpace.createDerivatives(ImageFloat32.class, FactoryImageGenerator.create(ImageFloat32.class));

		return new FeatureLaplacePyramid<ImageFloat32,ImageFloat32>(detector,sparseLaplace,deriv,1);
	}

	@Override
	protected List<Point2D_I32> detectFeature(ImageFloat32 input,  double[] scales , Object detector) {

		ScaleSpacePyramid<ImageFloat32> ss = new ScaleSpacePyramid<ImageFloat32>(ImageFloat32.class,scales);
		ss.update(input);

		FeatureLaplacePyramid<ImageFloat32,ImageFloat32> alg = (FeatureLaplacePyramid<ImageFloat32,ImageFloat32>)detector;
		alg.detect(ss);

		return (List)alg.getInterestPoints();
	}

}