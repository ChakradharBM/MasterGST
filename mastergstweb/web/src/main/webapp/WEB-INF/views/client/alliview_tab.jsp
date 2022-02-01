	<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
	<c:set var="varRetType" value='<%= (String)request.getParameter("returntype") %>'/>
	<c:set var="tabName" value='<%= (String)request.getParameter("tabName") %>'/>
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
	<style>
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
	<h4 class="hdrtitlefiling" style="color: #404144; font-size: 18px;  margin-bottom: 5px; line-height: 2; display: table; width: 100%;">
	<c:if test="${otherreturn_type ne 'additionalInv'}">
		<c:if test="${varRetType eq 'Purchase Register'}">
	<label> , Invoice View By</label>
	<select class="invoiceview" style="font-size: 16px;margin-left: 5px;" onchange="invoiceviewByTrDate(this)"><option value="Invoice Date">Invoice Date</option><option value="Transaction Date">Transaction Date</option></select></c:if>
	<c:if test="${varRetType eq 'GSTR1' || varRetType eq varANX1}">
	<label> , Invoice View By</label>
	<select class="invoiceview1" style="font-size: 16px;margin-left: 5px;" onchange="invoiceviewByfp(this)"><option value="Return Period">Return Period</option><option value="Invoice Date">Invoice Date</option></select></c:if>
		<c:if test="${varRetType eq varGSTR1 || varRetType eq varGSTR4 || varRetType eq varGSTR5 || varRetType eq varGSTR6 || varRetType eq varANX1}"><span class="text-right" style="font-size:18px; margin-top:-2px; margin-left:5px">and Filing Status : 
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
	<select class="invoiceview" style="font-size: 16px;margin-left: 5px;" onchange="invoiceviewByTrDate(this)"><option value="Invoice Date">Invoice Date</option><option value="Transaction Date">Transaction Date</option></select></c:if>
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
			<c:otherwise>Sales Invoices</c:otherwise>
			</c:choose>
		</span>
		<span class="text-right" style="float: right;margin-top: -19px;font-size:18px;">Total 
		<c:choose>
		<c:when test="${varRetType eq varPurchase || varRetType eq varGSTR2 || varRetType eq varGSTR6}">Claimed ITC:</c:when>
		<c:when test="${varRetType eq 'Unclaimed'}">UnClaimed ITC:</c:when>
		<c:when test="${varRetType eq varGSTR2A}">Eligible ITC:</c:when>
		<c:when test="${varRetType eq 'EWAYBILL'}">Amount:</c:when>
		<c:otherwise>Sales:</c:otherwise>
		</c:choose>
			<span class="total_amount" style="font-size:27px; margin-left:0px!important">
				<span class="ind_format" id="idTabTotal${varRetType.replaceAll(' ', '_')}">
				</span>
			</span>
		</span>
	</h4>
	<div class="normaltable meterialform">
		<div class="filter">
			<div class="noramltable-row">
				<div class="noramltable-row-hdr">Filter</div>
				<div class="noramltable-row-desc">
				<div class="sfilter">
					<span id="divFilters${varRetType.replaceAll(' ', '_')}"></span>
					<span class="btn-remove-tag" onclick="clearFilters('${varRetType}')">Clear All<span data-role="remove"></span></span>
				</div>
				</div>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Search Filter</div>
			<div class="noramltable-row-desc">
			
				<c:choose>
					<c:when test="${(otherreturnType eq 'SalesRegister' || otherreturnType eq 'PurchaseRegister') && (varRetType ne varDeliverchallan) && (varRetType ne varProforma) && (varRetType ne varEstimates) && (varRetType ne varPurchaseOrder)&& (varRetType ne 'EWAYBILL')}">
						<select id="multiselect${varRetType.replaceAll(' ', '_')}1" class="multiselect-ui form-control" multiple="multiple">
							<option value="Paid">Paid</option>
							<option value="Not Paid">Not Paid</option>
							<option value="Partially Paid">Partially Paid</option>
						</select>	
					</c:when>
					<c:when test="${varRetType ne varPurchase && varRetType ne varGSTR2A && varRetType ne 'Unclaimed' && varRetType ne varDeliverchallan && varRetType ne varProforma && varRetType ne varEstimates && varRetType ne varPurchaseOrder && otherreturnType ne 'SalesRegister' && otherreturnType ne 'PurchaseRegister'&& varRetType ne 'EWAYBILL'}">
						<select id="multiselect${varRetType.replaceAll(' ', '_')}1" class="multiselect-ui form-control" multiple="multiple">
							<option value="SUCCESS">Uploaded</option>
							<option value="Filed">Filed</option>
							<option value="${statusSubmitted}">${statusSubmitted}</option>
							<option value="CANCELLED">Cancelled</option>
							<option value="In Progress">In Progress</option>
							<option value="Pending">Pending</option>
							<option value="Failed">Failed</option>
						</select>
					</c:when>
				</c:choose>
				<c:if test="${varRetType ne varDeliverchallan && varRetType ne varProforma && varRetType ne varEstimates && varRetType ne varPurchaseOrder}">
				<span class="invoiceTypelist">
				<select id="multiselect${varRetType.replaceAll(' ', '_')}2" class="multiselect-ui form-control" multiple="multiple">
					<c:if test="${varRetType eq 'GSTR1' || varRetType eq varANX1 || varRetType eq 'GSTR1Amnd' || varRetType eq 'GSTR2' || varRetType eq varPurchase || varRetType eq 'GSTR2A' || varRetType eq 'Unclaimed' || varRetType eq 'GSTR4'}">
					<option value="<%=MasterGSTConstants.ADVANCES%>">Advance Receipts (Receipt voucher)</option>
					<option value="Adv. Adjustments">Adv. Adjustments</option>
					</c:if>
					<c:if test="${varRetType eq 'EWAYBILL' || varRetType eq 'GSTR1' || varRetType eq varANX1 || varRetType eq 'GSTR1Amnd' || varRetType eq 'GSTR2' || varRetType eq varPurchase || varRetType eq 'GSTR2A' || varRetType eq 'Unclaimed' || varRetType eq 'GSTR6' || varRetType eq 'GSTR5' || varRetType eq 'GSTR4'}">
					<option value="B2B">B2B Invoices</option>
					</c:if>
					<c:if test="${varRetType eq 'GSTR2' || varRetType eq varPurchase || varRetType eq 'GSTR2A' || varRetType eq 'Unclaimed' || varRetType eq 'GSTR4'}">
					<option value="<%=MasterGSTConstants.B2BUR%>"><%=MasterGSTConstants.B2BUR%></option>
					</c:if>
					<c:if test="${varRetType eq 'GSTR1' || varRetType eq varANX1 || varRetType eq 'GSTR1Amnd' || varRetType eq 'GSTR2' || varRetType eq varPurchase || varRetType eq 'GSTR2A' || varRetType eq 'Unclaimed' || varRetType eq 'GSTR5'}">
					<option value="B2C">B2CS (Small) Invoices</option>
					</c:if>
					<c:if test="${varRetType eq 'GSTR1' || varRetType eq varANX1 || varRetType eq 'GSTR1Amnd' || varRetType eq 'GSTR2' || varRetType eq varPurchase || varRetType eq 'GSTR2A' || varRetType eq 'Unclaimed'}">
					<option value="B2CL">B2CL (Large)</option>
					</c:if>
					<c:if test="${varRetType eq 'GSTR1' || varRetType eq varANX1 || varRetType eq 'GSTR1Amnd' || varRetType eq 'GSTR2' || varRetType eq varPurchase || varRetType eq 'GSTR2A' || varRetType eq 'Unclaimed' || varRetType eq 'GSTR6' || varRetType eq 'GSTR5' || varRetType eq 'GSTR4'}">
					<option value="Debit Note">Debit Note</option>
					<option value="Credit Note">Credit Note</option>
					</c:if>
					<c:if test="${varRetType eq 'GSTR1' || varRetType eq varANX1 || varRetType eq 'GSTR1Amnd' || varRetType eq 'GSTR2' || varRetType eq varPurchase || varRetType eq 'GSTR2A' || varRetType eq 'Unclaimed' || varRetType eq 'GSTR5' || varRetType eq 'GSTR4'}">
					<option value="Debit Note(UR)">Debit Note(UR)</option>
					<option value="Credit Note(UR)">Credit Note(UR)</option>
					</c:if>
					<c:if test="${varRetType eq 'GSTR1' || varRetType eq varANX1 || varRetType eq 'GSTR1Amnd'}">>
					<option value="<%=MasterGSTConstants.EXPORTS%>"><%=MasterGSTConstants.EXPORTS%></option>
					</c:if>
					<c:if test="${varRetType eq 'GSTR2' || varRetType eq varPurchase || varRetType eq 'GSTR2A' || varRetType eq 'Unclaimed' || varRetType eq 'GSTR5'}">
					<option value="<%=MasterGSTConstants.IMP_GOODS%>"><%=MasterGSTConstants.IMP_GOODS%>(Bill of Entry)</option>
					</c:if>
					<c:if test="${varRetType eq 'GSTR2' || varRetType eq varPurchase || varRetType eq 'GSTR2A' || varRetType eq 'Unclaimed' || varRetType eq 'GSTR4'}">
					<option value="<%=MasterGSTConstants.IMP_SERVICES%>"><%=MasterGSTConstants.IMP_SERVICES%></option>
					</c:if>
					<c:if test="${varRetType eq 'GSTR2' || varRetType eq varPurchase || varRetType eq 'GSTR2A' || varRetType eq 'Unclaimed'}">
					<option value="<%=MasterGSTConstants.ITC_REVERSAL%>"><%=MasterGSTConstants.ITC_REVERSAL%></option>
					</c:if>
					<c:if test="${varRetType eq 'GSTR1' || varRetType eq varANX1 || varRetType eq 'GSTR1Amnd' || varRetType eq 'GSTR2' || varRetType eq varPurchase || varRetType eq 'GSTR2A' || varRetType eq 'Unclaimed'}">
					<option value="<%=MasterGSTConstants.NIL%>">Bill of Supply</option>
					</c:if>
					<c:if test="${varRetType eq 'GSTR2' || varRetType eq varPurchase || varRetType eq 'GSTR2A' || varRetType eq 'Unclaimed' || varRetType eq 'GSTR6'}">
					<option value="<%=MasterGSTConstants.ISD%>">ISD</option>
					</c:if>
				</select>
				</span>
				</c:if>
				<c:if test="${varRetType ne 'EWAYBILL'}">
				<span class="multiselectuserlist">
					<select id="multiselect${varRetType.replaceAll(' ', '_')}3" class="multiselect-ui form-control" multiple="multiple">
					</select>
				</span>
				</c:if>
				<span class="multiselectsupplierlist">
				<select id="multiselect${varRetType.replaceAll(' ', '_')}4" class="multiselect-ui form-control" multiple="multiple">
				</select>
				</span>
				<c:if test="${varRetType ne 'EWAYBILL'}"><select id="multiselect${varRetType.replaceAll(' ', '_')}5" class="multiselect-ui form-control" multiple="multiple">
				</select>
				<select id="multiselect${varRetType.replaceAll(' ', '_')}6" class="multiselect-ui form-control" multiple="multiple"></select></c:if>
				<c:if test="${varRetType eq 'EWAYBILL'}">
				<select id="multiselect${varRetType.replaceAll(' ', '_')}8" class="multiselect-ui form-control" multiple="multiple">
				<option value="O">Outward</option><option value="I">Inward</option>
				</select>
				</c:if>
				<c:if test="${varRetType ne varDeliverchallan && varRetType ne varProforma && varRetType ne varEstimates && varRetType ne varPurchaseOrder && varRetType ne 'EWAYBILL'}">
					<select id="multiselect${varRetType.replaceAll(' ', '_')}7" class="multiselect-ui form-control" multiple="multiple">
					<option value="Reverse">Yes</option>
					<option value="Regular">No</option>
					</select>
				</c:if>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Filter Summary</div>
			<div class="noramltable-row-desc">
				<div class="normaltable-col hdr">Total Invoices
					<div class="normaltable-col-txt" id="idCount${varRetType.replaceAll(' ', '_')}"></div>
				</div>
				<div class="normaltable-col hdr">Total Amount 
					<div class="normaltable-col-txt" id="idTotAmtVal${varRetType.replaceAll(' ', '_')}"></div>
				</div>
				<div class="normaltable-col hdr">Total Taxable Value
					<div class="normaltable-col-txt" id="idTaxableVal${varRetType.replaceAll(' ', '_')}"></div>
				</div>
				<c:if test="${varRetType eq 'GSTR1' || varRetType eq 'SalesRegister'}">
					<div class="normaltable-col hdr">Total Exempted
						<div class="normaltable-col-txt" id="idExemptedVal${varRetType.replaceAll(' ', '_')}"></div>
					</div>
				</c:if>
				<div class="normaltable-col hdr">Total Tax Value
					<div class="normaltable-col-txt" id="idTaxVal${varRetType.replaceAll(' ', '_')}"></div>
				</div>
				<div class="normaltable-col hdr filsummary">Total IGST
					<div class="normaltable-col-txt" id="idIGST${varRetType.replaceAll(' ', '_')}"></div>
				</div>
				<div class="normaltable-col hdr filsummary">Total CGST
					<div class="normaltable-col-txt" id="idCGST${varRetType.replaceAll(' ', '_')}"></div>
				</div>
				<div class="normaltable-col hdr filsummary">Total SGST
					<div class="normaltable-col-txt" id="idSGST${varRetType.replaceAll(' ', '_')}"></div>
				</div>
				<div class="normaltable-col hdr filsummary">Total CESS
					<div class="normaltable-col-txt" id="idCESS${varRetType.replaceAll(' ', '_')}"></div>
				</div>
				<c:if test="${varRetType eq 'GSTR2' || varRetType eq varPurchase || varRetType eq 'Unclaimed' || varRetType eq varGSTR6}">
				<div class="normaltable-col hdr">ITC Available
					<div class="normaltable-col-txt" id="idITC${varRetType.replaceAll(' ', '_')}"></div>
				</div>
				</c:if>
			</div>
		</div>
	</div>
	<h4 class="hdrtitle" style="margin:0px">
	<c:if test="${varRetType eq varEWAYBILL}">
		<a href="#" class="btn btn-greendark pull-right btn-all-iview-sales btn-sm downloadbtn" style="margin-bottom:5px!important;" onclick="downloadEwayBIllInv('${returntype}')">DOWNLOAD FROM EWAYBILL</a>
		<a href="#" class="btn btn-blue pull-right btn-all-iview-sales btn-sm disabled" id="addtogstr1btn" onclick="addtoGSTR1(this,true)">ADD TO SaleRegister</a>
		<a href="#" onclick="showDeleteAllPopup('${contextPath}/delinvs/${urlSuffix}/${month}/${year}?type=inv','<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>')" class="btn btn-blue permissionInvoices-Sales-Delete pull-right btn-all-iview-sales disabled" id="deleteEwaybillInvoices">Delete</a>
		<a href="#" data-toggle="modal" data-target="#vehicleUpdateModal" id="vehicleupdate" class="btn btn-blue pull-right btn-all-iview-sales disabled" onclick="vehicleUpdate('${returntype}')">Update Vehicle</a>
		</c:if>
		<c:if test="${varRetType eq varPurchase}">
		<a href="#" onclick="showDeleteAllPopup('${contextPath}/delinvs/${urlSuffix}/${month}/${year}?type=inv','${varRetType}','<c:out value="${otherreturnType}"/>')" class="btn btn-blue pull-right permissionInvoices-Purchase-Delete btn-all-iview disabled" id="deletePurchaseInvoices" style="margin-top:2px">Delete</a>
		<a href="" data-toggle="modal" data-target="#importModal" style="margin-top:2px" class="btn btn-blue pull-right btn-all-iview permissionGeneral-Import_Purchases <c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">disable</c:if>" onClick="updateImportModal('<%=MasterGSTConstants.PURCHASE_REGISTER%>')">Import</a>
		<a href="#" class="btn btn-blue pull-right btn-all-iview-sales" id="itcclaimbtn" data-toggle="modal" data-target="#claimModal" onclick="">ITC Claim</a>
		</c:if>
		<c:if test="${otherreturn_type ne 'additionalInv'}">
		<c:if test="${varRetType ne varPurchase && varRetType ne varGSTR2A && varRetType ne 'Unclaimed' && varRetType ne varProforma && varRetType ne varDeliverchallan && varRetType ne varEstimates && varRetType ne varPurchaseOrder}">
		<c:if test="${varRetType ne 'GSTR1Amnd' && varRetType ne varEWAYBILL && varRetType ne varANX1A}">
		<a href="#" id="idPermissionUpload_Invoice" class="btn btn-greendark pull-right btn-all-iview-sales btn-sm uploadbtn <c:if test="${varRetType eq varGSTR1 || varRetType eq varGSTR5}">permissionGSTN_Actions-Upload_Sales</c:if> <c:if test="${varRetType eq varGSTR2 || varRetType eq varGSTR4 || varRetType eq varGSTR6 || varRetType eq varANX1}">permissionGSTN_Actions-Upload_Purchases</c:if> <c:if test="${varRetType eq varGSTR2}">disabled</c:if>" <c:if test="${varRetType ne varGSTR2}">onclick="verifyAccess(this)"</c:if> <c:if test="${varRetType eq varGSTR2}">data-toggle="tooltip" data-placement="top" title="GSTR2 is not Available"</c:if>>Upload To GSTN</a>
		</c:if>
		<c:if test="${varRetType ne varEWAYBILL}">
		<a href="#" class="btn btn-greendark<c:if test="${varRetType eq varGSTR1 || varRetType eq varGSTR5 || varRetType eq varANX1}"> permissionGSTN_Actions-Download_Sales</c:if> <c:if test="${varRetType eq varGSTR2 || varRetType eq varGSTR4 || varRetType eq varGSTR6}"> permissionGSTN_Actions-Download_Purchases</c:if> pull-right btn-all-iview-sales btn-sm downloadbtn <c:if test="${varRetType eq varGSTR2}">disabled</c:if>"  <c:if test="${varRetType ne varGSTR2}">onClick="downloadFromGSTN('<c:out value="${varRetType}"/>')" </c:if>  <c:if test="${varRetType eq varGSTR2}">data-toggle="tooltip" data-placement="top" title="GSTR2 is not Available"</c:if>>Download from GSTN</a>
		</c:if>
		</c:if>
		</c:if>
		<c:if test="${varRetType eq varGSTR1A || varRetType eq 'GSTR1Amnd' || varRetType eq varANX1A}">
		<a href="#" class="btn btn-red reject" onclick="processInv(this,false)" style="margin-right:2px!important">Reject</a>
		<a href="#" class="btn btn-green accept" onclick="processInv(this,true)">Accept</a>
		</c:if>
		<c:if test="${varRetType eq 'DELIVERYCHALLANS'}">
		<a href="#" onclick="showDeleteAllPopup('${contextPath}/delinvs/${urlSuffix}/${month}/${year}?type=inv','<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>')" class="btn btn-blue permissionInvoices-Sales-Delete pull-right btn-all-iview-sales disabled" id="deleteDelChallanInvoices">Delete</a>
		<a href="#" class="btn btn-blue pull-right" onClick="showInvPopup('<%=MasterGSTConstants.DELIVERYCHALLANS%>','<c:out value="${varRetType}"/>',true)">Add Delivery Challans</a>
		</c:if>
		<c:if test="${varRetType eq 'PROFORMAINVOICES'}">
		<a href="#" onclick="showDeleteAllPopup('${contextPath}/delinvs/${urlSuffix}/${month}/${year}?type=inv','<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>')" class="btn btn-blue permissionInvoices-Sales-Delete pull-right btn-all-iview-sales disabled" id="deleteProformaInvoices">Delete</a>
		<a href="#" class="btn btn-blue pull-right" link='${contextPath}/syncinv/${urlSuffix}' data-toggle="modal" data-target="#proformainvoiceModal" onClick="showInvPopup('<%=MasterGSTConstants.PROFORMAINVOICES%>','<c:out value="${varRetType}"/>',true)">Add Proforma Invoices</a>
		</c:if>
		<c:if test="${varRetType eq 'ESTIMATES'}">
		<a href="#" onclick="showDeleteAllPopup('${contextPath}/delinvs/${urlSuffix}/${month}/${year}?type=inv','<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>')" class="btn btn-blue permissionInvoices-Sales-Delete pull-right btn-all-iview-sales disabled" id="deleteEstimateInvoices">Delete</a>
		<a href="#" class="btn btn-blue pull-right" link='${contextPath}/syncinv/${urlSuffix}' data-toggle="modal" data-target="#estimateModal" onClick="showInvPopup('<%=MasterGSTConstants.ESTIMATES%>','<c:out value="${varRetType}"/>',true)">Add Estimates</a>
		</c:if>
		<c:if test="${varRetType eq 'PurchaseOrder'}">
		<a href="#" onclick="showDeleteAllPopup('${contextPath}/delinvs/${urlSuffix}/${month}/${year}?type=inv','${varRetType}','<c:out value="${otherreturnType}"/>')" class="btn btn-blue pull-right permissionInvoices-Purchase-Delete btn-all-iview disabled" id="deletePurchaseOrderInvoices" style="margin-top:2px">Delete</a>
		<a href="#" class="btn btn-blue pull-right" link='${contextPath}/syncinv/${urlSuffix}' data-toggle="modal" data-target="#estimateModal" onClick="showInvPopup('<%=MasterGSTConstants.PURCHASEORDER%>','<c:out value="${varRetType}"/>',true)">Add Purchase Order</a>
		</c:if>
		<c:if test="${varRetType eq varGSTR1 || varRetType eq varGSTR4 || varRetType eq varGSTR5 || varRetType eq varANX1}">
		<c:if test="${otherreturn_type ne 'additionalInv'}"><c:if test="${varRetType eq varGSTR1}"><a type="button" class="btn btn-greendark" style="color:white; box-shadow:none; font-size:14px" href="${contextPath}/updateInvStatus/${id}/${fullname}/${usertype}/${client.id}/GSTR1/${month}/${year}">Status<i class="fa fa-refresh"  style="font-size: 14px; color: #fff; margin-left:5px"></i></a></c:if></c:if>
		<a href="" data-toggle="modal" data-target="#importModal" onClick="updateImportModal('<%=MasterGSTConstants.GSTR1%>')" class="btn btn-blue pull-right btn-all-iview-sales permissionGeneral-Import_Sales <c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">disable</c:if>">Import</a>
		<a href="#" onclick="showDeleteAllPopup('${contextPath}/delinvs/${urlSuffix}/${month}/${year}?type=inv','<c:out value="${varRetType}"/>','<c:out value="${otherreturnType}"/>')" class="btn btn-blue permissionInvoices-Sales-Delete pull-right btn-all-iview-sales disabled" id="deleteSalesInvoices">Delete</a>
		</c:if>
		<c:if test="${varRetType eq varGSTR6}">
		<a href="" data-toggle="modal" data-target="#importModal" onClick="updateImportModal('<%=MasterGSTConstants.PURCHASE_REGISTER%>')" class="btn btn-blue permissionGeneral-Import_Purchases <c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">disable</c:if>">Import</a>
		<a href="#" onclick="showDeleteAllPopup('${contextPath}/delinvs/${urlSuffix}/${month}/${year}?type=inv','<c:out value="${varRetType}"/>')" class="btn btn-blue permissionInvoices-Purchase-Delete pull-right btn-all-iview-sales disabled" id="deletePurchaseInvoices">Delete</a>
		</c:if>
		<c:if test="${varRetType eq varGSTR2A}">
		<a href="#" class="btn btn-greendark permissionGSTN_Actions-Download_GSTR2A pull-right btn-all-iview-sales btn-sm downloadbtn" onClick="downloadFromGSTN('<c:out value="${varRetType}"/>')" link='${contextPath}/dwnldinv/${urlSuffix}'>Download from GSTN</a>
		<a href="#" link="${contextPath}/reconcileinv/${urlSuffix}/${month}/${year}" onClick="reconcileGstr2a()" class="btn btn-greendark pull-right btn-all-iview-sales btn-sm reconcile_btn">Reconcile</a>
		<a href="${contextPath}/populatetradename/${usertype}/${varRetType}/${fullname}/${id}/${client.id}/${month}/${year}" class="btn btn-blue gstr2atradename" style="float:left;margin-left: 0px;">Populate Supplier Name</a>
		</c:if>
		<c:if test="${varRetType ne varProforma && varRetType ne varDeliverchallan && varRetType ne varEstimates && varRetType ne varPurchaseOrder}">
		<%-- <a href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/itemwise" class="btn btn-blue excel" onClick="excelreport()">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a> --%>
		<c:if test="${varRetType eq varEWAYBILL}">
		 <a href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/itemwise" class="btn btn-blue excel" onClick="excelreport()">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>
		<a  href="#" class="btn btn-blue" id="addEwaybillinv" onClick="showInvPopup('<%=MasterGSTConstants.B2B%>','<c:out value="${varRetType}"/>',true)" link1="${contextPath}/addEwaybillinv/${urlSuffix}?stype=<%=MasterGSTConstants.EWAYBILL%>">Generate EwayBill</a>
		</c:if>
		<c:if test="${varRetType ne varEWAYBILL}"><c:if test="${varRetType eq varGSTR1}"><div class="dropdown pull-right permissionExcel_Download_In_Books_And_Returns-Sales" style="margin-left: 10px;"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;"><a class="dropdown-item" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/itemwise">ITEM WISE DOWNLOAD</a><a class="dropdown-item" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/invoicewise">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" href="${contextPath}/fulldwnldmonthlyxls/${id}/${client.id}/${varRetType}/${month}/${year}">ALL DETAILS DOWNLOAD</a></div></div></div></c:if>
		<c:if test="${varRetType eq varGSTR2}"><div class="dropdown pull-right permissionExcel_Download_In_Books_And_Returns-GSTR2" style="margin-left: 10px;"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;"><a class="dropdown-item" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/itemwise">ITEM WISE DOWNLOAD</a><a class="dropdown-item" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/invoicewise">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" href="${contextPath}/fulldwnldmonthlyxls/${id}/${client.id}/${varRetType}/${month}/${year}">ALL DETAILS DOWNLOAD</a></div></div></div></c:if>
		<c:if test="${varRetType eq varPurchase}"><div class="dropdown pull-right permissionExcel_Download_In_Books_And_Returns-Purchases" style="margin-left: 10px;"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;"><a class="dropdown-item" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/itemwise">ITEM WISE DOWNLOAD</a><a class="dropdown-item" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/invoicewise">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" href="${contextPath}/fulldwnldmonthlyxls/${id}/${client.id}/${varRetType}/${month}/${year}">ALL DETAILS DOWNLOAD</a></div></div></div></c:if>
		<c:if test="${varRetType eq 'GSTR1Amnd'}"><div class="dropdown pull-right permissionExcel_Download_In_Books_And_Returns-GSTR1A" style="margin-left: 10px;"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;"><a class="dropdown-item" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/itemwise">ITEM WISE DOWNLOAD</a><a class="dropdown-item" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/invoicewise">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" href="${contextPath}/fulldwnldmonthlyxls/${id}/${client.id}/${varRetType}/${month}/${year}">ALL DETAILS DOWNLOAD</a></div></div></div></c:if>
		<c:if test="${varRetType eq varGSTR2A}"><div class="dropdown pull-right permissionExcel_Download_In_Books_And_Returns-GSTR2A" style="margin-left: 10px;"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;"><a class="dropdown-item" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/itemwise">ITEM WISE DOWNLOAD</a><a class="dropdown-item" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/invoicewise">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" href="${contextPath}/fulldwnldmonthlyxls/${id}/${client.id}/${varRetType}/${month}/${year}">ALL DETAILS DOWNLOAD</a></div></div></div></c:if>
		<c:if test="${varRetType eq 'Unclaimed'}"><a href="#" class="btn btn-blue pull-right btn-all-iview-sales btn-sm" id="itcclaimbtn" data-toggle="modal" data-target="#claimModal" onclick="">ITC Claim</a><div class="dropdown pull-right permissionExcel_Download_In_Books_And_Returns-Unclaimed_ITC" style="margin-left: 10px;"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;"><a class="dropdown-item" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/itemwise">ITEM WISE DOWNLOAD</a><a class="dropdown-item" href="${contextPath}/dwnldxls/${id}/${client.id}/${varRetType}/${month}/${year}/invoicewise">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" href="${contextPath}/fulldwnldmonthlyxls/${id}/${client.id}/${varRetType}/${month}/${year}">ALL DETAILS DOWNLOAD</a></div></div></div></c:if>
		<a href="#"  class="btn btn-blue recordpayments disabled" data-toggle="modal" data-target="#paymentsModal" onclick="updatePayment('${varRetType}')"><c:if test="${varRetType eq 'GSTR1' || varRetType eq varANX1 || varRetType eq varANX1A || varRetType eq 'GSTR1Amnd'}">Add Receipt</c:if><c:if test="${varRetType eq 'GSTR2' || varRetType eq varANX2 || varRetType eq varPurchase || varRetType eq 'Unclaimed' || varRetType eq varGSTR2A}">Add Payment</c:if></a>
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
						<c:when test="${varRetType eq varPurchase}">
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
				<c:when test="${varRetType eq varPurchase}">
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
					<c:if test="${varRetType eq varGSTR1 || varRetType eq varANX1}">
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
	<div id="invview_Process" class=""  style="color:red;font-size:20px;position:absolute;z-index:99;margin-top:134px;top:37%;left: 46%;"></div>
	<div class="customtable db-ca-view tabtable1 invtabtable1 dbTable${varRetType.replaceAll(' ', '_')}">
		<table id='dbTable${varRetType.replaceAll(" ", "_")}' class="row-border dataTable meterialform" cellspacing="0" width="100%">
			<thead>
				<tr>
					<th class="text-center"><div class="checkbox"><label><input type="checkbox" id='check${varRetType.replaceAll(" ", "_")}' onClick="updateMainSelection('${varRetType}','${client.status}',this)"/><i class="helper"></i></label></div> </th>
					<th><c:choose><c:when test="${varRetType eq 'EWAYBILL'}">EBillNo</c:when><c:otherwise>Status</c:otherwise></c:choose></th>
					<th>EBill Date</th><th>Type</th>
					<th class="text-center">Invoice No</th>
					<c:if test="${varRetType eq varGSTR2 || varRetType eq varGSTR2A || varRetType eq varPurchase || varRetType eq 'Unclaimed' || varRetType eq varGSTR6 || varRetType eq varPurchaseOrder}">
					<th class="text-center" style="max-width:230px!important;width:auto!important;">Suppliers</th>
					</c:if>
					<c:if test="${varRetType ne varGSTR2 && varRetType ne varGSTR2A && varRetType ne varPurchase && varRetType ne 'Unclaimed' && varRetType ne varGSTR6 && varRetType ne varPurchaseOrder}">
					<th class="text-center" style="max-width:230px!important;width:auto!important;">Customers</th>
					</c:if>
					<th class="text-center">GSTIN</th><th class="text-center">Invoice Date</th><th class="text-center">Taxable Amt</th><th class="text-center">Total Tax</th><th class="text-center">Total Amt</th>
					<th class="text-center">Upload By</th><th class="text-center">Branch</th><th class="text-center">Vertical</th><th class="text-center">Customer ID</th><th class="text-center">Supply Type</th>
					<c:if test="${varRetType eq varPurchase || varRetType eq varGSTR2 || varRetType eq varGSTR6 || varRetType eq 'Unclaimed'}">
					<th class="text-center">ITC Claimed</th>
					</c:if>
					<c:if test="${varRetType ne varGSTR2 && varRetType ne varGSTR2A}">
					<th class="text-center"style="max-width:48px!important;width:auto!important;">Action</th>
					</c:if>
					<c:if test="${varRetType eq varGSTR2A}">
						<th class="text-center" style=""></th>
					</c:if>
				</tr>
			</thead>
			<tbody id='invBody${varRetType.replaceAll(" ", "_")}'>
			</tbody>
		</table>
	</div>
	<script type="text/javascript">
	var totalTaxableValue = 0, totalIGST = 0, totalCGST = 0, totalSGST = 0, totalCESS = 0,  totalITCIGST = 0, totalITCCGST = 0, totalITCSGST = 0, totalITCCESS = 0,totalTax = 0, totalITC = 0, totalValue = 0, totalInvoices = 0, totalUploaded = 0, totalPending = 0, totalFailed = 0, totalExemptedValue = 0;var NotInJournals = 'NotInJournals';var hsnrowCount = 1;
	var mnths=['Jan','Feb','Mar','Apr','May','June','July','Aug','Sept','Oct','Nov','Dec'];
	$('#${tabName}').on('click', function() {
	if('${varRetType}' == 'EWAYBILL'){
			var dwldflag = '<c:out value="${dwldflag}"/>';var eflag = '<c:out value="${eflag}"/>';var connSttaus = '<c:out value="${connSttaus}"/>';
			if(connSttaus == "Active" || dwldflag == "Active"){
				$('#authStatus').html('Active').css("color","green");
			}else if(connSttaus == ""){
				$('#authStatus').html('InActive, <a href="${contextPath}/cp_upload/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${month}"/>/<c:out value="${year}"/>?type=ewaybillconfig">configure now</a>').css("color","orange");
			}else{
			$('#authStatus').html('<span style="color:red;">Expired</span><a href="#" onclick="configauthentication()" style="color:green;"> <span id="inactivebtn">Authenticate Now</span></a>');
			}
			$('#gtab1').addClass("active");
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
		
	if('${varRetType}' != 'EWAYBILL'){
		$.ajax({
			url: "${contextPath}/cp_users/${id}/${client.id}",
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				if (response.length > 0) {
					$("#multiselect${varRetType.replaceAll(' ', '_')}3").append($("<option></option>").attr("value",globaluser).text(globaluser)); 
					response.forEach(function(cp_user) {
						$("#multiselect${varRetType.replaceAll(' ', '_')}3").append($("<option></option>").attr("value",cp_user.name).text(cp_user.name)); 
					});
				}else{
					$("#multiselect${varRetType.replaceAll(' ', '_')}3").append($("<option></option>").attr("value",globaluser).text(globaluser)); 
				}
				$("#multiselect${varRetType.replaceAll(' ', '_')}3").multiselect({
					nonSelectedText: '- User -',
					includeSelectAllOption: true,
					onChange: function(element, checked) {
						applyFilters('${varRetType}');
					},
					onSelectAll: function() {
						applyFilters('${varRetType}');
					},
					onDeselectAll: function() {
						applyFilters('${varRetType}');
					}
				});
				$("#multiselect${varRetType.replaceAll(' ', '_')}3").multiselect('refresh');
			}
		});
	}
		var custText = '';
		if('${varRetType}' == 'GSTR1' || '${varRetType}' == 'GSTR1Amnd' || '${varRetType}' == 'GSTR4' || '${varRetType}' == 'GSTR5' || '${varRetType}' == 'ESTIMATES' || '${varRetType}' == 'DELIVERYCHALLANS' || '${varRetType}' == 'PROFORMAINVOICES' || '${varRetType}' == 'EWAYBILL'){
			custText = '- Customers -';
		}else{
			custText = '- Suppliers -';
		}
		$('#multiselect${varRetType.replaceAll(" ", "_")}4').multiselect({
			nonSelectedText: custText,
			includeSelectAllOption: true,
			onChange: function(element, checked) {
				applyFilters('${varRetType}');
			},
			onSelectAll: function() {
				applyFilters('${varRetType}');
			},
			onDeselectAll: function() {
				applyFilters('${varRetType}');
			}
		});
		$('#divFilters${varRetType.replaceAll(' ', '_')}').on('click', '.deltag', function(e) {
			var val = $(this).data('val');
			<c:if test="${varRetType ne varPurchase && varRetType ne varGSTR2A}">
			$('#multiselect${varRetType.replaceAll(" ", "_")}1').multiselect('deselect', [val]);
			</c:if>
			<c:if test="${otherreturnType eq 'PurchaseRegister'}">
			$('#multiselect${varRetType.replaceAll(" ", "_")}1').multiselect('deselect', [val]);
			</c:if>
			$('#multiselect${varRetType.replaceAll(" ", "_")}2').multiselect('deselect', [val]);
			$('#multiselect${varRetType.replaceAll(" ", "_")}3').multiselect('deselect', [val]);
			$('#multiselect${varRetType.replaceAll(" ", "_")}4').multiselect('deselect', [val]);
			$('#multiselect${varRetType.replaceAll(" ", "_")}5').multiselect('deselect', [val]);
			$('#multiselect${varRetType.replaceAll(" ", "_")}6').multiselect('deselect', [val]);
			$('#multiselect${varRetType.replaceAll(" ", "_")}7').multiselect('deselect', [val]);
			$('#multiselect${varRetType.replaceAll(" ", "_")}8').multiselect('deselect', [val]);
			applyFilters('${varRetType}');
		});
		if('${varRetType}' != 'EWAYBILL'){
			<c:forEach items="${client.branches}" var="branch">
				$("#multiselect${varRetType.replaceAll(' ', '_')}5").append($("<option></option>").attr("value","${branch.name}").text("${branch.name}"));
			</c:forEach>
			<c:forEach items="${client.verticals}" var="vertical">
				$("#multiselect${varRetType.replaceAll(' ', '_')}6").append($("<option></option>").attr("value","${vertical.name}").text("${vertical.name}"));
			</c:forEach>
		}	
		var statusText = '';
		
		
		if(('${otherreturnType}' == 'SalesRegister' || '${otherreturnType}' == 'PurchaseRegister') && ('${varRetType}' != 'DELIVERYCHALLANS') && ('${varRetType}' != 'PROFORMAINVOICES') && ('${varRetType}' != 'ESTIMATES') && ('${varRetType}' != 'PurchaseOrder')){
			statusText = '- Payment Status -';
		}else{
			statusText = '- Invoice Status -';
		}
		
		<c:if test="${varRetType ne varPurchase && varRetType ne varGSTR2A}">
		$('#multiselect${varRetType.replaceAll(" ", "_")}1').multiselect({
			nonSelectedText: statusText,
			includeSelectAllOption: true,
			onChange: function(element, checked) {
				applyFilters('${varRetType}');
			},
			onSelectAll: function() {
				applyFilters('${varRetType}');
			},
			onDeselectAll: function() {
				applyFilters('${varRetType}');
			}
		});
		</c:if>
		<c:if test="${otherreturnType eq 'PurchaseRegister'}">
		$('#multiselect${varRetType.replaceAll(" ", "_")}1').multiselect({
			nonSelectedText: statusText,
			includeSelectAllOption: true,
			onChange: function(element, checked) {
				applyFilters('${varRetType}');
			},
			onSelectAll: function() {
				applyFilters('${varRetType}');
			},
			onDeselectAll: function() {
				applyFilters('${varRetType}');
			}
		});
		</c:if>
		$('#multiselect${varRetType.replaceAll(" ", "_")}2').multiselect({
			nonSelectedText: '- Invoice Type -',
			includeSelectAllOption: true,
			onChange: function(element, checked) {
				applyFilters('${varRetType}');
			},
			onSelectAll: function() {
				applyFilters('${varRetType}');
			},
			onDeselectAll: function() {
				applyFilters('${varRetType}');
			}
		});
		if('${varRetType}' != 'EWAYBILL'){
			$('#multiselect${varRetType.replaceAll(" ", "_")}5').multiselect({
				nonSelectedText: '- Branches -',
				includeSelectAllOption: true,
				onChange: function(element, checked) {
					applyFilters('${varRetType}');
				},
				onSelectAll: function() {
					applyFilters('${varRetType}');
				},
				onDeselectAll: function() {
					applyFilters('${varRetType}');
				}
			});
			$('#multiselect${varRetType.replaceAll(" ", "_")}6').multiselect({
				nonSelectedText: '- Verticals -',
				includeSelectAllOption: true,
				onChange: function(element, checked) {
					applyFilters('${varRetType}');
				},
				onSelectAll: function() {
					applyFilters('${varRetType}');
				},
				onDeselectAll: function() {
					applyFilters('${varRetType}');
				}
			});
		}
		  if('${varRetType}' != 'DELIVERYCHALLANS' && '${varRetType}' != 'PROFORMAINVOICES' && '${varRetType}' != 'ESTIMATES' && '${varRetType}' != 'PurchaseOrder' && '${varRetType}' != 'EWAYBILL'){
			$('#multiselect${varRetType.replaceAll(" ", "_")}7').multiselect({
				nonSelectedText: '- Reverse Charge -',
				includeSelectAllOption: true,
				onChange: function(element, checked) {
					applyFilters('${varRetType}');
				},
				onSelectAll: function() {
					applyFilters('${varRetType}');
				},
				onDeselectAll: function() {
					applyFilters('${varRetType}');
				}
			});
		  } 
			$('#multiselect${varRetType.replaceAll(" ", "_")}8').multiselect({
				nonSelectedText: '- Supply Type -',
				includeSelectAllOption: true,
				onChange: function(element, checked) {
					applyFilters('${varRetType}');
				},
				onSelectAll: function() {
					applyFilters('${varRetType}');
				},
				onDeselectAll: function() {
					applyFilters('${varRetType}');
				}
			});		
		var pUrl = "";
		<c:choose>
			<c:when test="${varRetType eq 'PROFORMAINVOICES' || varRetType eq varDeliverchallan || varRetType eq varEstimates || varRetType eq varPurchaseOrder}">
			pUrl = "${contextPath}/getAddtionalInvs/${id}/${client.id}/${varRetType}/${month}/${year}";
			</c:when>
			<c:when test="${otherreturn_type eq 'additionalInv'}">
				pUrl = "${contextPath}/getAddtionalInvs/${id}/${client.id}/${varRetType}/${month}/${year}";
			</c:when>
			<c:when test="${varRetType eq 'EWAYBILL'}">
				pUrl = "${contextPath}/getAddtionalInvs/${id}/${client.id}/${varRetType}/${month}/${year}";
			</c:when>
			
			<c:otherwise>
			pUrl = "${contextPath}/getinvs/${id}/${client.id}/${varRetType}/${month}/${year}";
			</c:otherwise>
		</c:choose>
			
		<c:if test="${varRetType eq 'Unclaimed'}">
			pUrl = "${contextPath}/getunclaimed?clientId=${client.id}&month=${month}&year=${year}";
		</c:if>
		<c:if test="${varRetType eq 'GSTR1Amnd' || varRetType eq 'GSTR4Amnd' || varRetType eq 'GSTR6Amnd'}">
			pUrl = "${contextPath}/getamnddata/${id}/${usertype}/${client.id}/${varRetType}/${month}/${year}";
		</c:if>
		$.ajax({
			url: pUrl,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(data) {
				totalTaxableValue = 0;
				totalIGST = 0;
				totalCGST = 0;
				totalSGST = 0;
				totalCESS = 0;
				totalTax = 0;
				totalValue = 0;
				totalITC = 0;
				totalInvoices = 0;
				totalUploaded = 0;
				totalPending = 0;
				totalFailed = 0;
				totalExemptedValue = 0;
				var tranaction  = 0;
				var today = new Date();
				var tddate = today.getFullYear() + '-' + ('0' + (today.getMonth() + 1)).slice(-2) + '-' + ('0' + today.getDate()).slice(-2);
				var gstr3bmonth = mnths[today.getMonth()];
				var content='';
				var invoiceList = null;
				if(data && data.content) {
					invoiceList = data.content;
				} else {
					invoiceList = data;
				}
				var gArray = [];
				if(invoiceList instanceof Array) {
					if(invoiceList.length > 0) {
						var counts =0;
						var custnames = [];
						invoiceList.forEach(function(invoice){
							invoice = updateInvoiceDetails(invoice);
							if(invoice.invoiceCustomerId){
								if(invoice.billedtoname) {
									if(counts == 0){
										custnames.push(invoice.billedtoname+"("+invoice.invoiceCustomerId+")");
										$("#multiselect${varRetType.replaceAll(' ', '_')}4").append($("<option></option>").attr("value",invoice.billedtoname+"("+invoice.invoiceCustomerId+")").text(invoice.billedtoname+" ("+invoice.invoiceCustomerId+")"));
									}
									if(jQuery.inArray( invoice.billedtoname+"("+invoice.invoiceCustomerId+")", custnames ) == -1){
										custnames.push(invoice.billedtoname+"("+invoice.invoiceCustomerId+")");
										$("#multiselect${varRetType.replaceAll(' ', '_')}4").append($("<option></option>").attr("value",invoice.billedtoname+"("+invoice.invoiceCustomerId+")").text(invoice.billedtoname+" ("+invoice.invoiceCustomerId+")"));
									}
								}
							}else{
								if(invoice.billedtoname) {
									if(counts == 0){
										custnames.push(invoice.billedtoname);
										$("#multiselect${varRetType.replaceAll(' ', '_')}4").append($("<option></option>").attr("value",invoice.billedtoname).text(invoice.billedtoname));
									}
									if(jQuery.inArray( invoice.billedtoname, custnames ) == -1){
										custnames.push(invoice.billedtoname);
										$("#multiselect${varRetType.replaceAll(' ', '_')}4").append($("<option></option>").attr("value",invoice.billedtoname).text(invoice.billedtoname));
									}
								}
							}
							var gRow = [];
							gRow.push('<div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" id="invFilter${varRetType}'+invoice.id+'" onClick="updateSelection(\''+invoice.id+'\', \'${varRetType}\', \''+invoice.gstStatus+'\',this)"/><i class="helper"></i></label></div>');
							var colType='';
							totalInvoices++;
							if(invoice.gstStatus != 'CANCELLED'){
								tranaction++;
							}
							var othrrtype = '${otherreturnType}';var rtype = '${varRetType}';
							if(othrrtype != 'SalesRegister' &&  othrrtype != 'PurchaseRegister' && rtype != 'EWAYBILL'){
									if(invoice.gstStatus == null || invoice.gstStatus == '') {
										totalPending++;
										colType += '<span class="color-yellow" id="statuss'+invoice.id+'" data-toggle="tooltip" title="GST Filing Status : Pending">Pending</span>';
									} else if(invoice.gstStatus == 'SUCCESS') {
										totalUploaded++;
										colType += '<span class="color-green" id="statuss'+invoice.id+'" data-toggle="tooltip" title="GST Filing Status : Uploaded">Uploaded</span>';
									} else if(invoice.gstStatus == 'Filed') {
										totalUploaded++;
										colType += '<span class="color-green" id="statuss'+invoice.id+'" data-toggle="tooltip" title="GST Filing Status : Filed">Filed</span>';
									} else if(invoice.gstStatus == 'CANCELLED') {
										colType += '<span class="color-red" id="statuss'+invoice.id+'" data-toggle="tooltip" title="GST Filing Status : Cancelled">Cancelled</span>';
									}else if(invoice.gstStatus == 'Submitted') {
										colType += '<span class="color-green" id="statuss'+invoice.id+'" data-toggle="tooltip" title="GST Filing Status : Submitted">Submitted</span>';
									} else if(invoice.gstStatus == 'In Progress') {
										colType += '<span class="color-blue" style="color:blue;" id="statuss'+invoice.id+'" data-toggle="tooltip" title="GST Filing Status : In Progress">In Progress</span>';
									} else {
										totalFailed++;
										colType += '<span class="color-red" id="statuss'+invoice.id+'" data-toggle="tooltip" title="GST Filing Status : Failed">Failed </span><i class="fa fa-info-circle" style="font-size:17px;color:red" rel="tooltip" title="'+invoice.gstStatus+'"></i>';
									}
							}else if(rtype == 'EWAYBILL'){
								colType += '<span class="" id="">'+invoice.ewayBillNumber+'</span>';
							}else{
							  if(invoice.paymentStatus == 'Paid'){
								  colType += '<span class="color-green" id="statuss'+invoice.id+'" data-toggle="tooltip" title="Payment Status : Paid">Paid</span>';
							  }else if(invoice.paymentStatus == 'Not Paid'){
								  colType += '<span class="color-red" id="statuss'+invoice.id+'" data-toggle="tooltip" title="Payment Status : Not Paid">Not Paid</span>';
							  }else if(invoice.paymentStatus == 'Partially Paid'){
								  colType += '<span class="color-yellow" id="statuss'+invoice.id+'" data-toggle="tooltip" title="Payment Status : Partially Paid">Partially Paid</span>';
							  } else if(invoice.paymentStatus == '' || invoice.paymentStatus == null){
								  colType += '<span class="color-red" id="statuss'+invoice.id+'"  data-toggle="tooltip" title="Payment Status : Not Paid">Not Paid</span>';
							  }
							}
							gRow.push(colType);							
								gRow.push('<span class="text-left invoiceclk" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')">'+invoice.eBillDate+'</span>');
							if(invoice.revchargetype == 'Reverse'){
								gRow.push('<span class="text-left invoiceclk <c:if test="${varRetType eq varGSTR1 || varRetType eq varGSTR4 || varRetType eq varGSTR5}">permissionInvoices-Sales-Edits</c:if> <c:if test="${varRetType eq varPurchase || varRetType eq varGSTR6}">permissionInvoices-Purchase-Edits</c:if> " onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')">'+invoice.invtype+'</span><span><img data-toggle="tooltip" title="Reverse Charge Applied on this Invoice" src="${contextPath}/static/mastergst/images/dashboard-ca/reversecharge.png" alt="reversecharge" style="height: 18px;margin-left: 10px;margin-bottom:3px"></span>');
							}else{
								gRow.push('<span class="text-left invoiceclk <c:if test="${varRetType eq varGSTR1 || varRetType eq varGSTR4 || varRetType eq varGSTR5}">permissionInvoices-Sales-Edits</c:if> <c:if test="${varRetType eq varPurchase || varRetType eq varGSTR6}">permissionInvoices-Purchase-Edits</c:if> " onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')">'+invoice.invtype+'</span>');
							}
							gRow.push('<span class="text-left invoiceclk <c:if test="${varRetType eq varGSTR1 || varRetType eq varGSTR4 || varRetType eq varGSTR5}">permissionInvoices-Sales-Edits</c:if> <c:if test="${varRetType eq varPurchase || varRetType eq varGSTR6}">permissionInvoices-Purchase-Edits</c:if>" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')">'+invoice.invoiceno+'</span>');
							gRow.push('<span class="text-left invoiceclk <c:if test="${varRetType eq varGSTR1 || varRetType eq varGSTR4 || varRetType eq varGSTR5}">permissionInvoices-Sales-Edits</c:if> <c:if test="${varRetType eq varPurchase || varRetType eq varGSTR6}">permissionInvoices-Purchase-Edits</c:if>" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')">'+invoice.billedtoname+'</span>');
							<c:choose>
								<c:when test="${varRetType eq varGSTR2A}">
									if(invoice.cfs == ''){
										gRow.push('<span class="text-left invoiceclk <c:if test="${varRetType eq varGSTR1 || varRetType eq varGSTR4 || varRetType eq varGSTR5}">permissionInvoices-Sales-Edits</c:if> <c:if test="${varRetType eq varPurchase || varRetType eq varGSTR6}">permissionInvoices-Purchase-Edits</c:if>" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')">'+invoice.billedtogstin+'</span>');	
									}else if(invoice.cfs == 'Y'){
										gRow.push('<span class="text-left invoiceclk <c:if test="${varRetType eq varGSTR1 || varRetType eq varGSTR4 || varRetType eq varGSTR5}">permissionInvoices-Sales-Edits</c:if> <c:if test="${varRetType eq varPurchase || varRetType eq varGSTR6}">permissionInvoices-Purchase-Edits</c:if>" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="color:#119400!important" data-toggle="tooltip" title="Supplier Invoice Status is Filed">'+invoice.billedtogstin+'</span>');
									}else{
										gRow.push('<span class="text-left invoiceclk <c:if test="${varRetType eq varGSTR1 || varRetType eq varGSTR4 || varRetType eq varGSTR5}">permissionInvoices-Sales-Edits</c:if> <c:if test="${varRetType eq varPurchase || varRetType eq varGSTR6}">permissionInvoices-Purchase-Edits</c:if>" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="color:rgb(235, 188, 0)" data-toggle="tooltip" title="Supplier Invoice Status is Pending">'+invoice.billedtogstin+'</span>');
									}
								</c:when>
								<c:otherwise>
								gRow.push('<span class="text-left invoiceclk <c:if test="${varRetType eq varGSTR1 || varRetType eq varGSTR4 || varRetType eq varGSTR5}">permissionInvoices-Sales-Edits</c:if> <c:if test="${varRetType eq varPurchase || varRetType eq varGSTR6}">permissionInvoices-Purchase-Edits</c:if>" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')">'+invoice.billedtogstin+'</span>');
								</c:otherwise>
							</c:choose>
							gRow.push('<span class="text-left invoiceclk <c:if test="${varRetType eq varGSTR1 || varRetType eq varGSTR4 || varRetType eq varGSTR5}">permissionInvoices-Sales-Edits</c:if> <c:if test="${varRetType eq varPurchase || varRetType eq varGSTR6}">permissionInvoices-Purchase-Edits</c:if>" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')">'+invoice.dateofinvoice+'</span>');
							gRow.push('<span class="ind_formats text-right invoiceclk <c:if test="${varRetType eq varGSTR1 || varRetType eq varGSTR4 || varRetType eq varGSTR5}">permissionInvoices-Sales-Edits</c:if> <c:if test="${varRetType eq varPurchase || varRetType eq varGSTR6}">permissionInvoices-Purchase-Edits</c:if>" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')">'+formatNumber(invoice.totaltaxableamount.toFixed(2))+'</span>');
							gRow.push('<span id="tot_tax" class="ind_formats text-right invoiceclk <c:if test="${varRetType eq varGSTR1 || varRetType eq varGSTR4 || varRetType eq varGSTR5}">permissionInvoices-Sales-Edits</c:if> <c:if test="${varRetType eq varPurchase || varRetType eq varGSTR6}">permissionInvoices-Purchase-Edits</c:if>" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')">'+formatNumber(invoice.totaltax.toFixed(2))+'<div id="tax_tot_drop1" style="display:none"><div id="drop-tottax"></div><h6 style="text-align: center;" class="mb-2 tax_text">TAX AMOUNT</h6><div class="row pl-3" style="height:25px"><p class="mr-3">IGST <span style="margin-left:5px">:<span></p><span><label class="dropdown taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(invoice.igstamount.toFixed(2))+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">CGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(invoice.cgstamount.toFixed(2))+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">SGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(invoice.sgstamount.toFixed(2))+'</label></span></div></div></span>');
							gRow.push('<span class="ind_formats text-right invoiceclk <c:if test="${varRetType eq varGSTR1 || varRetType eq varGSTR4 || varRetType eq varGSTR5}">permissionInvoices-Sales-Edits</c:if> <c:if test="${varRetType eq varPurchase || varRetType eq varGSTR6}">permissionInvoices-Purchase-Edits</c:if>" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')">'+formatNumber(invoice.totalamount.toFixed(2))+'</span>');
							gRow.push('<span class="text-left invoiceclk <c:if test="${varRetType eq varGSTR1 || varRetType eq varGSTR4 || varRetType eq varGSTR5}">permissionInvoices-Sales-Edits</c:if> <c:if test="${varRetType eq varPurchase || varRetType eq varGSTR6}">permissionInvoices-Purchase-Edits</c:if>" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')">'+invoice.fullname+'</span>');
							gRow.push('<span class="text-left invoiceclk <c:if test="${varRetType eq varGSTR1 || varRetType eq varGSTR4 || varRetType eq varGSTR5}">permissionInvoices-Sales-Edits</c:if> <c:if test="${varRetType eq varPurchase || varRetType eq varGSTR6}">permissionInvoices-Purchase-Edits</c:if>" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')">'+invoice.branch+'</span>');
							gRow.push('<span class="text-left invoiceclk <c:if test="${varRetType eq varGSTR1 || varRetType eq varGSTR4 || varRetType eq varGSTR5}">permissionInvoices-Sales-Edits</c:if> <c:if test="${varRetType eq varPurchase || varRetType eq varGSTR6}">permissionInvoices-Purchase-Edits</c:if>" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')">'+invoice.vertical+'</span>');
							gRow.push('<span class="text-left invoiceclk <c:if test="${varRetType eq varGSTR1 || varRetType eq varGSTR4 || varRetType eq varGSTR5}">permissionInvoices-Sales-Edits</c:if> <c:if test="${varRetType eq varPurchase || varRetType eq varGSTR6}">permissionInvoices-Purchase-Edits</c:if>" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')">'+invoice.invoiceCustomerIdAndBilledToName+'</span>');
							gRow.push('<span class="text-left invoiceclk" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')">'+invoice.supplyType+'</span>');
							<c:if test="${varRetType eq varPurchase || varRetType eq varGSTR2 || varRetType eq varGSTR6 || varRetType eq 'Unclaimed'}">
							if(invoice.invtype == 'Advances' || invoice.invtype == 'Advance Adjusted Detail' || invoice.invtype == 'ITC Reversal' || invoice.invtype == 'Nil Supplies' || invoice.invtype == 'Adv. Adjustments' || invoice.invtype == 'ISD'){
								gRow.push('<span class="text-right ind_formats">'+formatNumber(0)+'</span><div class="dropdown" style="display:inline-block;margin-left:5px"><i class="dropdown-toggle itc-avail-drop" type="button" data-toggle="dropdown" style="border: 1px solid #5769bb;padding:  0px;border-radius: 2px;"><span class="caret"></span></i><div class="dropdown-menu" style="padding: 25px; margin-left:-300px;width:270px;font-size: 15px;"><span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/close-icon.png" alt="Close" style="height: 24px;position: absolute; top: 5px; right: 1%; color: #5769bb;"></span><div class="row"><div class="col-md-12" style="color:red;">This Invoice is not Eligible for ITC</div></div></div></div>');
							}else{
								gRow.push('<span class="text-right ind_formats" id="itcVal${varRetType.replaceAll(" ", "_")}'+invoice.id+'">'+formatNumber(invoice.totalitc.toFixed(2))+'</span><div class="dropdown" style="display:inline-block;margin-left:5px"><i class="dropdown-toggle itc-avail-drop" type="button" data-toggle="dropdown" style="border: 1px solid #5769bb;padding:  0px;border-radius: 2px;"><span class="caret"></span></i><div class="dropdown-menu" style="padding: 25px; margin-left:-300px;width:270px;font-size: 15px;height:150px;"><span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/close-icon.png" alt="Close" style="height: 24px;position: absolute; top: 5px; right: 1%; color: #5769bb;"></span><div class="row"><label class="col-md-4" style="padding-top: 0px;padding-bottom: 13px;padding-left: 15px;padding-right: 1px;">Input Type<span aria-hidden="true"></span></label><div class="col-md-6"><form><select id="'+invoice.id+'${varRetType.replaceAll(" ", "_")}itc_droptype" onchange="updateITCEligibity(this.value,\'${varRetType}\',\''+invoice.id+'\')" class="form-control itc_drop"><option value="">- Input Type -</option><option value="cp">Capital Good</option><option value="ip">Inputs</option><option value="is">Input Services</option><option value="no">Ineligible</option><option value="pending">Not Selected</option> </select></form></div></div><span style="color:red;font-size:12px;">Your Input will reflect in GSTR 3B for the month of <span class="'+invoice.id+'${varRetType.replaceAll(" ", "_")}itc_claimeddate">'+gstr3bmonth+'</span></span><div class="row"><label class="col-md-4">ITC Claimed in</label><div class="col-md-5 p-0"> <div id="datepicker"><input type="date" min="2017-08-15" value="'+tddate+'" class="form-control input-datepicker claimdate_gstr3b" name="date" id="'+invoice.id+'${varRetType.replaceAll(" ", "_")}itc_claimeddate" onchange="gstr3bmonthChange(\''+invoice.id+'${varRetType.replaceAll(" ", "_")}itc_claimeddate\')" required/></div></div></div><div class="row" style="margin-top: 10px;"><label class="col-md-4">ITC Amount</label><input id="'+invoice.id+'${varRetType.replaceAll(" ", "_")}itc_dropamt" type="text"  maxlength="3" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)"  onkeyup="itcclaim()" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeyup="itcclaim()" class="form-control itc_amount col-md-2" value="100" style="width:  45px;"><span class="col-md-1" style="padding: 0px;margin-top:6px">%</span><button class="btn btn-blue col-md-1" id="claimbtn" style="height:24px" onclick="updateITCType(\'${varRetType}\',\''+invoice.id+'\')" value="ok"> ok </button></div><a class="" href="#"  style="" onclick="updateunclaimdetails(\'${varRetType}\',\''+invoice.id+'\')">Clear ITC Claimed Values</a></div></div>');
								//gRow.push('<span class="text-right ind_formats" id="itcVal${varRetType.replaceAll(" ", "_")}'+invoice.id+'">'+formatNumber(invoice.totalitc.toFixed(2))+'</span><div class="dropdown" style="display:inline-block;margin-left:5px"><i class="dropdown-toggle itc-avail-drop" type="button" data-toggle="dropdown" style="border: 1px solid #5769bb;padding:  0px;border-radius: 2px;"><span class="caret"></span></i><div class="dropdown-menu" style="padding: 25px; margin-left:-300px;width:270px;font-size: 15px;height:114px;"><span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/close-icon.png" alt="Close" style="height: 24px;position: absolute; top: 5px; right: 1%; color: #5769bb;"></span><div class="row"><label class="col-md-4" style="padding-top: 0px;padding-bottom: 13px;padding-left: 15px;padding-right: 1px;">Input Type<span aria-hidden="true"></span></label><div class="col-md-6"><form><select id="'+invoice.id+'${varRetType.replaceAll(" ", "_")}itc_droptype" onchange="updateITCEligibity(this.value,\'${varRetType}\',\''+invoice.id+'\')" class="form-control itc_drop"><option value="">- Input Type -</option><option value="cp">Capital Good</option><option value="ip">Inputs</option><option value="is">Input Services</option><option value="no">Ineligible</option><option value="pending">Not Selected</option> </select></form></div></div><div class="row"><label class="col-md-4">ITC Claimed in</label><div class="col-md-5 p-0"> <div id="datepicker"><input type="date" min="2017-08-15" value="'+tddate+'" class="form-control input-datepicker" name="date" id="'+invoice.id+'${varRetType.replaceAll(" ", "_")}itc_claimeddate" required/></div></div></div><div class="row" style="margin-top: 10px;"><label class="col-md-4">ITC Amount</label><input id="'+invoice.id+'${varRetType.replaceAll(" ", "_")}itc_dropamt" type="text" class="form-control itc_amount col-md-2" value="100" style=" width:  45px;"><span class="col-md-1" style="padding: 0px;margin-top:6px">%</span><button class="btn btn-blue col-md-1" style="height:24px" onclick="updateITCType(\'${varRetType}\',\''+invoice.id+'\')" value="ok"> ok </button></div></div></div>');
								$('#btnOK').attr('onclick','claimallinvoices(\'${varRetType}\',\''+invoice.id+'\')');
							}
							</c:if>
							<c:if test="${varRetType eq varPurchase || varRetType eq varGSTR6}">
							<c:if test="${otherreturnType ne 'PurchaseRegister' && varRetType ne varPurchase}">
								gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank" style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Purchase-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style=" "><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a>');
							</c:if>
							<c:if test="${otherreturnType eq 'PurchaseRegister' || varRetType eq varPurchase}">
							if(invoice.invtype == 'B2B' || invoice.invtype == 'Credit Note' || invoice.invtype == 'Debit Note'  || invoice.invtype == 'Credit/Debit Notes' || invoice.invtype == 'Credit/Debit Note for Unregistered Taxpayers'){
								if(invoice.matchingStatus == 'Matched' || invoice.matchingStatus == 'Matched In Other Months' || invoice.matchingStatus == 'Round Off Matched' || invoice.matchingStatus == 'Probable Matched'){
									if(invoice.cfs == ''){
										gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank" style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Purchase-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="margin-right: 2px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a><span data-toggle="tooltip" title="'+invoice.matchingStatus+' In GSTR2A" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:#53bc45;"></i></span>');	
									}else if(invoice.cfs == 'Y'){
										gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank" style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Purchase-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="margin-right: 2px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a><span data-toggle="tooltip" title="'+invoice.matchingStatus+' In GSTR2A <br>and Supplier is Filed" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:#53bc45;"></i></span>');
									}else{
										gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank" style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Purchase-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="margin-right: 2px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a><span data-toggle="tooltip" title="'+invoice.matchingStatus+' In GSTR2A <br>and Supplier is Not Filed" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="display: block;background: linear-gradient( 90deg, #53bc45 49.7%, rgb(235, 188, 0) 50% ); -webkit-background-clip: text;-webkit-text-fill-color: transparent;margin-right: -4px;"></i></span>');
									}
								}else if(invoice.matchingStatus == '' || invoice.matchingStatus == null){
									gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank" style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Purchase-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="margin-right: 2px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a><span data-toggle="tooltip" title="Not In GSTR2A" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:red;"></i></span>');
								}else if(invoice.matchingStatus == 'Mismatched' || invoice.matchingStatus == 'Not In GSTR 2A' || invoice.matchingStatus == 'Not In Purchases'){
									gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank" style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Purchase-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="margin-right: 2px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a><span data-toggle="tooltip" title="'+invoice.matchingStatus+'" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:red;"></i></span>');
								}else{
									gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank" style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Purchase-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="margin-right: 2px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a><span data-toggle="tooltip" title="'+invoice.matchingStatus+'" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:yellow;"></i></span>');
								}
							}else{
								gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank" style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Purchase-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="margin-right: 2px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a><span data-toggle="tooltip" title="Not Eligible" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:grey;"></i></span>');
							}
							</c:if>
							</c:if>
						
							<c:if test="${varRetType eq varGSTR1 || varRetType eq varANX1 || varRetType eq varGSTR4 || varRetType eq varGSTR5 || varRetType eq varProforma || varRetType eq varDeliverchallan || varRetType eq varEstimates || varRetType eq varPurchaseOrder}">
							
							<c:if test="${otherreturnType ne 'SalesRegister'}">
								gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank"  style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Sales-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a>');
							</c:if>
							<c:if test="${varRetType ne varProforma && varRetType ne varDeliverchallan && varRetType ne varEstimates && varRetType ne varPurchaseOrder}">
							<c:if test="${otherreturnType eq 'SalesRegister'}">
							
								if(invoice.gstStatus == null || invoice.gstStatus == ''){
									gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank"  style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Sales-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="margin-right: 2px;"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="GST Filing Status : Pending" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:#FF9900;"></i></span>');
								}else if(invoice.gstStatus == 'SUCCESS' || invoice.gstStatus == 'Filed'){
									gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank"  style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Sales-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="margin-right: 2px;"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="GST Filing Status : Filed" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:green;"></i></span>');
								}else if(invoice.gstStatus == 'CANCELLED'){
									gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank"  style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Sales-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="margin-right: 2px;"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="GST Filing Status : Cancelled" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:red;"></i></span>');
								}else{
									gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank"  style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Sales-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="margin-right: 2px;"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="GST Filing Status : Failed" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:red;"></i></span>');
								}
							</c:if>
							</c:if>
							</c:if>
						
						if('${varRetType}' != 'DELIVERYCHALLANS' && '${varRetType}' != 'PROFORMAINVOICES' && '${varRetType}' != 'ESTIMATES' && '${varRetType}' != 'PurchaseOrder'){
							<c:if test="${otherreturnType eq 'SalesRegister' || otherreturnType eq 'PurchaseRegister' || varRetType eq varPurchase}">
							//gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank"  style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Sales-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a>');
						<c:if test="${otherreturnType eq 'SalesRegister'}">
						
							if(invoice.gstStatus == null || invoice.gstStatus == ''){
								gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank"  style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Sales-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="margin-right: 2px;"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="GST Filing Status : Pending" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:#FF9900;"></i></span>');
							}else if(invoice.gstStatus == 'SUCCESS' || invoice.gstStatus == 'Filed'){
								gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank"  style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Sales-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="margin-right: 2px;"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="GST Filing Status : Filed" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:green;"></i></span>');
							}else if(invoice.gstStatus == 'CANCELLED'){
								gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank"  style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Sales-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')"style="margin-right: 2px;"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="GST Filing Status : Cancelled" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:red;"></i></span>');
							}
							
							</c:if>
							<c:if test="${otherreturnType eq 'PurchaseRegister' || varRetType eq varPurchase}">
							
							if(invoice.invtype == 'B2B' || invoice.invtype == 'Credit Note' || invoice.invtype == 'Debit Note'  || invoice.invtype == 'Credit/Debit Notes' || invoice.invtype == 'Credit/Debit Note for Unregistered Taxpayers'){
								if(invoice.matchingStatus == 'Matched' || invoice.matchingStatus == 'Matched In Other Months' || invoice.matchingStatus == 'Round Off Matched' || invoice.matchingStatus == 'Probable Matched'){
									if(invoice.cfs == ''){
										gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank" style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Purchase-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="margin-right: 2px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a><span data-toggle="tooltip" title="'+invoice.matchingStatus+' In GSTR2A" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:#53bc45;"></i></span>');	
									}else if(invoice.cfs == 'Y'){
										gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank" style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Purchase-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="margin-right: 2px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a><span data-toggle="tooltip" title="'+invoice.matchingStatus+' In GSTR2A <br>and Supplier is Filed" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:#53bc45;"></i></span>');
									}else{
										gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank" style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Purchase-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="margin-right: 2px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a><span data-toggle="tooltip" title="'+invoice.matchingStatus+' In GSTR2A <br>and Supplier is Not Filed" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="display: block;background: linear-gradient( 90deg, #53bc45 49.7%, rgb(235, 188, 0) 50% ); -webkit-background-clip: text;-webkit-text-fill-color: transparent;margin-right: -4px;"></i></span>');
									}
								}else if(invoice.matchingStatus == '' || invoice.matchingStatus == null){
									gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank" style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Purchase-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="margin-right: 2px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a><span data-toggle="tooltip" title="Not In GSTR2A" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:red;"></i></span>');
								}else if(invoice.matchingStatus == 'Mismatched' || invoice.matchingStatus == 'Not In GSTR 2A' || invoice.matchingStatus == 'Not In Purchases'){
									gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank" style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Purchase-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="margin-right: 2px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a><span data-toggle="tooltip" title="'+invoice.matchingStatus+'" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:red;"></i></span>');
								}else{
									gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank" style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Purchase-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="margin-right: 2px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a><span data-toggle="tooltip" title="'+invoice.matchingStatus+'" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:yellow;"></i></span>');
								}
							}else{
								gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank" style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Purchase-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="margin-right: 2px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a><span data-toggle="tooltip" title="Not Eligible" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:grey;"></i></span>');
							}
							</c:if>
							</c:if>
							}else{
								gRow.push('<a href="${contextPath}/invprint/${urlSuffix}/'+invoice.id+'" target="_blank" style="float: right;"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a href="#" class="permissionInvoices-Purchase-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style=" "><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a>');
							}
							
							<c:if test="${varRetType eq 'Unclaimed' || varRetType eq 'GSTR1Amnd' || varRetType eq 'GSTR4Amnd' || varRetType eq 'GSTR6Amnd'}">
							if('${varRetType}' == 'Unclaimed'){
								if(invoice.invtype == 'B2B' || invoice.invtype == 'Credit Note' || invoice.invtype == 'Debit Note'  || invoice.invtype == 'Credit/Debit Notes' || invoice.invtype == 'Credit/Debit Note for Unregistered Taxpayers'){	
								if(invoice.matchingStatus == 'Matched' || invoice.matchingStatus == 'Matched In Other Months' || invoice.matchingStatus == 'Round Off Matched' || invoice.matchingStatus == 'Probable Matched'){
									if(invoice.cfs == ''){
										gRow.push('<a href="#" style="float:right;" class="permissionInvoices-Edit_As_Amendment-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="'+invoice.matchingStatus+' In GSTR2A" style="float: right;margin-right: 3px;"><i class="fa fa-circle" style="color:#53bc45;"></i></span>');	
									}else if(invoice.cfs == 'Y'){
										gRow.push('<a href="#" style="float: right;" class="permissionInvoices-Edit_As_Amendment-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="'+invoice.matchingStatus+' In GSTR2A <br>and Supplier is Filed" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:#53bc45;"></i></span>');
									}else{
										gRow.push('<a href="#" style="float: right;" class="permissionInvoices-Edit_As_Amendment-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="'+invoice.matchingStatus+' In GSTR2A <br>and Supplier is Not Filed" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="display: block;background: linear-gradient( 90deg, #53bc45 49.7%, rgb(235, 188, 0) 50% ); -webkit-background-clip: text;-webkit-text-fill-color: transparent;margin-right: -4px;"></i></span>');
									}
								}else if(invoice.matchingStatus == '' || invoice.matchingStatus == null){
									gRow.push('<a href="#" style="float:right;" class="permissionInvoices-Edit_As_Amendment-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="Not In GSTR2A" style="float: right;margin-right: 3px;"><i class="fa fa-circle" style="color:red;"></i></span>');
								}else if(invoice.matchingStatus == 'Mismatched' || invoice.matchingStatus == 'Not In GSTR 2A' || invoice.matchingStatus == 'Not In Purchases'){
									gRow.push('<a href="#" style="float:right;" class="permissionInvoices-Edit_As_Amendment-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="'+invoice.matchingStatus+'" style="float: right;margin-right: 3px;"><i class="fa fa-circle" style="color:red;"></i></span>');
								}else{
									gRow.push('<a href="#" style="float:right;" class="permissionInvoices-Edit_As_Amendment-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="'+invoice.matchingStatus+'" style="float: right;margin-right: 3px;"><i class="fa fa-circle" style="color:yellow;"></i></span>');
								}
								}else{gRow.push('<a href="#" style="float:right;" class="permissionInvoices-Edit_As_Amendment-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a>');}
							}else{
								gRow.push('<a href="#" style="float:right;" class="permissionInvoices-Edit_As_Amendment-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a>');
							}
							</c:if>
							<c:if test="${varRetType eq varGSTR2A}">
							if(invoice.invtype == 'B2B' || invoice.invtype == 'Credit Note' || invoice.invtype == 'Debit Note'  || invoice.invtype == 'Credit/Debit Notes' || invoice.invtype == 'Credit/Debit Note for Unregistered Taxpayers'){	
								if(invoice.matchingStatus == 'Matched' || invoice.matchingStatus == 'Matched In Other Months' || invoice.matchingStatus == 'Round Off Matched' || invoice.matchingStatus == 'Probable Matched'){
									gRow.push('<span data-toggle="tooltip" title="'+invoice.matchingStatus+' In Purchases" style="float: right;margin-right: 3px;"><i class="fa fa-circle" style="color:#53bc45;"></i></span>');
								}else if(invoice.matchingStatus == '' || invoice.matchingStatus == null){
									gRow.push('<span data-toggle="tooltip" title="Not In Purchases" style="float: right;margin-right: 3px;"><i class="fa fa-circle" style="color:red;"></i></span>');
								}else if(invoice.matchingStatus == 'Mismatched' || invoice.matchingStatus == 'Not In GSTR 2A' || invoice.matchingStatus == 'Not In Purchases'){
									gRow.push('<span data-toggle="tooltip" title="'+invoice.matchingStatus+'" style="float: right;margin-right: 3px;"><i class="fa fa-circle" style="color:red;"></i></span>');
								}else{
									gRow.push('<span data-toggle="tooltip" title="'+invoice.matchingStatus+'" style="float: right;margin-right: 3px;"><i class="fa fa-circle" style="color:yellow;"></i></span>');
								}
							}
							
							</c:if>
							<c:if test="${varRetType eq varEWAYBILL}">
								gRow.push('<a href="#" class="permissionInvoices-Purchase-Edit" onClick="editInvPopup(null,\'${varRetType}\',\''+invoice.id+'\',\''+NotInJournals+'\')" style=" "><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right;"></i> </a>');
							</c:if>
						
							gArray.push(gRow);
							counts++;
						});
						//$('.permissionInvoices-Sales-Edits').removeAttr('onClick');
						$("#multiselect${varRetType.replaceAll(' ', '_')}4").multiselect('rebuild');
						//$('#invBody${varRetType.replaceAll(" ", "_")}').html(content);
					}
					invoiceArray['${varRetType.replaceAll(" ", "_")}'] = invoiceList;
				} else {
					invoiceArray['${varRetType.replaceAll(" ", "_")}'] = [];
					errorNotification(invoiceList);
				}
				//if(invoiceArray['Purchase_Register'] != undefined && invoiceArray['GSTR2'] != undefined) {
					//if(invoiceArray['prFY'] == undefined) {
						invoiceArray['prFY'] = new Array();
						var fyUrl = "${contextPath}/getMatchedAndPresentMonthfyinvs/${id}/${client.id}/${varPurchase}/${month}/${year}";
						$.ajax({
							url: fyUrl,
							async: true,
							cache: false,
							dataType:"json",
							contentType: 'application/json',
							success : function(fyData) {
								
								var fyList = null;
								if(fyData && fyData.content) {
									fyList = fyData.content;
								} else {
									fyList = fyData;
								}
								if(fyList instanceof Array) {
									if(fyList.length > 0) {
										fyList.forEach(function(fyInv){
											JSON.stringify(fyInv);
											var fyInvoice = updateInvoiceDetails(fyInv);
											invoiceArray['prFY'].push(fyInvoice);
										});
									}
								}
								
									invoiceArray['g2FYMatched'] = new Array();
									var fyUrl = "${contextPath}/getgstr2Matchedfyinvs/${id}/${client.id}/${varGSTR2}/${month}/${year}";
									$.ajax({
										url: fyUrl,
										async: false,
										cache: false,
										dataType:"json",
										contentType: 'application/json',
										success : function(fyData) {
											var fyList = null;
											if(fyData && fyData.content) {
												fyList = fyData.content;
											} else {
												fyList = fyData;
											}
											if(fyList instanceof Array) {
												if(fyList.length > 0) {
													fyList.forEach(function(fyInv){
														var fyInvoice = updateInvoiceDetails(fyInv);
														invoiceArray['g2FYMatched'].push(fyInvoice);
													});
												}
											}
										},
										error : function(data) {
											invoiceArray['g2FYMatched'] = invoiceArray['GSTR2'];
											updateMismatchData();
										}
									});
									invoiceArray['g2MannualFYMatched'] = new Array();
									var fyUrl = "${contextPath}/getgstr2MannualMatchedfyinvs/${id}/${client.id}/${varGSTR2}/${month}/${year}";
									$.ajax({
										url: fyUrl,
										async: false,
										cache: false,
										dataType:"json",
										contentType: 'application/json',
										success : function(fyData) {
											var fyList = null;
											if(fyData && fyData.content) {
												fyList = fyData.content;
											} else {
												fyList = fyData;
											}
											if(fyList instanceof Array) {
												if(fyList.length > 0) {
													fyList.forEach(function(fyInv){
														var fyInvoice = updateInvoiceDetails(fyInv);
														invoiceArray['g2MannualFYMatched'].push(fyInvoice);
													});
												}
											}
										},
										error : function(data) {
											
											updateMismatchData();
										}
									});
									invoiceArray['gPMannualFYMatched'] = new Array();
									var fyUrl = "${contextPath}/getMannualMatchedAndPresentMonthfyinvs/${id}/${client.id}/${varGSTR2}/${month}/${year}";
									$.ajax({
										url: fyUrl,
										async: false,
										cache: false,
										dataType:"json",
										contentType: 'application/json',
										success : function(fyData) {
											var fyList = null;
											if(fyData && fyData.content) {
												fyList = fyData.content;
											} else {
												fyList = fyData;
											}
											if(fyList instanceof Array) {
												if(fyList.length > 0) {
													fyList.forEach(function(fyInv){
														var fyInvoice = updateInvoiceDetails(fyInv);
														invoiceArray['gPMannualFYMatched'].push(fyInvoice);
													});
												}
											}
										},
										error : function(data) {
											
											updateMismatchData();
										}
									});
								updateMismatchData();
							},
							error : function(data) {
								invoiceArray['prFY'] = invoiceArray['Purchase_Register'];
								updateMismatchData();
							}
						});
					//}
				//}
				var hiddenCols = new Array();
				<c:if test="${varRetType eq varPurchase || varRetType eq varGSTR2A || varRetType eq 'Unclaimed' || varRetType eq 'GSTR1Amnd' || varRetType eq 'GSTR4Amnd' || varRetType eq 'GSTR6Amnd'}">
				<c:if test="${varRetType eq varGSTR2A}">
				hiddenCols.push(0);
				</c:if>
				<c:if test="${otherreturnType ne 'PurchaseRegister'}">
				hiddenCols.push(1);
				</c:if>
				</c:if>
				<c:if test="${varRetType eq varProforma || varRetType eq varDeliverchallan || varRetType eq varEstimates || varRetType eq varPurchaseOrder}">
				hiddenCols.push(1);
				</c:if>
				<c:if test="${varRetType ne 'EWAYBILL'}">
				hiddenCols.push(2);
				</c:if>
				
				hiddenCols.push(11);
				hiddenCols.push(12);
				hiddenCols.push(13);
				hiddenCols.push(14);
				hiddenCols.push(15);
				var amtCols = [8,9,10];
				<c:if test="${varRetType eq varPurchase || varRetType eq varGSTR2 || varRetType eq varGSTR6 || varRetType eq 'Unclaimed'}">
				amtCols.push(16);
				
				</c:if>
				if(invoiceTable['${varRetType.replaceAll(" ", "_")}']){
					invoiceTable['${varRetType.replaceAll(" ", "_")}'].destroy();
				}
				invoiceTable['${varRetType.replaceAll(" ", "_")}'] = $('#dbTable${varRetType.replaceAll(" ", "_")}').DataTable({
					"dom": '<"toolbar"f>lrtip<"clear">',
					"data": gArray,
					"deferRender": true,
					"paging": true,
					"orderClasses": false,
					"searching": true,
					"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
					"responsive": true,
					"order": [[7,'desc']],
					"columnDefs": [
						{
							"targets": hiddenCols,
							"visible": false,
							"searchable": true
						},
						{
							"targets": amtCols,
							className: 'dt-body-right'
						}
					],
					drawCallback: function(){
				          $('.paginate_button', this.api().table().container())          
				             .on('click', function(){
				            		invoiceTable['${varRetType.replaceAll(" ", "_")}'].rows().every( function () {
				    				    var d = this.data();
				    				    if('${varRetType}' != 'GSTR2' && '${varRetType}' != 'Purchase Register'){
				    				    	if(d[1]){
					    					 	if(d[1].indexOf("Cancelled") != -1){
					    					 		this.nodes().to$().addClass('cancelled_line');
					    					 	}
				    				    	}
				    				    }
				    				    if('${varRetType}' != 'DELIVERYCHALLANS' && '${varRetType}' != 'PROFORMAINVOICES' && '${varRetType}' != 'ESTIMATES' && '${varRetType}' != 'PurchaseOrder'){
				    				    	if('${otherreturnType}' == 'SalesRegister' || '${otherreturnType}' == 'PurchaseRegister'){
				    				    		if(d[15]){
				    						  		if(d[15].indexOf("Cancelled") != -1){
				    							 		this.nodes().to$().addClass('cancelled_line');
				    							 	}
				    				    		}
				    				    	}
				    				    }
				    				});
				             });       
				       }
				});
				invoiceTable['${varRetType.replaceAll(" ", "_")}'].on('draw',rolesPermissions);
				invoiceTable['${varRetType.replaceAll(" ", "_")}'].rows().invalidate().draw();
				invoiceTable['${varRetType.replaceAll(" ", "_")}'].rows().every( function () {
				    var d = this.data();
				    if('${varRetType}' != 'GSTR2' && '${varRetType}' != 'Purchase Register'){
				    	if(d[1]){
						 	if(d[1].indexOf("Cancelled") != -1){
						 		this.nodes().to$().addClass('cancelled_line');
						 	}
				    	}
				    }
				    if('${varRetType}' != 'DELIVERYCHALLANS' && '${varRetType}' != 'PROFORMAINVOICES' && '${varRetType}' != 'ESTIMATES' && '${varRetType}' != 'PurchaseOrder'){
				    	if('${otherreturnType}' == 'SalesRegister' || '${otherreturnType}' == 'PurchaseRegister'){
				    		if(d[16]){
						  		if(d[16].indexOf("Cancelled") != -1){
							 		this.nodes().to$().addClass('cancelled_line');
							 	}
				    		}
				    	}
				    }
				});
				//invoiceTable['${varRetType.replaceAll(" ", "_")}'].rows().invalidate().draw();				
				
				if('${otherreturn_type}' != 'additionalInv'){
					if('${client.prevPendingInv}' == 'previousMonthsPendingInv' && '${varRetType}' == 'GSTR1'){
						$(".dbTable${varRetType.replaceAll(' ', '_')} div.toolbar").append('<c:if test="${varRetType eq varGSTR1}"><span <c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">style="display:none" </c:if>><div class="form-check mb-2 mb-sm-0" style="display: inline-block; float: right;  margin-top: 22px; margin-right: 13px;"><div class="meterialform"><div class="checkbox"><label id="include_old_invoices" style="font-size: 15px; font-weight: bold; color:grey" title="On selection of this check box, All Pending / Not Filed invoices of previous months till now will show in the below list to file in the current month" data-toggle="tooltip"><input type="checkbox" name="includeoldinv" onclick="includeOldInvs(this, \'${varRetType.replaceAll(" ", "_")}\')" id="includeoldinv" class="previousmonthdata1" checked><i class="helper che-box" style="top:2px; left:5px"></i>Show Previous Months Pending Invoices</label></div></div></div><c:if test="${not empty tallyHsnSummary}"><a href="#" class="hsnsummarybtn mt-2" onclick="viewHsnSummary(\'${varRetType.replaceAll(" ", "_")}\')">HSN Summary</a></c:if></span></c:if>');
					}else{
						$(".dbTable${varRetType.replaceAll(' ', '_')} div.toolbar").append('<c:if test="${varRetType eq varGSTR1}"><span <c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">style="display:none" </c:if>><div class="form-check mb-2 mb-sm-0" style="display: inline-block; float: right;  margin-top: 22px; margin-right: 13px;"><div class="meterialform"><div class="checkbox"><label id="include_old_invoices" style="font-size: 15px; font-weight: bold; color:grey" title="On selection of this check box, All Pending / Not Filed invoices of previous months till now will show in the below list to file in the current month" data-toggle="tooltip"><input type="checkbox" name="includeoldinv" onclick="includeOldInvs(this, \'${varRetType.replaceAll(" ", "_")}\')" id="includeoldinv" class="previousmonthdata1"><i class="helper che-box" style="top:2px; left:5px"></i>Show Previous Months Pending Invoices</label></div></div></div><c:if test="${not empty tallyHsnSummary}"><a href="#" class="hsnsummarybtn mt-2"  onclick="viewHsnSummary(\'${varRetType.replaceAll(" ", "_")}\')">HSN Summary</a></c:if></span></c:if>');
					}
				}
				<c:if test="${varRetType eq varGSTR1 || varRetType eq varANX1 || varRetType eq varPurchase || varRetType eq varGSTR6 || varRetType eq varGSTR4 || varRetType eq varGSTR5}">
					updateInvoiceSummary("${varRetType.replaceAll(' ', '_')}","${month}","${year}");
				</c:if>
				<c:if test="${varRetType ne varGSTR2}">
				$('#totalInvoices${varRetType.replaceAll(" ", "_")}').html(totalInvoices);
				$('#totalUploaded${varRetType.replaceAll(" ", "_")}').html(totalUploaded);
				$('#totalPending${varRetType.replaceAll(" ", "_")}').html(totalPending);
				$('#totalFailed${varRetType.replaceAll(" ", "_")}').html(totalFailed);
				</c:if>
				<c:if test="${varRetType eq varPurchase}">
				$('#totalInvoices${varGSTR2.replaceAll(" ", "_")}').html(totalInvoices);
				$('#totalUploaded${varGSTR2.replaceAll(" ", "_")}').html(totalUploaded);
				$('#totalPending${varGSTR2.replaceAll(" ", "_")}').html(totalPending);
				$('#totalFailed${varGSTR2.replaceAll(" ", "_")}').html(totalFailed);
				</c:if>
				$('#idCount${varRetType.replaceAll(" ", "_")}').html(tranaction);
				$('#idTaxableVal${varRetType.replaceAll(" ", "_")}').html('<i class="fa fa-rupee"></i>'+formatNumber(totalTaxableValue.toFixed(2)));
				$('#idExemptedVal${varRetType.replaceAll(" ", "_")}').html('<i class="fa fa-rupee"></i>'+formatNumber(totalExemptedValue.toFixed(2)));
				$('#idIGST${varRetType.replaceAll(" ", "_")}').html('<i class="fa fa-rupee"></i>'+formatNumber(totalIGST.toFixed(2)));
				$('#idCGST${varRetType.replaceAll(" ", "_")}').html('<i class="fa fa-rupee"></i>'+formatNumber(totalCGST.toFixed(2)));
				$('#idSGST${varRetType.replaceAll(" ", "_")}').html('<i class="fa fa-rupee"></i>'+formatNumber(totalSGST.toFixed(2)));
				$('#idCESS${varRetType.replaceAll(" ", "_")}').html('<i class="fa fa-rupee"></i>'+formatNumber(totalCESS.toFixed(2)));
				$('#idTaxVal${varRetType.replaceAll(" ", "_")}').html('<i class="fa fa-rupee"></i>'+formatNumber(totalTax.toFixed(2)));
				$('#idITC${varRetType.replaceAll(" ", "_")}').html('<i class="fa fa-rupee"></i>'+formatNumber(totalITC.toFixed(2)));
				$('#idTotAmtVal${varRetType.replaceAll(" ", "_")}, #idTabTotal${varRetType.replaceAll(" ", "_")}').html('<i class="fa fa-rupee"></i>'+formatNumber(totalValue.toFixed(2)));
				<c:if test="${varRetType eq varPurchase || varRetType eq varGSTR2 || varRetType eq varGSTR6}">
				$('#idTabTotal${varRetType.replaceAll(" ", "_")}').html('<i class="fa fa-rupee"></i>'+formatNumber(totalITC.toFixed(2)));
				</c:if>
				<c:if test="${varRetType eq 'GSTR2A'}">
					$('#idTabTotal${varRetType.replaceAll(" ", "_")}').html('<i class="fa fa-rupee"></i>'+formatNumber(totalTax.toFixed(2)));
				</c:if>
				<c:if test="${varRetType eq 'Unclaimed'}">
					$('#idTabTotal${varRetType.replaceAll(" ", "_")}').html('<i class="fa fa-rupee"></i>'+formatNumber(totalTax.toFixed(2)));
				</c:if>
			},
			error: function(data) {
				invoiceArray['${varRetType.replaceAll(" ", "_")}'] = [];
				if(data.responseText == 'OTP verification is not yet completed!' || data.responseText == 'Invalid Session' || data.responseText == 'Unauthorized User!' || data.responseText == 'Unauthorized User' || data.responseText == 'RefreshToken Session Cannot Be Extended' || data.responseText == 'Missing Mandatory Params' || data.responseText == 'API Authorization Failed'){
					errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.')
				}else if(data.responseText == 'No Invoices found for the provided Inputs' || !data.responseText){
					
				}else{
						errorNotification(data.responseText);
				}
				$('#totalInvoices${varRetType.replaceAll(" ", "_")}').html(totalInvoices);
				$('#totalUploaded${varRetType.replaceAll(" ", "_")}').html(totalUploaded);
				$('#totalPending${varRetType.replaceAll(" ", "_")}').html(totalPending);
				$('#totalFailed${varRetType.replaceAll(" ", "_")}').html(totalFailed);
				$('#idCount${varRetType.replaceAll(" ", "_")}').html(0);
				$('#idTaxableVal${varRetType.replaceAll(" ", "_")}').html('<i class="fa fa-rupee"></i>'+formatNumber(0));
				$('#idExemptedVal${varRetType.replaceAll(" ", "_")}').html('<i class="fa fa-rupee"></i>'+formatNumber(0));
				$('#idIGST${varRetType.replaceAll(" ", "_")}').html('<i class="fa fa-rupee"></i>'+formatNumber(0));
				$('#idCGST${varRetType.replaceAll(" ", "_")}').html('<i class="fa fa-rupee"></i>'+formatNumber(0));
				$('#idSGST${varRetType.replaceAll(" ", "_")}').html('<i class="fa fa-rupee"></i>'+formatNumber(0));
				$('#idCESS${varRetType.replaceAll(" ", "_")}').html('<i class="fa fa-rupee"></i>'+formatNumber(0));
				$('#idTaxVal${varRetType.replaceAll(" ", "_")}').html('<i class="fa fa-rupee"></i>'+formatNumber(0));
				$('#idITC${varRetType.replaceAll(" ", "_")}').html('<i class="fa fa-rupee"></i>'+formatNumber(0));
				$('#idTotAmtVal${varRetType.replaceAll(" ", "_")}, #idTabTotal${varRetType.replaceAll(" ", "_")}').html('<i class="fa fa-rupee"></i>'+formatNumber(0));
				<c:if test="${varRetType eq varPurchase || varRetType eq varGSTR2 || varRetType eq varGSTR6 || varRetType eq varGSTR2A}">
				$('#idTabTotal${varRetType.replaceAll(" ", "_")}').html('<i class="fa fa-rupee"></i>'+formatNumber(0));
				</c:if>
				<c:if test="${varRetType eq 'Unclaimed'}">
					$('#idTabTotal${varRetType.replaceAll(" ", "_")}').html('<i class="fa fa-rupee"></i>'+formatNumber(0));
				</c:if>
				var hiddenCols = new Array();
				<c:if test="${varRetType eq varPurchase || varRetType eq varGSTR2A || varRetType eq 'Unclaimed' || varRetType eq 'GSTR1Amnd' || varRetType eq 'GSTR4Amnd' || varRetType eq 'GSTR6Amnd'}">
				<c:if test="${varRetType eq varGSTR2A}">
				hiddenCols.push(0);
				</c:if>
				
				<c:if test="${otherreturnType ne 'PurchaseRegister'}">
				hiddenCols.push(1);
				</c:if>
				
				</c:if>
				<c:if test="${varRetType ne 'EWAYBILL'}">
				hiddenCols.push(2);
				</c:if>
				hiddenCols.push(11);
				hiddenCols.push(12);
				hiddenCols.push(13);
				hiddenCols.push(14);
				hiddenCols.push(15);
				var amtCols = [8,9,10];
				<c:if test="${varRetType eq varPurchase || varRetType eq varGSTR2 || varRetType eq varGSTR6 || varRetType eq 'Unclaimed'}">
				amtCols.push(16);
				</c:if>
				if(invoiceTable['${varRetType.replaceAll(" ", "_")}']){
					invoiceTable['${varRetType.replaceAll(" ", "_")}'].destroy();
				}
				invoiceTable['${varRetType.replaceAll(" ", "_")}'] = $('#dbTable${varRetType.replaceAll(" ", "_")}').DataTable({
					"dom": '<"toolbar"f>lrtip<"clear">',
					"paging": true,
					"searching": true,
					"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
					"responsive": true,
					"order": [[7,'desc']],
					"columnDefs": [
						{
							"targets": hiddenCols,
							"visible": false,
							"searchable": true
						}
					]
				});
				invoiceTable['${varRetType.replaceAll(" ", "_")}'].on('draw',rolesPermissions);
				invoiceTable['${varRetType.replaceAll(" ", "_")}'].rows().invalidate().draw();
				if('${otherreturn_type}' != 'additionalInv'){
					if('${client.prevPendingInv}' == 'previousMonthsPendingInv' && '${varRetType}' == 'GSTR1'){
						$(".dbTable${varRetType.replaceAll(' ', '_')} div.toolbar").append('<c:if test="${varRetType eq varGSTR1}"><span <c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">style="display:none" </c:if>><div class="form-check mb-2 mb-sm-0" style="display: inline-block; float: right;  margin-top: 22px; margin-right: 13px;"><div class="meterialform"><div class="checkbox"><label id="include_old_invoices" style="font-size: 15px; font-weight: bold; color:grey" title="On selection of this check box, All Pending / Not Filed invoices of previous months till now will show in the below list to file in the current month" data-toggle="tooltip"><input type="checkbox" name="includeoldinv" onclick="includeOldInvs(this, \'${varRetType.replaceAll(" ", "_")}\')" id="includeoldinv" class="previousmonthdata2" checked><i class="helper che-box" style="top:2px; left:5px"></i>Show Previous Months Pending Invoices</label></div></div></div></span></c:if>');
					}else{
						$(".dbTable${varRetType.replaceAll(' ', '_')} div.toolbar").append('<c:if test="${varRetType eq varGSTR1}"><span <c:if test="${client.status eq statusSubmitted || client.status eq statusFiled}">style="display:none" </c:if>><div class="form-check mb-2 mb-sm-0" style="display: inline-block; float: right;  margin-top: 22px; margin-right: 13px;"><div class="meterialform"><div class="checkbox"><label id="include_old_invoices" style="font-size: 15px; font-weight: bold; color:grey" title="On selection of this check box, All Pending / Not Filed invoices of previous months till now will show in the below list to file in the current month" data-toggle="tooltip"><input type="checkbox" name="includeoldinv" onclick="includeOldInvs(this, \'${varRetType.replaceAll(" ", "_")}\')" id="includeoldinv" class="previousmonthdata2"><i class="helper che-box" style="top:2px; left:5px"></i>Show Previous Months Pending Invoices</label></div></div></div></span></c:if>');
					}
				}
				if('${varRetType}' == 'GSTR1' || '${varRetType}' == 'GSTR1Amnd' || '${varRetType}' == 'ESTIMATES' || '${varRetType}' == 'DELIVERYCHALLANS' || '${varRetType}' == 'PROFORMAINVOICES'){
					$('#multiselect${varRetType.replaceAll(" ", "_")}4').multiselect({
						nonSelectedText: '- Customers -',
						includeSelectAllOption: true,
						onChange: function(element, checked) {
							applyFilters('${varRetType}');
						},
						onSelectAll: function() {
							applyFilters('${varRetType}');
						},
						onDeselectAll: function() {
							applyFilters('${varRetType}');
						}
					});
				}else{
					$('#multiselect${varRetType.replaceAll(" ", "_")}4').multiselect({
						nonSelectedText: '- Suppliers -',
						includeSelectAllOption: true,
						onChange: function(element, checked) {
							applyFilters('${varRetType}');
						},
						onSelectAll: function() {
							applyFilters('${varRetType}');
						},
						onDeselectAll: function() {
							applyFilters('${varRetType}');
						}
					});	
				}
			}
		});
		var rettype = '<c:out value="${varRetType}"/>';
	if('${client.prevPendingInv}' == 'previousMonthsPendingInv' && rettype == "GSTR1" && '${client.status}' != 'Filed' && '${client.status}' != 'Submitted'){
			if('${otherreturn_type}' != 'additionalInv'){
				includeOldInvs1(rettype);
			}
		}
	});
	function applyFilters(retType) {
		invoiceTable[retType.replace(" ", "_")].clear();
		selInvArray=new Array();
		selArray[retType.replace(" ", "_")]=new Array();
		var statusOptions = new Array();
		if(retType != 'Purchase Register' && retType != 'GSTR2A' && retType != 'DELIVERYCHALLANS' && retType != 'PROFORMAINVOICES' && retType != 'ESTIMATES' && retType != 'PurchaseOrder'){
			statusOptions = $('#multiselect'+retType.replace(" ", "_")+'1 option:selected');
		}
		<c:if test="${otherreturnType eq 'PurchaseRegister'}">
			statusOptions = $('#multiselect'+retType.replace(" ", "_")+'1 option:selected');
		</c:if>
		var typeOptions = new Array();
		var supplyTypeOptions = new Array();
		typeOptions = $('#multiselect'+retType.replace(" ", "_")+'2 option:selected');
		var userOptions = $('#multiselect'+retType.replace(" ", "_")+'3 option:selected');
		var vendorOptions = $('#multiselect'+retType.replace(" ", "_")+'4 option:selected');
		var branchOptions = $('#multiselect'+retType.replace(" ", "_")+'5 option:selected');
		var verticalOptions = $('#multiselect'+retType.replace(" ", "_")+'6 option:selected');
		var reverseChargeOptions = $('#multiselect'+retType.replace(" ", "_")+'7 option:selected');
		supplyTypeOptions = $('#multiselect'+retType.replace(" ", "_")+'8 option:selected');
		if(statusOptions.length > 0 || typeOptions.length > 0 || userOptions.length > 0 || vendorOptions.length > 0 || branchOptions.length > 0 || verticalOptions.length > 0 || reverseChargeOptions.length > 0 || supplyTypeOptions.length > 0){
			
			$('.normaltable .filter').css("display","block");
		}else{
			$('.normaltable .filter').css("display","none");
		}
		var statusArr=new Array();
		var typeArr=new Array();
		var userArr=new Array();
		var vendorArr=new Array();
		var branchArr=new Array();
		var verticalArr=new Array();
		var reverseChargeArr=new Array();
		var supplyTypeArr=new Array();
		var filterContent='';
		if(statusOptions.length > 0) {
			for(var i=0;i<statusOptions.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput" >'+statusOptions[i].text+'<span data-val="'+statusOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
				statusArr.push(statusOptions[i].value);
			}
		} else {
			statusArr.push('All');
		}
		if(typeOptions.length > 0) {
			for(var i=0;i<typeOptions.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+typeOptions[i].text+'<span data-val="'+typeOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
				typeArr.push(typeOptions[i].value);
			}
		} else {
			typeArr.push('All');
		}
		if(userOptions.length > 0) {
			for(var i=0;i<userOptions.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+userOptions[i].value+'<span data-val="'+userOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
				userArr.push(userOptions[i].value);
			}
		} else {
			userArr.push('All');
		}
		if(vendorOptions.length > 0) {
			for(var i=0;i<vendorOptions.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+vendorOptions[i].value+'<span data-val="'+vendorOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
				vendorArr.push(vendorOptions[i].value);
			}
		} else {
			vendorArr.push('All');
		}
		if(branchOptions.length > 0) {
			for(var i=0;i<branchOptions.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+branchOptions[i].value+'<span data-val="'+branchOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
				branchArr.push(branchOptions[i].value);
			}
		} else {
			branchArr.push('All');
		}
		if(verticalOptions.length > 0) {
			for(var i=0;i<verticalOptions.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+verticalOptions[i].value+'<span data-val="'+verticalOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
				verticalArr.push(verticalOptions[i].value);
			}
		} else {
			verticalArr.push('All');
		}
		if(reverseChargeOptions.length > 0) {
			for(var i=0;i<reverseChargeOptions.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+reverseChargeOptions[i].value+'<span data-val="'+reverseChargeOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
				reverseChargeArr.push(reverseChargeOptions[i].value);
			}
		} else {
			reverseChargeArr.push('All');
		}
		if(supplyTypeOptions.length > 0) {
			for(var i=0;i<supplyTypeOptions.length;i++) {
				filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+supplyTypeOptions[i].text+'<span data-val="'+supplyTypeOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
				supplyTypeArr.push(supplyTypeOptions[i].value);
			}
		} else {
			supplyTypeArr.push('All');
		}
		$('#divFilters'+retType.replace(" ", "_")).html(filterContent);
		commonInvoiceFilter(retType, statusArr, typeArr, userArr,vendorArr,branchArr,verticalArr,reverseChargeArr,supplyTypeArr);
		invoiceTable[retType.replace(" ", "_")].draw();
	}
	function clearFilters(retType) {
		$('.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
		$('#divFilters'+retType.replace(" ", "_")).html('');
		$('.normaltable .filter').css("display","none");
		invoiceTable[retType.replace(" ", "_")].clear();
		selInvArray=new Array();
		selArray[retType.replace(" ", "_")]=new Array();
		commonInvoiceFilter(retType, new Array(), new Array(), new Array(),new Array(),new Array(),new Array(), new Array(),new Array());
		invoiceTable[retType.replace(" ", "_")].draw();
	}
	function commonInvoiceFilter(retType, arrStatus, arrInvType, arrUser, arrVendor, arrBranch, arrVertical,arrReverseCharge,arrSupplyType) {
		if(invoiceArray[retType.replace(" ", "_")].length > 0) {
			var rows = new Array();
			var taxArray = new Array();
			var rowNode;var ebillNo="";
			var today = new Date();
			var tddate = today.getFullYear() + '-' + ('0' + (today.getMonth() + 1)).slice(-2) + '-' + ('0' + today.getDate()).slice(-2);
			var gstr3bmonth = mnths[today.getMonth()];
			invoiceArray[retType.replace(" ", "_")].forEach(function(invoice) {
				ebillNo = invoice.ewayBillNumber;
				var status = "";
				if('${otherreturnType}' == 'SalesRegister' || '${otherreturnType}' == 'PurchaseRegister'){
					if(invoice.paymentStatus == '' || invoice.paymentStatus == null){
						invoice.paymentStatus = 'Not Paid';
					}
					status = invoice.paymentStatus;
				}else if(retType == 'EWAYBILL'){
					status = invoice.ewayBillNumber;
				}else{
					status = invoice.gstStatus;
					if(status == '' || status == null) {
						status = 'Pending';
					}	
				}
				var reversecharge = invoice.revchargetype;
				var creditDebit = 'credit';
				var invtype = invoice.invtype;
				if(retType == 'GSTR1' && invtype == 'Credit Note'){
						if(invoice.cdnr[0].nt[0].ntty == 'C'){
								creditDebit = 'credit';
						}else{
							creditDebit = 'debit';
						}
				}else if((retType == 'GSTR2' || retType == 'GSTR2A' || retType == 'Purchase Register' || retType == 'Unclaimed') && invtype == 'Debit Note'){
						if(invoice.cdn[0].nt[0].ntty == 'D'){
								creditDebit = 'credit';
						}else{
							creditDebit = 'debit';
						}
				}else if(invtype == 'Credit Note(UR)' || invtype == 'Debit Note(UR)'){
						if(retType == 'GSTR1'){
							if(invoice.cdnur[0].ntty == 'C'){
									creditDebit = 'credit';
							}else{
								creditDebit = 'debit';
							}
						}else{
							if(invoice.cdnur[0].ntty == 'D'){
									creditDebit = 'credit';
							}else{
								creditDebit = 'debit';
							}
						}
				}else{
					creditDebit = 'debit';
				}
				var btogst = "";
				if(retType == 'GSTR2A'){
					btogst = invoice.billedtogstin+"-"+invoice.cfs;
				}else{
					btogst = invoice.billedtogstin;
				}
				var rowData = [invoice.revchargetype,invoice.id, '<div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" onclick="updateSelection(\''+invoice.id+'\', \''+retType+'\', \''+invoice.gstStatus+'\',this)" id="invFilter'+retType.replace(" ", "_")+invoice.id+'" /><i class="helper"></i></label></div>', status, invoice.eBillDate, invoice.invtype, invoice.invoiceno, invoice.billedtoname, btogst, invoice.dateofinvoice, formatNumber(invoice.totaltaxableamount.toFixed(2)), formatNumber(invoice.totaltax.toFixed(2)), formatNumber(invoice.totalamount.toFixed(2)), invoice.fullname,invoice.branch, invoice.vertical, invoice.invoiceCustomerIdAndBilledToName,invoice.supplyType];
				if(retType == 'Purchase Register' || retType == 'GSTR2' || retType == 'Unclaimed' || retType == 'GSTR6'){
					if(invoice.invtype == 'Advances' || invoice.invtype == 'Advance Adjusted Detail' || invoice.invtype == 'ITC Reversal' || invoice.invtype == 'ISD'){
						rowData.push('<td class="text-right invoiceclk" onClick="editInvPopup(null,\''+retType+'\',\''+invoice.id+'\',\''+NotInJournals+'\')""><span class="ind_formats">'+formatNumber(0)+'</span><div class="dropdown" style="display:inline-block;margin-left:5px"><i class="dropdown-toggle itc-avail-drop" type="button" data-toggle="dropdown" style="border: 1px solid #5769bb;padding:  0px;border-radius: 2px;"><span class="caret"></span></i><div class="dropdown-menu" style="padding: 25px; margin-left:-300px;width:270px;font-size: 15px;"><span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/close-icon.png" alt="Close" style="height: 24px;position: absolute; top: 5px; right: 1%; color: #5769bb;"></span><div class="row"><div class="col-md-12" style="color:red;">This Invoice is not Eligible for ITC</div></div></div></div></td>');
					}else{
						rowData.push('<td class="text-right invoiceclk" onClick="editInvPopup(null,\''+retType+'\',\''+invoice.id+'\',\''+NotInJournals+'\')"><span class="ind_formats" id="itcVal'+retType.replace(" ", "_")+invoice.id+'">'+formatNumber(invoice.totalitc.toFixed(2))+'</span><div class="dropdown" style="display:inline-block;margin-left:5px"><i class="dropdown-toggle itc-avail-drop" type="button" data-toggle="dropdown" style="border: 1px solid #5769bb;padding:  0px;border-radius: 2px;"><span class="caret"></span></i><div class="dropdown-menu" style="padding: 25px; margin-left:-300px;width:270px;font-size: 15px;height:150px;"><span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/close-icon.png" alt="Close" style="height: 24px;position: absolute; top: 5px; right: 1%; color: #5769bb;"></span><div class="row"><label class="col-md-4" style="padding-top: 0px;padding-bottom: 13px;padding-left: 15px;padding-right: 1px;">Input Type<span aria-hidden="true"></span></label><div class="col-md-6"><form><select id="'+invoice.id+retType.replace(" ", "_")+'itc_droptype" onchange="updateITCEligibity(this.value,\''+retType.replace(" ", "_")+'\',\''+invoice.id+'\')" class="form-control itc_drop"><option value="">- Input Type -</option><option value="cp">Capital Good</option><option value="ip">Inputs</option><option value="is">Input Service</option><option value="no">Ineligible</option><option value="pending">Not Selected</option> </select></form></div></div><span style="color:red;font-size:12px;">Your Input will reflect in GSTR 3B for the month of <span class="'+invoice.id+retType.replace(" ", "_")+'itc_claimeddate">'+gstr3bmonth+'</span></span><div class="row"><label class="col-md-4">ITC Claimed in</label><div class="col-md-5 p-0"> <div id="datepicker"><input type="date" value="'+tddate+'" class="form-control input-datepicker" name="date" id="'+invoice.id+retType.replace(" ", "_")+'itc_claimeddate" onchange="gstr3bmonthChange(\''+invoice.id+retType.replace(" ", "_")+'itc_claimeddate\')" required/></div></div></div><div class="row" style="margin-top: 10px;"><label class="col-md-4">ITC Amount</label><input id="'+invoice.id+retType.replace(" ", "_")+'itc_dropamt" type="text"  maxlength="3" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"  onkeyup="itcclaim()" class="form-control itc_amount col-md-2" value="100" style="width:  45px;"><span class="col-md-1" style="padding: 0px;margin-top:6px">%</span><button class="btn btn-blue col-md-1" id="claimbtn" style="height:24px" onclick="updateITCType(\''+retType.replace(" ", "_")+'\',\''+invoice.id+'\')" value="ok"> ok </button></div><a class="" href="#"  style="" onclick="updateunclaimdetails(\''+retType.replace(" ", "_")+'\',\''+invoice.id+'\')">Clear ITC Claimed Values</a></div></div></td>');
					}
				}
				if(retType == 'Purchase Register' || retType == 'GSTR2' || retType == 'GSTR1' || retType == 'ANX1' || retType == 'GSTR6' || retType == 'GSTR4' || retType == 'GSTR5' || retType == 'ESTIMATES' || retType == 'PROFORMAINVOICES' || retType == 'DELIVERYCHALLANS' || retType == 'PurchaseOrder'){
					if(retType == 'GSTR1' || retType == 'ANX1' || retType == 'GSTR4' || retType == 'GSTR5'){
						if('${otherreturnType}' == 'SalesRegister'){
							if(invoice.gstStatus == null || invoice.gstStatus == ''){
								rowData.push('<a href="${contextPath}/invprint/${id}/${fullname}/${usertype}/${client.id}/'+retType+'/'+invoice.id+'" target="_blank"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a class="permissionInvoices-Sales-Edit" onclick="editInvPopup(null,\''+retType+'\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="margin-right: 2px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="GST Filing Status : Pending" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:#FF9900;"></i></span>');
							}else if(invoice.gstStatus == 'SUCCESS' || invoice.gstStatus == 'Filed'){
								rowData.push('<a href="${contextPath}/invprint/${id}/${fullname}/${usertype}/${client.id}/'+retType+'/'+invoice.id+'" target="_blank"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a class="permissionInvoices-Sales-Edit" onclick="editInvPopup(null,\''+retType+'\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="margin-right: 2px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="GST Filing Status : Filed" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:green;"></i></span>');
							}else if(invoice.gstStatus == 'CANCELLED'){
								rowData.push('<a href="${contextPath}/invprint/${id}/${fullname}/${usertype}/${client.id}/'+retType+'/'+invoice.id+'" target="_blank"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a class="permissionInvoices-Sales-Edit" onclick="editInvPopup(null,\''+retType+'\',\''+invoice.id+'\',\''+NotInJournals+'\')" style="margin-right: 2px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="GST Filing Status : Cancelled" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:red;"></i></span>');
							}
						}else{
							rowData.push('<a href="${contextPath}/invprint/${id}/${fullname}/${usertype}/${client.id}/'+retType+'/'+invoice.id+'" target="_blank"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a class="permissionInvoices-Sales-Edit" onclick="editInvPopup(null,\''+retType+'\',\''+invoice.id+'\',\''+NotInJournals+'\')" ><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a>');
						}
					}else if(retType == 'Purchase Register' || retType == 'GSTR6'){
						if('${otherreturnType}' == 'PurchaseRegister' || retType == 'Purchase Register' || retType == 'GSTR2'){
							if(invoice.invtype == 'B2B' || invoice.invtype == 'Credit Note' || invoice.invtype == 'Debit Note' || invoice.invtype == 'Credit/Debit Notes' || invoice.invtype == 'Credit/Debit Note for Unregistered Taxpayers'){
								if(invoice.matchingStatus == 'Matched' || invoice.matchingStatus == 'Matched In Other Months' || invoice.matchingStatus == 'Round Off Matched' || invoice.matchingStatus == 'Probable Matched'){
									if(invoice.cfs == ''){
										rowData.push('<a href="${contextPath}/invprint/${id}/${fullname}/${usertype}/${client.id}/'+retType+'/'+invoice.id+'" target="_blank"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a>&nbsp;&nbsp;<a class="permissionInvoices-Purchase-Edit" style="margin-right: 0px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="'+invoice.matchingStatus+' In GSTR2A" style="float: right;vertical-align: middle;margin-right: 2px;"><i class="fa fa-circle" style="color:#53bc45;"></i></span>');	
									}else if(invoice.cfs == 'Y'){
										rowData.push('<a href="${contextPath}/invprint/${id}/${fullname}/${usertype}/${client.id}/'+retType+'/'+invoice.id+'" target="_blank"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a>&nbsp;&nbsp;<a class="permissionInvoices-Purchase-Edit" style="margin-right: 0px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="'+invoice.matchingStatus+' In GSTR2A <br> and Supplier is Filed" style="float: right;vertical-align: middle;margin-right: 2px;"><i class="fa fa-circle" style="color:#53bc45;"></i></span>');
									}else{
										rowData.push('<a href="${contextPath}/invprint/${id}/${fullname}/${usertype}/${client.id}/'+retType+'/'+invoice.id+'" target="_blank"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a>&nbsp;&nbsp;<a class="permissionInvoices-Purchase-Edit" style="margin-right: 0px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="'+invoice.matchingStatus+' In GSTR2A <br> and Supplier is Not Filed" style="float: right;vertical-align: middle;margin-right: 2px;"><i class="fa fa-circle" style="display: block;background: linear-gradient( 90deg, #53bc45 49.7%, rgb(235, 188, 0) 50% ); -webkit-background-clip: text;-webkit-text-fill-color: transparent;margin-right: -4px;"></i></span>');
									}
								}else if(invoice.matchingStatus == '' || invoice.matchingStatus == null){
									rowData.push('<a href="${contextPath}/invprint/${id}/${fullname}/${usertype}/${client.id}/'+retType+'/'+invoice.id+'" target="_blank"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a>&nbsp;&nbsp;<a class="permissionInvoices-Purchase-Edit" style="margin-right: 0px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="Not In GSTR2A" style="float: right;vertical-align: middle;margin-right: 2px;"><i class="fa fa-circle" style="color:red;"></i></span>');
								}else if(invoice.matchingStatus == 'Mismatched' || invoice.matchingStatus == 'Not In GSTR 2A' || invoice.matchingStatus == 'Not In Purchases'){
									rowData.push('<a href="${contextPath}/invprint/${id}/${fullname}/${usertype}/${client.id}/'+retType+'/'+invoice.id+'" target="_blank"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a>&nbsp;&nbsp;<a class="permissionInvoices-Purchase-Edit" style="margin-right: 0px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="'+invoice.matchingStatus+'" style="float: right;vertical-align: middle;margin-right: 2px;"><i class="fa fa-circle" style="color:red;"></i></span>');
								}else{
									rowData.push('<a href="${contextPath}/invprint/${id}/${fullname}/${usertype}/${client.id}/'+retType+'/'+invoice.id+'" target="_blank"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a>&nbsp;&nbsp;<a class="permissionInvoices-Purchase-Edit" style="margin-right: 0px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="'+invoice.matchingStatus+'" style="float: right;vertical-align: middle;margin-right: 2px;"><i class="fa fa-circle" style="color:yellow;"></i></span>');
								}
							}else{
								rowData.push('<a href="${contextPath}/invprint/${id}/${fullname}/${usertype}/${client.id}/'+retType+'/'+invoice.id+'" target="_blank"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a>&nbsp;&nbsp;<a class="permissionInvoices-Purchase-Edit" style="margin-right: 0px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="Not Eligible" style="float: right;margin-right: 2px;"><i class="fa fa-circle" style="color:grey;"></i></span>');
							}
						}else{
							rowData.push('<a href="${contextPath}/invprint/${id}/${fullname}/${usertype}/${client.id}/'+retType+'/'+invoice.id+'" target="_blank"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a>&nbsp;&nbsp;<a class="permissionInvoices-Purchase-Edit" style="margin-right: 0px;"><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a>');
						}
					}else{
						rowData.push('<a href="${contextPath}/invprint/${id}/${fullname}/${usertype}/${client.id}/'+retType+'/'+invoice.id+'" target="_blank"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right;"></a><a><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a>');
					}
				} else if(retType == 'Unclaimed'){
					if(invoice.invtype == 'B2B' || invoice.invtype == 'Credit Note' || invoice.invtype == 'Debit Note'  || invoice.invtype == 'Credit/Debit Notes' || invoice.invtype == 'Credit/Debit Note for Unregistered Taxpayers'){	
						if(invoice.matchingStatus == 'Matched' || invoice.matchingStatus == 'Matched In Other Months' || invoice.matchingStatus == 'Round Off Matched' || invoice.matchingStatus == 'Probable Matched'){
							if(invoice.cfs == ''){
								rowData.push('<a class="permissionInvoices-Purchase-Edit"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="'+invoice.matchingStatus+' In GSTR2A" style="float: right;vertical-align: middle;margin-right: 3px;"><i class="fa fa-circle" style="color:#53bc45;"></i></span>');	
							}else if(invoice.cfs == 'Y'){
								rowData.push('<a class="permissionInvoices-Purchase-Edit"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="'+invoice.matchingStatus+' In GSTR2A <br> and Supplier is Filed" style="float: right;vertical-align: middle;margin-right: 3px;"><i class="fa fa-circle" style="color:#53bc45;"></i></span>');
							}else{
								rowData.push('<a class="permissionInvoices-Purchase-Edit"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="'+invoice.matchingStatus+' In GSTR2A <br> and Supplier is Not Filed" style="float: right;vertical-align: middle;margin-right: 3px;"><i class="fa fa-circle" style="display: block;background: linear-gradient( 90deg, #53bc45 49.7%, rgb(235, 188, 0) 50% ); -webkit-background-clip: text;-webkit-text-fill-color: transparent;margin-right: -4px;"></i></span>');
							}
						}else if(invoice.matchingStatus == '' || invoice.matchingStatus == null){
							rowData.push('<a class="permissionInvoices-Purchase-Edit"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="Not In GSTR2A" style="float: right;vertical-align: middle;margin-right: 3px;"><i class="fa fa-circle" style="color:red;"></i></span>');
						}else if(invoice.matchingStatus == 'Mismatched' || invoice.matchingStatus == 'Not In GSTR 2A' || invoice.matchingStatus == 'Not In Purchases'){
							rowData.push('<a class="permissionInvoices-Purchase-Edit"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="'+invoice.matchingStatus+'" style="float: right;vertical-align: middle;margin-right: 3px;"><i class="fa fa-circle" style="color:red;"></i></span>');
						}else{
							rowData.push('<a class="permissionInvoices-Purchase-Edit"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a><span data-toggle="tooltip" title="'+invoice.matchingStatus+'" style="float: right;vertical-align: middle;margin-right: 3px;"><i class="fa fa-circle" style="color:yellow;"></i></span>');
						}
					}else{
						rowData.push('<a class="permissionInvoices-Purchase-Edit"> <i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i></a>');
					}
				}
				if(retType == 'GSTR2A'){
					if(invoice.invtype == 'B2B' || invoice.invtype == 'Credit Note' || invoice.invtype == 'Debit Note'  || invoice.invtype == 'Credit/Debit Notes' || invoice.invtype == 'Credit/Debit Note for Unregistered Taxpayers'){	
						if(invoice.matchingStatus == 'Matched' || invoice.matchingStatus == 'Matched In Other Months' || invoice.matchingStatus == 'Round Off Matched' || invoice.matchingStatus == 'Probable Matched'){
							rowData.push('<span data-toggle="tooltip" title="'+invoice.matchingStatus+' In Purchases" style="float: right;vertical-align: middle;margin-right: 3px;"><i class="fa fa-circle" style="color:#53bc45;"></i></span>');
						}else if(invoice.matchingStatus == '' || invoice.matchingStatus == null){
							rowData.push('<span data-toggle="tooltip" title="Not In Purchases" style="float: right;vertical-align: middle;margin-right: 3px;"><i class="fa fa-circle" style="color:red;"></i></span>');
						}else if(invoice.matchingStatus == 'Mismatched' || invoice.matchingStatus == 'Not In GSTR 2A' || invoice.matchingStatus == 'Not In Purchases'){
							rowData.push('<span data-toggle="tooltip" title="'+invoice.matchingStatus+'" style="float: right;vertical-align: middle;margin-right: 3px;"><i class="fa fa-circle" style="color:red;"></i></span>');
						}else{
							rowData.push('<span data-toggle="tooltip" title="'+invoice.matchingStatus+'" style="float: right;vertical-align: middle;margin-right: 3px;"><i class="fa fa-circle" style="color:yellow;"></i></span>');
						}
					}
				}
				if(retType == 'EWAYBILL'){
					rowData.push('<a class="permissionInvoices-Sales-Edit" onclick="editInvPopup(null,\''+retType+'\',\''+invoice.id+'\',\''+NotInJournals+'\')" ><i class="fa fa-edit" style="font-size: 17px;vertical-align: middle;float:right"></i>');
				}
				rows.push(rowData);
				taxArray.push([invoice.igstamount,invoice.cgstamount,invoice.sgstamount,invoice.cessamount,invoice.totaltaxableamount,invoice.totaltax,invoice.totalitc,invoice.totalamount,creditDebit,invoice.totalExempted]);
			});
			var index = 0, transCount=0, tIGST=0, tCGST=0, tSGST=0, tCESS=0, tTaxableAmount=0, tTotalTax=0, tTotalItc=0, tTotalAmount=0, tTotalExempted=0;
			rows.forEach(function(row) {
			  if((arrStatus.length == 0 || $.inArray('All', arrStatus) >= 0 || $.inArray(row[3], arrStatus) >= 0	|| ($.inArray('Failed', arrStatus) >= 0 && row[3] != 'Pending' && row[3] != 'SUCCESS' && row[3] != 'Filed' && row[3] != 'CANCELLED' && row[3] != 'Submitted' && row[3] != 'Paid' && row[3] != 'Partially Paid' && row[3] != 'Not Paid'))
					&& (arrInvType.length == 0 || $.inArray('All', arrInvType) >= 0 || $.inArray(row[5], arrInvType) >= 0)
					&& (arrUser.length == 0 || $.inArray('All', arrUser) >= 0 || $.inArray(row[13], arrUser) >= 0)
					&& (arrVendor.length == 0 || $.inArray('All', arrVendor) >= 0 || $.inArray(row[16], arrVendor) >= 0)
					&& (arrBranch.length == 0 || $.inArray('All', arrBranch) >= 0 || $.inArray(row[14], arrBranch) >= 0)
					&& (arrVertical.length == 0 || $.inArray('All', arrVertical) >= 0 || $.inArray(row[15], arrVertical) >= 0)
					&& (arrSupplyType.length == 0 || $.inArray('All', arrSupplyType) >= 0 || $.inArray(row[17], arrSupplyType) >= 0)
					&& (arrReverseCharge.length == 0 || $.inArray('All', arrReverseCharge) >= 0 || $.inArray(row[0], arrReverseCharge) >= 0)){
				var invid = row[1];
				  var reversecharge =  row[0];
				  row.splice(0, 2);
				  var status=null;
				  status=row[1];
				  if(status  !='CANCELLED'){
					  transCount++;
				  }
					var gstStatus = "<span class='color-yellow'>Pending</span>";
					if(row[1] == 'SUCCESS') {
						gstStatus = "<span class='color-green' data-toggle='tooltip' title='GST Filing Status : Uploaded'>Uploaded</span>";
					} else if(row[1] == 'CANCELLED') {
						gstStatus = "<span class='color-red' data-toggle='tooltip' title='GST Filing Status : Cancelled'>Cancelled</span>";
					} else if(row[1] == 'Pending') {
						gstStatus = "<span class='color-yellow' data-toggle='tooltip' title='GST Filing Status : Pending'>Pending</span>";
					} else if(row[1] == 'Filed'){
						gstStatus = "<span class='color-green' data-toggle='tooltip' title='GST Filing Status : Filed'>Filed</span>";
					} else if(row[1] == 'Submitted'){
						gstStatus = "<span class='color-green' data-toggle='tooltip' title='GST Filing Status : Submitted'>Submitted</span>";
					} else if(row[1] == 'In Progress'){
						gstStatus = "<span class='color-blue' style= 'color:blue;' data-toggle='tooltip' title='GST Filing Status : In Progress'>In Progress</span>";
					} else if(row[1] == 'Paid'){
						gstStatus = "<span class='color-green' data-toggle='tooltip' title='Payment Status : Paid'>Paid</span>";
					}else if(row[1] == 'Not Paid'){
						gstStatus = "<span class='color-red' data-toggle='tooltip' title='Payment Status : Not Paid'>Not Paid</span>";
					}else if(row[1] == 'Partially Paid'){
						  gstStatus = "<span class='color-yellow' data-toggle='tooltip' title='Payment Status : Partially Paid'>Partially Paid</span>";
					}else if(row[1] == status){
						gstStatus = "<span class=''>"+status+"</span>";
					}else {
						gstStatus = "<span class='color-red' data-toggle='tooltip' title='GST Filing Status : Failed'>Failed </span><i class='fa fa-info-circle' style='font-size:17px;color:red' rel='tooltip' title='"+row[1]+"'></i>";
					}
					row[1] = gstStatus;
					var reverseChargeType = row[3];
					if(reversecharge == 'Reverse'){
						reverseChargeType = row[3]+"<span><img data-toggle='tooltip' title='Reverse Charge Applied on this Invoice' src='${contextPath}/static/mastergst/images/dashboard-ca/reversecharge.png' alt='reversecharge' style='height: 18px;margin-left: 10px;margin-bottom:3px'></span>";
					}
					row[3] = reverseChargeType;
					if(retType == 'GSTR2A'){
						var btgst = row[6].split("-"); 
						if(btgst[1] == 'Y'){row[6] = "<span class='' style='color:#119400!important' data-toggle='tooltip' title='Supplier Invoice Status is Filed'>"+btgst[0]+"</span>";}else if(btgst[1] == 'N'){row[6] = "<span class='' style='color:rgb(235, 188, 0)' data-toggle='tooltip' title='Supplier Invoice Status is Pending'>"+btgst[0]+"</span>";}else{row[6] = btgst[0];}
					}
					row[8] = "<span class='ind_formats'>"+row[8]+"</span>";
					row[9] = "<span id='tot_tax' class='ind_formats'>"+row[9]+"<div id='tax_tot_drop1' style='display:none'><div id='drop-tottax'></div><h6 style='text-align: center;' class='mb-2 tax_text'>TAX AMOUNT</h6><div class='row pl-3' style='height:25px'><p class='mr-3'>IGST <span style='margin-left:5px'>:<span></p><span><label class='dropdown taxindformat' id='' name='' style='border:none;padding-top: 2px;background: none;'>"+formatNumber(taxArray[index][0].toFixed(2))+"</label></span></div><div class='row pl-3' style='height:25px'><p class='mr-3'>CGST :</p><span><label class='taxindformat' id='' name='' style='border:none;padding-top: 2px;background: none;'>"+formatNumber(taxArray[index][1].toFixed(2))+"</label></span></div><div class='row pl-3' style='height:25px'><p class='mr-3'>SGST :</p><span><label class='taxindformat' id='' name='' style='border:none;padding-top: 2px;background: none;'>"+formatNumber(taxArray[index][2].toFixed(2))+"</label></span></div></div></span>";
					row[10] = "<span class='ind_formats'>"+row[10]+"</span>";
					rowNode = invoiceTable[retType.replace(" ", "_")].row.add(row);
					invoiceArray[retType.replace(" ", "_")].forEach(function(invoice) {
						
						if(invoice.id == invid) {
							if(retType == 'GSTR1' || retType == 'ANX1' || retType == 'GSTR4' || retType == 'GSTR5'){
								$(rowNode.draw().node()).children().addClass('invoiceclk permissionInvoices-Sales-Edits').attr('onclick',"editInvPopup(null,'"+retType+"','"+invoice.id+"','"+NotInJournals+"')");
							}else if(retType == 'Purchase Register' || retType == 'GSTR6'){
								$(rowNode.draw().node()).children().addClass('invoiceclk permissionInvoices-Purchase-Edits').attr('onclick',"editInvPopup(null,'"+retType+"','"+invoice.id+"','"+NotInJournals+"')");
							}else{
								$(rowNode.draw().node()).children().addClass('invoiceclk').attr('onclick',"editInvPopup(null,'"+retType+"','"+invoice.id+"','"+NotInJournals+"')");
							}
						}
					});
					invoiceTable[retType.replace(" ", "_")].row(rowNode).column(8).nodes().to$().addClass('text-right');
					invoiceTable[retType.replace(" ", "_")].row(rowNode).column(9).nodes().to$().addClass('text-right');
					invoiceTable[retType.replace(" ", "_")].row(rowNode).column(10).nodes().to$().addClass('text-right');
					if(row.length > 15) {
						invoiceTable[retType.replace(" ", "_")].row(rowNode).column(16).nodes().to$().addClass('text-right').removeAttr('onclick');
						invoiceTable[retType.replace(" ", "_")].row(rowNode).column(17).nodes().to$().addClass('text-center');
					}
					invoiceTable[retType.replace(" ", "_")].row(rowNode).column(0).nodes().to$().addClass('text-right').removeAttr('onclick');
					invoiceTable[retType.replace(" ", "_")].rows().every( function () {
					    var d = this.data();
					    if('${varRetType}' != 'GSTR2' && '${varRetType}' != 'Purchase Register'){
						 	if(d[1]){
						    	if(d[1].indexOf("Cancelled") != -1){
							 		this.nodes().to$().addClass('cancelled_line');
							 	}
						 	}
					    }
						if('${otherreturnType}' == 'SalesRegister'){
							if(d[16]){
							 	if(d[16].indexOf("Cancelled") != -1){
							 		this.nodes().to$().addClass('cancelled_line');
							 	}
							}
						} 
					});
					 if(status  !='CANCELLED'){
						if(taxArray[index][8] != 'credit'){
							tIGST+=taxArray[index][0];
							tCGST+=taxArray[index][1];
							tSGST+=taxArray[index][2];
							tCESS+=taxArray[index][3];
							tTaxableAmount+=parseFloat(taxArray[index][4]);
							tTotalTax+=parseFloat(taxArray[index][5]);
							tTotalItc+=parseFloat(taxArray[index][6]);
							tTotalAmount+=parseFloat(taxArray[index][7]);
							tTotalExempted+=parseFloat(taxArray[index][9]);
						}
					 }
			  }
			  index++;
			  });
			
			$('#idCount'+retType.replace(" ", "_")).html(transCount);
			$('#idIGST'+retType.replace(" ", "_")).html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tIGST).toFixed(2)));
			$('#idCGST'+retType.replace(" ", "_")).html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tCGST).toFixed(2)));
			$('#idSGST'+retType.replace(" ", "_")).html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tSGST).toFixed(2)));
			$('#idCESS'+retType.replace(" ", "_")).html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tCESS).toFixed(2)));
			$('#idTaxableVal'+retType.replace(" ", "_")).html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tTaxableAmount).toFixed(2)));
			$('#idExemptedVal'+retType.replace(" ", "_")).html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tTotalExempted).toFixed(2)));
			$('#idTaxVal'+retType.replace(" ", "_")).html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tTotalTax).toFixed(2)));
			$('#idITC'+retType.replace(" ", "_")).html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tTotalItc).toFixed(2)));
			$('#idTotAmtVal'+retType.replace(" ", "_")).html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tTotalAmount).toFixed(2)));
			$('#idTabTotal'+retType.replace(" ", "_")).html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tTotalAmount).toFixed(2)));
			if(retType == 'Purchase Register' || retType == 'GSTR2' || retType == 'GSTR6'){
				$('#idTabTotal'+retType.replace(" ", "_")).html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tTotalItc).toFixed(2)));
			}
			if(retType == 'Unclaimed'){
				$('#idTabTotal'+retType.replace(" ", "_")).html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tTotalTax).toFixed(2)));
				
			}
		}else{}
		}
	
	
	function addtoGSTR1(btn,addflag){
	 if(ewaybillArray.length > 0) {
				for(var i=0;i<ewaybillArray.length;i++) {
					addtogstr1(ewaybillArray[i]);
				}
	} else {
				addtogstr1(new Array());
			} 
		
	}
	 function addtogstr1(ewaybillArray){
		$.ajax({
			type: "GET",
			url: "${contextPath}/addtogstr1fromebill/"+ewaybillArray+"/${client.id}/${month}/${year}",
			success : function(response) {
				if(response == true || response == "true"){
					$('#addtogstr1Modal').modal('show');
					$('#maininv_head').html("Invoice is already in SaleRegister, you want to Replace it?");
					$('#view_GSTR1').html("Replace");
					$('#view_GSTR1').attr("onclick","viewGSTR1Inv('true')");
				}else{
					$('#addtogstr1Modal').modal('show');
					$('#view_GSTR1').attr("onclick","viewGSTR1Inv('false')");
				}
			},
			error : function(e, status, error) {
				if(e.responseText) {errorNotification(e.responseText);}
			}
		});
	} 
	
	
	function updateITCEligibity(value, retType, invId) {
		var amt = $('#'+invId+retType.replace(" ", "_")+'itc_dropamt').val();
		if(otherconfigdetails != ''){
			if(value == 'ip'){
				$('#'+invId+retType.replace(" ", "_")+'itc_dropamt').val(otherconfigdetails.itcinput);
			}else if(value == 'is'){
				$('#'+invId+retType.replace(" ", "_")+'itc_dropamt').val(otherconfigdetails.itcinputService);
			}else if(value == 'cp'){
				$('#'+invId+retType.replace(" ", "_")+'itc_dropamt').val(otherconfigdetails.itcCapgood);
			}else{
				$('#'+invId+retType.replace(" ", "_")+'itc_dropamt').val("0");
			}
			//findITCValue(no);
		}else{
		if(value == 'ip' || value == 'cp') {
			if(amt == 0) {
				$('#'+invId+retType.replace(" ", "_")+'itc_dropamt').val(100);
			}
		} else {
			$('#'+invId+retType.replace(" ", "_")+'itc_dropamt').val(0);
		}
		}
	}
	
function updateunclaimdetails(retType, invId){

	var returnType = retType;
	if(retType == 'Purchase_Register' || retType == 'Unclaimed'){
		returnType = 'Purchase Register';
	}
	$.ajax({
		url : contextPath+'/unclaimupdt/'+invId+'/'+returnType,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
		if(response > 0) {
			$('#itcVal'+retType.replace(" ", "_")+invId).html(formatNumber(response.toFixed(2)));
			invoiceArray[returnType.replace(" ", "_")].forEach(function(pInv) {
				if(pInv.id == invId) {
					pInv.totalitc = response;
				}
			});
		}else if(response == 0) {
			$('#itcVal'+retType.replace(" ", "_")+invId).html(formatNumber(response.toFixed(2)));
			invoiceArray[returnType.replace(" ", "_")].forEach(function(pInv) {
				if(pInv.id == invId) {
					pInv.totalitc = response;
				}
			});
		}		
		},
		error : function(e, status, error) {}
	});
	}
	
	function updateITCType(retType, invId) {
		var type = $('#'+invId+retType.replace(" ", "_")+'itc_droptype').val();
		var amt = $('#'+invId+retType.replace(" ", "_")+'itc_dropamt').val();
		var claimeddate = $('#'+invId+retType.replace(" ", "_")+'itc_claimeddate').val();
		var returnType = retType;
		if(retType == 'Unclaimed'){
			returnType = 'Purchase Register';
		}
		if(amt != null && amt>100){
			amt = '100';
		}	
		$.ajax({
			url : contextPath+'/itcupdt/'+invId+'/'+returnType+'/'+type+'/'+amt+'/'+claimeddate,
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				if(response > 0) {
					$('#itcVal'+retType.replace(" ", "_")+invId).html(formatNumber(response.toFixed(2)));
					invoiceArray[returnType.replace(" ", "_")].forEach(function(pInv) {
						if(pInv.id == invId) {
							pInv.totalitc = response;
						}
					});
				}else if(response == 0) {
					$('#itcVal'+retType.replace(" ", "_")+invId).html(formatNumber(response.toFixed(2)));
					invoiceArray[returnType.replace(" ", "_")].forEach(function(pInv) {
						if(pInv.id == invId) {
							pInv.totalitc = response;
						}
					});
				}
			}
		});
	}
	function processInv(btn, acceptFlag) {
		$(btn).addClass('btn-loader');
		window.setTimeout(function(){
			$('.accept, .reject').removeClass("btn-loader");
		}, 2000);
		if(selArray['${varRetType.replaceAll(' ', '_')}']) {
			$.ajax({
				type: "POST",
				url: "${contextPath}/updtamnddata/${client.id}/${varRetType}/"+acceptFlag,
				async: false,
				cache: false,
				data: JSON.stringify(selArray['${varRetType.replaceAll(' ', '_')}']),
				contentType: 'application/json',
				success : function(response) {
				},
				error : function(e, status, error) {
					$(btn).removeClass('btn-loader');
					if(e.responseText) {
						errorNotification(e.responseText);
					}
				}
			});
		}else if(chkBox.checked) {

			$('#rejectbtn').removeClass('disable');
			$('#acceptbtn').removeClass('disable');
		} else {
			$('#acceptbtn').addClass('disable');
			$('#rejectbtn').addClass('disable');
		}
	}
	function updateInvoiceDetails(invoice) {
		var totalIGST1 = 0, totalCGST1 = 0, totalSGST1 = 0, totalCESS1 = 0,  totalITCIGST1 = 0, totalITCCGST1 = 0, totalITCSGST1 = 0, totalITCCESS1 = 0, totalinvitc = 0, totalExempted = 0;
		var rettype = '${return_type}';var etype = '${eType}';
		invoice.id = invoice.userid;
		invoice.invoicetype = invoice.invtype;
		invoice.subSupplyType = invoice.subSupplyType;
		if(invoice.actualDist){
			invoice.transDistance = invoice.actualDist;
		}
		invoice.transDistance = invoice.transDistance;
		if(invoice.supplyType == null){
			invoice.supplyType = '';
		}
		if(invoice.eBillNo == null){
			invoice.eBillNo = '';
		}
		if(invoice.invoiceno == null){
			invoice.invoiceno = '';
		}
		if(invoice.paymentStatus == null){
			invoice.paymentStatus = '';
		}
		if(invoice.clientAddress == null){
			invoice.clientAddress = '${client.address}';
		}
		if(invoice.invoiceEcomGSTIN == null){
			invoice.invoiceEcomGSTIN = '';
		}
		if(invoice.tdstcsenable == null){
			invoice.tdstcsenable = 'false';
		}
		if(invoice.section == null){
			invoice.section = '';
		}
		if(invoice.tcstdspercentage == null){
			invoice.tcstdspercentage = 0.00;
		}
		if(invoice.tcstdsAmount == null){
			invoice.tcstdsAmount = 0.00;
		}
		if(invoice.netAmount == null){
			invoice.netAmount = 0.00;
		}
		if(invoice.revchargetype == null){
			invoice.revchargetype = 'Regular';
		}
		if(invoice.ledgerName == null){
			invoice.ledgerName = '';
		}
		if(invoice.dealerType == null){
			invoice.dealerType = '';
		}
		if(invoice.vehicleType == null){
			invoice.vehicleType = 'R';
		}
		if(invoice.vendorName == null){
			invoice.vendorName = '';
		}
		invoice.serialnoofinvoice = invoice.invoiceno;
		if(invoice.b2b && invoice.b2b.length > 0) {
			if(invoice.b2b[0].ctin == null){
				invoice.b2b[0].ctin = '';
			}
			if(invoice.b2b[0].cfs == null){
				invoice.b2b[0].cfs = '';
			}
			invoice.cfs = invoice.b2b[0].cfs; 
			invoice.billedtogstin = invoice.b2b[0].ctin;
			if(invoice.b2b[0].inv && invoice.b2b[0].inv.length > 0) {
				if(etype == 'EWAYBILL' || rettype == 'EWAYBILL'){
					if(invoice.fromAddr1 == '' && invoice.fromAddr2 == ''){
						invoice.address = invoice.fromPlace+", pincode -"+invoice.fromPincode;
					}else{
					invoice.address = invoice.fromAddr1+","+invoice.fromAddr2+","+invoice.fromPlace+", pincode -"+invoice.fromPincode;
					}
					if(invoice.toAddr1 == '' && invoice.toAddr2 == ''){
						invoice.consigneeaddress = invoice.toPlace+", pincode -"+invoice.toPincode;
					}else{
					invoice.consigneeaddress = invoice.toAddr1+","+invoice.toAddr2+","+invoice.toPlace+", pincode -"+invoice.toPincode;
					}
				}else{
					invoice.address = invoice.b2b[0].inv[0].address;
				}
				invoice.invTyp = invoice.b2b[0].inv[0].invTyp;
			}
		} else {
			invoice.billedtogstin = '';
			invoice.address = '';
		}
		if(invoice.pi && invoice.pi.length > 0){
			var expDate = invoice.pi[0].expirydate;
			expDate = expDate.split('-'); 
			invoice.expirydateofinvoice = expDate[0] + "/" + expDate[1] + "/" + expDate[2];
		}else if(invoice.est && invoice.est.length > 0){
			var expDate = invoice.est[0].expirydate;
			expDate = expDate.split('-');
			invoice.expirydateofinvoice = expDate[0] + "/" + expDate[1] + "/" + expDate[2];
		}
		if(invoice.po && invoice.po.length > 0){
			var deliveryDate = invoice.po[0].deliverydate;
			deliveryDate = deliveryDate.split('-');
			invoice.deliverydate = deliveryDate[0] + "/" + deliveryDate[1] + "/" + deliveryDate[2];
		}
		
		if(invoice.dc && invoice.dc.length > 0){
			invoice.challantype = invoice.dc[0].challanType;
		}
		
		if(invoice.diffPercent == null){
			invoice.diffPercent = 'No';
		}
		if(invoice.billDate != null){
			var billDate = new Date(invoice.billDate);
			var day = billDate.getDate() + "";var month = (billDate.getMonth() + 1) + "";var year = billDate.getFullYear() + "";
			day = checkZero(day);month = checkZero(month);year = checkZero(year);
			invoice.billDate = day + "/" + month + "/" + year;
		}
		if(invoice.dateofinvoice != null){
			var invDate = new Date(invoice.dateofinvoice);
			var day = invDate.getDate() + "";var month = (invDate.getMonth() + 1) + "";	var year = invDate.getFullYear() + "";
			day = checkZero(day);month = checkZero(month);year = checkZero(year);
			invoice.dateofinvoice = day + "/" + month + "/" + year;
		}else{invoice.dateofinvoice = "";}
		if(invoice.dueDate != null){
			var invDate = new Date(invoice.dueDate);
			var day = invDate.getDate() + "";var month = (invDate.getMonth() + 1) + "";	var year = invDate.getFullYear() + "";
			day = checkZero(day);month = checkZero(month);year = checkZero(year);
			invoice.dueDate = day + "/" + month + "/" + year;
		}else{invoice.dueDate = "";}
		if(invoice.dateofitcClaimed != null){
			var invDate = new Date(invoice.dateofitcClaimed);
			var day = invDate.getDate() + "";var month = (invDate.getMonth() + 1) + "";var year = invDate.getFullYear() + "";
			day = checkZero(day);month = checkZero(month);year = checkZero(year);
			invoice.dateofitcClaimed = day + "/" + month + "/" + year;
		}else{invoice.dateofitcClaimed = "";}
		if(invoice.eBillDate != null){
			var eDate = new Date(invoice.eBillDate);var day = eDate.getDate() + "";var month = (eDate.getMonth() + 1) + "";var year = eDate.getFullYear() + "";
			day = checkZero(day);month = checkZero(month);year = checkZero(year);
			invoice.eBillDate = day + "/" + month + "/" + year;
		}else{invoice.eBillDate = "";}
		if(invoice.bankDetails) {
			if(invoice.bankDetails.bankname == '' && invoice.bankDetails.accountnumber == '' && invoice.bankDetails.branchname == '' && invoice.bankDetails.ifsccode == ''){
				invoice.bankdetails = 'false';
			}else{
				invoice.bankdetails = 'true';
			}	
			invoice.bankname = invoice.bankDetails.bankname;
			invoice.accountnumber = invoice.bankDetails.accountnumber;
			invoice.branchname = invoice.bankDetails.branchname;
			invoice.ifsccode = invoice.bankDetails.ifsccode;
			if(invoice.bankDetails.accountName == null){
				invoice.accountname = '';
			}else{
				invoice.accountname = invoice.bankDetails.accountName;
			}
			} else {
			invoice.bankdetails = 'false';
		}
		if(invoice.fullname == null) {
			invoice.fullname = '';
		}
		if((invoice.billedtoname == '' && invoice.invoiceCustomerId == '') || (invoice.billedtoname == null && invoice.invoiceCustomerId == null)) {
			invoice.invoiceCustomerIdAndBilledToName = '';
		}else if((invoice.billedtoname != null && invoice.invoiceCustomerId == null) || (invoice.billedtoname != '' && invoice.invoiceCustomerId == '')) {
			invoice.invoiceCustomerIdAndBilledToName =invoice.billedtoname;
		}else if((invoice.billedtoname != null || invoice.billedtoname != '') && (invoice.invoiceCustomerId != null || invoice.invoiceCustomerId != '')) {
			invoice.invoiceCustomerIdAndBilledToName = invoice.billedtoname+"("+invoice.invoiceCustomerId+")";
		}
		if(invoice.invoiceCustomerId == null) {
			invoice.invoiceCustomerId = '';
		}
		if(invoice.billedtoname == null) {
			invoice.billedtoname = '';
		}
		if(invoice.branch == null) {
			invoice.branch = '';
		}
		if(invoice.vertical == null) {
			invoice.vertical = '';
		}
		if(invoice.totaltaxableamount) {
			if(invoice.gstStatus != 'CANCELLED'){
				if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
						if(invoice.cdn[0].nt[0].ntty == 'D'){
							totalTaxableValue += invoice.totaltaxableamount;
						}else{
							totalTaxableValue-=invoice.totaltaxableamount;
						}
				}else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
					if(invoice.cdnr[0].nt[0].ntty == 'C'){
						totalTaxableValue-=invoice.totaltaxableamount;
					}else{
						totalTaxableValue+=invoice.totaltaxableamount;
					}
				}else if(('<%=MasterGSTConstants.CDNUR%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
					if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
						if(invoice.cdnur[0].ntty == 'D'){
							totalTaxableValue+=invoice.totaltaxableamount;
						}else{
							totalTaxableValue-=invoice.totaltaxableamount;
						}
					}else{
						if(invoice.cdnur[0].ntty == 'C'){
							totalTaxableValue-=invoice.totaltaxableamount;
						}else{
							totalTaxableValue+=invoice.totaltaxableamount;
						}
					}
					
				}else{
					totalTaxableValue+=invoice.totaltaxableamount;
				}
			}
		} else {
			invoice.totaltaxableamount = 0.00;
		}
		if(invoice.totaltax) {
			if(invoice.gstStatus != 'CANCELLED'){
				if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
					if(invoice.cdn[0].nt[0].ntty == 'D'){
						totalTax+=invoice.totaltax;
					}else{
						totalTax-=invoice.totaltax;
					}
				}else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
					if(invoice.cdnr[0].nt[0].ntty == 'C'){
						totalTax-=invoice.totaltax;
					}else{
						totalTax+=invoice.totaltax;
					}
				}else if(('<%=MasterGSTConstants.CDNUR%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
					if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
						if(invoice.cdnur[0].ntty == 'D'){
							totalTax+=invoice.totaltax;
						}else{
							totalTax-=invoice.totaltax;
						}
					}else{
						if(invoice.cdnur[0].ntty == 'C'){
							totalTax-=invoice.totaltax;
						}else{
							totalTax+=invoice.totaltax;
						}
					}
				}else{
					totalTax+=invoice.totaltax;
				}
			}
		} else {
			invoice.totaltax = 0.00;
		}
		if(invoice.totalamount) {
			if(invoice.gstStatus != 'CANCELLED'){
				if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
					if(invoice.cdn[0].nt[0].ntty == 'D'){
							totalValue+=invoice.totalamount;
						}else{
							totalValue-=invoice.totalamount;
						}
				}else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
					if(invoice.cdnr[0].nt[0].ntty == 'C'){
						totalValue-=invoice.totalamount;
					}else{
						totalValue+=invoice.totalamount;
					}
				}else if(('<%=MasterGSTConstants.CDNUR%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
					if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
						if(invoice.cdnur[0].ntty == 'D'){
							totalValue+=invoice.totalamount;
						}else{
							totalValue-=invoice.totalamount;
						}
					}else{
						if(invoice.cdnur[0].ntty == 'C'){
							totalValue-=invoice.totalamount;
						}else{
							totalValue+=invoice.totalamount;
						}
					}
				}else{
					totalValue+=invoice.totalamount;
				}
			}
		} else {
			invoice.totalamount = invoice.totaltax + invoice.totaltaxableamount;
			if(invoice.gstStatus != 'CANCELLED'){
				if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
					if(invoice.cdn[0].nt[0].ntty == 'D'){
							totalValue+=invoice.totalamount;
						}else{
							totalValue-=invoice.totalamount;
						}
				}else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
					if(invoice.cdnr[0].nt[0].ntty == 'C'){
						totalValue-=invoice.totalamount;
					}else{
						totalValue+=invoice.totalamount;
					}
				}else if(('<%=MasterGSTConstants.CDNUR%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
					if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
						if(invoice.cdnur[0].ntty == 'D'){
							totalValue+=invoice.totalamount;
						}else{
							totalValue-=invoice.totalamount;
						}
					}else{
						if(invoice.cdnur[0].ntty == 'C'){
							totalValue-=invoice.totalamount;
						}else{
							totalValue+=invoice.totalamount;
						}
					}
				}else{
					totalValue+=invoice.totalamount;
				}
			}
			
		}
		if(('<%=MasterGSTConstants.B2C%>' == invoice.invtype && invoice.b2cs && invoice.b2cs.length > 0) || ('<%=MasterGSTConstants.B2CSA%>' == invoice.invtype && invoice.b2cs && invoice.b2cs.length > 0)) {
			invoice.splyTy = invoice.b2cs[0].splyTy;
			invoice.ecommercegstin = invoice.b2cs[0].etin;
		}
		if(('<%=MasterGSTConstants.ADVANCES%>' == invoice.invtype && invoice.at && invoice.at.length > 0) || ('<%=MasterGSTConstants.ATA%>' == invoice.invtype && invoice.at && invoice.at.length > 0)) {
			invoice.splyTy = invoice.at[0].splyTy;
		}else if(('<%=MasterGSTConstants.ADVANCES%>' == invoice.invtype && invoice.txi && invoice.txi.length > 0) || ('<%=MasterGSTConstants.ATA%>' == invoice.invtype && invoice.txi && invoice.txi.length > 0)){
			invoice.splyTy = invoice.txi[0].splyTy;
		}
		if(('<%=MasterGSTConstants.ATPAID%>' == invoice.invtype && invoice.txpd && invoice.txpd.length > 0) || ('<%=MasterGSTConstants.ATA%>' == invoice.invtype && invoice.txpd && invoice.txpd.length > 0)) {
			invoice.splyTy = invoice.txpd[0].splyTy;
			invoice.invtype = 'Adv. Adjustments';
		}
		if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
			invoice.voucherNumber = invoice.cdn[0].nt[0].inum;
			invoice.ntty = invoice.cdn[0].nt[0].ntty;
			invoice.rsn = invoice.cdn[0].nt[0].rsn;
			invoice.pGst = invoice.cdn[0].nt[0].pGst;
			if(invoice.cdn[0].cfs == null){
				invoice.cdn[0].cfs = '';
			}
			invoice.cfs = invoice.cdn[0].cfs; 
			if(invoice.cdn[0].nt[0].idt != null){
				invoice.voucherDate = invoice.cdn[0].nt[0].idt;
			}else{
				invoice.voucherDate = '';
			}
			if(invoice.cdn[0].nt[0].ntty == 'C'){
				invoice.invtype = 'Credit Note';
			}else if(invoice.cdn[0].nt[0].ntty == 'D'){
				invoice.invtype = 'Debit Note';
			}
			
		} else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)) {
			invoice.voucherNumber = invoice.cdnr[0].nt[0].inum;
			invoice.ntty = invoice.cdnr[0].nt[0].ntty;
			invoice.rsn = invoice.cdnr[0].nt[0].rsn;
			invoice.pGst = invoice.cdnr[0].nt[0].pGst;
			if(invoice.cdnr[0].nt[0].idt != null){
				invoice.voucherDate = invoice.cdnr[0].nt[0].idt;
			}else{
				invoice.voucherDate = '';
			}
			if(invoice.cdnr[0].nt[0].ntty == 'C'){
				invoice.invtype = 'Credit Note';
			}else if(invoice.cdnr[0].nt[0].ntty == 'D'){
				invoice.invtype = 'Debit Note';
			}
		}
		if(('<%=MasterGSTConstants.CDNUR%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)) {
			invoice.voucherNumber = invoice.cdnur[0].inum;
			if(invoice.cdnur[0].idt != null){
				invoice.voucherDate = invoice.cdnur[0].idt;
			}else{
				invoice.voucherDate = '';
			}
			invoice.ntty = invoice.cdnur[0].ntty;
			invoice.rsn = invoice.cdnur[0].rsn;
			invoice.pGst = invoice.cdnur[0].pGst;
			invoice.cdnurtyp = invoice.cdnur[0].typ;
			if(invoice.cdnur[0].ntty == 'C'){
				invoice.invtype = 'Credit Note(UR)';
			}else if(invoice.cdnur[0].ntty == 'D'){
				invoice.invtype = 'Debit Note(UR)';
			}
		}
		if(('<%=MasterGSTConstants.EXPORTS%>' == invoice.invtype && invoice.exp && invoice.exp.length > 0 && invoice.exp[0].inv && invoice.exp[0].inv.length > 0) || ('<%=MasterGSTConstants.EXPA%>' == invoice.invtype && invoice.exp && invoice.exp.length > 0 && invoice.exp[0].inv && invoice.exp[0].inv.length > 0)) {
			invoice.portcode = invoice.exp[0].inv[0].sbpcode;
			invoice.shippingBillNumber = invoice.exp[0].inv[0].sbnum;
			invoice.exportType = invoice.exp[0].expTyp;
			if(invoice.exp[0].inv[0].sbdt != null && invoice.exp[0].inv[0].sbdt != undefined){
				invoice.shippingBillDate = formatDate(invoice.exp[0].inv[0].sbdt);
			}else{
				invoice.shippingBillDate = '';
			}
		}
		if(('<%=MasterGSTConstants.ISD%>' == invoice.invtype && invoice.isd && invoice.isd.length > 0 && invoice.isd[0].doclist && invoice.isd[0].doclist.length > 0) || ('<%=MasterGSTConstants.ISD%>' == invoice.invtype && invoice.isd && invoice.isd.length > 0 && invoice.isd[0].doclist && invoice.isd[0].doclist.length > 0)) {
			invoice.isdDocty = invoice.isd[0].doclist[0].isdDocty;
			invoice.documentNumber = invoice.isd[0].doclist[0].docnum;
			if(invoice.isd[0].doclist[0].docdt != null && invoice.isd[0].doclist[0].docdt != undefined){
				invoice.documentDate = formatDate(invoice.isd[0].doclist[0].docdt);
			}else{
				invoice.documentDate = '';
			}
		}
		if('<%=MasterGSTConstants.IMP_GOODS%>' == invoice.invtype && invoice.impGoods && invoice.impGoods.length > 0) {
			invoice.isSez = invoice.impGoods[0].isSez;
			invoice.stin = invoice.impGoods[0].stin;
			invoice.impBillNumber = invoice.impGoods[0].boeNum;
			if(invoice.impGoods[0].boeDt != null){
				invoice.impBillDate = formatDate(invoice.impGoods[0].boeDt);
			}else{
				invoice.impBillDate = '';
			}
			invoice.boeVal = invoice.impGoods[0].boeVal;
			invoice.impPortcode = invoice.impGoods[0].portCode;
		}
		if(invoice.advOriginalInvoiceNumber == null) {
			invoice.advOriginalInvoiceNumber = 0.00;
		}
		if(invoice.advPCustname == null) {
			invoice.advPCustname = "";
		}
		if(invoice.advPInvamt == null) {
			invoice.advPInvamt = 0.00;
		}
		if(invoice.advPIgstamt == null) {
			invoice.advPIgstamt = 0.00;
		}
		if(invoice.advPCgstamt == null) {
			invoice.advPCgstamt = 0.00;
		}
		if(invoice.advPSgstamt == null) {
			invoice.advPSgstamt = 0.00;
		}
		if(invoice.VehiclListDetails){
			if(VehiclListDetails.enteredDate != null){
				var eDate = new Date(VehiclListDetails.enteredDate);var day = eDate.getDate() + "";var month = (eDate.getMonth() + 1) + "";var year = eDate.getFullYear() + "";
				day = checkZero(day);month = checkZero(month);year = checkZero(year);
				VehiclListDetails.enteredDate = day + "/" + month + "/" + year;
			}else{invoice.enteredDate = "";}
			if(VehiclListDetails.transDocDate != null){
				var tdDate = new Date(VehiclListDetails.transDocDate);var day = tdDate.getDate() + "";var month = (tdDate.getMonth() + 1) + "";var year = tdDate.getFullYear() + "";
				day = checkZero(day);month = checkZero(month);year = checkZero(year);
				VehiclListDetails.transDocDate = day + "/" + month + "/" + year;
			}else{invoice.transDocDate = "";}
			invoice.VehiclListDetails.forEach(function(VehiclListDetails) {
				VehiclListDetails.enteredDate = VehiclListDetails.enteredDate;
				VehiclListDetails.transDocDate = VehiclListDetails.transDocDate;
				VehiclListDetails.vehicleNo = VehiclListDetails.vehicleNo;
				VehiclListDetails.fromPlace = VehiclListDetails.fromPlace;
				VehiclListDetails.fromState = VehiclListDetails.fromState;
				VehiclListDetails.tripshtNo = VehiclListDetails.tripshtNo;
				VehiclListDetails.userGSTINTransin = VehiclListDetails.userGSTINTransin;
				VehiclListDetails.transMode = VehiclListDetails.transMode;
			if(VehiclListDetails.transDocNo){VehiclListDetails.transDocNo = VehiclListDetails.transDocNo;}
			VehiclListDetails.groupNo = VehiclListDetails.groupNo;
			});
		}
		if(invoice.items) {
			invoice.items.forEach(function(item) {
				if(item.rate == null) {
					if(item.igstrate) {
						item.rate = item.igstrate;
					} else if(item.cgstrate) {
						item.rate = 2*item.cgstrate;
					}
				}
				if(item.hsn) {
					item.code = item.hsn;
					if(item.hsn.indexOf(':') > 0) {
						item.hsn=item.hsn.substring(0,item.hsn.indexOf(' :'));
					}
				}
				if(item.igstamount) {
					if(invoice.gstStatus != 'CANCELLED'){
						if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
							if(invoice.cdn[0].nt[0].ntty == 'D'){
								totalIGST += item.igstamount;
								totalIGST1 += item.igstamount;
							}else{
								totalIGST-=item.igstamount;
								totalIGST1 -=item.igstamount;
							}
						}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
							if(invoice.cdnr[0].nt[0].ntty == 'C'){
								totalIGST -= item.igstamount;
								totalIGST1 -= item.igstamount;
							}else{
								totalIGST+=item.igstamount;
								totalIGST1 +=item.igstamount;
							}
						}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
							if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
								if(invoice.cdnur[0].ntty == 'D'){
									totalIGST+=item.igstamount;
									totalIGST1 +=item.igstamount;
								}else{
									totalIGST-=item.igstamount;
									totalIGST1 -=item.igstamount;
								}
							}else{
								if(invoice.cdnur[0].ntty == 'C'){
									totalIGST-=item.igstamount;
									totalIGST1 -=item.igstamount;
								}else{
									totalIGST+=item.igstamount;
									totalIGST1 +=item.igstamount;
								}
							}
						}else{
							totalIGST+=item.igstamount;
							totalIGST1 +=item.igstamount;
						}
					}
				} else {
					item.igstamount = 0.00;
				}
				if(item.cgstamount) {
					if(invoice.gstStatus != 'CANCELLED'){
						if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
							if(invoice.cdn[0].nt[0].ntty == 'D'){
								totalCGST+=item.cgstamount;
								totalCGST1 +=item.cgstamount;
							}else{
								totalCGST-=item.cgstamount;
								totalCGST1 -=item.cgstamount;
							}
						}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
							if(invoice.cdnr[0].nt[0].ntty == 'C'){
								totalCGST-=item.cgstamount;
								totalCGST1 -=item.cgstamount;
							}else{
								totalCGST+=item.cgstamount;
								totalCGST1 +=item.cgstamount;
							}
						}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
							if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
								if(invoice.cdnur[0].ntty == 'D'){
									totalCGST+=item.cgstamount;
									totalCGST1 +=item.cgstamount;
								}else{
									totalCGST-=item.cgstamount;
									totalCGST1 -=item.cgstamount;
								}
							}else{
								if(invoice.cdnur[0].ntty == 'C'){
									totalCGST-=item.cgstamount;
									totalCGST1 -=item.cgstamount;
								}else{
									totalCGST+=item.cgstamount;
									totalCGST1 +=item.cgstamount;
								}
							}
						}else{
							totalCGST+=item.cgstamount;
							totalCGST1 +=item.cgstamount;
						}
					}
				} else {
					item.cgstamount = 0.00;
				}
				if(item.sgstamount) {
					if(invoice.gstStatus != 'CANCELLED'){
						if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
								if(invoice.cdn[0].nt[0].ntty == 'D'){
									totalSGST+=item.sgstamount;
									totalSGST1 +=item.sgstamount;
								}else{
									totalSGST-=item.sgstamount;
									totalSGST1 -=item.sgstamount;
								}
							}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
								if(invoice.cdnr[0].nt[0].ntty == 'C'){
									totalSGST -= item.sgstamount;
									totalSGST1 -=item.sgstamount;
								}else{
									totalSGST+=item.sgstamount;
									totalSGST1 +=item.sgstamount;
								}
							}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
								if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
								if(invoice.cdnur[0].ntty == 'D'){
									totalSGST+=item.sgstamount;
									totalSGST1 +=item.sgstamount;
								}else{
									totalSGST+=item.sgstamount;
									totalSGST1 +=item.sgstamount;
								}
							}else{
								if(invoice.cdnur[0].ntty == 'C'){
									totalSGST-=item.sgstamount;
									totalSGST1 -=item.sgstamount;
								}else{
									totalSGST+=item.sgstamount;
									totalSGST1 +=item.sgstamount;
								}
							}
							}else{
								totalSGST+=item.sgstamount;
								totalSGST1 +=item.sgstamount;
							}
					}
				} else {
					item.sgstamount = 0.00;
				}
				if(item.cessamount) {
					if(invoice.gstStatus != 'CANCELLED'){
							if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
								if(invoice.cdn[0].nt[0].ntty == 'D'){
									totalCESS+=item.cessamount;
									totalCESS1 +=item.cessamount;
								}else{
									totalCESS-=item.cessamount;
									totalCESS1 -=item.cessamount;
								}
							}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
								if(invoice.cdnr[0].nt[0].ntty == 'C'){
									totalCESS-=item.cessamount;
									totalCESS1 -=item.cessamount;
								}else{
									totalCESS+=item.cessamount;
									totalCESS1 +=item.cessamount;
								}
							}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
								if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
									if(invoice.cdnur[0].ntty == 'D'){
										totalCESS+=item.cessamount;
										totalCESS1 +=item.cessamount;
									}else{
										totalCESS-=item.cessamount;
										totalCESS1 -=item.cessamount;
									}
								}else{
									if(invoice.cdnur[0].ntty == 'C'){
										totalCESS-=item.cessamount;
										totalCESS1 -=item.cessamount;
									}else{
										totalCESS+=item.cessamount;
										totalCESS1 +=item.cessamount;
									}
								}
							}else{
								totalCESS+=item.cessamount;
								totalCESS1 +=item.cessamount;
							}
					}
				} else {
					item.cessamount = 0.00;
				}
				if(item.exmepted != null && item.quantity != null){
					if(invoice.gstStatus != 'CANCELLED'){
						
						if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
							if(invoice.cdnr[0].nt[0].ntty == 'C'){
								totalExemptedValue = totalExemptedValue-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
								totalExempted == totalExempted-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
							}else{
								totalExemptedValue += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
								totalExempted += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
							}
						}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
							if(invoice.cdnur[0].ntty == 'C'){
								totalExemptedValue = totalExemptedValue-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
								totalExempted == totalExempted-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
							}else{
								totalExemptedValue += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
								totalExempted += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
							}
						}else{
							totalExemptedValue += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
							totalExempted += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
						}
						
						
					}
				}
				
				if(item.discount == null) {
					item.discount = 0.00;
				}
				if(item.advreceived == null) {
					item.advreceived = 0.00;
				}
				if(item.advReceiptNo == null){
					item.advReceiptNo = "";
				}
				if(item.advReceiptDate == null){
					item.advReceiptDate = "";
				}
				if(item.advStateName == null){
					item.advStateName = "";
				}
				if(item.advReceivedAmount == null){
					item.advReceivedAmount = 0.00;
				}
				if(item.advAdjustableAmount == null){
					item.advAdjustableAmount = 0.00;
				}
				if(item.advadjustedAmount == null){
					item.advadjustedAmount = 0.00;
				}
				if(item.igstavltax) {
					totalinvitc+= item.igstavltax;
					if(invoice.gstStatus != 'CANCELLED'){
					totalITCIGST+=item.igstavltax;
					totalITCIGST1 +=item.igstavltax;
					}
				} else {
					item.igstavltax = 0.00;
				}
				if(item.cgstavltax) {
					totalinvitc+= item.cgstavltax;
					if(invoice.gstStatus != 'CANCELLED'){
					totalITCCGST+=item.cgstavltax;
					totalITCCGST1 +=item.cgstavltax;
					}
				} else {
					item.cgstavltax = 0.00;
				}
				if(item.sgstavltax) {
					totalinvitc+= item.sgstavltax;
					if(invoice.gstStatus != 'CANCELLED'){
					totalITCSGST+=item.sgstavltax;
					totalITCSGST1 +=item.sgstavltax;
					}
				} else {
					item.sgstavltax = 0.00;
				}
				if(item.cessavltax) {
					totalinvitc+= item.cessavltax;
					if(invoice.gstStatus != 'CANCELLED'){
					totalITCCESS+=item.cessavltax;
					totalITCCESS1 +=item.cessavltax;
					}
				} else {
					item.cessavltax = 0.00;
				}
				if(item.type) {
				} else {
					item.type = '';
				}
			});
		}
		invoice.totalitc = totalinvitc;
		if(invoice.totalitc) {
			if(invoice.gstStatus != 'CANCELLED'){
				if(invoice.invtype != 'Advances' && invoice.invtype != 'Adv. Adjustments' && invoice.invtype != 'ISD' && invoice.invtype != 'ITC Reversal' && invoice.invtype != 'Nil Supplies'){
					totalITC+=invoice.totalitc;
				}
			}
		} else {
			invoice.totalitc = 0.00;
		}
		invoice.igstamount = totalIGST1;
		invoice.cgstamount = totalCGST1;
		invoice.sgstamount = totalSGST1;
		invoice.cessamount = totalCESS1;
		invoice.igstavltax = totalITCIGST1;
		invoice.cgstavltax = totalITCCGST1;
		invoice.sgstavltax = totalITCSGST1;
		invoice.cessavltax = totalITCCESS1;
		invoice.totalExempted = totalExempted; 
		return invoice;
	}
	function excelreport(){
		$('a.btn.btn-blue.excel').addClass('btn-loader');
			window.setTimeout(function(){
			$('a.btn.btn-blue.excel').removeClass("btn-loader");
			}, 2000);
	}
	function configauthentication(){
		$('#authStatus').html('<span style="color:red;">Expired</span><a href="#" style="color:green;"> <span id="inactivebtn">Authenticating...</span></a>');
		$.ajax({
			type: "GET",
			url: "${contextPath}/authconfigdetails/${id}/${name}/${usertype}/${client.id}/${returntype}/${month}/${year}",
			async: true,
			cache: false,
			contentType: 'application/json',
			success : function(response) {
				if(response.connStaus == "Active"){
					$('#authStatus').html('Active').css("color","green");
				}else{
					$('#ebill_msg').html('<span style="color:red;">Error</span> : Please Check Your <a href="${contextPath}/cp_upload/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${month}"/>/<c:out value="${year}"/>?type=ewaybillconfig">Configuration</a>');
					$('#authStatus').html('');
				}	
			},
			error : function(e, status, error) {
				if(e.responseText) {
					errorNotification(e.responseText);
				}
			}
		});
	}
	function itcclaim(){
		var p = $('.itc_amount').val();
		if(p<=100){
			
		}else{
			$('.itc_amount').val('100');
		}		
	}
	function gstr3bmonthChange(id){
		var claimdate = $('#'+id).val().split("-");
		$('.'+id).html(mnths[parseInt(claimdate[1])-1]);
		
	}
	
	$(document).on('mouseover','#tot_tax', function (event) {
		$(this).find('#tax_tot_drop1').css({"display":"block","background-color":"#fff","border":"1px solid #f5f5f5","padding":"10px","position":"absolute","z-index":"1","box-shadow":"0px 0px 5px 0px #e5e5e5","width":"10%","margin-top":"5px"});
	}).on('mouseleave','#tot_tax',  function(){
	    	$(this).next().css("display","none");
	    	$(this).find("#tax_tot_drop1").stop(true, true).delay(100).fadeOut(300);
	   }); 
	</script>