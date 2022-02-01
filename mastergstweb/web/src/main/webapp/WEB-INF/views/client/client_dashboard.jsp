<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Client Dashboard</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/common/jquery.tablednd.0.7.min.js" type="text/javascript"></script>
<style>.drop_work:hover .work_menu{display:block}</style>
</head>
<body>
<%@include file="/WEB-INF/views/includes/client_header.jsp" %>
<!--- breadcrumb start -->
 
<div class="breadcrumbwrap">
<div class="container">
	<div class="row">
        <div class="col-md-12 col-sm-12">
				<ol class="breadcrumb">
					<li class="breadcrumb-item active"><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></li>
				</ol>
				<span id="errorMessage" class="ml-4" style="color:red;font-size:12px;font-weight:bold;"></span>
				<span class="datetimetxt"> 
					<input type="text" class="form-control" id="datetimepicker" /><i class="fa fa-sort-desc"></i>  
				</span>
				<span class="f-14-b pull-right mt-1 font-weight-bold">
					Return Period: 
				</span>
				<div class="retresp"></div>
			</div>
		</div>
	</div>
</div>

<!--- breadcrumb end -->
  <div class="db-ca-wrap cilent-db-ca-wrap">
    <div class="container">
      <div class="row">
        <!-- dashboard ca table begin -->
        <div class="col-sm-9 col-md-9 client-info">
		<div id="all_client">
          <table id="dbTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
            <thead>
              <tr>
                <th> Business / Client Name</th>
				<th> Group Name</th>
				<th class="text-center">State</th>
                <th class="text-center">Mismatches</th>
                <!-- <th class="text-center">Stage</th> -->
                
                <th class="text-center">Action</th>
              </tr>
            </thead>
            <tbody>
			 <c:forEach items="${lClient}" var="client">
			   <c:set var = "varfn" value = "${client.firstname}"/>
			   <c:set var = "varln" value = "${client.lastname}"/>
			   <c:set var = "brnchln" value = "0"/>
			   <c:set var = "verticalln" value = "0"/>
			   <c:set var="contains" value="false" />
			   <c:set var="clientaccess" value="false" />
			   <c:forEach var="item" items="${companyUser.company}">
					<c:if test="${item eq client.id}">
						<c:set var="contains" value="true" />
						<c:if test='${not empty companyUser.disable}'>
							<c:if test='${companyUser.disable eq "true"}'>
								<c:set var="clientaccess" value="true" />
							</c:if>
						</c:if>
			    	</c:if>
				</c:forEach>
				<c:set var="hypenAccessSales" value=""/>
				<c:set var="hypenAccessPurchase" value=""/>
				<c:set var="hypenAccessGSTR2" value=""/>
				<c:set var="hypenAccessGSTR2A" value=""/>
				<c:set var="hypenAccessMisMatched" value=""/>
				<c:set var="hypenAccessGSTR3B" value=""/>
				<c:set var="hypenAccessGSTR4" value=""/>
				<c:set var="hypenAccessGSTR5" value=""/>
				<c:set var="hypenAccessGSTR6" value=""/>
				<c:set var="hypenAccessGSTR9" value=""/>
				<c:set var="hypenAccessGSTR4Annual" value=""/>
				<c:set var="hypenAccessGSTR6" value=""/>
				<c:set var="hypenAccessGSTR8" value=""/>
				<c:set var="hypenAccessEinvoice" value=""/>
				<c:set var="hypenAccessEwaybill" value=""/>
				<c:set var="hypenAccessImportTemplates" value=""/>
				<c:set var="hypenAccessBulkImports" value=""/>
				<c:if test="${contains}">
					<c:forEach var="role" items="${dbroles}">
				 		<c:if test="${role.clientid eq client.id}">
				 			<c:forEach var="rolename" items="${role.roles}">
							 	<c:if test="${rolename eq 'Sales'}"> <c:set var="hypenAccessSales" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'Purchase'}"> <c:set var="hypenAccessPurchase" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR2'}"> <c:set var="hypenAccessGSTR2" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR2A'}"> <c:set var="hypenAccessGSTR2A" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'MisMatched'}"> <c:set var="hypenAccessMisMatched" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR3B'}"> <c:set var="hypenAccessGSTR3B" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR4'}"> <c:set var="hypenAccessGSTR4" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR5'}"> <c:set var="hypenAccessGSTR5" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR6'}"> <c:set var="hypenAccessGSTR6" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR9'}"> <c:set var="hypenAccessGSTR9" value="d-none"/> </c:if>
							 	
							 	<c:if test="${rolename eq 'GSTR4Annual'}"> <c:set var="hypenAccessGSTR4Annual" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR6'}"> <c:set var="hypenAccessGSTR6" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR8'}"> <c:set var="hypenAccessGSTR8" value="d-none"/> </c:if>
							 	
							 	<c:if test="${rolename eq 'Einvoice'}"> <c:set var="hypenAccessEinvoice" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'Ewaybill'}"> <c:set var="hypenAccessEwaybill" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'Import Templates'}"> <c:set var="hypenAccessImportTemplates" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'Bulk Imports'}"> <c:set var="hypenAccessBulkImports" value="d-none"/> </c:if>
							 </c:forEach>
						</c:if>
			 		</c:forEach>
				</c:if>
			   <c:choose>
					<c:when test="${not empty companyUser}">
						<c:if test='${not empty companyUser.branch}'>
							<c:forEach items = "${companyUser.branch}" var="branch">
								<c:forEach items="${client.branches}" var="branchs">
									<c:if test='${branch eq branchs.name}'>
										<c:set var = "brnchln" value = "${brnchln + 1}"/>
									</c:if>
								</c:forEach>
							</c:forEach>
						</c:if>
						<c:if test='${not empty companyUser.vertical}'>
							<c:forEach items = "${companyUser.vertical}" var="vertical">
								<c:forEach items="${client.verticals}" var="verticals">
									<c:if test='${vertical eq verticals.name}'>
										<c:set var = "verticalln" value = "${verticalln + 1}"/>
									</c:if>
								</c:forEach>
							</c:forEach>
						</c:if>
					</c:when>
					<c:otherwise>
						<c:forEach items="${client.branches}" var="branch">
							<c:set var = "brnchln" value = "${brnchln + 1}"/>
						</c:forEach>
						<c:forEach items="${client.verticals}" var="verticals">
							<c:set var = "verticalln" value = "${verticalln + 1}"/>
						</c:forEach>
					</c:otherwise>
				</c:choose>	
				<tr <c:if test="${clientaccess eq 'true'}"> data-toggle="tooltip" title="Your Access to <c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose>- ${client.gstnnumber} is restricted, Please contact your admin user for further assistance."</c:if>>										
                <td><span class="imgsize-wrap-thumb1"><c:if test="${not empty client.logoid}"><img src="${contextPath}/getlogo/${client.logoid}" alt="Logo" class="imgsize-thumb" id="clntlogo"  style="float: left;"></c:if>
							<c:if test="${empty client.logoid}"><img src="${contextPath}/static/mastergst/images/master/defaultcompany.png" alt="Logo" class="imgsize-thumb" id="clntlogo"  style="float: left;"></c:if><c:if test="${contains}"><img src="${contextPath}/static/mastergst/images/master/only-link.png" alt="Logo" class="imgsize-thumb" style="float: left;max-width: 50%;margin-top: 17px;margin-left: 17px;"></c:if> </span>
								<%-- <a class="permissionSettings-Company_Details-View-Settings-Company_Details" style="display:none;"><span class="compname" title="${client.businessname}" style="margin-top: -3px;padding:5px;"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose><span class="caption">${client.signatoryName}</span></span></a> --%>
								<c:if test="${clientaccess eq 'false'}"><a href="#" class="urllink" link="${contextPath}/about/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>"><span class="compname" title="${client.businessname}" style="margin-top: -3px;padding:5px;"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose><span class="caption">${client.signatoryName}</span></span></a></c:if><c:if test="${clientaccess eq 'true'}"><span class="compname" title="${client.businessname}" style="margin-top: -3px;padding:5px;"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose><span class="caption">${client.signatoryName}</span></span></c:if></td>
                <td align="center"  class="boldtxt" style="white-space: nowrap">${client.groupName}</td>
			    <td align="right"  class="boldtxt" style="white-space: nowrap">${client.statename}<span class="caption">${client.gstnnumber}</span></td>          
				<%-- <td align="center"><a href="#" class="boldtxt urllink idMisMatch${client.id} permissionGSTR2-MisMatched">${client.mismatches}</a></td> --%>
				<td align="center"><c:if test="${clientaccess eq 'false'}"><a href="#" class="boldtxt urllink idMisMatch${client.id}">${client.mismatches}</a></c:if><c:if test="${clientaccess eq 'true'}"><a href="#" class="boldtxt idMisMatch${client.id}">${client.mismatches}</a></c:if></td>
                <!-- <td align="center"><c:if test="${clientaccess eq 'false'}"><a href="#" id="idStage${client.id}" class="boldtxt urllink idStage${client.id}"></a></c:if><c:if test="${clientaccess eq 'true'}"><a href="#" id="idStage${client.id}" class="boldtxt idStage${client.id}"></a></c:if></td> -->
                <td class="actionicons"><c:if test="${clientaccess eq 'false'}"><div class="drop_work btn-group">
				  <a class="btn btn-blue" href="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/${month}/${year}/?type=initial">Work on this</a>
				  <button class="btn btn-blue dropdown-toggle caret_drop" data-toggle="dropdown"><span class="caret"></span></button>
				  <ul class="dropdown-menu work_menu"> <!-- dropdown menu links --><li class="dropdown-submenu"><a class="test" tabindex="-1" href="#">Books <span class="caret"></span></a><c:set var="contains" value="gstr1" /><c:if test='${not empty client.returnsSummary}'><c:forEach items="${client.returnsSummary}" var="GSTReturnsSummury"><c:if test='${GSTReturnsSummury.active == "true"}'><c:choose><c:when test="${GSTReturnsSummury.returntype eq varGSTR1 || GSTReturnsSummury.returntype eq varGSTR3B || GSTReturnsSummury.returntype eq varGSTR2}"><c:set var="contains" value="gstr1" /></c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR8}"><c:set var="contains" value="gstr8" /></c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR4}"><c:set var="contains" value="gstr4" />	</c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR5}"><c:set var="contains" value="gstr5" /></c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR6}"><c:set var="contains" value="gstr6" /></c:when></c:choose></c:if></c:forEach></c:if><ul class="dropdown-menu sub_menu"><c:if test="${contains ne 'gstr4' && contains ne 'gstr5' && contains ne 'gstr6'}">
				  	<li class="${hypenAccessSales}"><a  tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/SalesRegister/${month}/${year}/?type=">Sales Register</a></li>				  	
				  	<li class="${hypenAccessPurchase}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/PurchaseRegister/${month}/${year}/?type=">Purchase Register</a></li>
				  	<li class=""><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/PurchaseRegister/${month}/${year}/?type=Exp">Expenses</a></li>
					<li><a tabindex="-1" href="${contextPath}/journaldetails/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/${month}/${year}/?type=Monthly">Journals</a></li>
					</c:if><c:if test="${contains == 'gstr4'}"><li class="${hypenAccessSales}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<%=MasterGSTConstants.GSTR4%>/${month}/${year}/?type=">Sales Register</a></li></c:if><c:if test="${contains == 'gstr5'}"><li class="${hypenAccessSales}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<%=MasterGSTConstants.GSTR5%>/${month}/${year}/?type=">Sales Register</a></li></c:if><c:if test="${contains == 'gstr6'}"><li class="${hypenAccessPurchase}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/PurchaseRegister/${month}/${year}/?type=">Purchase Register</a></li></c:if></ul></li>
				    <li class="dropdown-submenu1"><a class="test" tabindex="-1" href="#">GST<span class="caret"></span></a><ul class="dropdown-menu sub_menu1"><c:if test='${not empty client.returnsSummary}'><c:forEach items="${client.returnsSummary}" var="GSTReturnsSummury"><c:if test='${GSTReturnsSummury.active == "true"}'><c:choose><c:when test="${GSTReturnsSummury.returntype eq varGSTR1 }">
				      
				    <li class="${hypenAccessSales}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=">${GSTReturnsSummury.returntype}</a></li>
				    </c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR3B }">
				    	<li class="${hypenAccessGSTR3B}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=">${GSTReturnsSummury.returntype}</a></li>
					</c:when>
					<c:when test="${GSTReturnsSummury.returntype eq varGSTR8}">
						<li class="${hypenAccessGSTR8}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/GSTR8/${month}/${year}/?type=">GSTR8</a></li>
					</c:when>
					<c:when test="${GSTReturnsSummury.returntype eq varGSTR4 }"><li class="${hypenAccessGSTR4}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=">${GSTReturnsSummury.returntype}</a></li></c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR5 }"><li class="${hypenAccessGSTR5}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=">${GSTReturnsSummury.returntype}</a></li></c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR6 }"><li class="${hypenAccessGSTR6}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=">${GSTReturnsSummury.returntype}</a></li>
					</c:when><c:when test="${GSTReturnsSummury.returntype == 'GSTR9' }"><li class="${hypenAccessGSTR9}"><a tabindex="-1" href="${contextPath}/addAnnualinvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/${month}/${year}">${GSTReturnsSummury.returntype}</a></li></c:when>
					<c:when test="${GSTReturnsSummury.returntype eq varGSTR2}"><li class="${hypenAccessGSTR2}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=dwnld">${GSTReturnsSummury.returntype}</a></li><li class="${hypenAccessGSTR2A}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=dwnldgstr2a">GSTR2A</a></li><li class="${hypenAccessMisMatched}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=mmtch">RECONCILE</a></li></c:when></c:choose></c:if></c:forEach></c:if></ul></li>
				      <li class="dropdown-submenu2"><a class="test" tabindex="-1" href="#">Import<span class="caret"></span></a><ul class="dropdown-menu sub_menu2"><%--<li><a tabindex="-1" href="#" data-toggle="modal" data-target="#importModal" onclick="updateImportModal('<%=MasterGSTConstants.GSTR1%>')">Sales Invoices</a></li> <li><a tabindex="-1" href="#" data-toggle="modal" data-target="#importModal" onclick="updateImportModal('Purchase Register')">Purchase Invoices</a> </li>--%><li class="${hypenAccessImportTemplates}"><a tabindex="-1" href="${contextPath}/imports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/${month}/${year}/?type=tax">Import Template</a></li><li class="${hypenAccessBulkImports}"><a tabindex="-1" href="${contextPath}/bulkimports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/${month}/${year}/?type=tax">Bulk imports</a></li></ul></li>
				      <li><a tabindex="-1" href="${contextPath}/dreports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/${month}/${year}">Reports</a></li>
				      <li class="dropdown-submenu3"><a class="test" tabindex="-1" href="#">Payment<span class="caret"></span></a><ul class="dropdown-menu sub_menu3"><li><a tabindex="-1" href="${contextPath}/payments_history/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<%=MasterGSTConstants.GSTR1%>/${month}/${year}/?type=receive">Receipts</a></li><li><a tabindex="-1" href="${contextPath}/payments_history/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<%=MasterGSTConstants.GSTR2%>/${month}/${year}/?type=made">Payments</a></li></ul></li>
					 <li class="${hypenAccessEwaybill}"><a tabindex="-1" href="${contextPath}/ewaybill/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<%=MasterGSTConstants.EWAYBILL%>/<c:out value="${client.id}"/>/${month}/${year}">EwayBill</a></li>
				  	 <li class="ideinvoice ${hypenAccessEinvoice}"><a tabindex="-1" href="${contextPath}/einvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<%=MasterGSTConstants.EINVOICE%>/<c:out value="${client.id}"/>/${month}/${year}">E-Invoice</a></li>
				  </ul>
				</div> </c:if><c:if test="${clientaccess eq 'true'}"><a href="#" class="btn btn-blue">Work on this</a></c:if></td>
              </tr>
	            </c:forEach>
                
            </tbody>
          </table>
		  </div>
		  <div id="inactive_clients" style="display:none">
		   <table id="dbTable1" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
            <thead>
              <tr>
                <th> Business / Client Name</th>
				<th> Group Name</th>
				<th class="text-center">State</th>
                <th class="text-center">Mismatches</th>
               <!--<th class="text-center">Stage</th> -->
                
                <th class="text-center">Action</th>
              </tr>
            </thead>
            <tbody>
			 <c:forEach items="${lClient}" var="client">
			  <c:if test="${client.active ne false}">
			   <c:set var = "varfn" value = "${client.firstname}"/>
			   <c:set var = "varln" value = "${client.lastname}"/>
			   <c:set var = "brnchln" value = "0"/>
			   <c:set var = "verticalln" value = "0"/>
			   <c:set var="contains" value="false" />
			   <c:set var="clientaccess" value="false" />
			   <c:forEach var="item" items="${companyUser.company}">
					<c:if test="${item eq client.id}">
						<c:set var="contains" value="true" />
			    	</c:if>
			    	<c:if test='${not empty companyUser.disable}'>
							<c:if test='${companyUser.disable eq "true"}'>
								<c:set var="clientaccess" value="true" />
							</c:if>
						</c:if>
				</c:forEach>
				<c:set var="hypenAccessSales" value=""/>
				<c:set var="hypenAccessPurchase" value=""/>
				<c:set var="hypenAccessGSTR2" value=""/>
				<c:set var="hypenAccessGSTR2A" value=""/>
				<c:set var="hypenAccessMisMatched" value=""/>
				<c:set var="hypenAccessGSTR3B" value=""/>
				<c:set var="hypenAccessGSTR4" value=""/>
				<c:set var="hypenAccessGSTR5" value=""/>
				<c:set var="hypenAccessGSTR6" value=""/>
				<c:set var="hypenAccessGSTR9" value=""/>
				<c:set var="hypenAccessGSTR4Annual" value=""/>
				<c:set var="hypenAccessGSTR8" value=""/>
				<c:set var="hypenAccessEinvoice" value=""/>
				<c:set var="hypenAccessEwaybill" value=""/>
				<c:set var="hypenAccessImportTemplates" value=""/>
				<c:set var="hypenAccessBulkImports" value=""/>
				<c:if test="${contains}">
					<c:forEach var="role" items="${dbroles}">
				 		<c:if test="${role.clientid eq client.id}">
				 			<c:forEach var="rolename" items="${role.roles}">
							 	<c:if test="${rolename eq 'Sales'}"> <c:set var="hypenAccessSales" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'Purchase'}"> <c:set var="hypenAccessPurchase" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR2'}"> <c:set var="hypenAccessGSTR2" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR2A'}"> <c:set var="hypenAccessGSTR2A" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'MisMatched'}"> <c:set var="hypenAccessMisMatched" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR3B'}"> <c:set var="hypenAccessGSTR3B" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR4'}"> <c:set var="hypenAccessGSTR4" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR5'}"> <c:set var="hypenAccessGSTR5" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR6'}"> <c:set var="hypenAccessGSTR6" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR9'}"> <c:set var="hypenAccessGSTR9" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR4Annual'}"> <c:set var="hypenAccessGSTR4Annual" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR8'}"> <c:set var="hypenAccessGSTR8" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'Einvoice'}"> <c:set var="hypenAccessEinvoice" value="d-none"/> </c:if>
								<c:if test="${rolename eq 'Ewaybill'}"> <c:set var="hypenAccessEwaybill" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'Import Templates'}"> <c:set var="hypenAccessImportTemplates" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'Bulk Imports'}"> <c:set var="hypenAccessBulkImports" value="d-none"/> </c:if>
							 </c:forEach>
						</c:if>
			 		</c:forEach>
				</c:if>
			   <c:choose>
					<c:when test="${not empty companyUser}">
						<c:if test='${not empty companyUser.branch}'>
							<c:forEach items = "${companyUser.branch}" var="branch">
								<c:forEach items="${client.branches}" var="branchs">
									<c:if test='${branch eq branchs.name}'>
										<c:set var = "brnchln" value = "${brnchln + 1}"/>
									</c:if>
								</c:forEach>
							</c:forEach>
						</c:if>
						<c:if test='${not empty companyUser.vertical}'>
							<c:forEach items = "${companyUser.vertical}" var="vertical">
								<c:forEach items="${client.verticals}" var="verticals">
									<c:if test='${vertical eq verticals.name}'>
										<c:set var = "verticalln" value = "${verticalln + 1}"/>
									</c:if>
								</c:forEach>
							</c:forEach>
						</c:if>
					</c:when>
					<c:otherwise>
						<c:forEach items="${client.branches}" var="branch">
							<c:set var = "brnchln" value = "${brnchln + 1}"/>
						</c:forEach>
						<c:forEach items="${client.verticals}" var="verticals">
							<c:set var = "verticalln" value = "${verticalln + 1}"/>
						</c:forEach>
					</c:otherwise>
				</c:choose>		
				<tr <c:if test="${clientaccess eq 'true'}"> data-toggle="tooltip" title="Your Access to <c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose>- ${client.gstnnumber} is restricted, Please contact your admin user for further assistance.""</c:if>>										
                <td><span class="imgsize-wrap-thumb1"><c:if test="${not empty client.logoid}"><img src="${contextPath}/getlogo/${client.logoid}" alt="Logo" class="imgsize-thumb" id="clntlogo"  style="float: left;"></c:if>
<c:if test="${empty client.logoid}"><img src="${contextPath}/static/mastergst/images/master/defaultcompany.png" alt="Logo" class="imgsize-thumb" id="clntlogo"  style="float: left;"></c:if> </span>
	<%-- <a  style="display:none;"><span class="compname" title="${client.businessname}" style="margin-top: -3px;padding:5px;"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose><span class="caption">${client.signatoryName}</span></span></a> --%>
	<c:if test="${clientaccess eq 'false'}"><a href="#" class="urllink" link="${contextPath}/about/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>"><span class="compname" title="${client.businessname}" style="margin-top: -3px;padding:5px;"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose><span class="caption">${client.signatoryName}</span></span></a></c:if><c:if test="${clientaccess eq 'true'}"><span class="compname" title="${client.businessname}" style="margin-top: -3px;padding:5px;"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose><span class="caption">${client.signatoryName}</span></span></c:if></td>
                <td align="center"  class="boldtxt" style="white-space: nowrap">${client.groupName}</td>
				<td align="center"  class="boldtxt" style="white-space: nowrap">${client.statename}</td>              
				
                <td align="center"><c:if test="${clientaccess eq 'false'}"><a href="#" class="boldtxt urllink idMisMatch${client.id}">${client.mismatches}</a></c:if><c:if test="${clientaccess eq 'true'}"><a href="#" class="boldtxt idMisMatch${client.id}">${client.mismatches}</a></c:if></td>
                <!-- <td align="center"><c:if test="${clientaccess eq 'false'}"><a href="#" id="idStage${client.id}" class="boldtxt urllink idStage${client.id}"></a></c:if><c:if test="${clientaccess eq 'true'}"><a href="#" id="idStage${client.id}" class="boldtxt idStage${client.id}"></a></c:if></td> -->
                <td class="actionicons"><c:if test="${clientaccess eq 'false'}"><div class="drop_work btn-group">
				  <a class="btn btn-blue" href="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/${month}/${year}/?type=initial">Work on this</a>
				  <button class="btn btn-blue dropdown-toggle caret_drop" data-toggle="dropdown"><span class="caret"></span></button>
				  <ul class="dropdown-menu work_menu"> <!-- dropdown menu links -->
				  <li class="dropdown-submenu"><a class="test" tabindex="-1" href="#">Books <span class="caret"></span></a><c:set var="contains" value="gstr1" /><c:if test='${not empty client.returnsSummary}'><c:forEach items="${client.returnsSummary}" var="GSTReturnsSummury"><c:if test='${GSTReturnsSummury.active == "true"}'><c:choose><c:when test="${GSTReturnsSummury.returntype eq varGSTR1 || GSTReturnsSummury.returntype eq varGSTR3B || GSTReturnsSummury.returntype eq varGSTR2}"><c:set var="contains" value="gstr1" /></c:when>
				  <c:when test="${GSTReturnsSummury.returntype eq varGSTR4}"><c:set var="contains" value="gstr4" />	</c:when>
				  <c:when test="${GSTReturnsSummury.returntype eq varGSTR5}"><c:set var="contains" value="gstr5" />
				  </c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR6}"><c:set var="contains" value="gstr6" /></c:when>
				  </c:choose></c:if></c:forEach></c:if><ul class="dropdown-menu sub_menu"><c:if test="${contains ne 'gstr4' && contains ne 'gstr5' && contains ne 'gstr6'}">
				  <li class="${hypenAccessSales}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/SalesRegister/${month}/${year}/?type=">Sales Register</a></li>
				  <li class="${hypenAccessPurchase}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/PurchaseRegister/${month}/${year}/?type=">Purchase Register</a></li>
				  <li><a tabindex="-1" href="${contextPath}/journaldetails/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/${month}/${year}/?type=Monthly">Journals</a></li></c:if>
				  <c:if test="${contains == 'gstr4'}"><li class="${hypenAccessSales}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<%=MasterGSTConstants.GSTR4%>/${month}/${year}/?type=">Sales Register</a></li></c:if><c:if test="${contains == 'gstr5'}"><li class="${hypenAccessSales}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<%=MasterGSTConstants.GSTR5%>/${month}/${year}/?type=">Sales Register</a></li></c:if><c:if test="${contains == 'gstr6'}"><li class="${hypenAccessPurchase}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/PurchaseRegister/${month}/${year}/?type=">Purchase Register</a></li></c:if></ul></li>
				      <li class="dropdown-submenu1"><a class="test" tabindex="-1" href="#">GST<span class="caret"></span></a><ul class="dropdown-menu sub_menu1"><c:if test='${not empty client.returnsSummary}'><c:forEach items="${client.returnsSummary}" var="GSTReturnsSummury"><c:if test='${GSTReturnsSummury.active == "true"}'><c:choose><c:when test="${GSTReturnsSummury.returntype eq varGSTR1 }"><li class="${hypenAccessSales}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=">${GSTReturnsSummury.returntype}</a></li></c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR3B }"><li class="${hypenAccessGSTR3B}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=">${GSTReturnsSummury.returntype}</a></li></c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR8}"><li class="${hypenAccessGSTR8}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=">${GSTReturnsSummury.returntype}</a></li></c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR4 }"><li class="${hypenAccessGSTR4}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=">${GSTReturnsSummury.returntype}</a></li></c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR5 }"><li class="${hypenAccessGSTR5}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=">${GSTReturnsSummury.returntype}</a></li></c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR6 }"><li class="${hypenAccessGSTR6}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=">${GSTReturnsSummury.returntype}</a></li></c:when><c:when test="${GSTReturnsSummury.returntype == 'GSTR9' }"><li class="${hypenAccessGSTR9}"><a tabindex="-1" href="${contextPath}/addAnnualinvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/${month}/${year}">${GSTReturnsSummury.returntype}</a></li></c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR2}"><li class="${hypenAccessGSTR2}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=dwnld">${GSTReturnsSummury.returntype}</a></li><li class="${hypenAccessGSTR2A}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=dwnldgstr2a">GSTR2A</a></li><li class="${hypenAccesshypenAccessMisMatched}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=mmtch">RECONCILE</a></li></c:when></c:choose></c:if></c:forEach></c:if></ul></li>
				      <li class="dropdown-submenu2"><a class="test" tabindex="-1" href="#">Import<span class="caret"></span></a><ul class="dropdown-menu sub_menu2"><%-- <li><a tabindex="-1" href="#" data-toggle="modal" data-target="#importModal" onclick="updateImportModal('<%=MasterGSTConstants.GSTR1%>')">Sales Invoices</a></li> <li><a tabindex="-1" href="#" data-toggle="modal" data-target="#importModal" onclick="updateImportModal('Purchase Register')">Purchase Invoices</a> </li>--%><li class="${hypenAccessImportTemplates}"><a tabindex="-1" href="${contextPath}/imports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/${month}/${year}/?type=tax">Import Template</a></li><li class="${hypenAccessBulkImports}"><a tabindex="-1" href="${contextPath}/bulkimports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/${month}/${year}/?type=tax">Bulk imports</a></li></ul></li>
				      <li><a tabindex="-1" href="${contextPath}/dreports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/${month}/${year}">Reports</a></li>
				      <li class="dropdown-submenu3"><a class="test" tabindex="-1" href="#">Payment<span class="caret"></span></a><ul class="dropdown-menu sub_menu3"><li><a tabindex="-1" href="${contextPath}/payments_history/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<%=MasterGSTConstants.GSTR1%>/${month}/${year}/?type=receive">Receipts</a></li><li><a tabindex="-1" href="${contextPath}/payments_history/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<%=MasterGSTConstants.GSTR2%>/${month}/${year}/?type=made">Payments</a></li></ul></li>
				  <li class="${hypenAccessEwaybill}"><a tabindex="-1" href="${contextPath}/ewaybill/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<%=MasterGSTConstants.EWAYBILL%>/<c:out value="${client.id}"/>/${month}/${year}">EwayBill</a></li>
				  <li class="ideinvoice ${hypenAccessEinvoice}"><a tabindex="-1" href="${contextPath}/einvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<%=MasterGSTConstants.EINVOICE%>/<c:out value="${client.id}"/>/${month}/${year}">E-Invoice</a></li>
				  </ul>
				</div></c:if><c:if test="${clientaccess eq 'true'}"><a href="#" class="btn btn-blue">Work on this</a></c:if></td>
              </tr>
			  </c:if>
	             
	            </c:forEach>
                
            </tbody>
          </table>
		  </div>
		  
		  <div id="active_clients" style="display:none">
		   <table id="dbTable1" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
            <thead>
              <tr>
                <th> Business / Client Name</th>
				<th> Group Name</th>
				<th class="text-center">State</th>
                <th class="text-center">Mismatches</th>
               <!-- <th class="text-center">Stage</th> -->
                
                <th class="text-center">Action</th>
              </tr>
            </thead>
            <tbody>
			 <c:forEach items="${lClient}" var="client">
			  <c:if test="${client.active eq false}">
			   <c:set var = "varfn" value = "${client.firstname}"/>
			   <c:set var = "varln" value = "${client.lastname}"/>
			   <c:set var = "brnchln" value = "0"/>
			   <c:set var = "verticalln" value = "0"/>
			   <c:set var="contains" value="false" />
			    <c:set var="clientaccess" value="false" />
			   <c:forEach var="item" items="${companyUser.company}">
					<c:if test="${item eq client.id}">
						<c:set var="contains" value="true" />
			    	</c:if>
			    	<c:if test='${not empty companyUser.disable}'>
							<c:if test='${companyUser.disable eq "true"}'>
								<c:set var="clientaccess" value="true" />
							</c:if>
						</c:if>
				</c:forEach>
				<c:set var="hypenAccessSales" value=""/>
				<c:set var="hypenAccessPurchase" value=""/>
				<c:set var="hypenAccessGSTR2" value=""/>
				<c:set var="hypenAccessGSTR2A" value=""/>
				<c:set var="hypenAccessMisMatched" value=""/>
				<c:set var="hypenAccessGSTR3B" value=""/>
				<c:set var="hypenAccessGSTR4" value=""/>
				<c:set var="hypenAccessGSTR5" value=""/>
				<c:set var="hypenAccessGSTR6" value=""/>
				<c:set var="hypenAccessGSTR9" value=""/>
				<c:set var="hypenAccessGSTR4Annual" value=""/>
				<c:set var="hypenAccessGSTR8" value=""/>
				
				<c:set var="hypenAccessEinvoice" value=""/>
				<c:set var="hypenAccessEwaybill" value=""/>
				<c:set var="hypenAccessImportTemplates" value=""/>
				<c:set var="hypenAccessBulkImports" value=""/>
				<c:if test="${contains}">
					<c:forEach var="role" items="${dbroles}">
				 		<c:if test="${role.clientid eq client.id}">
				 			<c:forEach var="rolename" items="${role.roles}">
							 	<c:if test="${rolename eq 'Sales'}"> <c:set var="hypenAccessSales" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'Purchase'}"> <c:set var="hypenAccessPurchase" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR2'}"> <c:set var="hypenAccessGSTR2" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR2A'}"> <c:set var="hypenAccessGSTR2A" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'MisMatched'}"> <c:set var="hypenAccessMisMatched" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR3B'}"> <c:set var="hypenAccessGSTR3B" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR4'}"> <c:set var="hypenAccessGSTR4" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR5'}"> <c:set var="hypenAccessGSTR5" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR6'}"> <c:set var="hypenAccessGSTR6" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR9'}"> <c:set var="hypenAccessGSTR9" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR4Annual'}"> <c:set var="hypenAccessGSTR4Annual" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'GSTR8'}"> <c:set var="hypenAccessGSTR8" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'Einvoice'}"> <c:set var="hypenAccessEinvoice" value="d-none"/> </c:if>
								<c:if test="${rolename eq 'Ewaybill'}"> <c:set var="hypenAccessEwaybill" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'Import Templates'}"> <c:set var="hypenAccessImportTemplates" value="d-none"/> </c:if>
							 	<c:if test="${rolename eq 'Bulk Imports'}"> <c:set var="hypenAccessBulkImports" value="d-none"/> </c:if>
							 </c:forEach>
						</c:if>
			 		</c:forEach>
				</c:if>
			   <c:choose>
					<c:when test="${not empty companyUser}">
						<c:if test='${not empty companyUser.branch}'>
							<c:forEach items = "${companyUser.branch}" var="branch">
								<c:forEach items="${client.branches}" var="branchs">
									<c:if test='${branch eq branchs.name}'>
										<c:set var = "brnchln" value = "${brnchln + 1}"/>
									</c:if>
								</c:forEach>
							</c:forEach>
						</c:if>
						<c:if test='${not empty companyUser.vertical}'>
							<c:forEach items = "${companyUser.vertical}" var="vertical">
								<c:forEach items="${client.verticals}" var="verticals">
									<c:if test='${vertical eq verticals.name}'>
										<c:set var = "verticalln" value = "${verticalln + 1}"/>
									</c:if>
								</c:forEach>
							</c:forEach>
						</c:if>
					</c:when>
					<c:otherwise>
						<c:forEach items="${client.branches}" var="branch">
							<c:set var = "brnchln" value = "${brnchln + 1}"/>
						</c:forEach>
						<c:forEach items="${client.verticals}" var="verticals">
							<c:set var = "verticalln" value = "${verticalln + 1}"/>
						</c:forEach>
					</c:otherwise>
				</c:choose>	
				<tr <c:if test="${clientaccess eq 'true'}"> data-toggle="tooltip" title="Your Access to <c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose>- ${client.gstnnumber} is restricted, Please contact your admin user for further assistance.""</c:if>>											
                <td><span class="imgsize-wrap-thumb1"><c:if test="${not empty client.logoid}"><img src="${contextPath}/getlogo/${client.logoid}" alt="Logo" class="imgsize-thumb" id="clntlogo"  style="float: left;"></c:if>
							<c:if test="${empty client.logoid}"><img src="${contextPath}/static/mastergst/images/master/defaultcompany.png" alt="Logo" class="imgsize-thumb" id="clntlogo"  style="float: left;"></c:if> </span>
								<%-- <a  style="display:none;"><span class="compname" title="${client.businessname}" style="margin-top: -3px;padding:5px;"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose><span class="caption">${client.signatoryName}</span></span></a> --%>
								<c:if test="${clientaccess eq 'false'}"><a href="#" class="urllink" link="${contextPath}/about/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>"><span class="compname" title="${client.businessname}" style="margin-top: -3px;padding:5px;"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose><span class="caption">${client.signatoryName}</span></span></a></c:if><c:if test="${clientaccess eq 'true'}"><span class="compname" title="${client.businessname}" style="margin-top: -3px;padding:5px;"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose><span class="caption">${client.signatoryName}</span></span></c:if></td>
                <td align="center"  class="boldtxt" style="white-space: nowrap">${client.groupName}</td>
				<td align="center"  class="boldtxt" style="white-space: nowrap">${client.statename}</td>              
				<td align="center"><c:if test="${clientaccess eq 'false'}"><a href="#" class="boldtxt urllink idMisMatch${client.id}">${client.mismatches}</a></c:if><c:if test="${clientaccess eq 'true'}"><a href="#" class="boldtxt idMisMatch${client.id}">${client.mismatches}</a></c:if></td>
                <!-- <td align="center"><c:if test="${clientaccess eq 'false'}"><a href="#" id="idStage${client.id}" class="boldtxt urllink idStage${client.id}"></a></c:if><c:if test="${clientaccess eq 'true'}"><a href="#" id="idStage${client.id}" class="boldtxt idStage${client.id}"></a></c:if></td> -->
                <td class="actionicons"><c:if test="${clientaccess eq 'false'}"><div class="drop_work btn-group">
				  <a class="btn btn-blue" href="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/${month}/${year}/?type=initial">Work on this</a>
				  <button class="btn btn-blue dropdown-toggle caret_drop" data-toggle="dropdown"><span class="caret"></span></button>
				  <ul class="dropdown-menu work_menu"> <!-- dropdown menu links --><li class="dropdown-submenu"><a class="test" tabindex="-1" href="#">Books <span class="caret"></span></a><c:set var="contains" value="gstr1" /><c:if test='${not empty client.returnsSummary}'><c:forEach items="${client.returnsSummary}" var="GSTReturnsSummury"><c:if test='${GSTReturnsSummury.active == "true"}'><c:choose><c:when test="${GSTReturnsSummury.returntype eq varGSTR1 || GSTReturnsSummury.returntype eq varGSTR3B || GSTReturnsSummury.returntype eq varGSTR2}"><c:set var="contains" value="gstr1" /></c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR4}"><c:set var="contains" value="gstr4" />	</c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR5}"><c:set var="contains" value="gstr5" /></c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR6}"><c:set var="contains" value="gstr6" /></c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR8}"><c:set var="contains" value="gstr8" /></c:when></c:choose></c:if></c:forEach></c:if>
				  <ul class="dropdown-menu sub_menu">
				  	<c:if test="${contains ne 'gstr4' && contains ne 'gstr5' && contains ne 'gstr6'}">
				  	<li class="${hypenAccessSales}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/SalesRegister/${month}/${year}/?type=">Sales Register</a></li>
				  	<li class="${hypenAccessPurchase}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/PurchaseRegister/${month}/${year}/?type=">Purchase Register</a></li>
				  	<li><a tabindex="-1" href="${contextPath}/journaldetails/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/${month}/${year}/?type=Monthly">Journals</a></li></c:if><c:if test="${contains == 'gstr4'}"><li class="${hypenAccessSales}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<%=MasterGSTConstants.GSTR4%>/${month}/${year}/?type=">Sales Register</a></li></c:if><c:if test="${contains == 'gstr5'}"><li class="${hypenAccessSales}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<%=MasterGSTConstants.GSTR5%>/${month}/${year}/?type=">Sales Register</a></li></c:if><c:if test="${contains == 'gstr6'}"><li class="${hypenAccessPurchase}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/PurchaseRegister/${month}/${year}/?type=">Purchase Register</a></li></c:if></ul></li>
				      <li class="dropdown-submenu1"><a class="test" tabindex="-1" href="#">GST<span class="caret"></span></a><ul class="dropdown-menu sub_menu1"><c:if test='${not empty client.returnsSummary}'><c:forEach items="${client.returnsSummary}" var="GSTReturnsSummury"><c:if test='${GSTReturnsSummury.active == "true"}'><c:choose><c:when test="${GSTReturnsSummury.returntype eq varGSTR1 }"><li class="${hypenAccessSales}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=">${GSTReturnsSummury.returntype}</a></li></c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR3B }"><li class="${hypenAccessGSTR3B}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=">${GSTReturnsSummury.returntype}</a></li></c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR8}"><li class=""${hypenAccessGSTR8}><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=">${GSTReturnsSummury.returntype}</a></li></c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR4 }"><li class="${hypenAccessGSTR4}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=">${GSTReturnsSummury.returntype}</a></li></c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR5 }"><li class="${hypenAccessGSTR5}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=">${GSTReturnsSummury.returntype}</a></li></c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR6 }"><li class="${hypenAccessGSTR6}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=">${GSTReturnsSummury.returntype}</a></li></c:when><c:when test="${GSTReturnsSummury.returntype == 'GSTR9' }"><li class="${hypenAccessGSTR9}"><a tabindex="-1" href="${contextPath}/addAnnualinvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/${month}/${year}">${GSTReturnsSummury.returntype}</a></li></c:when><c:when test="${GSTReturnsSummury.returntype eq varGSTR2}"><li class="${hypenAccessGSTR2}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=dwnld">${GSTReturnsSummury.returntype}</a></li><li class="${hypenAccessGSTR2A}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=dwnldgstr2a">GSTR2A</a></li><li class="${hypenAccesshypenAccessMisMatched}"><a tabindex="-1" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/${month}/${year}/?type=mmtch">RECONCILE</a></li></c:when></c:choose></c:if></c:forEach></c:if></ul></li>
				      <li class="dropdown-submenu2"><a class="test" tabindex="-1" href="#">Import<span class="caret"></span></a><ul class="dropdown-menu sub_menu2"><%-- <li><a tabindex="-1" href="#" data-toggle="modal" data-target="#importModal" onclick="updateImportModal('<%=MasterGSTConstants.GSTR1%>')">Sales Invoices</a></li> <li><a tabindex="-1" href="#" data-toggle="modal" data-target="#importModal" onclick="updateImportModal('Purchase Register')">Purchase Invoices</a> </li>--%><li class="${hypenAccessImportTemplates}"><a tabindex="-1" href="${contextPath}/imports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/${month}/${year}/?type=tax">Import Template</a></li><li class="${hypenAccessBulkImports}"><a tabindex="-1" href="${contextPath}/bulkimports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/${month}/${year}/?type=tax">Bulk imports</a></li></ul></li>
				      <li><a tabindex="-1" href="${contextPath}/dreports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/${month}/${year}">Reports</a></li>
				      <li class="dropdown-submenu3"><a class="test" tabindex="-1" href="#">Payment<span class="caret"></span></a><ul class="dropdown-menu sub_menu3"><li><a tabindex="-1" href="${contextPath}/payments_history/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<%=MasterGSTConstants.GSTR1%>/${month}/${year}/?type=receive">Receipts</a></li><li><a tabindex="-1" href="${contextPath}/payments_history/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<%=MasterGSTConstants.GSTR2%>/${month}/${year}/?type=made">Payments</a></li></ul></li>
				 	<li class="${hypenAccessEwaybill}"><a tabindex="-1" href="${contextPath}/ewaybill/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<%=MasterGSTConstants.EWAYBILL%>/<c:out value="${client.id}"/>/${month}/${year}">EwayBill</a></li>
				    <li class="ideinvoice ${hypenAccessEinvoice}"><a tabindex="-1" href="${contextPath}/einvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<%=MasterGSTConstants.EINVOICE%>/<c:out value="${client.id}"/>/${month}/${year}">E-Invoice</a></li>
				  </ul>
				</div></c:if><c:if test="${clientaccess eq 'true'}"><a href="#" class="btn btn-blue">Work on this</a></c:if></td>
              </tr>
			  </c:if>
	            </c:forEach>
            </tbody>
          </table>
		  </div>
          <div class="text-center mt-2 mb-4 permissionSettings-Company_Details-Add" id="idPermissionAdd_Client">
          <c:choose>
          	<c:when test='${empty addClient}'>
          		<a href="#" link="${contextPath}/addclient/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>" class="qwerty btn btn-blue-dark urladdlink" onclick="addclient()">Add <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Client</c:when><c:otherwise>Business</c:otherwise></c:choose></a>
          	</c:when>
          	<c:when test='${not empty addClient && addClient eq "true"}'>
          		<a href="#" link="${contextPath}/addclient/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>" class="abcd btn btn-blue-dark urladdlink" onclick="addclient()">Add <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Client</c:when><c:otherwise>Business</c:otherwise></c:choose></a>
          	</c:when>
          	<c:otherwise>
          		<!-- <a href="#" link="${contextPath}/addclient/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>" class="btn btn-blue-dark urladdlink" onclick="addclient()">Add <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Client</c:when><c:otherwise>Business</c:otherwise></c:choose></a> -->
          	</c:otherwise>
          </c:choose>
          
          </div>
        </div>
        <!-- dashboard ca table end -->
        <!-- right side begin -->
        <div class="col-md-3 col-sm-12 down-client-files">
          <!-- notifications begin -->
          <div class="rightside-wrap gst-notify">
            <h5>Notifications <span class="badge-circle-red">5</span></h5>
            <ul>
                 <li><span class="sm-img"></span>GSTR1 File Successfully Filed. </li>
              <li><span class="sm-img"></span>You have pending invoices to GSTR2. </li>
              <li><span class="sm-img"></span>100 Invoices Mismatched with your Purchase Register.</li>
              <li><span class="sm-img"></span>Admin User Created Successfully.</li>
              <li><span class="sm-img"></span>You have registered successfully, Welcome to MasterGST</li> 
              
            </ul>
          </div>
          <!-- notifications begin -->
          <!-- Actions begin -->
          <div class="rightside-wrap">
            <h5>Download Templates</h5>
            <ul>
             <li><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/MasterGST_Sales_Invoices_Template.xls">MasterGST Sales Template</a></li>
              <li><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Tally_Sales_Template.xls">Tally Sales Template</a> </li>
              <li><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Tally_Sales_Template_v17.xls">Tally Sales Template_V1.7</a> </li>
              <li><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Tally_Sales_Prime_Template.xls">Tally Sales Prime Template</a> </li>
              <li><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/GSTR1_Offline_Utility.xlsx">GSTR1 Offline Template</a> </li>
              <li><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Sage_Sales_Invoices_Template.xlsx">Sage Sales Template</a></li>
	      	<li class="fhpl_import_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/MasterGST_Sales_Invoices_Template_with_Additional_Fields.xls">MasterGST Sales Template with Additional Fields</a></li>
	      	<li><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/MasterGST_Sales_Invoices_Template_with_All_Fields.xls">MasterGST Sales Template with All Fields</a></li>
              <li><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/MasterGST_Purchase_Invoices_Template.xls">MasterGST Purchase Template</a></li>
			  <li><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Tally_Purchase_Invoices_Template.xlsx">Tally Purchase Template</a></li>
			  <li><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Sage_Purchase_Invoices_Template.xlsx">Sage Purchase Template</a></li>
			  <li><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Single_Sheet_Purchase_Invoices_Template.xlsx">Single Sheet Template of Purchase Invoice</a></li>
              <li><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/MasterGST_ewaybill_excel_Template.xls">MasterGST EwayBill Template</a></li>
              <li><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/MasterGST_einvoices_Template.xls">E-Invoice Template</a></li>
			  <li><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Sage_einvoices_Template.xls">Sage E-Invoice Template</a></li>
			  <li class="entertaiment_einvoice_import_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Entertainment_Mastergst_Einvoice_Template.xls">Entertainment E-Invoice Template</a></li>
            </ul>
          </div>
          <!-- Actions begin -->
          <!-- Reports begin -->
          <div class="rightside-wrap">
            <h5><a class="urllink" href="#" link="${contextPath}/cp_ClientsReports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>">Global Reports</a></h5>
            <ul>
              <li><span class="sm-img"></span><a class="pull-right" href="${contextPath}/cp_ClientsReportsData/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}">All clients filing status Report</a></li>
              <c:if test="${usertype eq userCenter}">
					<li><span class="sm-img"></span><a class="pull-right" href="${contextPath}/cp_allcenters/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}">All Suvidha Kendra Usage Report.</a></li>
			  </c:if>
			   <li><span class="sm-img"></span><a href="${contextPath}/cp_ClientsReportsGroupData/Sales/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}">Group Wise Sales Reports</a></li>
             <li><span class="sm-img"></span><a href="${contextPath}/cp_ClientsReportsGroupData/GSTR2/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}">Group Wise Purchase Reports</a></li>
              <li><span class="sm-img"></span><a href="${contextPath}/PaymentReportsGroupData/GSTR1/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}">Group Wise Payments Received Report</a></li>
             <li><span class="sm-img"></span><a href="${contextPath}/PaymentReportsGroupData/GSTR2/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}">Group Wise Payments Made Report</a></li>
              <li><span class="sm-img"></span><a href="${contextPath}/cp_multiGtstin/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}?type=multigstin">Multi GSTIN Search Report </a></li>
			<li></li>
            </ul>
          </div>
          <!-- Reports begin -->
          <!-- right side end -->
        </div>
           <!-- right side end -->
      </div>
    </div>
  </div>
</div>
<!-- footer begin here -->
 <%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->
<script type="text/javascript">
	var clientList = new Array(), statusResponse = new Array(),roleslist = new Array();
  $(document).ready(function(){
	$('#nav-client').addClass('active');
	$("table#dbTable, table#dbTable1").tableDnD();
	var addclient = '<c:out value="${addClient}"/>';
	//$('.xdsoft_calendar').hide();
	<c:forEach items="${lClient}" var="client">
	clientList.push('<c:out value="${client.id}"/>');
	</c:forEach>
	fetchStatus();
	var table = $('table#dbTable, table#dbTable1').DataTable({
   "dom": '<"toolbar">frtip',
    
     "pageLength": 10,
	 "order": [],
	"columnDefs": [
            {
                "targets": [ 1 ],
                "visible": false,
                "searchable": true
            }
        ],
     "language": {
  			"search": "_INPUT_",
        "searchPlaceholder": "Search...",
        "paginate": {

           "previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",

           "next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"

        }

     }

   });
	if(addclient == "true"){
    	$("#all_client div.toolbar").html('<h4>Show <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}"><select name="display" class="display" id="display" style="font-size:16px;margin-left: 4px;" onchange="activeclientcheck(this.value)"><option value="all">All Clients</option><option value="inactive">Inactive Clients</option><option value="active"> Active Clients</option></select></c:when> <c:otherwise><select name="display" id="display1" class="display" style="font-size:15px;" onchange="activeclientcheck(this.value)"><option value="all">Business</option><option value="inactive">Inactive</option><option value="active">Active</option></select></c:otherwise></c:choose>  </h4><a href="#" link="${contextPath}/addclient/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>" class="btn btn-blue-dark permissionSettings-Company_Details-Add urladdlink mt-1" onclick="addclient()">Add <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Client</c:when><c:otherwise>Business</c:otherwise></c:choose></a>');
	 	$("#active_clients div.toolbar").html('<h4>Show <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}"><select name="display" class="display" id="display" style="font-size:16px;margin-left: 4px;" onchange="activeclientcheck(this.value)"><option value="all">All Clients</option><option value="inactive">Inactive Clients</option><option value="active"> Active Clients</option></select></c:when> <c:otherwise><select name="display" id="display1" class="display" style="font-size:15px;" onchange="activeclientcheck(this.value)"><option value="all">Business</option><option value="inactive">Inactive</option><option value="active">Active</option></select></c:otherwise></c:choose>  </h4><a href="#" link="${contextPath}/addclient/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>" class="btn btn-blue-dark permissionSettings-Company_Details-Add urladdlink mt-1" onclick="addclient()">Add <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Client</c:when><c:otherwise>Business</c:otherwise></c:choose></a>');
	  	$("#inactive_clients div.toolbar").html('<h4>Show <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}"><select name="display" class="display" id="display" style="font-size:16px;margin-left: 4px;" onchange="activeclientcheck(this.value)"><option value="all">All Clients</option><option value="inactive">Inactive Clients</option><option value="active"> Active Clients</option></select></c:when> <c:otherwise><select name="display" id="display1" class="display" style="font-size:15px;" onchange="activeclientcheck(this.value)"><option value="all">Business</option><option value="inactive">Inactive</option><option value="active">Active</option></select></c:otherwise></c:choose>  </h4><a href="#" link="${contextPath}/addclient/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>" class="btn btn-blue-dark permissionSettings-Company_Details-Add urladdlink mt-1" onclick="addclient()">Add <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Client</c:when><c:otherwise>Business</c:otherwise></c:choose></a>');
	}else{
		$("#all_client div.toolbar").html('<h4>Show <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}"><select name="display" class="display" id="display" style="font-size:16px;margin-left: 4px;" onchange="activeclientcheck(this.value)"><option value="all">All Clients</option><option value="inactive">Inactive Clients</option><option value="active"> Active Clients</option></select></c:when> <c:otherwise><select name="display" id="display1" class="display" style="font-size:15px;" onchange="activeclientcheck(this.value)"><option value="all">Business</option><option value="inactive">Inactive</option><option value="active">Active</option></select></c:otherwise></c:choose>  </h4>');
	 	$("#active_clients div.toolbar").html('<h4>Show <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}"><select name="display" class="display" id="display" style="font-size:16px;margin-left: 4px;" onchange="activeclientcheck(this.value)"><option value="all">All Clients</option><option value="inactive">Inactive Clients</option><option value="active"> Active Clients</option></select></c:when> <c:otherwise><select name="display" id="display1" class="display" style="font-size:15px;" onchange="activeclientcheck(this.value)"><option value="all">Business</option><option value="inactive">Inactive</option><option value="active">Active</option></select></c:otherwise></c:choose>  </h4>');
	  	$("#inactive_clients div.toolbar").html('<h4>Show <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}"><select name="display" class="display" id="display" style="font-size:16px;margin-left: 4px;" onchange="activeclientcheck(this.value)"><option value="all">All Clients</option><option value="inactive">Inactive Clients</option><option value="active"> Active Clients</option></select></c:when> <c:otherwise><select name="display" id="display1" class="display" style="font-size:15px;" onchange="activeclientcheck(this.value)"><option value="all">Business</option><option value="inactive">Inactive</option><option value="active">Active</option></select></c:otherwise></c:choose>  </h4>');
	}
 //$("#all_client div.toolbar").html('<h4>All <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when> <c:otherwise>Business</c:otherwise></c:choose>  </h4> <div class="form-check mb-2 mb-sm-0" id="active_client_check"><div class="meterialform"><div class="checkbox pull-right"><label style="position:relative;margin-top:-1px;"><input id="addBankDetails" type="checkbox" name="addBankDetails" onclick="activeclientcheck()"><i class="helper active_client" style="left:6px;"></i> Hide InActive Clients</label></div></div></div> <a href="${contextPath}/addclient/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${month}"/>/<c:out value="${year}"/>" class="btn btn-blue-dark permissionAdd_Client urladdlink mt-1">Add <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Client</c:when><c:otherwise>Business</c:otherwise></c:choose></a>');
 //$("div").html('<input type="radio" id="status" value="active" checked /> Active Clients"<input type="radio" id="status" value="all" />All Clients');
 //$("#inactive_clients div.toolbar").html('<h4>All <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when> <c:otherwise>Business</c:otherwise></c:choose>  </h4> <div class="form-check mb-2 mb-sm-0" id="inactive_client_check"><div class="meterialform"><div class="checkbox pull-right"><label style="position:relative;margin-top:-1px;"><input id="addBankDetails1" type="checkbox" name="addBankDetails1" onclick="activeclientcheck1()"><i class="helper active_client" style="left:6px;"></i> Hide InActive Clients</label></div></div></div> <a href="${contextPath}/addclient/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${month}"/>/<c:out value="${year}"/>" class="btn btn-blue-dark permissionAdd_Client urladdlink mt-1">Add <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Client</c:when><c:otherwise>Business</c:otherwise></c:choose></a>');
 
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
	var userPermissions;
	$(function() {
  		$.ajax({
			url: "${contextPath}/usrprms?userid=${id}",
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(permissions) {
				userPermissions = permissions;
				if(userPermissions) {
					pArray.forEach(function(item) {
						if($.inArray(item, userPermissions) == -1) {
							$('.permission'+item.replace(/\s/g, '_')).hide();
						}
					});
				} else {
					pArray.forEach(function(item) {
						$('.permission'+item.replace(/\s/g, '_')).hide();
					});
				}
			}
		});
	});
  function addclient() {
		var url = $('.urladdlink').attr('link');
		$.ajax({
			url: "${contextPath}/addclntelgbty/${id}",
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(status) {
				if(status) {
					if(url) {
						url+='/'+month+'/'+year;
						location.href=url;
					}
				} else {
					errorNotification('New client addition is not allowed as the limit is already crossed.');
				}
			},
			error : function(status) {
				if(url) {
					url+='/'+month+'/'+year;
					location.href=url;
				}
			}
		});
	}
  
	function updateReturnPeriod(eDate) {
		var month = eDate.getMonth()+1;
		var year = eDate.getFullYear();
		window.location.href = '${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+month+'/'+year;
	}
	function fetchStatus() {
		$.ajax({
			url: "${contextPath}/cstatus?month="+month+"&year="+year+"&clientIds="+clientList,
			async: false,
			cache: false,
			success : function(response) {
				statusResponse = response;
				updateStatus();
			}
		});
		$.ajax({
			url: "${contextPath}/mismtchstatus?month="+month+"&year="+year+"&clientIds="+clientList,
			async: false,
			cache: false,
			success : function(response) {
				Object.keys(response).forEach(function(key) {
					$('.idMisMatch'+key).html(response[key]);
					if(response[key] > 0) {
						$('.idMisMatch'+key).css("color","red");
						$('.idMisMatch'+key).attr('link', '${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+key+'/<%=MasterGSTConstants.GSTR2%>?type=mmtch');
					}
				});
			}
		});
	}
	function updateStatus() {
		Object.keys(statusResponse).forEach(function(key) {
			$('.idStage'+key).attr('link', '${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+key+'/'+statusResponse[key]+'?type=');
			$('.idStage'+key).html(statusResponse[key]);
			if(statusResponse[key] == 'GSTR1'){
				$('#idStage'+key).addClass('permissionInvoices-Sales-View');
				$('.idMisMatch'+key).addClass(' permissionGSTR2-MisMatched');
			}else if(statusResponse[key] == 'GSTR2'){
				$('#idStage'+key).addClass('permissionInvoices-Purchase-View');				
				//$('.idStage'+key).addClass('permission'+statusResponse[key]+'-'+statusResponse[key]);
			}
			
		});
	}
	
	function activeclientcheck(selectedValue){
		$('.display').val(selectedValue);
		if(selectedValue == "all") {
			$('#inactive_clients').css("display","none");
			$('#active_clients').css("display","none");
			$('#all_client').css("display","block");
		}
		else if(selectedValue == "inactive") {
			$('#inactive_clients').css("display","none");
			$('#active_clients').css("display","block");
			$('#all_client').css("display","none");
		}
		else {
			$('#inactive_clients').css("display","block");
			$('#active_clients').css("display","none");
			$('#all_client').css("display","none");
		}
	}
 </script>
</body>
</html>
