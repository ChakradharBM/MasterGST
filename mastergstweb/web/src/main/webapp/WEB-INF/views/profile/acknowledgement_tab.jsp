<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<c:set var="ackTabName" value='<%= (String)request.getParameter("ackTabName") %>'/>
<div class="clienti-info ml-2" style="position:absolute;margin-top: 11px;">
	<h4 class="text-left m-0 p-0"><c:choose><c:when test='${fn:length(client.businessname) > 25}'>${fn:substring(client.businessname, 0, 25)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose> - ${client.gstnnumber}</h4>
</div>
<input type="hidden" name="clientid_${ackTabName}" id="clientid_${ackTabName}" value="${clientid}"/>
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
<div id="ackprogress-bar_${ackTabName}" class="d-none"  style="color:red;font-size:20px;position:absolute;z-index:99;top:37%;left: 46%;">
	<img src="${contextPath}/static/mastergst/images/eclipse-spinner.gif" alt="spinner-img" style="width: 150px;height: 150px;"/>
</div>
<script type="text/javascript">
var retType='GSTR1';
var pendingOrUpload ='pending';
var companyArray = new Array();
var customerArray = new Array();
$(function(){
	var invperiod = $('#monthlyvalue_pendingTab').val().split('-');
	var month =parseInt(invperiod[0]);
	var year=parseInt(invperiod[1]);		
	var noclientfound ='<c:out value="${acknowlegementAlert}"/>';
	if(noclientfound == ""){
		loadAcknowledgemtUserInvoices('${id}',retType,month, year, pendingOrUpload, '' ,'pendingTab');		
	}else{
		$('#errorMessage').text(noclientfound);
		$('.gst-notifications').css('display','block');
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
	var noclientfound ='<c:out value="${acknowlegementAlert}"/>';
	if(noclientfound ==""){
		if(rtype == 'Monthly'){
			var invperiod = $('#monthlyvalue_'+tabRefName).val().split('-');
			var month =parseInt(invperiod[0]);
			var year=parseInt(invperiod[1]);			
			loadAcknowledgemtUserInvoices('${id}',retType,month, year, pendingOrUpload,rtype,tabRefName);
		}else if(rtype == 'Yearly'){
			var month = 0;
			var year=parseInt($('#yearlyvalue_'+tabRefName).val());
			loadAcknowledgemtUserInvoices('${id}',retType, month, year, pendingOrUpload,rtype,tabRefName);
		}else{
			var pfrom_value=$('#fromvalue_'+tabRefName).val();
			var pto_value=$('#tovalue_'+tabRefName).val();
			loadAcknowledgemtUserInvoices('${id}',retType,pfrom_value, pto_value, pendingOrUpload,rtype,tabRefName);
		}		
	}else{
		$('#errorMessage').text(noclientfound);
		$('.gst-notifications').css('display','block');
	}
}
function yearlyReturns(tabRefName){
	returntypeView(tabRefName);
}
var is${ackTabName}Loaded = false;
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

	var noclientfound ='<c:out value="${acknowlegementAlert}"/>';
	if(noclientfound ==""){
		if(rtype == "Monthly"){
			var invperiod = $('#monthlyvalue_'+tabRefName).val().split('-');
			var month =parseInt(invperiod[0]);
			var year=parseInt(invperiod[1]);
			loadAcknowledgemtUserInvoices('${id}',retType,month, year, pendingOrUpload,rtype,tabRefName);
		}else if(rtype == 'Yearly'){
			var month = 0;
			var year=parseInt($('#yearlyvalue_'+tabRefName).val());
			loadAcknowledgemtUserInvoices('${id}',retType,month, year, pendingOrUpload,rtype,tabRefName);
		}else{
			var pfrom_value=$('#fromvalue_'+tabRefName).val();
			var pto_value=$('#tovalue_'+tabRefName).val();			
			loadAcknowledgemtUserInvoices('${id}',retType, pfrom_value, pto_value, pendingOrUpload,rtype,tabRefName);
		}		
	}else{
		$('#errorMessage').text(noclientfound);
		$('.gst-notifications').css('display','block');
	}
});
</script>
