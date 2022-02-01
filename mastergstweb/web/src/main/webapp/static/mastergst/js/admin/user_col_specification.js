function formatUpdatedDate(dat, type, row){
	var createdDt = new Date(dat.createdDate) ;
    var month = createdDt.getUTCMonth() + 1; 
	var day = createdDt.getUTCDate();
	var year = createdDt.getUTCFullYear();
	return day+'-'+month+'-'+year;
}

function formatUpdatesdDate(dat, type, row){
	var createdDt = new Date(dat.updatedDate) ;
    var month = createdDt.getUTCMonth() + 1; 
	var day = createdDt.getUTCDate();
	var year = createdDt.getUTCFullYear();
	return day+'-'+month+'-'+year;
}

function formatStatus(dat, type, row){
	if(dat.disable && dat.disable == 'true'){
		 return 'Disabled';
	 }
	 if(!dat.disable){
		 return 'Disabled';
	 }
	 return 'Enabled';
}

function formatQuotationStatus(dat, type, row){

	if(dat.quotationSent == 'true'){
		 return 'Yes';
	 }else{
		 return 'No';
	 }
	 //return 'Yes';
}
function formatNeed_followupdate(dat, type, row){
	
	return dat.needToFollowUpDate;
}
function formatNeed_followup(dat, type, row){

	if(dat.needFollowup == 'true'){
		 
		return '<i class="fa fa-flag" aria-hidden="true" style="color: red; margin-left: 42px"></i> &nbsp;';
	}else{
		 return ' ';
	 }
	 //return 'Yes';
}
function formatcheckbox(dat, type, row){

	return '<div id="'+dat.userId+'" class="checkbox"><label><input type="checkbox" class="spamuserchckbox" id="'+dat.userId+'" value="'+dat.userId+'" onclick="deleteSelectedUsers(\''+dat.userId+'\')"/><i class="helper"></i></label></div>';
}
function formatcheckboxselectall(dat, type, row){

	return '<div id="chckboxall" class="checkbox"><label><input type="checkbox" id="alldeletespamusrs" onclick="deleteSelectedallUsers()"/><i class="helper"></i></label></div>';
}


function formatSandboxStatus(dat, type, row){
	var isEnabled = 'Disabled';
	 $.each(dat.userkeys, function(i, dt){
		 if(dt.stage == 'Sandbox' && dt.isenabled == true){
			 isEnabled = 'Enabled';
		 }
	 });
	 return isEnabled;
}

function formatProductionStatus(dat, type, row){
	var isEnabled = 'Disabled';
	 $.each(dat.userkeys, function(i, dt){
		 if(dt.stage == 'Production' && dt.isenabled == true){
			 isEnabled = 'Enabled';
		 }
	 });
	 return isEnabled;
}

var columnSpecification = {
							"All":[
	         						 {title:"Name", data: 'fullname', name:'fullname'},
	        						 {title:"Email", data: 'email', name:'email' },
	        						 {title:"Phone", data: 'mobilenumber', name:'mobilenumber' },
	        						 {title:"Ref By", data: 'refid', name:'refid' },
	        						 {title:"Created Date", data: formatUpdatedDate, name:'createdDate'},
	        						 {title:"Status", data: formatStatus, name:'disable'},
	        						 {title:"Type", data: 'type' , name:'type'},
	        						 
	        						 {title:"Quotation Sent", data: formatQuotationStatus , name:'quotationSent', defaultContent:'' },
	        						 
	        						 {title:"Sub. Type", data: "subscriptionType", name: "subscriptionType", defaultContent:'' },
				                     {title:"Paid Amount", data: "paidAmount", name: "paidAmount", type: "numeric", defaultContent:''},
	        						 {title:"Need.FollowUp", data: formatNeed_followup , name:'needFollowup', defaultContent:'' },
	        						 {title:"FollowUp Date", data: 'needFollowupdate' , name:'needFollowupdate', defaultContent:'' }
	        						 ],
	        						 
    						 "aspdeveloper":[
	         						 {title:"Name", data: 'fullname', name:'fullname' },
	        						 {title:"Email", data: 'email', name:'email'},
	        						 {title:"Phone", data: 'mobilenumber', name:'mobilenumber'  },
	        						 
	        						 {title:"Created Date", data: formatUpdatedDate, name:'createdDate' },
	        						 {title:"Acc. Status",  data: formatStatus, name:'disable'},
	        						 {title:"sandboxApplied", data : 'sandboxApplied', defaultContent: "NO"},
	        						 {title:"Sales Satus", data : 'needToFollowUp', name : 'needToFollowUp'},
	        						 {title:"Agr. Status", data : 'agreementStatus', name : 'agreementStatus'},
	        						 {title:"Quotation Sent", data: formatQuotationStatus , name:'quotationSent', defaultContent:'' },
	        						 {title:"Need.FollowUp", data: formatNeed_followup , name:'needFollowup', defaultContent:'' },
	        						 {title:"FollowUp Date", data: 'needFollowupdate' , name:'needFollowupdate', defaultContent:'' }
	        						 /*{title:"End Date", data : 'end date', defaultContent: ""},
	        						 {title:"Comments", data : 'comments', defaultContent: ""},
	        						 {title:"Ref By", data: 'refid', name:'refid'  },
	        						 {title:"Status", data: formatStatus, name:'disable' },
	        						 {title:"Sub. Type", data: "subscriptionType", name: "subscriptionType", defaultContent: ""},
	        						 {title:"Paid Amount", data: "paidAmount", name: "paidAmount", type: "numeric", defaultContent:''},
	        						 {title:"S. Access", data : formatSandboxStatus},
	        						 {title:"P. Access", data : formatProductionStatus},
	        						 {title:"S. API Count", data: 'anyerp' , name:'anyerp'},
	        						 {title:"P. API Count", data: 'branches', name:'branches' }*/
	        						 ] ,
					        "cacmas":[
					               {title:"Name", data: 'fullname', name:'fullname'},
					               {title:"Email", data: 'email', name:'email'},
					               {title:"Phone", data: 'mobilenumber', name:'mobilenumber'},
					               
					               {title:"Created Date", data: formatUpdatedDate, name:'createdDate' },
					               {title:"Acc. Status",  data: formatStatus, name:'disable'},        						 
					               {title:"Sales Satus", data : 'needToFollowUp', name : 'needToFollowUp'},
					               {title:"Agr. Status", data : 'agreementStatus', name : 'agreementStatus'},
					               {title:"Quotation Sent", data: formatQuotationStatus , name:'quotationSent', defaultContent:'' },
					               {title:"# Of Clients", data: 'totalClients', type: "numeric", defaultContent: ""},
					               {title:"# of Used Invoices", data: 'totalInvoicesUsed', type: "numeric", defaultContent: ""},
					               {title:"Need.FollowUp", data: formatNeed_followup , name:'needFollowup', defaultContent:'' },
					               {title:"FollowUp Date", data: 'needFollowupdate' , name:'needFollowupdate', defaultContent:'' }
					               /*{title:"End Date", data : 'end date', defaultContent: ""},
					               {title:"Comments", data : 'comments', defaultContent: ""}
					               {title:"Status", data: formatStatus, name:'disable'},
					               {title:"Sub. Type", data: "subscriptionType", name: "subscriptionType", 'defaultContent':''},
				                   {title:"Paid Amount", data: "paidAmount", name: "paidAmount", type: "numeric", defaultContent:''},
					               {title:"Ref By", data: 'refid', name:'refid'},
					               {title:"Created Date", data: formatUpdatedDate, name:'createdDate'}*/
					               ],
			               "taxp":[
			                      {title:"Name", data: 'fullname', name:'fullname'},
			                      {title:"Email", data: 'email', name:'email'},
			                      {title:"Phone", data: 'mobilenumber', name:'mobilenumber'},
			                      {title:"Ref By", data: 'refid', name:'refid'},
			                      {title:"Created Date", data: formatUpdatedDate, name:'createdDate'},
			                      {title:"Status", data: formatStatus, name:'disable'},
			                      {title:"Quotation Sent", data: formatQuotationStatus , name:'quotationSent', defaultContent:'' },
			                      {title:"Sub. Type", data: "subscriptionType", 'defaultContent':''},
			                      {title:"Paid Amount", data: "paidAmount", type: "numeric", 'defaultContent':''},
	        					  {title:"Need.FollowUp", data: formatNeed_followup , name:'needFollowup', defaultContent:'' },
	        					  {title:"FollowUp Date", data: 'needFollowupdate' , name:'needFollowupdate', defaultContent:'' }
			                ],
	                      "business":[
									{title:"Name", data: 'fullname', name:'fullname'},
									{title:"Email", data: 'email', name:'email'},
									{title:"Phone", data: 'mobilenumber', name:'mobilenumber'},
									{title:"Ref By", data: 'refid', name:'refid'},
									{title:"Created Date", data: formatUpdatedDate, name:'createdDate'},
									{title:"Status", data: formatStatus, name:'disable'},
									{title:"Quotation Sent", data: formatQuotationStatus , name:'quotationSent', defaultContent:'' },
									{title:"Sub. Type", data: "subscriptionType", 'defaultContent':''},
									{title:"Paid Amount", data: "paidAmount", type: "numeric", 'defaultContent':''},
									{title:"Need.FollowUp", data: formatNeed_followup , name:'needFollowup', defaultContent:'' },
									{title:"FollowUp Date", data: 'needFollowupdate' , name:'needFollowupdate', defaultContent:'' }
	                             ],
                     "enterprise":[
                                 {title:"Name", data: 'fullname', name:'fullname'},
                                 {title:"Email", data: 'email', name:'email'},
                                 {title:"Phone", data: 'mobilenumber', name:'mobilenumber'},
                                 {title:"Ref By", data: 'refid', name:'refid'},
                                 {title:"Created Date", data: formatUpdatedDate, name:'createdDate'},
                                 {title:"Status", data: formatStatus, name:'disable'},
                                 {title:"Quotation Sent", data: formatQuotationStatus , name:'quotationSent', defaultContent:'' },
                                 {title:"Sub. Type", data: "subscriptionType", 'defaultContent':''},
			                     {title:"Paid Amount", data: "paidAmount", type: "numeric", 'defaultContent':''},
	        					 {title:"Need.FollowUp", data: formatNeed_followup , name:'needFollowup', defaultContent:'' },
	        					 {title:"FollowUp Date", data: 'needFollowupdate' , name:'needFollowupdate', defaultContent:'' }
                                 ],
                      "partner":[
                               {title:"Partner ID", data: 'fullname', name:'fullname'},
                               {title:"Name", data: 'fullname', name:'fullname'},
                               {title:"Email", data: 'email', name:'email'},
                               {title:"Phone", data: 'mobilenumber', name:'mobilenumber'},
                               {title:"# invited", data: "totalInvitedClients", 'defaultContent':''},
                               {title:"# Joined", data: "totalJoinedClients", 'defaultContent':''},
                               {title:"# Pending", data: "totalPendingClients", 'defaultContent':''},
                               {title:"# Subscribed", data:"totalSubscribedClients", 'defaultContent': ''},
                               {title:"# Amount Paid", data: "paidAmount", type: "numeric", 'defaultContent':''},
      						   {title:"Need.FollowUp", data: formatNeed_followup , name:'needFollowup', defaultContent:'' },
	        				   {title:"FollowUp Date", data: 'needFollowupdate' , name:'needFollowupdate', defaultContent:'' }
                               ],
                   "suvidha":[
                                 {title:"Name", data: 'fullname', name:'fullname'},
                                 {title:"Email", data: 'email', name:'email'},
                                 {title:"Phone", data: 'mobilenumber', name:'mobilenumber'},
                                 
                                 {title:"Created Date", data: formatUpdatedDate, name:'createdDate' },
        						 {title:"Acc. Status",  data: formatStatus, name:'disable'},        						 
        						 {title:"Sales Satus", data : 'needToFollowUp', name : 'needToFollowUp'},
        						 {title:"Agr. Status", data : 'agreementStatus', name : 'agreementStatus'},
        						 {title:"Quotation Sent", data: formatQuotationStatus , name:'quotationSent', defaultContent:'' },
        						 {title:"End Date", data : 'end date', defaultContent: ""},
        						 {title:"Comments", data : 'comments', defaultContent: ""},
        						 {title:"Need.FollowUp", data: formatNeed_followup , name:'needFollowup', defaultContent:'' },
 	        					 {title:"FollowUp Date", data: 'needFollowupdate' , name:'needFollowupdate', defaultContent:'' }
                                 
        						 /*{title:"# Of Clients", data: "totalClients", "defaultContent": ""},
                                 {title:"# of Invoices", data: "totalInvoices","defaultContent": ""},
                                 {title:"Status", data: formatStatus, name:'disable'},
                                 {title:"Sub. Type", data: "subscriptionType", 'defaultContent':''},
			                     {title:"Paid Amount", data: "paidAmount", type: "numeric", 'defaultContent':''},
                                 {title:"Ref By", data: 'refid', name:'refid'},
								 {title:"Parent", data: 'parentid', name:'parentid',visible: false,searchable: false},
                                 {title:"Created Date", data: formatUpdatedDate, name:'createdDate'}*/
                                 ],
					"subusers":[
			                      {title:"Name", data: 'fullname', name:'fullname'},
	        						 {title:"Email", data: 'email', name:'email' },
	        						 {title:"Phone", data: 'mobilenumber', name:'mobilenumber' },
	        						 {title:"Parent Name", data: 'parentName', name:'parentName' },
									 {title:"Parent EmailId", data: 'parentEmailId', name:'parentEmailId' },
									 {title:"Quotation Sent", data: formatQuotationStatus , name:'quotationSent', defaultContent:'' },
	        						 {title:"Created Date", data: formatUpdatedDate, name:'createdDate'},
	        						 {title:"Type", data: 'type' , name:'type'},
	        						 {title:"Need.FollowUp", data: formatNeed_followup , name:'needFollowup', defaultContent:'' },
			        				 {title:"FollowUp Date", data: 'needFollowupdate' , name:'needFollowupdate', defaultContent:'' },
			        				 
			                      ],
			       "subcenters":[
			    	   				{title:"Name", data: 'fullname', name:'fullname'},
		        					{title:"Email", data: 'email', name:'email' },
		        					{title:"Phone", data: 'mobilenumber', name:'mobilenumber' },
		        					{title:"Parent Name", data: 'parentName', name:'parentName' },
									{title:"Parent EmailId", data: 'parentEmailId', name:'parentEmailId' },
									{title:"Quotation Sent", data: formatQuotationStatus , name:'quotationSent', defaultContent:'' },
		        					{title:"Created Date", data: formatUpdatedDate, name:'createdDate'},
		        					{title:"Type", data: 'type' , name:'type'},
	        						 {title:"Need.FollowUp", data: formatNeed_followup , name:'needFollowup', defaultContent:'' },
		        					{title:"FollowUp Date", data: 'needFollowupdate' , name:'needFollowupdate', defaultContent:'' }
				                ],
				"testaccounts":[
			    	   				{title:"Name", data: 'fullname', name:'fullname'},
		        					{title:"Email", data: 'email', name:'email' },
		        					{title:"Phone", data: 'mobilenumber', name:'mobilenumber' },
		        					{title:"Parent Name", data: 'parentName', name:'parentName' },
		        					{title:"Quotation Sent", data: formatQuotationStatus , name:'quotationSent', defaultContent:'' },
									{title:"Parent EmailId", data: 'parentEmailId', name:'parentEmailId' },
		        					{title:"Created Date", data: formatUpdatedDate, name:'createdDate'},
		        					{title:"Type", data: 'type' , name:'type'},
	        						{title:"Need.FollowUp", data: formatNeed_followup , name:'needFollowup', defaultContent:'' },
		        					{title:"FollowUp Date", data: 'needFollowupdate' , name:'needFollowupdate', defaultContent:'' }
				                ],
				    "spamusers":[
				    				//formatcheckboxselectall
				    				//{title:"checkbox", data:formatcheckbox , name:'id'},
				    				{title:"", data:formatcheckbox , name:'id'},
				    				{title:"Name", data: 'fullname', name:'fullname',defaultContent:'',className: "usrinfo" },
		        					{title:"Email", data: 'email', name:'email',defaultContent:'',className: "usrinfo"},
		        					{title:"Phone", data: 'mobilenumber', name:'mobilenumber',defaultContent:'',className: "usrinfo"},
		        					{title:"Otp Verified", data: 'otpVerified', name:'otpVerified',defaultContent:'',className: "usrinfo"},
		        					{title:"Type", data: 'type' , name:'type',defaultContent:'',className: "usrinfo"},
		        					{title:"Created Date", data: formatUpdatedDate, name:'createdDate',defaultContent:'',className: "usrinfo"}
				                ],

					"active-aspdeveloper":[
									 {title:"Name", data: 'fullname', name:'fullname' },
									 {title:"Email", data: 'email', name:'email'},
									 {title:"Phone", data: 'mobilenumber', name:'mobilenumber'  },
									 {title:"Sandbox Applied", data : formatSandBox, defaultContent: "NO"},
									 {title:"Einvoice Sandbox Applied", data : formatEinvoiceSandBox, defaultContent: "Disable"},
									 {title:"Ewaybill Sandbox Applied", data : formatEwaybillSandBox, defaultContent: "Disable"},
									 {title:"Production Applied", data : formatProduction, defaultContent: "Disable"},
									 {title:"Einvoice Production Applied", data : formatEinvoiceProduction, defaultContent: "Disable"},
									 {title:"Ewaybill Production Applied", data : formatEwaybillProduction, defaultContent: "Disable"},
									 {title:"Applied Counts(S | P)", data : formatApplied, name : '0 | 0'}
							],
					"active-cacmas":[
					               {title:"Name", data: 'fullname', name:'fullname'},
					               {title:"Email", data: 'email', name:'email'},
					               {title:"Phone", data: 'mobilenumber', name:'mobilenumber'},
					               
					               {title:"Created Date", data: formatUpdatedDate, name:'createdDate' },
					               {title:"Acc. Status",  data: formatStatus, name:'disable'},        						 
					               {title:"Sales Satus", data : 'needToFollowUp', name : 'needToFollowUp'},
					               {title:"Agr. Status", data : 'agreementStatus', name : 'agreementStatus'},
					               {title:"Quotation Sent", data: formatQuotationStatus , name:'quotationSent', defaultContent:'' },
					               {title:"# Of Clients", data: 'totalClients', type: "numeric", defaultContent: ""},
					               {title:"# of Used Invoices", data: 'totalInvoicesUsed', type: "numeric", defaultContent: ""},
					               {title:"Need.FollowUp", data: formatNeed_followup , name:'needFollowup', defaultContent:'' },
					               {title:"FollowUp Date", data: 'needFollowupdate' , name:'needFollowupdate', defaultContent:'' }
					       ],
			               "active-taxp":[
			                      {title:"Name", data: 'fullname', name:'fullname'},
			                      {title:"Email", data: 'email', name:'email'},
			                      {title:"Phone", data: 'mobilenumber', name:'mobilenumber'},
			                      {title:"Ref By", data: 'refid', name:'refid'},
			                      {title:"Created Date", data: formatUpdatedDate, name:'createdDate'},
			                      {title:"Status", data: formatStatus, name:'disable'},
			                      {title:"Quotation Sent", data: formatQuotationStatus , name:'quotationSent', defaultContent:'' },
			                      {title:"Sub. Type", data: "subscriptionType", 'defaultContent':''},
			                      {title:"Paid Amount", data: "paidAmount", type: "numeric", 'defaultContent':''},
	        					  {title:"Need.FollowUp", data: formatNeed_followup , name:'needFollowup', defaultContent:'' },
	        					  {title:"FollowUp Date", data: 'needFollowupdate' , name:'needFollowupdate', defaultContent:'' }
			                ],
	                      "active-business":[
									{title:"Name", data: 'fullname', name:'fullname'},
									{title:"Email", data: 'email', name:'email'},
									{title:"Phone", data: 'mobilenumber', name:'mobilenumber'},
									{title:"Ref By", data: 'refid', name:'refid'},
									{title:"Created Date", data: formatUpdatedDate, name:'createdDate'},
									{title:"Status", data: formatStatus, name:'disable'},
									{title:"Quotation Sent", data: formatQuotationStatus , name:'quotationSent', defaultContent:'' },
									{title:"Sub. Type", data: "subscriptionType", 'defaultContent':''},
									{title:"Paid Amount", data: "paidAmount", type: "numeric", 'defaultContent':''},
									{title:"Need.FollowUp", data: formatNeed_followup , name:'needFollowup', defaultContent:'' },
									{title:"FollowUp Date", data: 'needFollowupdate' , name:'needFollowupdate', defaultContent:'' }
	                             ],
                    "active-enterprise":[
                                {title:"Name", data: 'fullname', name:'fullname'},
                                {title:"Email", data: 'email', name:'email'},
                                {title:"Phone", data: 'mobilenumber', name:'mobilenumber'},
                                {title:"Ref By", data: 'refid', name:'refid'},
                                {title:"Created Date", data: formatUpdatedDate, name:'createdDate'},
                                {title:"Status", data: formatStatus, name:'disable'},
                                {title:"Quotation Sent", data: formatQuotationStatus , name:'quotationSent', defaultContent:'' },
                                {title:"Sub. Type", data: "subscriptionType", 'defaultContent':''},
			                     {title:"Paid Amount", data: "paidAmount", type: "numeric", 'defaultContent':''},
	        					 {title:"Need.FollowUp", data: formatNeed_followup , name:'needFollowup', defaultContent:'' },
	        					 {title:"FollowUp Date", data: 'needFollowupdate' , name:'needFollowupdate', defaultContent:'' }
                                ],
                  "active-suvidha":[
                                {title:"Name", data: 'fullname', name:'fullname'},
                                {title:"Email", data: 'email', name:'email'},
                                {title:"Phone", data: 'mobilenumber', name:'mobilenumber'},
                                
                                {title:"Created Date", data: formatUpdatedDate, name:'createdDate' },
       						 {title:"Acc. Status",  data: formatStatus, name:'disable'},        						 
       						 {title:"Sales Satus", data : 'needToFollowUp', name : 'needToFollowUp'},
       						 {title:"Agr. Status", data : 'agreementStatus', name : 'agreementStatus'},
       						 {title:"Quotation Sent", data: formatQuotationStatus , name:'quotationSent', defaultContent:'' },
       						 {title:"End Date", data : 'end date', defaultContent: ""},
       						 {title:"Comments", data : 'comments', defaultContent: ""},
       						 {title:"Need.FollowUp", data: formatNeed_followup , name:'needFollowup', defaultContent:'' },
	        					 {title:"FollowUp Date", data: 'needFollowupdate' , name:'needFollowupdate', defaultContent:'' }
                  ]
			}
function formatSandBox(dat, type, row){
	var isenabled = 'Disabled';
	if(dat.userkeys && dat.userkeys.length > 0){
		$.each(dat.userkeys, function(index,obj) {
			if(obj.stage == 'Sandbox'){
				if(obj.isenabled == true || obj.isenabled == 'true'){
					isenabled = 'Enabled';
				}
			}
		});
	}
	return isenabled;
}
function formatEinvoiceSandBox(dat, type, row){
	var isenabled = 'Disabled';
	if(dat.userkeys && dat.userkeys.length > 0){
		$.each(dat.userkeys, function(index,obj) {
			if(obj.stage == 'EInvoiceSandBox'){
				if(obj.isenabled == true || obj.isenabled == 'true'){
					isenabled = 'Enabled';
				}
			}
		});
	}
	return isenabled;
}
function formatEwaybillSandBox(dat, type, row){
	var isenabled = 'Disabled';
	if(dat.userkeys && dat.userkeys.length > 0){
		$.each(dat.userkeys, function(index,obj) {
			if(obj.stage == 'EwaybillSandBox'){
				if(obj.isenabled == true || obj.isenabled == 'true'){
					isenabled = 'Enabled';
				}
			}
		});
	}
	return isenabled;
}
function formatProduction(dat, type, row){
	var isenabled = 'Disabled';
	if(dat.userkeys && dat.userkeys.length > 0){
		$.each(dat.userkeys, function(index,obj) {
			if(obj.stage == 'Production'){
				if(obj.isenabled == true || obj.isenabled == 'true'){
					isenabled = 'Enabled';
				}
			}
		});
	}
	return isenabled;
}
function formatEinvoiceProduction (dat, type, row){
	var isenabled = 'Disabled';
	if(dat.userkeys && dat.userkeys.length > 0){
		$.each(dat.userkeys, function(index,obj) {
			if(obj.stage == 'EinvoiceProduction'){
				if(obj.isenabled == true || obj.isenabled == 'true'){
					isenabled = 'Enabled';
				}
			}
		});
	}
	return isenabled;
}
function formatEwaybillProduction(dat, type, row){
	var isenabled = 'Disabled';
	if(dat.userkeys && dat.userkeys.length > 0){
		$.each(dat.userkeys, function(index,obj) {
			if(obj.stage == 'EwaybillProduction'){
				if(obj.isenabled == true || obj.isenabled == 'true'){
					isenabled = 'Enabled';
				}
			}
		});
	}
	return isenabled;
}
function formatApplied(dat, type, row){
	var sand = 0, prod = 0;
	if(dat.userkeys && dat.userkeys.length > 0){
		$.each(dat.userkeys, function(index,obj) {
			if(obj.stage == 'Production' || obj.stage == 'EInvoiceProduction' || obj.stage == 'EWaybillProduction'){
				if(obj.isenabled  == true || obj.isenabled == 'true'){
					prod += 1;
				}
			}else{
				sand += 1;
			}
		});
	}
	return sand + " | "+ prod;
}
