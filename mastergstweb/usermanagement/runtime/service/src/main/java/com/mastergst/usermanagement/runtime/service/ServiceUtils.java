package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.mastergst.configuration.service.ConfigService;
import com.mastergst.configuration.service.StateConfig;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTR3B;
import com.mastergst.usermanagement.runtime.domain.GSTR3BDetails;
import com.mastergst.usermanagement.runtime.domain.GSTR3BITCDetails;
import com.mastergst.usermanagement.runtime.domain.GSTR3BInterSupplyDetails;
import com.mastergst.usermanagement.runtime.domain.GSTR3BInterestDetails;
import com.mastergst.usermanagement.runtime.domain.GSTR3BInwardSupplyDetails;
import com.mastergst.usermanagement.runtime.domain.GSTR3BOffsetLiability;
import com.mastergst.usermanagement.runtime.domain.GSTR3BSupplyDetails;
import com.mastergst.usermanagement.runtime.domain.GSTR3B_VS_GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTR3B_VS_GSTR2A;
import com.mastergst.usermanagement.runtime.domain.GSTRNilItems;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.repository.GSTR1Repository;

@Component
public class ServiceUtils {
	
	private static final Logger logger = LogManager.getLogger(ServiceUtils.class.getName());
	private static final String CLASSNAME = "ServiceUtils::";
	String[] monthArray= {"","January","February","March","April","May","June","July","August","September","October","November","December"};
	@Autowired
	private ClientService clientService;
	@Autowired
	private GSTR1Repository gstr1Repository;
	@Autowired
	ConfigService configService;
	
	public GSTR3B_VS_GSTR1 comparision_GSTR3B_VS_GSTR1(String clientid, String userid,int month,int year) {
		final String method="comparision_GSTR3B_VS_GSTR1";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client = clientService.findById(clientid);
		String strMonth = month < 10 ? "0" + month : month + "";
		String returnPeriod = strMonth + year;
		GSTR3B_VS_GSTR1 gstr3b_gstr1 = new GSTR3B_VS_GSTR1();
		gstr3b_gstr1.setMonth(monthArray[month]+"-"+year);
		gstr3b_gstr1.setGSTR3B_3a_Taxableamt(0d);
		gstr3b_gstr1.setGSTR3B_3a_Taxamt(0d);
		gstr3b_gstr1.setGSTR3B_3b_Taxableamt(0d);
		gstr3b_gstr1.setGSTR3B_3b_Taxamt(0d);
		gstr3b_gstr1.setGSTR3B_3c_Taxableamt(0d);
		gstr3b_gstr1.setGSTR3B_3c_Taxamt(0d);
		gstr3b_gstr1.setGSTR3B_3d_Taxableamt(0d);
		gstr3b_gstr1.setGSTR3B_3d_Taxamt(0d);
		gstr3b_gstr1.setGSTR3B_3e_Taxableamt(0d);
		gstr3b_gstr1.setGSTR3B_3e_Taxamt(0d);
		gstr3b_gstr1.setGSTR3B_Cess(0d);
		gstr3b_gstr1.setGSTR3B_IGST(0d);
		gstr3b_gstr1.setGSTR3B_CGST_SGST(0d);
		gstr3b_gstr1.setGSTR3B_Total_Taxableamt(0d);
		GSTR3B gstr3b = clientService.getSuppliesInvoice(clientid, returnPeriod);
		if(isNotEmpty(gstr3b)) {
			/**
			 * 3.1(a) Taxable Amount Tax Amount -> igst cgst+sgst+cess
			 * supDetails.osupDet.txval
			 */
			if (isNotEmpty(gstr3b.getSupDetails().getOsupDet().getTxval())) {
				gstr3b_gstr1.setGSTR3B_3a_Taxableamt(gstr3b_gstr1.getGSTR3B_3a_Taxableamt() + gstr3b.getSupDetails().getOsupDet().getTxval());
			}
			if (isNotEmpty(gstr3b.getSupDetails().getOsupDet().getIamt())) {
				gstr3b_gstr1.setGSTR3B_3a_Taxamt(gstr3b_gstr1.getGSTR3B_3a_Taxamt() + gstr3b.getSupDetails().getOsupDet().getIamt());
				gstr3b_gstr1.setGSTR3B_IGST(gstr3b_gstr1.getGSTR3B_IGST()+gstr3b.getSupDetails().getOsupDet().getIamt());
			}
			if (isNotEmpty(gstr3b.getSupDetails().getOsupDet().getCamt())) {
				gstr3b_gstr1.setGSTR3B_3a_Taxamt(gstr3b_gstr1.getGSTR3B_3a_Taxamt() + gstr3b.getSupDetails().getOsupDet().getCamt());
				gstr3b_gstr1.setGSTR3B_CGST_SGST(gstr3b_gstr1.getGSTR3B_CGST_SGST() + gstr3b.getSupDetails().getOsupDet().getCamt());
			}
			if (isNotEmpty(gstr3b.getSupDetails().getOsupDet().getSamt())) {
				gstr3b_gstr1.setGSTR3B_3a_Taxamt(gstr3b_gstr1.getGSTR3B_3a_Taxamt() + gstr3b.getSupDetails().getOsupDet().getSamt());
				gstr3b_gstr1.setGSTR3B_CGST_SGST(gstr3b_gstr1.getGSTR3B_CGST_SGST() + gstr3b.getSupDetails().getOsupDet().getSamt());
			}
			if (isNotEmpty(gstr3b.getSupDetails().getOsupDet().getCsamt())) {
				gstr3b_gstr1.setGSTR3B_Cess(gstr3b_gstr1.getGSTR3B_Cess() + gstr3b.getSupDetails().getOsupDet().getCsamt());
			}
			/**
			 * 3.2(b) Taxable Amount Tax Amount -> igst supDetails.osupZero.txval
			 */
			if (isNotEmpty(gstr3b.getSupDetails().getOsupZero().getTxval())) {
				gstr3b_gstr1.setGSTR3B_3b_Taxableamt(gstr3b_gstr1.getGSTR3B_3b_Taxableamt() + gstr3b.getSupDetails().getOsupZero().getTxval());
			}
			if (isNotEmpty(gstr3b.getSupDetails().getOsupZero().getIamt())) {
				gstr3b_gstr1.setGSTR3B_3b_Taxamt(gstr3b_gstr1.getGSTR3B_3b_Taxamt() + gstr3b.getSupDetails().getOsupZero().getIamt());
				gstr3b_gstr1.setGSTR3B_IGST(gstr3b_gstr1.getGSTR3B_IGST()+gstr3b.getSupDetails().getOsupDet().getIamt());
			}
			if (isNotEmpty(gstr3b.getSupDetails().getOsupZero().getCsamt())) {
				gstr3b_gstr1.setGSTR3B_Cess(gstr3b_gstr1.getGSTR3B_Cess() + gstr3b.getSupDetails().getOsupZero().getCsamt());
			}
	
			/**
			 * 3.2(c) Taxable Amount Tax Amount not available.
			 */
			if (isNotEmpty(gstr3b.getSupDetails().getOsupNilExmp().getTxval())) {
				gstr3b_gstr1.setGSTR3B_3c_Taxableamt(gstr3b_gstr1.getGSTR3B_3c_Taxableamt() + gstr3b.getSupDetails().getOsupNilExmp().getTxval());
			}
	
			/**
			 * 3.2(d) Taxable Amount Tax Amount igst+cgst_sgst+cess
			
			if (isNotEmpty(gstr3b.getSupDetails().getIsupRev().getTxval())) {
				gstr3b_gstr1.setGSTR3B_3d_Taxableamt(gstr3b_gstr1.getGSTR3B_3d_Taxableamt() + gstr3b.getSupDetails().getIsupRev().getTxval());
			}
			if (isNotEmpty(gstr3b.getSupDetails().getIsupRev().getIamt())) {
				gstr3b_gstr1.setGSTR3B_3d_Taxamt(gstr3b_gstr1.getGSTR3B_3d_Taxamt() + gstr3b.getSupDetails().getIsupRev().getIamt());
				gstr3b_gstr1.setGSTR3B_IGST(gstr3b_gstr1.getGSTR3B_IGST()+gstr3b.getSupDetails().getOsupDet().getIamt());
			}
			if (isNotEmpty(gstr3b.getSupDetails().getIsupRev().getCamt())) {
				gstr3b_gstr1.setGSTR3B_3d_Taxamt(gstr3b_gstr1.getGSTR3B_3d_Taxamt() + gstr3b.getSupDetails().getIsupRev().getCamt());
				gstr3b_gstr1.setGSTR3B_CGST_SGST(gstr3b_gstr1.getGSTR3B_CGST_SGST() + gstr3b.getSupDetails().getIsupRev().getCamt());
			}
			if (isNotEmpty(gstr3b.getSupDetails().getIsupRev().getSamt())) {
				gstr3b_gstr1.setGSTR3B_3d_Taxamt(gstr3b_gstr1.getGSTR3B_3d_Taxamt() + gstr3b.getSupDetails().getIsupRev().getSamt());
				gstr3b_gstr1.setGSTR3B_CGST_SGST(gstr3b_gstr1.getGSTR3B_CGST_SGST() + gstr3b.getSupDetails().getIsupRev().getSamt());
	
			}
			if (isNotEmpty(gstr3b.getSupDetails().getIsupRev().getCsamt())) {
				gstr3b_gstr1.setGSTR3B_Cess(gstr3b_gstr1.getGSTR3B_Cess() + gstr3b.getSupDetails().getIsupRev().getCsamt());
			}
			 */
			/**
			 * 3.2(e) Taxable Amount Tax Amount igst+cgst_sgst+cess
			 */
			if (isNotEmpty(gstr3b.getSupDetails().getOsupNongst().getTxval())) {
				gstr3b_gstr1.setGSTR3B_3e_Taxableamt(gstr3b_gstr1.getGSTR3B_3e_Taxableamt() + gstr3b.getSupDetails().getOsupNongst().getTxval());
			}
		}
		
		/**
		 * To set Total Taxamount (3a+3b+3d) To set Total Taxableamount (3a+3b+3c+3d+3e)
		 * To CGST_SGST-> 3a+3d
		 */
		//System.out.println(gstr3b_gstr1.getGSTR3B_IGST());
		gstr3b_gstr1.setGSTR3B_IGST(gstr3b_gstr1.getGSTR3B_3a_Taxamt() + gstr3b_gstr1.getGSTR3B_3b_Taxamt() + 
				 gstr3b_gstr1.getGSTR3B_3d_Taxamt() - gstr3b_gstr1.getGSTR3B_CGST_SGST());
		
		//gstr3b_gstr1.setGSTR3B_IGST(gstr3b_gstr1.getGSTR3B_IGST());
		gstr3b_gstr1.setGSTR3B_Total_Taxableamt(gstr3b_gstr1.getGSTR3B_3a_Taxableamt()
				+ gstr3b_gstr1.getGSTR3B_3b_Taxableamt() + gstr3b_gstr1.getGSTR3B_3c_Taxableamt()
				+ gstr3b_gstr1.getGSTR3B_3e_Taxableamt());

		gstr3b_gstr1.setGSTR3B_Total_Taxableamt1(gstr3b_gstr1.getGSTR3B_3a_Taxableamt() + gstr3b_gstr1.getGSTR3B_3b_Taxableamt()
						+ gstr3b_gstr1.getGSTR3B_3c_Taxableamt() + gstr3b_gstr1.getGSTR3B_3e_Taxableamt());
		gstr3b_gstr1.setGSTR3B_Total_Taxamt2(gstr3b_gstr1.getGSTR3B_IGST() + gstr3b_gstr1.getGSTR3B_CGST_SGST());

		//clientid will be client
		Page<? extends InvoiceParent> gstr1List = clientService.getInvoices(null,client,userid,MasterGSTConstants.GSTR1,"reports",month,year);
		/**
		 * B2B/B2BC(L) INVOICE_TYPE CBW --> Sale from Bonded Warehouse
		 * 
		 * set default values in gstr3b_gstr1 variables
		 */
		gstr3b_gstr1.setB2b_R_CBW_Taxableamount(0d);
		gstr3b_gstr1.setB2b_R_CBW_Taxamount(0d);
		gstr3b_gstr1.setB2cl_R_CBW_Taxableamount(0d);
		gstr3b_gstr1.setB2cl_R_CBW_Taxamount(0d);
		gstr3b_gstr1.setB2b_CL_DE_Taxableamount(0d);
		gstr3b_gstr1.setB2b_CL_DE_Taxamount(0d);
		gstr3b_gstr1.setB2b_CL_SEWP_Taxableamount(0d);
		gstr3b_gstr1.setB2b_CL_SEWP_Taxamount(0d);
		gstr3b_gstr1.setB2b_CL_SEWOP_Taxableamount(0d);
		gstr3b_gstr1.setB2b_CL_SEWOP_Taxamount(0d);

		gstr3b_gstr1.setExports_WP_Taxableamount(0d);
		gstr3b_gstr1.setExports_WP_Taxamount(0d);
		gstr3b_gstr1.setExports_WOP_Taxableamount(0d);
		gstr3b_gstr1.setExports_WOP_Taxamount(0d);

		gstr3b_gstr1.setNilrated_Exempted_Taxableamount(0d);
		gstr3b_gstr1.setNilrated_Exempted_Taxamount(0d);
		gstr3b_gstr1.setNongst_Taxableamount(0d);
		gstr3b_gstr1.setNongst_Taxamount(0d);

		gstr3b_gstr1.setRcm_Taxableamount(0d);
		gstr3b_gstr1.setRcm_Taxamount(0d);
		gstr3b_gstr1.setBook_inter_Taxableamount(0d);
		gstr3b_gstr1.setBook_intra_Taxableamount(0d);
		gstr3b_gstr1.setBook_inter_Taxamount(0d);
		gstr3b_gstr1.setBook_intra_Taxamount(0d);

		/**
		 * GSTR1 Default values
		 */
		
		gstr3b_gstr1.setGstr1b2b_R_CBW_Taxableamount(0d);
		gstr3b_gstr1.setGstr1b2b_R_CBW_Taxamount(0d);
		gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxableamount(0d);
		gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxamount(0d);
		gstr3b_gstr1.setGstr1b2b_CL_DE_Taxableamount(0d);
		gstr3b_gstr1.setGstr1b2b_CL_DE_Taxamount(0d);
		gstr3b_gstr1.setGstr1b2b_CL_SEWP_Taxableamount(0d);
		gstr3b_gstr1.setGstr1b2b_CL_SEWP_Taxamount(0d);
		gstr3b_gstr1.setGstr1b2b_CL_SEWOP_Taxableamount(0d);
		gstr3b_gstr1.setGstr1b2b_CL_SEWOP_Taxamount(0d);

		gstr3b_gstr1.setGstr1exports_WP_Taxableamount(0d);
		gstr3b_gstr1.setGstr1exports_WP_Taxamount(0d);
		gstr3b_gstr1.setGstr1exports_WOP_Taxableamount(0d);
		gstr3b_gstr1.setGstr1exports_WOP_Taxamount(0d);

		gstr3b_gstr1.setGstr1nilrated_Exempted_Taxableamount(0d);
		gstr3b_gstr1.setGstr1nilrated_Exempted_Taxamount(0d);
		gstr3b_gstr1.setGstr1nongst_Taxableamount(0d);
		gstr3b_gstr1.setGstr1nongst_Taxamount(0d);

		gstr3b_gstr1.setGstr1rcm_Taxableamount(0d);
		gstr3b_gstr1.setGstr1rcm_Taxamount(0d);
		gstr3b_gstr1.setGstr1_inter_Taxableamount(0d);
		gstr3b_gstr1.setGstr1_intra_Taxableamount(0d);
		gstr3b_gstr1.setGstr1_inter_Taxamount(0d);
		gstr3b_gstr1.setGstr1_intra_Taxamount(0d);
		
		List<String> invTypes = new ArrayList<String>();
		invTypes.add(MasterGSTConstants.B2BA);
		invTypes.add(MasterGSTConstants.B2CSA);
		invTypes.add(MasterGSTConstants.B2CLA);
		invTypes.add(MasterGSTConstants.CDNA);
		invTypes.add(MasterGSTConstants.CDNURA);
		invTypes.add(MasterGSTConstants.EXPA);
		List<String> amendmentRefIds = Lists.newArrayList();
		if(isNotEmpty(gstr1List)) {
			gstr1List.forEach(gstr -> {
				if(isNotEmpty(gstr.getInvtype())){
					if(invTypes.contains(gstr.getInvtype()) && isNotEmpty(gstr.getAmendmentRefId())) {
						if(isNotEmpty(gstr.getAmendmentRefId()) && gstr.getAmendmentRefId().size() > 0) {
							amendmentRefIds.addAll(gstr.getAmendmentRefId());
						}
					}
				}
			});
		}
		
		if(isNotEmpty(gstr1List)) {
			gstr1List.forEach(gstr -> {
				if(!amendmentRefIds.contains(gstr.getId().toString())) {
					
					if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype())) && (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("R")
									|| gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("CBW")))) {
		
						//orginal_invno.put(gstr.getB2b().get(0).getInv().get(0).getInum(), "B2B_R_CBW");
						if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
							if(isNotEmpty(gstr.getTotaltaxableamount())) {
								gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
							}
							if(isNotEmpty(gstr.getTotaltax())) {
								gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
							}
						}else {
							if(isNotEmpty(gstr.getTotaltaxableamount())) {
								gstr3b_gstr1.setB2b_R_CBW_Taxableamount(gstr3b_gstr1.getB2b_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
							}
							if(isNotEmpty(gstr.getTotaltax())) {
								gstr3b_gstr1.setB2b_R_CBW_Taxamount(gstr3b_gstr1.getB2b_R_CBW_Taxamount() + gstr.getTotaltax());
							}
						}
					} else if ((MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype()))
							&& (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("R") || gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("CBW")))) {
		
						//orginal_invno.put(gstr.getB2b().get(0).getInv().get(0).getInum(), "B2C_L_CBW");
						if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
							if(isNotEmpty(gstr.getTotaltaxableamount())) {
								gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
							}
							if(isNotEmpty(gstr.getTotaltax())) {
								gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
							}
						}else {
							if(isNotEmpty(gstr.getTotaltaxableamount())) {
								gstr3b_gstr1.setB2cl_R_CBW_Taxableamount(gstr3b_gstr1.getB2cl_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
							}
							if(isNotEmpty(gstr.getTotaltax())) {
								gstr3b_gstr1.setB2cl_R_CBW_Taxamount(gstr3b_gstr1.getB2cl_R_CBW_Taxamount() + gstr.getTotaltax());
							}
						}
					} else if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype())
							|| MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype())
							|| MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype())) && (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("DE")))) {
		
						//orginal_invno.put(gstr.getB2b().get(0).getInv().get(0).getInum(), "DE");
						if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
							if(isNotEmpty(gstr.getTotaltaxableamount())) {
								gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
							}
							if(isNotEmpty(gstr.getTotaltax())) {
								gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
							}
						}else {
							if(isNotEmpty(gstr.getTotaltaxableamount())) {
								gstr3b_gstr1.setB2b_CL_DE_Taxableamount(gstr3b_gstr1.getB2b_CL_DE_Taxableamount() + gstr.getTotaltaxableamount());
							}
							if(isNotEmpty(gstr.getTotaltax())) {
								gstr3b_gstr1.setB2b_CL_DE_Taxamount(gstr3b_gstr1.getB2b_CL_DE_Taxamount() + gstr.getTotaltax());
							}
						}
					} else if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype())
							|| MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype())
							|| MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype())) && (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("SEWP")))) {
						//orginal_invno.put(gstr.getB2b().get(0).getInv().get(0).getInum(), "SEWP");
						if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
							if(isNotEmpty(gstr.getTotaltaxableamount())) {
								gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
							}
							if(isNotEmpty(gstr.getTotaltax())) {
								gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
							}
						}else {
							if(isNotEmpty(gstr.getTotaltaxableamount())) {
								gstr3b_gstr1.setB2b_CL_SEWP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxableamount() + gstr.getTotaltaxableamount());
							}
							if(isNotEmpty(gstr.getTotaltax())) {
								gstr3b_gstr1.setB2b_CL_SEWP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxamount() + gstr.getTotaltax());
							}
						}
					} else if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype())
							|| MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype())
							|| MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype()))	&& (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("SEWOP")))) {
		
						//orginal_invno.put(gstr.getB2b().get(0).getInv().get(0).getInum(), "SEWOP");
						if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
							if(isNotEmpty(gstr.getTotaltaxableamount())) {
								gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
							}
							if(isNotEmpty(gstr.getTotaltax())) {
								gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
							}
						}else {
							if(isNotEmpty(gstr.getTotaltaxableamount())) {
								gstr3b_gstr1.setB2b_CL_SEWOP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxableamount() + gstr.getTotaltaxableamount());
							}
							if(isNotEmpty(gstr.getTotaltax())) {
								gstr3b_gstr1.setB2b_CL_SEWOP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxamount() + gstr.getTotaltax());
							}
						}
					}else if((MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype()))){
						//orginal_invno.put(gstr.getB2b().get(0).getInv().get(0).getInum(), "B2C_L_CBW");
						if(isNotEmpty(gstr.getTotaltaxableamount())) {
							gstr3b_gstr1.setB2cl_R_CBW_Taxableamount(gstr3b_gstr1.getB2cl_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
						}
						if(isNotEmpty(gstr.getTotaltax())) {
							gstr3b_gstr1.setB2cl_R_CBW_Taxamount(gstr3b_gstr1.getB2cl_R_CBW_Taxamount() + gstr.getTotaltax());
						}
					}
		
					if (MasterGSTConstants.EXPORTS.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.EXPA.equalsIgnoreCase(gstr.getInvtype())) {
						if(isNotEmpty(gstr.getExp()) && isNotEmpty(gstr.getExp().get(0).getExpTyp())) {
							if ("WPAY".equalsIgnoreCase(gstr.getExp().get(0).getExpTyp())) {
								//orginal_invno.put(gstr.getB2b().get(0).getInv().get(0).getInum(), "EXPORT_WPAY");
								if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
									if(isNotEmpty(gstr.getTotaltaxableamount())) {
										gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
									}
									if(isNotEmpty(gstr.getTotaltax())) {
										gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
									}
								}else{
									if(isNotEmpty(gstr.getTotaltaxableamount())) {
										gstr3b_gstr1.setExports_WP_Taxableamount(gstr3b_gstr1.getExports_WP_Taxableamount() + gstr.getTotaltaxableamount());
									}
									if(isNotEmpty(gstr.getTotaltax())) {
										gstr3b_gstr1.setExports_WP_Taxamount(gstr3b_gstr1.getExports_WP_Taxamount() + gstr.getTotaltax());
									}
								}
							} else if ("WOPAY".equalsIgnoreCase(gstr.getExp().get(0).getExpTyp())) {
								//orginal_invno.put(gstr.getB2b().get(0).getInv().get(0).getInum(), "EXPORT_WOPAY");
								if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
									if(isNotEmpty(gstr.getTotaltaxableamount())) {
										gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
									}
									if(isNotEmpty(gstr.getTotaltax())) {
										gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
									}
								}else {
									if(isNotEmpty(gstr.getTotaltaxableamount())) {
										gstr3b_gstr1.setExports_WOP_Taxableamount(gstr3b_gstr1.getExports_WOP_Taxableamount() + gstr.getTotaltaxableamount());
									}
									if(isNotEmpty(gstr.getTotaltax())) {
										gstr3b_gstr1.setExports_WOP_Taxamount(gstr3b_gstr1.getExports_WOP_Taxamount() + gstr.getTotaltax());
									}
								}
							}
						}
					}
		
					if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNUR.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNURA.equalsIgnoreCase(gstr.getInvtype())) {
		
						String org_invno = "";
						String docType = "";
						if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)) {
							if (isNotEmpty(((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty())) {
								docType = ((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty();
								org_invno =((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getInum(); 
										//orginal_invno.get(((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getInum());
							}
						} else if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)) {
							if (isNotEmpty(((GSTR1) gstr).getCdnur().get(0).getNtty())) {
								docType = gstr.getCdnur().get(0).getNtty();
								org_invno = gstr.getCdnur().get(0).getInum(); 
										//orginal_invno.get(gstr.getCdnur().get(0).getInum());
							}
						}
						
						Date stDate = null;
						Date endDate = null;
						Calendar cal = Calendar.getInstance();
						if(month < 10) {
							cal.set(year-1, 3, 1, 0, 0, 0);
							stDate = new java.util.Date(cal.getTimeInMillis());
							cal = Calendar.getInstance();
							cal.set(year, month, 0, 23, 59, 59);
							endDate = new java.util.Date(cal.getTimeInMillis());
						}else {
							cal.set(year, 3, 1, 0, 0, 0);
							stDate = new java.util.Date(cal.getTimeInMillis());
							cal = Calendar.getInstance();
							cal.set(year, month, 0, 23, 59, 59);
							endDate = new java.util.Date(cal.getTimeInMillis());
						}
						List<GSTR1> gstr1list = gstr1Repository.findByInvoicenoAndClientidAndDateofinvoiceBetween(org_invno, client.getId().toString(), stDate, endDate);
						
						if (docType.equalsIgnoreCase("C")) {
							boolean flag = false;
							if(isNotEmpty(gstr1list)) {
								for(InvoiceParent gstrr1 : gstr1list) {
									if(isNotEmpty(gstrr1) && isNotEmpty(gstrr1.getInvoiceno())) {
										if(org_invno.equals(gstrr1.getInvoiceno())) {
											flag = true;
											if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B) || gstrr1.getInvtype().equals(MasterGSTConstants.B2C) || gstrr1.getInvtype().equals(MasterGSTConstants.B2CL)) {
												String creverseChargeType = "";
												if(isNotEmpty(gstrr1.getRevchargetype())) {
													creverseChargeType = gstrr1.getRevchargetype();
												}else {
													creverseChargeType = "Regular";
												}
												if("Regular".equals(creverseChargeType)) {
													String invType = "";
													if(isNotEmpty(gstrr1.getB2b()) && isNotEmpty(gstrr1.getB2b().get(0).getInv())	&& isNotEmpty(gstrr1.getB2b().get(0).getInv().get(0).getInvTyp())) {
														invType = gstrr1.getB2b().get(0).getInv().get(0).getInvTyp();
													}else {
														invType = "R";
													}
													if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B)){
														if("R".equals(invType) || "CBW".equals(invType)) {
															if(isNotEmpty(gstr.getTotaltaxableamount())) {
																gstr3b_gstr1.setB2b_R_CBW_Taxableamount(gstr3b_gstr1.getB2b_R_CBW_Taxableamount() - gstr.getTotaltaxableamount());
															}
															if(isNotEmpty(gstr.getTotaltax())) {
																gstr3b_gstr1.setB2b_R_CBW_Taxamount(gstr3b_gstr1.getB2b_R_CBW_Taxamount() - gstr.getTotaltax());
															}
														}											
													}else {
														if("R".equals(invType) || "CBW".equals(invType)) {
															if(isNotEmpty(gstr.getTotaltaxableamount())) {
																gstr3b_gstr1.setB2cl_R_CBW_Taxableamount(gstr3b_gstr1.getB2cl_R_CBW_Taxableamount() - gstr.getTotaltaxableamount());
															}
															if(isNotEmpty(gstr.getTotaltax())) {
																gstr3b_gstr1.setB2cl_R_CBW_Taxamount(gstr3b_gstr1.getB2cl_R_CBW_Taxamount() - gstr.getTotaltax());
															}
														}
													}
													if("DE".equals(invType)) {
														if(isNotEmpty(gstr.getTotaltaxableamount())) {
															gstr3b_gstr1.setB2b_CL_DE_Taxableamount(gstr3b_gstr1.getB2b_CL_DE_Taxableamount() - gstr.getTotaltaxableamount());
														}
														if(isNotEmpty(gstr.getTotaltax())) {
															gstr3b_gstr1.setB2b_CL_DE_Taxamount(gstr3b_gstr1.getB2b_CL_DE_Taxamount() - gstr.getTotaltax());
														}
													}else if("SEWP".equals(invType) || "SEWPC".equals(invType)) {
														if(isNotEmpty(gstr.getTotaltaxableamount())) {
															gstr3b_gstr1.setB2b_CL_SEWP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxableamount() - gstr.getTotaltaxableamount());
														}
														if(isNotEmpty(gstr.getTotaltax())) {
															gstr3b_gstr1.setB2b_CL_SEWP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxamount() - gstr.getTotaltax());
														}
													}else if("SEWOP".equals(invType)) {
														if(isNotEmpty(gstr.getTotaltaxableamount())) {
															gstr3b_gstr1.setB2b_CL_SEWOP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxableamount() - gstr.getTotaltaxableamount());
														}
														if(isNotEmpty(gstr.getTotaltax())) {
															gstr3b_gstr1.setB2b_CL_SEWOP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxamount() - gstr.getTotaltax());
														}
													}else if("WPAY".equals(invType)) {
														if(isNotEmpty(gstr.getTotaltaxableamount())) {
															gstr3b_gstr1.setExports_WP_Taxableamount(gstr3b_gstr1.getExports_WP_Taxableamount() - gstr.getTotaltaxableamount());
														}
														if(isNotEmpty(gstr.getTotaltax())) {
															gstr3b_gstr1.setExports_WP_Taxamount(gstr3b_gstr1.getExports_WP_Taxamount() - gstr.getTotaltax());
														}
													}else if("WOPAY".equals(invType)) {
														if(isNotEmpty(gstr.getTotaltaxableamount())) {
															gstr3b_gstr1.setExports_WOP_Taxableamount(gstr.getTotaltaxableamount());
														}
														if(isNotEmpty(gstr.getTotaltax())) {
															gstr3b_gstr1.setExports_WOP_Taxamount(gstr3b_gstr1.getExports_WP_Taxamount() - gstr.getTotaltax());
														}
													}
												}else {
													if(isNotEmpty(gstr.getTotaltaxableamount())) {
														gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() - gstr.getTotaltaxableamount());
													}
													if(isNotEmpty(gstr.getTotaltax())) {
														gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() - gstr.getTotaltax());
													}
												}
											}
										}
									}
								}	
							}
							if(!flag) {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setB2b_R_CBW_Taxableamount(gstr3b_gstr1.getB2b_R_CBW_Taxableamount() - gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())) {
									gstr3b_gstr1.setB2b_R_CBW_Taxamount(gstr3b_gstr1.getB2b_R_CBW_Taxamount() - gstr.getTotaltax());
								}
							}
						} else if (docType.equalsIgnoreCase("D")) {
							boolean flag = false;
							if(isNotEmpty(gstr1list)) {
								for(InvoiceParent gstrr1 : gstr1list) {
									if(isNotEmpty(gstrr1) && isNotEmpty(gstrr1.getInvoiceno())) {
										if(org_invno.equals(gstrr1.getInvoiceno())) {
											flag = true;
											if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B) || gstrr1.getInvtype().equals(MasterGSTConstants.B2C) || gstrr1.getInvtype().equals(MasterGSTConstants.B2CL)) {
												String creverseChargeType = "";
												if(isNotEmpty(gstrr1.getRevchargetype())) {
													creverseChargeType = gstrr1.getRevchargetype();
												}else {
													creverseChargeType = "Regular";
												}
												if("Regular".equals(creverseChargeType)) {
													String invType = "";
													if(isNotEmpty(gstrr1.getB2b()) && isNotEmpty(gstrr1.getB2b().get(0).getInv()) && isNotEmpty(gstrr1.getB2b().get(0).getInv().get(0).getInvTyp())) {
														invType = gstrr1.getB2b().get(0).getInv().get(0).getInvTyp();
													}else {
														invType = "R";
													}
													if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B)){
														if("R".equals(invType) || "CBW".equals(invType)) {
															if(isNotEmpty(gstr.getTotaltaxableamount())) {
																gstr3b_gstr1.setB2b_R_CBW_Taxableamount(gstr3b_gstr1.getB2b_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
															}
															if(isNotEmpty(gstr.getTotaltax())) {
																gstr3b_gstr1.setB2b_R_CBW_Taxamount(gstr3b_gstr1.getB2b_R_CBW_Taxamount() + gstr.getTotaltax());
															}
														}											
													}else {
														if("R".equals(invType) || "CBW".equals(invType)) {
															if(isNotEmpty(gstr.getTotaltaxableamount())) {
																gstr3b_gstr1.setB2cl_R_CBW_Taxableamount(gstr3b_gstr1.getB2cl_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
															}
															if(isNotEmpty(gstr.getTotaltax())) {
																gstr3b_gstr1.setB2cl_R_CBW_Taxamount(gstr3b_gstr1.getB2cl_R_CBW_Taxamount() + gstr.getTotaltax());
															}
														}
													}
													if("DE".equals(invType)) {
														if(isNotEmpty(gstr.getTotaltaxableamount())) {
															gstr3b_gstr1.setB2b_CL_DE_Taxableamount(gstr3b_gstr1.getB2b_CL_DE_Taxableamount() + gstr.getTotaltaxableamount());
														}
														if(isNotEmpty(gstr.getTotaltax())) {
															gstr3b_gstr1.setB2b_CL_DE_Taxamount(gstr3b_gstr1.getB2b_CL_DE_Taxamount() + gstr.getTotaltax());
														}
													}else if("SEWP".equals(invType) || "SEWPC".equals(invType)) {
														if(isNotEmpty(gstr.getTotaltaxableamount())) {
															gstr3b_gstr1.setB2b_CL_SEWP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxableamount() + gstr.getTotaltaxableamount());
														}
														if(isNotEmpty(gstr.getTotaltax())) {
															gstr3b_gstr1.setB2b_CL_SEWP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxamount() + gstr.getTotaltax());
														}
													}else if("SEWOP".equals(invType)) {
														if(isNotEmpty(gstr.getTotaltaxableamount())) {
															gstr3b_gstr1.setB2b_CL_SEWOP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxableamount() + gstr.getTotaltaxableamount());
														}
														if(isNotEmpty(gstr.getTotaltax())) {
															gstr3b_gstr1.setB2b_CL_SEWOP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxamount() + gstr.getTotaltax());
														}
													}else if("WPAY".equals(invType)) {
														if(isNotEmpty(gstr.getTotaltaxableamount())) {
															gstr3b_gstr1.setExports_WP_Taxableamount(gstr3b_gstr1.getExports_WP_Taxableamount() + gstr.getTotaltaxableamount());
														}
														if(isNotEmpty(gstr.getTotaltax())) {
															gstr3b_gstr1.setExports_WP_Taxamount(gstr3b_gstr1.getExports_WP_Taxamount() + gstr.getTotaltax());
														}
													}else if("WOPAY".equals(invType)) {
														if(isNotEmpty(gstr.getTotaltaxableamount())) {
															gstr3b_gstr1.setExports_WOP_Taxableamount(gstr.getTotaltaxableamount());
														}
														if(isNotEmpty(gstr.getTotaltax())) {
															gstr3b_gstr1.setExports_WOP_Taxamount(gstr3b_gstr1.getExports_WP_Taxamount() + gstr.getTotaltax());
														}
													}
												}else {
													if(isNotEmpty(gstr.getTotaltaxableamount())) {
														gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
													}
													if(isNotEmpty(gstr.getTotaltax())) {
														gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
													}
												}
											}
										}
									}
								}	
							}
							
							if(!flag) {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setB2b_R_CBW_Taxableamount(gstr3b_gstr1.getB2b_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())) {
									gstr3b_gstr1.setB2b_R_CBW_Taxamount(gstr3b_gstr1.getB2b_R_CBW_Taxamount() + gstr.getTotaltax());
								}
							}
						}
					}
					if (MasterGSTConstants.NIL.equalsIgnoreCase(gstr.getInvtype())) {
						gstr.getItems().forEach(item -> {
							if ("Exempted".equalsIgnoreCase(item.getType()) || "Nil Rated".equalsIgnoreCase(item.getType())) {
								if(isNotEmpty(item.getTaxablevalue())) {
									gstr3b_gstr1.setNilrated_Exempted_Taxableamount(gstr3b_gstr1.getNilrated_Exempted_Taxableamount() + item.getTaxablevalue());
								}//System.out.println(item.getTaxablevalue());
								gstr3b_gstr1.setNilrated_Exempted_Taxamount(0d);
							} else if ("Non-GST".equalsIgnoreCase(item.getType())) {
								gstr3b_gstr1.setNongst_Taxableamount(gstr3b_gstr1.getNongst_Taxableamount() + item.getTaxablevalue());
								gstr3b_gstr1.setNongst_Taxamount(0d);
							}
						});
					}
					if (isNotEmpty(client.getStatename())) {
						Double taxableamount=0d, taxamount=0d;
						int multiply = 1;
						
						
						if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNUR.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNURA.equalsIgnoreCase(gstr.getInvtype())) {
							
							String docType = "";
							if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)) {
								if (isNotEmpty(((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty())) {
									docType = ((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty();
								}
							} else if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)) {
								if (isNotEmpty(((GSTR1) gstr).getCdnur().get(0).getNtty())) {
									docType = gstr.getCdnur().get(0).getNtty();
								}
							}
							if(docType.equalsIgnoreCase("C")) {
								 multiply = -1;
							}else if (docType.equalsIgnoreCase("D")) {
								 multiply = 1;
							}
						}
						
						if(isNotEmpty(gstr.getTotaltaxableamount())) {
							taxableamount += gstr.getTotaltaxableamount() * multiply;
						}
						if(isNotEmpty(gstr.getTotaltax())) {
							taxamount += gstr.getTotaltax() * multiply;
						}
						if (MasterGSTConstants.NIL.equalsIgnoreCase(gstr.getInvtype())) {
							if(isNotEmpty(gstr.getNil()) && isNotEmpty(gstr.getNil().getInv())) {
								for(GSTRNilItems nil : gstr.getNil().getInv()) {
									Double niltaxbleamt = 0d;
									if(isNotEmpty(nil.getExptAmt())) {
										niltaxbleamt += nil.getExptAmt();
									}
									if(isNotEmpty(nil.getNgsupAmt())) {
										niltaxbleamt += nil.getNgsupAmt();
									}
									if(isNotEmpty(nil.getNilAmt())) {
										niltaxbleamt += nil.getNilAmt();
									}
									if(isNotEmpty(nil.getSplyType()) && ("INTRAB2B".equalsIgnoreCase(nil.getSplyType()) || "INTRAB2C".equalsIgnoreCase(nil.getSplyType()))) {
											gstr3b_gstr1.setBook_intra_Taxableamount(gstr3b_gstr1.getBook_intra_Taxableamount() + niltaxbleamt);
									}else {
											gstr3b_gstr1.setBook_inter_Taxableamount(gstr3b_gstr1.getBook_inter_Taxableamount() + niltaxbleamt);
									}
								}
							}
						}else {
							if (client.getStatename().equalsIgnoreCase(gstr.getStatename())) {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setBook_intra_Taxableamount(gstr3b_gstr1.getBook_intra_Taxableamount() + taxableamount);
								}
								if(isNotEmpty(gstr.getTotaltax())) {
									gstr3b_gstr1.setBook_intra_Taxamount(gstr3b_gstr1.getBook_intra_Taxamount() + taxamount);
								}
							} else {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
								gstr3b_gstr1.setBook_inter_Taxableamount(gstr3b_gstr1.getBook_inter_Taxableamount() + taxableamount);
								}
								if(isNotEmpty(gstr.getTotaltax())) {
									gstr3b_gstr1.setBook_inter_Taxamount(gstr3b_gstr1.getBook_inter_Taxamount() + taxamount);
								}
							}
						}
					}
				}
			});
		}
		gstr3b_gstr1.setBook_Total_Taxableamt1(
				gstr3b_gstr1.getB2b_R_CBW_Taxableamount()+
				gstr3b_gstr1.getB2cl_R_CBW_Taxableamount()+
				gstr3b_gstr1.getB2b_CL_DE_Taxableamount()+
				gstr3b_gstr1.getRcm_Taxableamount()+
				gstr3b_gstr1.getB2b_CL_SEWP_Taxableamount()+
				gstr3b_gstr1.getB2b_CL_SEWOP_Taxableamount()+
				gstr3b_gstr1.getExports_WP_Taxableamount()+
				gstr3b_gstr1.getExports_WOP_Taxableamount()+
				gstr3b_gstr1.getNilrated_Exempted_Taxableamount()+
				gstr3b_gstr1.getNongst_Taxableamount());
		gstr3b_gstr1.setBook_Total_Taxamt1(gstr3b_gstr1.getB2b_R_CBW_Taxamount()+
				gstr3b_gstr1.getB2cl_R_CBW_Taxamount()+
				gstr3b_gstr1.getB2b_CL_DE_Taxamount()+
				gstr3b_gstr1.getRcm_Taxamount()+
				gstr3b_gstr1.getB2b_CL_SEWP_Taxamount()+
				gstr3b_gstr1.getB2b_CL_SEWOP_Taxamount()+
				gstr3b_gstr1.getExports_WP_Taxamount()+
				gstr3b_gstr1.getExports_WOP_Taxamount()+
				gstr3b_gstr1.getNilrated_Exempted_Taxamount()+
				gstr3b_gstr1.getNongst_Taxamount());

		gstr3b_gstr1.setBook_Total_Taxableamt2(gstr3b_gstr1.getBook_intra_Taxableamount() + gstr3b_gstr1.getBook_inter_Taxableamount());
		gstr3b_gstr1.setBook_Total_Taxamt2(gstr3b_gstr1.getBook_intra_Taxamount() + gstr3b_gstr1.getBook_inter_Taxamount());

		/**
		 * 
		 * Books-GSTR3B
		 */
		double book_diffTaxableamount1 = gstr3b_gstr1.getB2b_R_CBW_Taxableamount()
				+ gstr3b_gstr1.getB2cl_R_CBW_Taxableamount() + gstr3b_gstr1.getB2b_CL_DE_Taxableamount()
				+ gstr3b_gstr1.getRcm_Taxableamount();
		double book_diffTaxamount1 = gstr3b_gstr1.getB2b_R_CBW_Taxamount() + gstr3b_gstr1.getB2cl_R_CBW_Taxamount()
				+ gstr3b_gstr1.getB2b_CL_DE_Taxamount() + gstr3b_gstr1.getRcm_Taxamount();

		gstr3b_gstr1.setDiffBook_GSTR3B_Taxableamount_Total1(
				book_diffTaxableamount1 - gstr3b_gstr1.getGSTR3B_3a_Taxableamt());
		gstr3b_gstr1.setDiffBook_GSTR3B_Taxamount_Total1(book_diffTaxamount1 - gstr3b_gstr1.getGSTR3B_3a_Taxamt());

		double book_diffTaxableamount2 = gstr3b_gstr1.getB2b_CL_SEWP_Taxableamount()
				+ gstr3b_gstr1.getB2b_CL_SEWOP_Taxableamount() + gstr3b_gstr1.getExports_WP_Taxableamount()
				+ gstr3b_gstr1.getExports_WOP_Taxableamount();
		double book_diffTaxamount2 = gstr3b_gstr1.getB2b_CL_SEWP_Taxamount() + gstr3b_gstr1.getB2b_CL_SEWOP_Taxamount()
				+ gstr3b_gstr1.getExports_WP_Taxamount() + gstr3b_gstr1.getExports_WOP_Taxamount();

		gstr3b_gstr1.setDiffBook_GSTR3B_Taxableamount_Total2(
				book_diffTaxableamount2 - gstr3b_gstr1.getGSTR3B_3b_Taxableamt());
		gstr3b_gstr1.setDiffBook_GSTR3B_Taxamount_Total2(book_diffTaxamount2 - gstr3b_gstr1.getGSTR3B_3b_Taxamt());

		gstr3b_gstr1.setDiffBook_GSTR3B_Nilrated_Exempted_Taxableamount(gstr3b_gstr1.getNilrated_Exempted_Taxableamount() - gstr3b_gstr1.getGSTR3B_3c_Taxableamt());
		gstr3b_gstr1.setDiffBook_GSTR3B_Nilrated_Exempted_Taxamount(gstr3b_gstr1.getNilrated_Exempted_Taxamount() - gstr3b_gstr1.getGSTR3B_3c_Taxamt());

		gstr3b_gstr1.setDiffBook_GSTR3B_NonGST_Taxableamount(gstr3b_gstr1.getNongst_Taxableamount() - gstr3b_gstr1.getGSTR3B_3e_Taxableamt());
		gstr3b_gstr1.setDiffBook_GSTR3B_NonGST_Taxamount(gstr3b_gstr1.getNongst_Taxamount());

		gstr3b_gstr1.setGSTR3B_Total_Taxableamt1(gstr3b_gstr1.getGSTR3B_3a_Taxableamt() + gstr3b_gstr1.getGSTR3B_3b_Taxableamt()+ gstr3b_gstr1.getGSTR3B_3c_Taxableamt() + gstr3b_gstr1.getGSTR3B_3d_Taxableamt());

		//diffBook_GSTR3B_Total_Taxableamount1
		
		
		
		gstr3b_gstr1.setDiffBook_GSTR3B_Total_Taxableamount1(gstr3b_gstr1.getDiffBook_GSTR3B_Taxableamount_Total1()	+ gstr3b_gstr1.getDiffBook_GSTR3B_Taxableamount_Total2()+ gstr3b_gstr1.getDiffBook_GSTR3B_Nilrated_Exempted_Taxableamount()+ gstr3b_gstr1.getDiffBook_GSTR3B_NonGST_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR3B_Total_Taxamount1(gstr3b_gstr1.getDiffBook_GSTR3B_Taxamount_Total1() + gstr3b_gstr1.getDiffBook_GSTR3B_Taxamount_Total2()+ gstr3b_gstr1.getDiffBook_GSTR3B_Nilrated_Exempted_Taxamount()	+ gstr3b_gstr1.getDiffBook_GSTR3B_NonGST_Taxamount());

		gstr3b_gstr1.setDiffBook_GSTR3B_Inter_Taxableamount((gstr3b_gstr1.getBook_inter_Taxableamount() + gstr3b_gstr1.getBook_intra_Taxableamount())- gstr3b_gstr1.getGSTR3B_Total_Taxableamt());
		gstr3b_gstr1.setDiffBook_GSTR3B_Inter_Taxamount(gstr3b_gstr1.getBook_inter_Taxamount() - gstr3b_gstr1.getGSTR3B_IGST());
		gstr3b_gstr1.setDiffBook_GSTR3B_Intra_Taxamount(gstr3b_gstr1.getBook_intra_Taxamount() - gstr3b_gstr1.getGSTR3B_CGST_SGST());
		gstr3b_gstr1.setDiffBook_GSTR3B_Total_Taxableamount2(gstr3b_gstr1.getDiffBook_GSTR3B_Inter_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR3B_Total_Taxamount2(gstr3b_gstr1.getDiffBook_GSTR3B_Inter_Taxamount() + gstr3b_gstr1.getDiffBook_GSTR3B_Intra_Taxamount());

		/**
		 * GSTR1 Data isAmendment=true or GovtInvoiceStatus() == SUCCESS
		 */
		if(isNotEmpty(gstr1List)) {
			gstr1List.forEach(gstr -> {
				//System.out.println(" -->"+ gstr.getB2b().get(0).getInv().get(0).getInvTyp());
				//System.out.println(" --> inv no"+gstr.getInvoiceno()+" -- "+gstr.isAmendment());
				if(isNotEmpty(gstr.getGovtInvoiceStatus())) {
					if(!amendmentRefIds.contains(gstr.getId().toString())) {
						if(MasterGSTConstants.SUCCESS.equalsIgnoreCase(gstr.getGovtInvoiceStatus())) {
								
							if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype())) &&  (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("R") || gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("CBW")))) {
								if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
									if(isNotEmpty(gstr.getTotaltaxableamount())) {
										gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() + gstr.getTotaltaxableamount());
									}
									if(isNotEmpty(gstr.getTotaltax())){
										gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() + gstr.getTotaltax());
									}
								}else {
									if(isNotEmpty(gstr.getTotaltaxableamount())) {
										gstr3b_gstr1.setGstr1b2b_R_CBW_Taxableamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
									}
									if(isNotEmpty(gstr.getTotaltax())){
										gstr3b_gstr1.setGstr1b2b_R_CBW_Taxamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxamount() + gstr.getTotaltax());
									}
								}
							}else if ((MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype()))
															&& (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("R") || gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("CBW")))) {
								if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
									if(isNotEmpty(gstr.getTotaltaxableamount())) {
										gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() + gstr.getTotaltaxableamount());
									}
									if(isNotEmpty(gstr.getTotaltax())){
										gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() + gstr.getTotaltax());
									}
								}else {
									if(isNotEmpty(gstr.getTotaltaxableamount())) {
										gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxableamount(gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
									}
									if(isNotEmpty(gstr.getTotaltax())){
										gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxamount(gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxamount() + gstr.getTotaltax());
									}
								}
							} else if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) 	|| MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype())
									|| MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype()) 	|| MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype())
									|| MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype())) && (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("DE")))) {
								if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
									if(isNotEmpty(gstr.getTotaltaxableamount())) {
										gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() + gstr.getTotaltaxableamount());
									}
									if(isNotEmpty(gstr.getTotaltax())){
										gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() + gstr.getTotaltax());
									}
								}else {
									if(isNotEmpty(gstr.getTotaltaxableamount())) {
										gstr3b_gstr1.setGstr1b2b_CL_DE_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_DE_Taxableamount() + gstr.getTotaltaxableamount());									
									}
									if(isNotEmpty(gstr.getTotaltax())){
										gstr3b_gstr1.setGstr1b2b_CL_DE_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_DE_Taxamount() + gstr.getTotaltax());
									}
								}
							} else if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype())
									|| MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype()) 	|| MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype())
									|| MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype())) && (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("SEWP")))) {
								if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
									if(isNotEmpty(gstr.getTotaltaxableamount())) {
										gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() + gstr.getTotaltaxableamount());
									}
									if(isNotEmpty(gstr.getTotaltax())){
										gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() + gstr.getTotaltax());
									}
								}else {
									if(isNotEmpty(gstr.getTotaltaxableamount())) {
										gstr3b_gstr1.setGstr1b2b_CL_SEWP_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxableamount() + gstr.getTotaltaxableamount());
									}
									if(isNotEmpty(gstr.getTotaltax())){
										gstr3b_gstr1.setGstr1b2b_CL_SEWP_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxamount() + gstr.getTotaltax());
									}
								}
							} else if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype())
										|| MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype()) 	|| MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype())
										|| MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype()))	&& (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("SEWOP")))) {
				
								if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
									if(isNotEmpty(gstr.getTotaltaxableamount())) {
										gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() + gstr.getTotaltaxableamount());
									}
									if(isNotEmpty(gstr.getTotaltax())){
										gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() + gstr.getTotaltax());
									}
								}else {
									if(isNotEmpty(gstr.getTotaltaxableamount())) {
										gstr3b_gstr1.setGstr1b2b_CL_SEWOP_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxableamount() + gstr.getTotaltaxableamount());
									}
									if(isNotEmpty(gstr.getTotaltax())){
										gstr3b_gstr1.setGstr1b2b_CL_SEWOP_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxamount() + gstr.getTotaltax());
									}
								}
							}else if (MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype())){
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxableamount(gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())){
									gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxamount(gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxamount() + gstr.getTotaltax());
								}
							}
							if (MasterGSTConstants.EXPORTS.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.EXPA.equalsIgnoreCase(gstr.getInvtype())) {
								if(isNotEmpty(gstr.getExp()) && isNotEmpty(gstr.getExp().get(0).getExpTyp())) {
									if ("WPAY".equalsIgnoreCase(gstr.getExp().get(0).getExpTyp())) {
										if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
											if(isNotEmpty(gstr.getTotaltaxableamount())) {
												gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() + gstr.getTotaltaxableamount());
											}
											if(isNotEmpty(gstr.getTotaltax())){
												gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() + gstr.getTotaltax());
											}
										}else {
											if(isNotEmpty(gstr.getTotaltaxableamount())) {
												gstr3b_gstr1.setGstr1exports_WP_Taxableamount(gstr3b_gstr1.getGstr1exports_WP_Taxableamount() + gstr.getTotaltaxableamount());
											}
											if(isNotEmpty(gstr.getTotaltax())){
												gstr3b_gstr1.setGstr1exports_WP_Taxamount(gstr3b_gstr1.getGstr1exports_WP_Taxamount() + gstr.getTotaltax());
											}
										}
									} else if ("WOPAY".equalsIgnoreCase(gstr.getExp().get(0).getExpTyp())) {
										if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
											if(isNotEmpty(gstr.getTotaltaxableamount())) {
												gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() + gstr.getTotaltaxableamount());
											}
											if(isNotEmpty(gstr.getTotaltax())){
												gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() + gstr.getTotaltax());
											}
										}else{
											if(isNotEmpty(gstr.getTotaltaxableamount())) {
												gstr3b_gstr1.setGstr1exports_WOP_Taxableamount(gstr3b_gstr1.getGstr1exports_WOP_Taxableamount() + gstr.getTotaltaxableamount());
											}
											if(isNotEmpty(gstr.getTotaltax())){
												gstr3b_gstr1.setGstr1exports_WOP_Taxamount(gstr3b_gstr1.getGstr1exports_WOP_Taxamount() + gstr.getTotaltax());
											}
										}
									}
								}
							}
							if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNUR.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNURA.equalsIgnoreCase(gstr.getInvtype())) {
								String org_invno = "";
								String docType = "";
								if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)) {
									if (isNotEmpty(((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty();
										org_invno =((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getInum(); 
												//orginal_invno.get(((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getInum());
									}
								} else if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)) {
									if (isNotEmpty(((GSTR1) gstr).getCdnur().get(0).getNtty())) {
										docType = gstr.getCdnur().get(0).getNtty();
										org_invno = gstr.getCdnur().get(0).getInum(); 
												//orginal_invno.get(gstr.getCdnur().get(0).getInum());
									}
								}
								
								Date stDate = null;
								Date endDate = null;
								Calendar cal = Calendar.getInstance();
								if(month < 10) {
									cal.set(year-1, 3, 1, 0, 0, 0);
									stDate = new java.util.Date(cal.getTimeInMillis());
									cal = Calendar.getInstance();
									cal.set(year, month, 0, 23, 59, 59);
									endDate = new java.util.Date(cal.getTimeInMillis());
								}else {
									cal.set(year, 3, 1, 0, 0, 0);
									stDate = new java.util.Date(cal.getTimeInMillis());
									cal = Calendar.getInstance();
									cal.set(year, month, 0, 23, 59, 59);
									endDate = new java.util.Date(cal.getTimeInMillis());
								}
								List<GSTR1> gstr1list = gstr1Repository.findByInvoicenoAndClientidAndDateofinvoiceBetween(org_invno, client.getId().toString(), stDate, endDate);
								
								if (docType.equalsIgnoreCase("C")) {
									boolean flag = false;
									if(isNotEmpty(gstr1list)) {
										for(InvoiceParent gstrr1 : gstr1list) {
											if(isNotEmpty(gstrr1.getGovtInvoiceStatus())) {
												if(MasterGSTConstants.SUCCESS.equalsIgnoreCase(gstrr1.getGovtInvoiceStatus())) {
													if(isNotEmpty(gstrr1) && isNotEmpty(gstrr1.getInvoiceno())) {
														if(org_invno.equals(gstrr1.getInvoiceno())) {
															flag = true;
															if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B) || gstrr1.getInvtype().equals(MasterGSTConstants.B2C) || gstrr1.getInvtype().equals(MasterGSTConstants.B2CL)) {
																String creverseChargeType = "";
																if(isNotEmpty(gstrr1.getRevchargetype())) {
																	creverseChargeType = gstrr1.getRevchargetype();
																}else {
																	creverseChargeType = "Regular";
																}
																if("Regular".equals(creverseChargeType)) {
																	String invType = "";
																	if(isNotEmpty(gstrr1.getB2b()) && isNotEmpty(gstrr1.getB2b().get(0).getInv())	&& isNotEmpty(gstrr1.getB2b().get(0).getInv().get(0).getInvTyp())) {
																		invType = gstrr1.getB2b().get(0).getInv().get(0).getInvTyp();
																	}else {
																		invType = "R";
																	}
																	if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B)){
																		if("R".equals(invType) || "CBW".equals(invType)) {
																			if(isNotEmpty(gstr.getTotaltaxableamount())) {
																				gstr3b_gstr1.setGstr1b2b_R_CBW_Taxableamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxableamount() - gstr.getTotaltaxableamount());																		
																			}
																			if(isNotEmpty(gstr.getTotaltax())){
																				gstr3b_gstr1.setGstr1b2b_R_CBW_Taxamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxamount() - gstr.getTotaltax());
																			}
																		}											
																	}else {
																		if("R".equals(invType) || "CBW".equals(invType)) {
																			if(isNotEmpty(gstr.getTotaltaxableamount())) {
																				gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxableamount(gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxableamount() - gstr.getTotaltaxableamount());
																			}
																			if(isNotEmpty(gstr.getTotaltax())){
																				gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxamount(gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxamount() - gstr.getTotaltax());
																			}
																		}
																	}
																	if("DE".equals(invType)) {
																		if(isNotEmpty(gstr.getTotaltaxableamount())) {
																			gstr3b_gstr1.setGstr1b2b_CL_DE_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_DE_Taxableamount() - gstr.getTotaltaxableamount());
																		}
																		if(isNotEmpty(gstr.getTotaltax())){
																			gstr3b_gstr1.setGstr1b2b_CL_DE_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_DE_Taxamount() - gstr.getTotaltax());
																		}
																	}else if("SEWP".equals(invType) || "SEWPC".equals(invType)) {
																		if(isNotEmpty(gstr.getTotaltaxableamount())) {
																			gstr3b_gstr1.setGstr1b2b_CL_SEWP_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxableamount() - gstr.getTotaltaxableamount());
																		}
																		if(isNotEmpty(gstr.getTotaltax())){
																			gstr3b_gstr1.setGstr1b2b_CL_SEWP_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxamount() - gstr.getTotaltax());
																		}
																	}else if("SEWOP".equals(invType)) {
																		if(isNotEmpty(gstr.getTotaltaxableamount())) {
																			gstr3b_gstr1.setGstr1b2b_CL_SEWOP_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxableamount() - gstr.getTotaltaxableamount());
																		}
																		gstr3b_gstr1.setGstr1b2b_CL_SEWOP_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxamount() - gstr.getTotaltax());
																	}else if("WPAY".equals(invType)) {
																		if(isNotEmpty(gstr.getTotaltaxableamount())) {
																			gstr3b_gstr1.setGstr1exports_WP_Taxableamount(gstr3b_gstr1.getGstr1exports_WP_Taxableamount() - gstr.getTotaltaxableamount());
																		}
																		if(isNotEmpty(gstr.getTotaltax())){
																			gstr3b_gstr1.setGstr1exports_WP_Taxamount(gstr3b_gstr1.getGstr1exports_WP_Taxamount() - gstr.getTotaltax());
																		}
																	}else if("WOPAY".equals(invType)) {
																		if(isNotEmpty(gstr.getTotaltaxableamount())) {
																			gstr3b_gstr1.setGstr1exports_WOP_Taxableamount(gstr.getTotaltaxableamount());
																		}
																		if(isNotEmpty(gstr.getTotaltax())){
																			gstr3b_gstr1.setGstr1exports_WOP_Taxamount(gstr3b_gstr1.getGstr1exports_WP_Taxamount() - gstr.getTotaltax());
																		}
																	}
																}else {
																	if(isNotEmpty(gstr.getTotaltaxableamount())) {
																		gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() - gstr.getTotaltaxableamount());
																	}
																	if(isNotEmpty(gstr.getTotaltax())){
																		gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() - gstr.getTotaltax());
																	}
																}
															}
														}
													}
												}
											}
										}
										
									}
									if(!flag) {
										if(isNotEmpty(gstr.getTotaltaxableamount())) {
											gstr3b_gstr1.setGstr1b2b_R_CBW_Taxableamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxableamount() - gstr.getTotaltaxableamount());																		
										}
										if(isNotEmpty(gstr.getTotaltax())){
											gstr3b_gstr1.setGstr1b2b_R_CBW_Taxamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxamount() - gstr.getTotaltax());
										}
									}
								} else if (docType.equalsIgnoreCase("D")) {
									boolean flag = false;
									if(isNotEmpty(gstr1list)) {
										for(InvoiceParent gstrr1 : gstr1list) {
											if(isNotEmpty(gstrr1.getGovtInvoiceStatus())) {
												if(isNotEmpty(gstrr1) && isNotEmpty(gstrr1.getInvoiceno())) {
													if(org_invno.equals(gstrr1.getInvoiceno())) {
														flag = true;
														if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B) || gstrr1.getInvtype().equals(MasterGSTConstants.B2C) || gstrr1.getInvtype().equals(MasterGSTConstants.B2CL)) {
															String creverseChargeType = "";
															if(isNotEmpty(gstrr1.getRevchargetype())) {
																creverseChargeType = gstrr1.getRevchargetype();
															}else {
																creverseChargeType = "Regular";
															}
															if("Regular".equals(creverseChargeType)) {
																String invType = "";
																if(isNotEmpty(gstrr1.getB2b()) && isNotEmpty(gstrr1.getB2b().get(0).getInv()) && isNotEmpty(gstrr1.getB2b().get(0).getInv().get(0).getInvTyp())) {
																	invType = gstrr1.getB2b().get(0).getInv().get(0).getInvTyp();
																}else {
																	invType = "R";
																}
																if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B)){
																	if("R".equals(invType) || "CBW".equals(invType)) {
																		if(isNotEmpty(gstr.getTotaltaxableamount())) {
																			gstr3b_gstr1.setGstr1b2b_R_CBW_Taxableamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
																		}
																		if(isNotEmpty(gstr.getTotaltax())){
																			gstr3b_gstr1.setGstr1b2b_R_CBW_Taxamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxamount() + gstr.getTotaltax());
																		}
																	}											
																}else {
																	if("R".equals(invType) || "CBW".equals(invType)) {
																		if(isNotEmpty(gstr.getTotaltaxableamount())) {
																			gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxableamount(gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
																		}
																		if(isNotEmpty(gstr.getTotaltax())){
																			gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxamount(gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxamount() + gstr.getTotaltax());
																		}
																	}
																}
																if("DE".equals(invType)) {
																	if(isNotEmpty(gstr.getTotaltaxableamount())) {
																		gstr3b_gstr1.setGstr1b2b_CL_DE_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_DE_Taxableamount() + gstr.getTotaltaxableamount());
																	}
																	if(isNotEmpty(gstr.getTotaltax())){
																		gstr3b_gstr1.setGstr1b2b_CL_DE_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_DE_Taxamount() + gstr.getTotaltax());
																	}
																}else if("SEWP".equals(invType) || "SEWPC".equals(invType)) {
																	if(isNotEmpty(gstr.getTotaltaxableamount())) {
																		gstr3b_gstr1.setGstr1b2b_CL_SEWP_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxableamount() + gstr.getTotaltaxableamount());
																	}
																	if(isNotEmpty(gstr.getTotaltax())){
																		gstr3b_gstr1.setGstr1b2b_CL_SEWP_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxamount() + gstr.getTotaltax());
																	}
																}else if("SEWOP".equals(invType)) {
																	if(isNotEmpty(gstr.getTotaltaxableamount())) {
																		gstr3b_gstr1.setGstr1b2b_CL_SEWOP_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxableamount() + gstr.getTotaltaxableamount());
																	}
																	if(isNotEmpty(gstr.getTotaltax())){
																		gstr3b_gstr1.setGstr1b2b_CL_SEWOP_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxamount() + gstr.getTotaltax());
																	}
																}else if("WPAY".equals(invType)) {
																	if(isNotEmpty(gstr.getTotaltaxableamount())) {
																		gstr3b_gstr1.setGstr1exports_WP_Taxableamount(gstr3b_gstr1.getGstr1exports_WP_Taxableamount() + gstr.getTotaltaxableamount());
																	}
																	if(isNotEmpty(gstr.getTotaltax())){
																		gstr3b_gstr1.setGstr1exports_WP_Taxamount(gstr3b_gstr1.getGstr1exports_WP_Taxamount() + gstr.getTotaltax());
																	}
																}else if("WOPAY".equals(invType)) {
																	if(isNotEmpty(gstr.getTotaltaxableamount())) {
																		gstr3b_gstr1.setGstr1exports_WOP_Taxableamount(gstr.getTotaltaxableamount());
																	}
																	if(isNotEmpty(gstr.getTotaltax())){
																		gstr3b_gstr1.setGstr1exports_WOP_Taxamount(gstr3b_gstr1.getGstr1exports_WP_Taxamount() + gstr.getTotaltax());
																	}
																}
															}else {
																if(isNotEmpty(gstr.getTotaltaxableamount())) {
																	gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() + gstr.getTotaltaxableamount());
																}
																if(isNotEmpty(gstr.getTotaltax())){
																	gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() + gstr.getTotaltax());
																}
															}
														}
													}
												}
											}	
										}
									}
									if(!flag) {
										if(isNotEmpty(gstr.getTotaltaxableamount())) {
											gstr3b_gstr1.setGstr1b2b_R_CBW_Taxableamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
										}
										if(isNotEmpty(gstr.getTotaltax())){
											gstr3b_gstr1.setGstr1b2b_R_CBW_Taxamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxamount() + gstr.getTotaltax());
										}
									}
								}
							}
							
							if (MasterGSTConstants.NIL.equalsIgnoreCase(gstr.getInvtype())) {
								gstr.getItems().forEach(item -> {
									if ("Exempted".equalsIgnoreCase(item.getType()) || "Nil Rated".equalsIgnoreCase(item.getType())) {
										if(isNotEmpty(item.getTaxablevalue())) {
											gstr3b_gstr1.setGstr1nilrated_Exempted_Taxableamount(gstr3b_gstr1.getGstr1nilrated_Exempted_Taxableamount() + item.getTaxablevalue());
										}
										gstr3b_gstr1.setGstr1nilrated_Exempted_Taxamount(0d);
									} else if ("Non-GST".equalsIgnoreCase(item.getType())) {
										if(isNotEmpty(item.getTaxablevalue())) {
											gstr3b_gstr1.setGstr1nongst_Taxableamount(gstr3b_gstr1.getGstr1nongst_Taxableamount() + item.getTaxablevalue());
										}
										gstr3b_gstr1.setGstr1nongst_Taxamount(0d);
									}
								});
							}
							if (isNotEmpty(client.getStatename())) {
								Double taxableamount=0d, taxamount=0d;
								int multiply = 1;
								
								if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNUR.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNURA.equalsIgnoreCase(gstr.getInvtype())) {
									
									String docType = "";
									if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)) {
										if (isNotEmpty(((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty())) {
											docType = ((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty();
										}
									} else if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)) {
										if (isNotEmpty(((GSTR1) gstr).getCdnur().get(0).getNtty())) {
											docType = gstr.getCdnur().get(0).getNtty();
										}
									}
									if(docType.equalsIgnoreCase("C")) {
										 multiply = -1;
									}else if (docType.equalsIgnoreCase("D")) {
										 multiply = 1;
									}
								}
								
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									taxableamount += gstr.getTotaltaxableamount() * multiply;
								}
								if(isNotEmpty(gstr.getTotaltax())) {
									taxamount += gstr.getTotaltax() * multiply;
								}
								
								if (MasterGSTConstants.NIL.equalsIgnoreCase(gstr.getInvtype())) {
									if(isNotEmpty(gstr.getNil()) && isNotEmpty(gstr.getNil().getInv())) {
										for(GSTRNilItems nil : gstr.getNil().getInv()) {
											Double niltaxbleamt = 0d;
											if(isNotEmpty(nil.getExptAmt())) {
												niltaxbleamt += nil.getExptAmt();
											}
											if(isNotEmpty(nil.getNgsupAmt())) {
												niltaxbleamt += nil.getNgsupAmt();
											}
											if(isNotEmpty(nil.getNilAmt())) {
												niltaxbleamt += nil.getNilAmt();
											}
											if(isNotEmpty(nil.getSplyType()) && ("INTRAB2B".equalsIgnoreCase(nil.getSplyType()) || "INTRAB2C".equalsIgnoreCase(nil.getSplyType()))) {
													gstr3b_gstr1.setGstr1_intra_Taxableamount(gstr3b_gstr1.getGstr1_intra_Taxableamount() + niltaxbleamt);
											}else {
													gstr3b_gstr1.setGstr1_inter_Taxableamount(gstr3b_gstr1.getGstr1_inter_Taxableamount() + niltaxbleamt);
											}
										}
									}
								}else {
									if (client.getStatename().equalsIgnoreCase(gstr.getStatename())) {
										if(isNotEmpty(gstr.getTotaltaxableamount())) {
											gstr3b_gstr1.setGstr1_intra_Taxableamount(gstr3b_gstr1.getGstr1_intra_Taxableamount() + taxableamount);
										}
										if(isNotEmpty(gstr.getTotaltax())) {
											gstr3b_gstr1.setGstr1_intra_Taxamount(gstr3b_gstr1.getGstr1_intra_Taxamount() + taxamount);
										}
									} else {
										if(isNotEmpty(gstr.getTotaltaxableamount())) {
											gstr3b_gstr1.setGstr1_inter_Taxableamount(gstr3b_gstr1.getGstr1_inter_Taxableamount() + taxableamount);
										}
										if(isNotEmpty(gstr.getTotaltax())) {
											gstr3b_gstr1.setGstr1_inter_Taxamount(gstr3b_gstr1.getGstr1_inter_Taxamount() + taxamount);
										}
									}
								}
							}
						
						}
					}
				}
			});
		}
		gstr3b_gstr1.setGstr1_Total_Taxableamt1(
				gstr3b_gstr1.getGstr1b2b_R_CBW_Taxableamount()+
				gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxableamount()+
				gstr3b_gstr1.getGstr1b2b_CL_DE_Taxableamount()+
				gstr3b_gstr1.getGstr1rcm_Taxableamount()+
				gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxableamount()+
				gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxableamount()+
				gstr3b_gstr1.getGstr1exports_WP_Taxableamount()+
				gstr3b_gstr1.getGstr1exports_WOP_Taxableamount()+
				gstr3b_gstr1.getGstr1nilrated_Exempted_Taxableamount()+
				gstr3b_gstr1.getGstr1nongst_Taxableamount());
		gstr3b_gstr1.setGstr1_Total_Taxamt1(
				gstr3b_gstr1.getGstr1b2b_R_CBW_Taxamount()+
				gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxamount()+
				gstr3b_gstr1.getGstr1b2b_CL_DE_Taxamount()+
				gstr3b_gstr1.getGstr1rcm_Taxamount()+
				gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxamount()+
				gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxamount()+
				gstr3b_gstr1.getGstr1exports_WP_Taxamount()+
				gstr3b_gstr1.getGstr1exports_WOP_Taxamount()+
				gstr3b_gstr1.getGstr1nilrated_Exempted_Taxamount()+
				gstr3b_gstr1.getGstr1nongst_Taxamount());
				
		gstr3b_gstr1.setGstr1_Total_Taxableamt2(gstr3b_gstr1.getGstr1_intra_Taxableamount() + gstr3b_gstr1.getGstr1_inter_Taxableamount());
		gstr3b_gstr1.setGstr1_Total_Taxamt2(gstr3b_gstr1.getGstr1_intra_Taxamount() + gstr3b_gstr1.getGstr1_inter_Taxamount());

		/**
		 * 
		 * GSTR1-GSTR3B
		 */
		Double gstr1_diffTaxableamount1=gstr3b_gstr1.getGstr1b2b_R_CBW_Taxableamount()+
				gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxableamount()+
				gstr3b_gstr1.getGstr1b2b_CL_DE_Taxableamount()+
				gstr3b_gstr1.getGstr1rcm_Taxableamount();
		
		Double gstr1_diffTaxamount1=gstr3b_gstr1.getGstr1b2b_R_CBW_Taxamount()+
				gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxamount()+
				gstr3b_gstr1.getGstr1b2b_CL_DE_Taxamount()+
				gstr3b_gstr1.getGstr1rcm_Taxamount();
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Taxableamount_Total1(gstr3b_gstr1.getGSTR3B_3a_Taxableamt()-gstr1_diffTaxableamount1);
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Taxamount_Total1(gstr3b_gstr1.getGSTR3B_3a_Taxamt()-gstr1_diffTaxamount1);
		
		double gstr3b_diffTaxableamount2 = gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxableamount()+ gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxableamount() +
				gstr3b_gstr1.getGstr1exports_WP_Taxableamount()+gstr3b_gstr1.getGstr1exports_WOP_Taxableamount();
		double gstr3b_diffTaxamount2 = gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxamount() + gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxamount()+ 
										gstr3b_gstr1.getGstr1exports_WP_Taxamount() +gstr3b_gstr1.getGstr1exports_WOP_Taxamount();
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Taxableamount_Total2(gstr3b_gstr1.getGSTR3B_3b_Taxableamt()-gstr3b_diffTaxableamount2);
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Taxamount_Total2(gstr3b_gstr1.getGSTR3B_3b_Taxamt()-gstr3b_diffTaxamount2);

		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Nilrated_Exempted_Taxableamount(gstr3b_gstr1.getGSTR3B_3c_Taxableamt()-gstr3b_gstr1.getGstr1nilrated_Exempted_Taxableamount());
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Nilrated_Exempted_Taxamount(gstr3b_gstr1.getGSTR3B_3c_Taxamt()-gstr3b_gstr1.getGstr1nilrated_Exempted_Taxamount());

		gstr3b_gstr1.setDiffGSTR1_GSTR3B_NonGST_Taxableamount(gstr3b_gstr1.getGSTR3B_3e_Taxableamt()-gstr3b_gstr1.getGstr1nongst_Taxableamount());
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_NonGST_Taxamount(-gstr3b_gstr1.getGstr1nongst_Taxamount());

		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Total_Taxableamount1(gstr3b_gstr1.getDiffGSTR1_GSTR3B_Taxableamount_Total1()+gstr3b_gstr1.getDiffGSTR1_GSTR3B_Taxableamount_Total2()
														+gstr3b_gstr1.getDiffGSTR1_GSTR3B_Nilrated_Exempted_Taxableamount()+gstr3b_gstr1.getDiffGSTR1_GSTR3B_NonGST_Taxableamount());
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Total_Taxamount1(gstr3b_gstr1.getDiffGSTR1_GSTR3B_Taxamount_Total1()+gstr3b_gstr1.getDiffGSTR1_GSTR3B_Taxamount_Total2()
														+gstr3b_gstr1.getDiffGSTR1_GSTR3B_Nilrated_Exempted_Taxamount()+gstr3b_gstr1.getDiffGSTR1_GSTR3B_NonGST_Taxamount());
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Inter_Taxableamount(gstr3b_gstr1.getGSTR3B_Total_Taxableamt()-	(gstr3b_gstr1.getGstr1_inter_Taxableamount() + gstr3b_gstr1.getGstr1_intra_Taxableamount()));
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Inter_Taxamount(gstr3b_gstr1.getGSTR3B_IGST()-gstr3b_gstr1.getGstr1_inter_Taxamount());
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Intra_Taxamount(gstr3b_gstr1.getGSTR3B_CGST_SGST()-gstr3b_gstr1.getGstr1_intra_Taxamount());
		
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Total_Taxamount2(gstr3b_gstr1.getDiffGSTR1_GSTR3B_Inter_Taxamount()+gstr3b_gstr1.getDiffGSTR1_GSTR3B_Intra_Taxamount());
		
		/**
		 * Books-GST1
		 */
		gstr3b_gstr1.setDiffBook_GSTR1_B2B_Taxableamount(gstr3b_gstr1.getB2b_R_CBW_Taxableamount()- gstr3b_gstr1.getGstr1b2b_R_CBW_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_B2B_Taxamount(gstr3b_gstr1.getB2b_R_CBW_Taxamount()- gstr3b_gstr1.getGstr1b2b_R_CBW_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_B2C_Taxableamount(gstr3b_gstr1.getB2cl_R_CBW_Taxableamount()-gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_B2C_Taxamount(gstr3b_gstr1.getB2cl_R_CBW_Taxamount()-gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Deemed_Taxableamount(gstr3b_gstr1.getB2b_CL_DE_Taxableamount()-gstr3b_gstr1.getGstr1b2b_CL_DE_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Deemed_Taxamount(gstr3b_gstr1.getB2b_CL_DE_Taxamount()-gstr3b_gstr1.getGstr1b2b_CL_DE_Taxamount());
		
		gstr3b_gstr1.setDiffBook_GSTR1_RCM_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount()-gstr3b_gstr1.getGstr1rcm_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_RCM_Taxamount(gstr3b_gstr1.getRcm_Taxamount()-gstr3b_gstr1.getGstr1rcm_Taxamount());
		
		gstr3b_gstr1.setDiffBook_GSTR1_SEZWP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxableamount()-gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_SEZWP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxamount()-gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_SEZWOP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxableamount()-gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_SEZWOP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxamount()-gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Export_WP_Taxableamount(gstr3b_gstr1.getExports_WP_Taxableamount()-gstr3b_gstr1.getGstr1exports_WP_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Export_WP_Taxamount(gstr3b_gstr1.getExports_WP_Taxamount()-gstr3b_gstr1.getGstr1exports_WP_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Export_WOP_Taxableamount(gstr3b_gstr1.getExports_WOP_Taxableamount()-gstr3b_gstr1.getGstr1exports_WOP_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Export_WOP_Taxamount(gstr3b_gstr1.getExports_WOP_Taxamount()-gstr3b_gstr1.getGstr1exports_WOP_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Nil_Taxableamount(gstr3b_gstr1.getNilrated_Exempted_Taxableamount()-gstr3b_gstr1.getGstr1nilrated_Exempted_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Nil_Taxamount(gstr3b_gstr1.getNilrated_Exempted_Taxamount()-gstr3b_gstr1.getGstr1nilrated_Exempted_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_NonGst_Taxableamount(gstr3b_gstr1.getNongst_Taxableamount()-gstr3b_gstr1.getGstr1nongst_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_NonGst_Taxamount(gstr3b_gstr1.getNongst_Taxamount()-gstr3b_gstr1.getGstr1nongst_Taxamount());
				
		gstr3b_gstr1.setDiffBook_GSTR1_Inter_Taxableamount(gstr3b_gstr1.getBook_inter_Taxableamount()-gstr3b_gstr1.getGstr1_inter_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Inter_Taxamount(gstr3b_gstr1.getBook_inter_Taxamount()-gstr3b_gstr1.getGstr1_inter_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Intra_Taxableamount(gstr3b_gstr1.getBook_intra_Taxableamount()-gstr3b_gstr1.getGstr1_intra_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Intra_Taxamount(gstr3b_gstr1.getBook_intra_Taxamount()-gstr3b_gstr1.getGstr1_intra_Taxamount());
	
		gstr3b_gstr1.setDiffBook_GSTR1_Total_Taxableamount2(gstr3b_gstr1.getDiffBook_GSTR1_Inter_Taxableamount()+gstr3b_gstr1.getDiffBook_GSTR1_Intra_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Total_Taxamount2(gstr3b_gstr1.getDiffBook_GSTR1_Inter_Taxamount()+gstr3b_gstr1.getDiffBook_GSTR1_Intra_Taxamount());
		
		gstr3b_gstr1.setDiffBook_GSTR1_Total_Taxableamount1(
					gstr3b_gstr1.getDiffBook_GSTR1_B2B_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_B2C_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Deemed_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_RCM_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_SEZWP_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_SEZWOP_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Export_WP_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Export_WOP_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Nil_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_NonGst_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Total_Taxamount1(
					gstr3b_gstr1.getDiffBook_GSTR1_B2B_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_B2C_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Deemed_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_RCM_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_SEZWP_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_SEZWOP_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Export_WP_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Export_WOP_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Nil_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_NonGst_Taxamount());
		
		//System.out.println(orginal_invno);
		//System.out.println(gstr3b_gstr1);
		logger.debug(CLASSNAME + method + "END");
		return gstr3b_gstr1;
	}
	/*
	 * yearly comparison means 12Months	
	 */
	public List<GSTR3B_VS_GSTR1> getYearlyComparision_GSTR3B_VS_GSTR1(String clientid,String userid,int year) throws Exception {
		final String method="getYearlyComparision_GSTR3B_VS_GSTR1";
		logger.debug(CLASSNAME + method + BEGIN);
		List<GSTR3B_VS_GSTR1> gstr3b_vs_gstr1=new ArrayList<GSTR3B_VS_GSTR1>();
		
		for(int month=4;month<=12;month++) {
			GSTR3B_VS_GSTR1 data=comparision_GSTR3B_VS_GSTR1(clientid, userid, month, year);
			if(isNotEmpty(data)) {
				gstr3b_vs_gstr1.add(data);
			}
		}
		for(int month=1;month<=3;month++) {
			int yr=year+1;
			GSTR3B_VS_GSTR1 data=comparision_GSTR3B_VS_GSTR1(clientid, userid, month, yr);
			if(isNotEmpty(data)) {
				gstr3b_vs_gstr1.add(data);
			}
		}
		logger.debug(CLASSNAME + method + "END");
		return gstr3b_vs_gstr1;
	}
	
	/**
	 * GSTR3B-vs-GSTR1 summary yearly report
	 */
	public GSTR3B_VS_GSTR1 getYearlySummaryComparision_GSTR3B_VS_GSTR1(String clientid,String userid,int month, int year) throws Exception {
		final String method="getYearlyComparision_GSTR3B_VS_GSTR1";
		logger.debug(CLASSNAME + method + BEGIN);
		List<GSTR3B> gstr3blist=new ArrayList<GSTR3B>();
		for(int mnth=1;mnth<=12;mnth++) {
			if(mnth<4) {
				int yr=year+1;
				String strMonth = "0" + mnth;
				String returnPeriod = strMonth + yr;
				GSTR3B gstr3b = clientService.getSuppliesInvoice(clientid, returnPeriod);
				if(isNotEmpty(gstr3b)) {
					gstr3blist.add(gstr3b);
				}
			}else {
				String strMonth = mnth < 10 ? "0" + mnth : mnth + "";
				String returnPeriod = strMonth + year;
				GSTR3B gstr3b = clientService.getSuppliesInvoice(clientid, returnPeriod);
				if(isNotEmpty(gstr3b)) {
					gstr3blist.add(gstr3b);
				}
			}
		}
		Client client = clientService.findById(clientid);
		
		Page<? extends InvoiceParent> gstr1Lst=clientService.getInvoices(null, client, userid, MasterGSTConstants.GSTR1,year);
		GSTR3B_VS_GSTR1 gstr3b_gstr1 = new GSTR3B_VS_GSTR1();
		gstr3b_gstr1.setSummaryyear(year+"-"+(year+1));
		gstr3b_gstr1.setGSTR3B_3a_Taxableamt(0d);
		gstr3b_gstr1.setGSTR3B_3a_Taxamt(0d);
		gstr3b_gstr1.setGSTR3B_3b_Taxableamt(0d);
		gstr3b_gstr1.setGSTR3B_3b_Taxamt(0d);
		gstr3b_gstr1.setGSTR3B_3c_Taxableamt(0d);
		gstr3b_gstr1.setGSTR3B_3c_Taxamt(0d);
		gstr3b_gstr1.setGSTR3B_3d_Taxableamt(0d);
		gstr3b_gstr1.setGSTR3B_3d_Taxamt(0d);
		gstr3b_gstr1.setGSTR3B_3e_Taxableamt(0d);
		gstr3b_gstr1.setGSTR3B_3e_Taxamt(0d);
		gstr3b_gstr1.setGSTR3B_Cess(0d);
		gstr3b_gstr1.setGSTR3B_IGST(0d);
		gstr3b_gstr1.setGSTR3B_CGST_SGST(0d);
		gstr3b_gstr1.setGSTR3B_Total_Taxableamt(0d);

		if(isNotEmpty(gstr3blist)) {
			for(GSTR3B gstr3b:gstr3blist) {
				/**
				 * 3.1(a) Taxable Amount Tax Amount -> igst cgst+sgst+cess
				 * supDetails.osupDet.txval
				 */
				if (isNotEmpty(gstr3b.getSupDetails().getOsupDet().getTxval())) {
					gstr3b_gstr1.setGSTR3B_3a_Taxableamt(gstr3b_gstr1.getGSTR3B_3a_Taxableamt() + gstr3b.getSupDetails().getOsupDet().getTxval());
				}
		
				if (isNotEmpty(gstr3b.getSupDetails().getOsupDet().getIamt())) {
					gstr3b_gstr1.setGSTR3B_3a_Taxamt(gstr3b_gstr1.getGSTR3B_3a_Taxamt() + gstr3b.getSupDetails().getOsupDet().getIamt());
				}
		
				if (isNotEmpty(gstr3b.getSupDetails().getOsupDet().getCamt())) {
					gstr3b_gstr1.setGSTR3B_CGST_SGST(gstr3b_gstr1.getGSTR3B_CGST_SGST() + gstr3b.getSupDetails().getOsupDet().getCamt());
					gstr3b_gstr1.setGSTR3B_3a_Taxamt(gstr3b_gstr1.getGSTR3B_3a_Taxamt() + gstr3b.getSupDetails().getOsupDet().getCamt());
				}
				if (isNotEmpty(gstr3b.getSupDetails().getOsupDet().getSamt())) {
					gstr3b_gstr1.setGSTR3B_CGST_SGST(gstr3b_gstr1.getGSTR3B_CGST_SGST() + gstr3b.getSupDetails().getOsupDet().getSamt());
					gstr3b_gstr1.setGSTR3B_3a_Taxamt(gstr3b_gstr1.getGSTR3B_3a_Taxamt() + gstr3b.getSupDetails().getOsupDet().getSamt());
				}
				if (isNotEmpty(gstr3b.getSupDetails().getOsupDet().getCsamt())) {
					gstr3b_gstr1.setGSTR3B_Cess(gstr3b_gstr1.getGSTR3B_Cess() + gstr3b.getSupDetails().getOsupDet().getCsamt());
				}
				/**
				 * 3.2(b) Taxable Amount Tax Amount -> igst supDetails.osupZero.txval
				 */
				if (isNotEmpty(gstr3b.getSupDetails().getOsupZero().getTxval())) {
					gstr3b_gstr1.setGSTR3B_3b_Taxableamt(gstr3b_gstr1.getGSTR3B_3b_Taxableamt() + gstr3b.getSupDetails().getOsupZero().getTxval());
				}
				if (isNotEmpty(gstr3b.getSupDetails().getOsupZero().getIamt())) {
					gstr3b_gstr1.setGSTR3B_3b_Taxamt(gstr3b_gstr1.getGSTR3B_3b_Taxamt() + gstr3b.getSupDetails().getOsupZero().getIamt());
				}
				if (isNotEmpty(gstr3b.getSupDetails().getOsupZero().getCsamt())) {
					gstr3b_gstr1.setGSTR3B_Cess(gstr3b_gstr1.getGSTR3B_Cess() + gstr3b.getSupDetails().getOsupZero().getCsamt());
				}
		
				/**
				 * 3.2(c) Taxable Amount Tax Amount not available.
				 */
				if (isNotEmpty(gstr3b.getSupDetails().getOsupNilExmp().getTxval())) {
					gstr3b_gstr1.setGSTR3B_3c_Taxableamt(gstr3b_gstr1.getGSTR3B_3c_Taxableamt() + gstr3b.getSupDetails().getOsupNilExmp().getTxval());
				}
		
				/**
				 * 3.2(d) Taxable Amount Tax Amount igst+cgst_sgst+cess
				 
				if (isNotEmpty(gstr3b.getSupDetails().getIsupRev().getTxval())) {
					gstr3b_gstr1.setGSTR3B_3d_Taxableamt(gstr3b_gstr1.getGSTR3B_3d_Taxableamt() + gstr3b.getSupDetails().getIsupRev().getTxval());
				}
				if (isNotEmpty(gstr3b.getSupDetails().getIsupRev().getIamt())) {
					gstr3b_gstr1.setGSTR3B_3d_Taxamt(gstr3b_gstr1.getGSTR3B_3d_Taxamt() + gstr3b.getSupDetails().getIsupRev().getIamt());
				}
				if (isNotEmpty(gstr3b.getSupDetails().getIsupRev().getCamt())) {
					gstr3b_gstr1.setGSTR3B_CGST_SGST(gstr3b_gstr1.getGSTR3B_CGST_SGST() + gstr3b.getSupDetails().getIsupRev().getCamt());
					gstr3b_gstr1.setGSTR3B_3d_Taxamt(gstr3b_gstr1.getGSTR3B_3d_Taxamt() + gstr3b.getSupDetails().getIsupRev().getCamt());
				}
				if (isNotEmpty(gstr3b.getSupDetails().getIsupRev().getSamt())) {
					gstr3b_gstr1.setGSTR3B_CGST_SGST(gstr3b_gstr1.getGSTR3B_CGST_SGST() + gstr3b.getSupDetails().getIsupRev().getSamt());
					gstr3b_gstr1.setGSTR3B_3d_Taxamt(gstr3b_gstr1.getGSTR3B_3d_Taxamt() + gstr3b.getSupDetails().getIsupRev().getSamt());
				}
				if (isNotEmpty(gstr3b.getSupDetails().getIsupRev().getCsamt())) {
					gstr3b_gstr1.setGSTR3B_Cess(gstr3b_gstr1.getGSTR3B_Cess() + gstr3b.getSupDetails().getIsupRev().getCsamt());
				}
				 */
				
				/**
				 * 3.2(e) Taxable Amount Tax Amount igst+cgst_sgst+cess
				 */
				if (isNotEmpty(gstr3b.getSupDetails().getOsupNongst().getTxval())) {
					gstr3b_gstr1.setGSTR3B_3e_Taxableamt(gstr3b_gstr1.getGSTR3B_3e_Taxableamt() + gstr3b.getSupDetails().getOsupNongst().getTxval());
				}
			}
		}		
		/**
		 * To set Total Taxamount (3a+3b+3d) To set Total Taxableamount (3a+3b+3c+3d+3e)
		 * To CGST_SGST-> 3a+3d
		 */
		gstr3b_gstr1.setGSTR3B_IGST(gstr3b_gstr1.getGSTR3B_3a_Taxamt() + gstr3b_gstr1.getGSTR3B_3b_Taxamt()
				+ gstr3b_gstr1.getGSTR3B_3d_Taxamt() - gstr3b_gstr1.getGSTR3B_CGST_SGST());
		gstr3b_gstr1.setGSTR3B_Total_Taxableamt(gstr3b_gstr1.getGSTR3B_3a_Taxableamt()+ gstr3b_gstr1.getGSTR3B_3b_Taxableamt() 
				+ gstr3b_gstr1.getGSTR3B_3c_Taxableamt()+ gstr3b_gstr1.getGSTR3B_3e_Taxableamt());

		gstr3b_gstr1.setGSTR3B_Total_Taxableamt1(gstr3b_gstr1.getGSTR3B_3a_Taxableamt() + gstr3b_gstr1.getGSTR3B_3b_Taxableamt()
						+ gstr3b_gstr1.getGSTR3B_3c_Taxableamt());
		gstr3b_gstr1.setGSTR3B_Total_Taxamt2(gstr3b_gstr1.getGSTR3B_IGST() + gstr3b_gstr1.getGSTR3B_CGST_SGST());

		/**
		 * B2B/B2BC(L) INVOICE_TYPE CBW --> Sale from Bonded Warehouse
		 * 
		 * set default values in gstr3b_gstr1 variables
		 */
		gstr3b_gstr1.setB2b_R_CBW_Taxableamount(0d);
		gstr3b_gstr1.setB2b_R_CBW_Taxamount(0d);
		gstr3b_gstr1.setB2cl_R_CBW_Taxableamount(0d);
		gstr3b_gstr1.setB2cl_R_CBW_Taxamount(0d);
		gstr3b_gstr1.setB2b_CL_DE_Taxableamount(0d);
		gstr3b_gstr1.setB2b_CL_DE_Taxamount(0d);
		gstr3b_gstr1.setB2b_CL_SEWP_Taxableamount(0d);
		gstr3b_gstr1.setB2b_CL_SEWP_Taxamount(0d);
		gstr3b_gstr1.setB2b_CL_SEWOP_Taxableamount(0d);
		gstr3b_gstr1.setB2b_CL_SEWOP_Taxamount(0d);

		gstr3b_gstr1.setExports_WP_Taxableamount(0d);
		gstr3b_gstr1.setExports_WP_Taxamount(0d);
		gstr3b_gstr1.setExports_WOP_Taxableamount(0d);
		gstr3b_gstr1.setExports_WOP_Taxamount(0d);

		gstr3b_gstr1.setNilrated_Exempted_Taxableamount(0d);
		gstr3b_gstr1.setNilrated_Exempted_Taxamount(0d);
		gstr3b_gstr1.setNongst_Taxableamount(0d);
		gstr3b_gstr1.setNongst_Taxamount(0d);

		gstr3b_gstr1.setRcm_Taxableamount(0d);
		gstr3b_gstr1.setRcm_Taxamount(0d);
		gstr3b_gstr1.setBook_inter_Taxableamount(0d);
		gstr3b_gstr1.setBook_intra_Taxableamount(0d);
		gstr3b_gstr1.setBook_inter_Taxamount(0d);
		gstr3b_gstr1.setBook_intra_Taxamount(0d);

		/**
		 * GSTR1 Default values
		 */
		
		gstr3b_gstr1.setGstr1b2b_R_CBW_Taxableamount(0d);
		gstr3b_gstr1.setGstr1b2b_R_CBW_Taxamount(0d);
		gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxableamount(0d);
		gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxamount(0d);
		gstr3b_gstr1.setGstr1b2b_CL_DE_Taxableamount(0d);
		gstr3b_gstr1.setGstr1b2b_CL_DE_Taxamount(0d);
		gstr3b_gstr1.setGstr1b2b_CL_SEWP_Taxableamount(0d);
		gstr3b_gstr1.setGstr1b2b_CL_SEWP_Taxamount(0d);
		gstr3b_gstr1.setGstr1b2b_CL_SEWOP_Taxableamount(0d);
		gstr3b_gstr1.setGstr1b2b_CL_SEWOP_Taxamount(0d);

		gstr3b_gstr1.setGstr1exports_WP_Taxableamount(0d);
		gstr3b_gstr1.setGstr1exports_WP_Taxamount(0d);
		gstr3b_gstr1.setGstr1exports_WOP_Taxableamount(0d);
		gstr3b_gstr1.setGstr1exports_WOP_Taxamount(0d);

		gstr3b_gstr1.setGstr1nilrated_Exempted_Taxableamount(0d);
		gstr3b_gstr1.setGstr1nilrated_Exempted_Taxamount(0d);
		gstr3b_gstr1.setGstr1nongst_Taxableamount(0d);
		gstr3b_gstr1.setGstr1nongst_Taxamount(0d);

		gstr3b_gstr1.setGstr1rcm_Taxableamount(0d);
		gstr3b_gstr1.setGstr1rcm_Taxamount(0d);
		gstr3b_gstr1.setGstr1_inter_Taxableamount(0d);
		gstr3b_gstr1.setGstr1_intra_Taxableamount(0d);
		gstr3b_gstr1.setGstr1_inter_Taxamount(0d);
		gstr3b_gstr1.setGstr1_intra_Taxamount(0d);
		
		List<String> invTypes = new ArrayList<String>();
		invTypes.add(MasterGSTConstants.B2BA);
		invTypes.add(MasterGSTConstants.B2CSA);
		invTypes.add(MasterGSTConstants.B2CLA);
		invTypes.add(MasterGSTConstants.CDNA);
		invTypes.add(MasterGSTConstants.CDNURA);
		invTypes.add(MasterGSTConstants.EXPA);
		List<String> amendmentRefIds = Lists.newArrayList();
		if(isNotEmpty(gstr1Lst)) {
			gstr1Lst.forEach(gstr -> {
				if(isNotEmpty(gstr.getInvtype())){
					if(invTypes.contains(gstr.getInvtype()) && isNotEmpty(gstr.getAmendmentRefId())) {
						if(isNotEmpty(gstr.getAmendmentRefId()) && gstr.getAmendmentRefId().size() > 0) {
							amendmentRefIds.addAll(gstr.getAmendmentRefId());
						}
					}
				}
			});
		}
		
		if(isNotEmpty(gstr1Lst)) {
			gstr1Lst.forEach(gstr -> {
				// System.out.println(" -->"+ gstr.getB2b().get(0).getInv().get(0).getInvTyp());
	
				if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype())) && (isNotEmpty(gstr.getB2b())  && isNotEmpty(gstr.getB2b().get(0)) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("R")
								|| gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("CBW")))) {
	
					//orginal_invno.put(gstr.getB2b().get(0).getInv().get(0).getInum(), "B2B_R_CBW");
					if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
						if(isNotEmpty(gstr.getTotaltaxableamount())) {
							gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
						}
						if(isNotEmpty(gstr.getTotaltax())) {
							gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
						}
					}else {
						if(isNotEmpty(gstr.getTotaltaxableamount())) {
							gstr3b_gstr1.setB2b_R_CBW_Taxableamount(gstr3b_gstr1.getB2b_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
						}
						if(isNotEmpty(gstr.getTotaltax())) {
							gstr3b_gstr1.setB2b_R_CBW_Taxamount(gstr3b_gstr1.getB2b_R_CBW_Taxamount() + gstr.getTotaltax());
						}
					}
				} else if ((MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype()))
						&& (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0)) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) &&  (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("R") || gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("CBW")))) {
	
					//orginal_invno.put(gstr.getB2b().get(0).getInv().get(0).getInum(), "B2C_L_CBW");
					if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
						if(isNotEmpty(gstr.getTotaltaxableamount())) {
							gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
						}
						if(isNotEmpty(gstr.getTotaltax())) {
							gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
						}
					}else {
						if(isNotEmpty(gstr.getTotaltaxableamount())) {
							gstr3b_gstr1.setB2cl_R_CBW_Taxableamount(gstr3b_gstr1.getB2cl_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
						}
						if(isNotEmpty(gstr.getTotaltax())) {
							gstr3b_gstr1.setB2cl_R_CBW_Taxamount(gstr3b_gstr1.getB2cl_R_CBW_Taxamount() + gstr.getTotaltax());
						}
					}
				} else if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype())
						|| MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype())
						|| MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype())) && (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0)) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("DE")))) {
	
					//orginal_invno.put(gstr.getB2b().get(0).getInv().get(0).getInum(), "DE");
					if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
						if(isNotEmpty(gstr.getTotaltaxableamount())) {
							gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
						}
						if(isNotEmpty(gstr.getTotaltax())) {
							gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
						}
					}else {
						if(isNotEmpty(gstr.getTotaltaxableamount())) {
							gstr3b_gstr1.setB2b_CL_DE_Taxableamount(gstr3b_gstr1.getB2b_CL_DE_Taxableamount() + gstr.getTotaltaxableamount());
						}
						if(isNotEmpty(gstr.getTotaltax())) {
							gstr3b_gstr1.setB2b_CL_DE_Taxamount(gstr3b_gstr1.getB2b_CL_DE_Taxamount() + gstr.getTotaltax());
						}
					}
				} else if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype())
						|| MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype())
						|| MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype())) && (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0)) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("SEWP")))) {
					//orginal_invno.put(gstr.getB2b().get(0).getInv().get(0).getInum(), "SEWP");
					if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
						if(isNotEmpty(gstr.getTotaltaxableamount())) {
							gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
						}
						if(isNotEmpty(gstr.getTotaltax())) {
							gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
						}
					}else {
						if(isNotEmpty(gstr.getTotaltaxableamount())) {
							gstr3b_gstr1.setB2b_CL_SEWP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxableamount() + gstr.getTotaltaxableamount());
						}
						if(isNotEmpty(gstr.getTotaltax())) {
							gstr3b_gstr1.setB2b_CL_SEWP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxamount() + gstr.getTotaltax());
						}
					}
				} else if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype())
						|| MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype())
						|| MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype()))	&& (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0)) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("SEWOP")))) {
	
					//orginal_invno.put(gstr.getB2b().get(0).getInv().get(0).getInum(), "SEWOP");
					if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
						if(isNotEmpty(gstr.getTotaltaxableamount())) {
							gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
						}
						if(isNotEmpty(gstr.getTotaltax())) {
							gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
						}
					}else {
						if(isNotEmpty(gstr.getTotaltaxableamount())) {
							gstr3b_gstr1.setB2b_CL_SEWOP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxableamount() + gstr.getTotaltaxableamount());
						}
						if(isNotEmpty(gstr.getTotaltax())) {
							gstr3b_gstr1.setB2b_CL_SEWOP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxamount() + gstr.getTotaltax());
						}
					}
				}else if (MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype())	|| MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype())){
						
					if(isNotEmpty(gstr.getTotaltaxableamount())) {
						gstr3b_gstr1.setB2cl_R_CBW_Taxableamount(gstr3b_gstr1.getB2cl_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
					}
					if(isNotEmpty(gstr.getTotaltax())) {
						gstr3b_gstr1.setB2cl_R_CBW_Taxamount(gstr3b_gstr1.getB2cl_R_CBW_Taxamount() + gstr.getTotaltax());
					}
				}
	
				if (MasterGSTConstants.EXPORTS.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.EXPA.equalsIgnoreCase(gstr.getInvtype())) {
					if(isNotEmpty(gstr.getExp()) && isNotEmpty(gstr.getExp().get(0)) && isNotEmpty(gstr.getExp().get(0).getExpTyp())) {
						if ("WPAY".equalsIgnoreCase(gstr.getExp().get(0).getExpTyp())) {
							//orginal_invno.put(gstr.getB2b().get(0).getInv().get(0).getInum(), "EXPORT_WPAY");
							if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())) {
									gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
								}
							}else{
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setExports_WP_Taxableamount(gstr3b_gstr1.getExports_WP_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())) {
									gstr3b_gstr1.setExports_WP_Taxamount(gstr3b_gstr1.getExports_WP_Taxamount() + gstr.getTotaltax());
								}
							}
						} else if ("WOPAY".equalsIgnoreCase(gstr.getExp().get(0).getExpTyp())) {
							//orginal_invno.put(gstr.getB2b().get(0).getInv().get(0).getInum(), "EXPORT_WOPAY");
							if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())) {
									gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
								}
							}else {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setExports_WOP_Taxableamount(gstr3b_gstr1.getExports_WOP_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())) {
									gstr3b_gstr1.setExports_WOP_Taxamount(gstr3b_gstr1.getExports_WOP_Taxamount() + gstr.getTotaltax());
								}
							}
						}
					}
				}
	
				if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNUR.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNURA.equalsIgnoreCase(gstr.getInvtype())) {
	
					String org_invno = "";
					String docType = "";
					if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)) {
						if (isNotEmpty(((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty())) {
							docType = ((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty();
							org_invno =((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getInum(); 
									//orginal_invno.get(((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getInum());
						}
					} else if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)) {
						if (isNotEmpty(((GSTR1) gstr).getCdnur().get(0).getNtty())) {
							docType = gstr.getCdnur().get(0).getNtty();
							org_invno = gstr.getCdnur().get(0).getInum(); 
									//orginal_invno.get(gstr.getCdnur().get(0).getInum());
						}
					}
					
					Date stDate = null;
					Date endDate = null;
					Calendar cal = Calendar.getInstance();
					if(month < 10) {
						cal.set(year-1, 3, 1, 0, 0, 0);
						stDate = new java.util.Date(cal.getTimeInMillis());
						cal = Calendar.getInstance();
						cal.set(year, month, 0, 0, 0, 0);
						endDate = new java.util.Date(cal.getTimeInMillis());
					}else {
						cal.set(year, 3, 1, 0, 0, 0);
						stDate = new java.util.Date(cal.getTimeInMillis());
						cal = Calendar.getInstance();
						cal.set(year, month, 0, 0, 0, 0);
						endDate = new java.util.Date(cal.getTimeInMillis());
					}
					List<GSTR1> gstr1list = gstr1Repository.findByInvoicenoAndClientidAndDateofinvoiceBetween(org_invno, client.getId().toString(), stDate, endDate);
					
					if (docType.equalsIgnoreCase("C")) {
						boolean flag = false;
						if(isNotEmpty(gstr1list)) {
							for(InvoiceParent gstrr1 : gstr1list) {
								if(isNotEmpty(gstrr1) && isNotEmpty(gstrr1.getInvoiceno())) {
									if(org_invno.equals(gstrr1.getInvoiceno())) {
										flag = true;
										if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B) || gstrr1.getInvtype().equals(MasterGSTConstants.B2C) || gstrr1.getInvtype().equals(MasterGSTConstants.B2CL)) {
											String creverseChargeType = "";
											if(isNotEmpty(gstrr1.getRevchargetype())) {
												creverseChargeType = gstrr1.getRevchargetype();
											}else {
												creverseChargeType = "Regular";
											}
											if("Regular".equals(creverseChargeType)) {
												String invType = "";
												if(isNotEmpty(gstrr1.getB2b()) && isNotEmpty(gstrr1.getB2b().get(0)) && isNotEmpty(gstrr1.getB2b().get(0).getInv())	&& isNotEmpty(gstrr1.getB2b().get(0).getInv().get(0).getInvTyp())) {
													invType = gstrr1.getB2b().get(0).getInv().get(0).getInvTyp();
												}else {
													invType = "R";
												}
												if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B)){
													if("R".equals(invType) || "CBW".equals(invType)) {
														if(isNotEmpty(gstr.getTotaltaxableamount())) {
															gstr3b_gstr1.setB2b_R_CBW_Taxableamount(gstr3b_gstr1.getB2b_R_CBW_Taxableamount() - gstr.getTotaltaxableamount());
														}
														if(isNotEmpty(gstr.getTotaltax())) {
															gstr3b_gstr1.setB2b_R_CBW_Taxamount(gstr3b_gstr1.getB2b_R_CBW_Taxamount() - gstr.getTotaltax());
														}
													}											
												}else {
													if("R".equals(invType) || "CBW".equals(invType)) {
														if(isNotEmpty(gstr.getTotaltaxableamount())) {
															gstr3b_gstr1.setB2cl_R_CBW_Taxableamount(gstr3b_gstr1.getB2cl_R_CBW_Taxableamount() - gstr.getTotaltaxableamount());
														}
														if(isNotEmpty(gstr.getTotaltax())) {
															gstr3b_gstr1.setB2cl_R_CBW_Taxamount(gstr3b_gstr1.getB2cl_R_CBW_Taxamount() - gstr.getTotaltax());
														}
													}
												}
												if("DE".equals(invType)) {
													if(isNotEmpty(gstr.getTotaltaxableamount())) {
														gstr3b_gstr1.setB2b_CL_DE_Taxableamount(gstr3b_gstr1.getB2b_CL_DE_Taxableamount() - gstr.getTotaltaxableamount());
													}
													if(isNotEmpty(gstr.getTotaltax())) {
														gstr3b_gstr1.setB2b_CL_DE_Taxamount(gstr3b_gstr1.getB2b_CL_DE_Taxamount() - gstr.getTotaltax());
													}
												}else if("SEWP".equals(invType) || "SEWPC".equals(invType)) {
													if(isNotEmpty(gstr.getTotaltaxableamount())) {
														gstr3b_gstr1.setB2b_CL_SEWP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxableamount() - gstr.getTotaltaxableamount());
													}
													if(isNotEmpty(gstr.getTotaltax())) {
														gstr3b_gstr1.setB2b_CL_SEWP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxamount() - gstr.getTotaltax());
													}
												}else if("SEWOP".equals(invType)) {
													if(isNotEmpty(gstr.getTotaltaxableamount())) {
														gstr3b_gstr1.setB2b_CL_SEWOP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxableamount() - gstr.getTotaltaxableamount());
													}
													if(isNotEmpty(gstr.getTotaltax())) {
														gstr3b_gstr1.setB2b_CL_SEWOP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxamount() - gstr.getTotaltax());
													}
												}else if("WPAY".equals(invType)) {
													if(isNotEmpty(gstr.getTotaltaxableamount())) {
														gstr3b_gstr1.setExports_WP_Taxableamount(gstr3b_gstr1.getExports_WP_Taxableamount() - gstr.getTotaltaxableamount());
													}
													if(isNotEmpty(gstr.getTotaltax())) {
														gstr3b_gstr1.setExports_WP_Taxamount(gstr3b_gstr1.getExports_WP_Taxamount() - gstr.getTotaltax());
													}
												}else if("WOPAY".equals(invType)) {
													if(isNotEmpty(gstr.getTotaltaxableamount())) {
														gstr3b_gstr1.setExports_WOP_Taxableamount(gstr3b_gstr1.getExports_WOP_Taxableamount() - gstr.getTotaltaxableamount());
													}
													if(isNotEmpty(gstr.getTotaltax())) {
														gstr3b_gstr1.setExports_WOP_Taxamount(gstr3b_gstr1.getExports_WOP_Taxamount() - gstr.getTotaltax());
													}
												}
											}else {
												if(isNotEmpty(gstr.getTotaltaxableamount())) {
													gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() - gstr.getTotaltaxableamount());
												}
												if(isNotEmpty(gstr.getTotaltax())) {
													gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() - gstr.getTotaltax());
												}
											}
										}
									}
								}
							}	
						}
						if(!flag) {
							if(isNotEmpty(gstr.getTotaltaxableamount())) {
								gstr3b_gstr1.setB2b_R_CBW_Taxableamount(gstr3b_gstr1.getB2b_R_CBW_Taxableamount() - gstr.getTotaltaxableamount());
							}
							if(isNotEmpty(gstr.getTotaltax())) {
								gstr3b_gstr1.setB2b_R_CBW_Taxamount(gstr3b_gstr1.getB2b_R_CBW_Taxamount() - gstr.getTotaltax());
							}
						}
					} else if (docType.equalsIgnoreCase("D")) {
						boolean flag = false;
						if(isNotEmpty(gstr1list)) {
							for(InvoiceParent gstrr1 : gstr1list) {
								if(isNotEmpty(gstrr1) && isNotEmpty(gstrr1.getInvoiceno())) {
									if(org_invno.equals(gstrr1.getInvoiceno())) {
										flag = true;
										if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B) || gstrr1.getInvtype().equals(MasterGSTConstants.B2C) || gstrr1.getInvtype().equals(MasterGSTConstants.B2CL)) {
											String creverseChargeType = "";
											if(isNotEmpty(gstrr1.getRevchargetype())) {
												creverseChargeType = gstrr1.getRevchargetype();
											}else {
												creverseChargeType = "Regular";
											}
											if("Regular".equals(creverseChargeType)) {
												String invType = "";
												if(isNotEmpty(gstrr1.getB2b()) && isNotEmpty(gstrr1.getB2b().get(0)) && isNotEmpty(gstrr1.getB2b().get(0).getInv()) && isNotEmpty(gstrr1.getB2b().get(0).getInv().get(0).getInvTyp())) {
													invType = gstrr1.getB2b().get(0).getInv().get(0).getInvTyp();
												}else {
													invType = "R";
												}
												if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B)){
													if("R".equals(invType) || "CBW".equals(invType)) {
														if(isNotEmpty(gstr.getTotaltaxableamount())) {
															gstr3b_gstr1.setB2b_R_CBW_Taxableamount(gstr3b_gstr1.getB2b_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
														}
														if(isNotEmpty(gstr.getTotaltax())) {
															gstr3b_gstr1.setB2b_R_CBW_Taxamount(gstr3b_gstr1.getB2b_R_CBW_Taxamount() + gstr.getTotaltax());
														}
													}											
												}else {
													if("R".equals(invType) || "CBW".equals(invType)) {
														if(isNotEmpty(gstr.getTotaltaxableamount())) {
															gstr3b_gstr1.setB2cl_R_CBW_Taxableamount(gstr3b_gstr1.getB2cl_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
														}
														if(isNotEmpty(gstr.getTotaltax())) {
															gstr3b_gstr1.setB2cl_R_CBW_Taxamount(gstr3b_gstr1.getB2cl_R_CBW_Taxamount() + gstr.getTotaltax());
														}
													}
												}
												if("DE".equals(invType)) {
													if(isNotEmpty(gstr.getTotaltaxableamount())) {
														gstr3b_gstr1.setB2b_CL_DE_Taxableamount(gstr3b_gstr1.getB2b_CL_DE_Taxableamount() + gstr.getTotaltaxableamount());
													}
													if(isNotEmpty(gstr.getTotaltax())) {
														gstr3b_gstr1.setB2b_CL_DE_Taxamount(gstr3b_gstr1.getB2b_CL_DE_Taxamount() + gstr.getTotaltax());
													}
												}else if("SEWP".equals(invType)) {
													if(isNotEmpty(gstr.getTotaltaxableamount())) {
														gstr3b_gstr1.setB2b_CL_SEWP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxableamount() + gstr.getTotaltaxableamount());
													}
													if(isNotEmpty(gstr.getTotaltax())) {
														gstr3b_gstr1.setB2b_CL_SEWP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxamount() + gstr.getTotaltax());
													}
												}else if("SEWOP".equals(invType)) {
													if(isNotEmpty(gstr.getTotaltaxableamount())) {
														gstr3b_gstr1.setB2b_CL_SEWOP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxableamount() + gstr.getTotaltaxableamount());
													}
													if(isNotEmpty(gstr.getTotaltax())) {
														gstr3b_gstr1.setB2b_CL_SEWOP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxamount() + gstr.getTotaltax());
													}
												}else if("WPAY".equals(invType)) {
													if(isNotEmpty(gstr.getTotaltaxableamount())) {
														gstr3b_gstr1.setExports_WP_Taxableamount(gstr3b_gstr1.getExports_WP_Taxableamount() + gstr.getTotaltaxableamount());
													}
													if(isNotEmpty(gstr.getTotaltax())) {
														gstr3b_gstr1.setExports_WP_Taxamount(gstr3b_gstr1.getExports_WP_Taxamount() + gstr.getTotaltax());
													}
												}else if("WOPAY".equals(invType)) {
													if(isNotEmpty(gstr.getTotaltaxableamount())) {
														gstr3b_gstr1.setExports_WOP_Taxableamount(gstr3b_gstr1.getExports_WOP_Taxableamount() + gstr.getTotaltaxableamount());
													}
													if(isNotEmpty(gstr.getTotaltax())) {
														gstr3b_gstr1.setExports_WOP_Taxamount(gstr3b_gstr1.getExports_WOP_Taxamount() + gstr.getTotaltax());
													}
												}
											}else {
												if(isNotEmpty(gstr.getTotaltaxableamount())) {
													gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
												}
												if(isNotEmpty(gstr.getTotaltax())) {
													gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
												}
											}
										}
									}
								}
							}	
						}
						if(!flag) {
							if(isNotEmpty(gstr.getTotaltaxableamount())) {
								gstr3b_gstr1.setB2b_R_CBW_Taxableamount(gstr3b_gstr1.getB2b_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
							}
							if(isNotEmpty(gstr.getTotaltax())) {
								gstr3b_gstr1.setB2b_R_CBW_Taxamount(gstr3b_gstr1.getB2b_R_CBW_Taxamount() + gstr.getTotaltax());
							}
						}
					}
				}
				if (MasterGSTConstants.NIL.equalsIgnoreCase(gstr.getInvtype())) {
					gstr.getItems().forEach(item -> {
						if ("Exempted".equalsIgnoreCase(item.getType()) || "Nil Rated".equalsIgnoreCase(item.getType())) {
							if(isNotEmpty(item.getTaxablevalue())) {
								gstr3b_gstr1.setNilrated_Exempted_Taxableamount(gstr3b_gstr1.getNilrated_Exempted_Taxableamount() + item.getTaxablevalue());
							}
							gstr3b_gstr1.setNilrated_Exempted_Taxamount(0d);
						} else if ("Non-GST".equalsIgnoreCase(item.getType())) {
							if(isNotEmpty(item.getTaxablevalue())) {
								gstr3b_gstr1.setNongst_Taxableamount(gstr3b_gstr1.getNongst_Taxableamount() + item.getTaxablevalue());
							}
							gstr3b_gstr1.setNongst_Taxamount(0d);
						}
					});
				}
				if (isNotEmpty(client.getStatename())) {
					Double taxableamount=0d, taxamount=0d;
					int multiply = 1;
					
					if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNUR.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNURA.equalsIgnoreCase(gstr.getInvtype())) {
						
						String docType = "";
						if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)) {
							if (isNotEmpty(((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty())) {
								docType = ((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty();
							}
						} else if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)) {
							if (isNotEmpty(((GSTR1) gstr).getCdnur().get(0).getNtty())) {
								docType = gstr.getCdnur().get(0).getNtty();
							}
						}
						if(docType.equalsIgnoreCase("C")) {
							 multiply = -1;
						}else if (docType.equalsIgnoreCase("D")) {
							 multiply = 1;
						}
					}
					
					if(isNotEmpty(gstr.getTotaltaxableamount())) {
						taxableamount += gstr.getTotaltaxableamount() * multiply;
					}
					if(isNotEmpty(gstr.getTotaltax())) {
						taxamount += gstr.getTotaltax() * multiply;
					}
					if (MasterGSTConstants.NIL.equalsIgnoreCase(gstr.getInvtype())) {
						if(isNotEmpty(gstr.getNil()) && isNotEmpty(gstr.getNil().getInv())) {
							for(GSTRNilItems nil : gstr.getNil().getInv()) {
								Double niltaxbleamt = 0d;
								if(isNotEmpty(nil.getExptAmt())) {
									niltaxbleamt += nil.getExptAmt();
								}
								if(isNotEmpty(nil.getNgsupAmt())) {
									niltaxbleamt += nil.getNgsupAmt();
								}
								if(isNotEmpty(nil.getNilAmt())) {
									niltaxbleamt += nil.getNilAmt();
								}
								if(isNotEmpty(nil.getSplyType()) && ("INTRAB2B".equalsIgnoreCase(nil.getSplyType()) || "INTRAB2C".equalsIgnoreCase(nil.getSplyType()))) {
										gstr3b_gstr1.setBook_intra_Taxableamount(gstr3b_gstr1.getBook_intra_Taxableamount() + niltaxbleamt);
								}else {
										gstr3b_gstr1.setBook_inter_Taxableamount(gstr3b_gstr1.getBook_inter_Taxableamount() + niltaxbleamt);
								}
							}
						}
					}else {
						String clntstatename = client.getStatename();
						if(isNotEmpty(gstr.getInvtype()) && (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.EXPORTS) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.EXPA))) {
							gstr.setStatename("97-Other Territory");
						}
						String invstatename = "";
						if(isNotEmpty(gstr.getStatename())) {
							invstatename = gstr.getStatename();
						}else {
							gstr.setStatename("97-Other Territory");
							invstatename = "97-Other Territory";
						}
						if(clntstatename.contains("-")) {
							clntstatename = clntstatename.substring(3);
						}
						if(invstatename.contains("-")) {
							invstatename = invstatename.substring(3);
						}
						if (client.getStatename().equalsIgnoreCase(gstr.getStatename()) || clntstatename.equalsIgnoreCase(invstatename) || client.getStatename().substring(0, 2).equalsIgnoreCase(gstr.getStatename().substring(0, 2))) {
							if(isNotEmpty(gstr.getTotaltaxableamount())) {
								gstr3b_gstr1.setBook_intra_Taxableamount(gstr3b_gstr1.getBook_intra_Taxableamount() + taxableamount);
							}
							if(isNotEmpty(gstr.getTotaltax())) {
								gstr3b_gstr1.setBook_intra_Taxamount(gstr3b_gstr1.getBook_intra_Taxamount() + taxamount);
							}
						} else {
							if(isNotEmpty(gstr.getTotaltaxableamount())) {
								gstr3b_gstr1.setBook_inter_Taxableamount(gstr3b_gstr1.getBook_inter_Taxableamount() + taxableamount);
							}
							if(isNotEmpty(gstr.getTotaltax())) {
								gstr3b_gstr1.setBook_inter_Taxamount(gstr3b_gstr1.getBook_inter_Taxamount() + taxamount);
							}
						}
					}
				}
			});
		}
		gstr3b_gstr1.setBook_Total_Taxableamt1(
				gstr3b_gstr1.getB2b_R_CBW_Taxableamount()+
				gstr3b_gstr1.getB2cl_R_CBW_Taxableamount()+
				gstr3b_gstr1.getB2b_CL_DE_Taxableamount()+
				gstr3b_gstr1.getRcm_Taxableamount()+
				gstr3b_gstr1.getB2b_CL_SEWP_Taxableamount()+
				gstr3b_gstr1.getB2b_CL_SEWOP_Taxableamount()+
				gstr3b_gstr1.getExports_WP_Taxableamount()+
				gstr3b_gstr1.getExports_WOP_Taxableamount()+
				gstr3b_gstr1.getNilrated_Exempted_Taxableamount()+
				gstr3b_gstr1.getNongst_Taxableamount());
		gstr3b_gstr1.setBook_Total_Taxamt1(gstr3b_gstr1.getB2b_R_CBW_Taxamount()+
				gstr3b_gstr1.getB2cl_R_CBW_Taxamount()+
				gstr3b_gstr1.getB2b_CL_DE_Taxamount()+
				gstr3b_gstr1.getRcm_Taxamount()+
				gstr3b_gstr1.getB2b_CL_SEWP_Taxamount()+
				gstr3b_gstr1.getB2b_CL_SEWOP_Taxamount()+
				gstr3b_gstr1.getExports_WP_Taxamount()+
				gstr3b_gstr1.getExports_WOP_Taxamount()+
				gstr3b_gstr1.getNilrated_Exempted_Taxamount()+
				gstr3b_gstr1.getNongst_Taxamount());

		gstr3b_gstr1.setBook_Total_Taxableamt2(gstr3b_gstr1.getBook_intra_Taxableamount() + gstr3b_gstr1.getBook_inter_Taxableamount());
		gstr3b_gstr1.setBook_Total_Taxamt2(gstr3b_gstr1.getBook_intra_Taxamount() + gstr3b_gstr1.getBook_inter_Taxamount());

		/**
		 * 
		 * Books-GSTR3B
		 */
		double book_diffTaxableamount1 = gstr3b_gstr1.getB2b_R_CBW_Taxableamount()
				+ gstr3b_gstr1.getB2cl_R_CBW_Taxableamount() + gstr3b_gstr1.getB2b_CL_DE_Taxableamount()
				+ gstr3b_gstr1.getRcm_Taxableamount();
		double book_diffTaxamount1 = gstr3b_gstr1.getB2b_R_CBW_Taxamount() + gstr3b_gstr1.getB2cl_R_CBW_Taxamount()
				+ gstr3b_gstr1.getB2b_CL_DE_Taxamount() + gstr3b_gstr1.getRcm_Taxamount();

		gstr3b_gstr1.setDiffBook_GSTR3B_Taxableamount_Total1(
				book_diffTaxableamount1 - gstr3b_gstr1.getGSTR3B_3a_Taxableamt());
		gstr3b_gstr1.setDiffBook_GSTR3B_Taxamount_Total1(book_diffTaxamount1 - gstr3b_gstr1.getGSTR3B_3a_Taxamt());

		double book_diffTaxableamount2 = gstr3b_gstr1.getB2b_CL_SEWP_Taxableamount()
				+ gstr3b_gstr1.getB2b_CL_SEWOP_Taxableamount() + gstr3b_gstr1.getExports_WP_Taxableamount()
				+ gstr3b_gstr1.getExports_WOP_Taxableamount();
		double book_diffTaxamount2 = gstr3b_gstr1.getB2b_CL_SEWP_Taxamount() + gstr3b_gstr1.getB2b_CL_SEWOP_Taxamount()
				+ gstr3b_gstr1.getExports_WP_Taxamount() + gstr3b_gstr1.getExports_WOP_Taxamount();

		gstr3b_gstr1.setDiffBook_GSTR3B_Taxableamount_Total2(
				book_diffTaxableamount2 - gstr3b_gstr1.getGSTR3B_3b_Taxableamt());
		gstr3b_gstr1.setDiffBook_GSTR3B_Taxamount_Total2(book_diffTaxamount2 - gstr3b_gstr1.getGSTR3B_3b_Taxamt());

		gstr3b_gstr1.setDiffBook_GSTR3B_Nilrated_Exempted_Taxableamount(gstr3b_gstr1.getNilrated_Exempted_Taxableamount() - gstr3b_gstr1.getGSTR3B_3c_Taxableamt());
		gstr3b_gstr1.setDiffBook_GSTR3B_Nilrated_Exempted_Taxamount(gstr3b_gstr1.getNilrated_Exempted_Taxamount() - gstr3b_gstr1.getGSTR3B_3c_Taxamt());

		gstr3b_gstr1.setDiffBook_GSTR3B_NonGST_Taxableamount(gstr3b_gstr1.getNongst_Taxableamount() - gstr3b_gstr1.getGSTR3B_3e_Taxableamt());
		gstr3b_gstr1.setDiffBook_GSTR3B_NonGST_Taxamount(gstr3b_gstr1.getNongst_Taxamount());

		gstr3b_gstr1.setGSTR3B_Total_Taxableamt1(gstr3b_gstr1.getGSTR3B_3a_Taxableamt() + gstr3b_gstr1.getGSTR3B_3b_Taxableamt()+ gstr3b_gstr1.getGSTR3B_3c_Taxableamt() + gstr3b_gstr1.getGSTR3B_3d_Taxableamt());

		
		
		
		
		gstr3b_gstr1.setDiffBook_GSTR3B_Total_Taxableamount1(gstr3b_gstr1.getDiffBook_GSTR3B_Taxableamount_Total1()	+ gstr3b_gstr1.getDiffBook_GSTR3B_Taxableamount_Total2()+ gstr3b_gstr1.getDiffBook_GSTR3B_Nilrated_Exempted_Taxableamount()+ gstr3b_gstr1.getDiffBook_GSTR3B_NonGST_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR3B_Total_Taxamount1(gstr3b_gstr1.getDiffBook_GSTR3B_Taxamount_Total1() + gstr3b_gstr1.getDiffBook_GSTR3B_Taxamount_Total2()+ gstr3b_gstr1.getDiffBook_GSTR3B_Nilrated_Exempted_Taxamount()	+ gstr3b_gstr1.getDiffBook_GSTR3B_NonGST_Taxamount());

		gstr3b_gstr1.setDiffBook_GSTR3B_Inter_Taxableamount((gstr3b_gstr1.getBook_inter_Taxableamount() + gstr3b_gstr1.getBook_intra_Taxableamount())- gstr3b_gstr1.getGSTR3B_Total_Taxableamt());
		gstr3b_gstr1.setDiffBook_GSTR3B_Inter_Taxamount(gstr3b_gstr1.getBook_inter_Taxamount() - gstr3b_gstr1.getGSTR3B_IGST());
		gstr3b_gstr1.setDiffBook_GSTR3B_Intra_Taxamount(gstr3b_gstr1.getBook_intra_Taxamount() - gstr3b_gstr1.getGSTR3B_CGST_SGST());
		gstr3b_gstr1.setDiffBook_GSTR3B_Total_Taxableamount2(gstr3b_gstr1.getDiffBook_GSTR3B_Inter_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR3B_Total_Taxamount2(gstr3b_gstr1.getDiffBook_GSTR3B_Inter_Taxamount() + gstr3b_gstr1.getDiffBook_GSTR3B_Intra_Taxamount());

		/**
		 * GSTR1 Data isAmendment=true or getGovtInvoiceStatus() == SUCCESS
		 */
		if(isNotEmpty(gstr1Lst)){
			gstr1Lst.forEach(gstr -> {
				if(isNotEmpty(gstr.getGovtInvoiceStatus())) {
					if(!amendmentRefIds.contains(gstr.getId().toString())) {
					if(MasterGSTConstants.SUCCESS.equalsIgnoreCase(gstr.getGovtInvoiceStatus())) {
						if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype())) && (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0)) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("R") || gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("CBW")))) {
							if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())) {
									gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() + gstr.getTotaltax());
								}
							}else {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setGstr1b2b_R_CBW_Taxableamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())) {
									gstr3b_gstr1.setGstr1b2b_R_CBW_Taxamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxamount() + gstr.getTotaltax());
								}
							}
							}else if ((MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype()))
														&& (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0)) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("R") || gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("CBW")))) {
							if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())) {
									gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() + gstr.getTotaltax());
								}
							}else {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxableamount(gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())) {
									gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxamount(gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxamount() + gstr.getTotaltax());
								}
							}
						} else if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) 	|| MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype())
								|| MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype())) && (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0)) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("DE")))) {
							if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())) {
									gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() + gstr.getTotaltax());
								}
							}else {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setGstr1b2b_CL_DE_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_DE_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())) {
									gstr3b_gstr1.setGstr1b2b_CL_DE_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_DE_Taxamount() + gstr.getTotaltax());
								}
							}
						} else if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype())
								|| MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype())) && (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0)) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("SEWP")))) {
							if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())) {
									gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() + gstr.getTotaltax());
								}
							}else {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setGstr1b2b_CL_SEWP_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())) {
									gstr3b_gstr1.setGstr1b2b_CL_SEWP_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxamount() + gstr.getTotaltax());
								}
							}
						} else if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype())
									|| MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype()))	&& (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0)) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("SEWOP")))) {
							if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() + gstr.getTotaltaxableamount());									
								}
								if(isNotEmpty(gstr.getTotaltax())) {
									gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() + gstr.getTotaltax());
								}
							}else {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setGstr1b2b_CL_SEWOP_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())) {
									gstr3b_gstr1.setGstr1b2b_CL_SEWOP_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxamount() + gstr.getTotaltax());
								}
							}
						}else if (MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype())){
							if(isNotEmpty(gstr.getTotaltaxableamount())) {
								gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxableamount(gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
							}
							if(isNotEmpty(gstr.getTotaltax())){
								gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxamount(gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxamount() + gstr.getTotaltax());
							}
						}
						if (MasterGSTConstants.EXPORTS.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.EXPA.equalsIgnoreCase(gstr.getInvtype())) {
							if(isNotEmpty(gstr.getExp()) && isNotEmpty(gstr.getExp().get(0)) && isNotEmpty(gstr.getExp().get(0).getExpTyp())) {
								if ("WPAY".equalsIgnoreCase(gstr.getExp().get(0).getExpTyp())) {
									if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
										if(isNotEmpty(gstr.getTotaltaxableamount())) {
											gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() + gstr.getTotaltaxableamount());
										}
										if(isNotEmpty(gstr.getTotaltax())) {
											gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() + gstr.getTotaltax());
										}
									}else {
										if(isNotEmpty(gstr.getTotaltaxableamount())) {
											gstr3b_gstr1.setGstr1exports_WP_Taxableamount(gstr3b_gstr1.getGstr1exports_WP_Taxableamount() + gstr.getTotaltaxableamount());
										}
										if(isNotEmpty(gstr.getTotaltax())) {
											gstr3b_gstr1.setGstr1exports_WP_Taxamount(gstr3b_gstr1.getGstr1exports_WP_Taxamount() + gstr.getTotaltax());
										}
									}
								} else if ("WOPAY".equalsIgnoreCase(gstr.getExp().get(0).getExpTyp())) {
									if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
										if(isNotEmpty(gstr.getTotaltaxableamount())) {
											gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() + gstr.getTotaltaxableamount());
										}
										if(isNotEmpty(gstr.getTotaltax())) {
											gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() + gstr.getTotaltax());
										}
									}else{
										if(isNotEmpty(gstr.getTotaltaxableamount())) {
											gstr3b_gstr1.setGstr1exports_WOP_Taxableamount(gstr3b_gstr1.getGstr1exports_WOP_Taxableamount() + gstr.getTotaltaxableamount());
										}
										if(isNotEmpty(gstr.getTotaltax())) {
											gstr3b_gstr1.setGstr1exports_WOP_Taxamount(gstr3b_gstr1.getGstr1exports_WOP_Taxamount() + gstr.getTotaltax());
										}
									}
								}
							}	
						}
						if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNUR.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNURA.equalsIgnoreCase(gstr.getInvtype())) {
							String org_invno = "";
							String docType = "";
							if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)) {
								if (isNotEmpty(((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty())) {
									docType = ((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty();
									org_invno =((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getInum(); 
											//orginal_invno.get(((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getInum());
								}
							} else if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)) {
								if (isNotEmpty(((GSTR1) gstr).getCdnur().get(0).getNtty())) {
									docType = gstr.getCdnur().get(0).getNtty();
									org_invno = gstr.getCdnur().get(0).getInum(); 
											//orginal_invno.get(gstr.getCdnur().get(0).getInum());
								}
							}
							
							Date stDate = null;
							Date endDate = null;
							Calendar cal = Calendar.getInstance();
							if(month < 10) {
								cal.set(year-1, 3, 1, 0, 0, 0);
								stDate = new java.util.Date(cal.getTimeInMillis());
								cal = Calendar.getInstance();
								cal.set(year, month, 0, 0, 0, 0);
								endDate = new java.util.Date(cal.getTimeInMillis());
							}else {
								cal.set(year, 3, 1, 0, 0, 0);
								stDate = new java.util.Date(cal.getTimeInMillis());
								cal = Calendar.getInstance();
								cal.set(year, month, 0, 0, 0, 0);
								endDate = new java.util.Date(cal.getTimeInMillis());
							}
							List<GSTR1> gstr1list = gstr1Repository.findByInvoicenoAndClientidAndDateofinvoiceBetween(org_invno, client.getId().toString(), stDate, endDate);
							
							if (docType.equalsIgnoreCase("C")) {
								boolean flag = false;
								if(isNotEmpty(gstr1list)) {
									for(InvoiceParent gstrr1 : gstr1list) {
										if(isNotEmpty(gstrr1.getGovtInvoiceStatus())) {
											if(MasterGSTConstants.SUCCESS.equalsIgnoreCase(gstr.getGovtInvoiceStatus())) {
												if(isNotEmpty(gstrr1) && isNotEmpty(gstrr1.getInvoiceno())) {
													if(org_invno.equals(gstrr1.getInvoiceno())) {
														flag = true;
														if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B) || gstrr1.getInvtype().equals(MasterGSTConstants.B2C) || gstrr1.getInvtype().equals(MasterGSTConstants.B2CL)) {
															String creverseChargeType = "";
															if(isNotEmpty(gstrr1.getRevchargetype())) {
																creverseChargeType = gstrr1.getRevchargetype();
															}else {
																creverseChargeType = "Regular";
															}
															if("Regular".equals(creverseChargeType)) {
																String invType = "";
																if(isNotEmpty(gstrr1.getB2b()) && isNotEmpty(gstrr1.getB2b().get(0)) && isNotEmpty(gstrr1.getB2b().get(0).getInv())	&& isNotEmpty(gstrr1.getB2b().get(0).getInv().get(0).getInvTyp())) {
																	invType = gstrr1.getB2b().get(0).getInv().get(0).getInvTyp();
																}else {
																	invType = "R";
																}
																if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B)){
																	if("R".equals(invType) || "CBW".equals(invType)) {
																		if(isNotEmpty(gstr.getTotaltaxableamount())) {
																			gstr3b_gstr1.setGstr1b2b_R_CBW_Taxableamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxableamount() - gstr.getTotaltaxableamount());
																		}
																		if(isNotEmpty(gstr.getTotaltax())) {
																			gstr3b_gstr1.setGstr1b2b_R_CBW_Taxamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxamount() - gstr.getTotaltax());
																		}
																	}											
																}else {
																	if("R".equals(invType) || "CBW".equals(invType)) {
																		if(isNotEmpty(gstr.getTotaltaxableamount())) {
																			gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxableamount(gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxableamount() - gstr.getTotaltaxableamount());
																		}
																		if(isNotEmpty(gstr.getTotaltax())) {
																			gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxamount(gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxamount() - gstr.getTotaltax());
																		}
																	}
																}
																if("DE".equals(invType)) {
																	if(isNotEmpty(gstr.getTotaltaxableamount())) {
																		gstr3b_gstr1.setGstr1b2b_CL_DE_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_DE_Taxableamount() - gstr.getTotaltaxableamount());
																	}
																	if(isNotEmpty(gstr.getTotaltax())) {
																		gstr3b_gstr1.setGstr1b2b_CL_DE_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_DE_Taxamount() - gstr.getTotaltax());
																	}
																}else if("SEWP".equals(invType) || "SEWPC".equals(invType)) {
																	if(isNotEmpty(gstr.getTotaltaxableamount())) {
																		gstr3b_gstr1.setGstr1b2b_CL_SEWP_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxableamount() - gstr.getTotaltaxableamount());
																	}
																	if(isNotEmpty(gstr.getTotaltax())) {
																		gstr3b_gstr1.setGstr1b2b_CL_SEWP_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxamount() - gstr.getTotaltax());
																	}
																}else if("SEWOP".equals(invType)) {
																	if(isNotEmpty(gstr.getTotaltaxableamount())) {
																		gstr3b_gstr1.setGstr1b2b_CL_SEWOP_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxableamount() - gstr.getTotaltaxableamount());
																	}
																	if(isNotEmpty(gstr.getTotaltax())) {
																		gstr3b_gstr1.setGstr1b2b_CL_SEWOP_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxamount() - gstr.getTotaltax());
																	}
																}else if("WPAY".equals(invType)) {
																	if(isNotEmpty(gstr.getTotaltaxableamount())) {
																		gstr3b_gstr1.setGstr1exports_WP_Taxableamount(gstr3b_gstr1.getGstr1exports_WP_Taxableamount() - gstr.getTotaltaxableamount());
																	}
																	if(isNotEmpty(gstr.getTotaltax())) {
																		gstr3b_gstr1.setGstr1exports_WP_Taxamount(gstr3b_gstr1.getGstr1exports_WP_Taxamount() - gstr.getTotaltax());
																	}
																}else if("WOPAY".equals(invType)) {
																	if(isNotEmpty(gstr.getTotaltaxableamount())) {
																		gstr3b_gstr1.setGstr1exports_WOP_Taxableamount(gstr3b_gstr1.getGstr1exports_WOP_Taxableamount() - gstr.getTotaltaxableamount());
																	}
																	if(isNotEmpty(gstr.getTotaltax())) {
																		gstr3b_gstr1.setGstr1exports_WOP_Taxamount(gstr3b_gstr1.getGstr1exports_WOP_Taxamount() - gstr.getTotaltax());
																	}
																}
															}else {
																if(isNotEmpty(gstr.getTotaltaxableamount())) {
																	gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() - gstr.getTotaltaxableamount());
																}
																if(isNotEmpty(gstr.getTotaltax())) {
																	gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() - gstr.getTotaltax());
																}
															}
														}
													}
												}
											}
										}
									}	
								}
								if(!flag) {
									if(isNotEmpty(gstr.getTotaltaxableamount())) {
										gstr3b_gstr1.setGstr1b2b_R_CBW_Taxableamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxableamount() - gstr.getTotaltaxableamount());
									}
									if(isNotEmpty(gstr.getTotaltax())) {
										gstr3b_gstr1.setGstr1b2b_R_CBW_Taxamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxamount() - gstr.getTotaltax());
									}
								}
							} else if (docType.equalsIgnoreCase("D")) {
								boolean flag = false;
								if(isNotEmpty(gstr1list)) {
									for(InvoiceParent gstrr1 : gstr1list) {
										if(isNotEmpty(gstrr1.getGovtInvoiceStatus())) {
											if(MasterGSTConstants.SUCCESS.equalsIgnoreCase(gstr.getGovtInvoiceStatus())) {
												if(isNotEmpty(gstrr1) && isNotEmpty(gstrr1.getInvoiceno())) {
													if(org_invno.equals(gstrr1.getInvoiceno())) {
														flag = true;
														if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B) || gstrr1.getInvtype().equals(MasterGSTConstants.B2C) || gstrr1.getInvtype().equals(MasterGSTConstants.B2CL)) {
															String creverseChargeType = "";
															if(isNotEmpty(gstrr1.getRevchargetype())) {
																creverseChargeType = gstrr1.getRevchargetype();
															}else {
																creverseChargeType = "Regular";
															}
															if("Regular".equals(creverseChargeType)) {
																String invType = "";
																if(isNotEmpty(gstrr1.getB2b()) && isNotEmpty(gstrr1.getB2b().get(0)) && isNotEmpty(gstrr1.getB2b().get(0).getInv()) && isNotEmpty(gstrr1.getB2b().get(0).getInv().get(0).getInvTyp())) {
																	invType = gstrr1.getB2b().get(0).getInv().get(0).getInvTyp();
																}else {
																	invType = "R";
																}
																if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B)){
																	if("R".equals(invType) || "CBW".equals(invType)) {
																		if(isNotEmpty(gstr.getTotaltaxableamount())) {
																			gstr3b_gstr1.setB2b_R_CBW_Taxableamount(gstr3b_gstr1.getB2b_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
																		}
																		if(isNotEmpty(gstr.getTotaltax())) {
																			gstr3b_gstr1.setB2b_R_CBW_Taxamount(gstr3b_gstr1.getB2b_R_CBW_Taxamount() + gstr.getTotaltax());
																		}
																	}											
																}else {
																	if("R".equals(invType) || "CBW".equals(invType)) {
																		if(isNotEmpty(gstr.getTotaltaxableamount())) {
																			gstr3b_gstr1.setB2cl_R_CBW_Taxableamount(gstr3b_gstr1.getB2cl_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
																		}
																		if(isNotEmpty(gstr.getTotaltax())) {
																			gstr3b_gstr1.setB2cl_R_CBW_Taxamount(gstr3b_gstr1.getB2cl_R_CBW_Taxamount() + gstr.getTotaltax());
																		}
																	}
																}
																if("DE".equals(invType)) {
																	if(isNotEmpty(gstr.getTotaltaxableamount())) {
																		gstr3b_gstr1.setB2b_CL_DE_Taxableamount(gstr3b_gstr1.getB2b_CL_DE_Taxableamount() + gstr.getTotaltaxableamount());
																	}
																	if(isNotEmpty(gstr.getTotaltax())) {
																		gstr3b_gstr1.setB2b_CL_DE_Taxamount(gstr3b_gstr1.getB2b_CL_DE_Taxamount() + gstr.getTotaltax());
																	}
																}else if("SEWP".equals(invType) || "SEWPC".equals(invType)) {
																	if(isNotEmpty(gstr.getTotaltaxableamount())) {
																		gstr3b_gstr1.setB2b_CL_SEWP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxableamount() + gstr.getTotaltaxableamount());
																	}
																	if(isNotEmpty(gstr.getTotaltax())) {
																		gstr3b_gstr1.setB2b_CL_SEWP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxamount() + gstr.getTotaltax());
																	}
																}else if("SEWOP".equals(invType)) {
																	if(isNotEmpty(gstr.getTotaltaxableamount())) {
																		gstr3b_gstr1.setB2b_CL_SEWOP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxableamount() + gstr.getTotaltaxableamount());
																	}
																	if(isNotEmpty(gstr.getTotaltax())) {
																		gstr3b_gstr1.setB2b_CL_SEWOP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxamount() + gstr.getTotaltax());
																	}
																}else if("WPAY".equals(invType)) {
																	if(isNotEmpty(gstr.getTotaltaxableamount())) {
																		gstr3b_gstr1.setExports_WP_Taxableamount(gstr3b_gstr1.getExports_WP_Taxableamount() + gstr.getTotaltaxableamount());
																	}
																	if(isNotEmpty(gstr.getTotaltax())) {
																		gstr3b_gstr1.setExports_WP_Taxamount(gstr3b_gstr1.getExports_WP_Taxamount() + gstr.getTotaltax());
																	}
																}else if("WOPAY".equals(invType)) {
																	if(isNotEmpty(gstr.getTotaltaxableamount())) {
																		gstr3b_gstr1.setExports_WOP_Taxableamount(gstr3b_gstr1.getExports_WOP_Taxableamount() + gstr.getTotaltaxableamount());
																	}
																	if(isNotEmpty(gstr.getTotaltax())) {
																		gstr3b_gstr1.setExports_WOP_Taxamount(gstr3b_gstr1.getExports_WOP_Taxamount() + gstr.getTotaltax());
																	}
																}
															}else {
																if(isNotEmpty(gstr.getTotaltaxableamount())) {
																	gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
																}
																if(isNotEmpty(gstr.getTotaltax())) {
																	gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
																}
															}
														}
													}
												}
											}
										}
									}	
								}
								if(!flag) {
									if(isNotEmpty(gstr.getTotaltaxableamount())) {
										gstr3b_gstr1.setB2b_R_CBW_Taxableamount(gstr3b_gstr1.getB2b_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
									}
									if(isNotEmpty(gstr.getTotaltax())) {
										gstr3b_gstr1.setB2b_R_CBW_Taxamount(gstr3b_gstr1.getB2b_R_CBW_Taxamount() + gstr.getTotaltax());
									}
								}
							}
						}
						if (MasterGSTConstants.NIL.equalsIgnoreCase(gstr.getInvtype())) {
							gstr.getItems().forEach(item -> {
								if ("Exempted".equalsIgnoreCase(item.getType()) || "Nil Rated".equalsIgnoreCase(item.getType())) {
									if(isNotEmpty(item.getTaxablevalue())) {
										gstr3b_gstr1.setGstr1nilrated_Exempted_Taxableamount(gstr3b_gstr1.getGstr1nilrated_Exempted_Taxableamount() + item.getTaxablevalue());
									}
									gstr3b_gstr1.setGstr1nilrated_Exempted_Taxamount(0d);
								} else if ("Non-GST".equalsIgnoreCase(item.getType())) {
									if(isNotEmpty(item.getTaxablevalue())) {
										gstr3b_gstr1.setGstr1nongst_Taxableamount(gstr3b_gstr1.getGstr1nongst_Taxableamount() + item.getTaxablevalue());
									}
									gstr3b_gstr1.setGstr1nongst_Taxamount(0d);
								}
							});
						}
						if (isNotEmpty(client.getStatename())) {
							Double taxableamount=0d, taxamount=0d;
							int multiply = 1;
							
							if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNUR.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNURA.equalsIgnoreCase(gstr.getInvtype())) {
								
								String docType = "";
								if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)) {
									if (isNotEmpty(((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty();
									}
								} else if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)) {
									if (isNotEmpty(((GSTR1) gstr).getCdnur().get(0).getNtty())) {
										docType = gstr.getCdnur().get(0).getNtty();
									}
								}
								if(docType.equalsIgnoreCase("C")) {
									 multiply = -1;
								}else if (docType.equalsIgnoreCase("D")) {
									 multiply = 1;
								}
							}
							
							if(isNotEmpty(gstr.getTotaltaxableamount())) {
								taxableamount += gstr.getTotaltaxableamount() * multiply;
							}
							if(isNotEmpty(gstr.getTotaltax())) {
								taxamount += gstr.getTotaltax() * multiply;
							}
							if (MasterGSTConstants.NIL.equalsIgnoreCase(gstr.getInvtype())) {
								if(isNotEmpty(gstr.getNil()) && isNotEmpty(gstr.getNil().getInv())) {
									for(GSTRNilItems nil : gstr.getNil().getInv()) {
										Double niltaxbleamt = 0d;
										if(isNotEmpty(nil.getExptAmt())) {
											niltaxbleamt += nil.getExptAmt();
										}
										if(isNotEmpty(nil.getNgsupAmt())) {
											niltaxbleamt += nil.getNgsupAmt();
										}
										if(isNotEmpty(nil.getNilAmt())) {
											niltaxbleamt += nil.getNilAmt();
										}
										if(isNotEmpty(nil.getSplyType()) && ("INTRAB2B".equalsIgnoreCase(nil.getSplyType()) || "INTRAB2C".equalsIgnoreCase(nil.getSplyType()))) {
												gstr3b_gstr1.setGstr1_intra_Taxableamount(gstr3b_gstr1.getGstr1_intra_Taxableamount() + niltaxbleamt);
										}else {
												gstr3b_gstr1.setGstr1_inter_Taxableamount(gstr3b_gstr1.getGstr1_inter_Taxableamount() + niltaxbleamt);
										}
									}
								}
							}else {
								String clntstatename = client.getStatename();
								String invstatename = "";
								if(isNotEmpty(gstr.getStatename())) {
									invstatename = gstr.getStatename();
								}else {
									gstr.setStatename("97-Other Territory");
									invstatename = "97-Other Territory";
								}
								if(clntstatename.contains("-")) {
									clntstatename = clntstatename.substring(3);
								}
								if(invstatename.contains("-")) {
									invstatename = invstatename.substring(3);
								}
								if (client.getStatename().equalsIgnoreCase(gstr.getStatename()) || clntstatename.equalsIgnoreCase(invstatename) || client.getStatename().substring(0, 2).equalsIgnoreCase(gstr.getStatename().substring(0, 2))) {
									if(isNotEmpty(gstr.getTotaltaxableamount())) {
										gstr3b_gstr1.setGstr1_intra_Taxableamount(gstr3b_gstr1.getGstr1_intra_Taxableamount() + taxableamount);
									}
									if(isNotEmpty(gstr.getTotaltax())) {
										gstr3b_gstr1.setGstr1_intra_Taxamount(gstr3b_gstr1.getGstr1_intra_Taxamount() + taxamount);
									}
								} else {
									if(isNotEmpty(gstr.getTotaltaxableamount())) {
										gstr3b_gstr1.setGstr1_inter_Taxableamount(gstr3b_gstr1.getGstr1_inter_Taxableamount() + taxableamount);
									}
									if(isNotEmpty(gstr.getTotaltax())) {
										gstr3b_gstr1.setGstr1_inter_Taxamount(gstr3b_gstr1.getGstr1_inter_Taxamount() + taxamount);
									}
								}
							}
						}
					}
				}
			}
			});
		}
		gstr3b_gstr1.setGstr1_Total_Taxableamt1(
				gstr3b_gstr1.getGstr1b2b_R_CBW_Taxableamount()+
				gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxableamount()+
				gstr3b_gstr1.getGstr1b2b_CL_DE_Taxableamount()+
				gstr3b_gstr1.getGstr1rcm_Taxableamount()+
				gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxableamount()+
				gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxableamount()+
				gstr3b_gstr1.getGstr1exports_WP_Taxableamount()+
				gstr3b_gstr1.getGstr1exports_WOP_Taxableamount()+
				gstr3b_gstr1.getGstr1nilrated_Exempted_Taxableamount()+
				gstr3b_gstr1.getGstr1nongst_Taxableamount());
		gstr3b_gstr1.setGstr1_Total_Taxamt1(
				gstr3b_gstr1.getGstr1b2b_R_CBW_Taxamount()+
				gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxamount()+
				gstr3b_gstr1.getGstr1b2b_CL_DE_Taxamount()+
				gstr3b_gstr1.getGstr1rcm_Taxamount()+
				gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxamount()+
				gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxamount()+
				gstr3b_gstr1.getGstr1exports_WP_Taxamount()+
				gstr3b_gstr1.getGstr1exports_WOP_Taxamount()+
				gstr3b_gstr1.getGstr1nilrated_Exempted_Taxamount()+
				gstr3b_gstr1.getGstr1nongst_Taxamount());
				
		gstr3b_gstr1.setGstr1_Total_Taxableamt2(gstr3b_gstr1.getGstr1_intra_Taxableamount() + gstr3b_gstr1.getGstr1_inter_Taxableamount());
		gstr3b_gstr1.setGstr1_Total_Taxamt2(gstr3b_gstr1.getGstr1_intra_Taxamount() + gstr3b_gstr1.getGstr1_inter_Taxamount());

		/**
		 * 
		 * GSTR1-GSTR3B
		 */
		Double gstr1_diffTaxableamount1=gstr3b_gstr1.getGstr1b2b_R_CBW_Taxableamount()+
				gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxableamount()+
				gstr3b_gstr1.getGstr1b2b_CL_DE_Taxableamount()+
				gstr3b_gstr1.getGstr1rcm_Taxableamount();
		
		Double gstr1_diffTaxamount1=gstr3b_gstr1.getGstr1b2b_R_CBW_Taxamount()+
				gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxamount()+
				gstr3b_gstr1.getGstr1b2b_CL_DE_Taxamount()+
				gstr3b_gstr1.getGstr1rcm_Taxamount();
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Taxableamount_Total1(gstr3b_gstr1.getGSTR3B_3a_Taxableamt()-gstr1_diffTaxableamount1);
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Taxamount_Total1(gstr3b_gstr1.getGSTR3B_3a_Taxamt()-gstr1_diffTaxamount1);
		
		double gstr3b_diffTaxableamount2 = gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxableamount()+ gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxableamount() +
				gstr3b_gstr1.getGstr1exports_WP_Taxableamount()+gstr3b_gstr1.getGstr1exports_WOP_Taxableamount();
		double gstr3b_diffTaxamount2 = gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxamount() + gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxamount()+ 
										gstr3b_gstr1.getGstr1exports_WP_Taxamount() +gstr3b_gstr1.getGstr1exports_WOP_Taxamount();
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Taxableamount_Total2(gstr3b_gstr1.getGSTR3B_3b_Taxableamt()-gstr3b_diffTaxableamount2);
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Taxamount_Total2(gstr3b_gstr1.getGSTR3B_3b_Taxamt()-gstr3b_diffTaxamount2);

		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Nilrated_Exempted_Taxableamount(gstr3b_gstr1.getGSTR3B_3c_Taxableamt()-gstr3b_gstr1.getGstr1nilrated_Exempted_Taxableamount());
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Nilrated_Exempted_Taxamount(gstr3b_gstr1.getGSTR3B_3c_Taxamt()-gstr3b_gstr1.getGstr1nilrated_Exempted_Taxamount());

		gstr3b_gstr1.setDiffGSTR1_GSTR3B_NonGST_Taxableamount(gstr3b_gstr1.getGSTR3B_3e_Taxableamt()-gstr3b_gstr1.getGstr1nongst_Taxableamount());
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_NonGST_Taxamount(-gstr3b_gstr1.getGstr1nongst_Taxamount());

		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Total_Taxableamount1(gstr3b_gstr1.getDiffGSTR1_GSTR3B_Taxableamount_Total1()+gstr3b_gstr1.getDiffGSTR1_GSTR3B_Taxableamount_Total2()
														+gstr3b_gstr1.getDiffGSTR1_GSTR3B_Nilrated_Exempted_Taxableamount()+gstr3b_gstr1.getDiffGSTR1_GSTR3B_NonGST_Taxableamount());
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Total_Taxamount1(gstr3b_gstr1.getDiffGSTR1_GSTR3B_Taxamount_Total1()+gstr3b_gstr1.getDiffGSTR1_GSTR3B_Taxamount_Total2()
														+gstr3b_gstr1.getDiffGSTR1_GSTR3B_Nilrated_Exempted_Taxamount()+gstr3b_gstr1.getDiffGSTR1_GSTR3B_NonGST_Taxamount());
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Inter_Taxableamount(gstr3b_gstr1.getGSTR3B_Total_Taxableamt()-	(gstr3b_gstr1.getGstr1_inter_Taxableamount() + gstr3b_gstr1.getGstr1_intra_Taxableamount()));
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Inter_Taxamount(gstr3b_gstr1.getGSTR3B_IGST()-gstr3b_gstr1.getGstr1_inter_Taxamount());
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Intra_Taxamount(gstr3b_gstr1.getGSTR3B_CGST_SGST()-gstr3b_gstr1.getGstr1_intra_Taxamount());
		
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Total_Taxamount2(gstr3b_gstr1.getDiffGSTR1_GSTR3B_Inter_Taxamount()+gstr3b_gstr1.getDiffGSTR1_GSTR3B_Intra_Taxamount());
		
		/**
		 * Books-GST1
		 */
		gstr3b_gstr1.setDiffBook_GSTR1_B2B_Taxableamount(gstr3b_gstr1.getB2b_R_CBW_Taxableamount()- gstr3b_gstr1.getGstr1b2b_R_CBW_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_B2B_Taxamount(gstr3b_gstr1.getB2b_R_CBW_Taxamount()- gstr3b_gstr1.getGstr1b2b_R_CBW_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_B2C_Taxableamount(gstr3b_gstr1.getB2cl_R_CBW_Taxableamount()-gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_B2C_Taxamount(gstr3b_gstr1.getB2cl_R_CBW_Taxamount()-gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Deemed_Taxableamount(gstr3b_gstr1.getB2b_CL_DE_Taxableamount()-gstr3b_gstr1.getGstr1b2b_CL_DE_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Deemed_Taxamount(gstr3b_gstr1.getB2b_CL_DE_Taxamount()-gstr3b_gstr1.getGstr1b2b_CL_DE_Taxamount());
		
		gstr3b_gstr1.setDiffBook_GSTR1_RCM_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount()-gstr3b_gstr1.getGstr1rcm_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_RCM_Taxamount(gstr3b_gstr1.getRcm_Taxamount()-gstr3b_gstr1.getGstr1rcm_Taxamount());
		
		gstr3b_gstr1.setDiffBook_GSTR1_SEZWP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxableamount()-gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_SEZWP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxamount()-gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_SEZWOP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxableamount()-gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_SEZWOP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxamount()-gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Export_WP_Taxableamount(gstr3b_gstr1.getExports_WP_Taxableamount()-gstr3b_gstr1.getGstr1exports_WP_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Export_WP_Taxamount(gstr3b_gstr1.getExports_WP_Taxamount()-gstr3b_gstr1.getGstr1exports_WP_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Export_WOP_Taxableamount(gstr3b_gstr1.getExports_WOP_Taxableamount()-gstr3b_gstr1.getGstr1exports_WOP_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Export_WOP_Taxamount(gstr3b_gstr1.getExports_WOP_Taxamount()-gstr3b_gstr1.getGstr1exports_WOP_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Nil_Taxableamount(gstr3b_gstr1.getNilrated_Exempted_Taxableamount()-gstr3b_gstr1.getGstr1nilrated_Exempted_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Nil_Taxamount(gstr3b_gstr1.getNilrated_Exempted_Taxamount()-gstr3b_gstr1.getGstr1nilrated_Exempted_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_NonGst_Taxableamount(gstr3b_gstr1.getNongst_Taxableamount()-gstr3b_gstr1.getGstr1nongst_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_NonGst_Taxamount(gstr3b_gstr1.getNongst_Taxamount()-gstr3b_gstr1.getGstr1nongst_Taxamount());
				
		gstr3b_gstr1.setDiffBook_GSTR1_Inter_Taxableamount(gstr3b_gstr1.getBook_inter_Taxableamount()-gstr3b_gstr1.getGstr1_inter_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Inter_Taxamount(gstr3b_gstr1.getBook_inter_Taxamount()-gstr3b_gstr1.getGstr1_inter_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Intra_Taxableamount(gstr3b_gstr1.getBook_intra_Taxableamount()-gstr3b_gstr1.getGstr1_intra_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Intra_Taxamount(gstr3b_gstr1.getBook_intra_Taxamount()-gstr3b_gstr1.getGstr1_intra_Taxamount());
	
		gstr3b_gstr1.setDiffBook_GSTR1_Total_Taxableamount2(gstr3b_gstr1.getDiffBook_GSTR1_Inter_Taxableamount()+gstr3b_gstr1.getDiffBook_GSTR1_Intra_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Total_Taxamount2(gstr3b_gstr1.getDiffBook_GSTR1_Inter_Taxamount()+gstr3b_gstr1.getDiffBook_GSTR1_Intra_Taxamount());
		
		gstr3b_gstr1.setDiffBook_GSTR1_Total_Taxableamount1(
					gstr3b_gstr1.getDiffBook_GSTR1_B2B_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_B2C_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Deemed_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_RCM_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_SEZWP_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_SEZWOP_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Export_WP_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Export_WOP_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Nil_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_NonGst_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Total_Taxamount1(
					gstr3b_gstr1.getDiffBook_GSTR1_B2B_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_B2C_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Deemed_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_RCM_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_SEZWP_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_SEZWOP_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Export_WP_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Export_WOP_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Nil_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_NonGst_Taxamount());
		logger.debug(CLASSNAME + method + "END");
		//sSystem.out.println(gstr3b_gstr1);
		return gstr3b_gstr1;
	}
	
	
	/**
	 * GSTR2-vs-GSTR3B-vs-GSTR2A comparison
	 */
	public GSTR3B_VS_GSTR2A comparision_GSTR3B_VS_GSTR2A(String clientid,int month,int year) {
		final String method="comparision_GSTR3B_VS_GSTR2A::";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client = clientService.findById(clientid);
		String strMonth = month < 10 ? "0" + month : month + "";
		String returnPeriod = strMonth + year;
		
		GSTR3B_VS_GSTR2A gstr3b_VS_gstr2a=new GSTR3B_VS_GSTR2A();
		gstr3b_VS_gstr2a.setMonth(monthArray[month]+"-"+year);
		gstr3b_VS_gstr2a.setImportGoods_IGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setImportServices_IGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setRcm_GSTR3B(0d);
		gstr3b_VS_gstr2a.setRcm_CGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setRcm_SGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setRcm_IGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setIsd_CGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setIsd_SGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setIsd_IGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setIsd_GSTR3B(0d);
		gstr3b_VS_gstr2a.setEligible_GSTR3B(0d);
		gstr3b_VS_gstr2a.setEligible_CGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setEligible_SGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setEligible_IGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setIneligible_GSTR3B(0d);
		gstr3b_VS_gstr2a.setIneligible_CGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setIneligible_SGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setIneligible_IGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setCredit_Reversed_GSTR3B(0d);
		gstr3b_VS_gstr2a.setCredit_Reversed_CGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setCredit_Reversed_SGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setCredit_Reversed_IGST_GSTR3B(0d);
		
		GSTR3B gstr3b = clientService.getSuppliesInvoice(clientid, returnPeriod);
		if(isNotEmpty(gstr3b)) {
			if(isNotEmpty(gstr3b.getItcElg())) {
				if(isNotEmpty(gstr3b.getItcElg().getItcAvl())){
					int elgsize = gstr3b.getItcElg().getItcAvl().size();
					if(elgsize >= 1) {
						//itcElg.itcAvl[0].iamt 4(a1)
						if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(0))) {
							if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(0).getIamt())) {
								gstr3b_VS_gstr2a.setImportGoods_IGST_GSTR3B(gstr3b_VS_gstr2a.getImportGoods_IGST_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(0).getIamt());
							}
						}
					}
					if(elgsize >= 2) {
						if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(1))) {
							//itcElg.itcAvl[1].iamt 4(a2)
							if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(1).getIamt())) {
								gstr3b_VS_gstr2a.setImportServices_IGST_GSTR3B(gstr3b_VS_gstr2a.getImportServices_IGST_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(1).getIamt());
							}
						}
					}
					if(elgsize >= 3) {
						if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(2))) {
							//itcElg.itcAvl[2].camt 4(a3)
							if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(2).getCamt())) {
								gstr3b_VS_gstr2a.setRcm_GSTR3B(gstr3b_VS_gstr2a.getRcm_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(2).getCamt());
								gstr3b_VS_gstr2a.setRcm_CGST_GSTR3B(gstr3b_VS_gstr2a.getRcm_CGST_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(2).getCamt());
							}if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(2).getSamt())) {
								gstr3b_VS_gstr2a.setRcm_SGST_GSTR3B(gstr3b_VS_gstr2a.getRcm_SGST_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(2).getSamt());
								gstr3b_VS_gstr2a.setRcm_GSTR3B(gstr3b_VS_gstr2a.getRcm_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(2).getSamt());
							}if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(2).getIamt())) {
								gstr3b_VS_gstr2a.setRcm_IGST_GSTR3B(gstr3b_VS_gstr2a.getRcm_IGST_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(2).getIamt());
								gstr3b_VS_gstr2a.setRcm_GSTR3B(gstr3b_VS_gstr2a.getRcm_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(2).getIamt());
							}
						}
					}
					if(elgsize >= 4) {
						if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(3))) {
						//itcElg.itcAvl[3].iamt 4(a4)
							if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(3).getCamt())) {
								gstr3b_VS_gstr2a.setIsd_GSTR3B(gstr3b_VS_gstr2a.getIsd_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(3).getCamt());
								gstr3b_VS_gstr2a.setIsd_CGST_GSTR3B(gstr3b_VS_gstr2a.getIsd_CGST_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(3).getCamt());
							}if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(3).getSamt())) {
								gstr3b_VS_gstr2a.setIsd_GSTR3B(gstr3b_VS_gstr2a.getIsd_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(3).getSamt());
								gstr3b_VS_gstr2a.setIsd_SGST_GSTR3B(gstr3b_VS_gstr2a.getIsd_SGST_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(3).getSamt());
							}if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(3).getIamt())) {
								gstr3b_VS_gstr2a.setIsd_GSTR3B(gstr3b_VS_gstr2a.getIsd_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(3).getIamt());
								gstr3b_VS_gstr2a.setIsd_IGST_GSTR3B(gstr3b_VS_gstr2a.getIsd_IGST_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(3).getIamt());
							}
						}
					}
					if(elgsize >= 5) {
						if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(4))) {
							//itcElg.itcAvl[4].iamt 4(a5)
							if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(4).getCamt())) {
								gstr3b_VS_gstr2a.setEligible_GSTR3B(gstr3b_VS_gstr2a.getEligible_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(4).getCamt());
								gstr3b_VS_gstr2a.setEligible_CGST_GSTR3B(gstr3b_VS_gstr2a.getEligible_CGST_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(4).getCamt());
							}if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(4).getSamt())) {
								gstr3b_VS_gstr2a.setEligible_GSTR3B(gstr3b_VS_gstr2a.getEligible_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(4).getSamt());
								gstr3b_VS_gstr2a.setEligible_SGST_GSTR3B(gstr3b_VS_gstr2a.getEligible_SGST_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(4).getSamt());
							}if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(4).getIamt())) {
								gstr3b_VS_gstr2a.setEligible_GSTR3B(gstr3b_VS_gstr2a.getEligible_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(4).getIamt());
								gstr3b_VS_gstr2a.setEligible_IGST_GSTR3B(gstr3b_VS_gstr2a.getEligible_IGST_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(4).getIamt());
							}
						}
					}
				}
			}
			/*
			 //itcElg.itcNet.iamt 4(c)
			if(isNotEmpty(gstr3b.getItcElg().getItcNet().getIamt())) {
				gstr3b_VS_gstr2a.setIneligible_GSTR3B(gstr3b_VS_gstr2a.getIneligible_GSTR3B()+gstr3b.getItcElg().getItcNet().getIamt());
				gstr3b_VS_gstr2a.setIneligible_IGST_GSTR3B(gstr3b_VS_gstr2a.getIneligible_IGST_GSTR3B()+gstr3b.getItcElg().getItcNet().getIamt());
			}if(isNotEmpty(gstr3b.getItcElg().getItcNet().getCamt())) {
				gstr3b_VS_gstr2a.setIneligible_GSTR3B(gstr3b_VS_gstr2a.getIneligible_GSTR3B()+gstr3b.getItcElg().getItcNet().getCamt());
				gstr3b_VS_gstr2a.setIneligible_CGST_GSTR3B(gstr3b_VS_gstr2a.getIneligible_CGST_GSTR3B()+gstr3b.getItcElg().getItcNet().getCamt());
			}if(isNotEmpty(gstr3b.getItcElg().getItcNet().getSamt())) {
				gstr3b_VS_gstr2a.setIneligible_GSTR3B(gstr3b_VS_gstr2a.getIneligible_GSTR3B()+gstr3b.getItcElg().getItcNet().getSamt());
				gstr3b_VS_gstr2a.setIneligible_SGST_GSTR3B(gstr3b_VS_gstr2a.getIneligible_SGST_GSTR3B()+gstr3b.getItcElg().getItcNet().getSamt());
			}*/
			if(isNotEmpty(gstr3b.getItcElg())) {
				//itcElg.ItcInelg.(1).iamt 4(d)
				if(isNotEmpty(gstr3b.getItcElg().getItcInelg()) && isNotEmpty(gstr3b.getItcElg().getItcInelg().get(1)) && isNotEmpty(gstr3b.getItcElg().getItcInelg().get(1).getIamt())) {
					gstr3b_VS_gstr2a.setIneligible_GSTR3B(gstr3b_VS_gstr2a.getIneligible_GSTR3B()+gstr3b.getItcElg().getItcInelg().get(1).getIamt());
					gstr3b_VS_gstr2a.setIneligible_IGST_GSTR3B(gstr3b_VS_gstr2a.getIneligible_IGST_GSTR3B()+gstr3b.getItcElg().getItcInelg().get(1).getIamt());
				}if(isNotEmpty(gstr3b.getItcElg().getItcInelg()) && isNotEmpty(gstr3b.getItcElg().getItcInelg().get(1)) && isNotEmpty(gstr3b.getItcElg().getItcInelg().get(1).getCamt())) {
					gstr3b_VS_gstr2a.setIneligible_GSTR3B(gstr3b_VS_gstr2a.getIneligible_GSTR3B()+gstr3b.getItcElg().getItcInelg().get(1).getCamt());
					gstr3b_VS_gstr2a.setIneligible_CGST_GSTR3B(gstr3b_VS_gstr2a.getIneligible_CGST_GSTR3B()+gstr3b.getItcElg().getItcInelg().get(1).getCamt());
				}if(isNotEmpty(gstr3b.getItcElg().getItcInelg()) && isNotEmpty(gstr3b.getItcElg().getItcInelg().get(1)) && isNotEmpty(gstr3b.getItcElg().getItcInelg().get(1).getSamt())) {
					gstr3b_VS_gstr2a.setIneligible_GSTR3B(gstr3b_VS_gstr2a.getIneligible_GSTR3B()+gstr3b.getItcElg().getItcInelg().get(1).getSamt());
					gstr3b_VS_gstr2a.setIneligible_SGST_GSTR3B(gstr3b_VS_gstr2a.getIneligible_SGST_GSTR3B()+gstr3b.getItcElg().getItcInelg().get(1).getSamt());
				}
			}
			if(isNotEmpty(gstr3b.getItcElg())){
				if(isNotEmpty(gstr3b.getItcElg().getItcRev())){
					int elgrevsize = gstr3b.getItcElg().getItcRev().size();
					if(elgrevsize >= 1) {
						if(isNotEmpty(gstr3b.getItcElg().getItcRev().get(0))) {
							//itcElg.itcRev[0].iamt 4(b)
							if(isNotEmpty(gstr3b.getItcElg().getItcRev().get(0).getIamt())) {
								gstr3b_VS_gstr2a.setCredit_Reversed_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR3B()+gstr3b.getItcElg().getItcRev().get(0).getIamt());
								gstr3b_VS_gstr2a.setCredit_Reversed_IGST_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_IGST_GSTR3B()+gstr3b.getItcElg().getItcRev().get(0).getIamt());
							}
							if(isNotEmpty(gstr3b.getItcElg().getItcRev().get(0).getCamt())) {
								gstr3b_VS_gstr2a.setCredit_Reversed_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR3B()+gstr3b.getItcElg().getItcRev().get(0).getCamt());
								gstr3b_VS_gstr2a.setCredit_Reversed_CGST_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_CGST_GSTR3B()+gstr3b.getItcElg().getItcRev().get(0).getCamt());
							}
							if(isNotEmpty(gstr3b.getItcElg().getItcRev().get(0).getSamt())) {
								gstr3b_VS_gstr2a.setCredit_Reversed_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR3B()+gstr3b.getItcElg().getItcRev().get(0).getSamt());
								gstr3b_VS_gstr2a.setCredit_Reversed_SGST_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_SGST_GSTR3B()+gstr3b.getItcElg().getItcRev().get(0).getSamt());
							}
						}		
					}
					if(elgrevsize >= 2) {
						if(isNotEmpty(gstr3b.getItcElg().getItcRev().get(1))) {	
							if(isNotEmpty(gstr3b.getItcElg().getItcRev().get(1).getIamt())) {
								gstr3b_VS_gstr2a.setCredit_Reversed_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR3B()+gstr3b.getItcElg().getItcRev().get(1).getIamt());
								gstr3b_VS_gstr2a.setCredit_Reversed_IGST_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_IGST_GSTR3B()+gstr3b.getItcElg().getItcRev().get(1).getIamt());
							}
							if(isNotEmpty(gstr3b.getItcElg().getItcRev().get(1).getCamt())) {
								gstr3b_VS_gstr2a.setCredit_Reversed_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR3B()+gstr3b.getItcElg().getItcRev().get(1).getCamt());
								gstr3b_VS_gstr2a.setCredit_Reversed_CGST_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_CGST_GSTR3B()+gstr3b.getItcElg().getItcRev().get(1).getCamt());
							}
							if(isNotEmpty(gstr3b.getItcElg().getItcRev().get(1).getIamt())) {
								gstr3b_VS_gstr2a.setCredit_Reversed_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR3B()+gstr3b.getItcElg().getItcRev().get(1).getSamt());
								gstr3b_VS_gstr2a.setCredit_Reversed_SGST_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_SGST_GSTR3B()+gstr3b.getItcElg().getItcRev().get(1).getSamt());
							}
						}
					}
				}
			}
		}
		
		Page<? extends InvoiceParent> purchaseList = clientService.getInvoices(null,client,"userid",MasterGSTConstants.PURCHASE_REGISTER,"reports",month,year);
				//clientService.getInvoices(null, clientid, MasterGSTConstants.PURCHASE_REGISTER, month, year);
		Page<? extends InvoiceParent> gstr2AList = clientService.getInvoices(null,client,"userid",MasterGSTConstants.GSTR2,"reports",month,year);
				//clientService.getInvoices(null, clientid, MasterGSTConstants.GSTR2, month, year);
		//GSTR2(Purchase Register) variables
		gstr3b_VS_gstr2a.setImportGoods_IGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setImportServices_IGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setRcm_CGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setRcm_SGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setRcm_IGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setIsd_CGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setIsd_SGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setIsd_IGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setEligible_CGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setEligible_SGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setEligible_IGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setIneligible_CGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setIneligible_SGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setIneligible_IGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setCredit_Reversed_CGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setCredit_Reversed_SGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setCredit_Reversed_IGST_GSTR2(0d);
		if(isNotEmpty(purchaseList)) {
			purchaseList.forEach(purchase->{
				if(purchase.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
					String reverseChargeType = "";
					if(isNotEmpty(purchase.getRevchargetype())) {
						reverseChargeType = purchase.getRevchargetype();
					}else {
						reverseChargeType = "Regular";
					}
					//import of goods only igst
					if("Regular".equalsIgnoreCase(reverseChargeType)) {
						if(isNotEmpty(purchase.getItems())){
							purchase.getItems().forEach(items->{
								if(isNotEmpty(items.getIgstamount())){
									gstr3b_VS_gstr2a.setImportGoods_IGST_GSTR2(gstr3b_VS_gstr2a.getImportGoods_IGST_GSTR2()+items.getIgstamount());
								}
							});
						}
					}else {
						if(isNotEmpty(purchase.getItems())){
							purchase.getItems().forEach(items->{
								if(isNotEmpty(items.getIgstamount())){
									gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()+items.getIgstamount());
									gstr3b_VS_gstr2a.setRcm_IGST_GSTR2(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2()+items.getIgstamount());
								}
							});
						}
					}
				}
				
				if(purchase.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_SERVICES)) {
					String reverseChargeType = "";
					if(isNotEmpty(reverseChargeType)) {
						reverseChargeType = purchase.getRevchargetype();
					}else {
						reverseChargeType = "Regular";
					}
					//import of services only igst
					if("Regular".equalsIgnoreCase(purchase.getRevchargetype())) {
						if(isNotEmpty(purchase.getItems())){
							purchase.getItems().forEach(items->{
								if(isNotEmpty(items.getIgstamount())){
									gstr3b_VS_gstr2a.setImportServices_IGST_GSTR2(gstr3b_VS_gstr2a.getImportServices_IGST_GSTR2()+items.getIgstamount());
								}
							});
						}
					}else {
						if(isNotEmpty(purchase.getItems())){
							purchase.getItems().forEach(items->{
								if(isNotEmpty(items.getIgstamount())){
									gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()+items.getIgstamount());
									gstr3b_VS_gstr2a.setRcm_IGST_GSTR2(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2()+items.getIgstamount());
								}
							});
						}
					}
				}
	
				if(purchase.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISD)) {
					String reverseChargeType = "";
					if(isNotEmpty(purchase.getRevchargetype())) {
						reverseChargeType = purchase.getRevchargetype();
					}else {
						reverseChargeType = "Regular";
					}
					if("Regular".equalsIgnoreCase(reverseChargeType)) {
						if(isNotEmpty(purchase.getItems())){
							purchase.getItems().forEach(items->{
								if(isNotEmpty(items.getCgstamount())){
									gstr3b_VS_gstr2a.setIsd_GSTR2(gstr3b_VS_gstr2a.getIsd_GSTR2()+items.getCgstamount());
									gstr3b_VS_gstr2a.setIsd_CGST_GSTR2(gstr3b_VS_gstr2a.getIsd_CGST_GSTR2()+items.getCgstamount());
								}
								if(isNotEmpty(items.getSgstamount())){
									gstr3b_VS_gstr2a.setIsd_GSTR2(gstr3b_VS_gstr2a.getIsd_GSTR2()+items.getSgstamount());
									gstr3b_VS_gstr2a.setIsd_SGST_GSTR2(gstr3b_VS_gstr2a.getIsd_SGST_GSTR2()+items.getSgstamount());
								}
								if(isNotEmpty(items.getIgstamount())){
									gstr3b_VS_gstr2a.setIsd_GSTR2(gstr3b_VS_gstr2a.getIsd_GSTR2()+items.getIgstamount());
									gstr3b_VS_gstr2a.setIsd_IGST_GSTR2(gstr3b_VS_gstr2a.getIsd_IGST_GSTR2()+items.getIgstamount());
		
								}
							});
						}
					}else {
						if(isNotEmpty(purchase.getItems())){
							purchase.getItems().forEach(items->{
								if(isNotEmpty(items.getCgstamount())){
									gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()+items.getCgstamount());
									gstr3b_VS_gstr2a.setRcm_CGST_GSTR2(gstr3b_VS_gstr2a.getRcm_CGST_GSTR2()+items.getCgstamount());
								}
								if(isNotEmpty(items.getSgstamount())){
									gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()+items.getSgstamount());
									gstr3b_VS_gstr2a.setRcm_SGST_GSTR2(gstr3b_VS_gstr2a.getRcm_SGST_GSTR2()+items.getSgstamount());
								}
								if(isNotEmpty(items.getIgstamount())){
									gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()+items.getIgstamount());
									gstr3b_VS_gstr2a.setRcm_IGST_GSTR2(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2()+items.getIgstamount());
								}
							});
						}
					}
				}
				
				if(purchase.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B) || purchase.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2BUR)) {
					String reverseChargeType = "";
					if(isNotEmpty(purchase.getRevchargetype())) {
						reverseChargeType = purchase.getRevchargetype();
					}else {
						reverseChargeType = "Regular";
					}
					if("Regular".equalsIgnoreCase(reverseChargeType)) {
						if(isNotEmpty(purchase.getItems())){
							purchase.getItems().forEach(items->{
								String itcElgType = "";
								if(isNotEmpty(items.getElg())) {
									itcElgType = items.getElg();
								}
								if("NO".equalsIgnoreCase(itcElgType)) {
									if(isNotEmpty(items.getCgstamount())){
										gstr3b_VS_gstr2a.setIneligible_GSTR2(gstr3b_VS_gstr2a.getIneligible_GSTR2()+items.getCgstamount());
										gstr3b_VS_gstr2a.setIneligible_CGST_GSTR2(gstr3b_VS_gstr2a.getIneligible_CGST_GSTR2()+items.getCgstamount());
									}
									if(isNotEmpty(items.getSgstamount())){
										gstr3b_VS_gstr2a.setIneligible_GSTR2(gstr3b_VS_gstr2a.getIneligible_GSTR2()+items.getSgstamount());
										gstr3b_VS_gstr2a.setIneligible_SGST_GSTR2(gstr3b_VS_gstr2a.getIneligible_SGST_GSTR2()+items.getSgstamount());
									}
									if(isNotEmpty(items.getIgstamount())){
										gstr3b_VS_gstr2a.setIneligible_GSTR2(gstr3b_VS_gstr2a.getIneligible_GSTR2()+items.getIgstamount());
										gstr3b_VS_gstr2a.setIneligible_IGST_GSTR2(gstr3b_VS_gstr2a.getIneligible_IGST_GSTR2()+items.getIgstamount());
									}
								}else {
									if(isNotEmpty(items.getCgstamount())){
										gstr3b_VS_gstr2a.setEligible_GSTR2(gstr3b_VS_gstr2a.getEligible_GSTR2()+items.getCgstamount());
										gstr3b_VS_gstr2a.setEligible_CGST_GSTR2(gstr3b_VS_gstr2a.getEligible_CGST_GSTR2()+items.getCgstamount());
									}
									if(isNotEmpty(items.getSgstamount())){
										gstr3b_VS_gstr2a.setEligible_GSTR2(gstr3b_VS_gstr2a.getEligible_GSTR2()+items.getSgstamount());
										gstr3b_VS_gstr2a.setEligible_SGST_GSTR2(gstr3b_VS_gstr2a.getEligible_SGST_GSTR2()+items.getSgstamount());
									}
									if(isNotEmpty(items.getIgstamount())){
										gstr3b_VS_gstr2a.setEligible_GSTR2(gstr3b_VS_gstr2a.getEligible_GSTR2()+items.getIgstamount());
										gstr3b_VS_gstr2a.setEligible_IGST_GSTR2(gstr3b_VS_gstr2a.getEligible_IGST_GSTR2()+items.getIgstamount());
									}
								}
							});
						}
					}else {
						if(isNotEmpty(purchase.getItems())){
							purchase.getItems().forEach(items->{
								if(isNotEmpty(items.getCgstamount())){
									gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()+items.getCgstamount());
									gstr3b_VS_gstr2a.setRcm_CGST_GSTR2(gstr3b_VS_gstr2a.getRcm_CGST_GSTR2()+items.getCgstamount());
								}
								if(isNotEmpty(items.getSgstamount())){
									gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()+items.getSgstamount());
									gstr3b_VS_gstr2a.setRcm_SGST_GSTR2(gstr3b_VS_gstr2a.getRcm_SGST_GSTR2()+items.getSgstamount());
								}
								if(isNotEmpty(items.getIgstamount())){
									gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()+items.getIgstamount());
									gstr3b_VS_gstr2a.setRcm_IGST_GSTR2(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2()+items.getIgstamount());
								}
							});
						}
					}
				}
				if(purchase.getInvtype().equalsIgnoreCase(MasterGSTConstants.ITC_REVERSAL)) {
					String reverseChargeType = "";
					if(isNotEmpty(purchase.getRevchargetype())) {
						reverseChargeType = purchase.getRevchargetype();
					}else {
						reverseChargeType = "Regular";
					}
					if("Regular".equalsIgnoreCase(reverseChargeType)) {
						if(isNotEmpty(purchase.getItems())){
							purchase.getItems().forEach(items->{
								if(isNotEmpty(items.getCgstamount())){
									gstr3b_VS_gstr2a.setCredit_Reversed_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR2()+items.getCgstamount());
									gstr3b_VS_gstr2a.setCredit_Reversed_CGST_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_CGST_GSTR2()+items.getCgstamount());
								}
								if(isNotEmpty(items.getSgstamount())){
									gstr3b_VS_gstr2a.setCredit_Reversed_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR2()+items.getSgstamount());
									gstr3b_VS_gstr2a.setCredit_Reversed_SGST_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_SGST_GSTR2()+items.getSgstamount());
								}
								if(isNotEmpty(items.getIgstamount())){
									gstr3b_VS_gstr2a.setCredit_Reversed_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR2()+items.getIgstamount());
									gstr3b_VS_gstr2a.setCredit_Reversed_IGST_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_IGST_GSTR2()+items.getIgstamount());
								}
							});
						}
					}else {
						if(isNotEmpty(purchase.getItems())){
							purchase.getItems().forEach(items->{
								if(isNotEmpty(items.getCgstamount())){
									gstr3b_VS_gstr2a.setCredit_Reversed_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR2()+items.getCgstamount());
									gstr3b_VS_gstr2a.setCredit_Reversed_CGST_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_CGST_GSTR2()+items.getCgstamount());
								}
								if(isNotEmpty(items.getSgstamount())){
									gstr3b_VS_gstr2a.setCredit_Reversed_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR2()+items.getSgstamount());
									gstr3b_VS_gstr2a.setCredit_Reversed_SGST_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_SGST_GSTR2()+items.getSgstamount());
								}
								if(isNotEmpty(items.getIgstamount())){
									gstr3b_VS_gstr2a.setCredit_Reversed_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR2()+items.getIgstamount());
									gstr3b_VS_gstr2a.setCredit_Reversed_IGST_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_IGST_GSTR2()+items.getIgstamount());
								}
							});
						}
					}
				}
				if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(purchase.getInvtype()) || MasterGSTConstants.CDNUR.equalsIgnoreCase(purchase.getInvtype())) {
					String org_invno = "";
					String docType = "";
					
					if(purchase.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
						docType = purchase.getCdn().get(0).getNt().get(0).getNtty();
						org_invno = purchase.getCdn().get(0).getNt().get(0).getInum();
					}else {
						docType = purchase.getCdnur().get(0).getNtty();
						org_invno = purchase.getCdnur().get(0).getInum();
					}
					
					if (docType.equalsIgnoreCase("C")) {
						String creverseChargeType = "";
						if(isNotEmpty(purchase.getRevchargetype())) {
							creverseChargeType = purchase.getRevchargetype();
						}else {
							creverseChargeType = "Regular";
						}	
						if("Regular".equals(creverseChargeType)) {
							if(isNotEmpty(purchase.getItems())){
								purchase.getItems().forEach(items->{
									String itcElgType = "";
									if(isNotEmpty(items.getElg())) {
										itcElgType = items.getElg();
									}
									if("NO".equalsIgnoreCase(itcElgType)) {
										if(isNotEmpty(items.getCgstamount())){
											gstr3b_VS_gstr2a.setIneligible_GSTR2(gstr3b_VS_gstr2a.getIneligible_GSTR2()-items.getCgstamount());
											gstr3b_VS_gstr2a.setIneligible_CGST_GSTR2(gstr3b_VS_gstr2a.getIneligible_CGST_GSTR2()-items.getCgstamount());
										}
										if(isNotEmpty(items.getSgstamount())){
											gstr3b_VS_gstr2a.setIneligible_GSTR2(gstr3b_VS_gstr2a.getIneligible_GSTR2()-items.getSgstamount());
											gstr3b_VS_gstr2a.setIneligible_SGST_GSTR2(gstr3b_VS_gstr2a.getIneligible_SGST_GSTR2()-items.getSgstamount());
										}
										if(isNotEmpty(items.getIgstamount())){
											gstr3b_VS_gstr2a.setIneligible_GSTR2(gstr3b_VS_gstr2a.getIneligible_GSTR2()-items.getIgstamount());
											gstr3b_VS_gstr2a.setIneligible_IGST_GSTR2(gstr3b_VS_gstr2a.getIneligible_IGST_GSTR2()-items.getIgstamount());
										}
									}else {
										if(isNotEmpty(items.getCgstamount())){
											gstr3b_VS_gstr2a.setEligible_GSTR2(gstr3b_VS_gstr2a.getEligible_GSTR2()-items.getCgstamount());
											gstr3b_VS_gstr2a.setEligible_CGST_GSTR2(gstr3b_VS_gstr2a.getEligible_CGST_GSTR2()-items.getCgstamount());
										}
										if(isNotEmpty(items.getSgstamount())){
											gstr3b_VS_gstr2a.setEligible_GSTR2(gstr3b_VS_gstr2a.getEligible_GSTR2()-items.getSgstamount());
											gstr3b_VS_gstr2a.setEligible_SGST_GSTR2(gstr3b_VS_gstr2a.getEligible_SGST_GSTR2()-items.getSgstamount());
										}
										if(isNotEmpty(items.getIgstamount())){
											gstr3b_VS_gstr2a.setEligible_GSTR2(gstr3b_VS_gstr2a.getEligible_GSTR2()-items.getIgstamount());
											gstr3b_VS_gstr2a.setEligible_IGST_GSTR2(gstr3b_VS_gstr2a.getEligible_IGST_GSTR2()-items.getIgstamount());
										}
									}
								});
							}
						}else {
							if(isNotEmpty(purchase.getItems())){
								purchase.getItems().forEach(items->{
									if(isNotEmpty(items.getCgstamount())){
										gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()-items.getCgstamount());
										gstr3b_VS_gstr2a.setRcm_CGST_GSTR2(gstr3b_VS_gstr2a.getRcm_CGST_GSTR2()-items.getCgstamount());
									}
									if(isNotEmpty(items.getSgstamount())){
										gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()-items.getSgstamount());
										gstr3b_VS_gstr2a.setRcm_SGST_GSTR2(gstr3b_VS_gstr2a.getRcm_SGST_GSTR2()-items.getSgstamount());
									}
									if(isNotEmpty(items.getIgstamount())){
										gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()-items.getIgstamount());
										gstr3b_VS_gstr2a.setRcm_IGST_GSTR2(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2()-items.getIgstamount());
									}
								});
							}
						}			
					} else if (docType.equalsIgnoreCase("D")) {
						String creverseChargeType = "";
						if(isNotEmpty(purchase.getRevchargetype())) {
							creverseChargeType = purchase.getRevchargetype();
						}else {
							creverseChargeType = "Regular";
						}
						if("Regular".equals(creverseChargeType)) {
							if(isNotEmpty(purchase.getItems())){
								purchase.getItems().forEach(items->{
									String itcElgType = "";
									if(isNotEmpty(items.getElg())) {
										itcElgType = items.getElg();
									}
									if("NO".equalsIgnoreCase(itcElgType)) {
										if(isNotEmpty(items.getCgstamount())){
											gstr3b_VS_gstr2a.setIneligible_GSTR2(gstr3b_VS_gstr2a.getIneligible_GSTR2()+items.getCgstamount());
											gstr3b_VS_gstr2a.setIneligible_CGST_GSTR2(gstr3b_VS_gstr2a.getIneligible_CGST_GSTR2()+items.getCgstamount());
										}
										if(isNotEmpty(items.getSgstamount())){
											gstr3b_VS_gstr2a.setIneligible_GSTR2(gstr3b_VS_gstr2a.getIneligible_GSTR2()+items.getSgstamount());
											gstr3b_VS_gstr2a.setIneligible_SGST_GSTR2(gstr3b_VS_gstr2a.getIneligible_SGST_GSTR2()+items.getSgstamount());
										}
										if(isNotEmpty(items.getIgstamount())){
											gstr3b_VS_gstr2a.setIneligible_GSTR2(gstr3b_VS_gstr2a.getIneligible_GSTR2()+items.getIgstamount());
											gstr3b_VS_gstr2a.setIneligible_IGST_GSTR2(gstr3b_VS_gstr2a.getIneligible_IGST_GSTR2()+items.getIgstamount());
										}
									}else {
										if(isNotEmpty(items.getCgstamount())){
											gstr3b_VS_gstr2a.setEligible_GSTR2(gstr3b_VS_gstr2a.getEligible_GSTR2()+items.getCgstamount());
											gstr3b_VS_gstr2a.setEligible_CGST_GSTR2(gstr3b_VS_gstr2a.getEligible_CGST_GSTR2()+items.getCgstamount());
										}
										if(isNotEmpty(items.getSgstamount())){
											gstr3b_VS_gstr2a.setEligible_GSTR2(gstr3b_VS_gstr2a.getEligible_GSTR2()+items.getSgstamount());
											gstr3b_VS_gstr2a.setEligible_SGST_GSTR2(gstr3b_VS_gstr2a.getEligible_SGST_GSTR2()+items.getSgstamount());
										}
										if(isNotEmpty(items.getIgstamount())){
											gstr3b_VS_gstr2a.setEligible_GSTR2(gstr3b_VS_gstr2a.getEligible_GSTR2()+items.getIgstamount());
											gstr3b_VS_gstr2a.setEligible_IGST_GSTR2(gstr3b_VS_gstr2a.getEligible_IGST_GSTR2()+items.getIgstamount());
										}
									}
								});
							}
						}else {
							if(isNotEmpty(purchase.getItems())){
								purchase.getItems().forEach(items->{
									if(isNotEmpty(items.getCgstamount())){
										gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()+items.getCgstamount());
										gstr3b_VS_gstr2a.setRcm_CGST_GSTR2(gstr3b_VS_gstr2a.getRcm_CGST_GSTR2()+items.getCgstamount());
									}
									if(isNotEmpty(items.getSgstamount())){
										gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()+items.getSgstamount());
										gstr3b_VS_gstr2a.setRcm_SGST_GSTR2(gstr3b_VS_gstr2a.getRcm_SGST_GSTR2()+items.getSgstamount());
									}
									if(isNotEmpty(items.getIgstamount())){
										gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()+items.getIgstamount());
										gstr3b_VS_gstr2a.setRcm_IGST_GSTR2(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2()+items.getIgstamount());
									}
								});
							}
						}			
					}
				}
			});
		}			
		//GSTR2(Purchase Register) - GSTR3B
		gstr3b_VS_gstr2a.setDiffImportGoods_IGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getImportGoods_IGST_GSTR2()-gstr3b_VS_gstr2a.getImportGoods_IGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffImportServices_IGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getImportServices_IGST_GSTR2()-gstr3b_VS_gstr2a.getImportServices_IGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffRCM_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getRcm_GSTR2()-gstr3b_VS_gstr2a.getRcm_GSTR3B());
		gstr3b_VS_gstr2a.setDiffRCM_IGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2()-gstr3b_VS_gstr2a.getRcm_IGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffRCM_CGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getRcm_CGST_GSTR2()-gstr3b_VS_gstr2a.getRcm_CGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffRCM_SGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getRcm_SGST_GSTR2()-gstr3b_VS_gstr2a.getRcm_SGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffISD_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getIsd_GSTR2()-gstr3b_VS_gstr2a.getIsd_GSTR3B());
		gstr3b_VS_gstr2a.setDiffISD_IGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getIsd_IGST_GSTR2()-gstr3b_VS_gstr2a.getIsd_IGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffISD_CGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getIsd_CGST_GSTR2()-gstr3b_VS_gstr2a.getIsd_CGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffISD_SGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getIsd_SGST_GSTR2()-gstr3b_VS_gstr2a.getIsd_SGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffCredit_Reversed_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR2()-gstr3b_VS_gstr2a.getCredit_Reversed_GSTR3B());
		gstr3b_VS_gstr2a.setDiffCredit_Reversed_IGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_IGST_GSTR2()-gstr3b_VS_gstr2a.getCredit_Reversed_IGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffCredit_Reversed_CGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_CGST_GSTR2()-gstr3b_VS_gstr2a.getCredit_Reversed_CGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffCredit_Reversed_SGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_SGST_GSTR2()-gstr3b_VS_gstr2a.getCredit_Reversed_SGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffEligible_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getEligible_GSTR2()-gstr3b_VS_gstr2a.getEligible_GSTR3B());
		gstr3b_VS_gstr2a.setDiffEligible_IGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getEligible_IGST_GSTR2()-gstr3b_VS_gstr2a.getEligible_IGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffEligible_CGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getEligible_CGST_GSTR2()-gstr3b_VS_gstr2a.getEligible_CGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffEligible_SGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getEligible_SGST_GSTR2()-gstr3b_VS_gstr2a.getEligible_SGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffIneligible_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getIneligible_GSTR2()-gstr3b_VS_gstr2a.getIneligible_GSTR3B());
		gstr3b_VS_gstr2a.setDiffIneligible_IGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getIneligible_IGST_GSTR2()-gstr3b_VS_gstr2a.getIneligible_IGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffIneligible_CGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getIneligible_CGST_GSTR2()-gstr3b_VS_gstr2a.getIneligible_CGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffIneligible_SGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getIneligible_SGST_GSTR2()-gstr3b_VS_gstr2a.getIneligible_SGST_GSTR3B());
		if(isNotEmpty(gstr2AList)) {
			gstr2AList.forEach(gstr2a->{
				if(gstr2a.isAmendment()) {
						if(gstr2a.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
							String reverseChargeType = "";
							if(isNotEmpty(gstr2a.getRevchargetype())) {
								reverseChargeType = gstr2a.getRevchargetype();
							}else {
								reverseChargeType = "Regular";
							}
							//import of goods only igst
							if("Regular".equalsIgnoreCase(reverseChargeType)) {
								if(isNotEmpty(gstr2a.getItems())){
									gstr2a.getItems().forEach(items->{
										if(isNotEmpty(items.getIgstamount())){
											gstr3b_VS_gstr2a.setImportGoods_IGST_GSTR2A(gstr3b_VS_gstr2a.getImportGoods_IGST_GSTR2A()+items.getIgstamount());
										}
									});
								}
							}else {
								if(isNotEmpty(gstr2a.getItems())){
									gstr2a.getItems().forEach(items->{
										if(isNotEmpty(items.getIgstamount())){
											gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()+items.getIgstamount());
											gstr3b_VS_gstr2a.setRcm_IGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2A()+items.getIgstamount());
										}
									});
								}
							}
						}
					if(gstr2a.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B) || gstr2a.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2BUR)) {
						String reverseChargeType = "";
						if(isNotEmpty(gstr2a.getRevchargetype())) {
							reverseChargeType = gstr2a.getRevchargetype();
						}else {
							reverseChargeType = "Regular";
						}
						if("Regular".equalsIgnoreCase(reverseChargeType)) {
							if(isNotEmpty(gstr2a.getItems())){
								gstr2a.getItems().forEach(items->{
									String itcElgType = "";
									if(isNotEmpty(items.getElg())) {
										itcElgType = items.getElg();
									}
									if("NO".equalsIgnoreCase(itcElgType)) {
										if(isNotEmpty(items.getCgstamount())){
											gstr3b_VS_gstr2a.setIneligible_GSTR2A(gstr3b_VS_gstr2a.getIneligible_GSTR2A()+items.getCgstamount());
											gstr3b_VS_gstr2a.setIneligible_CGST_GSTR2A(gstr3b_VS_gstr2a.getIneligible_CGST_GSTR2A()+items.getCgstamount());
										}
										if(isNotEmpty(items.getSgstamount())){
											gstr3b_VS_gstr2a.setIneligible_GSTR2A(gstr3b_VS_gstr2a.getIneligible_GSTR2A()+items.getSgstamount());
											gstr3b_VS_gstr2a.setIneligible_SGST_GSTR2A(gstr3b_VS_gstr2a.getIneligible_SGST_GSTR2A()+items.getSgstamount());
										}
										if(isNotEmpty(items.getIgstamount())){
											gstr3b_VS_gstr2a.setIneligible_GSTR2A(gstr3b_VS_gstr2a.getIneligible_GSTR2A()+items.getIgstamount());
											gstr3b_VS_gstr2a.setIneligible_IGST_GSTR2A(gstr3b_VS_gstr2a.getIneligible_IGST_GSTR2A()+items.getIgstamount());
										}
									}else {
										if(isNotEmpty(items.getCgstamount())){
											gstr3b_VS_gstr2a.setEligible_GSTR2A(gstr3b_VS_gstr2a.getEligible_GSTR2A()+items.getCgstamount());
											gstr3b_VS_gstr2a.setEligible_CGST_GSTR2A(gstr3b_VS_gstr2a.getEligible_CGST_GSTR2()+items.getCgstamount());
										}
										if(isNotEmpty(items.getSgstamount())){
											gstr3b_VS_gstr2a.setEligible_GSTR2A(gstr3b_VS_gstr2a.getEligible_GSTR2A()+items.getSgstamount());
											gstr3b_VS_gstr2a.setEligible_SGST_GSTR2A(gstr3b_VS_gstr2a.getEligible_SGST_GSTR2()+items.getSgstamount());
										}
										if(isNotEmpty(items.getIgstamount())){
											gstr3b_VS_gstr2a.setEligible_GSTR2A(gstr3b_VS_gstr2a.getEligible_GSTR2A()+items.getIgstamount());
											gstr3b_VS_gstr2a.setEligible_IGST_GSTR2A(gstr3b_VS_gstr2a.getEligible_IGST_GSTR2A()+items.getIgstamount());
										}
									}
								});
							}
						}else {
							if(isNotEmpty(gstr2a.getItems())){
								gstr2a.getItems().forEach(items->{
									if(isNotEmpty(items.getCgstamount())){
										gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()+items.getCgstamount());
										gstr3b_VS_gstr2a.setRcm_CGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_CGST_GSTR2A()+items.getCgstamount());
									}
									if(isNotEmpty(items.getSgstamount())){
										gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()+items.getSgstamount());
										gstr3b_VS_gstr2a.setRcm_SGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_SGST_GSTR2A()+items.getSgstamount());
									}
									if(isNotEmpty(items.getIgstamount())){
										gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()+items.getIgstamount());
										gstr3b_VS_gstr2a.setRcm_IGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2A()+items.getIgstamount());
									}
								});
							}
						}
					}
					if(gstr2a.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISD)) {
						String reverseChargeType = "";
						if(isNotEmpty(gstr2a.getRevchargetype())) {
							reverseChargeType = gstr2a.getRevchargetype();
						}else {
							reverseChargeType = "Regular";
						}
						if("Regular".equalsIgnoreCase(reverseChargeType)) {
							if(isNotEmpty(gstr2a.getItems())){
								gstr2a.getItems().forEach(items->{
									if(isNotEmpty(items.getCgstamount())){
										gstr3b_VS_gstr2a.setIsd_GSTR2A(gstr3b_VS_gstr2a.getIsd_GSTR2A()+items.getCgstamount());
										gstr3b_VS_gstr2a.setIsd_CGST_GSTR2A(gstr3b_VS_gstr2a.getIsd_CGST_GSTR2A()+items.getCgstamount());
									}
									if(isNotEmpty(items.getSgstamount())){
										gstr3b_VS_gstr2a.setIsd_GSTR2A(gstr3b_VS_gstr2a.getIsd_GSTR2A()+items.getSgstamount());
										gstr3b_VS_gstr2a.setIsd_SGST_GSTR2A(gstr3b_VS_gstr2a.getIsd_SGST_GSTR2A()+items.getSgstamount());
									}
									if(isNotEmpty(items.getIgstamount())){
										gstr3b_VS_gstr2a.setIsd_GSTR2A(gstr3b_VS_gstr2a.getIsd_GSTR2A()+items.getIgstamount());
										gstr3b_VS_gstr2a.setIsd_IGST_GSTR2A(gstr3b_VS_gstr2a.getIsd_IGST_GSTR2A()+items.getIgstamount());
			
									}
								});
							}
						}else {
							if(isNotEmpty(gstr2a.getItems())){
								gstr2a.getItems().forEach(items->{
									if(isNotEmpty(items.getCgstamount())){
										gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()+items.getCgstamount());
										gstr3b_VS_gstr2a.setRcm_CGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_CGST_GSTR2A()+items.getCgstamount());
									}
									if(isNotEmpty(items.getSgstamount())){
										gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()+items.getSgstamount());
										gstr3b_VS_gstr2a.setRcm_SGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_SGST_GSTR2A()+items.getSgstamount());
									}
									if(isNotEmpty(items.getIgstamount())){
										gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()+items.getIgstamount());
										gstr3b_VS_gstr2a.setRcm_IGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2A()+items.getIgstamount());
									}
								});
							}
						}
					}
					if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(gstr2a.getInvtype()) || MasterGSTConstants.CDNUR.equalsIgnoreCase(gstr2a.getInvtype())) {
						String org_invno = "";
						String docType = "";
						
						if(gstr2a.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
							docType = gstr2a.getCdn().get(0).getNt().get(0).getNtty();
							org_invno = gstr2a.getCdn().get(0).getNt().get(0).getInum();
						}else {
							docType = gstr2a.getCdnur().get(0).getNtty();
							org_invno = gstr2a.getCdnur().get(0).getInum();
						}
						if (docType.equalsIgnoreCase("C")) {
							if(gstr2a.isAmendment()){
								String creverseChargeType = "";
								if(isNotEmpty(gstr2a.getRevchargetype())) {
									creverseChargeType = gstr2a.getRevchargetype();
								}else {
									creverseChargeType = "Regular";
								}
								if("Regular".equals(creverseChargeType)) {
									if(isNotEmpty(gstr2a.getItems())){
										gstr2a.getItems().forEach(items->{
											String itcElgType = "";
											if(isNotEmpty(items.getElg())) {
												itcElgType = items.getElg();
											}
											if("NO".equalsIgnoreCase(itcElgType)) {
												if(isNotEmpty(items.getCgstamount())){
													gstr3b_VS_gstr2a.setIneligible_GSTR2A(gstr3b_VS_gstr2a.getIneligible_GSTR2A()-items.getCgstamount());
													gstr3b_VS_gstr2a.setIneligible_CGST_GSTR2A(gstr3b_VS_gstr2a.getIneligible_CGST_GSTR2A()-items.getCgstamount());
												}
												if(isNotEmpty(items.getSgstamount())){
													gstr3b_VS_gstr2a.setIneligible_GSTR2A(gstr3b_VS_gstr2a.getIneligible_GSTR2A()-items.getSgstamount());
													gstr3b_VS_gstr2a.setIneligible_SGST_GSTR2A(gstr3b_VS_gstr2a.getIneligible_SGST_GSTR2A()-items.getSgstamount());
												}
												if(isNotEmpty(items.getIgstamount())){
													gstr3b_VS_gstr2a.setIneligible_GSTR2A(gstr3b_VS_gstr2a.getIneligible_GSTR2A()-items.getIgstamount());
													gstr3b_VS_gstr2a.setIneligible_IGST_GSTR2A(gstr3b_VS_gstr2a.getIneligible_IGST_GSTR2A()-items.getIgstamount());
												}
											}else {
												if(isNotEmpty(items.getCgstamount())){
													gstr3b_VS_gstr2a.setEligible_GSTR2A(gstr3b_VS_gstr2a.getEligible_GSTR2A()-items.getCgstamount());
													gstr3b_VS_gstr2a.setEligible_CGST_GSTR2A(gstr3b_VS_gstr2a.getEligible_CGST_GSTR2A()-items.getCgstamount());
												}
												if(isNotEmpty(items.getSgstamount())){
													gstr3b_VS_gstr2a.setEligible_GSTR2A(gstr3b_VS_gstr2a.getEligible_GSTR2A()-items.getSgstamount());
													gstr3b_VS_gstr2a.setEligible_SGST_GSTR2A(gstr3b_VS_gstr2a.getEligible_SGST_GSTR2A()-items.getSgstamount());
												}
												if(isNotEmpty(items.getIgstamount())){
													gstr3b_VS_gstr2a.setEligible_GSTR2A(gstr3b_VS_gstr2a.getEligible_GSTR2A()-items.getIgstamount());
													gstr3b_VS_gstr2a.setEligible_IGST_GSTR2A(gstr3b_VS_gstr2a.getEligible_IGST_GSTR2A()-items.getIgstamount());
												}
											}
										});				
									}
								}else {
									if(isNotEmpty(gstr2a.getItems())){
										gstr2a.getItems().forEach(items->{
											if(isNotEmpty(items.getCgstamount())){
												gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()-items.getCgstamount());
												gstr3b_VS_gstr2a.setRcm_CGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_CGST_GSTR2A()-items.getCgstamount());
											}
											if(isNotEmpty(items.getSgstamount())){
												gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()-items.getSgstamount());
												gstr3b_VS_gstr2a.setRcm_SGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_SGST_GSTR2A()-items.getSgstamount());
											}
											if(isNotEmpty(items.getIgstamount())){
												gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()-items.getIgstamount());
												gstr3b_VS_gstr2a.setRcm_IGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2A()-items.getIgstamount());
											}
										});
									}				
								}
							}
						} else if (docType.equalsIgnoreCase("D")) {
							if(gstr2a.isAmendment()) {
								String creverseChargeType = "";
								if(isNotEmpty(gstr2a.getRevchargetype())) {
									creverseChargeType = gstr2a.getRevchargetype();
								}else {
									creverseChargeType = "Regular";
								}
								if("Regular".equals(creverseChargeType)) {
									if(isNotEmpty(gstr2a.getItems())){
										gstr2a.getItems().forEach(items->{

											String itcElgType = "";
											if(isNotEmpty(items.getElg())) {
												itcElgType = items.getElg();
											}
											if("NO".equalsIgnoreCase(itcElgType)) {
												if(isNotEmpty(items.getCgstamount())){
													gstr3b_VS_gstr2a.setIneligible_GSTR2A(gstr3b_VS_gstr2a.getIneligible_GSTR2A()+items.getCgstamount());
													gstr3b_VS_gstr2a.setIneligible_CGST_GSTR2A(gstr3b_VS_gstr2a.getIneligible_CGST_GSTR2A()+items.getCgstamount());
												}
												if(isNotEmpty(items.getSgstamount())){
													gstr3b_VS_gstr2a.setIneligible_GSTR2A(gstr3b_VS_gstr2a.getIneligible_GSTR2A()+items.getSgstamount());
													gstr3b_VS_gstr2a.setIneligible_SGST_GSTR2A(gstr3b_VS_gstr2a.getIneligible_SGST_GSTR2A()+items.getSgstamount());
												}
												if(isNotEmpty(items.getIgstamount())){
													gstr3b_VS_gstr2a.setIneligible_GSTR2A(gstr3b_VS_gstr2a.getIneligible_GSTR2A()+items.getIgstamount());
													gstr3b_VS_gstr2a.setIneligible_IGST_GSTR2A(gstr3b_VS_gstr2a.getIneligible_IGST_GSTR2A()+items.getIgstamount());
												}
											}else {
												if(isNotEmpty(items.getCgstamount())){
													gstr3b_VS_gstr2a.setEligible_GSTR2A(gstr3b_VS_gstr2a.getEligible_GSTR2A()+items.getCgstamount());
													gstr3b_VS_gstr2a.setEligible_CGST_GSTR2A(gstr3b_VS_gstr2a.getEligible_CGST_GSTR2A()+items.getCgstamount());
												}
												if(isNotEmpty(items.getSgstamount())){
													gstr3b_VS_gstr2a.setEligible_GSTR2A(gstr3b_VS_gstr2a.getEligible_GSTR2A()+items.getSgstamount());
													gstr3b_VS_gstr2a.setEligible_SGST_GSTR2A(gstr3b_VS_gstr2a.getEligible_SGST_GSTR2A()+items.getSgstamount());
												}
												if(isNotEmpty(items.getIgstamount())){
													gstr3b_VS_gstr2a.setEligible_GSTR2A(gstr3b_VS_gstr2a.getEligible_GSTR2A()+items.getIgstamount());
													gstr3b_VS_gstr2a.setEligible_IGST_GSTR2A(gstr3b_VS_gstr2a.getEligible_IGST_GSTR2A()+items.getIgstamount());
												}
											}
										});
									}
								}else {
									if(isNotEmpty(gstr2a.getItems())){
										gstr2a.getItems().forEach(items->{
											if(isNotEmpty(items.getCgstamount())){
												gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()+items.getCgstamount());
												gstr3b_VS_gstr2a.setRcm_CGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_CGST_GSTR2A()+items.getCgstamount());
											}
											if(isNotEmpty(items.getSgstamount())){
												gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()+items.getSgstamount());
												gstr3b_VS_gstr2a.setRcm_SGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_SGST_GSTR2A()+items.getSgstamount());
											}
											if(isNotEmpty(items.getIgstamount())){
												gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()+items.getIgstamount());
												gstr3b_VS_gstr2a.setRcm_IGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2A()+items.getIgstamount());
											}
										});
									}
								}
							}
						}
					}
				}
			});
		}
		//GSTR2(Purchase Register) - GSTR2A
		
		gstr3b_VS_gstr2a.setDiffImportGoods_IGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getImportGoods_IGST_GSTR2()-gstr3b_VS_gstr2a.getImportGoods_IGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffImportServices_IGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getImportServices_IGST_GSTR2()-gstr3b_VS_gstr2a.getImportServices_IGST_GSTR2A());
		
		gstr3b_VS_gstr2a.setDiffRCM_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2()-gstr3b_VS_gstr2a.getRcm_GSTR2A());
		gstr3b_VS_gstr2a.setDiffRCM_IGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2()-gstr3b_VS_gstr2a.getRcm_IGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffRCM_CGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getRcm_CGST_GSTR2()-gstr3b_VS_gstr2a.getRcm_CGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffRCM_SGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getRcm_SGST_GSTR2()-gstr3b_VS_gstr2a.getRcm_SGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffISD_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getIsd_GSTR2()-gstr3b_VS_gstr2a.getIsd_GSTR2A());
		gstr3b_VS_gstr2a.setDiffISD_IGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getIsd_IGST_GSTR2()-gstr3b_VS_gstr2a.getIsd_IGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffISD_CGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getIsd_CGST_GSTR2()-gstr3b_VS_gstr2a.getIsd_CGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffISD_SGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getIsd_SGST_GSTR2()-gstr3b_VS_gstr2a.getIsd_SGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffEligible_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getEligible_GSTR2()-gstr3b_VS_gstr2a.getEligible_GSTR2A());
		gstr3b_VS_gstr2a.setDiffEligible_IGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getEligible_IGST_GSTR2()-gstr3b_VS_gstr2a.getEligible_IGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffEligible_CGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getEligible_CGST_GSTR2()-gstr3b_VS_gstr2a.getEligible_CGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffEligible_SGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getEligible_SGST_GSTR2()-gstr3b_VS_gstr2a.getEligible_SGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffIneligible_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getIneligible_GSTR2()-gstr3b_VS_gstr2a.getIneligible_GSTR2A());
		gstr3b_VS_gstr2a.setDiffIneligible_IGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getIneligible_IGST_GSTR2()-gstr3b_VS_gstr2a.getIneligible_IGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffIneligible_CGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getIneligible_CGST_GSTR2()-gstr3b_VS_gstr2a.getIneligible_CGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffIneligible_SGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getIneligible_SGST_GSTR2()-gstr3b_VS_gstr2a.getIneligible_SGST_GSTR2A());
		
		//GSTR3B - GSTR2A
		gstr3b_VS_gstr2a.setDiffImportGoods_IGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getImportGoods_IGST_GSTR3B()-gstr3b_VS_gstr2a.getImportGoods_IGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffImportServices_IGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getImportServices_IGST_GSTR3B()-gstr3b_VS_gstr2a.getImportServices_IGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffRCM_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR3B()-gstr3b_VS_gstr2a.getRcm_GSTR2A());
		gstr3b_VS_gstr2a.setDiffRCM_IGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getRcm_IGST_GSTR3B()-gstr3b_VS_gstr2a.getRcm_IGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffRCM_CGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getRcm_CGST_GSTR3B()-gstr3b_VS_gstr2a.getRcm_CGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffRCM_SGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getRcm_SGST_GSTR3B()-gstr3b_VS_gstr2a.getRcm_SGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffISD_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getIsd_GSTR3B()-gstr3b_VS_gstr2a.getIsd_GSTR2A());
		gstr3b_VS_gstr2a.setDiffISD_IGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getIsd_IGST_GSTR3B()-gstr3b_VS_gstr2a.getIsd_IGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffISD_CGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getIsd_CGST_GSTR3B()-gstr3b_VS_gstr2a.getIsd_CGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffISD_SGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getIsd_SGST_GSTR3B()-gstr3b_VS_gstr2a.getIsd_SGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffEligible_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getEligible_GSTR3B()-gstr3b_VS_gstr2a.getEligible_GSTR2A());
		gstr3b_VS_gstr2a.setDiffEligible_IGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getEligible_IGST_GSTR3B()-gstr3b_VS_gstr2a.getEligible_IGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffEligible_CGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getEligible_CGST_GSTR3B()-gstr3b_VS_gstr2a.getEligible_CGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffEligible_SGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getEligible_SGST_GSTR3B()-gstr3b_VS_gstr2a.getEligible_SGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffIneligible_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getIneligible_GSTR3B()-gstr3b_VS_gstr2a.getIneligible_GSTR2A());
		gstr3b_VS_gstr2a.setDiffIneligible_IGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getIneligible_IGST_GSTR3B()-gstr3b_VS_gstr2a.getIneligible_IGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffIneligible_CGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getIneligible_CGST_GSTR3B()-gstr3b_VS_gstr2a.getIneligible_CGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffIneligible_SGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getIneligible_SGST_GSTR3B()-gstr3b_VS_gstr2a.getIneligible_SGST_GSTR2A());
		
		logger.debug(CLASSNAME + method + "END");
		return gstr3b_VS_gstr2a;
	}
	
	public List<GSTR3B_VS_GSTR2A> getYearlyComparision_GSTR3B_VS_GSTR2A(String clientid,int year) throws Exception {
		final String method="getYearlyComparision_GSTR3B_VS_GSTR2A::";
		logger.debug(CLASSNAME + method + BEGIN);
		List<GSTR3B_VS_GSTR2A> gstr3b_vs_gstr2a=new ArrayList<GSTR3B_VS_GSTR2A>();
		
		for(int month=4;month<=12;month++) {
			GSTR3B_VS_GSTR2A data=comparision_GSTR3B_VS_GSTR2A(clientid, month, year);
			if(isNotEmpty(data)) {
				gstr3b_vs_gstr2a.add(data);
			}
		}
		for(int month=1;month<=3;month++) {
			int yr=year+1;
			GSTR3B_VS_GSTR2A data=comparision_GSTR3B_VS_GSTR2A(clientid, month, yr);
			if(isNotEmpty(data)) {
				gstr3b_vs_gstr2a.add(data);
			}
		}
		logger.debug(CLASSNAME + method + "END");
		return gstr3b_vs_gstr2a;
	}
	
	/**
	 * GSTR2-vs-GSTR3B-vs-GSTR2A yearly summary
	 */
	public GSTR3B_VS_GSTR2A getYearlySummaryComparision_GSTR3B_VS_GSTR2A(String clientid,String userid,int month, int year) throws Exception {
		final String method="getYearlyComparision_GSTR3B_VS_GSTR2A";
		logger.debug(CLASSNAME + method + BEGIN);
		List<GSTR3B> gstr3blist=new ArrayList<GSTR3B>();
		for(int mnth=1;mnth<=12;mnth++) {
			if(mnth<4) {
				int yr=year+1;
				String strMonth = "0" + mnth;
				String returnPeriod = strMonth + yr;
				GSTR3B gstr3b = clientService.getSuppliesInvoice(clientid, returnPeriod);
				if(isNotEmpty(gstr3b)) {
					gstr3blist.add(gstr3b);
				}
			}else {
				String strMonth = mnth < 10 ? "0" + mnth : mnth + "";
				String returnPeriod = strMonth + year;
				GSTR3B gstr3b = clientService.getSuppliesInvoice(clientid, returnPeriod);
				if(isNotEmpty(gstr3b)) {
					gstr3blist.add(gstr3b);
				}
			}
		}
		Client client = clientService.findById(clientid);
		
		GSTR3B_VS_GSTR2A gstr3b_VS_gstr2a=new GSTR3B_VS_GSTR2A();
		gstr3b_VS_gstr2a.setMonth(monthArray[month]+"-"+year);
		gstr3b_VS_gstr2a.setSummaryyear(year+"-"+(year+1));
		gstr3b_VS_gstr2a.setImportGoods_IGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setImportServices_IGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setRcm_GSTR3B(0d);
		gstr3b_VS_gstr2a.setRcm_CGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setRcm_SGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setRcm_IGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setIsd_CGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setIsd_SGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setIsd_IGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setIsd_GSTR3B(0d);
		gstr3b_VS_gstr2a.setEligible_GSTR3B(0d);
		gstr3b_VS_gstr2a.setEligible_CGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setEligible_SGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setEligible_IGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setIneligible_GSTR3B(0d);
		gstr3b_VS_gstr2a.setIneligible_CGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setIneligible_SGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setIneligible_IGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setCredit_Reversed_GSTR3B(0d);
		gstr3b_VS_gstr2a.setCredit_Reversed_CGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setCredit_Reversed_SGST_GSTR3B(0d);
		gstr3b_VS_gstr2a.setCredit_Reversed_IGST_GSTR3B(0d);
		
		if(gstr3blist.size()>0) {
			gstr3blist.forEach(gstr3b->{
				if(isNotEmpty(gstr3b)) {
					//itcElg.itcAvl[0].iamt 4(a1)
					if(isNotEmpty(gstr3b.getItcElg())){
						if(isNotEmpty(gstr3b.getItcElg().getItcAvl())){
							int elgsize = gstr3b.getItcElg().getItcAvl().size();
							if(elgsize >= 1) {
								if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(0))) {
									if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(0).getIamt())) {
										gstr3b_VS_gstr2a.setImportGoods_IGST_GSTR3B(gstr3b_VS_gstr2a.getImportGoods_IGST_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(0).getIamt());
									}
								}
							}
							if(elgsize >= 2) {
								if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(1))) {
									//itcElg.itcAvl[1].iamt 4(a2)
									if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(1).getIamt())) {
										gstr3b_VS_gstr2a.setImportServices_IGST_GSTR3B(gstr3b_VS_gstr2a.getImportServices_IGST_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(1).getIamt());
									}
								}					
							}
							if(elgsize >= 3) {
								if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(2))) {
									//itcElg.itcAvl[2].camt 4(a3)
									if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(2).getCamt())) {
										gstr3b_VS_gstr2a.setRcm_GSTR3B(gstr3b_VS_gstr2a.getRcm_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(2).getCamt());
										gstr3b_VS_gstr2a.setRcm_CGST_GSTR3B(gstr3b_VS_gstr2a.getRcm_CGST_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(2).getCamt());
									}if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(2).getSamt())) {
										gstr3b_VS_gstr2a.setRcm_SGST_GSTR3B(gstr3b_VS_gstr2a.getRcm_SGST_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(2).getSamt());
										gstr3b_VS_gstr2a.setRcm_GSTR3B(gstr3b_VS_gstr2a.getRcm_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(2).getSamt());
									}if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(2).getIamt())) {
										gstr3b_VS_gstr2a.setRcm_IGST_GSTR3B(gstr3b_VS_gstr2a.getRcm_IGST_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(2).getIamt());
										gstr3b_VS_gstr2a.setRcm_GSTR3B(gstr3b_VS_gstr2a.getRcm_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(2).getIamt());
									}
								}
							}
							if(elgsize >= 4) {
								if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(3))) {
									//itcElg.itcAvl[3].iamt 4(a4)
									if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(3).getCamt())) {
										gstr3b_VS_gstr2a.setIsd_GSTR3B(gstr3b_VS_gstr2a.getIsd_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(3).getCamt());
										gstr3b_VS_gstr2a.setIsd_CGST_GSTR3B(gstr3b_VS_gstr2a.getIsd_CGST_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(3).getCamt());
									}if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(3).getSamt())) {
										gstr3b_VS_gstr2a.setIsd_GSTR3B(gstr3b_VS_gstr2a.getIsd_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(3).getSamt());
										gstr3b_VS_gstr2a.setIsd_SGST_GSTR3B(gstr3b_VS_gstr2a.getIsd_SGST_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(3).getSamt());
									}if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(3).getCamt())) {
										gstr3b_VS_gstr2a.setIsd_GSTR3B(gstr3b_VS_gstr2a.getIsd_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(3).getIamt());
										gstr3b_VS_gstr2a.setIsd_IGST_GSTR3B(gstr3b_VS_gstr2a.getIsd_IGST_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(3).getIamt());
									}
								}
							}
							if(elgsize >= 5) {
								if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(4))) {
									//itcElg.itcAvl[4].iamt 4(a5)
									if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(4).getCamt())) {
										gstr3b_VS_gstr2a.setEligible_GSTR3B(gstr3b_VS_gstr2a.getEligible_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(4).getCamt());
										gstr3b_VS_gstr2a.setEligible_CGST_GSTR3B(gstr3b_VS_gstr2a.getEligible_CGST_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(4).getCamt());
									}if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(4).getSamt())) {
										gstr3b_VS_gstr2a.setEligible_GSTR3B(gstr3b_VS_gstr2a.getEligible_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(4).getSamt());
										gstr3b_VS_gstr2a.setEligible_SGST_GSTR3B(gstr3b_VS_gstr2a.getEligible_SGST_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(4).getSamt());
									}if(isNotEmpty(gstr3b.getItcElg().getItcAvl().get(4).getIamt())) {
										gstr3b_VS_gstr2a.setEligible_GSTR3B(gstr3b_VS_gstr2a.getEligible_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(4).getIamt());
										gstr3b_VS_gstr2a.setEligible_IGST_GSTR3B(gstr3b_VS_gstr2a.getEligible_IGST_GSTR3B()+gstr3b.getItcElg().getItcAvl().get(4).getIamt());
									}
								}
							}
						}
					}
					if(isNotEmpty(gstr3b.getItcElg())) {
						/*if(isNotEmpty(gstr3b.getItcElg().getItcNet())) {
							//itcElg.itcNet.iamt 4(c)
							if(isNotEmpty(gstr3b.getItcElg().getItcNet().getIamt())) {
								gstr3b_VS_gstr2a.setIneligible_GSTR3B(gstr3b_VS_gstr2a.getIneligible_GSTR3B()+gstr3b.getItcElg().getItcNet().getIamt());
								gstr3b_VS_gstr2a.setIneligible_IGST_GSTR3B(gstr3b_VS_gstr2a.getIneligible_IGST_GSTR3B()+gstr3b.getItcElg().getItcNet().getIamt());
							}if(isNotEmpty(gstr3b.getItcElg().getItcNet().getCamt())) {
								gstr3b_VS_gstr2a.setIneligible_GSTR3B(gstr3b_VS_gstr2a.getIneligible_GSTR3B()+gstr3b.getItcElg().getItcNet().getCamt());
								gstr3b_VS_gstr2a.setIneligible_CGST_GSTR3B(gstr3b_VS_gstr2a.getIneligible_CGST_GSTR3B()+gstr3b.getItcElg().getItcNet().getCamt());
							}if(isNotEmpty(gstr3b.getItcElg().getItcNet().getSamt())) {
								gstr3b_VS_gstr2a.setIneligible_GSTR3B(gstr3b_VS_gstr2a.getIneligible_GSTR3B()+gstr3b.getItcElg().getItcNet().getSamt());
								gstr3b_VS_gstr2a.setIneligible_SGST_GSTR3B(gstr3b_VS_gstr2a.getIneligible_SGST_GSTR3B()+gstr3b.getItcElg().getItcNet().getSamt());
							}
						}*/
						//itcElg.ItcInelg.(1).iamt 4(d)
						if(isNotEmpty(gstr3b.getItcElg().getItcInelg()) && isNotEmpty(gstr3b.getItcElg().getItcInelg().get(1)) && isNotEmpty(gstr3b.getItcElg().getItcInelg().get(1).getIamt())) {
							gstr3b_VS_gstr2a.setIneligible_GSTR3B(gstr3b_VS_gstr2a.getIneligible_GSTR3B()+gstr3b.getItcElg().getItcInelg().get(1).getIamt());
							gstr3b_VS_gstr2a.setIneligible_IGST_GSTR3B(gstr3b_VS_gstr2a.getIneligible_IGST_GSTR3B()+gstr3b.getItcElg().getItcInelg().get(1).getIamt());
						}if(isNotEmpty(gstr3b.getItcElg().getItcInelg()) && isNotEmpty(gstr3b.getItcElg().getItcInelg().get(1)) && isNotEmpty(gstr3b.getItcElg().getItcInelg().get(1).getCamt())) {
							gstr3b_VS_gstr2a.setIneligible_GSTR3B(gstr3b_VS_gstr2a.getIneligible_GSTR3B()+gstr3b.getItcElg().getItcInelg().get(1).getCamt());
							gstr3b_VS_gstr2a.setIneligible_CGST_GSTR3B(gstr3b_VS_gstr2a.getIneligible_CGST_GSTR3B()+gstr3b.getItcElg().getItcInelg().get(1).getCamt());
						}if(isNotEmpty(gstr3b.getItcElg().getItcInelg()) && isNotEmpty(gstr3b.getItcElg().getItcInelg().get(1)) && isNotEmpty(gstr3b.getItcElg().getItcInelg().get(1).getSamt())) {
							gstr3b_VS_gstr2a.setIneligible_GSTR3B(gstr3b_VS_gstr2a.getIneligible_GSTR3B()+gstr3b.getItcElg().getItcInelg().get(1).getSamt());
							gstr3b_VS_gstr2a.setIneligible_SGST_GSTR3B(gstr3b_VS_gstr2a.getIneligible_SGST_GSTR3B()+gstr3b.getItcElg().getItcInelg().get(1).getSamt());
						}
					}
					if(isNotEmpty(gstr3b.getItcElg())) {
						if(isNotEmpty(gstr3b.getItcElg().getItcRev())){
							//itcElg.itcRev[0].iamt 4(b)
							int elgrevsize = gstr3b.getItcElg().getItcRev().size();
							if(elgrevsize >= 1) {
								if(isNotEmpty(gstr3b.getItcElg().getItcRev().get(0))) {
									if(isNotEmpty(gstr3b.getItcElg().getItcRev().get(0).getIamt())) {
										gstr3b_VS_gstr2a.setCredit_Reversed_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR3B()+gstr3b.getItcElg().getItcRev().get(0).getIamt());
										gstr3b_VS_gstr2a.setCredit_Reversed_IGST_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_IGST_GSTR3B()+gstr3b.getItcElg().getItcRev().get(0).getIamt());
									}
									if(isNotEmpty(gstr3b.getItcElg().getItcRev().get(0).getCamt())) {
										gstr3b_VS_gstr2a.setCredit_Reversed_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR3B()+gstr3b.getItcElg().getItcRev().get(0).getCamt());
										gstr3b_VS_gstr2a.setCredit_Reversed_CGST_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_CGST_GSTR3B()+gstr3b.getItcElg().getItcRev().get(0).getCamt());
									}
									if(isNotEmpty(gstr3b.getItcElg().getItcRev().get(0).getSamt())) {
										gstr3b_VS_gstr2a.setCredit_Reversed_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR3B()+gstr3b.getItcElg().getItcRev().get(0).getSamt());
										gstr3b_VS_gstr2a.setCredit_Reversed_SGST_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_SGST_GSTR3B()+gstr3b.getItcElg().getItcRev().get(0).getSamt());
									}
								}
							}
							if(elgrevsize >= 2) {
								if(isNotEmpty(gstr3b.getItcElg().getItcRev().get(1))) {
									if(isNotEmpty(gstr3b.getItcElg().getItcRev().get(1).getIamt())) {
										gstr3b_VS_gstr2a.setCredit_Reversed_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR3B()+gstr3b.getItcElg().getItcRev().get(1).getIamt());
										gstr3b_VS_gstr2a.setCredit_Reversed_IGST_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_IGST_GSTR3B()+gstr3b.getItcElg().getItcRev().get(1).getIamt());
									}
									if(isNotEmpty(gstr3b.getItcElg().getItcRev().get(1).getCamt())) {
										gstr3b_VS_gstr2a.setCredit_Reversed_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR3B()+gstr3b.getItcElg().getItcRev().get(1).getCamt());
										gstr3b_VS_gstr2a.setCredit_Reversed_CGST_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_CGST_GSTR3B()+gstr3b.getItcElg().getItcRev().get(1).getCamt());
									}
									if(isNotEmpty(gstr3b.getItcElg().getItcRev().get(1).getIamt())) {
										gstr3b_VS_gstr2a.setCredit_Reversed_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR3B()+gstr3b.getItcElg().getItcRev().get(1).getSamt());
										gstr3b_VS_gstr2a.setCredit_Reversed_SGST_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_SGST_GSTR3B()+gstr3b.getItcElg().getItcRev().get(1).getSamt());
									}
								}
							}
						}
					}
				}
			});
		}
		//Page<? extends InvoiceParent> gstr1Lst=clientService.getInvoices(null, client, userid, MasterGSTConstants.GSTR1,year);
		
		Page<? extends InvoiceParent> purchaseList = clientService.getInvoices(null, client, userid, MasterGSTConstants.PURCHASE_REGISTER,year);
				//clientService.getInvoices(null,client,"userid",MasterGSTConstants.PURCHASE_REGISTER,month,year);
				//clientService.getInvoices(null, client, userid, MasterGSTConstants.PURCHASE_REGISTER,year);
				//clientService.getInvoices(null, clientid, MasterGSTConstants.PURCHASE_REGISTER, month, year);
		Page<? extends InvoiceParent> gstr2AList =  clientService.getInvoices(null, client, userid, MasterGSTConstants.GSTR2A,year);
				//clientService.getInvoices(null,client,"userid",MasterGSTConstants.GSTR2,month,year);
				//clientService.getInvoices(null, client, userid, MasterGSTConstants.GSTR2,year);
				//clientService.getInvoices(null, clientid, MasterGSTConstants.GSTR2, month, year);
		//GSTR2(Purchase Register) variables
		gstr3b_VS_gstr2a.setImportGoods_IGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setImportServices_IGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setRcm_CGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setRcm_SGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setRcm_IGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setIsd_CGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setIsd_SGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setIsd_IGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setEligible_CGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setEligible_SGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setEligible_IGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setIneligible_CGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setIneligible_SGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setIneligible_IGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setCredit_Reversed_CGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setCredit_Reversed_SGST_GSTR2(0d);
		gstr3b_VS_gstr2a.setCredit_Reversed_IGST_GSTR2(0d);
		if(isNotEmpty(purchaseList)) {
			purchaseList.forEach(purchase->{
				if(purchase.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
					String reverseChargeType = "";
					if(isNotEmpty(purchase.getRevchargetype())) {
						reverseChargeType = purchase.getRevchargetype();
					}else {
						reverseChargeType = "Regular";
					}
					//import of goods only igst
					if("Regular".equalsIgnoreCase(reverseChargeType)) {
						if(isNotEmpty(purchase.getItems())){
							purchase.getItems().forEach(items->{
								if(isNotEmpty(items.getIgstamount())){
									gstr3b_VS_gstr2a.setImportGoods_IGST_GSTR2(gstr3b_VS_gstr2a.getImportGoods_IGST_GSTR2()+items.getIgstamount());
								}
							});
						}
					}else {
						if(isNotEmpty(purchase.getItems())){
							purchase.getItems().forEach(items->{
								if(isNotEmpty(items.getIgstamount())){
									gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()+items.getIgstamount());
									gstr3b_VS_gstr2a.setRcm_IGST_GSTR2(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2()+items.getIgstamount());
								}
							});
						}
					}
				}
				
				if(purchase.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_SERVICES)) {
					String reverseChargeType = "";
					if(isNotEmpty(purchase.getRevchargetype())) {
						reverseChargeType = purchase.getRevchargetype();
					}else {
						reverseChargeType = "Regular";
					}
					//import of services only igst
					if("Regular".equalsIgnoreCase(reverseChargeType)) {
						if(isNotEmpty(purchase.getItems())){
							purchase.getItems().forEach(items->{
								if(isNotEmpty(items.getIgstamount())){
									gstr3b_VS_gstr2a.setImportServices_IGST_GSTR2(gstr3b_VS_gstr2a.getImportServices_IGST_GSTR2()+items.getIgstamount());
								}
							});
						}
					}else {
						if(isNotEmpty(purchase.getItems())){
							purchase.getItems().forEach(items->{
								if(isNotEmpty(items.getIgstamount())){
									gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()+items.getIgstamount());
									gstr3b_VS_gstr2a.setRcm_IGST_GSTR2(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2()+items.getIgstamount());
								}
							});
						}
					}
				}
	
				if(purchase.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISD)) {
					String reverseChargeType = "";
					if(isNotEmpty(purchase.getRevchargetype())) {
						reverseChargeType = purchase.getRevchargetype();
					}else {
						reverseChargeType = "Regular";
					}
					if("Regular".equalsIgnoreCase(reverseChargeType)) {
						if(isNotEmpty(purchase.getItems())){
							purchase.getItems().forEach(items->{
								if(isNotEmpty(items.getCgstamount())){
									gstr3b_VS_gstr2a.setIsd_GSTR2(gstr3b_VS_gstr2a.getIsd_GSTR2()+items.getCgstamount());
									gstr3b_VS_gstr2a.setIsd_CGST_GSTR2(gstr3b_VS_gstr2a.getIsd_CGST_GSTR2()+items.getCgstamount());
								}
								if(isNotEmpty(items.getSgstamount())){
									gstr3b_VS_gstr2a.setIsd_GSTR2(gstr3b_VS_gstr2a.getIsd_GSTR2()+items.getSgstamount());
									gstr3b_VS_gstr2a.setIsd_SGST_GSTR2(gstr3b_VS_gstr2a.getIsd_SGST_GSTR2()+items.getSgstamount());
								}
								if(isNotEmpty(items.getIgstamount())){
									gstr3b_VS_gstr2a.setIsd_GSTR2(gstr3b_VS_gstr2a.getIsd_GSTR2()+items.getIgstamount());
									gstr3b_VS_gstr2a.setIsd_IGST_GSTR2(gstr3b_VS_gstr2a.getIsd_IGST_GSTR2()+items.getIgstamount());
		
								}
							});
						}
					}else {
						if(isNotEmpty(purchase.getItems())){
							purchase.getItems().forEach(items->{
								if(isNotEmpty(items.getCgstamount())){
									gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()+items.getCgstamount());
									gstr3b_VS_gstr2a.setRcm_CGST_GSTR2(gstr3b_VS_gstr2a.getRcm_CGST_GSTR2()+items.getCgstamount());
								}
								if(isNotEmpty(items.getSgstamount())){
									gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()+items.getSgstamount());
									gstr3b_VS_gstr2a.setRcm_SGST_GSTR2(gstr3b_VS_gstr2a.getRcm_SGST_GSTR2()+items.getSgstamount());
								}
								if(isNotEmpty(items.getIgstamount())){
									gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()+items.getIgstamount());
									gstr3b_VS_gstr2a.setRcm_IGST_GSTR2(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2()+items.getIgstamount());
								}
							});
						}
					}
				}
				
				if(purchase.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B) || purchase.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2BUR)) {
					String reverseChargeType = "";
					if(isNotEmpty(purchase.getRevchargetype())) {
						reverseChargeType = purchase.getRevchargetype();
					}else {
						reverseChargeType = "Regular";
					}
					if("Regular".equalsIgnoreCase(reverseChargeType)) {
						if(isNotEmpty(purchase.getItems())){
							purchase.getItems().forEach(items->{
								String itcElgType = "";
								if(isNotEmpty(items.getElg())) {
									itcElgType = items.getElg();
								}
								if("NO".equalsIgnoreCase(itcElgType)) {
									if(isNotEmpty(items.getCgstamount())){
										gstr3b_VS_gstr2a.setIneligible_GSTR2(gstr3b_VS_gstr2a.getIneligible_GSTR2()+items.getCgstamount());
										gstr3b_VS_gstr2a.setIneligible_CGST_GSTR2(gstr3b_VS_gstr2a.getIneligible_CGST_GSTR2()+items.getCgstamount());
									}
									if(isNotEmpty(items.getSgstamount())){
										gstr3b_VS_gstr2a.setIneligible_GSTR2(gstr3b_VS_gstr2a.getIneligible_GSTR2()+items.getSgstamount());
										gstr3b_VS_gstr2a.setIneligible_SGST_GSTR2(gstr3b_VS_gstr2a.getIneligible_SGST_GSTR2()+items.getSgstamount());
									}
									if(isNotEmpty(items.getIgstamount())){
										gstr3b_VS_gstr2a.setIneligible_GSTR2(gstr3b_VS_gstr2a.getIneligible_GSTR2()+items.getIgstamount());
										gstr3b_VS_gstr2a.setIneligible_IGST_GSTR2(gstr3b_VS_gstr2a.getIneligible_IGST_GSTR2()+items.getIgstamount());
									}
								}else {
									if(isNotEmpty(items.getCgstamount())){
										gstr3b_VS_gstr2a.setEligible_GSTR2(gstr3b_VS_gstr2a.getEligible_GSTR2()+items.getCgstamount());
										gstr3b_VS_gstr2a.setEligible_CGST_GSTR2(gstr3b_VS_gstr2a.getEligible_CGST_GSTR2()+items.getCgstamount());
									}
									if(isNotEmpty(items.getSgstamount())){
										gstr3b_VS_gstr2a.setEligible_GSTR2(gstr3b_VS_gstr2a.getEligible_GSTR2()+items.getSgstamount());
										gstr3b_VS_gstr2a.setEligible_SGST_GSTR2(gstr3b_VS_gstr2a.getEligible_SGST_GSTR2()+items.getSgstamount());
									}
									if(isNotEmpty(items.getIgstamount())){
										gstr3b_VS_gstr2a.setEligible_GSTR2(gstr3b_VS_gstr2a.getEligible_GSTR2()+items.getIgstamount());
										gstr3b_VS_gstr2a.setEligible_IGST_GSTR2(gstr3b_VS_gstr2a.getEligible_IGST_GSTR2()+items.getIgstamount());
									}
								}
							});
						}
					}else {
						if(isNotEmpty(purchase.getItems())){
							purchase.getItems().forEach(items->{
								if(isNotEmpty(items.getCgstamount())){
									gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()+items.getCgstamount());
									gstr3b_VS_gstr2a.setRcm_CGST_GSTR2(gstr3b_VS_gstr2a.getRcm_CGST_GSTR2()+items.getCgstamount());
								}
								if(isNotEmpty(items.getSgstamount())){
									gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()+items.getSgstamount());
									gstr3b_VS_gstr2a.setRcm_SGST_GSTR2(gstr3b_VS_gstr2a.getRcm_SGST_GSTR2()+items.getSgstamount());
								}
								if(isNotEmpty(items.getIgstamount())){
									gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()+items.getIgstamount());
									gstr3b_VS_gstr2a.setRcm_IGST_GSTR2(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2()+items.getIgstamount());
								}
							});
						}
					}
				}
				if(purchase.getInvtype().equalsIgnoreCase(MasterGSTConstants.ITC_REVERSAL)) {
					String reverseChargeType = "";
					if(isNotEmpty(purchase.getRevchargetype())) {
						reverseChargeType = purchase.getRevchargetype();
					}else {
						reverseChargeType = "Regular";
					}
					if("Regular".equalsIgnoreCase(reverseChargeType)) {
						if(isNotEmpty(purchase.getItems())){
							purchase.getItems().forEach(items->{
								if(isNotEmpty(items.getCgstamount())){
									gstr3b_VS_gstr2a.setCredit_Reversed_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR2()+items.getCgstamount());
									gstr3b_VS_gstr2a.setCredit_Reversed_CGST_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_CGST_GSTR2()+items.getCgstamount());
								}
								if(isNotEmpty(items.getSgstamount())){
									gstr3b_VS_gstr2a.setCredit_Reversed_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR2()+items.getSgstamount());
									gstr3b_VS_gstr2a.setCredit_Reversed_SGST_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_SGST_GSTR2()+items.getSgstamount());
								}
								if(isNotEmpty(items.getIgstamount())){
									gstr3b_VS_gstr2a.setCredit_Reversed_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR2()+items.getIgstamount());
									gstr3b_VS_gstr2a.setCredit_Reversed_IGST_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_IGST_GSTR2()+items.getIgstamount());
								}
							});
						}
					}else {
						if(isNotEmpty(purchase.getItems())){
							purchase.getItems().forEach(items->{
								if(isNotEmpty(items.getCgstamount())){
									gstr3b_VS_gstr2a.setCredit_Reversed_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR2()+items.getCgstamount());
									gstr3b_VS_gstr2a.setCredit_Reversed_CGST_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_CGST_GSTR2()+items.getCgstamount());
								}
								if(isNotEmpty(items.getSgstamount())){
									gstr3b_VS_gstr2a.setCredit_Reversed_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR2()+items.getSgstamount());
									gstr3b_VS_gstr2a.setCredit_Reversed_SGST_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_SGST_GSTR2()+items.getSgstamount());
								}
								if(isNotEmpty(items.getIgstamount())){
									gstr3b_VS_gstr2a.setCredit_Reversed_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR2()+items.getIgstamount());
									gstr3b_VS_gstr2a.setCredit_Reversed_IGST_GSTR2(gstr3b_VS_gstr2a.getCredit_Reversed_IGST_GSTR2()+items.getIgstamount());
								}
							});
						}
					}
				}
				
				if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(purchase.getInvtype()) || MasterGSTConstants.CDNUR.equalsIgnoreCase(purchase.getInvtype())) {
					String org_invno = "";
					String docType = "";
					
					if(purchase.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
						docType = purchase.getCdn().get(0).getNt().get(0).getNtty();
						org_invno = purchase.getCdn().get(0).getNt().get(0).getInum();
					}else {
						docType = purchase.getCdnur().get(0).getNtty();
						org_invno = purchase.getCdnur().get(0).getInum();
					}
					
					if (docType.equalsIgnoreCase("C")) {
						String creverseChargeType = "";
						if(isNotEmpty(purchase.getRevchargetype())) {
							creverseChargeType = purchase.getRevchargetype();
						}else {
							creverseChargeType = "Regular";
						}	
						if("Regular".equals(creverseChargeType)) {
							if(isNotEmpty(purchase.getItems())){
								purchase.getItems().forEach(items->{
									String itcElgType = "";
									if(isNotEmpty(items.getElg())) {
										itcElgType = items.getElg();
									}
									if("NO".equalsIgnoreCase(itcElgType)) {
										if(isNotEmpty(items.getCgstamount())){
											gstr3b_VS_gstr2a.setIneligible_GSTR2(gstr3b_VS_gstr2a.getIneligible_GSTR2()-items.getCgstamount());
											gstr3b_VS_gstr2a.setIneligible_CGST_GSTR2(gstr3b_VS_gstr2a.getIneligible_CGST_GSTR2()-items.getCgstamount());
										}
										if(isNotEmpty(items.getSgstamount())){
											gstr3b_VS_gstr2a.setIneligible_GSTR2(gstr3b_VS_gstr2a.getIneligible_GSTR2()-items.getSgstamount());
											gstr3b_VS_gstr2a.setIneligible_SGST_GSTR2(gstr3b_VS_gstr2a.getIneligible_SGST_GSTR2()-items.getSgstamount());
										}
										if(isNotEmpty(items.getIgstamount())){
											gstr3b_VS_gstr2a.setIneligible_GSTR2(gstr3b_VS_gstr2a.getIneligible_GSTR2()-items.getIgstamount());
											gstr3b_VS_gstr2a.setIneligible_IGST_GSTR2(gstr3b_VS_gstr2a.getIneligible_IGST_GSTR2()-items.getIgstamount());
										}
									}else {
										if(isNotEmpty(items.getCgstamount())){
											gstr3b_VS_gstr2a.setEligible_GSTR2(gstr3b_VS_gstr2a.getEligible_GSTR2()-items.getCgstamount());
											gstr3b_VS_gstr2a.setEligible_CGST_GSTR2(gstr3b_VS_gstr2a.getEligible_CGST_GSTR2()-items.getCgstamount());
										}
										if(isNotEmpty(items.getSgstamount())){
											gstr3b_VS_gstr2a.setEligible_GSTR2(gstr3b_VS_gstr2a.getEligible_GSTR2()-items.getSgstamount());
											gstr3b_VS_gstr2a.setEligible_SGST_GSTR2(gstr3b_VS_gstr2a.getEligible_SGST_GSTR2()-items.getSgstamount());
										}
										if(isNotEmpty(items.getIgstamount())){
											gstr3b_VS_gstr2a.setEligible_GSTR2(gstr3b_VS_gstr2a.getEligible_GSTR2()-items.getIgstamount());
											gstr3b_VS_gstr2a.setEligible_IGST_GSTR2(gstr3b_VS_gstr2a.getEligible_IGST_GSTR2()-items.getIgstamount());
										}
									}
								});
							}
						}else {
							if(isNotEmpty(purchase.getItems())){
								purchase.getItems().forEach(items->{
									if(isNotEmpty(items.getCgstamount())){
										gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()-items.getCgstamount());
										gstr3b_VS_gstr2a.setRcm_CGST_GSTR2(gstr3b_VS_gstr2a.getRcm_CGST_GSTR2()-items.getCgstamount());
									}
									if(isNotEmpty(items.getSgstamount())){
										gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()-items.getSgstamount());
										gstr3b_VS_gstr2a.setRcm_SGST_GSTR2(gstr3b_VS_gstr2a.getRcm_SGST_GSTR2()-items.getSgstamount());
									}
									if(isNotEmpty(items.getIgstamount())){
										gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()-items.getIgstamount());
										gstr3b_VS_gstr2a.setRcm_IGST_GSTR2(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2()-items.getIgstamount());
									}
								});
							}
						}			
					} else if (docType.equalsIgnoreCase("D")) {
						String creverseChargeType = "";
						if(isNotEmpty(purchase.getRevchargetype())) {
							creverseChargeType = purchase.getRevchargetype();
						}else {
							creverseChargeType = "Regular";
						}
						if("Regular".equals(creverseChargeType)) {
							if(isNotEmpty(purchase.getItems())){
								purchase.getItems().forEach(items->{
									String itcElgType = "";
									if(isNotEmpty(items.getElg())) {
										itcElgType = items.getElg();
									}
									if("NO".equalsIgnoreCase(itcElgType)) {
										if(isNotEmpty(items.getCgstamount())){
											gstr3b_VS_gstr2a.setIneligible_GSTR2(gstr3b_VS_gstr2a.getIneligible_GSTR2()+items.getCgstamount());
											gstr3b_VS_gstr2a.setIneligible_CGST_GSTR2(gstr3b_VS_gstr2a.getIneligible_CGST_GSTR2()+items.getCgstamount());
										}
										if(isNotEmpty(items.getSgstamount())){
											gstr3b_VS_gstr2a.setIneligible_GSTR2(gstr3b_VS_gstr2a.getIneligible_GSTR2()+items.getSgstamount());
											gstr3b_VS_gstr2a.setIneligible_SGST_GSTR2(gstr3b_VS_gstr2a.getIneligible_SGST_GSTR2()+items.getSgstamount());
										}
										if(isNotEmpty(items.getIgstamount())){
											gstr3b_VS_gstr2a.setIneligible_GSTR2(gstr3b_VS_gstr2a.getIneligible_GSTR2()+items.getIgstamount());
											gstr3b_VS_gstr2a.setIneligible_IGST_GSTR2(gstr3b_VS_gstr2a.getIneligible_IGST_GSTR2()+items.getIgstamount());
										}
									}else {
										if(isNotEmpty(items.getCgstamount())){
											gstr3b_VS_gstr2a.setEligible_GSTR2(gstr3b_VS_gstr2a.getEligible_GSTR2()+items.getCgstamount());
											gstr3b_VS_gstr2a.setEligible_CGST_GSTR2(gstr3b_VS_gstr2a.getEligible_CGST_GSTR2()+items.getCgstamount());
										}
										if(isNotEmpty(items.getSgstamount())){
											gstr3b_VS_gstr2a.setEligible_GSTR2(gstr3b_VS_gstr2a.getEligible_GSTR2()+items.getSgstamount());
											gstr3b_VS_gstr2a.setEligible_SGST_GSTR2(gstr3b_VS_gstr2a.getEligible_SGST_GSTR2()+items.getSgstamount());
										}
										if(isNotEmpty(items.getIgstamount())){
											gstr3b_VS_gstr2a.setEligible_GSTR2(gstr3b_VS_gstr2a.getEligible_GSTR2()+items.getIgstamount());
											gstr3b_VS_gstr2a.setEligible_IGST_GSTR2(gstr3b_VS_gstr2a.getEligible_IGST_GSTR2()+items.getIgstamount());
										}
									}
								});
							}
						}else {
							if(isNotEmpty(purchase.getItems())){
								purchase.getItems().forEach(items->{
									if(isNotEmpty(items.getCgstamount())){
										gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()+items.getCgstamount());
										gstr3b_VS_gstr2a.setRcm_CGST_GSTR2(gstr3b_VS_gstr2a.getRcm_CGST_GSTR2()+items.getCgstamount());
									}
									if(isNotEmpty(items.getSgstamount())){
										gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()+items.getSgstamount());
										gstr3b_VS_gstr2a.setRcm_SGST_GSTR2(gstr3b_VS_gstr2a.getRcm_SGST_GSTR2()+items.getSgstamount());
									}
									if(isNotEmpty(items.getIgstamount())){
										gstr3b_VS_gstr2a.setRcm_GSTR2(gstr3b_VS_gstr2a.getRcm_GSTR2()+items.getIgstamount());
										gstr3b_VS_gstr2a.setRcm_IGST_GSTR2(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2()+items.getIgstamount());
									}
								});
							}
						}			
					}
				}
			});
		}
		
		//GSTR2(Purchase Register) - GSTR3B
		gstr3b_VS_gstr2a.setDiffImportGoods_IGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getImportGoods_IGST_GSTR2()-gstr3b_VS_gstr2a.getImportGoods_IGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffImportServices_IGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getImportServices_IGST_GSTR2()-gstr3b_VS_gstr2a.getImportServices_IGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffRCM_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getRcm_GSTR2()-gstr3b_VS_gstr2a.getRcm_GSTR3B());
		gstr3b_VS_gstr2a.setDiffRCM_IGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2()-gstr3b_VS_gstr2a.getRcm_IGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffRCM_CGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getRcm_CGST_GSTR2()-gstr3b_VS_gstr2a.getRcm_CGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffRCM_SGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getRcm_SGST_GSTR2()-gstr3b_VS_gstr2a.getRcm_SGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffISD_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getIsd_GSTR2()-gstr3b_VS_gstr2a.getIsd_GSTR3B());
		gstr3b_VS_gstr2a.setDiffISD_IGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getIsd_IGST_GSTR2()-gstr3b_VS_gstr2a.getIsd_IGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffISD_CGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getIsd_CGST_GSTR2()-gstr3b_VS_gstr2a.getIsd_CGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffISD_SGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getIsd_SGST_GSTR2()-gstr3b_VS_gstr2a.getIsd_SGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffCredit_Reversed_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_GSTR2()-gstr3b_VS_gstr2a.getCredit_Reversed_GSTR3B());
		gstr3b_VS_gstr2a.setDiffCredit_Reversed_IGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_IGST_GSTR2()-gstr3b_VS_gstr2a.getCredit_Reversed_IGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffCredit_Reversed_CGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_CGST_GSTR2()-gstr3b_VS_gstr2a.getCredit_Reversed_CGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffCredit_Reversed_SGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getCredit_Reversed_SGST_GSTR2()-gstr3b_VS_gstr2a.getCredit_Reversed_SGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffEligible_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getEligible_GSTR2()-gstr3b_VS_gstr2a.getEligible_GSTR3B());
		gstr3b_VS_gstr2a.setDiffEligible_IGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getEligible_IGST_GSTR2()-gstr3b_VS_gstr2a.getEligible_IGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffEligible_CGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getEligible_CGST_GSTR2()-gstr3b_VS_gstr2a.getEligible_CGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffEligible_SGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getEligible_SGST_GSTR2()-gstr3b_VS_gstr2a.getEligible_SGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffIneligible_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getIneligible_GSTR2()-gstr3b_VS_gstr2a.getIneligible_GSTR3B());
		gstr3b_VS_gstr2a.setDiffIneligible_IGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getIneligible_IGST_GSTR2()-gstr3b_VS_gstr2a.getIneligible_IGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffIneligible_CGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getIneligible_CGST_GSTR2()-gstr3b_VS_gstr2a.getIneligible_CGST_GSTR3B());
		gstr3b_VS_gstr2a.setDiffIneligible_SGST_GSTR2_GSTR3B(gstr3b_VS_gstr2a.getIneligible_SGST_GSTR2()-gstr3b_VS_gstr2a.getIneligible_SGST_GSTR3B());
		if(isNotEmpty(gstr2AList)) {
			gstr2AList.forEach(gstr2a->{
				if(gstr2a.isAmendment()) {
					if(gstr2a.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
						String reverseChargeType = "";
						if(isNotEmpty(gstr2a.getRevchargetype())) {
							reverseChargeType = gstr2a.getRevchargetype();
						}else {
							reverseChargeType = "Regular";
						}
						//import of goods only igst
						if("Regular".equalsIgnoreCase(reverseChargeType)) {
							if(isNotEmpty(gstr2a.getItems())){
								gstr2a.getItems().forEach(items->{
									if(isNotEmpty(items.getIgstamount())){
										gstr3b_VS_gstr2a.setImportGoods_IGST_GSTR2A(gstr3b_VS_gstr2a.getImportGoods_IGST_GSTR2A()+items.getIgstamount());
									}
								});
							}
						}else {
							if(isNotEmpty(gstr2a.getItems())){
								gstr2a.getItems().forEach(items->{
									if(isNotEmpty(items.getIgstamount())){
										gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()+items.getIgstamount());
										gstr3b_VS_gstr2a.setRcm_IGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2A()+items.getIgstamount());
									}
								});
							}
						}
					}
					if(gstr2a.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B) || gstr2a.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2BUR)) {
						String reverseChargeType = "";
						if(isNotEmpty(gstr2a.getRevchargetype())) {
							reverseChargeType = gstr2a.getRevchargetype();
						}else {
							reverseChargeType = "Regular";
						}
						if("Regular".equalsIgnoreCase(reverseChargeType)) {
							if(isNotEmpty(gstr2a.getItems())){
								gstr2a.getItems().forEach(items->{
									String itcElgType = "";
									if(isNotEmpty(items.getElg())) {
										itcElgType = items.getElg();
									}
									if("NO".equalsIgnoreCase(itcElgType)) {
										if(isNotEmpty(items.getCgstamount())){
											gstr3b_VS_gstr2a.setIneligible_GSTR2A(gstr3b_VS_gstr2a.getIneligible_GSTR2A()+items.getCgstamount());
											gstr3b_VS_gstr2a.setIneligible_CGST_GSTR2A(gstr3b_VS_gstr2a.getIneligible_CGST_GSTR2A()+items.getCgstamount());
										}
										if(isNotEmpty(items.getSgstamount())){
											gstr3b_VS_gstr2a.setIneligible_GSTR2A(gstr3b_VS_gstr2a.getIneligible_GSTR2A()+items.getSgstamount());
											gstr3b_VS_gstr2a.setIneligible_SGST_GSTR2A(gstr3b_VS_gstr2a.getIneligible_SGST_GSTR2A()+items.getSgstamount());
										}
										if(isNotEmpty(items.getIgstamount())){
											gstr3b_VS_gstr2a.setIneligible_GSTR2A(gstr3b_VS_gstr2a.getIneligible_GSTR2A()+items.getIgstamount());
											gstr3b_VS_gstr2a.setIneligible_IGST_GSTR2A(gstr3b_VS_gstr2a.getIneligible_IGST_GSTR2A()+items.getIgstamount());
										}
									}else {
										if(isNotEmpty(items.getCgstamount())){
											gstr3b_VS_gstr2a.setEligible_GSTR2A(gstr3b_VS_gstr2a.getEligible_GSTR2A()+items.getCgstamount());
											gstr3b_VS_gstr2a.setEligible_CGST_GSTR2A(gstr3b_VS_gstr2a.getEligible_CGST_GSTR2()+items.getCgstamount());
										}
										if(isNotEmpty(items.getSgstamount())){
											gstr3b_VS_gstr2a.setEligible_GSTR2A(gstr3b_VS_gstr2a.getEligible_GSTR2A()+items.getSgstamount());
											gstr3b_VS_gstr2a.setEligible_SGST_GSTR2A(gstr3b_VS_gstr2a.getEligible_SGST_GSTR2()+items.getSgstamount());
										}
										if(isNotEmpty(items.getIgstamount())){
											gstr3b_VS_gstr2a.setEligible_GSTR2A(gstr3b_VS_gstr2a.getEligible_GSTR2A()+items.getIgstamount());
											gstr3b_VS_gstr2a.setEligible_IGST_GSTR2A(gstr3b_VS_gstr2a.getEligible_IGST_GSTR2A()+items.getIgstamount());
										}
									}
								});
							}
						}else {
							gstr2a.getItems().forEach(items->{
								if(isNotEmpty(items.getCgstamount())){
									gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()+items.getCgstamount());
									gstr3b_VS_gstr2a.setRcm_CGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_CGST_GSTR2A()+items.getCgstamount());
								}
								if(isNotEmpty(items.getSgstamount())){
									gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()+items.getSgstamount());
									gstr3b_VS_gstr2a.setRcm_SGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_SGST_GSTR2A()+items.getSgstamount());
								}
								if(isNotEmpty(items.getIgstamount())){
									gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()+items.getIgstamount());
									gstr3b_VS_gstr2a.setRcm_IGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2A()+items.getIgstamount());
								}
							});
						}
					}
					if(gstr2a.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISD)) {
						String reverseChargeType = "";
						if(isNotEmpty(gstr2a.getRevchargetype())) {
							reverseChargeType = gstr2a.getRevchargetype();
						}else {
							reverseChargeType = "Regular";
						}
						if("Regular".equalsIgnoreCase(reverseChargeType)) {
							if(isNotEmpty(gstr2a.getItems())){
								gstr2a.getItems().forEach(items->{
									if(isNotEmpty(items.getCgstamount())){
										gstr3b_VS_gstr2a.setIsd_GSTR2A(gstr3b_VS_gstr2a.getIsd_GSTR2A()+items.getCgstamount());
										gstr3b_VS_gstr2a.setIsd_CGST_GSTR2A(gstr3b_VS_gstr2a.getIsd_CGST_GSTR2A()+items.getCgstamount());
									}
									if(isNotEmpty(items.getSgstamount())){
										gstr3b_VS_gstr2a.setIsd_GSTR2A(gstr3b_VS_gstr2a.getIsd_GSTR2A()+items.getSgstamount());
										gstr3b_VS_gstr2a.setIsd_SGST_GSTR2A(gstr3b_VS_gstr2a.getIsd_SGST_GSTR2A()+items.getSgstamount());
									}
									if(isNotEmpty(items.getIgstamount())){
										gstr3b_VS_gstr2a.setIsd_GSTR2A(gstr3b_VS_gstr2a.getIsd_GSTR2A()+items.getIgstamount());
										gstr3b_VS_gstr2a.setIsd_IGST_GSTR2A(gstr3b_VS_gstr2a.getIsd_IGST_GSTR2A()+items.getIgstamount());
			
									}
								});
							}
						}else {
							if(isNotEmpty(gstr2a.getItems())){
								gstr2a.getItems().forEach(items->{
									if(isNotEmpty(items.getCgstamount())){
										gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()+items.getCgstamount());
										gstr3b_VS_gstr2a.setRcm_CGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_CGST_GSTR2A()+items.getCgstamount());
									}
									if(isNotEmpty(items.getSgstamount())){
										gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()+items.getSgstamount());
										gstr3b_VS_gstr2a.setRcm_SGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_SGST_GSTR2A()+items.getSgstamount());
									}
									if(isNotEmpty(items.getIgstamount())){
										gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()+items.getIgstamount());
										gstr3b_VS_gstr2a.setRcm_IGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2A()+items.getIgstamount());
									}
								});
							}
						}
					}
					if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(gstr2a.getInvtype()) || MasterGSTConstants.CDNUR.equalsIgnoreCase(gstr2a.getInvtype())) {
						String org_invno = "";
						String docType = "";
						
						if(gstr2a.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
							docType = gstr2a.getCdn().get(0).getNt().get(0).getNtty();
							org_invno = gstr2a.getCdn().get(0).getNt().get(0).getInum();
						}else {
							docType = gstr2a.getCdnur().get(0).getNtty();
							org_invno = gstr2a.getCdnur().get(0).getInum();
						}
						if (docType.equalsIgnoreCase("C")) {
							if(gstr2a.isAmendment()){
								String creverseChargeType = "";
								if(isNotEmpty(gstr2a.getRevchargetype())) {
									creverseChargeType = gstr2a.getRevchargetype();
								}else {
									creverseChargeType = "Regular";
								}
								if("Regular".equals(creverseChargeType)) {
									if(isNotEmpty(gstr2a.getItems())){
										gstr2a.getItems().forEach(items->{
											String itcElgType = "";
											if(isNotEmpty(items.getElg())) {
												itcElgType = items.getElg();
											}
											if("NO".equalsIgnoreCase(itcElgType)) {
												if(isNotEmpty(items.getCgstamount())){
													gstr3b_VS_gstr2a.setIneligible_GSTR2A(gstr3b_VS_gstr2a.getIneligible_GSTR2A()-items.getCgstamount());
													gstr3b_VS_gstr2a.setIneligible_CGST_GSTR2A(gstr3b_VS_gstr2a.getIneligible_CGST_GSTR2A()-items.getCgstamount());
												}
												if(isNotEmpty(items.getSgstamount())){
													gstr3b_VS_gstr2a.setIneligible_GSTR2A(gstr3b_VS_gstr2a.getIneligible_GSTR2A()-items.getSgstamount());
													gstr3b_VS_gstr2a.setIneligible_SGST_GSTR2A(gstr3b_VS_gstr2a.getIneligible_SGST_GSTR2A()-items.getSgstamount());
												}
												if(isNotEmpty(items.getIgstamount())){
													gstr3b_VS_gstr2a.setIneligible_GSTR2A(gstr3b_VS_gstr2a.getIneligible_GSTR2A()-items.getIgstamount());
													gstr3b_VS_gstr2a.setIneligible_IGST_GSTR2A(gstr3b_VS_gstr2a.getIneligible_IGST_GSTR2A()-items.getIgstamount());
												}
											}else {
												if(isNotEmpty(items.getCgstamount())){
													gstr3b_VS_gstr2a.setEligible_GSTR2A(gstr3b_VS_gstr2a.getEligible_GSTR2A()-items.getCgstamount());
													gstr3b_VS_gstr2a.setEligible_CGST_GSTR2A(gstr3b_VS_gstr2a.getEligible_CGST_GSTR2A()-items.getCgstamount());
												}
												if(isNotEmpty(items.getSgstamount())){
													gstr3b_VS_gstr2a.setEligible_GSTR2A(gstr3b_VS_gstr2a.getEligible_GSTR2A()-items.getSgstamount());
													gstr3b_VS_gstr2a.setEligible_SGST_GSTR2A(gstr3b_VS_gstr2a.getEligible_SGST_GSTR2A()-items.getSgstamount());
												}
												if(isNotEmpty(items.getIgstamount())){
													gstr3b_VS_gstr2a.setEligible_GSTR2A(gstr3b_VS_gstr2a.getEligible_GSTR2A()-items.getIgstamount());
													gstr3b_VS_gstr2a.setEligible_IGST_GSTR2A(gstr3b_VS_gstr2a.getEligible_IGST_GSTR2A()-items.getIgstamount());
												}
											}
										});				
									}
								}else {
									if(isNotEmpty(gstr2a.getItems())){
										gstr2a.getItems().forEach(items->{
											if(isNotEmpty(items.getCgstamount())){
												gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()-items.getCgstamount());
												gstr3b_VS_gstr2a.setRcm_CGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_CGST_GSTR2A()-items.getCgstamount());
											}
											if(isNotEmpty(items.getSgstamount())){
												gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()-items.getSgstamount());
												gstr3b_VS_gstr2a.setRcm_SGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_SGST_GSTR2A()-items.getSgstamount());
											}
											if(isNotEmpty(items.getIgstamount())){
												gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()-items.getIgstamount());
												gstr3b_VS_gstr2a.setRcm_IGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2A()-items.getIgstamount());
											}
										});
									}				
								}
							}
						} else if (docType.equalsIgnoreCase("D")) {
							if(gstr2a.isAmendment()) {
								String creverseChargeType = "";
								if(isNotEmpty(gstr2a.getRevchargetype())) {
									creverseChargeType = gstr2a.getRevchargetype();
								}else {
									creverseChargeType = "Regular";
								}
								if("Regular".equals(creverseChargeType)) {
									if(isNotEmpty(gstr2a.getItems())){
										gstr2a.getItems().forEach(items->{

											String itcElgType = "";
											if(isNotEmpty(items.getElg())) {
												itcElgType = items.getElg();
											}
											if("NO".equalsIgnoreCase(itcElgType)) {
												if(isNotEmpty(items.getCgstamount())){
													gstr3b_VS_gstr2a.setIneligible_GSTR2A(gstr3b_VS_gstr2a.getIneligible_GSTR2A()+items.getCgstamount());
													gstr3b_VS_gstr2a.setIneligible_CGST_GSTR2A(gstr3b_VS_gstr2a.getIneligible_CGST_GSTR2A()+items.getCgstamount());
												}
												if(isNotEmpty(items.getSgstamount())){
													gstr3b_VS_gstr2a.setIneligible_GSTR2A(gstr3b_VS_gstr2a.getIneligible_GSTR2A()+items.getSgstamount());
													gstr3b_VS_gstr2a.setIneligible_SGST_GSTR2A(gstr3b_VS_gstr2a.getIneligible_SGST_GSTR2A()+items.getSgstamount());
												}
												if(isNotEmpty(items.getIgstamount())){
													gstr3b_VS_gstr2a.setIneligible_GSTR2A(gstr3b_VS_gstr2a.getIneligible_GSTR2A()+items.getIgstamount());
													gstr3b_VS_gstr2a.setIneligible_IGST_GSTR2A(gstr3b_VS_gstr2a.getIneligible_IGST_GSTR2A()+items.getIgstamount());
												}
											}else {
												if(isNotEmpty(items.getCgstamount())){
													gstr3b_VS_gstr2a.setEligible_GSTR2A(gstr3b_VS_gstr2a.getEligible_GSTR2A()+items.getCgstamount());
													gstr3b_VS_gstr2a.setEligible_CGST_GSTR2A(gstr3b_VS_gstr2a.getEligible_CGST_GSTR2A()+items.getCgstamount());
												}
												if(isNotEmpty(items.getSgstamount())){
													gstr3b_VS_gstr2a.setEligible_GSTR2A(gstr3b_VS_gstr2a.getEligible_GSTR2A()+items.getSgstamount());
													gstr3b_VS_gstr2a.setEligible_SGST_GSTR2A(gstr3b_VS_gstr2a.getEligible_SGST_GSTR2A()+items.getSgstamount());
												}
												if(isNotEmpty(items.getIgstamount())){
													gstr3b_VS_gstr2a.setEligible_GSTR2A(gstr3b_VS_gstr2a.getEligible_GSTR2A()+items.getIgstamount());
													gstr3b_VS_gstr2a.setEligible_IGST_GSTR2A(gstr3b_VS_gstr2a.getEligible_IGST_GSTR2A()+items.getIgstamount());
												}
											}
										});
									}
								}else {
									if(isNotEmpty(gstr2a.getItems())){
										gstr2a.getItems().forEach(items->{
											if(isNotEmpty(items.getCgstamount())){
												gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()+items.getCgstamount());
												gstr3b_VS_gstr2a.setRcm_CGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_CGST_GSTR2A()+items.getCgstamount());
											}
											if(isNotEmpty(items.getSgstamount())){
												gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()+items.getSgstamount());
												gstr3b_VS_gstr2a.setRcm_SGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_SGST_GSTR2A()+items.getSgstamount());
											}
											if(isNotEmpty(items.getIgstamount())){
												gstr3b_VS_gstr2a.setRcm_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2A()+items.getIgstamount());
												gstr3b_VS_gstr2a.setRcm_IGST_GSTR2A(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2A()+items.getIgstamount());
											}
										});
									}
								}
							}
						}
					}
				}
			});
		}
		//GSTR2(Purchase Register) - GSTR2A
		gstr3b_VS_gstr2a.setDiffImportGoods_IGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getImportGoods_IGST_GSTR2()-gstr3b_VS_gstr2a.getImportGoods_IGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffRCM_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR2()-gstr3b_VS_gstr2a.getRcm_GSTR2A());
		gstr3b_VS_gstr2a.setDiffRCM_IGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getRcm_IGST_GSTR2()-gstr3b_VS_gstr2a.getRcm_IGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffRCM_CGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getRcm_CGST_GSTR2()-gstr3b_VS_gstr2a.getRcm_CGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffRCM_SGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getRcm_SGST_GSTR2()-gstr3b_VS_gstr2a.getRcm_SGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffISD_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getIsd_GSTR2()-gstr3b_VS_gstr2a.getIsd_GSTR2A());
		gstr3b_VS_gstr2a.setDiffISD_IGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getIsd_IGST_GSTR2()-gstr3b_VS_gstr2a.getIsd_IGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffISD_CGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getIsd_CGST_GSTR2()-gstr3b_VS_gstr2a.getIsd_CGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffISD_SGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getIsd_SGST_GSTR2()-gstr3b_VS_gstr2a.getIsd_SGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffEligible_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getEligible_GSTR2()-gstr3b_VS_gstr2a.getEligible_GSTR2A());
		gstr3b_VS_gstr2a.setDiffEligible_IGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getEligible_IGST_GSTR2()-gstr3b_VS_gstr2a.getEligible_IGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffEligible_CGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getEligible_CGST_GSTR2()-gstr3b_VS_gstr2a.getEligible_CGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffEligible_SGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getEligible_SGST_GSTR2()-gstr3b_VS_gstr2a.getEligible_SGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffIneligible_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getIneligible_GSTR2()-gstr3b_VS_gstr2a.getIneligible_GSTR2A());
		gstr3b_VS_gstr2a.setDiffIneligible_IGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getIneligible_IGST_GSTR2()-gstr3b_VS_gstr2a.getIneligible_IGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffIneligible_CGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getIneligible_CGST_GSTR2()-gstr3b_VS_gstr2a.getIneligible_CGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffIneligible_SGST_GSTR2_GSTR2A(gstr3b_VS_gstr2a.getIneligible_SGST_GSTR2()-gstr3b_VS_gstr2a.getIneligible_SGST_GSTR2A());
		
		//GSTR3B - GSTR2A
		gstr3b_VS_gstr2a.setDiffImportGoods_IGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getImportGoods_IGST_GSTR3B()-gstr3b_VS_gstr2a.getImportGoods_IGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffRCM_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getRcm_GSTR3B()-gstr3b_VS_gstr2a.getRcm_GSTR2A());
		gstr3b_VS_gstr2a.setDiffRCM_IGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getRcm_IGST_GSTR3B()-gstr3b_VS_gstr2a.getRcm_IGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffRCM_CGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getRcm_CGST_GSTR3B()-gstr3b_VS_gstr2a.getRcm_CGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffRCM_SGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getRcm_SGST_GSTR3B()-gstr3b_VS_gstr2a.getRcm_SGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffISD_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getIsd_GSTR3B()-gstr3b_VS_gstr2a.getIsd_GSTR2A());
		gstr3b_VS_gstr2a.setDiffISD_IGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getIsd_IGST_GSTR3B()-gstr3b_VS_gstr2a.getIsd_IGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffISD_CGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getIsd_CGST_GSTR3B()-gstr3b_VS_gstr2a.getIsd_CGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffISD_SGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getIsd_SGST_GSTR3B()-gstr3b_VS_gstr2a.getIsd_SGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffEligible_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getEligible_GSTR3B()-gstr3b_VS_gstr2a.getEligible_GSTR2A());
		gstr3b_VS_gstr2a.setDiffEligible_IGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getEligible_IGST_GSTR3B()-gstr3b_VS_gstr2a.getEligible_IGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffEligible_CGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getEligible_CGST_GSTR3B()-gstr3b_VS_gstr2a.getEligible_CGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffEligible_SGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getEligible_SGST_GSTR3B()-gstr3b_VS_gstr2a.getEligible_SGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffIneligible_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getIneligible_GSTR3B()-gstr3b_VS_gstr2a.getIneligible_GSTR2A());
		gstr3b_VS_gstr2a.setDiffIneligible_IGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getIneligible_IGST_GSTR3B()-gstr3b_VS_gstr2a.getIneligible_IGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffIneligible_CGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getIneligible_CGST_GSTR3B()-gstr3b_VS_gstr2a.getIneligible_CGST_GSTR2A());
		gstr3b_VS_gstr2a.setDiffIneligible_SGST_GSTR3B_GSTR2A(gstr3b_VS_gstr2a.getIneligible_SGST_GSTR3B()-gstr3b_VS_gstr2a.getIneligible_SGST_GSTR2A());
		
		logger.debug(CLASSNAME + method + "END");
		
		return gstr3b_VS_gstr2a;
	}
	
	
	/*
	 * yearly comparison means 12Months	
	 */
	public List<GSTR3B_VS_GSTR1> getYearlyQuartelyComparision_GSTR3B_VS_GSTR1(String clientid,String userid,int year) throws Exception {
		final String method="getYearlyComparision_GSTR3B_VS_GSTR1";
		logger.debug(CLASSNAME + method + BEGIN);
		List<GSTR3B_VS_GSTR1> gstr3b_vs_gstr1=new ArrayList<GSTR3B_VS_GSTR1>();
		int[] monthArr= {1,4,7,10};
		for(int month:monthArr) {
			if(month == 1) {
				int yr=year+1;
				GSTR3B_VS_GSTR1 data=comparision_GSTR3B_VS_GSTR1Quartly(clientid, userid, month, yr);
				if(isNotEmpty(data)) {
					gstr3b_vs_gstr1.add(data);
				}
			}else {
				GSTR3B_VS_GSTR1 data=comparision_GSTR3B_VS_GSTR1Quartly(clientid, userid, month, year);
				if(isNotEmpty(data)) {
					gstr3b_vs_gstr1.add(data);
				}
			}
		}
			
		logger.debug(CLASSNAME + method + "END");
		return gstr3b_vs_gstr1;
	}
	
	
	/*
	 * sales quartely 
	 */
	
	public GSTR3B_VS_GSTR1 comparision_GSTR3B_VS_GSTR1Quartly(String clientid, String userid,int month,int year) {
		Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);

		final String method="comparision_GSTR3B_VS_GSTR1";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client = clientService.findById(clientid);
		
		
		List<String> gstr3bReturnPeriod=new ArrayList<String>();
		
		int startMnth=month;
		int endMonth=month+2;
		
		
		for(int i=startMnth;i<=endMonth;i++) {
			String strMonth = startMnth < 10 ? "0" + startMnth : startMnth + "";
			String returnPeriod = strMonth + year;
			startMnth++;
			gstr3bReturnPeriod.add(returnPeriod);
		}
		
		GSTR3B_VS_GSTR1 gstr3b_gstr1 = new GSTR3B_VS_GSTR1();
		gstr3b_gstr1.setMonth(monthArray[month]+"-"+monthArray[(month+2)]+" "+year);
		gstr3b_gstr1.setGSTR3B_3a_Taxableamt(0d);
		gstr3b_gstr1.setGSTR3B_3a_Taxamt(0d);
		gstr3b_gstr1.setGSTR3B_3b_Taxableamt(0d);
		gstr3b_gstr1.setGSTR3B_3b_Taxamt(0d);
		gstr3b_gstr1.setGSTR3B_3c_Taxableamt(0d);
		gstr3b_gstr1.setGSTR3B_3c_Taxamt(0d);
		gstr3b_gstr1.setGSTR3B_3d_Taxableamt(0d);
		gstr3b_gstr1.setGSTR3B_3d_Taxamt(0d);
		gstr3b_gstr1.setGSTR3B_3e_Taxableamt(0d);
		gstr3b_gstr1.setGSTR3B_3e_Taxamt(0d);
		gstr3b_gstr1.setGSTR3B_Cess(0d);
		gstr3b_gstr1.setGSTR3B_IGST(0d);
		gstr3b_gstr1.setGSTR3B_CGST_SGST(0d);
		gstr3b_gstr1.setGSTR3B_Total_Taxableamt(0d);

		List<GSTR3B> gstr3bLst = clientService.getComaprisionReqportQuarterlySuppliesInvoice(clientid, gstr3bReturnPeriod);
		
		if(isNotEmpty(gstr3bLst)) {
			for(GSTR3B gstr3b:gstr3bLst) {
				
				if(isNotEmpty(gstr3b)) {
					/**
					 * 3.1(a) Taxable Amount Tax Amount -> igst cgst+sgst+cess
					 * supDetails.osupDet.txval
					 */
					if (isNotEmpty(gstr3b.getSupDetails().getOsupDet().getTxval())) {
						gstr3b_gstr1.setGSTR3B_3a_Taxableamt(gstr3b_gstr1.getGSTR3B_3a_Taxableamt() + gstr3b.getSupDetails().getOsupDet().getTxval());
					}
			
					if (isNotEmpty(gstr3b.getSupDetails().getOsupDet().getIamt())) {
						gstr3b_gstr1.setGSTR3B_3a_Taxamt(gstr3b_gstr1.getGSTR3B_3a_Taxamt() + gstr3b.getSupDetails().getOsupDet().getIamt());
						gstr3b_gstr1.setGSTR3B_IGST(gstr3b_gstr1.getGSTR3B_IGST()+gstr3b.getSupDetails().getOsupDet().getIamt());
					}
			
					if (isNotEmpty(gstr3b.getSupDetails().getOsupDet().getCamt())) {
						gstr3b_gstr1.setGSTR3B_3a_Taxamt(gstr3b_gstr1.getGSTR3B_3a_Taxamt() + gstr3b.getSupDetails().getOsupDet().getCamt());
						gstr3b_gstr1.setGSTR3B_CGST_SGST(gstr3b_gstr1.getGSTR3B_CGST_SGST() + gstr3b.getSupDetails().getOsupDet().getCamt());
					}
					if (isNotEmpty(gstr3b.getSupDetails().getOsupDet().getSamt())) {
						gstr3b_gstr1.setGSTR3B_3a_Taxamt(gstr3b_gstr1.getGSTR3B_3a_Taxamt() + gstr3b.getSupDetails().getOsupDet().getSamt());
						gstr3b_gstr1.setGSTR3B_CGST_SGST(gstr3b_gstr1.getGSTR3B_CGST_SGST() + gstr3b.getSupDetails().getOsupDet().getSamt());
					}
					if (isNotEmpty(gstr3b.getSupDetails().getOsupDet().getCsamt())) {
						gstr3b_gstr1.setGSTR3B_Cess(gstr3b_gstr1.getGSTR3B_Cess() + gstr3b.getSupDetails().getOsupDet().getCsamt());
					}
					/**
					 * 3.2(b) Taxable Amount Tax Amount -> igst supDetails.osupZero.txval
					 */
					if (isNotEmpty(gstr3b.getSupDetails().getOsupZero().getTxval())) {
						gstr3b_gstr1.setGSTR3B_3b_Taxableamt(gstr3b_gstr1.getGSTR3B_3b_Taxableamt() + gstr3b.getSupDetails().getOsupZero().getTxval());
					}
					if (isNotEmpty(gstr3b.getSupDetails().getOsupZero().getIamt())) {
						gstr3b_gstr1.setGSTR3B_3b_Taxamt(gstr3b_gstr1.getGSTR3B_3b_Taxamt() + gstr3b.getSupDetails().getOsupZero().getIamt());
						gstr3b_gstr1.setGSTR3B_IGST(gstr3b_gstr1.getGSTR3B_IGST()+gstr3b.getSupDetails().getOsupDet().getIamt());
					}
					if (isNotEmpty(gstr3b.getSupDetails().getOsupZero().getCsamt())) {
						gstr3b_gstr1.setGSTR3B_Cess(gstr3b_gstr1.getGSTR3B_Cess() + gstr3b.getSupDetails().getOsupZero().getCsamt());
					}
			
					/**
					 * 3.2(c) Taxable Amount Tax Amount not available.
					 */
					if (isNotEmpty(gstr3b.getSupDetails().getOsupNilExmp().getTxval())) {
						gstr3b_gstr1.setGSTR3B_3c_Taxableamt(gstr3b_gstr1.getGSTR3B_3c_Taxableamt() + gstr3b.getSupDetails().getOsupNilExmp().getTxval());
					}
					
					/**
					 * 3.2(e) Taxable Amount Tax Amount igst+cgst_sgst+cess
					 */
					if (isNotEmpty(gstr3b.getSupDetails().getOsupNongst().getTxval())) {
						gstr3b_gstr1.setGSTR3B_3e_Taxableamt(gstr3b_gstr1.getGSTR3B_3e_Taxableamt() + gstr3b.getSupDetails().getOsupNongst().getTxval());
					}
				}
			}
		}
		
		/**
		 * To set Total Taxamount (3a+3b+3d) To set Total Taxableamount (3a+3b+3c+3d+3e)
		 * To CGST_SGST-> 3a+3d
		 */
		//System.out.println(gstr3b_gstr1.getGSTR3B_IGST());
		gstr3b_gstr1.setGSTR3B_IGST(gstr3b_gstr1.getGSTR3B_3a_Taxamt() + gstr3b_gstr1.getGSTR3B_3b_Taxamt() + 
				 gstr3b_gstr1.getGSTR3B_3d_Taxamt() - gstr3b_gstr1.getGSTR3B_CGST_SGST());
		
		//gstr3b_gstr1.setGSTR3B_IGST(gstr3b_gstr1.getGSTR3B_IGST());
		gstr3b_gstr1.setGSTR3B_Total_Taxableamt(gstr3b_gstr1.getGSTR3B_3a_Taxableamt()
				+ gstr3b_gstr1.getGSTR3B_3b_Taxableamt() + gstr3b_gstr1.getGSTR3B_3c_Taxableamt()
				+ gstr3b_gstr1.getGSTR3B_3e_Taxableamt());

		gstr3b_gstr1.setGSTR3B_Total_Taxableamt1(gstr3b_gstr1.getGSTR3B_3a_Taxableamt() + gstr3b_gstr1.getGSTR3B_3b_Taxableamt()
						+ gstr3b_gstr1.getGSTR3B_3c_Taxableamt() + gstr3b_gstr1.getGSTR3B_3e_Taxableamt());
		gstr3b_gstr1.setGSTR3B_Total_Taxamt2(gstr3b_gstr1.getGSTR3B_IGST() + gstr3b_gstr1.getGSTR3B_CGST_SGST());

		//clientid will be client
		client.setFilingOption("Quarterly");
		Page<? extends InvoiceParent> gstr1List = clientService.getComaprisionReportQuaterlyInvoices(null,client,userid,MasterGSTConstants.GSTR1,"reports",month,year);
				//clientService.getInvoiceBasedOnClientidAndFP(null, clientid, MasterGSTConstants.GSTR1, returnPeriod);
				//clientService.getInvoices(null, clientid, MasterGSTConstants.GSTR1, month, year);
		/**
		 * B2B/B2BC(L) INVOICE_TYPE CBW --> Sale from Bonded Warehouse
		 * 
		 * set default values in gstr3b_gstr1 variables
		 */
		gstr3b_gstr1.setB2b_R_CBW_Taxableamount(0d);
		gstr3b_gstr1.setB2b_R_CBW_Taxamount(0d);
		gstr3b_gstr1.setB2cl_R_CBW_Taxableamount(0d);
		gstr3b_gstr1.setB2cl_R_CBW_Taxamount(0d);
		gstr3b_gstr1.setB2b_CL_DE_Taxableamount(0d);
		gstr3b_gstr1.setB2b_CL_DE_Taxamount(0d);
		gstr3b_gstr1.setB2b_CL_SEWP_Taxableamount(0d);
		gstr3b_gstr1.setB2b_CL_SEWP_Taxamount(0d);
		gstr3b_gstr1.setB2b_CL_SEWOP_Taxableamount(0d);
		gstr3b_gstr1.setB2b_CL_SEWOP_Taxamount(0d);

		gstr3b_gstr1.setExports_WP_Taxableamount(0d);
		gstr3b_gstr1.setExports_WP_Taxamount(0d);
		gstr3b_gstr1.setExports_WOP_Taxableamount(0d);
		gstr3b_gstr1.setExports_WOP_Taxamount(0d);

		gstr3b_gstr1.setNilrated_Exempted_Taxableamount(0d);
		gstr3b_gstr1.setNilrated_Exempted_Taxamount(0d);
		gstr3b_gstr1.setNongst_Taxableamount(0d);
		gstr3b_gstr1.setNongst_Taxamount(0d);

		gstr3b_gstr1.setRcm_Taxableamount(0d);
		gstr3b_gstr1.setRcm_Taxamount(0d);
		gstr3b_gstr1.setBook_inter_Taxableamount(0d);
		gstr3b_gstr1.setBook_intra_Taxableamount(0d);
		gstr3b_gstr1.setBook_inter_Taxamount(0d);
		gstr3b_gstr1.setBook_intra_Taxamount(0d);

		/**
		 * GSTR1 Default values
		 */
		
		gstr3b_gstr1.setGstr1b2b_R_CBW_Taxableamount(0d);
		gstr3b_gstr1.setGstr1b2b_R_CBW_Taxamount(0d);
		gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxableamount(0d);
		gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxamount(0d);
		gstr3b_gstr1.setGstr1b2b_CL_DE_Taxableamount(0d);
		gstr3b_gstr1.setGstr1b2b_CL_DE_Taxamount(0d);
		gstr3b_gstr1.setGstr1b2b_CL_SEWP_Taxableamount(0d);
		gstr3b_gstr1.setGstr1b2b_CL_SEWP_Taxamount(0d);
		gstr3b_gstr1.setGstr1b2b_CL_SEWOP_Taxableamount(0d);
		gstr3b_gstr1.setGstr1b2b_CL_SEWOP_Taxamount(0d);

		gstr3b_gstr1.setGstr1exports_WP_Taxableamount(0d);
		gstr3b_gstr1.setGstr1exports_WP_Taxamount(0d);
		gstr3b_gstr1.setGstr1exports_WOP_Taxableamount(0d);
		gstr3b_gstr1.setGstr1exports_WOP_Taxamount(0d);

		gstr3b_gstr1.setGstr1nilrated_Exempted_Taxableamount(0d);
		gstr3b_gstr1.setGstr1nilrated_Exempted_Taxamount(0d);
		gstr3b_gstr1.setGstr1nongst_Taxableamount(0d);
		gstr3b_gstr1.setGstr1nongst_Taxamount(0d);

		gstr3b_gstr1.setGstr1rcm_Taxableamount(0d);
		gstr3b_gstr1.setGstr1rcm_Taxamount(0d);
		gstr3b_gstr1.setGstr1_inter_Taxableamount(0d);
		gstr3b_gstr1.setGstr1_intra_Taxableamount(0d);
		gstr3b_gstr1.setGstr1_inter_Taxamount(0d);
		gstr3b_gstr1.setGstr1_intra_Taxamount(0d);
		
		List<String> invTypes = new ArrayList<String>();
		invTypes.add(MasterGSTConstants.B2BA);
		invTypes.add(MasterGSTConstants.B2CSA);
		invTypes.add(MasterGSTConstants.B2CLA);
		invTypes.add(MasterGSTConstants.CDNA);
		invTypes.add(MasterGSTConstants.CDNURA);
		invTypes.add(MasterGSTConstants.EXPA);
		List<String> amendmentRefIds = Lists.newArrayList();
		if(isNotEmpty(gstr1List)) {
			gstr1List.forEach(gstr -> {
				if(isNotEmpty(gstr.getInvtype())){
					if(invTypes.contains(gstr.getInvtype()) && isNotEmpty(gstr.getAmendmentRefId())) {
						if(isNotEmpty(gstr.getAmendmentRefId()) && gstr.getAmendmentRefId().size() > 0) {
							amendmentRefIds.addAll(gstr.getAmendmentRefId());
						}
					}
				}
			});
		}
		
		if(isNotEmpty(gstr1List)) {
			gstr1List.forEach(gstr -> {
				// System.out.println(" -->"+ gstr.getB2b().get(0).getInv().get(0).getInvTyp());
				if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype())) && (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("R")
								|| gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("CBW")))) {
	
					//orginal_invno.put(gstr.getB2b().get(0).getInv().get(0).getInum(), "B2B_R_CBW");
					if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
						if(isNotEmpty(gstr.getTotaltaxableamount())) {
							gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
						}
						if(isNotEmpty(gstr.getTotaltax())) {
							gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
						}
					}else {
						if(isNotEmpty(gstr.getTotaltaxableamount())) {
							gstr3b_gstr1.setB2b_R_CBW_Taxableamount(gstr3b_gstr1.getB2b_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
						}
						if(isNotEmpty(gstr.getTotaltax())) {
							gstr3b_gstr1.setB2b_R_CBW_Taxamount(gstr3b_gstr1.getB2b_R_CBW_Taxamount() + gstr.getTotaltax());
						}
					}
				} else if ((MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype()))
						&& (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("R") || gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("CBW")))) {
	
					//orginal_invno.put(gstr.getB2b().get(0).getInv().get(0).getInum(), "B2C_L_CBW");
					if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
						if(isNotEmpty(gstr.getTotaltaxableamount())) {
							gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
						}
						if(isNotEmpty(gstr.getTotaltax())) {
							gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
						}
					}else {
						if(isNotEmpty(gstr.getTotaltaxableamount())) {
							gstr3b_gstr1.setB2cl_R_CBW_Taxableamount(gstr3b_gstr1.getB2cl_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
						}
						if(isNotEmpty(gstr.getTotaltax())) {
							gstr3b_gstr1.setB2cl_R_CBW_Taxamount(gstr3b_gstr1.getB2cl_R_CBW_Taxamount() + gstr.getTotaltax());
						}
					}
				} else if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype())
						|| MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype())  || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype())) && (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("DE")))) {
	
					//orginal_invno.put(gstr.getB2b().get(0).getInv().get(0).getInum(), "DE");
					if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
						if(isNotEmpty(gstr.getTotaltaxableamount())) {
							gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
						}
						if(isNotEmpty(gstr.getTotaltax())) {
							gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
						}
					}else {
						if(isNotEmpty(gstr.getTotaltaxableamount())) {
							gstr3b_gstr1.setB2b_CL_DE_Taxableamount(gstr3b_gstr1.getB2b_CL_DE_Taxableamount() + gstr.getTotaltaxableamount());
						}
						if(isNotEmpty(gstr.getTotaltax())) {
							gstr3b_gstr1.setB2b_CL_DE_Taxamount(gstr3b_gstr1.getB2b_CL_DE_Taxamount() + gstr.getTotaltax());
						}
					}
				} else if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype())
						|| MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype())  || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype())  || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype())) && (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("SEWP")))) {
					//orginal_invno.put(gstr.getB2b().get(0).getInv().get(0).getInum(), "SEWP");
					if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
						if(isNotEmpty(gstr.getTotaltaxableamount())) {
							gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
						}
						if(isNotEmpty(gstr.getTotaltax())) {
							gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
						}
					}else {
						if(isNotEmpty(gstr.getTotaltaxableamount())) {
							gstr3b_gstr1.setB2b_CL_SEWP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxableamount() + gstr.getTotaltaxableamount());
						}
						if(isNotEmpty(gstr.getTotaltax())) {
							gstr3b_gstr1.setB2b_CL_SEWP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxamount() + gstr.getTotaltax());
						}
					}
				} else if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype())
						|| MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype())  || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype())  || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype()))	&& (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("SEWOP")))) {
	
					//orginal_invno.put(gstr.getB2b().get(0).getInv().get(0).getInum(), "SEWOP");
					if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
						if(isNotEmpty(gstr.getTotaltaxableamount())) {
							gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
						}
						if(isNotEmpty(gstr.getTotaltax())) {
							gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
						}
					}else {
						if(isNotEmpty(gstr.getTotaltaxableamount())) {
							gstr3b_gstr1.setB2b_CL_SEWOP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxableamount() + gstr.getTotaltaxableamount());
						}
						if(isNotEmpty(gstr.getTotaltax())) {
							gstr3b_gstr1.setB2b_CL_SEWOP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxamount() + gstr.getTotaltax());
						}
					}
				}
	
				if (MasterGSTConstants.EXPORTS.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.EXPA.equalsIgnoreCase(gstr.getInvtype())) {
					if(isNotEmpty(gstr.getExp()) && isNotEmpty(gstr.getExp().get(0).getExpTyp())) {
						if ("WPAY".equalsIgnoreCase(gstr.getExp().get(0).getExpTyp())) {
							//orginal_invno.put(gstr.getB2b().get(0).getInv().get(0).getInum(), "EXPORT_WPAY");
							if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())) {
									gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
								}
							}else{
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setExports_WP_Taxableamount(gstr3b_gstr1.getExports_WP_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())) {
									gstr3b_gstr1.setExports_WP_Taxamount(gstr3b_gstr1.getExports_WP_Taxamount() + gstr.getTotaltax());
								}
							}
						} else if ("WOPAY".equalsIgnoreCase(gstr.getExp().get(0).getExpTyp())) {
							//orginal_invno.put(gstr.getB2b().get(0).getInv().get(0).getInum(), "EXPORT_WOPAY");
							if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())) {
									gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
								}
							}else {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setExports_WOP_Taxableamount(gstr3b_gstr1.getExports_WOP_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())) {
									gstr3b_gstr1.setExports_WOP_Taxamount(gstr3b_gstr1.getExports_WOP_Taxamount() + gstr.getTotaltax());
								}
							}
						}
					}
				}
	
				if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNUR.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNURA.equalsIgnoreCase(gstr.getInvtype())) {
	
					String org_invno = "";
					String docType = "";
					if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)) {
						if (isNotEmpty(((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty())) {
							docType = ((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty();
							org_invno =((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getInum(); 
									//orginal_invno.get(((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getInum());
						}
					} else if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)) {
						if (isNotEmpty(((GSTR1) gstr).getCdnur().get(0).getNtty())) {
							docType = gstr.getCdnur().get(0).getNtty();
							org_invno = gstr.getCdnur().get(0).getInum(); 
									//orginal_invno.get(gstr.getCdnur().get(0).getInum());
						}
					}
					
					Date stDate = null;
					Date endDate = null;
					Calendar cal = Calendar.getInstance();
					if(month < 10) {
						cal.set(year-1, 3, 1, 0, 0, 0);
						stDate = new java.util.Date(cal.getTimeInMillis());
						cal = Calendar.getInstance();
						cal.set(year, month+2, 0, 23, 59, 59);
						endDate = new java.util.Date(cal.getTimeInMillis());
					}else {
						cal.set(year, 3, 1, 0, 0, 0);
						stDate = new java.util.Date(cal.getTimeInMillis());
						cal = Calendar.getInstance();
						cal.set(year, month+2, 0, 23, 59, 59);
						endDate = new java.util.Date(cal.getTimeInMillis());
					}
					List<GSTR1> gstr1list = gstr1Repository.findByInvoicenoAndClientidAndDateofinvoiceBetween(org_invno, client.getId().toString(), stDate, endDate);
					
					if (docType.equalsIgnoreCase("C")) {
						boolean flag = false;
						if(isNotEmpty(gstr1list)) {
							for(InvoiceParent gstrr1 : gstr1list) {
								if(isNotEmpty(gstrr1) && isNotEmpty(gstrr1.getInvoiceno())) {
									if(org_invno.equals(gstrr1.getInvoiceno())) {
										flag = true;
										if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B) || gstrr1.getInvtype().equals(MasterGSTConstants.B2C) || gstrr1.getInvtype().equals(MasterGSTConstants.B2CL)) {
											String creverseChargeType = "";
											if(isNotEmpty(gstrr1.getRevchargetype())) {
												creverseChargeType = gstrr1.getRevchargetype();
											}else {
												creverseChargeType = "Regular";
											}
											if("Regular".equals(creverseChargeType)) {
												String invType = "";
												if(isNotEmpty(gstrr1.getB2b()) && isNotEmpty(gstrr1.getB2b().get(0).getInv())	&& isNotEmpty(gstrr1.getB2b().get(0).getInv().get(0).getInvTyp())) {
													invType = gstrr1.getB2b().get(0).getInv().get(0).getInvTyp();
												}else {
													invType = "R";
												}
												if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B)){
													if("R".equals(invType) || "CBW".equals(invType)) {
														if(isNotEmpty(gstr.getTotaltaxableamount())) {
															gstr3b_gstr1.setB2b_R_CBW_Taxableamount(gstr3b_gstr1.getB2b_R_CBW_Taxableamount() - gstr.getTotaltaxableamount());
														}
														if(isNotEmpty(gstr.getTotaltax())) {
															gstr3b_gstr1.setB2b_R_CBW_Taxamount(gstr3b_gstr1.getB2b_R_CBW_Taxamount() - gstr.getTotaltax());
														}
													}											
												}else {
													if("R".equals(invType) || "CBW".equals(invType)) {
														if(isNotEmpty(gstr.getTotaltaxableamount())) {
															gstr3b_gstr1.setB2cl_R_CBW_Taxableamount(gstr3b_gstr1.getB2cl_R_CBW_Taxableamount() - gstr.getTotaltaxableamount());
														}
														if(isNotEmpty(gstr.getTotaltax())) {
															gstr3b_gstr1.setB2cl_R_CBW_Taxamount(gstr3b_gstr1.getB2cl_R_CBW_Taxamount() - gstr.getTotaltax());
														}
													}
												}
												if("DE".equals(invType)) {
													if(isNotEmpty(gstr.getTotaltaxableamount())) {
														gstr3b_gstr1.setB2b_CL_DE_Taxableamount(gstr3b_gstr1.getB2b_CL_DE_Taxableamount() - gstr.getTotaltaxableamount());
													}
													if(isNotEmpty(gstr.getTotaltax())) {
														gstr3b_gstr1.setB2b_CL_DE_Taxamount(gstr3b_gstr1.getB2b_CL_DE_Taxamount() - gstr.getTotaltax());
													}
												}else if("SEWP".equals(invType) || "SEWPC".equals(invType)) {
													if(isNotEmpty(gstr.getTotaltaxableamount())) {
														gstr3b_gstr1.setB2b_CL_SEWP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxableamount() - gstr.getTotaltaxableamount());
													}
													if(isNotEmpty(gstr.getTotaltax())) {
														gstr3b_gstr1.setB2b_CL_SEWP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxamount() - gstr.getTotaltax());
													}
												}else if("SEWOP".equals(invType)) {
													if(isNotEmpty(gstr.getTotaltaxableamount())) {
														gstr3b_gstr1.setB2b_CL_SEWOP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxableamount() - gstr.getTotaltaxableamount());
													}
													if(isNotEmpty(gstr.getTotaltax())) {
														gstr3b_gstr1.setB2b_CL_SEWOP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxamount() - gstr.getTotaltax());
													}
												}else if("WPAY".equals(invType)) {
													if(isNotEmpty(gstr.getTotaltaxableamount())) {
														gstr3b_gstr1.setExports_WP_Taxableamount(gstr3b_gstr1.getExports_WP_Taxableamount() - gstr.getTotaltaxableamount());
													}
													if(isNotEmpty(gstr.getTotaltax())) {
														gstr3b_gstr1.setExports_WP_Taxamount(gstr3b_gstr1.getExports_WP_Taxamount() - gstr.getTotaltax());
													}
												}else if("WOPAY".equals(invType)) {
													if(isNotEmpty(gstr.getTotaltaxableamount())) {
														gstr3b_gstr1.setExports_WOP_Taxableamount(gstr.getTotaltaxableamount());
													}
													if(isNotEmpty(gstr.getTotaltax())) {
														gstr3b_gstr1.setExports_WOP_Taxamount(gstr3b_gstr1.getExports_WP_Taxamount() - gstr.getTotaltax());
													}
												}
											}else {
												if(isNotEmpty(gstr.getTotaltaxableamount())) {
													gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() - gstr.getTotaltaxableamount());
												}
												if(isNotEmpty(gstr.getTotaltax())) {
													gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() - gstr.getTotaltax());
												}
											}
										}
									}							
								}
							}	
						}
						if(!flag) {
							if(isNotEmpty(gstr.getTotaltaxableamount())) {
								gstr3b_gstr1.setB2b_R_CBW_Taxableamount(gstr3b_gstr1.getB2b_R_CBW_Taxableamount() - gstr.getTotaltaxableamount());
							}
							if(isNotEmpty(gstr.getTotaltax())) {
								gstr3b_gstr1.setB2b_R_CBW_Taxamount(gstr3b_gstr1.getB2b_R_CBW_Taxamount() - gstr.getTotaltax());
							}
						}
					} else if (docType.equalsIgnoreCase("D")) {
						boolean flag = false;
						if(isNotEmpty(gstr1list)) {
							for(InvoiceParent gstrr1 : gstr1list) {
								if(isNotEmpty(gstrr1) && isNotEmpty(gstrr1.getInvoiceno())) {
									if(org_invno.equals(gstrr1.getInvoiceno())) {
										flag = true;
										if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B) || gstrr1.getInvtype().equals(MasterGSTConstants.B2C) || gstrr1.getInvtype().equals(MasterGSTConstants.B2CL)) {
											String creverseChargeType = "";
											if(isNotEmpty(gstrr1.getRevchargetype())) {
												creverseChargeType = gstrr1.getRevchargetype();
											}else {
												creverseChargeType = "Regular";
											}
											if("Regular".equals(creverseChargeType)) {
												String invType = "";
												if(isNotEmpty(gstrr1.getB2b()) && isNotEmpty(gstrr1.getB2b().get(0).getInv()) && isNotEmpty(gstrr1.getB2b().get(0).getInv().get(0).getInvTyp())) {
													invType = gstrr1.getB2b().get(0).getInv().get(0).getInvTyp();
												}else {
													invType = "R";
												}
												if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B)){
													if("R".equals(invType) || "CBW".equals(invType)) {
														if(isNotEmpty(gstr.getTotaltaxableamount())) {
															gstr3b_gstr1.setB2b_R_CBW_Taxableamount(gstr3b_gstr1.getB2b_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
														}
														if(isNotEmpty(gstr.getTotaltax())) {
															gstr3b_gstr1.setB2b_R_CBW_Taxamount(gstr3b_gstr1.getB2b_R_CBW_Taxamount() + gstr.getTotaltax());
														}
													}											
												}else {
													if("R".equals(invType) || "CBW".equals(invType)) {
														if(isNotEmpty(gstr.getTotaltaxableamount())) {
															gstr3b_gstr1.setB2cl_R_CBW_Taxableamount(gstr3b_gstr1.getB2cl_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
														}
														if(isNotEmpty(gstr.getTotaltax())) {
															gstr3b_gstr1.setB2cl_R_CBW_Taxamount(gstr3b_gstr1.getB2cl_R_CBW_Taxamount() + gstr.getTotaltax());
														}
													}
												}
												if("DE".equals(invType)) {
													if(isNotEmpty(gstr.getTotaltaxableamount())) {
														gstr3b_gstr1.setB2b_CL_DE_Taxableamount(gstr3b_gstr1.getB2b_CL_DE_Taxableamount() + gstr.getTotaltaxableamount());
													}
													if(isNotEmpty(gstr.getTotaltax())) {
														gstr3b_gstr1.setB2b_CL_DE_Taxamount(gstr3b_gstr1.getB2b_CL_DE_Taxamount() + gstr.getTotaltax());
													}
												}else if("SEWP".equals(invType) || "SEWPC".equals(invType)) {
													if(isNotEmpty(gstr.getTotaltaxableamount())) {
														gstr3b_gstr1.setB2b_CL_SEWP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxableamount() + gstr.getTotaltaxableamount());
													}
													if(isNotEmpty(gstr.getTotaltax())) {
														gstr3b_gstr1.setB2b_CL_SEWP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxamount() + gstr.getTotaltax());
													}
												}else if("SEWOP".equals(invType)) {
													if(isNotEmpty(gstr.getTotaltaxableamount())) {
														gstr3b_gstr1.setB2b_CL_SEWOP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxableamount() + gstr.getTotaltaxableamount());
													}
													if(isNotEmpty(gstr.getTotaltax())) {
														gstr3b_gstr1.setB2b_CL_SEWOP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxamount() + gstr.getTotaltax());
													}
												}else if("WPAY".equals(invType)) {
													if(isNotEmpty(gstr.getTotaltaxableamount())) {
														gstr3b_gstr1.setExports_WP_Taxableamount(gstr3b_gstr1.getExports_WP_Taxableamount() + gstr.getTotaltaxableamount());
													}
													if(isNotEmpty(gstr.getTotaltax())) {
														gstr3b_gstr1.setExports_WP_Taxamount(gstr3b_gstr1.getExports_WP_Taxamount() + gstr.getTotaltax());
													}
												}else if("WOPAY".equals(invType)) {
													if(isNotEmpty(gstr.getTotaltaxableamount())) {
														gstr3b_gstr1.setExports_WOP_Taxableamount(gstr.getTotaltaxableamount());
													}
													if(isNotEmpty(gstr.getTotaltax())) {
														gstr3b_gstr1.setExports_WOP_Taxamount(gstr3b_gstr1.getExports_WP_Taxamount() + gstr.getTotaltax());
													}
												}
											}else {
												if(isNotEmpty(gstr.getTotaltaxableamount())) {
													gstr3b_gstr1.setRcm_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount() + gstr.getTotaltaxableamount());
												}
												if(isNotEmpty(gstr.getTotaltax())) {
													gstr3b_gstr1.setRcm_Taxamount(gstr3b_gstr1.getRcm_Taxamount() + gstr.getTotaltax());
												}
											}
										}
									}
								}
							}	
						}
						if(!flag) {
							if(isNotEmpty(gstr.getTotaltaxableamount())) {
								gstr3b_gstr1.setB2b_R_CBW_Taxableamount(gstr3b_gstr1.getB2b_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
							}
							if(isNotEmpty(gstr.getTotaltax())) {
								gstr3b_gstr1.setB2b_R_CBW_Taxamount(gstr3b_gstr1.getB2b_R_CBW_Taxamount() + gstr.getTotaltax());
							}
						}
					}
				}
				if (MasterGSTConstants.NIL.equalsIgnoreCase(gstr.getInvtype())) {
					gstr.getItems().forEach(item -> {
						if ("Exempted".equalsIgnoreCase(item.getType()) || "Nil Rated".equalsIgnoreCase(item.getType())) {
							if(isNotEmpty(item.getTaxablevalue())) {
								gstr3b_gstr1.setNilrated_Exempted_Taxableamount(gstr3b_gstr1.getNilrated_Exempted_Taxableamount() + item.getTaxablevalue());
							}//System.out.println(item.getTaxablevalue());
							gstr3b_gstr1.setNilrated_Exempted_Taxamount(0d);
						} else if ("Non-GST".equalsIgnoreCase(item.getType())) {
							gstr3b_gstr1.setNongst_Taxableamount(gstr3b_gstr1.getNongst_Taxableamount() + item.getTaxablevalue());
							gstr3b_gstr1.setNongst_Taxamount(0d);
						}
					});
				}
				if (isNotEmpty(client.getStatename())) {
					Double taxableamount=0d, taxamount=0d;
					int multiply = 1;
					
					if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNUR.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNURA.equalsIgnoreCase(gstr.getInvtype())) {
						
						String docType = "";
						if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)) {
							if (isNotEmpty(((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty())) {
								docType = ((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty();
							}
						} else if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)) {
							if (isNotEmpty(((GSTR1) gstr).getCdnur().get(0).getNtty())) {
								docType = gstr.getCdnur().get(0).getNtty();
							}
						}
						if(docType.equalsIgnoreCase("C")) {
							 multiply = -1;
						}else if (docType.equalsIgnoreCase("D")) {
							 multiply = 1;
						}
					}
					
					if(isNotEmpty(gstr.getTotaltaxableamount())) {
						taxableamount += gstr.getTotaltaxableamount() * multiply;
					}
					if(isNotEmpty(gstr.getTotaltax())) {
						taxamount += gstr.getTotaltax() * multiply;
					}
					if (MasterGSTConstants.NIL.equalsIgnoreCase(gstr.getInvtype())) {
						if(isNotEmpty(gstr.getNil()) && isNotEmpty(gstr.getNil().getInv())) {
							for(GSTRNilItems nil : gstr.getNil().getInv()) {
								Double niltaxbleamt = 0d;
								if(isNotEmpty(nil.getExptAmt())) {
									niltaxbleamt += nil.getExptAmt();
								}
								if(isNotEmpty(nil.getNgsupAmt())) {
									niltaxbleamt += nil.getNgsupAmt();
								}
								if(isNotEmpty(nil.getNilAmt())) {
									niltaxbleamt += nil.getNilAmt();
								}
								if(isNotEmpty(nil.getSplyType()) && ("INTRAB2B".equalsIgnoreCase(nil.getSplyType()) || "INTRAB2C".equalsIgnoreCase(nil.getSplyType()))) {
										gstr3b_gstr1.setBook_intra_Taxableamount(gstr3b_gstr1.getBook_intra_Taxableamount() + niltaxbleamt);
								}else {
										gstr3b_gstr1.setBook_inter_Taxableamount(gstr3b_gstr1.getBook_inter_Taxableamount() + niltaxbleamt);
								}
							}
						}
					}else {
						String clntstatename = client.getStatename();
						String invstatename = "";
						if(isNotEmpty(gstr.getStatename())) {
							invstatename = gstr.getStatename();
						}else {
							gstr.setStatename("97-Other Territory");
							invstatename = "97-Other Territory";
						}
						if(clntstatename.contains("-")) {
							clntstatename = clntstatename.substring(3);
						}
						if(invstatename.contains("-")) {
							invstatename = invstatename.substring(3);
						}
						if (client.getStatename().equalsIgnoreCase(gstr.getStatename()) || clntstatename.equalsIgnoreCase(invstatename) || client.getStatename().substring(0, 2).equalsIgnoreCase(gstr.getStatename().substring(0, 2))) {
							if(isNotEmpty(gstr.getTotaltaxableamount())) {
								gstr3b_gstr1.setBook_intra_Taxableamount(gstr3b_gstr1.getBook_intra_Taxableamount() + taxableamount);
							}
							if(isNotEmpty(gstr.getTotaltax())) {
								gstr3b_gstr1.setBook_intra_Taxamount(gstr3b_gstr1.getBook_intra_Taxamount() + taxamount);
							}
						} else {
							if(isNotEmpty(gstr.getTotaltaxableamount())) {
							gstr3b_gstr1.setBook_inter_Taxableamount(gstr3b_gstr1.getBook_inter_Taxableamount() + taxableamount);
							}
							if(isNotEmpty(gstr.getTotaltax())) {
								gstr3b_gstr1.setBook_inter_Taxamount(gstr3b_gstr1.getBook_inter_Taxamount() + taxamount);
							}
						}
					}
				}
			});
		}
		gstr3b_gstr1.setBook_Total_Taxableamt1(
				gstr3b_gstr1.getB2b_R_CBW_Taxableamount()+
				gstr3b_gstr1.getB2cl_R_CBW_Taxableamount()+
				gstr3b_gstr1.getB2b_CL_DE_Taxableamount()+
				gstr3b_gstr1.getRcm_Taxableamount()+
				gstr3b_gstr1.getB2b_CL_SEWP_Taxableamount()+
				gstr3b_gstr1.getB2b_CL_SEWOP_Taxableamount()+
				gstr3b_gstr1.getExports_WP_Taxableamount()+
				gstr3b_gstr1.getExports_WOP_Taxableamount()+
				gstr3b_gstr1.getNilrated_Exempted_Taxableamount()+
				gstr3b_gstr1.getNongst_Taxableamount());
		gstr3b_gstr1.setBook_Total_Taxamt1(gstr3b_gstr1.getB2b_R_CBW_Taxamount()+
				gstr3b_gstr1.getB2cl_R_CBW_Taxamount()+
				gstr3b_gstr1.getB2b_CL_DE_Taxamount()+
				gstr3b_gstr1.getRcm_Taxamount()+
				gstr3b_gstr1.getB2b_CL_SEWP_Taxamount()+
				gstr3b_gstr1.getB2b_CL_SEWOP_Taxamount()+
				gstr3b_gstr1.getExports_WP_Taxamount()+
				gstr3b_gstr1.getExports_WOP_Taxamount()+
				gstr3b_gstr1.getNilrated_Exempted_Taxamount()+
				gstr3b_gstr1.getNongst_Taxamount());

		gstr3b_gstr1.setBook_Total_Taxableamt2(gstr3b_gstr1.getBook_intra_Taxableamount() + gstr3b_gstr1.getBook_inter_Taxableamount());
		gstr3b_gstr1.setBook_Total_Taxamt2(gstr3b_gstr1.getBook_intra_Taxamount() + gstr3b_gstr1.getBook_inter_Taxamount());

		/**
		 * 
		 * Books-GSTR3B
		 */
		double book_diffTaxableamount1 = gstr3b_gstr1.getB2b_R_CBW_Taxableamount()
				+ gstr3b_gstr1.getB2cl_R_CBW_Taxableamount() + gstr3b_gstr1.getB2b_CL_DE_Taxableamount()
				+ gstr3b_gstr1.getRcm_Taxableamount();
		double book_diffTaxamount1 = gstr3b_gstr1.getB2b_R_CBW_Taxamount() + gstr3b_gstr1.getB2cl_R_CBW_Taxamount()
				+ gstr3b_gstr1.getB2b_CL_DE_Taxamount() + gstr3b_gstr1.getRcm_Taxamount();

		gstr3b_gstr1.setDiffBook_GSTR3B_Taxableamount_Total1(
				book_diffTaxableamount1 - gstr3b_gstr1.getGSTR3B_3a_Taxableamt());
		gstr3b_gstr1.setDiffBook_GSTR3B_Taxamount_Total1(book_diffTaxamount1 - gstr3b_gstr1.getGSTR3B_3a_Taxamt());

		double book_diffTaxableamount2 = gstr3b_gstr1.getB2b_CL_SEWP_Taxableamount()
				+ gstr3b_gstr1.getB2b_CL_SEWOP_Taxableamount() + gstr3b_gstr1.getExports_WP_Taxableamount()
				+ gstr3b_gstr1.getExports_WOP_Taxableamount();
		double book_diffTaxamount2 = gstr3b_gstr1.getB2b_CL_SEWP_Taxamount() + gstr3b_gstr1.getB2b_CL_SEWOP_Taxamount()
				+ gstr3b_gstr1.getExports_WP_Taxamount() + gstr3b_gstr1.getExports_WOP_Taxamount();

		gstr3b_gstr1.setDiffBook_GSTR3B_Taxableamount_Total2(
				book_diffTaxableamount2 - gstr3b_gstr1.getGSTR3B_3b_Taxableamt());
		gstr3b_gstr1.setDiffBook_GSTR3B_Taxamount_Total2(book_diffTaxamount2 - gstr3b_gstr1.getGSTR3B_3b_Taxamt());

		gstr3b_gstr1.setDiffBook_GSTR3B_Nilrated_Exempted_Taxableamount(gstr3b_gstr1.getNilrated_Exempted_Taxableamount() - gstr3b_gstr1.getGSTR3B_3c_Taxableamt());
		gstr3b_gstr1.setDiffBook_GSTR3B_Nilrated_Exempted_Taxamount(gstr3b_gstr1.getNilrated_Exempted_Taxamount() - gstr3b_gstr1.getGSTR3B_3c_Taxamt());

		gstr3b_gstr1.setDiffBook_GSTR3B_NonGST_Taxableamount(gstr3b_gstr1.getNongst_Taxableamount() - gstr3b_gstr1.getGSTR3B_3e_Taxableamt());
		gstr3b_gstr1.setDiffBook_GSTR3B_NonGST_Taxamount(gstr3b_gstr1.getNongst_Taxamount());

		gstr3b_gstr1.setGSTR3B_Total_Taxableamt1(gstr3b_gstr1.getGSTR3B_3a_Taxableamt() + gstr3b_gstr1.getGSTR3B_3b_Taxableamt()+ gstr3b_gstr1.getGSTR3B_3c_Taxableamt() + gstr3b_gstr1.getGSTR3B_3d_Taxableamt());

		//diffBook_GSTR3B_Total_Taxableamount1
		
		
		
		gstr3b_gstr1.setDiffBook_GSTR3B_Total_Taxableamount1(gstr3b_gstr1.getDiffBook_GSTR3B_Taxableamount_Total1()	+ gstr3b_gstr1.getDiffBook_GSTR3B_Taxableamount_Total2()+ gstr3b_gstr1.getDiffBook_GSTR3B_Nilrated_Exempted_Taxableamount()+ gstr3b_gstr1.getDiffBook_GSTR3B_NonGST_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR3B_Total_Taxamount1(gstr3b_gstr1.getDiffBook_GSTR3B_Taxamount_Total1() + gstr3b_gstr1.getDiffBook_GSTR3B_Taxamount_Total2()+ gstr3b_gstr1.getDiffBook_GSTR3B_Nilrated_Exempted_Taxamount()	+ gstr3b_gstr1.getDiffBook_GSTR3B_NonGST_Taxamount());

		gstr3b_gstr1.setDiffBook_GSTR3B_Inter_Taxableamount((gstr3b_gstr1.getBook_inter_Taxableamount() + gstr3b_gstr1.getBook_intra_Taxableamount())- gstr3b_gstr1.getGSTR3B_Total_Taxableamt());
		gstr3b_gstr1.setDiffBook_GSTR3B_Inter_Taxamount(gstr3b_gstr1.getBook_inter_Taxamount() - gstr3b_gstr1.getGSTR3B_IGST());
		gstr3b_gstr1.setDiffBook_GSTR3B_Intra_Taxamount(gstr3b_gstr1.getBook_intra_Taxamount() - gstr3b_gstr1.getGSTR3B_CGST_SGST());
		gstr3b_gstr1.setDiffBook_GSTR3B_Total_Taxableamount2(gstr3b_gstr1.getDiffBook_GSTR3B_Inter_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR3B_Total_Taxamount2(gstr3b_gstr1.getDiffBook_GSTR3B_Inter_Taxamount() + gstr3b_gstr1.getDiffBook_GSTR3B_Intra_Taxamount());

		/**
		 * GSTR1 Data isAmendment=true or GovtInvoiceStatus() == SUCCESS
		 */
		if(isNotEmpty(gstr1List)) {
			gstr1List.forEach(gstr -> {
				if(isNotEmpty(gstr.getGovtInvoiceStatus())) {
					if(!amendmentRefIds.contains(gstr.getId().toString())) {
					if(MasterGSTConstants.SUCCESS.equalsIgnoreCase(gstr.getGovtInvoiceStatus())) {
							
						if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype())) &&  (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("R") || gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("CBW")))) {
							if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())){
									gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() + gstr.getTotaltax());
								}
							}else {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setGstr1b2b_R_CBW_Taxableamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())){
									gstr3b_gstr1.setGstr1b2b_R_CBW_Taxamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxamount() + gstr.getTotaltax());
								}
							}
						}else if ((MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype()))
														&& (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("R") || gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("CBW")))) {
							if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())){
									gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() + gstr.getTotaltax());
								}
							}else {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxableamount(gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())){
									gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxamount(gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxamount() + gstr.getTotaltax());
								}
							}
						} else if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) 	|| MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype())
								|| MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype())) && (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("DE")))) {
							if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())){
									gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() + gstr.getTotaltax());
								}
							}else {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setGstr1b2b_CL_DE_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_DE_Taxableamount() + gstr.getTotaltaxableamount());									
								}
								if(isNotEmpty(gstr.getTotaltax())){
									gstr3b_gstr1.setGstr1b2b_CL_DE_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_DE_Taxamount() + gstr.getTotaltax());
								}
							}
						} else if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype())
								|| MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype())) && (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("SEWP")))) {
							if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())){
									gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() + gstr.getTotaltax());
								}
							}else {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setGstr1b2b_CL_SEWP_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())){
									gstr3b_gstr1.setGstr1b2b_CL_SEWP_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxamount() + gstr.getTotaltax());
								}
							}
						} else if ((MasterGSTConstants.B2B.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2C.equalsIgnoreCase(gstr.getInvtype())
									|| MasterGSTConstants.B2CL.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CSA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2CLA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.B2BA.equalsIgnoreCase(gstr.getInvtype()))	&& (isNotEmpty(gstr.getB2b()) && isNotEmpty(gstr.getB2b().get(0).getInv()) && isNotEmpty(gstr.getB2b().get(0).getInv().get(0).getInvTyp()) && (gstr.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("SEWOP")))) {
			
							if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())){
									gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() + gstr.getTotaltax());
								}
							}else {
								if(isNotEmpty(gstr.getTotaltaxableamount())) {
									gstr3b_gstr1.setGstr1b2b_CL_SEWOP_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxableamount() + gstr.getTotaltaxableamount());
								}
								if(isNotEmpty(gstr.getTotaltax())){
									gstr3b_gstr1.setGstr1b2b_CL_SEWOP_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxamount() + gstr.getTotaltax());
								}
							}
						}
						if (MasterGSTConstants.EXPORTS.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.EXPA.equalsIgnoreCase(gstr.getInvtype())) {
							if(isNotEmpty(gstr.getExp()) && isNotEmpty(gstr.getExp().get(0).getExpTyp())) {
								if ("WPAY".equalsIgnoreCase(gstr.getExp().get(0).getExpTyp())) {
									if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
										if(isNotEmpty(gstr.getTotaltaxableamount())) {
											gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() + gstr.getTotaltaxableamount());
										}
										if(isNotEmpty(gstr.getTotaltax())){
											gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() + gstr.getTotaltax());
										}
									}else {
										if(isNotEmpty(gstr.getTotaltaxableamount())) {
											gstr3b_gstr1.setGstr1exports_WP_Taxableamount(gstr3b_gstr1.getGstr1exports_WP_Taxableamount() + gstr.getTotaltaxableamount());
										}
										if(isNotEmpty(gstr.getTotaltax())){
											gstr3b_gstr1.setGstr1exports_WP_Taxamount(gstr3b_gstr1.getGstr1exports_WP_Taxamount() + gstr.getTotaltax());
										}
									}
								} else if ("WOPAY".equalsIgnoreCase(gstr.getExp().get(0).getExpTyp())) {
									if ("Reverse".equalsIgnoreCase(gstr.getRevchargetype())) {
										if(isNotEmpty(gstr.getTotaltaxableamount())) {
											gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() + gstr.getTotaltaxableamount());
										}
										if(isNotEmpty(gstr.getTotaltax())){
											gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() + gstr.getTotaltax());
										}
									}else{
										if(isNotEmpty(gstr.getTotaltaxableamount())) {
											gstr3b_gstr1.setGstr1exports_WOP_Taxableamount(gstr3b_gstr1.getGstr1exports_WOP_Taxableamount() + gstr.getTotaltaxableamount());
										}
										if(isNotEmpty(gstr.getTotaltax())){
											gstr3b_gstr1.setGstr1exports_WOP_Taxamount(gstr3b_gstr1.getGstr1exports_WOP_Taxamount() + gstr.getTotaltax());
										}
									}
								}
							}
						}
						if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNUR.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNURA.equalsIgnoreCase(gstr.getInvtype())) {
							String org_invno = "";
							String docType = "";
							if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)) {
								if (isNotEmpty(((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty())) {
									docType = ((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty();
									org_invno =((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getInum(); 
											//orginal_invno.get(((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getInum());
								}
							} else if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)) {
								if (isNotEmpty(((GSTR1) gstr).getCdnur().get(0).getNtty())) {
									docType = gstr.getCdnur().get(0).getNtty();
									org_invno = gstr.getCdnur().get(0).getInum(); 
											//orginal_invno.get(gstr.getCdnur().get(0).getInum());
								}
							}
							
							Date stDate = null;
							Date endDate = null;
							Calendar cal = Calendar.getInstance();
							if(month < 10) {
								cal.set(year-1, 3, 1, 0, 0, 0);
								stDate = new java.util.Date(cal.getTimeInMillis());
								cal = Calendar.getInstance();
								cal.set(year, month+2, 0, 23, 59, 59);
								endDate = new java.util.Date(cal.getTimeInMillis());
							}else {
								cal.set(year, 3, 1, 0, 0, 0);
								stDate = new java.util.Date(cal.getTimeInMillis());
								cal = Calendar.getInstance();
								cal.set(year, month+2, 0, 23, 59, 59);
								endDate = new java.util.Date(cal.getTimeInMillis());
							}
							List<GSTR1> gstr1list = gstr1Repository.findByInvoicenoAndClientidAndDateofinvoiceBetween(org_invno, client.getId().toString(), stDate, endDate);
							
							if (docType.equalsIgnoreCase("C")) {
								boolean flag = false;
								if(isNotEmpty(gstr1list)) {
									for(InvoiceParent gstrr1 : gstr1list) {
										if(isNotEmpty(gstrr1.getGovtInvoiceStatus())) {
											if(MasterGSTConstants.SUCCESS.equalsIgnoreCase(gstrr1.getGovtInvoiceStatus())) {
												if(isNotEmpty(gstrr1) && isNotEmpty(gstrr1.getInvoiceno())) {
													if(org_invno.equals(gstrr1.getInvoiceno())) {
														flag = true;
														if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B) || gstrr1.getInvtype().equals(MasterGSTConstants.B2C) || gstrr1.getInvtype().equals(MasterGSTConstants.B2CL)) {
															String creverseChargeType = "";
															if(isNotEmpty(gstrr1.getRevchargetype())) {
																creverseChargeType = gstrr1.getRevchargetype();
															}else {
																creverseChargeType = "Regular";
															}
															if("Regular".equals(creverseChargeType)) {
																String invType = "";
																if(isNotEmpty(gstrr1.getB2b()) && isNotEmpty(gstrr1.getB2b().get(0).getInv())	&& isNotEmpty(gstrr1.getB2b().get(0).getInv().get(0).getInvTyp())) {
																	invType = gstrr1.getB2b().get(0).getInv().get(0).getInvTyp();
																}else {
																	invType = "R";
																}
																if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B)){
																	if("R".equals(invType) || "CBW".equals(invType)) {
																		if(isNotEmpty(gstr.getTotaltaxableamount())) {
																			gstr3b_gstr1.setGstr1b2b_R_CBW_Taxableamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxableamount() - gstr.getTotaltaxableamount());																		
																		}
																		if(isNotEmpty(gstr.getTotaltax())){
																			gstr3b_gstr1.setGstr1b2b_R_CBW_Taxamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxamount() - gstr.getTotaltax());
																		}
																	}											
																}else {
																	if("R".equals(invType) || "CBW".equals(invType)) {
																		if(isNotEmpty(gstr.getTotaltaxableamount())) {
																			gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxableamount(gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxableamount() - gstr.getTotaltaxableamount());
																		}
																		if(isNotEmpty(gstr.getTotaltax())){
																			gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxamount(gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxamount() - gstr.getTotaltax());
																		}
																	}
																}
																if("DE".equals(invType)) {
																	if(isNotEmpty(gstr.getTotaltaxableamount())) {
																		gstr3b_gstr1.setGstr1b2b_CL_DE_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_DE_Taxableamount() - gstr.getTotaltaxableamount());
																	}
																	if(isNotEmpty(gstr.getTotaltax())){
																		gstr3b_gstr1.setGstr1b2b_CL_DE_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_DE_Taxamount() - gstr.getTotaltax());
																	}
																}else if("SEWP".equals(invType) || "SEWPC".equals(invType)) {
																	if(isNotEmpty(gstr.getTotaltaxableamount())) {
																		gstr3b_gstr1.setGstr1b2b_CL_SEWP_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxableamount() - gstr.getTotaltaxableamount());
																	}
																	if(isNotEmpty(gstr.getTotaltax())){
																		gstr3b_gstr1.setGstr1b2b_CL_SEWP_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxamount() - gstr.getTotaltax());
																	}
																}else if("SEWOP".equals(invType)) {
																	if(isNotEmpty(gstr.getTotaltaxableamount())) {
																		gstr3b_gstr1.setGstr1b2b_CL_SEWOP_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxableamount() - gstr.getTotaltaxableamount());
																	}
																	gstr3b_gstr1.setGstr1b2b_CL_SEWOP_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxamount() - gstr.getTotaltax());
																}else if("WPAY".equals(invType)) {
																	if(isNotEmpty(gstr.getTotaltaxableamount())) {
																		gstr3b_gstr1.setGstr1exports_WP_Taxableamount(gstr3b_gstr1.getGstr1exports_WP_Taxableamount() - gstr.getTotaltaxableamount());
																	}
																	if(isNotEmpty(gstr.getTotaltax())){
																		gstr3b_gstr1.setGstr1exports_WP_Taxamount(gstr3b_gstr1.getGstr1exports_WP_Taxamount() - gstr.getTotaltax());
																	}
																}else if("WOPAY".equals(invType)) {
																	if(isNotEmpty(gstr.getTotaltaxableamount())) {
																		gstr3b_gstr1.setGstr1exports_WOP_Taxableamount(gstr.getTotaltaxableamount());
																	}
																	if(isNotEmpty(gstr.getTotaltax())){
																		gstr3b_gstr1.setGstr1exports_WOP_Taxamount(gstr3b_gstr1.getGstr1exports_WP_Taxamount() - gstr.getTotaltax());
																	}
																}
															}else {
																if(isNotEmpty(gstr.getTotaltaxableamount())) {
																	gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() - gstr.getTotaltaxableamount());
																}
																if(isNotEmpty(gstr.getTotaltax())){
																	gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() - gstr.getTotaltax());
																}
															}
														}
													}			
												}
											}
										}
									}
								}
								if(!flag) {
									if(isNotEmpty(gstr.getTotaltaxableamount())) {
										gstr3b_gstr1.setGstr1b2b_R_CBW_Taxableamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxableamount() - gstr.getTotaltaxableamount());																		
									}
									if(isNotEmpty(gstr.getTotaltax())){
										gstr3b_gstr1.setGstr1b2b_R_CBW_Taxamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxamount() - gstr.getTotaltax());
									}
								}
							} else if (docType.equalsIgnoreCase("D")) {
								boolean flag = false;
								if(isNotEmpty(gstr1list)) {
									for(InvoiceParent gstrr1 : gstr1list) {
										if(isNotEmpty(gstrr1.getGovtInvoiceStatus())) {
											if(isNotEmpty(gstrr1) && isNotEmpty(gstrr1.getInvoiceno())) {
												if(org_invno.equals(gstrr1.getInvoiceno())) {
													flag = true;
													if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B) || gstrr1.getInvtype().equals(MasterGSTConstants.B2C) || gstrr1.getInvtype().equals(MasterGSTConstants.B2CL)) {
														String creverseChargeType = "";
														if(isNotEmpty(gstrr1.getRevchargetype())) {
															creverseChargeType = gstrr1.getRevchargetype();
														}else {
															creverseChargeType = "Regular";
														}
														if("Regular".equals(creverseChargeType)) {
															String invType = "";
															if(isNotEmpty(gstrr1.getB2b()) && isNotEmpty(gstrr1.getB2b().get(0).getInv()) && isNotEmpty(gstrr1.getB2b().get(0).getInv().get(0).getInvTyp())) {
																invType = gstrr1.getB2b().get(0).getInv().get(0).getInvTyp();
															}else {
																invType = "R";
															}
															if(gstrr1.getInvtype().equals(MasterGSTConstants.B2B)){
																if("R".equals(invType) || "CBW".equals(invType)) {
																	if(isNotEmpty(gstr.getTotaltaxableamount())) {
																		gstr3b_gstr1.setGstr1b2b_R_CBW_Taxableamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
																	}
																	if(isNotEmpty(gstr.getTotaltax())){
																		gstr3b_gstr1.setGstr1b2b_R_CBW_Taxamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxamount() + gstr.getTotaltax());
																	}
																}											
															}else {
																if("R".equals(invType) || "CBW".equals(invType)) {
																	if(isNotEmpty(gstr.getTotaltaxableamount())) {
																		gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxableamount(gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
																	}
																	if(isNotEmpty(gstr.getTotaltax())){
																		gstr3b_gstr1.setGstr1b2cl_R_CBW_Taxamount(gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxamount() + gstr.getTotaltax());
																	}
																}
															}
															if("DE".equals(invType)) {
																if(isNotEmpty(gstr.getTotaltaxableamount())) {
																	gstr3b_gstr1.setGstr1b2b_CL_DE_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_DE_Taxableamount() + gstr.getTotaltaxableamount());
																}
																if(isNotEmpty(gstr.getTotaltax())){
																	gstr3b_gstr1.setGstr1b2b_CL_DE_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_DE_Taxamount() + gstr.getTotaltax());
																}
															}else if("SEWP".equals(invType) || "SEWPC".equals(invType)) {
																if(isNotEmpty(gstr.getTotaltaxableamount())) {
																	gstr3b_gstr1.setGstr1b2b_CL_SEWP_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxableamount() + gstr.getTotaltaxableamount());
																}
																if(isNotEmpty(gstr.getTotaltax())){
																	gstr3b_gstr1.setGstr1b2b_CL_SEWP_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxamount() + gstr.getTotaltax());
																}
															}else if("SEWOP".equals(invType)) {
																if(isNotEmpty(gstr.getTotaltaxableamount())) {
																	gstr3b_gstr1.setGstr1b2b_CL_SEWOP_Taxableamount(gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxableamount() + gstr.getTotaltaxableamount());
																}
																if(isNotEmpty(gstr.getTotaltax())){
																	gstr3b_gstr1.setGstr1b2b_CL_SEWOP_Taxamount(gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxamount() + gstr.getTotaltax());
																}
															}else if("WPAY".equals(invType)) {
																if(isNotEmpty(gstr.getTotaltaxableamount())) {
																	gstr3b_gstr1.setGstr1exports_WP_Taxableamount(gstr3b_gstr1.getGstr1exports_WP_Taxableamount() + gstr.getTotaltaxableamount());
																}
																if(isNotEmpty(gstr.getTotaltax())){
																	gstr3b_gstr1.setGstr1exports_WP_Taxamount(gstr3b_gstr1.getGstr1exports_WP_Taxamount() + gstr.getTotaltax());
																}
															}else if("WOPAY".equals(invType)) {
																if(isNotEmpty(gstr.getTotaltaxableamount())) {
																	gstr3b_gstr1.setGstr1exports_WOP_Taxableamount(gstr.getTotaltaxableamount());
																}
																if(isNotEmpty(gstr.getTotaltax())){
																	gstr3b_gstr1.setGstr1exports_WOP_Taxamount(gstr3b_gstr1.getGstr1exports_WP_Taxamount() + gstr.getTotaltax());
																}
															}
														}else {
															if(isNotEmpty(gstr.getTotaltaxableamount())) {
																gstr3b_gstr1.setGstr1rcm_Taxableamount(gstr3b_gstr1.getGstr1rcm_Taxableamount() + gstr.getTotaltaxableamount());
															}
															if(isNotEmpty(gstr.getTotaltax())){
																gstr3b_gstr1.setGstr1rcm_Taxamount(gstr3b_gstr1.getGstr1rcm_Taxamount() + gstr.getTotaltax());
															}
														}
													}
												}
											}
										}	
									}
								}
								if(!flag) {
									if(isNotEmpty(gstr.getTotaltaxableamount())) {
										gstr3b_gstr1.setGstr1b2b_R_CBW_Taxableamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxableamount() + gstr.getTotaltaxableamount());
									}
									if(isNotEmpty(gstr.getTotaltax())){
										gstr3b_gstr1.setGstr1b2b_R_CBW_Taxamount(gstr3b_gstr1.getGstr1b2b_R_CBW_Taxamount() + gstr.getTotaltax());
									}
								}
							}
						}
						
						if (MasterGSTConstants.NIL.equalsIgnoreCase(gstr.getInvtype())) {
							gstr.getItems().forEach(item -> {
								if ("Exempted".equalsIgnoreCase(item.getType()) || "Nil Rated".equalsIgnoreCase(item.getType())) {
									if(isNotEmpty(item.getTaxablevalue())) {
										gstr3b_gstr1.setGstr1nilrated_Exempted_Taxableamount(gstr3b_gstr1.getGstr1nilrated_Exempted_Taxableamount() + item.getTaxablevalue());
									}
									gstr3b_gstr1.setGstr1nilrated_Exempted_Taxamount(0d);
								} else if ("Non-GST".equalsIgnoreCase(item.getType())) {
									if(isNotEmpty(item.getTaxablevalue())) {
										gstr3b_gstr1.setGstr1nongst_Taxableamount(gstr3b_gstr1.getGstr1nongst_Taxableamount() + item.getTaxablevalue());
									}
									gstr3b_gstr1.setGstr1nongst_Taxamount(0d);
								}
							});
						}
						if (isNotEmpty(client.getStatename())) {
							Double taxableamount=0d, taxamount=0d;
							int multiply = 1;
							if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNUR.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNA.equalsIgnoreCase(gstr.getInvtype()) || MasterGSTConstants.CDNURA.equalsIgnoreCase(gstr.getInvtype())) {
								
								String docType = "";
								if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)) {
									if (isNotEmpty(((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR1) gstr).getCdnr().get(0).getNt().get(0).getNtty();
									}
								} else if (gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR) || gstr.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)) {
									if (isNotEmpty(((GSTR1) gstr).getCdnur().get(0).getNtty())) {
										docType = gstr.getCdnur().get(0).getNtty();
									}
								}
								if(docType.equalsIgnoreCase("C")) {
									 multiply = -1;
								}else if (docType.equalsIgnoreCase("D")) {
									 multiply = 1;
								}
							}
							if(isNotEmpty(gstr.getTotaltaxableamount())) {
								taxableamount += gstr.getTotaltaxableamount() * multiply;
							}
							if(isNotEmpty(gstr.getTotaltax())) {
								taxamount += gstr.getTotaltax() * multiply;
							}
							if (MasterGSTConstants.NIL.equalsIgnoreCase(gstr.getInvtype())) {
								if(isNotEmpty(gstr.getNil()) && isNotEmpty(gstr.getNil().getInv())) {
									for(GSTRNilItems nil : gstr.getNil().getInv()) {
										Double niltaxbleamt = 0d;
										if(isNotEmpty(nil.getExptAmt())) {
											niltaxbleamt += nil.getExptAmt();
										}
										if(isNotEmpty(nil.getNgsupAmt())) {
											niltaxbleamt += nil.getNgsupAmt();
										}
										if(isNotEmpty(nil.getNilAmt())) {
											niltaxbleamt += nil.getNilAmt();
										}
										if(isNotEmpty(nil.getSplyType()) && ("INTRAB2B".equalsIgnoreCase(nil.getSplyType()) || "INTRAB2C".equalsIgnoreCase(nil.getSplyType()))) {
												gstr3b_gstr1.setGstr1_intra_Taxableamount(gstr3b_gstr1.getGstr1_intra_Taxableamount() + niltaxbleamt);
										}else {
												gstr3b_gstr1.setGstr1_inter_Taxableamount(gstr3b_gstr1.getGstr1_inter_Taxableamount() + niltaxbleamt);
										}
									}
								}
							}else {
								String clntstatename = client.getStatename();
								String invstatename = "";
								if(isNotEmpty(gstr.getStatename())) {
									invstatename = gstr.getStatename();
								}else {
									gstr.setStatename("97-Other Territory");
									invstatename = "97-Other Territory";
								}
								if(clntstatename.contains("-")) {
									clntstatename = clntstatename.substring(3);
								}
								if(invstatename.contains("-")) {
									invstatename = invstatename.substring(3);
								}
								if (client.getStatename().equalsIgnoreCase(gstr.getStatename()) || clntstatename.equalsIgnoreCase(invstatename) || client.getStatename().substring(0, 2).equalsIgnoreCase(gstr.getStatename().substring(0, 2))) {
									if(isNotEmpty(gstr.getTotaltaxableamount())) {
										gstr3b_gstr1.setGstr1_intra_Taxableamount(gstr3b_gstr1.getGstr1_intra_Taxableamount() + taxableamount);
									}
									if(isNotEmpty(gstr.getTotaltax())) {
										gstr3b_gstr1.setGstr1_intra_Taxamount(gstr3b_gstr1.getGstr1_intra_Taxamount() + taxamount);
									}
								} else {
									if(isNotEmpty(gstr.getTotaltaxableamount())) {
										gstr3b_gstr1.setGstr1_inter_Taxableamount(gstr3b_gstr1.getGstr1_inter_Taxableamount() + taxableamount);
									}
									if(isNotEmpty(gstr.getTotaltax())) {
										gstr3b_gstr1.setGstr1_inter_Taxamount(gstr3b_gstr1.getGstr1_inter_Taxamount() + taxamount);
									}
								}
							}
						}
					
					}
				}
			}
			});
		}
		gstr3b_gstr1.setGstr1_Total_Taxableamt1(
				gstr3b_gstr1.getGstr1b2b_R_CBW_Taxableamount()+
				gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxableamount()+
				gstr3b_gstr1.getGstr1b2b_CL_DE_Taxableamount()+
				gstr3b_gstr1.getGstr1rcm_Taxableamount()+
				gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxableamount()+
				gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxableamount()+
				gstr3b_gstr1.getGstr1exports_WP_Taxableamount()+
				gstr3b_gstr1.getGstr1exports_WOP_Taxableamount()+
				gstr3b_gstr1.getGstr1nilrated_Exempted_Taxableamount()+
				gstr3b_gstr1.getGstr1nongst_Taxableamount());
		gstr3b_gstr1.setGstr1_Total_Taxamt1(
				gstr3b_gstr1.getGstr1b2b_R_CBW_Taxamount()+
				gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxamount()+
				gstr3b_gstr1.getGstr1b2b_CL_DE_Taxamount()+
				gstr3b_gstr1.getGstr1rcm_Taxamount()+
				gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxamount()+
				gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxamount()+
				gstr3b_gstr1.getGstr1exports_WP_Taxamount()+
				gstr3b_gstr1.getGstr1exports_WOP_Taxamount()+
				gstr3b_gstr1.getGstr1nilrated_Exempted_Taxamount()+
				gstr3b_gstr1.getGstr1nongst_Taxamount());
				
		gstr3b_gstr1.setGstr1_Total_Taxableamt2(gstr3b_gstr1.getGstr1_intra_Taxableamount() + gstr3b_gstr1.getGstr1_inter_Taxableamount());
		gstr3b_gstr1.setGstr1_Total_Taxamt2(gstr3b_gstr1.getGstr1_intra_Taxamount() + gstr3b_gstr1.getGstr1_inter_Taxamount());

		/**
		 * 
		 * GSTR1-GSTR3B
		 */
		Double gstr1_diffTaxableamount1=gstr3b_gstr1.getGstr1b2b_R_CBW_Taxableamount()+
				gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxableamount()+
				gstr3b_gstr1.getGstr1b2b_CL_DE_Taxableamount()+
				gstr3b_gstr1.getGstr1rcm_Taxableamount();
		
		Double gstr1_diffTaxamount1=gstr3b_gstr1.getGstr1b2b_R_CBW_Taxamount()+
				gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxamount()+
				gstr3b_gstr1.getGstr1b2b_CL_DE_Taxamount()+
				gstr3b_gstr1.getGstr1rcm_Taxamount();
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Taxableamount_Total1(gstr3b_gstr1.getGSTR3B_3a_Taxableamt()-gstr1_diffTaxableamount1);
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Taxamount_Total1(gstr3b_gstr1.getGSTR3B_3a_Taxamt()-gstr1_diffTaxamount1);
		
		double gstr3b_diffTaxableamount2 = gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxableamount()+ gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxableamount() +
				gstr3b_gstr1.getGstr1exports_WP_Taxableamount()+gstr3b_gstr1.getGstr1exports_WOP_Taxableamount();
		double gstr3b_diffTaxamount2 = gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxamount() + gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxamount()+ 
										gstr3b_gstr1.getGstr1exports_WP_Taxamount() +gstr3b_gstr1.getGstr1exports_WOP_Taxamount();
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Taxableamount_Total2(gstr3b_gstr1.getGSTR3B_3b_Taxableamt()-gstr3b_diffTaxableamount2);
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Taxamount_Total2(gstr3b_gstr1.getGSTR3B_3b_Taxamt()-gstr3b_diffTaxamount2);

		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Nilrated_Exempted_Taxableamount(gstr3b_gstr1.getGSTR3B_3c_Taxableamt()-gstr3b_gstr1.getGstr1nilrated_Exempted_Taxableamount());
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Nilrated_Exempted_Taxamount(gstr3b_gstr1.getGSTR3B_3c_Taxamt()-gstr3b_gstr1.getGstr1nilrated_Exempted_Taxamount());

		gstr3b_gstr1.setDiffGSTR1_GSTR3B_NonGST_Taxableamount(gstr3b_gstr1.getGSTR3B_3e_Taxableamt()-gstr3b_gstr1.getGstr1nongst_Taxableamount());
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_NonGST_Taxamount(-gstr3b_gstr1.getGstr1nongst_Taxamount());

		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Total_Taxableamount1(gstr3b_gstr1.getDiffGSTR1_GSTR3B_Taxableamount_Total1()+gstr3b_gstr1.getDiffGSTR1_GSTR3B_Taxableamount_Total2()
														+gstr3b_gstr1.getDiffGSTR1_GSTR3B_Nilrated_Exempted_Taxableamount()+gstr3b_gstr1.getDiffGSTR1_GSTR3B_NonGST_Taxableamount());
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Total_Taxamount1(gstr3b_gstr1.getDiffGSTR1_GSTR3B_Taxamount_Total1()+gstr3b_gstr1.getDiffGSTR1_GSTR3B_Taxamount_Total2()
														+gstr3b_gstr1.getDiffGSTR1_GSTR3B_Nilrated_Exempted_Taxamount()+gstr3b_gstr1.getDiffGSTR1_GSTR3B_NonGST_Taxamount());
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Inter_Taxableamount(gstr3b_gstr1.getGSTR3B_Total_Taxableamt()-	(gstr3b_gstr1.getGstr1_inter_Taxableamount() + gstr3b_gstr1.getGstr1_intra_Taxableamount()));
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Inter_Taxamount(gstr3b_gstr1.getGSTR3B_IGST()-gstr3b_gstr1.getGstr1_inter_Taxamount());
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Intra_Taxamount(gstr3b_gstr1.getGSTR3B_CGST_SGST()-gstr3b_gstr1.getGstr1_intra_Taxamount());
		
		gstr3b_gstr1.setDiffGSTR1_GSTR3B_Total_Taxamount2(gstr3b_gstr1.getDiffGSTR1_GSTR3B_Inter_Taxamount()+gstr3b_gstr1.getDiffGSTR1_GSTR3B_Intra_Taxamount());
		
		/**
		 * Books-GST1
		 */
		gstr3b_gstr1.setDiffBook_GSTR1_B2B_Taxableamount(gstr3b_gstr1.getB2b_R_CBW_Taxableamount()- gstr3b_gstr1.getGstr1b2b_R_CBW_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_B2B_Taxamount(gstr3b_gstr1.getB2b_R_CBW_Taxamount()- gstr3b_gstr1.getGstr1b2b_R_CBW_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_B2C_Taxableamount(gstr3b_gstr1.getB2cl_R_CBW_Taxableamount()-gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_B2C_Taxamount(gstr3b_gstr1.getB2cl_R_CBW_Taxamount()-gstr3b_gstr1.getGstr1b2cl_R_CBW_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Deemed_Taxableamount(gstr3b_gstr1.getB2b_CL_DE_Taxableamount()-gstr3b_gstr1.getGstr1b2b_CL_DE_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Deemed_Taxamount(gstr3b_gstr1.getB2b_CL_DE_Taxamount()-gstr3b_gstr1.getGstr1b2b_CL_DE_Taxamount());
		
		gstr3b_gstr1.setDiffBook_GSTR1_RCM_Taxableamount(gstr3b_gstr1.getRcm_Taxableamount()-gstr3b_gstr1.getGstr1rcm_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_RCM_Taxamount(gstr3b_gstr1.getRcm_Taxamount()-gstr3b_gstr1.getGstr1rcm_Taxamount());
		
		gstr3b_gstr1.setDiffBook_GSTR1_SEZWP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxableamount()-gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_SEZWP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWP_Taxamount()-gstr3b_gstr1.getGstr1b2b_CL_SEWP_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_SEZWOP_Taxableamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxableamount()-gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_SEZWOP_Taxamount(gstr3b_gstr1.getB2b_CL_SEWOP_Taxamount()-gstr3b_gstr1.getGstr1b2b_CL_SEWOP_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Export_WP_Taxableamount(gstr3b_gstr1.getExports_WP_Taxableamount()-gstr3b_gstr1.getGstr1exports_WP_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Export_WP_Taxamount(gstr3b_gstr1.getExports_WP_Taxamount()-gstr3b_gstr1.getGstr1exports_WP_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Export_WOP_Taxableamount(gstr3b_gstr1.getExports_WOP_Taxableamount()-gstr3b_gstr1.getGstr1exports_WOP_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Export_WOP_Taxamount(gstr3b_gstr1.getExports_WOP_Taxamount()-gstr3b_gstr1.getGstr1exports_WOP_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Nil_Taxableamount(gstr3b_gstr1.getNilrated_Exempted_Taxableamount()-gstr3b_gstr1.getGstr1nilrated_Exempted_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Nil_Taxamount(gstr3b_gstr1.getNilrated_Exempted_Taxamount()-gstr3b_gstr1.getGstr1nilrated_Exempted_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_NonGst_Taxableamount(gstr3b_gstr1.getNongst_Taxableamount()-gstr3b_gstr1.getGstr1nongst_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_NonGst_Taxamount(gstr3b_gstr1.getNongst_Taxamount()-gstr3b_gstr1.getGstr1nongst_Taxamount());
				
		gstr3b_gstr1.setDiffBook_GSTR1_Inter_Taxableamount(gstr3b_gstr1.getBook_inter_Taxableamount()-gstr3b_gstr1.getGstr1_inter_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Inter_Taxamount(gstr3b_gstr1.getBook_inter_Taxamount()-gstr3b_gstr1.getGstr1_inter_Taxamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Intra_Taxableamount(gstr3b_gstr1.getBook_intra_Taxableamount()-gstr3b_gstr1.getGstr1_intra_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Intra_Taxamount(gstr3b_gstr1.getBook_intra_Taxamount()-gstr3b_gstr1.getGstr1_intra_Taxamount());
	
		gstr3b_gstr1.setDiffBook_GSTR1_Total_Taxableamount2(gstr3b_gstr1.getDiffBook_GSTR1_Inter_Taxableamount()+gstr3b_gstr1.getDiffBook_GSTR1_Intra_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Total_Taxamount2(gstr3b_gstr1.getDiffBook_GSTR1_Inter_Taxamount()+gstr3b_gstr1.getDiffBook_GSTR1_Intra_Taxamount());
		
		gstr3b_gstr1.setDiffBook_GSTR1_Total_Taxableamount1(
					gstr3b_gstr1.getDiffBook_GSTR1_B2B_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_B2C_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Deemed_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_RCM_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_SEZWP_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_SEZWOP_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Export_WP_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Export_WOP_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Nil_Taxableamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_NonGst_Taxableamount());
		gstr3b_gstr1.setDiffBook_GSTR1_Total_Taxamount1(
					gstr3b_gstr1.getDiffBook_GSTR1_B2B_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_B2C_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Deemed_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_RCM_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_SEZWP_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_SEZWOP_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Export_WP_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Export_WOP_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_Nil_Taxamount()+
					gstr3b_gstr1.getDiffBook_GSTR1_NonGst_Taxamount());
		
		//System.out.println(orginal_invno);
		//System.out.println(gstr3b_gstr1);
		logger.debug(CLASSNAME + method + "END");
		return gstr3b_gstr1;
	}
	
	public  boolean isScientificNotation(String numberString) {

	    // Validate number
	    try {
	        new BigDecimal(numberString);
	    } catch (NumberFormatException e) {
	        return false;
	    }

	    // Check for scientific notation
	    return numberString.toUpperCase().contains("E") && numberString.charAt(1)=='.';   
	}
	
	public String getStateCode(String strState) {
		List<StateConfig> states = configService.getStates();
		if (isNotEmpty(strState)) {
			for (StateConfig state : states) {
				if (state.getName().equals(strState)) {
					Integer tin = state.getTin();
					return (tin < 10) ? ("0" + tin) : (tin + "");
				} else if (state.getCode().equals(strState)) {
					Integer tin = state.getTin();
					return (tin < 10) ? ("0" + tin) : (tin + "");
				} else if (state.getTin().toString().equals(strState)) {
					Integer tin = state.getTin();
					return (tin < 10) ? ("0" + tin) : (tin + "");
				} else if (state.getName().equals(state.getTin() + "-" + strState)) {
					Integer tin = state.getTin();
					return (tin < 10) ? ("0" + tin) : (tin + "");
				}
			}
			if (strState.contains("-")) {
				strState = strState.substring(0, strState.indexOf("-")).trim();
				return (strState.length() < 2) ? ("0" + strState) : strState;
			}
			if (strState.equals(",")) {
				return "";
			}
		}
		return strState;
	}
}
