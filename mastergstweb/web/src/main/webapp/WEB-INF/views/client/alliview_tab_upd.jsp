<%@page import="com.mastergst.usermanagement.runtime.support.InvoiceListSupport"%>
<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
	<c:set var="varRetType" value='<%= (String)request.getParameter("returntype") %>'/>
	<c:set var="tabName" value='<%= (String)request.getParameter("tabName") %>'/>
	<c:set var="varRetTypeCode" value='${varRetType.replaceAll(" ", "_")}'/>
	<c:set var="urlSuffix" value="${id}/${fullname}/${usertype}/${client.id}/${varRetType}"/>
	<c:set var="misurlSuffix" value="${id}/${fullname}/${usertype}/${client.id}"/>
	<c:set var="varGSTR1" value="<%=MasterGSTConstants.GSTR1%>"/>
	<c:set var="varGSTR1A" value="<%=MasterGSTConstants.GSTR1A%>"/>
	<c:set var="varGSTR2" value="<%=MasterGSTConstants.GSTR2%>"/>
	<c:set var="varGSTR2A" value="<%=MasterGSTConstants.GSTR2A%>"/>
	<c:set var="varGSTR3B" value="<%=MasterGSTConstants.GSTR3B%>"/>
	<c:set var="varGSTR4" value="<%=MasterGSTConstants.GSTR4%>"/>
	<c:set var="varGSTR6" value="<%=MasterGSTConstants.GSTR6%>"/>
	<c:set var="varGSTR5" value="<%=MasterGSTConstants.GSTR5%>"/>
	<c:set var="varANX1" value="<%=MasterGSTConstants.ANX1%>"/>
	<c:set var="varANX1A" value="<%=MasterGSTConstants.ANX1A%>"/>
	<c:set var="varANX2" value="<%=MasterGSTConstants.ANX2%>"/>
	<c:set var="varANX2A" value="<%=MasterGSTConstants.ANX2A%>"/>
	<c:set var="varEWAYBILL" value="<%=MasterGSTConstants.EWAYBILL%>"/>
	<c:set var="varPurchase" value="<%=MasterGSTConstants.PURCHASE_REGISTER%>"/>
	<c:set var="statusSubmitted" value="<%=MasterGSTConstants.STATUS_SUBMITTED%>"/>
	<c:set var="statusFiled" value="<%=MasterGSTConstants.STATUS_FILED%>"/>
	<c:set var="statusPending" value="<%=MasterGSTConstants.PENDING%>"/>
	<c:set var="varDeliverchallan" value="<%=MasterGSTConstants.DELIVERYCHALLANS%>"/>
	<c:set var="varProforma" value="<%=MasterGSTConstants.PROFORMAINVOICES%>"/>
	<c:set var="varEstimates" value="<%=MasterGSTConstants.ESTIMATES%>"/>
	<c:set var="varPurchaseOrder" value="<%=MasterGSTConstants.PURCHASEORDER%>"/>
	<c:set var="varExpenses" value="<%=MasterGSTConstants.EXPENSES%>"/>
	<style>
.dropdown:hover .dropdown-content#invDateDropdown{display:none;}; 
.reportmenu:hover .dropdown-menu#reportdrop{display:block}
.reportmenu{ border-top-right-radius: 0!important; border-bottom-right-radius: 0!important;}
.reportdrop{top: 28px;}
button#monthlydwnldxls ,#yearlydwnldxls,#customdwnldxls{margin-left: 0px;height: 30px;box-shadow:none;}
	#add_invoice_drop{height: max-content;max-height:250px;overflow-y:auto}
	.otp_form_input input[type='text']::placeholder{font-size:50px;} 
	.nav-bread{height:35px!important}
	.db-ca-wrap{padding-top: 99px!important;} 
	.form-control.input-datepicker{height: 18px;width: 123px;}
	.form-control.itc_amount{width: 45px;height: 18px;}
	input[type="date"] {position: relative; padding: 10px;}
input[type="date"]::-webkit-calendar-picker-indicator {color: transparent; background: none; z-index: 1;}
input[type="date"]:before {color: transparent;background: none;display: block; font-family: 'FontAwesome'; content: '\f073'; width: 15px; height: 20px; position: absolute; top: 9px; right: 6px; color: #999;}
/* .dataTables_wrapper .toolbar{display:inline;} */.invtabtable1{margin-bottom: 10px!important;} a.downloadbtn.disabled,a.uploadbtn.disabled { pointer-events: all; }
</style>
<script>
	</script>
	<h4 class="hdrtitlefiling" style="color: #404144; font-size: 18px;  margin-bottom: 5px; line-height: 2; display: table; width: 100%;">
	<c:if test="${otherreturn_type ne 'additionalInv'}">
		<c:if test="${varRetType eq 'Purchase Register' || varRetType eq 'Unclaimed'}">
	<label> , Invoice View By</label>
	<select class="invoiceview" style="font-size: 16px;margin-left: 5px;" onchange="invoiceviewByTrDate(this.value,'${id}','${varRetType}','${varRetTypeCode}','${otherreturnType}')"><option value="Invoice Date">Invoice Date</option><option value="Transaction Date">Transaction Date</option></select></c:if>
	<c:if test="${varRetType eq 'GSTR1' || varRetType eq varANX1 || varRetType eq varANX2}">
	<label> , Invoice View By</label>
	<select class="invoiceview1" style="font-size: 16px;margin-left: 5px;" onchange="invoiceviewByfp('${id}','${varRetType}','${varRetTypeCode}','${otherreturnType}','${client.prevPendingInv}','${tallyHsnSummary}')"><option value="Return Period">Return Period</option><option value="Invoice Date">Invoice Date</option></select></c:if>
		<c:if test="${varRetType eq varGSTR1 || varRetType eq varGSTR4 || varRetType eq varGSTR5 || varRetType eq varGSTR6 || varRetType eq varANX1 || varRetType eq varANX2}"><span class="text-right" style="font-size:18px; margin-top:-2px; margin-left:5px">and Filing Status : 
				<span style="font-size:16px; margin-left:0px!important">
					<c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">
					<span class="color-green status-style">${client.status}</span>
					</c:if>
					<c:if test="${client.status eq statusPending || empty client.status}">
					<span class="color-yellow pen-style">Pending</span>
					</c:if>
				</span>
			</span>
			</c:if>
		</c:if>
		<c:if test="${otherreturn_type eq 'additionalInv'}">
		<c:if test="${varRetType eq varPurchase}">
		<label> , Invoice View By</label>
	<select class="invoiceview" style="font-size: 16px;margin-left: 5px;" onchange="invoiceviewByTrDate('${id}','${varRetType}','${varRetTypeCode}','${otherreturnType}')"><option value="Invoice Date">Invoice Date</option><option value="Transaction Date">Transaction Date</option></select></c:if>
		</c:if>
		<span class="summary_retperiod" style="float: left;">
			<c:choose>
			<c:when test="${varRetType eq varPurchase || varRetType eq varGSTR6}">Purchase Invoices</c:when>
			<c:when test="${varRetType eq 'Unclaimed'}">Unclaimed Invoices</c:when>
			<c:when test="${varRetType eq varGSTR2}">GSTR2 Invoices</c:when>
			<c:when test="${varRetType eq varGSTR2A}">GSTR2A Invoices</c:when>
			<c:when test="${varRetType eq varGSTR5}">GSTR5 Invoices</c:when>
			<c:when test="${varRetType eq 'DELIVERYCHALLANS'}"> Delivery Challan </c:when>
			<c:when test="${varRetType eq 'PROFORMAINVOICES'}">Proforma Invoices </c:when>
			<c:when test="${varRetType eq 'ESTIMATES'}">Estimates Invoices </c:when>
			<c:when test="${varRetType eq 'PurchaseOrder'}">Purchase Orders</c:when>
			<c:when test="${varRetType eq 'EWAYBILL'}">EWAY Bill Invoices</c:when>
			<c:when test="${varRetType eq 'ANX2'}">ANX2 Invoices</c:when>
			<c:when test="${varRetType eq 'EXPENSES'}">Expenses</c:when>
			<c:otherwise>Sales Invoices</c:otherwise>
			</c:choose>
		</span>
		<span class="text-right" style="float: right;margin-top: -19px;font-size:18px;">Total 
		<c:choose>
		<c:when test="${varRetType eq varPurchase || varRetType eq varGSTR2 || varRetType eq varGSTR6}">Claimed ITC:</c:when>
		<c:when test="${varRetType eq 'Unclaimed'}">UnClaimed ITC:</c:when>
		<c:when test="${varRetType eq varGSTR2A}">Eligible ITC:</c:when>
		<c:when test="${varRetType eq varANX2}">s:</c:when>
		<c:when test="${varRetType eq 'EWAYBILL'}">Amount:</c:when>
		<c:when test="${varRetType eq 'EXPENSES'}">Expenses</c:when>
		<c:otherwise>Sales:</c:otherwise>
		</c:choose>
			<span class="total_amount" style="font-size:27px; margin-left:0px!important">
				<span class="ind_format" id="idTabTotal${varRetTypeCode}">
				</span>
			</span>
		</span>
	</h4>
	<div class="normaltable meterialform updatefilter_summary" id="updatefilter_summary" style="display:none;">
		<div class="filter">
			<div class="noramltable-row">
				<div class="noramltable-row-hdr">Filter</div>
				<div class="noramltable-row-desc">
				<div class="sfilter">
					<span id="divFilters${varRetTypeCode}"></span>
					<span class="btn-remove-tag" onclick="clearInvFilters('${varRetType}','${otherreturnType}')">Clear All<span data-role="remove"></span></span>
				</div>
				</div>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Search Filter</div>
			<div class="noramltable-row-desc">
				<select id="multiselect${varRetTypeCode}1" class="multiselect-ui form-control" multiple="multiple">
				</select>	
				<span class="invoiceTypelist">
				<select id="multiselect${varRetTypeCode}2" class="multiselect-ui form-control" multiple="multiple">
					<c:forEach items="${InvoiceListSupport.getInvTypeList(varRetType)}" var="invTypeLi">
						<option value="${invTypeLi.key}">${invTypeLi.value}</option>
					</c:forEach>
				</select>
				</span>
				<span class="multiselectuserlist">
					<select id="multiselect${varRetTypeCode}3" class="multiselect-ui form-control" multiple="multiple">
					</select>
				</span>
				<span class="multiselectsupplierlist">
				<select id="multiselect${varRetTypeCode}4" class="multiselect-ui form-control" multiple="multiple">
				</select>
				</span>
				<select id="multiselect${varRetTypeCode}5" class="multiselect-ui form-control" multiple="multiple">
					<c:forEach items="${client.branches}" var="branch">
						<option value="${branch.name}">${branch.name}</option>
					</c:forEach>
				</select>
				<select id="multiselect${varRetTypeCode}6" class="multiselect-ui form-control" multiple="multiple">
					<c:forEach items="${client.verticals}" var="vertical">
						<option value="${vertical.name}">${vertical.name}</option>
					</c:forEach>
				</select>
				<select id="multiselect${varRetTypeCode}7" class="multiselect-ui form-control" multiple="multiple">
					<option value="Reverse">Yes</option>
					<option value="Regular">No</option>
				</select>
				<select id="multiselect${varRetTypeCode}8" class="multiselect-ui form-control" multiple="multiple">
					<option value="Matched">Matched</option>
					<option value="Round Off Matched">Round Off Matched</option>
					<option value="Matched In Other Months">Matched In Other Months</option>
					<option value="Probable Matched">Probable Matched</option>
					<option value="Manual Matched">Manual Matched</option>
					<option value="Mismatched">Mismatched</option>
					<option value="Tax Mismatched">Tax Mismatched</option>
					<option value="Invoice No Mismatched">Invoice No Mismatched</option>
					<option value="Invoice Value Mismatched">Invoice Value Mismatched</option>
					<option value="GST No Mismatched">GST No Mismatched</option>
					<option value="Invoice Date Mismatched">Invoice Date Mismatched</option>
					<c:choose>
						<c:when test="${varRetType eq varGSTR2A}">
							<option value="Not In Purchases">Not In Purchases</option>
						</c:when>
						<c:otherwise>
							<option value="Not In GSTR 2A">Not In GSTR 2A</option>
						</c:otherwise>
					</c:choose>
					
				</select>
				 <select id="multiselect${varRetTypeCode}9" class="multiselect-ui form-control" multiple="multiple">
					<option value="O">Outward</option>
					<option value="I">Inward</option>
				</select>
				<select id="multiselect${varRetTypeCode}10" class="multiselect-ui form-control" multiple="multiple"></select> 
				<select id="multiselect${varRetTypeCode}11" class="multiselect-ui form-control" multiple="multiple">
					<option value="Mismatched">Mismatched</option>
					<option value="Matched">Matched</option>
					<option value="Round Off Matched">Round Off Matched</option>
					<option value="Manual Matched">Manual Matched</option>
					<option value="Matched In Other Months">Matched In Other Months</option>
					<option value="Probable Matched">Probable Matched</option>
					<option value="Invoice No Mismatched">Invoice No Mismatched</option>
					<option value="Invoice Value Mismatched">Invoice Value Mismatched</option>
					<option value="Invoice Date Mismatched">Invoice Date Mismatched</option>
					<option value="GST No Mismatched">GST No Mismatched</option>
					<option value="Tax Mismatched">Tax Mismatched</option>
					<option value="Not In Purchases">Not In Purchases</option>
					<option value="Not In GSTR 2A">Not In GSTR 2A</option>
				</select> 
				<select id="multiselect${varRetTypeCode}12" class="multiselect-ui form-control" multiple="multiple">
				<option value="">Accepted</option><option value="">Rejected</option>
				</select> 
				<select id="multiselect${varRetTypeCode}13" class="multiselect-ui form-control" multiple="multiple">
				<option value="A">Accept</option><option value="R">Reject</option><option value="P">Pending</option>
				</select> 
				<select id="multiselect${varRetTypeCode}14" class="multiselect-ui form-control" multiple="multiple">
				<option value="Y">Filed</option><option value="N">Not Filed</option>
				</select>
			<select id="multiselect${varRetTypeCode}15" class="multiselect-ui form-control" multiple="multiple"></select>
			<select id="multiselect${varRetTypeCode}16" class="multiselect-ui form-control" multiple="multiple"></select>
			<select id="multiselect${varRetTypeCode}17" class="multiselect-ui form-control" multiple="multiple"></select>
			<select id="multiselect${varRetTypeCode}18" class="multiselect-ui form-control" multiple="multiple"></select>
			<select id="multiselect${varRetTypeCode}19" class="multiselect-ui form-control" multiple="multiple"></select> 
			<select id="multiselect${varRetTypeCode}20" class="multiselect-ui form-control" multiple="multiple">
					<option value="Not Generated">Not Generated</option>
					<option value="Generated">Generated</option>
					<option value="Cancelled">Cancelled</option>
					<option value="Rejected">Rejected</option>
				</select>
			<c:if test="${varRetType eq varGSTR2A}">
				<div id="" class="invoiceDateFilter dropdown dropdown-toggle">-  Invoice Date -</div> 
					<div class="dropdown-content invDate_dropdown" id="invDateDropdown">
						<div>
						<label for="start-date" class="mb-0 date-txt">Start Date </label><span class="sdate_msg ml-1"></span>
						<input type="text" placeholder="DD-MM-YYYY" class="form-control" id="start-date" name="startDate" pattern="[0-9]{2}/[0-9]{2}/[0-9]{4}"  value="">
						<i class="fa fa-calendar startdatewrap" style="position: absolute;top: 23%;right: 12%;"></i>
						</div>
						<div>
						<label for="end-date" class="mb-0 date-txt">End Date</label><span class="edate_msg ml-1"></span>
						<input type="text" placeholder="DD-MM-YYYY" class="form-control"  id="end-date" name="endDate" pattern="[0-9]{2}/[0-9]{2}/[0-9]{4}" value=""> 
						<i class="fa fa-calendar enddatewrap" style="position: absolute;top: 54%;right: 12%;"></i>
						</div>
						<div class="table-filter--buttons">
							<input type="button" class="btn btn-blue-dark mt-2 mr-2" id="invdate_ok" onclick="okDate()" value="OK"/>
							<input type="button" class="btn btn-blue-dark mt-2" id="invdate_cancel" onclick="cancelDate()" value="cancel">
						</div>
					</div> 
			</c:if>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Filter Summary</div>
			<div class="noramltable-row-desc">
				<div class="normaltable-col hdr" style="width:8%">Total Invoices<!-- Transactions -->
					<div class="normaltable-col-txt" id="idCount${varRetTypeCode}"></div>
				</div>
				<div class="normaltable-col hdr">Total Amount 
					<div class="normaltable-col-txt" id="idTotAmtVal${varRetTypeCode}"></div>
				</div>
				<div class="normaltable-col hdr">Total Taxable Value
					<div class="normaltable-col-txt" id="idTaxableVal${varRetTypeCode}"></div>
				</div>
				<c:if test="${varRetType eq 'GSTR1' || varRetType eq 'SalesRegister'}">
				<div class="normaltable-col hdr">Total Exempted
					<div class="normaltable-col-txt" id="idExemptedVal${varRetTypeCode}"></div>
				</div>
				</c:if>
				<div class="normaltable-col hdr">Total Tax Value
					<div class="normaltable-col-txt" id="idTaxVal${varRetTypeCode}"></div>
				</div>
				<div class="normaltable-col hdr filsummary">Total IGST
					<div class="normaltable-col-txt" id="idIGST${varRetTypeCode}"></div>
				</div>
				<div class="normaltable-col hdr filsummary">Total CGST
					<div class="normaltable-col-txt" id="idCGST${varRetTypeCode}"></div>
				</div>
				<div class="normaltable-col hdr filsummary">Total SGST
					<div class="normaltable-col-txt" id="idSGST${varRetTypeCode}"></div>
				</div>
				<div class="normaltable-col hdr filsummary">Total CESS
					<div class="normaltable-col-txt" id="idCESS${varRetTypeCode}"></div>
				</div>
				<c:if test="${varRetType eq 'GSTR2' || varRetType eq varPurchase || varRetType eq 'Unclaimed' || varRetType eq varGSTR6}">
				<div class="normaltable-col hdr">ITC Available
					<div class="normaltable-col-txt" id="idITC${varRetTypeCode}"></div>
				</div>
				</c:if>
				<c:if test="${varRetType eq 'GSTR1' || varRetType eq 'GSTR5' || varRetType eq 'GSTR4' || varRetType eq 'GSTR2' || varRetType eq varPurchase || varRetType eq 'Unclaimed' || varRetType eq varGSTR6}">
				<div class="normaltable-col hdr filsummary">
					<c:if test="${varRetType eq 'GSTR1' || varRetType eq 'GSTR5' || varRetType eq 'GSTR4'}">
						Total TCS
					</c:if>
					<c:if test="${varRetType eq 'GSTR2' || varRetType eq varPurchase || varRetType eq 'Unclaimed' || varRetType eq varGSTR6}">Total TDS
					</c:if>
					<div class="normaltable-col-txt" id="idTCSTDS${varRetTypeCode}"></div>
				</div>
				</c:if>
			</div>
		</div>
	</div>
	<div class="normaltable meterialform updateExpensefilter_summary" id="updateExpensefilter_summary" style="display:none;">
		<div class="filter efilter">
			<div class="noramltable-row">
				<div class="noramltable-row-hdr">Filter</div>
				<div class="noramltable-row-desc">
				<div class="sfilter">
					<span id="divExpFilters${varRetTypeCode}"></span>
					<span class="btn-remove-tag" onclick="clearExpFilters('${varRetType}','${otherreturnType}')">Clear All<span data-role="remove"></span></span>
				</div>
				</div>
			</div>
		</div>
			<div class="noramltable-row">
				<div class="noramltable-row-hdr">Search Filter</div>
					<div class="noramltable-row-desc">
						<select id="expMultiselect${varRetTypeCode}1" class="multiselect-ui form-control" multiple="multiple"></select>	
						<select id="expMultiselect${varRetTypeCode}2" class="multiselect-ui form-control" multiple="multiple"><option value="Cash">Cash</option><option value="Bank">Bank</option><option value="Cheque">Cheque</option></select>	
						<div id="" class="paymentDateFilter dropdown dropdown-toggle" style="display:inline;">-  Payment Date -</div> 
					<div class="dropdown-content pmntDateDropdown" id="pmntDateDropdown" style="left:25%;">
						<div>
						<label for="start-date" class="mb-0 date-txt">Start Date </label><span class="sdate_msg ml-1"></span>
						<input type="text" placeholder="DD-MM-YYYY" class="form-control expstartdate expPmntd1" id="expstartdate" name="startDate"  value="">
						<span style="position: absolute;top: 20%;right: 12%;"><i class="fa fa-calendar startExpdatewrap"></i> </span>
						</div>
						<div>
						<label for="end-date" class="mb-0 date-txt">End Date</label><span class="edate_msg ml-1"></span>
						<input type="text" placeholder="DD-MM-YYYY" class="form-control expenddate expPmntd2"  id="expenddate" name="endDate" value=""> 
						<span style="position: absolute;top: 53%;right: 12%;"><i class="fa fa-calendar endExpdatewrap"></i></span>
						</div>
						<div class="table-filter--buttons">
							<input type="button" class="btn btn-blue-dark mt-2 mr-2" id="invdate_ok" onclick="okExpDate()" value="OK"/>
							<input type="button" class="btn btn-blue-dark mt-2" id="invdate_cancel" onclick="cancelExpDate()" value="cancel">
						</div>
					</div> 	
					</div>
			</div>
			<div class="noramltable-row">
				<div class="noramltable-row-hdr">Filter Summary</div>
				<div class="noramltable-row-desc">
					<div class="normaltable-col hdr" style="width:10%">Total Expenses
						<div class="normaltable-col-txt" id="idExpCount${varRetTypeCode}"></div>
					</div>
					<div class="normaltable-col hdr" style="width:10%">Total Amount 
						<div class="normaltable-col-txt" id="idExpTotAmtVal${varRetTypeCode}"></div>
					</div>
					<div class="normaltable-col hdr" style="width:10%">
						
					</div>
			  </div>
		</div>
	</div>
	<h4 class="hdrtitle" style="margin:0px">
	<c:if test="${varRetType eq 'EWAYBILL'}">
		<a href="#" class="btn btn-greendark pull-right btn-all-iview-sales btn-sm downloadbtn permissionEwaybill_Actions-Download_Ewaybill" style="margin-bottom:5px!important;" onclick="downloadEwayBIllInv('${returntype}')">DOWNLOAD FROM EWAYBILL</a>
		<a  href="#" class="btn btn-greendark btn-all-iview-sales btn-sm permissionEwaybill_Actions-Generate_Ewaybill" id="addEwaybillinv" onClick="showInvPopup('<%=MasterGSTConstants.B2B%>','<c:out value="${varRetType}"/>',true)" link1="${contextPath}/addEwaybillinv/${urlSuffix}?stype=<%=MasterGSTConstants.EWAYBILL%>">GENERATE EWAYBILL</a>
		<%-- <a href="#" id="vehicleupdate" class="btn btn-greendark pull-right btn-all-iview-sales btn-sm permissionEwaybill_Actions-Update_Vehicle disabled" onclick="vehicleUpdate('${returntype}')">UPDATE VEHICLE</a> --%>
		<div class="dropdown pull-right permissionEwaybill_Actions" style="margin-left: 10px;">
			<div class="split-button-menu-dropdown reportmenu">
				<button class="btn btn-greendark b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" >
					<span class="showarrow">
						<i class="fa fa-caret-down"></i>
					</span>
				</button>
				<button class="btn btn-greendark reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">Actions</button>
				<div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;">
				<a href="#" onclick="vehicleUpdate('${returntype}')" class="permissionEwaybill_Actions-Update_Vehicle pull-right dropdown-item disabled" id="vehicleupdate">UPDATE VEHICLE</a>
				<a href="#" onclick="extendValidity()" class=" pull-right dropdown-item disabled" id="extendValidity">EXTEND VALIDITY</a>
				<a href="#" onclick="cancelWaybill()" class=" pull-right dropdown-item disabled" id="cancelAllEwaybills">CANCEL EWAYBILL</a>
				<a href="#" onclick="rejectWaybill()" class=" pull-right dropdown-item disabled" id="rejectAllEwaybills">REJECT EWAYBILL</a> 
				</div>
			</div>
		</div>
		<a href="#" class="btn btn-blue pull-right btn-all-iview-sales btn-sm disabled" id="addtogstr1btn" onclick="addtoGSTR1(this,true)">ADD TO SaleRegister</a>
		<a href="" data-toggle="modal" data-target="#ebillimportModal" style="margin-top:2px;height:27px;" class="btn btn-blue pull-right btn-all-iview">Import</a>
		<div class="dropdown pull-right permissionInvoices-Sales-Delete" style="margin-left: 10px;">
			<div class="split-button-menu-dropdown reportmenu">
				<button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" >
					<span class="showarrow">
						<i class="fa fa-caret-down"></i>
					</span>
				</button>
				<button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">Delete</button>
				<div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;">
				<a href="#" onclick="showDeleteAllPopup('<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>','<c:out value="${otherreturnType}"/>','selectedInvs','<c:out value="${month}"/>','<c:out value="${year}"/>')" class="permissionInvoices-Sales-Delete pull-right dropdown-item disabled" id="deleteEwaybillInvoices">DELETE</a>
				<a href="#" onclick="showDeleteAllPopup('<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>','<c:out value="${otherreturnType}"/>','deleteAll','<c:out value="${month}"/>','<c:out value="${year}"/>')" class="permissionInvoices-Sales-Delete pull-right dropdown-item deleteAllInvoices${varRetType.replaceAll(' ', '_')}">DELETE ALL</a>
				</div>
			</div>
		</div>
		 <%-- <a href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/itemwise" class="btn btn-blue excel" onClick="excelreport()">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a> --%>
		<div class="dropdown pull-right permissionExcel_Download_In_Books_And_Returns-Sales" style="margin-left: 10px;"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;"><a class="dropdown-item" id="itemWiseUrl${varRetType}" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/itemwise">ITEM WISE DOWNLOAD</a><a class="dropdown-item" id="invWiseUrl${varRetType}" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/invoicewise">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" id="fullDwnldUrl${varRetType}" href="${contextPath}/fulldwnldmonthlyxls/${id}/${client.id}/${varRetType}/${month}/${year}">ALL DETAILS DOWNLOAD</a></div></div></div>
		</c:if>
		<c:if test="${varRetType eq varPurchase}">
		<div class="dropdown pull-right permissionInvoices-Purchase-Delete" style="margin-left: 10px;">
			<div class="split-button-menu-dropdown reportmenu">
				<button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" >
					<span class="showarrow">
						<i class="fa fa-caret-down"></i>
					</span>
				</button>
				<button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">Delete</button>
				<div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;">
				<a href="#" onclick="showDeleteAllPopup('<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>','<c:out value="${otherreturnType}"/>','selectedInvs','<c:out value="${month}"/>','<c:out value="${year}"/>')" class="permissionInvoices-Purchase-Delete pull-right dropdown-item disabled" id="deletePurchaseInvoices">DELETE</a>
				<a href="#" onclick="showDeleteAllPopup('<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>','<c:out value="${otherreturnType}"/>','deleteAll','<c:out value="${month}"/>','<c:out value="${year}"/>')" class="permissionInvoices-Purchase-Delete pull-right dropdown-item deleteAllInvoices${varRetType.replaceAll(' ', '_')}">DELETE ALL</a>
				</div>
			</div>
		</div>
		<a href="" data-toggle="modal" data-target="#importModal" style="margin-top:2px" class="btn btn-blue pull-right btn-all-iview permissionGeneral-Import_Purchases <c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">disable</c:if>" onClick="updateImportModal('<%=MasterGSTConstants.PURCHASE_REGISTER%>')">Import</a>
		<a href="#" class="btn btn-blue pull-right btn-all-iview-sales" id="itcclaimbtn" data-toggle="modal" data-target="#claimModal">ITC Claim</a>
		<a href="#" class="btn btn-blue pull-right btn-all-iview-sales disabled" id="itcUnclaimbtn" onclick="clearITCValues()">ITC Unclaim</a>
		</c:if>
		<c:if test="${otherreturn_type ne 'additionalInv'}">
			<c:if test="${varRetType ne varPurchase && varRetType ne varGSTR2A && varRetType ne 'Unclaimed' && varRetType ne varProforma && varRetType ne varDeliverchallan && varRetType ne varEstimates && varRetType ne varPurchaseOrder}">
			<c:if test="${varRetType ne 'GSTR1Amnd' && varRetType ne 'EWAYBILL' && varRetType ne varANX1A}">
			<div class="dropdown pull-right <c:if test="${returntype eq varGSTR1 || returntype eq varGSTR5}">permissionGSTN_Actions-Upload_Sales</c:if> <c:if test="${returntype eq varGSTR2 || returntype eq varGSTR4 || returntype eq varGSTR6}">permissionGSTN_Actions-Upload_Purchases</c:if> <c:if test="${returntype eq varGSTR2}">disabled</c:if>" style="margin-left: 10px;">
			<div class="split-button-menu-dropdown reportmenu">
				<button class="btn btn-greendark b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #ffffff;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" >
					<span class="showarrow">
						<i class="fa fa-caret-down"></i>
					</span>
				</button>
				<button id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left; border-bottom-right-radius: 0px; border-top-right-radius: 0px;" aria-expanded="false" class="btn btn-greendark pull-right btn-all-iview-sales btn-sm uploadbtn btn btn-greendark pull-right btn-all-iview-sales btn-sm uploadbtn <c:if test="${returntype eq varGSTR1 || returntype eq varGSTR5}">permissionGSTN_Actions-Upload_Sales</c:if> <c:if test="${returntype eq varGSTR2 || returntype eq varGSTR4 || returntype eq varGSTR6}">permissionGSTN_Actions-Upload_Purchases</c:if> <c:if test="${returntype eq varGSTR2}">disabled</c:if>" <c:if test="${returntype eq varGSTR2}">data-toggle="tooltip" data-placement="top" title="GSTR2 is not Available"</c:if>><c:if test="${varRetType ne varANX2}">Upload To GSTN</c:if><c:if test="${varRetType eq varANX2}">Update To GSTN</c:if></button>
				<div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;">
				<a href="#" <c:if test="${returntype ne varGSTR2}">onclick="verifyAccess(this,'hsn')"</c:if> class="pull-right dropdown-item" id="">Upload to GSTN</a>
				<a href="#" <c:if test="${returntype ne varGSTR2}">onclick="verifyAccess(this,'nothsn')"</c:if> class="pull-right dropdown-item" id="">Upload Only HSN Summary</a>
				</div>
			</div>
		</div>
			</c:if>
			<c:if test="${varRetType ne 'EWAYBILL'}">
			<a href="#" class="btn btn-greendark <c:if test="${returntype eq varANX2}">permissionGSTN_Actions-Download_ANX2</c:if><c:if test="${returntype eq varGSTR1 || returntype eq varGSTR5 || returntype eq varANX1}"> permissionGSTN_Actions-Download_Sales</c:if> <c:if test="${returntype eq varGSTR2 || returntype eq varGSTR4 || returntype eq varGSTR6}"> permissionGSTN_Actions-Download_Purchases</c:if> pull-right btn-all-iview-sales btn-sm downloadbtn <c:if test="${returntype eq varGSTR2}">disabled</c:if>"  <c:if test="${returntype ne varGSTR2 && returntype ne varANX1}">onClick="downloadFromGSTN('<c:out value="${varRetType}"/>')" link="${contextPath}/dwnldinv/${urlSuffix}"</c:if><c:if test="${returntype eq varANX1}">onClick="downloadFromGSTN('<c:out value="${varRetType}"/>')" link="${contextPath}/dwnldAnx1inv/${urlSuffix}"</c:if>  <c:if test="${returntype eq varGSTR2}">data-toggle="tooltip" data-placement="top" title="GSTR2 is not Available"</c:if>>Download from GSTN</a>
			</c:if>
			</c:if>
		</c:if>
		<c:if test="${varRetType eq varANX2}">
		<a href="#" link="${contextPath}/reconcileinv/${urlSuffix}/${month}/${year}" onClick="reconcileGstr2a()" class="btn btn-greendark pull-right btn-all-iview-sales btn-sm reconcile_btn">Reconcile</a>
		<a href="#" class="btn btn-yellow" onclick="#">Pending</a>
		</c:if>
		<c:if test="${varRetType eq varGSTR1A || varRetType eq 'GSTR1Amnd' || varRetType eq varANX1A}">
		<a href="#" class="btn btn-red reject" onclick="processInv(this,false)" style="margin-right:2px!important">Reject</a>
		<a href="#" class="btn btn-green accept" onclick="processInv(this,true)">Accept</a>
		</c:if>
		<c:if test="${varRetType eq 'DELIVERYCHALLANS'}">
		<div class="dropdown pull-right permissionInvoices-Sales-Delete" style="margin-left: 10px;">
			<div class="split-button-menu-dropdown reportmenu">
				<button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" >
					<span class="showarrow">
						<i class="fa fa-caret-down"></i>
					</span>
				</button>
				<button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">Delete</button>
				<div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;">
				<a href="#" onclick="showDeleteAllPopup('<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>','<c:out value="${otherreturnType}"/>','selectedInvs','<c:out value="${month}"/>','<c:out value="${year}"/>')" class="permissionInvoices-Sales-Delete pull-right dropdown-item disabled" id="deleteDelChallanInvoices">DELETE</a>
				<a href="#" onclick="showDeleteAllPopup('<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>','<c:out value="${otherreturnType}"/>','deleteAll','<c:out value="${month}"/>','<c:out value="${year}"/>')" class="permissionInvoices-Sales-Delete pull-right dropdown-item deleteAllInvoices${varRetType.replaceAll(' ', '_')}">DELETE ALL</a>
				</div>
			</div>
		</div>
		<a href="#" class="btn btn-blue pull-right" onClick="showInvPopup('<%=MasterGSTConstants.DELIVERYCHALLANS%>','<c:out value="${varRetType}"/>',true)">Add Delivery Challans</a>
		</c:if>
		<c:if test="${varRetType eq 'PROFORMAINVOICES'}">
		<div class="dropdown pull-right permissionInvoices-Sales-Delete" style="margin-left: 10px;">
			<div class="split-button-menu-dropdown reportmenu">
				<button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" >
					<span class="showarrow">
						<i class="fa fa-caret-down"></i>
					</span>
				</button>
				<button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">Delete</button>
				<div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;">
				<a href="#" onclick="showDeleteAllPopup('<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>','<c:out value="${otherreturnType}"/>','selectedInvs','<c:out value="${month}"/>','<c:out value="${year}"/>')" class="permissionInvoices-Sales-Delete pull-right dropdown-item disabled" id="deleteProformaInvoices">DELETE</a>
				<a href="#" onclick="showDeleteAllPopup('<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>','<c:out value="${otherreturnType}"/>','deleteAll','<c:out value="${month}"/>','<c:out value="${year}"/>')" class="permissionInvoices-Sales-Delete pull-right dropdown-item deleteAllInvoices${varRetType.replaceAll(' ', '_')}">DELETE ALL</a>
				</div>
			</div>
		</div>
		<a href="#" class="btn btn-blue pull-right" id="cnvrt_PI_To_Invoice" onclick="cnvrt_PI_To_Invoice()">Convert To Invoice</a>
		<a href="#" class="btn btn-blue pull-right" link='${contextPath}/syncinv/${urlSuffix}' data-toggle="modal" data-target="#proformainvoiceModal" onClick="showInvPopup('<%=MasterGSTConstants.PROFORMAINVOICES%>','<c:out value="${varRetType}"/>',true)">Add Proforma Invoices</a>
		</c:if>
		<c:if test="${varRetType eq 'ESTIMATES'}">
		<div class="dropdown pull-right permissionInvoices-Sales-Delete" style="margin-left: 10px;">
			<div class="split-button-menu-dropdown reportmenu">
				<button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" >
					<span class="showarrow">
						<i class="fa fa-caret-down"></i>
					</span>
				</button>
				<button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">Delete</button>
				<div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;">
				<a href="#" onclick="showDeleteAllPopup('<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>','<c:out value="${otherreturnType}"/>','selectedInvs','<c:out value="${month}"/>','<c:out value="${year}"/>')" class="permissionInvoices-Sales-Delete pull-right dropdown-item disabled" id="deleteEstimateInvoices">DELETE</a>
				<a href="#" onclick="showDeleteAllPopup('<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>','<c:out value="${otherreturnType}"/>','deleteAll','<c:out value="${month}"/>','<c:out value="${year}"/>')" class="permissionInvoices-Sales-Delete pull-right dropdown-item deleteAllInvoices${varRetType.replaceAll(' ', '_')}">DELETE ALL</a>
				</div>
			</div>
		</div>
		<a href="#" class="btn btn-blue pull-right" link='${contextPath}/syncinv/${urlSuffix}' data-toggle="modal" data-target="#estimateModal" onClick="showInvPopup('<%=MasterGSTConstants.ESTIMATES%>','<c:out value="${varRetType}"/>',true)">Add Estimates</a>
		</c:if>
		<c:if test="${varRetType eq 'PurchaseOrder'}">
		<div class="dropdown pull-right permissionInvoices-Purchase-Delete" style="margin-left: 10px;">
			<div class="split-button-menu-dropdown reportmenu">
				<button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" >
					<span class="showarrow">
						<i class="fa fa-caret-down"></i>
					</span>
				</button>
				<button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">Delete</button>
				<div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;">
				<a href="#" onclick="showDeleteAllPopup('<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>','<c:out value="${otherreturnType}"/>','selectedInvs','<c:out value="${month}"/>','<c:out value="${year}"/>')" class="permissionInvoices-Purchase-Delete pull-right dropdown-item disabled" id="deletePurchaseOrderInvoices">DELETE</a>
				<a href="#" onclick="showDeleteAllPopup('<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>','<c:out value="${otherreturnType}"/>','deleteAll','<c:out value="${month}"/>','<c:out value="${year}"/>')" class="permissionInvoices-Purchase-Delete pull-right dropdown-item deleteAllInvoices${varRetType.replaceAll(' ', '_')}">DELETE ALL</a>
				</div>
			</div>
		</div>
		<a href="#" class="btn btn-blue pull-right" id="cnvrt_PO_To_Invoice" onclick="cnvrt_PO_To_Invoice()">Convert To Invoice</a>
		<a href="#" class="btn btn-blue pull-right" link='${contextPath}/syncinv/${urlSuffix}' data-toggle="modal" data-target="#estimateModal" onClick="showInvPopup('<%=MasterGSTConstants.PURCHASEORDER%>','<c:out value="${varRetType}"/>',true)">Add Purchase Order</a>
		</c:if>
		<c:if test="${varRetType eq varGSTR1 || varRetType eq varGSTR4 || varRetType eq varGSTR5 || varRetType eq varANX1}">
		<c:if test="${otherreturn_type ne 'additionalInv'}"><c:if test="${varRetType eq varGSTR1}"><a type="button" class="btn btn-greendark" style="color:white; box-shadow:none; font-size:14px" onclick = "updateInvoiceStatus()" href="#" link="${contextPath}/updateInvStatus/${id}/${fullname}/${usertype}/${client.id}/GSTR1/${month}/${year}">Status<i class="fa fa-refresh"  style="font-size: 14px; color: #fff; margin-left:5px"></i></a></c:if></c:if>
		<c:if test="${varRetType ne varANX1}"><a href="" data-toggle="modal" data-target="#importModal" onClick="updateImportModal('<%=MasterGSTConstants.GSTR1%>')" class="btn btn-blue pull-right btn-all-iview-sales permissionGeneral-Import_Sales <c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">disable</c:if>">Import</a></c:if>
		<div class="dropdown pull-right permissionInvoices-Sales-Delete" style="margin-left: 10px;">
			<div class="split-button-menu-dropdown reportmenu">
				<button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" >
					<span class="showarrow">
						<i class="fa fa-caret-down"></i>
					</span>
				</button>
				<button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">Delete</button>
				<div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;">
				<a href="#" onclick="showDeleteAllPopup('<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>','<c:out value="${otherreturnType}"/>','selectedInvs','<c:out value="${month}"/>','<c:out value="${year}"/>')" class="permissionInvoices-Sales-Delete pull-right dropdown-item disabled" id="deleteSalesInvoices">DELETE</a>
				<a href="#" onclick="showDeleteAllPopup('<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>','<c:out value="${otherreturnType}"/>','deleteAll','<c:out value="${month}"/>','<c:out value="${year}"/>')" class="permissionInvoices-Sales-Delete pull-right dropdown-item deleteAllInvoices${varRetType.replaceAll(' ', '_')}">DELETE ALL</a>
				</div>
			</div>
		</div>
		</c:if>
		<c:if test="${varRetType eq varGSTR6}">
		<a href="" data-toggle="modal" data-target="#importModal" onClick="updateImportModal('<%=MasterGSTConstants.PURCHASE_REGISTER%>')" class="btn btn-blue permissionGeneral-Import_Purchases <c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">disable</c:if>">Import</a>
		<div class="dropdown pull-right permissionInvoices-Purchase-Delete" style="margin-left: 10px;">
			<div class="split-button-menu-dropdown reportmenu">
				<button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" >
					<span class="showarrow">
						<i class="fa fa-caret-down"></i>
					</span>
				</button>
				<button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">Delete</button>
				<div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;">
				<a href="#" onclick="showDeleteAllPopup('<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>','<c:out value="${otherreturnType}"/>','selectedInvs','<c:out value="${month}"/>','<c:out value="${year}"/>')" class="permissionInvoices-Purchase-Delete pull-right dropdown-item disabled" id="deletePurchaseInvoices">DELETE</a>
				<a href="#" onclick="showDeleteAllPopup('<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>','<c:out value="${otherreturnType}"/>','deleteAll','<c:out value="${month}"/>','<c:out value="${year}"/>')" class="permissionInvoices-Purchase-Delete pull-right dropdown-item deleteAllInvoices${varRetType.replaceAll(' ', '_')}" id="deleteAllInvoices">DELETE ALL</a>
				</div>
			</div>
		</div>
		</c:if>
		<c:if test="${varRetType eq 'EXPENSES'}">
		<div class="dropdown pull-right" style="margin-left: 10px;">
			<div class="split-button-menu-dropdown reportmenu">
				<button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" >
					<span class="showarrow">
						<i class="fa fa-caret-down"></i>
					</span>
				</button>
				<button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">Delete</button>
				<div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;">
				<a href="#" onclick="showDeleteAllPopup('<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>','<c:out value="${otherreturnType}"/>','selectedInvs','<c:out value="${month}"/>','<c:out value="${year}"/>')" class="permissionInvoices-Sales-Delete pull-right dropdown-item disabled" id="deleteExpensesInvoices">DELETE</a>
				<a href="#" onclick="showDeleteAllPopup('<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>','<c:out value="${otherreturnType}"/>','deleteAll','<c:out value="${month}"/>','<c:out value="${year}"/>')" class="permissionInvoices-Sales-Delete pull-right dropdown-item deleteAllInvoices${varRetType.replaceAll(' ', '_')}">DELETE ALL</a>
				</div>
			</div>
		</div>
		<div class="dropdown pull-right permissionExcel_Download_In_Books_And_Returns-Sales" style="margin-left: 10px;"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;"><a class="dropdown-item" id="expitemWiseUrl${varRetType}" href="${contextPath}/dwnldExpenseXls/${id}/${client.id}/${varRetType}/${month}/${year}/itemwise">ITEM WISE DOWNLOAD</a><a class="dropdown-item" id="expWiseUrl${varRetType}" href="${contextPath}/dwnldExpenseXls/${id}/${client.id}/${varRetType}/${month}/${year}/expensewise">EXPENSE WISE DOWNLOAD</a></div></div></div>
		<a href="#" class="btn btn-blue pull-right" id="add_expense" onclick="addExpenses()">Add Expenses</a>
		</c:if>
		<c:if test="${varRetType eq varGSTR2A}">
		<a href="#" class="btn btn-greendark permissionGSTN_Actions-Download_GSTR2A pull-right btn-all-iview-sales btn-sm downloadbtn" onClick="downloadFromGSTN('<c:out value="${varRetType}"/>')" link='${contextPath}/dwnldinv/${urlSuffix}'>Download from GSTN</a>
		<a href="#" link="${contextPath}/reconcileinv/${urlSuffix}/${month}/${year}" onClick="reconcileGstr2a()" class="btn btn-greendark pull-right btn-all-iview-sales btn-sm reconcile_btn">Reconcile</a>
		<a href="${contextPath}/populatetradename/${usertype}/${varRetType}/${fullname}/${id}/${client.id}/${month}/${year}" class="btn btn-blue gstr2atradename" style="float:left;margin-left: 0px;">Populate Supplier Name</a>
		</c:if>
		<c:if test="${varRetType ne varProforma && varRetType ne varDeliverchallan && varRetType ne varEstimates && varRetType ne varPurchaseOrder}">
			<c:if test="${varRetType ne 'EWAYBILL'}"><c:if test="${varRetType eq varGSTR1}"><div class="dropdown pull-right permissionExcel_Download_In_Books_And_Returns-Sales" style="margin-left: 10px;"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;"><a class="dropdown-item" id="itemWiseUrl${varRetType}" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/itemwise">ITEM WISE DOWNLOAD</a><a class="dropdown-item" id="invWiseUrl${varRetType}" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/invoicewise">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" id="fullDwnldUrl${varRetType}" href="${contextPath}/fulldwnldmonthlyxls/${id}/${client.id}/${varRetType}/${month}/${year}">ALL DETAILS DOWNLOAD</a></div></div></div></c:if>
			<c:if test="${varRetType eq varGSTR2}"><div class="dropdown pull-right permissionExcel_Download_In_Books_And_Returns-GSTR2" style="margin-left: 10px;"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;"><a class="dropdown-item" id="itemWiseUrl${varRetTypeCode}" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/itemwise">ITEM WISE DOWNLOAD</a><a class="dropdown-item" id="invWiseUrl${varRetTypeCode}" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/invoicewise">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" id="fullDwnldUrl${varRetTypeCode}" href="${contextPath}/fulldwnldmonthlyxls/${id}/${client.id}/${varRetType}/${month}/${year}">ALL DETAILS DOWNLOAD</a></div></div></div></c:if>
			<c:if test="${varRetType eq varPurchase}"><div class="dropdown pull-right permissionExcel_Download_In_Books_And_Returns-Purchases" style="margin-left: 10px;"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;"><a class="dropdown-item" id="itemWiseUrl${varRetTypeCode}" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/itemwise">ITEM WISE DOWNLOAD</a><a class="dropdown-item" id="invWiseUrl${varRetTypeCode}" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/invoicewise">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" id="fullDwnldUrl${varRetTypeCode}" href="${contextPath}/fulldwnldmonthlyxls/${id}/${client.id}/${varRetType}/${month}/${year}">ALL DETAILS DOWNLOAD</a></div></div></div></c:if>
			<c:if test="${varRetType eq 'GSTR1Amnd'}"><div class="dropdown pull-right permissionExcel_Download_In_Books_And_Returns-GSTR1A" style="margin-left: 10px;"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;"><a class="dropdown-item" id="itemWiseUrl${varRetTypeCode}" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/itemwise">ITEM WISE DOWNLOAD</a><a class="dropdown-item" id="invWiseUrl${varRetTypeCode}" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/invoicewise">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" id="fullDwnldUrl${varRetTypeCode}" href="${contextPath}/fulldwnldmonthlyxls/${id}/${client.id}/${varRetType}/${month}/${year}">ALL DETAILS DOWNLOAD</a></div></div></div></c:if>
			<c:if test="${varRetType eq varGSTR2A}"><div class="dropdown pull-right permissionExcel_Download_In_Books_And_Returns-GSTR2A" style="margin-left: 10px;"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;"><a class="dropdown-item" id="itemWiseUrl${varRetTypeCode}" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/itemwise">ITEM WISE DOWNLOAD</a><a class="dropdown-item" id="invWiseUrl${varRetTypeCode}" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/invoicewise">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" id="fullDwnldUrl${varRetTypeCode}" href="${contextPath}/fulldwnldmonthlyxls/${id}/${client.id}/${varRetType}/${month}/${year}">ALL DETAILS DOWNLOAD</a></div></div></div></c:if>
			<c:if test="${varRetType eq 'Unclaimed'}"><a href="#" class="btn btn-blue pull-right btn-all-iview-sales btn-sm" id="itcclaimbtn" data-toggle="modal" data-target="#claimModal" onclick="">ITC Claim</a><div class="dropdown pull-right permissionExcel_Download_In_Books_And_Returns-Unclaimed_ITC" style="margin-left: 10px;"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;"><a class="dropdown-item" id="itemWiseUrl${varRetTypeCode}" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/itemwise">ITEM WISE DOWNLOAD</a><a class="dropdown-item" id="invWiseUrl${varRetTypeCode}" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/invoicewise">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" id="fullDwnldUrl${varRetTypeCode}" href="${contextPath}/fulldwnldmonthlyxls/${id}/${client.id}/${varRetType}/${month}/${year}">ALL DETAILS DOWNLOAD</a></div></div></div></c:if>
			<c:if test="${varRetType ne 'EXPENSES'}"><c:if test="${varRetType ne varANX2}"><c:if test="${varRetType ne varANX1}"><a href="#"  class="btn btn-blue recordpayments disabled" data-toggle="modal" data-target="#paymentsModal" onclick="updatePayment('${returntype}')"><c:if test="${returntype eq 'GSTR1' || returntype eq 'GSTR1Amnd'}">Add Receipt</c:if><c:if test="${returntype eq 'GSTR2' || returntype eq varANX2 || returntype eq varPurchase || returntype eq 'Unclaimed' || returntype eq varGSTR2A}">Add Payment</c:if></a></c:if></c:if></c:if>
		</c:if>
		</c:if>
		<c:if test="${varRetType eq varGSTR1 || varRetType eq varANX1 || varRetType eq varGSTR4 || varRetType eq varGSTR6 || varRetType eq varGSTR5 || varRetType eq varPurchase}">
		<div class="addsales dropdown pull-right btn-all-iview-sales <c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">disable</c:if>">
			<div class="split-button-menu-dropdown">
				<c:choose>
					<c:when test="${varRetType eq varPurchase}">
						<button class="btn btn-blue dropdown-toggle permissionInvoices-Purchase-Add b-split-left b-r-cta b-m-super-subtle" id="idMenuDropdown" data-toggle="dropdown" aria-haspopup="true" style="width:  219px;box-shadow:none; text-align:left" aria-expanded="false" href="#" link1="${contextPath}/addpinv/${urlSuffix}?stype=">
					</c:when>
					<c:when test="${varRetType eq varGSTR1 || varRetType eq varANX1}">
						<button class="btn btn-blue dropdown-toggle permissionInvoices-Sales-Add b-split-left b-r-cta b-m-super-subtle" style="margin-right:24px;box-shadow:none;"id="idMenuDropdown" aria-haspopup="true" aria-expanded="false" href="#" link1="${contextPath}/addsinv/${urlSuffix}?stype=">
					</c:when>
					<c:when test="${varRetType eq varGSTR4}">
						<button class="btn btn-blue dropdown-toggle permissionInvoices-Sales-Add b-split-left b-r-cta b-m-super-subtle" id="idMenuDropdown" data-toggle="dropdown" aria-haspopup="true" style="margin-right:24px;box-shadow:none; text-align:left" aria-expanded="false" href="#" link1="${contextPath}/addpinv/${urlSuffix}?stype=">
					</c:when>
					<c:when test="${varRetType eq varGSTR5}">
						<button class="btn btn-blue dropdown-toggle permissionInvoices-Sales-Add b-split-left b-r-cta b-m-super-subtle" style="margin-right:24px;box-shadow:none;"id="idMenuDropdown" aria-haspopup="true" aria-expanded="false" href="#" link1="${contextPath}/addsinv/${urlSuffix}?stype=">
					</c:when>
					<c:otherwise>
						<button class="btn btn-blue dropdown-toggle permissionInvoices-Purchase-Add b-split-left b-r-cta b-m-super-subtle" style="margin-right:24px;box-shadow:none;"id="idMenuDropdown" aria-haspopup="true" aria-expanded="false" href="#" link1="${contextPath}/addsinv/${urlSuffix}?stype=">
					</c:otherwise>
					</c:choose>Add<c:choose>
						<c:when test="${varRetType eq varPurchase || varRetType eq varGSTR6}">Purchase Invoice</c:when>
						<c:otherwise>Sales Invoice</c:otherwise>
					</c:choose> 
					<c:choose>
						<c:when test="${varRetType eq varPurchase || varRetType eq varANX2}">
							<button class="btn btn-blue dropdown-toggle permissionInvoices-Purchase-Add b-split-right b-r-cta b-m-super-subtle" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button>
						</c:when>
						<c:when test="${varRetType eq varGSTR1 || varRetType eq varANX1}">
							<button class="btn btn-blue dropdown-toggle permissionInvoices-Sales-Add b-split-right b-r-cta b-m-super-subtle" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button>
						</c:when>
						<c:when test="${varRetType eq varGSTR4}">
							<button class="btn btn-blue dropdown-toggle permissionInvoices-Purchase-Add b-split-right b-r-cta b-m-super-subtle" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button>
						</c:when>
						<c:when test="${varRetType eq varGSTR5}">
							<button class="btn btn-blue dropdown-toggle permissionInvoices-Sales-Add b-split-right b-r-cta b-m-super-subtle" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button>
						</c:when>
						<c:otherwise>
							<button class="btn btn-blue dropdown-toggle permissionInvoices-Sales-Add b-split-right b-r-cta b-m-super-subtle" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button>
						</c:otherwise>
					</c:choose>
				<c:choose>
				<c:when test="${varRetType eq varPurchase || varRetType eq varANX2}">
				<div class="dropdown-menu dropdown-menu-right" aria-labelledby="idMenuDropdown" id="add_invoice_drop">
					<a class="dropdown-item urllink" href="#" onClick="showInvPopup('<%=MasterGSTConstants.B2B%>','<c:out value="${varRetType}"/>',true)" link1="${contextPath}/addpinv/${urlSuffix}?stype=<%=MasterGSTConstants.B2B%>">PURCHASE INVOICE</a>
					<a class="dropdown-item urllink" href="#" onClick="showInvPopup('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>','<c:out value="${varRetType}"/>',true)" link1="${contextPath}/addpinv/${urlSuffix}?stype=<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>">CREDIT/DEBIT NOTES</a>
					<a class="dropdown-item urllink" href="#" onClick="showInvPopup('<%=MasterGSTConstants.ADVANCES%>','<c:out value="${varRetType}"/>',true)" link1="${contextPath}/addpinv/${urlSuffix}?stype=<%=MasterGSTConstants.ADVANCES%>">ADVANCE PAYMENTS</a>
					<a class="dropdown-item urllink" href="#" onClick="showInvPopup('<%=MasterGSTConstants.ATPAID%>','<c:out value="${varRetType}"/>',true)" link1="${contextPath}/addpinv/${urlSuffix}?stype=<%=MasterGSTConstants.ATPAID%>">ADVANCE ADJUSTED DETAIL</a>
					<a class="dropdown-item urllink" href="#" onClick="showInvPopup('<%=MasterGSTConstants.IMP_GOODS%>','<c:out value="${varRetType}"/>',true)" link1="${contextPath}/addpinv/${urlSuffix}?stype=<%=MasterGSTConstants.IMP_GOODS%>">IMPORT OF GOODS(BILL OF ENTRY)</a>
					<a class="dropdown-item urllink" href="#" onClick="showInvPopup('<%=MasterGSTConstants.IMP_SERVICES%>','<c:out value="${varRetType}"/>',true)" link1="${contextPath}/addpinv/${urlSuffix}?stype=<%=MasterGSTConstants.IMP_SERVICES%>">IMPORT OF SERVICES</a>
					<a class="dropdown-item urllink" href="#" onClick="showInvPopup('<%=MasterGSTConstants.NIL%>','<c:out value="${varRetType}"/>',true)" link1="${contextPath}/addpinv/${urlSuffix}?stype=<%=MasterGSTConstants.NIL%>">BILL OF SUPPLY</a>
					<a class="dropdown-item urllink" href="#" onClick="showInvPopup('<%=MasterGSTConstants.ISD%>','<c:out value="${varRetType}"/>',true)" link1="${contextPath}/addpinv/${urlSuffix}?stype=<%=MasterGSTConstants.ISD%>">ISD INVOICE</a>
					<a class="dropdown-item urllink" href="#" onClick="showInvPopup('<%=MasterGSTConstants.ITC_REVERSAL%>','<c:out value="${varRetType}"/>',true)" link1="${contextPath}/addpinv/${urlSuffix}?stype=<%=MasterGSTConstants.ITC_REVERSAL%>">ITC REVERSAL</a>
				</div>
				</c:when>
				<c:otherwise>
				<div class="dropdown-menu dropdown-menu-right" aria-labelledby="idMenuDropdown" id="add_invoice_drop">
					<a class="dropdown-item urllink" href="#" onClick="showInvPopup('<%=MasterGSTConstants.B2B%>','<c:out value="${varRetType}"/>',true)" link1="${contextPath}/addsinv/${urlSuffix}?stype=<%=MasterGSTConstants.B2B%>">SALES INVOICE (B2B<c:if test="${varRetType eq varGSTR4}">)</c:if><c:if test="${varRetType eq varGSTR1 || varRetType eq varGSTR5 || varRetType eq varANX1}">/B2CL/B2CS)</c:if></a>
					<c:if test="${varRetType eq varGSTR1 || varRetType eq varANX1}">
					<a class="dropdown-item urllink" href="#" onClick="showInvPopup('<%=MasterGSTConstants.EXPORTS%>','<c:out value="${varRetType}"/>',true)" link1="${contextPath}/addsinv/${urlSuffix}?stype=<%=MasterGSTConstants.EXPORTS%>">EXPORT INVOICE</a>
					</c:if>
					<a class="dropdown-item urllink" href="#" onClick="showInvPopup('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>','<c:out value="${varRetType}"/>',true)" link1="${contextPath}/addsinv/${urlSuffix}?stype=<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>">CREDIT/DEBIT NOTES</a>
					<c:if test="${varRetType eq varGSTR1}">
					<a class="dropdown-item urllink" href="#" onClick="showInvPopup('<%=MasterGSTConstants.NIL%>','<c:out value="${varRetType}"/>',true)" link1="${contextPath}/addsinv/${urlSuffix}?stype=<%=MasterGSTConstants.NIL%>">BILL OF SUPPLY</a>
					</c:if>
					<c:if test="${varRetType eq varGSTR4}">
					<a class="dropdown-item urllink" href="#" onClick="showInvPopup('<%=MasterGSTConstants.B2BUR%>','<c:out value="${varRetType}"/>',true)" link1="${contextPath}/addsinv/${urlSuffix}?stype=<%=MasterGSTConstants.B2BUR%>">B2B UNREGISTERED</a>
					<a class="dropdown-item urllink" href="#" onClick="showInvPopup('<%=MasterGSTConstants.IMP_SERVICES%>','<c:out value="${varRetType}"/>',true)" link1="${contextPath}/addsinv/${urlSuffix}?stype=<%=MasterGSTConstants.IMP_SERVICES%>">IMPORT OF SERVICES</a>
					</c:if>
					<c:if test="${varRetType eq varGSTR4 || varRetType eq varGSTR1}">
					<a class="dropdown-item urllink" href="#" onClick="showInvPopup('<%=MasterGSTConstants.ADVANCES%>','<c:out value="${varRetType}"/>',true)" link1="${contextPath}/addsinv/${urlSuffix}?stype=<%=MasterGSTConstants.ADVANCES%>">ADVANCE RECEIPTS (RECEIPT VOUCHER)</a>
					<a class="dropdown-item urllink" href="#" onClick="showInvPopup('<%=MasterGSTConstants.ATPAID%>','<c:out value="${varRetType}"/>',true)" link1="${contextPath}/addsinv/${urlSuffix}?stype=<%=MasterGSTConstants.ATPAID%>">ADVANCE ADJUSTED DETAIL</a>
					</c:if>
					<c:if test="${varRetType eq varGSTR5}">
					<a class="dropdown-item urllink" href="#" onClick="showInvPopup('<%=MasterGSTConstants.IMP_GOODS%>','<c:out value="${varRetType}"/>',true)" link1="${contextPath}/addpinv/${urlSuffix}?stype=<%=MasterGSTConstants.IMP_GOODS%>">IMPORT OF GOODS</a>
					</c:if>
					<c:if test="${varRetType eq varGSTR6}">
					<a class="dropdown-item urllink" href="#" onClick="showInvPopup('<%=MasterGSTConstants.ISD%>','<c:out value="${varRetType}"/>',true)" link1="${contextPath}/addpinv/${urlSuffix}?stype=<%=MasterGSTConstants.ISD%>">ISD INVOICE</a>
					</c:if>
				</div>
				</c:otherwise>
				</c:choose>
			</div>
		</div>
		</c:if>
	</h4>
	<div id="invview_Process" class="invview_Process d-none"  style="color:red;font-size:20px;position:absolute;z-index:99;top:37%;left: 46%;">
		<img src="${contextPath}/static/mastergst/images/eclipse-spinner.gif" alt="spinner-img" style="width: 150px;height: 150px;"/>
	</div>
	<div class="customtable db-ca-view tabtable1 invtabtable1 dbTable${varRetTypeCode}">
	<span class="select_msg" style="position:absolute;left:60%;color:#5769bb;margin-top:10px;"></span>
		<table id='dbTable${varRetTypeCode}' class="row-border dataTable meterialform" cellspacing="0" width="100%">
		<c:if test="${varRetType ne 'EXPENSES'}">
			<thead>
				<tr>
					<c:if test="${varRetType ne varGSTR2A}">
					<th class="text-center"><div class="checkbox"><label><input type="checkbox" id='check${varRetTypeCode}' onClick="updateMainSelection('${varRetType}','${client.status}',this)"/><i class="helper"></i></label></div> </th>
					<c:if test="${varRetType ne varProforma && varRetType ne varDeliverchallan && varRetType ne varEstimates && varRetType ne varPurchaseOrder && varRetType ne 'EWAYBILL' && varRetType ne 'Unclaimed'}">
					<c:choose>
					<c:when test="${otherreturnType eq 'PurchaseRegister' || otherreturnType eq 'SalesRegister' || varRetType eq varGSTR1 || varRetType eq varANX1}">
					<th>Status</th>
					</c:when>
					<c:otherwise>
					</c:otherwise>
					</c:choose>
					</c:if>
					</c:if>
					<c:if test="${varRetType eq 'EWAYBILL'}">
						<th>Status</th>
						<th>EBill No</th>
						<th>EBill Date</th>
						<th>Valid Upto</th>
					</c:if>
					<th>Type</th>
					<c:if test="${varRetType ne 'EWAYBILL'}">
						<th class="text-center">Invoice No</th>
					</c:if>
					<c:if test="${varRetType eq varGSTR2 || varRetType eq varGSTR2A || varRetType eq varPurchase || varRetType eq 'Unclaimed' || varRetType eq varGSTR6 || varRetType eq varPurchaseOrder || varRetType eq varANX2}">
					<th class="text-center" style="max-width:230px!important;width:auto!important;">Suppliers</th>
					</c:if>
					<c:if test="${varRetType ne varGSTR2 && varRetType ne varGSTR2A && varRetType ne varPurchase && varRetType ne 'Unclaimed' && varRetType ne varGSTR6 && varRetType ne varPurchaseOrder && varRetType ne varANX2}">
					<th class="text-center" style="max-width:230px!important;width:auto!important;">Customers</th>
					</c:if>
					<th class="text-center">GSTIN</th><c:if test="${varRetType ne 'EWAYBILL'}"><th class="text-center">Date</th></c:if><th class="text-center">Taxable Amt</th><th class="text-center">Total Tax</th><th class="text-center">Total Amt</th>
					<c:if test="${varRetType eq varPurchase || varRetType eq varGSTR2 || varRetType eq varGSTR6 || varRetType eq 'Unclaimed' || varRetType eq varANX2}">
					<th class="text-center">ITC Claimed</th>
					</c:if>
					<c:if test="${varRetType eq varANX2}">
					<!-- <th class="text-center" style="max-width:40px!important">ITC Claimed</th> -->
					<th class="text-center" style="max-width:50px!important" data-toggle="tooltip" title="Supply covered under sec 7 of IGST Act">Section 7 of IGST Act</th>
					<th class="text-center" style="max-width:40px!important">Reconcile Status</th>
					<th class="text-center" style="max-width:40px!important">Portal Status</th>
					<!-- <th class="text-center"style="max-width:40px!important;">Action</th> -->
					</c:if>
					<c:if test="${varRetType ne varGSTR2 && varRetType ne varGSTR2A}">
					<th class="text-center"style="max-width:48px!important;width:auto!important;">Action</th>
					</c:if>
					<c:if test="${varRetType eq varGSTR2A}">
						<th class="text-center" style=""></th>
					</c:if>
				</tr>
			</thead>
			</c:if>
			<c:if test="${varRetType eq 'EXPENSES'}">
			<thead>
			  <tr>
			  <th class="text-center"><div class="checkbox"><label><input type="checkbox" id='expCheck${varRetTypeCode}' onClick="updateExpMainSelection('${varRetType}',this)"/><i class="helper"></i></label></div> </th>
				<th class="text-center">Expense Category</th><th class="text-center">Payment Mode</th><th class="text-center">Payment Date</th><th class="text-center">Total Value</th><th class="text-center">Action</th>
			  </tr>
			</thead>
			</c:if>
			<tbody id="invBody${varRetTypeCode}">
			</tbody>
		</table>
	</div>
	<div class="modal fade" id="ewybuyeraddrModal" role="dialog" aria-labelledby="ewybuyeraddrModal" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
				<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr addrHdr" style="width:100%">
                        <h3 style="color:white;">Add Buyer Details</h3>
                    </div>
				</div>
				<form:form method="POST" data-toggle="validator" class="buyerform meterialform" id="buyerform">
                <div class="modal-body meterialform bs-fancy-checks">
                    <!-- row begin -->
					<div class="row  pl-5 pr-5 pt-3">
					<div class="form-group col-md-6 col-sm-12" id="ewybuyerdetgstin">
                            <p class="lable-txt astrich" id="ewybuyerdetgstinlabel">GSTIN/Unique ID <a href="#" onClick="ewayinvokegstnPublicAPI(this,'buyer')" class="btn btn-green btn-sm pull-right">Get GSTIN Details</a> </p>
                            <span class="errormsg" id="ewybuyerGstno_msg"></span>
                            <input type="text" id="ewybuyerGstno" aria-describedby="ewybuyerGstno" onchange="ewayupdatePan(this.value,'buyer')" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})|([Uu][Rr][Pp])$" data-error="Please enter Valid GSTIN.(Sample 07CQZCD1111I4Z7)" maxlength="15" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" placeholder="07CQZCD1111I4Z7" />
                            <label for="ewydispatchGstno" class="control-label"></label>
                            <i class="bar"></i> </div>
                        <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Name</p>
                            <span class="errormsg" id="ewybuyerTradeName_msg"></span>
                            <input type="text" id="ewybuyerTradeName" value="" />
                            <label for="ewybuyername" class="control-label"></label>
							
                            <i class="bar"></i> 
						</div>
						
						 <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Address 1</p>
                            <span class="errormsg" id="ewybuyerAddress1_msg"></span>
                            <input type="text" id="ewybuyerAddress1" value="" />
                            <label for="ewyaddress1" class="control-label"></label>
							
                            <i class="bar"></i> 
						</div>
						
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Address 2</p>
                            <span class="errormsg" id=""></span>
                            <input type="text" id="ewybuyerAddress2"  value="" />
                            <label for="ewyaddress2" class="control-label"></label>
							
                            <i class="bar"></i> 
						</div>
						
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Location</p>
                            <span class="errormsg" id="ewybuyerloc_msg"></span>
							<input type="text" id="ewybuyerloc" value="" data-error="please enter location"/>
                            <label for="location" class="control-label"></label>
                            <i class="bar"></i> 
						</div>
						
						
							<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Pincode</p>
                            <span class="errormsg" id="buyerPincode_msg"></span>
                            <input type="text" id="ewybuyerPincode" required data-error="Please enter Pincode" value="" />
                            <label for="Pincode" class="control-label"></label>
							
                            <i class="bar"></i> 
						</div>

						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">State Code</p>
                            <span class="errormsg" id="buyerStatecode_msg"></span>
                            <input type="text" id="ewybuyerStatecode" name="" required="required" data-error="Please enter State Code" value="" />
                            <label for="statecode" class="control-label"></label>
							
							<div id="ewybuyerStatecodeempty" style="display:none">
		                  		<div class="ddbox"><p>Search didn't return any results.</p></div>
		                  </div>
                            <i class="bar"></i> 
						</div>
                    </div>
					
                    <!-- row end -->
                </div>
               
				<div class="modal-footer text-center" style="display:block">
				<label for="" class="btn btn-blue-dark"  onclick="ewybuyerAddrDetails()" id="ewybuyer_save">Save</label>
				<a href="#" class="btn btn-blue-dark" data-dismiss="modal" aria-label="Close">Cancel</a>
				</div>
				 </form:form>
            </div>
        </div>
    </div>
    <div class="modal fade" id="ewydispatchaddrModal" role="dialog" aria-labelledby="ewydispatchaddrModal" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
				<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr addrHdr" style="width:100%">
                        <h3 style="color:white;">Add Dispatcher Details</h3>
                    </div>
				</div>
				<form:form method="POST" data-toggle="validator" class="dispatchform meterialform" id="ewdispatchform">
                <div class="modal-body meterialform bs-fancy-checks">
                    <!-- row begin -->
					
					<div class="row  pl-5 pr-5 pt-3">
                        <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Name</p>
                            <span class="errormsg" id=""></span>
                            <input type="text" id="ewydispatchTradeName" value="" />
                            <label for="accountnumber" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
						
						 <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Address 1</p>
                            <span class="errormsg" id=""></span>
                            <input type="text" id="ewydispatchAddress1" value="" />
                            <label for="address1" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
						
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Address 2</p>
                            <span class="errormsg" id=""></span>
                            <input type="text" id="ewydispatchAddress2"  value="" />
                            <label for="address2" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
						
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Location</p>
                            <input type="text" id="ewydispatchloc" value="" data-error="please enter location"/>
                            <label for="location" class="control-label"></label>
                            <i class="bar"></i> 
						</div>
						
						
							<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Pincode</p>
                            <span class="errormsg" id=""></span>
                            <input type="text" id="ewydispatchPincode" required="required"  data-error="Please enter Pincode" value="" />
                            <label for="Pincode" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>

						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">State Code</p>
                            <span class="errormsg" id="dispatchState"></span>
                            <input type="text" id="ewydispatchStatecode" name="" required="required" data-error="Please enter State Code" value="" />
                            <label for="statecode" class="control-label"></label>
							<div class="help-block with-errors"></div>
							<div id="ewydispatchStatecodeempty" style="display:none">
		                  		<div class="ddbox"><p>Search didn't return any results.</p></div>
		                  </div>
                            <i class="bar"></i> 
						</div>
                    </div>
                    <!-- row end -->
                </div>
				<div class="modal-footer text-center" style="display:block">
				<label for="" class="btn btn-blue-dark"  onclick="ewydispatchAddrDetails()" id="ewydispatch_save">Save</label>
				<a href="#" class="btn btn-blue-dark" data-dismiss="modal" aria-label="Close">Cancel</a>
				</div>
				 </form:form>
            </div>
        </div>
    </div>
    
    	    <div class="modal fade" id="ewyshipaddrModal" role="dialog" aria-labelledby="ewyshipaddrModal" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
				<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr addrHdr" style="width:100%">
                        <h3 style="color:white;">Add Shipment Details</h3>
                    </div>
				</div>
                <div class="modal-body meterialform bs-fancy-checks">
                    <!-- row begin -->
					<form:form method="POST" data-toggle="validator" class="meterialform shipmentform" id="ewshipmentform">
					<div class="row  pl-5 pr-5 pt-3">
                        <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Trade Name</p>
                            <span class="errormsg" id=""></span>
                            <input type="text" id="ewyshipmentTradeName" value="" />
                            <label for="accountnumber" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
						 <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Address 1</p>
                            <span class="errormsg" id=""></span>
                            <input type="text" id="ewyshipmentAddress1" value="" />
                            <label for="address1" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Address 2</p>
                            <span class="errormsg" id=""></span>
                            <input type="text" id="ewyshipmentAddress2"  value="" />
                            <label for="address2" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Location</p>
                            <input type="text" id="ewyshipmentloc" value="" data-error="please enter location"/>
                            <label for="location" class="control-label"></label>
                            <i class="bar"></i> 
						</div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Pincode</p>
                            <span class="errormsg" id=""></span>
                            <input type="text" id="ewyshipmentPincode" required="required"  data-error="Please enter Pincode" value="" />
                            <label for="Pincode" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">State Code</p>
                            <span class="errormsg" id=""></span>
                            <input type="text" id="ewyshipmentStatecode" required="required" data-error="Please enter State Code" value="" />
                            <label for="statecode" class="control-label"></label>
							<div class="help-block with-errors"></div>
							<div id="ewyshipmentStatecodeempty" style="display:none">
		                  		<div class="ddbox"><p>Search didn't return any results.</p></div>
		                  </div>
                            <i class="bar"></i> 
						</div>												
                    </div>
					</form:form>
                    <!-- row end -->
                </div>
				<div class="modal-footer text-center" style="display:block">
				<label for="" class="btn btn-blue-dark"  onclick="ewyshipmentAddrDetails()" id="ewyshipment_save">Save</label>
				<a href="#" class="btn btn-blue-dark" data-dismiss="modal" aria-label="Close">Cancel</a>
				</div>
            </div>
        </div>
    </div>
   
	<script type="text/javascript">
	var totalTaxableValue = 0, totalIGST = 0, totalCGST = 0, totalSGST = 0, totalCESS = 0,  totalITCIGST = 0, totalITCCGST = 0, totalITCSGST = 0, totalITCCESS = 0,totalTax = 0, totalITC = 0, totalValue = 0, totalInvoices = 0, totalUploaded = 0, totalPending = 0, totalFailed = 0, totalExemptedValue = 0;var NotInJournals = 'NotInJournals';var hsnrowCount = 1;
	var mnths=['Jan','Feb','Mar','Apr','May','June','July','Aug','Sept','Oct','Nov','Dec'];
	var is${tabName}Loaded = false;var clitfilingStatus = '<c:out value="${client.status}"/>';
	var ewaybillAuthSttaus = '<c:out value="${connSttaus}"/>';var varRetType_Code='<c:out value="${varRetTypeCode}"/>';
	$('#${tabName}').on('click',function() {
		if('#${tabName}' != "#expensesTab" && '#${tabName}' != "#reconcileTab" && '#${tabName}' !="#gstr2bmisMatchTab"){
			 $('.inv_view_txt').text('Invoice');
			var booksOrReturns = '<c:out value="${otherreturnType}"/>';
			var varRetTypeCode='<c:out value="${varRetTypeCode}"/>';
			if(!is${tabName}Loaded){
				if(booksOrReturns == "" && varRetTypeCode !="Purchase_Register"){
					subscriptioncheck(varRetTypeCode);
				}
				var dwldflag = '<c:out value="${dwldflag}"/>';var eflag = '<c:out value="${eflag}"/>';var connSttaus = '<c:out value="${connSttaus}"/>';
				var pendinginv = '<c:out value="${client.prevPendingInv}"/>';
				var clientfilingStatus = '<c:out value="${client.status}"/>';
				//$('#updatefilter_summary').hide();
				initiateCallBacksForMultiSelect('${varRetType}','${varRetTypeCode}','${otherreturnType}');
				loadUsersByClient('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}',loadUsersInDropDown);	
				var invviewoption = '${client.invoiceViewOption}';
				if(booksOrReturns != "" && invviewoption == "Yearly"){
					var finyr = $('#invoiceviewfinancialYear').val();
					var yr = parseInt(finyr);
					loadInvTable('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', 0, yr, '${usertype}', '${fullname}','${otherreturnType}',pendinginv,'${tallyHsnSummary}',clientfilingStatus);
					loadInvoiceSupport('${client.id}', '${varRetType}', '${varRetTypeCode}',0, yr+1, loadCustomersInDropdown);	
					invsDownloads('${id}', '${client.id}','${varRetType}','${otherreturnType}','${varRetTypeCode}', '${month}', yr+1, '${usertype}', '${fullname}', booksOrReturns);
				}else{
					loadInvTable('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', '${month}', '${year}', '${usertype}', '${fullname}','${otherreturnType}',pendinginv,'${tallyHsnSummary}',clientfilingStatus);
					loadInvoiceSupport('${client.id}', '${varRetType}', '${varRetTypeCode}','${month}', '${year}', loadCustomersInDropdown);
					invsDownloads('${id}', '${client.id}','${varRetType}','${otherreturnType}','${varRetTypeCode}', '${month}', '${year}', '${usertype}', '${fullname}', booksOrReturns);
				}	
				clearInvFiltersss( '${varRetType}','${varRetTypeCode}',booksOrReturns);
				
				//TODO need to apply the filter manipulation		
				initializeRemoveAppliedFilters('${varRetType}','${varRetTypeCode}','${otherreturnType}');
				//loadEwayBillStatus('${varRetType}','${varRetTypeCode}',connSttaus,dwldflag);
				loadEwayBillStatus('${varRetType}','${varRetTypeCode}',ewaybillAuthSttaus);
				is${tabName}Loaded = true;
			}else{
				clearInvFiltersss( '${varRetType}','${varRetTypeCode}',booksOrReturns);
				if(booksOrReturns == "" && varRetTypeCode !="Purchase_Register"){
					subscriptioncheck(varRetTypeCode);	
				}
				var pUrl = invTableUrl['${varRetTypeCode}'];
				invTable['${varRetTypeCode}'].ajax.url(pUrl).load();
				invsDownloads('${id}', '${client.id}','${varRetType}','${otherreturnType}','${varRetTypeCode}', '${month}', '${year}', '${usertype}', '${fullname}', booksOrReturns);
			}
			if(otherconfigdetails.enableTransDate == true){
				$('.invoiceview').val('Transaction Date');
			}else{
				$('.invoiceview').val('Invoice Date');
			}
			if(otherconfigdetails.enableinvoiceview == true){
				$('.invoiceview1').val('Return Period');
			}else{
				$('.invoiceview1').val('Invoice Date');
			}
	   }else{
		   if('#${tabName}' != "#reconcileTab" && '#${tabName}' !="#gstr2bmisMatchTab"){
			   $('.inv_view_txt').text('Expenses');
			   var invviewoption = '${client.invoiceViewOption}';
				if(invviewoption == "Yearly"){
					var finyr = $('#invoiceviewfinancialYear').val();
					var yr = parseInt(finyr);
					 loadExpenseTable('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', 0,yr, '${usertype}', '${fullname}','${otherreturnType}');
				}else{
			   		loadExpenseTable('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', '${month}', '${year}', '${usertype}', '${fullname}','${otherreturnType}');
				}
			   expensesDownloads('${id}', '${client.id}','${varRetType}','${otherreturnType}','${varRetTypeCode}', '${month}', '${year}');
			   initiateExpensesCallBacksForMultiSelect('${varRetType}','${varRetTypeCode}','${otherreturnType}');
			   loadExpensesSupport('${client.id}', '${varRetType}', '${varRetTypeCode}','${month}', '${year}', loadCategoryInDropdown);	
			   initializeExpensesRemoveAppliedFilters('${varRetType}','${varRetTypeCode}');
			   clearExpFiltersss( '${varRetType}','${varRetTypeCode}');			   
		   }else if('#${tabName}' != "#reconcileTab"){
				loadMismatchUsersByClient('${id}', '${client.id}', loadMismatchUsersInDropDown);
				loadMismatchInvoiceSupport('${id}', '${client.id}', '${month}', '${year}', loadMismatchCustomersInDropdown);
				loadMismatchInvTable('${id}', '${client.id}', '${month}', '${year}');
				initiateCallBacksForMultiSelectMismatch();
				initializeRemoveAppliedFiltersMismatch();
				loadReconcileSummary('${id}', '${client.id}', '${month}', '${year}');
		   }else if('#${tabName}' !="#gstr2bmisMatchTab"){
			    loadGstr2bReconcileUsersByClient('${id}', '${client.id}', loadGstr2bReconcileUsersInDropDown);
				loadGstr2bReconcileInvoiceSupport('${id}', '${client.id}', '${month}', '${year}', loadGstr2bReconcileCustomersInDropdown);
				loadGstr2bReconcileInvTable('${id}', '${client.id}', '${month}', '${year}');
				initiateCallBacksForMultiSelectGstr2bReconcile();
				initializeRemoveAppliedFiltersGstr2bReconcile();
				loadGstr2bReconcileSummary('${id}', '${client.id}', '${month}', '${year}');
		   }
	   }
	});
	
	$('#invoiceviewfinancialYear').on('change',function(){
	var varRetTypeCode='<c:out value="${varRetTypeCode}"/>';
	var finyr = $('#invoiceviewfinancialYear').val();
	var yr = parseInt(finyr);
	var booksOrReturns = '<c:out value="${otherreturnType}"/>';
	var pendinginv = '<c:out value="${client.prevPendingInv}"/>';
	var clientfilingStatus = '<c:out value="${client.status}"/>';
	if(varRetTypeCode == "Purchase_Register" || varRetTypeCode == "PurchaseOrder" || varRetTypeCode == "GSTR1"|| varRetTypeCode == "PROFORMAINVOICES"|| varRetTypeCode == "DELIVERYCHALLANS"|| varRetTypeCode == "ESTIMATES"){
		var financialYear = $('#invoiceviewfinancialYear').val();
		var yesr = $('#invoiceviewfinancialYear option:selected').text();
		$('.summary_retperiod').each(function(e) {
			var str1 = $(this).text().substr(0,$(this).text().indexOf('Of'));
			var str = $(this).text().substr($(this).text().indexOf('Of'));
			str = str.replace(str,"");
			$(this).html(str1+' Of <span style="color:#ff9900">'+yesr+' </span>');
		});
		
		clearInvFiltersss( '${varRetType}','${varRetTypeCode}',booksOrReturns);
		initiateCallBacksForMultiSelect('${varRetType}','${varRetTypeCode}','${otherreturnType}');
		var pUrl = invTableUrl['${varRetTypeCode}'];
		if(invTable['${varRetTypeCode}']){
			pUrl = _getContextPath()+'/getAddtionalInvs/${id}/${client.id}/${varRetType}/${month}/'+yr+'?booksOrReturns=${otherreturnType}&reportType=notreports';
			invTableUrl['${varRetTypeCode}'] = pUrl;
			invTable['${varRetTypeCode}'].ajax.url(pUrl).load();
		}
		loadUsersByClient('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}',loadUsersInDropDown);		
		loadInvoiceSupport('${client.id}', '${varRetType}', '${varRetTypeCode}',0, yr+1, loadCustomersInDropdown);		
		invsDownloads('${id}', '${client.id}','${varRetType}','${otherreturnType}','${varRetTypeCode}', '${month}', yr+1, '${usertype}', '${fullname}', booksOrReturns);
		initializeRemoveAppliedFilters('${varRetType}','${varRetTypeCode}','${otherreturnType}');
		$('.deleteAllInvoices${varRetType.replaceAll(' ', '_')}').attr("onclick","showDeleteAllPopup('${varRetType}','${otherreturnType}','${otherreturnType}','deleteAll','${month}','"+yr+"')")
	}else if(varRetTypeCode == "EXPENSES"){
		var pUrl = invTableUrl['EXPENSES'];
		if(invTable['EXPENSES']){
			pUrl = _getContextPath()+'/getExpenses/${id}/${client.id}/EXPENSES/${month}/'+yr+'?booksOrReturns=${otherreturnType}&reportType=notreports';
			invTableUrl['EXPENSES'] = pUrl;
			invTable['EXPENSES'].ajax.url(pUrl).load();
		}
		   expensesDownloads('${id}', '${client.id}','${varRetType}','${otherreturnType}','${varRetTypeCode}', '${month}', yr+1);
		   initiateExpensesCallBacksForMultiSelect('${varRetType}','${varRetTypeCode}','${otherreturnType}');
		   loadExpensesSupport('${client.id}', '${varRetType}', '${varRetTypeCode}',0, yr+1, loadCategoryInDropdown);	
		   initializeExpensesRemoveAppliedFilters('${varRetType}','${varRetTypeCode}');
		   clearExpFiltersss( '${varRetType}','${varRetTypeCode}');
	}
});
	$('.invoiceDateFilter').click(function () {
		$('.dropdown-content#invDateDropdown').show();
	});
	$('.paymentDateFilter').click(function () {
		$('.dropdown-content#pmntDateDropdown').show();
	});
	$('.pmntdatewrap').click(function(){
		$('.expense_pmntDate').focus();
	});
</script>