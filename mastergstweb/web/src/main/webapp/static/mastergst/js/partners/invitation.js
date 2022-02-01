var invTableUrl = new Object();
var totalelements = 0;
$(document).ready(function(){
	
var stateoptions = {
		url: function(phrase) {
			phrase = phrase.replace('(',"\\(");
			phrase = phrase.replace(')',"\\)");
			return _getContextPath()+"/stateconfig?query="+ phrase + "&format=json";
		},
		getValue: "name",
		list: {
			onLoadEvent: function() {
				if($("#eac-container-state ul").children().length == 0) {
					//$("#state").val('');
					$("#stateempty").show();
				} else {
					$("#stateempty").hide();
				}
			},
			maxNumberOfElements: 37
		}
	};
	$("#state").easyAutocomplete(stateoptions);
	
	var industryTypeoptions = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return _getContextPath()+"/getIndustryType?query="+ phrase + "&format=json";
			},
			getValue: "name",
			list: {
				onLoadEvent: function() {
					if($("#eac-container-industryType ul").children().length == 0) {
						$("#industryTypeempty").show();
					} else {
						$("#industryTypeempty").hide();
					}
				},
				maxNumberOfElements: 37
			}
		};
		$("#industryType").easyAutocomplete(industryTypeoptions);
});

function loadInvitationsTable(id,name){
	var iUrl =_getContextPath()+'/getpartnerinvitations/'+id+'/'+name;	
	invTableUrl = $('#invdbTable').DataTable({
		"dom": 'f<"toolbar">lrtip<"clear">',
		 "processing": true,
		 "serverSide": true,
		 "lengthMenu": [ [10, 25, 50, 100, -1], [10, 25, 50, 100, "All"] ],
	     "ajax": {
	         url: iUrl,
	         type: 'GET',
	         contentType: 'application/json',
	         dataType: "json",
	         'dataSrc': function(resp){
	        	 resp.recordsTotal = resp.totalElements;
	        	 resp.recordsFiltered = resp.totalElements;
	        	 return resp.content;
	         }
	     },
		"paging": true,
		'pageLength':10,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		'columns': getInvColumns(id),
		'columnDefs' : getInvColumnDefs()
	});
	//$("div.toolbar").html('<h4 style="margin-top: 6px;">Invitations</h4>');
	$('#invitationsBody').on('click','tr', function(e){
		if (!$(e.target).closest('.nottoedit').length) {
			var dat = invTableUrl.row($(this)).data();
			editInvitationsPopup(dat.userid);
		}
		
});
}

function getInvColumns(id){
	var clienttype = {data:  function ( data, type, row ) {
		var clientType = data.clienttype ? data.clienttype : "";
		return '<span class="text-left invoiceclk">'+clientType+'</span>';
		}};
	var clientname = {data:  function ( data, type, row ) {
		var name = data.name ? data.name : "";
		return '<span class="text-left invoiceclk">'+name+'</span>';
		}};
	var clientemail = {data:  function ( data, type, row ) {
		var email = data.email ? data.email : "";
		return '<span class="text-left invoiceclk">'+email+'</span>';
		}};
	var clientmobile = {data:  function ( data, type, row ) {
		var mobilenumber = data.mobilenumber ? data.mobilenumber : "";
		return '<span class="text-left invoiceclk">'+mobilenumber+'</span>';
		}};
	var createddate = {data:  function ( data, type, row ) {
		var createdDate  = data.createdDate ? data.createdDate : "";
		if(createdDate != ""){
			return '<span class="text-left invoiceclk">'+(new Date(createdDate)).toLocaleDateString('en-GB')+'</span>';
		}else{
			var updatedDate  = data.updatedDate ? data.updatedDate : "";
			if(updatedDate != ""){
				return '<span class="text-left invoiceclk">'+(new Date(updatedDate)).toLocaleDateString('en-GB')+'</span>';
			}else{
				return '<span class="text-left invoiceclk"></span>';
			}
		}
	}};
	var joindate = {data:  function ( data, type, row ) {
		var joinDate  = data.joinDate ? data.joinDate : "";
		if(joinDate != ""){
			return '<span class="text-left invoiceclk">'+(new Date(joinDate)).toLocaleDateString('en-GB')+'</span>';
		}else{
			return '<span class="text-left invoiceclk"></span>';
		}
	}};
	var estimatedcost = {data:  function ( data, type, row ) {
		
		var estimatedCost = 0.0;
					if(data.estimatedCost){
						estimatedCost = data.estimatedCost;
					}
		return '<span class="invoiceclk indformat" style="float:right">'+formatNumber(estimatedCost.toFixed(2))+'</span>';
		}};
	var status = {data:  function ( data, type, row ) {
		var Status = data.status ? data.status : "";
		return '<span class="text-left invoiceclk color-green">'+Status+'</span>';
		}};
	return [clienttype , clientname,clientemail, clientmobile, createddate, joindate,estimatedcost,status,
        {data: function ( data, type, row ) {
        	if(data.status == "New"){
        		return '<a href="#" class="btn btn-secondary" style="font-weight:bold" onclick="resendInvite(\''+userId+'\',\''+data.userid+'\',\''+data.name+'\',\''+data.content+'\',\''+data.email+'\',\''+data.mobilenumber+'\')">Send</a>';
        	}else{
        		return '<a href="#" class="btn btn-secondary" style="font-weight:bold" onclick="resendInvite(\''+userId+'\',\''+data.userid+'\',\''+data.name+'\',\''+data.content+'\',\''+data.email+'\',\''+data.mobilenumber+'\')">Resend</a>';
        	}
            }},
            {data: function ( data, type, row ) {
            	return '<a href="#" class="nottoedit" id="lead_cmts" onclick="leadsComments(\''+data.userid+'\')"><img style="width:26px;" class="cmntsimg" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a>';
            }}
        ];
}
function getInvColumnDefs(){
	return  [
		{
			"targets":  [9],
			className: 'dt-body-center'
		}
	];
}
function getInvitationsData(inviteId, popudateInvitationsData){
	var urlStr = _getContextPath()+'/getPartner/'+inviteId;
	$.ajax({
		url: urlStr,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			popudateInvitationsData(response);
		}
	});
}
function editInvitationsPopup(inviteId){
	$('#inviteBusinessModal').modal("show");
	$('#inviteBtnTxt').removeClass('disable');
	getInvitationsData(inviteId, function(partner) {
        if (partner.userid == inviteId) {
			$("#partner_id").remove();
			$('#customerName').val(partner.name);
			$('#customerEmail').val(partner.email);
			$('#mobileId').val(partner.mobilenumber);
			$('#addcurrencyCode').val(partner.clienttype);
			$('#estimatedCost').val(partner.estimatedCost);
			$('#description').val(partner.content);
			$('#state').val(partner.state ? partner.state : '');
			$('#city').val(partner.city ? partner.city : '');
			$('#salesStatus').val(partner.salesstatus ? partner.salesstatus : '');
			$('#industryType').val(partner.industryType ? partner.industryType : '');
			var needtoFollow = partner.needFollowup ? partner.needFollowup : '';
			if(needtoFollow == true){
				$('#followup').prop("checked",true);
				$('#followup').val("true");
				$('#followupdateDiv').css("display","block");
				$('#needFollowupdate').val(partner.needFollowupdate ? partner.needFollowupdate : '');
			}else{
				$('#followup').prop("checked",false);
				$('#followup').val("false");
				$('#followupdateDiv').css("display","none");
			}
			var demostatus = partner.demostatus ? partner.demostatus : '';
			if(demostatus == true){
				$('#demostatus').prop("checked",true);
				$('#demostatus').val("true");
			}else{
				$('#demostatus').prop("checked",false);
				$('#demostatus').val("false");
			}
			$('#productType').select2().val(partner.productType).trigger('change');
			if(partner.status == 'New'){
				$('#isLead').val("true");
			}else{
				$('#isLead').val("false");
			}
			$("form[name='userform']").append('<input type="hidden" id="partner_id" name="id" value="'+partner.userid+'">');
			//$("form[name='userform']").append('<input type="hidden" id="mthCd_id" name="mthCd" value="'+partner.mthCd+'">');
			//$("form[name='userform']").append('<input type="hidden" id="weekCd_id" name="weekCd" value="'+partner.weekCd+'">');
			//$("form[name='userform']").append('<input type="hidden" id="yrCd_id" name="yrCd" value="'+partner.yrCd+'">');
			//$("form[name='userform']").append('<input type="hidden" id="dayCd_id" name="dayCd" value="'+partner.dayCd+'">');
			if(partner.status == "New"){
				$('#leadorBusinessTxt').html("Edit LEAD");
				$('#inviteBtnTxt').html("SAVE");
			}else{
				$('#leadorBusinessTxt').html("Edit a Business");
				$('#inviteBtnTxt').html("SAVE");
			}
        }
	});
}

function formatNumber(nStr) {
	var negativenumber = false;
	if(nStr && nStr.includes("-")){
		negativenumber = true;
		nStr = nStr.replace("-","");
	}
	nStr=nStr.toString();var afterPoint = '';
	if(nStr.indexOf('.') > 0)
	   afterPoint = nStr.substring(nStr.indexOf('.'),nStr.length);
	nStr = Math.floor(nStr);
	nStr=nStr.toString();
	var lastThree = nStr.substring(nStr.length-3);
	var otherNumbers = nStr.substring(0,nStr.length-3);
	if(otherNumbers != '')
	    lastThree = ',' + lastThree;
	var res = otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",") + lastThree + afterPoint;
	if(negativenumber){
		res = "-"+res;
	}
	return res;
}
function leadsComments(id){
	$('#leadsCommentsModal').modal('show');
	$('.leads_commentsTab').html("");
	$('#nocomments_leads').text("");
	$('#addComment').attr("onclick","addLeadsComments(\""+id+"\")");
	$.ajax({
		url: contextPath+"/leadscomments/"+id,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response){
			if(response.length == 0){
				$('#nocomments_leads').text("No Comments Added Yet, Please add your Comments");
			}
			for(var i=0;i<response.length;i++){
				$('#nocomments_leads').text("");
				$('.leads_commentsTab').append('<div class="leadscommentsTab mb-2" style="margin-right: 10px;"><strong><label class="label_txt">Added By : '+response[i].addedby+'</label></strong><strong><label style="float:right;">Date : '+formatDate(response[i].commentDate)+'</label></strong><br/>'+response[i].leadscomments+'</div>');
			}
		},error:function(err){
		}
	});
}
function addLeadsComments(inviteid){
	var comments = $('#leads_comments').val();
	if(comments != ""){
		$.ajax({
			url: contextPath+"/saveLeadsComments/"+inviteid+"/"+userId,
			method:"POST",
			contentType: 'application/x-www-form-urlencoded',
			data: {
				'comments': comments
			},
			success : function(response){
				$('.leads_commentsTab').html("");
				for(var i=0;i<response.length;i++){
					$('.leads_commentsTab').append('<div class="leadscommentsTab mb-2" style="margin-right: 10px;"><strong><label class="label_txt">Added By : '+response[i].addedby+'</label></strong><strong><label style="float:right;">Date : '+formatDate(response[i].commentDate)+'</label></strong><br/>'+response[i].leadscomments+'</div>');
				}
					$('#leads_comments').val("");
					$('#nocomments_leads').text("");
			},error:function(err){
			}
		});
}
}
function formatDate(date) {
	if(date == null || typeof(date) === 'string' || date instanceof String) {
		return date;
	} else {
		var d = new Date(date),
			month = '' + (d.getMonth() + 1),
			day = '' + d.getDate(),
			year = d.getFullYear();

		if (month.length < 2) month = '0' + month;
		if (day.length < 2) day = '0' + day;

		return [day, month, year].join('-');
	}
}