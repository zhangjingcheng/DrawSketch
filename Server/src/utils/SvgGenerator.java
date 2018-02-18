package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.dom.GenericDOMImplementation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SvgGenerator {

	public static final int RESULT_OK = 0;
	public static final int RESULT_EXCEPTION = -1;
	public static final int RESULT_ERROR = -2;

	public static int outputLines(List<List<Double>> datax, List<List<Double>> datay, String outputPath) {
		if (datax.size() != datay.size()) {
			return RESULT_ERROR;
		}
		DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
		// Create an instance of org.w3c.dom.Document.
		String svgNS = "http://www.w3.org/2000/svg";
		Document document = domImpl.createDocument(svgNS, "svg", null);
		// Create an instance of the SVG Generator.
		Element svgRoot = document.getDocumentElement();
		svgRoot.setAttributeNS(null, "width", "1080");
		svgRoot.setAttributeNS(null, "height", "1920");
		for (int i = 0; i < datax.size(); i++) {
			List<Double> linex = datax.get(i);
			List<Double> liney = datay.get(i);
			if (linex.size() == liney.size() && linex.size() > 1) {
				Element path = document.createElementNS(svgNS, "path");
				path.setAttributeNS(null, "d", pathGenerator(linex, liney));
				path.setAttributeNS(null, "style", "fill:white;stroke:black;stroke-width:2.5");
				svgRoot.appendChild(path);
			} else {
				return RESULT_ERROR;
			}
		}
		try {
			TransformerFactory transFactory = TransformerFactory.newInstance();  
	        Transformer transFormer = transFactory.newTransformer();  
	        transFormer.setOutputProperty(OutputKeys.ENCODING, "GB2312");  
	        DOMSource domSource = new DOMSource(document);  
	        File file = new File(outputPath);
	        file.createNewFile();
			Writer out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			StreamResult result = new StreamResult(out);
			transFormer.transform(domSource, result);  
		}catch(Exception e) {
			e.printStackTrace();
			return RESULT_EXCEPTION;
		}
		return RESULT_OK;
	}

	private static String pathGenerator(List<Double> linex, List<Double> liney) {
		StringBuffer sb = new StringBuffer();
		sb.append("M");
		sb.append(String.valueOf(linex.get(0)) + " " + String.valueOf(liney.get(0)) + " ");
		if (linex.size() == 2) {
			sb.append("L" + String.valueOf(linex.get(1)) + " " + String.valueOf(liney.get(1)) + " ");
			return sb.toString();
		} else if (linex.size() == 1) {
			return sb.toString();
		}
		for (int i = 2; i < linex.size(); i++) {
			double x1 = linex.get(i - 1);
			double y1 = liney.get(i - 1);
			double x2 = linex.get(i);
			double y2 = liney.get(i);
			sb.append( "C" + String.valueOf(x1) + " " + String.valueOf(y1) + " " + String.valueOf(x2) + " "
					+ String.valueOf(y2) + " " + String.valueOf(x2) + " " + String.valueOf(y2) + " ");
		}
		return sb.toString();
	}
}
