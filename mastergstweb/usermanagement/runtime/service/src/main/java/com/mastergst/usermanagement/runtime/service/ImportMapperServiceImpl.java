package com.mastergst.usermanagement.runtime.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.collect.Lists;
import com.mastergst.core.util.MasterGstExecutorService;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.TemplateMapper;
import com.mastergst.usermanagement.runtime.domain.TemplateMapperDoc;
import com.mastergst.usermanagement.runtime.repository.TemplateMapperDocRepository;
import com.mastergst.usermanagement.runtime.repository.TemplateMapperRepository;


/**
 * Service Impl class Import Mappers
 * 
 * @author BVM Consultancy Services(www.bvmcs.com)
 * @version 1.0
 */
@Service
public class ImportMapperServiceImpl implements ImportMapperService {
	
	private static final Logger log = LogManager.getLogger(ImportMapperServiceImpl.class.getName());
	
	@Autowired
	private TemplateMapperRepository templateMapperRepository;
	
	@Autowired
	private TemplateMapperDocRepository templateMapperDocRepository;
	
	@Autowired
	private MasterGstExecutorService executorService;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	private Map<String, GstWorkSheet> salesWorkBookDetails;
	private Map<String, GstWorkSheet> purchagesWorkBookDetails;
	private Map<String, GstWorkSheet> einvoiceWorkBookDetails;
	private Map<String, GstWorkSheet> salesWorkBookSimplifiedDetails;
	private Map<String, GstWorkSheet> purchagesWorkSimplifiedBookDetails;
	private Map<String, GstWorkSheet> einvoiceWorkBookSimplifiedDetails;
	
	public ImportMapperServiceImpl(){
	}
	
	@PostConstruct
	public void init(){
		executorService.execute(()->{
			salesWorkBookDetails = readTemplate("classpath:mastergst_excel_config_custom_import_mapper.xml");
			purchagesWorkBookDetails = readTemplate("classpath:mastergst_purchase_excel_config_custom_import_template.xml");
			einvoiceWorkBookDetails = readTemplate("classpath:mastergst_einvoice_excel_config_custom_import_mapper.xml");
			salesWorkBookSimplifiedDetails = readTemplate("classpath:mastergst_excel_config_simplified.xml");
			purchagesWorkSimplifiedBookDetails = readTemplate("classpath:mastergst_purchase_excel_config_simplified.xml");
			einvoiceWorkBookSimplifiedDetails = readTemplate("classpath:mastergst_einvoice_excel_config_simplified.xml");
		});
	}
		
		
	private Map<String, GstWorkSheet> readTemplate(String path){
		try {
			//String xmlPath = "classpath:mastergst_excel_config.xml";
			Resource config = resourceLoader.getResource(path);
	        SAXParserFactory factory = SAXParserFactory.newInstance();
	        SAXParser saxParser = factory.newSAXParser();
	        TemplateHandler salesHandler = new TemplateHandler();
	        saxParser.parse(config.getInputStream(), salesHandler);   
	        return  salesHandler.getWorkbookDetails();
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
		return null;
	}
	
	@Override
	public Map<String, GstWorkSheet> getSalesTemplateFields() {
		return salesWorkBookDetails;
	}
	
	@Override
	public Map<String, GstWorkSheet> getSalesTemplateFields(String simplifiedOrDetail) {
		if("Detailed".equalsIgnoreCase(simplifiedOrDetail)) {
			return salesWorkBookDetails;
		}else {
			return salesWorkBookSimplifiedDetails;
		}
	}
	
	public Set<String> getSalesTemplateAllPages() {
		return salesWorkBookDetails.keySet();
	}
	
	@Override
	public Set<String> getPurchagesTemplateAllPages() {
		return purchagesWorkBookDetails.keySet();
 	}
	
	@Override
	public Map<String, GstWorkSheet> getPurchageTemplateFields() {
		return purchagesWorkBookDetails;
	}
	
	@Override
	public Map<String, GstWorkSheet> getPurchageTemplateFields(String simplifiedOrDetail) {
		if("Detailed".equalsIgnoreCase(simplifiedOrDetail)) {
			return purchagesWorkBookDetails;
		}else {
			return purchagesWorkSimplifiedBookDetails;
		}
	}
	
	
	@Override
	public Map<String,Map<String,List<String>>> readMapperAndNonMapperFile(Map<String, MultipartFile> multipartData,int skipRows){
		
		Map<String,Map<String,List<String>>> mapperfieldsData = new HashMap<>();
		
		Map<String, List<String>> mapperData = new HashMap<>();
		Map<String, List<String>> nonMapperData = new HashMap<>();
		multipartData.values().forEach((MultipartFile file)->{
		try(InputStream inp = file.getInputStream();  Workbook workbook = WorkbookFactory.create(inp);){
			workbook.forEach((Sheet sh)->{
				mapperData.put(sh.getSheetName(), getSheetFields(sh,skipRows));
				
				/*for(int i=0; i<skipRows; i++) {
					nonMapperData.put(sh.getSheetName()+i, getSheetFields(sh,i));
				}*/
			});
			//getSheetFields(workbook.getSheetAt(0),skipRows);
	        
		}catch(Exception e){
			e.printStackTrace();
		}});
		mapperfieldsData.put("mapperFields", mapperData);
		mapperfieldsData.put("nonMapperFields", nonMapperData);
		
		return mapperfieldsData;
	}
	
	
	@Override
	public Map<String,List<String>> readMapperFile(Map<String, MultipartFile> multipartData,int skipRows){
		Map<String, List<String>> mapperData = new HashMap<>();
		multipartData.values().forEach((MultipartFile file)->{
		try(InputStream inp = file.getInputStream();  Workbook workbook = WorkbookFactory.create(inp);){
			workbook.forEach((Sheet sh)->{
				mapperData.put(sh.getSheetName(), getSheetFields(sh,skipRows));
				
				for(int i=0; i<skipRows; i++) {
					mapperData.put(sh.getSheetName()+i, getSheetFields(sh,i));
				}
			});
			//getSheetFields(workbook.getSheetAt(0),skipRows);
	        
		}catch(Exception e){
			e.printStackTrace();
		}});
		return mapperData;
	}
	
	@Override
	public Map<String,List<String>> readNonMapperFile(Map<String, MultipartFile> multipartData,int skipRows){
		Map<String, List<String>> mapperData = new HashMap<>();
		multipartData.values().forEach((MultipartFile file)->{
		try(InputStream inp = file.getInputStream();  Workbook workbook = WorkbookFactory.create(inp);){
			workbook.forEach((Sheet sh)->{
				for(int i=0; i<skipRows; i++) {
					mapperData.put(sh.getSheetName()+i, getSheetFields(sh,i));
				}
				
			});
				/*
				 * for(int j=0; j<skipRows; j++) { getSheetFields(workbook.getSheetAt(0),j); }
				 */
	        
		}catch(Exception e){
			e.printStackTrace();
		}});
		return mapperData;
	}
	
	private List<String> getSheetFields(Sheet detailsSheet,int skipRows){
		List<String> mapperFields = new ArrayList<>();
		Row rw = detailsSheet.getRow(skipRows);
		if(NullUtil.isNotEmpty(rw)) {
	    	Iterator<Cell> cellIterator = rw.iterator();
	        while (cellIterator.hasNext()) {
	        	Cell currentCell = cellIterator.next();
	        	mapperFields.add(currentCell.getStringCellValue());
	        }
		}
        return mapperFields;
	}
	
	public TemplateMapper saveMapper(TemplateMapper templateMapper, Map<String,List<String>> mapperPageFields){
		//removeMappedPages(templateMapper, mapperPageFields);
		boolean isValid = true;
		orderMapperConfig(templateMapper, mapperPageFields);
		templateMapper.setMapperPageFields(mapperPageFields);
		templateMapper.setIsCompleted(Boolean.toString(isValid));
		List<String> invTypes=Lists.newArrayList();
		if(NullUtil.isNotEmpty(templateMapper.getMapperConfig())){
			for(String key : templateMapper.getMapperConfig().keySet()) {
				String page = (String)templateMapper.getMapperConfig().get(key).get("page");
				if(NullUtil.isNotEmpty(templateMapper.getMapperConfig().get(key))
						&& NullUtil.isNotEmpty(templateMapper.getMapperConfig().get(key).get("mappings"))
						&& NullUtil.isNotEmpty(mapperPageFields)
						&& NullUtil.isNotEmpty(mapperPageFields.get(page))
						//&& (((Map)templateMapper.getMapperConfig().get(key).get("mappings")).values().size() >= (mapperPageFields.get(page).size()-3))
						) {
					invTypes.add(key);
				}
			}
		}
		//executorService.execute(()->{createTemplateMapperDoc(templateMapper, invTypes);});
		TemplateMapperDoc templateMapperDoc = createTemplateMapperDoc(templateMapper, invTypes);
		Map<String, Map> mapperConfig12 = templateMapper.getMapperConfig();
		mapperConfig12.forEach((String key, Map m)->{
			String page = (String)m.get("page");
			List<String> fields = mapperPageFields.get(page);
			Map<String, String> mappings = (Map)m.get("mappings"); 
			LinkedHashMap<String, String> config = new LinkedHashMap<>();
			if(NullUtil.isNotEmpty(fields)) {
				for(String field : fields) {
					Set<Entry<String, String>> keySet = mappings.entrySet();
					String removeKey = null;
					for(Entry<String, String> ent : keySet) {
						if(ent.getValue().split("#MGST#")[0].equals(field)) {
							config.put(ent.getKey(), ent.getValue().split("#MGST#")[0]);
							removeKey = ent.getKey();
							break;
						}
					}
					mappings.remove(removeKey);
				}
			}
			m.put("mappings", config); 
		});
		templateMapper.setMapperConfig(mapperConfig12);
		templateMapper.setTemplateMapperdocid(templateMapperDoc.getId().toString());
		TemplateMapper templateMapper2 = templateMapperRepository.save(templateMapper);
		return templateMapper2;
	}
	
	public TemplateMapper saveMapper(TemplateMapper templateMapper, Map<String,List<String>> mapperPageFields, Map<String,List<String>> nonMapperPageFields){
		//removeMappedPages(templateMapper, mapperPageFields);
		boolean isValid = true;
		orderMapperConfig(templateMapper, mapperPageFields);
		templateMapper.setMapperPageFields(mapperPageFields);
		templateMapper.setNonMapperPageFields(nonMapperPageFields);
		templateMapper.setIsCompleted(Boolean.toString(isValid));
		List<String> invTypes=Lists.newArrayList();
		if(NullUtil.isNotEmpty(templateMapper.getMapperConfig())){
			for(String key : templateMapper.getMapperConfig().keySet()) {
				String page = (String)templateMapper.getMapperConfig().get(key).get("page");
				if(NullUtil.isNotEmpty(templateMapper.getMapperConfig().get(key))
						&& NullUtil.isNotEmpty(templateMapper.getMapperConfig().get(key).get("mappings"))
						&& NullUtil.isNotEmpty(mapperPageFields)
						&& NullUtil.isNotEmpty(mapperPageFields.get(page))
						//&& (((Map)templateMapper.getMapperConfig().get(key).get("mappings")).values().size() >= (mapperPageFields.get(page).size()-3))
						) {
					invTypes.add(key);
				}
			}
		}
		//executorService.execute(()->{createTemplateMapperDoc(templateMapper, invTypes);});
		TemplateMapperDoc templateMapperDoc = createTemplateMapperDoc(templateMapper, invTypes);
		Map<String, Map> mapperConfig12 = templateMapper.getMapperConfig();
		mapperConfig12.forEach((String key, Map m)->{
			String page = (String)m.get("page");
			List<String> fields = mapperPageFields.get(page);
			Map<String, String> mappings = (Map)m.get("mappings"); 
			LinkedHashMap<String, String> config = new LinkedHashMap<>();
			if(NullUtil.isNotEmpty(fields)) {
				for(String field : fields) {
					Set<Entry<String, String>> keySet = mappings.entrySet();
					String removeKey = null;
					for(Entry<String, String> ent : keySet) {
						if(ent.getValue().split("#MGST#")[0].equals(field)) {
							config.put(ent.getKey(), ent.getValue().split("#MGST#")[0]);
							removeKey = ent.getKey();
							break;
						}
					}
					mappings.remove(removeKey);
				}
			}
			m.put("mappings", config); 
		});
		templateMapper.setMapperConfig(mapperConfig12);
		templateMapper.setTemplateMapperdocid(templateMapperDoc.getId().toString());
		TemplateMapper templateMapper2 = templateMapperRepository.save(templateMapper);
		return templateMapper2;
	}
	
	private void orderMapperConfig(TemplateMapper templateMapper, Map<String,List<String>> mapperPageFields){
		Map<String, Map> mapperConfig = templateMapper.getMapperConfig();
		mapperConfig.forEach((String key, Map m)->{
			String page = (String)m.get("page");
			List<String> fields = mapperPageFields.get(page);
			Map<String, String> mappings = (Map)m.get("mappings"); 
			LinkedHashMap<String, String> config = new LinkedHashMap<>();
			if(NullUtil.isNotEmpty(fields)) {
				for(String field : fields) {
					Set<Entry<String, String>> keySet = mappings.entrySet();
					String removeKey = null;
					for(Entry<String, String> ent : keySet) {
						if(ent.getValue().split("#MGST#")[0].equals(field)) {
							config.put(ent.getKey(), ent.getValue());
							removeKey = ent.getKey();
							break;
						}
					}
					mappings.remove(removeKey);
				}
			}
			m.put("mappings", config); 
		});
	}
	
	private void removeMappedPages(TemplateMapper templateMapper, Map<String,List<String>> mapperPageFields){
		Map<String, Map> mapperConfig = templateMapper.getMapperConfig();
		mapperConfig.forEach((String k, Map m)->{
			mapperPageFields.remove(m.get("page"));
		});
		templateMapper.setMapperPageFields(mapperPageFields);
		templateMapper.setIsCompleted(Boolean.toString(mapperPageFields.isEmpty()));
	}
	
	public TemplateMapper editMapper(String id, boolean isValid, TemplateMapper templateMapper){
		TemplateMapper templateMapperOriginal = templateMapperRepository.getById(id);
		templateMapper.setId(templateMapperOriginal.getId());
		Map<String, Map> mapperConfigOriginal = templateMapperOriginal.getMapperConfig();
		Map<String, Map> mapperConfigUpdated = templateMapper.getMapperConfig();
		Map<String,List<String>> mapperPageFields = templateMapperOriginal.getMapperPageFields();
		Map<String,List<String>> nonMapperPageFields = new HashMap<String, List<String>>(); 
				if(NullUtil.isNotEmpty(templateMapperOriginal.getNonMapperPageFields())) {
					nonMapperPageFields = templateMapperOriginal.getNonMapperPageFields();
				}
				LinkedHashMap<String,String> discConfig =  new LinkedHashMap<>();
		if(NullUtil.isNotEmpty(templateMapperOriginal.getDiscountConfig())) {
			LinkedHashMap<String,String> discConfigOld =  templateMapperOriginal.getDiscountConfig();
			LinkedHashMap<String,String> discConfignew = templateMapper.getDiscountConfig();
			Set<Entry<String, String>> keySet = discConfignew.entrySet();
			for(Entry<String, String> ent : keySet) {
				if(discConfigOld.containsKey(ent.getKey())) {
					discConfigOld.remove(ent.getKey());
					discConfigOld.put(ent.getKey(), ent.getValue());
				}else {
					discConfigOld.put(ent.getKey(), ent.getValue());
				}
			}
			discConfig = discConfigOld;
		}else {
			discConfig = templateMapper.getDiscountConfig();
		}	
		templateMapper.setDiscountConfig(discConfig);
		orderMapperConfig(templateMapper, mapperPageFields);
		/*mapperConfigUpdated.forEach((String key, Map valMap)->{
			if(mapperConfigOriginal.containsKey(key)){
				String updatedPage = (String)valMap.get("page");
				Map existedMap = mapperConfigOriginal.get(key);
				String existedPage = (String)existedMap.get("page");
				if(!existedPage.equals(updatedPage)){
					Map<String, String> mappings = (Map<String, String>)existedMap.get("mappings");
					List<String> fileds = (List<String>)existedMap.get("upMappedFields");
					if(fileds == null){
						fileds = new ArrayList<String>();
					}
					fileds.addAll(mappings.values());
					Map<String, List<String>> mapperPageFields = templateMapperOriginal.getMapperPageFields();
					if(mapperPageFields == null){
						mapperPageFields = new HashMap<String, List<String>>();
					}
					mapperPageFields.put(existedPage, fileds);
				}
			}
		});*/
		
		mapperConfigOriginal.putAll(mapperConfigUpdated);
		mapperConfigUpdated.clear();
		mapperConfigUpdated.putAll(mapperConfigOriginal);
		//removeMappedPages(templateMapper, templateMapperOriginal.getMapperPageFields());
		templateMapper.setNonMapperPageFields(nonMapperPageFields);
		templateMapper.setMapperPageFields(mapperPageFields);
		if(NullUtil.isNotEmpty(templateMapper.getMapperConfig())){
			List<String> invTypes=Lists.newArrayList();
			for(String key : templateMapper.getMapperConfig().keySet()) {
				String page = (String)templateMapper.getMapperConfig().get(key).get("page");
				if(NullUtil.isNotEmpty(templateMapper.getMapperConfig().get(key))
						&& NullUtil.isNotEmpty(templateMapper.getMapperConfig().get(key).get("mappings"))
						&& NullUtil.isNotEmpty(mapperPageFields)
						&& NullUtil.isNotEmpty(mapperPageFields.get(page))
						//&& (((Map)templateMapper.getMapperConfig().get(key).get("mappings")).values().size() >= (mapperPageFields.get(page).size()-3))
						) {
					invTypes.add(key);
				}
			}
			templateMapper.setIsCompleted(Boolean.toString(isValid));
			if(isValid) {
				TemplateMapperDoc templateMapperDoc = createTemplateMapperDoc(templateMapper, invTypes);
				templateMapper.setTemplateMapperdocid(templateMapperDoc.getId().toString());
			}
			Map<String, Map> mapperConfig12 = templateMapper.getMapperConfig();
			mapperConfig12.forEach((String key, Map m)->{
				String page = (String)m.get("page");
				List<String> fields = mapperPageFields.get(page);
				Map<String, String> mappings = (Map)m.get("mappings"); 
				LinkedHashMap<String, String> config = new LinkedHashMap<>();
				if(NullUtil.isNotEmpty(fields)) {
					for(String field : fields) {
						Set<Entry<String, String>> keySet = mappings.entrySet();
						String removeKey = null;
						for(Entry<String, String> ent : keySet) {
							if(ent.getValue().split("#MGST#")[0].equals(field)) {
								config.put(ent.getKey(), ent.getValue().split("#MGST#")[0]);
								removeKey = ent.getKey();
								break;
							}
						}
						mappings.remove(removeKey);
					}
				}
				m.put("mappings", config); 
			});
			templateMapper.setMapperConfig(mapperConfig12);
			TemplateMapper templateMapper2 = templateMapperRepository.save(templateMapper);
			return templateMapper2;
		} else {
			templateMapper.setIsCompleted(Boolean.toString(isValid));
			return templateMapperRepository.save(templateMapper);
		}
	}
	
	public void deleteMapper(String id){
		TemplateMapper templateMapper = templateMapperRepository.findOne(id);
		if(NullUtil.isNotEmpty(templateMapper)){
			String userId = templateMapper.getUserId();
			String clientId = templateMapper.getClientId();
			String mapperName = templateMapper.getMapperName();
			templateMapperDocRepository.deleteByUserIdAndClientIdAndMapperName(userId, clientId, mapperName);
		}
		templateMapperRepository.delete(id);
	}
	
	public List<TemplateMapper> getMappersByUserIdAndClientId(String userId, String clientId){
		return templateMapperRepository.getByUserIdAndClientId(userId, clientId);
	}
	
	public List<TemplateMapper> getMappersByClientId(String clientId){
		List<TemplateMapper> templateMappers = Lists.newArrayList();
		List<TemplateMapper> templateMappersByClientid = templateMapperRepository.getByClientId(clientId); 
		
		List<String> clientIds=Lists.newArrayList(clientId);
		List<TemplateMapper> templateMappersByCompanyIn = templateMapperRepository.findByCompanyIn(clientIds);
		if(NullUtil.isNotEmpty(templateMappersByClientid)) {
			templateMappers.addAll(templateMappersByClientid);
		}
		if(NullUtil.isNotEmpty(templateMappersByCompanyIn)) {
			if(NullUtil.isNotEmpty(templateMappersByClientid)) {
				for(TemplateMapper templateMap : templateMappersByCompanyIn) {
					if(!templateMappersByClientid.contains(templateMap)) {
						templateMappers.add(templateMap);
					}
				}	
			}else {
				templateMappers.addAll(templateMappersByCompanyIn);
			}
		}
		return templateMappers;
	}
	
	
	public TemplateMapper getMapperById(String id){
		return templateMapperRepository.getById(id);
	}
	
	public List<TemplateMapper> getByUserIdAndClientIdAndMapperTypeAndIsCompleted(String userId, String clientId,String mapperType, String isCompleted){
		return templateMapperRepository.getByUserIdAndClientIdAndMapperTypeAndIsCompleted(userId, clientId, mapperType, isCompleted);
	}
	
	private TemplateMapperDoc createTemplateMapperDoc(TemplateMapper templateMapper, List<String> invTypes){
		try {
			TemplateMapperDoc templateMapperDoc = templateMapperDocRepository.getByUserIdAndClientIdAndMapperTypeAndMapperName(
					templateMapper.getUserId(), templateMapper.getClientId(), templateMapper.getMapperType(), templateMapper.getMapperName());
			if(templateMapperDoc == null){
				templateMapperDoc = new TemplateMapperDoc();
				templateMapperDoc.setUserId(templateMapper.getUserId());
				templateMapperDoc.setClientId(templateMapper.getClientId());
				templateMapperDoc.setMapperType(templateMapper.getMapperType());
				templateMapperDoc.setMapperName(templateMapper.getMapperName());
			}
			templateMapperDoc.setCompany(templateMapper.getCompany());
			templateMapperDoc.setGlobaltemplate(templateMapper.isGlobaltemplate());
			templateMapperDoc.setInvTypes(invTypes);
			templateMapperDoc.setXmlDoc(generateWorkbookTemplate(templateMapper));
			TemplateMapperDoc templateMapperDoc1 = templateMapperDocRepository.save(templateMapperDoc);
			return templateMapperDoc1;
		} catch (Exception e) {
			
		}
		return null;
	}
	
	private String generateWorkbookTemplate(TemplateMapper templateMapper){
		Map<String, GstWorkSheet> fields = null;
		if("Sales".equalsIgnoreCase(templateMapper.getMapperType())){
			fields = getSalesTemplateFields(templateMapper.getSimplifiedOrDetail());
		}else if("einvoice".equalsIgnoreCase(templateMapper.getMapperType())) {
			fields = getEinvoiceTemplateFields(templateMapper.getSimplifiedOrDetail());
		}else{
			fields = getPurchageTemplateFields(templateMapper.getSimplifiedOrDetail());
		}
		Map<String, GstWorkSheet> fieldsLocal = fields;
		Map<String, Map> config = templateMapper.getMapperConfig();
		StringBuilder sb = new StringBuilder();
		config.forEach((String key, Map map)->{
			sb.append("\n" + generateWorkSheetTemplate(key, map, fieldsLocal.get(key),templateMapper));
		});
		return "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> \n <workbook>" + sb.toString() + "</workbook>";
	}
	
	private String generateWorkSheetTemplate(String code, Map config, GstWorkSheet fields){
		List<Field> fieldsLocal = new ArrayList<>();
		fieldsLocal.addAll(fields.getFields());
		String page = (String)config.get("page");
		LinkedHashMap<String, String> mappings = (LinkedHashMap)config.get("mappings"); 
		StringBuilder first = new StringBuilder();
		StringBuilder second = new StringBuilder();
		first.append("<loop startRow=\"0\" endRow=\"0\">\n <loopbreakcondition> \n <rowcheck offset=\"0\"> \n");
		second.append("<loop startRow=\"1\" endRow=\"1\" items=\""+fields.getItems()+"\" var=\""+fields.getVar()+"\" varType=\""+fields.getVarType()+"\"> \n  <section startRow=\"1\" endRow=\"1\">");
		Set<Entry<String, String>> entrySet = mappings.entrySet();
		int counter=0;
		for(Entry<String, String> entry : entrySet){
			String entryKey = entry.getKey();
			String entryValue = entry.getValue();
			if(NullUtil.isNotEmpty(entryValue)) {
			
				String[] value = entryValue.split("#MGST#"); 
				first.append("<cellcheck offset=\""+(Integer.parseInt(value[1])-1)+"\">"+value[0]+"</cellcheck>");
				int i=0;
				for(;i<fieldsLocal.size();i++){
					Field fild = fieldsLocal.get(i);
					if(entryKey.equals(fild.getName())){
						second.append("<mapping row=\"1\" col=\""+(Integer.parseInt(value[1])-1)+"\" ");
						if(fild.getAttrs() != null){
							second.append(fild.getAttrs());
						}
						second.append(">"+fild.getConversion()+"</mapping> \n");
						break;
					}
				}
				fieldsLocal.remove(i);
				counter++;
			
			}
		}
		
		
		first.append("</rowcheck> \n </loopbreakcondition> \n </loop> \n ");
		second.append("</section> \n <loopbreakcondition> \n <rowcheck offset=\"0\"> \n  <cellcheck offset=\"0\" /> \n </rowcheck> \n  </loopbreakcondition> \n </loop> \n");
		if(page.contains("&")) {
			page = page.replaceAll("&", "&amp;");
		}
		return "<worksheet name=\""+page+"\">"+ first.toString() + "<section startRow=\"0\" endRow=\"0\" /> \n " + second.toString() + "</worksheet>";
	}
	
	private String generateWorkSheetTemplate(String code, Map config, GstWorkSheet fields,TemplateMapper templateMapper){
		int skRows = Integer.parseInt(templateMapper.getSkipRows());
		int skipRows = 0;
		if(NullUtil.isNotEmpty(templateMapper.getSkipRows())) {
			skipRows = Integer.parseInt(templateMapper.getSkipRows())-1;
		}
		List<Field> fieldsLocal = new ArrayList<>();
		fieldsLocal.addAll(fields.getFields());
		String page = (String)config.get("page");
		LinkedHashMap<String, String> mappings = (LinkedHashMap)config.get("mappings"); 
		StringBuilder first = new StringBuilder();
		StringBuilder second = new StringBuilder();
		
		for(int j=0;j<skipRows;j++) {
			/*List<String> nonmappedrows = templateMapper.getNonMapperPageFields().get(page+j);
			if(NullUtil.isNotEmpty(nonmappedrows)) {
				first.append("<loop startRow=\""+j+"\" endRow=\""+j+"\">\n <loopbreakcondition> \n <rowcheck offset=\""+j+"\"> \n");
				
				for(int k=0;k<nonmappedrows.size();k++) {
					String header = nonmappedrows.get(k);
					first.append("<cellcheck offset=\""+k+"\">"+header+"</cellcheck>");
				}
			}else {
				first.append("<loop startRow=\""+j+"\" endRow=\""+j+"\">\n <loopbreakcondition> \n <rowcheck offset=\""+j+"\">");
			}
			int l = j+1;
			
			first.append("</rowcheck> \n </loopbreakcondition> \n </loop> \n ");*/
			int l = j+1;
			second.append("<section startRow=\""+l+"\" endRow=\""+l+"\"/> \n");
		}
		first.append("<loop startRow=\""+skipRows+"\" endRow=\""+skipRows+"\">\n <loopbreakcondition> \n <rowcheck offset=\""+skipRows+"\"> \n");
		second.append("<loop startRow=\""+skRows+"\" endRow=\""+skRows+"\" items=\""+fields.getItems()+"\" var=\""+fields.getVar()+"\" varType=\""+fields.getVarType()+"\"> \n  <section startRow=\""+skRows+"\" endRow=\""+skRows+"\">");
		Set<Entry<String, String>> entrySet = mappings.entrySet();
		int counter=0;
		for(Entry<String, String> entry : entrySet){
			String entryKey = entry.getKey();
			String entryValue = entry.getValue();
			if(NullUtil.isNotEmpty(entryValue)) {
				if(entryValue.contains("#MGST#")) {
					String[] value = entryValue.split("#MGST#"); 
					first.append("<cellcheck offset=\""+(Integer.parseInt(value[1])-1)+"\">"+value[0]+"</cellcheck>");
					int i=0;
					for(;i<fieldsLocal.size();i++){
						Field fild = fieldsLocal.get(i);
						if(entryKey.equals(fild.getName())){
							second.append("<mapping row=\""+skRows+"\" col=\""+(Integer.parseInt(value[1])-1)+"\" ");
							if(fild.getAttrs() != null){
								second.append(fild.getAttrs());
							}
							second.append(">"+fild.getConversion()+"</mapping> \n");
							break;
						}
					}
					fieldsLocal.remove(i);
					counter++;
				}else {
					List<String> fieldindex = templateMapper.getMapperPageFields().get(page);
					int index = fieldindex.indexOf(entryValue);
					
					first.append("<cellcheck offset=\""+index+"\">"+entryValue+"</cellcheck>");
					int i=0;
					for(;i<fieldsLocal.size();i++){
						Field fild = fieldsLocal.get(i);
						if(entryKey.equals(fild.getName())){
							second.append("<mapping row=\""+skRows+"\" col=\""+index+"\" ");
							if(fild.getAttrs() != null){
								second.append(fild.getAttrs());
							}
							second.append(">"+fild.getConversion()+"</mapping> \n");
							break;
						}
					}
					fieldsLocal.remove(i);
					counter++;
					
				}
			}
		}
		
		
		first.append("</rowcheck> \n </loopbreakcondition> \n </loop> \n ");
		second.append("</section> \n <loopbreakcondition> \n <rowcheck offset=\"0\"> \n  <cellcheck offset=\"0\" /> \n </rowcheck> \n  </loopbreakcondition> \n </loop> \n");
		if(page.contains("&")) {
			page = page.replaceAll("&", "&amp;");
		}
		if(skipRows == 0) {
		return "<worksheet name=\""+page+"\">"+ first.toString() + "<section startRow=\"0\" endRow=\"0\" /> \n " + second.toString() + "</worksheet>";
		}else {
			return "<worksheet name=\""+page+"\">"+ first.toString() + "\n " + second.toString() + "</worksheet>";
		}
	}
	
	public List<TemplateMapperDoc> getMapperDocs(String userId, String clientId,String mapperType){
		return templateMapperDocRepository.getByUserIdAndClientIdAndMapperType(userId, clientId, mapperType);
	}
	
	@Transactional(readOnly = true)
	public List<TemplateMapperDoc> getMapperDocs(String userId, String clientId){
		return templateMapperDocRepository.getByUserIdAndClientId(userId, clientId);
	}
	
	@Transactional(readOnly = true)
	public List<TemplateMapperDoc> getMapperDocs(String clientId){
		List<TemplateMapperDoc> templateMapperDocs = Lists.newArrayList();
		List<TemplateMapperDoc> templateMapperDocsByClientid = templateMapperDocRepository.getByClientId(clientId); 
		
		List<String> clientIds=Lists.newArrayList(clientId);
		List<TemplateMapperDoc> templateMapperDocsByCompanyIn = templateMapperDocRepository.findByCompanyIn(clientIds);
		if(NullUtil.isNotEmpty(templateMapperDocsByClientid)) {
			templateMapperDocs.addAll(templateMapperDocsByClientid);
		}
		if(NullUtil.isNotEmpty(templateMapperDocsByCompanyIn)) {
			if(NullUtil.isNotEmpty(templateMapperDocsByClientid)) {
				for(TemplateMapperDoc templateMap : templateMapperDocsByCompanyIn) {
					if(!templateMapperDocsByClientid.contains(templateMap)) {
						templateMapperDocs.add(templateMap);
					}
				}	
			}else {
				templateMapperDocs.addAll(templateMapperDocsByCompanyIn);
			}
		}
		return templateMapperDocs;
	}
	
	public TemplateMapperDoc getMapperDoc(String userId, String clientId,String mapperType,String mapperName){
		return templateMapperDocRepository.getByUserIdAndClientIdAndMapperTypeAndMapperName(userId, clientId, mapperType, mapperName);
	}

	public TemplateMapperDoc getMapperDoc(String userId, String clientId,String mapperName){
		return templateMapperDocRepository.findOne(mapperName);
		//return templateMapperDocRepository.getByUserIdAndClientIdAndMapperName(userId, clientId, mapperName);
	}
	
	class TemplateHandler extends DefaultHandler{
		
		   private Map<String, GstWorkSheet> workBookDetails = new HashMap<>();
		   private boolean isCellCheck = false;
		   private boolean isMapping = false;
		   private GstWorkSheet workSht = null;
		   private Queue<String> queue = new LinkedList<>();
		   private String mappingStr = null;
		   private boolean isNullAlowed = false;
		   @Override
		   public void startElement(String uri, 
		   String localName, String qName, Attributes attributes) throws SAXException {
			   if (qName.equalsIgnoreCase("worksheet")) {
				   workSht = new GstWorkSheet(attributes.getValue("name"));
			   }else  if(qName.equalsIgnoreCase("loop")) {
				   String startRow = attributes.getValue("startRow");
				   String endRow = attributes.getValue("endRow");
				   if("0".equals(startRow)){
					  
				   }else{
					  
					   workSht.setItems(attributes.getValue("items"));
					   workSht.setVar(attributes.getValue("var"));
					   workSht.setVarType(attributes.getValue("varType"));
				   }
			   }else  if(qName.equalsIgnoreCase("cellcheck")) {
				   isCellCheck = true;
			   }else  if(qName.equalsIgnoreCase("mapping")) {
				   String iNull = attributes.getValue("nullAllowed");
				   isMapping = true;
				   if(iNull != null && "true".equals(iNull)){
					   isNullAlowed = true;
				   }
			   }
			   
		   }
		   
		   @Override
		   public void endElement(String uri, 
		   String localName, String qName) throws SAXException {
			   if(qName.equalsIgnoreCase("loop")) {
				   isCellCheck = false;
				   isMapping = false;
			   }else if(qName.equalsIgnoreCase("cellcheck")) {
				   isCellCheck = false;
			   }else if(qName.equalsIgnoreCase("mapping")) {
				   String name = queue.poll();
				   String code = name.replaceAll(" ", "");
				   code = code.replaceAll("[^a-zA-Z 0-9]+","");
				   /*if("HSN/SACCode".equalsIgnoreCase(code)){
					   code =  "HSNSACCode";
				   }else if("Category(Goods/Services)".equalsIgnoreCase(code)){
					   code =  "Category";
				   }*/
				   workSht.addFields(new Field(name, code, mappingStr, isNullAlowed? "nullAllowed=\"true\"" : null, (Object o)->{return true;}));
				   isNullAlowed = false;
				   mappingStr = null;
				   isMapping = false;
			   }else if(qName.equalsIgnoreCase("worksheet")){
				   workBookDetails.put(workSht.getName(), workSht);
			   }
			   
		   }
		   
		   @Override
		   public void characters(char ch[], int start, int length) throws SAXException {
			   if(isCellCheck){
				   queue.offer(new String(ch, start, length));
			   }else if(isMapping){
				   if(mappingStr == null){
					   mappingStr = new String(ch, start, length);
				   }else{
					   mappingStr += new String(ch, start, length);
				   }
			   }
		     
		   }
		   
		   public Map<String, GstWorkSheet> getWorkbookDetails(){
			   return workBookDetails;
		   }
	}

	@Override
	public TemplateMapper getMapper(String userId, String clientId,	String mapperName) {
		return templateMapperRepository.findByTemplateMapperdocid(mapperName);
		//return templateMapperRepository.getByUserIdAndClientIdAndMapperName(userId, clientId, mapperName);
	}
	
	@Override
	public TemplateMapper getMapper(String clientId, String mapperName) {
		List<String> clientIds=Lists.newArrayList(clientId);
		return templateMapperRepository.findByCompanyInAndMapperName(clientIds, mapperName);
	}
	@Override
	public Set<String> getEinvoiceTemplateAllPages() {
		return einvoiceWorkBookDetails.keySet();
	}
	@Override
	public Map<String, GstWorkSheet> getEinvoiceTemplateFields(String simplifiedOrDetail) {
		if("Detailed".equalsIgnoreCase(simplifiedOrDetail)) {
			return einvoiceWorkBookDetails;
		}else {
			return einvoiceWorkBookSimplifiedDetails;
		}
	}
	@Override
	public Map<String, GstWorkSheet> getEinvoiceTemplateFields() {
		return einvoiceWorkBookDetails;
	}
}


