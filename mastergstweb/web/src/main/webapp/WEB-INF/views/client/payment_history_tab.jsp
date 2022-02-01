<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<c:set var="paymentsTabName" value='<%= (String)request.getParameter("paymentsTabName") %>'/>
<div class="col-md-12 col-sm-12 customtable p-0 mt-3">
	<div>
		<div class="normaltable meterialform" id="monthlynormaltable">
			<div class="filter ${paymentsTabName}">
				<div class="noramltable-row"><div class="noramltable-row-hdr">Filter</div>
					<div class="noramltable-row-desc">
						<div class="sfilter"><span id="divFilters${paymentsTabName}"></span>
							<span class="btn-remove-tag" onClick="clearPaymentsFilters('${paymentsTabName}')">Clear All<span data-role="remove"></span></span>
						</div>
					</div>
				</div>
			</div>
			<div class="noramltable-row">
				<div class="noramltable-row-hdr">Search Filter</div>
				<div class="noramltable-row-desc">
					<select id="multiselect${paymentsTabName}1" class="multiselect-ui form-control" multiple="multiple"><option value="2021-2022">2021-2022</option><option value="2020-2021">2020-2021</option><option value="2019-2020">2019-2020</option><option value="2018-2019">2018-2019</option><option value="2017-2018">2017-2018</option></select>
					<select id="multiselect${paymentsTabName}2" class="multiselect-ui form-control" multiple="multiple"><option value="1">January</option><option value="2">February</option><option value="3">March</option><option value="4">April</option><option value="5">May</option><option value="6">June</option><option value="7">July</option><option value="8">August</option><option value="9">September</option><option value="10">October</option><option value="11">November</option><option value="12">December</option></select>
					<select id="multiselect${paymentsTabName}3" class="multiselect-ui form-control" multiple="multiple"></select>
					<select id="multiselect${paymentsTabName}4" class="multiselect-ui form-control" multiple="multiple"></select>
					<%-- <select id="multiselect${paymentsTabName}5" class="multiselect-ui form-control" multiple="multiple"><option value="cash">Cash</option><option value="Bank">Bank</option><option value="TDS-IT">TDS-IT</option><option value="TDS-GST">TDS-GST</option><option value="Discount">Discount</option><option value="Others">Others</option></select> --%>
				</div>
			</div>
			<div class="noramltable-row">
				<div class="noramltable-row-hdr">Filter Summary</div>
				<div class="noramltable-row-desc">
					<div class="normaltable-col hdr">
						<c:if test="${paymentsTabName eq 'salesTab'}">Total No.Of Receipts</c:if>
						<c:if test="${paymentsTabName eq 'purchasesTab'}">Total No.Of Payments</c:if>
						<div class="normaltable-col-txt" id="idCount${paymentsTabName}"></div>
					</div>
					<div class="normaltable-col hdr">
						<c:if test="${paymentsTabName eq 'salesTab'}">Total Amount Receivable</c:if>
						<c:if test="${paymentsTabName eq 'purchasesTab'}">Total Amount Payable</c:if>
						<div class="normaltable-col-txt" id="idTotalInvoiceVal${paymentsTabName}"></div>
					</div>
					<div class="normaltable-col hdr">
						<c:if test="${paymentsTabName eq 'salesTab'}">Total Amount Received</c:if>
						<c:if test="${paymentsTabName eq 'purchasesTab'}">Total Amount Paid</c:if>
						<div class="normaltable-col-txt" id="idTotalPaidAmt${paymentsTabName}"></div>
					</div>
					<div class="normaltable-col hdr">
						<c:if test="${paymentsTabName eq 'salesTab'}">Total Amount To Be Received</c:if>
						<c:if test="${paymentsTabName eq 'purchasesTab'}">Total Amount To Be Paid</c:if>
						<div class="normaltable-col-txt" id="idTotalPending${paymentsTabName}"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="noramltable-row" style="background-color:white;padding: 8px;margin-bottom: 10px;border: 1px solid lightgray;border-radius: 4px;">
				<div class="noramltable-row-hdr" style="width: 12%;font-size: 14px;">Amount Distributed as</div>
				<div class="noramltable-row-desc">
					<div class="normaltable-col hdr">
						Total Cash Amount<div class="normaltable-col-txt" id="idCashAmt${paymentsTabName}"></div>
					</div>
					<div class="normaltable-col hdr">
						Total Bank Amount<div class="normaltable-col-txt" id="idBankAmt${paymentsTabName}"></div>
					</div>
					<div class="normaltable-col hdr">
						Total TDS Amount<div class="normaltable-col-txt" id="idTDSITAmt${paymentsTabName}"></div>
					</div>
					<div class="normaltable-col hdr">
						Total TDS-GST Amount<div class="normaltable-col-txt" id="idTDSGSTAmt${paymentsTabName}"></div>
					</div>
					<div class="normaltable-col hdr">
						Total Discount Amount<div class="normaltable-col-txt" id="idDiscountAmt${paymentsTabName}"></div>
					</div>
					<div class="normaltable-col hdr">
						Total Others Amount<div class="normaltable-col-txt" id="idOthersAmt${paymentsTabName}"></div>
					</div>
				</div>
			</div>
    <table id='paymentstable_${paymentsTabName}' class="row-border dataTable meterialform" cellspacing="0" width="100%">
		<thead>
			<!-- <tr><th class="text-center" width=3%>S.no</th> -->
			<th class="text-center" width=70px>Date</th>
			<th class="text-center">
				<c:if test="${paymentsTabName eq 'salesTab'}">Receipt Id</c:if>
				<c:if test="${paymentsTabName eq 'purchasesTab'}">Payment Id</c:if>
			</th>
			<th class="text-center">Invoice Number</th>
			<th class="text-center">
				<c:if test="${paymentsTabName eq 'salesTab'}">Customer</c:if>
				<c:if test="${paymentsTabName eq 'purchasesTab'}">Supplier</c:if>
			</th>
			<th class="text-center">GSTIN</th>
			<th class="text-center">
				<c:if test="${paymentsTabName eq 'salesTab'}">Amount Received</c:if>
				<c:if test="${paymentsTabName eq 'purchasesTab'}">Amount Paid</c:if>
			</th>
			<!-- <th class="text-center">Payment Mode</th> -->
			<th class="text-center">Reference No</th><th class="text-center">Action</th></tr>	
		</thead>
		<tbody id='paymentsalestable'></tbody>
	</table>
</div>
<div id="paymentsprogress-bar_${paymentsTabName}" class="d-none"  style="color:red;font-size:20px;position:absolute;z-index:99;top:37%;left: 46%;">
	<img src="${contextPath}/static/mastergst/images/eclipse-spinner.gif" alt="spinner-img" style="width: 150px;height: 150px;"/>
</div>
<script type="text/javascript">
	var is${paymentsTabName}Loaded = false;
	$('#${paymentsTabName}').on('click',function() {
		var finndate = new Date();
		var finyear = finndate.getFullYear();
		var finmnth = finndate.getMonth()+1;
		var financialyear = "";
		if(finmnth < 4){
			var prevyear = finyear-1;
			financialyear = prevyear+"-"+finyear;
		}else{
			var nxtyear = finyear+1;
			financialyear = finyear+"-"+nxtyear;
		}
		if(!is${paymentsTabName}Loaded){
			loadPaymentInvoices('${id}', '${client.id}', '${paymentsTabName}',financialyear);
			initiateCallBacksForMultiSelect('${paymentsTabName}');
			loadPaymentSupport('${id}', '${client.id}', '${paymentsTabName}' ,loadPaymentCustomersInDropdown);
			initializeRemoveAppliedFilters('${paymentsTabName}');
			is${paymentsTabName}Loaded = true;
		}else{
			paymentsFilters('${paymentsTabName}',financialyear);
		}
		defaultApplyFinancialYear('${paymentsTabName}',financialyear);
	});
</script>