package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.util.NullUtil.isEmpty;

import java.text.DecimalFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.BulkImportTask;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.repository.BulkImportTaskRepository;

@Service
public class BulkImportTaskServiceImpl implements BulkImportTaskService{

	 private static DecimalFormat df2 = new DecimalFormat("#.##");
	@Autowired
	private BulkImportTaskRepository bulkImportTaskRepository;
	
	@Override
	public BulkImportTask createBulkImportTask(BulkImportTask bulkImportTask) {
		
		return bulkImportTaskRepository.save(bulkImportTask);
	}

	
	@Override
	public List<BulkImportTask> getBulkImportTask(String userid,String clientid) {
		
		return bulkImportTaskRepository.findByUseridAndClientid(userid,clientid);
	}
	
	@Override
	public Item changeInvoiceAmounts(Item item) {
		if(NullUtil.isNotEmpty(item.getTaxablevalue())) {
			item.setTaxablevalue(Double.parseDouble(df2.format(item.getTaxablevalue())));
			if(isEmpty(item.getRateperitem())) {
				item.setRateperitem(Double.parseDouble(df2.format(item.getTaxablevalue())));
			}
		}
		if(isEmpty(item.getQuantity())) {
			item.setQuantity(1d);
		}
		if(NullUtil.isNotEmpty(item.getIgstamount())) {
			item.setIgstamount(Double.parseDouble(df2.format(item.getIgstamount())));
		}
		if(NullUtil.isNotEmpty(item.getCgstamount())) {
			item.setCgstamount(Double.parseDouble(df2.format(item.getCgstamount())));
		}
		if(NullUtil.isNotEmpty(item.getSgstamount())) {
			item.setSgstamount(Double.parseDouble(df2.format(item.getSgstamount())));
		}
		if(NullUtil.isNotEmpty(item.getDiscount())) {
			item.setDiscount(Double.parseDouble(df2.format(item.getDiscount())));
		}
		if(NullUtil.isNotEmpty(item.getRateperitem())) {
			item.setRateperitem(Double.parseDouble(df2.format(item.getRateperitem())));
		}
		if(NullUtil.isNotEmpty(item.getCessamount())) {
			item.setCessamount(Double.parseDouble(df2.format(item.getCessamount())));
		}
		if(NullUtil.isNotEmpty(item.getIgstavltax())) {
			item.setIgstavltax(Double.parseDouble(df2.format(item.getIgstavltax())));
		}
		if(NullUtil.isNotEmpty(item.getSgstavltax())) {
			item.setSgstavltax(Double.parseDouble(df2.format(item.getSgstavltax())));
		}
		if(NullUtil.isNotEmpty(item.getCgstavltax())) {
			item.setCgstavltax(Double.parseDouble(df2.format(item.getCgstavltax())));
		}if(NullUtil.isNotEmpty(item.getCessavltax())) {
			item.setCessavltax(Double.parseDouble(df2.format(item.getCessavltax())));
		}
		Double total = 0d;
		if(NullUtil.isEmpty(item.getTotal()) || item.getTotal() <= 0) {
			if(NullUtil.isNotEmpty(item.getTaxablevalue())) {
				total += item.getTaxablevalue();
			}
			if(NullUtil.isNotEmpty(item.getIgstamount())) {
				total += item.getIgstamount();
			}
			if(NullUtil.isNotEmpty(item.getCgstamount())) {
				total += item.getCgstamount();
			}
			if(NullUtil.isNotEmpty(item.getSgstamount())) {
				total += item.getSgstamount();
			}
			if(NullUtil.isNotEmpty(item.getCessamount())) {
				total += item.getCessamount();
			}
			item.setTotal(total);
		}
		
		if(NullUtil.isNotEmpty(item.getTotal())) {
			item.setTotal(Double.parseDouble(df2.format(item.getTotal())));
		}
		
		
		
		return item;
		
	}
	
}
