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
<c:set var="reconcileType" value="<%=MasterGSTConstants.MONTHLY%>"/>
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
<script src="${contextPath}/static/mastergst/js/client/reconcile.js" type="text/javascript"></script>
<span style="font-size:13px; color:#0dce2f; font-weight:bold;margin-left:3px;float: right;">
	<c:if test="${empty client.reconcileDate }"><span style="color:red;font-size:13px">You Haven't Reconciled Yet,</span></c:if>
	<c:if test="${not empty client.reconcileDate }">Last Reconciled on: ${client.reconcileDate}</c:if> 
</span>
<h4 class="hdrtitle">
	<span class="summary_retperiod"><a href="#" data-toggle="modal" data-target="#reconcilemodal" style="color: #404144;text-decoration: underline;">Reconcile Summary</a></span>
	<label> , Reconciled By</label>
	<select class="invoiceview" style="font-size: 16px;margin-left: 5px;" onchange="invoiceviewByRTrDate('${id}',this.value,'Purchase Register','Purchase_Register','${client.address}')">
		<option value="Invoice Date">Invoice Date</option>
		<option value="Transaction Date">Transaction Date</option>
	</select>
	<!-- <a href="#" onClick="reconcileMonthlyGstr2a()" class="btn btn-blue reconbtn">Reconcile Now</a> -->
	
	<c:if test="${reconcileCounts eq null}">
		<a href="#" onClick="reconcileMonthlyGstr2a()" class="btn btn-blue reconbtn reconcile_btn" style="color: #404144;;margin-left:10px;font-size: 12px;float: right; position: absolute; padding: 2px 6px 0px;">Reconcile Now</a>
	</c:if>
	<c:if test="${not empty reconcileCounts}">
		<!-- style="margin-left: 116px;" -->
		<a href="#" onClick="reconcileMonthlyGstr2a()" class="btn btn-blue reconbtn reconcile_btn d-none" style="color: #404144;;margin-left:10px;font-size: 12px;float: right; position: absolute; padding: 2px 6px 0px;">Reconcile Now</a>
		<span style="font-size: 14px;" class="rccn"><img src="${contextPath}/static/mastergst/images/master/gears.gif" width="40px" style="margin-left:6px;margin-right:6px;"/> <a href="#" onclick="verifyReconsilation('${client.id}')">click here </a>for status</span>
	</c:if>
	<span style="font-size: 14px;" class="processverification d-none"><c:out value="${reconcileCounts}"/>  <img src="${contextPath}/static/mastergst/images/master/gears.gif" width="40px" style="margin-left:6px;margin-right:6px;"/> <a href="#" onclick="verifyReconsilation('${client.id}')">click here </a>for status</span>
	
	<a href="${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=yearlyRecocileReport" class="btn btn-blue yearreconbtn">Yearly Reconcile</a>
	<div class="dropdown" style="color: #404144;;margin-left:20px;font-size: 17px;float:right">Reconcile Rules
		<div class="dropdown-content" style="z-index:3">
			<span class="arrow-right"></span>
			<div>Allowed Tax/Invoice Amount Difference for Reconcile
				<span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span>
				<span>
					<c:choose><c:when test="${not empty clientConfig.reconcileDiff}">${clientConfig.reconcileDiff}  </c:when><c:otherwise>0.00</c:otherwise></c:choose> Rupees
				</span>
				<br/>Allowed days for Invoice Date Difference for Reconcile<span class="colon" style="margin-left: 10px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.allowedDays}"><fmt:formatNumber type="number" pattern="###" value="${clientConfig.allowedDays}" />  </c:when><c:otherwise>0</c:otherwise></c:choose> Days</span>
				<br/>Considers as Matched if Invoice Number matches anywhere in the Invoice Number<span class="colon" style="margin-left: 10px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableInvoiceMatch}"><c:if test="${clientConfig.enableInvoiceMatch eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableInvoiceMatch eq false}"><span style="color:red">No</span></c:if> </c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
				<br/>Ignore "/"(Backward Slash)<span class="colon" style="margin-left: 10px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableIgnoreSlash}"><c:if test="${clientConfig.enableIgnoreSlash eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableIgnoreSlash eq false}"><span style="color:red">No</span></c:if></c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
				<br/>Ignore "-"(Hyphen)<span class="colon" style="margin-left: 51px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableIgnoreHyphen}"><c:if test="${clientConfig.enableIgnoreHyphen eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableIgnoreHyphen eq false}"><span style="color:red">No</span></c:if></c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
				<br/>Ignore "0"(Zero/O)<span class="colon" style="margin-left: 53px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableIgnoreZero}"><c:if test="${clientConfig.enableIgnoreZero eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableIgnoreZero eq false}"><span style="color:red">No</span></c:if></c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
				<br/>Ignore "I"(Capital i)<span class="colon" style="margin-left: 50px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableIgnoreI}"><c:if test="${clientConfig.enableIgnoreI eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableIgnoreI eq false}"><span style="color:red">No</span></c:if></c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
				<br/>Ignore "l"(Small l)<span class="colon" style="margin-left: 53px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableIgnoreL}"><c:if test="${clientConfig.enableIgnoreL eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableIgnoreL eq false}"><span style="color:red">No</span></c:if></c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
				<a href="#" onClick="moreReconcileConfig()" >Click here for more configurations....</a>
			</div>
		</div>
	</div>
</h4>
<%-- <jsp:include page="/WEB-INF/views/client/updateMismatchfilter.jsp"/> --%>

<%-- <jsp:include page="/WEB-INF/views/client/mismatchfilter.jsp" /> --%>
<div class="normaltable meterialform">
	<div class="filter">
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Filter</div>
			<div class="noramltable-row-desc">
				<div class="mismatchfilter">
					<span id="divMMFilters"></span>
					<span class="btn-remove-tag" onclick="clearMMFilters()">Clear All<span data-role="remove"></span></span>
				</div>
			</div>
		</div>
	</div>
	<div class="noramltable-row">
		<div class="noramltable-row-hdr">Search &nbsp; Filter</div>
			<div class="noramltable-row-desc">
				<select id="MMmultiselect1" class="multiselect-ui form-control" multiple="multiple">
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
				<select id="MMmultiselect2" class="multiselect-ui form-control" multiple="multiple">
					<option value="B2B">B2B Invoices</option>
					<option value="B2BA">B2BA Invoices</option>
					 <option value="Debit Note">Debit Note</option>
					<option value="Credit Note">Credit Note</option>
					<!--<option value="Credit/Debit Notes">Credit/Debit Notes</option>-->
					<option value="CDNA">CDNA</option>
					<option value="<%=MasterGSTConstants.IMP_GOODS%>"><%=MasterGSTConstants.IMP_GOODS%></option>
					<option value="<%=MasterGSTConstants.IMP_SERVICES%>"><%=MasterGSTConstants.IMP_SERVICES%></option>
					<option value="<%=MasterGSTConstants.ISD%>">ISD</option>
				</select>
				<span class="multiselectuserlist">
					<select id="MMmultiselect3" class="multiselect-ui form-control" multiple="multiple">
					</select>
				</span>
				<span class="missmultiselectuserlist">
				<select id="MMmultiselect4" class="multiselect-ui form-control" multiple="multiple">
				</select>
				</span>
				<select id="MMmultiselect5" class="multiselect-ui form-control" multiple="multiple">
					<c:forEach items="${client.branches}" var="branch">
						<option value="${branch.name}">${branch.name}</option>
					</c:forEach>
				</select>
				<select id="MMmultiselect6" class="multiselect-ui form-control" multiple="multiple">
					<c:forEach items="${client.verticals}" var="vertical">
						<option value="${vertical.name}">${vertical.name}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Filter Summary</div>
			<div class="noramltable-row-desc">
				<div class="normaltable-col hdr">Total Invoices
					<div class="normaltable-col-txt" id="idMMCount"></div>
				</div>
				<div class="normaltable-col hdr">Total Taxable Value
					<div class="normaltable-col-txt" id="idMMTaxableVal"></div>
				</div>
				<div class="normaltable-col hdr">Total Tax Value
					<div class="normaltable-col-txt" id="idMMTaxVal"></div>
				</div>
				<div class="normaltable-col hdr">Total IGST
					<div class="normaltable-col-txt" id="idMMIGST"></div>
				</div>
				<div class="normaltable-col hdr">Total CGST
					<div class="normaltable-col-txt" id="idMMCGST"></div>
				</div>
				<div class="normaltable-col hdr">Total SGST
					<div class="normaltable-col-txt" id="idMMSGST"></div>
				</div>
				<div class="normaltable-col hdr">Total CESS
					<div class="normaltable-col-txt" id="idMMCESS"></div>
				</div>
			</div>
		</div>
	</div>
<h4 class="hdrtitle"style="position: relative;margin-top: -15px!important; z-index:2;">
<!-- <a href="#" id="btnMisMatchReject" class="btn btn-red disable" onClick="updateMismatchDataNew(this,false)">Reject GSTR2A invoice(s)</a> -->
<a href="#" id="btnMisMatchAccept" class="btn btn-acceptgreen disable permissionGSTR2-Accept_GSTR2A" onClick="updateMismatchDataNew(this,true)">Accept GSTR2A invoice(s)</a>
<div class="dropdown pull-right permissionExcel_Download_In_Books_And_Returns-Reconcile" style="margin-left: 10px;">
	<div class="split-button-menu-dropdown reportmenu">
		<button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button>
		<button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button>
		<div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 190px!important;"><a class="dropdown-item" href="${contextPath}/dwnldReconsilexls/${id}/${client.id}/<%=MasterGSTConstants.GSTR2%>/${month}/${year}?dwnldtype=invoicewise">INVOICE WISE DOWNLOAD</a>
		<a class="dropdown-item" href="${contextPath}/dwnldReconsilexls/${id}/${client.id}/<%=MasterGSTConstants.GSTR2%>/${month}/${year}?dwnldtype=alldetailswise">All DETAILS DOWNLOAD</a></div>
	</div>
</div>
<a href="#" class="btn btn-blue sendmessage disable" data-toggle="modal" data-target="#sendMessageModal">Send Message</a> <a href="#" class="btn btn-blue reconModal mannualMatching disable" data-toggle="modal" data-target="#reconModal" onclick="mannualMatchingInv('${client.id}','monthly')">Manual Match</a></h4>
<div class="customtable db-ca-view tabtable2 reconciletablee">
	<div class="customtable db-ca-view">
		<span class="mt-1 select_msg" style="position:absolute;left:60%;color:#5769bb;"></span>
		<table id="reconsileDbTable" class="row-border dataTable meterialform" cellspacing="0" width="100%">
			<thead>
				<tr><th class="text-center"><div class="checkbox"> <label><input type="checkbox" id="checkMismatch" onClick="updateMainMisMatchSelection(this)"/><i class="helper"></i></label></div> </th>
				<th> </th>
				<th> Type</th>
				<th class="text-center">Suppliers</th><th>Ret.Period</th>
				<th class="text-center">Invoice Number</th>
				<th class="text-center">Invoice Date</th><th class="text-center">GSTN No</th>
				<th class="text-center">Invoice Value</th><th class="text-center">Taxable Amt</th>
				<th class="text-center">Total Tax</th>
				<th class="text-center">Status</th><th class="text-center">Notes</th></tr>
			</thead>
			<tbody id="idMisMatchBody"></tbody>
		</table>
	</div>
</div>
<div class="modal fade" id="reconcileprocessModal" tabindex="-1" role="dialog" aria-labelledby="reconcileprocessModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document" style="max-width: unset; width: 50%;">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Reconciliation Status</h3>
        </div>
        <div class=" pl-4 pt-4 pr-4">
        	<div class="reconprogress">
        	<p><span id="verificationType"> </span>&nbsp;Reconciliation is in Process, Initiated by :&nbsp; <span id="reconinitiatedby"></span></p>
        	
        	<h5>Reconciliation Status</h5>
          <p><span id="gstr2areconrtntype"></span> : <span id="processInfo" style="font-weight:bold"></span> invoices out of <span id="verificationInfo"  style="font-weight:bold"></span></p>
          <p class="prinfo" style="display:none;">Purchase Register : <span id="processPrInfo" style="font-weight:bold"></span> invoices out of <span id="verificationPrInfo"  style="font-weight:bold"></span></p>
          </div>
          <div class="reconcompleted">
          	<p><span id="reconcomplete"></span></p>
          </div>
        </div>
      </div>
      <div class="modal-footer text-center" style="display:block">
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
<script type="text/javascript">
$(function(){
	if(otherconfigdetails.enableTransDate == true){
		$('.invoiceview').val('Transaction Date');
	}else{
		$('.invoiceview').val('Invoice Date');
	}
	var isreconcileTabLoaded = false;
	$('#reconcileTab').on('click',function() {
		if(!isreconcileTabLoaded){
			loadMismatchUsersByClient('${id}', '${client.id}', loadMismatchUsersInDropDown);
			loadMismatchInvoiceSupport('${id}', '${client.id}', '${month}', '${year}', '${reconcileType}', loadMismatchCustomersInDropdown);
			loadMismatchInvTable('${id}', '${client.id}', '${month}', '${year}', '${reconcileType}');
			initiateCallBacksForMultiSelectMismatch();
			initializeRemoveAppliedFiltersMismatch();
			loadReconcileSummary('${id}', '${client.id}', '${month}', '${year}', '${reconcileType}');
			isreconcileTabLoaded = true;
		}
	});
	var rc = "${reconcileCounts}";
	if(rc != null && rc != ""){
		executeQuery('${client.id}');
	}
});

function reconcileMonthlyGstr2a(){
	$('.reconcile_btn').addClass("btn-loader");
	$('.reconcile_btn').html("Reconciling...");
	$.ajax({
		url : '${contextPath}/verifySubscription/${id}/${client.id}',
		async: false,
		cache: false,
		contentType: 'application/json',
		success : function(response) {
			$('.reconcile_btn,.2breconbtn').addClass("d-none");
			$('.processverification').removeClass("d-none");
			if(response == 'verified') {
				//var urlStr = '${contextPath}/reconcileinv/${id}/${fullname}/${usertype}/${client.id}/GSTR2A/${month}/${year}?reconcileType=${reconcileType}';
				var urlStr = '${contextPath}/performReconcile/${id}/${fullname}/${usertype}/${client.id}/GSTR2A/${month}/${year}?reconcileType=${reconcileType}';
				$.ajax({
					url: urlStr,
					async: true,
					cache: false,
					dataType:"json",
					contentType: 'application/json',
					success : function(response) {
					}
				});
				setTimeout(function() { executeQuery('${client.id}'); }, 5000);
			}else{
				 $('.processverification,.rccn').addClass("d-none");
		    	  $('.reconcile_btn').removeClass("btn-loader");
		    	  $('.reconcile_btn').html("Reconcile Now");
		    	  $('.reconcile_btn').removeClass("d-none");
				errorNotification(response);
			}
		},error : function(error){
		}
	});	
}
</script>
