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

package boofcv.alg.feature.orientation.impl;

import boofcv.misc.AutoTypeImage;
import boofcv.misc.CodeGeneratorBase;

import java.io.FileNotFoundException;


/**
 * @author Peter Abeles
 */
public class GenerateImplOrientationImageAverage extends CodeGeneratorBase {

	AutoTypeImage imageType;

	@Override
	public void generate() throws FileNotFoundException {
		printClass(AutoTypeImage.F32);
		printClass(AutoTypeImage.U8);
	}

	private void printClass( AutoTypeImage imageType ) throws FileNotFoundException {
		this.imageType = imageType;
		setOutputFile("ImplOrientationImageAverage_"+imageType.getAbbreviatedType());
		printPreamble();
		printFunctions();

		out.print("}\n");
	}

	private void printPreamble() {
		String type = imageType.getImageName();

		out.print("import boofcv.alg.feature.orientation.OrientationImageAverage;\n" +
				"import boofcv.struct.image."+type+";\n" +
				"\n" +
				"\n" +
				"/**\n" +
				" *\n" +
				" * <p>\n" +
				" * Implementation of {@link boofcv.alg.feature.orientation.OrientationImageAverage} for a specific image type.\n" +
				" * </p>\n" +
				" *\n" +
				" * <p>\n" +
				" * WARNING: Do not modify.  Automatically generated by {@link GenerateImplOrientationImageAverage}.\n" +
				" * </p>\n" +
				" *\n" +
				" * @author Peter Abeles\n" +
				" */\n" +
				"public class "+className+" extends OrientationImageAverage<"+type+"> {\n" +
				"\n" +
				"\tpublic "+className+"(int radius) {\n" +
				"\t\tsuper(radius);\n" +
				"\t}\n" +
				"\n\n" );
	}

	private void printFunctions() {
		printWeighted();
	}


	private void printWeighted() {
		out.print("\t@Override\n" +
				"\tpublic double computeAngle( int c_x , int c_y ) {\n" +
				"\n" +
				"\t\tdouble sumX=0,sumY=0;\n" +
				"\n" +
				"\t\tfor( int y = rect.y0; y < rect.y1; y++ ) {\n" +
				"\t\t\tint index = image.startIndex + image.stride*y + rect.x0;\n" +
				"\t\t\tint indexW = (y-c_y+radiusScale)*kerCosine.width + rect.x0-c_x+radiusScale;\n" +
				"\n" +
				"\t\t\tfor( int x = rect.x0; x < rect.x1; x++ , index++ , indexW++ ) {\n" +
				"\t\t\t\t"+imageType.getSumType()+" val = image.data[index]"+imageType.getBitWise()+";\n" +
				"\t\t\t\tsumX += kerCosine.data[indexW]*val;\n" +
				"\t\t\t\tsumY += kerSine.data[indexW]*val;\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\n" +
				"\t\treturn Math.atan2(sumY,sumX);\n" +
				"\t}\n" +
				"\n" +
				"\tpublic Class<"+imageType.getImageName()+"> getImageType() {\n" +
				"\t\treturn "+imageType.getImageName()+".class;\n" +
				"\t}\n\n");
	}

	public static void main( String args[] ) throws FileNotFoundException {
		GenerateImplOrientationImageAverage app = new GenerateImplOrientationImageAverage();
		app.generate();
	}
}