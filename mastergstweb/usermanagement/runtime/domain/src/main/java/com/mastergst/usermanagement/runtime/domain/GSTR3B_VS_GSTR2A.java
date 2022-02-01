package com.mastergst.usermanagement.runtime.domain;

public class GSTR3B_VS_GSTR2A {

	// GSTR3B variables
	private String month;
	private String summaryyear;
	private double importGoods_IGST_GSTR3B;
	private double importServices_IGST_GSTR3B;
	private double rcm_GSTR3B;
	private double rcm_CGST_GSTR3B;
	private double rcm_SGST_GSTR3B;
	private double rcm_IGST_GSTR3B;
	private double isd_GSTR3B;
	private double isd_CGST_GSTR3B;
	private double isd_SGST_GSTR3B;
	private double isd_IGST_GSTR3B;
	private double eligible_GSTR3B;
	private double eligible_CGST_GSTR3B;
	private double eligible_SGST_GSTR3B;
	private double eligible_IGST_GSTR3B;
	private double ineligible_GSTR3B;
	private double ineligible_CGST_GSTR3B;
	private double ineligible_SGST_GSTR3B;
	private double ineligible_IGST_GSTR3B;
	private double credit_Reversed_GSTR3B;
	private double credit_Reversed_CGST_GSTR3B;
	private double credit_Reversed_SGST_GSTR3B;
	private double credit_Reversed_IGST_GSTR3B;
	// GSTR2A variables
	private double importGoods_IGST_GSTR2A;
	private double importServices_IGST_GSTR2A;
	private double rcm_GSTR2A;
	private double rcm_CGST_GSTR2A;
	private double rcm_SGST_GSTR2A;
	private double rcm_IGST_GSTR2A;
	private double isd_GSTR2A;
	private double isd_CGST_GSTR2A;
	private double isd_SGST_GSTR2A;
	private double isd_IGST_GSTR2A;
	private double eligible_GSTR2A;
	private double eligible_CGST_GSTR2A;
	private double eligible_SGST_GSTR2A;
	private double eligible_IGST_GSTR2A;
	private double ineligible_GSTR2A;
	private double ineligible_CGST_GSTR2A;
	private double ineligible_SGST_GSTR2A;
	private double ineligible_IGST_GSTR2A;
	private double credit_Reversed_GSTR2A;
	private double credit_Reversed_CGST_GSTR2A;
	private double credit_Reversed_SGST_GSTR2A;
	private double credit_Reversed_IGST_GSTR2A;
	// GSTR2(Purchase Register)
	private double importGoods_IGST_GSTR2;
	private double importServices_IGST_GSTR2;
	private double rcm_GSTR2;
	private double rcm_CGST_GSTR2;
	private double rcm_SGST_GSTR2;
	private double rcm_IGST_GSTR2;
	private double isd_GSTR2;
	private double isd_CGST_GSTR2;
	private double isd_SGST_GSTR2;
	private double isd_IGST_GSTR2;
	private double eligible_GSTR2;
	private double eligible_CGST_GSTR2;
	private double eligible_SGST_GSTR2;
	private double eligible_IGST_GSTR2;
	private double ineligible_GSTR2;
	private double ineligible_CGST_GSTR2;
	private double ineligible_SGST_GSTR2;
	private double ineligible_IGST_GSTR2;
	private double credit_Reversed_GSTR2;
	private double credit_Reversed_CGST_GSTR2;
	private double credit_Reversed_SGST_GSTR2;
	private double credit_Reversed_IGST_GSTR2;

	// GSTR2(Purchase Register) - GSTR3B
	private double diffImportGoods_IGST_GSTR2_GSTR3B;
	private double diffImportServices_IGST_GSTR2_GSTR3B;
	private double diffRCM_GSTR2_GSTR3B;
	private double diffRCM_IGST_GSTR2_GSTR3B;
	private double diffRCM_CGST_GSTR2_GSTR3B;
	private double diffRCM_SGST_GSTR2_GSTR3B;

	private double diffISD_GSTR2_GSTR3B;
	private double diffISD_IGST_GSTR2_GSTR3B;
	private double diffISD_CGST_GSTR2_GSTR3B;
	private double diffISD_SGST_GSTR2_GSTR3B;

	private double diffCredit_Reversed_GSTR2_GSTR3B;
	private double diffCredit_Reversed_IGST_GSTR2_GSTR3B;
	private double diffCredit_Reversed_CGST_GSTR2_GSTR3B;
	private double diffCredit_Reversed_SGST_GSTR2_GSTR3B;

	private double diffEligible_GSTR2_GSTR3B;
	private double diffEligible_CGST_GSTR2_GSTR3B;
	private double diffEligible_SGST_GSTR2_GSTR3B;
	private double diffEligible_IGST_GSTR2_GSTR3B;

	private double diffIneligible_GSTR2_GSTR3B;
	private double diffIneligible_CGST_GSTR2_GSTR3B;
	private double diffIneligible_SGST_GSTR2_GSTR3B;
	private double diffIneligible_IGST_GSTR2_GSTR3B;

	// GSTR2(Purchase Register)-GSTR2A
	private double diffImportGoods_IGST_GSTR2_GSTR2A;
	private double diffImportServices_IGST_GSTR2_GSTR2A;
	private double diffRCM_GSTR2_GSTR2A;
	private double diffRCM_IGST_GSTR2_GSTR2A;
	private double diffRCM_CGST_GSTR2_GSTR2A;
	private double diffRCM_SGST_GSTR2_GSTR2A;

	private double diffISD_GSTR2_GSTR2A;
	private double diffISD_IGST_GSTR2_GSTR2A;
	private double diffISD_CGST_GSTR2_GSTR2A;
	private double diffISD_SGST_GSTR2_GSTR2A;

	private double diffEligible_GSTR2_GSTR2A;;
	private double diffEligible_CGST_GSTR2_GSTR2A;
	private double diffEligible_SGST_GSTR2_GSTR2A;
	private double diffEligible_IGST_GSTR2_GSTR2A;

	private double diffIneligible_GSTR2_GSTR2A;
	private double diffIneligible_CGST_GSTR2_GSTR2A;
	private double diffIneligible_SGST_GSTR2_GSTR2A;
	private double diffIneligible_IGST_GSTR2_GSTR2A;

	// GSTR3B-GSTR2A
	private double diffImportGoods_IGST_GSTR3B_GSTR2A;
	private double diffImportServices_IGST_GSTR3B_GSTR2A;
	private double diffRCM_GSTR3B_GSTR2A;
	private double diffRCM_IGST_GSTR3B_GSTR2A;
	private double diffRCM_CGST_GSTR3B_GSTR2A;
	private double diffRCM_SGST_GSTR3B_GSTR2A;

	private double diffISD_GSTR3B_GSTR2A;
	private double diffISD_IGST_GSTR3B_GSTR2A;
	private double diffISD_CGST_GSTR3B_GSTR2A;
	private double diffISD_SGST_GSTR3B_GSTR2A;

	private double diffEligible_GSTR3B_GSTR2A;;
	private double diffEligible_CGST_GSTR3B_GSTR2A;
	private double diffEligible_SGST_GSTR3B_GSTR2A;
	private double diffEligible_IGST_GSTR3B_GSTR2A;

	private double diffIneligible_GSTR3B_GSTR2A;
	private double diffIneligible_CGST_GSTR3B_GSTR2A;
	private double diffIneligible_SGST_GSTR3B_GSTR2A;
	private double diffIneligible_IGST_GSTR3B_GSTR2A;

	public double getDiffRCM_GSTR3B_GSTR2A() {
		return diffRCM_GSTR3B_GSTR2A;
	}

	public void setDiffRCM_GSTR3B_GSTR2A(double diffRCM_GSTR3B_GSTR2A) {
		this.diffRCM_GSTR3B_GSTR2A = diffRCM_GSTR3B_GSTR2A;
	}

	public double getDiffRCM_IGST_GSTR3B_GSTR2A() {
		return diffRCM_IGST_GSTR3B_GSTR2A;
	}

	public void setDiffRCM_IGST_GSTR3B_GSTR2A(double diffRCM_IGST_GSTR3B_GSTR2A) {
		this.diffRCM_IGST_GSTR3B_GSTR2A = diffRCM_IGST_GSTR3B_GSTR2A;
	}

	public double getDiffRCM_CGST_GSTR3B_GSTR2A() {
		return diffRCM_CGST_GSTR3B_GSTR2A;
	}

	public void setDiffRCM_CGST_GSTR3B_GSTR2A(double diffRCM_CGST_GSTR3B_GSTR2A) {
		this.diffRCM_CGST_GSTR3B_GSTR2A = diffRCM_CGST_GSTR3B_GSTR2A;
	}

	public double getDiffRCM_SGST_GSTR3B_GSTR2A() {
		return diffRCM_SGST_GSTR3B_GSTR2A;
	}

	public void setDiffRCM_SGST_GSTR3B_GSTR2A(double diffRCM_SGST_GSTR3B_GSTR2A) {
		this.diffRCM_SGST_GSTR3B_GSTR2A = diffRCM_SGST_GSTR3B_GSTR2A;
	}

	public double getDiffISD_GSTR3B_GSTR2A() {
		return diffISD_GSTR3B_GSTR2A;
	}

	public void setDiffISD_GSTR3B_GSTR2A(double diffISD_GSTR3B_GSTR2A) {
		this.diffISD_GSTR3B_GSTR2A = diffISD_GSTR3B_GSTR2A;
	}

	public double getDiffISD_IGST_GSTR3B_GSTR2A() {
		return diffISD_IGST_GSTR3B_GSTR2A;
	}

	public void setDiffISD_IGST_GSTR3B_GSTR2A(double diffISD_IGST_GSTR3B_GSTR2A) {
		this.diffISD_IGST_GSTR3B_GSTR2A = diffISD_IGST_GSTR3B_GSTR2A;
	}

	public double getDiffISD_CGST_GSTR3B_GSTR2A() {
		return diffISD_CGST_GSTR3B_GSTR2A;
	}

	public void setDiffISD_CGST_GSTR3B_GSTR2A(double diffISD_CGST_GSTR3B_GSTR2A) {
		this.diffISD_CGST_GSTR3B_GSTR2A = diffISD_CGST_GSTR3B_GSTR2A;
	}

	public double getDiffISD_SGST_GSTR3B_GSTR2A() {
		return diffISD_SGST_GSTR3B_GSTR2A;
	}

	public void setDiffISD_SGST_GSTR3B_GSTR2A(double diffISD_SGST_GSTR3B_GSTR2A) {
		this.diffISD_SGST_GSTR3B_GSTR2A = diffISD_SGST_GSTR3B_GSTR2A;
	}

	public double getDiffEligible_GSTR3B_GSTR2A() {
		return diffEligible_GSTR3B_GSTR2A;
	}

	public void setDiffEligible_GSTR3B_GSTR2A(double diffEligible_GSTR3B_GSTR2A) {
		this.diffEligible_GSTR3B_GSTR2A = diffEligible_GSTR3B_GSTR2A;
	}

	public double getDiffEligible_CGST_GSTR3B_GSTR2A() {
		return diffEligible_CGST_GSTR3B_GSTR2A;
	}

	public void setDiffEligible_CGST_GSTR3B_GSTR2A(double diffEligible_CGST_GSTR3B_GSTR2A) {
		this.diffEligible_CGST_GSTR3B_GSTR2A = diffEligible_CGST_GSTR3B_GSTR2A;
	}

	public double getDiffEligible_SGST_GSTR3B_GSTR2A() {
		return diffEligible_SGST_GSTR3B_GSTR2A;
	}

	public void setDiffEligible_SGST_GSTR3B_GSTR2A(double diffEligible_SGST_GSTR3B_GSTR2A) {
		this.diffEligible_SGST_GSTR3B_GSTR2A = diffEligible_SGST_GSTR3B_GSTR2A;
	}

	public double getDiffEligible_IGST_GSTR3B_GSTR2A() {
		return diffEligible_IGST_GSTR3B_GSTR2A;
	}

	public void setDiffEligible_IGST_GSTR3B_GSTR2A(double diffEligible_IGST_GSTR3B_GSTR2A) {
		this.diffEligible_IGST_GSTR3B_GSTR2A = diffEligible_IGST_GSTR3B_GSTR2A;
	}

	public double getDiffIneligible_GSTR3B_GSTR2A() {
		return diffIneligible_GSTR3B_GSTR2A;
	}

	public void setDiffIneligible_GSTR3B_GSTR2A(double diffIneligible_GSTR3B_GSTR2A) {
		this.diffIneligible_GSTR3B_GSTR2A = diffIneligible_GSTR3B_GSTR2A;
	}

	public double getDiffIneligible_CGST_GSTR3B_GSTR2A() {
		return diffIneligible_CGST_GSTR3B_GSTR2A;
	}

	public void setDiffIneligible_CGST_GSTR3B_GSTR2A(double diffIneligible_CGST_GSTR3B_GSTR2A) {
		this.diffIneligible_CGST_GSTR3B_GSTR2A = diffIneligible_CGST_GSTR3B_GSTR2A;
	}

	public double getDiffIneligible_SGST_GSTR3B_GSTR2A() {
		return diffIneligible_SGST_GSTR3B_GSTR2A;
	}

	public void setDiffIneligible_SGST_GSTR3B_GSTR2A(double diffIneligible_SGST_GSTR3B_GSTR2A) {
		this.diffIneligible_SGST_GSTR3B_GSTR2A = diffIneligible_SGST_GSTR3B_GSTR2A;
	}

	public double getDiffIneligible_IGST_GSTR3B_GSTR2A() {
		return diffIneligible_IGST_GSTR3B_GSTR2A;
	}

	public void setDiffIneligible_IGST_GSTR3B_GSTR2A(double diffIneligible_IGST_GSTR3B_GSTR2A) {
		this.diffIneligible_IGST_GSTR3B_GSTR2A = diffIneligible_IGST_GSTR3B_GSTR2A;
	}

	public double getDiffRCM_GSTR2_GSTR2A() {
		return diffRCM_GSTR2_GSTR2A;
	}

	public void setDiffRCM_GSTR2_GSTR2A(double diffRCM_GSTR2_GSTR2A) {
		this.diffRCM_GSTR2_GSTR2A = diffRCM_GSTR2_GSTR2A;
	}

	public double getDiffRCM_IGST_GSTR2_GSTR2A() {
		return diffRCM_IGST_GSTR2_GSTR2A;
	}

	public void setDiffRCM_IGST_GSTR2_GSTR2A(double diffRCM_IGST_GSTR2_GSTR2A) {
		this.diffRCM_IGST_GSTR2_GSTR2A = diffRCM_IGST_GSTR2_GSTR2A;
	}

	public double getDiffRCM_CGST_GSTR2_GSTR2A() {
		return diffRCM_CGST_GSTR2_GSTR2A;
	}

	public void setDiffRCM_CGST_GSTR2_GSTR2A(double diffRCM_CGST_GSTR2_GSTR2A) {
		this.diffRCM_CGST_GSTR2_GSTR2A = diffRCM_CGST_GSTR2_GSTR2A;
	}

	public double getDiffRCM_SGST_GSTR2_GSTR2A() {
		return diffRCM_SGST_GSTR2_GSTR2A;
	}

	public void setDiffRCM_SGST_GSTR2_GSTR2A(double diffRCM_SGST_GSTR2_GSTR2A) {
		this.diffRCM_SGST_GSTR2_GSTR2A = diffRCM_SGST_GSTR2_GSTR2A;
	}

	public double getDiffISD_GSTR2_GSTR2A() {
		return diffISD_GSTR2_GSTR2A;
	}

	public void setDiffISD_GSTR2_GSTR2A(double diffISD_GSTR2_GSTR2A) {
		this.diffISD_GSTR2_GSTR2A = diffISD_GSTR2_GSTR2A;
	}

	public double getDiffISD_IGST_GSTR2_GSTR2A() {
		return diffISD_IGST_GSTR2_GSTR2A;
	}

	public void setDiffISD_IGST_GSTR2_GSTR2A(double diffISD_IGST_GSTR2_GSTR2A) {
		this.diffISD_IGST_GSTR2_GSTR2A = diffISD_IGST_GSTR2_GSTR2A;
	}

	public double getDiffISD_CGST_GSTR2_GSTR2A() {
		return diffISD_CGST_GSTR2_GSTR2A;
	}

	public void setDiffISD_CGST_GSTR2_GSTR2A(double diffISD_CGST_GSTR2_GSTR2A) {
		this.diffISD_CGST_GSTR2_GSTR2A = diffISD_CGST_GSTR2_GSTR2A;
	}

	public double getDiffISD_SGST_GSTR2_GSTR2A() {
		return diffISD_SGST_GSTR2_GSTR2A;
	}

	public void setDiffISD_SGST_GSTR2_GSTR2A(double diffISD_SGST_GSTR2_GSTR2A) {
		this.diffISD_SGST_GSTR2_GSTR2A = diffISD_SGST_GSTR2_GSTR2A;
	}

	public double getDiffEligible_GSTR2_GSTR2A() {
		return diffEligible_GSTR2_GSTR2A;
	}

	public void setDiffEligible_GSTR2_GSTR2A(double diffEligible_GSTR2_GSTR2A) {
		this.diffEligible_GSTR2_GSTR2A = diffEligible_GSTR2_GSTR2A;
	}

	public double getDiffEligible_CGST_GSTR2_GSTR2A() {
		return diffEligible_CGST_GSTR2_GSTR2A;
	}

	public void setDiffEligible_CGST_GSTR2_GSTR2A(double diffEligible_CGST_GSTR2_GSTR2A) {
		this.diffEligible_CGST_GSTR2_GSTR2A = diffEligible_CGST_GSTR2_GSTR2A;
	}

	public double getDiffEligible_SGST_GSTR2_GSTR2A() {
		return diffEligible_SGST_GSTR2_GSTR2A;
	}

	public void setDiffEligible_SGST_GSTR2_GSTR2A(double diffEligible_SGST_GSTR2_GSTR2A) {
		this.diffEligible_SGST_GSTR2_GSTR2A = diffEligible_SGST_GSTR2_GSTR2A;
	}

	public double getDiffEligible_IGST_GSTR2_GSTR2A() {
		return diffEligible_IGST_GSTR2_GSTR2A;
	}

	public void setDiffEligible_IGST_GSTR2_GSTR2A(double diffEligible_IGST_GSTR2_GSTR2A) {
		this.diffEligible_IGST_GSTR2_GSTR2A = diffEligible_IGST_GSTR2_GSTR2A;
	}

	public double getDiffIneligible_GSTR2_GSTR2A() {
		return diffIneligible_GSTR2_GSTR2A;
	}

	public void setDiffIneligible_GSTR2_GSTR2A(double diffIneligible_GSTR2_GSTR2A) {
		this.diffIneligible_GSTR2_GSTR2A = diffIneligible_GSTR2_GSTR2A;
	}

	public double getDiffIneligible_CGST_GSTR2_GSTR2A() {
		return diffIneligible_CGST_GSTR2_GSTR2A;
	}

	public void setDiffIneligible_CGST_GSTR2_GSTR2A(double diffIneligible_CGST_GSTR2_GSTR2A) {
		this.diffIneligible_CGST_GSTR2_GSTR2A = diffIneligible_CGST_GSTR2_GSTR2A;
	}

	public double getDiffIneligible_SGST_GSTR2_GSTR2A() {
		return diffIneligible_SGST_GSTR2_GSTR2A;
	}

	public void setDiffIneligible_SGST_GSTR2_GSTR2A(double diffIneligible_SGST_GSTR2_GSTR2A) {
		this.diffIneligible_SGST_GSTR2_GSTR2A = diffIneligible_SGST_GSTR2_GSTR2A;
	}

	public double getDiffIneligible_IGST_GSTR2_GSTR2A() {
		return diffIneligible_IGST_GSTR2_GSTR2A;
	}

	public void setDiffIneligible_IGST_GSTR2_GSTR2A(double diffIneligible_IGST_GSTR2_GSTR2A) {
		this.diffIneligible_IGST_GSTR2_GSTR2A = diffIneligible_IGST_GSTR2_GSTR2A;
	}

	public double getImportGoods_IGST_GSTR3B() {
		return importGoods_IGST_GSTR3B;
	}

	public void setImportGoods_IGST_GSTR3B(double importGoods_IGST_GSTR3B) {
		this.importGoods_IGST_GSTR3B = importGoods_IGST_GSTR3B;
	}

	public double getImportServices_IGST_GSTR3B() {
		return importServices_IGST_GSTR3B;
	}

	public void setImportServices_IGST_GSTR3B(double importServices_IGST_GSTR3B) {
		this.importServices_IGST_GSTR3B = importServices_IGST_GSTR3B;
	}

	public double getRcm_CGST_GSTR3B() {
		return rcm_CGST_GSTR3B;
	}

	public void setRcm_CGST_GSTR3B(double rcm_CGST_GSTR3B) {
		this.rcm_CGST_GSTR3B = rcm_CGST_GSTR3B;
	}

	public double getRcm_SGST_GSTR3B() {
		return rcm_SGST_GSTR3B;
	}

	public void setRcm_SGST_GSTR3B(double rcm_SGST_GSTR3B) {
		this.rcm_SGST_GSTR3B = rcm_SGST_GSTR3B;
	}

	public double getRcm_IGST_GSTR3B() {
		return rcm_IGST_GSTR3B;
	}

	public void setRcm_IGST_GSTR3B(double rcm_IGST_GSTR3B) {
		this.rcm_IGST_GSTR3B = rcm_IGST_GSTR3B;
	}

	public double getIsd_CGST_GSTR3B() {
		return isd_CGST_GSTR3B;
	}

	public void setIsd_CGST_GSTR3B(double isd_CGST_GSTR3B) {
		this.isd_CGST_GSTR3B = isd_CGST_GSTR3B;
	}

	public double getIsd_SGST_GSTR3B() {
		return isd_SGST_GSTR3B;
	}

	public void setIsd_SGST_GSTR3B(double isd_SGST_GSTR3B) {
		this.isd_SGST_GSTR3B = isd_SGST_GSTR3B;
	}

	public double getIsd_IGST_GSTR3B() {
		return isd_IGST_GSTR3B;
	}

	public void setIsd_IGST_GSTR3B(double isd_IGST_GSTR3B) {
		this.isd_IGST_GSTR3B = isd_IGST_GSTR3B;
	}

	public double getEligible_CGST_GSTR3B() {
		return eligible_CGST_GSTR3B;
	}

	public void setEligible_CGST_GSTR3B(double eligible_CGST_GSTR3B) {
		this.eligible_CGST_GSTR3B = eligible_CGST_GSTR3B;
	}

	public double getEligible_SGST_GSTR3B() {
		return eligible_SGST_GSTR3B;
	}

	public void setEligible_SGST_GSTR3B(double eligible_SGST_GSTR3B) {
		this.eligible_SGST_GSTR3B = eligible_SGST_GSTR3B;
	}

	public double getEligible_IGST_GSTR3B() {
		return eligible_IGST_GSTR3B;
	}

	public void setEligible_IGST_GSTR3B(double eligible_IGST_GSTR3B) {
		this.eligible_IGST_GSTR3B = eligible_IGST_GSTR3B;
	}

	public double getIneligible_CGST_GSTR3B() {
		return ineligible_CGST_GSTR3B;
	}

	public void setIneligible_CGST_GSTR3B(double ineligible_CGST_GSTR3B) {
		this.ineligible_CGST_GSTR3B = ineligible_CGST_GSTR3B;
	}

	public double getIneligible_SGST_GSTR3B() {
		return ineligible_SGST_GSTR3B;
	}

	public void setIneligible_SGST_GSTR3B(double ineligible_SGST_GSTR3B) {
		this.ineligible_SGST_GSTR3B = ineligible_SGST_GSTR3B;
	}

	public double getIneligible_IGST_GSTR3B() {
		return ineligible_IGST_GSTR3B;
	}

	public void setIneligible_IGST_GSTR3B(double ineligible_IGST_GSTR3B) {
		this.ineligible_IGST_GSTR3B = ineligible_IGST_GSTR3B;
	}

	public double getCredit_Reversed_CGST_GSTR3B() {
		return credit_Reversed_CGST_GSTR3B;
	}

	public void setCredit_Reversed_CGST_GSTR3B(double credit_Reversed_CGST_GSTR3B) {
		this.credit_Reversed_CGST_GSTR3B = credit_Reversed_CGST_GSTR3B;
	}

	public double getCredit_Reversed_SGST_GSTR3B() {
		return credit_Reversed_SGST_GSTR3B;
	}

	public void setCredit_Reversed_SGST_GSTR3B(double credit_Reversed_SGST_GSTR3B) {
		this.credit_Reversed_SGST_GSTR3B = credit_Reversed_SGST_GSTR3B;
	}

	public double getCredit_Reversed_IGST_GSTR3B() {
		return credit_Reversed_IGST_GSTR3B;
	}

	public void setCredit_Reversed_IGST_GSTR3B(double credit_Reversed_IGST_GSTR3B) {
		this.credit_Reversed_IGST_GSTR3B = credit_Reversed_IGST_GSTR3B;
	}

	public double getImportGoods_IGST_GSTR2A() {
		return importGoods_IGST_GSTR2A;
	}

	public void setImportGoods_IGST_GSTR2A(double importGoods_IGST_GSTR2A) {
		this.importGoods_IGST_GSTR2A = importGoods_IGST_GSTR2A;
	}

	public double getImportServices_IGST_GSTR2A() {
		return importServices_IGST_GSTR2A;
	}

	public void setImportServices_IGST_GSTR2A(double importServices_IGST_GSTR2A) {
		this.importServices_IGST_GSTR2A = importServices_IGST_GSTR2A;
	}

	public double getRcm_CGST_GSTR2A() {
		return rcm_CGST_GSTR2A;
	}

	public void setRcm_CGST_GSTR2A(double rcm_CGST_GSTR2A) {
		this.rcm_CGST_GSTR2A = rcm_CGST_GSTR2A;
	}

	public double getRcm_SGST_GSTR2A() {
		return rcm_SGST_GSTR2A;
	}

	public void setRcm_SGST_GSTR2A(double rcm_SGST_GSTR2A) {
		this.rcm_SGST_GSTR2A = rcm_SGST_GSTR2A;
	}

	public double getRcm_IGST_GSTR2A() {
		return rcm_IGST_GSTR2A;
	}

	public void setRcm_IGST_GSTR2A(double rcm_IGST_GSTR2A) {
		this.rcm_IGST_GSTR2A = rcm_IGST_GSTR2A;
	}

	public double getIsd_CGST_GSTR2A() {
		return isd_CGST_GSTR2A;
	}

	public void setIsd_CGST_GSTR2A(double isd_CGST_GSTR2A) {
		this.isd_CGST_GSTR2A = isd_CGST_GSTR2A;
	}

	public double getIsd_SGST_GSTR2A() {
		return isd_SGST_GSTR2A;
	}

	public void setIsd_SGST_GSTR2A(double isd_SGST_GSTR2A) {
		this.isd_SGST_GSTR2A = isd_SGST_GSTR2A;
	}

	public double getIsd_IGST_GSTR2A() {
		return isd_IGST_GSTR2A;
	}

	public void setIsd_IGST_GSTR2A(double isd_IGST_GSTR2A) {
		this.isd_IGST_GSTR2A = isd_IGST_GSTR2A;
	}

	public double getEligible_CGST_GSTR2A() {
		return eligible_CGST_GSTR2A;
	}

	public void setEligible_CGST_GSTR2A(double eligible_CGST_GSTR2A) {
		this.eligible_CGST_GSTR2A = eligible_CGST_GSTR2A;
	}

	public double getEligible_SGST_GSTR2A() {
		return eligible_SGST_GSTR2A;
	}

	public void setEligible_SGST_GSTR2A(double eligible_SGST_GSTR2A) {
		this.eligible_SGST_GSTR2A = eligible_SGST_GSTR2A;
	}

	public double getEligible_IGST_GSTR2A() {
		return eligible_IGST_GSTR2A;
	}

	public void setEligible_IGST_GSTR2A(double eligible_IGST_GSTR2A) {
		this.eligible_IGST_GSTR2A = eligible_IGST_GSTR2A;
	}

	public double getIneligible_CGST_GSTR2A() {
		return ineligible_CGST_GSTR2A;
	}

	public void setIneligible_CGST_GSTR2A(double ineligible_CGST_GSTR2A) {
		this.ineligible_CGST_GSTR2A = ineligible_CGST_GSTR2A;
	}

	public double getIneligible_SGST_GSTR2A() {
		return ineligible_SGST_GSTR2A;
	}

	public void setIneligible_SGST_GSTR2A(double ineligible_SGST_GSTR2A) {
		this.ineligible_SGST_GSTR2A = ineligible_SGST_GSTR2A;
	}

	public double getIneligible_IGST_GSTR2A() {
		return ineligible_IGST_GSTR2A;
	}

	public void setIneligible_IGST_GSTR2A(double ineligible_IGST_GSTR2A) {
		this.ineligible_IGST_GSTR2A = ineligible_IGST_GSTR2A;
	}

	public double getCredit_Reversed_CGST_GSTR2A() {
		return credit_Reversed_CGST_GSTR2A;
	}

	public void setCredit_Reversed_CGST_GSTR2A(double credit_Reversed_CGST_GSTR2A) {
		this.credit_Reversed_CGST_GSTR2A = credit_Reversed_CGST_GSTR2A;
	}

	public double getCredit_Reversed_SGST_GSTR2A() {
		return credit_Reversed_SGST_GSTR2A;
	}

	public void setCredit_Reversed_SGST_GSTR2A(double credit_Reversed_SGST_GSTR2A) {
		this.credit_Reversed_SGST_GSTR2A = credit_Reversed_SGST_GSTR2A;
	}

	public double getCredit_Reversed_IGST_GSTR2A() {
		return credit_Reversed_IGST_GSTR2A;
	}

	public void setCredit_Reversed_IGST_GSTR2A(double credit_Reversed_IGST_GSTR2A) {
		this.credit_Reversed_IGST_GSTR2A = credit_Reversed_IGST_GSTR2A;
	}

	public double getImportGoods_IGST_GSTR2() {
		return importGoods_IGST_GSTR2;
	}

	public void setImportGoods_IGST_GSTR2(double importGoods_IGST_GSTR2) {
		this.importGoods_IGST_GSTR2 = importGoods_IGST_GSTR2;
	}

	public double getImportServices_IGST_GSTR2() {
		return importServices_IGST_GSTR2;
	}

	public void setImportServices_IGST_GSTR2(double importServices_IGST_GSTR2) {
		this.importServices_IGST_GSTR2 = importServices_IGST_GSTR2;
	}

	public double getRcm_CGST_GSTR2() {
		return rcm_CGST_GSTR2;
	}

	public void setRcm_CGST_GSTR2(double rcm_CGST_GSTR2) {
		this.rcm_CGST_GSTR2 = rcm_CGST_GSTR2;
	}

	public double getRcm_SGST_GSTR2() {
		return rcm_SGST_GSTR2;
	}

	public void setRcm_SGST_GSTR2(double rcm_SGST_GSTR2) {
		this.rcm_SGST_GSTR2 = rcm_SGST_GSTR2;
	}

	public double getRcm_IGST_GSTR2() {
		return rcm_IGST_GSTR2;
	}

	public void setRcm_IGST_GSTR2(double rcm_IGST_GSTR2) {
		this.rcm_IGST_GSTR2 = rcm_IGST_GSTR2;
	}

	public double getIsd_CGST_GSTR2() {
		return isd_CGST_GSTR2;
	}

	public void setIsd_CGST_GSTR2(double isd_CGST_GSTR2) {
		this.isd_CGST_GSTR2 = isd_CGST_GSTR2;
	}

	public double getIsd_SGST_GSTR2() {
		return isd_SGST_GSTR2;
	}

	public void setIsd_SGST_GSTR2(double isd_SGST_GSTR2) {
		this.isd_SGST_GSTR2 = isd_SGST_GSTR2;
	}

	public double getIsd_IGST_GSTR2() {
		return isd_IGST_GSTR2;
	}

	public void setIsd_IGST_GSTR2(double isd_IGST_GSTR2) {
		this.isd_IGST_GSTR2 = isd_IGST_GSTR2;
	}

	public double getEligible_CGST_GSTR2() {
		return eligible_CGST_GSTR2;
	}

	public void setEligible_CGST_GSTR2(double eligible_CGST_GSTR2) {
		this.eligible_CGST_GSTR2 = eligible_CGST_GSTR2;
	}

	public double getEligible_SGST_GSTR2() {
		return eligible_SGST_GSTR2;
	}

	public void setEligible_SGST_GSTR2(double eligible_SGST_GSTR2) {
		this.eligible_SGST_GSTR2 = eligible_SGST_GSTR2;
	}

	public double getEligible_IGST_GSTR2() {
		return eligible_IGST_GSTR2;
	}

	public void setEligible_IGST_GSTR2(double eligible_IGST_GSTR2) {
		this.eligible_IGST_GSTR2 = eligible_IGST_GSTR2;
	}

	public double getIneligible_CGST_GSTR2() {
		return ineligible_CGST_GSTR2;
	}

	public void setIneligible_CGST_GSTR2(double ineligible_CGST_GSTR2) {
		this.ineligible_CGST_GSTR2 = ineligible_CGST_GSTR2;
	}

	public double getIneligible_SGST_GSTR2() {
		return ineligible_SGST_GSTR2;
	}

	public void setIneligible_SGST_GSTR2(double ineligible_SGST_GSTR2) {
		this.ineligible_SGST_GSTR2 = ineligible_SGST_GSTR2;
	}

	public double getIneligible_IGST_GSTR2() {
		return ineligible_IGST_GSTR2;
	}

	public void setIneligible_IGST_GSTR2(double ineligible_IGST_GSTR2) {
		this.ineligible_IGST_GSTR2 = ineligible_IGST_GSTR2;
	}

	public double getCredit_Reversed_CGST_GSTR2() {
		return credit_Reversed_CGST_GSTR2;
	}

	public void setCredit_Reversed_CGST_GSTR2(double credit_Reversed_CGST_GSTR2) {
		this.credit_Reversed_CGST_GSTR2 = credit_Reversed_CGST_GSTR2;
	}

	public double getCredit_Reversed_SGST_GSTR2() {
		return credit_Reversed_SGST_GSTR2;
	}

	public void setCredit_Reversed_SGST_GSTR2(double credit_Reversed_SGST_GSTR2) {
		this.credit_Reversed_SGST_GSTR2 = credit_Reversed_SGST_GSTR2;
	}

	public double getCredit_Reversed_IGST_GSTR2() {
		return credit_Reversed_IGST_GSTR2;
	}

	public void setCredit_Reversed_IGST_GSTR2(double credit_Reversed_IGST_GSTR2) {
		this.credit_Reversed_IGST_GSTR2 = credit_Reversed_IGST_GSTR2;
	}

	public double getRcm_GSTR3B() {
		return rcm_GSTR3B;
	}

	public void setRcm_GSTR3B(double rcm_GSTR3B) {
		this.rcm_GSTR3B = rcm_GSTR3B;
	}

	public double getIsd_GSTR3B() {
		return isd_GSTR3B;
	}

	public void setIsd_GSTR3B(double isd_GSTR3B) {
		this.isd_GSTR3B = isd_GSTR3B;
	}

	public double getCredit_Reversed_GSTR3B() {
		return credit_Reversed_GSTR3B;
	}

	public void setCredit_Reversed_GSTR3B(double credit_Reversed_GSTR3B) {
		this.credit_Reversed_GSTR3B = credit_Reversed_GSTR3B;
	}

	public double getRcm_GSTR2A() {
		return rcm_GSTR2A;
	}

	public void setRcm_GSTR2A(double rcm_GSTR2A) {
		this.rcm_GSTR2A = rcm_GSTR2A;
	}

	public double getIsd_GSTR2A() {
		return isd_GSTR2A;
	}

	public void setIsd_GSTR2A(double isd_GSTR2A) {
		this.isd_GSTR2A = isd_GSTR2A;
	}

	public double getCredit_Reversed_GSTR2A() {
		return credit_Reversed_GSTR2A;
	}

	public void setCredit_Reversed_GSTR2A(double credit_Reversed_GSTR2A) {
		this.credit_Reversed_GSTR2A = credit_Reversed_GSTR2A;
	}

	public double getRcm_GSTR2() {
		return rcm_GSTR2;
	}

	public void setRcm_GSTR2(double rcm_GSTR2) {
		this.rcm_GSTR2 = rcm_GSTR2;
	}

	public double getIsd_GSTR2() {
		return isd_GSTR2;
	}

	public void setIsd_GSTR2(double isd_GSTR2) {
		this.isd_GSTR2 = isd_GSTR2;
	}

	public double getCredit_Reversed_GSTR2() {
		return credit_Reversed_GSTR2;
	}

	public void setCredit_Reversed_GSTR2(double credit_Reversed_GSTR2) {
		this.credit_Reversed_GSTR2 = credit_Reversed_GSTR2;
	}

	public double getEligible_GSTR3B() {
		return eligible_GSTR3B;
	}

	public void setEligible_GSTR3B(double eligible_GSTR3B) {
		this.eligible_GSTR3B = eligible_GSTR3B;
	}

	public double getIneligible_GSTR3B() {
		return ineligible_GSTR3B;
	}

	public void setIneligible_GSTR3B(double ineligible_GSTR3B) {
		this.ineligible_GSTR3B = ineligible_GSTR3B;
	}

	public double getEligible_GSTR2A() {
		return eligible_GSTR2A;
	}

	public void setEligible_GSTR2A(double eligible_GSTR2A) {
		this.eligible_GSTR2A = eligible_GSTR2A;
	}

	public double getIneligible_GSTR2A() {
		return ineligible_GSTR2A;
	}

	public void setIneligible_GSTR2A(double ineligible_GSTR2A) {
		this.ineligible_GSTR2A = ineligible_GSTR2A;
	}

	public double getEligible_GSTR2() {
		return eligible_GSTR2;
	}

	public void setEligible_GSTR2(double eligible_GSTR2) {
		this.eligible_GSTR2 = eligible_GSTR2;
	}

	public double getIneligible_GSTR2() {
		return ineligible_GSTR2;
	}

	public void setIneligible_GSTR2(double ineligible_GSTR2) {
		this.ineligible_GSTR2 = ineligible_GSTR2;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public double getDiffImportGoods_IGST_GSTR2_GSTR3B() {
		return diffImportGoods_IGST_GSTR2_GSTR3B;
	}

	public void setDiffImportGoods_IGST_GSTR2_GSTR3B(double diffImportGoods_IGST_GSTR2_GSTR3B) {
		this.diffImportGoods_IGST_GSTR2_GSTR3B = diffImportGoods_IGST_GSTR2_GSTR3B;
	}

	public double getDiffImportServices_IGST_GSTR2_GSTR3B() {
		return diffImportServices_IGST_GSTR2_GSTR3B;
	}

	public void setDiffImportServices_IGST_GSTR2_GSTR3B(double diffImportServices_IGST_GSTR2_GSTR3B) {
		this.diffImportServices_IGST_GSTR2_GSTR3B = diffImportServices_IGST_GSTR2_GSTR3B;
	}

	public double getDiffRCM_GSTR2_GSTR3B() {
		return diffRCM_GSTR2_GSTR3B;
	}

	public void setDiffRCM_GSTR2_GSTR3B(double diffRCM_GSTR2_GSTR3B) {
		this.diffRCM_GSTR2_GSTR3B = diffRCM_GSTR2_GSTR3B;
	}

	public double getDiffRCM_IGST_GSTR2_GSTR3B() {
		return diffRCM_IGST_GSTR2_GSTR3B;
	}

	public void setDiffRCM_IGST_GSTR2_GSTR3B(double diffRCM_IGST_GSTR2_GSTR3B) {
		this.diffRCM_IGST_GSTR2_GSTR3B = diffRCM_IGST_GSTR2_GSTR3B;
	}

	public double getDiffRCM_SGST_GSTR2_GSTR3B() {
		return diffRCM_SGST_GSTR2_GSTR3B;
	}

	public void setDiffRCM_SGST_GSTR2_GSTR3B(double diffRCM_SGST_GSTR2_GSTR3B) {
		this.diffRCM_SGST_GSTR2_GSTR3B = diffRCM_SGST_GSTR2_GSTR3B;
	}

	public double getDiffISD_GSTR2_GSTR3B() {
		return diffISD_GSTR2_GSTR3B;
	}

	public void setDiffISD_GSTR2_GSTR3B(double diffISD_GSTR2_GSTR3B) {
		this.diffISD_GSTR2_GSTR3B = diffISD_GSTR2_GSTR3B;
	}

	public double getDiffISD_IGST_GSTR2_GSTR3B() {
		return diffISD_IGST_GSTR2_GSTR3B;
	}

	public void setDiffISD_IGST_GSTR2_GSTR3B(double diffISD_IGST_GSTR2_GSTR3B) {
		this.diffISD_IGST_GSTR2_GSTR3B = diffISD_IGST_GSTR2_GSTR3B;
	}

	public double getDiffISD_SGST_GSTR2_GSTR3B() {
		return diffISD_SGST_GSTR2_GSTR3B;
	}

	public void setDiffISD_SGST_GSTR2_GSTR3B(double diffISD_SGST_GSTR2_GSTR3B) {
		this.diffISD_SGST_GSTR2_GSTR3B = diffISD_SGST_GSTR2_GSTR3B;
	}

	public double getDiffCredit_Reversed_GSTR2_GSTR3B() {
		return diffCredit_Reversed_GSTR2_GSTR3B;
	}

	public void setDiffCredit_Reversed_GSTR2_GSTR3B(double diffCredit_Reversed_GSTR2_GSTR3B) {
		this.diffCredit_Reversed_GSTR2_GSTR3B = diffCredit_Reversed_GSTR2_GSTR3B;
	}

	public double getDiffCredit_Reversed_IGST_GSTR2_GSTR3B() {
		return diffCredit_Reversed_IGST_GSTR2_GSTR3B;
	}

	public void setDiffCredit_Reversed_IGST_GSTR2_GSTR3B(double diffCredit_Reversed_IGST_GSTR2_GSTR3B) {
		this.diffCredit_Reversed_IGST_GSTR2_GSTR3B = diffCredit_Reversed_IGST_GSTR2_GSTR3B;
	}

	public double getDiffEligible_GSTR2_GSTR3B() {
		return diffEligible_GSTR2_GSTR3B;
	}

	public void setDiffEligible_GSTR2_GSTR3B(double diffEligible_GSTR2_GSTR3B) {
		this.diffEligible_GSTR2_GSTR3B = diffEligible_GSTR2_GSTR3B;
	}

	public double getDiffEligible_CGST_GSTR2_GSTR3B() {
		return diffEligible_CGST_GSTR2_GSTR3B;
	}

	public void setDiffEligible_CGST_GSTR2_GSTR3B(double diffEligible_CGST_GSTR2_GSTR3B) {
		this.diffEligible_CGST_GSTR2_GSTR3B = diffEligible_CGST_GSTR2_GSTR3B;
	}

	public double getDiffEligible_SGST_GSTR2_GSTR3B() {
		return diffEligible_SGST_GSTR2_GSTR3B;
	}

	public void setDiffEligible_SGST_GSTR2_GSTR3B(double diffEligible_SGST_GSTR2_GSTR3B) {
		this.diffEligible_SGST_GSTR2_GSTR3B = diffEligible_SGST_GSTR2_GSTR3B;
	}

	public double getDiffEligible_IGST_GSTR2_GSTR3B() {
		return diffEligible_IGST_GSTR2_GSTR3B;
	}

	public void setDiffEligible_IGST_GSTR2_GSTR3B(double diffEligible_IGST_GSTR2_GSTR3B) {
		this.diffEligible_IGST_GSTR2_GSTR3B = diffEligible_IGST_GSTR2_GSTR3B;
	}

	public double getDiffIneligible_GSTR2_GSTR3B() {
		return diffIneligible_GSTR2_GSTR3B;
	}

	public void setDiffIneligible_GSTR2_GSTR3B(double diffIneligible_GSTR2_GSTR3B) {
		this.diffIneligible_GSTR2_GSTR3B = diffIneligible_GSTR2_GSTR3B;
	}

	public double getDiffIneligible_CGST_GSTR2_GSTR3B() {
		return diffIneligible_CGST_GSTR2_GSTR3B;
	}

	public void setDiffIneligible_CGST_GSTR2_GSTR3B(double diffIneligible_CGST_GSTR2_GSTR3B) {
		this.diffIneligible_CGST_GSTR2_GSTR3B = diffIneligible_CGST_GSTR2_GSTR3B;
	}

	public double getDiffIneligible_SGST_GSTR2_GSTR3B() {
		return diffIneligible_SGST_GSTR2_GSTR3B;
	}

	public void setDiffIneligible_SGST_GSTR2_GSTR3B(double diffIneligible_SGST_GSTR2_GSTR3B) {
		this.diffIneligible_SGST_GSTR2_GSTR3B = diffIneligible_SGST_GSTR2_GSTR3B;
	}

	public double getDiffIneligible_IGST_GSTR2_GSTR3B() {
		return diffIneligible_IGST_GSTR2_GSTR3B;
	}

	public void setDiffIneligible_IGST_GSTR2_GSTR3B(double diffIneligible_IGST_GSTR2_GSTR3B) {
		this.diffIneligible_IGST_GSTR2_GSTR3B = diffIneligible_IGST_GSTR2_GSTR3B;
	}

	public double getDiffRCM_CGST_GSTR2_GSTR3B() {
		return diffRCM_CGST_GSTR2_GSTR3B;
	}

	public void setDiffRCM_CGST_GSTR2_GSTR3B(double diffRCM_CGST_GSTR2_GSTR3B) {
		this.diffRCM_CGST_GSTR2_GSTR3B = diffRCM_CGST_GSTR2_GSTR3B;
	}

	public double getDiffISD_CGST_GSTR2_GSTR3B() {
		return diffISD_CGST_GSTR2_GSTR3B;
	}

	public void setDiffISD_CGST_GSTR2_GSTR3B(double diffISD_CGST_GSTR2_GSTR3B) {
		this.diffISD_CGST_GSTR2_GSTR3B = diffISD_CGST_GSTR2_GSTR3B;
	}

	public double getDiffCredit_Reversed_CGST_GSTR2_GSTR3B() {
		return diffCredit_Reversed_CGST_GSTR2_GSTR3B;
	}

	public void setDiffCredit_Reversed_CGST_GSTR2_GSTR3B(double diffCredit_Reversed_CGST_GSTR2_GSTR3B) {
		this.diffCredit_Reversed_CGST_GSTR2_GSTR3B = diffCredit_Reversed_CGST_GSTR2_GSTR3B;
	}

	public double getDiffCredit_Reversed_SGST_GSTR2_GSTR3B() {
		return diffCredit_Reversed_SGST_GSTR2_GSTR3B;
	}

	public void setDiffCredit_Reversed_SGST_GSTR2_GSTR3B(double diffCredit_Reversed_SGST_GSTR2_GSTR3B) {
		this.diffCredit_Reversed_SGST_GSTR2_GSTR3B = diffCredit_Reversed_SGST_GSTR2_GSTR3B;
	}

	public String getSummaryyear() {
		return summaryyear;
	}

	public void setSummaryyear(String summaryyear) {
		this.summaryyear = summaryyear;
	}

	public double getDiffImportGoods_IGST_GSTR2_GSTR2A() {
		return diffImportGoods_IGST_GSTR2_GSTR2A;
	}

	public void setDiffImportGoods_IGST_GSTR2_GSTR2A(double diffImportGoods_IGST_GSTR2_GSTR2A) {
		this.diffImportGoods_IGST_GSTR2_GSTR2A = diffImportGoods_IGST_GSTR2_GSTR2A;
	}

	public double getDiffImportServices_IGST_GSTR2_GSTR2A() {
		return diffImportServices_IGST_GSTR2_GSTR2A;
	}

	public void setDiffImportServices_IGST_GSTR2_GSTR2A(double diffImportServices_IGST_GSTR2_GSTR2A) {
		this.diffImportServices_IGST_GSTR2_GSTR2A = diffImportServices_IGST_GSTR2_GSTR2A;
	}

	public double getDiffImportGoods_IGST_GSTR3B_GSTR2A() {
		return diffImportGoods_IGST_GSTR3B_GSTR2A;
	}

	public void setDiffImportGoods_IGST_GSTR3B_GSTR2A(double diffImportGoods_IGST_GSTR3B_GSTR2A) {
		this.diffImportGoods_IGST_GSTR3B_GSTR2A = diffImportGoods_IGST_GSTR3B_GSTR2A;
	}

	public double getDiffImportServices_IGST_GSTR3B_GSTR2A() {
		return diffImportServices_IGST_GSTR3B_GSTR2A;
	}

	public void setDiffImportServices_IGST_GSTR3B_GSTR2A(double diffImportServices_IGST_GSTR3B_GSTR2A) {
		this.diffImportServices_IGST_GSTR3B_GSTR2A = diffImportServices_IGST_GSTR3B_GSTR2A;
	}
	
	
}
