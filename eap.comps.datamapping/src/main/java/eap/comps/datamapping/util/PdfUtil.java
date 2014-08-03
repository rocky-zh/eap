package eap.comps.datamapping.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.WritableDirectElement;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.tool.xml.ElementList;
import com.itextpdf.tool.xml.Pipeline;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFilesImpl;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.TagProcessorFactory;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.ElementHandlerPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;

import eap.util.BeanUtil;
import eap.util.ReflectUtil;
import eap.util.StringUtil;

/**
 * <p> Title: </p>
 * <p> Description: </p>
 * @作者 chiknin@gmail.com
 * @创建时间 
 * @版本 1.00
 * @修改记录
 * <pre>
 * 版本       修改人         修改时间         修改内容描述
 * ----------------------------------------
 * 
 * ----------------------------------------
 * </pre>
 */
public class PdfUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(PdfUtil.class);
	
	// @see com.itextpdf.text.pdf.PdfWriter
	private static final Map<String, Integer> PDF_PERMISSIONS = new HashMap<String, Integer>();
	static {
		PDF_PERMISSIONS.put("printing", 4 + 2048);
		PDF_PERMISSIONS.put("modify_contents", 8);
		PDF_PERMISSIONS.put("copy", 16);
		PDF_PERMISSIONS.put("modify_annotations", 32);
		PDF_PERMISSIONS.put("fill_in", 256);
		PDF_PERMISSIONS.put("screenreaders", 512);
		PDF_PERMISSIONS.put("assembly", 1024);
		PDF_PERMISSIONS.put("degraded_printing", 4);
	}
	
	public static BaseFont FONT_STSONGSTD_LIGHT;
	static {
		try {
			FONT_STSONGSTD_LIGHT = BaseFont.createFont("STSongStd-Light","UniGB-UCS2-H", false);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
	}
	public static XMLWorkerFontProvider ZH_FONT_PROVIDER = new XMLWorkerFontProvider() {
		@Override
		public Font getFont(String fontname, String encoding, boolean embedded, float size, int style, BaseColor color) {
			return new Font(FONT_STSONGSTD_LIGHT, size, style, color);
		}
	};
	
	public static void concatenate(Document doc, PdfWriter writer, PdfReader reader) throws DocumentException, IOException {
		PdfContentByte cb = writer.getDirectContent();
		
		for (int i = 0; i < reader.getNumberOfPages();) {
			doc.newPage();
			PdfImportedPage page = writer.getImportedPage(reader, ++i);
			cb.addTemplate(page, 0, 0);
		}
		writer.flush();
	}
	public static void concatenate(Document doc, PdfWriter writer, String[] pdfStreams) throws DocumentException, IOException {
		PdfContentByte cb = writer.getDirectContent();
		
		PdfReader reader = null;
		for (int i = 0; i < pdfStreams.length; i++) {
			reader = new PdfReader(pdfStreams[i]);
			for (int j = 0; j < reader.getNumberOfPages();) {
				doc.newPage();
				PdfImportedPage page = writer.getImportedPage(reader, ++j);
				cb.addTemplate(page, 0, 0);
			}
			writer.flush();
		}
	}
	public static void concatenate(Document doc, PdfWriter writer, InputStream[] pdfStreams) throws DocumentException, IOException {
//		PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream("test5.pdf"));
//		doc.open();  
		PdfContentByte cb = writer.getDirectContent();
		
		PdfReader reader = null;
		for (int i = 0; i < pdfStreams.length; i++) {
			reader = new PdfReader(pdfStreams[i]);
//			try {
				for (int j = 0; j < reader.getNumberOfPages();) {
					doc.newPage();
					PdfImportedPage page = writer.getImportedPage(reader, ++j);
					cb.addTemplate(page, 0, 0);
				}
//			} finally {
//				reader.close();
//			}
			writer.flush();
		}
		
//		writer.close();
//		doc.close();
	}
	public static void concatenate(InputStream[] pdfStreams, OutputStream concatenatedStream) throws DocumentException, IOException {
		Document doc = new Document();
		try {
			concatenate(doc, pdfStreams, concatenatedStream);
		} finally {
			doc.close();
		}
	}
	public static void concatenate(Document doc, InputStream[] pdfStreams, OutputStream concatenatedStream) throws DocumentException, IOException {
		PdfCopy copy = new PdfCopy(doc, concatenatedStream);
		doc.open();
		PdfReader reader = null;
		int n = 0;
		for (int i = 0; i < pdfStreams.length; i++) {
			reader = new PdfReader(pdfStreams[i]);
			try {
				n = reader.getNumberOfPages();
				for (int page = 0; page < n; ) {
					copy.addPage(copy.getImportedPage(reader, ++page));
				}
			} finally {
				copy.freeReader(reader);
				reader.close();
			}
		}
	}
	
	public static void parseXHtml(InputStream htmlFileStream, String encoding, PdfWriter writer, Document doc) throws IOException {
		XMLWorkerHelper.getInstance().parseXHtml(writer, doc, htmlFileStream, null, Charset.forName(encoding), ZH_FONT_PROVIDER);
	}
	public static List<Element> parseXHtml(InputStream htmlFileStream, String encoding) throws IOException { // TODO font
		ElementList elList = new ElementList();
		
		CssFilesImpl cssFiles = new CssFilesImpl();
		cssFiles.add(XMLWorkerHelper.getInstance().getDefaultCSS());
		StyleAttrCSSResolver cssResolver = new StyleAttrCSSResolver(cssFiles);
		HtmlPipelineContext hpc = new HtmlPipelineContext(new CssAppliersImpl(ZH_FONT_PROVIDER));
		hpc.setAcceptUnknown(true).autoBookmark(true).setTagFactory((TagProcessorFactory)ReflectUtil.invokeMethod(XMLWorkerHelper.getInstance(), "getDefaultTagProcessorFactory", null));
		Pipeline<?> pipeline = new CssResolverPipeline(cssResolver, new HtmlPipeline(hpc, new ElementHandlerPipeline(elList, null)));
		XMLWorker worker = new XMLWorker(pipeline, true);
		XMLParser p = new XMLParser(true, Charset.forName(encoding));
		p.addListener(worker);
		p.parse(htmlFileStream);
		
		List<Element> result = new ArrayList<Element>();
		for (Element el : elList) {
			if (el instanceof LineSeparator || el instanceof WritableDirectElement) {
				continue;
			}
			result.add(el);
		}
		return result;
	}
	
	public static void encrypt(String src, String dest, String userPassword, String ownerPassword) throws IOException, DocumentException {
		PdfReader reader = null;
		PdfStamper stamper = null;
		try {
			reader = new PdfReader(src);
			stamper = new PdfStamper(reader, new FileOutputStream(dest));
			stamper.setEncryption(userPassword.getBytes(), ownerPassword.getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128 | PdfWriter.DO_NOT_ENCRYPT_METADATA);
		} finally {
			if (stamper != null) {
				stamper.close();
			}
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	public static void decrypt(String src, String dest, String ownerPassword) throws IOException, DocumentException {
		PdfReader reader = null;
		PdfStamper stamper = null;
		try {
			reader = new PdfReader(src, ownerPassword.getBytes());
			stamper = new PdfStamper(reader, new FileOutputStream(dest));
		} finally {
			if (stamper != null) {
				stamper.close();
			}
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	public static void fillForm(AcroFields form, Object data) throws DocumentException, IOException {
		form.setGenerateAppearances(true);
		Map dataMap = BeanUtil.toMap(data);
		String key;
		Object value;
		for (Object keyObj : dataMap.keySet()) {
			key = keyObj.toString();
			value = dataMap.get(keyObj);
			if (value == null) {
				continue;
			}
			
			if (value instanceof Map) {
				Map valueMap = (Map) value;
				
				String[] selection = null;
				String[] exportValues = new String[valueMap.size()];
				String[] displayValues = new String[valueMap.size()];
				int i = 0;
				for (Object vKeyObj : valueMap.keySet()) {
					Object vValue = valueMap.get(vKeyObj);
					if ("_SELECTION_".equals(vKeyObj.toString()) && vValue instanceof String[]) {
						selection = (String[]) vValue;
					} else {
						exportValues[i] = vKeyObj.toString();
						displayValues[i] = vValue.toString();
						i++;
					}
				}
				form.setListOption(key, exportValues, displayValues);
				if (selection != null) {
					form.setListSelection(key, selection);
				}
			} else {
//				form.setField(key, value.toString());
				form.setFieldRichValue(key, value.toString());
			}
		}
	}
	
	public static int toPermissionsAsInt(String permissions) {
		int permissionsAsInt = 0;
		if (StringUtil.isNotBlank(permissions)) {
			String[] permArray = permissions.split(",");
			for (String perm : permArray) {
				String key = perm.toLowerCase();
				if (PDF_PERMISSIONS.containsKey(key)) {
					permissionsAsInt |= PDF_PERMISSIONS.get(key);
				}
			}
		}
		return permissionsAsInt;
	}
	
	public static PdfPageEventHelper setPageHeadAndFooter = new PdfPageEventHelper() {
		public void onEndPage(PdfWriter writer, Document document) {
			PdfContentByte cb = writer.getDirectContent();  
			cb.saveState();  
			cb.beginText(); 
			
			BaseFont bf = FONT_STSONGSTD_LIGHT;
			cb.setFontAndSize(bf, 10);  
			
			float x = document.top(-20); // Header
			cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "H-Left", document.left(), x, 0); //左
			cb.showTextAligned(PdfContentByte.ALIGN_CENTER, writer.getPageNumber()+ " page", (document.right() + document.left()) / 2, x, 0); //中
			cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, "H-Right", document.right(), x, 0); //右
			
			float y = document.bottom(-20); // Footer
			cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "F-Left", document.left(), y, 0); //左
			cb.showTextAligned(PdfContentByte.ALIGN_CENTER, writer.getPageNumber()+" page", (document.right() + document.left()) / 2, y, 0); //中
			cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, "F-Right", document.right(), y, 0); //右  
			
			cb.endText();  
			cb.restoreState(); 
		};
	};
}