<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Reports</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<style>
	.table td {padding: .5rem;	padding-left: 2rem;	vertical-align: middle!important; border-top: none!important; border-bottom: 1px solid #eee;padding-right:0px!important;}
	.rtable td {border:none!important;}
	a.btn.btn-blue-dark.report.mr-0.pull-right{padding: 5px 24px !important;}
	p {font-size:15px}
</style>
</head>
<body class="body-cls">
<%@include file="/WEB-INF/views/includes/client_header.jsp" %> 
<div class="breadcrumbwrap">
<div class="container">
<div class="row">
        <div class="col-md-12 col-sm-12">
    <ol class="breadcrumb">
      <li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
	<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
      <li class="breadcrumb-item active">Reports</li>
    </ol>
	<div class="retresp"></div>
	</div>
	</div>
	</div>
  </div>
  <!--- breadcrumb end -->
  <div class="db-ca-wrap" >
    <div class="container" style="background: white;">
    <div class="returns_dropdown">
	<div class=" ">&nbsp;</div>
	<div class="card-deck">
  <div class="card col-md-6 col-sm-12 col-lg-6">
    <div class="card-body">
      <h5 class="card-title mt-4 text-center">Invoices</h5>
      <p class="card-text permissionReports-Sales">Sales Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=salesreports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text permissionReports-Purchase">Purchase Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=purchasereports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text permissionReports-Eway_Bill">Eway Bill Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=ewaybillreports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text permissionReports-Einvoice mb-4">E-invoice Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=einvoicereports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
    </div>
  </div>
  <div class="card col-md-6 col-sm-12 col-lg-6">
    <div class="card-body">
      <h5 class="card-title mt-4 text-center">Multi Month Reports</h5>
      <p class="card-text permissionReports-Multi_Month_GSTR1">Multi month GSTR-1 Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=multimonthgstr1Reports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
     <p class="card-text permissionReports-MultiMonthGSTR2A">Multi month GSTR-2A Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=multiMonthreports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
     <p class="card-text">Multi month GSTR-2B Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=multimoth2bReports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
     <p class="card-text mb-4 permissionReports-MultiMonthGSTR3B">Multi month GSTR-3B Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=multimoth3bReports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
    </div>
  </div>
</div>
<div class=" ">&nbsp;</div>
	<div class="card-deck">
  <div class="card col-md-6 col-sm-12 col-lg-6">
    <div class="card-body">
      <h5 class="card-title mt-4 text-center">Compliances Report</h5>
	  <p class="card-text permissionReports-Yearly_Reconcile">Purchase Register & GSTR2A Yearly Reconciliation Report<a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=yearlyRecocileReport" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text">Purchase Register & GSTR2B Yearly Reconciliation Report<a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=yearlyGstr2bRecocileReport" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text permissionReports-Sales_Vs_GSTR3B_Vs_GSTR1_Monthly">Sales vs GSTR-3B vs GSTR-1 Tax Comparison Report (Monthly)<a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=multiMonthreports3BVS1" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <!-- GSTR-3B vs GSTR-1 Tax Comparison Report (Yearly) url year two times mandatory -->
	  <p class="card-text permissionReports-Sales_Vs_GSTR3B_Vs_GSTR1_Yearly">Sales vs GSTR-3B vs GSTR-1 Tax Comparison Report (Yearly)<a href="${contextPath}/multiMonthreports3bvs1yearly/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}/Monthly/${year}" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text permissionReports-Purchases_Vs_GSTR3B_Vs_GSTR2A_Monthly">Purchases vs GSTR-3B vs GSTR-2A (Monthly)<a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=multiMonthreports3BVS2A" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <!-- GSTR-3B vs GSTR-2 Tax Comparison Report (Yearly) url year two times mandatory -->
	  <p class="card-text permissionReports-Purchases_Vs_GSTR3B_Vs_GSTR2A_Yearly">Purchases vs GSTR-3B vs GSTR-2A (Yearly)<a href="${contextPath}/multiMonthreports3BVS2Ayearly/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}/${year}" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <!-- <p class="card-text">GSTR-3B vs GSTR-2A ITC Comparison Report<a href="#" class="btn btn-blue-dark report mr-0 pull-right disable" role="button">View</a></p> -->
	  <p class="card-text permissionReports-Invoice_Wise_GSTR2_Vs_GSTR2A">Invoice Wise GSTR2A vs Purchase Register comparison Report<a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=multiMonthreports2AVS2_invoice" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
      <p class="card-text permissionReports-Supplier_Wise_GSTR2_Vs_GSTR2A">Supplier Wise GSTR2A vs Purchase Register comparison Report<a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=multiMonthreports2AVS2" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
      <p class="card-text mb-4">Invoice Wise GSTR2B vs Purchase Register comparison Report<a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=multiMonthreports2BVS2" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
    </div>
  </div>
  <div class="card col-md-6 col-sm-12 col-lg-6">
    <div class="card-body">
      <h5 class="card-title mt-4 text-center">Summary Reports</h5>
      <p class="card-text permissionReports-Monthly_Tax_and_ITC_summary">Monthly Tax and ITC summary Report<a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=monthlyTaxandItcSummary" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text permissionReports-Filing_Status">GST Filing Status Report<a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=filingStatusReports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <!--<p class="card-text mb-4">GST Filing summary Report<a href="#" class="btn btn-blue-dark report mr-0 pull-right disable" role="button">View</a></p>-->
	  <p class="card-text permissionReports-Suppliers_GST_Filing_summary">Suppliers GST Filing summary Report<a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=suppliercompliance" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text permissionReports-ITC_Claimed">ITC Claimed Report<a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=itcreports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
      <p class="card-text mb-4 permissionReports-ITC_Unclaimed">ITC Unclaimed Report<a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=itc_unclaimedreports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
    </div>
  </div>
</div>
<div class=" ">&nbsp;</div>
	 <div class="card-deck">
  <div class="card col-md-6 col-sm-12 col-lg-6">
    <div class="card-body">
      <h5 class="card-title mt-4 text-center">HSN and Tax Slab Wise Reports</h5>
      <p class="card-text permissionReports-HSN_Wise_Sales" >HSN wise Sales Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=hsnsalesreports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text permissionReports-HSN_Wise_Purchase">HSN wise Purchase Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=hsnpurchasereports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
    <p class="card-text permissionReports-Tax_Slab_Wise_Sales">Tax Slab Wise Sales Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=taxslabwisesalesreports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text permissionReports-Tax_Slab_Wise_Purchase mb-4">Tax Slab Wise Purchase Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=taxslabwisepurchasesreports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
    </div>
  </div>
  <div class="card col-md-6 col-sm-12 col-lg-6">
    <div class="card-body">
      <h5 class="card-title mt-4 text-center">Accounting Reports</h5>
      <p class="card-text permissionReports-Trial_Balance">Trial Balance Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=trailbalancereports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text permissionReports-P_And_L">P & L Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=pandlreport" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
     <p class="card-text permissionReports-Balance_Sheet">Balance Sheet Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=balancesheet" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	<p class="card-text">Ledger Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=ledgerreports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
    <p class="card-text">TDS Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=tdsreport" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
    <p class="card-text">TCS Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=tcsreport" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
    <p class="card-text">Aging Report of Payment <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=agingreport_payment" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
    <p class="card-text mb-4">Aging Report of Receipt<a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=agingreport_receipt" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
    </div>
  </div>
  </div> 
  <div class=" ">&nbsp;</div>
  <div class="card-deck">
   <div class="card col-md-6 col-sm-12 col-lg-6">
    <div class="card-body">
      <h5 class="card-title mt-4 text-center">Ledger Reports</h5>
      <p class="card-text permissionReports-Cash_Ledger">Cash Ledger<a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=cash" class="btn btn-blue-dark report mr-0 pull-right"  role="button">View</a></p>
     <p class="card-text permissionReports-Credit_Ledger">Credit Ledger<a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=credit" class="btn btn-blue-dark report mr-0 pull-right"  role="button">View</a></p>
	  <p class="card-text permissionReports-Liability_Ledger mb-3">Liability Ledger<a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=tax" class="btn btn-blue-dark report mr-0 pull-right"  role="button">View</a></p>
    </div>
  </div>
  <div class="card col-md-6 col-sm-12 col-lg-6">
	  <div class="card-body">
	      <h5 class="card-title mt-4 text-center">Inventory Reports</h5>
	      <p class="card-text permissionInventory_Reports-Stock_Summary">Stock Summary Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=stockSummaryreports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	      <p class="card-text permissionInventory_Reports-Stock_Detail">Stock Detail Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=stockDetailsreport" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	      <p class="card-text permissionInventory_Reports-Stock_Ledger">Stock Ledger Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=stockLedgerreports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	      <p class="card-text permissionInventory_Reports-Rate_List">Rate List Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=ratereports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	      <p class="card-text permissionInventory_Reports-Item_Sales_Summary">Item Sales Summary Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=itemSalesreports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	       <p class="card-text permissionInventory_Reports-Stock_Aging">Stock Aging Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=agingReports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	       <p class="card-text permissionInventory_Reports-Low_Stock_Level mb-4">Low Stock Level Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=lowStockReports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	      <%--  <p class="card-text">Aging Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=agingReports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	      <p class="card-text mb-4">Party Wise Item Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=partyWisereports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p> --%>
	  </div>
  </div>
  </div>
  
<div class=" ">&nbsp;</div>
</div>
<div class="unregisteredClient" style="display:none;">
	<div class=" ">&nbsp;</div>
	<div class="card-deck">
  <div class="card col-md-6 col-sm-12 col-lg-6">
    <div class="card-body">
      <h5 class="card-title mt-4 text-center">Invoices</h5>
      <p class="card-text permissionReports-Sales">Sales Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=salesreports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text permissionReports-Purchase">Purchase Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=purchasereports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <!-- <p class="card-text permissionReports-Eway_Bill mb-4">Eway Bill Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=ewaybillreports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p> -->
    </div>
  </div>
  <div class="card col-md-6 col-sm-12 col-lg-6">
    <div class="card-body">
      <h5 class="card-title mt-4 text-center">Summary Report</h5>
      <p class="card-text permissionReports-Monthly_Tax_and_ITC_summary">Monthly Tax and ITC summary Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=monthlyTaxandItcSummary" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <!-- <p class="card-text permissionReports-Filing_Status">GST Filing Status Report<a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=filingStatusReports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p> -->
	  <!--<p class="card-text mb-4">GST Filing summary Report<a href="#" class="btn btn-blue-dark report mr-0 pull-right disable" role="button">View</a></p>-->
	  <p class="card-text permissionReports-Suppliers_GST_Filing_summary">Suppliers GST Filing summary Report<a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=suppliercompliance" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text permissionReports-ITC_Claimed">ITC Claimed Report<a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=itcreports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
      <p class="card-text mb-4 permissionReports-ITC_Unclaimed">ITC Unclaimed Report<a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=itc_unclaimedreports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
    </div>
  </div>
</div>
<div class=" ">&nbsp;</div>
	 <div class="card-deck">
  <div class="card col-md-6 col-sm-12 col-lg-6">
    <div class="card-body">
      <h5 class="card-title mt-4 text-center">HSN and Tax Slab Wise Report</h5>
      <p class="card-text permissionReports-HSN_Wise_Sales" >HSN wise Sales Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=hsnsalesreports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text permissionReports-HSN_Wise_Purchase">HSN wise Purchase Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=hsnpurchasereports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
    <p class="card-text permissionReports-Tax_Slab_Wise_Sales">Tax Slab Wise Sales Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=taxslabwisesalesreports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text permissionReports-Tax_Slab_Wise_Purchase mb-4">Tax Slab Wise Purchase Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=taxslabwisepurchasesreports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
    </div>
  </div>
  <div class="card col-md-6 col-sm-12 col-lg-6">
    <div class="card-body">
      <h5 class="card-title mt-4 text-center">Accounting Reports</h5>
      <p class="card-text permissionReports-Trial_Balance">Trial Balance Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=trailbalancereports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text permissionReports-P_And_L">P & L Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=pandlreport" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
     <p class="card-text permissionReports-Balance_Sheet">Balance Sheet Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=balancesheet" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	<p class="card-text">Ledger Report <a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=ledgerreports" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
    <p class="card-text">TDS Report <a href="#" class="btn btn-blue-dark report mr-0 pull-right disabled" role="button">View</a></p>
    <p class="card-text mb-4">Aging Report <a href="#" class="btn btn-blue-dark report mr-0 pull-right disabled" role="button">View</a></p>
    </div>
  </div>
  </div> 
<div class=" ">&nbsp;</div>
</div>
	</div>
    </div>
	<!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->