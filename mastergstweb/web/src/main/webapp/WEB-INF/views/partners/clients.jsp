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
								<!-- <th class="text-center">S.No</th>
				<th class="text-center">Business Name</th>
				<th class="text-center">Business Email</th>
				<th class="text-center">Subscription Type</th>
				<th class="text-center">Subscription Amount</th>
				<th class="text-center">Status</th> -->
								<th class="text-center">Business Name</th>
								<th class="text-center">Email</th>
								<th class="text-center">Phone</th>
								<th class="text-center">User Type</th>
								<th class="text-center">Subscription Amount</th>
								<th class="text-center">Status</th>
								<th class="text-center">Comments</th>
							</tr>
						</thead>
						<tbody>
							<%--  <c:forEach items="${clientsList}" var="client" varStatus="slno">
              <tr data-toggle="modal" data-target="#viewModal${client.id}">
				<td align="center">${slno.count}</td>            
				<td align="center">${client.name}</td>
				<td align="center">${client.email}</td>
				<td align="center">${client.subscriptionType}</td>
				<td align="center"><i class="fa fa-rupee"></i> ${client.subscriptionAmount}</td>
				<td align="center" class="color-green">${client.status}</td>
              </tr>
			</c:forEach> --%>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>
</div>

<!-- viewModal Start -->
<c:forEach items="${clientsList}" var="client">
	<div class="modal fade modal-right" id="viewModal${client.id}"
		role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-sm" role="document">
			<div class="modal-content">

				<div class="modal-body meterialform popupright">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true"> <img
							src="${contextPath}/static/mastergst/images/dashboard-ca/closeicon.png"
							alt="Close" /></span>
					</button>
					<div class="invoice-hdr bluehdr">
						<h3>
							My Clients<br />
							<span class="gstid">${fullname}</span>
						</h3>

					</div>
					<div class="row  p-5">
						<div class="form-group col-md-12 col-sm-12">
							<p class="lable-txt astrich">S.No</p>
							<span class="errormsg" id="serialNumber_Msg"></span> <input
								type="text" id="serialNumber" name="serialNumber"
								required="required" data-error="Enter the GSTN serial number"
								placeholder="GSTN1001" value="${client.id}" /> <label
								for="input" class="control-label"></label> <i class="bar"></i>
						</div>

						<div class="form-group col-md-12 col-sm-12">
							<p class="lable-txt astrich">Business Name</p>
							<span class="errormsg" id="businessName_Msg"></span> <input
								type="text" id="businessName" name="businessName"
								required="required" data-error="Enter the business name"
								placeholder="BVM IT Consultancy Services" value="${client.name}" />
							<label for="input" class="control-label"></label> <i class="bar"></i>
						</div>

						<div class="form-group col-md-12 col-sm-12">
							<p class="lable-txt astrich">Email</p>
							<span class="errormsg" id="businessEmail_Msg"></span> <input
								type="text" id="businessEmail" name="businessEmail"
								required="required" data-error=" Enter the valid email"
								placeholder="rajesh@gmail.com" value="${client.email}" /> <label
								for="input" class="control-label"></label> <i class="bar"></i>
						</div>

						<div class="form-group col-md-12 col-sm-12">
							<p class="lable-txt astrich">Subscription Type</p>
							<span class="errormsg" id="subscriptionType_Msg"></span> <input
								type="text" id="subscriptionType" name="subscriptionType"
								required="required" data-error="Enter the subscription type"
								placeholder="SubScription Type"
								value="${client.subscriptionType}" /> <label for="input"
								class="control-label"></label> <i class="bar"></i>
						</div>

						<div class="bdr-b col-12">&nbsp;</div>
						<div class="clearfix col-12 mt-4 text-center">
							<a type="button" class="btn btn-blue-dark" data-toggle="modal"
								data-dismiss="modal" aria-label="Close"><span
								aria-hidden="true">Close</span></a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</c:forEach>

<div class="modal fade" id="inviteBusinessPartnerModal" role="dialog" aria-labelledby="inviteBusinessPartnerModal" aria-hidden="true">
	<div class="modal-dialog modal-md modal-right" role="document">
		<div class="modal-content" style="height: 100vh;">
			<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true"> 
					<img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
				</button>
				<div class="bluehdr" style="width: 100%">
					<h3 id="leadorBusinessTxt">Invite a Business</h3>
				</div>
			</div>
			<div class="modal-body meterialform popupright bs-fancy-checks">
				<div class="row pl-4 pr-4 pt-4 pb-0">
					<form:form method="POST" data-toggle="validator" id="businessinvite" class="meterialform row pr-4 pl-4" name="userform"  action="${contextPath}/paddclient/${id}/${fullname}" modelAttribute="client">
						<div class="form-group col-md-6 col-sm-12">
							<p class="lable-txt astrich">Customer Name</p>
							<span class="errormsg" id="customerName_Msg"></span>
							 <input type="text" id="businesscustomerName" name="name" required="required" placeholder="Rajesh" value="" />
							 <label for="input" class="control-label"></label> <i class="bar"></i>
						</div>

						<div class="form-group col-md-6 col-sm-12">
							<p class="lable-txt astrich">Customer Email</p>
							<span class="errormsg" id="customerEmail_Msg"></span> 
							<input type="text" id="businesscustomerEmail" name="email" required="required" placeholder="rajesh@gmail.com" value="" />
							<label for="input" class="control-label"></label> <i class="bar"></i>
						</div>

						<div class="form-group col-md-6 col-sm-12">
							<p class="lable-txt astrich">Phone</p>
							<span class="errormsg" id="phone_Msg"></span>
							<p class="indiamobilecode">+91</p>
							<input type="text" id="businessmobileId" name="mobilenumber" data-minlength="10" maxlength="10" pattern="[0-9]+" data-error="Please enter valid mobile number" required="required" placeholder="9848012345" value="" /> 
							<label for="input" class="control-label"></label> <i class="bar"></i>
						</div>
						<div class="form-group col-md-6 col-sm-12">
							<p class="lable-txt">User Type</p>
							<select class="form-control" name="clienttype" id="addcurrencyCode">
								<option value="">--Select--</option>
								<option value="cacmas">CA/CMA/CS/TAX PROFESSIONAL</option>
								<option value="suvidha">SUVIDHA CENTERS</option>
								<option value="business">SMALL/MEDIUM BUSINESS</option>
								<option value="enterprise">ENTERPRISE</option>
								<option value="partner">PARTNER</option>
								<option value="aspdeveloper">ASP/DEVELOPER</option>
							</select> <label for="input" class="control-label"></label> <i class="bar"></i>
						</div>
						<div class="form-group col-md-6 col-sm-12">
							<p class="lable-txt">Subscribed Cost</p>
							<input type="text" id="businessestimatedCost" name="estimatedCost" placeholder="123456" value="" /> 
							<label for="input" class="control-label"></label> <i class="bar"></i>
						</div>
						<div class="form-group col-md-6 col-sm-12">
							<p class="lable-txt">State</p>
							<input type="text" id="businessstate" name="state" value="" /> 
							<label for="state" class="control-label"></label>
							<div class="help-block with-errors"></div>
							<div id="stateempty" style="display: none">
								<div class="ddbox">
									<p>Search didn't return any results.</p>
								</div>
							</div>
							<i class="bar"></i>
						</div>
						<div class="form-group col-md-6 col-sm-12">
							<p class="lable-txt">City</p>
							<input type="text" id="businesscity" name="city" value="" />
							<label for="input" class="control-label"></label> <i class="bar"></i>
						</div>
						<div class="form-group col-md-6 col-sm-12">
							<p class="lable-txt">Industry/Vertical</p>
							<input type="text" id="businessindustryType" name="industryType" value=""/>
							<label for="input" class="control-label"></label>
							<div class="help-block with-errors"></div>
							<div id="industryTypeempty" style="display: none">
								<div class="ddbox">
									<p>Search didn't return any results.</p>
								</div>
							</div>
							<i class="bar"></i>
						</div>
						<div class="form-group col-md-6 col-sm-12">
							<p class="lable-txt">Product Type</p>
							<select id="businessproductType" class="mt-1" name="productType"multiple value="">
								<c:forEach items="${productTypes}" var="type">
									  <option value="${type}">${type}</option>
								</c:forEach> 
							</select> <label for="input" class="control-label"></label><i class="bar"></i>
						</div>
						<div class="form-group col-md-6 col-sm-12">
							<p class="lable-txt">Sales Status</p>
							<select class="form-control" id="salesStatus" name="salesstatus">
								<option value="" selected>-select-</option>
								<option value="Call Not Lift">Call Not Lift</option>
								<option value="Duplicate">Duplicate</option>
								<option value="Not Required">Not Required</option>
								<option value="Ready to Go">Ready to Go</option>
								<option value="Ready to Pay">Ready to Pay</option>
								<option value="Yet to Take Decision">Yet to Take Decision</option>
								<option value="Test Account">Test Account</option>
								<option value="Pricing Issue">Pricing Issue</option>
								<option value="Sandbox Testing">Sandbox Testing</option>
								<option value="Closed">Closed</option>
							</select>
							<label for="input" class="control-label"></label><i class="bar"></i>
						</div>
						<div class="form-group col-md-6 col-sm-12">
							<div class="form-group col-md-12 col-sm-12 ">
								<p class="lable-txt col-md-7 pl-0 pr-0" style="display: inline-block;">Demo Status</p>
								<div class="form-check form-check-inline ml-3">
									<input class="form-check-input" type="checkbox" id="businessdemostatus" name="demostatus" value="" /> 
									<label for="demostatus"><span class="ui"></span></label>
								</div>
							</div>
							<div class="form-group col-md-12 col-sm-12">
								<p class="lable-txt col-md-7 pl-0 pr-0"
									style="display: inline-block;">Need To FollowUp</p>
								<div class="form-check form-check-inline ml-3">
									<input class="form-check-input" type="checkbox" id="businessfollowup" name="needFollowup" value="" />
									<label for="followup"><span class="ui"></span> </label>
									<!-- <span class="labletxt" style="margin-top:0px!important">Active</span> -->
								</div>
							</div>
						</div>
						<div class="form-group col-md-6 col-sm-12" id="followupdateDiv" style="display: none;">
							<p class="lable-txt">FollowUp Date</p>
							<input type="text" id="businessneedFollowupdate"
								name="needFollowupdate" placeholder="dd/MM/YYYY" value="" /> <label
								for="input" class="control-label"></label> <i class="bar"></i>
						</div>
						<div class="form-group col-md-12 col-sm-12 mb-2">
							<p class="lable-txt astrich">Description</p>
							<span class="errormsg" id="paidAmount_Msg"></span>
							<textarea id="businessdescription" name="content"
								required="required"
								placeholder="I have noticed MasterGST is so simple and easy to use and file GST returns, I highly recommend this GST Software. Please click on the link to signup."></textarea>
							<label for="input" class="control-label"></label> <i class="bar"></i>
						</div>
				</div>
				<input type="hidden" name="userid" value="<c:out value="${id}"/>">
				<input type="hidden" name="fullname" value="<c:out value="${fullname}"/>">
				<input type="hidden" name="isLead" id="isLead" value="" />
			</form:form>
			</div>
			<div class="modal-footer text-center mt-0 pt-0" style="display: block">
				<label for="customer_submit" class="btn btn-blue-dark m-0" id="inviteBtnTxt" tabindex="0">Update</label>
				<!--<input type="submit" class="btn btn-blue-dark" value="Invite"/>-->
				<a type="button" class="btn btn-blue-dark" data-toggle="modal" data-dismiss="modal" aria-label="Close">
				<span aria-hidden="true">Close</span></a>
			</div>
		</div>
	</div>
</div>
<!-- viewModal End -->
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
	var clientTable;
	$(document).ready(function() {
		$('#nav_client').addClass('active');
		loadClientsTable("${id}", "${fullname}");
	});
	function loadClientsTable(id, name) {
		var iUrl = '${contextPath}/pcntdetials/' + id + '/' + name+"?type=";
		clientTable = $('#dbTable').DataTable(
				{
					"dom" : 'f<"toolbar">lrtip<"clear">',
					"processing" : true,
					"serverSide" : true,
					"lengthMenu" : [ [ 10, 25, 50, 100, -1 ],
							[ 10, 25, 50, 100, "All" ] ],
					"ajax" : {
						url : iUrl,
						type : 'GET',
						contentType : 'application/json',
						dataType : "json",
						'dataSrc' : function(resp) {
							resp.recordsTotal = resp.clients.totalElements;
							resp.recordsFiltered = resp.clients.totalElements;
							return resp.clients.content;
						}
					},
					"paging" : true,
					'pageLength' : 10,
					"responsive" : true,
					"order": [[0,'desc']],
					"searching" : true,
					'columns' : getClientColumns(id),
					'columnDefs' : getInvColumnDefs()
				});
		//$("div.toolbar").html('<h4 style="margin-top: 6px;">Invitations</h4>');
		$('#dbTable').on('click', 'tr', function(e) {
			if (!$(e.target).closest('.nottoedit').length) {
				var dat = clientTable.row($(this)).data();
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