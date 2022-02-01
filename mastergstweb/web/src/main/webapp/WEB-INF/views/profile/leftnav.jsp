<div class="col-md-2 col-sm-12">
	 <div class="sidebarmenu">
		<ul>
			
			<c:if test='${not empty client && not empty client.id}'>
			<li> <a id="cpAboutNav" href="#" class="urllink permissionSettings-Company_Details-View" link="${contextPath}/about/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Profile</a> </li>
			</c:if>
			
			<c:if test="${usertype eq userCenter}">
			<%--<li id="cp_Allcenter" style="display: none;"> <a id="cpAllCenterNav" href="#" class="urllink" link="${contextPath}/cp_allcenters/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>">Dashboard</a> </li> --%>
			<li id="cp_center" style="display: none;"> <a id="cpCenterNav" href="#" class="urllink" link="${contextPath}/cp_centers/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>">Suvidha Kendras</a> </li>
			<%-- <li id="cp_centerFiling" style="display: none;"> <a id="cpCenterFilingNav" href="#" class="urllink" link="${contextPath}/cp_centersfilings/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>">Filings	</a> </li>
			<li id="cp_centerClinet" style="display: none;"> <a id="cpCenterBillingNav" href="#" class="urllink" link="${contextPath}/cp_centersclients/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>">Clients</a> </li> --%>
			</c:if>
			<c:choose>
			<c:when test='${not empty client && not empty client.id}'>
			<li> <a id="cpUploadNav" href="#" class="urllink permissionSettings-Configurations-View" link="${contextPath}/cp_upload/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type="> Configurations</a> </li>
			<li> <a id="cpCustomerNav" href="#" class="urllink permissionSettings-Customers-View" link="${contextPath}/cp_customers/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Customers </a></li>
			<li> <a id="cpSupplierNav" href="#" class="urllink permissionSettings-Suppliers-View" link="${contextPath}/cp_suppliers/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Suppliers </a></li>
			<li> <a id="cpItemNav" href="#" class="urllink permissionSettings-Items-View" link="${contextPath}/cp_items/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Items</a> </li>
			<li id="cp_User"> <a id="cpUserNav" href="#" class="urllink permissionSettings-Users-View" link="${contextPath}/cp_user/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Users</a> </li>
			<li class="role_nav"> <a id="cpRoleNav" href="#" class="urllink permissionSettings-Roles-View" link="${contextPath}/cp_role/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Roles</a> </li>
			<li> <a id="cpBranchNav" href="#" class="urllink permissionSettings-Branches-View" link="${contextPath}/cp_branches/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=">Branches</a> </li>
			<li> <a id="cpVerticalNav" href="#" class="urllink permissionSettings-Verticals-View" link="${contextPath}/cp_verticals/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=">Verticals</a> </li> 
			<li> <a id="cpBankNav" href="#" class="urllink permissionSettings-Bank_Information-View" link="${contextPath}/cp_bankDetails/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Bank Information</a> </li>
			<li> <a id="cpaccountNav" href="#" class="urllink permissionSettings-Accounting-View" link="${contextPath}/cp_Accounting/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=group">Accounting</a> </li>
			<li> <a id="cpCostCenterNav" href="#" class="urllink permissionSettings-Branches-View" link="${contextPath}/cp_costcenter/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=">Cost Center</a> </li>
			<li> <a id="cpEcommerce" href="#" class="urllink permissionSettings-E_Commerce_Operator-View" link="${contextPath}/cp_ecommerce/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">E-Commerce Operator </a></li>
			</c:when>
			<c:otherwise>
			<li> <a id="cpUserNav" href="#" class="urllink" link="${contextPath}/teamuser/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>">Users</a> </li>
			<li class="role_nav"><a id="cpRoleNav" href="#" class="urllink" link="${contextPath}/teamrole/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>">Roles</a></li>
				<c:if test="${usertype eq userCenter || usertype eq userCA || usertype eq userTaxP}">
				<li id="cp_centerClinet" style="display: none;"> <a id="cpCenterBillingNav" href="#" class="urllink" link="${contextPath}/cp_centersclients/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>">All Clients</a> </li>
				</c:if>
				<c:if test="${usertype eq userEnterprise || usertype eq userBusiness}">
				<li id="cp_centerClinet" style="display: none;"> <a id="cpCenterBillingNav" href="#" class="urllink" link="${contextPath}/cp_centersclients/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>">All Businesses</a> </li>
				</c:if>
			<li id="cp_smtp"> <a id="cpSmtpNav" href="#" class="urllink" link="${contextPath}/cp_smtp/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>">SMTP</a> </li>
			</c:otherwise>
			</c:choose>
		</ul>
	</div>
</div>
