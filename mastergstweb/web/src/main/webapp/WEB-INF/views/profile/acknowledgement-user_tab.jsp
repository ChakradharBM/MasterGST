<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<c:set var="ackTabName" value='<%= (String)request.getParameter("ackTabName") %>'/>
<c:if test="${ackTabName ne 'savedCredentialsTab'}">
<div id="clients_group" class="clients_group pull-left p-2">
	<div class="form-group mb-0">
		<label for="multeselectclient">GSTN/Business Names :</label>
		<select id="multeselectclient_${ackTabName}" class="multeselectclient_${ackTabName} multiselect-ui form-control multeselectclient" multiple="multiple">
			<c:forEach items="${clients}" var="client">
				<option value="${client.id}"><c:out value="${client.businessname}"/>-<c:out value="${client.gstnnumber}"/></option>
			</c:forEach>
		</select>
		<span id="clientErrMsg_${ackTabName}" style="display:none;color:red;font-size:14px">Please select at least one client/business name</span>
	</div>
</div>
<div class="dropdown-container pull-right mt-2">
	<label for="return_type">View By:</label>
    <select class="" id="viewReturnType_${ackTabName}" style="border-radius: 4px;padding: 1px 10px;border-color: lightgrey;text-align: left;padding-left: 0px;" onchange="returntypeView('${ackTabName}')">
        <option value="Monthly">Monthly</option>
        <option value="Yearly">Yearly</option>
        <option value="Custom">Custom</option>
    </select>
    <div class="monthly_container_${ackTabName} ml-2 mr-2" style="display:inline-block">
        <span>Invoice Period:</span>
        <input type="text" class="form-control" id="monthlyvalue_${ackTabName}" value="03-2020" style="padding: 3px;width: 95px;display: inline-block;padding: 4px 10px;padding-left: 2px;"><i class="fa fa-sort-desc" id="date-drop" style="position: absolute;margin-left: -16px;margin-top: 2px;"></i>
    </div>
    <div class="yearly_container_${ackTabName} ml-2 mr-2" style="display:none">
        <span>Invoice Period:</span>
        <select class="" id="yearlyvalue_${ackTabName}" style="border-radius: 4px;padding: 1px 10px;border-color: lightgrey;text-align: left;padding-left: 0px;" onchange="yearlyReturns('${ackTabName}')">
            <option value="2022">2021 - 2022</option>
            <option value="2021">2020 - 2021</option>
            <option value="2020">2019 - 2020</option>
            <option value="2019">2018 - 2019</option>
            <option value="2018">2017 - 2018</option>
        </select>
    </div>
    <div class="custom_container_${ackTabName}" style="display:none">
        <span>From</span>
        <input type="text" class="form-control ml-1 mr-1 fromtime_${ackTabName} time_${ackTabName}" id="fromvalue_${ackTabName}" value="02-2012" style="padding: 3px;width: 95px;display: inline-block;padding: 4px 10px;padding-left: 2px;"><i class="fa fa-sort-desc" id="date-drop" style="position: absolute;margin-left: -16px;margin-top: 2px;"></i><span>To</span>
        <input type="text" class="form-control ml-1 mr-1 totime_${ackTabName} time_${ackTabName}" id="tovalue_${ackTabName}" value="02-2012" style="padding: 3px;width: 95px;display: inline-block;padding: 4px 10px;padding-left: 2px;"><i class="fa fa-sort-desc" id="date-drop" style="position: absolute;margin-left: -16px;margin-top: 2px;"></i>
    </div>
</div>
<div class="tab-pane mt-2" id="" role="tabpanel">
    <div class="normaltable meterialform" id="monthlynormaltable">
        <div class="filter">
            <div class="noramltable-row">
                <div class="noramltable-row-hdr">Filter</div>
                <div class="noramltable-row-desc">
                    <div class="sfilter"><span id="divFilters"></span>
                        <span class="btn-remove-tag" onClick="clearFiltersAndReloadAckTable('pending')">Clear All<span data-role="remove"></span></span>
                    </div>
                </div>
            </div>
        </div>
        <div class="noramltable-row">
            <!-- <div class="noramltable-row-hdr">Search Filter</div><div class="noramltable-row-desc">
										<select id="multiselectbilledtoname" class="multiselect-ui form-control" multiple="multiple"></select>
										<select id="multiselectinvoiceno" class="multiselect-ui form-control" multiple="multiple"></select>
									</div> -->
        </div>
    </div>
</div>
</c:if>
<c:if test="${ackTabName ne 'savedCredentialsTab'}">
<div class="col-md-12 col-sm-12 customtable p-0 mt-3">
    <table id="dbTableAcknowlegement_${ackTabName}" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
        <thead>
	     	<tr>
	         	<th class="text-center">Invoice No</th><th class="text-center">Inv Date</th><th class="text-center">Customer Name</th><th class="text-center">Customer Id</th><th class="text-center">Taxable Value</th><th class="text-center">Invoice Amount</th>
	         	<c:if test="${ackTabName eq 'uploadTab'}">
	         		<th class="text-center">Date of Submission</th><th class="text-center">No of days taken</th>
	         	</c:if>
	         	<th class="text-center">Attachments</th>
	        </tr>
	    </thead>
		<tbody></tbody>
	</table>
</div>
</c:if>
<c:if test="${ackTabName eq 'savedCredentialsTab'}">
	<div class="col-md-12 col-sm-12 customtable p-0 mt-3">
    <table id="dbTableCredentials_${ackTabName}" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
        <thead>
	     	<tr><th class="text-center">Client Name</th><th class="text-center">Access Key</th><th class="text-center">Access Secret Key</th><th class="text-center">Bucket Name</th><th class="text-center">Region</th><th class="text-center">Group Name</th><th class="actionicons">Action</th></tr>
	    </thead>
		<tbody id="credentialsBody"></tbody>
	</table>
</div>
</c:if>
<div id="ackprogress-bar_${ackTabName}" class="d-none"  style="color:red;font-size:20px;position:absolute;z-index:99;top:37%;left: 46%;">
	<img src="${contextPath}/static/mastergst/images/eclipse-spinner.gif" alt="spinner-img" style="width: 150px;height: 150px;"/>
</div>
<script type="text/javascript">
var retType='GSTR1';
var pendingOrUpload ='pending';

$(function(){
	var tab_name = '${ackTabName}';
	if(tab_name == "savedCredentialsTab"){
		loadCredentialsTable('${id}',retType, '${month}', '${year}');
	}else{
		var invperiod = $('#monthlyvalue_pendingTab').val().split('-');
		var month =parseInt(invperiod[0]);
		var year=parseInt(invperiod[1]);
		loadAcknowledgemtUserInvoices('${id}',retType, month, year, pendingOrUpload, '' ,'pendingTab');
	}
});
function returntypeView(tabRefName){
	var rtype = $('#viewReturnType_'+tabRefName).val();
	if(rtype == 'Monthly'){
		$('.monthly_container_'+tabRefName).css('display','inline-block');
		$('.yearly_container_'+tabRefName).css('display','none');
		$('.custom_container_'+tabRefName).css('display','none');
	}else if(rtype == 'Yearly'){
		$('.yearly_container_'+tabRefName).css('display','inline-block');
		$('.monthly_container_'+tabRefName).css('display','none')
		$('.custom_container_'+tabRefName).css('display','none');
	}else{
		$('.yearly_container_'+tabRefName).css('display','none');
		$('.monthly_container_'+tabRefName).css('display','none');
		$('.custom_container_'+tabRefName).css('display','inline-block');
	}
	
	var flag=true;
	$('#clientErrMsg_'+tabRefName).css('display','none');
	if(tabRefName == 'uploadTab'){
		pendingOrUpload ='upload';
		if(clientidsArray_uploadTab.length == 0){
			flag=false;
			$('#clientErrMsg_'+tabRefName).css('display','inline');
		}
	}
	if(tabRefName == 'pendingTab'){
		if(clientidsArray_pendingTab.length == 0){
			flag=false;
			$('#clientErrMsg_'+tabRefName).css('display','inline');
		}
	}
	if(flag == true){
		if(rtype == 'Monthly'){
			var invperiod = $('#monthlyvalue_'+tabRefName).val().split('-');
			var month =parseInt(invperiod[0]);
			var year=parseInt(invperiod[1]);			
			loadAcknowledgemtUserInvoices('${id}',retType, month, year, pendingOrUpload,rtype,tabRefName);
		}else if(rtype == 'Yearly'){
			var month = 0;
			var year=parseInt($('#yearlyvalue_'+tabRefName).val());
			loadAcknowledgemtUserInvoices('${id}',retType, month, year, pendingOrUpload,rtype,tabRefName);
		}else{
			var pfrom_value=$('#fromvalue_'+tabRefName).val();
			var pto_value=$('#tovalue_'+tabRefName).val();
			loadAcknowledgemtUserInvoices('${id}',retType, pfrom_value, pto_value, pendingOrUpload,rtype,tabRefName);
		}
	}
}
function yearlyReturns(tabRefName){
	returntypeView(tabRefName);
}
$('#${ackTabName}').on('click',function() {
	var tabRefName ='${ackTabName}';
	if("reportsTab" == tabRefName){
		pendingOrUpload='reports';
	}else if('uploadTab' == tabRefName){
		pendingOrUpload='upload';
	}else{
		pendingOrUpload='pending';
	}
	var rtype = $('#viewReturnType_'+tabRefName).val();
	if(rtype == 'Monthly'){
		$('.yearly_container_'+tabRefName).css('display','none');
		$('.custom_container_'+tabRefName).css('display','none');
		$('.monthly_container_'+tabRefName).css('display','inline-block');
	}else if(rtype == 'Yearly'){
		$('.monthly_container_'+tabRefName).css('display','none')
		$('.custom_container_'+tabRefName).css('display','none');
		$('.yearly_container_'+tabRefName).css('display','inline-block');
	}else{
		$('.yearly_container_'+tabRefName).css('display','none');
		$('.monthly_container_'+tabRefName).css('display','none');
		$('.custom_container_'+tabRefName).css('display','inline-block');
	}
	var flag=true;
	if(tabRefName == 'uploadTab'){
		clientidsArray=clientidsArray_uploadTab;
	}else{
		clientidsArray=clientidsArray_pendingTab;
	}
	$('#clientErrMsg_'+tabRefName).css('display','none');
	if(tabRefName == 'uploadTab'){
		pendingOrUpload ='upload';
		if(clientidsArray_uploadTab.length == 0){
			flag=false;
			$('#clientErrMsg_'+tabRefName).css('display','inline');
		}
	}
	if(tabRefName == 'pendingTab'){
		pendingOrUpload ='pending';
		if(clientidsArray_pendingTab.length == 0){
			flag=false;
			$('#clientErrMsg_'+tabRefName).css('display','inline');
		}
	}
	if(flag){
		if(rtype == "Monthly"){
			var invperiod = $('#monthlyvalue_'+tabRefName).val().split('-');
			var month =parseInt(invperiod[0]);
			var year=parseInt(invperiod[1]);
			loadAcknowledgemtUserInvoices('${id}',retType, month, year, pendingOrUpload,rtype,tabRefName);
		}else if(rtype == 'Yearly'){
			var month = 0;
			var year=parseInt($('#yearlyvalue_'+tabRefName).val());
			loadAcknowledgemtUserInvoices('${id}',retType, month, year, pendingOrUpload,rtype,tabRefName);
		}else{
			var pfrom_value=$('#fromvalue_'+tabRefName).val();
			var pto_value=$('#tovalue_'+tabRefName).val();			
			loadAcknowledgemtUserInvoices('${id}',retType, pfrom_value, pto_value, pendingOrUpload,rtype,tabRefName);
		}
	}
});
	

</script>
