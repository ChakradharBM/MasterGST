<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">

<head>
<title>MasterGST | Accounting</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>

<script src="${contextPath}/static/mastergst/js/accounts/accountingentries.js" type="text/javascript"></script>




</head>
<style>
#config_invoice_check .meterialform .checkbox input:checked~.helper::before{height: 10px;margin-left: 0px; width: 2px;    margin-top: 1px;}
.meterialform .form-check{ margin:0!important}
#config_invoice_check .meterialform .checkbox input:checked~.helper::after{height: 6px;margin-top: 3px;margin-left: 1px;}
.row.congic_number {margin-bottom: 12px;}
.gstr-info-tabs .nav-tabs .nav-link.active::before, .gstr-info-tabs .nav-tabs .nav-link:hover::before { border-top: 10px solid #ffffff;}
li.paramitem{font-size: 14px;font-weight: bold;margin-top: 10px;list-style: none;}
.form-check.form-check-inline.pull-right{margin-top: -18px!important;right:10%;}
#prefix{text-transform: uppercase;}
input#invoicetext{border: 1px solid black;width: 60%;float: right;margin-right: -73px;margin-top: -20px;}
.modal-body{ height: 100%;border-radius: 0;position:relative;}
.modal-footer{border-radius: 0;bottom:0px;position:absolute;width:100%;}
.css-serial {counter-reset: serial-number;  /* Set the serial number counter to 0 */}
.css-serial td:first-child:before {counter-increment: serial-number;  /* Increment the serial number counter */ content: counter(serial-number);  /* Display the counter */}
div.dataTables_length{margin-top: 11px;}
a.btn-edt{margin-right: 4px;vertical-align: middle;}div.dataTables_filter input{height:30px!important;}
.dr_cr_drop{width: 49px!important;border: 1px solid lightgrey!important;margin-top: 6px;}
</style>
<script type="text/javascript">
	var accountingdetails=new Array();
	var table;
	var submitStatus = '<c:out value="${client.gstsubmiton}"/>';
	var rettypeSales = '<c:out value="${rettypeSales}"/>';
	var rettypePurchase = '<c:out value="${rettypePurchase}"/>';
	var mnths = '<c:out value="${month}"/>';
	var clientid = '<c:out value="${client.id}"/>';
	var invoices = new Array();
	var table;currentId='';
	$(document).ready(function() {
	var type = '<c:out value="${type}"/>';
	if(type == 'group'){
		$('#groupstab').click();
	}else if(type == 'sgroup'){
		$('#sgroupstab').click();
	}else if(type == 'ledger'){
		$('#ledgertab').click();
	}
		$('#cpaccountNav').addClass('active');
		$('#nav-client').addClass('active');
		
		var options = {
				url: function(phrase) {
					phrase = phrase.replace('(',"\\(");
					phrase = phrase.replace(')',"\\)");
					return "${contextPath}/groupslist/"+clientid+"?query="+ phrase + "&format=json";
				},
				getValue: "groupname",
				
				list: {
					match: {
						enabled: true
					},
				onChooseEvent: function() {
					var groupdetails = $("#gosname").getSelectedItemData();
					$('#group_name').val(groupdetails.name);
					$('#subgroup_name').val(groupdetails.userid);
					$('#path_name').val(groupdetails.path);
					$('#path').text(groupdetails.path+"/"+$('#subgname').val());
					$('.headpath').css('display','block');
				}, 
					onLoadEvent: function() {
						if($("#eac-container-gosname ul").children().length == 0) {
							$("#HeadNameempty").show();
						} else {
							$("#HeadNameempty").hide();
						}
					},
					maxNumberOfElements: 10
				},
			};
		$('#gosname').easyAutocomplete(options);
		var options = {
				url: function(phrase) {
					phrase = phrase.replace('(',"\\(");
					phrase = phrase.replace(')',"\\)");
					return "${contextPath}/groupslist/"+clientid+"?query="+ phrase + "&format=json";
				},
				getValue: "groupname",
				
				list: {
					match: {
						enabled: true
					},
					onChooseEvent: function() {
						var groupdetails = $("#goslname").getSelectedItemData();$('#ledger_name').val(groupdetails.name);$('#sledger_name').val(groupdetails.userid);$('#lpath_name').val(groupdetails.path);
						$('#lpath').text(groupdetails.path+"/"+$('#ledgername').val());$('.headlpath').css('display','block');
					}, 
					onLoadEvent: function() {
						if($("#eac-container-goslname ul").children().length == 0) {
							$('#GroupNameempty').show();
						} else {
							$('#GroupNameempty').hide();
						}
					},
					maxNumberOfElements: 10
				},
			};
		$('#goslname').easyAutocomplete(options);
		grouptable = $('#dbTable1').DataTable({
			 "dom": '<"toolbar">Blfrtip',
			"paging": true,
			"searching": true,
			"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
			"responsive": true,
			"ordering": true,
			"pageLength": 10,
			"language": {
				"search": "_INPUT_",
				"searchPlaceholder": "Search...",
				"paginate": {
					"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
					"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
				}
			}
		});
		
		sgrouptable = $('#dbTable2').DataTable({
			"dom": '<"toolbar">Blfrtip',
			"paging": true,
			"searching": true,
			"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
			"responsive": true,
			"ordering": true,
			"pageLength": 10,
			"language": {
				"search": "_INPUT_",
				"searchPlaceholder": "Search...",
				"paginate": {
					"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
					"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
				}
			}
		});
		ledgertable = $('#dbTable3').DataTable({
			"dom": '<"toolbar">Blfrtip',
			"paging": true,
			"searching": true,
			"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
			"responsive": true,
			"ordering": true,
			"pageLength": 10,
			"language": {
				"search": "_INPUT_",
				"searchPlaceholder": "Search...",
				"paginate": {
					"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
					"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
				}
			}
		});
		//table.on('draw', rolesPermissions);
		$("#dbTable1_wrapper .toolbar").html('<h4>Groups</h4><a href="#" class="btn btn-blue-dark" data-toggle="modal" data-target="#geditModal" onclick="clearData(true)">Add</a>');
		$("#dbTable2_wrapper .toolbar").html('<h4>Sub Groups</h4><a href="#" class="btn btn-blue-dark" data-toggle="modal" data-target="#subgeditModal" onclick="clearData(false)">Add</a>');
		$("#dbTable3_wrapper .toolbar").html('<h4>Ledger</h4><a href="#" class="btn btn-blue-dark" data-toggle="modal" data-target="#ledgereditModal" onclick="clearData(false)">Add</a>');
		var headertext = [],
			headers = document.querySelectorAll("table.display th"),
			tablerows = document.querySelectorAll("table.display th"),
			tablebody = document.querySelector("table.display tbody");
		for (var i = 0; i < headers.length; i++) {
			var current = headers[i];
			headertext.push(current.textContent.replace(/\r?\n|\r/, ""));
		}
		for (var i = 0, row; row = tablebody.rows[i]; i++) {
			for (var j = 0, col; col = row.cells[j]; j++) {
				col.setAttribute("data-th", headertext[j]);
			}
		}
	
	});
	function clearData(isGroup) {
		currentId = '';
		if(isGroup){
			$('#gname').val('');
			$('#headname').val('');
			//$('.errormsg .bmsg').text('');
			$('#groupName_Msg').text('');
			$('.with-errors').html('');
			$('.form-group').removeClass('has-error has-danger');
			$('#oldgroupname').val('');
		}else{
			$('h6.headpath').css("display","none");
			$('h6.headlpath').css("display","none");
			$('#path').text('');
			$('.with-errors').html('');
			$('#subgname').val('');
			$('#gosname').val('');
			$('#ledgername').val('');
			$('#goslname').val('');
			$('#path_name').val('');
			$('#subgroup_name').val('');
			$('#group_name').val('');
			$('#lpath').html('');
			$('#oldsubgroupname').val('');
			$('#oldParentId').val('');
			$('#oldledgername').val('');
			$('#lpath_name').val('');
			var sdfsd = '${client.journalEnteringDate}';
			if(sdfsd != "" && sdfsd != "undefined"){
				var date = new Date(sdfsd.replace('IST', ''));
				var day = date.getDate() + "";var month = (date.getMonth() + 1) + "";	var year = date.getFullYear() + "";
				day = checkZero(day);month = checkZero(month);year = checkZero(year);
				var cdndate = day + "/" + month + "/" + year;
				$('#ledgerOpeningBalanceMonth').html(cdndate);
			}
		}
	}
	function populateLedgerElement(id,name,sname,spath,ledgerOpeningBalance,ledgerOpeningBalanceType,type) {
		$('.with-errors').html('');
		$('.form-group').removeClass('has-error has-danger');
		$('#ledgername').val(name);
		$('#goslname').val(sname);
		$('#lpath_name').val(spath);
		$('#oldledgername').val(name);
		$('#lpath').html(spath);
		if(ledgerOpeningBalanceType == '' || ledgerOpeningBalanceType == null || ledgerOpeningBalanceType == undefined){
			$('#ledgerOpeningBalanceType').val('Dr');			
		}else{
			$('#ledgerOpeningBalanceType').val(ledgerOpeningBalanceType);
		}
		$('#ledgerOpeningBalance').val(ledgerOpeningBalance);
		var ledgerOpeningBalanceMonth='<c:out value="${ledgerOpeningBalanceMonth}"/>';
		$('#ledgerOpeningBalanceMonth').html(ledgerOpeningBalanceMonth);
	}
	function populateElement(parentid,id,groupname,headname,path,type) {
		currentId = id;
		$('.with-errors').html('');
		$('#headname').val('');
		$('.form-group').removeClass('has-error has-danger');
		$('.gname').val('');
		$('#subgname').val('');
		$('#gosname').val('');
		$('#path_name').val('');
		$('#subgroup_name').val('');
		$('#group_name').val('');
		$('#oldgroupname').val('');
		$('h6.headpath').css("display","none");
		$('#path').text('');
		if(type == 'Group'){
			$('#gname').val(groupname);
			$('#headname').val(headname);
			$('#oldgroupname').val(groupname);
			$('h6.headspath').css("display","block");
			//$('#path').text(path);
			$('#headspath').text(path);
		
			$('.headspath').css('display','block');
		}else{
			$('h6.headpath').css("display","block");
			$('#subgname').val(groupname);
			$('#gosname').val(headname);
			$('#oldsubgroupname').val(groupname);
			$('#oldParentId').val(parentid);
			$('#path').text(path);
		}
	
		$.ajax({
			url: "${contextPath}/groupslist/${client.id}",
			contentType: 'application/json',
			success : function(response) {
				for(var i = 0; i < response.length; i++) {
					$('#gosname').append("<option value="+response[i].name+">"+response[i].name+"</option>");    
				}
			}
		});
	}
function showDeletePopup(id) {
	$('#deleteModal').modal('show');
	$('#btnDelete').attr('onclick', "deletegroup('"+id+"')");
}
function showLedgerDeletePopup(ledgerid) {
	$('#ledgerModal').modal('show');
	$('#LedgerbtnDelete').attr('onclick', "deleteledger('"+ledgerid+"')");
}

function deletegroup(id) {
	$.ajax({
		url: "${contextPath}/delgroup/"+id,
		success : function(response) {
			grouptable.row( $('#row'+id) ).remove().draw();
		}
	});
}
function deleteledger(ledgerid){
	$.ajax({
		url: "${contextPath}/delledger/"+ledgerid,
		success : function(response) {
			ledgertable.row( $('#row'+ledgerid) ).remove().draw();
		}
	});
}
function showSubGroupDeletePopup(subGroupId, name, groupId, clientid){
	$('#deleteModal').modal('show');
	$('#delPopupDetails').html(name);
	$('#btnDelete').attr('onclick', "deleteSubGroup('"+subGroupId+"','"+groupId+"', '"+clientid+"')");
}
function deleteSubGroup(subGroupId,groupId,clientid) {
	$.ajax({
		url: "${contextPath}/delSubGroup/"+subGroupId+"/"+groupId+"/"+clientid,
		success : function(response) {
			sgrouptable.row( $('#row'+subGroupId) ).remove().draw();
		}
	});
}
</script>
<body class="body-cls">
  <!-- header page begin -->
  <%@include file="/WEB-INF/views/includes/client_header.jsp" %>
		<!--- breadcrumb start -->
 				
<div class="breadcrumbwrap">
	<div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 25}'>${fn:substring(client.businessname, 0, 25)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item active">Company Profile</li>
					</ol>
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>

        <!--- breadcrumb end -->
        <div class="db-ca-wrap">
            <div class="container">
                <div class="row">
                    <!-- left side begin -->
                    <%@include file="/WEB-INF/views/profile/leftnav.jsp" %>
                    <!-- left side end -->

                    <!-- right sidebar begin -->
	<div class="col-md-10 col-sm-12 mb-4">
		<div class="whitebgbox meterialform" style="height:89%!important;min-height:89%!important">
			<div class="row p-3 pb-0 gstr-info-tabs" style="padding-top:0px!important;">

				<ul class="nav nav-tabs" role="tablist" id="tabsactive">
					<li class="nav-item"><a class="nav-link active" id="groupstab" data-toggle="tab" href="#gtab1" id="invoiceconfig" role="tab"><span class="serial-num">1</span>Groups</a></li>
					<li class="nav-item"><a class="nav-link" id="sgroupstab" data-toggle="tab" href="#gtab2" role="tab"><span class="serial-num">2</span> Sub Groups </a></li>
					<li class="nav-item"><a class="nav-link" id="ledgertab" data-toggle="tab" href="#gtab3" role="tab"><span class="serial-num">3</span> Ledger </a></li>
				</ul>
				<div class="tab-content" style="width:920px!important">
					<div class="tab-pane active" id="gtab1" role="tabpane1">
						<div class="col-sm-12 customtable p-0">
                        <table id="dbTable1" class="display row-border dataTable meterialform css-serial" cellspacing="0" width="100%">
                            <thead>
                                <tr>
                                    <th class="text-center">Group Id</th>
									<th class="text-center">Group Name</th>
									<th class="text-center">path</th>
									<th class="text-center">Actions</th>
                                </tr>
                            </thead>
                            <tbody id="grouptable">
								<c:forEach items="${accountingdetails}" var="groupdetails">
								<script type="text/javascript">
									var groupdetails = new Object();
									groupdetails.id = '<c:out value="${groupdetails.id}"/>';
									groupdetails.groupname = '<c:out value="${groupdetails.groupname}"/>';
									groupdetails.headname = '<c:out value="${groupdetails.headname}"/>';
									
									accountingdetails.push(groupdetails);
								</script>
                                <tr id="row${groupdetails.id}">
                                   
                                    <td align="left"></td>
                                    <td align="left">${groupdetails.groupname}</td>
                                    <td align="left" id="grouppath${groupdetails.id}">${groupdetails.path}</td>
 <c:if test="${groupdetails.readonly eq false}">
		                                    <td class="actionicons"><a class="btn-edt permissionSettings-Customers-Edit" href="#" data-toggle="modal" data-target="#geditModal" onClick="populateElement('${groupdetails.id}','${groupdetails.id}','${groupdetails.groupname}','${groupdetails.headname}','${groupdetails.path}','Group')"><i class="fa fa-edit"></i> </a><a href="#" class="permissionSettings-Customers-Delete" onClick="showDeletePopup('${groupdetails.id}')"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td>
		                                    <%-- <td class="actionicons"><a class="btn-edt permissionSettings-Customers-Edit" href="#" data-toggle="modal" data-target="#geditModal" onClick="populateGroupElement('${groupdetails.id}','${groupdetails.id}','${groupdetails.groupname}','${groupdetails.headname}','${groupdetails.path}','Group')"><i class="fa fa-edit"></i> </a><a href="#" class="permissionSettings-Customers-Delete" onClick="showDeletePopup('${groupdetails.id}')"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td> --%>
	                                    </c:if>
	                                     <c:if test="${groupdetails.readonly}">
		                                    <td class="actionicons"></td>
	                                    </c:if>
                                </tr>
								</c:forEach>
                            </tbody>
                        </table>

                    </div> 
						<!-- dashboard cp table end --> 
					</div>
					<div class="tab-pane" id="gtab2" role="tabpane2">
						<div class="col-sm-12 customtable p-0">
                        <table id="dbTable2" class="display row-border dataTable meterialform css-serial" cellspacing="0" width="100%">
                            <thead>
                                <tr>
                                    <th class="text-center">Sub GroupId</th>
									<th class="text-center">sub Group Name</th>
									<th class="text-center">path</th>
									<th class="text-center">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
								 <c:forEach items="${accountingdetails}" var="groups">
									 <c:forEach items="${groups.subgroups}" var="subgroups">
									
	                                <tr id="row${subgroups.id}">
	                                    
	                                    <td align="left"></td>
	                                    <td align="left">${subgroups.groupname}</td>
	                                    <td align="left">${subgroups.path}</td>
	                                    <td class="actionicons"><a class="btn-edt permissionSettings-Customers-Edit" href="#" data-toggle="modal" data-target="#subgeditModal" onClick="populateElement('${groups.id}','${subgroups.id}','${subgroups.groupname}','${subgroups.headname}','${subgroups.path}','SubGroup')"><i class="fa fa-edit"></i> </a><a href="#" class="permissionSettings-Customers-Delete" onClick="showSubGroupDeletePopup('${subgroups.id}','${subgroups.groupname}','${groups.id}','${client.id}')"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td>
	                                </tr>
								</c:forEach> 
									</c:forEach> 
                            </tbody>
                            </tbody>
                        </table>

                    </div> 
					</div>
					<div class="tab-pane" id="gtab3" role="tabpane3">
						<div class="col-sm-12 customtable p-0">
                        <table id="dbTable3" class="display row-border dataTable meterialform css-serial" cellspacing="0" width="100%">
                            <thead>
                                <tr>
                                    <th class="text-center">Ledger Id</th>
									<th class="text-center">Ledger Name</th>
									<th class="text-center">path</th>
									<th class="text-center">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                            	
								<c:forEach items="${ledgerdetails}" var="ledger_details">
									<tr id="row${ledger_details.id}">
                                    	<td align="left"></td>
                                    	<td align="left">${ledger_details.ledgerName}</td>
                                    	<td align="left">${ledger_details.ledgerpath}</td>
                                    	<td class="actionicons"><a class="btn-edt permissionSettings-Customers-Edit" href="#" data-toggle="modal" data-target="#ledgereditModal" onClick="populateLedgerElement('${ledger_details.id}','${ledger_details.ledgerName}','${ledger_details.grpsubgrpName}','${ledger_details.ledgerpath}','${ledger_details.ledgerOpeningBalance}','${ledger_details.ledgerOpeningBalanceType}','Ledger')"><i class="fa fa-edit"></i> </a><a href="#" class="permissionSettings-Customers-Delete" onClick="showLedgerDeletePopup('${ledger_details.id}')"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td>
                                	</tr>
								</c:forEach> 
								
                            </tbody>
                        </table>

                    </div> 
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- right sidebar end -->


      </div>
    </div>
  </div>
</div>
<!-- footer begin here -->
<%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->
<div class="modal fade" id="geditModal" role="dialog" aria-labelledby="editModal" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
			<div class="modal-header p-0">
			<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                     <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                     </button>
                 <div class="bluehdr" style="width:100%"><h3>ADD Group</h3></div>
			</div>
			    <div class="modal-body meterialform popupright bs-fancy-checks">
                     
                 <!-- row begin -->
                  <form:form method="POST" data-toggle="validator" class="meterialform col-12 p-0" name="groupdetails"  modelAttribute="groupdetails" style="display:block">
                	<div class="row content p-5">
                	<p class="lable-txt astrich col-md-4" id="idName" style="margin-top: 10px;">Group Name</p>
                	<div class="form-group mt-1 col-md-7" id="bmsg">
                            <span class="errormsg gmsg" id="groupName_Msg" style="margin-top:-16px;"></span> <span class="errormsg grpmsg" id="grpName_Msg" style="margin-top:-16px;"></span>
                            <input type="text" id="gname" class="gname" name="groupname" required="required" data-error="Enter the group Name" onkeyup="updateGroupDetails()" placeholder="Group Name" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" value="" />
                            <label for="name" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i>
                     </div>
                     <p class="lable-txt astrich col-md-4" style="margin-top: 24px;">Under the Head Name</p>
                     <div class="form-group col-md-7 mb-1" style="margin-top: 16px;">
                            <span class="errormsg hmsg" id="headname_Msg"></span><span class="errormsg headmsg" id="head_Msg" style="margin-top:-16px;"></span>
                            <input type="text" id="headname" class="gname" name="headname" required="required" data-error="Enter the head Name" placeholder="Head Name" onkeyup="updateGroupDetails()" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" value="" />
                     		
                            <label for="input" class="control-label"></label>
							<div class="help-block with-errors"></div>
							<div id="groupname_headnameempty" style="display:none">
								<div class="ddbox">
								  <p>Search didn't return any results.</p>
								</div>
							</div>
                            <i class="bar"></i> 
                     </div>
                     <h6 class="ml-3 mt-3 headspath" style="display:none;">Path :  <span class="text-left" id="headspath"></span></h6>                    
                  </div>
				  <div class="">
                 <a type="button" onClick="groupadd('Group')" class="btn btn-blue-dark group_submit" style="display:none"><span aria-hidden="true">SAVE Group</span></a>
                </div>
                </form:form>
                 </div>
                 <div class="modal-footer text-center" style="display:block">
				<label for="group_submit" class="btn btn-blue-dark m-0" tabindex="0" onClick="groupadd('Group')">Save</label>
				<button type="button" class="btn btn-blue-dark " data-dismiss="modal" aria-label="Close">cancel</button>  
				</div>
                    <!-- row end -->
				</div>
            </div>
        </div>
        <div class="modal fade" id="subgeditModal" role="dialog" aria-labelledby="editModal" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
			   <div class="modal-header p-0">
			   <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                     <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                     </button>
                 <div class="bluehdr" style="width:100%"><h3>ADD Sub Group</h3></div>
			   </div>
			    <div class="modal-body meterialform popupright bs-fancy-checks">
				<form:form method="POST" data-toggle="validator" class="meterialform col-12 p-0" name="groupdetails" modelAttribute="groupdetails" style="display:block">
                 <!-- row begin -->
                  
                	<div class="row content p-4 pt-5">
                	<p class="lable-txt astrich col-md-5" id="idsubName" style="margin-top: 12px;">Sub Group Name</p>
                	<div class="form-group mb-1 col-md-6" id="bmsg">
                            <span class="errormsg" id="subgroupName_Msg"></span><span class="errormsg subgrpmsg" id="subgrpName_Msg" style="margin-top:-16px;"></span>
                            <input type="text" id="subgname" class="subgname" name="subgrpname" required="required"  onkeyup="updateGroupDetails()" data-error="Enter the subgroup Name" placeholder="Subgroup Name" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" value="" />
                            <label for="name" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i>
                     </div>
                     <p class="lable-txt astrich col-md-5" style="margin-top: 20px;">Under the Group/Sub-group of</p>
                     <div class="form-group col-md-6 mb-1" style="margin-top: 14px;">
                            <span class="errormsg" id="headname_Msg"></span><span class="errormsg headmsg" id="subhead_Msg" style="margin-top:-16px;"></span>
                            <input type="text" class="gosname" id="gosname" name="gosname" data-error="Enter head name"  onkeyup="updateGroupDetails()" placeholder="Group or SubGroup" value="" />
                            <label for="input" class="control-label"></label>
							<div class="help-block with-errors"></div>
							<div id="HeadNameempty" style="display:none">
								<div class="ddbox">
								  <p>Search didn't return any results.</p>
								</div>
							</div>
                            <i class="bar"></i> 
                     </div>
                   
                   
                     <h6 class="ml-3 mt-3 headpath" style="display:none;">Path :  <span class="text-left" id="path"></span></h6>
                  </div>
                    <div class="" style="">
                  <input type="hidden" id="group_name"/> 
                   <input type="hidden" id="subgroup_name"/> 
                   <input type="hidden" id="path_name"/> 
		   <input type="hidden" id="oldsubgroupname" value="">
                   <input type="hidden" id="oldParentId" value="">
                     <a type="button" id="submitButton" onClick="groupadd('SubGroup')" class="btn btn-blue-dark subgroup_submit" style="display:none"><span aria-hidden="true">SAVE SubGroup</span></a>  
                </form:form>
                 </div>
                 
				<div class="modal-footer text-center" style="display:block">
				<label for="subgroup_submit" class="btn btn-blue-dark m-0" tabindex="0" onClick="groupadd('SubGroup')">Save</label>
				<button type="button" class="btn btn-blue-dark " data-dismiss="modal" aria-label="Close">cancel</button>  
				</div>
                    <!-- row end -->
				</div>
            </div>
        </div>
		</div>
        <div class="modal fade" id="ledgereditModal" role="dialog" aria-labelledby="editModal" aria-hidden="true">
         <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
			   <div class="modal-header p-0">
			        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                     <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                     </button>
                 <div class="bluehdr" style="width:100%"><h3>ADD Ledger</h3></div>
			   </div>
			    <div class="modal-body meterialform popupright bs-fancy-checks">
                 <form:form method="POST" data-toggle="validator" class="meterialform col-12 p-0" name="ledgerdetails"  modelAttribute="ledgerdetails" style="display:block">
                 <!-- row begin -->
                  
                	<div class="row content p-4 pt-5">
	                	<p class="lable-txt astrich col-md-5" id="idledName" style="margin-top: 15px;">Ledger Name</p>
	                	<div class="form-group mb-1 col-md-6" id="bmsg">
	                            <span class="errormsg" id="ledgerName_Msg"></span><span class="errormsg ledgermsg" id="ledger_Msg" style="margin-top:-16px;"></span>
	                            <input type="text" id="ledgername" class="ledgername update_Ledger" name="ledgerName" required="required" data-error="Enter the ledger Name" placeholder="ledger Name" value="" />
	                            <label for="name" class="control-label"></label>
								<div class="help-block with-errors"></div>
	                            <i class="bar"></i>
	                     </div>
	                     <p class="lable-txt astrich col-md-5" style="margin-top: 20px;">Under the Group/Sub-group of</p>
	                     <div class="form-group col-md-6 mb-6" style="margin-top: 15px;">
	                            <span class="errormsg" id="headname_Msg"></span><span class="errormsg ldgrheadmsg" id="ldgrhead_Msg" style="margin-top:-16px;"></span>
	                            <input type="text" id="goslname"  data-error="Enter the group or subgroup name"  name="grpsubgrpName" placeholder="Group or SubGroup" value="" />
	                            <label for="input" class="control-label"></label>
								<div class="help-block with-errors"></div>
								<div id="GroupNameempty" style="display:none">
									<div class="ddbox">
									  <p>Search didn't return any results.</p>
									</div>
								</div>
	                            <i class="bar"></i> 
	                     </div>	                     
	                    <p class="lable-txt col-md-5" id="idledName" style="margin-top: 15px;">Opening Balance as on ( <span id="ledgerOpeningBalanceMonth"></span> )</p>
	                	 <div class="col-md-6 row">
	                	 <div class="form-group mb-1 col-md-10 pr-0">
	                           <input type="text" id="ledgerOpeningBalance" class="ledgerOpeningBalance update_Ledger" name="ledgerOpeningBalance" data-error="Enter the Opening Balance" placeholder="Opening Balance" value=""/>
	                            <label for="name" class="control-label"></label>
								<div class="help-block with-errors"></div>
	                            <i class="bar"></i>
	                     </div>
	                     <div class="form-group mb-1 col-md-1">
	                      <select class="dr_cr_drop" id="ledgerOpeningBalanceType"><option>Dr</option><option>Cr</option></select>
	                      </div>
	                     </div>
	                     <div>
		                     <a href="${contextPath}/about/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="booksStarting pl-3 mr-1" style="color:#344371;font-size:14px;">Click here </a><p style="font-size:14px;display: contents;">to Change the Opening Balance Date (or Go to Settings -> Profile -> Edit Client and <span class="pl-3">update the date)</span></p>                     
	                     </div>
	                     <h6 class="mt-3 headlpath ml-3">Path :  <span class="text-left" id="lpath"></span></h6>
                  </div>
                 </div>
                 <div class="modal-footer" style="margin-top:500px;">
                  <input type="hidden" id="ledger_name"/>
                   <input type="hidden" id="sledger_name"/>
                   <input type="hidden" id="lpath_name"/>
		   <input type="hidden" id="oldledgername" value="">
                    <a type="button" id="submitButton_ledger" class="btn btn-blue-dark ledger_submit" style="display:none" onclick="ledgeradd('ledger')">Save Ledger</a> 
     			   </div>
                </form:form>
                 </div>
                 
				<div class="modal-footer text-center" style="display:block">
				<label for="ledger_submit" class="btn btn-blue-dark m-0" tabindex="0" onClick="ledgeradd('ledger')">Save</label>
				<button type="button" class="btn btn-blue-dark " data-dismiss="modal" aria-label="Close">cancel</button>  
				</div>

                    <!-- row end -->
				</div>
            </div>
        </div>
  <div class="modal fade" id="deleteModal" role="dialog" aria-labelledby="deleteModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content" style="height: 200px!important;">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Delete Group </h3>
        </div>
        <div class="pl-4 pt-4 pr-4">
          <h6>Are you sure you want to delete Group </h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once deleted, it cannot be reversed.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnDelete" data-dismiss="modal">Delete Group</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
      </div>
    </div>
  </div>
</div>
 <div class="modal fade" id="ledgerModal" role="dialog" aria-labelledby="ledgerModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content" style="height: 200px!important;">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Delete Ledger </h3>
        </div>
        <div class="pl-4 pt-4 pr-4">
          <h6>Are you sure you want to delete Ledger</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once deleted, it cannot be reversed.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="LedgerbtnDelete" data-dismiss="modal">Delete Ledger</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
      </div>
    </div>
  </div>
</div>
<script>

$('#gname').change(function() {
	var groupname=$('#gname').val();
	var clientid='<c:out value="${client.id}"/>';
	$.ajax({
		type : "GET",
		async: false,
		contentType : "application/json",
		url: "${contextPath}/groupnameexits/"+clientid+"/"+groupname,
		success : function(response) {
			if(response == 'success'){
				$('.gmsg').text('Group Name Already Exists').show();
				$('#bmsg').addClass('has-error has-danger');
				err=1;
			}else{
				err=0;
				$('.gmsg').text('').hide();
				$('#bmsg').removeClass('has-error has-danger');
			}
		}
	});
});
$('#subgname').change(function() {
	var groupname=$('#subgname').val();
	var clientid='<c:out value="${client.id}"/>';
	$.ajax({
		type : "GET",
		async: false,
		contentType : "application/json",
		url: "${contextPath}/subgroupnameexits/"+clientid+"/"+groupname,
		success : function(response) {
			if(response == 'success'){
				$('.subgrpmsg').text('Sub Group Name Already Exists').show();
				$('#bmsg').addClass('has-error has-danger');
				err=1;
			}else{
				err=0;
				$('.subgrpmsg').text('').hide();
				$('#bmsg').removeClass('has-error has-danger');
			}
		}
	});
});
$('#ledgername').change(function() {
	var ledgername=$('#ledgername').val();
	var clientid='<c:out value="${client.id}"/>';
	$.ajax({
		type : "GET",
		async: false,
		contentType : "application/json",
		url: "${contextPath}/ledgernameexits/"+clientid+"/"+ledgername,
		success : function(response) {
			if(response == 'success'){
				$('.ledgermsg').text('ledger Already Exists').show();
				$('#bmsg').addClass('has-error has-danger');
				err=1;
			}else{
				err=0;
				$('.ledgermsg').text('').hide();
				$('#bmsg').removeClass('has-error has-danger');
			}
		}
	});
});
function groupadd(type){
	var err=0;
	var groupname, headname,parentId="",subgroupid="",pathname="",oldgroupname="",oldsubgroupname="";
	var clientid='<c:out value="${client.id}"/>';
	var sgname = $('#subgname').val();
	var shname = $('#gosname').val();
	if(type == 'Group') {
		//oldgroupname = $('#oldgroupname').val();
		oldgroupname = $('#gname').val();
		groupname = $('#gname').val();
		headname = $('#headname').val();
		if(oldgroupname == '' || oldgroupname != groupname){
			$.ajax({
				type : "GET",
				async: false,
				contentType : "application/json",
				url: "${contextPath}/groupnameexits/"+clientid+"/"+groupname,
				success : function(response) {
					if(response == 'success'){
						$('.gmsg').text('Group Name Already Exists').show();
						$('#bmsg').addClass('has-error has-danger');
						err=1;
					}else{
						err=0;
						$('.gmsg').text('').hide();
						$('#bmsg').removeClass('has-error has-danger');
					}
				}
			});
		}else{
			err=0;
			$('.gmsg').text('').hide();
			$('#bmsg').removeClass('has-error has-danger');
		}
	} else {	
		var id = $('#group_name').val();
		var subGroupid = $('#subgroup_name').val();
		parentId = id;
		
		subgroupid = subGroupid;
		groupname = $('#subgname').val();
		var path = $('#path_name').val();
		pathname = path+"/"+groupname;
		headname = $('#gosname').val();
		oldsubgroupname = $('#oldsubgroupname').val();
		
		if(parentId == ""){
			parentId=$('#oldParentId').val();
		}
		
		
		if(oldsubgroupname == '' || oldsubgroupname != groupname){
			$.ajax({
				type : "GET",
				async: false,
				contentType : "application/json",
				url: "${contextPath}/subgroupnameexits/"+clientid+"/"+groupname,
				success : function(response) {
					if(response == 'success'){
						$('.subgrpmsg').text('Sub Group Name Already Exists').show();
						$('#bmsg').addClass('has-error has-danger');
						err=1;
					}else{
						err=0;
						$('.subgrpmsg').text('').hide();
						$('#bmsg').removeClass('has-error has-danger');
					}
				}
			});
		}else{
			err=0;
			$('.gmsg').text('').hide();
			$('#bmsg').removeClass('has-error has-danger');
		}
	}
	
	if(err != 0){
		$('.gmsg').text('Group Name Already Exists').show();
		$('#bmsg').addClass('has-error has-danger');
		return false;
	}else{
		if(accountingValidation(type)){
			$.ajax({
				url: "${contextPath}/cp_savegroupdetails/${id}/${client.id}/${fullname}/${usertype}/${month}/${year}",
				async: false,
				cache: false,
				data: {
					'type': type,
					'elemId': currentId,
					'groupname': groupname,
					'headname': headname,
					'parentId': parentId,
					'subgroupid':subgroupid,
					'pathname' : pathname
				},
				dataType: "text",
			    contentType: 'application/json',
				success : function(data) {
					if(type == 'Group'){
						location.href="${contextPath}/cp_Accounting/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=group";
					}else{
						location.href="${contextPath}/cp_Accounting/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=sgroup";
					}
				},
				error : function(data) {}	
			});
		}	
	}
}
function ledgeradd(type){
	var ledgername,groupname;
	ledgername = $('#ledgername').val();
	groupname = $('#goslname').val();
	var ledgerOpeningBalance = 0;
	if($('#ledgerOpeningBalance').val() != undefined || $('#ledgerOpeningBalance').val() != null || $('#ledgerOpeningBalance').val() != ""){
		ledgerOpeningBalance = $('#ledgerOpeningBalance').val();
	}
	
	var ledgerpath = $('#lpath_name').val();
	var clientid = '${client.id}';
	var oldledgername=$('#oldledgername').val();
	var err = 0;
	//var ledgerOpeningBalance=$('#ledgerOpeningBalance').val();
	var ledgerOpeningBalanceType=$('#ledgerOpeningBalanceType').val();
	if(oldledgername == '' || oldledgername != ledgername){
		$.ajax({
			type : "GET",
			async: false,
			contentType : "application/json",
			url: "${contextPath}/ledgernameexits/"+clientid+"/"+ledgername,
			success : function(response) {
				if(response == 'success'){
					$('.ledgermsg').text('ledger Already Exists').show();
					$('#bmsg').addClass('has-error has-danger');
					err=1;
				}else{
					err=0;
					$('.ledgermsg').text('').hide();
					$('#bmsg').removeClass('has-error has-danger');
				}
			}
		});
	}else{
		err=0;
		$('.gmsg').text('').hide();
		$('#bmsg').removeClass('has-error has-danger');
	}
	if(err != 0){
		$('.gmsg').text('Group Name Already Exists').show();
		$('#bmsg').addClass('has-error has-danger');
		return false;
	}else{	
if(ledgerValidation(type)){
	$.ajax({
		url: "${contextPath}/cp_saveledgerdetails/${id}/${client.id}/${fullname}/${usertype}/${month}/${year}",
		async: false,
		cache: false,
		data: {
			'clientid':clientid,
			'ledgerName': ledgername,
			'grpsubgrpName': groupname,
			'ledgerpath':ledgerpath,
			'oldledgername':oldledgername,
			'ledgerOpeningBalance':ledgerOpeningBalance,
			'ledgerOpeningBalanceType':ledgerOpeningBalanceType
		},
		dataType: "text",
	    contentType: 'application/json',
		success : function(data) {
			location.href="${contextPath}/cp_Accounting/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=ledger";
			$('#gtab3').click();
		},
		error : function(response) {}
	});
	}
	}
}
$('#subgname').keyup(function() {
	var spath = $("#subgname").val();
	var holdpath=$('#path').text();

	var splitpath=holdpath.split('/');
	
	var endpath=splitpath[splitpath.length-1];
	
	$('#path').text(holdpath.replace(endpath,spath));
	$('.headpath').css('display','block');
});
$('#ledgername').keyup(function() {
	var spath = $("#ledgername").val();
	var path = $("#lpath_name").val();
	var path = $('#lpath').text(path+"/"+spath);
	$('.headlpath').css('display','block');
});
function accountingValidation(type){
	var groupName = $('#gname').val();
	var grpHeadName = $('#headname').val();
	var subGroupName = $('#subgname').val();
	var subHeadName = $('#gosname').val();
	
	var c=0;
	if(type == 'Group') {	
		if(groupName ==""){
			$('.with-errors').html('');
			$('#grpName_Msg').text("Please Enter Group Name"); 
			c++;
		}else{
			$('#grpName_Msg').text(""); 
		}
		if(grpHeadName =="" || grpHeadName == null){
			$('.with-errors').html('');
			$('#head_Msg').text("Please Enter Head name"); 
			c++;
		}else{
			$('#head_Msg').text(""); 
		}
	}else {
		if(subGroupName ==""){
			$('.with-errors').html('');
			$('#subgrpName_Msg').text("Please Enter Sub Group Name"); 
			c++;
		}else{
			$('#subgrpName_Msg').text(""); 
		}
		if(subHeadName =="" || subHeadName ==null){
			$('.with-errors').html('');
			$('#subhead_Msg').text("Please Enter head name"); 
			c++;
		}else{
			$('#subhead_Msg').text(""); 
		}
	}
	return c==0; 
}
function updateGroupDetails(){
	var groupName = $('#gname').val();
	var grpHeadName = $('#headname').val();
	var subGroupName = $('#subgname').val();
	var subHeadName = $('#gosname').val();
	if(grpHeadName == ''){
		$('#headname_Msg').show();
	} else{
		$('#head_Msg').text(""); $('.with-errors').hide();
		$('#headname_Msg').hide();
	}
	if(subHeadName == ''){
		$('#headname_Msg').show();
		} else{
			$('#subhead_Msg').text(""); $('.with-errors').hide();
			$('#headname_Msg').hide();
		}
}
$( "#goslname").change(function() {
	var ldgrHeadName = $('#goslname').val();
	if(ldgrHeadName == '' || ldgrHeadName == null){
		$('#headname_Msg').show();
		} else{
			$('#ldgrhead_Msg').text(""); 
			$('#headname_Msg').hide();
		}
});
function ledgerValidation(type){
	var ledgerName = $('#ledgername').val();
	var ledgerHeadName = $('#goslname').val();
	var c=0;
	if(type == 'ledger') {	
		if(ledgerName == "" || ledgerName == null){
			$('.with-errors').html('');
			$('.ledgermsg').text("Please Enter ledger Name"); 
			c++;
		}else{
			$('.ledgermsg').text(""); 
		}
		if(ledgerHeadName =="" || ledgerHeadName == null){
			$('.with-errors').html('');
			$('#ldgrhead_Msg').text("Please Enter group or subgroup name"); 
			c++;
		}else{
			$('#ldgrhead_Msg').text(""); 
		}
}
	return c==0; 
}


</script>
</body>
</html>