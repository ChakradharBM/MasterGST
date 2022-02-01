<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Partner</title>
<%@include file="/WEB-INF/views/includes/common_script.jsp"%>
<link rel="stylesheet"
	href="${contextPath}/static/mastergst/css/dashboard-partners/dashboard-partners.css"
	media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js"
	type="text/javascript"></script>
<%@include file="/WEB-INF/views/partners/header.jsp"%>
<div class="db-ca-wrap mt-4">
	<div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
				<!-- table start -->
				<div class="customtable db-ca-view tabtable1">
					<table id="dbTable"
						class="display row-border dataTable meterialform" cellspacing="0"
						width="100%">
						<thead>
							<tr>
								<th class="text-center">Business Name</th>
								<th class="text-center">Email</th>
								<th class="text-center">Phone</th>
								<th class="text-center">User Type</th>
								<th class="text-center">Subscription Amount</th>
								<th class="text-center">Status</th>
								<th class="text-center">Comments</th>
							</tr>
						</thead>
						<tbody></tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="leadsCommentsModal" role="dialog" aria-labelledby="leadsCommentsModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document">
			<div class="modal-content" style="height:100vh;">
			<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>Add Comments/Narration</h3>
                    </div>
				</div>
				<div class="modal-body meterialform popupright">
					<div class="row pl-4 pr-2 pt-4">
					<span id="nocomments_leads" class="pl-2"></span>
					<div class="form-group col-md-12">
						<div class="leads_commentsTab" style="max-height:300px;min-height:300px;overflow-y:auto;">
						</div>
					</div>
						<div class="form-group col-md-12 mb-0">
						<label for="Comments">Add Comments/Narration :</label>
						<textarea class="form-control" id="leads_comments" style="width:97%;height:110px;border:1px solid lightgray;"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer" style="display:block;text-align:center;">
				<a type="button" class="btn  btn-blue-dark" id="addComment">Save</a>
				 <button type="button" class="btn  btn-blue-dark" data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>	
<!-- footer begin here -->
<%@include file="/WEB-INF/views/includes/footer.jsp"%>
<!-- footer end here -->
<script type="text/javascript">
	var clientsTable;
	$(document).ready(function() {
		$('#nav_client_notrequired').addClass('active');
		loadClientsTable("${id}", "${fullname}");
	});
	function loadClientsTable(id, name) {
		var iUrl = '${contextPath}/pcntdetials/' + id + '/' + name+"?type=Not Required";
		clientsTable = $('#dbTable').DataTable(
				{
					"dom" : 'f<"toolbar">lrtip<"clear">',
					"processing" : true,
					"serverSide" : true,
					"lengthMenu" : [ [ 10, 25, 50, 100, -1 ],[ 10, 25, 50, 100, "All" ] ],
					"ajax" : {
						url : iUrl,
						type : 'GET',
						contentType : 'application/json; charset=utf-8',
						dataType : "json",
						'dataSrc' : function(resp) {
							resp.recordsTotal = resp.clients.totalElements;
							resp.recordsFiltered = resp.clients.totalElements;
							return resp.clients.content;
						}
					},
					"paging" : true,
					'pageLength' : 10,
					"order": [[0,'desc']],
					"responsive" : true,
					"orderClasses" : false,
					"searching" : true,
					'columns' : getClientColumns(id),
					'columnDefs' : getInvColumnDefs()
				});
		$('#dbTable').on('click', 'tr', function(e) {
			if (!$(e.target).closest('.nottoedit').length) {
				var dat = clientsTable.row($(this)).data();
				editInvitationsPopup(dat.userid);
			}
		});
	}
	function getClientColumns(id) {
		var clienttype = {
			data : function(data, type, row) {
				var clientType = data.clienttype ? data.clienttype : "";
				return '<span class="text-left invoiceclk">' + clientType
						+ '</span>';
			}
		};
		var clientname = {
			data : function(data, type, row) {
				var name = data.name ? data.name : "";
				return '<span class="text-left invoiceclk">' + name + '</span>';
			}
		};
		var clientemail = {
			data : function(data, type, row) {
				var email = data.email ? data.email : "";
				return '<span class="text-left invoiceclk">' + email
						+ '</span>';
			}
		};
		var clientmobile = {
			data : function(data, type, row) {
				var mobilenumber = data.mobilenumber ? data.mobilenumber : "";
				return '<span class="text-left invoiceclk">' + mobilenumber
						+ '</span>';
			}
		};
		var estimatedcost = {
			data : function(data, type, row) {
				var estimatedCost = 0.0;
				if (data.subscriptionAmount) {
					estimatedCost = data.subscriptionAmount;
				}
				return '<span class="invoiceclk indformat" style="float:right">'
						+ formatNumber(estimatedCost.toFixed(2)) + '</span>';
			}
		};
		var status = {
			data : function(data, type, row) {
				var Status = data.status ? data.status : "";
				return '<span class="text-left invoiceclk color-green">'
						+ Status + '</span>';
			}
		};
		var comments = {
			data: function ( data, type, row ) {
		      	return '<a href="#" class="nottoedit" id="lead_cmts" onclick="leadsComments(\''+data.userid+'\')"><img style="width:26px;" class="cmntsimg" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a>';
		    }	
		};
		return [ clientname, clientemail, clientmobile, clienttype, estimatedcost, status, comments ];
	}
	function getInvColumnDefs() {
		return [ {
			"targets" : [ 6 ],
			className : 'dt-body-center'
		} ];
	}
	function getInvitationsData(inviteId, popudateInvitationsData) {
		var urlStr = _getContextPath() + '/getPartner/' + inviteId;
		$.ajax({
			url : urlStr,
			async : true,
			cache : false,
			dataType : "json",
			contentType : 'application/json',
			success : function(response) {
				popudateInvitationsData(response);
			}
		});
	}
	$('#businessproductType').select2();
	function editInvitationsPopup(inviteId) {
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
			if (needtoFollow == true) {
				$('#followup').prop("checked", true);
				$('#followup').val("true");
				$('#followupdateDiv').css("display", "block");
				$('#needFollowupdate').val(partner.needFollowupdate ? partner.needFollowupdate : '');
			} else {
				$('#followup').prop("checked", false);
				$('#followup').val("false");
				$('#followupdateDiv').css("display", "none");
			}
			var demostatus = partner.demostatus ? partner.demostatus : '';
			if (demostatus == true) {
				$('#demostatus').prop("checked", true);
				$('#demostatus').val("true");
			} else {
				$('#demostatus').prop("checked", false);
				$('#demostatus').val("false");
			}
			$('#productType').select2().val(partner.productType).trigger('change');
			if (partner.status == 'New') {
				$('#isLead').val("true");
			} else {
				$('#isLead').val("false");
			}
			$("form[name='userform']").append('<input type="hidden" id="partner_id" name="id" value="'+partner.userid+'">');
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
</script>
</body>
</html>