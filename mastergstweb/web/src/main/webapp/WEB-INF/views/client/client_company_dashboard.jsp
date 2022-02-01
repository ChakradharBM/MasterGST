<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Client Company Dashboard</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/reports.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard/client-company-dashboard.css" media="all" />
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/common/dataTables.fixedColumns.min.js"></script>
</head>
<body class="body-cls">
  <!-- header page begin -->
  <%@include file="/WEB-INF/views/includes/client_header.jsp" %>
	<!--- breadcrumb start -->
	<div class="breadcrumbwrap">
	<div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb"><li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li><li class="breadcrumb-item active"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></li></ol>
                    <select class="pull-right m-1 ml-0" name="financialYear" id="financialYear"><option value="2021">2021 - 2022</option><option value="2020">2020 - 2021</option><option value="2019">2019 - 2020</option><option value="2018">2018 - 2019</option><option value="2017">2017 - 2018</option></select><span class="f-14-b pull-right mt-1 font-weight-bold">Financial Year :</span><div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
        <div class="db-ca-wrap">
            <div class="container">                
               <div id="summary1" class="returns_dropdown">
               <h4 class="hdrtitle" style="font-size: 18px; margin-bottom:5px;">Filing Status for the year of <span class="compdashfinancialYear"  id="financialYear1" style="margin-left: 3px;"></span>
					<div class="addsales dropdown pull-right mb-2" style="z-index:2;">
			             <div class="split-button-menu-dropdown importdrop">
						 <button class="btn btn-blue dropdown-toggle permissionImport_Invoice b-split-right b-r-cta b-m-super-subtle" id="idMenuDropdown" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius:  0px;border-top-left-radius: 0px;"  ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue dropdown-toggle permissionImport_Invoice b-split-left b-r-cta b-m-super-subtle" id="idMenuDropdown" data-toggle="dropdown" aria-haspopup="true" style="width:180px;box-shadow:none;text-align:left" aria-expanded="false" href="#" >Import Invoices</button>
			             <div class="dropdown-menu dropdown-menu-right importmenu" aria-labelledby="idMenuDropdown" style="width: 181px!important;"><a class="dropdown-item importdropdown permissionGeneral-Import_Sales" href="#" data-toggle="modal" data-target="#importModal" onClick="updateImportModal('<%=MasterGSTConstants.GSTR1%>')">Sales Invoice</a><a class="dropdown-item importdropdown permissionGeneral-Import_Purchases" href="#" data-toggle="modal" data-target="#importModal" onClick="updateImportModal('<%=MasterGSTConstants.PURCHASE_REGISTER%>')">Purchase Invoice</a>
						 </div></div></div>
						<c:choose>
							<c:when test="${otperror eq 'Y'}"><a type="button" class="btn btn-success pull-right disable syn_data_btn" style="color:white;font-size:14px;" onclick="syncinfo()" data-toggle="modal" data-target="#syncmodal">Sync With GSTIN</a> </c:when>
							<c:when test="${headerkeys eq 'NOTFOUND'}"><a type="button" class="btn btn-success pull-right disable syn_data_btn" style="color:white;font-size:14px;" onclick="syncinfo()" data-toggle="modal" data-target="#syncmodal">Sync With GSTIN</a> </c:when>
							<c:when test="${otpexpires eq 'EXPIRED'}"><a type="button" class="btn btn-success pull-right disable syn_data_btn" style="color:white;font-size:14px;" data-toggle="modal" data-target="#syncmodal">Sync With GSTIN</a> </c:when>
							<c:otherwise><a type="button" class="btn btn-success pull-right syn_data_btn" style="color:white;font-size:14px;" onclick="syncinfo()" data-toggle="modal" data-target="#syncmodal">Sync With GSTIN</a> 	</c:otherwise>
						</c:choose>
						<a type="button" class="btn btn-primary" style="color:white; box-shadow:none; font-size:14px" onclick="refreshClientStatusSummary()">Filing Status<i class="fa fa-refresh" id="clntflngstatusrefreshSummary" style="font-size: 14px; color: #fff; margin-left:5px"></i></a>
						<a href="#" id="status_excel_btn"><img src="${contextPath}/static/mastergst/images/dashboard-ca/excel-icon.png" alt="Excel" style="width:35px;float:right;"></a>
						</h4>
           				<div class="customtable db-ca-view reportTable reportTable2 filing-status-table" style="overflow-x: visible!important;">
        				<table id="reportTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
            				<thead><tr><th class="text-center">Returns / Month</th><th class="text-center">April</th><th class="text-center">May</th><th class="text-center">June</th><th class="text-center">July</th><th class="text-center">August</th><th class="text-center">September</th><th class="text-center">October</th><th class="text-center">November</th><th class="text-center">December</th><th class="text-center">January</th><th class="text-center">February</th><th class="text-center">March</th></tr></thead>
                			<tbody>
								<c:set var="varPending" value="<%=com.mastergst.core.common.MasterGSTConstants.PENDING%>"/>
								<c:forEach items="${lGSTReturnsSummury}" var="GSTReturnsSummury">
									<c:if test='${GSTReturnsSummury.active == "true"}'>
										<c:if test="${GSTReturnsSummury.returntype eq 'GSTR9'}">
											<tr class="idGstr9">
												<td class="text-center"><h6>
	                                    			<p><a style="display:none;" href="#">${GSTReturnsSummury.returntype}</a><a class="idGstr9" href="${contextPath}/addAnnualinvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${month}"/>/<c:out value="${year}"/>?type=">${GSTReturnsSummury.returntype}</a></p></h6>
													<c:set var="sumDescr" value="${GSTReturnsSummury.description}" /><c:set var="sumDescr" value="${fn:replace(sumDescr, 'Filing', '')}" /> 
													<p>${sumDescr}</p>
												</td>
													<c:forEach var="i" begin="4" end="12"><td id="id${GSTReturnsSummury.returntype}${i}" class="dropbtn dropdown"></td></c:forEach><c:forEach var="j" begin="1" end="3"><td id="id${GSTReturnsSummury.returntype}${j}" class="dropbtn dropdown"></td></c:forEach>	
											</tr>
										</c:if>
										<c:if test="${GSTReturnsSummury.returntype ne 'GSTR9'}">
												<c:choose>
												<c:when test="${GSTReturnsSummury.returntype eq varGSTR2}">
													<tr class="permissionGSTR2-GSTR2A">
												</c:when>
												<c:when test="${GSTReturnsSummury.returntype eq varGSTR8}">
													<tr class="idaccessGstr8">
												</c:when>
												<c:otherwise>
											<tr id="id${GSTReturnsSummury.returntype}">
												</c:otherwise>
												</c:choose>
												<td class="text-center"><h6>
	                                    			<p><c:choose>
															<c:when test="${GSTReturnsSummury.returntype eq varGSTR1}"><a class="permissionInvoices-Sales-View-Invoices-Sales" style="display:none;" href="#">${GSTReturnsSummury.returntype}</a><a class="permissionInvoices-Sales-View" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/<c:out value="${month}"/>/<c:out value="${year}"/>?type=">${GSTReturnsSummury.returntype}</a></c:when>
															<c:when test="${GSTReturnsSummury.returntype eq varGSTR2}"><a class="permissionInvoices-Purchase-View-Invoices-Purchase" style="display:none;" href="#">${GSTReturnsSummury.returntype}</a><a class="permissionGSTR2-GSTR2A" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/<c:out value="${month}"/>/<c:out value="${year}"/>?type=dwnldgstr2a">GSTR2A</a></c:when>
															<c:when test="${GSTReturnsSummury.returntype eq varGSTR3B || GSTReturnsSummury.returntype eq varGSTR4 || GSTReturnsSummury.returntype eq varGSTR6 || GSTReturnsSummury.returntype eq varGSTR9 || GSTReturnsSummury.returntype eq varGSTR8}"><a class="permission${GSTReturnsSummury.returntype}-${GSTReturnsSummury.returntype}-${GSTReturnsSummury.returntype}" style="display:none;" href="#">${GSTReturnsSummury.returntype}</a><a class="permission${GSTReturnsSummury.returntype}-${GSTReturnsSummury.returntype}" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/<c:out value="${month}"/>/<c:out value="${year}"/>?type=">${GSTReturnsSummury.returntype}</a></c:when>
															<c:otherwise><a class="permission${GSTReturnsSummury.returntype}-${GSTReturnsSummury.returntype}-${GSTReturnsSummury.returntype}" style="display:none;" href="#">${GSTReturnsSummury.returntype}</a><a class="permission${GSTReturnsSummury.returntype}-${GSTReturnsSummury.returntype}" href="#" data-toggle="tooltip" data-placement="bottom" title="This feature will be made available over coming weeks on schedule with GSTN release">${GSTReturnsSummury.returntype}</a></c:otherwise>
													</c:choose></p></h6>
													<c:set var="sumDescr" value="${GSTReturnsSummury.description}" /><c:set var="sumDescr" value="${fn:replace(sumDescr, 'Filing', '')}" /> 
													<c:choose>
												<c:when test="${GSTReturnsSummury.returntype eq varGSTR2}">
													<p>Supplier Invoices</p>
												</c:when>
												<c:otherwise>
													<p>${sumDescr}</p>
												</c:otherwise>
												</c:choose>
												</td>
												<c:if test="${client.filingOption ne 'Quarterly' || GSTReturnsSummury.returntype ne 'GSTR1'}">
													<c:forEach var="i" begin="4" end="12"><td id="id${GSTReturnsSummury.returntype}${i}" class="dropbtn dropdown"></td></c:forEach><c:forEach var="j" begin="1" end="3"><td id="id${GSTReturnsSummury.returntype}${j}" class="dropbtn dropdown"></td></c:forEach>
												</c:if>
												<c:if test="${client.filingOption eq 'Quarterly' && GSTReturnsSummury.returntype eq 'GSTR1'}">
													<%-- <c:forEach var="i" begin="4" end="12"><td id="id${GSTReturnsSummury.returntype}${i}" colspan="3" class="dropbtn dropdown vimala"></td></c:forEach><c:forEach var="j" begin="1" end="3"><td id="id${GSTReturnsSummury.returntype}${j}" class="dropbtn dropdown"></td></c:forEach> --%>
													<c:forEach var="i" begin="4" end="12"><c:if test="${i eq '4'}"><td id="id${GSTReturnsSummury.returntype}${i}" colspan="3" class="dropbtn dropdown text-center"></td></c:if><c:if test="${i eq '7'}"><td id="id${GSTReturnsSummury.returntype}${i}" colspan="3" class="dropbtn dropdown text-center"></td></c:if><c:if test="${i eq '10'}"><td id="id${GSTReturnsSummury.returntype}${i}" colspan="3" class="dropbtn dropdown text-center"></td></c:if></c:forEach><c:forEach var="j" begin="1" end="3"><c:if test="${j eq '1'}"><td id="id${GSTReturnsSummury.returntype}${j}" class="dropbtn dropdown text-center" colspan="3"></td></c:if></c:forEach>
												</c:if>
											</tr>
											</c:if>
									</c:if>
								</c:forEach>
							</tbody>
						</table><span style="float:right;font-size:13px;">Note: The above <span class="color-red fa fa-circle" style="font-size:8px;"> </span> Indicates Late Filing</span>
					</div>
				</div>
                    <!-- GST returns summery end -->
					<div class="row" style="display:block;">
                    <!-- dashboard ca table begin -->
                    <div class="col-sm-12" >
					<div class ="row mb-4">
					<div class="col-sm-9 pr-0"><h4 class="hdrtitle" style="display: inline-block;">Financial Summary of <span class="compdashfinancialYear"  style="margin-left: 3px;"></span></h4></div>
					<div class="col-sm-3"><a href="${contextPath}/dwnldFinancialSummaryxls/${id}/${client.id}/${year}" id="dwnldxls" class="btn btn-blue mb-3 pull-right excel_btn" style="padding: 6px 15px 5px;font-weight: bold;color: #435a93;" data-toggle="tooltip" data-placement="top" title="Download Finacial Summary To Excel">Download To Excel<i class="fa fa-download ml-1" aria-hidden="true"></i></a></div>
					</div>
		<div class="customtable db-ca-view reportTable reportTable2 summaryTable" style="margin-top: -27px;">
            <table id="reportTable2" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
              <thead><tr><th class="text-center"></th><th class="text-center">April</th><th class="text-center">May</th><th class="text-center">June</th><th class="text-center">July</th><th class="text-center">August</th><th class="text-center">September</th><th class="text-center">October</th><th class="text-center">November</th><th class="text-center">December</th><th class="text-center">January</th><th class="text-center">February</th><th class="text-center">March</th><th class="text-center">YTD(Year To Date)</th></tr></thead>
              <tbody>
			  <c:set var="monthArray" value="${['4','5','6','7','8','9','10','11','12','1','2','3']}" />
				<c:if test="${dealertype eq 'NonCompound' || dealertype eq 'Compound' || dealertype eq 'Casual' || dealertype eq ''}"><tr data-toggle="modal" data-target="#viewModal"><td align="center"><h6>Sales</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right" id="sales${month}"><span class="ind_formatss" style="color:black">0.00</span></td></c:forEach><td class="text-right ytd ind_formatss ytdSales" id="ytdSales">0.00</td> </tr> </c:if>
				 <c:if test="${dealertype eq 'NonCompound' || dealertype eq 'InputServiceDistributor' || dealertype eq ''}"><tr><td align="center"><h6>Purchases</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right" id="purchase${month}"><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/PurchaseRegister/<c:out value="${month}"/>/<c:out value="${year}"/>?type="><span class="ind_formatss" style="color:black">0.00</span></a></td></c:forEach><td class="text-right ytd ind_formatss ytdPurchases" id="ytdPurchases">0.00</td></tr> </c:if>
				 <tr><td align="center"><h6>Expenses</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right" id="expense${month}"><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/PurchaseRegister/<c:out value="${month}"/>/<c:out value="${year}"/>?type=Exp"><span class="ind_formatss" style="color:black">0.00</span></a></td></c:forEach><td class="text-right ytd ind_formatss ytdExpenses" id="ytdExpenses">0.00</td></tr>
				 <c:if test="${dealertype eq 'NonCompound' || dealertype eq ''}"> <tr><td align="center"><h6>Balance</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right ind_formatss" id="bal${month}" data-toggle="tooltip" title = "">0.00</td></c:forEach><td class="text-right ytd ind_formatss ytdBalance" id="ytdBalance">0.00</td> </tr> </c:if>
				 <c:if test="${dealertype eq 'NonCompound' || dealertype eq 'Compound' || dealertype eq 'Casual' || dealertype eq ''}"><tr><td align="center"><h6>Output Tax</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right ind_formatss" id="salestax${month}" data-toggle="tooltip" title = "">0.00</td></c:forEach><td class="text-right ytd ind_formatss ytdSalestax" id="ytdSalestax">0.00</td></tr></c:if>
				 <c:if test="${dealertype eq 'NonCompound' || dealertype eq 'InputServiceDistributor' || dealertype eq ''}"><tr><td align="center"><h6>Input Tax</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right ind_formatss" id="purchasetax${month}" data-toggle="tooltip" title = "">0.00</td></c:forEach><td class="text-right ytd ind_formatss ytdPurchasetax" id="ytdPurchasetax">0.00</td></tr></c:if>
				 <c:if test="${dealertype eq 'NonCompound' || dealertype eq ''}"><tr class="addrowshadow"><td align="center"><h6>Monthly Tax</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right ind_formatss" id="tax${month}" data-toggle="tooltip" title = "" style="font-weight:bold">0.00</td></c:forEach><td class="text-right ytd ind_formatss ytdTax" id="ytdTax">0.00</td></tr></c:if>
                 <tr><td align="center"><h6>Exempted</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right ind_formatss" id="exempted${month}">0.00</td></c:forEach><td class="text-right ytd ind_formatss ytdexempted" id="ytdexempted">0.00</td></tr>
                 <tr><td align="center"><h6>TCS Payable</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right ind_formatss" id="tcs${month}">0.00</td></c:forEach><td class="text-right ytd ind_formatss ytdtcs" id="ytdtcs">0.00</td></tr>
                 <tr><td align="center"><h6>TCS Receivable</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right ind_formatss" id="ptcs${month}">0.00</td></c:forEach><td class="text-right ytd ind_formatss ytdptcs" id="ytdptcs">0.00</td></tr>
                 <tr><td align="center"><h6>TDS Payable</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right ind_formatss" id="tds${month}">0.00</td></c:forEach><td class="text-right ytd ind_formatss ytdtds" id="ytdtds">0.00</td></tr><tr><td align="center"><h6>Cummulative Tax</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right ind_formatss" id="cumtax${month}">0.00</td></c:forEach><td class="text-right ytd ind_formatss ytdCumTax" id="ytdCumTax">0.00</td> </tr>
               </tbody>
            </table><p style="font-size: 13px; text-align: right;">Note: The above currency is in Indian Rupees</p>
          </div>
		  </div>
		  </div>
                <!-- Dashboard body end -->
				<div class="row mb-2 returns_dropdown">
				<div class="col-md-6 pr-2">
				<div class="cashledger_row">
				<h6 class="mb-2 mt-2 ml-4" style="display: inline-block;color:#374583">CASH LEDGER</h6><a class="pl-1" style="font-size: 12px; color: black;" href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=cash">( Full Report )</a>
				<p style="display: inline-block;margin-left:5px;margin-bottom: 2px;float: right;margin-top: 5px;margin-right: 27px;padding: 2px;font-size:13px;">Closing Balance As on : <span class="ml-2 ledgerDate"></span></p>
				<div class="cashledger_summary_totals">
				<div class="noramltable-row">
					<div class="noramltable-row-desc">
						<div class="normaltable-col hdr">IGST<div class="normaltable-col-txt clind_formats" id="cashledgerigst">0.00</div></div>
						<div class="normaltable-col hdr">CGST<div class="normaltable-col-txt clind_formats" id="cashledgercgst">0.00</div></div>
						<div class="normaltable-col hdr">SGST<div class="normaltable-col-txt clind_formats" id="cashledgersgst">0.00</div></div>
						<div class="normaltable-col hdr">CESS<div class="normaltable-col-txt clind_formats" id="cashledgercess">0.00</div></div>
					</div>	
				</div>
				</div>
				</div>
				</div>
				<div class="col-md-6 pl-2">
				<div class="cashledger_row">
				<h6 class="mb-2 mt-2 ml-4" style="display: inline-block;color:#374583">CREDIT LEDGER</h6><a class="pl-1" style="font-size: 12px; color: black;" href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=credit">( Full Report )</a>
				<p style="display: inline-block;margin-left:5px;margin-bottom: 2px;float: right;margin-top: 5px;margin-right: 27px;padding: 2px;font-size:13px;">Closing Balance As on : <span class="ml-2 ledgerDate" style="padding: 1px;"></span></p>
				<div class="creditledger_summary_totals">
				<div class="noramltable-row">
					<div class="noramltable-row-desc">
						<div class="normaltable-col hdr">IGST<div class="normaltable-col-txt clind_formats" id="creditledgerigst">0.00</div></div>
						<div class="normaltable-col hdr">CGST<div class="normaltable-col-txt clind_formats" id="creditledgercgst">0.00</div></div>
						<div class="normaltable-col hdr">SGST<div class="normaltable-col-txt clind_formats" id="creditledgersgst">0.00</div></div>
						<div class="normaltable-col hdr">CESS<div class="normaltable-col-txt clind_formats" id="creditledgercess">0.00</div></div>
					</div>	
				</div>
				</div>
				</div>
				</div>
            </div>
        </div>
<div class="maskdbbg" style="display:none"><span class="selectdate">Please select the return period <i class="fa fa-share" aria-hidden="true"></i></span></div>
   <!-- footer begin here -->
 <%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->
 <div class="modal fade" id="syncmodal" role="dialog" aria-labelledby="syncmodal" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-right" role="document" style="width:1200px;">
            <div class="modal-content">
                <div class="modal-body meterialform popupright">
                 <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="height: 54px;"><h4 style="color:white">Sync with GSTIN</h4></div>
					<div class="syncmodal">
					<div class="col-11 m-0 p-0"><p class="" style="margin-top: -17px;">Please Click on <b>Sync With GSTIN</b> button to download all the data from GSTN till the date</p><p><span id="syncinfo_span"></span></div>
					<div class="text-right" style="margin-bottom: -15px;">
						<b style="vertical-align: inherit;">Choose Financial Year to Sync</b> : <select class="" name="financialYear" id="syncfinancialYear"><option value="2021" selected>2021-2022</option><option value="2020">2020-2021</option><option value="2019">2019-2020</option><option value="2018">2018-2019</option><option value="2017">2017-2018</option></select>&nbsp;
						<a class="btn btn-success" id="syn_data_btn" style="padding: 7px 10px;font-size: 14px;margin-top: -7px;" onclick="syncwithcontinue('${id}','${fullname}','${usertype}','${clientid}')">Sync With GSTN</a>
					</div>
					<div></div>
					<div class="clearfix">&nbsp;</div>
					<!--  table div start -->
					<div class="d-none" id="progress-bar" style="position: absolute;top: 35%;width:100%;text-align:center;z-index:1;"> 
						<img src="${contextPath}/static/mastergst/images/eclipse-spinner.gif" alt="spinner-img" style="width: 150px;height: 150px;"/>
						<p style="opacity: 1; font-weight: bolder;color: darkblue;margin-left: -20px;">We are downloading your data from GSTN, please wait...</p>
					</div>
					<div class="customtable db-ca-view tabtable1" style=" overflow-y: auto">
						<table id="syncdataTable"class="display row-border dataTable meterialform"	cellspacing="0" width="60%" style="border: 1px solid lightgray;">
							<thead><tr style="font-size:13px;"><th style="width: 17%;">Year/ Return Type</th><th class="text-center">GSTR1</th><th class="text-center">GSTR2A</th><th class="text-center">GSTR3B</th></tr></thead>
							<tbody id="sync_data"><tr><th class="sync_th">April</th><td class="text-center" id="GSTR14">-</td><td id="GSTR2A4" class="text-center">-</td><td id="GSTR3B4" class="text-center">-</td></tr><tr><th  class="sync_th">May</th><td id="GSTR15" class="text-center">-</td><td id="GSTR2A5" class="text-center">-</td><td id="GSTR3B5" class="text-center">-</td></tr><tr><th class="sync_th">June</th><td id="GSTR16" class="text-center">-</td><td id="GSTR2A6" class="text-center">-</td><td id="GSTR3B6" class="text-center">-</td></tr><tr><th class="sync_th">July</th><td id="GSTR17" class="text-center">-</td><td id="GSTR2A7" class="text-center">-</td><td id="GSTR3B7" class="text-center">-</td></tr><tr><th class="sync_th">August</th><td id="GSTR18" class="text-center">-</td><td id="GSTR2A8" class="text-center">-</td><td id="GSTR3B8" class="text-center">-</td></tr><tr><th class="sync_th">September</th><td id="GSTR19" class="text-center">-</td><td id="GSTR2A9" class="text-center">-</td><td id="GSTR3B9" class="text-center">-</td></tr><tr><th class="sync_th">October</th><td id="GSTR110"class="text-center">-</td><td id="GSTR2A10" class="text-center">-</td><td id="GSTR3B10" class="text-center">-</td></tr><tr><th class="sync_th">November</th><td id="GSTR111" class="text-center">-</td><td id="GSTR2A11" class="text-center">-</td><td id="GSTR3B11" class="text-center">-</td></tr><tr><th class="sync_th">December</th><td id="GSTR112" class="text-center">-</td><td id="GSTR2A12" class="text-center">-</td><td id="GSTR3B12" class="text-center">-</td></tr><tr><th class="sync_th">January</th><td id="GSTR11" class="text-center">-</td><td id="GSTR2A1" class="text-center">-</td><td id="GSTR3B1" class="text-center">-</td></tr><tr><th class="sync_th">February</th><td id="GSTR12" class="text-center">-</td><td id="GSTR2A2" class="text-center">-</td><td id="GSTR3B2" class="text-center">-</td></tr><tr><th class="sync_th">March</th><td id="GSTR13" class="text-center">-</td><td id="GSTR2A3" class="text-center">-</td><td id="GSTR3B3" class="text-center">-</td></tr></tbody>
						</table>
					</div>
					<!--  table div end -->
				</div>
			</div>
			</div>
			</div>
			</div>
			<!-- enableAccessModal Start -->
	<div class="modal fade" id="enableAccessModal" tabindex="-1" role="dialog" aria-labelledby="enableAccessModal" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
                <div class="modal-body">
                    <button type="button" id="enableAccessModalCloase" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/dashboard-ca/closeicon.png" alt="Close" /></span></button>
                    <div class="invoice-hdr bluehdr"><h4>Important Step before your upload invoices </h4></div>
					<div class=" gstnuser-wrap">
						<div class="formboxwrap p-0" style="min-height:unset;">
						<div class="alert alert-info" id="idClientError"></div>	
						  <h6><span class="steptext"><strong><u>Step 1 </u> : </strong></span>Click here to <a href="https://services.gst.gov.in/services/login" target="_blank">Enable API Access</a>, Follow <a href="${contextPath}/static/mastergst/Enable-API-Access-On-GST-Portal.pdf" target="_blank">Help Guide</a> </h6>
						  <p class="txt-sm"><span class="steptext">&nbsp;</span>Login into your <a href="https://services.gst.gov.in/services/login" target="_blank">GSTN portal</a> and enable authorization. For detailed follow this guidence. </p>
						  <span class=""><span class="steptext pull-left">&nbsp;</span> <a href="https://services.gst.gov.in/services/login" target="_blank" class="btn btn-blue-dark btn-sm pull-left mt-3">Enable API Access</a><a href="${contextPath}/static/mastergst/Enable-API-Access-On-GST-Portal.pdf" target="_blank" class="btn btn-sm btn-green pull-right mt-3">See Help Guide</a> </span>
                         <div class="mb-3 mt-5" style="border-bottom:1px solid #f5f5f5;width:100%;">&nbsp;</div> 
						<h6><span class="steptext"><strong><u>Step 2 </u> : </strong></span> Verify GSTIN User Name</h6>
 					        <div class="col-md-12 col-sm-12 m-auto p-0">
                                <div class="formbox otpbox mt-3">
                                    <form class="meterialform row" id="accessotpEntryForm">
										<span class="steptext">&nbsp;</span>
										<div class="col-md-5 col-sm-12">
                                                <div class="lable-txt">Enter Your GSTIN Login/User Name</div>
                                                <div class="form-group"><span class="errormsg" id="gstnUserIdMsg"></span><input type="text" id="gstnUserId" name="gstnUserId" required="required" data-error="Please enter the GSTN Username" aria-describedby="gstnUserId" placeholder="GSTIN Login/User Name"><label for="input" class="control-label"></label><i class="bar"></i> </div>
                                            </div>
											<div class="col-md-4 col-sm-12"><a href="#" onClick="showOtp();" class="btn btn-red btn-sm mt-4">Verify Now</a></div>
											<div class="whitebg gstn-otp-wrap">                                 
											<h5><span class="steptext"><strong><u>Step 3 </u> : </strong></span>GSTN has sent you an OTP please enter here for verification.</h5>          
                                            <div class="errormsg mt-2"> </div>
                                            <!-- serverside error end -->
                                            <span class="errormsg" id="otp_Msg"></span>  
                                            <div class="otp_form_input" style="display:block;margin-top:30px">
											<div class="col-12"></div>
											<div class="row"><span class="steptext col-sm-12">&nbsp;</span>
												<div class="col-md-9 col-sm-12"><input type="text" name="otp" class="form-control invoice_otp" id="accessotp1" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="1" placeholder="0"/><div class="help-block with-errors"></div><input type="text" name="otp" class="form-control invoice_otp" id="accessotp2" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="2" placeholder="0"/><div class="help-block with-errors"></div><input type="text" name="otp" class="form-control invoice_otp" id="accessotp3" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="3" placeholder="0"/><div class="help-block with-errors"></div><input type="text" name="otp" class="form-control invoice_otp" id="accessotp4" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="4" placeholder="0"/><div class="help-block with-errors"></div><input type="text" name="otp" class="form-control invoice_otp" id="accessotp5" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="5" placeholder="0"/><div class="help-block with-errors"></div><input type="text" name="otp" class="form-control invoice_otp" id="accessotp6" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="6" placeholder="0"/><div class="help-block with-errors"></div></div>
													<div class="col-md-3 col-sm-12 pull-right"><a href="#" onClick="validOtp()" class="btn btn-blue btn-sm btn-verify">Submit</a> </div><h6 class="col-md-9 col-sm-12 mt-3" style="display:inline-block;width:100%;text-align:center;">Didn&acute;t  Receive OTP? <a href="#" onClick="otpTryAgain('apiNotEnabled')">try again</a></h6>
                                                </div>
                                            </div>                                      
                                     	</div>
                                    </form>
                                </div>
                                <div class="formbox otpbox-suces">
                                    <form class="meterialform">
                                        <div class="whitebg row">
                                            <!-- serverside error begin -->
                                            <div class="errormsg"> </div>
                                            <!-- serverside error end -->
											<span class="steptext">&nbsp;</span>
                                            <div class="col-sm-10 pl-0" style="display:none;"><div class="mb-5 text-center greenbox" id="idVerifyClient"></div></div>
											<div class="form-group col-3 m-auto"><input type="button" value="Close" class="btn btn-blue-dark btn-sm" data-dismiss="modal" aria-label="Close" /></div>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
	<!-- enableAccessModal End -->
	<!-- otpModal Start -->
	<div class="modal fade" id="otpModal" tabindex="-1" role="dialog" aria-labelledby="otpModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document">
			<div class="modal-content">
				<div class="modal-body">
					<button type="button" id="otpModalClose" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/dashboard-ca/closeicon.png" alt="Close" /></span></button>
					<div class="invoice-hdr bluehdr"><h3>Add Client - Verify OTP</h3></div>
					<div class=" p-4">
						<div class="formboxwrap p-0" style="min-height: unset;">
							<h3> Filing GST Made Simple, & Pay your Tax easily </h3><h5>TRUSTED BY MOST CA's AND COMPANIES NATIONALLY</h5>
							<div class="col-md-12 col-sm-12 m-auto">
								<div class="formbox otpbox">
									<form class="meterialform" id="otpEntryForm" data-toggle="validator">
										<div class="whitebg"><h2> Verifying GSTIN User Name for smooth filing</h2><h6>OTP has been sent to your GSTIN registered mobile number & e-mail, Please enter the same below</h6>              
											<div class="errormsg"> </div>
											<!-- serverside error end --> 
											<span class="errormsg" id="cotp_Msg"></span>
											<div class="col-sm-12 otp_form_input" style="display:block;margin-top:30px">
												<div class=" "><div class=" "></div><input type="text" name="otp" class="form-control invoice_otp" id="otp1" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="11" placeholder="0" /><div class="help-block with-errors"></div><input type="text" name="otp" class="form-control invoice_otp" id="otp2" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="12" placeholder="0"/><div class="help-block with-errors"></div><input type="text" name="otp" class="form-control invoice_otp" id="otp3" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="13" placeholder="0"/><div class="help-block with-errors"></div><input type="text" name="otp" class="form-control invoice_otp" id="otp4" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="14" placeholder="0"/><div class="help-block with-errors"></div><input type="text" name="otp" class="form-control invoice_otp" id="otp5" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="15" placeholder="0"/><div class="help-block with-errors"></div><input type="text" name="otp" class="form-control invoice_otp" id="otp6" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="16" placeholder="0"/><div class="help-block with-errors"></div></div>
												<h6 style="display: inline-block;text-align: center;width: 100%;">Didn't receive OTP? <a href="#" onClick="otpTryAgain('apiEnabled')">try again</a></h6>
											</div>
										</div>
										<div class="p-2 text-center"><p><a href="#" onClick="validateOtp()" class="btn btn-lg btn-blue btn-verify">Verify OTP</a></p></div>
									</form>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- otpModal End -->
    <script>
        	var clntFlngStatusRestriction = false;
        	var summaryReportRestriction = false;
        	var refreshClntStatusSmryRestriction = false;
        $(document).ready(function() {
        	var uploadResponse, ipAddress='';
        	if('${headerkeys}' == 'NOTFOUND'){errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP_sync(this)">Verify Now</a> to proceed further.');
			} else if('${otpexpires}' == 'EXPIRED') {errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP_sync(this)">Verify Now</a> to proceed further.');}else{
			 	    	$.ajax({
				    		url: "${contextPath}/cashAndCreditLedgerDetails/${id}/${client.id}",
				    		contentType: 'application/json',
				    		success : function(data) {
				    			if(data.cashledger){
									$('#cashledgerigst').html(data.cashledger.igstbal.tot);$('#cashledgersgst').html(data.cashledger.sgstbal.tot);$('#cashledgercgst').html(data.cashledger.cgstbal.tot);$('#cashledgercess').html(data.cashledger.cessbal.tot);
								}
								if(data.creditledger){
									$('#creditledgerigst').html(data.creditledger.igstTaxBal);$('#creditledgersgst').html(data.creditledger.sgstTaxBal);$('#creditledgercgst').html(data.creditledger.cgstTaxBal);$('#cashledgercess').html(data.creditledger.cessTaxBal);
								}
								OSREC.CurrencyFormatter.formatAll({selector: '.clind_formats'});
				    	    }
				    	});	
			}
        	var ptype = '<c:out value="${ptype}"/>';	var selectedMonth;
        	if(ptype == 'cfirm'){$('#nav-dashb').addClass('active');
        	}else{$('#nav-client').addClass('active');}
			var fullDate = new Date();var twoDigitMonth = (fullDate.getMonth()+1)+"";if(twoDigitMonth.length==1) twoDigitMonth="0" +twoDigitMonth;var twoDigitDate = fullDate.getDate()+"";if(twoDigitDate.length==1)	twoDigitDate="0" +twoDigitDate;var ledgerDate = twoDigitDate + "/" + twoDigitMonth + "/" + fullDate.getFullYear(); $('.ledgerDate').text(ledgerDate);
			var currentDate = new Date();var currentMonth = currentDate.getMonth()+1;var currentYear = currentDate.getFullYear();var reportView = '<c:out value="${client.reportView}"/>';var x=document.getElementById("summary1");var y=document.getElementById("summary2");
				var pmonth = parseInt('${month}');var pyear =  parseInt('${year}');var pdate = pmonth +"/1/" + pyear;var edate = new Date(pdate);
				updateReturnPeriod(edate);
				$('body').css('height','auto');$('body').css('overflow','auto');$('.gstrValue').html('0.00');
			$('.maskdbbg').click(function(e) {e.preventDefault();$('#datetimepicker').datepicker('show');});
            $('#gstreturns').owlCarousel({
				margin:5,
				loop: true,
				responsiveClass: true,
				dots :false,
				navigation: true,
				navText: ["<img src='${contextPath}/static/mastergst/images/dashboard-ca/nexticon.png'>","<img src='${contextPath}/static/mastergst/images/dashboard-ca/previcon.png'>"],
				margin:0,
                responsive: {
                    0: {
                        items: 2,nav: true
                    },
                    600: {
                        items: 4,nav: true
                    },
                    1000: {
                        items: 6,loop: false,nav:true,margin:5,
                    }
                }
            });
        });
		function updateYearlyStatus(year,month) {
			var currentDates = new Date();var currentMonths = currentDates.getMonth()+1;var currentYear = currentDates.getFullYear();var filingOption = '<c:out value="${client.filingOption}"/>';var years = year;
			if(month <= 3){years--;}
			var monthstatus = ""+selectedMonth+year;
			if(selectedMonth == '9' || selectedMonth == '8' || selectedMonth == '7' || selectedMonth == '6' || selectedMonth == '5' || selectedMonth == '4' || selectedMonth == '3' || selectedMonth == '2' || selectedMonth == '1'){monthstatus = '0'+monthstatus;}
			$.ajax({
				url: "${contextPath}/clntflngstatus/${client.id}/"+years,
				async: true,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(summary) {updateYearlyClntFlngStatus(year,month,summary);
				},error: function(data) {}	
			});
			$('#financialYear').val(years);
			var nextyear = (parseInt(years)+1).toString();
			$('.compdashfinancialYear').text($('#financialYear').val()+" - "+nextyear);
		}
		function updateYearlyClntFlngStatus(year,month,summary){
			var currentDates = new Date();var currentMonths = currentDates.getMonth()+1;var currentYear = currentDates.getFullYear();var filingOption = '<c:out value="${client.filingOption}"/>';var years = year;
			$('#status_excel_btn').attr('href','${contextPath}/dwnldfilingSummary/${id}/${client.id}/'+month+'/'+year);
			//$('#idGSTR1').html('').append("abcdef");
			if(month <= 3){years--;}
			var fnextyear = (parseInt(years)+1).toString();
			var finyear = years+"-"+fnextyear;
			
			<c:forEach items = "${client.filingoptions}" var="filingoption">
				if('${filingoption.year}' == finyear){
					filingOption = '${filingoption.option}';
				}
			</c:forEach>
			var abcd = '<td class="text-center"><h6><a class="permissionInvoices-Sales-View-Invoices-Sales" style="display:none;" href="#">GSTR1</a><a class="permissionInvoices-Sales-View" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/GSTR1/<c:out value="${month}"/>/<c:out value="${year}"/>?type=">GSTR1</a></h6><p>Sales Returns</p></td>';
			if(filingOption == 'Quarterly'){
				<c:forEach var="i" begin="4" end="12">
					<c:if test="${i eq '6'}">
						abcd += '<td id="idGSTR1${i}" colspan="3" class="dropbtn dropdown text-center"></td>';
					</c:if>
					<c:if test="${i eq '9'}">
						abcd += '<td id="idGSTR1${i}" colspan="3" class="dropbtn dropdown text-center"></td>';	
					</c:if>
					<c:if test="${i eq '12'}">
						abcd += '<td id="idGSTR1${i}" colspan="3" class="dropbtn dropdown text-center"></td>'
					</c:if>
				</c:forEach>
				<c:forEach var="j" begin="1" end="3">
					<c:if test="${j eq '3'}">
						abcd += '<td id="idGSTR1${j}" class="dropbtn dropdown text-center" colspan="3"></td>';
					</c:if>
				</c:forEach>
			}else{
				<c:forEach var="i" begin="4" end="12">
					abcd += '<td id="idGSTR1${i}" class="dropbtn dropdown"></td>';
				</c:forEach>
				<c:forEach var="j" begin="1" end="3">
					abcd += '<td id="idGSTR1${j}" class="dropbtn dropdown"></td>';
				</c:forEach>
			}
			$('#idGSTR1').html('').append(abcd);
			var monthstatus = ""+selectedMonth+year;
			if(selectedMonth == '9' || selectedMonth == '8' || selectedMonth == '7' || selectedMonth == '6' || selectedMonth == '5' || selectedMonth == '4' || selectedMonth == '3' || selectedMonth == '2' || selectedMonth == '1'){monthstatus = '0'+monthstatus;}
			if(summary) {
				Object.keys(summary).forEach(function(rType) {
					Object.keys(summary[rType]).forEach(function(rPeriod) {
						var content = "";var dDate ="";var fDate="";
							if(summary[rType][rPeriod][0] != 'Pending') {
								if(rType == 'GSTR1' || rType == 'GSTR2' || rType == 'GSTR6' || rType == 'GSTR4' || rType == 'GSTR3B' || rType == 'GSTR9' || rType == 'GSTR8'){
									if(filingOption == 'Quarterly' && rType != 'GSTR3B'){
										if(rType == 'GSTR1'){
											if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
												if(summary[rType][rPeriod][2] != null){content = '<div class="dropdown-content"><span class="arrow-right"></span><div>ARN <span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][2]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permissionInvoices-Sales-View tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+03+'/'+rPeriod.substring(2,6)+'?type=" >View Details</a></p>';
												}else{content = '<div class="dropdown-content"><span class="arrow-right"></span><div>ARN <span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>------</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permissionInvoices-Sales-View tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+03+'/'+rPeriod.substring(2,6)+'?type=" >View Details</a></p>';}
												}else if(rPeriod.substring(0,2) == '04' || rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06'){
													if(summary[rType][rPeriod][2] != null){content = '<div class="dropdown-content"><span class="arrow-right"></span><div>ARN <span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][2]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permissionInvoices-Sales-View tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+06+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';
													}else{content = '<div class="dropdown-content"><span class="arrow-right"></span><div>ARN <span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>------</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permissionInvoices-Sales-View tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+06+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';}
												}else if(rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09'){
														if(summary[rType][rPeriod][2] != null){content = '<div class="dropdown-content"><span class="arrow-right"></span><div>ARN <span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][2]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permissionInvoices-Sales-View tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+09+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';
														}else{content = '<div class="dropdown-content"><span class="arrow-right"></span><div>ARN <span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>-------</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permissionInvoices-Sales-View tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+09+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';}
												}else if(rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12'){
															if(summary[rType][rPeriod][2] != null){content = '<div class="dropdown-content"><span class="arrow-right"></span><div>ARN <span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][2]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permissionInvoices-Sales-View tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+12+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';
															}else{content = '<div class="dropdown-content"><span class="arrow-right"></span><div>ARN <span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>------</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permissionInvoices-Sales-View tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+12+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';}
												}
										}else if(rType == 'GSTR2'){
											
											if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
												content = '<p><a class="permissionGSTR2-GSTR2A" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+03+'/'+rPeriod.substring(2,6)+'?type=dwnldgstr2a">View Details</a></p>';
											}else if(rPeriod.substring(0,2) == '04' || rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06'){
												content = '<p><a class="permissionGSTR2-GSTR2A" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+06+'/'+rPeriod.substring(2,6)+'?type=dwnldgstr2a">View Details</a></p>';
											}else if(rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09'){
												content = '<p><a class="permissionGSTR2-GSTR2A" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+09+'/'+rPeriod.substring(2,6)+'?type=dwnldgstr2a">View Details</a></p>';
											}else if(rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12'){
												content = '<p><a class="permissionGSTR2-GSTR2A" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+12+'/'+rPeriod.substring(2,6)+'?type=dwnldgstr2a">View Details</a></p>';
											}
										}else if(rType == 'GSTR9'){
											if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '04' || rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12'){
												content = '<h6 style="text-align:center"> - </h6>';
											}else{
												if(summary[rType][rPeriod][2] != null){content = '<div class="dropdown-content"><span class="arrow-right"></span><div>ARN <span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][2]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="tooltip_status" href="${contextPath}/addAnnualinvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';
												}else{content = '<div class="dropdown-content"><span class="arrow-right"></span><div>ARN <span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>------</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="tooltip_status" href="${contextPath}/addAnnualinvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';}
											}
										}else{
											if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
												if(summary[rType][rPeriod][2] != null){content = '<div class="dropdown-content"><span class="arrow-right"></span><div>ARN <span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][2]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permission'+rType+'-'+rType+' tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+03+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';
												}else{content = '<div class="dropdown-content"><span class="arrow-right"></span><div>ARN <span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>------</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permission'+rType+'-'+rType+' tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+03+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';}
											}else if(rPeriod.substring(0,2) == '04' || rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06'){
													if(summary[rType][rPeriod][2] != null){content = '<div class="dropdown-content"><span class="arrow-right"></span><div>ARN <span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][2]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permission'+rType+'-'+rType+' tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+06+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';
													}else{content = '<div class="dropdown-content"><span class="arrow-right"></span><div>ARN <span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>-------</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permission'+rType+'-'+rType+' tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+06+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';}
											}else if(rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09'){
														if(summary[rType][rPeriod][2] != null){content = '<div class="dropdown-content"><span class="arrow-right"></span><div>ARN <span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][2]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permission'+rType+'-'+rType+' tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+09+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';
														}else{content = '<div class="dropdown-content"><span class="arrow-right"></span><div>ARN <span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>-------</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permission'+rType+'-'+rType+' tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+09+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';}
											}else if(rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12'){
														if(summary[rType][rPeriod][2] != null){content = '<div class="dropdown-content"><span class="arrow-right"></span><div>ARN <span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][2]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permission'+rType+'-'+rType+' tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+12+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';
														}else{content = '<div class="dropdown-content"><span class="arrow-right"></span><div>ARN <span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>-------</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permission'+rType+'-'+rType+' tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+12+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';}
											}
										}
									}else{
										var duedate="";	var sm;
										if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04'){											
											if(rType == 'GSTR1'){if(rPeriod.substring(0,2) == '12'){sm=1;var nextyear = (parseInt(years)+1).toString();duedate = '11-' +sm+ "-" +nextyear;
										       dDate = duedate;}else{sm = parseInt(rPeriod.substring(0,2)) + 1; duedate = '11-' +sm+ "-" +years;
										       dDate = duedate;}
										       
										    }
										}
										if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
											if(rType == 'GSTR1'){sm = parseInt(rPeriod.substring(0,2)) + 1;var nextyear = (parseInt(years)+1).toString();duedate = '11-' +sm+ "-" +nextyear;dDate = duedate;}
										}
										if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04'){
											if(rType == 'GSTR2'){if(rPeriod.substring(0,2) == '12'){sm=1;}else{sm = parseInt(rPeriod.substring(0,2)) + 1;}
											duedate = '15-' +sm+ "-" +years;dDate = duedate;
										}
										}
										if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
											if(rType == 'GSTR2'){sm = parseInt(rPeriod.substring(0,2)) + 1;var nextyear = (parseInt(years)+1).toString();duedate = '15-' +sm+ "-" +nextyear;dDate = duedate;}
										}
										if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04'){
											if(rType == 'GSTR3B'){if(rPeriod.substring(0,2) == '12'){sm=1;var nextyear = (parseInt(years)+1).toString();duedate = '20-' +sm+ "-" +nextyear;dDate = duedate;}else{sm = parseInt(rPeriod.substring(0,2)) + 1;duedate = '20-' +sm+ "-" +years;dDate = duedate;}
										       
										}
										}
										if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
											if(rType == 'GSTR3B'){sm = parseInt(rPeriod.substring(0,2)) + 1;var nextyear = (parseInt(years)+1).toString();duedate = '20-' +sm+ "-" +nextyear;dDate = duedate;}
										}
										if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04' || rPeriod.substring(0,2) == '03'|| rPeriod.substring(0,2) == '02'|| rPeriod.substring(0,2) == '01'){
											if(rType == 'GSTR9'){var nextyear = (parseInt(years)+1).toString();  duedate = '31-' +12+ "-" +nextyear;dDate = duedate;}
										}
										if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04'){											
											if(rType == 'GSTR8'){if(rPeriod.substring(0,2) == '12'){sm=1;var nextyear = (parseInt(years)+1).toString();duedate = '10-' +sm+ "-" +nextyear;dDate = duedate;}else{sm = parseInt(rPeriod.substring(0,2)) + 1;duedate = '10-' +sm+ "-" +years;dDate = duedate;}
										       
										    }
										}
										if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
											if(rType == 'GSTR8'){sm = parseInt(rPeriod.substring(0,2)) + 1;var nextyear = (parseInt(years)+1).toString();duedate = '10-' +sm+ "-" +nextyear;dDate = duedate;}
										}
											if(rType == 'GSTR1'){
												if(summary[rType][rPeriod][2] != null){content = '<div class="dropdown-content"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green">'+summary[rType][rPeriod][0]+'</span><span style="color:red;margin-left: 3px;" class="'+rType+'lateFile_text'+rPeriod.substring(0,2)+'"></span></strong></div><div>ARN<span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][2]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permissionInvoices-Sales-View tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';	
												}else{content = '<div class="dropdown-content"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green">'+summary[rType][rPeriod][0]+'</span><span style="color:red;margin-left: 3px;" class="'+rType+'lateFile_text'+rPeriod.substring(0,2)+'"></span></strong></div><div>ARN<span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>------</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permissionInvoices-Sales-View tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';}
												}else if(rType == 'GSTR2'){
												content = '<p><a class="permissionGSTR2-GSTR2A" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type=dwnldgstr2a">View Details</a></p>';
											}else if(rType == 'GSTR9'){
												if(summary[rType][rPeriod][2] != null){content = '<div class="dropdown-content"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green">'+summary[rType][rPeriod][0]+'</span><span style="color:red;margin-left: 3px;" class="'+rType+'lateFile_text'+rPeriod.substring(0,2)+'"></span></strong></div><div>ARN<span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][2]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="tooltip_status" href="${contextPath}/addAnnualinvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';														
												}else{content = '<div class="dropdown-content"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green">'+summary[rType][rPeriod][0]+'</span><span style="color:red;margin-left: 3px;" class="'+rType+'lateFile_text'+rPeriod.substring(0,2)+'"></span></strong></div><div>ARN<span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>------</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="tooltip_status" href="${contextPath}/addAnnualinvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';}
											}else if(rType == 'GSTR8'){
												if(summary[rType][rPeriod][2] != null){content = '<div class="dropdown-content"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green">'+summary[rType][rPeriod][0]+'</span><span style="color:red;margin-left: 3px;" class="'+rType+'lateFile_text'+rPeriod.substring(0,2)+'"></span></strong></div><div>ARN<span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][2]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permissionInvoices-Sales-View tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';	
												}else{content = '<div class="dropdown-content"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green">'+summary[rType][rPeriod][0]+'</span><span style="color:red;margin-left: 3px;" class="'+rType+'lateFile_text'+rPeriod.substring(0,2)+'"></span></strong></div><div>ARN<span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>------</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permissionInvoices-Sales-View tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';}
												
											}else{
												if(summary[rType][rPeriod][2] != null){content = '<div class="dropdown-content"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green">'+summary[rType][rPeriod][0]+'</span><span style="color:red;margin-left: 3px;" class="'+rType+'lateFile_text'+rPeriod.substring(0,2)+'"></span></strong></div><div>ARN<span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][2]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permission'+rType+'-'+rType+' tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';	
												}else{content = '<div class="dropdown-content"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green">'+summary[rType][rPeriod][0]+'</span><span style="color:red;margin-left: 3px;" class="'+rType+'lateFile_text'+rPeriod.substring(0,2)+'"></span></strong></div><div>ARN<span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>------</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-green" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permission'+rType+'-'+rType+' tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type=">View Details</a></p>';}
											}
									}
								}else{
									if(rType == 'GSTR1'){content = '<h6 class="color-green">'+summary[rType][rPeriod][0]+'</h6><p><a class="permissionInvoices-Sales-View tooltip_status" href="#" data-toggle="tooltip" data-placement="bottom" title="This feature will be made available over coming weeks on schedule with GSTN release">View Details</a></p>';
									}else if(rType == 'GSTR2'){content = '<h6 class="color-green">'+summary[rType][rPeriod][0]+'</h6><p><a class="permissionInvoices-Purchase-View tooltip_status" href="#" data-toggle="tooltip" data-placement="bottom" title="This feature will be made available over coming weeks on schedule with GSTN release">View Details</a></p>';												
									}else{content = '<h6 class="color-green">'+summary[rType][rPeriod][0]+'</h6><p><a class="permission'+rType+'-'+rType+' tooltip_status" href="#" data-toggle="tooltip" data-placement="bottom" title="This feature will be made available over coming weeks on schedule with GSTN release">View Details</a></p>';}
								}
							
							} else {
								if(rType == 'GSTR1' || rType == 'GSTR2' || rType == 'GSTR6' || rType == 'GSTR4' || rType == 'GSTR3B'|| rType == 'GSTR9'|| rType == 'GSTR8'){
									if(filingOption == 'Quarterly' && rType != 'GSTR3B'){
										if(rType == 'GSTR1'){
											if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){var nextyear = (parseInt(years)+1).toString();duedate = '11- 04-'  +nextyear;content = '<div class="dropdown-content"><span class="arrow-right"></span><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-red" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permissionInvoices-Sales-View tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+03+'/'+rPeriod.substring(2,6)+'?type=">File Now</a></p>';
											}else if(rPeriod.substring(0,2) == '04' || rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06'){duedate = '11- 07-'  +years;content = '<div class="dropdown-content"><span class="arrow-right"></span><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-red" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permissionInvoices-Sales-View tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+06+'/'+rPeriod.substring(2,6)+'?type=">File Now</a></p>';
											}else if(rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09'){duedate = '11- 10-'  +years;content = '<div class="dropdown-content"><span class="arrow-right"></span><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-red" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permissionInvoices-Sales-View tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+09+'/'+rPeriod.substring(2,6)+'?type=">File Now</a></p>';
											}else if(rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12'){var nextyear = (parseInt(years)+1).toString();duedate = '11- 01-'  +nextyear;content = '<div class="dropdown-content"><span class="arrow-right"></span><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-red" style="display: contents;">'+summary[rType][rPeriod][0]+'</h6><p><a class="permissionInvoices-Sales-View tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+12+'/'+rPeriod.substring(2,6)+'?type=">File Now</a></p>';
											}
										}else if(rType == 'GSTR2'){
											if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
												content = '<p><a class="permissionGSTR2-GSTR2A" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+03+'/'+rPeriod.substring(2,6)+'?type=dwnldgstr2a">View Details</a></p>';
											}else if(rPeriod.substring(0,2) == '04' || rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06'){
												content = '<p><a class="permissionGSTR2-GSTR2A" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+06+'/'+rPeriod.substring(2,6)+'?type=dwnldgstr2a">View Details</a></p>';
											}else if(rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09'){
												content = '<p><a class="permissionGSTR2-GSTR2A" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+09+'/'+rPeriod.substring(2,6)+'?type=dwnldgstr2a">View Details</a></p>';
											}else if(rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12'){
												content = '<p><a class="permissionGSTR2-GSTR2A" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+12+'/'+rPeriod.substring(2,6)+'?type=dwnldgstr2a">View Details</a></p>';
											}
										}else if(rType == 'GSTR9'){
											if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '04' || rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' ){content = '<h6 style="text-align:center"> - </h6>';
											}else{var nextyear = (parseInt(years)+1).toString();  duedate = '31-' +12+ "-" +nextyear;dDate = duedate;content = '<div class="dropdown-content"><span class="arrow-right"></span><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-red">'+summary[rType][rPeriod][0]+'</h6><p><a class="tooltip_status" href="${contextPath}/addAnnualinvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type=">File Now</a></p>';
											}
										}else{
											if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){content = '<div class="dropdown-content"><span class="arrow-right"></span><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-red">'+summary[rType][rPeriod][0]+'</h6><p><a class="permission'+rType+'-'+rType+' tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+03+'/'+rPeriod.substring(2,6)+'?type=">File Now</a></p>';
											}else if(rPeriod.substring(0,2) == '04' || rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06'){content = '<div class="dropdown-content"><span class="arrow-right"></span><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-red">'+summary[rType][rPeriod][0]+'</h6><p><a class="permission'+rType+'-'+rType+' tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+06+'/'+rPeriod.substring(2,6)+'?type=">File Now</a></p>';
											}else if(rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09'){content = '<div class="dropdown-content"><span class="arrow-right"></span><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-red">'+summary[rType][rPeriod][0]+'</h6><p><a class="permission'+rType+'-'+rType+' tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+09+'/'+rPeriod.substring(2,6)+'?type=">File Now</a></p>';
											}else if(rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12'){content = '<div class="dropdown-content"><span class="arrow-right"></span><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][0]+'</span></div><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-red">'+summary[rType][rPeriod][0]+'</h6><p><a class="permission'+rType+'-'+rType+' tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+12+'/'+rPeriod.substring(2,6)+'?type=">File Now</a></p>';
											}
										}
									}else{
										var duedate="";var sm;
										if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04'){											
											if(rType == 'GSTR1'){if(rPeriod.substring(0,2) == '12'){sm=1;var nextyear = (parseInt(years)+1).toString();duedate = '11-' +sm+ "-" +nextyear;dDate = duedate;}else{sm = parseInt(rPeriod.substring(0,2)) + 1;duedate = '11-' +sm+ "-" +years;dDate = duedate;}
										       
										    }
										}
										if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
											if(rType == 'GSTR1'){sm = parseInt(rPeriod.substring(0,2)) + 1;var nextyear = (parseInt(years)+1).toString();duedate = '11-' +sm+ "-" +nextyear;dDate = duedate;}
										}
										if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04'){
											if(rType == 'GSTR2'){if(rPeriod.substring(0,2) == '12'){sm=1;}else{sm = parseInt(rPeriod.substring(0,2)) + 1;}
											duedate = '15-' +sm+ "-" +years;dDate = duedate;
										}
										}
										if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
											if(rType == 'GSTR2'){sm = parseInt(rPeriod.substring(0,2)) + 1;var nextyear = (parseInt(years)+1).toString();duedate = '15-' +sm+ "-" +nextyear;dDate = duedate;}
										}
										if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04'){
											if(rType == 'GSTR3B'){if(rPeriod.substring(0,2) == '12'){sm=1;var nextyear = (parseInt(years)+1).toString();duedate = '20-' +sm+ "-" +nextyear;dDate = duedate;}else{sm = parseInt(rPeriod.substring(0,2)) + 1;duedate = '20-' +sm+ "-" +years;dDate = duedate;}
										       
										}
										}
										if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
											if(rType == 'GSTR3B'){sm = parseInt(rPeriod.substring(0,2)) + 1;var nextyear = (parseInt(years)+1).toString();duedate = '20-' +sm+ "-" +nextyear;dDate = duedate;}
										}
										if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04' || rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
											if(rType == 'GSTR9'){var nextyear = (parseInt(years)+1).toString();  duedate = '31-' +12+ "-" +nextyear;dDate = duedate;
										}
										}
										if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04'){											
											if(rType == 'GSTR8'){if(rPeriod.substring(0,2) == '12'){sm=1;var nextyear = (parseInt(years)+1).toString();duedate = '10-' +sm+ "-" +nextyear;dDate = duedate;}else{sm = parseInt(rPeriod.substring(0,2)) + 1;duedate = '10-' +sm+ "-" +years;dDate = duedate;}
										       
										    }
										}
										if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
											if(rType == 'GSTR8'){sm = parseInt(rPeriod.substring(0,2)) + 1;var nextyear = (parseInt(years)+1).toString();duedate = '10-' +sm+ "-" +nextyear;dDate = duedate;}
										}
											if(rType == 'GSTR1'){content = '<div class="dropdown-content"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>----</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:red;">'+summary[rType][rPeriod][0]+'</span></strong></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-red">'+summary[rType][rPeriod][0]+'</h6><p><a class="permissionInvoices-Sales-View tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type=" class="tooltip_status">File Now</a></p>';
											}else if(rType == 'GSTR2'){content = '<p><a class="permissionGSTR2-GSTR2A tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type=dwnldgstr2a">View Details</a></p>';
											}else if(rType == 'GSTR9'){if(rPeriod.substring(0,2) == '03'){content = '<div class="dropdown-content"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>----</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:red;">'+summary[rType][rPeriod][0]+'</span></strong></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-red">'+summary[rType][rPeriod][0]+'</h6><p><a class="tooltip_status" href="${contextPath}/addAnnualinvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type=" class="tooltip_status">File Now</a></p>';}else{content = '<h6 style="text-align:center"> - </h6>';}
											}else if(rType == 'GSTR8'){content = '<div class="dropdown-content"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>----</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:red;">'+summary[rType][rPeriod][0]+'</span></strong></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-red">'+summary[rType][rPeriod][0]+'</h6><p><a class="permissionInvoices-Sales-View tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type=" class="tooltip_status">File Now</a></p>';
											}else{content = '<div class="dropdown-content"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>----</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:red;">'+summary[rType][rPeriod][0]+'</span></strong></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><h6 class="color-red">'+summary[rType][rPeriod][0]+'</h6><p><a class="permission'+rType+'-'+rType+' tooltip_status" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type=">File Now</a></p>';}
										}
								} else {
									if(rType == 'GSTR1'){content = '<h6 class="color-red">'+summary[rType][rPeriod][0]+'</h6><p><a class="permissionInvoices-Sales-View tooltip_status" href="#" data-toggle="tooltip" data-placement="bottom" title="This feature will be made available over coming weeks on schedule with GSTN release">File Now</a></p>';
									}else if(rType == 'GSTR2'){content = '<h6 class="color-red">'+summary[rType][rPeriod][0]+'</h6><p><a class="permissionInvoices-Purchase-View tooltip_status" href="#" data-toggle="tooltip" data-placement="bottom" title="This feature will be made available over coming weeks on schedule with GSTN release">File Now</a></p>';
									}else if(rType == 'GSTR9'){content = '<h6 class="color-red">'+summary[rType][rPeriod][0]+'</h6><p><a class="tooltip_status" href="#" data-toggle="tooltip" data-placement="bottom" title="This feature will be made available over coming weeks on schedule with GSTN release">File Now</a></p>';
									}else{content = '<h6 class="color-red">'+summary[rType][rPeriod][0]+'</h6><p><a class="permission'+rType+'-'+rType+' tooltip_status" href="#" data-toggle="tooltip" data-placement="bottom" title="This feature will be made available over coming weeks on schedule with GSTN release">File Now</a></p>';}
								}	
							}
							$('#id'+rType+parseInt(rPeriod.substring(0,2))).html(content);
							    fDate = summary[rType][rPeriod][1];
								if(fDate != undefined && fDate != null){
							      var dd = dDate.split("-");
								   var d = new Date(dd[2], dd[1] - 1, dd[0]);
								   var ff = fDate.split("-");
								   var f = new Date(ff[2], ff[1] - 1, ff[0]);
								if(d < f){
									$('.'+rType+'lateFile_class'+rPeriod.substring(0,2)).addClass("fa fa-circle");
									$('.'+rType+'lateFile_class'+rPeriod.substring(0,2)).css({'color':'red','font-size':'8px','display':'inline','vertical-align':'text-top'});
									$('.'+rType+'lateFile_text'+rPeriod.substring(0,2)).text("(Late Filed)");
								}else{
									$('.'+rType+'lateFile_text'+rPeriod.substring(0,2)).text("");
								}
							}
							//rolesPermissions();
							//clntFlngStatusRestriction = true;
							//console.log("ClntFlng Status Restriction ::"+clntFlngStatusRestriction);
							//applyReportRestrictions();
				        	if(rPeriod == monthstatus){$('#status'+rType).html(summary[rType][rPeriod][0]);
								if(rType == 'GSTR1'){$('#status'+rType).addClass("permissionInvoices-Sales-View");
								}else if(rType == 'GSTR2'){$('#status'+rType).addClass("permissionInvoices-Purchase-View");
								}else{$('#status'+rType).addClass('permission'+rType+'-'+rType);}
								if(summary[rType][rPeriod][0] != 'Pending') {$('#status'+rType).addClass("color-green");}else{$('#status'+rType).removeClass('color-green').addClass("text-danger");}
							}
						if(years != '2017'){
							if(currentMonths <= 3 && years == currentYear-1 && years <= currentYear){
								if(rType != 'GSTR9'){
									if(filingOption == 'Quarterly' && rType == 'GSTR1'){
										if(currentMonths >= 4){
											if(currentMonths <= 4 && currentMonths < 6) { 
												$('#id'+rType+'4').html('<h6 style="text-align:center"> - </h6>');
											}else if(currentMonths <= 7 && currentMonths < 9){
												$('#id'+rType+'7').html('<h6 style="text-align:center"> - </h6>');
											}else if(currentMonths <= 10 && currentMonths < 12){
												$('#id'+rType+'10').html('<h6 style="text-align:center"> - </h6>');
											}
										}else if(currentMonths < 3){
											$('#id'+rType+'1').html('<h6 style="text-align:center"> - </h6>');
										}
									}else{
										if(currentMonths >= 4) {
											for(var index = currentMonths+1; index <= 12; index++) {$('#id'+rType+index).html('<h6 style="text-align:center"> - </h6>');}
											$('#id'+rType+'1').html('<h6 style="text-align:center"> - </h6>');$('#id'+rType+'2').html('<h6 style="text-align:center"> - </h6>');$('#id'+rType+'3').html('<h6 style="text-align:center"> - </h6>');
										} else if(currentMonths < 3) {
											if(years != '2017' && currentMonths == '1'){$('#id'+rType+parseInt('2')).html('<h6 style="text-align:center"> - </h6>');$('#id'+rType+parseInt('3')).html('<h6 style="text-align:center"> - </h6>');}
											if(years != '2017' && currentMonths == '2'){$('#id'+rType+parseInt('3')).html('<h6 style="text-align:center"> - </h6>');}
										}
									}
								}		
							}else if(years >= currentYear){
								if(rType != 'GSTR9'){
									if(currentMonths >= 4) {
										for(var index = currentMonths+1; index <= 12; index++) {$('#id'+rType+index).html('<h6 style="text-align:center"> - </h6>');}
										$('#id'+rType+'1').html('<h6 style="text-align:center"> - </h6>');$('#id'+rType+'2').html('<h6 style="text-align:center"> - </h6>');$('#id'+rType+'3').html('<h6 style="text-align:center"> - </h6>');
									} else if(currentMonths < 3) {
										if(years != '2017' && currentMonths == '1'){$('#id'+rType+parseInt('2')).html('<h6 style="text-align:center"> - </h6>');$('#id'+rType+parseInt('3')).html('<h6 style="text-align:center"> - </h6>');}
										if(years != '2017' && currentMonths == '2'){$('#id'+rType+parseInt('3')).html('<h6 style="text-align:center"> - </h6>');}
									}
								}
							}
						
						}
						if(years == '2017'){$('#id'+rType+parseInt('4')).html('<h6 style="text-align:center"> - </h6>');$('#id'+rType+parseInt('5')).html('<h6 style="text-align:center"> - </h6>');$('#id'+rType+parseInt('6')).html('<h6 style="text-align:center"> - </h6>');$('#id'+rType+parseInt('7')).html('<h6 style="text-align:center"> - </h6>');}
					});
				});
						clntFlngStatusRestriction = true;
						//console.log("ClntFlng Status Restriction ::"+clntFlngStatusRestriction);
						applyReportRestrictions();
			}
		}
		function updateReturnPeriod(eDate) {
			$('body').css('height','auto');$('body').css('overflow','auto');
			var month = eDate.getMonth()+1;selectedMonth = month;var date = ((''+eDate.getDate()).length<2 ? '0' : '') + eDate.getDate() + '-'+ ((''+month).length<2 ? '0' : '') + month + '-' + eDate.getFullYear();
			$('#idSummaryHeader').html(' '+monthNames[month-1]+' - '+eDate.getFullYear());
			var sYear = eDate.getFullYear();
			updateYearlyStatus(sYear,month);
			if(month <= 3) {sYear--;}		
			$.ajax({
				url: "${contextPath}/mdfyclntreturns?clientid=${client.id}&date="+date,
				async: true,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(summary) {
					if(summary) {
						$.each(summary, function(key, value){$('#date'+key).html(value.duedate);$('#val'+key).html(value.fieldValue);});
						OSREC.CurrencyFormatter.formatAll({selector: '.ind_formats'});
					}
				}
			});
			summaryReport(sYear);$('#financialYear').val(sYear);
		}
		$('#financialYear1').change(function() {var finYear = $(this).val();updateYearlyStatus(finYear,4);});
		$('#financialYear').change(function() {var finYear = $(this).val();updateYearlyStatus(finYear,4);summaryReport(finYear);});
		function summaryReport(finYear){
			var excelurl = "${contextPath}/dwnldFinancialSummaryxls/${id}/${client.id}/"+finYear;
			$('.excel_btn').attr("href",excelurl);
			$.ajax({
				url: "${contextPath}/mdfyclntMonthlyreturns?clientid=${client.id}&year="+finYear,
				async: true,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(summary) {
					$.each(summary, function(key, value){
							if(key != 'totals'){
								if(key == '1' || key == '2' || key =='3'){	
									var finYears = parseInt(finYear)+1;
									$('#sales'+key).html('<a class="permissionInvoices-Sales-View" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/SalesRegister/'+key+'/'+finYears+'?type="><span class="ind_formatss" style="color:black" id="sal'+key+'">'+value.Sales+'</span></a>');
									$('#purchase'+key).html('<a class="permissionInvoices-Purchase-View" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/PurchaseRegister/'+key+'/'+finYears+'?type="><span class="ind_formatss" style="color:black" id="pur'+key+'">'+value.Purchase+'</span></a>');
									$('#expense'+key).html('<a class="permissionInvoices-Purchase-View" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/PurchaseRegister/'+key+'/'+finYears+'?type=Exp"><span class="ind_formatss" style="color:black" id="pur'+key+'">'+value.Expenses+'</span></a>');
								}else{
									$('#sales'+key).html('<a class="permissionInvoices-Sales-View" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/SalesRegister/'+key+'/'+finYear+'?type="><span class="ind_formatss" style="color:black" id="sal'+key+'">'+value.Sales+'</span></a>');
									$('#purchase'+key).html('<a class="permissionInvoices-Purchase-View" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/PurchaseRegister/'+key+'/'+finYear+'?type="><span class="ind_formatss" style="color:black" id="pur'+key+'">'+value.Purchase+'</span></a>');
									$('#expense'+key).html('<a class="permissionInvoices-Purchase-View" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/PurchaseRegister/'+key+'/'+finYear+'?type=Exp"><span class="ind_formatss" style="color:black" id="pur'+key+'">'+value.Expenses+'</span></a>');
								}
								$('#cumtax'+key).html(value.cummulativeTax);
								if(value.Balance < 0){$('#bal'+key).css("color","red");}else{$('#bal'+key).css("color","#000");}
								$('#bal'+key).html(value.Balance);$('#bal'+key).attr("data-original-title","Sales : "+formatNumber(value.Sales)+" - (Purchase : "+formatNumber(value.Purchase)+" + Expenses : "+formatNumber(value.Expenses)+") = "+formatNumber(value.Balance));$('#salestax'+key).html(value.SalesTax);$('#purchasetax'+key).html(value.PurchaseTax);$('#exempted'+key).html(value.exempted);$('#tcs'+key).html(value.tcsamount);$('#ptcs'+key).html(value.ptcsamount);$('#tds'+key).html(value.tdsamount);$('#tax'+key).html(value.Tax);$('#tax'+key).attr("data-original-title","Sales GST : "+formatNumber(value.SalesTax)+" - ITC : "+formatNumber(value.PurchaseTax)+" = "+formatNumber(value.Tax));
							}else{
								$('.ytdSales').html(parseFloat(summary[key].totSales));
								$('.ytdPurchases').html(parseFloat(summary[key].totpurchase));
								$('.ytdExpenses').html(parseFloat(summary[key].totexpense));
								$('.ytdBalance').html(parseFloat(summary[key].totbalance));
								$('.ytdSalestax').html(parseFloat(summary[key].totSalesTax));
								$('.ytdPurchasetax').html(parseFloat(summary[key].totPurchasetax));
								$('.ytdTax').html(parseFloat(summary[key].totTax));
								$('.ytdexempted').html(parseFloat(summary[key].totExempted));
								$('.ytdtcs').html(parseFloat(summary[key].totTcsAmount));
								$('.ytdptcs').html(parseFloat(summary[key].ptotTcsAmount));
								$('.ytdtds').html(parseFloat(summary[key].totTdsAmount));
								$('.ytdCumTax').html(parseFloat(summary[key].totTax));
							}
						});
						OSREC.CurrencyFormatter.formatAll({selector: '.ind_formatss'});
						summaryReportRestriction = true;
						//console.log("Summary Report Restriction ::"+summaryReportRestriction);
						applyReportRestrictions();
						//rolesPermissions();
				}
			});
		}
		function applyReportRestrictions(){
			if(clntFlngStatusRestriction && summaryReportRestriction){
				clntFlngStatusRestriction = false;
				summaryReportRestriction = false;
				//console.log('Apply Restrictions');
				rolesPermissions();
			}
			if(clntFlngStatusRestriction && refreshClntStatusSmryRestriction){
				//console.log("refresh ClntStatusSmry Restriction ::"+refreshClntStatusSmryRestriction);
				refreshClntStatusSmryRestriction = false;
				clntFlngStatusRestriction = false;
				rolesPermissions();
			}
		}
	function yearlyfunction(month_year) {
		var x=document.getElementById("summary1");var y=document.getElementById("summary2");
		$.ajax({
				url: "${contextPath}/mdfyclntReportView?clientid=${client.id}&reportView="+month_year,
				dataType: 'json',
				type: 'POST',
				cache: false,
				success : function(summary) {}
		});
		/* if(month_year == 'year'){y.style.display = "none";x.style.display = "block";
		}else if(month_year == 'month'){x.style.display = "none";y.style.display = "block";} */
	}
	$('td:last-child').hover(function () {
       $(this).tooltip({ position: "left" });$(this).children(':nth-child(2)').children(':nth-child(1)').attr('data-placement','left');$('.tooltip-inner').css("background-color","white");$('.tooltip-inner').css("color","black");
    });
	$('.filing-status-table tr td:nth-child(13) , .filing-status-table tr td:nth-child(12) , .filing-status-table tr td:nth-child(11)').hover(function() {
			$('.dropdown:hover .dropdown-content').css({'right':'80px','left':'unset'});$(this).find('span:first').removeClass('arrow-right').addClass('arrow-left');
		}, function() {$('.dropdown:hover .dropdown-content').css({'right':'unset','left':'80%'});$(this).find('span:first').addClass('arrow-right').removeClass('arrow-left');});
	$(document).ready(function() {
    var table = $('#reportTable2').DataTable( {
		"paging": false,
        scrollX:        true,
        scrollCollapse: true,
		ordering: false,
		info:false,
		filter:false,
		lengthChange: false,
		fixedColumns:{
		leftColumns:1,
		rightColumns:1
		}
    });
});
	function ajaxFunction(userid,fullname,usertype,clientid,financialyear){
		var types=['GSTR1','GSTR2A','GSTR3B'];var date = new Date();var currentyear = date.getFullYear();var currentmonth = date.getMonth()+1;
		for(var i=0;i<types.length;i++){
			var returntype=types[i];
			if(currentyear == financialyear){
				for (var month = 4; month <=currentmonth; month++) {
					var nextfinancialYear = financialyear;
					if(month <=3){nextfinancialYear = parseInt(financialyear)+1;
					}else{nextfinancialYear = financialyear;}
					$.ajax({
						url : '${contextPath}/downloadsyncdata/'+userid+'/'+fullname+'/'+usertype+'/'+returntype+'/'+clientid+'/'+month+'/'+nextfinancialYear,
						async: false,
						type: 'GET',
						success : function(response) {
								if(response == 'success'){$("#"+returntype+month).html("Downloaded").css({"color": "green","font-size": "14px"});
								}else{$("#"+returntype+month).html("Failed").css({"color": "red","font-size": "14px"});}
						},error : function(response) {}
					});
				}
				$('#syncinfo_span').text('Successfully Downloaded GSTN Data for the year of '+ $("#syncfinancialYear option:selected").text()).css({'color':'green'});;
				successNotification('Successfully Downloaded GSTN Data for the year of '+ $("#syncfinancialYear option:selected").text());
			}else if(financialyear == '2017'){
				for (var month =1; month <=12; month++) {
					var nextfinancialYear = financialyear;
					if(month <=3){nextfinancialYear = parseInt(financialyear)+1;
					}else{nextfinancialYear = financialyear;}
					if(month<=3  || month>=7){
						$.ajax({
							url : '${contextPath}/downloadsyncdata/'+userid+'/'+fullname+'/'+usertype+'/'+returntype+'/'+clientid+'/'+month+'/'+nextfinancialYear,
							async: false,
							type: 'GET',
							success : function(response) {$("#"+returntype+month).html("downloaded").css("color","green");
	               			},error : function(response) {}
						});
					}
				}
				$('#syncinfo_span').text('Successfully Downloaded GSTN Data for the year of '+ $("#syncfinancialYear option:selected").text()).css({'color':'green'});;
				successNotification('Successfully Downloaded GSTN Data for the year of '+ $("#syncfinancialYear option:selected").text());
			}else{
				for (var month =1; month <=12; month++) {
					var nextfinancialYear = financialyear;
					if(month <=3){nextfinancialYear = parseInt(financialyear)+1;
					}else{nextfinancialYear = financialyear;}
					$.ajax({
						url : '${contextPath}/downloadsyncdata/'+userid+'/'+fullname+'/'+usertype+'/'+returntype+'/'+clientid+'/'+month+'/'+nextfinancialYear,
						async: false,
						type: 'GET',
						success : function(response) {$("#"+returntype+month).html("downloaded").css("color","green");
               			},error : function(response) {}
					});
				}
				$('#syncinfo_span').text('Successfully Downloaded GSTN Data for the year of '+ $("#syncfinancialYear option:selected").text()).css({'color':'green'});;
				successNotification('Successfully Downloaded GSTN Data for the year of '+ $("#syncfinancialYear option:selected").text());
			}
		}
		$(".customtable.db-ca-view.tabtable1").css('opacity','1');$("#progress-bar").addClass('d-none');
	}
	function syncwithcontinue(userid,fullname,usertype,clientid){
		$.ajax({
			url : '${contextPath}/subscriptiondata/'+userid+'/${client.id}/${month}/${year}',
			async: false,
			type: 'GET',
			beforeSend: function () {$("#progress-bar").removeClass('d-none');
			$(".customtable.db-ca-view.tabtable1").css('opacity','0.4');},
			success : function(response) {
				if(response == "OTP_VERIFIED"){
					var financialyear=$('#syncfinancialYear').val();
					setTimeout(function(){ajaxFunction(userid,fullname,usertype,clientid,financialyear);
					},100);
				}else if(response == "expired"){
					$("#progress-bar").addClass('d-none');$(".customtable.db-ca-view.tabtable1").css('opacity','1');$('#syncinfo_span').text('Your subscription has expired.').css({'color':'red'});
					errorNotification('Your subscription has expired. Kindly <a href="${contextPath}/dbllng/${id}/${fullname}/${usertype}/${month}/${year}" class="btn btn-sm btn-blue-dark">Subscribe</a> to proceed further! ');
				}else{
					$("#progress-bar").addClass('d-none');$(".customtable.db-ca-view.tabtable1").css('opacity','1');$('#syncinfo_span').text('Your OTP Session Expired. please verify your otp').css('color','red');
					errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP_sync(this)">Verify Now</a> to proceed further.');
				}
			},error : function(response) {}
		});
	}
	$('#syncfinancialYear').change(function() {$("#syncdataTable tbody tr td").html('-');});
	function invokeOTP_sync(btn) {
		$(btn).addClass('btn-loader');
		var state = '<c:out value="${client.statename}"/>';var gstname = '<c:out value="${client.gstname}"/>';var gstnnumber = '<c:out value="${client.gstnnumber}"/>';
		$.ajax({
			url: "${contextPath}/verifyotp?state="+state+"&gstName="+gstname,
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				uploadResponse = response;
				if(uploadResponse.status_cd == '1') {$('#otpModal').modal("show");$('.syn_data_btn').removeClass('disable');
				} else if(uploadResponse.error && uploadResponse.error.error_cd == 'AUTH4037') {$('#enableAccessModal').modal('show');$('#idClientError').html("We noticed that your GSTIN <strong>( "+gstnnumber+" )</strong> doesn't have API Access with the GSTN Portal Login/User Name <strong>"+gstname+"</strong> Please enable the API access and update GSTN User Name correctly, Please follow below steps.");$('#gstnUserId').val(gstname);
				} else if(uploadResponse.error && uploadResponse.error.message) {errorNotification(uploadResponse.error.message);}
			$(btn).removeClass('btn-loader'); 
			},
			error : function(e, status, error) {$(btn).removeClass('btn-loader');if(e.responseText) {errorNotification(e.responseText);}}
		});
	}
	function otpTryAgain(apiAcessStatus){
		$('#cotp_Msg').text('').css("display","none");$('#otp_Msg').text('').css("display","none");var state = '<c:out value="${client.statename}"/>';var gstname = '<c:out value="${client.gstname}"/>';
		if(apiAcessStatus == 'apiEnabled'){$("#otpEntryForm")[0].reset();
		}else{$("#accessotpEntryForm")[0].reset();$('#gstnUserId').val(gstname);}
		$.ajax({
			url: "${contextPath}/verifyotp?state="+state+"&gstName="+gstname,
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {uploadResponse = response;
			},
			error : function(e, status, error) {if(e.responseText) {$('#idClientError').html(e.responseText);}}
		});
	}
	function showOtp(){
		var state = '<c:out value="${client.statename}"/>';var gstname = '<c:out value="${client.gstname}"/>';
		$.ajax({
			url: "${contextPath}/verifyotp?state="+state+"&gstName="+gstname,
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {uploadResponse = response;$('.gstn-otp-wrap').show();
			},
			error : function(e, status, error) {if(e.responseText) {$('#idClientError').html(e.responseText);}}
		});
	}
	function validOtp() {
		var gstname = '<c:out value="${client.gstname}"/>';var otp1 = $('#accessotp1').val();var otp2 = $('#accessotp2').val();var otp3 = $('#accessotp3').val();var otp4 = $('#accessotp4').val();var otp5 = $('#accessotp5').val();var otp6 = $('#accessotp6').val();
		if(otp1=="" || otp2=="" || otp3=="" || otp4=="" || otp5=="" || otp6==""){$('#otp_Msg').text('Please Enter otp').css("display","block");
		}else{
			var otp = otp1+otp2+otp3+otp4+otp5+otp6;var gstnnumber = '<c:out value="${client.gstnnumber}"/>';var pUrl = "${contextPath}/ihubauth/"+otp;
			$("#accessotpEntryForm")[0].reset();
			$.ajax({
				type: "POST",
				url: pUrl,
				async: false,
				cache: false,
				data: JSON.stringify(uploadResponse),
				dataType:"json",
				contentType: 'application/json',
				success : function(authResponse) {
					if(authResponse == null || authResponse == ''){$('#otp_Msg').text('We noticed your Internet is slow,Please try again').css("display","block");
					}else{if(authResponse.status_cd == '1') {$('.gstn-otp-wrap').hide();closeNotifications();$('.syn_data_btn').removeClass('disable');$('#idVerifyClient').parent().show();$('#idVerifyClient').html("Verified OTP Number successfully. Your User Name for GSTN Number (<strong>"+gstnnumber+"</strong>) verified.");
					}else{$('#gstnUserId').val(gstname);$('#otp_Msg').text('Please Enter Valid Otp').css('display','block');}}
				},
				error : function(e, status, error) {if(e.responseText) {$('#idClientError').html(e.responseText);}}
			});
		}
	}
	function validateOtp() {
		var otp1 = $('#otp1').val();var otp2 = $('#otp2').val();var otp3 = $('#otp3').val();var otp4 = $('#otp4').val();var otp5 = $('#otp5').val();var otp6 = $('#otp6').val();
		if(otp1=="" || otp2=="" || otp3=="" || otp4=="" || otp5=="" || otp6==""){$('#cotp_Msg').text('Please Enter otp').css("display","block");
		}else{
			var otp = otp1+otp2+otp3+otp4+otp5+otp6;var pUrl = "${contextPath}/ihubauth/"+otp;
			$("#otpEntryForm")[0].reset();
			$.ajax({
				type: "POST",
				url: pUrl,
				async: false,
				cache: false,
				data: JSON.stringify(uploadResponse),
				dataType:"json",
				contentType: 'application/json',
				success : function(authResponse) {
					if(authResponse == null || authResponse == ''){$('#cotp_Msg').text('We noticed your Internet is slow,Please try again').css("display","block");
					}else{if(authResponse.status_cd == '1') {$('#otpModalClose').click();$('.syn_data_btn').removeClass('disable');closeNotifications();}else{$('#cotp_Msg').text('Please Enter Valid otp').css("display","block");}}
				},
				error : function(e, status, error) {
					$('#otpModalClose').click();if(e.responseText) {errorNotification(e.responseText);}
				}
			});
		}
	}
	$(".otp_form_input .form-control").keyup(function () {
		if (this.value.length == this.maxLength) {$(this).next().next('.form-control').focus();}
	});
	$('.otp_form_input .form-control').keydown(function(e) {
	    if ((e.which == 8 || e.which == 46) && $(this).val() =='') {$(this).prev('input').focus();$(this).prev().prev('.form-control').focus();}
	});
	function syncinfo(){$('#syncinfo_span').text('');var currentyear=$('#syncfinancialYear').val();currentFinancialInfo(currentyear);var pUrl="${contextPath}/syncinfo/${userdetails.id}/${client.id}/"+currentyear;syncInfoAjaxFuntion(pUrl,currentyear);}
	function currentFinancialInfo(syncFinancialYear){
		var currentyear=new Date().getFullYear();var currentmonth=new Date().getMonth()+1;var types=['GSTR1','GSTR2A','GSTR3B'];var changeyear=parseInt(syncFinancialYear)+1;
		if(currentyear ==  syncFinancialYear){
			for(var i=0;i<types.length;i++){
				for(var j=4;j<=currentmonth;j++){
					$('#'+types[i]+j).html('Not Download<br/><a href="#" onclick="'+types[i]+'_download('+'\''+j+'\',\''+currentyear+'\')" style="font-size: 13px;">download</a>').css({"color": "red","font-size": "14px"});
				}
			}
		}else if(syncFinancialYear == '2017'){
			for(var i=0;i<types.length;i++){
				for(var j=1;j<=12;j++){
					if(j<=3){
						$('#'+types[i]+j).html('Not Download<br/><a href="#" onclick="'+types[i]+'_download('+'\''+j+'\',\''+changeyear+'\')" style="font-size: 13px;">download</a>').css({"color": "red","font-size": "14px"});
					}else if(j>=7){
						$('#'+types[i]+j).html('Not Download<br/><a href="#" onclick="'+types[i]+'_download('+'\''+j+'\',\''+syncFinancialYear+'\')" style="font-size: 13px;">download</a>').css({"color": "red","font-size": "14px"});
					}
				}
			}
		}else{
			for(var i=0;i<types.length;i++){
				for(var j=1;j<=12;j++){
					if(j<=3){$('#'+types[i]+j).html('Not Download<br/><a href="#" onclick="'+types[i]+'_download('+'\''+j+'\',\''+changeyear+'\')" style="font-size: 13px;">download</a>').css({"color": "red","font-size": "14px"});
					}else{$('#'+types[i]+j).html('Not Download<br/><a href="#" onclick="'+types[i]+'_download('+'\''+j+'\',\''+syncFinancialYear+'\')" style="font-size: 13px;">download</a>').css({"color": "red","font-size": "14px"});}
				}
			}
		}
	}
	$('#syncfinancialYear').change(function() {$('#syncinfo_span').text('');
		currentFinancialInfo($('#syncfinancialYear').val());var pUrl="${contextPath}/syncinfo/${userdetails.id}/${client.id}/"+$('#syncfinancialYear').val();syncInfoAjaxFuntion(pUrl,$('#syncfinancialYear').val());
	});
	function syncInfoAjaxFuntion(purl,financialYear){
		var currentyear=new Date().getFullYear();var currentmonth=new Date().getMonth()+1;
		$.ajax({
			type: "GET",
			url: purl,
			contentType: 'application/json',
			success : function(responseSummary) {
				if(responseSummary) {
					Object.keys(responseSummary).forEach(function(rPeriod) {
						var month = parseInt(responseSummary[rPeriod].substring(0,2));
						if(currentyear == financialYear){
							if(month <= currentmonth){
								if(rPeriod.substring(0,5) == 'GSTR1'){$('#'+rPeriod).html('Downloaded<br/><a href="#" onclick="'+rPeriod.substring(0,5)+'_download('+'\''+responseSummary[rPeriod].substring(0,2)+'\',\''+responseSummary[rPeriod].substring(2)+'\')" style="font-size: 13px;">download</a>').css({"color": "green","font-size": "14px"});																
								}else{$('#'+rPeriod).html('Downloaded<br/><a href="#" onclick="'+rPeriod.substring(0,6)+'_download('+'\''+responseSummary[rPeriod].substring(0,2)+'\',\''+responseSummary[rPeriod].substring(2)+'\')" style="font-size: 13px;">download</a>').css({"color": "green","font-size": "14px"});}	
							}
						}else if(financialYear == '2017'){
							if(responseSummary[rPeriod].substring(0,2)<=3 || responseSummary[rPeriod].substring(0,2)>=7){
								if(rPeriod.substring(0,5) == 'GSTR1'){$('#'+rPeriod).html('Downloaded<br/><a href="#" onclick="'+rPeriod.substring(0,5)+'_download('+'\''+responseSummary[rPeriod].substring(0,2)+'\',\''+responseSummary[rPeriod].substring(2)+'\')" style="font-size: 13px;">download</a>').css({"color": "green","font-size": "14px"});																
								}else{$('#'+rPeriod).html('Downloaded<br/><a href="#" onclick="'+rPeriod.substring(0,6)+'_download('+'\''+responseSummary[rPeriod].substring(0,2)+'\',\''+responseSummary[rPeriod].substring(2)+'\')" style="font-size: 13px;">download</a>').css({"color": "green","font-size": "14px"});}	
							}
						}else{
							if(rPeriod.substring(0,5) == 'GSTR1'){$('#'+rPeriod).html('Downloaded<br/><a href="#" onclick="'+rPeriod.substring(0,5)+'_download('+'\''+responseSummary[rPeriod].substring(0,2)+'\',\''+responseSummary[rPeriod].substring(2)+'\')" style="font-size: 13px;">download</a>').css({"color": "green","font-size": "14px"});																
							}else{$('#'+rPeriod).html('Downloaded<br/><a href="#" onclick="'+rPeriod.substring(0,6)+'_download('+'\''+responseSummary[rPeriod].substring(0,2)+'\',\''+responseSummary[rPeriod].substring(2)+'\')" style="font-size: 13px;">download</a>').css({"color": "green","font-size": "14px"});}
						}
					});
				}
			},error : function(error) {}
		});
	}
    </script>
    <script type="text/javascript">
    var subscriptionresponse = "";
    function subscription(){
    	$.ajax({
			url : "${contextPath}/subscriptiondata/${id}/${client.id}/${month}/${year}",
			async: false,
			type: 'GET',
			success : function(response) {
				subscriptionresponse = response;
				if(response == "OTP_VERIFIED"){
				}else if(response == "expired"){errorNotification('Your subscription has expired. Kindly <a href="${contextPath}/dbllng/${id}/${fullname}/${usertype}/${month}/${year}" class="btn btn-sm btn-blue-dark">Subscribe</a> to proceed further! ');
				}else{errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP_sync(this)">Verify Now</a> to proceed further.');
				}
			},error : function(response) {}
		});
    }
		var cMonths = ['non','Jan','Feb','Mar','Apr','May','June','July','Aug','Sept','Oct','Nov','Dec'];
	    function GSTR3B_download(mnth,year){
	    	var mnth=parseInt(mnth);
	    	subscription();
	    	if(subscriptionresponse == 'OTP_VERIFIED'){
				 $.ajax({
					url: "${contextPath}/addsupinvoice_reports/${id}/${fullname}/${usertype}/${client.id}/"+mnth+"/"+year,
					contentType: 'application/json',
					beforeSend: function () {$("#progress-bar").removeClass('d-none');
					$(".customtable.db-ca-view.tabtable1").css('opacity','0.4');},
					success : function(data) {
						$("#progress-bar").addClass('d-none');$(".customtable.db-ca-view.tabtable1").css('opacity','1');$('#syncinfo_span').text('GSTR3B Invoices Downloaded from GSTN Successfully for '+(cMonths[mnth]+'-'+year)).css({'color':'green'});$('#GSTR3B'+mnth).html('Downloaded<br/><a href="#" onclick="GSTR3B_download('+'\''+mnth+'\',\''+year+'\')" style="font-size: 13px;">Download</a>').css({"color": "green","font-size": "14px"});
				    }
				});
	    	}
		}
	   	function GSTR2A_download(mnth,year){
	   		var mnth=parseInt(mnth);
	   		subscription();
	    	if(subscriptionresponse == 'OTP_VERIFIED'){
		    	$.ajax({
		    		url: "${contextPath}/dwnldgstr2Asummary/${id}/${client.id}/"+mnth+"/"+year,
		    		contentType: 'application/json',
		    		beforeSend: function () {$("#progress-bar").removeClass('d-none');
					$(".customtable.db-ca-view.tabtable1").css('opacity','0.4');},
		    		success : function(data) {
		    			$("#progress-bar").addClass('d-none');$(".customtable.db-ca-view.tabtable1").css('opacity','1');$('#syncinfo_span').text('GSTR2A Invoices Downloaded from GSTN Successfully for '+(cMonths[mnth]+'-'+year)).css({'color':'green'});	$('#GSTR2A'+mnth).html('Downloaded<br/><a href="#" onclick="GSTR2A_download('+'\''+mnth+'\',\''+year+'\')" style="font-size: 13px;">Download</a>').css({"color": "green","font-size": "14px"});
		    	    }
		    	});	
	    	}
	    }	
	   	function GSTR1_download(mnth,year){
	   		var mnth=parseInt(mnth);
	   		subscription();
	    	if(subscriptionresponse == 'OTP_VERIFIED'){
		    	$.ajax({
		    		url: "${contextPath}/downloadsyncgstr1/${id}/${client.id}/"+mnth+"/"+year,
		    		contentType: 'application/json',
		    		beforeSend: function () {$("#progress-bar").removeClass('d-none');
					$(".customtable.db-ca-view.tabtable1").css('opacity','0.4');},
		    		success : function(data) {
		    			$("#progress-bar").addClass('d-none');$(".customtable.db-ca-view.tabtable1").css('opacity','1');$('#syncinfo_span').text('GSTR1 Invoices Downloaded from GSTN Successfully for '+(cMonths[mnth]+'-'+year)).css({'color':'green'});$('#GSTR1'+mnth).html('Downloaded<br/><a href="#" onclick="GSTR1_download('+'\''+mnth+'\',\''+year+'\')" style="font-size: 13px;">Download</a>').css({"color": "green","font-size": "14px"});
		    	    }
		    	});	
	    	}
	    }
	   	function refreshClientStatusSummary(){
	   		var financialyear=$('#financialYear').val();
	   		var pmonth = parseInt('${month}');
	   		$('#clntflngstatusrefreshSummary').addClass('fa-spin');
			$.ajax({
				url: "${contextPath}/clntflngstatuss/${client.id}/"+financialyear,
				async: true,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(summary) {
					var currentDates = new Date();var currentYear = currentDates.getFullYear();
					if(financialyear <= currentYear){
						pmonth = 4;
					}
					refreshClntStatusSmryRestriction = true;
					updateYearlyClntFlngStatus(financialyear , pmonth, summary);
					
					window.setTimeout(function(){
						$('#clntflngstatusrefreshSummary').removeClass("fa-spin");
							successNotification('Client Filing Status Refresh Successfully...');
						}, 4000);
				},error: function(data) {}	
			});
	   	}
	   function lateFiling(duedate,filingDate){	   	}
    </script>
	<script src="${contextPath}/static/mastergst/js/common/filedrag-map.js" type="text/javascript"></script>
</body>
</html>