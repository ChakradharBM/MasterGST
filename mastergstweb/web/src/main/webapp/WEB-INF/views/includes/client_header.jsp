<%@include file="/WEB-INF/views/includes/main_header.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/client-headers.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/invoice/einvoices.css" media="all" />

   <div class="bodywrap">
        <div class="bodybreadcrumb main">
			<div class="container">
				<div class="row">
					<div class="col-sm-12 client_datepicker_row">
						<c:choose>
						<c:when test='${not empty client && not empty client.id}'>
						<div class="navbar-left">
							<ul>
								<li class="nav-item"><a class="nav-link urllink" href="#" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change" style=" margin-left:7px!important"><img src="${contextPath}/static/mastergst/images/master/homeicon.png" style="height: 15px; margin-bottom:4px;"></a></li>
								<li class="nav-item"><li class="nav-item dropdown invoice">
									<a class="nav-link dropdown-toggle" href="#" id="navbarDropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">BOOKS</a>
									<c:set var="contains" value="gstr1" />
									<c:if test='${not empty lGSTReturnsSummury}'>
										<c:forEach items="${lGSTReturnsSummury}" var="GSTReturnsSummury">
											<c:if test='${GSTReturnsSummury.active == "true"}'>
												<c:choose>
													<c:when test="${GSTReturnsSummury.returntype eq varGSTR1 || GSTReturnsSummury.returntype eq varGSTR3B || GSTReturnsSummury.returntype eq varGSTR2}"><c:set var="contains" value="gstr1" /></c:when>
													<c:when test="${GSTReturnsSummury.returntype eq varGSTR4}"><c:set var="contains" value="gstr4" />	</c:when>
													<c:when test="${GSTReturnsSummury.returntype eq varGSTR5}"><c:set var="contains" value="gstr5" /></c:when>
													<c:when test="${GSTReturnsSummury.returntype eq varGSTR6}"><c:set var="contains" value="gstr6" /></c:when>
												</c:choose>
											</c:if>
										</c:forEach>
										</c:if>		
									 <%-- <div aria-labelledby="navbarDropdownMenuLink" id="booksmenu" <c:if test="${contains == 'gstr4'}">class="dropdown-menu gst4-drop" style="left:81px !important;padding:5px!important;width:300px!important;min-width:300px!important;max-width: max-content;"</c:if> <c:if test="${contains == 'gstr5'}">class="dropdown-menu gst5-drop" style="left:81px !important;padding:5px!important;width:300px!important;min-width:300px!important;max-width: max-content;"</c:if> <c:if test="${contains == 'gstr6'}">class="dropdown-menu gst6-drop" style="left:81px!important;padding:5px!important;width:300px!important;min-width:300px!important;max-width: max-content;"</c:if> <c:if test="${contains ne 'gstr4' && contains ne 'gstr5' && contains ne 'gstr6'}">class="dropdown-menu"</c:if>  style="padding:5px!important;width: 580px!important;min-width: 580px;left:122px!important;max-width: max-content;"> --%>
									      <div aria-labelledby="navbarDropdownMenuLink" id="booksmenu" <c:if test="${contains == 'gstr4'}">class="dropdown-menu gst4-drop" style="left:81px !important;padding:5px!important;width:300px!important;min-width:412px!important;max-width: max-content;"</c:if> <c:if test="${contains == 'gstr5'}">class="dropdown-menu gst5-drop" style="left:81px !important;padding:5px!important;width:300px!important;min-width:300px!important;max-width: max-content;"</c:if><c:if test="${contains == 'gstr6'}">class="dropdown-menu gst6-drop" style="padding:5px!important;width: 621px!important;left:122px!important;max-width: max-content;"</c:if> <c:if test="${contains ne 'gstr4' && contains ne 'gstr5'}">class="dropdown-menu"</c:if>  style="padding:5px!important;width: 580px!important;min-width: 652px;left:122px!important;max-width: max-content;">
											<%-- <c:if test="${contains ne 'gstr4' && contains ne 'gstr5' && contains ne 'gstr6'}"> --%>
											 <c:if test="${contains ne 'gstr4' && contains ne 'gstr5' && contains ne 'gstr6'}">
											<div class="row" style="background-color:#f8fdff;width: 640px;margin-left: 0PX;height: 59px;">
											<div class="link-dv permissionInvoices-Sales-View" style="padding-right:0px!important"><a class="urllink permissionInvoices-Sales-View pl-0 ar-link" href="#" link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/SalesRegister?type=">SALES REGISTER</a><p class="permissionInvoices-Sales-View" style="font-size:10px">Profarma invoices, Delivery Challans , Estimates</p></div>
											<div class="link-dv-br permissionInvoices-Sales-View" style="padding:7px!important;border-right: 1px solid #374583;height:48px;margin-top:5px;border-image: linear-gradient(to bottom,#f8fdff 0,#f8fdff 2.3%,#37458369 33.3%,#37458363 66.6%,#f8fdff 100.6%,#f8fdff) 5 90%;"></div>
											<div class="link-dv permissionInvoices-Purchase-View" style="padding-right:0px!important;padding-left: 10px;"><a href="#" class="urllink permissionInvoices-Purchase-View pl-0 ar-link" link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/PurchaseRegister?type=">PURCHASE REGISTER</a><p class="permissionInvoices-Purchase-View" style="font-size:10px">Purchase Order</p></div>
											<div class="link-dv-br permissionInvoices-Purchase-View" style="padding:7px!important;border-right: 1px solid #374583;height:48px;margin-top:5px;border-image: linear-gradient(to bottom,#f8fdff 0,#f8fdff 2.3%,#37458369 33.3%,#37458363 66.6%,#f8fdff 100.6%,#f8fdff) 5 90%;"></div>
											<div class="link-dv" style="padding-right:0px!important;padding-left: 10px;"><a href="#" class="urllink pl-0 ar-link" link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/PurchaseRegister?type=Exp">EXPENSES</a><p style="font-size:10px">Expenses</p></div>
											<div class="link-dv-br permissionInvoices-Purchase-View" style="padding:7px!important;border-right: 1px solid #374583;height:48px;margin-top:5px;border-image: linear-gradient(to bottom,#f8fdff 0,#f8fdff 2.3%,#37458369 33.3%,#37458363 66.6%,#f8fdff 100.6%,#f8fdff) 5 90%;"></div>
											<div class="link-dv" style="padding-right:0px!important;padding-left: 10px;"><a href="#" class="urllink pl-0 ar-link" link="${contextPath}/journaldetails/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=Monthly">JOURNALS</a><p style="font-size:10px">Accounting Entries</p></div>
											</div>
											</c:if>
											<%-- <c:if test="${contains == 'gstr4'}"><div class="col-md-12 link-dv" style="background-color:#f8fdff;"><a class="urllink permissionInvoices-Sales-View pl-0 ar-link ar-link" href="#" link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<%=MasterGSTConstants.GSTR4%>?type=" >SALES REGISTER</a><p style="font-size:10px">Profarma invoices, Delivery Challans , Estimates</p></div></c:if> --%>
											<c:if test="${contains == 'gstr4'}">
											<div class="row" style="background-color:#f8fdff;width: 400px;margin-left: 0PX;height: 59px;">
											<div class="link-dv permissionInvoices-Sales-View" style="padding-right:0px!important"><a class="urllink permissionInvoices-Sales-View pl-0 ar-link" href="#" link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/SalesRegister?type=">SALES REGISTER</a><p class="permissionInvoices-Sales-View" style="font-size:10px">Profarma invoices, Delivery Challans , Estimates</p></div>
											<div class="link-dv-br permissionInvoices-Sales-View" style="padding:7px!important;border-right: 1px solid #374583;height:48px;margin-top:5px;border-image:linear-gradient(to bottom,#f8fdff 0,#f8fdff 2.3%,#374583 33.3%,#374583 66.6%,#f8fdff 100.6%,#f8fdff) 5 90%"></div>
											<div class="link-dv permissionInvoices-Purchase-View" style="padding-right:0px!important;padding-left: 10px;"><a href="#" class="urllink permissionInvoices-Purchase-View pl-0 ar-link" link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/PurchaseRegister?type=">PURCHASE REGISTER</a><p class="permissionInvoices-Purchase-View" style="font-size:10px">Purchase Order</p></div>
											</div>
											</c:if>
											<c:if test="${contains == 'gstr5'}"><div class="col-md-12 link-dv" style="background-color:#f8fdff;"><a class="urllink permissionInvoices-Sales-View pl-0 ar-link ar-link" href="#" link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<%=MasterGSTConstants.GSTR5%>?type=" >SALES REGISTER</a><p style="font-size:10px">Profarma invoices, Delivery Challans , Estimates</p></div></c:if>
											<c:if test="${contains == 'gstr6'}">
											<div class="row" style="background-color:#f8fdff;width: 400px;margin-left: 0PX;height: 59px;">
											<div class="link-dv permissionInvoices-Sales-View" style="padding-right:0px!important"><a class="urllink permissionInvoices-Sales-View pl-0 ar-link" href="#" link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/Distribution?type=">DISTRIBUTIONS</a></div>
											<div class="link-dv-br permissionInvoices-Sales-View" style="padding:7px!important;border-right: 1px solid #374583;height:48px;margin-top:5px;border-image:linear-gradient(to bottom,#f8fdff 0,#f8fdff 2.3%,#374583 33.3%,#374583 66.6%,#f8fdff 100.6%,#f8fdff) 5 90%"></div>
											<div class="link-dv permissionInvoices-Purchase-View" style="padding-right:0px!important;padding-left: 10px;"><a href="#" class="urllink permissionInvoices-Purchase-View pl-0 ar-link" link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/GSTR6Pur?type=">PURCHASE REGISTER</a></div>
											
											</div>
												
											</c:if>
											</div>
								</li>
								<li class="nav-item dropdown returns_dropdown">
									<a class="nav-link dropdown-toggle" href="#" id="navbarDropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">GST</a>
									<div class="dropdown-menu returnmenudrop1" aria-labelledby="navbarDropdownMenuLink" style="background:unset;border:none;padding-top: 0px;">
										<div class="row">
											<a class="first-link" href="#"></a>
											<c:if test='${not empty lGSTReturnsSummury}'>
												<c:forEach items="${lGSTReturnsSummury}" var="GSTReturnsSummury">
													<c:if test='${GSTReturnsSummury.active == "true"}'>
														<c:choose>
															<c:when test="${GSTReturnsSummury.returntype eq varGSTR1 }"><a class="urllink permissionInvoices-Sales-View ar-link"  href="#" link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>?type=">${GSTReturnsSummury.returntype}</a><div class="right-br-cl permissionInvoices-Sales-View" ></div></c:when>
															<c:when test="${GSTReturnsSummury.returntype eq varGSTR2}">
																<!-- <a class="urllink permissionGSTR2-GSTR2 ar-link" href="#"  link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>?type=dwnld">${GSTReturnsSummury.returntype}</a><div class="right-br-cl permissionGSTR2-GSTR2" ></div> -->
																<a class="urllink permissionGSTR2-GSTR2A ar-link" href="#"   link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>?type=dwnldgstr2a">GSTR2A</a><div class="right-br-cl permissionGSTR2-GSTR2A" ></div>
																<a class="urllink ar-link"  id="idGSTR2B" href="#" link="${contextPath}/addgstr2binvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="<%=MasterGSTConstants.GSTR2B%>"/>"><%=MasterGSTConstants.GSTR2B%></a><div class="right-br-cl" ></div>
																<a class="urllink permissionGSTR2-MisMatched ar-link" href="#"   link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>?type=mmtch">RECONCILE</a><div class="right-br-cl permissionGSTR2-MisMatched" ></div>
															</c:when>
															<c:when test="${GSTReturnsSummury.returntype eq varGSTR3B}"><a class=" urllink permissionGSTR3B-GSTR3B ar-link" href="#"   link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>?type=">${GSTReturnsSummury.returntype}</a></c:when>
															<c:when test="${GSTReturnsSummury.returntype eq varGSTR8}"><a class=" urllink permissionGSTR8-GSTR8 ar-link idaccessGstr8" href="#" link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>?type=">${GSTReturnsSummury.returntype}</a><div class="right-br-cl permissionGSTR8-GSTR8 idaccessGstr8" ></div></c:when>
															<c:when test="${GSTReturnsSummury.returntype eq varGSTR4}"><a class="urllink permissionGSTR4-GSTR4 ar-link" href="#"  link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>?type=">${GSTReturnsSummury.returntype}</a><div class="right-br-cl permissionGSTR4-GSTR4" ></div></c:when>
															<c:when test="${GSTReturnsSummury.returntype eq varGSTR5}"><a class="urllink permissionGSTR5-GSTR5 ar-link" href="#"   link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>?type=">${GSTReturnsSummury.returntype}</a><div class="right-br-cl permissionGSTR5-GSTR5" ></div></c:when>
															<c:when test="${GSTReturnsSummury.returntype eq varGSTR6}"><a class="ar-link urllink permission${GSTReturnsSummury.returntype}-${GSTReturnsSummury.returntype}" href="#" link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>?type=">${GSTReturnsSummury.returntype}</a><div class="right-br-cl permission${GSTReturnsSummury.returntype}-${GSTReturnsSummury.returntype}" ></div><a class="urllink permissionGSTR6-GSTR6A ar-link" href="#"   link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>?type=dwnldgstr6a">GSTR6A</a><div class="right-br-cl permissionGSTR6-GSTR6A" ></div><%-- <a class="ar-link urllink permission${GSTReturnsSummury.returntype}-${GSTReturnsSummury.returntype}" href="#" link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/GSTR6_purchase?type=">GSTR6 Purchase</a><div class="right-br-cl permission${GSTReturnsSummury.returntype}-${GSTReturnsSummury.returntype}" ></div> --%> <a class="urllink  ar-link idGstr6" href="#" link="${contextPath}/addGSTR6invoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>" >GSTR6 Returns</a><div class="right-br-cl permission${GSTReturnsSummury.returntype}-${GSTReturnsSummury.returntype}" ></div></c:when>
															<c:when test="${GSTReturnsSummury.returntype == 'GSTR9'}"><a class="urllink  ar-link idGstr9" href="#" link="${contextPath}/addAnnualinvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>" >GSTR9</a><div class="right-br-cl" ></div> </c:when>
															<c:otherwise></c:otherwise>
														</c:choose>
													</c:if>
												</c:forEach>
													<!-- <a class="urllink  ar-link idGstr4" href="#" link="${contextPath}/addGSTR4invoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>" >GSTR4 Returns</a>-->
												
											</c:if>
											<%-- <c:if test='${not empty lGSTReturnsSummury}'>
												<c:forEach items="${lGSTReturnsSummury}" var="GSTReturnsSummury">
													<c:if test='${GSTReturnsSummury.active == "true"}'>
														<c:choose>
															<c:when test="${GSTReturnsSummury.returntype eq varGSTR1 }"><a class="urllink permissionInvoices-Sales-View ar-link"  href="#" link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>?type=">${GSTReturnsSummury.returntype}</a><div class="right-br-cl permissionInvoices-Sales-View" ></div></c:when>
															<c:when test="${GSTReturnsSummury.returntype eq varGSTR3B}"><a class=" urllink permissionGSTR3B-GSTR3B ar-link" href="#"   link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>?type=">${GSTReturnsSummury.returntype}</a><div class="right-br-cl permissionGSTR3B-GSTR3B" ></div></c:when>
															<c:when test="${GSTReturnsSummury.returntype eq varGSTR8}"><a class=" urllink permissionGSTR8-GSTR8 ar-link idaccessGstr8" href="#" link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>?type=">${GSTReturnsSummury.returntype}</a><div class="right-br-cl permissionGSTR8-GSTR8 idaccessGstr8" ></div></c:when>
															<c:when test="${GSTReturnsSummury.returntype eq varGSTR4}"><a class="urllink permissionGSTR4-GSTR4 ar-link" href="#"  link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>?type=">${GSTReturnsSummury.returntype}</a><div class="right-br-cl permissionGSTR4-GSTR4" ></div></c:when>
															<c:when test="${GSTReturnsSummury.returntype eq varGSTR5}"><a class="urllink permissionGSTR5-GSTR5 ar-link" href="#"   link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>?type=">${GSTReturnsSummury.returntype}</a><div class="right-br-cl permissionGSTR5-GSTR5" ></div></c:when>
															<c:when test="${GSTReturnsSummury.returntype eq varGSTR6}"><a class="ar-link urllink permission${GSTReturnsSummury.returntype}-${GSTReturnsSummury.returntype}" href="#" link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>?type=">${GSTReturnsSummury.returntype}</a><div class="right-br-cl permission${GSTReturnsSummury.returntype}-${GSTReturnsSummury.returntype}" ></div><a class="urllink permissionGSTR6-GSTR6A ar-link" href="#"   link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>?type=dwnldgstr6a">GSTR6A</a><div class="right-br-cl permissionGSTR6-GSTR6A" ></div><a class="ar-link urllink permission${GSTReturnsSummury.returntype}-${GSTReturnsSummury.returntype}" href="#" link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/GSTR6_purchase?type=">GSTR6 Purchase</a><div class="right-br-cl permission${GSTReturnsSummury.returntype}-${GSTReturnsSummury.returntype}" ></div> <a class="urllink  ar-link idGstr6" href="#" link="${contextPath}/addGSTR6invoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>" >GSTR6 Returns</a><div class="right-br-cl permission${GSTReturnsSummury.returntype}-${GSTReturnsSummury.returntype}" ></div></c:when>
															<c:when test="${GSTReturnsSummury.returntype == 'GSTR9'}"><a class="urllink  ar-link idGstr9" href="#" link="${contextPath}/addAnnualinvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>" >GSTR9</a><div class="right-br-cl" ></div></c:when>
															<c:when test="${GSTReturnsSummury.returntype eq varGSTR2}">
																<!-- <a class="urllink permissionGSTR2-GSTR2 ar-link" href="#"  link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>?type=dwnld">${GSTReturnsSummury.returntype}</a><div class="right-br-cl permissionGSTR2-GSTR2" ></div> -->
																<a class="urllink permissionGSTR2-GSTR2A ar-link" href="#"   link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>?type=dwnldgstr2a">GSTR2A</a><div class="right-br-cl permissionGSTR2-GSTR2A" ></div>
																<a class="urllink permissionGSTR2-MisMatched ar-link" href="#"   link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>?type=mmtch">RECONCILE</a><div class="right-br-cl permissionGSTR2-MisMatched" ></div>
															</c:when>
															<c:otherwise></c:otherwise>
														</c:choose>
													</c:if>
												</c:forEach>
													<!-- <a class="urllink  ar-link idGstr4" href="#" link="${contextPath}/addGSTR4invoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>" >GSTR4 Returns</a>-->
												<a class="urllink ar-link"  id="idGSTR2B" href="#" link="${contextPath}/addgstr2binvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="<%=MasterGSTConstants.GSTR2B%>"/>"><%=MasterGSTConstants.GSTR2B%></a>
												
											</c:if> --%>
											<a class="last-link" href="#"></a>
										</div>
									</div>
								</li>
								<li class="nav-item ideinv returns_dropdown ideinvoice permissionNew_Returns-E-invoice" id="ideinvoice"><a class="nav-link urllink" href="#" link="${contextPath}/einvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<%=MasterGSTConstants.EINVOICE%>/<c:out value="${client.id}"/>">E-INVOICE </a></li>
								<%--<li class="nav-item dropdown einvoice-menu-drop">
									<a class="nav-link dropdown-toggle urllink" href="#" id="navbarpaymentsMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" link="${contextPath}/einvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<%=MasterGSTConstants.EINVOICE%>/<c:out value="${client.id}"/>">e-Invoice</a>
									<div class="dropdown-menu returnmenudrop1" aria-labelledby="navbarImportMenuLink" id="navbareinvoicesdrop">
										<div class="row">
											<a class="first-link" href="#"></a>
											<a class="urllink permissionGeneral-Y ar-link" href="#"  onclick="showEinvoicePopup('<%=MasterGSTConstants.B2B%>','<%=MasterGSTConstants.EINVOICE%>',true)" >B2B</a><div class="right-br-cl" ></div>
											<a class="urllink permissionGeneral-Import_Purchases ar-link" href="#" onclick="showEinvoicePopup('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>','<%=MasterGSTConstants.EINVOICE%>',true)" >ADD CREDIT/DEBIT NOTES</a><div class="right-br-cl" ></div>
											<a class="urllink permissionGeneral-Import_Purchases ar-link" href="#" onclick="showEinvoicePopup('<%=MasterGSTConstants.EXPORTS%>','<%=MasterGSTConstants.EINVOICE%>',true)" >ADD EXPORT INVOICE</a><div class="right-br-cl" ></div>
											<a class="urllink permissionGeneral-Import_Purchases ar-link" href="#" onclick="showEinvoicePopup('<%=MasterGSTConstants.B2C%>','<%=MasterGSTConstants.EINVOICE%>',true)">B2C INVOICE</a>
											<a class="last-link" href="#"></a>
										</div>
									</div>
								</li>--%>
								
								
								<li class="nav-item idDwnldewabillinv returns_dropdown permissionNew_Returns-Ewaybill" id="idewaybill"><a class="nav-link urllink" href="#" link="${contextPath}/ewaybill/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<%=MasterGSTConstants.EWAYBILL%>/<c:out value="${client.id}"/>">EWAY BILL </a></li>
								<li class="nav-item" style="display:none" id="idDocs"><a class="nav-link urllink" href="#" link="${contextPath}/cp_docs/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">DOCS</a></li>
								<li class="nav-item dropdown report-menu-drop" id="idReports">
									<a class="nav-link dropdown-toggle" href="#" id="navbarreportMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">REPORTS</a>
									<div class="returnmenudrop1 dropdown-menu" aria-labelledby="navbarreportMenuLink" id="ReportMenu" style="padding:5px!important">
										<div class="row" style="margin-top: -5px;">
										<a class="first-link" href="#"></a>
										<a class="ar-link urllink permissionReports" href="#" link="${contextPath}/dreports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">REPORTS </a><div class="right-br-cl idTravel" ></div>
										<a class="ar-link urllink permissionReports idTravel" href="#" link="${contextPath}/treports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">TRAVEL</a>
										<a class="last-link" href="#"></a>
										</div>
									</div>
								</li>
								<li class="nav-item dropdown payment-menu-drop">
									<a class="nav-link dropdown-toggle" href="#" id="navbarpaymentsMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">PAYMENT</a>
									<div class="returnmenudrop1 dropdown-menu" aria-labelledby="navbarImportMenuLink" id="navbarpaymentsdrop" style="padding:5px!important">
										<div class="row" style="margin-top: -5px;">
											<a class="first-link" href="#"></a>
											<a class="urllink ar-link" href="#" link="${contextPath}/payments_history/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<%=MasterGSTConstants.GSTR1%>?type=receive">RECEIPTS</a><div class="right-br-cl" ></div>
											<a class="urllink ar-link" href="#" link="${contextPath}/payments_history/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<%=MasterGSTConstants.GSTR2%>?type=made">PAYMENTS</a>
											<a class="last-link" href="#"></a>
										</div>
									</div>
								</li>
								<li class="nav-item dropdown import-menu-drop" id="idMapImports">
									<a class="nav-link dropdown-toggle" href="#" id="navbarimportMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">IMPORT</a>
									<div class="returnmenudrop1 dropdown-menu" aria-labelledby="navbarImportMenuLink" id="ImportMenu" style="padding:5px!important">
										<div class="row" style="margin-top: -5px;">
											<a class="first-link" href="#"></a>
											<a class="urllink permissionGeneral-Import_Sales ar-link" href="#" data-toggle="modal" data-target="#importModal" onclick="updateImportModal('<%=MasterGSTConstants.GSTR1%>')" >SALES INVOICES</a><div class="right-br-cl" ></div>
											<a class="urllink permissionGeneral-Import_Purchases ar-link" href="#" data-toggle="modal" data-target="#importModal" onclick="updateImportModal('Purchase Register')" >PURCHASE INVOICES</a><div class="right-br-cl" ></div>
											<a class="urllink permissionGeneral-Import_Templates ar-link" href="#" link="${contextPath}/imports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=tax" >IMPORT TEMPLATES</a><div class="right-br-cl" ></div>
											<a class="urllink permissionGeneral-Bulk_Imports ar-link" href="#" link="${contextPath}/bulkimports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=tax" >BULK IMPORTS</a>
											<a class="last-link" href="#"></a>
										</div>
									</div>
								</li>
							</ul>
						</div>
						</c:when>
						<c:when test ="${headervalue eq 'allclients'}"><h3><span class="comptxt mt-1">All <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></span></h3><span class="pull-right"><a class="nav-link urllink" id="updates-btn" style="display: inline-block;color: white!important;" href="#" link="${contextPath}/latestupdates/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>">Latest Updates <i class="fa fa-star blink" aria-hidden="true"></i></a></span><span class="pull-right"><a class="nav-link urllink" id="news-btn" style="display: inline-block;color: white!important;" href="#" link="${contextPath}/latestnews/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>">Latest News <i class="fa fa-star blink" aria-hidden="true"></i></a></span></c:when><c:when test ="${clientvalue eq 'addclient'}"><h3><span class="comptxt mt-1">Add new client </span></h3></c:when><c:when test ="${connectorvalue eq 'connectors'}"><h3><span class="comptxt mt-1">Welcome to connectors </span></h3></c:when><c:when test ="${headervalue eq 'dashboardca'}"><h3><span class="comptxt mt-1">Welcome to ${fullname}'s Dashboard</span></h3></c:when><c:otherwise><h3><a id="dashboardbtn" class="nav-link urllink" style="color:white;" href="#" link="${contextPath}/cadb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"> DASHBOARD </a></h3><span class="pull-right"><a class="nav-link urllink" id="updates-btn" style="display: inline-block;color: white!important;" href="#" link="${contextPath}/latestupdates/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>">Latest Updates <i class="fa fa-star blink" aria-hidden="true"></i></a></span><span class="pull-right"><a class="nav-link urllink" id="news-btn" style="display: inline-block;color: white!important;" href="#" link="${contextPath}/latestnews/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>">Latest News <i class="fa fa-star blink" aria-hidden="true"></i></a></span></c:otherwise></c:choose>
						<c:if test='${not empty client}'>
						<c:if test='${not empty client.businessname}'>
							<c:set var = "varfn" value = "${client.firstname}"/>
							<c:set var = "varln" value = "${client.lastname}"/>
							<c:set var = "vargstn" value = "${fn:substring(client.statename, fn:indexOf(client.statename, '-')+1, fn:length(client.statename))}"/>
						<div class="compprofilename">
							<a href="#" class="urllink" link="${contextPath}/about/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">
							<span class="imgsize-wrap-thumb2" style="background-color: #fff;"><c:if test="${not empty client.logoid}"><img src="${contextPath}/getlogo/${client.logoid}" alt="Logo" class="imgsize-thumb" id="clntlogo" style="float: left;"></c:if>
							<c:if test="${empty client.logoid}"><img src="${contextPath}/static/mastergst/images/master/defaultcompany.png" alt="Logo" class="imgsize-thumb" id="clntlogo" style="float: left;"></c:if> </span>
							<h3><span class="comptxt companyname" title="${client.businessname}"><c:choose><c:when test='${fn:length(client.businessname) > 25}'>${fn:substring(client.businessname, 0, 25)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></span><span class="gstidtxt" style="color:#dae1ff">${client.gstnnumber} - <c:choose><c:when test='${fn:length(vargstn) > 22}'>${fn:substring(vargstn, 0, 22)}..</c:when><c:otherwise>${vargstn}</c:otherwise></c:choose></span></h3>
							</a>
							<a href="#" class="urllink" link="${contextPath}/about/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>"><span class="cogsicon" id="config-btn"> <img src="${contextPath}/static/mastergst/images/master/cogs-icon.png" class="cogsiconblue" alt="Cogs" /></span> </a>
							<div class="dropdown dropdown-search">
									  <a class="dropdown-toggle" href="#" type="" id="dropdownMenu" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"></a>
									  <div class="dropdown-menu search" aria-labelledby="dropdownMenu"><form class="filterform" action="#">
											  <div id="dtablesrch" class="dtablesrch"><span class="searchgstin" style="font-size: 13px;color: darkblue;text-transform: uppercase;">Choose your Client</span><span><input type="text" class="form-control srch filterinput" id="searchfilter" placeholder="Search here..." aria-controls="dbTable"></span></div>
									         <ul class="scrollbar listofclients" id="style-n"></ul>
									         <c:if test='${addClient == "true"}'>
									           <a href="${contextPath}/addclient/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${month}"/>/<c:out value="${year}"/>" class="btn btn-blue-dark permissionSettings-Company_Details-Add urladdclient" onclick="addclientinheader()">Add client</a>
									         </c:if>
									   </form></div>
							</div>
						</div>
						<div class="addlist-dd dropdown">
							<button class="btn btn-empty dropdown-toggle" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"> <span class="plusicon"> <img src="${contextPath}/static/mastergst/images/master/plus-symbol.png" alt="Plus" /></span> </button>
							<div class="dropdown-menu dropdown-menu-right" id="sidemenu" aria-labelledby="dropdownMenuButton">
								<div class="slidemenu">
									<ul class="">
									<c:if test='${not empty lGSTReturnsSummury}'>
									<c:forEach items="${lGSTReturnsSummury}" var="GSTReturnsSummury">
									<c:if test='${GSTReturnsSummury.active == "true"}'>
										<c:if test="${GSTReturnsSummury.returntype eq varGSTR1}"><li class="first"><a tabindex="-1" class="urllink permissionInvoices-Sales-Add" href="#" onclick="showInvPopup('<%=MasterGSTConstants.B2B%>','<%=MasterGSTConstants.GSTR1%>',true)" link1="${contextPath}/addsinv/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<%=MasterGSTConstants.GSTR1%>?stype=">ADD SALES INVOICE</a><ul class="slidemenu-sub permissionInvoices-Sales-Add"><li><a tabindex="-1" class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.B2B%>','<%=MasterGSTConstants.GSTR1%>',true)" >ADD SALE INVOICE (B2B/B2C/B2CL)<span class="captiontxt">On outward supply of taxable goods or services</span></a></li><li><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.EXPORTS%>','<%=MasterGSTConstants.GSTR1%>',true)">ADD EXPORT INVOICE <span class="captiontxt">On outward supply of taxable goods or services</span></a></li><li><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>','<%=MasterGSTConstants.GSTR1%>',true)">ADD CREDIT/DEBIT NOTES<span class="captiontxt">On outward supply of taxable goods or services</span></a></li><li><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.NIL%>','<%=MasterGSTConstants.GSTR1%>',true)">ADD BILL OF SUPPLY<span class="captiontxt"> Nil Rated / Exempted / Non-GST Invoice </span></a></li><li><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.ADVANCES%>','<%=MasterGSTConstants.GSTR1%>',true)">ADD ADVANCE RECEIPTS (RECEIPT VOUCHER) <span class="captiontxt">On outward supply of taxable goods or services</span></a></li><li><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.ATPAID%>','<%=MasterGSTConstants.GSTR1%>',true)">ADD ADVANCE ADJUSTED DETAIL <span class="captiontxt">On outward supply of taxable goods or services</span></a></li></ul></li></c:if>
										<c:if test="${GSTReturnsSummury.returntype eq varGSTR2}"><li><a tabindex="-2" class="urllink permissionInvoices-Purchase-Add" href="#" onclick="showInvPopup('<%=MasterGSTConstants.B2B%>','<%=MasterGSTConstants.PURCHASE_REGISTER%>',true)" link1="${contextPath}/addpinv/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<%=MasterGSTConstants.PURCHASE_REGISTER%>?stype=">ADD PURCHASE INVOICE</a><ul class="slidemenu-sub permissionInvoices-Purchase-Add"><li><a tabindex="-2" class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.B2B%>','<%=MasterGSTConstants.PURCHASE_REGISTER%>',true)">Add PURCHASE INVOICE<span class="captiontxt">On inward supply of taxable goods or services</span></a></li><li><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>','<%=MasterGSTConstants.PURCHASE_REGISTER%>',true)">ADD CREDIT/DEBIT NOTES<span class="captiontxt">On inward supply of taxable goods or services</span></a></li><li><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.ADVANCES%>','<%=MasterGSTConstants.PURCHASE_REGISTER%>',true)">ADD ADVANCE PAYMENTS <span class="captiontxt">On inward supply of taxable goods or services</span></a></li><li><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.ATPAID%>','<%=MasterGSTConstants.PURCHASE_REGISTER%>',true)">ADD ADVANCE ADJUSTED DETAIL <span class="captiontxt">On inward supply of taxable goods or services</span></a></li><li><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.IMP_GOODS%>','<%=MasterGSTConstants.PURCHASE_REGISTER%>',true)">ADD IMPORT OF GOODS(BILL OF ENTRY) <span class="captiontxt">On inward supply of taxable goods or services</span></a></li><li><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.IMP_SERVICES%>','<%=MasterGSTConstants.PURCHASE_REGISTER%>',true)">ADD IMPORT OF SERVICES <span class="captiontxt">On inward supply of taxable goods or services</span></a></li><li><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.NIL%>','<%=MasterGSTConstants.PURCHASE_REGISTER%>',true)">ADD BILL OF SUPPLY <span class="captiontxt"> Nil Rated / Exempted / Non-GST Invoice </span></a></li><li><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.ISD%>','<%=MasterGSTConstants.PURCHASE_REGISTER%>',true)">ADD ISD INVOICE <span class="captiontxt">On inward supply of taxable goods or services</span></a></li><li><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.ITC_REVERSAL%>','<%=MasterGSTConstants.PURCHASE_REGISTER%>',true)">ADD ITC REVERSAL <span class="captiontxt">On inward supply of taxable goods or services</span></a></li></ul></li></c:if>
										<c:if test="${GSTReturnsSummury.returntype eq varGSTR4}"><li <c:if test="${client.dealertype eq 'Compound'}"> class="first" </c:if>><a tabindex="-3" class="urllink permissionInvoices-Purchase-Add" href="#" onclick="showInvPopup('<%=MasterGSTConstants.B2B%>','<%=MasterGSTConstants.GSTR4%>',true)" link1="${contextPath}/addpinv/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<%=MasterGSTConstants.PURCHASE_REGISTER%>?stype=">ADD GSTR4 INVOICE</a><ul class="slidemenu-sub permissionInvoices-Purchase-Add"><li><a tabindex="-3" class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.B2B%>','<%=MasterGSTConstants.GSTR4%>',true)">ADD PURCHASE INVOICE<span class="captiontxt">On inward supply of taxable goods or services</span></a></li><li><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.ADVANCES%>','<%=MasterGSTConstants.GSTR4%>',true)">ADD ADVANCE RECEIPTS (RECEIPT VOUCHER) <span class="captiontxt">On inward supply of taxable goods or services</span></a></li><li><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.ATPAID%>','<%=MasterGSTConstants.GSTR4%>',true)">ADD ADVANCE ADJUSTED DETAIL <span class="captiontxt">On inward supply of taxable goods or services</span></a></li><li><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>','<%=MasterGSTConstants.GSTR4%>',true)">ADD CREDIT/DEBIT NOTE <span class="captiontxt">On inward supply of taxable goods or services</span></a></li><li><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.IMP_SERVICES%>','<%=MasterGSTConstants.GSTR4%>',true)">ADD IMPORT OF SERVICES <span class="captiontxt">On inward supply of taxable goods or services</span></a></li></ul></li></c:if>
											<c:if test="${GSTReturnsSummury.returntype eq varGSTR5}"><li <c:if test="${client.dealertype eq 'Casual'}"> class="first" </c:if>><a tabindex="-5" class="urllink permissionInvoices-Sales-Add" href="#" onclick="showInvPopup('<%=MasterGSTConstants.B2B%>','<%=MasterGSTConstants.GSTR5%>',true)" link1="${contextPath}/addsinv/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<%=MasterGSTConstants.GSTR1%>?stype=">ADD GSTR5 INVOICE</a><ul class="slidemenu-sub permissionInvoices-Sales-Add"><li><a tabindex="-5" class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.B2B%>','<%=MasterGSTConstants.GSTR5%>',true)">ADD SALES INVOICE(B2B/B2CL) <span class="captiontxt">On outward supply of taxable goods or services</span></a></li><li><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.B2C%>','<%=MasterGSTConstants.GSTR5%>',true)">ADD B2C SMALL <span class="captiontxt">On outward supply of taxable goods or services</span></a></li><li><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>','<%=MasterGSTConstants.GSTR5%>',true)">Add CREDIT/DEBIT NOTE <span class="captiontxt">On outward supply of taxable goods or services</span></a></li><li><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.IMP_GOODS%>','<%=MasterGSTConstants.GSTR5%>',true)">ADD IMPORT OF GOODS(BILL OF ENTRY) <span class="captiontxt">On outward supply of taxable goods or services</span></a></li></ul></li></c:if>
											<c:if test="${GSTReturnsSummury.returntype eq varGSTR6}"><li <c:if test="${client.dealertype eq 'InputServiceDistributor'}"> class="first" </c:if>><a tabindex="-4" class="urllink permissionInvoices-Purchase-Add" href="#" onclick="showInvPopup('<%=MasterGSTConstants.B2B%>','<%=MasterGSTConstants.GSTR6%>',true)" link1="${contextPath}/addpinv/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<%=MasterGSTConstants.PURCHASE_REGISTER%>?stype=">ADD GSTR6 INVOICE</a><ul class="slidemenu-sub permissionInvoices-Purchase-Add"><li><a tabindex="-4" class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.B2B%>','<%=MasterGSTConstants.GSTR6%>',true)">Add PURCHASE INVOICE<span class="captiontxt">On inward supply of taxable goods or services</span></a></li><li><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>','<%=MasterGSTConstants.GSTR6%>',true)">ADD CREDIT/DEBIT NOTE <span class="captiontxt">On inward supply of taxable goods or services</span></a></li><li><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.ISD%>','<%=MasterGSTConstants.GSTR6%>',true)">ADD ISD INVOICE <span class="captiontxt">On inward supply of taxable goods or services</span></a></li></ul></li></c:if>
											<c:if test="${GSTReturnsSummury.returntype eq varGSTR3B}">
											<li><a tabindex="-6" class="urllink permissionGSTR3B-GSTR3B_Invoices" href="#" link="${contextPath}/addsupinvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">ADD GSTR3B</a><ul class="slidemenu-sub"> <li><a tabindex="-6" class="urllink permissionGSTR3B-GSTR3B_Invoices" href="#" link="${contextPath}/addsupinvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">ADD SUPPLIES<span class="captiontxt">On inward supply of taxable goods or services</span></a></li><li><a class="urllink permissionGSTR3B-GSTR3B_Invoices" href="#" link="${contextPath}/addsupinvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">ADD INTER-STATE SUPPLIES<span class="captiontxt">On inward supply of taxable goods or services</span></a></li><li><a class="urllink permissionGSTR3B-GSTR3B_Invoices" href="#" link="${contextPath}/addsupinvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">ADD ELIGIBLE ITC<span class="captiontxt">On inward supply of taxable goods or services</span></a></li><li style="height:65px!important"><a class="urllink permissionGSTR3B-GSTR3B_Invoices" href="#" link="${contextPath}/addsupinvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Add EXEMPT, NIL-RATES AND NON-GST INWARD SUPPLIES<span class="captiontxt">On inward supply of taxable goods or services</span></a></li><li><a class="urllink permissionGSTR3B-GSTR3B_Invoices" href="#" link="${contextPath}/addsupinvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">ADD INTEREST & LATE FEE PAYABLE<span class="captiontxt">On inward supply of taxable goods or services</span></a></li><li><a class="urllink permissionGSTR3B-GSTR3B_Invoices" href="#" link="${contextPath}/addsupinvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">ADD OFFSET LIABILITY<span class="captiontxt">On inward supply of taxable goods or services</span></a></li></ul></li>
											</c:if>
											</c:if>
											</c:forEach>
											</c:if>
											<li class="ideinvoice"><a class="urllink" href="#" onclick="showEinvoicePopup('<%=MasterGSTConstants.B2B%>','<%=MasterGSTConstants.EINVOICE%>',true)">ADD EINVOICE</a><ul class="slidemenu-sub"><li><a tabindex="-1" class="urllink" href="#" onclick="showEinvoicePopup('<%=MasterGSTConstants.B2B%>','<%=MasterGSTConstants.EINVOICE%>',true)" >ADD B2B INVOICE<span class="captiontxt">On outward supply of taxable goods or services</span></a></li><li><a tabindex="-1" class="urllink" href="#" onclick="showEinvoicePopup('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>','<%=MasterGSTConstants.EINVOICE%>',true)" >ADD CREDIT/DEBIT NOTES<span class="captiontxt">On outward supply of taxable goods or services</span></a></li><li><a tabindex="-1" class="urllink" href="#" onclick="showEinvoicePopup('<%=MasterGSTConstants.EXPORTS%>','<%=MasterGSTConstants.EINVOICE%>',true)" >ADD EXPORT INVOICE<span class="captiontxt">On outward supply of taxable goods or services</span></a></li><li><a tabindex="-1" class="urllink" href="#" onclick="showEinvoicePopup('<%=MasterGSTConstants.B2C%>','<%=MasterGSTConstants.EINVOICE%>',true)" >ADD B2C INVOICE<span class="captiontxt">On outward supply of taxable goods or services</span></a></li> </ul></li>
											<li class="additionalinv"><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.B2B%>','<%=MasterGSTConstants.EWAYBILL%>',true)">ADD EWAYBILL</a><ul class="slidemenu-sub"> </ul></li>
											<li class="additionalinv"><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.DELIVERYCHALLANS%>','<%=MasterGSTConstants.DELIVERYCHALLANS%>',true)">ADD DELIVERY CHALLANS</a><ul class="slidemenu-sub"> </ul></li><li class="additionalinv"><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.PROFORMAINVOICES%>','<%=MasterGSTConstants.PROFORMAINVOICES%>',true)">ADD PROFORMA INVOICES</a><ul class="slidemenu-sub"> </ul></li><li class="additionalinv"><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.ESTIMATES%>','<%=MasterGSTConstants.ESTIMATES%>',true)" >ADD ESTIMATES</a><ul class="slidemenu-sub"> </ul></li><li class="additionalinv"><a class="urllink" href="#" onclick="showInvPopup('<%=MasterGSTConstants.PURCHASEORDER%>','<%=MasterGSTConstants.PURCHASEORDER%>',true)">ADD PURCHASE ORDER</a><ul class="slidemenu-sub"> </ul></li>
											<li><a class="permissionSettings-Customers-Add" tabindex="-7" href="#">ADD ADDITIONAL DETAILS</a><ul class="slidemenu-sub"> <li><a tabindex="-7" class="urllink permissionSettings-Customers-Add" href="#" link="${contextPath}/cp_customers/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">ADD CUSTOMER <span class="captiontxt">You can add all Customers here</span></a></li><li><a class="urllink permissionSettings-Suppliers-Add" href="#" link="${contextPath}/cp_suppliers/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">ADD SUPPLIER <span class="captiontxt">You can add all Suppliers here</span></a></li><li><a class="urllink permissionSettings-Items-Add" href="#" link="${contextPath}/cp_items/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">ADD ITEM(PRODUCTS/SERVICES) <span class="captiontxt">Add/Import all Items here</span></a></li><li id="cpUser"><a class="urllink permissionSettings-Users-Add" href="#" link="${contextPath}/cp_user/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">ADD NEW USER <span class="captiontxt">Add New User to provide new access</span></a></li><li><a class="urllink permissionSettings-Roles-Add" href="#" link="${contextPath}/cp_role/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">ADD ROLE <span class="captiontxt">Add Role here to provide role based access</span></a></li><li><a class="urllink permissionSettings-Bank_Details-Add" href="#" link="${contextPath}/cp_bankDetails/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">ADD BANK DETAILS <span class="captiontxt">Configure Bank Details to show in invoices</span></a></li><li><a class="urllink permissionSettings-Bank_Details-Add" href="#" link="${contextPath}/cp_Accounting/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=group">ADD ACCOUNTING DETAILS <span class="captiontxt">Configure Accounting Details</span></a></li><li><a class="urllink permissionSettings-Bank_Details-Add" href="#" link="${contextPath}/cp_ecommerce/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">ADD E-COMMERCE OPERATOR <span class="captiontxt">Configure E-Commerce Operator Details to show in invoices</span></a></li></ul></li>
											<li><a class="permissionSettings-Customers-Add" tabindex="-8" href="#">ADD ACCOUNTING ENTRIES</a><ul class="slidemenu-sub"> 	<li><a tabindex="-8" class="urllink permissionSettings-Customers-Add" href="#" data-toggle="modal" data-target="#voucherModal">ADD VOUCHER <span class="captiontxt">You can add all Vouchers here</span></a></li><li><a class="urllink permissionSettings-Suppliers-Add" href="#" data-toggle="modal" data-target="#ContraModal">ADD CONTRA <span class="captiontxt">You can add Contra here</span></a></li><li><a class="urllink permissionSettings-Suppliers-Add" href="#" data-toggle="modal" onclick="addLedger('','ledger','addLedgerModal')">ADD LEDGER <span class="captiontxt">You can add Ledger here</span></a></li></ul> </li>
											<li class="additionalinv"><a href="#" class="urllink" link="${contextPath}/about/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">SETTINGS</a><ul class="slidemenu-sub"> </ul></li>
										</ul>
									</div></div></div></c:if></c:if></div></div>
				<div class="alert alert-success gst-notifications" role="alert"> <img src="${contextPath}/static/mastergst/images/errors/success-alert.png" alt="Alert" class="mr-2" onclick="closeNotifications()"/><span id="successMessage"></span> <img src="${contextPath}/static/mastergst/images/errors/danger-alert.png" alt="Alert" class="pull-right" onclick="closeNotifications()" /> </div><div class="alert alert-warning gst-notifications" role="alert"> <img src="${contextPath}/static/mastergst/images/errors/success-alert.png" alt="Alert" class="mr-2" onclick="closeNotifications()"/><span id="successMessage"></span> <img src="${contextPath}/static/mastergst/images/errors/danger-alert.png" alt="Alert" class="pull-right" onclick="closeNotifications()" /> </div><div class="alert alert-danger gst-notifications" role="alert"> <img src="${contextPath}/static/mastergst/images/errors/danger-alert.png" alt="Alert" class="mr-2" onclick="closeNotifications()" /><span id="errorMessage"></span> <img src="${contextPath}/static/mastergst/images/errors/danger-alert.png" alt="Alert" class="pull-right" onclick="closeNotifications()" /> </div>
			</div></div>
	<div class="modal fade" id="importModal" role="dialog" aria-labelledby="importModal" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-right" role="document">
            <div class="modal-content" id="idImportBody">
                <div class="modal-body">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span></button>
                    <div class="invoice-hdr bluehdr"><h3 id="importModalTitle">Import Invoice</h3></div>
					
					<div class="row p-3">
					<div class="errormsg col-md-12" id="templateType_Msg" style="position:relative;left:49%;"></div>
						<div class="pl-5 pr-5">
						<form:form method="POST" class="meterialform" id="importModalForm" action="${contextPath}/uploadInvoice/${id}/${fullname}/${usertype}/${client.id}/<%=MasterGSTConstants.GSTR1%>/${month}/${year}/mastergst" enctype="multipart/form-data">
						<div class="row meterialform">
						<div class="col-md-4 p-0 mb-2 text-right"><h6><span class="astrich imp"></span>Select Template Type : </h6></div><div class="col-md-6"><select id="templateType" name="templatetype" onchange="updateInvoiceTypeUrl(this.value)" style="width:90%"></select></div>
						<div class="col-md-4 p-0 mb-2 text-right"><h6 class="vert_branch">Branch : </h6></div><div class="col-md-6"><select  name="branch" style="width:90%"><option value="">- Branch -</option><c:forEach items="${client.branches}" var="branch"><option value="${branch.name}">${branch.name}</option></c:forEach></select></div>
						<div class="col-md-4 p-0 mb-2 text-right"><h6 class="vert_branch vert_text">Vertical : </h6></div><div class="col-md-6"><select name="vertical" style="width:90%"><option value="">- Vertical -</option><c:forEach items="${client.verticals}" var="vertical"><option value="${vertical.name}">${vertical.name}</option></c:forEach></select></div>
						<div class="col-md-6 p-0 mt-2 text-right impFinYear" style="display:none;"><h6><span id="spantxt">Do You have Any B2C Invoices / HSN Summary ? : </span></h6></div>
						<div class="form-group-inline b2cradio" style="display:none;">
								<div class="form-radio">
									<div class="radio">
										<label><input name="submiton" id="submiton" type="radio" value="Yes" /><i class="helper"></i>Yes</label>
									</div>
								</div>
								<div class="form-radio">
									<div class="radio">
										<label><input name="submiton" id="subon" type="radio" value="No"/><i class="helper"></i>No</label>
									</div>
								</div>
							</div>
						<!-- <p id="tallyb2c" style="display:none;color:green;">(For B2C invoices Tally template doesn't provide invoice date, So please select Financial Year & Month.)</p> -->
						<p id="tallyb2c" style="display:none;color:green;">(For B2C invoices / HSN Summary template doesn't provide invoice date, So please select Financial Year & Month.)</p>
						<div class="col-md-4 p-0 mb-2 text-right timpFinYear" style="display:none;"><h6 class="vert_branch vert_text">Financial Year : </h6></div><div class="col-md-6 timpFinYear" style="display:none;"><select name="tallyfinancialYear" class="impTallyFinYear" onchange="updateImpFinacialYear(this.value)" style="width:90%"><option value="2021">2021 - 2022</option><option value="2020">2020 - 2021</option><option value="2019">2019 - 2020</option><option value="2018">2018 - 2019</option><option value="2017">2017 - 2018</option></select></div>
						<div class="col-md-4 p-0 mb-2 text-right timpFinYear" style="display:none;"><h6 class="vert_branch vert_text">Month : </h6></div><div class="col-md-6 timpFinYear" style="display:none;"><select name="tallymonth" class="impTallyMonth" onchange="updateImpMonth(this.value)" style="width:90%"><option value="4">April</option><option value="5">May</option><option value="6">June</option><option value="7">July</option><option value="8">August</option><option value="9">September</option><option value="10">October</option><option value="11">November</option><option value="12">December</option><option value="1">January</option><option value="2">February</option><option value="3">March</option></select></div>
						<div id="b2cstallytemplate" style="color:red;"></div>
					</div>
						<div class="row">
						<fieldset style="width:  100%;">
                              <div class="filedragwrap" onclick="choosefileSheets()">
              <div id="filedrag1" style="display: block;">
                <input type="hidden" id="MAX_FILE_SIZE" name="MAX_FILE_SIZE" value="300000">
                <div class="filedraginput"> <span id="chosefile-cnt">Choose File</span><input type="file" name="file" id="fileselect1" accept="application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" onchange="updateSheets()">  </div>
                <div class="ortxt"> --( OR )--</div> <div id="filedrag1" >Drop files here</div>
              </div> </div>
            </fieldset>	<div id="hsnsumtallytemplate">Note : Hsn Summary will be update</div>
            <div id="b2cstallytemplate" style="color:red;"></div>
							<div class="form-group col-md-12 col-sm-12" id="idSheet" style="display:none;">
								<p class="lable-txt">File Name : <span id="messages"></span> </p><div class="">&nbsp;</div>
								<div id="mastergstSheet" class="sheets"><div class="form-check form-check-inline" style="position:inherit;"><input class="form-check-input" type="checkbox" id="invoiceSheet" value="invoiceList" name='list' checked="checked" /><label for="invoiceSheet"><span class="ui"></span></label> <span class="labletxt">Invoices</span> </div><div class=""></div></div>
								<div id="tallySheet" class="sheets">
									<div class="form-check form-check-inline" style="position:initial;"><input class="form-check-input" type="checkbox" id="b2bSheet" value="b2bList" name='list' checked="checked" /><label for="b2bSheet"><span class="ui"></span></label> <span class="labletxt">B2B</span></div>
									<div class="form-check form-check-inline" style="position:initial;"><input class="form-check-input" type="checkbox" id="b2baSheet" value="b2baList" name='list' checked="checked" /><label for="b2baSheet"><span class="ui"></span></label> <span class="labletxt">B2BA</span></div>
									<div class=""></div>
									<div class="form-check form-check-inline" style="position:initial;"><input class="form-check-input" type="checkbox" id="b2cSheet" value="b2cList" name='list' checked="checked" /><label for="b2cSheet"><span class="ui"></span></label> <span class="labletxt">B2C</span></div>
									<div class="form-check form-check-inline" style="position:initial;"><input class="form-check-input" type="checkbox" id="b2csaSheet" value="b2csaList" name='list' checked="checked" /><label for="b2csaSheet"><span class="ui"></span></label> <span class="labletxt">B2CSA</span></div>
									<div class=""></div>
									<div class="form-check form-check-inline" style="position:initial;"><input class="form-check-input" type="checkbox" id="b2clSheet" value="b2clList" name='list' checked="checked" /><label for="b2clSheet"><span class="ui"></span></label> <span class="labletxt">B2CL</span></div><div class=""></div>
								</div>
								<div class="form-check form-check-inline" style="position:initial;"><input class="form-check-input" type="checkbox" id="b2clSheet" value="b2claList" name='list' checked="checked" /><label for="b2claSheet"><span class="ui"></span></label> <span class="labletxt">B2CLA</span></div><div class=""></div><!-- </div> -->
								<div class="form-check form-check-inline" style="position:initial;"><input class="form-check-input" type="checkbox" id="creditSheet" value="creditList" name='list' checked="checked" /><label for="creditSheet"><span class="ui"></span></label> <span class="labletxt">Credit/Debit Notes</span></div><div class=""></div>
								<div class="form-check form-check-inline" style="position:initial;"><input class="form-check-input" type="checkbox" id="cdnraSheet" value="cdnraList" name='list' checked="checked" /><label for="cdnraSheet"><span class="ui"></span></label> <span class="labletxt">CDNA</span></div><div class=""></div>
								<div class="form-check form-check-inline" style="position:initial;"><input class="form-check-input" type="checkbox" id="advanceSheet" value="advReceiptList" name='list' checked="checked" /><label for="advanceSheet"><span class="ui"></span></label> <span class="labletxt">Advances</span></div><div class=""></div>
								<div class="form-check form-check-inline" style="position:initial;"><input class="form-check-input" type="checkbox" id="ataSheet" value="ataList" name='list' checked="checked" /><label for="ataSheet"><span class="ui"></span></label> <span class="labletxt">ATA</span></div><div class=""></div>
								<div class="form-check form-check-inline" style="position:initial;"><input class="form-check-input" type="checkbox" id="advAdjustedSheet" value="advAdjustedList" name='list' checked="checked" /><label for="advAdjustedSheet"><span class="ui"></span></label> <span class="labletxt">Advance Adjusted Detail</span></div><div class=""></div>
								<div class="form-check form-check-inline" style="position:initial;"><input class="form-check-input" type="checkbox" id="atadjaSheet" value="txpaList" name='list' checked="checked" /><label for="atadjaSheet"><span class="ui"></span></label> <span class="labletxt">TXPA</span></div><div class=""></div>
								<div class="form-check form-check-inline" style="position:initial;"><input class="form-check-input" type="checkbox" id="nilSheet" value="nilList" name='list' checked="checked" /><label for="nilSheet"><span class="ui"></span></label> <span class="labletxt">Nil Supplies</span></div><div class=""></div>
								<div class="form-check form-check-inline" style="position:initial;"><input class="form-check-input" type="checkbox" id="cdnurSheet" value="cdnurList" name='list' checked="checked" /><label for="cdnurSheet"><span class="ui"></span></label> <span class="labletxt">Credit/Debit Note for Unregistered</span></div><div class=""></div>
								<div class="form-check form-check-inline" style="position:initial;"><input class="form-check-input" type="checkbox" id="cdnuraSheet" value="cdnuraList" name='list' checked="checked" /><label for="cdnuraSheet"><span class="ui"></span></label> <span class="labletxt">CDNURA</span></div><div class=""></div>
								<div class="form-check form-check-inline purchase_template" style="position:initial;"><input class="form-check-input" type="checkbox" id="b2buSheet" value="b2buList" name='list' checked="checked" /><label for="b2buSheet"><span class="ui"></span></label> <span class="labletxt">B2B Unregistered</span></div><div class=""></div>
								<div class="form-check form-check-inline purchase_template" style="position:initial;"><input class="form-check-input" type="checkbox" id="impGoodsSheet" value="impgList" name='list' checked="checked" /><label for="impGoodsSheet"><span class="ui"></span></label> <span class="labletxt">Import Goods</span></div><div class=""></div>
								<div class="form-check form-check-inline purchase_template" style="position:initial;"><input class="form-check-input" type="checkbox" id="impServicesSheet" value="impsList" name='list' checked="checked" /><label for="impServicesSheet"><span class="ui"></span></label> <span class="labletxt">Import Services</span></div><div class=""></div>
								<div class="form-check form-check-inline purchase_template" style="position:initial;"><input class="form-check-input" type="checkbox" id="itcRvslSheet" value="itrvslList" name='list' checked="checked" /><label for="itcRvslSheet"><span class="ui"></span></label> <span class="labletxt">ITC Reversal</span></div><div class=""></div>
								<div class="form-check form-check-inline sales_template" style="position:initial;"><input class="form-check-input" type="checkbox" id="exportSheet" value="exportList" name='list' checked="checked" /><label for="exportSheet"><span class="ui"></span></label> <span class="labletxt">Exports</span></div><div class=""></div>
								<div class="form-check form-check-inline sales_template" style="position:initial;"><input class="form-check-input" type="checkbox" id="expaSheet" value="expaList" name='list' checked="checked" /><label for="expaSheet"><span class="ui"></span></label> <span class="labletxt">EXPA</span></div>
							</div>
							<div class="row alert alert-danger" id="importerr" role="alert" style="display:none;"> <img src="${contextPath}/static/mastergst/images/errors/danger-alert.png" alt="alert" class="mr-2" /><span id="importSummaryError"></span></div>
							<div class="form-group col-4"><input type="button" class="btn btn-blue-dark" onClick="performImport(this)" value="Import Invoices"/></div><div class="form-group col-12" id="msgImportProgress" style="display:none;">We are importing your invoices. It may take few seconds based on your network bandwidth. Please wait.. </div>
						</div>
						</form:form>
						</div>
						<div class="mt-4"><div class="form-group col-md-12 col-sm-12"><div class="rightside-wrap"><h5>Download Templates</h5><ul><li class="col-12 sales_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/MasterGST_Sales_Invoices_Template.xls">MasterGST Sales Template</a></li> <li class="col-12 sales_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Tally_Sales_Template.xls">Tally Sales Template</a> </li><li class="col-12 sales_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Tally_Sales_Prime_Template.xls">Tally Sales Prime Template</a> </li><li class="col-12 sales_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Tally_Sales_Template_v17.xls">Tally Sales Template_V1.7</a> </li><li class="col-12 sales_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/GSTR1_Offline_Utility.xlsx">GSTR1 Offline Template</a> </li><li class="col-12 sales_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Sage_Sales_Invoices_Template.xlsx">Sage Sales Template</a></li> <li class="col-12 purchase_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/MasterGST_Purchase_Invoices_Template.xls">MasterGST Purchase Template</a></li><li class="col-12 purchase_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Tally_Purchase_Invoices_Template.xlsx">Tally Purchase Template</a></li><li class="col-12 purchase_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Sage_Purchase_Invoices_Template.xlsx">Sage Purchase Template</a></li><li class="col-12 purchase_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Single_Sheet_Purchase_Invoices_Template.xlsx">Single Sheet Template of Purchase Invoice</a></li>
						<li class="col-12 sales_template fhpl_template fhpl_import_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/MasterGST_Sales_Invoices_Template_with_Additional_Fields.xls">MasterGST Sales Template with Additional Fields</a></li><li class="col-12 sales_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/MasterGST_Sales_Invoices_Template_with_All_Fields.xls">MasterGST Sales Template with All Fields</a></li></ul></div></div></div>
            </div>
        </div>
    </div>
	<div class="modal-content" id="idImportSummary" style="display:none;">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/dashboard-ca/closeicon.png" alt="Close" /></span> </button>
        <div class="invoice-hdr bluehdr"><h3>Import Invoice Summary </h3></div> <div class="">&nbsp;</div>
		<div class="col-11 m-auto">
        <p>We have processed all your invoices and find the below Import invoices Summary, for Failed invoices you can download from the error log and correct the invoices and re-import again.</p>
          <table width="100%" border="0" cellspacing="0" cellpadding="5" class="table-imports table table-sm table-bordered table-hover"><thead><tr>
            	<th></th>
            	<th colspan="2">Totals</th>
            	<th colspan="2">Imported</th>
            	<th colspan="2">Failed</th>
            </tr>
              <tr>
                <th>Invoice Name</th>
                <th>Line Items</th>
                <th>Invoices</th>
                <th>Line Items</th>
                <th>Invoices</th>
                <th>Line Items</th>
                <th>Invoices</th>
              </tr></thead> <tbody id="importSummaryBody"></tbody> </table>
          <table width="100%" border="0" cellspacing="0" id="errorXls" cellpadding="5" class="table table-inverse" style="display:none"><tr><td>Download Error Log file <a class="ml-3" href="${contextPath}/geterrorxls"><i class="fa fa-file-excel-o"></i> Error Sheet</a></td><td class="redtxt"></td></tr></table>
        </div>
      </div>
      <div class="modal-footer"><c:if test="${empty returntype}"><a href="#" class="btn btn-blue-dark urllink" link="${contextPath}/ccdb/${id}/${fullname}/${usertype}/${client.id}?type=change">Continue..</a> <a href="#" class="btn btn-secondary urllink" link="${contextPath}/ccdb/${id}/${fullname}/${usertype}/${client.id}?type=change">Close</a></c:if><c:if test="${not empty returntype}"><a href="#" class="btn btn-blue-dark urllink" link="${contextPath}/alliview/${id}/${fullname}/${usertype}/${client.id}/${returntype}?type=prchse">Continue..</a><a href="#" class="btn btn-secondary urllink" link="${contextPath}/alliview/${id}/${fullname}/${usertype}/${client.id}/${returntype}?type=prchse">Close</a></c:if> </div>
    </div>
	</div>
</div>
 <!-- Add Expense Modal Start-->
   <div class="modal fade" id="addExpenseModal" role="dialog" aria-labelledby="addExpenseModal" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-right" role="document">
            <div class="modal-content" id="">
            <div class="modal-header p-0">
             		<button type="button" class="close" onclick="closeExpenseModal('addExpenseModal')"><span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span></button>
                    <div class="invoice-hdr bluehdr"style="width:100%"><h3 id="">Add Expenses</h3></div>
            </div>
                <div class="modal-body meterialform popupright bs-fancy-checks">
				
						<form method="POST" class="meterialform" id="addExpenceModalForm" action="${contextPath}/saveExpenses/${id}/${fullname}/${usertype}/${client.id}/<%=MasterGSTConstants.EXPENSES%>/${month}/${year}" modelAttribute="expense">
								 <div class="row pt-4 pl-5 pr-5">
								 <div class="row col-md-12 pr-0">
								 <div class="col-md-12 col-sm-12 pr-0"><div class="pull-right balancedue"><label class="bold-txt">Total Expenses</label><h1><i class="fa fa-rupee"></i> <span id="idExpenseTotal" class="indformat indformat_roundoff">0.00</span></h1></div></div>
								 <div class="form-group col-md-6 col-sm-12 p-0">
												<span class="errormsg" id="itemNumber_Msg"></span>
												<div class="row p-0">
												<p class="lable-txt col-md-4 mt-3">Payment Mode</p>
												<div class="col-md-6 pl-0">
												<select class="expense_pmntMode" id="expense_pmntMode" name="paymentMode" onchange="changePaymentMode()" required="required">
												<option value="">- Select -</option>
												<option value="Cash">Cash</option>
												<option value="Bank">Bank</option>
												<option value="Cheque">Cheque</option>
												</select>
												<label for="input" class="control-label" ></label>
												<div class="help-block with-errors"></div>
												<i class="bar"></i> 
												</div>
												</div>
										</div>
										<div class="form-group col-md-6 col-sm-12 p-0 expLedgerNameDiv">
												<span class="errormsg" id="ledgerName_Msg"></span>
												<div class="row p-0">
												<div class="col-md-2"></div>
												<p class="lable-txt col-md-4 mt-3 pl-4 expenseLdgrNameTxt">Ledger Name</p>
												<div class="col-md-6 pl-0">
												<input type="text" class="expense_ledgerName" id="expense_ledgerName" placeholder="Ledger Name" name="ledgerName"/>
												<label for="input" class="control-label" ></label>
												<div class="help-block with-errors"></div>
												<i class="bar"></i> 
												</div>
												</div>
										</div>
										<div class="form-group col-md-6 col-sm-12 p-0">
												<span class="errormsg" id="itemNumber_Msg"></span>
												<div class="row p-0">
												<p class="lable-txt col-md-4 mt-3 expensePmntDateText">Payment Date</p>
												<div class="col-md-6 pl-0">
												<input type="text" class="expense_pmntDate" id="expense_pmntDate" name="paymentDate" data-date-format="dd/mm/yy" required="required" placeholder="DD/MM/YYYY" />
												<span style="position: absolute;top: 20%;right: 9%;"><i class="fa fa-calendar pmntdatewrap"></i> </span>
												<label for="input" class="control-label"></label>
												<div class="help-block with-errors"></div>
												<i class="bar"></i> 
												</div>
												</div>
										</div>
										<div class="form-group col-md-6 col-sm-12 p-0">
												<span class="errormsg" id="itemNumber_Msg"></span>
												<div class="row p-0">
												<div class="col-md-2"></div>
												<p class="lable-txt col-md-4 mt-3 pl-4 expBranchText">Branch</p>
												<div class="col-md-6 pl-0">
												<select class="expense_branch" id="expense_branch" name="branchName"></select>
												<label for="input" class="control-label"></label>
												<div class="help-block with-errors"></div>
												<i class="bar"></i> 
												</div>
												</div>
										</div>
										</div><div class=" col-md-6 pl-3 pr-3"></div><div class=" col-md-6 pr-1 mb-2 text-right"><input type='button' class='btn btn-sm btn-blue-dark' value='Add New Expense' onclick='updatecategory()'></div>
								 		<div class="row pl-3 pr-3 ml-1 mr-1  categorymodule" id="categorymodule1">
			                    	    <div class="form-group col-md-12 col-sm-12">
												<span class="errormsg" id="itemNumber_Msg"></span>
												<div class="row p-0">
												<p class="lable-txt col-md-3 p-0 mt-3">Expense Category</p>
												<div class="col-md-6 pl-0 pr-0">
												<input type="text" class="expenseCategory expenseCategory1" id="expenseCategory1" name="expenses[0].category" required="required" data-error="Please enter more than 3 characters" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" placeholder="Category" />
												<div id='remainexempt_categoryempty1' style='display:none'><div class='expenseGroupbox dbbox' style="width:100%;">
							                  		<p>Please add new Category</p>
							                  		<input type='button' class='btn btn-sm btn-blue-dark' value='Add New Category' data-toggle='modal' onclick='updateGroups(1)' style="width:45%;">
							                  	</div></div>
							                  	<label for="input" class="control-label" ></label>
												<div class="help-block with-errors"></div>
												<i class="bar"></i> 
												</div>
												<div class="col-md-3 pl-3 pr-0" style="text-align: end;"><a href="javascript:void(0)" class="item_delete expitem_delete1" onclick="delete_category(1)" style="vertical-align: bottom;line-height: 1px; display:none;"><span class="fa fa-trash-o gstr2adeletefield expensecategorydelete"></span></a></div>
												<!-- <div class=" col-md-3 pl-3 pr-3"><input type='button' class='btn btn-sm btn-blue-dark' value='Add New' onclick='updatecategory()'></div> -->
												</div>
										</div>
										
										<div class="col-md-12 mt-4 p-0">
										<table width="100%" id="expencetable1" border="0" cellspacing="0" cellpadding="5" class="table-imports table table-sm table-bordered table-hover expencetable">
											<thead>
												<tr>
									            	<th class="text-center" width="50%">Expense Item Name</th>
									            	<th class="text-center">Quantity</th>
									            	<th class="text-center">Rate</th>
									            	<th class="text-center">Total Amount</th>
									            	<th></th>
									            </tr>
									        </thead> 
								            <tbody id="expencetablebody1">
								              	<tr id="expencerow1">
								              		<td ><input type="text" class="form-control expenceitem epxname11" id="exp_item_name11" placeholder="Expense Item Name" name="expenses[0].expensesList[0].itemName"></td>
								              		<td ><input type="text" class="form-control text-right expqtyVal expqty11" id="exp_item_quantity11" name="expenses[0].expensesList[0].quantity" onkeyup="findExpenseTaxable(1,1)"></td>
								              		<td ><input type="text" class="form-control  text-right exprateVal exprate11" id="exp_item_rate11" placeholder="Rate" name="expenses[0].expensesList[0].rate" onkeyup="findExpenseTaxable(1,1)"></td>
								              		<td class="tablegreybg"><input type="text" class="form-control  text-right exptot11 indformat" id="exp_item_total11" name="expenses[0].expensesList[0].total" readonly></td>
								              		<td align="center"><a href="javascript:void(0)" class="expense_delete expense_delete11 expdel11" onclick="deleteexpence_row(1,1)" style="display:none;"> <span class="fa fa-trash-o gstr2adeletefield"></span></a></td>
								              	</tr>
								            </tbody> 
								            <tfoot>
								            	<tr>
								            		<td class="text-right tfootwitebg" colspan="3"><span class="add pull-left" id="addExpenseRow" onclick="add_expencerow(1)"><i class="add-btn">+</i> Add another row</span>Total Amount</td>
								            		<td class="text-right indformat" id="expenseTot1" style="background-color: #fffbf5!important;">0.00</td>	
								            		<td class="tfootwitebg addbutton" style="display: table-cell;"> <span class="add add-btn" id="addrow" onclick="add_expencerow(1)">+</span></td>							            	
								            	</tr>
								            </tfoot>
							              </table>
							              </div>
							              </div>
							              <div class="form-group col-md-12 col-sm-12 p-0">
												<span class="errormsg" id="mrpPriceForB2B_Msg"></span>
												<p class="lable-txt pb-2">Remarks / Comments</p>
												<textarea rows="4" cols="4" id="exp_comments" name="comments" style="border: 1px solid lightgray;border-radius: 4px;resize: auto;"></textarea>
												<label for="input" class="control-label"></label>
												<div class="help-block with-errors"></div> 
											</div>
								</div>
								<input type="hidden" name="id" id="expense_id" value="">
						</form>
            </div>
            <div class="modal-footer text-right" style="display:block;border-top: 1px solid lightgray;">
            		<input type="submit" class="btn btn-blue-dark submit-expence-form hidden ml-3 mt-3" value="Save" style="padding: 4px 5px;opacity: 0;">
					<input type="button" class="btn btn-blue-dark m-0"  onclick="labelexpencesubmit()" value="Save">
					<a href="#" class="btn btn-blue-dark ml-2" onclick="closeExpenseModal('addExpenseModal')">Cancel</a>
				</div>
        </div>
    </div>
	
	</div>
   
   <!--Add Expense Modal End  -->
<script type="text/javascript">
var genEbill = true,dwndEbill = true,canEbill =true,uptVehicle = true,gnIRN = true,canIRN =  true;var rowno=1;var categoryNo=1;
var voucherList=new Array();var contraList=new Array();var ipaddressscript = "";var monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];var filingOption = '<c:out value="${client.filingOption}"/>';var globaluser='<c:out value="${fullname}"/>';var varRetType='<c:out value="${returntype}"/>';var contextPath='<c:out value="${contextPath}"/>';var urlSuffix='/${id}/${client.id}/${returntype}';var commonSuffix='/${id}/${fullname}/${usertype}';var subuseremailSuffix='/${usertype}/${id}';var urlSuffixs='/${id}/${client.id}';var clientId='<c:out value="${client.id}"/>';var client_Type='<c:out value="${client.clienttype}"/>';var userId='<c:out value="${id}"/>';var varUserType='<c:out value="${usertype}"/>';var commonturnOverSuffix='/${id}/${fullname}/${usertype}/${client.id}';var pmntsSuffix='${fullname}/${usertype}/${month}/${year}';var paymenturlSuffix='/${id}/${fullname}/${usertype}/${clientid}';var Paymenturlprefix='${month}/${year}';var invoicesTotalAmount,purchseinvoicesTotalAmount;
	$(function() {
		$('body').tooltip({
            container: 'body',
            trigger: 'hover',
            html: true,
            animation: false,
            selector: '[data-toggle="tooltip"]'        
		});
		expenseBranches();
		
		if(client_Type == "UnRegistered"){
			$('.returns_dropdown').css("display","none");
			$('.unregisteredClient').css("display","block");
		}else{
			$('.unregisteredClient').css("display","none");
		}
		var date = new Date();month = '<c:out value="${month}"/>';year = '<c:out value="${year}"/>';
		if(month == null || month == '') {month = date.getMonth()+1;year = date.getFullYear();}
		var mindat = "+0m";
		if(((filingOption == '<%=MasterGSTConstants.FILING_OPTION_QUARTERLY%>')	&& ('<c:out value="${paymentreturnType}"/>' == '<%=MasterGSTConstants.GSTR1%>')) || ((filingOption == '<%=MasterGSTConstants.FILING_OPTION_QUARTERLY%>') && ('<c:out value="${paymentreturnType}"/>' == '<%=MasterGSTConstants.GSTR4%>'))) {
		 mindat = "+2m";
		}
		var dateValue = ((''+month).length<2 ? '0' : '') + month + '-' + year;
		var date = $('#datetimepicker').datepicker({
			autoclose: true,
			viewMode: 1,
			minViewMode: 1,
			format: 'mm-yyyy',
			orientation: 'right',
			endDate: mindat,
			beforeShowMonth: function (date){
				if(((filingOption == '<%=MasterGSTConstants.FILING_OPTION_QUARTERLY%>')	&& ('<c:out value="${paymentreturnType}"/>' == '<%=MasterGSTConstants.GSTR1%>')) || ((filingOption == '<%=MasterGSTConstants.FILING_OPTION_QUARTERLY%>') && ('<c:out value="${paymentreturnType}"/>' == '<%=MasterGSTConstants.GSTR4%>'))) {
					if(date.getMonth() == 0 || date.getMonth() == 1	|| date.getMonth() == 3	|| date.getMonth() == 4	|| date.getMonth() == 6	|| date.getMonth() == 7	|| date.getMonth() == 9	|| date.getMonth() == 10) {return false;}
				}
			}
		}).on('changeDate', function(ev) {
			updateReturnPeriod(ev.date);month = ev.date.getMonth()+1;year = ev.date.getFullYear();
		});
		<c:if test='${not empty error}'>
			var errorMsg = '<c:out value="${error}"/>';
			if(errorMsg == 'OTP verification is not yet completed!'	|| errorMsg == 'Invalid Session' || errorMsg == 'Unauthorized User' || errorMsg == 'Unauthorized User!' || errorMsg == 'Missing Mandatory Params' || errorMsg == 'API Authorization Failed') {errorMsg = 'Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.';}
				errorNotification(errorMsg);
		</c:if>
		$('#datetimepicker').datepicker('update', dateValue);
		if($('#datetimepicker').val() == ""){
			$('#datetimepicker').val(dateValue);
		}
		var clientsid = '<c:out value="${clientid}"/>';
		if(clientsid != ""){
			$.ajax({
				url: "${contextPath}/clientlist/${id}/${fullname}/${clientid}/${usertype}/${month}/${year}",
				async: true,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(summary) {
						for(var i=0; i<summary.length;i++){
						    var len=(summary[i].businessname).length;var bname=summary[i].businessname;var statename=summary[i].statename;var st;
							if(bname.length > 30){bname = bname.substring(0,30)+"...";}
							if(statename.includes("-")){
							var state = statename.split("-");
								if(state[1].length > 20){st = state[1].substring(0,20)+"...";
						         }else{st = state[1];}
							}else{st = statename;}
								if(summary[i].contactperson == "true"){
									if(summary[i].logoid){$('.listofclients').append('<li class="droptxt" data-toggle="tooltip" data-placement="bottom" title="Your Access to '+bname+'-'+summary[i].gstnnumber+' is restricted, Please contact your admin user for further assistance."><span class="imgsize-wrap-thumb1"><img src="${contextPath}/getlogo/'+summary[i].logoid+'" alt="Logo" class="imgsize-thumb" id="clntlogo"  style="float: left;"></span><a class="urllink" href="#"><span class="compname" title="'+summary[i].businessname+'" style="margin-top: -3px;padding:5px;">'+bname+'<span class="caption">'+summary[i].gstnnumber+' - '+st+'</span></span></a></li>')
									}else{$('.listofclients').append('<li class="droptxt" data-toggle="tooltip" data-placement="bottom" title="Your Access to '+bname+'-'+summary[i].gstnnumber+' is restricted, Please contact your admin user for further assistance."><span class="imgsize-wrap-thumb1"><img src="${contextPath}/static/mastergst/images/master/defaultcompany.png" alt="Logo" class="imgsize-thumb" id="clntlogo"  style="float: left;"></span><a class="urllink" href="#"><span class="compname" title="'+summary[i].businessname+'" style="margin-top: -3px;padding:5px;">'+bname+'<span class="caption">'+summary[i].gstnnumber+' - '+st+'</span></span></a></li>')}
								}else{
									if(summary[i].logoid){$('.listofclients').append('<li class="droptxt"><span class="imgsize-wrap-thumb1"><img src="${contextPath}/getlogo/'+summary[i].logoid+'" alt="Logo" class="imgsize-thumb" id="clntlogo"  style="float: left;"></span><a class="urllink" href="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+summary[i].lastname+'/<c:out value="${month}"/>/<c:out value="${year}"/>?type=initial"><span class="compname" title="'+summary[i].businessname+'" style="margin-top: -3px;padding:5px;">'+bname+'<span class="caption">'+summary[i].gstnnumber+' - '+st+'</span></span></a></li>')
									}else{$('.listofclients').append('<li class="droptxt"><span class="imgsize-wrap-thumb1"><img src="${contextPath}/static/mastergst/images/master/defaultcompany.png" alt="Logo" class="imgsize-thumb" id="clntlogo"  style="float: left;"></span><a class="urllink" href="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+summary[i].lastname+'/<c:out value="${month}"/>/<c:out value="${year}"/>?type=initial"><span class="compname" title="'+summary[i].businessname+'" style="margin-top: -3px;padding:5px;">'+bname+'<span class="caption">'+summary[i].gstnnumber+' - '+st+'</span></span></a></li>')}
								}
	 					}
				},
				error : function(status) {}
			});
		}
		 scriptElement = document.createElement( "script" );
	     scriptElement.src = "https://maps.googleapis.com/maps/api/js?key=AIzaSyAg_Twe-j7K6RXYeUswZv3gu_kwMrjbatM&libraries=places&region=IN";
	     scriptElement.async= true;
	     scriptElement.defer= true;
	});
	function expenseBranches(){
		$('.expense_branch').children('option').remove();
		$(".expense_branch").append($("<option></option>").attr("value","").text("-- Select Branch --")); 
		<c:forEach items="${client.branches}" var="branch">
			$(".expense_branch").append($("<option></option>").attr("value","${branch.name}").text("${branch.name}"));
		</c:forEach>
		}
	function closeNotifications() {$('.gst-notifications').hide();}
	function errorNotification(errorMsg) {
		closeNotifications();
		if(errorMsg == 'OTP verification is not yet completed!'	|| errorMsg == 'Invalid Session' || errorMsg == 'Unauthorized User'	|| errorMsg == 'Unauthorized User!' || errorMsg == 'Missing Mandatory Params' || errorMsg == 'API Authorization Failed') {errorMsg += '. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.';}
		if(errorMsg == 'Your subscription has expired. Kindly subscribe to proceed further!'){
			if(varUserType == 'suvidha' || varUserType == 'enterprise'){errorMsg += ' Click <a type="button" class="btn btn-sm btn-blue-dark" data-toggle="modal" data-target="#subnowmodal"">Subscribe Now</a> to proceed further.';
			}else{errorMsg += ' Click <a href="'+contextPath+'/dbllng'+commonSuffix+'/${month}/${year}" class="btn btn-sm btn-blue-dark">Subscribe Now</a> to proceed further.';}
		}$('#errorMessage').html(errorMsg);$('#errorMessage').parent().show();
	}
	function successNotification(message) {closeNotifications();$('#successMessage').html(message);$('#successMessage').parent().show();}
	function updateInvoiceTypeUrl(invType) {
			$('#subon').prop('checked',true);$('#submiton').prop('checked',false);
			$('.impTallyFinYear').val('');
			$('.impTallyMonth').val('');
			var retType = $("#importModalForm").attr("rtype");
			$('#hsnsumtallytemplate,#b2cstallytemplate,#tallyb2c').css("display","none");
			$('.impFinYear,.timpFinYear,.b2cradio').css("display","none");
			if(invType == 'mastergst' || invType == 'mgstFHPL' || invType == 'mastergst_all_fileds') {
				
				if(invType == 'mastergst' || invType == 'mastergst_all_fileds') {
					$('#hsnsumtallytemplate').css("display","block");
					$('.impFinYear').css("display","block"); 
					$('.b2cradio').css("display","inline-block");
					$('#subon').attr('checked',true);$('#submiton').removeAttr('checked');
				}
				$("#importModalForm").attr("action","${contextPath}/uploadInvoice/${id}/${fullname}/${usertype}/${client.id}/"+retType+"/${month}/${year}/"+invType+"");
			} else if(invType == 'tally'){if(retType == '<%=MasterGSTConstants.GSTR1%>'){$('#hsnsumtallytemplate').css("display","block");$('.impFinYear').css("display","block"); $('.b2cradio').css("display","inline-block");$('#subon').attr('checked',true);$('#submiton').removeAttr('checked');}$("#importModalForm").attr("action","${contextPath}/uploadTallyInvoice/${id}/${fullname}/${usertype}/${client.id}/"+retType+"/${month}/${year}");
			}else if(invType == 'tallyv17' || invType == 'tallyprime'){if(retType == '<%=MasterGSTConstants.GSTR1%>'){$('#hsnsumtallytemplate').css("display","block");$('.impFinYear').css("display","block"); $('.b2cradio').css("display","inline-block");$('#subon').attr('checked',true);$('#submiton').removeAttr('checked');}$("#importModalForm").attr("action","${contextPath}/uploadTallyInvoiceNewVersion/${id}/${fullname}/${usertype}/${client.id}/"+retType+"/${month}/${year}/"+invType+"");
			}else if(invType == 'sage'){$("#importModalForm").attr("action","${contextPath}/importSageInvoice/${id}/${fullname}/${usertype}/${client.id}/"+retType+"/${month}/${year}");
			}else if(invType == 'walterpack'){$("#importModalForm").attr("action","${contextPath}/importWalterpackInvoice/${id}/${fullname}/${usertype}/${client.id}/"+retType+"/${month}/${year}");
			}else if(invType == 'offlineutility'){$('#hsnsumtallytemplate').css("display","block");$('.impFinYear').css("display","block"); $('.b2cradio').css("display","inline-block");$('#subon').attr('checked',true);$('#submiton').removeAttr('checked');$("#importModalForm").attr("action","${contextPath}/uploadGstr1OfflineUtility/${id}/${fullname}/${usertype}/${client.id}/"+retType+"/${month}/${year}");}
			else{
				//$("#tallyb2c").text('For B2C invoices Tally template doesn't provide invoice date, So please select Financial Year & Month.');
				//$('#spantxt').html('Do You have Any B2C Invoices? : ')
				//$("#tallyb2c").html('For HSN Summary import templates doesn\'t provide invoice date, So please select Financial Year & Month.');
				//$('#spantxt').text('Do You have Any HSN Summary? : ')
				$('#hsnsumtallytemplate').css("display","block");
				$('.impFinYear').css("display","block"); 
				$('.b2cradio').css("display","inline-block");
				$('#subon').attr('checked',true);$('#submiton').removeAttr('checked');
				$("#importModalForm").attr("action","${contextPath}/uploadmapImportInvoice/"+invType+"/${id}/${fullname}/${usertype}/${client.id}/"+retType+"/${month}/${year}");
			}
		}
	$("input[name='submiton']").click(function(){
		var status = $("input[name='submiton']:checked").val();
		if(status == 'Yes'){
			$('.timpFinYear').css("display","block");
			$('#b2cstallytemplate,#tallyb2c').css("display","block");
			$('.impTallyFinYear').val('${year}');
			var d = new Date();
			var n = d.getMonth();
			$('.impTallyMonth').val(n+1);
			var mnth = $(".impTallyMonth option:selected").text();
			var fintext = $( ".impTallyFinYear option:selected" ).text();
			$('#b2cstallytemplate').html("All B2C Invoices & HSN Summary will come under "+mnth+" of "+fintext+" ");
		}else{
			$('#b2cstallytemplate,#tallyb2c').css("display","none");
			$('.timpFinYear').css("display","none");
			$('.impTallyFinYear').val('');
			$('.impTallyMonth').val('');
		}
	});
	function updateImpFinacialYear(finVal){
		var months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
		var d = new Date();
		var n = d.getMonth();
		var month = months[n];
		var mnth = $(".impTallyMonth option:selected").text();
		var fintext = $( ".impTallyFinYear option:selected" ).text();$('#b2cstallytemplate').html("All B2C Invoices will come under "+mnth+" of "+fintext+" ");
	}
	function updateImpMonth(month){
		var mnth = $(".impTallyMonth option:selected").text();
		var fintext = $(".impTallyFinYear option:selected").text();$('#b2cstallytemplate').html("All B2C Invoices will come under "+mnth+" of "+fintext+" ");
	}
		function updateSheets() {
			$('#idSheet').show();$('.sheets').hide();var invType = $('#templateType').val();if(invType != 'tally'){invType = 'mastergst';}$('#'+invType+'Sheet').show();
		}
		function updateImportModal(retType) {
			$('.impTallyFinYear').val('');
			$('.impTallyMonth').val('');
			$('#subon').attr('checked',true);$('#submiton').removeAttr('checked');
			$('#hsnsumtallytemplate,#b2cstallytemplate,#tallyb2c').css("display","none");
			$('.impFinYear,.timpFinYear,.b2cradio').css("display","none");
			$("#importModalForm").attr("action","${contextPath}/uploadInvoice/${id}/${fullname}/${usertype}/${client.id}/"+retType+"/${month}/${year}/mastergst");$("#importModalForm").attr("rtype", retType);$('#idSheet').css("display","none");$('#fileselect1').attr('files', []);$('#messages').html('');$('#templateType').empty();$('#etemplateType').empty();
			if(retType == '<%=MasterGSTConstants.GSTR1%>') {
				$('#templateType').append($("<option></option>").attr("value","").text("- Select Template Type -"));
				$('#templateType').append($("<option></option>").attr("value","mastergst").text("MasterGST Template"));
				$('#templateType').append($("<option></option>").attr("value","tally").text("Tally Template"));
				$('#templateType').append($("<option></option>").attr("value","tallyv17").text("Tally Template-V1.7"));
				$('#templateType').append($("<option></option>").attr("value","tallyprime").text("Tally Prime Template"));
				$('#templateType').append($("<option></option>").attr("value","sage").text("Sage Template"));
				$('#templateType').append($("<option></option>").attr("value","mastergst_all_fileds").text("MasterGST All Fields Template"));
				if(userMail == 'true' || userMail == true) {
					$('#templateType').append($("<option></option>").attr("value","mgstFHPL").text("MasterGST Sales Template with Additional Fields"));
				}	
				$('#templateType').append($("<option></option>").attr("value","offlineutility").text("GSTR1 Offline Utility"));
				<c:forEach items="${mappers}" var="mapper"><c:if test='${mapper.mapperType == "Sales"}'>$('#templateType').append($("<option></option>").attr("value","${mapper.id}").text("${mapper.mapperName}"));</c:if></c:forEach>
				$('#importModalTitle').html('Import Sales Invoices');$('.sales_template').show();$('.purchase_template').hide();
			}else if(retType == '<%=MasterGSTConstants.EINVOICE%>'){
				$('#etemplateType').append($("<option></option>").attr("value","").text("- Select Template Type -"));$('#etemplateType').append($("<option></option>").attr("value","mastergst").text("MasterGST E-invoice Template"));
				<c:forEach items="${mappers}" var="mapper"><c:if test='${mapper.mapperType == "einvoice"}'>$('#etemplateType').append($("<option></option>").attr("value","${mapper.id}").text("${mapper.mapperName}"));</c:if></c:forEach>
				//$('#importModalTitle').html('Import Sales Invoices');$('.sales_template').show();$('.purchase_template').hide();
			}else {
				$('#templateType').append($("<option></option>").attr("value","").text("- Select Template Type -"));$('#templateType').append($("<option></option>").attr("value","mastergst").text("MasterGST Template")); $('#templateType').append($("<option></option>").attr("value","tally").text("Tally Template")); $('#templateType').append($("<option></option>").attr("value","sage").text("Sage Template"));$('#templateType').append($("<option></option>").attr("value","walterpack").text("Single Sheet Template of Purchase Invoice"));
				<c:forEach items="${mappers}" var="mapper"><c:if test='${mapper.mapperType == "Purchases"}'>$('#templateType').append($("<option></option>").attr("value","${mapper.id}").text("${mapper.mapperName}"));</c:if></c:forEach>
				$('#importModalTitle').html('Import Purchase Invoices');$('.sales_template').hide();$('.purchase_template').show();
			}
		}
		function performImport(btn) {
			var templateType = $('#templateType').val();
			var filename =  $('#messages').html();
			if(templateType != ''){
			$('#templateType_Msg').html('');
			if(filename != ''){
				$(btn).addClass('btn-loader');		
				$('#importModalForm').ajaxSubmit( {
					url: $("#importModalForm").attr("action"),
					dataType: 'json',
					type: 'POST',
					cache: false,
					success: function(response) {
						$('#msgImportProgress').show();$(btn).removeClass('btn-loader');
						if(response && response.summaryList && response.summaryList.length > 0) {
							response.summaryList.forEach(function(inv) {$('#importSummaryBody').append("<tr><td>"+inv.name+"</td><td class='blocktxt'>"+inv.total+"</td><td class='blocktxt'>"+inv.totalinvs+"</td><td class='greentxt'>"+inv.success+"</td><td class='greentxt'>"+inv.invsuccess+"</td><td class='redtxt'>"+inv.failed+"</td><td class='redtxt'>"+inv.invfailed+"</td></tr>");});
							$('#idImportSummary').show();$('#idImportBody').hide();
							if(response.month) {month = response.month;	year = response.year;}
							if(response.showLink) {$('#errorXls').show();}
						} else if(response && response.error) {$('#importSummaryError').parent().show().html(response.error);}
					},
					error: function(e) {$('#msgImportProgress').hide();	$(btn).removeClass('btn-loader');$('#importSummaryError').parent().show().html("Something went wrong, Please try again");}
				});
			}else{$('#importSummaryError').parent().show().html("Please Select File");}
			}else{$('#templateType_Msg').html('Please Select Template Type');}
		}
		function choosefileSheets(){$('#fileselect1')[0].click();}
$('#fileselect1').change(function(e){var fileName = e.target.files[0].name;$('#messages').html(fileName);$('#importerr').css('display','none');});
function addclientinheader() {
	var url = $('.urladdlink').attr('link');
	$.ajax({
		url: "${contextPath}/addclntelgbty/${id}",
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(status) {
			if(status) {if(url) {url+='/'+month+'/'+year;location.href=url;}
			} else {errorNotification('New client addition is not allowed as the limit is already crossed.');}
		},
		error : function(status) {
			if(url) {url+='/'+month+'/'+year;location.href=url;}
		}
	});
}
(function ($) {
	  jQuery.expr[':'].Contains = function(a,i,m){
	      return (a.textContent || a.innerText || "").toUpperCase().indexOf(m[3].toUpperCase())>=0;
	  };
	  function listFilter(header, list) {
	 	    $('#searchfilter').change( function () {
	 	    var filter = $(this).val();
	          if(filter) {
	          $(list).find("a:not(:Contains(" + filter + "))").parent().slideUp();$(list).find("a:Contains(" + filter + ")").parent().slideDown();
	        } else {$(list).find("li").slideDown(); }
	        return false;
	      })
	    .keyup( function () { $(this).change(); });
	  }
	  $(function () { listFilter($("#header"), $("#style-n")); });
	}(jQuery));
$('span.comptxt.companyname').hover(function () {$('#dropdownMenu').attr("aria-expanded","true");$('.dropdown.dropdown-search').addClass("show");}); 
$('.dropdown-menu.search').mouseout(function() { 
		$('#dropdownMenu').attr("aria-expanded","true");$('.dropdown.dropdown-search').addClass("show"); $( ".dropdown-menu.search" ).hover(function() { }, function() {$('#dropdownMenu').attr("aria-expanded","flase");$('.dropdown.dropdown-search').removeClass("show");});
});
$('ul.nav li.dropdown, .navbar-left ul li.dropdown, .addlist-dd.dropdown, .addsales.dropdown').hover(function() {
	  $(this).find('.dropdown-menu').stop(true, true).delay(200).fadeIn(500);}, function() {$(this).find('.dropdown-menu').stop(true, true).delay(200).fadeOut(500);});
</script>
<script src="${contextPath}/static/mastergst/js/common/filedrag-map.js" type="text/javascript"></script>
<c:if test='${not empty client}'>
<c:if test='${not empty client.businessname}'>
<jsp:include page="/WEB-INF/views/client/addinvoice_popup.jsp">
	<jsp:param name="id" value="${id}" />
	<jsp:param name="fullname" value="${fullname}" />
	<jsp:param name="usertype" value="${usertype}" />
	<jsp:param name="returntype" value="${returntype}" />
	<jsp:param name="otherreturnType" value="${otherreturnType}" />
	<jsp:param name="client" value="${client}" />
	<jsp:param name="contextPath" value="${contextPath}" />
	<jsp:param name="month" value="${month}" />
	<jsp:param name="year" value="${year}" />
</jsp:include>
<jsp:include page="/WEB-INF/views/einvoice/einvoicepopup.jsp" />
<%@include file="/WEB-INF/views/client/allAccountDetails.jsp"%>
<jsp:include page="/WEB-INF/views/client/addGroup.jsp" />
</c:if>
</c:if>