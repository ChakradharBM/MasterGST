var stateoptions;
var partneremailid;

var partnerClientsTable;
var partnerPendingPaymentTableUrl = new Object();
var clientPendingPaymentTableUrl = new Object();
function loadPartnerAndClientPayments(userid, month, year){
	loadClientPaymentDetails(userid, month, year);
	loadClientPendingPaymentDetails(userid, month, year);
	loadPartnerPaymentDetails(userid, month, year);
	loadPartnerPendingPaymentDetails(userid, month, year);
}
function generatePaidData(userid){
	
	var ress = $("#getpaidval").val();
	if(ress == "monthly"){
		var cpdmthcd = $('#paidmonthly').val().split("-");
		loadClientPaymentDetails(userid, cpdmthcd[0], cpdmthcd[1]);
		loadPartnerPaymentDetails(userid, cpdmthcd[0], cpdmthcd[1]);		
	}else{
		var cpdyrcd = $('#paidyearly').val();
		loadClientPaymentDetails(userid, 0, cpdyrcd);
		loadPartnerPaymentDetails(userid, 0, cpdyrcd);		
	}
}

function generatePendingData(userid){
	
	var res = $("#getpenpayval").val();
	if(res == "monthly"){
		var penmthCd = $('#pendingmonthly').val().split("-");
		loadClientPendingPaymentDetails(userid, penmthCd[0], penmthCd[1]);
		loadPartnerPendingPaymentDetails(userid, penmthCd[0], penmthCd[1]);		
	}else{
		var penyrCd = $('#pendingyearly').val();
		loadClientPendingPaymentDetails(userid, 0, penyrCd);
		loadPartnerPendingPaymentDetails(userid, 0, penyrCd);				
	}
}
$(function() {
	var createdDt = new Date() ;
	$('#financialYear').val(createdDt.getUTCFullYear());
	$('#SubscriptionStartDate').datetimepicker({
		  timepicker: false,
		  format: 'd-m-Y',
		  //maxDate: 0,
		  scrollMonth: true,
		  onChangeDateTime:function(dp,$input){
			var substype = $('#paymentSubscriptionType').val();
			var stdate = $input.val();
			if(substype != null && (substype == "RENEWALAPI" || substype == "NEWAPI")){
				var stdate=stdate.split('-');
				var expiryDate = new Date(stdate[2], (stdate[1]), stdate[0]);
				var month = expiryDate.getUTCMonth(); 
				var day = expiryDate.getUTCDate();
				var year = expiryDate.getUTCFullYear()+1;
				if(month == 0){
					month = 12;
					year = year-1;
				}else if(month == 1 && day == 31){
					month = 12;
				}
				var expdate=day+'-'+month+'-'+year;
				$('#SubscriptionExpiryDate').val(expdate)
			}
		}
	});
	function usrneeddatepick() {
		$('#userneedFollowupdate').datetimepicker({
			  timepicker: false,
			  format: 'd-m-Y',
			  minDate: 0,
			  scrollMonth: true
		});
	}
	$('#SubscriptionExpiryDate').datetimepicker({
		  timepicker: false,
		  format: 'd-m-Y',
		  scrollMonth: true
	});
		 
	function datepick() {
		$('#commentsDate').datetimepicker({
		  	timepicker: false,
		  	format: 'd-m-Y',
		  	maxDate: 0,
		  	scrollMonth: true
		});
	}
	$(document).on('change', '#accessDrive', function() {
		var status = $(this).is(':checked');
		$.ajax({
			url: contextPath+"/updtusrdoc/"+currentUser,
			async: false,
			cache: false,
			data: {
				'flag': status
			},
			success : function(data) {}
		});
	});
	$(document).on('change', '#accessTravel', function() {
		var status = $(this).is(':checked');
		$.ajax({
			url: contextPath+"/updtusrtravelreport/"+currentUser,
			async: false,
			cache: false,
			data: {
				'flag': status
			},
			success : function(data) {}
		});
	});
	$(document).on('change', '#accessImports', function() {
		var status = $(this).is(':checked');
		$.ajax({
			url: contextPath+"/updtusrimport/"+currentUser,
			async: false,
			cache: false,
			data: {
				'flag': status
			},
			success : function(data) {}
		});
	});
	$(document).on('change', '#accessReports', function() {
		var status = $(this).is(':checked');
		$.ajax({
			url: contextPath+"/updtusrreport/"+currentUser,
			async: false,
			cache: false,
			data: {
				'flag': status
			},
			success : function(data) {}
		});
	});
	$(document).on('change', '#accessJournalExcel', function() {
		var status = $(this).is(':checked');
		$.ajax({
			url: contextPath+"/updtusrjournalexcel/"+currentUser,
			async: false,
			cache: false,
			data: {
				'flag': status
			},
			success : function(data) {}
		});
	});
	$(document).on('change', '#accessGstr9', function() {
		var status = $(this).is(':checked');
		$.ajax({
			url: contextPath+"/updtusrgstr9access/"+currentUser,
			async: false,
			cache: false,
			data: {
				'flag': status
			},
			success : function(data) {}
		});
	});
	$(document).on('change', '#accessDwnldewabillinv', function() {
		var status = $(this).is(':checked');
		$.ajax({
			url: contextPath+"/updtusrDwnldewabillinvaccess/"+currentUser,
			async: false,
			cache: false,
			data: {
				'flag': status
			},
			success : function(data) {}
		});
	});
	$(document).on('change', '#accessMultiGSTNSearch', function() {
		var status = $(this).is(':checked');
		$.ajax({
			url: contextPath+"/updtusrmultiGstnSearchaccess/"+currentUser,
			async: false,
			cache: false,
			data: {
				'flag': status
			},
			success : function(data) {}
		});
	});
	
	$(document).on('change', '#accessReminders', function() {
		var status = $(this).is(':checked');
		$.ajax({
			url: contextPath+"/updtusrReminders/"+currentUser,
			async: false,
			cache: false,
			data: {
				'flag': status
			},
			success : function(data) {}
		});
	});
	$(document).on('change', '#accessANX1', function() {
		var status = $(this).is(':checked');
		$.ajax({
			url: contextPath+"/updtusrANX1/"+currentUser,
			async: false,
			cache: false,
			data: {
				'flag': status
			},
			success : function(data) {}
		});
	});
	$(document).on('change', '#accessANX2', function() {
		var status = $(this).is(':checked');
		$.ajax({
			url: contextPath+"/updtusrANX2/"+currentUser,
			async: false,
			cache: false,
			data: {
				'flag': status
			},
			success : function(data) {}
		});
	});
	$(document).on('change', '#accessEinvoice', function() {
		var status = $(this).is(':checked');
		$.ajax({
			url: contextPath+"/updtusrEinvoice/"+currentUser,
			async: false,
			cache: false,
			data: {
				'flag': status
			},
			success : function(data) {}
		});
	});
	$(document).on('change', '#accessEntertainmentEinvoiceTemplate', function() {
		var status = $(this).is(':checked');
		$.ajax({
			url: contextPath+"/updtusrEntertaimentEinvoiceTemplate/"+currentUser,
			async: false,
			cache: false,
			data: {
				'flag': status
			},
			success : function(data) {}
		});
	});
	$(document).on('change', '#accessCustomFieldTemplate', function() {
		var status = $(this).is(':checked');
		$.ajax({
			url: contextPath+"/updtusrCustomFieldTemplate/"+currentUser,
			async: false,
			cache: false,
			data: {
				'flag': status
			},
			success : function(data) {}
		});
	});
	$(document).on('change', '#accessAcknowledgement', function() {
		var status = $(this).is(':checked');
		$.ajax({
			url: contextPath+"/updtusrAcknowledgement/"+currentUser,
			async: false,
			cache: false,
			data: {
				'flag': status
			},
			success : function(data) {}
		});
	});
	$(document).on('change', '#businessSubscriptionType', function() {
		var subscriptionType = $(this).val();
		$.ajax({
			url: contextPath+"/getBusinessPlans/"+subscriptionType,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				$('#idASPAmt').val(response.data.price);
				$('#idASPInv').val(response.data.invoice);
				$('#idSuvidhaClnts').val(response.data.client);
				$('#planid').val(response.planid);
				if(response.data.duration == 12){
					$('#idDays').val(365);
				}else{
					$('#idDays').val(response.data.duration*30);
				}
			}
		});
	});
	$(document).on('change', '#subscriptionType', function() {
		var subscriptionType = $(this).val();
		$.ajax({
			url: contextPath+"/getCaPlans/"+subscriptionType,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				$('#idASPAmt').val(response.data.price);
				$('#idASPInv').val(response.data.invoice);
				$('#idSuvidhaClnts').val(response.data.client);
				$('#planid').val(response.planid);
				if(response.data.duration == 12){
					$('#idDays').val(365);
				}else{
					$('#idDays').val(response.data.duration*30);
				}
			}
		});
	});
	$(document).on('change', '#financialYear', function() {
		var finYear = $(this).val();
		$('.tddataEwayBillProduction,.tddataEwayBillSandbox,.tddataProduction,.tddataSandbox,.tddataEinvSandbox,.tddataEinvProduction').html(0);
		$.ajax({
			//mdfymonthlyinvoiceusage mdfymonthlyapiusage
			url: contextPath+"/mdfymonthlyinvoiceusage?year="+finYear+"&userId="+currentUser,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(summary) {
				$.each(summary, function(key, value){
					$('#'+key).html(value);								
					if(key == 'SandboxTotal'){
						$('#Sandbox13').html(value);
					}
					if(key == 'ProductionTotal'){
						$('#Production13').html(value);
					}
					if(key == 'EwayBillSandboxTotal'){
						$('#EwayBillSandBox13').html(value);
					}if(key == 'EwayBillProductionTotal'){
						$('#EwayBillProduction13').html(value);					
					}
					if(key == 'EinvSandboxTotal'){
						$('#EInvoiceSandBox13').html(value);
					}if(key == 'EinvProductionTotal'){
						$('#EInvoiceProduction13').html(value);					
					}
				});
			}
		});			
	});
	$(document).on('change', '.stageapiplanselect', function() {
	var val=this.value;
	if(val == 'GSTSANDBOXAPI' || val == 'EWAYBILLSANDBOXAPI' || val == 'E-INVOICESANDBOXAPI'){
		$.ajax({
			url: contextPath+"/sbscrpymtsretrivedata/"+currentUser+"/"+val,
			type: 'GET',
			success : function(data) {
				if(data == "" || data == null){
					var date=new Date();
					$('#SubscriptionExpiryDate').val(date.getDate()+"-"+(date.getMonth()+2)+"-"+date.getFullYear());
					$('#SubscriptionStartDate').val(date.getDate()+"-"+(date.getMonth()+1)+"-"+date.getFullYear());
					$('.paymentSubscriptionType').find('option').remove().end().append("<option value>-select-</option><option value='DEMO'>Demo</option><option value='UNLIMITED'>Unlimited</option>");
				}else{
					var type=data.subscriptionType;
					if(data.expiryDate != null){
						$('#SubscriptionExpiryDate').val(formatSubscriptionDate(data.expiryDate));
					}
					$('#SubscriptionStartDate').val(formatSubscriptionDate(data.registeredDate));
					$('.paymentSubscriptionType').find('option').remove().end().append("<option value>-select-</option><option value='DEMO'>Demo</option><option value='UNLIMITED'>Unlimited</option>");				
				}
			},
			error: function(errorRes){
				console.log(errorRes);
			}
		});
	}else{
		$.ajax({
			url: contextPath+"/sbscrpymtsretrivedata/"+currentUser+"/"+val,
			type: 'GET',
			
			success : function(data) {
				var type=data.subscriptionType;
				if(data.expiryDate != null){
				$('#SubscriptionExpiryDate').val(formatSubscriptionDate(data.expiryDate));
				}
				$('#SubscriptionStartDate').val(formatSubscriptionDate(data.registeredDate));
				$('#idASPMode').val('');
				$('#idASPRef').val('');
				$('#idASPAmt').val('');
				$('#idASPRate').val('');
				$('#statename').val('');
				$('#idASPInv').val('');
				if(type=='NEWAPI' || type == null){
					if(val == data.apiType){
					$('.paymentSubscriptionType').find('option').remove().end().append("<option value=''>-- Select Type --</option><option value='TOPUPAPI'>Top-Up</option><option value='RENEWALAPI'>Renewal</option>");					
					}else{
						$('#SubscriptionStartDate').val('');
						$('#SubscriptionExpiryDate').val('');
						$('.paymentSubscriptionType').find('option').remove().end().append("<option value='NEWAPI'>NEW</option>");			
					}			
				}else if(type=='TOPUPAPI'){
					$('.paymentSubscriptionType').find('option').remove().end().append("<option value=''>-- Select Type --</option><option value='TOPUPAPI'>Top-Up</option><option value='RENEWALAPI'>Renewal</option>");
				}else if(type=='RENEWALAPI'){
					$('.paymentSubscriptionType').find('option').remove().end().append("<option value=''>-- Select Type --</option><option value='TOPUPAPI'>Top-Up</option><option value='RENEWALAPI'>Renewal</option>");
				}else{
					$('#SubscriptionStartDate').val('');
					$('#SubscriptionExpiryDate').val('');
					//$('.paymentSubscriptionType').find('option').remove().end().append("<option value=''>-- Select Type --</option><option value='NEWAPI'>NEW</option><option value='TOPUPAPI'>Top-Up</option><option value='RENEWALAPI'>Renewal</option>");
					$('.paymentSubscriptionType').find('option').remove().end().append("<option value=''>-- Select Type --</option><option value='NEWAPI'>NEW</option>");
				}
			},
			error: function(errorRes){
				console.log(errorRes);
			}
		});
	}

	$(document).on('focusout', '#idASPAmt', function() {
		var amt = $('#idASPAmt').val();
		var rate = $('#idASPRate').val();
		if(rate) {
			$('#idASPInv').val(Math.round(amt/rate));
		}
	});
	$(document).on('focusout', '#idASPRate', function() {
		var amt = $('#idASPAmt').val();
		var rate = $('#idASPRate').val();
		if(rate) {
			$('#idASPInv').val(Math.round(amt/rate));
		}
	});
		
	$(document).on('focusout', '#idASPInv', function() {
		var amt = $('#idASPAmt').val();
		var rate = $('#idASPRate').val();
		if(rate) {
			$('#idASPInv').val(Math.round(amt/rate));
		}
	});
	$(document).on('focusout', '#idASPEwayInv', function() {
		var amt = $('#idASPEwayAmt').val();
		var rate = $('#idASPEwayRate').val();
		if(rate) {
			$('#idASPEwayInv').val(Math.round(amt/rate));
		}
	});	
});


$('#SubscriptionStartDate').datetimepicker({
	  timepicker: false,
	  format: 'd-m-Y',
	  scrollMonth: true,
	   onChangeDateTime:function(dp,$input){
			var substype = $('#paymentSubscriptionType').val();
			var stdate = $input.val();
			if(substype != null && (substype == "RENEWALAPI" || substype == "NEWAPI")){
				var stdate=stdate.split('-');
				var expiryDate = new Date(stdate[2], (stdate[1]), stdate[0]);
				var month = expiryDate.getUTCMonth(); 
				var day = expiryDate.getUTCDate();
				var year = expiryDate.getUTCFullYear()+1;
				if(month == 0){
					month = 12;
					year = year-1;
				}else if(month == 1 && day == 31){
					month = 12;
				}
				var expdate=day+'-'+month+'-'+year;
				$('#SubscriptionExpiryDate').val(expdate)
			}
		}
});
$('#SubscriptionExpiryDate').datetimepicker({
	  timepicker: false,
	  format: 'd-m-Y',
	 
	  scrollMonth: true
});
	$(document).on('change', '#paymentSubscriptionType', function() {
	var val=this.value;
	
	if(val == 'DEMO'){
		
		$('#idASPMode').val('Demo');
		$('#idASPRef').val('Demo');
		$('#idASPAmt').val('10');
		$('#idASPRate').val('0.20');
		$('#statename').val('36-Telangana');
		$('#idASPInv').val(Math.round(10/0.20));
		$('.expDate').removeClass("d-none");
	}else if(val  == 'UNLIMITED'){
		$('#idASPMode').val('');
		$('#idASPRef').val('');
		$('#idASPAmt').val('');
		$('#idASPRate').val('0.20');
		$('#statename').val('');
		$('#idASPInv').val('UNLIMITED');
		$('.expDate').addClass("d-none");
	}
	
	
	if(val=='NEWAPI'){
		
		var stageapiplanselect=$('.stageapiplanselect').val();
				
		$.ajax({
			url: contextPath+"/sbscrpymtsretrivedata/"+currentUser+"/"+stageapiplanselect,
			type: 'GET',
			
			success : function(data) {
				if(data == ''){
					var newDate = new Date();
					
					var day=newDate.getDate();
					var month=newDate.getMonth()+1;
					
					var year=newDate.getFullYear();
					var day1=newDate.getDate()-1;
					var year1=newDate.getFullYear()+1;
							
					var date1=day1+'-'+month+'-'+year1;
					var date=day+'-'+month+'-'+year;
					$('#SubscriptionExpiryDate').val(date1);
					$('#SubscriptionStartDate').val(date);
				}else{
					$('#SubscriptionExpiryDate').val(formatSubscriptionDate(data.expiryDate));
					$('#SubscriptionStartDate').val(formatSubscriptionDate(data.registeredDate));
				}
			},
			error: function(errorRes){
				//console.log(errorRes);
			}
		});
	}else if(val == 'RENEWALAPI'){
		var newDate = new Date();
		
		var day=newDate.getDate();
		var month=newDate.getMonth()+1;
		
		var year=newDate.getFullYear();
		var day1=newDate.getDate()-1;
		var year1=newDate.getFullYear()+1;
				
		var date1=day1+'-'+month+'-'+year1;
		var date=day+'-'+month+'-'+year;
				
		$('#SubscriptionStartDate').val(date);
		$('#SubscriptionExpiryDate').val(date1);
	}else if(val == 'TOPUPAPI'){
		var stageapiplanselect=$('.stageapiplanselect').val();
		if(stageapiplanselect ==''){
			$.ajax({
				url: contextPath+"/sbscrpymtsretrivedata/"+currentUser,
				type: 'GET',
				
				success : function(data) {
					
					$('#SubscriptionExpiryDate').val(formatSubscriptionDate(data.expiryDate));
					$('#SubscriptionStartDate').val(formatSubscriptionDate(data.registeredDate))
				},
				error: function(errorRes){
					//console.log(errorRes);
				}
			});
		}else{
			$.ajax({
				url: contextPath+"/sbscrpymtsretrivedata/"+currentUser+"/"+stageapiplanselect,
				type: 'GET',
				
				success : function(data) {
					
					$('#SubscriptionExpiryDate').val(formatSubscriptionDate(data.expiryDate));
					$('#SubscriptionStartDate').val(formatSubscriptionDate(data.registeredDate))
				},
				error: function(errorRes){
					//console.log(errorRes);
				}
			});
		}
	}
});
stateoptions = {
		url: function(phrase) {
			phrase = phrase.replace('(',"\\(");
			phrase = phrase.replace(')',"\\)");
			return contextPath+"/stateconfig?query="+ phrase + "&format=json";
		},
		getValue: "name",
		list: {
			onLoadEvent: function() {
				if($("#eac-container-statename ul").children().length == 0) {
					//$("#statename").val('');
					$("#statenameempty").show();
				} else {
					$("#statenameempty").hide();
				}
			}
		}

	};
	$("#statename").easyAutocomplete(stateoptions);
	$("#statename1").easyAutocomplete(stateoptions);

partneremailid = {
					url: function(phrase) {
						phrase = phrase.replace('(',"\\(");
						phrase = phrase.replace(')',"\\)");
						return contextPath+"/srchpartneremail?query="+ phrase + "&format=json";
					},
					getValue: "email",
					list: {
						onLoadEvent: function() {
							if($("#eac-container-userpartnerEmail ul").children().length == 0) {
								$("#partnerEmailempty").show();
							} else {
								$("#partnerEmailempty").hide();
							}
						}
					}
				};	
});
function usrneeddatepick() {
	$('#userneedFollowupdate').datetimepicker({
		  timepicker: false,
		  format: 'd-m-Y',
		  minDate: 0,
		  scrollMonth: true
	});
}	 
function datepick() {
	$('#commentsDate').datetimepicker({

	  	timepicker: false,
	  	format: 'd-m-Y',
	  	maxDate: 0,
	  	scrollMonth: true
	});
}
function formatSecondaryUserStatus(dat, type, row){
	if(dat.disable && dat.disable == 'true'){
		 return 'Disabled';
	 }
	 if(!dat.disable){
		 return 'Disabled';
	 }
	 return 'Enabled';
}
function tabActivities(userType){
	$('#userModal').modal('show');
	if(userType == 'aspdeveloper'){
		$('.payment_tab').css("display","block");
		$('.asp').css("display","block");
		$('.nonasp').css("display","none");
		$('.asp-dev').css("display","inline-flex");
		$('#usercreddetails').css("display","block");
		$('#sandcred').css("display","block");
		$('#prodcred').css("display","block");
		$('#ewaybillcred').css("display","block");
		$('#einvoicecredential').css("display","block");
		$('#userpymt').css("display","block");
		$('#div_agreementstatus').removeClass('d-none');
		$('#userewaypymt').css("display","block");
		$('#secondaryUser').css("display","none");
		$('#users').css("display","none");
		$('#client').css("display","none");
		$('#invoice').css("display","none");
		$('#sandboxCredentials').css("display","none");
		$('#productionCredentials').css("display","none");
		$('#ewaybillCredentials').css("display","none");
		$('#eInvoiceCredentials').css("display","none");
		$('#payment').css("display","none");
		$('#ewaypayment').css("display","none");
		$('#addCenter').css("display","none");
		$('.Invoices_Clients').css("display","none");
		$('.idSuvidhaClients').css("display","none");
		$('#paymentMode').parent().parent().css("display","block");
		
		$('#commentsTab,#partnerBankTab,#partnerBankTab1').css("display","none");
		$('#centersTab2').css("display","none");
		$('#refClientTab,#refClientTab1').css("display","none");
		$('#clientPaymentDetailsTab2').css("display","none");
		$('#clientPendingPaymentDetailsTab2').css("display","none");
		
		$('#paymentSubscriptionTypes').parent().parent().css("display","block");
		$('#referenceNo').parent().parent().css("display","block");
		$('#paidAmount').parent().parent().css("display","block");
		$('#aspRate').css("display","block");
		$('#aspStage').css("display","block");
		$('#aspAmount').css("display","block");
		$('#aspState').css("display","block");
		$('#suvidhaPaymentClients').css("display","none");
		$('#suvidhaPaymentCenters').css("display","none");
		$('#businessPaymentType').css("display","none");
		$('#suvidhaPaymentType').css("display","none");
		$('#freeDays').css("display","none");
		
		$('.div-userdashboard').css("display","block");
		$('#GSTAPIProduction').css("display","block");
		$('#GSTAPIProductionDiv').css("display","inline-flex");
		$('#GSTAPISanbox').css("display","block");
		$('#GSTAPISanboxDiv').css("display","inline-flex");
		$('#EWAYBILLAPIProduction').css("display","block");
		$('#EWAYBILLAPIProductionDiv').css("display","inline-flex");
		$('#EWAYBILLAPISanbox').css("display","block");
		$('#EWAYBILLAPISanboxDiv').css("display","inline-flex");
		
		
		$('.non-asp-developerdata').css("display","none");
		$('.suvidha_centers_data').css("display","none");
		$('.asp-developerdata').css("display","block");
		$('#asp_apiuserdetails1').css("display","block");
		$('#asp_apiuserdetailsTab').css("display","none");
		$('#headerkeysTabcon').css("display","block");
		$('#headerkeysTab').css("display","none");
		$('#featuresTabcon,#pymentTabcon').css("display","none");
	}else{
		$('.payment_tab').css("display","block");
		$('#headerkeysTabcon').css("display","none");
		$('.asp').css("display","none");
		$('#div_agreementstatus').removeClass('d-none');
		$('.nonasp').css("display","flex");
		$('#sandboxCredentials').css("display","none");
		$('#productionCredentials').css("display","none");
		$('#ewaybillCredentials').css("display","none");
		$('#eInvoiceCredentials').css("display","none");
		$('.asp-dev').css("display","none");
		$('#commentsTab,#partnerBankTab1,#partnerBankTab').css("display","none");
		$('#centersTab2').css("display","none");
		$('#refClientTab,#refClientTab1').css("display","none");
		$('#clientPaymentDetailsTab2').css("display","none");
		$('#clientPendingPaymentDetailsTab2').css("display","none");
		$('#usercreddetails').css("display","none");
		$('#sandcred').css("display","none");
		$('#prodcred').css("display","none");
		$('#ewaybillcred').css("display","none");
		$('#einvoicecredential').css("display","none");
		$('#userpymt').css("display","block");
		$('#secondaryUser').css("display","block");
		$('#users').css("display","none");
		$('#userewaypymt').css("display","none");
		$('#client').css("display","block");
		$('#invoice').css("display","block");
		$('#addCenter').css("display","none");
		$('#payment').css("display","none");
		$('#paymentMode').parent().parent().css("display","none");
		$('#paymentMode1').parent().parent().css("display","none");
		$('#ewaypayment').css("display","none");
		$('#paymentMode').parent().parent().css("display","none");
		$('#referenceNo').parent().parent().css("display","none");
		$('#paidAmount').parent().parent().css("display","none");
		$('#aspAmount').css("display","none");
		$('#aspState').css("display","none");
		$('#aspRate').css("display","none");
		$('#aspStage').css("display","none");
		$('#suvidhaPaymentClients').css("display","block");
		$('#suvidhaPaymentCenters').css("display","none");
		$('#suvidhaPaymentType').css("display","none");
		$('#businessPaymentType').css("display","none");
		$('#freeDays').css("display","block");
		$('.idSuvidhaClients').css("display","none");
		$('#asp_apiuserdetails1').css("display","none");
		$('#asp_apiuserdetailsTab').css("display","none");
		$('#headerkeysTabcon').css("display","none");
		$('#headerkeysTab,#pymentTab').css("display","none");
		//$('#featuresTab2').css("display","block");
		$('#featuresTabcon,#pymentTabcon').css("display","block");
		if(userType == 'suvidha'){
			$('#div_agreementstatus').removeClass('d-none');
			$('#addCenter').css("display","block");
			$('#userpymt').css("display","block");
			$('#headerkeysTabcon').css("display","block");
			$('#aspType').css("display","block");
			$('#centersTab2').css("display","block");
			$('#clientPaymentDetailsTab2').css("display","none");
			$('#clientPendingPaymentDetailsTab2').css("display","none");
			$('.div-userdashboard').css("display","none");
			$('.non-asp-developerdata').css("display","inline-flex");
			$('.suvidha_centers_data').css("display","block");
			$('.asp-developerdata').css("display","none");
			
			$('#GSTAPIProduction').css("display","none");
			$('#GSTAPIProductionDiv').css("display","none");
			$('#GSTAPISanbox').css("display","none");
			$('#GSTAPISanboxDiv').css("display","none");
			$('#EWAYBILLAPIProduction').css("display","none");
			$('#EWAYBILLAPIProductionDiv').css("display","none");
			$('#EWAYBILLAPISanbox').css("display","none");
			$('#EWAYBILLAPISanboxDiv').css("display","none");
			
			$('#paymentMode').parent().parent().css("display","block");
			$('#referenceNo').parent().parent().css("display","block");
			$('#paidAmount').parent().parent().css("display","block");
			$('#aspAmount').css("display","block");
			$('#aspState').css("display","block");
			$('#aspRate').css("display","none");
			$('#aspStage').css("display","none");
			$('#suvidhaPaymentClients').css("display","block");
			$('#suvidhaPaymentCenters').css("display","block");
			if(userArray.parentid) {
				$('#addCenter').css("display","none");
				$('#suvidhaPaymentCenters').css("display","none");
			}
			$('#freeDays').css("display","none");
		} else if(userType == 'enterprise' || userType == 'business'){
			
			if(userType == 'business'){
				$('#businessPaymentType').css("display","block");
				$('.suvidha_centers_data').css("display","none");
				$('#aspType').css("display","none");
			}else{
				$('#aspType').css("display","block");	
			}
			
			$('#centersTab').css("display","none");
			$('#div_agreementstatus').addClass('d-none');
			$('#clientPaymentDetailsTab2').css("display","none");
			$('#clientPendingPaymentDetailsTab2').css("display","none");
			$('#paymentMode').parent().parent().css("display","block");
			$('#referenceNo').parent().parent().css("display","block");
			$('#paidAmount').parent().parent().css("display","block");
			$('#aspState').css("display","block");
			$('#freeDays').css("display","none");
			
			$('.div-userdashboard').css("display","none");
			$('.non-asp-developerdata').css("display","inline-flex");
			$('.suvidha_centers_data').css("display","none");
			$('.asp-developerdata').css("display","none");
			$('#headerkeysTabcon').css("display","block");
			//$('#aspType').css("display","block");
			$('#GSTAPIProduction').css("display","none");
			$('#GSTAPIProductionDiv').css("display","none");
			$('#GSTAPISanbox').css("display","none");
			$('#GSTAPISanboxDiv').css("display","none");
			$('#EWAYBILLAPIProduction').css("display","none");
			$('#EWAYBILLAPIProductionDiv').css("display","none");
			$('#EWAYBILLAPISanbox').css("display","none");
			$('#EWAYBILLAPISanboxDiv').css("display","none");
			
			
		} else if(userType == 'cacmas'){
			$('#div_agreementstatus').addClass('d-none');
			$('#paymentMode').parent().parent().css("display","block");
			$('#referenceNo').parent().parent().css("display","block");
			$('#paidAmount').parent().parent().css("display","block");
			$('#headerkeysTabcon').css("display","block");
			$('#centersTab').css("display","none");
			$('#clientPaymentDetailsTab2').css("display","none");
			$('#clientPendingPaymentDetailsTab2').css("display","none");
			$('#aspType').css("display","none");				
			$('#GSTAPIProduction').css("display","none");
			$('#GSTAPIProductionDiv').css("display","none");
			$('#GSTAPISanbox').css("display","none");
			$('#GSTAPISanboxDiv').css("display","none");
			$('#EWAYBILLAPIProduction').css("display","none");
			$('#EWAYBILLAPIProductionDiv').css("display","none");
			$('#EWAYBILLAPISanbox').css("display","none");
			$('#EWAYBILLAPISanboxDiv').css("display","none");
			
			$('.div-userdashboard').css("display","none");
			$('.non-asp-developerdata').css("display","inline-flex");
			$('.suvidha_centers_data').css("display","none");
			$('.asp-developerdata').css("display","none");
			
			$('#suvidhaPaymentType').css("display","block");
			$('#aspState').css("display","block");
			$('#freeDays').css("display","block");
		}else {
			$('.idSuvidhaClients').css("display","none");
			$('#centersTab').css("display","none");
			$('#clientPaymentDetailsTab2').css("display","none");
		}
		if(userType == 'partner'){
			$('#secondaryUser').css("display","none");
			$('.Invoices_Clients').css("display","none");
			$('.idSuvidhaClients').css("display","inline-flex");
			$('.nonasp').css("display","none");
			$('.payment_tab').css("display","none");
			$('#centersTab').css("display","none");
			$('#refClientTab1').css("display","block");
			$('#clientPaymentDetailsTab2').css("display","block");
			$('#clientPendingPaymentDetailsTab2,#partnerBankTab1').css("display","block");
			
			$('.div-userdashboard').css("display","none");
			$('.non-asp-developerdata').css("display","none");
			$('.suvidha_centers_data').css("display","none");
			$('.asp-developerdata').css("display","none");
			
			$('#aspType').css("display","none");				
			$('#GSTAPIProduction').css("display","none");
			$('#GSTAPIProductionDiv').css("display","none");
			$('#GSTAPISanbox').css("display","none");
			$('#GSTAPISanboxDiv').css("display","none");
			$('#EWAYBILLAPIProduction').css("display","none");
			$('#EWAYBILLAPIProductionDiv').css("display","none");
			$('#EWAYBILLAPISanbox').css("display","none");
			$('#EWAYBILLAPISanboxDiv').css("display","none");
			$('#headerkeysTabcon').css("display","none");
			$('#headerkeysTab').css("display","none");
			$('#featuresTabcon,#pymentTabcon').css("display","none");
		} else {
			$('.Invoices_Clients').css("display","inline-flex");
			$('#centersTab').css("display","none");
			$('#clientPaymentDetailsTab2').css("display","none");
		}
	}
	$('#userModal a.nav-link').removeClass('active');
	$('#detail').addClass('active');
	$('#details').css("display","block");
	$('#clients').css("display","none");
	$('#invoices').css("display","none");
}
function loadComments(userId){
	 $.ajax({
			url: contextPath+"/getallcomments/"+userId,
			type:'GET',
			success : function(data) {
				$(".alertcommentsdata").html("");
				$.each(data, function(key, value) {
					$(".alertcommentsdata").append("<div class='alert alert-success commentsdata'>"+value.comments+'<br/>'+value.addedby+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+value.commentDate+"</div");
				});
			}
		}); 
	}

function loadPayments(userId){
	$('.paymentDtClear').val('');
	if ( $.fn.DataTable.isDataTable('#paymentsTable') ) {
		  $('#paymentsTable').DataTable().destroy();
	}
	$('#paymentsTable tbody').empty();
	paymentTable = $('#paymentsTable').DataTable({
		"ajax": {
	         url: contextPath+"/payments/"+userId,
	         type: 'GET'
	     },
	     "paging": true,
	     "order": [[0,'desc']],
	     'pageLength':3,
         dom: '<"toolbar">frtip',
         columns : [
                      {data : 'orderId', 'defaultContent':''},
        	      {data : 'apiType', 'defaultContent':''},
                      {data : formatPaymentDate, 'defaultContent':''},
                      {data : 'status', 'defaultContent':''},
                      {data : 'trackingId', 'defaultContent':''},
                      {data : 'amount', 'defaultContent':''}
                      ]
	 });
	 $(".tabPayments div.toolbar").html('<h4>Payments</h4>');
}

function loadPartnerBankdetails(userId){
	$('#partnerAccountName,#partnerBankName,#partnerBranchName,#partnerAccountNumber,#partnerIfscCode,#partnerPanNumber').html('');
	$.ajax({
			url: contextPath+"/partnerbankdetails?id="+userId,
			async: false,
			cache: false,
			success : function(data) {
				if(data != null){
					$('#partnerAccountName').html(data.accountholdername);
					$('#partnerBankName').html(data.bankname);
					$('#partnerBranchName').html(data.branchname);
					$('#partnerAccountNumber').html(data.accountnumber);
					$('#partnerIfscCode').html(data.ifsccode);
					$('#partnerPanNumber').html(data.pannumber);
				}
			}
	});
}

function loadUsageInvoices(userId){
	var createdDte = new Date();
	$('#financialYear').val(createdDte.getUTCFullYear());
	$('.tddataEinvProduction').html(0);
	$('.tddataEinvSandbox').html(0);
	$('.tddataEwayBillProduction').html(0);
	$('.tddataEwayBillSandbox').html(0);
	$('.tddataProduction').html(0);
	$('.tddataSandbox').html(0);
		$.ajax({
			url: contextPath+"/mdfymonthlyinvoiceusage?year="+createdDte.getUTCFullYear()+"&userId="+userId,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(summary) {
				$.each(summary, function(key, value){
				$('#'+key).html(value);								
				if(key == 'SandboxTotal'){
					$('#Sandbox13').html(value);
				}
				if(key == 'ProductionTotal'){
					$('#Production13').html(value);
				}
				if(key == 'EwayBillSandboxTotal'){
					$('#EwayBillSandBox13').html(value);
				}if(key == 'EwayBillProductionTotal'){
					$('#EwayBillProduction13').html(value);					
				}
				if(key == 'EinvSandboxTotal'){
					$('#EInvoiceSandBox13').html(value);
				}if(key == 'EinvProductionTotal'){
					$('#EInvoiceProduction13').html(value);					
				}
				});
			}
		});		 
}
function formatheaderkeys(dat, type, row){
	return '<a href="#" id="'+dat.headerid+'" class="" onClick="showDeleteHeaderkeysPopup(\''+dat.headerid+'\',\''+dat.userid+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a>';
}
function deleteHeaderkeys(headerkeyid,userid){
	if ( $.fn.DataTable.isDataTable('#headerkeysTable') ) {
		  $('#headerkeysTable').DataTable().destroy();
	}
	$('#headerkeysTable tbody').empty();
	headerkeysTable = $('#headerkeysTable').DataTable({
		"ajax": {
	         url: contextPath+"/deleteheaderkeys/"+headerkeyid+"/"+userid,
	         type: 'GET'
	     },
	     "paging": true,
	     "order": [[0,'desc']],
	     'pageLength':10,
	      dom: '<"toolbar">frtip',
	      columns : [
		      {data : 'gstusername', 'defaultContent':''},
		      {data : 'txn', 'defaultContent':''},
		      {data : 'authtoken', 'defaultContent':''},
			  {data : 'updatedDate', 'defaultContent':''},
			  {data : formatheaderkeys, 'defaultContent':''}
		 ]
	});
	$(".headerkeysTab div.toolbar").html('<h5>User Header keys</h5>');
}
function showDeleteHeaderkeysPopup(headerid,userid){
	$('#deleteHeaderKeysModal').modal("show");
	$('#btnDeleteHeaderkeys').attr("onclick", "deleteHeaderkeys('"+headerid+"','"+userid+"')");
}
function loadHeaderKeys(userId){
	if ( $.fn.DataTable.isDataTable('#headerkeysTable') ) {
		  $('#headerkeysTable').DataTable().destroy();
	}
	$('#headerkeysTable tbody').empty();
	headerkeysTable = $('#headerkeysTable').DataTable({
		"ajax": {
	         url: contextPath+"/getheaderkeys/"+userId,
	         type: 'GET'
	     },
	     "paging": true,
	     "order": [[0,'desc']],
	     'pageLength':10,
	      dom: '<"toolbar">frtip',
	      columns : [
		      {data : 'gstusername', 'defaultContent':''},
		      {data : 'txn', 'defaultContent':''},
		      {data : 'authtoken', 'defaultContent':''},
			  //{data : formatUpdatesdDate, 'defaultContent':''},
		      {data : formatUpdatedDate, 'defaultContent':''},
			  {data : formatheaderkeys, 'defaultContent':''}
		 ]
	});
	$(".user-admin-tab div.toolbar").html('<h5>User Header keys</h5>');
	
}
function loadAsp_apiuserdetails(userId){
	$.ajax({
		url: contextPath+"/getasp_apiuserdetails/"+userId,
		type:'GET',
		success : function(data) {
			if(data == "" || data ==null ){
				$('#apidetailslname').text("");
				$('#apidetailsemail').text("");
				$('#apidetailsmobileno').text("");
				$('#apidetailscmpnyregname').text("");
				$('#apidetailscmpnyaddress').text("");
				$('#apidetailscdate').text("");
			}else{
				$('#apidetailsfname').text(data.firstName);
				$('#apidetailslname').text(data.lastName);
				$('#apidetailsemail').text(data.email);
				$('#apidetailsmobileno').text(data.mobileNumber);
				$('#apidetailscmpnyregname').text(data.companyRegisterName);
				$('#apidetailscmpnyaddress').text(data.companyAddress);
				$('#apidetailscdate').text(data.addressProof);
			}
		}
	});
}
function loadSuvidhaPaymentData(userId){
	$('.paymentDtClear').val('');
	$.ajax({
		url: contextPath+"/suvidhaPaymentData/"+userId,
		type:'GET',
		success : function(data) {
			if(data==0){
				$('.paymentSubscriptionType').find('option').remove().end().append("<option value=''>-- Select Type --</option><option value='NEWAPI'>NEW</option>");			
			}else{		
				$('.paymentSubscriptionType').find('option').remove().end().append("<option value=''>-- Select Type --</option><option value='TOPUPAPI'>Top-Up</option><option value='RENEWALAPI'>Renewal</option>");
			}
		}
	});
} 
function loadClientsDeatils(userId){
	var pUrl = contextPath+"/referenceclients/"+userId;
	if(partnerClientsTable){
		partnerClientsTable.clear();
		partnerClientsTable.destroy();
	}
	partnerClientsTable = $('#refClientTable').DataTable({
		"dom": 'f<"toolbar">lrtip<"clear">',
		 "processing": true,
		 "serverSide": true,
		 "lengthMenu": [ [6, 10, 25, 50], [6, 10, 25, 50] ],
	     "ajax": {
	         url: pUrl,
	         type: 'GET',
	         contentType: 'application/json; charset=utf-8',
	         dataType: "json",
	         'dataSrc': function(resp){
	        	 resp.recordsTotal = resp.totalElements;
	        	 resp.recordsFiltered = resp.totalElements;
	        	 return resp.content;
	         }
	     },
		"paging": true,
		'pageLength':5,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"order": [[1,'desc']],
		'columns': getRefClientTableColumns()
	});
	$('#refClientTable tbody').empty();
	$(".refClientTable div.toolbar").html('<h5>Clients Details </h5>');
}
function loadClientPaymentDetails(userId, month, year){
	var pUrl = contextPath+"/clientpayments/"+userId+"?month="+month+"&year="+year+"&payment=Done";
	if(clientPaymentTable){
	    clientPaymentTable.clear();
		clientPaymentTable.destroy();
	}
	clientPaymentTable = $('#clientPaymentDetailsTable').DataTable({
		"dom": 'f<"toolbar">lrtip<"clear">',
		"processing": true,
		"serverSide": true,
		"lengthMenu": [ [5, 10, 25, 50], [5, 10, 25, 50] ],
		"ajax": {
			url: pUrl,
		    type: 'GET',
		    contentType: 'application/json; charset=utf-8',
		    dataType: "json",
		    'dataSrc': function(resp){
		    	resp.recordsTotal = resp.totalElements;
		        resp.recordsFiltered = resp.totalElements;
		        return resp.content;
		    }
		},
		"paging": true,
		"pageLength":5,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"order": [[1,'desc']],
		"columns": getPartnerClientPaymentTableColumns()
	});
	$(".clientPaymentDetailsTable div.toolbar").html('<h4>Payment Details</h4>');
}
function loadPartnerPaymentDetails(userId, month, year){
	var pUrl = contextPath+"/partnerpayments/"+userId+"?month="+month+"&year="+year+"&payment=Done";
	if(partnerPaymentTable){
		partnerPaymentTable.clear();
		partnerPaymentTable.destroy();
	}
	partnerPaymentTable = $('#partnerPaymentDetailsTable').DataTable({
		"dom": 'f<"toolbar">lrtip<"clear">',
		"processing": true,
		"serverSide": true,
		"lengthMenu": [ [5, 10, 25, 50], [5, 10, 25, 50] ],
		"ajax": {
			url: pUrl,
		    type: 'GET',
		    contentType: 'application/json; charset=utf-8',
		    dataType: "json",
		    'dataSrc': function(resp){
		    	resp.recordsTotal = resp.totalElements;
		        resp.recordsFiltered = resp.totalElements;
		        return resp.content;
		    }
		},
		"paging": true,
		"pageLength":5,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"order": [[1,'desc']],
		"columns": getPartnerPaymentTableColumns('paid')
	});
}
function loadPartnerPendingPaymentDetails(userId, month, year){
	var pUrl = contextPath+"/partnerpayments/"+userId+"?month="+month+"&year="+year+"&payment=Pending";
	if(partnerPendingPaymentTable){
		partnerPendingPaymentTable.clear();
		partnerPendingPaymentTable.destroy();
	}
	partnerPendingPaymentTableUrl['pending'] = pUrl;
	
	partnerPendingPaymentTable = $('#partnerPendingPaymentDetailsTable').DataTable({
		"dom": 'f<"toolbar">lrtip<"clear">',
		"processing": true,
		"serverSide": true,
		"lengthMenu": [ [5, 10, 25, 50], [5, 10, 25, 50] ],
		"ajax": {
			url: pUrl,
		    type: 'GET',
		    contentType: 'application/json; charset=utf-8',
		    dataType: "json",
		    'dataSrc': function(resp){
		    	resp.recordsTotal = resp.totalElements;
		        resp.recordsFiltered = resp.totalElements;
		        return resp.content;
		    }
		},
		"paging": true,
		"pageLength":5,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"order": [[1,'desc']],
		"columns": getPartnerPaymentTableColumns('pending')
	});
}
function loadClientPendingPaymentDetails(userId, month, year){
	var cUrl = contextPath+"/clientpayments/"+userId+"?month="+month+"&year="+year+"&payment=Pending";
	if(clientPendingPaymentTable){
		clientPendingPaymentTable.clear();
		clientPendingPaymentTable.destroy();
	}
	clientPendingPaymentTableUrl['pending'] = cUrl;
	clientPendingPaymentTable = $('#clientPendingPaymentDetailsTable').DataTable({
		"dom": 'f<"toolbar">lrtip<"clear">',
		"processing": true,
		"serverSide": true,
		"lengthMenu": [ [5, 10, 25, 50], [5, 10, 25, 50] ],
		"ajax": {
			url: cUrl,
		    type: 'GET',
		    contentType: 'application/json; charset=utf-8',
		    dataType: "json",
		    'dataSrc': function(resp){
		    	resp.recordsTotal = resp.totalElements;
		        resp.recordsFiltered = resp.totalElements;
		        return resp.content;
		    }
		},
		"paging": true,
		"pageLength":5,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"order": [[1,'desc']],
		"columns": getPartnerClientPaymentTableColumns()
	});
	$(".clientPendingPaymentDetailsTable div.toolbar").html('<h4>Pending Payment Details</h4>');
}
 
function loadSecondary(userId){
	if ( $.fn.DataTable.isDataTable('#usersTable') ) {
		  $('#usersTable').DataTable().destroy();
	}
		$('#usersTable tbody').empty();
		usersTable = $('#usersTable').DataTable({
			"ajax": {
		         url: contextPath+"/secondaryUsers/"+userId,
		         type: 'GET'
		     },
		     "paging": true,
		     "order": [[0,'desc']],
		     'pageLength':5,
	         dom: '<"toolbar">frtip',
	         columns : [
	                      {data : 'fullname', 'defaultContent':''},
	                      {data : 'email', 'defaultContent':''},
	                      {data : 'mobilenumber', 'defaultContent':''},
						  {data : formatSecondaryUserDate, 'defaultContent':''},
	                      {data : formatSecondaryUserStatus, 'defaultContent':''},
	                      {data : 'type', 'defaultContent':''}
	                      ]
		 });
		 $(".tabUsers div.toolbar").html('<h4>Users</h4>');
	}
	function loadSuvidhaCenters(userId){
		if ( $.fn.DataTable.isDataTable('#centersTable') ) {
			  $('#centersTable').DataTable().destroy();
		}
		$('#centersTable tbody').empty();
		usersTable = $('#centersTable').DataTable({
			"ajax": {
		         url: contextPath+"/secondaryCenters/"+userId,
		         type: 'GET'
		     },
		     "paging": true,
		     "order": [[0,'desc']],
		     'pageLength':5,
	         dom: '<"toolbar">frtip',
	         columns : [
	                      {data : 'name', 'defaultContent':''},
	                      {data : 'email', 'defaultContent':''},
	                      {data : 'mobilenumber', 'defaultContent':''},
						  {data : formatSecondaryUserDate, 'defaultContent':''},
	                      {data : formatSecondaryUserStatus, 'defaultContent':''},
	                      {data : 'type', 'defaultContent':''},
	                      {data : formatRefIdAndName, 'defaultContent':''}
	                  ]
		 });
		 $(".centersTable div.toolbar").html('<h4>Centers</h4>');
	}
	function getUsersDetails(userid){
		$.ajax({
			url: contextPath+"/getusers?id="+userid,
			async: false,
			cache: false,
			success : function(data) {
				var email = data.email;
				getAllUsersDetails(email);
			}
		});
	}
	function getAllUsersDetails(email){
		$.ajax({
			url: contextPath+"/getuserDetails?email="+email,
			async: false,
			cache: false,
			success : function(data) {
				userArray = data;
				var type = data.type;
				//$('#userModal').modal('show');
				if(data.usrLastLoggedIn) {
					data.usrLastLoggedIn=data.usrLastLoggedIn;
				}
				if(data.accessGstr9) {
					data.accessGstr9=data.accessGstr9;
				}
				if(data.accessDwnldewabillinv) {
					data.accessDwnldewabillinv=data.accessDwnldewabillinv;
				}
				if(data.accessMultiGSTNSearch) {
					data.accessMultiGSTNSearch=data.accessMultiGSTNSearch;
				}
				if(data.accessReminders) {
					data.accessReminders=data.accessReminders;
				}
				if(data.accessANX1) {
					data.accessANX1=data.accessANX1;
				}
				if(data.accessANX2) {
					data.accessANX2=data.accessANX2;
				}
				if(data.accessEinvoice) {
					data.accessEinvoice=data.accessEinvoice;
				}
				if(data.accessCustomFieldTemplate) {
					data.accessCustomFieldTemplate=data.accessCustomFieldTemplate;
				}
				if(data.accessEntertainmentEinvoiceTemplate) {
					data.accessEntertainmentEinvoiceTemplate=data.accessEntertainmentEinvoiceTemplate;
				}
				if(data.accessAcknowledgement) {
					data.accessAcknowledgement=data.accessAcknowledgement;
				}
				if(data.subscriptionType) {
					data.subscriptionType=data.subscriptionType;
				}
				if(data.paidAmount) {
					data.paidAmount=data.paidAmount;
				}
				if(data.totalCenters) {
					data.totalCenters=data.totalCenters;
				}
				if(data.totalInvoices) {
					data.totalInvoices=data.totalInvoices;
				}
				if(data.totalInvoicesUsed) {
					data.totalInvoicesUsed=data.totalInvoicesUsed;
				}
				
				if(data.comments) {
					data.comments=data.comments;
				}
				
				if(data.commentDate) {
					data.commentDate=data.commentDate;
				}
				
				if(data.addedby) {
					data.addedby=data.addedby;
				}
				
				/* if(data.branches) {
					user.branches=data.branches;
				}
				if(data.anyerp) {
					user.anyerp=data.anyerp;
				} */
				if(data.partnerType) {
					data.partnerType=data.partnerType;
				}
				if(data.totalClients) {
					data.totalClients=data.totalClients;
				}
				if(data.totalInvitedClients) {
					data.totalInvitedClients=data.totalInvitedClients;
				}
				if(data.totalPendingClients) {
					data.totalPendingClients=data.totalPendingClients;
				}
				if(data.totalJoinedClients) {
					data.totalJoinedClients=data.totalJoinedClients;
				}
				if(data.totalSubscribedClients) {
					data.totalSubscribedClients=data.totalSubscribedClients;
				}
				
				if(data.gstAPIAllowedInvoices) {
					data.gstAPIAllowedInvoices=data.gstAPIAllowedInvoices;
				}
				if(data.gstSanboxAllowedCountInvoices) {
					data.gstSanboxAllowedCountInvoices=data.gstSanboxAllowedCountInvoices;
				}
				if(data.ewaybillAPIAllowedInvoices) {
					data.ewaybillAPIAllowedInvoices=data.ewaybillAPIAllowedInvoices;
				}
				if(data.ewaybillSanboxAllowedInvoices) {
					data.ewaybillSanboxAllowedInvoices=data.ewaybillSanboxAllowedInvoices;
				}
				if(data.einvAPIAllowedInvoices) {
					data.einvAPIAllowedInvoices=data.einvAPIAllowedInvoices;
				}
				if(data.einvSanboxAllowedInvoices) {
					data.einvSanboxAllowedInvoices=data.einvSanboxAllowedInvoices;
				}
				if(data.gstAPIUsageCountInvoices) {	
					data.gstAPIUsageCountInvoices=data.gstAPIUsageCountInvoices;
				}
				if(data.gstSanboxUsageCountInvoices) {
					data.gstSanboxUsageCountInvoices=data.gstSanboxUsageCountInvoices;
				}
				if(data.ewaybillAPIUsageCountInvoices) {
					data.ewaybillAPIUsageCountInvoices=data.ewaybillAPIUsageCountInvoices;
				}
				if(data.ewaybillSanboxUsageCountInvoices) {
					data.ewaybillSanboxUsageCountInvoices=data.ewaybillSanboxUsageCountInvoices;
				}
				if(data.einvAPIUsageCountInvoices) {
					data.einvAPIUsageCountInvoices=data.einvAPIUsageCountInvoices;
				}
				if(data.einvSanboxUsageCountInvoices) {
					data.einvSanboxUsageCountInvoices=data.einvSanboxUsageCountInvoices;
				}
				if(data.subscriptionStartDate){
					data.subscriptionStartDate=data.subscriptionStartDate;
				}
				if(data.subscriptionExpiryDate){
					data.subscriptionExpiryDate=data.subscriptionExpiryDate;
				}
				if(data.gstSubscriptionStartDate){
					data.gstSubscriptionStartDate=data.gstSubscriptionStartDate;;
				}
				if(data.gstSubscriptionExpiryDate){	
					data.gstSubscriptionExpiryDate=data.gstSubscriptionExpiryDate;;
				}
				if(data.gstSandboxSubscriptionStartDate){
					data.gstSandboxSubscriptionStartDate=data.gstSandboxSubscriptionStartDate;;
				}
				if(data.gstSandboxSubscriptionExpiryDate){
					data.gstSandboxSubscriptionExpiryDate=data.gstSandboxSubscriptionExpiryDate;;
				}
				if(data.ewaybillSubscriptionStartDate){
					data.ewaybillSubscriptionStartDate=data.ewaybillSubscriptionStartDate;
				}
				if(data.ewaybillSubscriptionExpiryDate){
					data.ewaybillSubscriptionExpiryDate=data.ewaybillSubscriptionExpiryDate;
				}
				if(data.ewaybillSandboxSubscriptionStartDate){
					data.ewaybillSandboxSubscriptionStartDate=data.ewaybillSandboxSubscriptionStartDate;
				}
				if(data.ewaybillSandboxSubscriptionExpiryDate){
					data.ewaybillSandboxSubscriptionExpiryDate=data.ewaybillSandboxSubscriptionExpiryDate;
				}
				if(data.einvSubscriptionStartDate){
					data.einvSubscriptionStartDate=data.einvSubscriptionStartDate;
				}
				if(data.einvSubscriptionExpiryDate){
					data.einvSubscriptionExpiryDate=data.einvSubscriptionExpiryDate;
				}
				if(data.einvSandboxSubscriptionStartDate){
					data.einvSandboxSubscriptionStartDate=data.einvSandboxSubscriptionStartDate;
				}
				if(data.ewaybillSandboxSubscriptionExpiryDate){
					data.einvSandboxSubscriptionExpiryDate=data.einvSandboxSubscriptionExpiryDate;
				}
				if(data.gstAPIStatus){
					if(data.gstAPIStatus=='EXPIRED'){
						$('#gstAPIStatus').css("color", "red");
					}
					data.gstAPIStatus=data.gstAPIStatus;
				}
				if(data.gstSandboxAPIStatus){
					if(data.gstSandboxAPIStatus=='EXPIRED'){
						$('#gstSandboxAPIStatus').css("color", "red");
					}
					data.gstSandboxAPIStatus=data.gstSandboxAPIStatus;
				}
				if(data.ewaybillAPIStatus){
					if(data.ewaybillAPIStatus=='EXPIRED'){
						$('#ewaybillAPIStatus').css("color", "red");
					}
					data.ewaybillAPIStatus=data.ewaybillAPIStatus;
				}
				if(data.ewaybillSandboxAPIStatus){
					if(data.ewaybillSandboxAPIStatus=='EXPIRED'){
						$('#ewaybillSandboxAPIStatus').css("color", "red");
					}
					data.ewaybillSandboxAPIStatus=data.ewaybillSandboxAPIStatus;
				}
				if(data.einvSandboxAPIStatus){
					if(data.einvSandboxAPIStatus=='EXPIRED'){
						$('#einvSandboxAPIStatus').css("color", "red");
					}
					data.einvSandboxAPIStatus=data.einvSandboxAPIStatus;
				}
				if(data.type){
					data.type=data.type;
				}
				if(data.needFollowupdate){
					data.needFollowupdate=data.needFollowupdate;
				}
				$('#save_btn').css('display','none');
				$('#sand_save_btn').css('display','none');
				$('#sand_edit_btn').css('display','none');
				$('#prod_save_btn').css('display','none');
				$('#prod_edit_btn').css('display','none');
				$('#eway_save_btn').css('display','none');
				$('#eway_edit_btn').css('display','none');
				$('#ewaySand_save_btn').css('display','none');
				$('#ewaySand_edit_btn').css('display','none');
				$('#edit_btn').css('display','block');
				$('#addedby').val(fullName);
				var cdate = new Date();
				var mnth = cdate.getMonth() + 1;
				var cmntdate = cdate.getDate()+"/"+mnth+"/"+cdate.getFullYear();
				$('#commentsDate').val(cmntdate);
				if(type == 'aspdeveloper') {
					currentUser = data.password;
					tabActivities(type);
					loadPayments(data.password);
					loadComments(data.password);
					loadUsageInvoices(data.password);
					loadHeaderKeys(data.password);
					loadAsp_apiuserdetails(data.password);
					//loadewayPayments(currentUser);
					
					$('.usercomment_field').each(function(){ 
						var field = $(this).attr('data');
						var value=data[field];
						if(value==""){
							$(this).html('');
						}else{
							$(this).html(value);
						}
					});
			
					$('.user_field').each(function() {  
						var field = $(this).attr('data');
						var value='';
						value = data[field];
						if(field == 'disable') {
							if(data.disable == 'true') {
								$('#disable').prop("checked", true);
							} else {
								$('#disable').prop("checked", false);
							}
						}
						
						if(field == 'quotationSent' && value == 'false'){
							value ='No';
						}else if(field == 'quotationSent' && value == 'true'){
							value ='Yes';
						}
						
						if(field == 'needFollowup' && value == 'false'){
							value ='No';
							$('#enableNeed_followupsDate').addClass('d-none');
						}else if(field == 'needFollowup' && value == 'true'){
							value ='Yes';
							$('#enableNeed_followupsDate').removeClass('d-none');
						}
						
						if(field == 'disable' && value == 'false'){
							value ='Active';
						}else if(field == 'disable' && value == 'true'){
							value ='InActive';
						}else if(field == 'disable' && value == null){
							value ='InActive';
						}else if(field == 'gstAPIUsageCountInvoices' && value == null){
							value ='0';
						}else if(field == 'gstSanboxUsageCountInvoices' && value == null){
							value ='0';
						}else if(field == 'ewaybillAPIUsageCountInvoices' && value == null){
							value ='0';
						}else if(field == 'ewaybillSanboxUsageCountInvoices' && value == null){
							value ='0';
						}else{
							value = value;
						}
						$(this).html(value);
					});
					$('.userprod_field').each(function() {
						var field = $(this).attr('data');
						var value='';
						if(data.userkeys) {
							data.userkeys.forEach(function(userkey) {
								if(userkey.stage == 'Production') {
									value = userkey[field];
									if(userkey.isenabled == true) {
										$('#productionRadio').prop("checked", true);
									} else {
										$('#productionRadio').prop("checked", false);
									}
								}
							});
						}
						if(field == 'isenabled' && value == false){
							value ='No';
						}else if(field == 'isenabled' && value == true){
							value ='Yes';
						}else{
							value = value;
						}
						$(this).html(value);
					});
					$('.userpeway_field').each(function() {
						var field = $(this).attr('data');
						var value='';
						if(data.userkeys) {
							data.userkeys.forEach(function(userkey) {
								if(userkey.stage == 'EwayBillProduction') {
									value = userkey[field];
									if(userkey.isenabled == true) {
										$('#ewayRadio').prop("checked", true);
									} else {
										$('#ewayRadio').prop("checked", false);
									}
								}
							});
						}
						if(field == 'isenabled' && value == false){
							value ='No';
						}else if(field == 'isenabled' && value == true){
							value ='Yes';
						}else{
							value = value;
						}
						$(this).html(value);
					});
					$('.userseway_field').each(function() {
						var field = $(this).attr('data');
						var value='';
						if(data.userkeys) {
							data.userkeys.forEach(function(userkey) {
								if(userkey.stage == 'EwayBillSandBox') {
									value = userkey[field];
									if(userkey.isenabled == true) {
										$('#ewayRadio').prop("checked", true);
									} else {
										$('#ewayRadio').prop("checked", false);
									}
								}
							});
						}
						if(field == 'isenabled' && value == false){
							value ='No';
						}else if(field == 'isenabled' && value == true){
							value ='Yes';
						}else{
							value = value;
						}
						$(this).html(value);
					});
					$('.usersandbox_field').each(function() {
							var field = $(this).attr('data');
							var value='';
							if(data.userkeys) {
								data.userkeys.forEach(function(userkey) {
									if(userkey.stage == 'Sandbox') {
										value = userkey[field];
										if(userkey.isenabled == true) {
											$('#sandboxRadio').prop("checked", true);
										} else {
											$('#sandboxRadio').prop("checked", false);
										}
									}
								});
							}
							if(field == 'isenabled' && value == false){
								value ='No';
							}else if(field == 'isenabled' && value == true){
								value ='Yes';
							}else{
								value = value;
							}
							$(this).html(value);
						});
					$('.userseinv_field').each(function() {
						var field = $(this).attr('data');
						var value='';
						if(data.userkeys) {
							data.userkeys.forEach(function(userkey) {
								if(userkey.stage == 'EInvoiceSandBox') {
									value = userkey[field];
									if(userkey.isenabled == true) {
										$('#ewayRadio').prop("checked", true);
									} else {
										$('#ewayRadio').prop("checked", false);
									}
								}
							});
						}
						if(field == 'isenabled' && value == false){
							value ='No';
						}else if(field == 'isenabled' && value == true){
							value ='Yes';
						}else{
							value = value;
						}
						$(this).html(value);
					});
					$('.userpeinv_field').each(function() {
						var field = $(this).attr('data');
						var value='';
						if(data.userkeys) {
							data.userkeys.forEach(function(userkey) {
								if(userkey.stage == 'EInvoiceProduction') {
									value = userkey[field];
									if(userkey.isenabled == true) {
										$('#ewayRadio').prop("checked", true);
									} else {
										$('#ewayRadio').prop("checked", false);
									}
								}
							});
						}
						if(field == 'isenabled' && value == false){
							value ='No';
						}else if(field == 'isenabled' && value == true){
							value ='Yes';
						}else{
							value = value;
						}
						$(this).html(value);
					});
				}else if(type == 'business'){
					currentUser = data.password;
					tabActivities(type);
					loadPayments(data.password);
					loadComments(data.password);
					loadSecondary(data.password);
					loadHeaderKeys(data.password);
					$('.usercomment_field').each(function(){ 
						var field = $(this).attr('data');
						var value=data[field];
						if(value==""){
							$(this).html('');
						}else{
							$(this).html(value);
						}
					});
					
					$('.user_field').each(function() {
						var field = $(this).attr('data');
						var value='';
						value = data[field];
						if(field == 'disable') {
							if(data.disable == 'true') {
								$('#disable').prop("checked", true);
							} else {
								$('#disable').prop("checked", false);
							}
						}
						
						if(field == 'quotationSent' && value == 'false'){
							value ='No';
						}else if(field == 'quotationSent' && value == 'true'){
							value ='Yes';
						}			
						
						if(field == 'needFollowup' && value == 'false'){
							value ='No';
							$('#enableNeed_followupsDate').addClass('d-none');
						}else if(field == 'needFollowup' && value == 'true'){
							value ='Yes';
							$('#enableNeed_followupsDate').removeClass('d-none');
						}
						
						if(field == 'disable' && value == 'false'){
							value ='Active';
						}else if(field == 'disable' && value == 'true'){
							value ='InActive';
						}else if(field == 'disable' && value == null){
							value ='InActive';
						}else{
							value = value;
						}
						if(field == 'accessJournalExcel' && value == false){
							value = 'Disabled';	
						}else if(field == 'accessJournalExcel' && value == null){
							value = 'Disabled';	
						}else if(field == 'accessJournalExcel' && value == true){
							value = 'Enabled';	
						}
						if(field == 'accessGstr9' && value == false){
							value ='Disabled';
						}else if(field == 'accessGstr9' && value == null){
							value = 'Disabled';
						}else if(field == 'accessGstr9' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessDwnldewabillinv' && value == false){
							value ='Disabled';
						}else if(field == 'accessDwnldewabillinv' && value == null){
							value = 'Disabled';
						}else if(field == 'accessDwnldewabillinv' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessMultiGSTNSearch' && value == false){
							value ='Disabled';
						}else if(field == 'accessMultiGSTNSearch' && value == null){
							value = 'Disabled';
						}else if(field == 'accessMultiGSTNSearch' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessReminders' && value == false){
							value ='Disabled';
						}else if(field == 'accessReminders' && value == null){
							value = 'Disabled';
						}else if(field == 'accessReminders' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessANX1' && value == false){
							value ='Disabled';
						}else if(field == 'accessANX1' && value == null){
							value = 'Disabled';
						}else if(field == 'accessANX1' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessANX2' && value == false){
							value ='Disabled';
						}else if(field == 'accessANX2' && value == null){
							value = 'Disabled';
						}else if(field == 'accessANX2' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessEinvoice' && value == false){
							value ='Disabled';
						}else if(field == 'accessEinvoice' && value == null){
							value = 'Disabled';
						}else if(field == 'accessEinvoice' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessCustomFieldTemplate' && value == false){
							value ='Disabled';
						}else if(field == 'accessCustomFieldTemplate' && value == null){
							value = 'Disabled';
						}else if(field == 'accessCustomFieldTemplate' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessEntertainmentEinvoiceTemplate' && value == false){
							value ='Disabled';
						}else if(field == 'accessEntertainmentEinvoiceTemplate' && value == null){
							value = 'Disabled';
						}else if(field == 'accessEntertainmentEinvoiceTemplate' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessAcknowledgement' && (value == false || value == null)){
							value = 'Disabled';
						}else if(field == 'accessAcknowledgement' && value == true){
							value = 'Enabled';
						}
						$(this).html(value);
					});
				}else if(type == 'taxp'){
					currentUser = data.password;
					tabActivities(type);
					loadPayments(data.password);
					loadComments(data.password);
					loadSecondary(data.password);
					loadHeaderKeys(data.password);
					$('.usercomment_field').each(function(){ 
						var field = $(this).attr('data');
						var value=data[field];
						if(value==""){
							$(this).html('');
						}else{
							$(this).html(value);
						}
					});
					
					$('.user_field').each(function() {
						var field = $(this).attr('data');
						var value='';
						value = data[field];
						if(field == 'disable') {
							if(data.disable == 'true') {
								$('#disable').prop("checked", true);
							} else {
								$('#disable').prop("checked", false);
							}
						}
						
						if(field == 'quotationSent' && value == 'false'){
							value ='No';
						}else if(field == 'quotationSent' && value == 'true'){
							value ='Yes';
						}
						
						if(field == 'needFollowup' && value == 'false'){
							value ='No';
							$('#enableNeed_followupsDate').addClass('d-none');
						}else if(field == 'needFollowup' && value == 'true'){
							value ='Yes';
							$('#enableNeed_followupsDate').removeClass('d-none');
						}
						
						if(field == 'disable' && value == 'false'){
							value ='Active';
						}else if(field == 'disable' && value == 'true'){
							value ='InActive';
						}else if(field == 'disable' && value == null){
							value ='InActive';
						}else{
							value = value;
						}
						$(this).html(value);
					});
				}else if(type == 'enterprise'){
					currentUser = data.password;
					tabActivities(type);
					loadPayments(data.password);
					loadComments(data.password);
					loadSecondary(data.password);
					loadEnterprisePayments(data.password);
					loadHeaderKeys(data.password);
					$('.usercomment_field').each(function(){ 
						var field = $(this).attr('data');
						var value=data[field];
						if(value==""){
							$(this).html('');
						}else{
							$(this).html(value);
						}
							
					});
					
					$('.user_field').each(function() {
						var field = $(this).attr('data');
						var value='';
						value = data[field];
						if(field == 'disable') {
							if(data.disable == 'true') {
								$('#disable').prop("checked", true);
							} else {
								$('#disable').prop("checked", false);
							}
						}
						if(field == 'quotationSent' && value == 'false'){
							value ='No';
						}else if(field == 'quotationSent' && value == 'true'){
							value ='Yes';
						}
						
						if(field == 'needFollowup' && value == 'false'){
							value ='No';
							$('#enableNeed_followupsDate').addClass('d-none');
						}else if(field == 'needFollowup' && value == 'true'){
							value ='Yes';
							$('#enableNeed_followupsDate').removeClass('d-none');
						}
						if(field == 'accessGstr9' && value == false){
							value ='Disabled';
						}else if(field == 'accessGstr9' && value == null){
							value = 'Disabled';
						}else if(field == 'accessGstr9' && value == true){
							value = 'Enabled';
						}
						
						/*if(field == 'accessGstr4Annual' && value == false){
							value ='Disabled';
						}else if(field == 'accessGstr4Annual' && value == null){
							value = 'Disabled';
						}else if(field == 'accessGstr4Annual' && value == true){
							value = 'Enabled';
						}
						
						if(field == 'accessGstr6' && value == false){
							value ='Disabled';
						}else if(field == 'accessGstr6' && value == null){
							value = 'Disabled';
						}else if(field == 'accessGstr6' && value == true){
							value = 'Enabled';
						}*/
						
						if(field == 'accessGstr8' && value == false){
							value ='Disabled';
						}else if(field == 'accessGstr8' && value == null){
							value = 'Disabled';
						}else if(field == 'accessGstr8' && value == true){
							value = 'Enabled';
						}
						
						
						
						if(field == 'accessDwnldewabillinv' && value == false){
							value ='Disabled';
						}else if(field == 'accessDwnldewabillinv' && value == null){
							value = 'Disabled';
						}else if(field == 'accessDwnldewabillinv' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessMultiGSTNSearch' && value == false){
							value ='Disabled';
						}else if(field == 'accessMultiGSTNSearch' && value == null){
							value = 'Disabled';
						}else if(field == 'accessMultiGSTNSearch' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessReminders' && value == false){
							value ='Disabled';
						}else if(field == 'accessReminders' && value == null){
							value = 'Disabled';
						}else if(field == 'accessReminders' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessANX1' && value == false){
							value ='Disabled';
						}else if(field == 'accessANX1' && value == null){
							value = 'Disabled';
						}else if(field == 'accessANX1' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessANX2' && value == false){
							value ='Disabled';
						}else if(field == 'accessANX2' && value == null){
							value = 'Disabled';
						}else if(field == 'accessANX2' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessEinvoice' && value == false){
							value ='Disabled';
						}else if(field == 'accessEinvoice' && value == null){
							value = 'Disabled';
						}else if(field == 'accessEinvoice' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessCustomFieldTemplate' && value == false){
							value ='Disabled';
						}else if(field == 'accessCustomFieldTemplate' && value == null){
							value = 'Disabled';
						}else if(field == 'accessCustomFieldTemplate' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessEntertainmentEinvoiceTemplate' && value == false){
							value ='Disabled';
						}else if(field == 'accessEntertainmentEinvoiceTemplate' && value == null){
							value = 'Disabled';
						}else if(field == 'accessEntertainmentEinvoiceTemplate' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessAcknowledgement' && (value == false|| value == null)){
							value = 'Disabled';
						}else if(field == 'accessAcknowledgement' && value == true){
							value = 'Enabled';
						}
						if(field == 'disable' && value == 'false'){
							value ='Active';
						}else if(field == 'disable' && value == 'true'){
							value ='InActive';
						}else if(field == 'disable' && value == null){
							value ='InActive';
						}else{
							value = value;
						}
						$(this).html(value);
					});
				}else if(type == "partner"){
					/*$('#FullName').text(data.fullname);
					$('#EmailID').text(data.email);
					$('#Mnumber').text(data.mobilenumber);
					$('#UserTypeChange').text(data.type);
					$('#Status').text(data.type);
					$('#login-details').html(data.usrLastLoggedIn);*/
					currentUser = data.password;
					tabActivities(type);
					loadPartnerBankdetails(data.password);
					loadPayments(data.password);
					loadComments(data.password);
					loadClientsDeatils(data.password);
					//loadClientPaymentDetails(data.password);
					loadClientPendingPaymentDetails(data.password);
					
					$('.usercomment_field').each(function(){ 
						var field = $(this).attr('data');
						
						var value=data[field];
						if(value==""){
							$(this).html('');
						}else{
							$(this).html(value);
						}
							
					});
					
					$('.user_field').each(function() {
						var field = $(this).attr('data');
						var value='';
						value = data[field];
						if(field == 'disable') {
							if(data.disable == 'true') {
								$('#disable').prop("checked", true);
							} else {
								$('#disable').prop("checked", false);
							}
						}
						
						if(field == 'quotationSent' && value == 'false'){
							value ='No';
						}else if(field == 'quotationSent' && value == 'true'){
							value ='Yes';
						}
						
						if(field == 'needFollowup' && value == 'false'){
							value ='No';
							$('#enableNeed_followupsDate').addClass('d-none');
						}else if(field == 'needFollowup' && value == 'true'){
							value ='Yes';
							$('#enableNeed_followupsDate').removeClass('d-none');
						}
						
						if(field == 'disable' && value == 'false'){
							value ='Active';
						}else if(field == 'disable' && value == 'true'){
							value ='InActive';
						}else if(field == 'disable' && value == null){
							value ='InActive';
						}else{
							value = value;
						}
						$(this).html(value);
					});
				}else if(type == 'suvidha'){
					currentUser = data.password;
					tabActivities(type);
					loadPayments(data.password);
					loadComments(data.password);
					loadSuvidhaPaymentData(data.password);
					loadSecondary(data.password);
					loadSuvidhaCenters(data.password);
					loadHeaderKeys(data.password);
					$('.usercomment_field').each(function(){ 
						var field = $(this).attr('data');
						var value=data[field];
						if(value==""){
							$(this).html('');
						}else{
							$(this).html(value);
						}
					});
					
					$('.user_field').each(function() {
						var field = $(this).attr('data');
						var value='';
						value = data[field];
						if(field == 'disable') {
							if(data.disable == 'true') {
								$('#disable').prop("checked", true);
							} else {
								$('#disable').prop("checked", false);
							}
						}
						if(field == 'quotationSent' && value == 'false'){
							value ='No';
						}else if(field == 'quotationSent' && value == 'true'){
							value ='Yes';
						}
						
						if(field == 'needFollowup' && value == 'false'){
							value ='No';
							$('#enableNeed_followupsDate').addClass('d-none');
						}else if(field == 'needFollowup' && value == 'true'){
							value ='Yes';
							$('#enableNeed_followupsDate').removeClass('d-none');
						}
						
						if(field == 'disable' && value == 'false'){
							value ='Active';
						}else if(field == 'disable' && value == 'true'){
							value ='InActive';
						}else if(field == 'disable' && value == null){
							value ='InActive';
						}else{
							value = value;
						}
						if(field == 'accessGstr9' && value == false){
							value ='Disabled';
						}else if(field == 'accessGstr9' && value == null){
							value = 'Disabled';
						}else if(field == 'accessGstr9' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessDwnldewabillinv' && value == false){
							value ='Disabled';
						}else if(field == 'accessDwnldewabillinv' && value == null){
							value = 'Disabled';
						}else if(field == 'accessDwnldewabillinv' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessMultiGSTNSearch' && value == false){
							value ='Disabled';
						}else if(field == 'accessMultiGSTNSearch' && value == null){
							value = 'Disabled';
						}else if(field == 'accessMultiGSTNSearch' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessReminders' && value == false){
							value ='Disabled';
						}else if(field == 'accessReminders' && value == null){
							value = 'Disabled';
						}else if(field == 'accessReminders' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessANX1' && value == false){
							value ='Disabled';
						}else if(field == 'accessANX1' && value == null){
							value = 'Disabled';
						}else if(field == 'accessANX1' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessANX2' && value == false){
							value ='Disabled';
						}else if(field == 'accessANX2' && value == null){
							value = 'Disabled';
						}else if(field == 'accessANX2' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessEinvoice' && value == false){
							value ='Disabled';
						}else if(field == 'accessEinvoice' && value == null){
							value = 'Disabled';
						}else if(field == 'accessEinvoice' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessCustomFieldTemplate' && value == false){
							value ='Disabled';
						}else if(field == 'accessCustomFieldTemplate' && value == null){
							value = 'Disabled';
						}else if(field == 'accessCustomFieldTemplate' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessEntertainmentEinvoiceTemplate' && value == false){
							value ='Disabled';
						}else if(field == 'accessEntertainmentEinvoiceTemplate' && value == null){
							value = 'Disabled';
						}else if(field == 'accessEntertainmentEinvoiceTemplate' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessAcknowledgement' && value == false || value == null){
							value = 'Disabled';
						}else if(field == 'accessAcknowledgement' && value == true){
							value = 'Enabled';
						}
						
						$(this).html(value);
					});
				}else if(type == 'cacmas'){
					currentUser = data.password;
					tabActivities(data.type);
					loadPayments(data.password);
					loadComments(data.password);
					loadSecondary(data.password);
					loadHeaderKeys(data.password);
					$('.usercomment_field').each(function(){ 
						var field = $(this).attr('data');
						var value=data[field];
						if(value==""){
							$(this).html('');
						}else{
							$(this).html(value);
						}
					});
					
					$('.user_field').each(function() {
						var field = $(this).attr('data');
						var value='';
						value = data[field];		
						if(field == 'accessDrive' && value == false){
							value ='Disabled';
						}else if(field == 'accessDrive' && value == null){
							value = 'Disabled';	
						}else if(field == 'accessDrive' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessTravel' && value == false){
							value ='Disabled';
						}else if(field == 'accessTravel' && value == null){
							value = 'Disabled';	
						}else if(field == 'accessTravel' && value == true){
							value = 'Enabled';
						}
						if(field == 'quotationSent' && value == 'false'){
							value ='No';
						}else if(field == 'quotationSent' && value == 'true'){
							value ='Yes';
						}				
						
						if(field == 'needFollowup' && value == 'false'){
							value ='No';
							$('#enableNeed_followupsDate').addClass('d-none');
						}else if(field == 'needFollowup' && value == 'true'){
							value ='Yes';
							$('#enableNeed_followupsDate').removeClass('d-none');
						}
						
						if(field == 'accessImports' && value == false){
							value = 'Disabled';	
						}else if(field == 'accessImports' && value == null){
							value = 'Disabled';	
						}else if(field == 'accessImports' && value == true){
							value = 'Enabled';
						}
						
						if(field == 'accessReports' && value == false){
							value = 'Disabled';	
						}else if(field == 'accessReports' && value == null){
							value = 'Disabled';	
						}else if(field == 'accessReports' && value == true){
							value = 'Enabled';	
						}
						if(field == 'accessJournalExcel' && value == false){
							value = 'Disabled';	
						}else if(field == 'accessJournalExcel' && value == null){
							value = 'Disabled';	
						}else if(field == 'accessJournalExcel' && value == true){
							value = 'Enabled';	
						}
						if(field == 'accessGstr9' && value == false){
							value ='Disabled';
						}else if(field == 'accessGstr9' && value == null){
							value = 'Disabled';
						}else if(field == 'accessGstr9' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessDwnldewabillinv' && value == false){
							value ='Disabled';
						}else if(field == 'accessDwnldewabillinv' && value == null){
							value = 'Disabled';
						}else if(field == 'accessDwnldewabillinv' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessMultiGSTNSearch' && value == false){
							value ='Disabled';
						}else if(field == 'accessMultiGSTNSearch' && value == null){
							value = 'Disabled';
						}else if(field == 'accessMultiGSTNSearch' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessReminders' && value == false){
							value ='Disabled';
						}else if(field == 'accessReminders' && value == null){
							value = 'Disabled';
						}else if(field == 'accessReminders' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessANX1' && value == false){
							value ='Disabled';
						}else if(field == 'accessANX1' && value == null){
							value = 'Disabled';
						}else if(field == 'accessANX1' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessANX2' && value == false){
							value ='Disabled';
						}else if(field == 'accessANX2' && value == null){
							value = 'Disabled';
						}else if(field == 'accessANX2' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessEinvoice' && value == false){
							value ='Disabled';
						}else if(field == 'accessEinvoice' && value == null){
							value = 'Disabled';
						}else if(field == 'accessEinvoice' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessCustomFieldTemplate' && value == false){
							value ='Disabled';
						}else if(field == 'accessCustomFieldTemplate' && value == null){
							value = 'Disabled';
						}else if(field == 'accessCustomFieldTemplate' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessEntertainmentEinvoiceTemplate' && value == false){
							value ='Disabled';
						}else if(field == 'accessEntertainmentEinvoiceTemplate' && value == null){
							value = 'Disabled';
						}else if(field == 'accessEntertainmentEinvoiceTemplate' && value == true){
							value = 'Enabled';
						}
						if(field == 'accessAcknowledgement' && (value == false || value == null)){
							value ='Disabled';
						}else if(field == 'accessAcknowledgement' && value == true){
							value = 'Enabled';
						}
						if(field == 'disable') {
							if(data.disable == 'true') {
								$('#disable').prop("checked", true);
							} else {
								$('#disable').prop("checked", false);
							}
						}
						if(field == 'disable' && value == 'false'){
							value ='Active';
						}else if(field == 'disable' && value == 'true'){
							value ='InActive';
						}else if(field == 'disable' && value == null){
							value ='InActive';
						}else{
							value = value;
						}
						$(this).html(value);
					});
				}
			}
		});
	}
	function openPaymentTab(name,email){
		$('#SubscriptionStartDate').val('');
		$('#SubscriptionExpiryDate').val('');
		$('#div_suvidhacentername').removeClass('d-none');
		$('#suvidhacentername').find('option').remove().end().append("<option value='"+name+"'>"+name+"</option>");
		$.ajax({
			url: contextPath+"/suvidhacenterpayments?email="+email,
			type: "GET",
			success : function(data) {
				if(data==""){
					$('#suvidhaPaymentCenters').addClass('d-none');
					$('.suvidhasubcenter_id').val(data.userid);
					var newDate = new Date();
					
					var day=newDate.getDate();
					var month=newDate.getMonth()+1;
					
					var year=newDate.getFullYear();
					var day1=newDate.getDate()-1;
					var year1=newDate.getFullYear()+1;
							
					var date1=day1+'-'+month+'-'+year1;
					var date=day+'-'+month+'-'+year;
					$('#SubscriptionStartDate').val(date);
					$('#SubscriptionExpiryDate').val(date1);
					$('.paymentSubscriptionType').find('option').remove().end().append("<option value='NEW'>NEW</option>");
				}else{
					$('#SubscriptionStartDate').val(formatSubscriptionDate(data.registeredDate));
					$('#SubscriptionExpiryDate').val(formatSubscriptionDate(data.expiryDate));
					$('.paymentSubscriptionType').find('option').remove().end().append("<option value='TOPUPAPI'>Top-Up</option><option value='RENEWALAPI'>Renewal</option>");
					
				}
			}
		});
		
		$('a.payment_tab').tab('show');
		$('.payment_details').addClass('active');
		$('#centersTab').css("display","none");
		$('#payment').css("display","block");
	}
	function saveDetails(){
		$(".loader-imger").addClass("btn-loader");
		var userDetails = new Object;
		var fullname = $('#userfullname').val();
		var email = $('#useremail').val();
		var mobilenumber = $('#usermobilenumber').val();
		var partnerEmail=$('#userpartnerEmail').val();
		var agreementstatus=$('#useragreementStatus').val();
		var needtofollowup=$('#userneedToFollowUp').val();
		var needtofollowupcomment=$('#userneedToFollowUpComment').val();
		var needfollowupdate=$('#userneedFollowupdate').val();
		var sandboxApplieddata=$('#usersandboxApplied').val();
		var pType = $('#usersPartnerType').val();
		var gstin_number_input=$("#userGSTIN").val();
		
		var pan_number_input=$("#userPAN").val();
		var authorised_signatory_input=$("#userauthorisedSignatory").val();
		var authorised_pan_number_input=$("#userauthorisedPANNumber").val();
		var business_name_input=$("#userbusinessName").val();
		var dealer_type_input=$("#userdealerType").val();
		var input_state_name=$("#userstateName").val();
					
		userDetails.type = $('#usrtype_change').val();
		
		var partnerPercentage = $('#userpartnerPercentage').val();
		userDetails.disable = !$('#disable').is(':checked');
		userDetails.accessDrive = $('#accessDrive').is(':checked');
		userDetails.accessTravel = $('#accessTravel').is(':checked');
		userDetails.accessGstr9 = $('#accessGstr9').is(':checked');
		userDetails.accessDwnldewabillinv = $('#accessDwnldewabillinv').is(':checked');
		userDetails.accessMultiGSTNSearch = $('#accessMultiGSTNSearch').is(':checked');
		userDetails.accessReminders = $('#accessReminders').is(':checked');
		userDetails.accessANX1 = $('#accessANX1').is(':checked');
		userDetails.accessANX2 = $('#accessANX2').is(':checked');
		userDetails.accessEinvoice = $('#accessEinvoice').is(':checked');
		userDetails.accessCustomFieldTemplate = $('#accessCustomFieldTemplate').is(':checked');
		userDetails.accessEntertainmentEinvoiceTemplate = $('#accessEntertainmentEinvoiceTemplate').is(':checked');
		userDetails.accessAcknowledgement = $('#accessAcknowledgement').is(':checked');
		userDetails.accessImports = $('#accessImports').is(':checked');
		userDetails.accessReports = $('#accessReports').is(':checked');
		userDetails.quotationSent = $('#quotationsSent').is(':checked');
		
		userDetails.needFollowup = $('#need_follow_up').is(':checked');
		userDetails.accessJournalExcel = $('#accessJournalExcel').is(':checked');
		userDetails.needToFollowUp = needtofollowup; 
		userDetails.agreementStatus=agreementstatus;
		userDetails.sandboxApplied=sandboxApplieddata;
		userDetails.needFollowupdate=needfollowupdate;
		userDetails.gstin=gstin_number_input;
		userDetails.pan=pan_number_input;
		userDetails.authorisedSignatory=authorised_signatory_input;
		userDetails.authorisedPANNumber=authorised_pan_number_input;
		userDetails.businessName=business_name_input;
		userDetails.dealerType=dealer_type_input;
		userDetails.stateName=input_state_name;
		userDetails.partnerEmail=partnerEmail;
		if(needtofollowup == 'Duplicate' || needtofollowup == 'Not Required'){
			userDetails.needToFollowUpComment = needtofollowupcomment;
			$(".needToFollowUpComment").css("display","block");
		}else{
			$(".needToFollowUpComment").css("display","none");
			userDetails.needToFollowUpComment = "";
		}
		$('.user_details_field').each(function() {
			var field = $(this).attr('data');
			if(field != 'createdate' && field != 'disable' && field != 'accessDrive' && field != 'accessTravel' && field != 'accessImports' && field != 'accessReports' && field != 'needToFollowUpComment' && field != 'quotationSent' && field != 'needFollowup' && field != 'accessJournalExcel' && field != 'accessGstr9' && field != 'accessMultiGSTNSearch' && field != 'accessDwnldewabillinv' && field != 'accessReminders' && field != 'accessANX1' && field != 'accessANX2' && field != 'accessEinvoice' && field != 'accessCustomFieldTemplate' && field != 'accessEntertainmentEinvoiceTemplate' && field != 'accessAcknowledgement') {
				userDetails[field]=$('#user'+field).val();
			}
			$(this).html(userDetails[field]);
			if(field == 'disable' && userDetails.disable.toString() == 'false'){
				$(this).html("Active");
			}else if(field == 'disable' && userDetails.disable.toString() == 'true'){
				$(this).html("InActive");
			}
			if(field == 'accessDrive' && userDetails.accessDrive.toString() == 'false'){
				$(this).html("false");
			}else if(field == 'accessDrive' && userDetails.accessDrive.toString() == 'true'){
				$(this).html("true");
			}
			if(field == 'accessTravel' && userDetails.accessTravel.toString() == 'false'){
				$(this).html("false");
			}else if(field == 'accessTravel' && userDetails.accessTravel.toString() == 'true'){
				$(this).html("true");
			}
			if(field == 'accessGstr9' && userDetails.accessGstr9.toString() == 'false'){
				$(this).html("false");
			}else if(field == 'accessGstr9' && userDetails.accessGstr9.toString() == 'true'){
				$(this).html("true");
			}
			if(field == 'accessDwnldewabillinv' && userDetails.accessDwnldewabillinv.toString() == 'false'){
				$(this).html("false");
			}else if(field == 'accessDwnldewabillinv' && userDetails.accessDwnldewabillinv.toString() == 'true'){
				$(this).html("true");
			}
			if(field == 'accessMultiGSTNSearch' && userDetails.accessMultiGSTNSearch.toString() == 'false'){
				$(this).html("false");
			}else if(field == 'accessMultiGSTNSearch' && userDetails.accessMultiGSTNSearch.toString() == 'true'){
				$(this).html("true");
			}
			if(field == 'accessReminders' && userDetails.accessReminders.toString() == 'false'){
				$(this).html("false");
			}else if(field == 'accessReminders' && userDetails.accessReminders.toString() == 'true'){
				$(this).html("true");
			}
			if(field == 'accessANX1' && userDetails.accessANX1.toString() == 'false'){
				$(this).html("false");
			}else if(field == 'accessANX1' && userDetails.accessANX1.toString() == 'true'){
				$(this).html("true");
			}
			if(field == 'accessANX2' && userDetails.accessANX2.toString() == 'false'){
				$(this).html("false");
			}else if(field == 'accessANX2' && userDetails.accessANX2.toString() == 'true'){
				$(this).html("true");
			}
			if(field == 'accessEinvoice' && userDetails.accessEinvoice.toString() == 'false'){
				$(this).html("false");
			}else if(field == 'accessEinvoice' && userDetails.accessEinvoice.toString() == 'true'){
				$(this).html("true");
			}
			if(field == 'accessCustomFieldTemplate' && userDetails.accessCustomFieldTemplate.toString() == 'false'){
				$(this).html("false");
			}else if(field == 'accessCustomFieldTemplate' && userDetails.accessCustomFieldTemplate.toString() == 'true'){
				$(this).html("true");
			}
			if(field == 'accessEntertainmentEinvoiceTemplate' && userDetails.accessEntertainmentEinvoiceTemplate.toString() == 'false'){
				$(this).html("false");
			}else if(field == 'accessEntertainmentEinvoiceTemplate' && userDetails.accessEntertainmentEinvoiceTemplate.toString() == 'true'){
				$(this).html("true");
			}
			if(field == 'accessAcknowledgement' && userDetails.accessAcknowledgement.toString() == 'false'){
				$(this).html("false");
			}else if(field == 'accessAcknowledgement' && userDetails.accessAcknowledgement.toString() == 'true'){
				$(this).html("true");
			}
			if(field == 'accessImports' && userDetails.accessImports.toString() == 'false'){
				$(this).html("false");
			}else if(field == 'accessImports' && userDetails.accessImports.toString() == 'true'){
				$(this).html("true");
			}
			if(field == 'accessReports' && userDetails.accessReports.toString() == 'false'){
				$(this).html("false");
			}else if(field == 'accessReports' && userDetails.accessReports.toString() == 'true'){
				$(this).html("true");
			}
			
			if(field == 'quotationsSent' && userDetails.quotationSent.toString() == 'false'){
				$(this).html("No");
			}else if(field == 'quotationsSent' && userDetails.quotationSent.toString() == 'true'){
				$(this).html("Yes");
			}
			
			if(field == 'needFollowup' && userDetails.needFollowup.toString() == 'false'){
				$(this).html("No");
				$('#enableNeed_followupsDate').addClass('d-none');
			}else if(field == 'needFollowup' && userDetails.needFollowup.toString() == 'true'){
				$(this).html("Yes");
				$('#enableNeed_followupsDate').removeClass('d-none');
				$('#Need_FollowUp_date').val(userDetails.needFollowupdate);
			}
			
			if(field == 'accessJournalExcel' && userDetails.accessJournalExcel.toString() == 'false'){
				$(this).html("false");
			}else if(field == 'accessJournalExcel' && userDetails.accessJournalExcel.toString() == 'true'){
				$(this).html("true");
			}
		});
		userDetails.partnerType = pType;
		$('#PartnerType').text(pType);
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : 'updtuserdtls?id='+currentUser,
			data : JSON.stringify(userDetails),
			dataType : 'json',
			success : function(dat) {
				$(".loader-imger").removeClass("btn-loader");
				table.row(selRow).data(dat).draw();
				$('#save_btn').css('display','none');
				$('#edit_btn').css('display','block');
			}
		});
	}
	function editDetails() {
		   $('#save_btn').css('display','block');
		   $('#edit_btn').css('display','none');
		   $('.needToFollowUpComment').removeClass('d-none');
		   $(".needToFollowUpComment").css("display","none");
		   var name=$("#name span").text();
		   var email=$("#emailID span").text();
		   var mnumber=$("#mnumber span").text();
		   var status=$("#status span").text();
		   var enableDocs=$("#enableDocs span").text();
		   var enableImports=$("#enableImports span").text();
		   var enableReports=$("#enableReports span").text();
		   var enableGstr9=$("#enableGstr9 span").text();
		   var enableDwnldewabillinv=$("#enableDwnldewabillinv span").text();
		   var enableMultiGSTNSearch=$("#enableMultiGSTNSearch span").text();
		   var enableReminders=$("#enableReminders span").text();
		   var enableANX1=$("#enableANX1 span").text();
		   var enableANX2=$("#enableANX2 span").text();
		   var enableEinvoice=$("#enableEinvoice span").text();
		   var enableCustomFieldTemplate=$("#enableCustomFieldTemplate span").text();
		   var enableEntertainmentEinvoiceTemplate=$("#enableEntertainmentEinvoiceTemplate span").text();
		   var enableTravelReports=$("#enableTravelReports span").text();
		   var enableAcknowledgement=$("#enableAcknowledgement span").text();
		   //var enableReports=$("#enable span").text();
		   var enableJournalExcel=$("#enableJournalsExcel span").text();
		   var enableQuatation=$("#enableQuotationSents span").text();
		   var enableNeed_FollowUp=$("#enableNeed_followups span").text();
		   var needfollowupdate=$("#enableNeed_followups_date span").text();
		   var noofInvoices=$("#noofInvoices span").text();
		   var noofCenters=$("#noofCenters span").text();
		   var partnerPercent=$("#partner_percent span").text();
		   var partner_email=$("#Partner_Email span").text();
		   var partner_type=$("#partnerType span").text();
		   var gstin_num=$("#div_GSTIN span").text();
		   var pan_num=$("#div_PAN span").text();
		   var authorisedSignatory_nm=$("#div_authorisedSignatory span").text();
		   var authorisedPANNumber_nm=$("#div_authorisedPANNumber span").text();
		   var businessName_nm=$("#div_businessName span").text();
		   var dealerType_nm=$("#div_dealerType span").text();
		   var state_name_nm=$("#div_stateName span").text();
		   
		   
		   var agreementStatus=$("#agreement span").text();		
		   var needToFollowUp=$("#followUp span").text();		
		   var needToFollowUpComment=$("#needToFollowUpCommentDisplay span").text();
		   
		   var sandboxapplieddata=$("#sandboxapplieddiv span").text();
		   //data="type"
		   var type=$("#userTypeChange span").text();
		  
			if(needToFollowUpComment != ''){
				
				$(".needToFollowUpComment").css("display","block");
			}
		   	document.getElementById("FullName").innerHTML = "<input type='text' class='form-control' id='userfullname' value=''/>";
		   	document.getElementById("EmailID").innerHTML = "<input type='text' class='form-control' id='useremail' value=''/>";
		   	document.getElementById("Mnumber").innerHTML = "<input type='text' class='form-control' id='usermobilenumber' value=''/>";
		   	document.getElementById("QuotationSent").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='quotationsSent' data='quotationSent'><label for='quotation'><span class='ui'></span></label><span class='labletxt'>Yes</span></div>";
		   	document.getElementById("Need_FollowUp").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='need_follow_up' data='needFollowup' onchange='needfollowup_onoff()'><label for='needfollowup'><span class='ui'></span></label><span class='labletxt'>Yes</span></div>";
		   
		   	document.getElementById("PartnerEmail").innerHTML = "<input type='text' class='form-control' id='userpartnerEmail' placeholder='partner email'/><div class='help-block with-errors'></div><div id='partnerEmailempty' style='display:none'><div class='ddbox' style='width: 194px;margin-left: 14px;'><p>Search didn't return any results.</p></div></div>";
		   	
		   	document.getElementById("Need_FollowUp_date").innerHTML = '<input type="text" id="userneedFollowupdate" name="userneedFollowupdate" class="form-control" aria-describedby="userneedFollowupdate" placeholder="DD-MM-YYYY"/ onmousedown="usrneeddatepick();" readonly="readonly"><label for="userneedFollowupdate" class="control-label"></label>';
		   	
		   	document.getElementById("Status").innerHTML = "<div class='form-check form-check-inline' id='status'><input class='form-check-input' type='checkbox' id='disable' data='disable'><label for='Status'><span class='ui'></span> </label><span class='labletxt'>Active</span></div>";
		   	document.getElementById("divDrive").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='accessDrive' data='accessDrive'><label for='Docs'><span class='ui'></span> </label><span class='labletxt'>Docs</span></div>";
		   	document.getElementById("mapImports").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='accessImports' data='accessImports'><label for='Imports'><span class='ui'></span> </label><span class='labletxt'>Imports</span></div>";
		   	document.getElementById("mapGstr9").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='accessGstr9' data='accessGstr9'><label for='GSTR9'><span class='ui'></span> </label><span class='labletxt'>GSTR9</span></div>";
		   	document.getElementById("mapDwnldewabillinv").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='accessDwnldewabillinv' data='accessDwnldewabillinv'><label for='EwayBill'><span class='ui'></span> </label><span class='labletxt'>EwayBill</span></div>";
		   	document.getElementById("mapMultiGSTNSearch").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='accessMultiGSTNSearch' data='accessMultiGSTNSearch'><label for='MultiGSTN'><span class='ui'></span> </label><span class='labletxt'>MultiGSTN</span></div>";
		   	document.getElementById("mapReminders").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='accessReminders' data='accessReminders'><label for='Reminders'><span class='ui'></span> </label><span class='labletxt'>Reminders</span></div>";
		   	document.getElementById("mapANX1").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='accessANX1' data='accessANX1'><label for='ANX1'><span class='ui'></span> </label><span class='labletxt'>ANX1</span></div>";
		   	document.getElementById("mapANX2").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='accessANX2' data='accessANX2'><label for='ANX2'><span class='ui'></span> </label><span class='labletxt'>ANX2</span></div>";
		   	document.getElementById("mapEinvoice").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='accessEinvoice' data='accessEinvoice'><label for='Einvoice'><span class='ui'></span> </label><span class='labletxt'>Einvoice</span></div>";
			document.getElementById("mapCustomFieldTemplate").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='accessCustomFieldTemplate' data='accessCustomFieldTemplate'><label for='Einvoice'><span class='ui'></span> </label><span class='labletxt'>New Import Template</span></div>";
			document.getElementById("mapEntertainmentEinvoiceTemplate").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='accessEntertainmentEinvoiceTemplate' data='accessEntertainmentEinvoiceTemplate'><label for='Einvoice'><span class='ui'></span> </label><span class='labletxt'>Entertainment E-Invoice Import Template</span></div>";
			document.getElementById("mapTravelReports").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='accessTravel' data='accessTravel'><label for='Travel'><span class='ui'></span> </label><span class='labletxt'>Travel Reports</span></div>";
		   	document.getElementById("mapAcknowledgement").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='accessAcknowledgement' data='accessAcknowledgement'><label for='Einvoice'><span class='ui'></span> </label><span class='labletxt'>Acknowledgement</span></div>";
		   	document.getElementById("EnableJournalsExcel").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='accessJournalExcel' data='accessJournalExcel'><label for='EnableJournalsExcel'><span class='ui'></span> </label><span class='labletxt'>Journals Excel</span></div>";
			
		   	document.getElementById("UserTypeChange").innerHTML = '<div><select id="usertype" class="form-control"><option value="cacmas">cacmas</option><option value="suvidha">suvidha</option><option value="business">business</option><option value="enterprise">enterprise</option><option value="aspdeveloper">aspdeveloper</option><option value="partner">partner</option></select></div>';
		   	
		   	document.getElementById("reports").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='accessReports' data='accessReports'><label for='Reports'><span class='ui'></span> </label><span class='labletxt'>Reports</span></div>";
		  	document.getElementById("revenuePercentage").innerHTML = "<input type='text' class='form-control' id='userpartnerPercentage' value=''/>";
			//document.getElementById("noofCenters").innerHTML = "<input type='text' class='form-control' id='usertotalCenters' value=''/>";
		   
		   document.getElementById("AgreementStatus").innerHTML = "<select class='form-control' id='useragreementStatus' style='border: 1px solid black;'><option value='' selected>-select-</option><option value='Not Yet Sent'>Not Yet Sent</option><option value='Agreement Sent'>Agreement Sent</option><option value='Pending with Client'>Pending with Client</option><option value='Clarifications'>Clarifications</option><option value='Completed'>Completed</option></select>";
		  
		   document.getElementById("NeedToFollowUp").innerHTML = "<select class='form-control' id='userneedToFollowUp' onchange='needtofollow()'><option value='' selected>-select-</option><option value='Call Not Lift'>Call Not Lift</option><option value='Duplicate'>Duplicate</option><option value='Not Required'>Not Required</option><option value='Ready to Go'>Ready to Go</option><option value='Ready to Pay'>Ready to Pay</option><option value='Yet to Take Decision'>Yet to Take Decision</option><option value='Test Account'>Test Account</option><option value='Pricing Issue'>Pricing Issue</option><option value='Sandbox Testing'>Sandbox Testing</option><option value='Closed'>Closed</option></select>";
		   document.getElementById("NeedToFollowUpComment").innerHTML = "<textarea id='userneedToFollowUpComment' rows='2' cols='10' style='border: 1px solid black;width: 780px;margin-left: -3px;'></textarea>";
		   document.getElementById("SandboxApplied").innerHTML = "<select class='form-control' id='usersandboxApplied'><option value='' selected>-select-</option><option value='NO'>NO</option><option value='YES'>YES</option></select>";
		   document.getElementById("PartnerType").innerHTML = "<select class='form-control' id='usersPartnerType'><option value='Silver Partner'>Silver Partner</option><option value='Gold Partner'>Gold Partner</option><option value='Platinum Partner'>Platinum Partner</option><option value='Sales Team'>Sales Team</option></select>";

		   document.getElementById("GSTIN").innerHTML = "<input type='text' class='form-control' id='usergstin' value=''/>";
		   document.getElementById("PAN").innerHTML = "<input type='text' class='form-control' id='userpan' value=''/>";
		   document.getElementById("AuthorisedSignatory").innerHTML = "<input type='text' class='form-control' id='userauthorisedSignatory' value=''/>";
		   document.getElementById("AuthorisedPANNumber").innerHTML = "<input type='text' class='form-control' id='userauthorisedPANNumber' value=''/>";
		   document.getElementById("BusinessName").innerHTML = "<input type='text' class='form-control' id='userbusinessName' value=''/>";
		   document.getElementById("DealerType").innerHTML = "<input type='text' class='form-control' id='userdealerType' value=''/>";
		   document.getElementById("StateName").innerHTML = "<input type='text' class='form-control userstateName' id='userstateName' placeholder='State' value=''/>";
		   
		   //document.getElementById("StateName").innerHTML = "<input type='text' class='form-control userstateName' id='userstateName' placeholder='State' value=''/><div class='help-block with-errors'></div><div id='statenameempty' style='display:none'><div class='ddbox'><p>Search didn't return any results.</p></div></div><i class='bar'></i>";
		   
		   $('#userbusinessName').text(businessName_nm);
		   $('#userbusinessName').val(businessName_nm);
		   $('#usergstin').text(gstin_num);
		   $('#usergstin').val(gstin_num);
		   $('#userpan').text(pan_num);
		   $('#userpan').val(pan_num);
		   $('#userauthorisedSignatory').text(authorisedSignatory_nm);
		   $('#userauthorisedSignatory').val(authorisedSignatory_nm);
		   $('#userauthorisedPANNumber').text(authorisedPANNumber_nm);
		   $('#userauthorisedPANNumber').val(authorisedPANNumber_nm);
		   $('#userdealerType').text(dealerType_nm);
		   $('#userdealerType').val(dealerType_nm);
		   $('#userstateName').text(state_name_nm);
		   $('#userstateName').val(state_name_nm);
		   
		   $('#userneedFollowupdate').text(needfollowupdate);
		   $('#userneedFollowupdate').val(needfollowupdate);
		   $('#userpartnerEmail').text(partner_email);
		   $('#userpartnerEmail').val(partner_email);
		   $('#partnerEmail').text(partner_email);
		   if(partner_type == ""){
			   $('#usersPartnerType').val("Silver Partner");
		   }else{
		   	$('#usersPartnerType').val(partner_type);
		   }
		   $('#agreementStatus').text(agreementStatus);
		   $('#agreementStatus').val(agreementStatus);
		   $('#needToFollowUp').text(needToFollowUp);
		   $('#needToFollowUp').val(needToFollowUp);
		   $('#agreementStatusComment').text(needToFollowUpComment);
		   $('#agreementStatusComment').val(needToFollowUpComment);  
		   
		   $('#sandboxApplied').text(sandboxapplieddata);
		   $('#SandboxApplied').val(sandboxapplieddata);
		   $('#usersandboxApplied').val(sandboxapplieddata); 
		   
		   $('#useragreementStatus').val(agreementStatus); 
		   $('#userneedToFollowUp').val(needToFollowUp);
		   $('#userneedToFollowUpComment').val(needToFollowUpComment);
		   
		   $('#type').text(type);
		   $('#type').val(type);
		   $('#usertype').val(type);
		   
		   $('#userfullname').text(name);
		   $('#userfullname').val(name);
		   
		   $('#useremail').text(email);
		   $('#useremail').val(email);
		   
		   $('#usermobilenumber').text(mnumber);
		   $('#usermobilenumber').val(mnumber);
		   
		   $('#userpartnerPercentage').text(partnerPercent);
		   $('#userpartnerPercentage').val(partnerPercent);
		 	if(status == 'Active'){
				$('#disable').prop("checked", true);
			}else{
				$('#disable').prop("checked", false);
			}
			if(enableDocs == 'true' || enableDocs == 'Enabled'){
				$('#accessDrive').prop("checked", true);
			}else{
				$('#accessDrive').prop("checked", false);
			}
			if(enableImports == 'true' || enableImports == 'Enabled'){
				$('#accessImports').prop("checked", true);
			}else{
				$('#accessImports').prop("checked", false);
			}
			if(enableGstr9 == 'true' || enableGstr9 == 'Enabled'){
				$('#accessGstr9').prop("checked", true);
			}else{
				$('#accessGstr9').prop("checked", false);
			}
			if(enableDwnldewabillinv == 'true' || enableDwnldewabillinv == 'Enabled'){
				$('#accessDwnldewabillinv').prop("checked", true);
			}else{
				$('#accessDwnldewabillinv').prop("checked", false);
			}
			if(enableMultiGSTNSearch == 'true' || enableMultiGSTNSearch == 'Enabled'){
				$('#accessMultiGSTNSearch').prop("checked", true);
			}else{
				$('#accessMultiGSTNSearch').prop("checked", false);
			}
			if(enableReminders == 'true' || enableReminders == 'Enabled'){
				$('#accessReminders').prop("checked", true);
			}else{
				$('#accessReminders').prop("checked", false);
			}
			if(enableANX1 == 'true' || enableANX1 == 'Enabled'){
				$('#accessANX1').prop("checked", true);
			}else{
				$('#accessANX1').prop("checked", false);
			}
			if(enableANX2 == 'true' || enableANX2 == 'Enabled'){
				$('#accessANX2').prop("checked", true);
			}else{
				$('#accessANX2').prop("checked", false);
			}
			if(enableEinvoice == 'true' || enableEinvoice == 'Enabled'){
				$('#accessEinvoice').prop("checked", true);
			}else{
				$('#accessEinvoice').prop("checked", false);
			}
			if(enableCustomFieldTemplate == 'true' || enableCustomFieldTemplate == 'Enabled'){
				$('#accessCustomFieldTemplate').prop("checked", true);
			}else{
				$('#accessCustomFieldTemplate').prop("checked", false);
			}
			if(enableEntertainmentEinvoiceTemplate == 'true' || enableEntertainmentEinvoiceTemplate == 'Enabled'){
				$('#accessEntertainmentEinvoiceTemplate').prop("checked", true);
			}else{
				$('#accessEntertainmentEinvoiceTemplate').prop("checked", false);
			}
			
			if(enableTravelReports == 'true' || enableTravelReports == 'Enabled'){
				$('#accessTravel').prop("checked", true);
			}else{
				$('#accessTravel').prop("checked", false);
			}
			if(enableAcknowledgement == 'true' || enableAcknowledgement == 'Enabled'){
				$('#accessAcknowledgement').prop("checked", true);
			}else{
				$('#accessAcknowledgement').prop("checked", false);
			}
			if(enableReports == 'true' || enableReports == 'Enabled'){
				$('#accessReports').prop("checked", true);
			}else{
				$('#accessReports').prop("checked", false);
			}
			if(enableJournalExcel == 'true' || enableJournalExcel == 'Enabled'){
				$('#accessJournalExcel').prop("checked", true);
			}else{
				$('#accessJournalExcel').prop("checked", false);
			}
			if(enableQuatation == 'Yes'){
				$('#quotationsSent').prop("checked", true);
			}else{
				$('#quotationsSent').prop("checked", false);
			}
			
			if(enableNeed_FollowUp == 'Yes'){
				$('#need_follow_up').prop("checked", true);
				$('#userneedFollowupdate').val(needfollowupdate);
				$('#enableNeed_followupsDate').removeClass('d-none');
			}else{
				$('#need_follow_up').prop("checked", false);
				$('#userneedFollowupdate').val('');
				$('#enableNeed_followupsDate').addClass('d-none');
			}	
			$("#userstateName").easyAutocomplete(stateoptions);
			$('#userpartnerEmail').easyAutocomplete(partneremailid);
	}
	function loadEnterprisePayments(userId){
		$('#SubscriptionStartDate').val('');
		$('#SubscriptionExpiryDate').val('');
		$.ajax({
			url: contextPath+"/suvidhaPaymentData/"+userId,
			type:'GET',
			success : function(data) {
				if(data==0){
					$('.paymentSubscriptionType').find('option').remove().end().append("<option value=''>-- Select Type --</option><option value='NEWAPI'>NEW</option>");			
				}else{		
					$('.paymentSubscriptionType').find('option').remove().end().append("<option value=''>-- Select Type --</option><option value='TOPUPAPI'>Top-Up</option><option value='RENEWALAPI'>Renewal</option>");
				}
			}
		});
	}
	function formatPaymentDate(dat, type, row){
		var paymentDate = new Date(dat.paymentDate) ;
	    var month = paymentDate.getUTCMonth() + 1; 
		var day = paymentDate.getUTCDate();
		var year = paymentDate.getUTCFullYear();
		return day+'-'+month+'-'+year;
	}
	function formatSubscriptionDate(dat){
		var expiryDate = new Date(dat) ;
	    var month = expiryDate.getUTCMonth() + 1; 
		var day = expiryDate.getUTCDate();
		var year = expiryDate.getUTCFullYear();
		return day+'-'+month+'-'+year;
	}
	function formatClientPaymentDate(dat){
		var expiryDate = new Date(dat.paymentDate) ;
	    var month = expiryDate.getUTCMonth() + 1; 
		var day = expiryDate.getUTCDate();
		var year = expiryDate.getUTCFullYear();
		return day+'-'+month+'-'+year;
	}
	function formatCommentDate(dat, type, row){
		var commentDate = new Date(dat.commentDate) ;
	    var month = commentDate.getUTCMonth() + 1; 
		var day = commentDate.getUTCDate();
		var year = commentDate.getUTCFullYear();
		return day+'-'+month+'-'+year;
	}
	function formatSecondaryUserDate(dat, type, row){
		var paymentDate = new Date(dat.createdDate) ;
	    var month = paymentDate.getUTCMonth() + 1; 
		var day = paymentDate.getUTCDate();
		var year = paymentDate.getUTCFullYear();
		return day+'-'+month+'-'+year;
	}
	function formatUpdatedDate(dat, type, row){
		var createdDt = new Date(dat.updatedDate) ;
	    var month = createdDt.getUTCMonth() + 1; 
		var day = createdDt.getUTCDate();
		var year = createdDt.getUTCFullYear();
		return day+'-'+month+'-'+year;
	}
	function formatRefIdAndName(dat, type, row){
		var email=dat.email;
		var name=dat.name;
		return '<div class="button-type"><button onClick="openPaymentTab(\''+name+'\',\''+email+'\')" class="datatablebody btn btn-success" role="button" style="padding: 4px 10px 4px 10px; font-size: 14px;">Pay Now</button></div>';		
	} 
	function editUserKeys(stage) {
		if(stage == 'Sandbox') {
			$('#sand_save_btn').css('display','block');
			$('#sand_edit_btn').css('display','none');
			var sandKeyName=$("#sandKeyName span").text();
			var sandClientID=$("#sandClientID span").text();
			var sandSecretID=$("#sandSecretID span").text();
			var sandIsEnabled=$("#sandIsEnabled span").text();
			var sandGSTUSERName1=$("#sandGSTUSERName1 span").text();
			var sandGSTUSERName2=$("#sandGSTUSERName2 span").text();
			var sandGSTUSERName3=$("#sandGSTUSERName3 span").text();
			var sandGSTUSERName4=$("#sandGSTUSERName4 span").text();
			var sandGSTINNO1=$("#sandGSTINNO1 span").text();
			var sandGSTINNO2=$("#sandGSTINNO2 span").text();
			var sandGSTINNO3=$("#sandGSTINNO3 span").text();
			var sandGSTINNO4=$("#sandGSTINNO4 span").text();
			
			var sandGSTTDSUSERName1=$("#sandGSTTDSUSERName1 span").text();
			var sandGSTTDSUSERName2=$("#sandGSTTDSUSERName2 span").text();
			var sandGSTTDSUSERName3=$("#sandGSTTDSUSERName3 span").text();
			var sandGSTTDSUSERName4=$("#sandGSTTDSUSERName4 span").text();
			var sandTDSGSTINNO1=$("#sandTDSGSTINNO1 span").text();
			var sandTDSGSTINNO2=$("#sandTDSGSTINNO2 span").text();
			var sandTDSGSTINNO3=$("#sandTDSGSTINNO3 span").text();
			var sandTDSGSTINNO4=$("#sandTDSGSTINNO4 span").text();
			
			var sandGSTCOMPUSERName1=$("#sandGSTCOMPUSERName1 span").text();
			var sandGSTCOMPUSERName2=$("#sandGSTCOMPUSERName2 span").text();
			var sandGSTCOMPUSERName3=$("#sandGSTCOMPUSERName3 span").text();
			var sandGSTCOMPUSERName4=$("#sandGSTCOMPUSERName4 span").text();
			var sandCOMPGSTINNO1=$("#sandCOMPGSTINNO1 span").text();
			var sandCOMPGSTINNO2=$("#sandCOMPGSTINNO2 span").text();
			var sandCOMPGSTINNO3=$("#sandCOMPGSTINNO3 span").text();
			var sandCOMPGSTINNO4=$("#sandCOMPGSTINNO4 span").text();
			
			var sandGSTTCSUSERName1=$("#sandGSTTCSUSERName1 span").text();
			var sandGSTTCSUSERName2=$("#sandGSTTCSUSERName2 span").text();
			var sandGSTTCSUSERName3=$("#sandGSTTCSUSERName3 span").text();
			var sandGSTTCSUSERName4=$("#sandGSTTCSUSERName4 span").text();
			var sandTCSGSTINNO1=$("#sandTCSGSTINNO1 span").text();
			var sandTCSGSTINNO2=$("#sandTCSGSTINNO2 span").text();
			var sandTCSGSTINNO3=$("#sandTCSGSTINNO3 span").text();
			var sandTCSGSTINNO4=$("#sandTCSGSTINNO4 span").text();
			
			var sandGSTISDUSERName1=$("#sandGSTISDUSERName1 span").text();
			var sandGSTISDUSERName2=$("#sandGSTISDUSERName2 span").text();
			var sandGSTISDUSERName3=$("#sandGSTISDUSERName3 span").text();
			var sandGSTISDUSERName4=$("#sandGSTISDUSERName4 span").text();
			var sandISDGSTINNO1=$("#sandISDGSTINNO1 span").text();
			var sandISDGSTINNO2=$("#sandISDGSTINNO2 span").text();
			var sandISDGSTINNO3=$("#sandISDGSTINNO3 span").text();
			var sandISDGSTINNO4=$("#sandISDGSTINNO4 span").text();
	
			document.getElementById("SandKeyName").innerHTML = "<input type='text' class='form-control' id='sandkeyname' value=''/>";
			document.getElementById("SandClientID").innerHTML = "<input type='text' class='form-control' id='sandclientid' value=''/>";
			document.getElementById("SandSecretID").innerHTML = "<input type='text' class='form-control' id='sandclientsecret' value=''/>";
			document.getElementById("SandIsEnabled").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='sandboxRadio'><label for='sandboxRadio'><span class='ui'></span> </label><span class='labletxt'>Enabled</span> </div>";
			document.getElementById("SandGSTUserName1").innerHTML = "<input type='text' class='form-control' id='sandgstusername1' value=''/>";
			document.getElementById("SandGSTUserName2").innerHTML = "<input type='text' class='form-control' id='sandgstusername2' value=''/>";
			document.getElementById("SandGSTUserName3").innerHTML = "<input type='text' class='form-control' id='sandgstusername3' value=''/>";
			document.getElementById("SandGSTUserName4").innerHTML = "<input type='text' class='form-control' id='sandgstusername4' value=''/>";
			document.getElementById("SandGSTINNo1").innerHTML = "<input type='text' class='form-control' id='sandgstinno1' value=''/>";
			document.getElementById("SandGSTINNo2").innerHTML = "<input type='text' class='form-control' id='sandgstinno2' value=''/>";
			document.getElementById("SandGSTINNo3").innerHTML = "<input type='text' class='form-control' id='sandgstinno3' value=''/>";
			document.getElementById("SandGSTINNo4").innerHTML = "<input type='text' class='form-control' id='sandgstinno4' value=''/>";
			
			document.getElementById("SandGSTTDSUserName1").innerHTML = "<input type='text' class='form-control' id='sandgsttdsusername1' value=''/>";
			document.getElementById("SandGSTTDSUserName2").innerHTML = "<input type='text' class='form-control' id='sandgsttdsusername2' value=''/>";
			document.getElementById("SandGSTTDSUserName3").innerHTML = "<input type='text' class='form-control' id='sandgsttdsusername3' value=''/>";
			document.getElementById("SandGSTTDSUserName4").innerHTML = "<input type='text' class='form-control' id='sandgsttdsusername4' value=''/>";
			document.getElementById("SandTDSGSTINNo1").innerHTML = "<input type='text' class='form-control' id='sandtdsgstinno1' value=''/>";
			document.getElementById("SandTDSGSTINNo2").innerHTML = "<input type='text' class='form-control' id='sandtdsgstinno2' value=''/>";
			document.getElementById("SandTDSGSTINNo3").innerHTML = "<input type='text' class='form-control' id='sandtdsgstinno3' value=''/>";
			document.getElementById("SandTDSGSTINNo4").innerHTML = "<input type='text' class='form-control' id='sandtdsgstinno4' value=''/>";
			
			document.getElementById("SandGSTCOMPUserName1").innerHTML = "<input type='text' class='form-control' id='sandgstcompusername1' value=''/>";
			document.getElementById("SandGSTCOMPUserName2").innerHTML = "<input type='text' class='form-control' id='sandgstcompusername2' value=''/>";
			document.getElementById("SandGSTCOMPUserName3").innerHTML = "<input type='text' class='form-control' id='sandgstcompusername3' value=''/>";
			document.getElementById("SandGSTCOMPUserName4").innerHTML = "<input type='text' class='form-control' id='sandgstcompusername4' value=''/>";
			document.getElementById("SandCOMPGSTINNo1").innerHTML = "<input type='text' class='form-control' id='sandcompgstinno1' value=''/>";
			document.getElementById("SandCOMPGSTINNo2").innerHTML = "<input type='text' class='form-control' id='sandcompgstinno2' value=''/>";
			document.getElementById("SandCOMPGSTINNo3").innerHTML = "<input type='text' class='form-control' id='sandcompgstinno3' value=''/>";
			document.getElementById("SandCOMPGSTINNo4").innerHTML = "<input type='text' class='form-control' id='sandcompgstinno4' value=''/>";
			
			document.getElementById("SandGSTTCSUserName1").innerHTML = "<input type='text' class='form-control' id='sandgsttcsusername1' value=''/>";
			document.getElementById("SandGSTTCSUserName2").innerHTML = "<input type='text' class='form-control' id='sandgsttcsusername2' value=''/>";
			document.getElementById("SandGSTTCSUserName3").innerHTML = "<input type='text' class='form-control' id='sandgsttcsusername3' value=''/>";
			document.getElementById("SandGSTTCSUserName4").innerHTML = "<input type='text' class='form-control' id='sandgsttcsusername4' value=''/>";
			document.getElementById("SandTCSGSTINNo1").innerHTML = "<input type='text' class='form-control' id='sandtcsgstinno1' value=''/>";
			document.getElementById("SandTCSGSTINNo2").innerHTML = "<input type='text' class='form-control' id='sandtcsgstinno2' value=''/>";
			document.getElementById("SandTCSGSTINNo3").innerHTML = "<input type='text' class='form-control' id='sandtcsgstinno3' value=''/>";
			document.getElementById("SandTCSGSTINNo4").innerHTML = "<input type='text' class='form-control' id='sandtcsgstinno4' value=''/>";
			
			document.getElementById("SandGSTISDUserName1").innerHTML = "<input type='text' class='form-control' id='sandgstisdusername1' value=''/>";
			document.getElementById("SandGSTISDUserName2").innerHTML = "<input type='text' class='form-control' id='sandgstisdusername2' value=''/>";
			document.getElementById("SandGSTISDUserName3").innerHTML = "<input type='text' class='form-control' id='sandgstisdusername3' value=''/>";
			document.getElementById("SandGSTISDUserName4").innerHTML = "<input type='text' class='form-control' id='sandgstisdusername4' value=''/>";
			document.getElementById("SandISDGSTINNo1").innerHTML = "<input type='text' class='form-control' id='sandisdgstinno1' value=''/>";
			document.getElementById("SandISDGSTINNo2").innerHTML = "<input type='text' class='form-control' id='sandisdgstinno2' value=''/>";
			document.getElementById("SandISDGSTINNo3").innerHTML = "<input type='text' class='form-control' id='sandisdgstinno3' value=''/>";
			document.getElementById("SandISDGSTINNo4").innerHTML = "<input type='text' class='form-control' id='sandisdgstinno4' value=''/>";
			
			if(sandIsEnabled == 'Yes'){
				$('#sandboxRadio').prop("checked", true);
			}else{
				$('#sandboxRadio').prop("checked", false);
			}
			$('#sandkeyname').text(sandKeyName).val(sandKeyName);
			$('#sandclientid').text(sandClientID).val(sandClientID);
			$('#sandclientsecret').text(sandSecretID).val(sandSecretID);
			$('#sandgstusername1').text(sandGSTUSERName1).val(sandGSTUSERName1);
			$('#sandgstusername2').text(sandGSTUSERName2).val(sandGSTUSERName2);
			$('#sandgstusername3').text(sandGSTUSERName3).val(sandGSTUSERName3);	
			$('#sandgstusername4').text(sandGSTUSERName4).val(sandGSTUSERName4);
			$('#sandgstinno1').text(sandGSTINNO1).val(sandGSTINNO1);
			$('#sandgstinno2').text(sandGSTINNO2).val(sandGSTINNO2);	
			$('#sandgstinno3').text(sandGSTINNO3).val(sandGSTINNO3);
			$('#sandgstinno4').text(sandGSTINNO4).val(sandGSTINNO4);				

			$('#sandgsttdsusername1').text(sandGSTTDSUSERName1).val(sandGSTTDSUSERName1);
			$('#sandgsttdsusername2').text(sandGSTTDSUSERName2).val(sandGSTTDSUSERName2);
			$('#sandgsttdsusername3').text(sandGSTTDSUSERName3).val(sandGSTTDSUSERName3);	
			$('#sandgsttdsusername4').text(sandGSTTDSUSERName4).val(sandGSTTDSUSERName4);
			$('#sandtdsgstinno1').text(sandTDSGSTINNO1).val(sandTDSGSTINNO1);
			$('#sandtdsgstinno2').text(sandTDSGSTINNO2).val(sandTDSGSTINNO2);	
			$('#sandtdsgstinno3').text(sandTDSGSTINNO3).val(sandTDSGSTINNO3);
			$('#sandtdsgstinno4').text(sandTDSGSTINNO4).val(sandTDSGSTINNO4);
			
			$('#sandgstcompusername1').text(sandGSTCOMPUSERName1).val(sandGSTCOMPUSERName1);
			$('#sandgstcompusername2').text(sandGSTCOMPUSERName2).val(sandGSTCOMPUSERName2);
			$('#sandgstcompusername3').text(sandGSTCOMPUSERName3).val(sandGSTCOMPUSERName3);	
			$('#sandgstcompusername4').text(sandGSTCOMPUSERName4).val(sandGSTCOMPUSERName4);
			$('#sandcompgstinno1').text(sandCOMPGSTINNO1).val(sandCOMPGSTINNO1);
			$('#sandcompgstinno2').text(sandCOMPGSTINNO2).val(sandCOMPGSTINNO2);	
			$('#sandcompgstinno3').text(sandCOMPGSTINNO3).val(sandCOMPGSTINNO3);
			$('#sandcompgstinno4').text(sandCOMPGSTINNO4).val(sandCOMPGSTINNO4);
			
			$('#sandgsttcsusername1').text(sandGSTTCSUSERName1).val(sandGSTTCSUSERName1);
			$('#sandgsttcsusername2').text(sandGSTTCSUSERName2).val(sandGSTTCSUSERName2);
			$('#sandgsttcsusername3').text(sandGSTTCSUSERName3).val(sandGSTTCSUSERName3);	
			$('#sandgsttcsusername4').text(sandGSTTCSUSERName4).val(sandGSTTCSUSERName4);
			$('#sandtcsgstinno1').text(sandTCSGSTINNO1).val(sandTCSGSTINNO1);
			$('#sandtcsgstinno2').text(sandTCSGSTINNO2).val(sandTCSGSTINNO2);	
			$('#sandtcsgstinno3').text(sandTCSGSTINNO3).val(sandTCSGSTINNO3);
			$('#sandtcsgstinno4').text(sandTCSGSTINNO4).val(sandTCSGSTINNO4);
			
			$('#sandgstisdusername1').text(sandGSTISDUSERName1).val(sandGSTISDUSERName1);
			$('#sandgstisdusername2').text(sandGSTISDUSERName2).val(sandGSTISDUSERName2);
			$('#sandgstisdusername3').text(sandGSTISDUSERName3).val(sandGSTISDUSERName3);	
			$('#sandgstisdusername4').text(sandGSTISDUSERName4).val(sandGSTISDUSERName4);
			$('#sandisdgstinno1').text(sandISDGSTINNO1).val(sandISDGSTINNO1);
			$('#sandisdgstinno2').text(sandISDGSTINNO2).val(sandISDGSTINNO2);	
			$('#sandisdgstinno3').text(sandISDGSTINNO3).val(sandISDGSTINNO3);
			$('#sandisdgstinno4').text(sandISDGSTINNO4).val(sandISDGSTINNO4);
			
		} else if(stage == 'Production'){
			
			$('#prod_save_btn').css('display','block');
			$('#prod_edit_btn').css('display','none');
			var prodKeyName=$("#prodKeyName span").text();
			var prodClientID=$("#prodClientID span").text();
			var prodSecretID=$("#prodSecretID span").text();
			
			var prodGSTClientID=$("#prodGSTClientID span").text();
			var prodGSTSecretID=$("#prodGSTSecretID span").text();
			
			var prodPartnerID=$("#prodPartnerID span").text();
			var prodIsEnabled=$("#prodIsEnabled span").text();
			
			document.getElementById("ProdKeyName").innerHTML = "<input type='text' class='form-control' id='prodkeyname' value=''/>";
			document.getElementById("ProdClientID").innerHTML = "<input type='text' class='form-control' id='prodclientid' value=''/>";
			document.getElementById("ProdSecretID").innerHTML = "<input type='text' class='form-control' id='prodclientsecret' value=''/>";
			
			document.getElementById("ProdGSTClientID").innerHTML = "<input type='text' class='form-control' id='prodgstclientid' value=''/>";
			document.getElementById("ProdGSTSecretID").innerHTML = "<input type='text' class='form-control' id='prodgstclientsecret' value=''/>";
			
			document.getElementById("ProdPartnerID").innerHTML = "<input type='text' class='form-control' id='prodpartnerid' value=''/>";
			document.getElementById("ProdIsEnabled").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='productionRadio'><label for='productionRadio'><span class='ui'></span> </label><span class='labletxt'>Enabled</span> </div>";
			if(prodIsEnabled == 'Yes'){
				$('#productionRadio').prop("checked", true);
			}else{
				$('#productionRadio').prop("checked", false);
			}
			$('#prodkeyname').text(prodKeyName).val(prodKeyName);
			if(prodClientID == ''){
				var pclientid = generateUUID();
				$('#prodclientid').text(pclientid).val(pclientid);
			}else{
				$('#prodclientid').text(prodClientID).val(prodClientID);
			}
			if(prodSecretID == ''){
				var psecret = generateUUID();
				$('#prodclientsecret').text(psecret).val(psecret); 
			}else{
			$('#prodclientsecret').text(prodSecretID).val(prodSecretID); 
			}
			$('#prodgstclientid').text(prodGSTClientID).val(prodGSTClientID);
			$('#prodgstclientsecret').text(prodGSTSecretID).val(prodGSTSecretID); 
			
			$('#prodpartnerid').text(prodPartnerID).val(prodPartnerID);
		}else if(stage == 'EwayBillSandBox'){
			$('#ewaySand_save_btn').css('display','block');
			$('#ewaySand_edit_btn').css('display','none');
			var ewaySandKeyName=$("#ewaySandKeyName span").text();
			var ewaySandClientID=$("#ewaySandClientID span").text();
			var ewaySandSecretID=$("#ewaySandSecretID span").text();
			var ewaySandIsEnabled=$("#ewaySandIsEnabled span").text();
			document.getElementById("EwaySandKeyName").innerHTML = "<input type='text' class='form-control' id='ewaySandkeyname' value=''/>";
			document.getElementById("EwaySandClientID").innerHTML = "<input type='text' class='form-control' id='ewaySandclientid' value=''/>";
			document.getElementById("EwaySandSecretID").innerHTML = "<input type='text' class='form-control' id='ewaySandclientsecret' value=''/>";
			document.getElementById("EwaySandIsEnabled").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='ewaySandRadio'><label for='ewayRadio'><span class='ui'></span> </label><span class='labletxt'>Enabled</span> </div>";
			if(ewaySandIsEnabled == 'Yes'){
				$('#ewaySandRadio').prop("checked", true);
			}else{
				$('#ewaySandRadio').prop("checked", false);
			}
			$('#ewaySandkeyname').text(ewaySandKeyName).val(ewaySandKeyName);
			$('#ewaySandclientid').text(ewaySandClientID).val(ewaySandClientID);
			$('#ewaySandclientsecret').text(ewaySandSecretID).val(ewaySandSecretID); 
		}else if(stage == 'EwayBillProduction'){
			$('#eway_save_btn').css('display','block');
			$('#eway_edit_btn').css('display','none');
			var ewayKeyName=$("#ewayKeyName span").text();
			var ewayClientID=$("#ewayClientID span").text();
			var ewaySecretID=$("#ewaySecretID span").text();
			var ewayPartnerID=$("#ewayPartnerID span").text();
			var ewayIsEnabled=$("#ewayIsEnabled span").text();
			
			document.getElementById("EwayKeyName").innerHTML = "<input type='text' class='form-control' id='ewaykeyname' value=''/>";
			document.getElementById("EwayClientID").innerHTML = "<input type='text' class='form-control' id='ewayclientid' value=''/>";
			document.getElementById("EwaySecretID").innerHTML = "<input type='text' class='form-control' id='ewayclientsecret' value=''/>";
			document.getElementById("EwayPartnerID").innerHTML = "<input type='text' class='form-control' id='ewaypartnerid' value=''/>";
			document.getElementById("EwayIsEnabled").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='ewayRadio'><label for='ewayRadio'><span class='ui'></span> </label><span class='labletxt'>Enabled</span> </div>";
			if(ewayIsEnabled == 'Yes'){
				$('#ewayRadio').prop("checked", true);
			}else{
				$('#ewayRadio').prop("checked", false);
			}
			$('#ewaykeyname').text(ewayKeyName).val(ewayKeyName);
			if(ewayClientID != ''){
				$('#ewayclientid').text(ewayClientID).val(ewayClientID);
			}else{
				$('#ewayclientid').text(ewayClientID).val(ewaybillClientId);
			}
			if(ewaySecretID != ''){
				$('#ewayclientsecret').text(ewaySecretID).val(ewaySecretID); 
			}else{
				$('#ewayclientsecret').text(ewaySecretID).val(ewaybillClientSecretId); 
			}
			$('#ewaypartnerid').text(ewayPartnerID).val(ewayPartnerID);
		}else if(stage == 'EInvoiceSandBox'){
			$('#einvSand_save_btn').css('display','block');
			$('#einvSand_edit_btn').css('display','none');
			var einvSandKeyName=$("#einvSandKeyName span").text();
			var einvSandClientID=$("#einvSandClientID span").text();
			var einvSandSecretID=$("#einvSandSecretID span").text();
			var einvSandIsEnabled=$("#einvSandIsEnabled span").text();
			var einvSandUsername=$("#einvsandusername span").text();
			var einvSandPassword=$("#einvsandpassword span").text();
			document.getElementById("EinvSandKeyName").innerHTML = "<input type='text' class='form-control einvoiceSandbox' id='einvSandkeyname' value=''/>";
			document.getElementById("EinvSandClientID").innerHTML = "<input type='text' class='form-control einvoiceSandbox' id='einvSandclientid' value=''/>";
			document.getElementById("EinvSandSecretID").innerHTML = "<input type='text' class='form-control einvoiceSandbox' id='einvSandclientsecret' value=''/>";
			document.getElementById("EinvSandUsername").innerHTML = "<input type='text' class='form-control einvoiceSandbox einvSandUsername' id='einvSandusername' value=''/>";
			document.getElementById("EinvSandPassword").innerHTML = "<input type='text' class='form-control einvoiceSandbox einvSandPassword' id='einvSandpassword' value=''/>";
			document.getElementById("EinvSandIsEnabled").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input  einvoiceSandbox' type='checkbox' id='einvSandRadio'><label for='einvRadio'><span class='ui'></span> </label><span class='labletxt'>Enabled</span> </div>";
			if(einvSandIsEnabled == 'Yes'){
				$('#einvSandRadio').prop("checked", true);
			}else{
				$('#einvSandRadio').prop("checked", false);
			}
			$('#einvSandkeyname').text(einvSandKeyName).val(einvSandKeyName);
			$('#einvSandclientid').text(einvSandClientID).val(einvSandClientID);
			$('#einvSandclientsecret').text(einvSandSecretID).val(einvSandSecretID); 
			$('#einvSandusername').text(einvSandUsername).val(einvSandUsername);
			$('#einvSandpassword').text(einvSandPassword).val(einvSandPassword);
		}else if(stage == 'EInvoiceProduction'){
			$('#einv_save_btn').css('display','block');
			$('#einv_edit_btn').css('display','none');
			var einvKeyName=$("#einvKeyName span").text();
			var einvClientID=$("#einvClientID span").text();
			var einvSecretID=$("#einvSecretID span").text();
			var einvPartnerID=$("#einvPartnerID span").text();
			var einvIsEnabled=$("#einvIsEnabled span").text();
			
			document.getElementById("EinvKeyName").innerHTML = "<input type='text' class='form-control einvoiceProduction' id='einvkeyname' value=''/>";
			document.getElementById("EinvClientID").innerHTML = "<input type='text' class='form-control einvoiceProduction' id='einvclientid' value=''/>";
			document.getElementById("EinvSecretID").innerHTML = "<input type='text' class='form-control einvoiceProduction' id='einvclientsecret' value=''/>";
			document.getElementById("EinvPartnerID").innerHTML = "<input type='text' class='form-control einvoiceProduction' id='einvpartnerid' value=''/>";
			document.getElementById("EinvIsEnabled").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input einvoiceProduction' type='checkbox' id='einvRadio'><label for='einvRadio'><span class='ui'></span> </label><span class='labletxt'>Enabled</span> </div>";
			if(einvIsEnabled == 'Yes'){
				$('#einvRadio').prop("checked", true);
			}else{
				$('#einvRadio').prop("checked", false);
			}
			$('#einvkeyname').text(einvKeyName).val(einvKeyName);
			if(einvClientID != ''){
				$('#einvclientid').text(einvClientID).val(einvClientID);
			}else{
				$('#einvclientid').text(einvClientID).val(einvClientId);
			}
			if(einvSecretID != ''){
				$('#einvclientsecret').text(einvSecretID).val(einvSecretID); 
			}else{
				$('#einvclientsecret').text(einvSecretID).val(einvClientSecretId); 
			}
			$('#einvpartnerid').text(einvPartnerID).val(einvPartnerID);
		}
	}
	function savePaymentData() {
		var url = null;
		var amount = $('#idASPAmt').val();
		var invoices = $('#idASPInv').val();
		var rate = $('#idASPRate').val();
		var pMode = $('#idASPMode').val();
		var refNo = $('#idASPRef').val();
		var stage = $('#stage').val();
		var statename = $('#statename').val();
		var clients = $('#idSuvidhaClnts').val();
		var days = $('#idDays').val();
		var planid = $('#planid').val();
		
		var suvidhasubcenter_id=$('.suvidhasubcenter_id').val();
		
		var paymentSubscriptionType=$('#paymentSubscriptionType').val();
		var subscriptionStartDate=$('#SubscriptionStartDate').val();
		var subscriptionExpiryDate=$('#SubscriptionExpiryDate').val();      
				
		var centers = 0;
		if(userArray.type == 'suvidha' && userArray.parentid == null){
			centers = $('#idSuvidhaCenters').val();
		}
		
		if(userArray.type != 'suvidha' && userArray.type != 'enterprise' && userArray.type != 'cacmas' && userArray.type != 'business') {
			if(amount && statename) {
				url = contextPath+'/sbscrpymt/'+currentUser+'/'+userArray.type+'/'+amount+'/'+invoices+'/'+rate+'/'+pMode+'/'+refNo+'/'+stage+'/'+statename+'/'+paymentSubscriptionType+'/'+subscriptionStartDate+'/'+subscriptionExpiryDate;
			} else {
				url = contextPath+'/sbscrfreepln/'+currentUser+'/'+userArray.type+'/'+invoices+'/'+clients+'/'+days+'/'+subscriptionStartDate+'/'+subscriptionExpiryDate;
			}
		} else if(invoices > 0){
			if(userArray.type == 'enterprise'){
				url = contextPath+'/sbscrenterprisepymt/'+currentUser+'/'+userArray.type+'/'+amount+'/'+invoices+'/'+clients+'/'+pMode+'/'+refNo+'/'+statename+'/'+paymentSubscriptionType+'/'+subscriptionStartDate+'/'+subscriptionExpiryDate;
			}else if(userArray.type == 'business'){
				url = contextPath+'/sbscrbusinesspymt/'+currentUser+'/'+userArray.type+'/'+amount+'/'+invoices+'/'+clients+'/'+pMode+'/'+refNo+'/'+statename+'/'+planid+'/'+subscriptionStartDate+'/'+subscriptionExpiryDate;
			}else if(userArray.type == 'cacmas'){
				if((amount && statename) && (amount != 0 && statename)){
					url = contextPath+'/sbscrcacmaspymt/'+currentUser+'/'+userArray.type+'/'+amount+'/'+invoices+'/'+clients+'/'+pMode+'/'+refNo+'/'+statename+'/'+planid+'/'+subscriptionStartDate+'/'+subscriptionExpiryDate;
				}else{
					//url = '${contextPath}/sbscrfreepln/'+currentUser+'/'+userArray.type+'/'+invoices+'/'+clients+'/'+days+'?planid='+planid+'/'+subscriptionStartDate+'/'+subscriptionExpiryDate;
					
					url = contextPath+'/sbscrfreepln/'+currentUser+'/'+userArray.type+'/'+invoices+'/'+clients+'/'+days+'/'+planid+'/'+subscriptionStartDate+'/'+subscriptionExpiryDate;
				}
			}else{
				if(suvidhasubcenter_id == ''){
					var suvidhasubcenters_centers='0';
					if(centers==''){
						centers=suvidhasubcenters_centers;
					}
					url = contextPath+'/sbscrsuvidhapymt/'+currentUser+'/'+userArray.type+'/'+amount+'/'+invoices+'/'+clients+'/'+centers+'/'+pMode+'/'+refNo+'/'+statename+'/'+paymentSubscriptionType+'/'+subscriptionStartDate+'/'+subscriptionExpiryDate;
				}else{
					var suvidhasubcenters_centers='0';
					centers=suvidhasubcenters_centers;
					url = contextPath+'/sbscrsuvidhapymt/'+suvidhasubcenter_id+'/'+userArray.type+'/'+amount+'/'+invoices+'/'+clients+'/'+centers+'/'+pMode+'/'+refNo+'/'+statename+'/'+paymentSubscriptionType+'/'+subscriptionStartDate+'/'+subscriptionExpiryDate;			
				}
			}
		}
		if(url != null) {
			$.ajax({
				url : url,
				async: false,
				cache: false,
				success : function(data) {
					if(paymentTable) {
						paymentTable.ajax.reload();
					}
					if(userArray.type == 'enterprise' || userArray.type == 'business'){
						$('#TotalClientsAllowed').html(clients);
					}
					$('#TotalCentersAllowed').html(centers);
					$('#TotalInvoicesAllowed').html(invoices);
					table.ajax.reload();
					$('.paymentDt').val('');
				}
			});
		}
	}
	
	function savePaymentLinkData() {
		var url = null;
		var amount = $('#idASPyAmt').val();
		var invoices = $('#idASPPInv').val();
		var category = $('#paymentstage').val();
		var rateofinclusivetax = $('#rateofinclusivetax').is(':checked');
		if(amount && invoices) {		
			url = contextPath+'/sbscrpymtlink/'+currentUser+'/'+amount+'/'+invoices+'/'+category+'/'+rateofinclusivetax;
		}
		if(url != null) {
			$.ajax({
				url : url,
				async: false,
				cache: false,
				success : function(data) {
					$('#rateofinclusivetax').prop("checked", false);
					$('#idASPyAmt,#idASPPInv,#paymentstage').val('');
				}
			});
		}
	}
	
	
	function needtofollow() {
		$('.needToFollowUpComment').addClass('d-none');
		$(".needToFollowUpComment").css("display","none");
		var val=$('#userneedToFollowUp').val();
		if(val=='Duplicate'){
			$('.needToFollowUpComment').removeClass('d-none');
			$(".needToFollowUpComment").css("display","block");
		}
		if(val=='Not Required'){
			$('.needToFollowUpComment').removeClass('d-none');
			$(".needToFollowUpComment").css("display","block");
		}
	}
	function saveComments(){
		var table, selRow;
		var commentsData=new Object();
		var comments=$(".commentsData").val();
		var createdate=$("#commentsDate").val();
		var addedby=$(".addedby").val();
		commentsData.comments=comments;
		commentsData.commentDate=createdate;
		commentsData.addedby=addedby; 
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : contextPath+'/savecomments/'+currentUser,
			data : JSON.stringify(commentsData),
			dataType : 'json',
			success : function(data) {
				$(".commentsData").val('');
				//$("#commentsDate").val('');
				//$(".addedby").val('');
				$(".alertcommentsdata").html("");
				$.each(data, function(key, value) {
					$(".alertcommentsdata").append("<div class='alert alert-success commentsdata'>"+value.comments+'<br/>'+value.addedby+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+value.commentDate+"</div>");
				});			
			},
			error:function(dat){
			}
		});
	}
	function deleteSelectedallUsers(){
		var table = $('#dbTable').DataTable();
		//spamuserchckbox
		if ($("#alldeletespamusrs").prop("checked")) {
			deleteSpamUsersArray=new Array();
			 var x = document.getElementsByClassName("spamuserchckbox"); 
	         for (var i = 0; i < x.length; i++) {
	        	 deleteSpamUsersArray.push(x[i].value);
	        	 $('.spamuserchckbox').prop('checked',true);
	         }
		} else { 
		  var x = document.getElementsByClassName("spamuserchckbox");
		  deleteSpamUsersArray=new Array();
	       for (var i = 0; i < x.length; i++) { 
	      	 $('.spamuserchckbox').prop('checked',false);
	       }
		}
	}
	function deleteSelectedUsers(usrid){
		var tablerowslen = document.getElementById("dbTable").getElementsByTagName("tr").length;
		if(deleteSpamUsersArray.indexOf(usrid) !== -1){
			deleteSpamUsersArray.splice(usrid,1);	
		}else{
			deleteSpamUsersArray.push(usrid);
		}
		var tabrowlen=tablerowslen-1;
		if(tabrowlen == deleteSpamUsersArray.length){
			$('#alldeletespamusrs').prop('checked',true);
		}else{
			$('#alldeletespamusrs').prop('checked',false);
		}
		if(deleteSpamUsersArray.length==1){
			$('#otpverify').prop('disabled', false);
		}else{
			$('#otpverify').prop('disabled', true);
		}
	}
	function needfollowup_onoff(){
		if($('#need_follow_up').is(':checked')){
			$('#enableNeed_followupsDate').removeClass('d-none');
		}else{
			$('#enableNeed_followupsDate').addClass('d-none');
		}
		$('#userneedFollowupdate').val("");
	}
	$(document).on('show.bs.modal', '.modal', function (event) {
		var zIndex = 1040 + (10 * $('.modal:visible').length);
		$(this).css('z-index', zIndex);
		setTimeout(function() {
		$('.modal-backdrop').not('.modal-stack').css('z-index', zIndex - 1).addClass('modal-stack');
		}, 0);
	});
	
	function editFeaturesDetails() {
		$('#save_features_btn').css('display','block');
		$('#edit_features_btn').css('display','none');
		//var enableGstr4Annual=$("#enableGstr4Annual span").text();
		//var enableGstr6=$("#enableGstr6 span").text();
		var enableGstr8=$("#enableGstr8 span").text();
		//document.getElementById("mapGstr4Annual").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='accessGstr4Annual' data='accessGstr4Annual'><label for='GSTR4Annual'><span class='ui'></span> </label><span class='labletxt'>GSTR4 Annual</span></div>";
		//document.getElementById("mapGstr6").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='accessGstr6' data='accessGstr8'><label for='GSTR6'><span class='ui'></span> </label><span class='labletxt'>GSTR6</span></div>";
		document.getElementById("mapGstr8").innerHTML = "<div class='form-check form-check-inline'><input class='form-check-input' type='checkbox' id='accessGstr8' data='accessGstr8'><label for='GSTR8'><span class='ui'></span> </label></div>";
		
		/*if(enableGstr4Annual == 'true' || enableGstr4Annual == 'Enabled'){
			$('#accessGstr4Annual').prop("checked", true);
		}else{
			$('#accessGstr4Annual').prop("checked", false);
		}
		if(enableGstr6 == 'true' || enableGstr6 == 'Enabled'){
			$('#accessGstr6').prop("checked", true);
		}else{
			$('#accessGstr6').prop("checked", false);
		}*/
		if(enableGstr8 == 'true' || enableGstr8 == 'Enabled'){
			$('#accessGstr8').prop("checked", true);
		}else{
			$('#accessGstr8').prop("checked", false);
		}	
	}
	
	$(document).on('change', '#accessGstr4Annual', function() {
		var status = $(this).is(':checked');
		$.ajax({
			url: contextPath+"/updtusrAccess/"+currentUser+"?type=GSTR4Annual",
			async: false,
			cache: false,
			data: {
				'flag': status
			},
			success : function(data) {
			}
		});
	});
	$(document).on('change', '#accessGstr6', function() {
		var status = $(this).is(':checked');
		$.ajax({
			url: contextPath+"/updtusrAccess/"+currentUser+"?type=GSTR6",
			async: false,
			cache: false,
			data: {
				'flag': status
			},
			success : function(data) {
			}
		});
	});
	$(document).on('change', '#accessGstr8', function() {
		var status = $(this).is(':checked');
		$.ajax({
			url: contextPath+"/updtusrAccess/"+currentUser+"?type=GSTR8",
			async: false,
			cache: false,
			data: {
				'flag': status
			},
			success : function(data) {
			}
		});
	});
	function saveFeaturesDetails(){	
		$(".feature_loader-imger").addClass("btn-loader");
		$('#save_features_btn').css('display','none');
		$('#edit_features_btn').css('display','block');
		var userDetails = new Object;
		userDetails.accessGstr4Annual = $('#accessGstr4Annual').is(':checked');
		userDetails.accessGstr6 = $('#accessGstr6').is(':checked');
		userDetails.accessGstr8 = $('#accessGstr8').is(':checked');
		$('.user_feature_details_field').each(function() {
			var feature_field = $(this).attr('data');
			if(feature_field == 'accessGstr4Annual' && userDetails.accessGstr4Annual.toString() == 'false'){
				$(this).html("false");
			}else if(feature_field == 'accessGstr4Annual' && userDetails.accessGstr4Annual.toString() == 'true'){
				$(this).html("true");
			}
			if(feature_field == 'accessGstr6' && userDetails.accessGstr6.toString() == 'false'){
				$(this).html("false");
			}else if(feature_field == 'accessGstr6' && userDetails.accessGstr6.toString() == 'true'){
				$(this).html("true");
			}
			if(feature_field == 'accessGstr8' && userDetails.accessGstr8.toString() == 'false'){
				$(this).html("false");
			}else if(feature_field == 'accessGstr8' && userDetails.accessGstr8.toString() == 'true'){
				$(this).html("true");
			}
		});
		
	}
	
	function getRefClientTableColumns(){
		var name = {data:  function ( data, type, row ) {
			var fullname = '';
			if(data.name != undefined && data.name != ''){
				fullname = data.name;
			}
			return '<span class="text-left">'+fullname+'</span>';
		}};
		var email = {data:  function ( data, type, row ) {
			var emailid = '';
			if(data.email != undefined && data.email != ''){
				emailid = data.email;
			}
			return '<span class="text-left">'+emailid+'</span>';
		}};
		var mobileno = {data:  function ( data, type, row ) {
			var mobilenumber = '';
			if(data.mobilenumber != undefined && data.mobilenumber != ''){
				mobilenumber = data.mobilenumber;
			}
			return '<span class="text-left">'+mobilenumber+'</span>';
		}};
		var usertype = {data:  function ( data, type, row ) {
			var usrtyp = '';
			if(data.clienttype != undefined && data.clienttype != ''){
				usrtyp = data.clienttype;
			}
			return '<span class="text-left">'+usrtyp+'</span>';
		}};
		var paidamt = {data: function ( data, type, row ) {
			var amount = 0.0;
			if(data.subscriptionAmount){
				amount = data.subscriptionAmount;
			}
		   	 return '<span class="ind_formats text-right">'+formatNumber(amount.toFixed(2))+'</span>';
	    }};
		
		var jointype = {data:  function ( data, type, row ) {
			var join = '';
			if(data.status != undefined && data.status != ''){
				join = data.status;
			}
			return '<span class="text-left">'+join+'</span>';
		}};
		
		/*var action = {data: function ( data, type, row ) {
		   	return '<div class="button-type nottoedit"><button onClick="sendEmail(\''+data.docId+'\',\''+data.user.fullname+'\',\''+data.user.email+'\',\''+(new Date(data.expiryDate)).toLocaleDateString("en-GB")+'\')" class="datatablebody btn btn-success" data-toggle="modal" data-target="#sendMessageModal" role="button" style="padding: 4px 10px 4px 10px; font-size: 14px;">SEND</button></div>';
	    }};*/
		return [name, mobileno, email, jointype, paidamt];
	}
	
function getPartnerClientPaymentTableColumns(){
	var userid = {data:  function ( data, type, row ) {
		var userid = '';
		if(data.userid != undefined && data.userid != ''){
			userid = data.userid;
		}
		return '<span class="text-left">'+userid+'</span>';
	}};
	
	var name = {data:  function ( data, type, row ) {
		var fullname = '';
		if(data.partnerClient != undefined && data.partnerClient != ''){
			fullname = data.partnerClient.name;
		}
		return '<span class="text-left">'+fullname+'</span>';
	}};
	var email = {data:  function ( data, type, row ) {
		var emailid = '';
		if(data.partnerClient != undefined && data.partnerClient != ''){
			emailid = data.partnerClient.email;
		}
		return '<span class="text-left">'+emailid+'</span>';
	}};
	var mobileno = {data: function ( data, type, row ) {
		var mobilenumber = '';
		if(data.partnerClient != undefined && data.partnerClient != ''){
			mobilenumber = data.partnerClient.mobilenumber;
		}
		return '<span class="text-left">'+mobilenumber+'</span>';
	}};
	
	var paidamt = {data: function ( data, type, row ) {
		var amount = 0.0;
		if(data.amount){
			amount = data.amount;
		}
	   	 return '<span class="ind_formats text-right">'+formatNumber(amount.toFixed(2))+'</span>';
    }};
	var paymentDate = {data: function ( data, type, row ) {
		var paymentdate = "";
		
		return '<span class="text-left">'+(new Date(data.paymentDate)).toLocaleDateString("en-GB")+'</span>';
	}};
	
	var partnerPayment = {data: function ( data, type, row ) {
		var paymentstatus = 0.0;
		if(data.partnerPayment){
			paymentstatus = data.partnerPayment;
		}
	   	return '<span class="ind_formats text-right">'+paymentstatus+'</span>';
    }};
	
	var usertype = {data:  function ( data, type, row ) {
		var usrtyp = '';
		if(data.apiType != undefined && data.apiType != ''){
			usrtyp = data.apiType;
		}
		return '<span class="text-left">'+usrtyp+'</span>';
	}};
	
	var mthCode = {data: function ( data, type, row ) {
		var code = '';
		if(data.mthCd){
			code = data.mthCd;
		}
		if(data.yrCd){
			code = code+"/"+ data.yrCd;
		}
	   	return '<span class="ind_formats text-right">'+code+'</span>';
    }};
	
	return [userid, name, email, paidamt, paymentDate, mthCode, partnerPayment, usertype];
}

function getPartnerPaymentTableColumns(type){
	var userid = {data:  function ( data, type, row ) {
		var userid = '';
		if(data.docId != undefined && data.docId != ''){
			userid = data.docId;
		}
		return '<span class="text-left">'+userid+'</span>';
	}};
	
	var paidamt = {data: function ( data, type, row ) {
		var amount = 0.0;
		if(data.subscriptionamount){
			amount = data.subscriptionamount;
		}
	   	 return '<span class="ind_formats text-right">'+formatNumber(amount.toFixed(2))+'</span>';
    }};
	var percentageamt = {data: function ( data, type, row ) {
		var amount = 0.0;
		if(data.percentage){
			amount = data.percentage;
		}
	   	 return '<span class="ind_formats text-right">'+formatNumber(amount.toFixed(2))+'</span>';
    }};
	
	var partnerPayment = {data: function ( data, type, row ) {
		var paymentpayment = 0.0;
		if(data.partneramt){
			paymentpayment = data.partneramt;
		}
	   	return '<span class="ind_formats text-right">'+formatNumber(paymentpayment.toFixed(2))+'</span>';
    }};
	
		
	var mthCode = {data: function ( data, type, row ) {
		var code = '';
		if(data.mthCd){
			code = data.mthCd;
		}
		if(data.yrCd){
			code = code+"/"+ data.yrCd;
		}
	   	return '<span class="ind_formats text-right">'+code+'</span>';
    }};
	if(type == 'paid'){
		var paidAmount = {data: function ( data, type, row ) {
			var paidamt = 0.0;
			if(data.paidamount){
				paidamt = data.paidamount;
			}
		   	return '<span class="ind_formats text-right">'+formatNumber(paidamt.toFixed(2))+'</span>';
	    }};
		var status = {data: function ( data, type, row ) {
			var st = 0.0;
			if(data.partnerPayment){
				st = data.partnerPayment;
			}
		   	return '<span class="ind_formats text-right">'+st+'</span>';
	    }};
		return [userid, paidamt, percentageamt, partnerPayment, paidAmount, mthCode, status];
	}else{
		var action = {data:  function ( data, type, row ) {
			
			return  '<a class="btn-edt btn btn-success" href="#" data-toggle="modal" data-target="#patnerPaymentModal" onclick="showPartnerPayments(\''+data.userid+'\',\''+data.docId+'\',\''+data.mthCd+'\',\''+data.yrCd+'\')">Pay</a>';
			
			//return '<img style="width:25px;" data-toggle="modal" data-target="#ppaymentCommentsModal" onclick="payPartnerPayments(\''+data.docId +'\',\''+ data.mthCd +'\',\''+ data.yrCd+'\')" class="cmntsimg mr-2" src="'+_getContextPath()+'/static/mastergst/images/dashboard-ca/comments-black.png" /><a class="btn-edt" href="#" data-toggle="modal" data-target="#patnerPaymentModal" onclick="showPartnerPayments(\''+data.userId+'\',\''+data.fullname+'\',\''+data.subscriptionamount+'\',\''+data.partnerPercentage+'\')"><i class="fa fa-edit" style="font-size:22px;vertical-align:sub;"></i></a>';
	    }};
		return [userid, paidamt, percentageamt, partnerPayment, mthCode, action];
	}
}

function showPartnerPayments(userid, docid, month, yrCode){
	clearFormData()
	var prtnrpaidamount = 0;
	var percentage = 0;
	var partneramt = 0;
	var ramount = 0;
	var subamt = 0;
	var tdsAmt = 0;
	$.ajax({
		url:_getContextPath()+"/getppartnerPayments/"+docid+"?month="+month+"&year="+yrCode+"&type=",
		type:'GET',
		success : function(response) {
			
			if(response != null && response !=""){
				subamt = response.subscriptionamount;
				$('#pid').val(response.userid);
				$('#pname').val(response.fullname);
				$('#pshare').val(response.percentage);
				percentage = response.percentage;
				if(response.paidamount !=null && response.paidamount !=""){
					prtnrpaidamount += response.paidamount;
				}
				if(response.partneramt !=null && response.partneramt !=""){
					partneramt += response.partneramt					
				}
				if(response.tdsamt !=null && response.tdsamt !=""){
					tdsAmt += response.tdsamt;
				}
				$('#pinvoiceno').val(response.invoiceno);
				$('#pinvoicedate').val(response.invoicedate);
			}
			partneramt = (subamt * percentage) / 100;
			ramount = parseFloat(partneramt) - (parseFloat(prtnrpaidamount)+parseFloat(tdsAmt));
			$('#ptdsamt').val(tdsAmt.toFixed(2));
			$('#subamt').val(subamt.toFixed(2));
			$('#pamount').val(partneramt.toFixed(2));
			$('#paidamt').val(prtnrpaidamount.toFixed(2));
			$('#ramount').val(ramount.toFixed(2));
		}
	});
	$('#savePartnerPayments').attr("onClick","savePartnerPayments(\'"+userid+"\',\'"+docid+"\',\'"+month+"\',\'"+yrCode+"\')");
}

function savePartnerPayments(puserid, docid, mthCd, yrCd){
	var partnerPayments =new Object();
	$('#savePartnerPayments').addClass('btn-loader');
	$('#savePartnerPayments').addClass('disable');
	var fullname = $('#pname').val();
	var subscriptionamount = $('#subamt').val();
	var percentage = $('#pshare').val();
	var partneramt = $('#pamount').val();
	var paidamount = $('#paidamt').val();
	
	var paidtdsamount = $('#ptdsamt').val();
	var paidinvoiceno = $('#pinvoiceno').val();
	var paidinvoicedate = $('#pinvoicedate').val();
	
	partnerPayments.userid = puserid;
	partnerPayments.fullname = fullname;
	partnerPayments.percentage = percentage;
	partnerPayments.subscriptionamount = subscriptionamount;
	partnerPayments.paidamount = paidamount;
	partnerPayments.partneramt = partneramt;
	partnerPayments.invoiceno = paidinvoiceno;
	partnerPayments.invoicedate = paidinvoicedate;
	partnerPayments.tdsamt = paidtdsamount
	
	$.ajax({
		url:_getContextPath()+"/saveppartnerPayments/"+puserid+"?month="+mthCd+"&year="+yrCd+"&type=",
		type : "POST",
		contentType : "application/json",
		data : JSON.stringify(partnerPayments),
		dataType : 'json',
		success : function(response) {
			if(response != null && response !=""){
				subamt = response.subscriptionamount;
				if(response.paidamount !=null && response.paidamount !=""){
					prtnrpaidamount = response.paidamount;
				}
				if(response.partneramt !=null && response.partneramt !=""){
					partneramt = response.partneramt;					
				}
			}
			$('#savePartnerPayments').removeClass('btn-loader');
			$('#savePartnerPayments').removeClass('disable');
			$("#patnerPaymentModal").modal('hide');
		}
	});
	partnerPendingPaymentTableUrl['pending'].ajax.reload();
	clientPendingPaymentTableUrl['pending'].ajax.reload();
}
		
function findRemainingAmts(){
	var partneramt = $('#pamount').val();
	var paidamount = $('#paidamt').val();
	var tdsAmt = $('#ptdsamt').val();
	if(partneramt.trim() == ""){partneramt = 0;}
	if(paidamount.trim() == ""){paidamount = 0;}
	if(tdsAmt.trim()== ""){tdsAmt = 0;}
	var ramt = parseFloat(partneramt) - (parseFloat(paidamount)+parseFloat(tdsAmt));
	$('#ramount').val(parseFloat(ramt).toFixed(2));
}
function generateUUID() {
    var d = new Date().getTime();
    var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = (d + Math.random()*16)%16 | 0;
        d = Math.floor(d/16);
        return (c=='x' ? r : (r&0x3|0x8)).toString(16);
    });
    return "GSP"+uuid;
}
function clearFormData(){	
	$('#pid').val('');
	$('#pname').val('');
	$('#pshare').val('');
	$('#subamt').val('');
	$('#pamount').val('');
	$('#paidamt').val('');
	$('#ramount').val('');
	
	$('#ptdsamt').val('');
	$('#pinvoiceno').val('');
	$('#pinvoicedate').val('');
}