package com.mastergst.web.usermanagement.runtime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mastergst.usermanagement.runtime.dao.MigrateDao;

@Controller
public class MigrateController {
	@Autowired
	private MigrateDao migrateDao;
	
	@RequestMapping(value = "/migrate", method = RequestMethod.GET)
	public @ResponseBody String migrate(){
		migrateDao.updateGstr1TaxAmounts();
		return "{\"status\":\"success\"}";	
	}
	
	@RequestMapping(value = "/migrateadvadj", method = RequestMethod.GET)
	public @ResponseBody String advmigrate(){
		migrateDao.updateGstr1AdvAdjTaxAmounts();
		return "{\"status\":\"success\"}";	
	}
	
	@RequestMapping(value = "/migratepr", method = RequestMethod.GET)
	public @ResponseBody String migratepr(){
		migrateDao.updatePurchageRegTaxAmounts();
		return "{\"status\":\"success\"}";	
	}
	@RequestMapping(value = "/migrate2a", method = RequestMethod.GET)
	public @ResponseBody String migrate2a(){
		migrateDao.updateGstr2ATaxAmounts();
		return "{\"status\":\"success\"}";	
	}
	@RequestMapping(value = "/migratepdee", method = RequestMethod.GET)
	public @ResponseBody String migratepdee(){
		migrateDao.updateProformaTaxAmounts();
		migrateDao.updateDeliveryChallanTaxAmounts();
		migrateDao.updateEstimatesTaxAmounts();
		migrateDao.updatePurchaseorderTaxAmounts();
		migrateDao.updateEwayBillTaxAmounts();
		return "{\"status\":\"success\"}";	
	}
	@RequestMapping(value = "/migratesumstrs", method = RequestMethod.GET)
	public @ResponseBody String migratesumstrs(){
		migrateDao.updateForSumStrs();
		return "{\"status\":\"success\"}";	
	}
	
	@RequestMapping(value = "/migrategstr1sumstrs", method = RequestMethod.GET)
	public @ResponseBody String migrategstr1sumstrs(){
		migrateDao.updateForgstr1SumStrs();
		return "{\"status\":\"success\"}";	
	}
	@RequestMapping(value = "/migrateprsumstrs", method = RequestMethod.GET)
	public @ResponseBody String migrateprsumstrs(){
		migrateDao.updateForprSumStrs();
		return "{\"status\":\"success\"}";	
	}
	
	@RequestMapping(value = "/migrategstr21718sumstrs", method = RequestMethod.GET)
	public @ResponseBody String migrategstr21718sumstrs(){
		migrateDao.updateForgstr21718SumStrs();
		return "{\"status\":\"success\"}";	
	}
	
	@RequestMapping(value = "/migrategstr21819sumstrs", method = RequestMethod.GET)
	public @ResponseBody String migrategstr21819sumstrs(){
		migrateDao.updateForgstr21819SumStrs();
		return "{\"status\":\"success\"}";	
	}
	
	@RequestMapping(value = "/migrategstr21920sumstrs", method = RequestMethod.GET)
	public @ResponseBody String migrategstr21920sumstrs(){
		migrateDao.updateForgstr21920SumStrs();
		return "{\"status\":\"success\"}";	
	}
	
	@RequestMapping(value = "/migrateb2cinv", method = RequestMethod.GET)
	public @ResponseBody String migrateb2c(){
		migrateDao.updateGstr1B2CTaxAmounts();
		return "{\"status\":\"success\"}";	
	}
	
	@RequestMapping(value = "/migratepartnerclient", method = RequestMethod.GET)
	public @ResponseBody String migratepartnerclient(){
		migrateDao.updatePartnerClient();
		return "{\"status\":\"success\"}";	
	}
	
	@RequestMapping(value = "/migrategstr11718sumstrs", method = RequestMethod.GET)
	public @ResponseBody String migrategstr11718sumstrs(){
		migrateDao.updateForgstr1pendingamt1718SumStrs("2017-2018");
		return "{\"status\":\"success\"}";	
	}
	
	@RequestMapping(value = "/migrategstr11819sumstrs", method = RequestMethod.GET)
	public @ResponseBody String migrategstr11819sumstrs(){
		migrateDao.updateForgstr1pendingamt1718SumStrs("2018-2019");
		return "{\"status\":\"success\"}";	
	}
	
	@RequestMapping(value = "/migrategstr11920sumstrs", method = RequestMethod.GET)
	public @ResponseBody String migrategstr11920sumstrs(){
		migrateDao.updateForgstr1pendingamt1718SumStrs("2019-2020");
		return "{\"status\":\"success\"}";	
	}
	@RequestMapping(value = "/migrategstr12021sumstrs", method = RequestMethod.GET)
	public @ResponseBody String migrategstr12021sumstrs(){
		migrateDao.updateForgstr1pendingamt1718SumStrs("2020-2021");
		return "{\"status\":\"success\"}";	
	}
	
	@RequestMapping(value = "/migrategstr12122sumstrs", method = RequestMethod.GET)
	public @ResponseBody String migrategstr12122sumstrs(){
		migrateDao.updateForgstr1pendingamt1718SumStrs("2021-2022");
		return "{\"status\":\"success\"}";	
	}
	

	@RequestMapping(value = "/migratepr1718sumstrs", method = RequestMethod.GET)
	public @ResponseBody String migratepr21718sumstrs(){
		migrateDao.updateForprpendingamt1718SumStrs("2017-2018");
		return "{\"status\":\"success\"}";	
	}
	
	@RequestMapping(value = "/migratepr1819sumstrs", method = RequestMethod.GET)
	public @ResponseBody String migratepr1819sumstrs(){
		migrateDao.updateForprpendingamt1718SumStrs("2018-2019");
		return "{\"status\":\"success\"}";	
	}
	
	@RequestMapping(value = "/migratepr1920sumstrs", method = RequestMethod.GET)
	public @ResponseBody String migratepr1920sumstrs(){
		migrateDao.updateForprpendingamt1718SumStrs("2019-2020");
		return "{\"status\":\"success\"}";	
	}
	@RequestMapping(value = "/migratepr2021sumstrs", method = RequestMethod.GET)
	public @ResponseBody String migratepr2021sumstrs(){
		migrateDao.updateForprpendingamt1718SumStrs("2020-2021");
		return "{\"status\":\"success\"}";	
	}
	
	@RequestMapping(value = "/migratepr2122sumstrs", method = RequestMethod.GET)
	public @ResponseBody String migratepr2122sumstrs(){
		migrateDao.updateForprpendingamt1718SumStrs("2021-2022");
		return "{\"status\":\"success\"}";	
	}
	
	@RequestMapping(value = "/migrateUsers", method = RequestMethod.GET)
	public @ResponseBody String migrateUsers(){
		migrateDao.migrateUsers();
		return "{\"status\":\"success\"}";	
	}

	@RequestMapping(value = "/migratecustomfields", method = RequestMethod.GET)
	public @ResponseBody String migratecustomFields(){
		migrateDao.migrateClients();
		return "{\"status\":\"success\"}";	
	}
	
	@RequestMapping(value = "/migratejournals", method = RequestMethod.GET)
	public @ResponseBody String migratejournals(){
		migrateDao.updateJournalDate();
		return "{\"status\":\"success\"}";	
	}
	
	@RequestMapping(value = "/migratejournalsGSTR1", method = RequestMethod.GET)
	public @ResponseBody String migratejournalsGSTR1(){
		migrateDao.updateJournalGSTR1();
		return "{\"status\":\"success\"}";	
	}
	
	@RequestMapping(value = "/migratejournalsVC", method = RequestMethod.GET)
	public @ResponseBody String migratejournalsVC(){
		migrateDao.updateJournalVoucher();
		return "{\"status\":\"success\"}";	
	}
	
	@RequestMapping(value = "/migratejournalsPaymentReceipt", method = RequestMethod.GET)
	public @ResponseBody String migratejournalsPaymentReceipt(){
		migrateDao.updateJournalVoucher();
		return "{\"status\":\"success\"}";	
	}
	
	@RequestMapping(value = "/migratejournalsPayemnts", method = RequestMethod.GET)
	public @ResponseBody String migratejournalsPayemnts(){
		migrateDao.updateJournalVoucher();
		return "{\"status\":\"success\"}";	
	}

}
