	
function getUsersData(type, nav){
		if ( $.fn.DataTable.isDataTable('#dbTable') ) {
			  $('#dbTable').DataTable().destroy();
		}
		$('#dbTable thead').empty();
		$('#dbTable tbody').empty();
		if(nav){
			$('a.nav-link').removeClass('active');
			$('a.'+nav).addClass('active');
		}
		var typeurl;
		if(type =='subcenters'){
			typeurl=contextPath+"/allsubcenters?type="+type
		}else{
			typeurl=contextPath+"/allusers?type="+type
		}
		
		if(type !='All' && type != "subusers" && type !='subcenters' && type !='spamusers' && type !='testaccounts'){
			$('#sendEmailsBtn').attr('onclick', "sendEmails(\'"+type+"\')");
			$('#sendEmailsBtn').removeClass('d-none');
		}
		if(type =='All' || type == "subusers" || type =='subcenters' || type =='spamusers' || type == 'testaccounts'){
			$('#sendEmailsBtn').removeAttr('onclick');
			$('#sendEmailsBtn').addClass('d-none');
		}		
		table = $('#dbTable').DataTable({
		     "ajax": {
		         url: typeurl,
		         type: 'GET'
		     },
		     "serverSide": true,
		     "paging": true,
		     "order": [[0,'desc']],
		     'pageLength':10,
	         dom: '<"toolbar">lBfrtip',
	         columns: columnSpecification[type]
		 });
		$(".tabtable1  div.toolbar").html('<h4>My Users&nbsp;&nbsp;</h4>');
		if(type =='spamusers'){
			$('#alldeletespamusrs').prop('checked',false);
			$('#dbTable tbody').off("click");
			$('#spamusrlnk').addClass('active');
			$('#spam_users_delete').removeClass('d-none');
			$('#dbTable thead tr th:first-child').attr('disabled',true);
			$('#dbTable tbody').on('click', 'tr .usrinfo', function () {getUserDetails(this);}); 
		}else{
			$('#spam_users_delete').addClass('d-none');
			$('#dbTable tbody').on('click', 'tr', function () {getUserDetails(this);}); 
		}
	}
	function getUserDetails(obj){
		var user = table.row(obj).data();
		selRow = obj;
		userArray = user;
		$.ajax({
			url: contextPath+"/admusrdtls?id="+user.userId+"&type="+user.type,
			async: false,
			cache: false,
			success : function(data) {
				if(data.usrLastLoggedIn) {
					user.usrLastLoggedIn=data.usrLastLoggedIn;
				}
				if(data.accessGstr9) {
					user.accessGstr9=data.accessGstr9;
				}
				/*if(data.accessGstr4Annual) {
					user.accessGstr4Annual=data.accessGstr4Annual;
				}
				if(data.accessGstr6) {
					user.accessGstr6=data.accessGstr6;
				}*/
				if(data.accessGstr8) {
					user.accessGstr8=data.accessGstr8;
				}
				if(data.accessDwnldewabillinv) {
					user.accessDwnldewabillinv=data.accessDwnldewabillinv;
				}
				if(data.accessMultiGSTNSearch) {
					user.accessMultiGSTNSearch=data.accessMultiGSTNSearch;
				}
				if(data.accessReminders) {
					user.accessReminders=data.accessReminders;
				}
				if(data.accessANX1) {
					user.accessANX1=data.accessANX1;
				}
				if(data.accessANX2) {
					user.accessANX2=data.accessANX2;
				}
				if(data.accessEinvoice) {
					user.accessEinvoice=data.accessEinvoice;
				}
				if(data.accessCustomFieldTemplate) {
					user.accessCustomFieldTemplate=data.accessCustomFieldTemplate;
				}
				if(data.accessEntertainmentEinvoiceTemplate) {
					user.accessEntertainmentEinvoiceTemplate=data.accessEntertainmentEinvoiceTemplate;
				}
				if(data.accessAcknowledgement) {
					user.accessAcknowledgement=data.accessAcknowledgement;
				}
				if(data.subscriptionType) {
					user.subscriptionType=data.subscriptionType;
				}
				if(data.paidAmount) {
					user.paidAmount=data.paidAmount;
				}
				if(data.totalCenters) {
					user.totalCenters=data.totalCenters;
				}
				if(data.totalInvoices) {
					user.totalInvoices=data.totalInvoices;
				}
				if(data.totalInvoicesUsed) {
					user.totalInvoicesUsed=data.totalInvoicesUsed;
				}
				
				if(data.comments) {
					user.comments=data.comments;
				}
				
				if(data.commentDate) {
					user.commentDate=data.commentDate;
				}
				
				if(data.addedby) {
					user.addedby=data.addedby;
				}
				
				/* if(data.branches) {
					user.branches=data.branches;
				}
				if(data.anyerp) {
					user.anyerp=data.anyerp;
				} */
				if(data.partnerType) {
					user.partnerType=data.partnerType;
				}
				if(data.totalClients) {
					user.totalClients=data.totalClients;
				}
				if(data.totalInvitedClients) {
					user.totalInvitedClients=data.totalInvitedClients;
				}
				if(data.totalPendingClients) {
					user.totalPendingClients=data.totalPendingClients;
				}
				if(data.totalJoinedClients) {
					user.totalJoinedClients=data.totalJoinedClients;
				}
				if(data.totalSubscribedClients) {
					user.totalSubscribedClients=data.totalSubscribedClients;
				}
				
				if(data.gstAPIAllowedInvoices) {
					user.gstAPIAllowedInvoices=data.gstAPIAllowedInvoices;
				}
				if(data.gstSanboxAllowedCountInvoices) {
					user.gstSanboxAllowedCountInvoices=data.gstSanboxAllowedCountInvoices;
				}
				if(data.ewaybillAPIAllowedInvoices) {
					user.ewaybillAPIAllowedInvoices=data.ewaybillAPIAllowedInvoices;
				}
				if(data.ewaybillSanboxAllowedInvoices) {
					user.ewaybillSanboxAllowedInvoices=data.ewaybillSanboxAllowedInvoices;
				}
				if(data.einvAPIAllowedInvoices) {
					user.einvAPIAllowedInvoices=data.einvAPIAllowedInvoices;
				}
				if(data.einvSanboxAllowedInvoices) {
					user.einvSanboxAllowedInvoices=data.einvSanboxAllowedInvoices;
				}
				if(data.gstAPIUsageCountInvoices) {	
					user.gstAPIUsageCountInvoices=data.gstAPIUsageCountInvoices;
				}
				if(data.gstSanboxUsageCountInvoices) {
					user.gstSanboxUsageCountInvoices=data.gstSanboxUsageCountInvoices;
				}
				if(data.ewaybillAPIUsageCountInvoices) {
					user.ewaybillAPIUsageCountInvoices=data.ewaybillAPIUsageCountInvoices;
				}
				if(data.ewaybillSanboxUsageCountInvoices) {
					user.ewaybillSanboxUsageCountInvoices=data.ewaybillSanboxUsageCountInvoices;
				}
				if(data.einvAPIUsageCountInvoices) {
					user.einvAPIUsageCountInvoices=data.einvAPIUsageCountInvoices;
				}
				if(data.einvSanboxUsageCountInvoices) {
					user.einvSanboxUsageCountInvoices=data.einvSanboxUsageCountInvoices;
				}
				if(data.subscriptionStartDate){
					user.subscriptionStartDate=data.subscriptionStartDate;
				}
				if(data.subscriptionExpiryDate){
					user.subscriptionExpiryDate=data.subscriptionExpiryDate;
				}
				if(data.gstSubscriptionStartDate){
					user.gstSubscriptionStartDate=data.gstSubscriptionStartDate;;
				}
				if(data.gstSubscriptionExpiryDate){	
					user.gstSubscriptionExpiryDate=data.gstSubscriptionExpiryDate;;
				}
				if(data.gstSandboxSubscriptionStartDate){
					user.gstSandboxSubscriptionStartDate=data.gstSandboxSubscriptionStartDate;;
				}
				if(data.gstSandboxSubscriptionExpiryDate){
					user.gstSandboxSubscriptionExpiryDate=data.gstSandboxSubscriptionExpiryDate;;
				}
				if(data.ewaybillSubscriptionStartDate){
					user.ewaybillSubscriptionStartDate=data.ewaybillSubscriptionStartDate;
				}
				if(data.ewaybillSubscriptionExpiryDate){
					user.ewaybillSubscriptionExpiryDate=data.ewaybillSubscriptionExpiryDate;
				}
				if(data.ewaybillSandboxSubscriptionStartDate){
					user.ewaybillSandboxSubscriptionStartDate=data.ewaybillSandboxSubscriptionStartDate;
				}
				if(data.ewaybillSandboxSubscriptionExpiryDate){
					user.ewaybillSandboxSubscriptionExpiryDate=data.ewaybillSandboxSubscriptionExpiryDate;
				}
				if(data.einvSubscriptionStartDate){
					user.einvSubscriptionStartDate=data.einvSubscriptionStartDate;
				}
				if(data.einvSubscriptionExpiryDate){
					user.einvSubscriptionExpiryDate=data.einvSubscriptionExpiryDate;
				}
				if(data.einvSandboxSubscriptionStartDate){
					user.einvSandboxSubscriptionStartDate=data.einvSandboxSubscriptionStartDate;
				}
				if(data.ewaybillSandboxSubscriptionExpiryDate){
					user.einvSandboxSubscriptionExpiryDate=data.einvSandboxSubscriptionExpiryDate;
				}
				if(data.gstAPIStatus){
					if(data.gstAPIStatus=='EXPIRED'){
						$('#gstAPIStatus').css("color", "red");
					}
					user.gstAPIStatus=data.gstAPIStatus;
				}
				if(data.gstSandboxAPIStatus){
					if(data.gstSandboxAPIStatus=='EXPIRED'){
						$('#gstSandboxAPIStatus').css("color", "red");
					}
					user.gstSandboxAPIStatus=data.gstSandboxAPIStatus;
				}
				if(data.ewaybillAPIStatus){
					if(data.ewaybillAPIStatus=='EXPIRED'){
						$('#ewaybillAPIStatus').css("color", "red");
					}
					user.ewaybillAPIStatus=data.ewaybillAPIStatus;
				}
				if(data.ewaybillSandboxAPIStatus){
					if(data.ewaybillSandboxAPIStatus=='EXPIRED'){
						$('#ewaybillSandboxAPIStatus').css("color", "red");
					}
					user.ewaybillSandboxAPIStatus=data.ewaybillSandboxAPIStatus;
				}
				if(data.einvSandboxAPIStatus){
					if(data.einvSandboxAPIStatus=='EXPIRED'){
						$('#einvSandboxAPIStatus').css("color", "red");
					}
					user.einvSandboxAPIStatus=data.einvSandboxAPIStatus;
				}
				if(data.type){
					user.type=data.type;
				}
				if(data.needFollowupdate){
					user.needFollowupdate=data.needFollowupdate;
				}
			}
		});
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
		if(user.type == 'aspdeveloper') {
			currentUser = user.userId;
			tabActivities(user.type);
			loadPayments(currentUser);
			loadComments(currentUser);
			loadUsageInvoices(currentUser);
			loadHeaderKeys(currentUser);
			loadAsp_apiuserdetails(currentUser);
			//loadewayPayments(currentUser);
			
			$('.usercomment_field').each(function(){ 
				var field = $(this).attr('data');
				
				var value=user[field];
				if(value==""){
					$(this).html('');
				}else{
					
					$(this).html(value);
				}
					
			});
	
			$('.user_field').each(function() {  
				var field = $(this).attr('data');
				var value='';
				value = user[field];
				if(field == 'disable') {
					if(user.disable == 'true') {
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
				if(user.userkeys) {
					user.userkeys.forEach(function(userkey) {
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
				if(user.userkeys) {
					user.userkeys.forEach(function(userkey) {
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
				if(user.userkeys) {
					user.userkeys.forEach(function(userkey) {
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
					if(user.userkeys) {
						user.userkeys.forEach(function(userkey) {
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
				if(user.userkeys) {
					user.userkeys.forEach(function(userkey) {
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
				if(user.userkeys) {
					user.userkeys.forEach(function(userkey) {
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
		}else if(user.type == 'cacmas'){
			currentUser = user.userId;
			tabActivities(user.type);
			loadPayments(currentUser);
			loadComments(currentUser);
			loadSecondary(currentUser);
			loadHeaderKeys(currentUser);
			$('.usercomment_field').each(function(){ 
				var field = $(this).attr('data');
				
				var value=user[field];
				if(value==""){
					$(this).html('');
				}else{
					
					$(this).html(value);
				}
					
			});
			
			$('.user_field').each(function() {
				var field = $(this).attr('data');
				var value='';
				value = user[field];		
				if(field == 'accessDrive' && value == false){
					value ='Disabled';
				}else if(field == 'accessDrive' && value == null){
					value = 'Disabled';	
				}else if(field == 'accessDrive' && value == true){
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
				if(field == 'accessTravel' && value == false){
					value = 'Disabled';	
				}else if(field == 'accessTravel' && value == null){
					value = 'Disabled';	
				}else if(field == 'accessTravel' && value == true){
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
				if(field == 'accessAcknowledgement' && (value == false || value == null)){
					value ='Disabled';
				}else if(field == 'accessAcknowledgement' && value == true){
					value = 'Enabled';
				}
				if(field == 'disable') {
					if(user.disable == 'true') {
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
		}else if(user.type == 'business'){
			currentUser = user.userId;
			tabActivities(user.type);
			loadPayments(currentUser);
			loadComments(currentUser);
			loadSecondary(currentUser);
			loadHeaderKeys(currentUser);
			$('.usercomment_field').each(function(){ 
				var field = $(this).attr('data');
				
				var value=user[field];
				if(value==""){
					$(this).html('');
				}else{
					
					$(this).html(value);
				}
					
			});
			
			$('.user_field').each(function() {
				var field = $(this).attr('data');
				var value='';
				value = user[field];
				if(field == 'disable') {
					if(user.disable == 'true') {
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
				if(field == 'accessAcknowledgement' && (value == false || value == null)){
					value = 'Disabled';
				}else if(field == 'accessAcknowledgement' && value == true){
					value = 'Enabled';
				}
				$(this).html(value);
			});
		}else if(user.type == 'taxp'){
			currentUser = user.userId;
			tabActivities(user.type);
			loadPayments(currentUser);
			loadComments(currentUser);
			loadSecondary(currentUser);
			loadHeaderKeys(currentUser);
			$('.usercomment_field').each(function(){ 
				var field = $(this).attr('data');
				
				var value=user[field];
				if(value==""){
					$(this).html('');
				}else{
					
					$(this).html(value);
				}
					
			});
			
			$('.user_field').each(function() {
				var field = $(this).attr('data');
				var value='';
				value = user[field];
				if(field == 'disable') {
					if(user.disable == 'true') {
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
		}else if(user.type == 'enterprise'){
			currentUser = user.userId;
			tabActivities(user.type);
			loadPayments(currentUser);
			loadComments(currentUser);
			loadSecondary(currentUser);
			loadEnterprisePayments(currentUser);
			
			loadHeaderKeys(currentUser);
			$('.usercomment_field').each(function(){ 
				var field = $(this).attr('data');
				
				var value=user[field];
				if(value==""){
					$(this).html('');
				}else{
					
					$(this).html(value);
				}
					
			});
			
			$('.user_field').each(function() {
				var field = $(this).attr('data');
				var value='';
				value = user[field];
				if(field == 'disable') {
					if(user.disable == 'true') {
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
		}else if(user.type == 'partner'){
			var date = new Date();
			var month = date.getMonth()+1;
			var	year = date.getFullYear();
			
			var date1 = $('.paymentpendingMonths').datepicker({
				autoclose: true,
				viewMode: 1,
				minViewMode: 1,
				format: 'mm-yyyy'
			}).on('changeDate', function(ev) {
				month = ev.date.getMonth()+1;
				year = ev.date.getFullYear();
			});
			var dateValue = ((''+month).length<2 ? '0' : '') + month + '-' + year;
			$('.paymentpendingMonths').datepicker('update', dateValue);
			
			var paidmonth = date.getMonth()+1;
			var	paidyear = date.getFullYear();
			var date2 = $('.paymentpaidMonths').datepicker({
				autoclose: true,
				viewMode: 1,
				minViewMode: 1,
				format: 'mm-yyyy'
			}).on('changeDate', function(ev) {
				paidmonth = ev.date.getMonth()+1;
				paidyear = ev.date.getFullYear();
			});
			var paiddateValue1 = ((''+paidmonth).length<2 ? '0' : '') + paidmonth + '-' + paidyear;
			$('.paymentpaidMonths').datepicker('update', paiddateValue1);
			
			currentUser = user.userId;
			tabActivities(user.type);
			loadPayments(currentUser);
			loadPartnerBankdetails(currentUser);
			loadComments(currentUser);
			loadClientsDeatils(currentUser);
			
			loadPartnerAndClientPayments(currentUser, 0, year);
			$('#paidmonthlybtn').attr('onClick','generatePaidData("'+currentUser+'")');
			$('#pendingmonthlybtn').attr('onClick','generatePendingData("'+currentUser+'")');
			$('.usercomment_field').each(function(){ 
				var field = $(this).attr('data');
				
				var value=user[field];
				if(value==""){
					$(this).html('');
				}else{
					
					$(this).html(value);
				}
					
			});
			
			$('.user_field').each(function() {
				var field = $(this).attr('data');
				var value='';
				value = user[field];
				if(field == 'disable') {
					if(user.disable == 'true') {
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
			$('#clientPaymentDetailsTab').css("display","none");
			$('#clientPendingPaymentDetailsTab').css("display","none");
		}else if(user.type == 'suvidha'){
			currentUser = user.userId;
			tabActivities(user.type);
			loadPayments(currentUser);
			loadComments(currentUser);
			loadSuvidhaPaymentData(currentUser);
			loadSecondary(currentUser);
			loadSuvidhaCenters(currentUser);
			loadHeaderKeys(currentUser);
			$('.usercomment_field').each(function(){ 
				var field = $(this).attr('data');
				
				var value=user[field];
				if(value==""){
					$(this).html('');
				}else{
					
					$(this).html(value);
				}
					
			});
			
			$('.user_field').each(function() {
				var field = $(this).attr('data');
				var value='';
				value = user[field];
				if(field == 'disable') {
					if(user.disable == 'true') {
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
				if(field == 'accessAcknowledgement' && value == false || value == null){
					value = 'Disabled';
				}else if(field == 'accessAcknowledgement' && value == true){
					value = 'Enabled';
				}
				
				$(this).html(value);
			});
		}
			
	}
	
	$(document).ready(function(){
		$('#users_lnk').addClass('active');
		$('#nav_client').addClass('active');
		//getUsersData('All');
	});
	
	function saveUserKeys(stage) {
		var userkey=new Object;
		userkey.stage = stage;
		if(stage == 'Sandbox') {
			userkey.isenabled=$('#sandboxRadio').is(':checked');
			$('.usersandbox_field').each(function() {
				var field = $(this).attr('data');
				if(field != 'createdate' && field != 'isenabled') {
					userkey[field]=$('#sand'+field).val();
				}
				$(this).html(userkey[field]);
				if(field == 'isenabled' && userkey.isenabled.toString() == 'false'){
					$(this).html("No");
				}else if(field == 'isenabled' && userkey.isenabled.toString() == 'true'){
					$(this).html("Yes");
				}
			});
		} else if(stage == 'Production'){
			userkey.isenabled=$('#productionRadio').is(':checked');
			$('.userprod_field').each(function() {
				var field = $(this).attr('data');
				if(field != 'createdate' && field != 'isenabled' && field != 'clientid' && field != 'clientsecret' && field != 'gstclientid' && field != 'gstclientsecret') {
					userkey[field]=$('#prod'+field).val();
				}
				if(field == 'clientid'){
					var clientid = $('#prod'+field).val();
					userkey[field]=$.trim(clientid);
				}
				if(field == 'clientsecret'){
					var clientsecret = $('#prod'+field).val();
					userkey[field]=$.trim(clientsecret);
				}
				
				if(field == 'gstclientid'){
					var gstclientid = $('#prod'+field).val();
					userkey[field]=$.trim(gstclientid);
				}
				if(field == 'gstclientsecret'){
					var gstclientsecret = $('#prod'+field).val();
					userkey[field]=$.trim(gstclientsecret);
				}
				
				$(this).html(userkey[field]);
				if(field == 'isenabled' && userkey.isenabled.toString() == 'false'){
					$(this).html("No");
				}else if(field == 'isenabled' && userkey.isenabled.toString() == 'true'){
					$(this).html("Yes");
				}
			});
		}else if(stage == 'EwayBillSandBox'){
			userkey.isenabled=$('#ewaySandRadio').is(':checked');
			$('.userseway_field').each(function() {
				var field = $(this).attr('data');
				if(field != 'createdate' && field != 'isenabled' && field != 'clientid' && field != 'clientsecret') {
					userkey[field]=$('#ewaySand'+field).val();
				}
				if(field == 'clientid'){
					var clientid = $('#ewaySand'+field).val();
					userkey[field]=$.trim(clientid);
				}
				if(field == 'clientsecret'){
					var clientsecret = $('#ewaySand'+field).val();
					userkey[field]=$.trim(clientsecret);
				}
				$(this).html(userkey[field]);
				if(field == 'isenabled' && userkey.isenabled.toString() == 'false'){
					$(this).html("No");
				}else if(field == 'isenabled' && userkey.isenabled.toString() == 'true'){
					$(this).html("Yes");
				}
			});
		}else if(stage == 'EwayBillProduction'){
			userkey.isenabled=$('#ewayRadio').is(':checked');
			$('.userpeway_field').each(function() {
				var field = $(this).attr('data');
				if(field != 'createdate' && field != 'isenabled' && field != 'clientid' && field != 'clientsecret') {
					userkey[field]=$('#eway'+field).val();
				}
				if(field == 'clientid'){
					var clientid = $('#eway'+field).val();
					userkey[field]=$.trim(clientid);
				}
				if(field == 'clientsecret'){
					var clientsecret = $('#eway'+field).val();
					userkey[field]=$.trim(clientsecret);
				}
				$(this).html(userkey[field]);
				if(field == 'isenabled' && userkey.isenabled.toString() == 'false'){
					$(this).html("No");
				}else if(field == 'isenabled' && userkey.isenabled.toString() == 'true'){
					$(this).html("Yes");
				}
			});
		}else if(stage == 'EInvoiceSandBox'){
			userkey.isenabled=$('#einvSandRadio').is(':checked');
			$('.userseinv_field').each(function() {
				var field = $(this).attr('data');
				if(field != 'createdate' && field != 'isenabled' && field != 'clientid' && field != 'clientsecret' && field != 'username' && field != 'password') {
					userkey[field]=$('#einvSand'+field).val();
				}
				if(field == 'clientid'){
					var clientid = $('#einvSand'+field).val();
					userkey[field]=$.trim(clientid);
				}
				if(field == 'username'){
					var einvUsername = $('#einvSand'+field).val();
					userkey[field]=$.trim(einvUsername);	
				}
				if(field == 'password'){
					var einvpassword = $('#einvSand'+field).val();
					userkey[field]=$.trim(einvpassword);
				}
				if(field == 'clientsecret'){
					var clientsecret = $('#einvSand'+field).val();
					userkey[field]=$.trim(clientsecret);
				}
				$(this).html(userkey[field]);
				if(field == 'isenabled' && userkey.isenabled.toString() == 'false'){
					$(this).html("No");
				}else if(field == 'isenabled' && userkey.isenabled.toString() == 'true'){
					$(this).html("Yes");
				}
			});
		}else if(stage == 'EInvoiceProduction'){
			userkey.isenabled=$('#einvRadio').is(':checked');
			$('.userpeinv_field').each(function() {
				var field = $(this).attr('data');
				if(field != 'createdate' && field != 'isenabled' && field != 'clientid' && field != 'clientsecret') {
					userkey[field]=$('#einv'+field).val();
				}
				if(field == 'clientid'){
					var clientid = $('#einv'+field).val();
					userkey[field]=$.trim(clientid);
				}
				if(field == 'clientsecret'){
					var clientsecret = $('#einv'+field).val();
					userkey[field]=$.trim(clientsecret);
				}
				$(this).html(userkey[field]);
				if(field == 'isenabled' && userkey.isenabled.toString() == 'false'){
					$(this).html("No");
				}else if(field == 'isenabled' && userkey.isenabled.toString() == 'true'){
					$(this).html("Yes");
				}
			});
		}
		if(userkey.clientid != '' && userkey.clientsecret != '') {
			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : 'updatecredentials?id='+currentUser,
				data : JSON.stringify(userkey),
				dataType : 'json',
				success : function(data) {
					table.row(selRow).data(data).draw();
					if(stage == 'Sandbox'){
						$('#sand_save_btn').css('display','none');
						$('#sand_edit_btn').css('display','block');
					}else if(stage == 'Production'){
						$('#prod_save_btn').css('display','none');
						$('#prod_edit_btn').css('display','block');
					}else if(stage == 'EwayBillSandBox'){
						$('#ewaySand_save_btn').css('display','none');
						$('#ewaySand_edit_btn').css('display','block');
					}else if(stage == 'EwayBillProduction'){
						$('#eway_save_btn').css('display','none');
						$('#eway_edit_btn').css('display','block');
					}else if(stage == 'EInvoiceSandBox'){
						$('#einvSand_save_btn').css('display','none');
						$('#einvSand_edit_btn').css('display','block');
					}else if(stage == 'EInvoiceProduction'){
						$('#einv_save_btn').css('display','none');
						$('#einv_edit_btn').css('display','block');
					}
				}
			});
		}
	}
	function usersTabInfo(inv){
		$('.menu-link').removeClass('active');
		if(inv == 'All'){
			$('#Allusersdetls').removeClass('d-none');
			$('#activeusersdetls').addClass('d-none');			
			$('#spamusersdetls').addClass('d-none');
			getUsersData('All', 'All');
			$('.Allusers').addClass('active');
		}else if(inv == 'active'){
			$('#Allusersdetls').addClass('d-none');
			$('#activeusersdetls').removeClass('d-none');			
			$('#spamusersdetls').addClass('d-none');
			$('.activeusers').addClass('active');
			getUsersData('active-aspdeveloper', 'active-aspdeveloper');
		}else if(inv == 'spam'){
			$('#Allusersdetls').addClass('d-none');
			$('#activeusersdetls').addClass('d-none');			
			$('#spamusersdetls').removeClass('d-none');
			$('.spamusers').addClass('active');
			getUsersData('spamusers', 'spamusers');
		}
	}
	$(function(){
		usersTabInfo('All');
	});
	function adminDashboard(tabId,nav){
		$('#userModal a.nav-link').removeClass('active');
		$(nav).addClass('active');
		$('#clientPaymentDetailsTab').css("display","none");
		$('#clientPendingPaymentDetailsTab').css("display","none");
		if(tabId == 'pymentTab'){
			$('#commentsTab').removeClass('d-block');
			$('#commentsTab').removeClass("active");
			$('#'+tabId).css("display","block");
			$('#clients,#details').css("display","none");
			$('#invoices').css("display","none");
			$('#sandboxCredentials').css("display","none");
			$('#productionCredentials').css("display","none");
			$('#ewaybillCredentials').css("display","none");
			$('#eInvoiceCredentials').css("display","none");
			$('#payment').css("display","none");
			$('#ewaypayment').css("display","none");
			$('#users').css("display","none");
			$('#save_btn').css('display','none');
			$('#edit_btn').css('display','block');
			$('#sand_save_btn').css('display','none');
			$('#sand_edit_btn').css('display','none');
			$('#prod_save_btn').css('display','none');
			$('#prod_edit_btn').css('display','none');
			$('#eway_save_btn').css('display','none');
			$('#eway_edit_btn').css('display','none');
			$('#ewaySand_save_btn').css('display','none');
			$('#ewaySand_edit_btn').css('display','none');
			$('#einv_save_btn').css('display','none');
			$('#einv_edit_btn').css('display','none');
			$('#einvSand_save_btn').css('display','none');
			$('#einvSand_edit_btn').css('display','none');
			$('#commentsTab').css("display","none");
			$('#centersTab').css("display","none");
			$('#partnerBankTab').css("display","none");
			$('#refClientTab').css("display","none");
			$('#clientPaymentDetailsTab').css("display","none");
			$('#clientPendingPaymentDetailsTab').css("display","none");
			$('#headerkeysTab').css("display","none");
			$('#asp_apiuserdetailsTab').css("display","none");
			$('#featuresTab').css("display","none");
		}else if(tabId == 'details'){
			$('#commentsTab').removeClass('d-block');
			$('#commentsTab').removeClass("active");
			$('#'+tabId).css("display","block");
			$('#clients').css("display","none");
			$('#invoices').css("display","none");
			$('#sandboxCredentials,#pymentTab').css("display","none");
			$('#productionCredentials').css("display","none");
			$('#ewaybillCredentials').css("display","none");
			$('#eInvoiceCredentials').css("display","none");
			$('#payment').css("display","none");
			$('#ewaypayment').css("display","none");
			$('#users').css("display","none");
			$('#save_btn').css('display','none');
			$('#edit_btn').css('display','block');
			$('#sand_save_btn').css('display','none');
			$('#sand_edit_btn').css('display','none');
			$('#prod_save_btn').css('display','none');
			$('#prod_edit_btn').css('display','none');
			$('#eway_save_btn').css('display','none');
			$('#eway_edit_btn').css('display','none');
			$('#ewaySand_save_btn').css('display','none');
			$('#ewaySand_edit_btn').css('display','none');
			$('#einv_save_btn').css('display','none');
			$('#einv_edit_btn').css('display','none');
			$('#einvSand_save_btn').css('display','none');
			$('#einvSand_edit_btn').css('display','none');
			$('#commentsTab').css("display","none");
			$('#centersTab').css("display","none");
			$('#partnerBankTab').css("display","none");
			$('#refClientTab').css("display","none");
			$('#clientPaymentDetailsTab').css("display","none");
			$('#clientPendingPaymentDetailsTab').css("display","none");
			$('#headerkeysTab').css("display","none");
			$('#asp_apiuserdetailsTab').css("display","none");
			$('#featuresTab').css("display","none");
		}else if(tabId == 'clients'){
			$('#commentsTab').removeClass('d-block');
			$('#commentsTab').removeClass("active");
			$('#'+tabId).css("display","block");
			$('#details,#pymentTab').css("display","none");
			$('#invoices').css("display","none");
			$('#sandboxCredentials').css("display","none");
			$('#productionCredentials').css("display","none");
			$('#ewaybillCredentials').css("display","none");
			$('#eInvoiceCredentials').css("display","none");
			$('#payment').css("display","none");
			$('#ewaypayment').css("display","none");
			$('#users').css("display","none");
			$('#commentsTab').css("display","none");
			$('#partnerBankTab').css("display","none");
			$('#centersTab').css("display","none");
			$('#refClientTab').css("display","none");
			$('#clientPaymentDetailsTab').css("display","none");
			$('#clientPendingPaymentDetailsTab2').css("display","none");
			$('#headerkeysTab').css("display","none");
			$('#asp_apiuserdetailsTab').css("display","none");
			$('#featuresTab').css("display","none");
		}else if(tabId == 'invoices'){
			$('#commentsTab').removeClass('d-block');
			$('#commentsTab').removeClass("active");
			$('#'+tabId).css("display","block");
			$('#details,#pymentTab').css("display","none");
			$('#clients').css("display","none");
			$('#sandboxCredentials').css("display","none");
			$('#productionCredentials').css("display","none");
			$('#ewaybillCredentials').css("display","none");
			$('#eInvoiceCredentials').css("display","none");
			$('#payment').css("display","none");
			$('#ewaypayment').css("display","none");
			$('#users').css("display","none");
			$('#commentsTab').css("display","none");
			$('#partnerBankTab').css("display","none");
			$('#centersTab').css("display","none");
			$('#refClientTab').css("display","none");
			$('#clientPaymentDetailsTab').css("display","none");
			$('#clientPendingPaymentDetailsTab2').css("display","none");
			$('#headerkeysTab').css("display","none");
			$('#asp_apiuserdetailsTab').css("display","none");
			$('#featuresTab').css("display","none");
		}else if(tabId == 'sandboxCredentials'){
			$('#commentsTab').removeClass('d-block');
			$('#commentsTab').removeClass("active");
			$('#'+tabId).css("display","block");
			$('#details,#pymentTab').css("display","none");
			$('#clients').css("display","none");
			$('#invoices').css("display","none");
			$('#productionCredentials').css("display","none");
			$('#ewaybillCredentials').css("display","none");
			$('#eInvoiceCredentials').css("display","none");
			$('#payment').css("display","none");
			$('#ewaypayment').css("display","none");
			$('#sand_edit_btn').css('display','block');
			$('#sand_save_btn').css('display','none');
			$('#save_btn').css('display','none');
			$('#edit_btn').css('display','none');
			$('#prod_save_btn').css('display','none');
			$('#prod_edit_btn').css('display','none');
			$('#eway_save_btn').css('display','none');
			$('#eway_edit_btn').css('display','none');
			$('#ewaySand_save_btn').css('display','none');
			$('#ewaySand_edit_btn').css('display','none');
			$('#einv_save_btn').css('display','none');
			$('#einv_edit_btn').css('display','none');
			$('#einvSand_save_btn').css('display','none');
			$('#einvSand_edit_btn').css('display','none');
			$('#users').css("display","none");
			$('#commentsTab').css("display","none");
			$('#partnerBankTab').css("display","none");
			$('#centersTab').css("display","none");
			$('#refClientTab').css("display","none");
			$('#clientPaymentDetailsTab').css("display","none");
			$('#clientPendingPaymentDetailsTab2').css("display","none");
			$('#headerkeysTab').css("display","none");
			$('#asp_apiuserdetailsTab').css("display","none");
			$('#featuresTab').css("display","none");
		}else if(tabId == 'productionCredentials'){
			$('#commentsTab').removeClass('d-block');
			$('#commentsTab').removeClass("active");
			$('#'+tabId).css("display","block");
			$('#details,#pymentTab').css("display","none");
			$('#clients').css("display","none");
			$('#invoices').css("display","none");
			$('#sandboxCredentials').css("display","none");
			$('#ewaybillCredentials').css("display","none");
			$('#eInvoiceCredentials').css("display","none");
			$('#payment').css("display","none");
			$('#ewaypayment').css("display","none");
			$('#save_btn').css('display','none');
			$('#edit_btn').css('display','none');
			$('#sand_save_btn').css('display','none');
			$('#sand_edit_btn').css('display','none');
			$('#eway_save_btn').css('display','none');
			$('#eway_edit_btn').css('display','none');
			$('#prod_edit_btn').css('display','block');
			$('#prod_save_btn').css('display','none');
			$('#ewaySand_save_btn').css('display','none');
			$('#ewaySand_edit_btn').css('display','none');
			$('#einv_save_btn').css('display','none');
			$('#einv_edit_btn').css('display','none');
			$('#einvSand_save_btn').css('display','none');
			$('#einvSand_edit_btn').css('display','none');
			$('#users').css("display","none");
			$('#commentsTab').css("display","none");
			$('#partnerBankTab').css("display","none");
			$('#centersTab').css("display","none");
			$('#refClientTab').css("display","none");
			$('#clientPaymentDetailsTab').css("display","none");
			$('#clientPendingPaymentDetailsTab2').css("display","none");
			$('#headerkeysTab').css("display","none");
			$('#asp_apiuserdetailsTab').css("display","none");
			$('#featuresTab').css("display","none");
		}else if(tabId == 'ewaybillCredentials'){
			$('#commentsTab').removeClass('d-block');
			$('#commentsTab').removeClass("active");
			$('#'+tabId).css("display","block");
			$('#details,#pymentTab').css("display","none");
			$('#clients').css("display","none");
			$('#invoices').css("display","none");
			$('#sandboxCredentials').css("display","none");
			$('#productionCredentials').css("display","none");
			$('#eInvoiceCredentials').css("display","none");
			$('#payment').css("display","none");
			$('#ewaypayment').css("display","none");
			$('#save_btn').css('display','none');
			$('#edit_btn').css('display','none');
			$('#sand_save_btn').css('display','none');
			$('#sand_edit_btn').css('display','none');
			$('#prod_edit_btn').css('display','none');
			$('#prod_save_btn').css('display','none');
			$('#eway_edit_btn').css('display','block');
			$('#eway_save_btn').css('display','none');
			$('#ewaySand_save_btn').css('display','none');
			$('#ewaySand_edit_btn').css('display','block');
			$('#einv_save_btn').css('display','none');
			$('#einv_edit_btn').css('display','none');
			$('#einvSand_save_btn').css('display','none');
			$('#einvSand_edit_btn').css('display','none');
			$('#users').css("display","none");
			$('#commentsTab').css("display","none");
			$('#partnerBankTab').css("display","none");
			$('#centersTab').css("display","none");
			$('#refClientTab').css("display","none");
			$('#clientPaymentDetailsTab').css("display","none");
			$('#clientPendingPaymentDetailsTab2').css("display","none");
			$('#headerkeysTab').css("display","none");
			$('#asp_apiuserdetailsTab').css("display","none");
			$('#featuresTab').css("display","none");
		}else if(tabId == 'payment'){
			$('#commentsTab').removeClass('d-block');
			$('#commentsTab').removeClass("active");
			$('#'+tabId).css("display","block");
			$('#details,#pymentTab').css("display","none");
			$('#clients').css("display","none");
			$('#invoices').css("display","none");
			$('#sandboxCredentials').css("display","none");
			$('#productionCredentials').css("display","none");
			$('#ewaybillCredentials').css("display","none");
			$('#eInvoiceCredentials').css("display","none");
			$('#ewaypayment').css("display","none");
			$('#users').css("display","none");
			$('#clientPaymentDetailsTab').css("display","none");
			$('#commentsTab').css("display","none");
			$('#partnerBankTab').css("display","none");
			$('#centersTab').css("display","none");
			$('#refClientTab').css("display","none");
			$('#clientPendingPaymentDetailsTab2').css("display","none");
			$('#headerkeysTab').css("display","none");
			$('#asp_apiuserdetailsTab').css("display","none");
			$('#featuresTab').css("display","none");
		}else if(tabId == 'ewaypayment'){
			$('#commentsTab').removeClass('d-block');
			$('#commentsTab').removeClass("active");
			$('#'+tabId).css("display","block");
			$('#details,#pymentTab').css("display","none");
			$('#clients').css("display","none");
			$('#invoices').css("display","none");
			$('#sandboxCredentials').css("display","none");
			$('#productionCredentials').css("display","none");
			$('#ewaybillCredentials').css("display","none");
			$('#eInvoiceCredentials').css("display","none");
			$('#payment').css("display","none");
			$('#commentsTab').css("display","none");
			$('#partnerBankTab').css("display","none");
			$('#centersTab').css("display","none");
			$('#refClientTab').css("display","none");
			$('#clientPaymentDetailsTab').css("display","none");
			$('#clientPendingPaymentDetailsTab2').css("display","none");
			$('#headerkeysTab').css("display","none");
			$('#asp_apiuserdetailsTab').css("display","none");
			$('#featuresTab').css("display","none");
		}else if(tabId == 'users'){
			$('#commentsTab').removeClass('d-block');
			$('#commentsTab').removeClass("active");
			$('#'+tabId).css("display","block");
			$('#details,#pymentTab').css("display","none");
			$('#payment').css("display","none");
			$('#clients').css("display","none");
			$('#invoices').css("display","none");
			$('#sandboxCredentials').css("display","none");
			$('#productionCredentials').css("display","none");
			$('#ewaybillCredentials').css("display","none");
			$('#eInvoiceCredentials').css("display","none");
			$('#ewaypayment').css("display","none");
			$('#users').css("display","block");
			$('#commentsTab').css("display","none");
			$('#partnerBankTab').css("display","none");
			$('#centersTab').css("display","none");
			$('#refClientTab').css("display","none");
			$('#clientPaymentDetailsTab').css("display","none");
			$('#clientPendingPaymentDetailsTab').css("display","none");
			$('#asp_apiuserdetailsTab').css("display","none");
			$('#headerkeysTab').css("display","none");
			$('#featuresTab').css("display","none");
		}else if(tabId == 'commentsTab'){
			$('#commentsTab').addClass('d-block');
			$('#commentsTab').addClass("active");
			$('#'+tabId).css("display","block");
			$('#details,#pymentTab').css("display","none");
			$('#payment').css("display","none");
			$('#clients').css("display","none");
			$('#invoices').css("display","none");
			$('#sandboxCredentials').css("display","none");
			$('#productionCredentials').css("display","none");
			$('#ewaybillCredentials').css("display","none");
			$('#eInvoiceCredentials').css("display","none");
			$('#ewaypayment').css("display","none");
			$('#users').css("display","none");
			$('#centersTab').css("display","none");
			$('#refClientTab').css("display","none");
			$('#clientPaymentDetailsTab').css("display","none");
			$('#clientPendingPaymentDetailsTab').css("display","none");
			$('#headerkeysTab').css("display","none");
			$('#partnerBankTab').css("display","none");
			$('#asp_apiuserdetailsTab').css("display","none");
			$('#featuresTab').css("display","none");
		}else if(tabId == 'centersTab'){
			$('#commentsTab').removeClass('d-block');
			$('#commentsTab').removeClass("active");
			$('#'+tabId).css("display","block");
			$('#details,#pymentTab').css("display","none");
			$('#payment').css("display","none");
			$('#clients').css("display","none");
			$('#invoices').css("display","none");
			$('#sandboxCredentials').css("display","none");
			$('#productionCredentials').css("display","none");
			$('#ewaybillCredentials').css("display","none");
			$('#eInvoiceCredentials').css("display","none");
			$('#ewaypayment').css("display","none");
			$('#users').css("display","none");
			$('#commentsTab').css("display","none");
			$('#partnerBankTab').css("display","none");
			//$('#centersTab').css("display","block");
			$('#refClientTab').css("display","none");
			$('#clientPaymentDetailsTab').css("display","none");
			$('#clientPendingPaymentDetailsTab').css("display","none");
			$('#headerkeysTab').css("display","none");
			$('#asp_apiuserdetailsTab').css("display","none");
			$('#featuresTab').css("display","none");
		}else if(tabId == 'refClientTab'){
			$('#commentsTab').removeClass('d-block');
			$('#commentsTab').removeClass("active");
			$('#'+tabId).css("display","block");
			$('#details,#pymentTab').css("display","none");
			$('#payment').css("display","none");
			$('#clients').css("display","none");
			$('#invoices').css("display","none");
			$('#sandboxCredentials').css("display","none");
			$('#productionCredentials').css("display","none");
			$('#ewaybillCredentials').css("display","none");
			$('#eInvoiceCredentials').css("display","none");
			$('#ewaypayment').css("display","none");
			$('#users').css("display","none");
			$('#commentsTab').css("display","none");
			$('#partnerBankTab').css("display","none");
			$('#centersTab').css("display","none");
			$('#clientPaymentDetailsTab').css("display","none");
			$('#clientPendingPaymentDetailsTab').css("display","none");
			$('#headerkeysTab').css("display","none");
			$('#asp_apiuserdetailsTab').css("display","none");
			$('#featuresTab').css("display","none");
		}else if(tabId == 'clientPaymentDetailsTab'){
			$('#commentsTab').removeClass('d-block');
			$('#commentsTab').removeClass("active");
			$('#'+tabId).css("display","block");
			$('#details,#pymentTab').css("display","none");
			$('#payment').css("display","none");
			$('#clients').css("display","none");
			$('#invoices').css("display","none");
			$('#sandboxCredentials').css("display","none");
			$('#productionCredentials').css("display","none");
			$('#ewaybillCredentials').css("display","none");
			$('#eInvoiceCredentials').css("display","none");
			$('#ewaypayment').css("display","none");
			$('#users').css("display","none");
			$('#commentsTab').css("display","none");
			$('#partnerBankTab').css("display","none");
			$('#centersTab').css("display","none");
			$('#refClientTab').css("display","none");
			$('#clientPendingPaymentDetailsTab').css("display","none");
			$('#headerkeysTab').css("display","none");
			$('#asp_apiuserdetailsTab').css("display","none");
			$('#featuresTab').css("display","none");
		}else if(tabId == 'clientPendingPaymentDetailsTab'){
			$('#commentsTab').removeClass('d-block');
			$('#commentsTab').removeClass("active");
			
			$('#'+tabId).css("display","block");
			$('#details,#pymentTab').css("display","none");
			$('#payment').css("display","none");
			$('#clients').css("display","none");
			$('#invoices').css("display","none");
			$('#sandboxCredentials').css("display","none");
			$('#productionCredentials').css("display","none");
			$('#ewaybillCredentials').css("display","none");
			$('#eInvoiceCredentials').css("display","none");
			$('#ewaypayment').css("display","none");
			$('#users').css("display","none");
			$('#commentsTab').css("display","none");
			$('#partnerBankTab').css("display","none");
			$('#centersTab').css("display","none");
			$('#refClientTab').css("display","none");
			$('#clientPaymentDetailsTab').css("display","none");
			$('#headerkeysTab').css("display","none");
			$('#asp_apiuserdetailsTab').css("display","none");
			$('#featuresTab').css("display","none");
		}else if(tabId == 'headerkeysTab'){
			$('#'+tabId).css("display","block");
			$('#commentsTab').css("display","none");
			$('#partnerBankTab').css("display","none");
			$('#commentsTab').removeClass('d-block');
			$('#commentsTab').removeClass("active");
			$('#details,#pymentTab').css("display","none");
			$('#payment').css("display","none");
			$('#clients').css("display","none");
			$('#invoices').css("display","none");
			$('#sandboxCredentials').css("display","none");
			$('#productionCredentials').css("display","none");
			$('#ewaybillCredentials').css("display","none");
			$('#eInvoiceCredentials').css("display","none");
			$('#ewaypayment').css("display","none");
			$('#users').css("display","none");
			$('#centersTab').css("display","none");
			$('#refClientTab').css("display","none");
			$('#clientPaymentDetailsTab').css("display","none");
			$('#clientPendingPaymentDetailsTab').css("display","none");
			$('#asp_apiuserdetailsTab').css("display","none");
			$('#featuresTab').css("display","none");
		}else if(tabId == 'asp_apiuserdetailsTab'){
			$('#'+tabId).css("display","block");
			$('#commentsTab,#pymentTab').css("display","none");
			$('#partnerBankTab').css("display","none");
			$('#commentsTab').removeClass('d-block');
			$('#commentsTab').removeClass("active");
			$('#details').css("display","none");
			$('#payment').css("display","none");
			$('#clients').css("display","none");
			$('#invoices').css("display","none");
			$('#sandboxCredentials').css("display","none");
			$('#productionCredentials').css("display","none");
			$('#ewaybillCredentials').css("display","none");
			$('#eInvoiceCredentials').css("display","none");
			$('#ewaypayment').css("display","none");
			$('#users').css("display","none");
			$('#centersTab').css("display","none");
			$('#refClientTab').css("display","none");
			$('#clientPaymentDetailsTab').css("display","none");
			$('#clientPendingPaymentDetailsTab').css("display","none");
			$('#headerkeysTab').css("display","none");
			$('#featuresTab').css("display","none");
		}else if(tabId == 'eInvoiceCredentials'){
			$('#'+tabId).css("display","block");
			
			$('#commentsTab').removeClass('d-block');
			$('#partnerBankTab,#pymentTab').css("display","none");
			$('#commentsTab').removeClass("active");
			$('#details').css("display","none");
			$('#clients').css("display","none");
			$('#invoices').css("display","none");
			$('#sandboxCredentials').css("display","none");
			$('#productionCredentials').css("display","none");
			$('#ewaybillCredentials').css("display","none");
			$('#payment').css("display","none");
			$('#ewaypayment').css("display","none");
			$('#save_btn').css('display','none');
			$('#edit_btn').css('display','none');
			$('#sand_save_btn').css('display','none');
			$('#sand_edit_btn').css('display','none');
			$('#prod_edit_btn').css('display','none');
			$('#prod_save_btn').css('display','none');
			$('#eway_edit_btn').css('display','none');
			$('#eway_save_btn').css('display','none');
			$('#ewaySand_save_btn').css('display','none');
			$('#ewaySand_edit_btn').css('display','none');
			$('#einv_save_btn').css('display','none');
			$('#einv_edit_btn').css('display','block');
			$('#einvSand_save_btn').css('display','none');
			$('#einvSand_edit_btn').css('display','block');
			$('#users').css("display","none");
			$('#commentsTab').css("display","none");
			$('#centersTab').css("display","none");
			$('#refClientTab').css("display","none");
			$('#clientPaymentDetailsTab').css("display","none");
			$('#clientPendingPaymentDetailsTab').css("display","none");
			$('#headerkeysTab').css("display","none");
			$('#featuresTab').css("display","none");
			$('#asp_apiuserdetailsTab').css("display","none");
		}else if(tabId == 'featuresTab'){
			$('#'+tabId).css("display","block");
			$('#save_features_btn').css("display","none");
			$('#commentsTab,#pymentTab,#headerkeysTab').css("display","none");
			$('#commentsTab').removeClass('d-block');
			$('#commentsTab').removeClass("active");
			$('#details').css("display","none");
			$('#payment').css("display","none");
			$('#clients').css("display","none");
			$('#invoices').css("display","none");
			$('#sandboxCredentials').css("display","none");
			$('#productionCredentials').css("display","none");
			$('#ewaybillCredentials').css("display","none");
			$('#eInvoiceCredentials').css("display","none");
			$('#ewaypayment').css("display","none");
			$('#users').css("display","none");
			$('#centersTab').css("display","none");
			$('#refClientTab').css("display","none");
			$('#clientPaymentDetailsTab').css("display","none");
			$('#clientPendingPaymentDetailsTab').css("display","none");
			$('#asp_apiuserdetailsTab').css("display","none");
		}else if(tabId == 'partnerBankTab'){
			$('#partnerBankTab').css("display","block");
			
			$('#commentsTab').removeClass('d-block');
			$('#commentsTab').removeClass("active");
			$('#details,#pymentTab').css("display","none");
			$('#clients').css("display","none");
			$('#invoices').css("display","none");
			$('#eInvoiceCredentials').css("display","none");
			$('#sandboxCredentials').css("display","none");
			$('#productionCredentials').css("display","none");
			$('#ewaybillCredentials').css("display","none");
			$('#payment').css("display","none");
			$('#ewaypayment').css("display","none");
			$('#save_btn').css('display','none');
			$('#edit_btn').css('display','none');
			$('#sand_save_btn').css('display','none');
			$('#sand_edit_btn').css('display','none');
			$('#prod_edit_btn').css('display','none');
			$('#prod_save_btn').css('display','none');
			$('#eway_edit_btn').css('display','none');
			$('#eway_save_btn').css('display','none');
			$('#ewaySand_save_btn').css('display','none');
			$('#ewaySand_edit_btn').css('display','none');
			$('#einv_save_btn').css('display','none');
			$('#einv_edit_btn').css('display','none');
			$('#einvSand_save_btn').css('display','none');
			$('#einvSand_edit_btn').css('display','none');
			$('#users').css("display","none");
			$('#commentsTab').css("display","none");
			$('#centersTab').css("display","none");
			$('#refClientTab').css("display","none");
			$('#clientPaymentDetailsTab').css("display","none");
			$('#clientPendingPaymentDetailsTab').css("display","none");
			$('#headerkeysTab').css("display","none");
			$('#asp_apiuserdetailsTab').css("display","none");
		}
	}
	
	$(document).on('change', '.cPaytDetret', function() {
		  if($('.cPaytDetret').val() == '2'){
			  $('.yearly-wrap').addClass('d-none');
			  $('.monthly-wrap').removeClass('d-none');
			  $("#getpaidval").val("monthly");
		  }else{
			  $("#getpaidval").val("yearly");
			  $('.yearly-wrap').removeClass('d-none');
			  $('.monthly-wrap').addClass('d-none');
		  }
		});
	$(document).on('change', '.cpendingPaytDetret', function() {
		if($('.cpendingPaytDetret').val() == '2'){
			$('.penyearly-wrap').addClass('d-none');
			$('.penmonthly-wrap').removeClass('d-none');
			$("#getpenpayval").val("monthly");
		}else{
			$("#getpenpayval").val("yearly");
			$('.penyearly-wrap').removeClass('d-none');
			$('.penmonthly-wrap').addClass('d-none');
		} 
		pMonthPicker();
	});
	
	$('#nav_client').addClass('active');	
	$.fn.dataTable.ext.errMode = 'throw';
	//var urlPath = contextPath+"/allusers";
	function usrupdateotpverify(){
		if(deleteSpamUsersArray.length ==1){
			$.ajax({
				url: contextPath+"/updateusrotpverify/"+deleteSpamUsersArray[0],
				contentType: 'application/json',
				success : function(userdata) {},
				error:function(errorResponse){}
			});
			getUsersData('spamusers', 'spamuserslnk');
			$('#otpverify').prop('disabled', true);
			deleteSpamUsersArray = new Array();
		}
	}
	function deleteSpamUsersAction(){
		$('#deleteModal').modal('show');
		$('#adminselectespamuserscount').text(' ('+deleteSpamUsersArray.length+') selected, ');
		$('#btnDelete').attr('onclick', "deleteOtpNotVerifiedUsers()");
		//getUsersData('spamusers', 'spamuserslnk');
		//$('#alldeletespamusrs').prop('checked',false);
	}
	function deleteOtpNotVerifiedUsers(){
		if(deleteSpamUsersArray.length > 0) {
			for(var i=0;i<deleteSpamUsersArray.length;i++) {
				var usrid=deleteSpamUsersArray[i];
				$.ajax({
					url: contextPath+"/deletespamusers/"+deleteSpamUsersArray[i],
					contentType: 'application/json',
					success : function(userdata) {},
					error:function(errorResponse){}
				});
			}
			getUsersData('spamusers', 'spamuserslnk');
			$('#alldeletespamusrs').prop('checked',false);
		}
	}

$(document).on('change', '#pendingyearly', function() {
		var penyrCd = $('#pendingyearly').val();
		loadClientPendingPaymentDetails(currentUser, 0, penyrCd);
		loadPartnerPendingPaymentDetails(currentUser, 0, penyrCd);	
	});
	$(document).on('change', '#pendingmonthly', function() {
		var penmthCd = $('#pendingmonthly').val().split("-");
		loadClientPendingPaymentDetails(currentUser, penmthCd[0], penmthCd[1]);
		loadPartnerPendingPaymentDetails(currentUser, penmthCd[0], penmthCd[1]);
		pMonthPicker();
	});
		
	$(document).on('change', '#paidmonthly', function() {
		var cpdmthcd = $('#paidmonthly').val().split("-");
		loadClientPaymentDetails(currentUser, cpdmthcd[0], cpdmthcd[1]);
		loadPartnerPaymentDetails(currentUser, cpdmthcd[0], cpdmthcd[1]);		
	});
	$(document).on('change', '#paidyearly', function() {
		var cpdyrcd = $('#paidyearly').val();
		loadClientPaymentDetails(currentUser, 0, cpdyrcd);
		loadPartnerPaymentDetails(currentUser, 0, cpdyrcd);		
	});
	
	function sendEmails(type){
		$('#send_EmailModal').show();
		$('#invokeEmailBtn').attr('onclick','invokeEmail("'+type+'")');
		$('#email_subject').val('');
		$('#email_meassage').val('')
	}
	function invokeEmail(type){
		var subject = $('#email_subject').val();
		var mailBody = $('#email_meassage').val();
		var mailObject = new Object();
		
		mailObject.subject = subject;
		mailObject.mailBody = mailBody;
		mailObject.sender = fullName;
		mailObject.senderid = loginid;
		
		var eUrl = _getContextPath()+"/compainemails?type="+type;
		$.ajax({
			url: eUrl,
			data: JSON.stringify(mailObject),
			type: "POST",
			contentType: 'application/json',
			success: function(data) {
				//$('.successEmailmsg').removeClass("d-none");
				//$('#successEmailmsg').text("Mail Send Succesfully");
			},
			error: function(dat) {}
		});
		$('#send_EmailModal').modal('hide');
	}
	function email_Preview(){
		var emailMsg = $('#email_meassage').val();
		$('#preview_email').html(emailMsg);
	}
	