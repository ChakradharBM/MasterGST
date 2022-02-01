package com.mastergst.usermanagement.runtime.domain;

/**
 * @author sh
 *
 */
public class GSTR3B_VS_GSTR1 {

	private String month;
	private String summaryyear;
	private double GSTR3B_3a_Taxableamt;
	private double GSTR3B_3a_Taxamt;

	private double GSTR3B_3b_Taxableamt;
	private double GSTR3B_3b_Taxamt;

	private double GSTR3B_3c_Taxableamt;
	private double GSTR3B_3c_Taxamt;

	private double GSTR3B_3d_Taxableamt;
	private double GSTR3B_3d_Taxamt;

	private double GSTR3B_3e_Taxableamt;
	private double GSTR3B_3e_Taxamt;

	private double GSTR3B_Total_Taxableamt;
	private double GSTR3B_Total_Taxamt;
	private double GSTR3B_IGST;
	private double GSTR3B_CGST_SGST;
	private double GSTR3B_Cess;

	private double GSTR3B_Total_Taxableamt1;
	private double GSTR3B_Total_Taxableamt2;
	private double GSTR3B_Total_Taxamt1;
	private double GSTR3B_Total_Taxamt2;

	/**
	 * GSTR1 Books variables
	 */
	private double b2b_R_CBW_Taxableamount;
	private double b2b_R_CBW_Taxamount;
	private double b2cl_R_CBW_Taxableamount;
	private double b2cl_R_CBW_Taxamount;

	private double b2b_CL_DE_Taxableamount;
	private double b2b_CL_DE_Taxamount;

	private double b2b_CL_SEWP_Taxableamount;
	private double b2b_CL_SEWP_Taxamount;
	private double b2b_CL_SEWOP_Taxableamount;
	private double b2b_CL_SEWOP_Taxamount;

	private double exports_WP_Taxableamount;
	private double exports_WP_Taxamount;
	private double exports_WOP_Taxableamount;
	private double exports_WOP_Taxamount;

	private double nilrated_Exempted_Taxableamount;
	private double nilrated_Exempted_Taxamount;

	private double nongst_Taxableamount;
	private double nongst_Taxamount;

	private double gstr1_Total_IGST;
	private double gstr1_Total_CGST_SGST;

	private double rcm_Taxableamount;
	private double rcm_Taxamount;

	private double book_inter_Taxableamount;
	private double book_inter_Taxamount;

	private double book_intra_Taxableamount;
	private double book_intra_Taxamount;

	private double book_Total_Taxableamt1;
	private double book_Total_Taxableamt2;
	private double book_Total_Taxamt1;
	private double book_Total_Taxamt2;

	private double diffBook_GSTR3B_Taxableamount_Total1;
	private double diffBook_GSTR3B_Taxamount_Total1;
	private double diffBook_GSTR3B_Taxableamount_Total2;
	private double diffBook_GSTR3B_Taxamount_Total2;

	private double diffBook_GSTR3B_Total_Taxableamount1;
	private double diffBook_GSTR3B_Total_Taxamount1;
	private double diffBook_GSTR3B_Total_Taxableamount2;
	private double diffBook_GSTR3B_Total_Taxamount2;

	private double diffBook_GSTR3B_Nilrated_Exempted_Taxableamount;
	private double diffBook_GSTR3B_Nilrated_Exempted_Taxamount;
	private double diffBook_GSTR3B_NonGST_Taxableamount;
	private double diffBook_GSTR3B_NonGST_Taxamount;

	private double diffBook_GSTR3B_Inter_Taxableamount;
	private double diffBook_GSTR3B_Inter_Taxamount;

	private double diffBook_GSTR3B_Intra_Taxableamount;
	private double diffBook_GSTR3B_Intra_Taxamount;

	/**
	 * GSTR1 Variable (Like as isAmendment=true)
	 */
	private double gstr1b2b_R_CBW_Taxableamount;
	private double gstr1b2b_R_CBW_Taxamount;
	private double gstr1b2cl_R_CBW_Taxableamount;
	private double gstr1b2cl_R_CBW_Taxamount;

	private double gstr1b2b_CL_DE_Taxableamount;
	private double gstr1b2b_CL_DE_Taxamount;

	private double gstr1b2b_CL_SEWP_Taxableamount;
	private double gstr1b2b_CL_SEWP_Taxamount;
	private double gstr1b2b_CL_SEWOP_Taxableamount;
	private double gstr1b2b_CL_SEWOP_Taxamount;

	private double gstr1exports_WP_Taxableamount;
	private double gstr1exports_WP_Taxamount;
	private double gstr1exports_WOP_Taxableamount;
	private double gstr1exports_WOP_Taxamount;

	private double gstr1nilrated_Exempted_Taxableamount;
	private double gstr1nilrated_Exempted_Taxamount;

	private double gstr1nongst_Taxableamount;
	private double gstr1nongst_Taxamount;

	private double gstr1gstr1_Total_IGST;
	private double gstr1gstr1_Total_CGST_SGST;

	private double gstr1rcm_Taxableamount;
	private double gstr1rcm_Taxamount;

	private double gstr1_inter_Taxableamount;
	private double gstr1_inter_Taxamount;

	private double gstr1_intra_Taxableamount;
	private double gstr1_intra_Taxamount;

	private double gstr1_Total_Taxableamt1;
	private double gstr1_Total_Taxableamt2;
	private double gstr1_Total_Taxamt1;
	private double gstr1_Total_Taxamt2;
	// GSTR3B-GSTR1 variables
	private double diffGSTR1_GSTR3B_Taxableamount_Total1;
	private double diffGSTR1_GSTR3B_Taxamount_Total1;
	private double diffGSTR1_GSTR3B_Taxableamount_Total2;
	private double diffGSTR1_GSTR3B_Taxamount_Total2;

	private double diffGSTR1_GSTR3B_Total_Taxableamount1;
	private double diffGSTR1_GSTR3B_Total_Taxamount1;
	private double diffGSTR1_GSTR3B_Total_Taxableamount2;
	private double diffGSTR1_GSTR3B_Total_Taxamount2;

	private double diffGSTR1_GSTR3B_Nilrated_Exempted_Taxableamount;
	private double diffGSTR1_GSTR3B_Nilrated_Exempted_Taxamount;
	private double diffGSTR1_GSTR3B_NonGST_Taxableamount;
	private double diffGSTR1_GSTR3B_NonGST_Taxamount;

	private double diffGSTR1_GSTR3B_Inter_Taxableamount;
	private double diffGSTR1_GSTR3B_Inter_Taxamount;

	private double diffGSTR1_GSTR3B_Intra_Taxableamount;
	private double diffGSTR1_GSTR3B_Intra_Taxamount;

	// Books-GSTR1 variables
	private double diffBook_GSTR1_B2B_Taxableamount;
	private double diffBook_GSTR1_B2B_Taxamount;
	private double diffBook_GSTR1_B2C_Taxableamount;
	private double diffBook_GSTR1_B2C_Taxamount;

	private double diffBook_GSTR1_Deemed_Taxableamount;
	private double diffBook_GSTR1_Deemed_Taxamount;
	private double diffBook_GSTR1_RCM_Taxableamount;
	private double diffBook_GSTR1_RCM_Taxamount;
	private double diffBook_GSTR1_SEZWP_Taxableamount;
	private double diffBook_GSTR1_SEZWP_Taxamount;

	private double diffBook_GSTR1_SEZWOP_Taxableamount;
	private double diffBook_GSTR1_SEZWOP_Taxamount;
	private double diffBook_GSTR1_Export_WP_Taxableamount;
	private double diffBook_GSTR1_Export_WP_Taxamount;
	private double diffBook_GSTR1_Export_WOP_Taxableamount;
	private double diffBook_GSTR1_Export_WOP_Taxamount;

	private double diffBook_GSTR1_Nil_Taxableamount;
	private double diffBook_GSTR1_Nil_Taxamount;
	private double diffBook_GSTR1_NonGst_Taxableamount;
	private double diffBook_GSTR1_NonGst_Taxamount;
	private double diffBook_GSTR1_Inter_Taxableamount;
	private double diffBook_GSTR1_Inter_Taxamount;
	private double diffBook_GSTR1_Intra_Taxableamount;
	private double diffBook_GSTR1_Intra_Taxamount;

	private double diffBook_GSTR1_Total_Taxableamount1;
	private double diffBook_GSTR1_Total_Taxamount1;
	private double diffBook_GSTR1_Total_Taxableamount2;
	private double diffBook_GSTR1_Total_Taxamount2;
	
	public double getGSTR3B_Total_Taxamt() {
		return GSTR3B_Total_Taxamt;
	}

	public void setGSTR3B_Total_Taxamt(double gSTR3B_Total_Taxamt) {
		GSTR3B_Total_Taxamt = gSTR3B_Total_Taxamt;
	}

	public double getDiffBook_GSTR1_Total_Taxableamount1() {
		return diffBook_GSTR1_Total_Taxableamount1;
	}

	public void setDiffBook_GSTR1_Total_Taxableamount1(double diffBook_GSTR1_Total_Taxableamount1) {
		this.diffBook_GSTR1_Total_Taxableamount1 = diffBook_GSTR1_Total_Taxableamount1;
	}

	public double getDiffBook_GSTR1_Total_Taxamount1() {
		return diffBook_GSTR1_Total_Taxamount1;
	}

	public void setDiffBook_GSTR1_Total_Taxamount1(double diffBook_GSTR1_Total_Taxamount1) {
		this.diffBook_GSTR1_Total_Taxamount1 = diffBook_GSTR1_Total_Taxamount1;
	}

	public double getDiffBook_GSTR1_Total_Taxableamount2() {
		return diffBook_GSTR1_Total_Taxableamount2;
	}

	public void setDiffBook_GSTR1_Total_Taxableamount2(double diffBook_GSTR1_Total_Taxableamount2) {
		this.diffBook_GSTR1_Total_Taxableamount2 = diffBook_GSTR1_Total_Taxableamount2;
	}

	public double getDiffBook_GSTR1_Total_Taxamount2() {
		return diffBook_GSTR1_Total_Taxamount2;
	}

	public void setDiffBook_GSTR1_Total_Taxamount2(double diffBook_GSTR1_Total_Taxamount2) {
		this.diffBook_GSTR1_Total_Taxamount2 = diffBook_GSTR1_Total_Taxamount2;
	}

	public double getGSTR3B_3a_Taxableamt() {
		return GSTR3B_3a_Taxableamt;
	}

	public void setGSTR3B_3a_Taxableamt(double gSTR3B_3a_Taxableamt) {
		GSTR3B_3a_Taxableamt = gSTR3B_3a_Taxableamt;
	}

	public double getGSTR3B_3a_Taxamt() {
		return GSTR3B_3a_Taxamt;
	}

	public void setGSTR3B_3a_Taxamt(double gSTR3B_3a_Taxamt) {
		GSTR3B_3a_Taxamt = gSTR3B_3a_Taxamt;
	}

	public double getGSTR3B_3b_Taxableamt() {
		return GSTR3B_3b_Taxableamt;
	}

	public void setGSTR3B_3b_Taxableamt(double gSTR3B_3b_Taxableamt) {
		GSTR3B_3b_Taxableamt = gSTR3B_3b_Taxableamt;
	}

	public double getGSTR3B_3b_Taxamt() {
		return GSTR3B_3b_Taxamt;
	}

	public void setGSTR3B_3b_Taxamt(double gSTR3B_3b_Taxamt) {
		GSTR3B_3b_Taxamt = gSTR3B_3b_Taxamt;
	}

	public double getGSTR3B_3c_Taxableamt() {
		return GSTR3B_3c_Taxableamt;
	}

	public void setGSTR3B_3c_Taxableamt(double gSTR3B_3c_Taxableamt) {
		GSTR3B_3c_Taxableamt = gSTR3B_3c_Taxableamt;
	}

	public double getGSTR3B_3c_Taxamt() {
		return GSTR3B_3c_Taxamt;
	}

	public void setGSTR3B_3c_Taxamt(double gSTR3B_3c_Taxamt) {
		GSTR3B_3c_Taxamt = gSTR3B_3c_Taxamt;
	}

	public double getGSTR3B_3d_Taxableamt() {
		return GSTR3B_3d_Taxableamt;
	}

	public void setGSTR3B_3d_Taxableamt(double gSTR3B_3d_Taxableamt) {
		GSTR3B_3d_Taxableamt = gSTR3B_3d_Taxableamt;
	}

	public double getGSTR3B_3d_Taxamt() {
		return GSTR3B_3d_Taxamt;
	}

	public void setGSTR3B_3d_Taxamt(double gSTR3B_3d_Taxamt) {
		GSTR3B_3d_Taxamt = gSTR3B_3d_Taxamt;
	}

	public double getGSTR3B_3e_Taxableamt() {
		return GSTR3B_3e_Taxableamt;
	}

	public void setGSTR3B_3e_Taxableamt(double gSTR3B_3e_Taxableamt) {
		GSTR3B_3e_Taxableamt = gSTR3B_3e_Taxableamt;
	}

	public double getGSTR3B_3e_Taxamt() {
		return GSTR3B_3e_Taxamt;
	}

	public void setGSTR3B_3e_Taxamt(double gSTR3B_3e_Taxamt) {
		GSTR3B_3e_Taxamt = gSTR3B_3e_Taxamt;
	}

	public double getGSTR3B_Total_Taxableamt() {
		return GSTR3B_Total_Taxableamt;
	}

	public void setGSTR3B_Total_Taxableamt(double gSTR3B_Total_Taxableamt) {
		GSTR3B_Total_Taxableamt = gSTR3B_Total_Taxableamt;
	}

	public double getGSTR3B_IGST() {
		return GSTR3B_IGST;
	}

	public void setGSTR3B_IGST(double gSTR3B_IGST) {
		GSTR3B_IGST = gSTR3B_IGST;
	}

	public double getGSTR3B_CGST_SGST() {
		return GSTR3B_CGST_SGST;
	}

	public void setGSTR3B_CGST_SGST(double gSTR3B_CGST_SGST) {
		GSTR3B_CGST_SGST = gSTR3B_CGST_SGST;
	}

	public double getGSTR3B_Cess() {
		return GSTR3B_Cess;
	}

	public void setGSTR3B_Cess(double gSTR3B_Cess) {
		GSTR3B_Cess = gSTR3B_Cess;
	}

	public double getGSTR3B_Total_Taxableamt1() {
		return GSTR3B_Total_Taxableamt1;
	}

	public void setGSTR3B_Total_Taxableamt1(double gSTR3B_Total_Taxableamt1) {
		GSTR3B_Total_Taxableamt1 = gSTR3B_Total_Taxableamt1;
	}

	public double getGSTR3B_Total_Taxableamt2() {
		return GSTR3B_Total_Taxableamt2;
	}

	public void setGSTR3B_Total_Taxableamt2(double gSTR3B_Total_Taxableamt2) {
		GSTR3B_Total_Taxableamt2 = gSTR3B_Total_Taxableamt2;
	}

	public double getGSTR3B_Total_Taxamt1() {
		return GSTR3B_Total_Taxamt1;
	}

	public void setGSTR3B_Total_Taxamt1(double gSTR3B_Total_Taxamt1) {
		GSTR3B_Total_Taxamt1 = gSTR3B_Total_Taxamt1;
	}

	public double getGSTR3B_Total_Taxamt2() {
		return GSTR3B_Total_Taxamt2;
	}

	public void setGSTR3B_Total_Taxamt2(double gSTR3B_Total_Taxamt2) {
		GSTR3B_Total_Taxamt2 = gSTR3B_Total_Taxamt2;
	}

	public double getB2b_R_CBW_Taxableamount() {
		return b2b_R_CBW_Taxableamount;
	}

	public void setB2b_R_CBW_Taxableamount(double b2b_R_CBW_Taxableamount) {
		this.b2b_R_CBW_Taxableamount = b2b_R_CBW_Taxableamount;
	}

	public double getB2b_R_CBW_Taxamount() {
		return b2b_R_CBW_Taxamount;
	}

	public void setB2b_R_CBW_Taxamount(double b2b_R_CBW_Taxamount) {
		this.b2b_R_CBW_Taxamount = b2b_R_CBW_Taxamount;
	}

	public double getB2cl_R_CBW_Taxableamount() {
		return b2cl_R_CBW_Taxableamount;
	}

	public void setB2cl_R_CBW_Taxableamount(double b2cl_R_CBW_Taxableamount) {
		this.b2cl_R_CBW_Taxableamount = b2cl_R_CBW_Taxableamount;
	}

	public double getB2cl_R_CBW_Taxamount() {
		return b2cl_R_CBW_Taxamount;
	}

	public void setB2cl_R_CBW_Taxamount(double b2cl_R_CBW_Taxamount) {
		this.b2cl_R_CBW_Taxamount = b2cl_R_CBW_Taxamount;
	}

	public double getB2b_CL_DE_Taxableamount() {
		return b2b_CL_DE_Taxableamount;
	}

	public void setB2b_CL_DE_Taxableamount(double b2b_CL_DE_Taxableamount) {
		this.b2b_CL_DE_Taxableamount = b2b_CL_DE_Taxableamount;
	}

	public double getB2b_CL_DE_Taxamount() {
		return b2b_CL_DE_Taxamount;
	}

	public void setB2b_CL_DE_Taxamount(double b2b_CL_DE_Taxamount) {
		this.b2b_CL_DE_Taxamount = b2b_CL_DE_Taxamount;
	}

	public double getB2b_CL_SEWP_Taxableamount() {
		return b2b_CL_SEWP_Taxableamount;
	}

	public void setB2b_CL_SEWP_Taxableamount(double b2b_CL_SEWP_Taxableamount) {
		this.b2b_CL_SEWP_Taxableamount = b2b_CL_SEWP_Taxableamount;
	}

	public double getB2b_CL_SEWP_Taxamount() {
		return b2b_CL_SEWP_Taxamount;
	}

	public void setB2b_CL_SEWP_Taxamount(double b2b_CL_SEWP_Taxamount) {
		this.b2b_CL_SEWP_Taxamount = b2b_CL_SEWP_Taxamount;
	}

	public double getB2b_CL_SEWOP_Taxableamount() {
		return b2b_CL_SEWOP_Taxableamount;
	}

	public void setB2b_CL_SEWOP_Taxableamount(double b2b_CL_SEWOP_Taxableamount) {
		this.b2b_CL_SEWOP_Taxableamount = b2b_CL_SEWOP_Taxableamount;
	}

	public double getB2b_CL_SEWOP_Taxamount() {
		return b2b_CL_SEWOP_Taxamount;
	}

	public void setB2b_CL_SEWOP_Taxamount(double b2b_CL_SEWOP_Taxamount) {
		this.b2b_CL_SEWOP_Taxamount = b2b_CL_SEWOP_Taxamount;
	}

	public double getExports_WP_Taxableamount() {
		return exports_WP_Taxableamount;
	}

	public void setExports_WP_Taxableamount(double exports_WP_Taxableamount) {
		this.exports_WP_Taxableamount = exports_WP_Taxableamount;
	}

	public double getExports_WP_Taxamount() {
		return exports_WP_Taxamount;
	}

	public void setExports_WP_Taxamount(double exports_WP_Taxamount) {
		this.exports_WP_Taxamount = exports_WP_Taxamount;
	}

	public double getExports_WOP_Taxableamount() {
		return exports_WOP_Taxableamount;
	}

	public void setExports_WOP_Taxableamount(double exports_WOP_Taxableamount) {
		this.exports_WOP_Taxableamount = exports_WOP_Taxableamount;
	}

	public double getExports_WOP_Taxamount() {
		return exports_WOP_Taxamount;
	}

	public void setExports_WOP_Taxamount(double exports_WOP_Taxamount) {
		this.exports_WOP_Taxamount = exports_WOP_Taxamount;
	}

	public double getNilrated_Exempted_Taxableamount() {
		return nilrated_Exempted_Taxableamount;
	}

	public void setNilrated_Exempted_Taxableamount(double nilrated_Exempted_Taxableamount) {
		this.nilrated_Exempted_Taxableamount = nilrated_Exempted_Taxableamount;
	}

	public double getNilrated_Exempted_Taxamount() {
		return nilrated_Exempted_Taxamount;
	}

	public void setNilrated_Exempted_Taxamount(double nilrated_Exempted_Taxamount) {
		this.nilrated_Exempted_Taxamount = nilrated_Exempted_Taxamount;
	}

	public double getNongst_Taxableamount() {
		return nongst_Taxableamount;
	}

	public void setNongst_Taxableamount(double nongst_Taxableamount) {
		this.nongst_Taxableamount = nongst_Taxableamount;
	}

	public double getNongst_Taxamount() {
		return nongst_Taxamount;
	}

	public void setNongst_Taxamount(double nongst_Taxamount) {
		this.nongst_Taxamount = nongst_Taxamount;
	}

	public double getGstr1_Total_IGST() {
		return gstr1_Total_IGST;
	}

	public void setGstr1_Total_IGST(double gstr1_Total_IGST) {
		this.gstr1_Total_IGST = gstr1_Total_IGST;
	}

	public double getGstr1_Total_CGST_SGST() {
		return gstr1_Total_CGST_SGST;
	}

	public void setGstr1_Total_CGST_SGST(double gstr1_Total_CGST_SGST) {
		this.gstr1_Total_CGST_SGST = gstr1_Total_CGST_SGST;
	}

	public double getRcm_Taxableamount() {
		return rcm_Taxableamount;
	}

	public void setRcm_Taxableamount(double rcm_Taxableamount) {
		this.rcm_Taxableamount = rcm_Taxableamount;
	}

	public double getRcm_Taxamount() {
		return rcm_Taxamount;
	}

	public void setRcm_Taxamount(double rcm_Taxamount) {
		this.rcm_Taxamount = rcm_Taxamount;
	}

	public double getBook_inter_Taxableamount() {
		return book_inter_Taxableamount;
	}

	public void setBook_inter_Taxableamount(double book_inter_Taxableamount) {
		this.book_inter_Taxableamount = book_inter_Taxableamount;
	}

	public double getBook_inter_Taxamount() {
		return book_inter_Taxamount;
	}

	public void setBook_inter_Taxamount(double book_inter_Taxamount) {
		this.book_inter_Taxamount = book_inter_Taxamount;
	}

	public double getBook_intra_Taxableamount() {
		return book_intra_Taxableamount;
	}

	public void setBook_intra_Taxableamount(double book_intra_Taxableamount) {
		this.book_intra_Taxableamount = book_intra_Taxableamount;
	}

	public double getBook_intra_Taxamount() {
		return book_intra_Taxamount;
	}

	public void setBook_intra_Taxamount(double book_intra_Taxamount) {
		this.book_intra_Taxamount = book_intra_Taxamount;
	}

	public double getBook_Total_Taxableamt1() {
		return book_Total_Taxableamt1;
	}

	public void setBook_Total_Taxableamt1(double book_Total_Taxableamt1) {
		this.book_Total_Taxableamt1 = book_Total_Taxableamt1;
	}

	public double getBook_Total_Taxableamt2() {
		return book_Total_Taxableamt2;
	}

	public void setBook_Total_Taxableamt2(double book_Total_Taxableamt2) {
		this.book_Total_Taxableamt2 = book_Total_Taxableamt2;
	}

	public double getBook_Total_Taxamt1() {
		return book_Total_Taxamt1;
	}

	public void setBook_Total_Taxamt1(double book_Total_Taxamt1) {
		this.book_Total_Taxamt1 = book_Total_Taxamt1;
	}

	public double getBook_Total_Taxamt2() {
		return book_Total_Taxamt2;
	}

	public void setBook_Total_Taxamt2(double book_Total_Taxamt2) {
		this.book_Total_Taxamt2 = book_Total_Taxamt2;
	}

	public double getDiffBook_GSTR3B_Taxableamount_Total1() {
		return diffBook_GSTR3B_Taxableamount_Total1;
	}

	public void setDiffBook_GSTR3B_Taxableamount_Total1(double diffBook_GSTR3B_Taxableamount_Total1) {
		this.diffBook_GSTR3B_Taxableamount_Total1 = diffBook_GSTR3B_Taxableamount_Total1;
	}

	public double getDiffBook_GSTR3B_Taxamount_Total1() {
		return diffBook_GSTR3B_Taxamount_Total1;
	}

	public void setDiffBook_GSTR3B_Taxamount_Total1(double diffBook_GSTR3B_Taxamount_Total1) {
		this.diffBook_GSTR3B_Taxamount_Total1 = diffBook_GSTR3B_Taxamount_Total1;
	}

	public double getDiffBook_GSTR3B_Taxableamount_Total2() {
		return diffBook_GSTR3B_Taxableamount_Total2;
	}

	public void setDiffBook_GSTR3B_Taxableamount_Total2(double diffBook_GSTR3B_Taxableamount_Total2) {
		this.diffBook_GSTR3B_Taxableamount_Total2 = diffBook_GSTR3B_Taxableamount_Total2;
	}

	public double getDiffBook_GSTR3B_Taxamount_Total2() {
		return diffBook_GSTR3B_Taxamount_Total2;
	}

	public void setDiffBook_GSTR3B_Taxamount_Total2(double diffBook_GSTR3B_Taxamount_Total2) {
		this.diffBook_GSTR3B_Taxamount_Total2 = diffBook_GSTR3B_Taxamount_Total2;
	}

	public double getDiffBook_GSTR3B_Total_Taxableamount1() {
		return diffBook_GSTR3B_Total_Taxableamount1;
	}

	public void setDiffBook_GSTR3B_Total_Taxableamount1(double diffBook_GSTR3B_Total_Taxableamount1) {
		this.diffBook_GSTR3B_Total_Taxableamount1 = diffBook_GSTR3B_Total_Taxableamount1;
	}

	public double getDiffBook_GSTR3B_Total_Taxamount1() {
		return diffBook_GSTR3B_Total_Taxamount1;
	}

	public void setDiffBook_GSTR3B_Total_Taxamount1(double diffBook_GSTR3B_Total_Taxamount1) {
		this.diffBook_GSTR3B_Total_Taxamount1 = diffBook_GSTR3B_Total_Taxamount1;
	}

	public double getDiffBook_GSTR3B_Total_Taxableamount2() {
		return diffBook_GSTR3B_Total_Taxableamount2;
	}

	public void setDiffBook_GSTR3B_Total_Taxableamount2(double diffBook_GSTR3B_Total_Taxableamount2) {
		this.diffBook_GSTR3B_Total_Taxableamount2 = diffBook_GSTR3B_Total_Taxableamount2;
	}

	public double getDiffBook_GSTR3B_Total_Taxamount2() {
		return diffBook_GSTR3B_Total_Taxamount2;
	}

	public void setDiffBook_GSTR3B_Total_Taxamount2(double diffBook_GSTR3B_Total_Taxamount2) {
		this.diffBook_GSTR3B_Total_Taxamount2 = diffBook_GSTR3B_Total_Taxamount2;
	}

	public double getDiffBook_GSTR3B_Nilrated_Exempted_Taxableamount() {
		return diffBook_GSTR3B_Nilrated_Exempted_Taxableamount;
	}

	public void setDiffBook_GSTR3B_Nilrated_Exempted_Taxableamount(
			double diffBook_GSTR3B_Nilrated_Exempted_Taxableamount) {
		this.diffBook_GSTR3B_Nilrated_Exempted_Taxableamount = diffBook_GSTR3B_Nilrated_Exempted_Taxableamount;
	}

	public double getDiffBook_GSTR3B_Nilrated_Exempted_Taxamount() {
		return diffBook_GSTR3B_Nilrated_Exempted_Taxamount;
	}

	public void setDiffBook_GSTR3B_Nilrated_Exempted_Taxamount(double diffBook_GSTR3B_Nilrated_Exempted_Taxamount) {
		this.diffBook_GSTR3B_Nilrated_Exempted_Taxamount = diffBook_GSTR3B_Nilrated_Exempted_Taxamount;
	}

	public double getDiffBook_GSTR3B_NonGST_Taxableamount() {
		return diffBook_GSTR3B_NonGST_Taxableamount;
	}

	public void setDiffBook_GSTR3B_NonGST_Taxableamount(double diffBook_GSTR3B_NonGST_Taxableamount) {
		this.diffBook_GSTR3B_NonGST_Taxableamount = diffBook_GSTR3B_NonGST_Taxableamount;
	}

	public double getDiffBook_GSTR3B_NonGST_Taxamount() {
		return diffBook_GSTR3B_NonGST_Taxamount;
	}

	public void setDiffBook_GSTR3B_NonGST_Taxamount(double diffBook_GSTR3B_NonGST_Taxamount) {
		this.diffBook_GSTR3B_NonGST_Taxamount = diffBook_GSTR3B_NonGST_Taxamount;
	}

	public double getDiffBook_GSTR3B_Inter_Taxableamount() {
		return diffBook_GSTR3B_Inter_Taxableamount;
	}

	public void setDiffBook_GSTR3B_Inter_Taxableamount(double diffBook_GSTR3B_Inter_Taxableamount) {
		this.diffBook_GSTR3B_Inter_Taxableamount = diffBook_GSTR3B_Inter_Taxableamount;
	}

	public double getDiffBook_GSTR3B_Inter_Taxamount() {
		return diffBook_GSTR3B_Inter_Taxamount;
	}

	public void setDiffBook_GSTR3B_Inter_Taxamount(double diffBook_GSTR3B_Inter_Taxamount) {
		this.diffBook_GSTR3B_Inter_Taxamount = diffBook_GSTR3B_Inter_Taxamount;
	}

	public double getDiffBook_GSTR3B_Intra_Taxableamount() {
		return diffBook_GSTR3B_Intra_Taxableamount;
	}

	public void setDiffBook_GSTR3B_Intra_Taxableamount(double diffBook_GSTR3B_Intra_Taxableamount) {
		this.diffBook_GSTR3B_Intra_Taxableamount = diffBook_GSTR3B_Intra_Taxableamount;
	}

	public double getDiffBook_GSTR3B_Intra_Taxamount() {
		return diffBook_GSTR3B_Intra_Taxamount;
	}

	public void setDiffBook_GSTR3B_Intra_Taxamount(double diffBook_GSTR3B_Intra_Taxamount) {
		this.diffBook_GSTR3B_Intra_Taxamount = diffBook_GSTR3B_Intra_Taxamount;
	}

	public double getGstr1b2b_R_CBW_Taxableamount() {
		return gstr1b2b_R_CBW_Taxableamount;
	}

	public void setGstr1b2b_R_CBW_Taxableamount(double gstr1b2b_R_CBW_Taxableamount) {
		this.gstr1b2b_R_CBW_Taxableamount = gstr1b2b_R_CBW_Taxableamount;
	}

	public double getGstr1b2b_R_CBW_Taxamount() {
		return gstr1b2b_R_CBW_Taxamount;
	}

	public void setGstr1b2b_R_CBW_Taxamount(double gstr1b2b_R_CBW_Taxamount) {
		this.gstr1b2b_R_CBW_Taxamount = gstr1b2b_R_CBW_Taxamount;
	}

	public double getGstr1b2cl_R_CBW_Taxableamount() {
		return gstr1b2cl_R_CBW_Taxableamount;
	}

	public void setGstr1b2cl_R_CBW_Taxableamount(double gstr1b2cl_R_CBW_Taxableamount) {
		this.gstr1b2cl_R_CBW_Taxableamount = gstr1b2cl_R_CBW_Taxableamount;
	}

	public double getGstr1b2cl_R_CBW_Taxamount() {
		return gstr1b2cl_R_CBW_Taxamount;
	}

	public void setGstr1b2cl_R_CBW_Taxamount(double gstr1b2cl_R_CBW_Taxamount) {
		this.gstr1b2cl_R_CBW_Taxamount = gstr1b2cl_R_CBW_Taxamount;
	}

	public double getGstr1b2b_CL_DE_Taxableamount() {
		return gstr1b2b_CL_DE_Taxableamount;
	}

	public void setGstr1b2b_CL_DE_Taxableamount(double gstr1b2b_CL_DE_Taxableamount) {
		this.gstr1b2b_CL_DE_Taxableamount = gstr1b2b_CL_DE_Taxableamount;
	}

	public double getGstr1b2b_CL_DE_Taxamount() {
		return gstr1b2b_CL_DE_Taxamount;
	}

	public void setGstr1b2b_CL_DE_Taxamount(double gstr1b2b_CL_DE_Taxamount) {
		this.gstr1b2b_CL_DE_Taxamount = gstr1b2b_CL_DE_Taxamount;
	}

	public double getGstr1b2b_CL_SEWP_Taxableamount() {
		return gstr1b2b_CL_SEWP_Taxableamount;
	}

	public void setGstr1b2b_CL_SEWP_Taxableamount(double gstr1b2b_CL_SEWP_Taxableamount) {
		this.gstr1b2b_CL_SEWP_Taxableamount = gstr1b2b_CL_SEWP_Taxableamount;
	}

	public double getGstr1b2b_CL_SEWP_Taxamount() {
		return gstr1b2b_CL_SEWP_Taxamount;
	}

	public void setGstr1b2b_CL_SEWP_Taxamount(double gstr1b2b_CL_SEWP_Taxamount) {
		this.gstr1b2b_CL_SEWP_Taxamount = gstr1b2b_CL_SEWP_Taxamount;
	}

	public double getGstr1b2b_CL_SEWOP_Taxableamount() {
		return gstr1b2b_CL_SEWOP_Taxableamount;
	}

	public void setGstr1b2b_CL_SEWOP_Taxableamount(double gstr1b2b_CL_SEWOP_Taxableamount) {
		this.gstr1b2b_CL_SEWOP_Taxableamount = gstr1b2b_CL_SEWOP_Taxableamount;
	}

	public double getGstr1b2b_CL_SEWOP_Taxamount() {
		return gstr1b2b_CL_SEWOP_Taxamount;
	}

	public void setGstr1b2b_CL_SEWOP_Taxamount(double gstr1b2b_CL_SEWOP_Taxamount) {
		this.gstr1b2b_CL_SEWOP_Taxamount = gstr1b2b_CL_SEWOP_Taxamount;
	}

	public double getGstr1exports_WP_Taxableamount() {
		return gstr1exports_WP_Taxableamount;
	}

	public void setGstr1exports_WP_Taxableamount(double gstr1exports_WP_Taxableamount) {
		this.gstr1exports_WP_Taxableamount = gstr1exports_WP_Taxableamount;
	}

	public double getGstr1exports_WP_Taxamount() {
		return gstr1exports_WP_Taxamount;
	}

	public void setGstr1exports_WP_Taxamount(double gstr1exports_WP_Taxamount) {
		this.gstr1exports_WP_Taxamount = gstr1exports_WP_Taxamount;
	}

	public double getGstr1exports_WOP_Taxableamount() {
		return gstr1exports_WOP_Taxableamount;
	}

	public void setGstr1exports_WOP_Taxableamount(double gstr1exports_WOP_Taxableamount) {
		this.gstr1exports_WOP_Taxableamount = gstr1exports_WOP_Taxableamount;
	}

	public double getGstr1exports_WOP_Taxamount() {
		return gstr1exports_WOP_Taxamount;
	}

	public void setGstr1exports_WOP_Taxamount(double gstr1exports_WOP_Taxamount) {
		this.gstr1exports_WOP_Taxamount = gstr1exports_WOP_Taxamount;
	}

	public double getGstr1nilrated_Exempted_Taxableamount() {
		return gstr1nilrated_Exempted_Taxableamount;
	}

	public void setGstr1nilrated_Exempted_Taxableamount(double gstr1nilrated_Exempted_Taxableamount) {
		this.gstr1nilrated_Exempted_Taxableamount = gstr1nilrated_Exempted_Taxableamount;
	}

	public double getGstr1nilrated_Exempted_Taxamount() {
		return gstr1nilrated_Exempted_Taxamount;
	}

	public void setGstr1nilrated_Exempted_Taxamount(double gstr1nilrated_Exempted_Taxamount) {
		this.gstr1nilrated_Exempted_Taxamount = gstr1nilrated_Exempted_Taxamount;
	}

	public double getGstr1nongst_Taxableamount() {
		return gstr1nongst_Taxableamount;
	}

	public void setGstr1nongst_Taxableamount(double gstr1nongst_Taxableamount) {
		this.gstr1nongst_Taxableamount = gstr1nongst_Taxableamount;
	}

	public double getGstr1nongst_Taxamount() {
		return gstr1nongst_Taxamount;
	}

	public void setGstr1nongst_Taxamount(double gstr1nongst_Taxamount) {
		this.gstr1nongst_Taxamount = gstr1nongst_Taxamount;
	}

	public double getGstr1gstr1_Total_IGST() {
		return gstr1gstr1_Total_IGST;
	}

	public void setGstr1gstr1_Total_IGST(double gstr1gstr1_Total_IGST) {
		this.gstr1gstr1_Total_IGST = gstr1gstr1_Total_IGST;
	}

	public double getGstr1gstr1_Total_CGST_SGST() {
		return gstr1gstr1_Total_CGST_SGST;
	}

	public void setGstr1gstr1_Total_CGST_SGST(double gstr1gstr1_Total_CGST_SGST) {
		this.gstr1gstr1_Total_CGST_SGST = gstr1gstr1_Total_CGST_SGST;
	}

	public double getGstr1rcm_Taxableamount() {
		return gstr1rcm_Taxableamount;
	}

	public void setGstr1rcm_Taxableamount(double gstr1rcm_Taxableamount) {
		this.gstr1rcm_Taxableamount = gstr1rcm_Taxableamount;
	}

	public double getGstr1rcm_Taxamount() {
		return gstr1rcm_Taxamount;
	}

	public void setGstr1rcm_Taxamount(double gstr1rcm_Taxamount) {
		this.gstr1rcm_Taxamount = gstr1rcm_Taxamount;
	}

	public double getGstr1_inter_Taxableamount() {
		return gstr1_inter_Taxableamount;
	}

	public void setGstr1_inter_Taxableamount(double gstr1_inter_Taxableamount) {
		this.gstr1_inter_Taxableamount = gstr1_inter_Taxableamount;
	}

	public double getGstr1_inter_Taxamount() {
		return gstr1_inter_Taxamount;
	}

	public void setGstr1_inter_Taxamount(double gstr1_inter_Taxamount) {
		this.gstr1_inter_Taxamount = gstr1_inter_Taxamount;
	}

	public double getGstr1_intra_Taxableamount() {
		return gstr1_intra_Taxableamount;
	}

	public void setGstr1_intra_Taxableamount(double gstr1_intra_Taxableamount) {
		this.gstr1_intra_Taxableamount = gstr1_intra_Taxableamount;
	}

	public double getGstr1_intra_Taxamount() {
		return gstr1_intra_Taxamount;
	}

	public void setGstr1_intra_Taxamount(double gstr1_intra_Taxamount) {
		this.gstr1_intra_Taxamount = gstr1_intra_Taxamount;
	}

	public double getGstr1_Total_Taxableamt1() {
		return gstr1_Total_Taxableamt1;
	}

	public void setGstr1_Total_Taxableamt1(double gstr1_Total_Taxableamt1) {
		this.gstr1_Total_Taxableamt1 = gstr1_Total_Taxableamt1;
	}

	public double getGstr1_Total_Taxableamt2() {
		return gstr1_Total_Taxableamt2;
	}

	public void setGstr1_Total_Taxableamt2(double gstr1_Total_Taxableamt2) {
		this.gstr1_Total_Taxableamt2 = gstr1_Total_Taxableamt2;
	}

	public double getGstr1_Total_Taxamt1() {
		return gstr1_Total_Taxamt1;
	}

	public void setGstr1_Total_Taxamt1(double gstr1_Total_Taxamt1) {
		this.gstr1_Total_Taxamt1 = gstr1_Total_Taxamt1;
	}

	public double getGstr1_Total_Taxamt2() {
		return gstr1_Total_Taxamt2;
	}

	public void setGstr1_Total_Taxamt2(double gstr1_Total_Taxamt2) {
		this.gstr1_Total_Taxamt2 = gstr1_Total_Taxamt2;
	}

	public double getDiffGSTR1_GSTR3B_Taxableamount_Total1() {
		return diffGSTR1_GSTR3B_Taxableamount_Total1;
	}

	public void setDiffGSTR1_GSTR3B_Taxableamount_Total1(double diffGSTR1_GSTR3B_Taxableamount_Total1) {
		this.diffGSTR1_GSTR3B_Taxableamount_Total1 = diffGSTR1_GSTR3B_Taxableamount_Total1;
	}

	public double getDiffGSTR1_GSTR3B_Taxamount_Total1() {
		return diffGSTR1_GSTR3B_Taxamount_Total1;
	}

	public void setDiffGSTR1_GSTR3B_Taxamount_Total1(double diffGSTR1_GSTR3B_Taxamount_Total1) {
		this.diffGSTR1_GSTR3B_Taxamount_Total1 = diffGSTR1_GSTR3B_Taxamount_Total1;
	}

	public double getDiffGSTR1_GSTR3B_Taxableamount_Total2() {
		return diffGSTR1_GSTR3B_Taxableamount_Total2;
	}

	public void setDiffGSTR1_GSTR3B_Taxableamount_Total2(double diffGSTR1_GSTR3B_Taxableamount_Total2) {
		this.diffGSTR1_GSTR3B_Taxableamount_Total2 = diffGSTR1_GSTR3B_Taxableamount_Total2;
	}

	public double getDiffGSTR1_GSTR3B_Taxamount_Total2() {
		return diffGSTR1_GSTR3B_Taxamount_Total2;
	}

	public void setDiffGSTR1_GSTR3B_Taxamount_Total2(double diffGSTR1_GSTR3B_Taxamount_Total2) {
		this.diffGSTR1_GSTR3B_Taxamount_Total2 = diffGSTR1_GSTR3B_Taxamount_Total2;
	}

	public double getDiffGSTR1_GSTR3B_Total_Taxableamount1() {
		return diffGSTR1_GSTR3B_Total_Taxableamount1;
	}

	public void setDiffGSTR1_GSTR3B_Total_Taxableamount1(double diffGSTR1_GSTR3B_Total_Taxableamount1) {
		this.diffGSTR1_GSTR3B_Total_Taxableamount1 = diffGSTR1_GSTR3B_Total_Taxableamount1;
	}

	public double getDiffGSTR1_GSTR3B_Total_Taxamount1() {
		return diffGSTR1_GSTR3B_Total_Taxamount1;
	}

	public void setDiffGSTR1_GSTR3B_Total_Taxamount1(double diffGSTR1_GSTR3B_Total_Taxamount1) {
		this.diffGSTR1_GSTR3B_Total_Taxamount1 = diffGSTR1_GSTR3B_Total_Taxamount1;
	}

	public double getDiffGSTR1_GSTR3B_Total_Taxableamount2() {
		return diffGSTR1_GSTR3B_Total_Taxableamount2;
	}

	public void setDiffGSTR1_GSTR3B_Total_Taxableamount2(double diffGSTR1_GSTR3B_Total_Taxableamount2) {
		this.diffGSTR1_GSTR3B_Total_Taxableamount2 = diffGSTR1_GSTR3B_Total_Taxableamount2;
	}

	public double getDiffGSTR1_GSTR3B_Total_Taxamount2() {
		return diffGSTR1_GSTR3B_Total_Taxamount2;
	}

	public void setDiffGSTR1_GSTR3B_Total_Taxamount2(double diffGSTR1_GSTR3B_Total_Taxamount2) {
		this.diffGSTR1_GSTR3B_Total_Taxamount2 = diffGSTR1_GSTR3B_Total_Taxamount2;
	}

	public double getDiffGSTR1_GSTR3B_Nilrated_Exempted_Taxableamount() {
		return diffGSTR1_GSTR3B_Nilrated_Exempted_Taxableamount;
	}

	public void setDiffGSTR1_GSTR3B_Nilrated_Exempted_Taxableamount(
			double diffGSTR1_GSTR3B_Nilrated_Exempted_Taxableamount) {
		this.diffGSTR1_GSTR3B_Nilrated_Exempted_Taxableamount = diffGSTR1_GSTR3B_Nilrated_Exempted_Taxableamount;
	}

	public double getDiffGSTR1_GSTR3B_Nilrated_Exempted_Taxamount() {
		return diffGSTR1_GSTR3B_Nilrated_Exempted_Taxamount;
	}

	public void setDiffGSTR1_GSTR3B_Nilrated_Exempted_Taxamount(double diffGSTR1_GSTR3B_Nilrated_Exempted_Taxamount) {
		this.diffGSTR1_GSTR3B_Nilrated_Exempted_Taxamount = diffGSTR1_GSTR3B_Nilrated_Exempted_Taxamount;
	}

	public double getDiffGSTR1_GSTR3B_NonGST_Taxableamount() {
		return diffGSTR1_GSTR3B_NonGST_Taxableamount;
	}

	public void setDiffGSTR1_GSTR3B_NonGST_Taxableamount(double diffGSTR1_GSTR3B_NonGST_Taxableamount) {
		this.diffGSTR1_GSTR3B_NonGST_Taxableamount = diffGSTR1_GSTR3B_NonGST_Taxableamount;
	}

	public double getDiffGSTR1_GSTR3B_NonGST_Taxamount() {
		return diffGSTR1_GSTR3B_NonGST_Taxamount;
	}

	public void setDiffGSTR1_GSTR3B_NonGST_Taxamount(double diffGSTR1_GSTR3B_NonGST_Taxamount) {
		this.diffGSTR1_GSTR3B_NonGST_Taxamount = diffGSTR1_GSTR3B_NonGST_Taxamount;
	}

	public double getDiffGSTR1_GSTR3B_Inter_Taxableamount() {
		return diffGSTR1_GSTR3B_Inter_Taxableamount;
	}

	public void setDiffGSTR1_GSTR3B_Inter_Taxableamount(double diffGSTR1_GSTR3B_Inter_Taxableamount) {
		this.diffGSTR1_GSTR3B_Inter_Taxableamount = diffGSTR1_GSTR3B_Inter_Taxableamount;
	}

	public double getDiffGSTR1_GSTR3B_Inter_Taxamount() {
		return diffGSTR1_GSTR3B_Inter_Taxamount;
	}

	public void setDiffGSTR1_GSTR3B_Inter_Taxamount(double diffGSTR1_GSTR3B_Inter_Taxamount) {
		this.diffGSTR1_GSTR3B_Inter_Taxamount = diffGSTR1_GSTR3B_Inter_Taxamount;
	}

	public double getDiffGSTR1_GSTR3B_Intra_Taxableamount() {
		return diffGSTR1_GSTR3B_Intra_Taxableamount;
	}

	public void setDiffGSTR1_GSTR3B_Intra_Taxableamount(double diffGSTR1_GSTR3B_Intra_Taxableamount) {
		this.diffGSTR1_GSTR3B_Intra_Taxableamount = diffGSTR1_GSTR3B_Intra_Taxableamount;
	}

	public double getDiffGSTR1_GSTR3B_Intra_Taxamount() {
		return diffGSTR1_GSTR3B_Intra_Taxamount;
	}

	public void setDiffGSTR1_GSTR3B_Intra_Taxamount(double diffGSTR1_GSTR3B_Intra_Taxamount) {
		this.diffGSTR1_GSTR3B_Intra_Taxamount = diffGSTR1_GSTR3B_Intra_Taxamount;
	}

	public double getDiffBook_GSTR1_B2B_Taxableamount() {
		return diffBook_GSTR1_B2B_Taxableamount;
	}

	public void setDiffBook_GSTR1_B2B_Taxableamount(double diffBook_GSTR1_B2B_Taxableamount) {
		this.diffBook_GSTR1_B2B_Taxableamount = diffBook_GSTR1_B2B_Taxableamount;
	}

	public double getDiffBook_GSTR1_B2B_Taxamount() {
		return diffBook_GSTR1_B2B_Taxamount;
	}

	public void setDiffBook_GSTR1_B2B_Taxamount(double diffBook_GSTR1_B2B_Taxamount) {
		this.diffBook_GSTR1_B2B_Taxamount = diffBook_GSTR1_B2B_Taxamount;
	}

	public double getDiffBook_GSTR1_B2C_Taxableamount() {
		return diffBook_GSTR1_B2C_Taxableamount;
	}

	public void setDiffBook_GSTR1_B2C_Taxableamount(double diffBook_GSTR1_B2C_Taxableamount) {
		this.diffBook_GSTR1_B2C_Taxableamount = diffBook_GSTR1_B2C_Taxableamount;
	}

	public double getDiffBook_GSTR1_B2C_Taxamount() {
		return diffBook_GSTR1_B2C_Taxamount;
	}

	public void setDiffBook_GSTR1_B2C_Taxamount(double diffBook_GSTR1_B2C_Taxamount) {
		this.diffBook_GSTR1_B2C_Taxamount = diffBook_GSTR1_B2C_Taxamount;
	}

	public double getDiffBook_GSTR1_Deemed_Taxableamount() {
		return diffBook_GSTR1_Deemed_Taxableamount;
	}

	public void setDiffBook_GSTR1_Deemed_Taxableamount(double diffBook_GSTR1_Deemed_Taxableamount) {
		this.diffBook_GSTR1_Deemed_Taxableamount = diffBook_GSTR1_Deemed_Taxableamount;
	}

	public double getDiffBook_GSTR1_Deemed_Taxamount() {
		return diffBook_GSTR1_Deemed_Taxamount;
	}

	public void setDiffBook_GSTR1_Deemed_Taxamount(double diffBook_GSTR1_Deemed_Taxamount) {
		this.diffBook_GSTR1_Deemed_Taxamount = diffBook_GSTR1_Deemed_Taxamount;
	}

	public double getDiffBook_GSTR1_RCM_Taxableamount() {
		return diffBook_GSTR1_RCM_Taxableamount;
	}

	public void setDiffBook_GSTR1_RCM_Taxableamount(double diffBook_GSTR1_RCM_Taxableamount) {
		this.diffBook_GSTR1_RCM_Taxableamount = diffBook_GSTR1_RCM_Taxableamount;
	}

	public double getDiffBook_GSTR1_RCM_Taxamount() {
		return diffBook_GSTR1_RCM_Taxamount;
	}

	public void setDiffBook_GSTR1_RCM_Taxamount(double diffBook_GSTR1_RCM_Taxamount) {
		this.diffBook_GSTR1_RCM_Taxamount = diffBook_GSTR1_RCM_Taxamount;
	}

	public double getDiffBook_GSTR1_SEZWP_Taxableamount() {
		return diffBook_GSTR1_SEZWP_Taxableamount;
	}

	public void setDiffBook_GSTR1_SEZWP_Taxableamount(double diffBook_GSTR1_SEZWP_Taxableamount) {
		this.diffBook_GSTR1_SEZWP_Taxableamount = diffBook_GSTR1_SEZWP_Taxableamount;
	}

	public double getDiffBook_GSTR1_SEZWP_Taxamount() {
		return diffBook_GSTR1_SEZWP_Taxamount;
	}

	public void setDiffBook_GSTR1_SEZWP_Taxamount(double diffBook_GSTR1_SEZWP_Taxamount) {
		this.diffBook_GSTR1_SEZWP_Taxamount = diffBook_GSTR1_SEZWP_Taxamount;
	}

	public double getDiffBook_GSTR1_SEZWOP_Taxableamount() {
		return diffBook_GSTR1_SEZWOP_Taxableamount;
	}

	public void setDiffBook_GSTR1_SEZWOP_Taxableamount(double diffBook_GSTR1_SEZWOP_Taxableamount) {
		this.diffBook_GSTR1_SEZWOP_Taxableamount = diffBook_GSTR1_SEZWOP_Taxableamount;
	}

	public double getDiffBook_GSTR1_SEZWOP_Taxamount() {
		return diffBook_GSTR1_SEZWOP_Taxamount;
	}

	public void setDiffBook_GSTR1_SEZWOP_Taxamount(double diffBook_GSTR1_SEZWOP_Taxamount) {
		this.diffBook_GSTR1_SEZWOP_Taxamount = diffBook_GSTR1_SEZWOP_Taxamount;
	}

	public double getDiffBook_GSTR1_Export_WP_Taxableamount() {
		return diffBook_GSTR1_Export_WP_Taxableamount;
	}

	public void setDiffBook_GSTR1_Export_WP_Taxableamount(double diffBook_GSTR1_Export_WP_Taxableamount) {
		this.diffBook_GSTR1_Export_WP_Taxableamount = diffBook_GSTR1_Export_WP_Taxableamount;
	}

	public double getDiffBook_GSTR1_Export_WP_Taxamount() {
		return diffBook_GSTR1_Export_WP_Taxamount;
	}

	public void setDiffBook_GSTR1_Export_WP_Taxamount(double diffBook_GSTR1_Export_WP_Taxamount) {
		this.diffBook_GSTR1_Export_WP_Taxamount = diffBook_GSTR1_Export_WP_Taxamount;
	}

	public double getDiffBook_GSTR1_Export_WOP_Taxableamount() {
		return diffBook_GSTR1_Export_WOP_Taxableamount;
	}

	public void setDiffBook_GSTR1_Export_WOP_Taxableamount(double diffBook_GSTR1_Export_WOP_Taxableamount) {
		this.diffBook_GSTR1_Export_WOP_Taxableamount = diffBook_GSTR1_Export_WOP_Taxableamount;
	}

	public double getDiffBook_GSTR1_Export_WOP_Taxamount() {
		return diffBook_GSTR1_Export_WOP_Taxamount;
	}

	public void setDiffBook_GSTR1_Export_WOP_Taxamount(double diffBook_GSTR1_Export_WOP_Taxamount) {
		this.diffBook_GSTR1_Export_WOP_Taxamount = diffBook_GSTR1_Export_WOP_Taxamount;
	}

	public double getDiffBook_GSTR1_Nil_Taxableamount() {
		return diffBook_GSTR1_Nil_Taxableamount;
	}

	public void setDiffBook_GSTR1_Nil_Taxableamount(double diffBook_GSTR1_Nil_Taxableamount) {
		this.diffBook_GSTR1_Nil_Taxableamount = diffBook_GSTR1_Nil_Taxableamount;
	}

	public double getDiffBook_GSTR1_Nil_Taxamount() {
		return diffBook_GSTR1_Nil_Taxamount;
	}

	public void setDiffBook_GSTR1_Nil_Taxamount(double diffBook_GSTR1_Nil_Taxamount) {
		this.diffBook_GSTR1_Nil_Taxamount = diffBook_GSTR1_Nil_Taxamount;
	}

	public double getDiffBook_GSTR1_NonGst_Taxableamount() {
		return diffBook_GSTR1_NonGst_Taxableamount;
	}

	public void setDiffBook_GSTR1_NonGst_Taxableamount(double diffBook_GSTR1_NonGst_Taxableamount) {
		this.diffBook_GSTR1_NonGst_Taxableamount = diffBook_GSTR1_NonGst_Taxableamount;
	}

	public double getDiffBook_GSTR1_NonGst_Taxamount() {
		return diffBook_GSTR1_NonGst_Taxamount;
	}

	public void setDiffBook_GSTR1_NonGst_Taxamount(double diffBook_GSTR1_NonGst_Taxamount) {
		this.diffBook_GSTR1_NonGst_Taxamount = diffBook_GSTR1_NonGst_Taxamount;
	}

	public double getDiffBook_GSTR1_Inter_Taxableamount() {
		return diffBook_GSTR1_Inter_Taxableamount;
	}

	public void setDiffBook_GSTR1_Inter_Taxableamount(double diffBook_GSTR1_Inter_Taxableamount) {
		this.diffBook_GSTR1_Inter_Taxableamount = diffBook_GSTR1_Inter_Taxableamount;
	}

	public double getDiffBook_GSTR1_Inter_Taxamount() {
		return diffBook_GSTR1_Inter_Taxamount;
	}

	public void setDiffBook_GSTR1_Inter_Taxamount(double diffBook_GSTR1_Inter_Taxamount) {
		this.diffBook_GSTR1_Inter_Taxamount = diffBook_GSTR1_Inter_Taxamount;
	}

	public double getDiffBook_GSTR1_Intra_Taxableamount() {
		return diffBook_GSTR1_Intra_Taxableamount;
	}

	public void setDiffBook_GSTR1_Intra_Taxableamount(double diffBook_GSTR1_Intra_Taxableamount) {
		this.diffBook_GSTR1_Intra_Taxableamount = diffBook_GSTR1_Intra_Taxableamount;
	}

	public double getDiffBook_GSTR1_Intra_Taxamount() {
		return diffBook_GSTR1_Intra_Taxamount;
	}

	public void setDiffBook_GSTR1_Intra_Taxamount(double diffBook_GSTR1_Intra_Taxamount) {
		this.diffBook_GSTR1_Intra_Taxamount = diffBook_GSTR1_Intra_Taxamount;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getSummaryyear() {
		return summaryyear;
	}

	public void setSummaryyear(String summaryyear) {
		this.summaryyear = summaryyear;
	}
}
