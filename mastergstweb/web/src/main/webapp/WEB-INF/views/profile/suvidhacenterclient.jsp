<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Clients/Business</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/common/datetimepicker-inv.js" type="text/javascript"></script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAg_Twe-j7K6RXYeUswZv3gu_kwMrjbatM&libraries=places&region=IN"></script>
<script src="${contextPath}/static/mastergst/js/signups/google-address.js" type="text/javascript"></script>
<style>div#dbTable1_length{margin-left: 15px;}div.dataTables_filter input{height:30px!important;</style>
</head>
<script type="text/javascript">
	var table,customerArray = new Array();;
	$(document).ready(function() {
		$('#cp_center').css("display","block");$('#cp_Allcenter').css("display","block");$('#cp_centerFiling').css("display","block");$('#cp_centerClinet').css("display","block");$('#cpCenterBillingNav').addClass('active');$('#nav-team').addClass('active');$('.nonAspAdmin').addClass('active');
		table = $('table.display').DataTable({
			dom: '<"toolbar"f>Blfrtip<"clear">',
			"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
			"pageLength": 10,
			'columnDefs': [{ 'orderable': false, 'targets': 0 }],
			"language": {
				"search": "_INPUT_",
				"searchPlaceholder": "Search...",
				"paginate": {
					"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
					"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
				}
			}
		});
		var usrtype = '${usertype}';
		if(usrtype == 'enterprise' || usrtype == 'business'){$("div.toolbar").html('<h4 class="mt-1">All Business</h4><a href="${contextPath}/dwnldallclientxls/${id}/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}" class="btn btn-blue">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a><a href="#" class="child_user btn btn-blue-dark"  onclick="showDeleteAllPopup()">Delete All Business</a><a href="#" class="btn btn-blue-dark disabled" id="clientUpdate" onclick="showInvoiceCuttoff()">Update Invoice CutOff Date</a> ');
		}else{$("div.toolbar").html('<h4 class="mt-1">All Clients</h4><a href="${contextPath}/dwnldallclientxls/${id}/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}" class="btn btn-blue">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a><a href="#" class="child_user btn btn-blue-dark"  onclick="showDeleteAllPopup()">Delete All Clients</a><a href="#" class="btn btn-blue-dark disabled" id="clientUpdate" onclick="showInvoiceCuttoff()">Update Invoice CutOff Date</a> ');}
		var headertext = [],
			headers = document.querySelectorAll("table.display th"),
			tablerows = document.querySelectorAll("table.display th"),
			tablebody = document.querySelector("table.display tbody");
		for (var i = 0; i < headers.length; i++) {var current = headers[i];headertext.push(current.textContent.replace(/\r?\n|\r/, ""));}
		for (var i = 0, row; row = tablebody.rows[i]; i++) {for (var j = 0, col; col = row.cells[j]; j++) {col.setAttribute("data-th", headertext[j]);}}
		$('#inv_cutoffdateSales,#inv_cutoffdatePurchases').datetimepicker({
	  	timepicker: false,
	  	format: 'd/m/Y',
	  	scrollMonth: true
	});
	});
	
	function showDeletePopup(clientId, userId, name) {$('#deleteModal').modal('show');$('#delPopupDetails').html(name);$('#btnDelete').attr('onclick', "deleteClient('"+clientId+"','"+userId+"')");}
	function deleteClient(clientId, userId) {
		$.ajax({
			url: "${contextPath}/delclnt/"+clientId+"/"+userId,
			success : function(response) {table.row( $('#row'+clientId) ).remove().draw();}
		});
	}
	function showDeleteAllPopup() {$('#deleteAllModal').modal('show');$('#btnAllDelete').attr('onclick', "deleteAllClients()");}
	function showInvoiceCuttoff() {$('#invoiceCutoffModal').modal('show');$('#btnAllUpdate').attr('onclick', "updateAllClients()");}
	function updateAllClients() {
		$('.invview_Process').removeClass('d-none');
		var clientdat = new Object;
		clientdat.clientids = customerArray;
		clientdat.salescutOffdate = $('#inv_cutoffdateSales').val();
		clientdat.purchasecutOffdate = $('#inv_cutoffdatePurchases').val();
		setTimeout(function(){
			$.ajax({
				url: "${contextPath}/updateSelectedClients/${id}",
				type:'POST',
				async: false,
				cache: false,
				contentType: 'application/json',
				data: JSON.stringify(clientdat),
				success : function(response) {
					$('.invview_Process').addClass('d-none');
					window.location.href = "${contextPath}/cp_centersclients/${id}/${fullname}/${usertype}/${month}/${year}";
				}
			});
		},1000);
	}
	function deleteAllClients() {
		$.ajax({
			url: "${contextPath}/delAllclnts/${id}",
			success : function(response) {table.clear().draw();}
		});
	}
	function showRemovePopup(userid,email,clientid) {$('#removeModal').modal('show');$('#btnRemove').attr('onclick', "removeUser('"+userid+"','"+email+"','"+clientid+"')");}
	function removeUser(userid,email,clientid) {
		$.ajax({
			url: "${contextPath}/delinkUser/"+userid+"/"+email+"/"+clientid,
			success : function(response) {table.row($('#row'+clientid)).remove().draw();}
		});
	}
	function updateMainSelection(chkBox) {
		customerArray = new Array();
		var check = $('#allclients').prop("checked");
		var rows = table.rows().nodes();
		if(check) {
			table.rows().every(function () {
				var row = this.data();
				var index = $(row[0]).attr('index');
				customerArray.push(index);
		   });
		}else{
			customerArray = new Array();
		}
		if(customerArray.length > 0){
			$('#clientUpdate').removeClass("disabled");
		}else{
			$('#clientUpdate').addClass("disabled");
		}
		$('input[type="checkbox"]', rows).prop('checked', check);
	}
	
	function updateSelection(id, chkBox) {
		if(chkBox.checked) {
			customerArray.push(id);
		} else {
			var pArray=new Array();
			var poArray = new Array();
			customerArray.forEach(function(inv) {
				if(inv != id) {
					pArray.push(inv);
				}
			});
			customerArray = pArray;
			customerArray.forEach(function(inv) {
				if(inv != id) {
					poArray.push(inv);
				}
			});
			customerArray = poArray;
		}
		var purchaseTableLength = $('#dbTable1').dataTable().fnGetData().length;
		if(customerArray.length == purchaseTableLength){
			$('#allclients').prop("checked",true);
		}else{
			$('#allclients').prop("checked",false);
		}
		if(customerArray.length > 0){
			$('#clientUpdate').removeClass("disabled");
		}else{
			$('#clientUpdate').addClass("disabled");
		}
}
</script>
<body class="body-cls">
  <!-- header page begin -->
 <c:choose>
	<c:when test='${not empty client && not empty client.id}'>
		<%@include file="/WEB-INF/views/includes/client_header.jsp" %>
	</c:when>
	<c:otherwise>
		<%@include file="/WEB-INF/views/includes/newclintheader.jsp" %>
	</c:otherwise>
	</c:choose>
<div class="breadcrumbwrap">
	<div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb"><li class="breadcrumb-item"><c:choose><c:when test="${usertype eq userCenter}"><a href="#" class="urllink" link="${contextPath}/cp_centers/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:when><c:otherwise><a href="#" class="urllink" link="${contextPath}/teamuser/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:otherwise></c:choose></li><li class="breadcrumb-item active"><c:choose><c:when test="${usertype eq userEnterprise}">All Business</c:when><c:otherwise>All Clients</c:otherwise></c:choose></li></ol>
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
        <div class="db-ca-wrap">
            <div class="container">
                <div class="row">
                    <!-- left side begin -->
                    <%@include file="/WEB-INF/views/profile/leftnav.jsp" %>
                    <!-- left side end -->
                    <!-- dashboard cp  begin -->
                    <div class="col-md-10 col-sm-12 customtable p-0">
                        <table id="dbTable1" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                            <thead><tr>
							<th><div class="checkbox"><label><input type="checkbox" id='allclients' onClick="updateMainSelection(this)"/><i class="helper"></i></label></div> </th>
							<th></th><th class="text-center">Business / Client Name</th><th class="text-center">EmailId</th><th class="text-center">Contact Number</th><th class="text-center">GSTIN</th><th class="text-center">State</th><th class="text-center">Action</th></tr></thead>
                            <tbody>
							<c:if test="${not empty centers}">
							<c:forEach items="${centers}" var="center">
							<c:set var="contains" value="false" />
							<c:forEach var="item" items="${companyUser.company}">
							  <c:if test="${item eq center.id}">
							    <c:set var="contains" value="true" />
							  </c:if>
							</c:forEach>
								<tr id="row${center.id}">
									<td><div class="checkbox" index="${center.id}"><label><input type="checkbox" id="invFilter${center.id}" onClick="updateSelection('${center.id}',this)"/><i class="helper"></i></label></div></td>
									<td><span class="imgsize-wrap-thumb1"><c:if test="${not empty center.logoid}"><img src="${contextPath}/getlogo/${center.logoid}" alt="Logo" class="imgsize-thumb" id="clntlogo"  style="float: left;"></c:if><c:if test="${empty center.logoid}"><img src="${contextPath}/static/mastergst/images/master/defaultcompany.png" alt="Logo" class="imgsize-thumb" id="clntlogo"  style="float: left;"></c:if><c:if test="${contains}"><img src="${contextPath}/static/mastergst/images/master/only-link.png" alt="Logo" class="imgsize-thumb" style="float: left;max-width: 50%;margin-top: 17px;margin-left: 17px;"></c:if> </span></td>
                                    <td>${center.businessname}</td><td class="text-left">${center.email}</td><td class="text-left">${center.mobilenumber}</td><td class="text-left">${center.gstnnumber}</td><td class="text-left">${center.statename}</td>
									<td class="text-right" data-th="Action"><a href="#" <c:if test="${contains}">onClick="showRemovePopup('${id}','${email}','${center.id}')" <span>Remove</span></c:if><c:if test="${not contains}"> onClick="showDeletePopup('${center.id}','${id}','${center.businessname}')"><span>Delete</span></c:if></a></td>
                                </tr>
								</c:forEach>
								</c:if>
                            </tbody>
                        </table>
                    </div> 
  <!-- dashboard cp table end -->
</div>                    
                </div>
            </div>
        </div>
    <!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->
</body>
<!-- Delete Modal -->
<div class="modal fade" id="deleteModal" role="dialog" aria-labelledby="deleteModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Delete <c:choose><c:when test="${usertype eq userEnterprise}">Business</c:when><c:otherwise>Client</c:otherwise></c:choose> </h3>
        </div>
        <div class=" pl-4 pt-4 pr-4">
          <h6>Are you sure you want to delete <c:choose><c:when test="${usertype eq userEnterprise}">business</c:when><c:otherwise>client</c:otherwise></c:choose> <span id="delPopupDetails"></span> ?</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once deleted, it cannot be reversed.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnDelete" data-dismiss="modal">Delete <c:choose><c:when test="${usertype eq userEnterprise}">Business</c:when><c:otherwise>Client</c:otherwise></c:choose></button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
      </div>
    </div>
  </div>
</div>
<div class="modal fade" id="deleteAllModal" role="dialog" aria-labelledby="deleteAllModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Delete All <c:choose><c:when test="${usertype eq userEnterprise}">Business</c:when><c:otherwise>Clients</c:otherwise></c:choose> </h3>
        </div>
        <div class=" pl-4 pt-4 pr-4">
          <h6>Are you sure you want to delete all <c:choose><c:when test="${usertype eq userEnterprise}">business</c:when><c:otherwise>clients</c:otherwise></c:choose> ?</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once deleted, it cannot be reversed.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnAllDelete" data-dismiss="modal">Delete All <c:choose><c:when test="${usertype eq userEnterprise}">Business</c:when><c:otherwise>Clients</c:otherwise></c:choose></button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
      </div>
    </div>
  </div>
</div>
<div class="modal fade" id="removeModal" role="dialog" aria-labelledby="removeModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Remove Client </h3>
        </div>
        <div class=" pl-4 pt-4 pr-4">
          <h6>Are you sure you want to remove Client <span id="delPopupDetails"></span> ?</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once removed, Client will not have access.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnRemove" data-dismiss="modal">Remove Client</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Remove</button>
      </div>
    </div>
  </div>
</div>
<div class="modal fade" id="invoiceCutoffModal" role="dialog" aria-labelledby="invoiceCutoffModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document" style="max-width:650px;">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Invoice CutOff Date Configuration </h3>
        </div>
		<div id="invview_Process" class="invview_Process d-none"  style="color:red;font-size:20px;position:absolute;z-index:99;top:37%;left: 46%;">
			<img src="${contextPath}/static/mastergst/images/eclipse-spinner.gif" alt="spinner-img" style="width: 150px;height: 150px;"/>
		</div>
		<div class="form-group inv-text-div col-md-12 col-sm-12 pl-4">
			<span class="lable-txt">Invoice Date CutOff for Sales(dd/mm/yyyy) <span class="colon" style="margin-left:30px">: </span>  </span>                          
			<span id="inv_cutoffdate_sales" class="inv_cutoffdate_sales" style="display: inline-block;"><input type="text" class="form-control reconcile" id="inv_cutoffdateSales" placeholder="dd/mm/yyyy" style="border: 1px solid lightgrey;" value=""></span>
		</div>
		<div class="form-group inv-text-div col-md-12 col-sm-12 pl-4">
			<span class="lable-txt">Invoice Date CutOff for Purchases(dd/mm/yyyy) <span class="colon">: </span>  </span>                          
			<span id="inv_cutoffdate_purchases" class="inv_cutoffdate_purchases" style="display: inline-block;"><input type="text" class="form-control reconcile" id="inv_cutoffdatePurchases" placeholder="dd/mm/yyyy" style="border: 1px solid lightgrey;" value=""></span>
		</div>
        <div class=" pl-4 pt-4 pr-4">
          <h6>Are you sure you want to update all <c:choose><c:when test="${usertype eq userEnterprise}">business</c:when><c:otherwise>clients</c:otherwise></c:choose> ?</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once updated, it cannot be reversed.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnAllUpdate" >Update All <c:choose><c:when test="${usertype eq userEnterprise}">Business</c:when><c:otherwise>Clients</c:otherwise></c:choose></button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Update</button>
      </div>
    </div>
  </div>
</div>
</html>