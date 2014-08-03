package eap.comps.datamapping.factory.object;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import eap.comps.datamapping.definition.DataMappingDefinition;
import eap.comps.datamapping.definition.RendererDefinition;
import eap.comps.datamapping.definition.SectionDefinition;
import eap.comps.datamapping.definition.parser.ParserDefinition;
import eap.comps.datamapping.definition.parser.PojoToPdfParserDefinition;
import eap.comps.datamapping.factory.IDataMappingFactory;
import eap.comps.datamapping.factory.object.template.ITemplateEngine;
import eap.comps.datamapping.util.PdfUtil;
import eap.util.FileUtil;
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
public class PojoToPdfMapping extends AbstractDataMapping<Object, byte[]> {
	
	private ITemplateEngine tplEngine;
	
	private String basePath = "";
	
	@Override
	protected byte[] mappingImpl(String name, Object source, Map<String, Object> params,
		DataMappingDefinition dmd, ParserDefinition pd) 
	{
		PojoToPdfParserDefinition ptppd = (PojoToPdfParserDefinition) pd;
		List<SectionDefinition> sections = ptppd.getSections();
		String encoding = ptppd.getEncoding();
		
		OutputStream outputStream = (OutputStream) params.get("outputStream");
		boolean autoCreateOuputStream = false;
		if (outputStream == null) {
			outputStream = new ByteArrayOutputStream();
			autoCreateOuputStream = true;
		}
		
		Document doc = this.createDocument(ptppd);
		PdfWriter writer = this.getPdfWriter(doc, outputStream, ptppd);
		
		this.render(new Object[] {doc, writer}, ptppd, ptppd.getRenderers(), RendererDefinition.PHASE_BEFORE);
		doc.open();
		
		try {
			for (SectionDefinition section : sections) {
				String file = section.getFile();
				if (StringUtil.isBlank(file)) {
					continue;
				}
				
				if (SectionDefinition.RENDER_TYPE_HTML.equalsIgnoreCase(section.getRenderType())) {
//					Properties tplSettings = section.getProps(); // TODO encoding
					Properties tplSettings = new Properties();
					tplSettings.put("output_encoding", encoding);
					String xhtml = (String) tplEngine.process(file, source, tplSettings);
					
					PdfUtil.parseXHtml(new ByteArrayInputStream(xhtml.getBytes(encoding)), encoding, writer, doc);
				} 
				else if (SectionDefinition.RENDER_TYPE_FILL_FORM.equalsIgnoreCase(section.getRenderType())) {
					PdfReader reader = new PdfReader(new ClassPathResource(basePath + file).getInputStream());
					AcroFields form = reader.getAcroFields();
					if (form != null) {
						PdfUtil.fillForm(form, source);
					}
					PdfUtil.concatenate(doc, writer, reader);
				}
				else {
					PdfUtil.concatenate(doc, writer, new InputStream[] {new ClassPathResource(basePath + file).getInputStream()});
				}
				
				if (section.isNewPage()) {
					doc.newPage();
				}
			}
			
			this.render(new Object[] {doc, writer}, ptppd, ptppd.getRenderers(), RendererDefinition.PHASE_AFTER);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		} finally {
			try {
				doc.close();
			} catch (Exception e) {}
			try {
				writer.close();
			} catch (Exception e) {}
		}
		
		return autoCreateOuputStream ? ((ByteArrayOutputStream) outputStream).toByteArray() : null; // outputStream;
	}
	
	private PdfWriter getPdfWriter(Document doc, OutputStream outputStream, PojoToPdfParserDefinition ptppd) {
		try {
			PdfWriter writer = PdfWriter.getInstance(doc, outputStream);
			
			if (StringUtil.isNotBlank(ptppd.getUserPassword()) && StringUtil.isNotBlank(ptppd.getOwnerPassword())) {
				String encoding = ptppd.getEncoding();
				String userPassword = ptppd.getUserPassword();
				String ownerPassword = ptppd.getOwnerPassword();
				String permissions = ptppd.getPermissions();
				int permissionsAsInt = StringUtil.isNotBlank(permissions) ? PdfUtil.toPermissionsAsInt(permissions) : PdfWriter.ALLOW_PRINTING;
				
				writer.setEncryption(userPassword.getBytes(encoding), ownerPassword.getBytes(encoding), permissionsAsInt, PdfWriter.ENCRYPTION_AES_128 | PdfWriter.DO_NOT_ENCRYPT_METADATA);
			}
			writer.createXmpMetadata();
			
			return writer;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	private Document createDocument(PojoToPdfParserDefinition ptppd) {
		Document doc = new Document();
		
		if (StringUtil.isNotBlank(ptppd.getPageRectangle())) {
			doc.setPageSize(PageSize.getRectangle(ptppd.getPageRectangle()));
		}
		if (ptppd.getMarginsAsFloat() != null) {
			doc.setMargins(ptppd.getMarginsAsFloat()[3], ptppd.getMarginsAsFloat()[1], ptppd.getMarginsAsFloat()[0], ptppd.getMarginsAsFloat()[2]);
		}
		if (StringUtil.isNotBlank(ptppd.getTitle())) {
			doc.addTitle(ptppd.getTitle());
		}
		if (StringUtil.isNotBlank(ptppd.getAuthor())) {
			doc.addAuthor(ptppd.getAuthor());
		}
		if (StringUtil.isNotBlank(ptppd.getSubject())) {
			doc.addSubject(ptppd.getSubject());
		}
		if (StringUtil.isNotBlank(ptppd.getKeywords())) {
			doc.addKeywords(ptppd.getKeywords());
		}
		if (StringUtil.isNotBlank(ptppd.getCreator())) {
			doc.addCreator(ptppd.getCreator());
		}
		if (ptppd.isCreationDate()) {
			doc.addCreationDate();
		}
		if (ptppd.isProducer()) {
			doc.addProducer();
		}
		
		return doc;
	}
	
	@Override
	public void setDataMappingFactory(IDataMappingFactory dataMappingFactory) {
		super.setDataMappingFactory(dataMappingFactory);
		this.basePath = dataMappingFactory.getConstantValue("Env.basePath", "");
		this.initTplEngine();
	}
	
	private void initTplEngine() {
		if (tplEngine == null) {
			tplEngine = ((PojoToTemplateMapping)dataMappingFactory.getDataMappingByParserType("PojoToTemplate")).getTplEngine();
		}
	}
}