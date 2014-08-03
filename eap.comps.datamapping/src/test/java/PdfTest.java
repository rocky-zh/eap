
//import org.xhtmlrenderer.pdf.ITextFontResolver;
//import org.xhtmlrenderer.pdf.ITextRenderer;
//
//import com.lowagie.text.pdf.BaseFont;

//import org.apache.pdfbox.exceptions.COSVisitorException;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.pdmodel.PDPage;
//import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
//import org.apache.pdfbox.pdmodel.font.PDFont;
//import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class PdfTest {
	public static void main(String[] args) throws Exception {
		
		
		
		
//		PDDocument document = new PDDocument();
//		PDPage page = new PDPage();
//		document.addPage(page);
//
//		// PDFont font = PDTrueTypeFont.loadTTF(document, new File("SIMSUN.TTC"));
//		PDFont font = PDType1Font.HELVETICA_BOLD;
//
//		PDPageContentStream contentStream = new PDPageContentStream(document,
//				page);
//		contentStream.beginText();
//		contentStream.setFont(font, 14);
//		contentStream.moveTextPositionByAmount(100, 700);
////		contentStream.drawString("Hello World");
//		 contentStream.drawString("中文");
//		contentStream.endText();
//
//		contentStream.close();
//
//		try {
//			document.save("test.pdf");
//		} catch (COSVisitorException e) {
//			e.printStackTrace();
//		}
//		document.close();
		
		
	}
	
	public boolean convertHtmlToPdf(String inputFile, String outputFile)  
		    throws Exception {  
		          
//		        OutputStream os = new FileOutputStream(outputFile);       
//		        ITextRenderer renderer = new ITextRenderer();       
//		        String url = new File(inputFile).toURI().toURL().toString();   
//		         
//		        renderer.setDocument(url);     
//		          
//		        // 解决中文支持问题       
//		        ITextFontResolver fontResolver = renderer.getFontResolver();      
//		        fontResolver.addFont("simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);       
//		        //解决图片的相对路径问题  
//		        renderer.getSharedContext().setBaseURL("/Users/fuumining/work/dev/java/eclipse-workspace/eap/eap.comps.datamapping");  
//		        renderer.layout();      
//		        renderer.createPDF(os);    
//		          
//		        os.flush();  
//		        os.close();  
		        return true;  
		    }  
}
