package com.mastergst.usermanagement.runtime.audit;

import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mastergst.core.common.AuditLogConstants;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.repository.UserRepository;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.repository.AuditLogRepository;
import com.mastergst.usermanagement.runtime.repository.ClientRepository;
import com.mastergst.usermanagement.runtime.support.Utility;

@Service
@Transactional(readOnly = true)
public class AuditlogServiceImpl implements AuditlogService{
	
	private static final Logger logger = LogManager.getLogger(AuditlogServiceImpl.class.getName());
	private static final String CLASSNAME = "AuditlogServiceImpl::";
	private static SimpleDateFormat dateFormatOnlyDate = new SimpleDateFormat("dd/MM/yyyy");
	@Autowired private AuditLogRepository auditLogRepository;
	@Autowired private UserRepository userRepository;
	@Autowired private ClientRepository clientRepository;
	@Autowired private AuditDao auditDao;
	
	@Override
	public void saveAuditLog(String userid, String clientid,String invoiceNumber, String action,String returntype,InvoiceParent oldinvoice, InvoiceParent newInvoice) {
		User usr = userRepository.findOne(userid);
		String parentid = usr != null ? usr.getParentid() != null ? usr.getParentid() : "" : "";
		String username = usr != null ? usr.getFullname() != null ? usr.getFullname() : "" : "";
		String useremail = usr != null ? usr.getEmail() != null ? usr.getEmail() : "" : "";
		
		Client client = clientRepository.findOne(clientid);
		
		String clientname = client != null ? client.getBusinessname() != null ? client.getBusinessname() : "" : "";
		String gstn = client != null ? client.getGstnnumber() != null ? client.getGstnnumber() : "" : "";
		
		Auditlog auditlog;
		try {
			String desc = auditlogDescription(action,invoiceNumber,returntype);
			Date dt = new Date();
			int month=-1,year=-1;
			month = dt.getMonth();
			year = dt.getYear()+1900;
			int quarter = month/3;
			quarter = quarter == 0 ? 4 : quarter;
			String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
			month++;
			AuditingFields auditingFields = null;
			if(action.equalsIgnoreCase(AuditLogConstants.EDITANDSAVE)) {
				auditingFields = auditingFields(oldinvoice,newInvoice);
			}
			auditlog = new Auditlog(parentid,userid,clientid,clientname,gstn,username,useremail,action,InetAddress.getLocalHost().getHostAddress(),invoiceNumber,desc,returntype,""+month,yearCode,""+quarter,dateFormatOnlyDate.format(dt),auditingFields);
			auditlog.setCreatedDate(dt);
			auditLogRepository.save(auditlog);
		} catch (UnknownHostException e) {
			logger.error(CLASSNAME + "saveAuditLog : ERROR", e);
			e.printStackTrace();
		}
	}
	
	public AuditingFields auditingFields(InvoiceParent oldInvoice,InvoiceParent newInvoice) {
		AuditingFields auditingFields = null;
		boolean invoicechanges = true;
		boolean invoiceNumber = oldInvoice.getInvoiceno() != null ? newInvoice.getInvoiceno() != null ? !oldInvoice.getInvoiceno().equals(newInvoice.getInvoiceno()) ? true : false : false : false;
		if(invoiceNumber) {
			if(invoicechanges) {
				auditingFields = new AuditingFields();
				invoicechanges = false;
			}
			auditingFields.setOinvoiceNumber(oldInvoice.getInvoiceno() != null ? oldInvoice.getInvoiceno() : "");
			auditingFields.setNinvoiceNumber(newInvoice.getInvoiceno() != null ? newInvoice.getInvoiceno() : "");
		}
		boolean invoiceDate = oldInvoice.getDateofinvoice_str() != null ? newInvoice.getDateofinvoice_str() != null ? !oldInvoice.getDateofinvoice_str().equals(newInvoice.getDateofinvoice_str()) ? true : false : false : false;
		if(invoiceDate) {
			if(invoicechanges) {
				auditingFields = new AuditingFields();
				invoicechanges = false;
			}
			auditingFields.setOinvoiceDate(oldInvoice.getDateofinvoice_str() != null ? oldInvoice.getDateofinvoice_str() : "");
			auditingFields.setNinvoiceDate(newInvoice.getDateofinvoice_str() != null ? newInvoice.getDateofinvoice_str() : "");
		}
		
		boolean invoiceTotalTaxable = oldInvoice.getTotaltaxableamount() != null ? newInvoice.getTotaltaxableamount() != null ? !oldInvoice.getTotaltaxableamount().equals(newInvoice.getTotaltaxableamount()) ? true : false : false : false;
		if(invoiceTotalTaxable) {
			if(invoicechanges) {
				auditingFields = new AuditingFields();
				invoicechanges = false;
			}
			auditingFields.setOtotaltaxableamount(oldInvoice.getTotaltaxableamount() != null ? oldInvoice.getTotaltaxableamount() : 0d);
			auditingFields.setNtotaltaxableamount(newInvoice.getTotaltaxableamount() != null ? newInvoice.getTotaltaxableamount() : 0d);
		}
		
		boolean invoiceTotalAmount = oldInvoice.getTotalamount() != null ? newInvoice.getTotalamount() != null ? !oldInvoice.getTotalamount().equals(newInvoice.getTotalamount()) ? true : false : false : false;
		if(invoiceTotalAmount) {
			if(invoicechanges) {
				auditingFields = new AuditingFields();
				invoicechanges = false;
			}
			auditingFields.setOtotalamount(oldInvoice.getTotalamount() != null ? oldInvoice.getTotalamount() : 0d);
			auditingFields.setNtotalamount(newInvoice.getTotalamount() != null ? newInvoice.getTotalamount() : 0d);
		}
		boolean invoiceTotalIGST = oldInvoice.getTotalIgstAmount() != null ? newInvoice.getTotalIgstAmount() != null ? !oldInvoice.getTotalIgstAmount().equals(newInvoice.getTotalIgstAmount()) ? true : false : false : false;
		if(invoiceTotalIGST) {
			if(invoicechanges) {
				auditingFields = new AuditingFields();
				invoicechanges = false;
			}
			auditingFields.setOtotalIGSTAmount(oldInvoice.getTotalIgstAmount() != null ? oldInvoice.getTotalIgstAmount() : 0d);
			auditingFields.setNtotalIGSTAmount(newInvoice.getTotalIgstAmount() != null ? newInvoice.getTotalIgstAmount() : 0d);
		}
		boolean invoiceTotalCGST = oldInvoice.getTotalCgstAmount() != null ? newInvoice.getTotalCgstAmount() != null ? !oldInvoice.getTotalCgstAmount().equals(newInvoice.getTotalCgstAmount()) ? true : false : false : false;
		if(invoiceTotalCGST) {
			if(invoicechanges) {
				auditingFields = new AuditingFields();
				invoicechanges = false;
			}
			auditingFields.setOtotalCGSTAmount(oldInvoice.getTotalCgstAmount() != null ? oldInvoice.getTotalCgstAmount() : 0d);
			auditingFields.setNtotalCGSTAmount(newInvoice.getTotalCgstAmount() != null ? newInvoice.getTotalCgstAmount() : 0d);
		}
		boolean invoiceTotalSGST = oldInvoice.getTotalSgstAmount() != null ? newInvoice.getTotalSgstAmount() != null ? !oldInvoice.getTotalSgstAmount().equals(newInvoice.getTotalSgstAmount()) ? true : false : false : false;
		if(invoiceTotalSGST) {
			if(invoicechanges) {
				auditingFields = new AuditingFields();
				invoicechanges = false;
			}
			auditingFields.setOtotalSGSTAmount(oldInvoice.getTotalSgstAmount() != null ? oldInvoice.getTotalSgstAmount() : 0d);
			auditingFields.setNtotalSGSTAmount(newInvoice.getTotalSgstAmount() != null ? newInvoice.getTotalSgstAmount() : 0d);
		}
		boolean invoiceTotalCESS = oldInvoice.getTotalCessAmount() != null ? newInvoice.getTotalCessAmount() != null ? !oldInvoice.getTotalCessAmount().equals(newInvoice.getTotalCessAmount()) ? true : false : false : false;
		if(invoiceTotalCESS) {
			if(invoicechanges) {
				auditingFields = new AuditingFields();
				invoicechanges = false;
			}
			auditingFields.setOtotalCESSAmount(oldInvoice.getTotalCessAmount() != null ? oldInvoice.getTotalCessAmount() : 0d);
			auditingFields.setNtotalCESSAmount(newInvoice.getTotalCessAmount() != null ? newInvoice.getTotalCessAmount() : 0d);
		}
		return auditingFields;
	}
	
	@Override
	public String auditlogDescription(String action,String invoiceNumber,String returntype) {
		String description = "";
		switch (action) {
		case AuditLogConstants.SAVEASDRAFT : description = returntype+" of Inv. No. "+invoiceNumber+ " is Created";
			break;
		case AuditLogConstants.SAVEINVOICE : description = returntype+" of Inv. No. "+invoiceNumber+ " is Created";
			break;
		case AuditLogConstants.EDITANDSAVE : description = returntype+" of Inv. No. "+invoiceNumber+ " is Updated";
			break;
		case AuditLogConstants.CANCELLEDIRN : description = "Einvoice of Inv. No. "+invoiceNumber+ " is Cancelled";
			break;
		case AuditLogConstants.CANCELLEDALLIRN : description = ""+invoiceNumber+ " Einvoices are Cancelled";
			break;
		case AuditLogConstants.DELETE : description = returntype+" of Inv. No. "+invoiceNumber+ " is Deleted";
			break;
		case AuditLogConstants.DELETEALL : description = ""+invoiceNumber+ " "+returntype+"s are Deleted";
			break;
		case AuditLogConstants.DELETESELECTED : description = ""+invoiceNumber+ " "+returntype+"s are Deleted";
			break;
		case AuditLogConstants.GENERATEIRN : description = "Einvoice of Inv. No. "+invoiceNumber+ " is Generated";
			break;
		case AuditLogConstants.ALLINVGENERATEIRN : description = ""+invoiceNumber+ " Einvoices are Generated";
			break;
		case AuditLogConstants.CANCELLED : description = returntype+" of Inv. No. "+invoiceNumber+ " is Cancelled";
			break;
		case AuditLogConstants.GENERATEEWAYBIll : description = returntype+" of Inv. No. "+invoiceNumber+ " is Generated";
			break;
		case AuditLogConstants.CANCELEWAYBILL : description = returntype+" of Inv. No. "+invoiceNumber+ " is Cancelled";
			break;
		case AuditLogConstants.REJECTEWAYBILL : description = returntype+" of Inv. No. "+invoiceNumber+ " is Rejected";
			break;
		case AuditLogConstants.EXTENDVALIDITY : description = returntype+" of Inv. No. "+invoiceNumber+ " is Extended Validity";
			break;
		case AuditLogConstants.UPDATEVEHICLE : description =  returntype+" of Inv. No. "+invoiceNumber+ " is Updated Vehicle";
			break;
		default:description = "";
			break;
		}
		return description;
	}

	@Override
	public Map<String, Object> getAuditlogs(Pageable pageable, String userid, List<String> clientids, int month, int year, int start, int length,AuditlogFilter filter, String searchVal, String fieldName, String order) {
		Map<String, Object> auditlogMap = new HashMap<String, Object>();
		String yearCode = Utility.getYearCode(month, year);
		Page<? extends Auditlog> auditlogs = auditDao.findByClientidInAndMonthAndYear(clientids, month, yearCode, start, length,filter, searchVal, fieldName, order);
		auditlogMap.put("auditlogs", auditlogs);
		return auditlogMap;
	}

	@Override
	public Map<String, Object> getAuditLogCustom(Pageable pageable, String userid, List<String> clientids,String fromtime, String totime, int start, int length,AuditlogFilter filter, String searchVal,String fieldName, String order) {
		Map<String, Object> auditlogMap = new HashMap<String, Object>();
		Date stDate = null;
		Date endDate = null;
		String[] fromtimes = fromtime.split("-");
		String[] totimes = totime.split("-");
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1, Integer.parseInt(fromtimes[0]) - 1, 23, 59, 59);
		stDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(Integer.parseInt(totimes[2]), Integer.parseInt(totimes[1]) - 1, Integer.parseInt(totimes[0]), 23,59, 59);
		endDate = new java.util.Date(cal.getTimeInMillis());
		
		Page<? extends Auditlog> auditlogs = auditDao.findByClientidInAndFromtimeAndTotime(clientids, stDate, endDate, start, length,filter, searchVal, fieldName, order);
		auditlogMap.put("auditlogs", auditlogs);
		return auditlogMap;
	}

	@Override
	public AuditlogFilter auditlogFilter(HttpServletRequest request) {
		AuditlogFilter auditlogFilter = new AuditlogFilter();
		auditlogFilter.setAction(request.getParameter("action"));
		String clientname = request.getParameter("clientname");
		if(isNotEmpty(clientname)) {
			if(clientname.contains("-mgst-")) {
				clientname = clientname.replaceAll("-mgst-", "&");
			}
		}
		auditlogFilter.setClientname(clientname);
		String username = request.getParameter("username");
		if(isNotEmpty(username)) {
			if(username.contains("-mgst-")) {
				username = username.replaceAll("-mgst-", "&");
			}
		}
		auditlogFilter.setUsername(username);
		return auditlogFilter;
	}

	@Override
	public Page<? extends Auditlog> getAuditlogsExcel(String userid, List<String> listofclients, int month, int year,
			int start, int length, AuditlogFilter filter, String searchVal, Pageable pageable) {
		String yearCode = Utility.getYearCode(month, year);
		return auditDao.findByClientidInAndMonthAndYearExcel(listofclients, month, yearCode, start, length, filter, searchVal, pageable);
	}

	@Override
	public Page<? extends Auditlog> getAuditlogsCustomExcel(String userid, List<String> clientids, String fromtime,
			String totime, int start, int length, AuditlogFilter filter, String searchVal, Pageable pageable) {
		Date stDate = null;
		Date endDate = null;
		String[] fromtimes = fromtime.split("-");
		String[] totimes = totime.split("-");
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1, Integer.parseInt(fromtimes[0]) - 1, 23, 59, 59);
		stDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(Integer.parseInt(totimes[2]), Integer.parseInt(totimes[1]) - 1, Integer.parseInt(totimes[0]), 23,59, 59);
		endDate = new java.util.Date(cal.getTimeInMillis());
		return auditDao.findByClientidInAndFromtimeAndTotimeExcel(clientids, stDate, endDate, start, length, filter, searchVal, pageable);
	}

}
