<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Client Reminders</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/userprofile_edit/reminder.css" media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>

<script type="text/javascript">
	var table;var reminderArray=new Array();
	var usrId  = '${user.id}';var usrname = '${user.fullname}';var usremail = '${user.email}';var usrmobile = '${user.mobilenumber}';
	$(document).ready(function() {
		$('.auditorhead').addClass('active');
		table = $('table.display').DataTable({
			"dom": '<"toolbar">frtip',
			"pageLength": 10,
			"order": [[4,'desc']],
			"language": {
				"search": "_INPUT_",
				"searchPlaceholder": "Search...",
				"paginate": {
					"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
					"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
				}
			}
		});
		$("div.toolbar").html('<h4>All Auditors</h4><a href="#" class="btn btn-blue-dark" data-toggle="modal" data-target="#editModal" onclick="populateElement()">Add</a>');
		var headertext = [],
			headers = document.querySelectorAll("table.display th"),
			tablerows = document.querySelectorAll("table.display th"),
			tablebody = document.querySelector("table.display tbody");
		for (var i = 0; i < headers.length; i++) {var current = headers[i];headertext.push(current.textContent.replace(/\r?\n|\r/, ""));}
		for (var i = 0, row; row = tablebody.rows[i]; i++) {for (var j = 0, col; col = row.cells[j]; j++) {col.setAttribute("data-th", headertext[j]);}}
	});

</script>
</head>
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
					<ol class="breadcrumb"><li class="breadcrumb-item"><c:choose><c:when test="${usertype eq userCenter}"><a href="#" class="urllink" link="${contextPath}/cp_centers/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:when><c:otherwise><a href="#" class="urllink" link="${contextPath}/teamuser/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:otherwise></c:choose></li><li class="breadcrumb-item active"><c:choose><c:when test="${usertype eq userEnterprise}">Auditors</c:when><c:otherwise>Auditors</c:otherwise></c:choose></li></ol>
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
        <div class="db-ca-wrap">
            <div class="container">
                <div class="row">
                  
                    <!-- dashboard cp  begin -->
                    <div class="col-md-12 col-sm-12 customtable p-0">
                        <table id="dbTable1" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                            <thead><tr><th class="text-left">Auditor Name</th><th class="text-left">Place</th><th class="text-left">Membership Number</th><th class="text-left">PAN</th><th class="text-left">State</th><th class="text-left" width="12%">Action</th></tr></thead>
                            <tbody>
                            <c:forEach items="${auditorDetails}" var="auditorDetail">
								<tr>
                                    <td>${auditorDetail.nameofsignatory}</td>
                                    <td class="text-left">${auditorDetail.place}</td>
                                    <td class="text-left">${auditorDetail.membershipno}</td>
                                    <td class="text-left">${auditorDetail.panno}</td>
                                    <td class="text-left">${auditorDetail.statename}</td>
									 <td class="actionicons"><a class="btn-edt" href="#" data-toggle="modal" data-target="#editModal" onClick="populateElement('${auditorDetail.id}')"><i class="fa fa-edit"></i> </a></td>
                                </tr>
							</c:forEach>	
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
<div class="modal fade" id="AllRemindersModal" role="dialog" aria-labelledby="AllRemindersModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document">
			<div class="modal-content">
			<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>All Reminders</h3>
                    </div>
				</div>
				<div class="modal-body popupright ">
					<div class="row pl-4 pr-2 pt-4">
					<h6 id="noReminders"></h6>
					<div class="form-group col-md-12">
						<div class="remindersTab" style="max-height:500px;overflow-y:auto;">
						
						</div>
					</div>
						
					</div>
				</div>
				<div class="modal-footer" style="display:block;text-align:center;">
				 <button type="button" class="btn  btn-blue-dark" data-dismiss="modal">Close</button>
				</div>
				
			</div>
		</div>
	</div>
	<div class="modal fade" id="editModal" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
				<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span></button>
                    <div class="bluehdr" style="width:100%"><h3>Auditor Details</h3></div>
				</div>
                <div class="modal-body meterialform popupright bs-fancy-checks">
                    <form:form method="POST" data-toggle="validator" class="meterialform" id="auditorform" name="auditorform" action="${contextPath}/saveauditordetails/${id}/${fullname}/${usertype}/${month}/${year}" modelAttribute="auditorDetails">
					<div class="row  p-5" style="padding-top:1rem!important">
                        <div class="form-group col-md-6 col-sm-12"><p class="lable-txt astrich">Place</p><input type="text" id="place" class="place" name="place" required="required" data-error="Please enter Place" placeholder="Hyderabad" value="" /><label for="email" class="control-label"></label> <div class="help-block with-errors"></div><i class="bar"></i></div>				
						<div class="form-group col-md-6 col-sm-12"><p class="lable-txt astrich">Name Of Signatory</p><input type="text" id="nameofsignatory" name="nameofsignatory" required="required" data-error="Please Enter Name Of signatory" placeholder="Name" value="" /><label for="input" class="control-label"></label><div class="help-block with-errors"></div><i class="bar"></i></div>
                        <div class="form-group col-md-6 col-sm-12"><p class="lable-txt astrich">Membership No </p><input type="text" id="membershipno" name="membershipno" data-minlength="1" maxlength="8" required="required" data-error="Please enter lessthan than 9 characters" placeholder="123456789" value="" /><label for="input" class="control-label"></label><div class="help-block with-errors"></div><i class="bar"></i></div>
						<div class="form-group col-md-6 col-sm-12"><p class="lable-txt astrich">PAN No</p><input type="text" id="panno" name="panno" required="required" placeholder="123456789" value="" /><label for="input" class="control-label"></label><div class="help-block with-errors"></div><i class="bar"></i></div>
						<div class="form-group col-md-6 col-sm-12"><p class="lable-txt astrich">Building No./Flat No</p><input type="text" name="buildorflatno" id="buildorflatno" required="required"  placeholder=" " value="" /><label for="input" class="control-label"></label><div class="help-block with-errors"></div><i class="bar"></i></div>								
						<div class="form-group col-md-6 col-sm-12"><p class="lable-txt">Floor No</p><input type="text" name="floorno" id="floorno" required="required"  placeholder=" " value="" /><label for="input" class="control-label"></label><i class="bar"></i> </div>
                        <div class="form-group col-md-6 col-sm-12"><p class="lable-txt">Building Name</p><input type="text" name="buildingname" id="buildingname" required="required"  placeholder=" " value="" /><label for="input" class="control-label"></label><i class="bar"></i> </div>
                        <div class="form-group col-md-6 col-sm-12"><p class="lable-txt">Street Name</p><input type="text" name="streetname" id="streetname" required="required"  placeholder=" " value="" /><label for="input" class="control-label"></label><i class="bar"></i> </div>
                        <div class="form-group col-md-6 col-sm-12"><p class="lable-txt">Locality/Town/Village</p><input type="text" name="locality" id="locality" required="required"  placeholder=" " value="" /><label for="input" class="control-label"></label><i class="bar"></i> </div>
                        <div class="form-group col-md-6 col-sm-12"><p class="lable-txt">District name</p><input type="text" name="districtname" id="districtname" required="required"  placeholder=" " value="" /><label for="input" class="control-label"></label><i class="bar"></i> </div>
                        <div class="form-group col-md-6 col-sm-12"><p class="lable-txt">State</p><input type="text" name="statename" id="statename" required="required"  placeholder=" " value="" /><label for="input" class="control-label"></label><i class="bar"></i> </div>
                        <div class="form-group col-md-6 col-sm-12"><p class="lable-txt">Pin code</p><input type="text" name="pincode" id="pincode" required="required"  placeholder=" " value="" /><label for="input" class="control-label"></label><i class="bar"></i> </div>
                        <div class="col-12 text-center mt-3" style="display:none"><input type="hidden" name="userid" value="<c:out value="${id}"/>"><input type="hidden" name="fullname" value="<c:out value="${fullname}"/>"><input type="hidden" name="usertype" value="<c:out value="${usertype}"/>"><input type="hidden" name="id" id="auditorid" value="">
                        <input type="submit" class="btn btn-blue-dark auditor_submit" style="display:none" value="Save"/><a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close">Cancel</a></div>
                    </div>
					</form:form>
                </div>
				<div class="modal-footer text-center" style="display:block">
				<label for="auditor_submit" class="btn btn-blue-dark m-0 auditorlabbut" tabindex="0" >Save</label>
				<a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close" onClick="test()">Cancel</a>
				</div>
            </div>
        </div>
    </div>
<script>
var form = $('#auditorform');
$('.auditorlabbut').click(function(){
	$('#auditorform').submit();
});

var auditorDetails = [
	{'txt':'place','val':'place'},
	{'txt':'nameofsignatory','val':'nameofsignatory'},
	{'txt':'membershipno','val':'membershipno'},
	{'txt':'panno','val':'panno'},
	{'txt':'buildorflatno','val':'buildorflatno'},
	{'txt':'floorno','val':'floorno'},
	{'txt':'buildingname','val':'buildingname'},
	{'txt':'streetname','val':'streetname'},
	{'txt':'locality','val':'locality'},
	{'txt':'districtname','val':'districtname'},
	{'txt':'statename','val':'statename'},
	{'txt':'pincode','val':'pincode'}
];

function getInvData(auditorid, popudateData){
	var urlStr = contextPath+'/getaudit/'+auditorid;
	$.ajax({
		url: urlStr,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			popudateData(response);
		}
	});
}
function populateElement(auditorId){
	$('.with-errors').html('');
	$('.form-group').removeClass('has-error has-danger');
	$.each(auditorDetails,function(item,index){
		form.find("input[id='"+index.txt+"']").val("");
	});
	if(auditorId){
		$('#auditorid').attr('name','id').val('');
	getInvData(auditorId, function(auditor) {
		$.each(auditor,function(key,value) {
			form.find("input[id='"+key+"']").val(value);
			if(key=='userid'){
				$('#auditorid').val(value);
			}
		});
	})
	}else{
		$('#auditorid').attr('name','abcd').val('');
	}
}




	
	
</script>
</html>