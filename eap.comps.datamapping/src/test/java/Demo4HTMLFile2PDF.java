import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Section;
import com.itextpdf.text.WritableDirectElement;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.tool.xml.ElementHandler;
import com.itextpdf.tool.xml.Writable;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.pipeline.WritableElement;

import eap.comps.datamapping.util.PdfUtil;
import eap.util.FileUtil;

/**
 * HTML文件转换为PDF
 *
 * @author <a href="http://www.micmiu.com">Michael Sun</a>
 */
public class Demo4HTMLFile2PDF {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		 System.out.println("1");
		
		String htmlFile = "test.html";

		// 直接把HTML文件转为PDF文件
		String pdfFile = "test.pdf";
//		Demo4HTMLFile2PDF.parseHTML2PDFFile(pdfFile, new FileInputStream(
//				htmlFile));
//
////		// HTML文件转为PDF中的Elements
		String pdfFile2 = "test3.pdf";
		Demo4HTMLFile2PDF.parseHTML2PDFElement(pdfFile2, new FileInputStream(
				htmlFile));
		
		
		
////		// step 1
//        Document document = new Document();
//        System.out.println("3");
////        // step 2
//        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile2));
//        document.setPageSize(PageSize.A4);
//        document.setMargins(36, 72, 108, 180);
//        document.setMarginMirroring(true);
//        
//        writer.setPageEvent(PdfUtil.setPageHeadAndFooter);
//        System.out.println("2");
////        // step 3
//        document.open();
////        // step 4
//        document.add(new Paragraph(
//            "The left margin of this odd page is 36pt (0.5 inch); " +
//            "the right margin 72pt (1 inch); " +
//            "the top margin 108pt (1.5 inch); " +
//            "the bottom margin 180pt (2.5 inch)."));
//        
////        Paragraph paragraph = new Paragraph();
////        paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
////        for (int i = 0; i < 20; i++) {
////            paragraph.add("Hello World! Hello People! " +
////            		"Hello Sky! Hello Sun! Hello Moon! Hello Stars!");
////        }
////        document.add(paragraph);
////        document.add(new Paragraph(
////            "The right margin of this even page is 36pt (0.5 inch); " +
////            "the left margin 72pt (1 inch)."));
////        // step 5
//        
//        long s = System.currentTimeMillis();
//        PdfUtil.concatenate(document, writer, new FileInputStream[] {
//    			new FileInputStream(pdfFile),
//    			new FileInputStream(pdfFile)
//    		});
//        long e = System.currentTimeMillis();
//        System.out.println(e - s);
//        
//        document.close();
//        writer.close();
	}

	/**
	 * 用于HTML直接转换为PDF文件
	 *
	 * @param fileName
	 * @throws Exception
	 */
	public static void parseHTML2PDFFile(String pdfFile,
			InputStream htmlFileStream) throws Exception {

		BaseFont bfCN =BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		// 中文字体定义
		Font chFont = new Font(bfCN, 12, Font.NORMAL, BaseColor.BLUE);
		Font secFont = new Font(bfCN, 12, Font.NORMAL, new BaseColor(0, 204,
				255));
		
		ByteArrayOutputStream ss = new ByteArrayOutputStream();

		Document document = new Document();
		PdfWriter pdfwriter = PdfWriter.getInstance(document,
				ss);
//		pdfwriter.setViewerPreferences(PdfWriter.HideToolbar);
//		pdfwriter.setPageEmpty(false);
//		pdfwriter.setPageEvent();
		
		document.open();

		int chNum = 1;
		Chapter chapter = new Chapter(new Paragraph("HTML文件转PDF测试", chFont),
				chNum++);

		Section section = chapter.addSection(new Paragraph("/dev/null 2>&1 详解",
				secFont));
		// section.setNumberDepth(2);
		// section.setBookmarkTitle("基本信息");
		section.setIndentation(10);
		section.setIndentationLeft(10);
		section.setBookmarkOpen(false);
		section.setNumberStyle(Section.NUMBERSTYLE_DOTTED_WITHOUT_FINAL_DOT);
		section.add(Chunk.NEWLINE);
		document.add(chapter);
		
		document.newPage();document.newPage();
		
		// html文件
		InputStreamReader isr = new InputStreamReader(htmlFileStream, "UTF-8");
		
//		PdfUtil.parseXHtml(isr, pdfwriter, document);
		
		// 方法一：默认参数转换
		XMLWorkerHelper.getInstance().parseXHtml(pdfwriter, document, isr);

		// 方法二：可以自定义参数
		// HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
		// htmlContext.charSet(Charset.forName("UTF-8"));
		// htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
		// CSSResolver cssResolver = XMLWorkerHelper.getInstance()
		// .getDefaultCssResolver(true);
		// Pipeline<?> pipeline = new CssResolverPipeline(cssResolver,
		// new HtmlPipeline(htmlContext, new PdfWriterPipeline(document,
		// pdfwriter)));
		// XMLWorker worker = new XMLWorker(pipeline, true);
		// XMLParser p = new XMLParser();
		// p.addListener(worker);
		//
		// p.parse(isr);
		// p.flush();

		document.close();
		
		FileUtil.writeByteArrayToFile(new File("y.pdf"), ss.toByteArray());
	}

	/**
	 * HTML文件转为PDF中的Elements,便于把HTML内容追加到已有的PDF中
	 *
	 * @param pdfFile
	 * @param htmlFileStream
	 */
	public static void parseHTML2PDFElement(String pdfFile,
			InputStream htmlFileStream) {
		try {
			Document document = new Document(PageSize.A4);

			FileOutputStream outputStream = new FileOutputStream(pdfFile);
			PdfWriter pdfwriter = PdfWriter.getInstance(document, outputStream);
			// pdfwriter.setViewerPreferences(PdfWriter.HideToolbar);
			pdfwriter.setEncryption("abc".getBytes(), "123".getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.STANDARD_ENCRYPTION_128);
//			pdfwriter.createXmpMetadata();
			document.open();
			
			BaseFont bfCN = BaseFont.createFont("STSongStd-Light",
					"UniGB-UCS2-H", false);
			// 中文字体定义
			Font chFont = new Font(bfCN, 12, Font.NORMAL, BaseColor.BLUE);
			Font secFont = new Font(bfCN, 12, Font.NORMAL, new BaseColor(0,
					204, 255));
			Font textFont = new Font(bfCN, 12, Font.NORMAL, BaseColor.BLACK);

			int chNum = 1;
			Chapter chapter = new Chapter(new Paragraph(
					"HTML文件转PDF元素，便于追加其他内容", chFont), chNum++);

			Section section = chapter.addSection(new Paragraph(
					"/dev/null 2>&1 详解", secFont));

			section.setIndentation(10);
			section.setIndentationLeft(10);
			section.setBookmarkOpen(false);
			section.setNumberStyle(Section.NUMBERSTYLE_DOTTED_WITHOUT_FINAL_DOT);
			section.add(Chunk.NEWLINE);

//			final List<Element> pdfeleList = new ArrayList<Element>();
//			ElementHandler elemH = new ElementHandler() {
//
//				public void add(final Writable w) {
//					if (w instanceof WritableElement) {
//						pdfeleList.addAll(((WritableElement) w).elements());
//					}
//
//				}
//				
//			};
//			InputStreamReader isr = new InputStreamReader(htmlFileStream,
//					"UTF-8");
//			XMLWorkerHelper.getInstance().parseXHtml(elemH, isr);
			
			
			final List<Element> pdfeleList = PdfUtil.parseXHtml(htmlFileStream, "UTF-8");
			
			List<Element> list = new ArrayList<Element>();
			for (Element ele : pdfeleList) {
				if (ele instanceof LineSeparator
						|| ele instanceof WritableDirectElement) {
					System.out.println("11111111");
					continue;
				}
				list.add(ele);
			}
			section.addAll(list);

			section = chapter.addSection(new Paragraph("继续添加章节", secFont));

			section.setIndentation(10);
			section.setIndentationLeft(10);
			section.setBookmarkOpen(false);
			section.setNumberStyle(Section.NUMBERSTYLE_DOTTED_WITHOUT_FINAL_DOT);
			section.add(new Chunk("测试HTML转为PDF元素，方便追加其他内容", textFont));

			document.add(chapter);
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void mergePdf(String pdfFile, InputStream is1, InputStream is2) {
		
	}
}