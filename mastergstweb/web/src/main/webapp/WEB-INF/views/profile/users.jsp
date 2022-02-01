<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Users</title>
<%@include file="/WEB-INF/views/includes/common_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard-cp/dashboard-cp.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard-ca/dashboard-cas.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/select2.min.css" media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/select2.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/userss.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<style type="text/css">.companieslist{font-size: 14px;font-weight: bold;margin-top: 4px;}#allcompanies{border-radius: 4px;color: #5769bb;top: 3px;left: 8px;height: 14px;width: 14px;}div#dbTable1_length{margin-left: 15px;}div.dataTables_filter input{height:30px!important;}</style>
<script type="text/javascript">
	var users = new Array(), currentEmail='';var table;
	$(document).ready(function() {
		$('#cpUserNav').addClass('active');$('.nonAspAdmin').addClass('active');
			table = $('table.display').DataTable({
				dom: '<"toolbar"f>Blfrtip<"clear">', 	
				"pageLength": 10,
				"paging": true,
				"searching": true,
				"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
				"responsive": true,
				"ordering": true,
				"language": {
					"search": "_INPUT_",
					"searchPlaceholder": "Search...",
					"paginate": {
					   "previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
						"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
				   }
				 }
		});
			table.on('draw', rolesPermissions);
			var addsubuser = '${addsubuser}';
			if(addsubuser == 'false'){$("div.toolbar").html('<h4 style="margin-top: 5px;">Users</h4>');
			}else{$("div.toolbar").html('<h4 style="margin-top: 5px;">Users</h4><a href="#" class="btn btn-blue-dark"  data-toggle="modal" data-target="#editModal" onclick="populateElement()">Add</a> ');}
		var headertext = [],headers = document.querySelectorAll("table.display th"),tablerows = document.querySelectorAll("table.display th"),tablebody = document.querySelector("table.display tbody");
		for (var i = 0; i < headers.length; i++) {var current = headers[i];headertext.push(current.textContent.replace(/\r?\n|\r/, ""));}
		for (var i = 0, row; row = tablebody.rows[i]; i++) {for (var j = 0, col; col = row.cells[j]; j++) {col.setAttribute("data-th", headertext[j]);}}
		$('#role, #company, #branches, #verticals,#customers').select2();
		var cUserType ='<c:out value="${usertype}"/>';
		var userEmail = {
			url: function(phrase) {phrase = phrase.replace('(',"\\(");
			phrase = phrase.replace(')',"\\)");return "${contextPath}/srchUsers/${usertype}/${id}?query="+ phrase + "&format=json";},
			getValue: "email",
			list: {
				onChooseEvent: function() {
					var custData = $("#profileemail1").getSelectedItemData();
					if(custData && custData.email) {
						$('#profileemail1').val(custData.email);
						$.ajax({
							url: "${contextPath}/chkemail?email="+$('#profileemail1').val(),
							async: false,
							cache: false,
							dataType:"json",
							success : function(response, status, request) {
								if(response) {
									var usertypes = [];
									if(cUserType == 'suvidha'){usertypes = ["suvidha","business"];}else if(cUserType == 'cacmas'){usertypes = ["cacmas","business","enterprise"];}else if(cUserType == 'enterprise'){usertypes = ["cacmas","enterprise"];}else if(cUserType == 'business'){usertypes = ["cacmas","suvidha","business"];}
									response.id = request.getResponseHeader('user_id');response.cdate = request.getResponseHeader('cdate');
									if(!usertypes.includes(request.getResponseHeader('exstngtype')) || (request.getResponseHeader('parentid') == '')) {populateElement();$("#profilemailempty").show();
									} else {$("#profilemailempty").hide();showUserData(response);}
								}}
						});
					}}}};
		$("#profileemail1").easyAutocomplete(userEmail);
	});
	function showUserData(user) {
		$("input[name='id']").remove();$('#uname').val(user.fullname);$('#role').select2().val(user.role).trigger('change');/* $('#company').select2().val(user.company).trigger('change'); */
		<c:if test="${not empty centers}">$('#center').select2().val(user.centers).trigger('change');</c:if>
		$('#password').val(user.password);$('#confirmPassword').val(user.password);$('#mobile').val(user.mobilenumber);
		if(user.email) {document.getElementById("password").disabled = true;document.getElementById("confirmPassword").disabled = true;document.getElementById("mobile").type='password';document.getElementById("mobile").disabled = true;
		} else {document.getElementById("password").disabled = false;document.getElementById("confirmPassword").disabled = false;document.getElementById("mobile").type='text';document.getElementById("mobile").disabled = false;}
		$('#numberofclients').val(user.numberofclients);$('#branches').select2().val(user.branch).trigger('change');$('#verticals').select2().val(user.vertical).trigger('change');$('#customers').select2().val(user.customer).trigger('change');
		if(user.isglobal == 'true') {$("#isglobal").prop("checked", true);
		} else {$("#isglobal").prop("checked", false);}
		if(user.disable == 'true') {$("#disable").prop("checked", false);$('#disable').val("true");} else {$("#disable").prop("checked", true);$('#disable').val("false");}
		if(user.addsubuser == 'true') {$("#addsubuser").prop("checked", true);$('#addsubuser').val("true");} else {$("#addsubuser").prop("checked", false);$('#addsubuser').val("false");}
		if(user.addclient == 'true') {$("#adduserclient").prop("checked", true);$('#adduserclient').val("true");} else {$("#adduserclient").prop("checked", false);$('#adduserclient').val("false");}
		if(user.id){$("form[name='userform']").append('<input type="hidden" name="id" value="'+user.id+'">');}
		$("input[name='createdDate']").val(user.cdate);$("input[name='createdBy']").val(user.createdBy);$("form[name='userform']").validator('update');
		currentEmail = user.email;
	}
	function populateElement(userId) {
		$('#profileemail1,#uname,#password,#confirmPassword,#mobile').prop("readonly", false);$('#role').children('option').remove();$('#role').append($("<option></option>").attr("value","").text("-- Select Role --"));
		<c:forEach items="${roles}" var="role">$('#role').append($("<option></option>").attr("value","${role.id}").text("${role.name}"));</c:forEach>
		$('.with-errors').html('');$('.form-group').removeClass('has-error has-danger');$('#subuserEmail_Msg').html('');currentEmail = '';
		$('#uname').val('');$('#role').select2().val('').trigger('change');
		
		var cArray=new Array();
		cArray.push('${client.id}');$('#company').select2().val('').trigger('change');$('#password').val('').prop('readonly', false);	$('#confirmPassword').val('').prop('readonly', false);$('.emailId').val('');$('#mobile').val('');$('#numberofclients').val('');$('#branches').select2().val('').trigger('change');$('#verticals').select2().val('').trigger('change');$('#customers').select2().val('').trigger('change');$("#isglobal").prop("checked", false);$("#disable").prop("checked", true);$("#addsubuser").prop("checked", false);$("#addsubuser").val("false");$("#adduserclient").prop("checked", false);$("#adduserclient").val("false");$("input[name='id']").remove();$("input[name='createdDate']").val('');$("input[name='createdBy']").val('');
		<c:if test="${not empty centers}">$('#center').select2().val('').trigger('change');</c:if>
		if(userId) {
			users.forEach(function(user) {
				if(user.id == userId) {
					$('#profileemail1,#uname,#password,#confirmPassword,#mobile').prop("readonly", true);
					if(user.isglobal == 'true') {$('#role').children('option').remove();$('#role').append($("<option></option>").attr("value","").text("-- Select Role --"));<c:forEach items="${roles}" var="adminrole">$('#role').append($("<option></option>").attr("value","${adminrole.id}").text("${adminrole.name}"));</c:forEach>
					}else{$('#role').children('option').remove();$('#role').append($("<option></option>").attr("value","").text("-- Select Role --"));<c:forEach items="${roles}" var="role">$('#role').append($("<option></option>").attr("value","${role.id}").text("${role.name}"));</c:forEach>}
					$('#uname').val(user.name);$('#role').select2().val(user.role).trigger('change');<c:if test="${not empty client}">$('#company').select2({disabled:'readonly'}).val(user.company).trigger('change')</c:if>;
					<c:if test="${empty client}">$('#company').select2().val(user.company).trigger('change');if(user.company.length == length){$('#all_companies').prop('checked',true);}else{$('#all_companies').prop('checked',false);} </c:if>;<c:if test="${not empty centers}">$('#center').select2().val(user.centers).trigger('change');</c:if>
					$('#password').val(user.password);$('#confirmPassword').val(user.password);$('.emailId').val(user.email);$('#mobile').val(user.mobile);$('#numberofclients').val(user.numberofclients);$('#branches').select2().val(user.branch).trigger('change');$('#verticals').select2().val(user.vertical).trigger('change');$('#customers').select2().val(user.customer).trigger('change');
					if(user.isglobal == 'true') {$("#isglobal").prop("checked", true);} else {$("#isglobal").prop("checked", false);}
					if(user.disable == 'true') {$("#disable").prop("checked", false);$('#disable').val("true");} else {$("#disable").prop("checked", true);$('#disable').val("false");}
					if(user.addsubuser == 'true') {$("#addsubuser").prop("checked", true);$('#addsubuser').val("true");} else {$("#addsubuser").prop("checked", false);$('#addsubuser').val("false");}
					if(user.addclient == 'true') {$("#adduserclient").prop("checked", true);$('#adduserclient').val("true");} else {$("#adduserclient").prop("checked", false);$('#adduserclient').val("false");}
					$("form[name='userform']").append('<input type="hidden" name="id" value="'+user.id+'">');$("input[name='createdDate']").val(user.createdDate);$("input[name='createdBy']").val(user.createdBy);currentEmail = user.email;
				}
			});
		}else{<c:if test="${not empty client}">$('#company').select2({disabled:'readonly'}).val('<c:out value="${client.id}"/>').trigger('change');</c:if>$("#adduserclient").prop("checked", true);$('#adduserclient').val("true");}
		<c:if test="${not empty client}">$('#subuserform').attr("action","${contextPath}/createprofileuserClient/${id}/${fullname}/${usertype}/${month}/${year}");</c:if>
		<c:if test="${empty client}">$('#subuserform').attr("action","${contextPath}/createprofileuser/${id}/${fullname}/${usertype}/${month}/${year}");</c:if>
		
		$("form[name='userform']").validator('update');
	}
</script>
</head>
<body class="body-cls">
<c:choose><c:when test='${not empty client && not empty client.id}'><%@include file="/WEB-INF/views/includes/client_header.jsp" %></c:when><c:otherwise><%@include file="/WEB-INF/views/includes/newclintheader.jsp" %></c:otherwise></c:choose>
<div class="breadcrumbwrap"><div class="container"><div class="row"><div class="col-md-12 col-sm-12"><ol class="breadcrumb"><c:choose><c:when test='${not empty client && not empty client.id}'><li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li><li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li><li class="breadcrumb-item active">Company Profile</li></c:when><c:otherwise><li class="breadcrumb-item"><c:choose><c:when test="${usertype eq userCenter}"><a href="#" class="urllink" link="${contextPath}/cp_centers/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:when><c:otherwise><a href="#" class="urllink" link="${contextPath}/teamuser/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:otherwise></c:choose></li><li class="breadcrumb-item active">Users</li></c:otherwise></c:choose></ol><div class="retresp"></div></div></div></div></div>
<div class="db-ca-wrap"><div class="container"><div class="row">
	<%@include file="/WEB-INF/views/profile/leftnav.jsp" %>
   <div class="col-md-10 col-sm-12 customtable p-0">
                        <table id="dbTable1" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                            <thead><tr><c:if test="${not empty client}"><th></th></c:if><th>User Name</th><th class="text-center">Company</th><th class="text-center">Role</th><th class="text-center">Email ID</th><th class="text-center">Mobile No</th><th class="text-center">Last Logged In</th><th class="text-center">Disabled?</th><th class="text-center">Action</th></tr></thead>
                            <tbody>
								<c:forEach items="${users}" var="user">
								<script type="text/javascript">var user = new Object();user.id = '<c:out value="${user.id}"/>';user.userid = '<c:out value="${user.userid}"/>';user.fullname = '<c:out value="${user.fullname}"/>';user.clientid = '<c:out value="${user.clientid}"/>';user.name = '<c:out value="${user.name}"/>';user.password = '<c:out value="${user.password}"/>';user.company = new Array();<c:if test='${not empty user.company}'><c:forEach items="${user.company}" var="company">user.company.push('${company}');</c:forEach></c:if>user.centers = new Array();<c:if test='${not empty user.centers}'><c:forEach items="${user.centers}" var="center">user.centers.push('${center}');</c:forEach></c:if>user.branch = new Array();<c:if test='${not empty user.branch}'><c:forEach items = "${user.branch}" var="branch">user.branch.push('${branch}');</c:forEach></c:if>user.vertical = new Array();<c:if test='${not empty user.vertical}'><c:forEach items = "${user.vertical}" var="vertical">user.vertical.push('${vertical}');</c:forEach></c:if>user.customer = new Array();<c:if test='${not empty user.customer}'><c:forEach items = "${user.customer}" var="customer">user.customer.push('${customer}');</c:forEach></c:if>user.role = '<c:out value="${user.role}"/>';user.numberofclients = '<c:out value="${user.numberofclients}"/>';user.addedclients = '<c:out value="${user.addedclients}"/>';user.group = '<c:out value="${user.group}"/>';user.email = '<c:out value="${user.email}"/>';user.mobile = '<c:out value="${user.mobile}"/>';user.isglobal = '<c:out value="${user.isglobal}"/>';user.disable = '<c:out value="${user.disable}"/>';user.addsubuser = '<c:out value="${user.addsubuser}"/>';user.addclient = '<c:out value="${user.addclient}"/>';user.createdDate = '<fmt:formatDate pattern="yyyy-MM-dd" value="${user.createdDate}" />';user.createdBy = '<c:out value="${user.createdBy}"/>';users.push(user);</script>
								<tr id="row${user.id}"><c:if test="${not empty client}"><td><span class="imgsize-wrap-thumb1"><c:if test="${not empty client.logoid}"><img src="${contextPath}/getlogo/${client.logoid}" alt="Logo" class="imgsize-thumb" id="clntlogo"  style="float: left;"></c:if><c:if test="${empty client.logoid}"><img src="${contextPath}/static/mastergst/images/master/defaultcompany.png" alt="Logo" class="imgsize-thumb" id="clntlogo"  style="float: left;"></c:if><c:choose><c:when test="${fn:contains(user.name, '(Admin)')}"></c:when><c:otherwise><img src="${contextPath}/static/mastergst/images/master/only-link.png" alt="Logo" class="imgsize-thumb" style="float: left;max-width: 50%;margin-top: 17px;margin-left: 17px;"></c:otherwise></c:choose> </span></td></c:if><td align="left">${user.name}</td><td align="left"><c:set var="contains" value="false" /><c:if test="${not empty client}">${client.businessname}</c:if><c:if test="${empty client}"><c:forEach items="${user.company}" var="company" varStatus="loop"><c:forEach items="${clients}" var="extclient"><c:if test="${extclient.id eq company}"><c:set var="contains" value="true" />${loop.index+1}) ${extclient.businessname}<br/></c:if></c:forEach></c:forEach><c:if test="${contains eq false}">-</c:if></c:if></td><td align="left"><c:choose><c:when test="${empty user.isglobal}"><c:set var="contains" value="false" /><c:forEach items="${roles}" var="role"><c:if test="${role.id eq user.role}"><c:set var="contains" value="true" />${role.name}</c:if>	</c:forEach><c:if test="${contains eq false}">-</c:if></c:when><c:otherwise><c:set var="contains" value="false" /><c:forEach items="${roles}" var="adminrole"><c:if test="${adminrole.id eq user.role}"><c:set var="contains" value="true" />${adminrole.name}</c:if>	</c:forEach><c:if test="${contains eq false}">-</c:if></c:otherwise></c:choose></td><td align="right">${user.email}</td><td align="right">${user.mobile}</td><td>${user.usrLastloggedin}</td><td align="left"><c:choose><c:when test="${not empty user.disable && user.disable eq true}">Yes</c:when><c:otherwise>No</c:otherwise></c:choose></td><c:if test="${not empty client}"><td class="actionicons"><c:choose><c:when test="${empty companyUser}"><c:choose><c:when test="${user.addsubuser eq 'main'}"></c:when><c:otherwise><a class="btn-edt permissionSettings-Users-Edit kjh" href="#" data-toggle="modal" data-target="#editModal" onClick="populateElement('${user.id}')"><i class="fa fa-edit"></i> </a><c:if test="${not empty client}"><a href="#" class="btn-edt permissionSettings-Users-Delete"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" onClick="showDeletePopup('${user.id}','${user.email}','${client.id}')" alt="Delete" style="margin-top: -6px;"></a></c:if></c:otherwise></c:choose></c:when><c:otherwise><c:choose><c:when test="${user.addsubuser eq 'main'}"></c:when><c:otherwise><a class="btn-edt permissionSettings-Users-Edit kjh" href="#" data-toggle="modal" data-target="#editModal" onClick="populateElement('${user.id}')"><i class="fa fa-edit"></i> </a><c:if test="${not empty client}"><a href="#" class="btn-edt permissionSettings-Users-Delete"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" onClick="showDeletePopup('${user.id}','${user.email}','${client.id}')" alt="Delete" style="margin-top: -6px;"></a></c:if></c:otherwise></c:choose></c:otherwise></c:choose></td></c:if><c:if test="${empty client}"><td class="actionicons"><a class="btn-edt permissionSettings-Users-Edit hjjh" href="#" data-toggle="modal" data-target="#editModal" onClick="populateElement('${user.id}')"><i class="fa fa-edit"></i> </a><a href="#" class="btn-edt permissionSettings-Users-Delete"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" onClick="showAdminUserDeletePopup('${user.id}')" alt="Delete" style="margin-top: -6px;"></a></td></c:if></tr>
								</c:forEach>
                            </tbody>
                        </table>
                    </div></div></div></div>
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <div class="modal fade" id="editModal" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
				<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span></button>
                    <div class="bluehdr" style="width:100%"><h3>USERS</h3></div>
				</div>
                <div class="modal-body meterialform popupright bs-fancy-checks">
                    <form:form method="POST" data-toggle="validator" class="meterialform" id="subuserform" name="userform" action="${contextPath}/createprofileuser/${id}/${fullname}/${usertype}/${month}/${year}" modelAttribute="user">
					<div class="row  p-5" style="padding-top:1rem!important">
					<div class="errormsg col-md-12" id="subuserEmail_Msg" style="height:36px;font-size:14px!important"></div>
                        <div class="form-group col-md-6 col-sm-12" id="subuseremail"><p class="lable-txt astrich">Email</p><input type="text" id="profileemail1" class="emailId" name="email" required="required" data-error="Please enter valid email address" placeholder="john@gmail.com" value="" pattern='^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$' onChange="userEmailCheck()" data-invalidemail="invalidemail" data-invalidemail-error="Email Id already exists" /><label for="email" class="control-label"></label> <div class="help-block with-errors"></div><div id="profilemailempty" style="display:none"><div class="ddbox"><p>Email Id already exists.</p></div></div><i class="bar"></i></div>				
						<div class="form-group col-md-6 col-sm-12"><p class="lable-txt astrich">User Name</p><span class="errormsg" id="userName_Msg"></span><input type="text" id="uname" name="name" data-minlength="3" required="required" data-error="Please enter more than 3 characters" placeholder="Name" value=""  /><label for="input" class="control-label"></label><div class="help-block with-errors"></div><i class="bar"></i></div>
                        <div class="form-group col-md-6 col-sm-12"><p class="lable-txt astrich">Password </p><span class="errormsg" id="password_Msg"></span><input type="password" id="password" name="password" data-minlength="6" required="required" data-error="Please enter more than 6 characters" placeholder="******" value="" /><label for="input" class="control-label"></label><div class="help-block with-errors"></div><i class="bar"></i></div>
						<div class="form-group col-md-6 col-sm-12"><p class="lable-txt astrich">Confirm Password</p><span class="errormsg" id="confirmPassword_Msg"></span><input type="password" id="confirmPassword" name="confirmPassword" data-minlength="6" required="required" data-match="#password" data-match-error="Passwords doesn't match" placeholder="******" value="" /><label for="input" class="control-label"></label><div class="help-block with-errors"></div><i class="bar"></i></div>
						<div class="form-group col-md-6 col-sm-12"><p class="lable-txt astrich">Mobile</p><span class="errormsg" id="date_Msg"></span><input type="Tel" name="mobile" id="mobile" required="required" data-minlength="10" maxlength="10" pattern="[0-9]+" data-error="Please enter valid mobile number" placeholder=" " value="" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))"/><label for="input" class="control-label"></label><div class="help-block with-errors"></div><i class="bar"></i></div>								
						<div class="form-group col-md-6 col-sm-12"><p class="lable-txt">Role</p><span class="errormsg" id="role_Msg"></span><select id="role" class="mt-1" name="role" value="" onChange="selectRoleid(this)"><option value=""> - Select - </option><c:forEach items="${roles}" var="role"><option value="${role.id}">${role.name}</option></c:forEach></select><label for="input" class="control-label"></label><i class="bar"></i> </div>
                      	<div class="form-group col-sm-12" id="acknwldg_Div"><span class="errormsg" id="acknwldg_Msg"></span></div>
                        <div class="form-group col-md-6 col-sm-12"><p class="lable-txt">Company</p><c:if test='${empty client || empty client.id}'><span><div class="form-check mb-2 mb-sm-0 pull-right"><div class="meterialform"><div class="checkbox"><label class="companieslist"><input type="checkbox" id="all_companies" value="1"><i class="helper che-box" id="allcompanies" name="comapny_checkbox"></i> Select all companies</label> </div></div></div></span></c:if> <span class="errormsg" id="company_Msg"></span><select id="company" class="mt-1" name="company" onChange="selectCompanyName(this)" multiple value="" ><c:forEach items="${clients}" var="client"><option value="${client.id}">${client.businessname} - ${client.gstnnumber}</option></c:forEach> </select><label for="input" class="control-label"></label><i class="bar"></i> </div>
                        <div class="form-group col-md-6 col-sm-12"><p class="lable-txt">Branch</p><span class="errormsg" id="branch_Msg"></span><select id="branches" class="mt-1" class="userbranch" name="branch" multiple value="" ></select><label for="input" class="control-label"></label><i class="bar"></i></div><div class="form-group col-md-6 col-sm-12"><p class="lable-txt">Vertical</p><span class="errormsg" id="branch_Msg"></span><select id="verticals" class="mt-1" class="uservertical" name="vertical" multiple value="" ></select><label for="input" class="control-label"></label><i class="bar"></i></div>
                        <div class="form-group col-md-6 col-sm-12"><p class="lable-txt">Customer</p><span class="errormsg" id="customer_Msg"></span><select id="customers" class="mt-1" class="userCustomer" name="customer" multiple value="" ></select><label for="input" class="control-label"></label><i class="bar"></i></div> 
						<c:if test="${not empty centers}"><div class="form-group col-md-6 col-sm-12"><p class="lable-txt">Centers</p><span class="errormsg" id="center_Msg"></span><select id="center" class="mt-1" name="centers" multiple value="" ><c:forEach items="${centers}" var="center"><option value="${center.id}">${center.name}</option></c:forEach> </select><label for="input" class="control-label"></label><i class="bar"></i> </div></c:if>
						<div class="col-md-12 mt-1"><span style="font-size:14px;font-weight:bold;">Enable Access For : </span></div>
						<div class="form-group col-md-6 col-sm-12 mt-3"> <span class="labletxt" style="margin-top: 0px; margin-right: 5px;">Add User Access : </span><input class="form-check-input" type="checkbox" id="addsubuser" name="addsubuser" value="false" /><label for="addsubuser"><span class="ui"></span></label><br><p style="font-size:12px;">(On Enable this option, User will have an access to add Users for this Company and on Disable add Users access is restricted.)</p></div>
						<div class="form-group col-md-6 col-sm-12 mt-3"> <span class="labletxt" style="margin-top: 0px; margin-right: 5px;">Add <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Client</c:when><c:otherwise>Business</c:otherwise></c:choose> Access : </span><input class="form-check-input" type="checkbox" id="adduserclient" name="addclient" value="false" /><label for="adduserclient"><span class="ui"></span></label><br><p style="font-size:12px;">(On Enable this option, User will have an access to add <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Client</c:when><c:otherwise>Business</c:otherwise></c:choose> and on Disable add Add <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Client</c:when><c:otherwise>Business</c:otherwise></c:choose> access is restricted.)</p></div>
						<div style="display:inline-flex; margin-top:15px;margin-left:15px"><div class="form-group col-md-6 col-sm-12" style="padding:0px!important"> <span class="labletxt" style="margin-top: 0px; margin-right: 5px;">Enable Access to this User : </span><input class="form-check-input" type="checkbox" id="disable" name="disable" value="false" /><label for="disable"><span class="ui"></span></label><br><p style="font-size:12px;">(On Enable this option, User will have an access to Company and on Disable User Company access is restricted.)</p></div></div><div class="bdr-b col-12" style="display:block">&nbsp;</div>
                        <div class="col-12 text-center mt-3" style="display:none"><input type="hidden" name="userid" value="<c:out value="${id}"/>"><input type="hidden" name="fullname" value="<c:out value="${fullname}"/>"><c:if test='${not empty client && not empty client.id}'><input type="hidden" name="clientid" value="<c:out value="${client.id}"/>"></c:if><input type="hidden" name="createdDate" value='<fmt:formatDate pattern="yyyy-MM-dd" value="${user.createdDate}" />'><input type="hidden" name="createdBy" value="${user.createdBy}"><input type="submit" class="btn btn-blue-dark users_submit" style="display:none" value="Save"/><a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close">Cancel</a></div>
                    </div>
					</form:form>
                </div>
				<div class="modal-footer text-center" style="display:block">
				<label for="users_submit" class="btn btn-blue-dark m-0 userslabbut" tabindex="0" >Save</label>
				<a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close" onClick="test()">Cancel</a>
				</div>
            </div>
        </div>
    </div>
    <jsp:include page="/WEB-INF/views/profile/deletepopup.jsp" />
	<c:choose><c:when test='${not empty client && not empty client.id}'><script type="text/javascript">$(document).ready(function() {$('#nav-client').addClass('active');$('#cp_center').css("display","none");$('#cp_Allcenter').css("display","none");$('#cp_centerFiling').css("display","none");	$('#cp_centerClinet').css("display","none");}); </script></c:when><c:otherwise><script type="text/javascript">$(document).ready(function() {$('#cp_center').css("display","block");	$('#cp_Allcenter').css("display","block");$('#cp_centerFiling').css("display","block");	$('#cp_centerClinet').css("display","block");$('#nav-team').addClass('active');}); </script></c:otherwise></c:choose>
</body>
</html>