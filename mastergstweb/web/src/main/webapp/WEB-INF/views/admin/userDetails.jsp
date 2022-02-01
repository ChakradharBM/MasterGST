<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<c:set var="varEwayClientId" value="<%=UUID.randomUUID().toString()%>"/>
<c:set var="varEwayClientSecret" value="<%=UUID.randomUUID().toString()%>"/>

<c:set var="varEinvClientId" value="<%=UUID.randomUUID().toString()%>"/>
<c:set var="varEinvClientSecret" value="<%=UUID.randomUUID().toString()%>"/>
<script>
ewaybillClientId = '${varEwayClientId}';
ewaybillClientSecretId = '${varEwayClientSecret}';
einvClientId = '${varEinvClientId}';
einvClientSecretId = '${varEinvClientSecret}';
</script>
	<!--   Modal begin -->
<div class="modal fade" id="userModal" role="dialog" aria-labelledby="userModal" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-right" role="document" style="min-width:1050px;">
    <div class="modal-content" style="height: 100vh;overflow-y: auto;overflow-x: hidden;">
      <div class="modal-body meterialform bs-fancy-checks popupright" style="height:660px;">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"><img src="${contextPath}/static/mastergst/images/credentials/closebtn.png" alt="Close" /></span> </button>
        <div class="bluehdr">
          <h3 class="user_field user_details_field" data="fullname"></h3>
          <div class="login-details">Last Loggedin :
	          <span class="user_field user_details_field" data="usrLastLoggedIn"></span>
          </div>
        </div>
        <!--  form begin -->
		
		<ul class="nav nav-tabs navtab-lnk" role="tablist">
			<li class="nav-item permissionDETAILS"><a class="nav-link active permissionDETAILS" href="#" id="detail" onClick="adminDashboard('details',this)">Details</a></li>
			<li class="nav-item" id="sandcred"><a class="nav-link" href="#" id="credential" onClick="adminDashboard('sandboxCredentials',this)">GST Sandbox</a></li>
			<li class="nav-item permissionGST_PRODUCTION" id="prodcred"><a class="nav-link permissionGST_PRODUCTION" href="#" id="credential" onClick="adminDashboard('productionCredentials',this)">GST Production</a></li>
			<li class="nav-item permissionEWAY_BILL_PRODUCTION" id="ewaybillcred"><a class="nav-link permissionEWAY_BILL_PRODUCTION" href="#" id="credential" onClick="adminDashboard('ewaybillCredentials',this)">E-Way Bill</a></li>
			
			<li class="nav-item" id="einvoicecred"><a class="nav-link" href="#" id="einvoicecredential" onClick="adminDashboard('eInvoiceCredentials',this)">E-Invoice</a></li>
			
			<li class="nav-item permissionPAYMENTS" id="userpymt"><a class="nav-link permissionPAYMENTS payment_tab" href="#" id="navPymt" onClick="adminDashboard('payment',this)">Payment</a></li>
			<li class="nav-item" id="secondaryUser"><a class="nav-link users_permissions_tab" href="#" id="navPymt" onClick="adminDashboard('users',this)">Users</a></li>
			<li class="nav-item" id="commentsTab1"><a class="nav-link" href="#" id="commentsTab2" onClick="adminDashboard('commentsTab',this)">Comments</a></li>
			<li class="nav-item permissionHEADER_KEYS" id="headerkeysTabcon" style="display:none"><a class="nav-link" href="#" id="headerkeysTab2" onClick="adminDashboard('headerkeysTab',this)">Header Keys</a></li>
			<li class="nav-item" id="asp_apiuserdetails1"><a class="nav-link" href="#" id="asp_apiuserdetails2" onClick="adminDashboard('asp_apiuserdetailsTab',this)">API User Details</a></li>
			
			<li class="nav-item" id="centersTab1"><a class="nav-link" href="#" id="centersTab2" onClick="adminDashboard('centersTab',this)">Centers</a></li>
			<li class="nav-item" id="refClientTab1"><a class="nav-link" href="#" id="refClientTab2" onClick="adminDashboard('refClientTab',this)">Ref. Clients</a></li>
			<li class="nav-item" id="clientPaymentDetailsTab1"><a class="nav-link" href="#" id="clientPaymentDetailsTab2" onClick="adminDashboard('clientPaymentDetailsTab',this)">Payment Details</a></li>
			<li class="nav-item" id="clientPendingPaymentDetailsTab1"><a class="nav-link" href="#" id="clientPendingPaymentDetailsTab2" onClick="adminDashboard('clientPendingPaymentDetailsTab',this)">Pending Payment</a></li>
		</ul>
		
		<div class="tab-content">
			<div id="details" class="tab-pane active permissionDETAILS" style="height:467px;overflow-y: auto;">
			<div class="container pt-4 row">
			<div>&nbsp;</div>
        <!--<div class="pl-4 pr-4 row">-->
		
        <div class="form-group col-md-6 row">
           <label class="col-md-6" for="name">Name<span class="coln-txt">:</span></label>
				<div class="col-md-6" id="name">
					<span class="user_field user_details_field" id="FullName" data="fullname"></span>
				</div>
       </div>
       <div class="form-group col-md-6 row">
			<label class="col-md-6" for="email">Email<span class="coln-txt">:</span></label>
			<div class="col-md-6" id="emailID">
				<span class="user_field user_details_field" id="EmailID" data="email"></span>
			</div>
		</div>
		
		<div class="form-group col-md-6 row">
           <label class="col-md-6" for="number" >Phone Number<span class="coln-txt">:</span></label>
				<div class="col-md-6" id="mnumber">
					<span class="user_field user_details_field" id="Mnumber" data="mobilenumber"></span>
				</div>
       </div>
       <div class="form-group col-md-6 row">
           <label class="col-md-6" for="number" >Account Status<span class="coln-txt">:</span></label>
				<div class="col-md-6" id="status">
					<span class="user_field user_details_field" id="Status" data="disable"></span>
				</div>
        </div>
        <div class="form-group col-md-6 row permissionUSER_TYPE_CHANGE">
			<label class="col-md-6" >User Type Change<span class="coln-txt">:</span></label>
			<div class="col-md-6" id="userTypeChange">          
				<span class="user_field user_details_field" id="UserTypeChange" data="type"></span>
			</div>
		</div>
        	<div id="div_agreementstatus" class="form-group col-md-6 row  pr-0" style="display: inline-flex;">
				<label class="col-md-6" for="agreementStatus">Agreement Status<span class="coln-txt">:</span></label>
				<div class="col-md-6" id="agreement">
					<span class="user_field user_details_field" id="AgreementStatus" data="agreementStatus"></span>
				</div>
			</div>
        	<div class="form-group col-md-6 row pr-0" style="display: inline-flex;">
				<label class="col-md-6" for="needToFollowUp" >Sales Status<span class="coln-txt">:</span></label>
				<div class="col-md-6" id="followUp">
					<span class="user_field user_details_field" id="NeedToFollowUp" data="needToFollowUp"></span>
				</div>
			</div>
			
			
		<div class="form-group col-md-6 row needToFollowUpComment d-none">
			<label class="col-md-4" for="needToFollowUpCommenttext" style="vertical-align: top;">Not Required Comment<span class="coln-txt">:</span></label>
			<div class="col-md-8" id="needToFollowUpCommentDisplay" style="display:inline-block">
				<span class="user_field user_details_field" id="NeedToFollowUpComment" data="needToFollowUpComment"></span>	
			</div>
		</div>
		
		<div class="form-group row col-md-6 pr-0" style="display: inline-flex;">
				<label class="col-md-6" for="QuotationSent">Quotation Sent<span class="coln-txt">:</span></label>
				<div class="col-md-6" id="enableQuotationSents">
					<span class="user_field user_details_field" id="QuotationSent" data="quotationSent"></span>
				</div>
			</div>
			<div class="form-group row col-md-6 pr-0" style="display: inline-flex;">
				<label class="col-md-6" for="Need_followup">Need Follow Up<span class="coln-txt">:</span></label>
				<div class="col-md-6" id="enableNeed_followups">
					<span class="user_field user_details_field" id="Need_FollowUp" data="needFollowup"></span>
				</div>
			</div>
		<div class="form-group col-md-6 row d-none" id="enableNeed_followupsDate">
			<div class="col-md-12 row pl-0 pr-0" style="display: inline-flex;">
				<label class="col-md-6" for="Need_followup_date">Need Follow Up Date<span class="coln-txt">:</span></label>
				<div class="col-md-6" id="enableNeed_followups_date">
					<span class="user_field user_details_field" id="Need_FollowUp_date" data="needFollowupdate"></span>
				</div>
			</div>
		</div>
		<div class="form-group col-md-6 row permissionPARTNER_LINK">
			<div class="col-md-12 pl-0 pr-0" style="display: inline-flex;">
				<label class="col-md-6" for="Partner_email">Partner Link<span class="coln-txt">:</span></label>
				<div class="col-md-6" id="Partner_Email">
					<span class="user_field user_details_field" id="PartnerEmail" data="partnerEmail"></span>
				</div>
			</div>
		</div>
		<div class="form-group col-md-6 row permissionPARTNER_LINK">
			<div class="col-md-12 pl-0 pr-0" style="display: inline-flex;">
				<label class="col-md-6" for="Partner_type">Partner Type<span class="coln-txt">:</span></label>
				<div class="col-md-6" id="partnerType">
					<span class="user_field user_details_field" id="PartnerType" data="partnerType"></span>
				</div>
			</div>
		</div>
		<!-- ASP Developer purpose start-->
		
			<div class="form-group col-md-6 row asp-dev">
				<label class="col-md-6" for="sandboxApplied" style="vertical-align: top;">Sandbox Applied<span class="coln-txt">:</span></label>
				<div class="col-md-6" id="sandboxapplieddiv" style="display:inline-block">
					<span class="user_field user_details_field" id="SandboxApplied" data="sandboxApplied"></span>	
				</div>
			</div>
		<!-- ASP Developer purpose end-->
		
	<!-- ------------------gst sub start----------------------- -->
		<div class="asp-developerdata">
			<div class="customtable user-admin-tab pl-3 db-ca-view reportTable reportTable2" style="width: 960px;">
				<table id="reportTable3" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
	              <thead>
	                <tr>
	                  <th class="text-center">API Type</th>
	                  <th class="text-center">Access</th>
	                  <th class="text-center">Sub.Status</th>
	                  <th class="text-center">Start Date</th>
	                  <th class="text-center">End Date</th>
	                  <th class="text-center">Allowed APIs</th>
			  		  <th class="text-center">Used APIs</th>
	                </tr>
	              </thead>
	              <tbody>
					 <tr>
					 	<td>GST Produciton</td>
					 	<td><span class="prodIsEnabled userprod_field" data="isenabled"></span></td>
					 	<td><span class="user_field user_details_field" id="gstAPIStatus" data="gstAPIStatus"></span></td>
					 	<td><span class="user_field user_details_field" id="substartdate" data="gstSubscriptionStartDate"></span></td>
					 	<td><span class="user_field user_details_field" id="subenddate" data="gstSubscriptionExpiryDate"></span></td>
					 	<td><span class="user_field user_details_field" id="substartdate" data="gstAPIAllowedInvoices"></span></td>
					 	<td><span class="user_field user_details_field" id="substartdate" data="gstAPIUsageCountInvoices"></span></td>
	                 </tr>
	                  <tr>
					 	<td>GST Sandbox</td>
					 	<td><span class="sandIsEnabled usersandbox_field" data="isenabled"></span></td>
					 	<td><span class="user_field user_details_field" id="gstSandboxAPIStatus" data="gstSandboxAPIStatus"></span></td>
					 	<td><span class="user_field user_details_field" id="substartdate" data="gstSandboxSubscriptionStartDate"></span></td>
					 	<td><span class="user_field user_details_field" id="subenddate" data="gstSandboxSubscriptionExpiryDate"></span></td>
					 	<td><span class="user_field user_details_field" id="subenddate" data="gstSanboxAllowedCountInvoices"></span></td>
					 	<td><span class="user_field user_details_field" id="subenddate" data="gstSanboxUsageCountInvoices"></span></td>
	                 </tr>    
	                  <tr>
					 	<td>Eway Bill Production</td>
					 	<td><span class="ewayIsEnabled userpeway_field" data="isenabled"></span></td>
					 	<td><span class="user_field user_details_field" id="ewaybillAPIStatus" data="ewaybillAPIStatus"></span></td>
					 	<td><span class="user_field user_details_field" id="substartdate" data="ewaybillSubscriptionStartDate"></span></td>
					 	<td><span class="user_field user_details_field" id="subenddate" data="ewaybillSubscriptionExpiryDate"></span></td>
					 	<td><span class="user_field user_details_field" id="substartdate" data="ewaybillAPIAllowedInvoices"></span></td>
					 	<td><span class="user_field user_details_field" id="subenddate" data="ewaybillAPIUsageCountInvoices"></span></td>
	                 </tr>    
	                  <tr>
					 	<td>Eway Bill Sandbox</td>
					 	<td><span class="ewaySandIsEnabled userseway_field"  data="isenabled"></span></td>
					 	<td><span class="user_field user_details_field" id="ewaybillSandboxAPIStatus" data="ewaybillSandboxAPIStatus"></span></td>
					 	<td><span class="user_field user_details_field" id="substartdate" data="ewaybillSandboxSubscriptionStartDate"></span></td>
					 	<td><span class="user_field user_details_field" id="subenddate" data="ewaybillSandboxSubscriptionExpiryDate"></span></td>
					 	<td><span class="user_field user_details_field" id="subenddate" data="ewaybillSanboxAllowedInvoices"></span></td>
					 	<td><span class="user_field user_details_field" id="subenddate" data="ewaybillSanboxUsageCountInvoices"></span></td>
	                 </tr>
					 <tr>
					 	<td>E-Invoice Production</td>
					 	<td><span class="einvIsEnabled userpeway_field" data="isenabled"></span></td>
					 	<td><span class="user_field user_details_field" id="einvAPIStatus" data="einvAPIStatus"></span></td>
					 	<td><span class="user_field user_details_field" id="substartdate" data="einvSubscriptionStartDate"></span></td>
					 	<td><span class="user_field user_details_field" id="subenddate" data="einvSubscriptionExpiryDate"></span></td>
					 	<td><span class="user_field user_details_field" id="substartdate" data="einvAPIAllowedInvoices"></span></td>
					 	<td><span class="user_field user_details_field" id="subenddate" data="einvAPIUsageCountInvoices"></span></td>
	                 </tr>    
	                  <tr>
					 	<td>E-Invoice Sandbox</td>
					 	<td><span class="einvSandIsEnabled userseway_field"  data="isenabled"></span></td>
					 	<td><span class="user_field user_details_field" id="einvSandboxAPIStatus" data="einvSandboxAPIStatus"></span></td>
					 	<td><span class="user_field user_details_field" id="substartdate" data="einvSandboxSubscriptionStartDate"></span></td>
					 	<td><span class="user_field user_details_field" id="subenddate" data="einvSandboxSubscriptionExpiryDate"></span></td>
					 	<td><span class="user_field user_details_field" id="subenddate" data="einvSanboxAllowedInvoices"></span></td>
					 	<td><span class="user_field user_details_field" id="subenddate" data="einvSanboxUsageCountInvoices"></span></td>
	                 </tr>					 
	               </tbody>
            </table>
          </div>		
		</div>
	<!-- -------------------gst sub end ---------------------- -->	  
		
		<!-- suvidha centers fields start -->
			<div class="suvidha_centers_data">
				<div class="form-group row">
					<label class="col-md-3">GSTIN<span class="coln-txt">:</span></label>
					<div class="col-md-3" id="div_GSTIN">          
						<span class="user_field user_details_field" id="GSTIN" data="gstin"></span>
					</div>
					<label class="col-md-3">PAN<span class="coln-txt">:</span></label>
					<div class="col-md-3" id="div_PAN">          
						<span class="user_field user_details_field" id="PAN" data="pan"></span>
					</div>
				</div>
				
				<div class="form-group row">
					<label class="col-md-3">Authorised Signatory<span class="coln-txt">:</span></label>
					<div class="col-md-3" id="div_authorisedSignatory">          
						<span class="user_field user_details_field" id="AuthorisedSignatory" data="authorisedSignatory"></span>
					</div>
					<label class="col-md-3">Authorised PAN Number<span class="coln-txt">:</span></label>
					<div class="col-md-3" id="div_authorisedPANNumber">          
						<span class="user_field user_details_field" id="AuthorisedPANNumber" data="authorisedPANNumber"></span>
					</div>
				</div>
				<div class="form-group row">
					<label class="col-md-3">Business Name<span class="coln-txt">:</span></label>
					<div class="col-md-3" id="div_businessName">          
						<span class="user_field user_details_field" id="BusinessName" data="businessName"></span>
					</div>
					<label class="col-md-3">Dealer Type<span class="coln-txt">:</span></label>
					<div class="col-md-3" id="div_dealerType">          
						<span class="user_field user_details_field" id="DealerType" data="dealerType"></span>
					</div>
				</div>
				<div class="col-md-6 row pl-0 pr-0">
				<label class="col-md-6">State<span class="coln-txt" style="margin-right:-6px">:</span></label>
				<div class="col-md-6 pr-0" id="div_stateName" style="padding-left: 21px;margin-bottom: 20px;">          
					<span class="user_field user_details_field" id="StateName" data="stateName"></span>
				</div>
				</div>
			</div>
			<!-- suvidhs centers fields end -->
		<div class="form-group col-md-6 row nonasp">
           <label class="col-md-6" for="number" >Docs<span class="coln-txt">:</span></label>
				<div class="col-md-6" id="enableDocs">
					<span class="user_field user_details_field" id="divDrive" data="accessDrive"></span>
				</div>
        </div>
        <div class="form-group col-md-6 row nonasp">
           <label class="col-md-6" for="number" >Imports<span class="coln-txt">:</span></label>
				<div class="col-md-6" id="enableImports">
					<span class="user_field user_details_field" id="mapImports" data="accessImports"></span>
				</div>
        </div>
        <div class="form-group col-md-6 row nonasp">
			 <label class="col-md-6" for="number" >GSTR9<span class="coln-txt">:</span></label>
				<div class="col-md-6" id="enableGstr9">
					<span class="user_field user_details_field" id="mapGstr9" data="accessGstr9"></span>
				</div>
		</div>
		<div class="form-group col-md-6 row nonasp">
			<label class="col-md-6" for="accessMultiGSTNSearch" >MultiGSTN<span class="coln-txt">:</span></label>
			<div class="col-md-6" id="enableMultiGSTNSearch">
				<span class="user_field user_details_field" id="mapMultiGSTNSearch" data="accessMultiGSTNSearch"></span>
			</div>
		</div>
		<div class="form-group col-md-6 row nonasp">
			 <label class="col-md-6" for="number" >EwayBill<span class="coln-txt">:</span></label>
				<div class="col-md-6" id="enableDwnldewabillinv">
					<span class="user_field user_details_field" id="mapDwnldewabillinv" data="accessDwnldewabillinv"></span>
				</div>
		</div>
		<div class="form-group col-md-6 row nonasp">	
				<label class="col-md-6" for="accessReminders" >Reminders<span class="coln-txt">:</span></label>
			<div class="col-md-6" id="enableReminders">
				<span class="user_field user_details_field" id="mapReminders" data="accessReminders"></span>
			</div>
		</div>
		<div class="form-group col-md-6 row nonasp">
			<label class="col-md-6" for="accessAcknowledgement">Acknowledgement<span class="coln-txt">:</span></label>
			<div class="col-md-6" id="enableAcknowledgement">
				<span class="user_field user_details_field" id="mapAcknowledgement" data="accessAcknowledgement"></span>
			</div>
		</div>
		<div class="form-group col-md-6 row nonasp">
			<label class="col-md-6" for="accessEinvoice" >E-invoice<span class="coln-txt">:</span></label>
			<div class="col-md-6" id="enableEinvoice">
				<span class="user_field user_details_field" id="mapEinvoice" data="accessEinvoice"></span>
			</div>
		</div>
		<div class="form-group col-md-6 row nonasp">
			<label class="col-md-6" for="accessCustomFieldTemplate">New Import Template<span class="coln-txt">:</span></label>
			<div class="col-md-6" id="enableCustomFieldTemplate">
				<span class="user_field user_details_field" id="mapCustomFieldTemplate" data="accessCustomFieldTemplate"></span>
			</div>
		</div>
		<div class="form-group col-md-6 row nonasp">
			<label class="col-md-6" for="accessEntertainmentEinvoiceTemplate">Entertainment Einvoice Import Template<span class="coln-txt">:</span></label>
			<div class="col-md-6" id="enableEntertainmentEinvoiceTemplate">
				<span class="user_field user_details_field" id="mapEntertainmentEinvoiceTemplate" data="accessEntertainmentEinvoiceTemplate"></span>
			</div>
		</div>
		<div class="form-group col-md-6 row nonasp">
			<label class="col-md-6" for="accessANX1">ANX1<span class="coln-txt">:</span></label>
			<div class="col-md-6" id="enableANX1">
				<span class="user_field user_details_field" id="mapANX1" data="accessANX1"></span>
			</div>
		</div>
		<div class="form-group col-md-6 row nonasp">
			<label class="col-md-6" for="accessANX2">ANX2<span class="coln-txt">:</span></label>
			<div class="col-md-6" id="enableANX2">
				<span class="user_field user_details_field" id="mapANX2" data="accessANX2"></span>
			</div>
		</div>
		<!--<div class="form-group row">
			<label class="col-md-3">Status<span class="coln-txt">:</span></label>
			<div class="col-md-4 form-check form-check-inline" id="status">          
				<input class="user_field form-check-input" type="checkbox" id="disable" data="disable">
                <label for="Status"><span class="ui"></span> </label>
                <span class="labletxt">Active</span>
			</div>
		</div>-->
		<div class="form-group col-md-6 row nonasp">
           <label class="col-md-6" for="number">Reports<span class="coln-txt">:</span></label>
				<div class="col-md-6" id="enableReports">
					<span class="user_field user_details_field" id="reports" data="accessReports"></span>
				</div>
      </div>
	  <div class="form-group col-md-6 row nonasp">
           <label class="col-md-6" for="accessTravel" >Travel Reports<span class="coln-txt">:</span></label>
				<div class="col-md-6" id="enableTravelReports">
					<span class="user_field user_details_field" id="mapTravelReports" data="accessTravel"></span>
				</div>
        </div>
		<div style="display:none;padding:0px;" id="addCenter" class="form-group col-md-6 nonasp row">
		
			<label class="col-md-6"># Of Centers Allowed<span class="coln-txt">:</span></label>
			<span class="col-md-6" id="noofCenters">          
				<span class="user_field" id="TotalCentersAllowed" data="totalCenters"></span>
			</span>
		</div>
		
		
		<div class="form-group col-md-6 row non-asp-developerdata">
			<label class="col-md-6" for="substartdate" >Subscription start date<span class="coln-txt">:</span></label>
				<div class="col-md-6" id="startdate">
					<span class="user_field user_details_field" id="substartdate" data="subscriptionStartDate"></span>
				</div>
		</div>
		<div class="form-group col-md-6 row non-asp-developerdata">
			<label class="col-md-6" for="subenddate" >Subscription end date<span class="coln-txt">:</span></label>
				<div class="col-md-6" id="enddate">
					<span class="user_field user_details_field" id="subenddate" data="subscriptionExpiryDate"></span>
				</div>
		</div>
		<div class="form-group row col-md-6 Invoices_Clients">
			<label class="col-md-6"># Of Invoices Allowed<span class="coln-txt">:</span></label>
			<div class="col-md-6" id="noofInvoices">          
				<span class="user_field" id="TotalInvoicesAllowed" data="totalInvoices"></span>
			</div>
		</div>
		<div class="form-group row col-md-6 Invoices_Clients">
			<label class="col-md-6"># Of Clients Allowed<span class="coln-txt">:</span></label>
			<div class="col-md-6" id="noofInvoices">          
				<span class="user_field" id="TotalClientsAllowed" data="totalClients"></span>
			</div>
		</div>
		
		<div class="form-group col-md-6 row Invoices_Clients">
			<label class="col-md-6"># Of Invoices Used<span class="coln-txt">:</span></label>
			<div class="col-md-6" id="noofInvoices">          
				<span class="user_field" id="NoOfInvoices" data="totalInvoicesUsed"></span>
			</div>
		</div>
		<div class="form-group col-md-6 row Invoices_Clients">
			<label class="col-md-6">Journals Excel<span class="coln-txt">:</span></label>
			<div class="col-md-6" id="enableJournalsExcel">          
				<span class="user_field user_details_field" id="EnableJournalsExcel" data="accessJournalExcel"></span>
			</div>
		</div>
		
			<div class="form-group row col-md-6 idSuvidhaClients">
				<label class="col-md-6"># Invited<span class="coln-txt">:</span></label>
				<div class="col-md-6">
					<span class="user_field" data="totalInvitedClients"></span>
				</div>
			</div>
			<div class="form-group row col-md-6 idSuvidhaClients">
				<label class="col-md-6"># Pending<span class="coln-txt">:</span></label>
				<div class="col-md-6">
					<span class="user_field" data="totalPendingClients"></span>
				</div>
			</div>
			<div class="form-group row col-md-6 idSuvidhaClients">
				<label class="col-md-6"># Joined<span class="coln-txt">:</span></label>
				<div class="col-md-6">
					<span class="user_field" data="totalJoinedClients"></span>
				</div>
			</div>
			<div class="form-group row col-md-6 idSuvidhaClients">
				<label class="col-md-6"># Subscribed<span class="coln-txt">:</span></label>
				<div class="col-md-6">
					<span class="user_field" data="totalSubscribedClients"></span>
				</div>
			</div>
			<div class="form-group row col-md-6 idSuvidhaClients">
				<label class="col-md-6">Revenue Percentage<span class="coln-txt">:</span></label>
				<div class="col-md-6" id="partner_percent">
					<span class="user_details_field user_field" data="partnerPercentage" id="revenuePercentage"></span>
				</div>
			</div>
<!-- dashboard table start -->
<div class="div-userdashboard" style="width: 960px;">
 			<div class ="row" style="margin-top:50px;" >
					<div class="col-sm-12">
						<!-- <h4 class="hdrtitle" style="display: inline-block;color: #374583;font-size: 20px;">Monthly API Usage</h4>  -->
						
						<h4 class="hdrtitle1 pl-3" style="display: inline-block; color: #374583;font-size: 20px;">Monthly API Usage In The Financial Year : </h4> 
						<select class="" name="financialYear" id="financialYear">
							<option value="2022">2022</option>
							<option value="2021">2021</option>
							<option value="2020">2020</option>
							<option value="2019">2019</option>
							<option value="2018">2018</option>
							<option value="2017">2017</option>
						</select>
					</div>
					</div>
		<div class="customtable user-admin pl-3 db-ca-view reportTable reportTable2" style="width: 960px;">
            <table id="reportTable2" class="display row-border dataTable meterialform" cellspacing="0" width="100%" >
              <thead>
                <tr>
                  <th class="text-center"></th>
                  <th class="text-center">Jan</th>
                  <th class="text-center">Feb</th>
                  <th class="text-center">Mar</th>
				  <th class="text-center">Apr</th>
                  <th class="text-center">May</th>
                  <th class="text-center">June</th>
                  <th class="text-center">July</th>
                  <th class="text-center">Aug</th>
                  <th class="text-center">Sept</th>
                  <th class="text-center">Oct</th>
                  <th class="text-center">Nov</th>
                  <th class="text-center">Dec</th>
                  <th class="text-center">Total</th>
                </tr>
              </thead>
              <tbody>
			  <c:set var="monthArray" value="${['1','2','3','4','5','6','7','8','9','10','11','12','13']}" />
				<tr data-toggle="modal" data-target="#viewModal">
					<td align="center"><h6>Sandbox</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right tddataSandbox" data-val = "Sandbox${month}" id="Sandbox${month}">${summaryMap[month]['Sandbox']}</td>
					</c:forEach>
                 </tr>
				 <tr data-toggle="modal" data-target="#viewModal">
					<td align="center"><h6>Production</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right tddataSandbox" data-val = "Production${month}" id="Production${month}">${summaryMap[month]['Production']}</td>
					</c:forEach>
                 </tr>
				 <tr data-toggle="modal" data-target="#viewModal">
					<td align="center"><h6>EwayBill Sandbox</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right tddataEwayBillSandbox" data-val = "EwayBillSandBox${month}" id="EwayBillSandBox${month}">${summaryMap[month]['EwayBillSandBox']}</td>
					</c:forEach>
                 </tr>
				 <tr data-toggle="modal" data-target="#viewModal">
					<td align="center"><h6>EwayBill Production</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right tddataEwayBillProduction" data-val = "EwayBillProduction${month}" id="EwayBillProduction${month}">${summaryMap[month]['EwayBillProduction']}</td>
					</c:forEach>
                 </tr>  
				 <tr data-toggle="modal" data-target="#viewModal">
					<td align="center"><h6>E-Invoice Sandbox</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right tddataEinvSandbox" data-val = "EInvoiceSandBox${month}" id="EInvoiceSandBox${month}">${summaryMap[month]['EInvoiceSandBox']}</td>
					</c:forEach>
                 </tr>
				 <tr data-toggle="modal" data-target="#viewModal">
					<td align="center"><h6>E-Invoice Production</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right tddataEinvProduction" data-val = "EInvoiceProduction${month}" id="EInvoiceProduction${month}">${summaryMap[month]['EInvoiceProduction']}</td>
					</c:forEach>
                 </tr>
               </tbody>
            </table>
          </div>
          
</div>		 
<!-- dashboard table end -->	
<!-- ---ending add columns ------- -->	
				
        </div>
		
		<div class="modal-footer footer-fixed-bottom" style="background-color: lightgray;">
		<button type="button" class="btn btn-blue-dark permissionEDIT"  id="edit_btn" onClick="editDetails()">Edit</button>
		<button type="button" class="btn  btn-blue-dark loader-imger" id="save_btn" onClick="saveDetails()"> Save </button>
        
        <button type="button" class="btn btn-lg btn-blue"  data-dismiss="modal">Close</button>
      </div>
         </div>
		 
		 <div id="sandboxCredentials" class="tab-pane" style="height:467px; overflow-y:auto">
		 <div class="container">
			<!--<h5 class="mt-4 pl-4">Credentials :</h5>-->
			
			 <div id="usercreddetails" class="meterialform">
          <!--  <h6>Sandbox :  <div class="form-check form-check-inline">
                <input class="form-check-input" type="checkbox" id="sandboxRadio">
                <label for="sandboxRadio"><span class="ui"></span> </label>
                <span class="labletxt">Enabled</span> </div> <span class="pull-right"><img src="${contextPath}/static/mastergst/images/master/editdd.png" alt="Edit" onclick="editUserKeys('Sandbox')"> <img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" id="deleteSandbox"><img src="${contextPath}/static/mastergst/images/master/save-icon.png" alt="Save" onclick="saveUserKeys('Sandbox')"></span></h6>-->
          	<div>&nbsp;</div>
			<div class="row">
			<div class="row">
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">SandBox Access<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandIsEnabled">          
							<span class="sandIsEnabled usersandbox_field" id="SandIsEnabled" data="isenabled"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">Name<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandKeyName">          
							<span class="sandName usersandbox_field" id="SandKeyName" data="keyname"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">Client ID<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandClientID">          
							<span class="sandClientId usersandbox_field" id="SandClientID" data="clientid"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">Client Secret ID<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandSecretID">          
							<span class="sandSecretId usersandbox_field" id="SandSecretID" data="clientsecret"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">Created Date<span class="coln-txt">:</span></label>
						<div class="col-md-8">          
							<span class="sandDate usersandbox_field" data="createdate"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					
				</div>
			</div>
			</div>
			<h6>Normal TaxPayer</h6>
			<div class="row">
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTUserName1<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTUSERName1">          
							<span class="sandGSTUserName1 usersandbox_field" id="SandGSTUserName1" data="gstusername1"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTIN No1<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTINNO1">          
							<span class="sandGSTINNo1 usersandbox_field" id="SandGSTINNo1" data="gstinno1"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTUserName2<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTUSERName2">          
							<span class="sandGSTUserName2 usersandbox_field" id="SandGSTUserName2" data="gstusername2"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTIN No2<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTINNO2">          
							<span class="sandGSTINNo2 usersandbox_field" id="SandGSTINNo2" data="gstinno2"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTUserName3<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTUSERName3">          
							<span class="sandGSTUserName3 usersandbox_field" id="SandGSTUserName3" data="gstusername3"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTIN No3<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTINNO3">          
							<span class="sandGSTINNo3 usersandbox_field" id="SandGSTINNo3" data="gstinno3"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTUserName4<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTUSERName4">          
							<span class="sandGSTUserName4 usersandbox_field" id="SandGSTUserName4" data="gstusername4"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTIN No4<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTINNO4">          
							<span class="sandGSTINNo4 usersandbox_field" id="SandGSTINNo4" data="gstinno4"></span>
						</div>
				</div>
			</div>
			</div>
			<h6>Composite User</h6>
			<div class="row">
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTUserName1<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTCOMPUSERName1">          
							<span class="sandGSTCOMPUserName1 usersandbox_field" id="SandGSTCOMPUserName1" data="gstcompusername1"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTIN No1<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandCOMPGSTINNO1">          
							<span class="sandCOMPGSTINNo1 usersandbox_field" id="SandCOMPGSTINNo1" data="compgstinno1"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTUserName2<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTCOMPUSERName2">          
							<span class="sandGSTCOMPUserName2 usersandbox_field" id="SandGSTCOMPUserName2" data="gstcompusername2"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTIN No2<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandCOMPGSTINNO2">          
							<span class="sandCOMPGSTINNo2 usersandbox_field" id="SandCOMPGSTINNo2" data="compgstinno2"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTUserName3<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTCOMPUSERName3">          
							<span class="sandGSTCOMPUserName3 usersandbox_field" id="SandGSTCOMPUserName3" data="gstcompusername3"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTIN No3<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandCOMPGSTINNO3">          
							<span class="sandCOMPGSTINNo3 usersandbox_field" id="SandCOMPGSTINNo3" data="compgstinno3"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTUserName4<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTCOMPUSERName4">          
							<span class="sandGSTCOMPUserName4 usersandbox_field" id="SandGSTCOMPUserName4" data="gstcompusername4"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTIN No4<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandCOMPGSTINNO4">          
							<span class="sandCOMPGSTINNo4 usersandbox_field" id="SandCOMPGSTINNo4" data="compgstinno4"></span>
						</div>
				</div>
			</div>
			</div>
			<h6>TCS User</h6>
			<div class="row">
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTUserName1<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTTCSUSERName1">          
							<span class="sandGSTTCSUserName1 usersandbox_field" id="SandGSTTCSUserName1" data="gsttcsusername1"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTIN No1<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandTCSGSTINNO1">          
							<span class="sandTCSGSTINNo1 usersandbox_field" id="SandTCSGSTINNo1" data="tcsgstinno1"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTUserName2<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTTCSUSERName2">          
							<span class="sandGSTTCSUserName2 usersandbox_field" id="SandGSTTCSUserName2" data="gsttcsusername2"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTIN No2<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandTCSGSTINNO2">          
							<span class="sandTCSGSTINNo2 usersandbox_field" id="SandTCSGSTINNo2" data="tcsgstinno2"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTUserName3<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTTCSUSERName3">          
							<span class="sandGSTTCSUserName3 usersandbox_field" id="SandGSTTCSUserName3" data="gsttcsusername3"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTIN No3<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandTCSGSTINNO3">          
							<span class="sandTCSGSTINNo3 usersandbox_field" id="SandTCSGSTINNo3" data="tcsgstinno3"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTUserName4<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTTCSUSERName4">          
							<span class="sandGSTTCSUserName4 usersandbox_field" id="SandGSTTCSUserName4" data="gsttcsusername4"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTIN No4<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandTCSGSTINNO4">          
							<span class="sandTCSGSTINNo4 usersandbox_field" id="SandTCSGSTINNo4" data="tcsgstinno4"></span>
						</div>
				</div>
			</div>
			</div>
			<h6>TDS User</h6>
			<div class="row">
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTUserName1<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTTDSUSERName1">          
							<span class="sandGSTTDSUserName1 usersandbox_field" id="SandGSTTDSUserName1" data="gsttdsusername1"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTIN No1<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandTDSGSTINNO1">          
							<span class="sandTDSGSTINNo1 usersandbox_field" id="SandTDSGSTINNo1" data="tdsgstinno1"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTUserName2<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTTDSUSERName2">          
							<span class="sandGSTTDSUserName2 usersandbox_field" id="SandGSTTDSUserName2" data="gsttdsusername2"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTIN No2<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandTDSGSTINNO2">          
							<span class="sandTDSGSTINNo2 usersandbox_field" id="SandTDSGSTINNo2" data="tdsgstinno2"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTUserName3<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTTDSUSERName3">          
							<span class="sandGSTTDSUserName3 usersandbox_field" id="SandGSTTDSUserName3" data="gsttdsusername3"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTIN No3<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandTDSGSTINNO3">          
							<span class="sandTDSGSTINNo3 usersandbox_field" id="SandTDSGSTINNo3" data="tdsgstinno3"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTUserName4<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTTDSUSERName4">          
							<span class="sandGSTTDSUserName4 usersandbox_field" id="SandGSTTDSUserName4" data="gsttdsusername4"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTIN No4<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandTDSGSTINNO4">          
							<span class="sandTDSGSTINNo4 usersandbox_field" id="SandTDSGSTINNo4" data="tdsgstinno4"></span>
						</div>
				</div>
			</div>
			</div>
			<h6>ISD User</h6>
			<div class="row">
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTUserName1<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTISDUSERName1">          
							<span class="sandGSTISDUserName1 usersandbox_field" id="SandGSTISDUserName1" data="gstisdusername1"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTIN No1<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandISDGSTINNO1">          
							<span class="sandISDGSTINNo1 usersandbox_field" id="SandISDGSTINNo1" data="isdgstinno1"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTUserName2<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTISDUSERName2">          
							<span class="sandGSTISDUserName2 usersandbox_field" id="SandGSTISDUserName2" data="gstisdusername2"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTIN No2<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandISDGSTINNO2">          
							<span class="sandISDGSTINNo2 usersandbox_field" id="SandISDGSTINNo2" data="isdgstinno2"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTUserName3<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTISDUSERName3">          
							<span class="sandGSTISDUserName3 usersandbox_field" id="SandGSTISDUserName3" data="gstisdusername3"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTIN No3<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandISDGSTINNO3">          
							<span class="sandISDGSTINNo3 usersandbox_field" id="SandISDGSTINNo3" data="isdgstinno3"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTUserName4<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandGSTISDUSERName4">          
							<span class="sandGSTISDUserName4 usersandbox_field" id="SandGSTISDUserName4" data="gstisdusername4"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">GSTIN No4<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="sandISDGSTINNO4">          
							<span class="sandISDGSTINNo4 usersandbox_field" id="SandISDGSTINNo4" data="isdgstinno4"></span>
						</div>
				</div>
			</div>
			</div>
			</div>
			</div>
		 
		</div>
		<div class="modal-footer footer-fixed-bottom" style="background-color:white">
		<button type="button" class="btn btn-blue-dark permissionEDIT"  id="sand_edit_btn" onClick="editUserKeys('Sandbox')">Edit</button>
		<button type="button" class="btn  btn-blue-dark" id="sand_save_btn" onClick="saveUserKeys('Sandbox')">Save </button>
        <button type="button" class="btn btn-lg btn-blue"  data-dismiss="modal">Close</button>
      </div>
		 </div>

	<div id="payment" class="tab-pane permissionPAYMENTS payment_details" style="height:467px;overflow-y:auto">
		 <div class="container">
			<!--<h5 class="mt-4 pl-4">Credentials :</h5>-->
			
			 <div id="usercreddetailss" class="meterialform">
            
			<div>&nbsp;</div>
			<div class="row">
			<input type="hidden" class="form-control suvidhasubcenter_id"/>				
			<div class="form-group col-md-6 col-sm-12" id="aspStage"><div class="row">
				<label class="col-md-5">API Type<span class="coln-txt">:</span></label>
				<div class="col-md-7" id="apiStage">          		
					<select class='form-control paymentDt stageapiplanselect' id="stage">
						<option value="">-- Select Stage --</option>
						<option value="GSTAPI">GST APi</option>
						<option value="EWAYAPI">Eway Bill APi</option>
						<option value="E-INVOICEAPI">E-Invoice API</option>
						<option value="GSTSANDBOXAPI">GST Sandbox API</option>
						<option value="EWAYBILLSANDBOXAPI">Eway Bill Sandbox API</option>
						<option value="E-INVOICESANDBOXAPI">E-Invoice Sandbox API</option>
					</select>
				</div>
			</div></div>
			
			<div class="form-group col-md-6 col-sm-12 d-none" id="div_suvidhacentername">
				<div class="row">
					<label class="col-md-5">Center Name<span class="coln-txt">:</span></label>
					<div class="col-md-7 suvidhacentername">
						<select class='form-control' id="suvidhacentername"></select>
					</div>
				</div>
			</div>
						
			<div class="form-group col-md-6 col-sm-12 displayAPITYPES" id="aspType"><div class="row">
				<label class="col-md-5">Subscription Type<span class="coln-txt">:</span></label>
				<div class="col-md-7" id="paymentSubscriptionTypes">          
					<select class='form-control paymentDt paymentSubscriptionType paymentDtClear' id="paymentSubscriptionType">
						<option value="">-- Select Type --</option>
					</select> 
				</div>
			</div></div>
													
			<div class="form-group col-md-6 col-sm-12"><div class="row">
				<label class="col-md-5">Sub. Start Date<span class="coln-txt">:</span></label>
			<div class="col-md-7" id="subStartDate">          
					<input type="text" id="SubscriptionStartDate" class="form-control paymentDt paymentDtClear" name="SubscriptionStartDate" aria-describedby="SubscriptionStartDate" placeholder="DD-MM-YYYY" readonly="readonly"/>
           			<label for="SubscriptionStartDate" class="control-label"></label>
				</div>
			</div></div>
					
			<div class="form-group col-md-6 col-sm-12 expDate"><div class="row">
				<label class="col-md-5">Sub. Expiry Date<span class="coln-txt">:</span></label>
				<div class="col-md-7 ">
				<input type="text" id="SubscriptionExpiryDate" class="form-control paymentDt paymentDtClear" name="SubscriptionExpiryDate" aria-describedby="SubscriptionExpiryDate" placeholder="DD-MM-YYYY" readonly="readonly"/>
           		<label for="SubscriptionExpiryDate" class="control-label"></label>        
					
				</div>
			</div></div>
			
			<div class="form-group col-md-6 col-sm-12"><div class="row">
				<label class="col-md-5">Payment Mode<span class="coln-txt">:</span></label>
				<div class="col-md-7" id="paymentMode">          
					<input type="text" class='form-control paymentDt' id="idASPMode">
				</div>
			</div></div>
			<div class="form-group col-md-6 col-sm-12"><div class="row">
				<label class="col-md-5">Reference No<span class="coln-txt">:</span></label>
				<div class="col-md-7" id="referenceNo">          
					<input type="text" class='form-control paymentDt' id="idASPRef">
				</div>
			</div></div>
			<div class="form-group col-md-6 col-sm-12" id="suvidhaPaymentType"><div class="row">
				<label class="col-md-5">Subscription Type<span class="coln-txt">:</span></label>
				<div class="col-md-7" id="casubscriptionType">          
					<!--<input type="text" class='form-control paymentDt' id="stage">-->
					<select class='form-control paymentDt' id="subscriptionType">
						<option value="">-- Select Type --</option>
						<option value="Test">Test</option>
						<option value="Small">Small</option>
						<option value="Large">Large</option>
						<option value="Pro">Pro</option>
					</select>
				</div>
			</div></div>
			
			<div class="form-group col-md-6 col-sm-12" id="businessPaymentType"><div class="row">
				<label class="col-md-5">Subscription Type<span class="coln-txt">:</span></label>
				<div class="col-md-7" id="casubscriptionType">          
					<!--<input type="text" class='form-control paymentDt' id="stage">-->
					<select class='form-control paymentDt' id="businessSubscriptionType">
						<option value="">-- Select Type --</option>
						<option value="Test">Test</option>
						<option value="Small">Small</option>
						<option value="Medium">Medium</option>
					</select>
				</div>
			</div></div>
			
			
			
			
			
			
			<div class="form-group col-md-6 col-sm-12"><div class="row">
				<label class="col-md-5">Paid Amount<span class="coln-txt">:</span></label>
				<div class="col-md-7" id="paidAmount">          
					<input type="text" class='form-control paymentDt' id="idASPAmt">
				</div>
			</div></div>
			<div class="form-group col-md-6 col-sm-12" id="aspRate"><div class="row">
				<label class="col-md-5">Rate per Invoice<span class="coln-txt">:</span></label>
				<div class="col-md-7" id="paidRate">
					<input type="text" class='form-control paymentDt' id="idASPRate">
				</div>
			</div></div>
			<div class="form-group col-md-6 col-sm-12 suvidhacenter_state" id="aspState"><div class="row">
                  <label class="col-md-5">State<span class="coln-txt">:</span></label>
                  <div class="col-md-7" >          
				 	 <input type="text" class='form-control paymentDt' id="statename" required="required" name="statename" placeholder="State" />
				  </div>
				  <div class="help-block with-errors"></div>
				  <div id="statenameempty" style="display:none">
						<div class="ddbox">
						  <p>Search didn't return any results.</p>
						</div>
					</div>
                  <i class="bar"></i> </div></div>
			 
			<div class="form-group col-md-6 col-sm-12"><div class="row">
				<label class="col-md-5"># of Invoices<span class="coln-txt">:</span></label>
				<div class="col-md-7" id="alInvs">          
					<input type="text" class='form-control paymentDt' id="idASPInv">
				</div>
			</div></div>
			
			<!--<div class="form-group col-md-6 col-sm-12" id="aspStage"><div class="row">
				<label class="col-md-5">Stage<span class="coln-txt">:</span></label>
				<div class="col-md-7" id="apiStage">          
					<input type="text" class='form-control paymentDt' id="stage">
					<select class='form-control paymentDt' id="stage">
						<option value="">-- Select Stage --</option>
						<option value="GSTAPI">GST APi</option>
						<option value="EWAYAPI">Eway Bill APi</option>
					</select>
				</div>
			</div></div>-->
					
			<div class="form-group col-md-6 col-sm-12" id="suvidhaPaymentClients"><div class="row">
				<label class="col-md-5"># of Clients<span class="coln-txt">:</span></label>
				<div class="col-md-7" id="suvidhaClients">          
					<input type="text" class='form-control paymentDt' id="idSuvidhaClnts">
				</div>
			</div></div>
			
			<div class="form-group col-md-6 col-sm-12" id="suvidhaPaymentCenters"><div class="row">
				<label class="col-md-5"># of Centers<span class="coln-txt">:</span></label>
				<div class="col-md-7" id="suvidhaCenters">          
					<input type="text" class='form-control paymentDt' id="idSuvidhaCenters">
				</div>
			</div></div>

			<div class="form-group col-md-6 col-sm-12" id="freeDays"><div class="row">
				<label class="col-md-5"># of Days<span class="coln-txt">:</span></label>
				<div class="col-md-7">          
					<input type="text" class='form-control paymentDt' id="idDays">
				</div>
			</div></div>
			<input type="hidden" id="planid">
				  
			</div>
			</div>
									
			<div>&nbsp;</div>
			<div class="customtable tabPayments">
                <table id="paymentsTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                  <thead>
                    <tr>
                      <th>Order ID</th>
                      <th>Payment</th>
                      <th>Payment Date</th>
                      <th>Status</th>
                      <th>Transaction ID</th>
                      <th>Paid Amount ( <i class="fa fa-rupee"></i> )</th>
                    </tr>
                  </thead>
                  <tbody>
                  </tbody>
                </table>
              </div>
		</div>
		<div class="modal-footer footer-fixed-bottom">
		<button type="button" class="btn  btn-blue-dark" id="save_pmnt_data" onClick="savePaymentData()">Add Payment</button>
        <button type="button" class="btn btn-lg btn-blue"  data-dismiss="modal">Close</button>
      </div>
		 </div>

<div id="ewaypayment" class="tab-pane">
		 <div class="container">
			<!--<h5 class="mt-4 pl-4">Credentials :</h5>-->
			
			 <div id="userewaycreddetailss" class="meterialform">
            
			<div>&nbsp;</div>
			<div class="row">
			<div class="form-group col-md-6 col-sm-12"><div class="row">
				<label class="col-md-5">Payment Mode<span class="coln-txt">:</span></label>
				<div class="col-md-7" id="ewaypaymentMode">          
					<input type="text" class='form-control ewaypaymentDt' id="idASPEwayMode">
				</div>
			</div></div>
			<div class="form-group col-md-6 col-sm-12"><div class="row">
				<label class="col-md-5">Reference No<span class="coln-txt">:</span></label>
				<div class="col-md-7" id="ewayreferenceNo">          
					<input type="text" class='form-control ewaypaymentDt' id="idASPEwayRef">
				</div>
			</div></div>
			<div class="form-group col-md-6 col-sm-12"><div class="row">
				<label class="col-md-5">Paid Amount<span class="coln-txt">:</span></label>
				<div class="col-md-7" id="ewaypaidAmount">          
					<input type="text" class='form-control ewaypaymentDt' id="idASPEwayAmt">
				</div>
			</div></div>
			<div class="form-group col-md-6 col-sm-12" id="aspRate"><div class="row">
				<label class="col-md-5">Rate per Invoice<span class="coln-txt">:</span></label>
				<div class="col-md-7" id="ewaypaidRate">
					<input type="text" class='form-control ewaypaymentDt' id="idASPEwayRate">
				</div>
			</div></div>
			<div class="form-group col-md-6 col-sm-12" id="aspState"><div class="row">
                  <label class="col-md-5">State<span class="coln-txt">:</span></label>
                  <div class="col-md-7" >          
				 	 <input type="text" class='form-control ewaypaymentDt' id="statename1" required="required" name="statename" placeholder="State" />
				  </div>
				  <div class="help-block with-errors"></div>
				  <div id="statenameempty" style="display:none">
						<div class="ddbox">
						  <p>Search didn't return any results.</p>
						</div>
					</div>
                  <i class="bar"></i> </div></div>
			 
			<div class="form-group col-md-6 col-sm-12"><div class="row">
				<label class="col-md-5"># of Invoices<span class="coln-txt">:</span></label>
				<div class="col-md-7" id="ewayalInvs">          
					<input type="text" class='form-control ewaypaymentDt' id="idASPEwayInv">
				</div>
			</div></div>
				  
			</div>
			</div>
			<div>&nbsp;</div>
			<div class="customtable tabPayments">
                <table id="ewaypaymentsTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                  <thead>
                    <tr>
                      <th>Order ID</th>
                      <th>Payment Date</th>
                      <th>Status</th>
                      <th>Transaction ID</th>
                      <th>Paid Amount ( <i class="fa fa-rupee"></i> )</th>
                    </tr>
                  </thead>
                  <tbody>
                  </tbody>
                </table>
              </div>
		</div>
		<div class="modal-footer footer-fixed-bottom">
		<button type="button" class="btn  btn-blue-dark" onClick="savePaymentData()">Save </button>
        <button type="button" class="btn btn-lg btn-blue"  data-dismiss="modal">Close</button>
      </div>
		 </div>
		 
		  <div id="productionCredentials" class="tab-pane permissionGST_PRODUCTION">
		 <div class="container">
			<!--<h5 class="mt-4 pl-4">Credentials :</h5>-->
			
			 <div id="usercreddetails" class="meterialform">
            <!--<h6>Production :  <div class="form-check form-check-inline">
                <input class="form-check-input" type="checkbox" id="productionRadio">
                <label for="productRadio"><span class="ui"></span> </label>
                <span class="labletxt">Enabled</span> </div> <span class="pull-right"><img src="${contextPath}/static/mastergst/images/master/editdd.png" alt="Edit" onclick="editUserKeys('Production')"> <img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" id="deleteProductbox"><img src="${contextPath}/static/mastergst/images/master/save-icon.png" alt="Save" onclick="saveUserKeys('Production')"> </span></h6>-->
			<div>&nbsp;</div>
			<div class="row">
			<div class="text-danger col-sm-12 text-center"><b>Client ID And Client Secret ID Could not Empty, If Empty Cannot Save</b></div>
			<br/>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">Production Access<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="prodIsEnabled">          
							<span class="prodIsEnabled userprod_field" id="ProdIsEnabled" data="isenabled"></span>
						</div>
				</div>
			</div>
			
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">Name<span class="coln-txt">:</span></label>
					<div class="col-md-8" id="prodKeyName">          
						<span class="productName userprod_field"  id="ProdKeyName" data="keyname"></span>
					</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">Client ID<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="prodClientID">          
							<span class="productClientId userprod_field" id="ProdClientID" data="clientid"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">Client Secret ID<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="prodSecretID">          
							<span class="productSecretId userprod_field" id="ProdSecretID" data="clientsecret"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">Partner Portal ID<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="prodPartnerID">          
							<span class="productPartnerId userprod_field" id="ProdPartnerID" data="partnerid"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">Created Date<span class="coln-txt">:</span></label>
						<div class="col-md-8">          
							<span class="productDate userprod_field" data="createdate"></span>
						</div>
				</div>
			</div>
				
			</div>
		 </div>
		
		</div>
		  <div class="modal-footer footer-fixed-bottom">
		<button type="button" class="btn btn-blue-dark permissionEDIT"  id="prod_edit_btn" onClick="editUserKeys('Production')">Edit</button>
		<button type="button" class="btn  btn-blue-dark" id="prod_save_btn" onClick="saveUserKeys('Production')">Save </button>
        <button type="button" class="btn btn-lg btn-blue"  data-dismiss="modal">Close</button>
      </div>
		 </div>
		 
		  <div id="ewaybillCredentials" class="tab-pane permissionEWAY_BILL_PRODUCTION">
		 <div class="container">
			<!--<h5 class="mt-4 pl-4">Credentials :</h5>-->
			
			 <div id="usercreddetails" class="meterialform">
            <!--<h6>Production :  <div class="form-check form-check-inline">
                <input class="form-check-input" type="checkbox" id="productionRadio">
                <label for="productRadio"><span class="ui"></span> </label>
                <span class="labletxt">Enabled</span> </div> <span class="pull-right"><img src="${contextPath}/static/mastergst/images/master/editdd.png" alt="Edit" onclick="editUserKeys('Production')"> <img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" id="deleteProductbox"><img src="${contextPath}/static/mastergst/images/master/save-icon.png" alt="Save" onclick="saveUserKeys('Production')"> </span></h6>-->
			<div>&nbsp;</div>
			<h4>Sandbox</h4>
			<div class="row">
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">E-Way Bill Access<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="ewaySandIsEnabled">          
							<span class="ewaySandIsEnabled userseway_field" id="EwaySandIsEnabled" data="isenabled"></span>
						</div>
				</div>
			</div>
			
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">Name<span class="coln-txt">:</span></label>
					<div class="col-md-8" id="ewaySandKeyName">          
						<span class="ewayName userseway_field"  id="EwaySandKeyName" data="keyname"></span>
					</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">Client ID<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="ewaySandClientID">          
							<span class="ewaySandClientID userseway_field" id="EwaySandClientID" data="clientid"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">Client Secret ID<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="ewaySandSecretID">          
							<span class="ewaySandSecretID userseway_field" id="EwaySandSecretID" data="clientsecret"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">Created Date<span class="coln-txt">:</span></label>
						<div class="col-md-8">          
							<span class="ewayDate userseway_field" data="createdate"></span>
						</div>
				</div>
			</div>
				
			</div>
			 <div class="modal-footer">
		<button type="button" class="btn btn-blue-dark"  id="ewaySand_edit_btn" onClick="editUserKeys('EwayBillSandBox')">Edit</button>
		<button type="button" class="btn  btn-blue-dark" id="ewaySand_save_btn" onClick="saveUserKeys('EwayBillSandBox')">Save </button>
        <button type="button" class="btn btn-lg btn-blue"  data-dismiss="modal">Close</button>
      </div>
		 </div>
		 <div class="btm-brdr"></div>
		 <div id="usercreddetails" class="meterialform">
            <!--<h6>Production :  <div class="form-check form-check-inline">
                <input class="form-check-input" type="checkbox" id="productionRadio">
                <label for="productRadio"><span class="ui"></span> </label>
                <span class="labletxt">Enabled</span> </div> <span class="pull-right"><img src="${contextPath}/static/mastergst/images/master/editdd.png" alt="Edit" onclick="editUserKeys('Production')"> <img src="${contextPath}/static/mastergst/images/master/delicon.png" alt="Delete" id="deleteProductbox"><img src="${contextPath}/static/mastergst/images/master/save-icon.png" alt="Save" onclick="saveUserKeys('Production')"> </span></h6>-->
			<div>&nbsp;</div>
			<h4>Production</h4>
			<div class="row">
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">E-Way Bill Access<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="ewayIsEnabled">          
							<span class="ewayIsEnabled userpeway_field" id="EwayIsEnabled" data="isenabled"></span>
						</div>
				</div>
			</div>
			
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">Name<span class="coln-txt">:</span></label>
					<div class="col-md-8" id="ewayKeyName">          
						<span class="ewayName userpeway_field"  id="EwayKeyName" data="keyname"></span>
					</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">Client ID<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="ewayClientID">          
							<span class="ewayClientId userpeway_field" id="EwayClientID" data="clientid"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">Client Secret ID<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="ewaySecretID">          
							<span class="ewaySecretId userpeway_field" id="EwaySecretID" data="clientsecret"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">Partner Portal ID<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="ewayPartnerID">          
							<span class="ewayPartnerId userpeway_field" id="EwayPartnerID" data="partnerid"></span>
						</div>
				</div>
			</div>
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">Created Date<span class="coln-txt">:</span></label>
						<div class="col-md-8">          
							<span class="ewayDate userpeway_field" data="createdate"></span>
						</div>
				</div>
			</div>
				
			</div>
			 <div class="modal-footer">
		<button type="button" class="btn btn-blue-dark"  id="eway_edit_btn" onClick="editUserKeys('EwayBillProduction')">Edit</button>
		<button type="button" class="btn  btn-blue-dark" id="eway_save_btn" onClick="saveUserKeys('EwayBillProduction')">Save </button>
        <button type="button" class="btn btn-lg btn-blue"  data-dismiss="modal">Close</button>
      </div>
		 </div>
		</div>
	</div>
	<!-- e-invoice start -->
	<div id="eInvoiceCredentials" class="tab-pane">
		 <div class="container">
			<div id="usercreddetails" class="meterialform">
	            <div>&nbsp;</div>
				<h4>Sandbox</h4>
				<div class="row">
					<div class="form-group col-md-6 col-sm-12">
						<div class="row">
							<label class="col-md-4">E-Invoice Access<span class="coln-txt">:</span></label>
							<div class="col-md-8" id="einvSandIsEnabled">          
								<span class="einvSandIsEnabled userseinv_field" id="EinvSandIsEnabled" data="isenabled"></span>
							</div>
						</div>
					</div>
					
					<div class="form-group col-md-6 col-sm-12">
						<div class="row">
							<label class="col-md-4">Name<span class="coln-txt">:</span></label>
							<div class="col-md-8" id="einvSandKeyName">          
								<span class="einvName userseinv_field"  id="EinvSandKeyName" data="keyname"></span>
							</div>
						</div>
					</div>
					<div class="form-group col-md-6 col-sm-12">
						<div class="row">
							<label class="col-md-4">Client ID<span class="coln-txt">:</span></label>
							<div class="col-md-8" id="einvSandClientID">          
								<span class="einvSandClientID userseinv_field" id="EinvSandClientID" data="clientid"></span>
							</div>
						</div>
					</div>
					<div class="form-group col-md-6 col-sm-12">
						<div class="row">
							<label class="col-md-4">Client Secret ID<span class="coln-txt">:</span></label>
							<div class="col-md-8" id="einvSandSecretID">          
								<span class="einvSandSecretID userseinv_field" id="EinvSandSecretID" data="clientsecret"></span>
							</div>
						</div>
					</div>
					<div class="form-group col-md-6 col-sm-12">
						<div class="row">
							<label class="col-md-4">User Name<span class="coln-txt">:</span></label>
							<div class="col-md-8" id="einvsandusername">          
								<span class="einvUsername userseinv_field" id="EinvSandUsername" data="username"></span>
							</div>
						</div>
					</div>
					<div class="form-group col-md-6 col-sm-12">
						<div class="row">
							<label class="col-md-4">Password<span class="coln-txt">:</span></label>
							<div class="col-md-8" id="einvsandpassword">          
								<span class="einvPassword userseinv_field" id="EinvSandPassword" data="password"></span>
							</div>
						</div>
					</div>
					<div class="form-group col-md-6 col-sm-12">
						<div class="row">
							<label class="col-md-4">Created Date<span class="coln-txt">:</span></label>
							<div class="col-md-8">          
								<span class="einvDate userseinv_field" data="createdate"></span>
							</div>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-blue-dark"  id="einvSand_edit_btn" onClick="editUserKeys('EInvoiceSandBox')">Edit</button>
					<button type="button" class="btn  btn-blue-dark" id="einvSand_save_btn" onClick="saveUserKeys('EInvoiceSandBox')">Save </button>
			        <button type="button" class="btn btn-lg btn-blue"  data-dismiss="modal">Close</button>
			    </div>
		 	</div>
		 	<div class="btm-brdr"></div>
		 	<div id="usercreddetails" class="meterialform">
            	<div>&nbsp;</div>
				<h4>Production</h4>
				<div class="row">
					<div class="form-group col-md-6 col-sm-12">
						<div class="row">
							<label class="col-md-4">E-Invoice Access<span class="coln-txt">:</span></label>
							<div class="col-md-8" id="einvIsEnabled">          
								<span class="einvIsEnabled userpeinv_field" id="EinvIsEnabled" data="isenabled"></span>
							</div>
						</div>
					</div>
					<div class="form-group col-md-6 col-sm-12">
						<div class="row">
							<label class="col-md-4">Name<span class="coln-txt">:</span></label>
							<div class="col-md-8" id="einvKeyName">          
								<span class="einvName userpeinv_field"  id="EinvKeyName" data="keyname"></span>
							</div>
						</div>
					</div>
					<div class="form-group col-md-6 col-sm-12">
						<div class="row">
							<label class="col-md-4">Client ID<span class="coln-txt">:</span></label>
							<div class="col-md-8" id="einvClientID">          
								<span class="einvClientId userpeinv_field" id="EinvClientID" data="clientid"></span>
							</div>
						</div>
					</div>
					<div class="form-group col-md-6 col-sm-12">
						<div class="row">
							<label class="col-md-4">Client Secret ID<span class="coln-txt">:</span></label>
							<div class="col-md-8" id="einvSecretID">          
								<span class="einvSecretId userpeinv_field" id="EinvSecretID" data="clientsecret"></span>
							</div>
						</div>
					</div>
					<div class="form-group col-md-6 col-sm-12">
						<div class="row">
							<label class="col-md-4">Partner Portal ID<span class="coln-txt">:</span></label>
							<div class="col-md-8" id="einvPartnerID">          
								<span class="einvPartnerId userpeinv_field" id="EinvPartnerID" data="partnerid"></span>
							</div>
						</div>
					</div>
					<div class="form-group col-md-6 col-sm-12">
						<div class="row">
							<label class="col-md-4">Created Date<span class="coln-txt">:</span></label>
							<div class="col-md-8">          
								<span class="einvDate userpeinv_field" data="createdate"></span>
							</div>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-blue-dark"  id="einv_edit_btn" onClick="editUserKeys('EInvoiceProduction')">Edit</button>
					<button type="button" class="btn  btn-blue-dark" id="einv_save_btn" onClick="saveUserKeys('EInvoiceProduction')">Save </button>
			        <button type="button" class="btn btn-lg btn-blue"  data-dismiss="modal">Close</button>
			    </div>
		 	</div>
		</div>	 
	</div>
	<!-- e-invoice end -->
	
		 
		 <div id="users" class="tab-pane">
		 <div class="container">
			<!--<h5 class="mt-4 pl-4">Credentials :</h5>-->
			<div>&nbsp;</div>
			<div class="customtable tabUsers">
                <table id="usersTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                  <thead>
                    <tr>
                      <th>Name</th>
                      <th>Email</th>
                      <th>Phone</th>
                      <th>Created Date</th>
                      <th>Status</th>
					  <th>Type</th>
                    </tr>
                  </thead>
                  <tbody>
                  </tbody>
                </table>
              </div>
		</div>
		 </div>
		
		<div id="headerkeysTab" class="tab-pane" style="height:450px;">
			<div class="container">
				<div class="customtable user-admin-tab db-ca-view headerkeysTab headerkeysTab2 mt-3">
					<table id="headerkeysTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
	                  <thead>
	                    <tr>
	                      <th>Gstusername</th>
	                      <th>Transaction no(txn)</th>
	                      <th>authtoken</th>
	                      <th>updated date</th>
						  <th>Action</th>
	                    </tr>
	                  </thead>
	                  <tbody>
	                  </tbody>
	                </table>
	              </div>
			</div>
		 <div class="modal-footer footer-fixed-bottom">
	        <button type="button" class="btn btn-lg btn-blue"  data-dismiss="modal">Close</button>
	     </div>
		</div>
		<div id="asp_apiuserdetailsTab" class="tab-pane" style="height:450px;">
			<div class="container">
				<div class="row">
					<div class="form-group col-md-6">
						<label class="" for="apidetailsfname" >First Name<span class="coln-txt">:</span></label>
						<span class="" id="apidetailsfname"></span>
					</div>
					<div class="form-group col-md-6">
						<label class="" for="apidetailslname" >Last Name<span class="coln-txt">:</span></label>
						<span class="" id="apidetailslname"></span>
					</div>
					<div class="form-group col-md-6">
						<label class="" for="apidetailsemail" >Email<span class="coln-txt">:</span></label>
						<span class="" id="apidetailsemail"></span>
					</div>
					<div class="form-group col-md-6">
						<label class="" for="apidetailsmobileno" >Mobile No<span class="coln-txt">:</span></label>
						<span class="" id="apidetailsmobileno"></span>
					</div>
					<div class="form-group col-md-6">
						<label class="" for="apidetailscmpnyregname" >Company Register Name<span class="coln-txt">:</span></label>
						<span class="" id="apidetailscmpnyregname"></span>
					</div>
					<div class="form-group col-md-6">
						<label class="" for="apidetailscmpnyaddress" >Company Address<span class="coln-txt">:</span></label>
						<span class="" id="apidetailscmpnyaddress"></span>
					</div>
					<div class="form-group col-md-6">
						<label class="" for="apidetailscdate" >Created Date<span class="coln-txt">:</span></label>
						<span class="" id="apidetailscdate"></span>
					</div>
				</div>
			</div>
			<div class="modal-footer footer-fixed-bottom">
	        	<button type="button" class="btn btn-lg btn-blue"  data-dismiss="modal">Close</button>
	      	</div>
		</div>
		
		<div id="commentsTab" class="tab-pane" style="height:450px;width:798px;">
		 <div class="container">
			<div>&nbsp;</div>
			<div class="customtable tabComments alertcommentsdata" style="width:1000px;padding:14px;"></div>
			<div>&nbsp;</div>
			
			<div class="form-group col-md-12 col-sm-12">
				<div class="row">
					<label class="col-md-2">Comments<span class="coln-txt">:</span></label>
						<div class="col-md-10" id="divcommentsdata">          
							<textarea class='commentsData' id='exampleFormControlTextarea1'></textarea>
						</div>
				</div>
			</div>
			<div class="row p-3">
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">Date<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="divcommentsdate">          
							<input type='text' id='commentsDate' style='width:95%' class='form-control commentsDate showcommentdate' name='commentsDate' aria-describedby='commentsDate' placeholder='DD-MM-YYYY' onmousedown='datepick();'/><label for='commentsDate' class='control-label'></label>
						</div>
				</div>
			</div>	
			<div class="form-group col-md-6 col-sm-12">
				<div class="row">
					<label class="col-md-4">Added By<span class="coln-txt">:</span></label>
						<div class="col-md-8" id="divcommentsaddedby">          
							<input class='form-control addedby' style='width:95%' id='addedby' placeholder='Enter Name'/>
						</div>
				</div>
			</div>
			</div>
			</div>
              <div class="modal-footer footer-fixed-bottom">
				
				<button type="button" class="btn  btn-blue-dark" id="cmmnt_save_btn" onClick="saveComments()">SUBMIT</button>
        		<button type="button" class="btn btn-lg btn-blue"  data-dismiss="modal">Close</button>
      		  </div>
		
		 </div>	 <!-- style="height:450px;width:798px;" -->
		<!-- centersTab start  -->
	    <div id="centersTab" class="tab-pane">
	    	<div class="container">
	    		<div>&nbsp;</div>
				<div class="customtable centersTable">
	                <table id="centersTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
	                  <thead>
	                    <tr>
	                      <th>Name</th>
	                      <th>Email</th>
	                      <th>Phone</th>
	                      <th>Created Date</th>
	                      <th>Status</th>
						  <th>Type</th>
						  <th>Action</th>
	                    </tr>
	                  </thead>
	                  <tbody>
	                  </tbody>
	                </table>
	              </div>
	    	</div>
	    	<div class="modal-footer footer-fixed-bottom">	
				<!-- <button type="button" class="btn btn-blue-dark" id="cmmn_save_btn" onClick="saveomments()">SUBMIT</button> -->
        		<button type="button" class="btn btn-lg btn-blue"  data-dismiss="modal">Close</button>
      		</div>
	    </div>
	    <!-- centersTab end  -->
	    
	    <!-- refClientTab start  -->
	    <div id="refClientTab" class="tab-pane">
	    	<div class="container">
		    	<div>&nbsp;</div>
		    	<div class="partner-data">
					<div class="customtable user-admin-tab db-ca-view refClientTable refClientTable2">
						<table id="refClientTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
			              <thead>
			                <tr>
			                  <th class="text-center">Client Name</th>
			                  <th class="text-center">Client Phone Number</th>
			                  <th class="text-center">Client Email</th>
			                  <th class="text-center">Join Status</th>
			                  <!-- <th class="text-center">Subsci. Date</th> -->
			                 <!--  <th class="text-center">Subscri. Type</th> -->
					  		  <th class="text-center">Subscri. Amount</th>
			                </tr>
			              </thead>
			              <tbody>         
			              </tbody>
		            	</table>
		          	</div>		
				</div>
	    	</div>
	    	<div class="modal-footer footer-fixed-bottom">	
				<!-- <button type="button" class="btn btn-blue-dark" id="cmmn_save_btn" onClick="saveomments()">SUBMIT</button> -->
        		<button type="button" class="btn btn-lg btn-blue"  data-dismiss="modal">Close</button>
      		</div>
	    </div>
	    <!-- refClientTab end  -->
       
     	<!-- clientPaymentDetailsTab start  -->
	    <div id="clientPaymentDetailsTab" class="tab-pane">
	    	<div class="container">
		    	<div>&nbsp;</div>
		    	<div class="partner-data">
					<div class="customtable user-admin-tab db-ca-view clientPaymentDetailsTable clientPaymentDetailsTable2">
						<table id="clientPaymentDetailsTable123" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
			              <thead>
			                <tr> 
			                  <!-- <th class="text-center">Payment Ref</th>
			                  <th class="text-center">Payment Amount</th>
			                  <th class="text-center">Paid Date</th>
			                  <th class="text-center">Payment Type</th>
			                  <th class="text-center">Notes</th> -->
			                  <th class="text-center">Ref. ID/Client ID</th>
			                  <th class="text-center">Client Name</th>
			                  <th class="text-center">Client Sub.Amount</th>
			                  <th class="text-center">Ref.Amount</th>
			                  <th class="text-center">Payment Status</th>
			                  <th class="text-center">Payment.Ref.</th>
			                </tr>
			              </thead>
			              <tbody>     
			              </tbody>
		            	</table>
		          </div>		
				</div>
	    	</div>
	    	<div class="modal-footer footer-fixed-bottom">	
				<!-- <button type="button" class="btn btn-blue-dark" id="cmmn_save_btn" onClick="saveomments()">SUBMIT</button> -->
        		<button type="button" class="btn btn-lg btn-blue"  data-dismiss="modal">Close</button>
      		</div>
	    </div>
	    <!-- clientPaymentDetailsTab end  -->
	    
	    <!-- clientPendingPaymentDetailsTab start  -->
	    <div id="clientPendingPaymentDetailsTab" class="tab-pane">
	    	<div class="container">
		    	<div>&nbsp;</div>
		    	<div class="partner-data">
					<div class="customtable user-admin-tab db-ca-view clientPendingPaymentDetailsTable clientPendingPaymentDetailsTable2">
						<table id="clientPendingPaymentDetailsTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
			              <thead>
			                <tr> 
			                  <th class="text-center">Ref. ID/Client ID</th>
			                  <th class="text-center">Client Name</th>
			                  <th class="text-center">Client Sub.Amount</th>
			                  <th class="text-center">Ref.Amount</th>
			                  <th class="text-center">Payment Status</th>
			                  <th class="text-center">Payment.Ref.</th>
			                </tr>
			              </thead>
			              <tbody>     
			              </tbody>
		            	</table>
		          </div>		
				</div>
	    	</div>
	    	<div class="modal-footer footer-fixed-bottom">	
				<!-- <button type="button" class="btn btn-blue-dark" id="cmmn_save_btn" onClick="saveomments()">SUBMIT</button> -->
        		<button type="button" class="btn btn-lg btn-blue"  data-dismiss="modal">Close</button>
      		</div>
	    </div>
	    <!-- clientPendingPaymentDetailsTab end  -->
	    </div>  
  </div>
</div>
<!--  Modal end -->
</div>
</div>
<script>
function adminDashboard(tabId,nav){
	$('#userModal a.nav-link').removeClass('active');
	$(nav).addClass('active');
	$('#clientPaymentDetailsTab').css("display","none");
	$('#clientPendingPaymentDetailsTab').css("display","none");
	if(tabId == 'pymentTab'){
		$('#commentsTab').removeClass('d-block');
		$('#commentsTab').removeClass("active");
		$('#'+tabId).css("display","block");
		$('#details').css("display","none");
		$('#clients').css("display","none");
		$('#invoices').css("display","none");
		$('#sandboxCredentials').css("display","none");
		$('#productionCredentials').css("display","none");
		$('#ewaybillCredentials').css("display","none");
		$('#eInvoiceCredentials').css("display","none");
		$('#payment').css("display","none");
		$('#ewaypayment').css("display","none");
		$('#users').css("display","none");
		$('#save_btn').css('display','none');
		$('#edit_btn').css('display','block');
		$('#sand_save_btn').css('display','none');
		$('#sand_edit_btn').css('display','none');
		$('#prod_save_btn').css('display','none');
		$('#prod_edit_btn').css('display','none');
		$('#eway_save_btn').css('display','none');
		$('#eway_edit_btn').css('display','none');
		$('#ewaySand_save_btn').css('display','none');
		$('#ewaySand_edit_btn').css('display','none');
		$('#einv_save_btn').css('display','none');
		$('#einv_edit_btn').css('display','none');
		$('#einvSand_save_btn').css('display','none');
		$('#einvSand_edit_btn').css('display','none');
		$('#commentsTab').css("display","none");
		$('#centersTab').css("display","none");
		$('#refClientTab').css("display","none");
		$('#clientPaymentDetailsTab').css("display","none");
		$('#clientPendingPaymentDetailsTab').css("display","none");
		$('#headerkeysTab').css("display","none");
		$('#asp_apiuserdetailsTab').css("display","none");
	}else if(tabId == 'details'){
		$('#commentsTab').removeClass('d-block');
		$('#commentsTab').removeClass("active");
		$('#'+tabId).css("display","block");
		$('#clients').css("display","none");
		$('#invoices').css("display","none");
		$('#sandboxCredentials').css("display","none");
		$('#productionCredentials').css("display","none");
		$('#ewaybillCredentials').css("display","none");
		$('#eInvoiceCredentials').css("display","none");
		$('#payment').css("display","none");
		$('#ewaypayment').css("display","none");
		$('#users').css("display","none");
		$('#save_btn').css('display','none');
		$('#edit_btn').css('display','block');
		$('#sand_save_btn').css('display','none');
		$('#sand_edit_btn').css('display','none');
		$('#prod_save_btn').css('display','none');
		$('#prod_edit_btn').css('display','none');
		$('#eway_save_btn').css('display','none');
		$('#eway_edit_btn').css('display','none');
		$('#ewaySand_save_btn').css('display','none');
		$('#ewaySand_edit_btn').css('display','none');
		$('#einv_save_btn').css('display','none');
		$('#einv_edit_btn').css('display','none');
		$('#einvSand_save_btn').css('display','none');
		$('#einvSand_edit_btn').css('display','none');
		$('#commentsTab').css("display","none");
		$('#centersTab').css("display","none");
		$('#refClientTab').css("display","none");
		$('#clientPaymentDetailsTab').css("display","none");
		$('#clientPendingPaymentDetailsTab').css("display","none");
		$('#headerkeysTab').css("display","none");
		$('#asp_apiuserdetailsTab').css("display","none");
	}else if(tabId == 'clients'){
		$('#commentsTab').removeClass('d-block');
		$('#commentsTab').removeClass("active");
		$('#'+tabId).css("display","block");
		$('#details').css("display","none");
		$('#invoices').css("display","none");
		$('#sandboxCredentials').css("display","none");
		$('#productionCredentials').css("display","none");
		$('#ewaybillCredentials').css("display","none");
		$('#eInvoiceCredentials').css("display","none");
		$('#payment').css("display","none");
		$('#ewaypayment').css("display","none");
		$('#users').css("display","none");
		$('#commentsTab').css("display","none");
		$('#centersTab').css("display","none");
		$('#refClientTab').css("display","none");
		$('#clientPaymentDetailsTab').css("display","none");
		$('#clientPendingPaymentDetailsTab').css("display","none");
		$('#headerkeysTab').css("display","none");
		$('#asp_apiuserdetailsTab').css("display","none");
	}else if(tabId == 'invoices'){
		$('#commentsTab').removeClass('d-block');
		$('#commentsTab').removeClass("active");
		$('#'+tabId).css("display","block");
		$('#details').css("display","none");
		$('#clients').css("display","none");
		$('#sandboxCredentials').css("display","none");
		$('#productionCredentials').css("display","none");
		$('#ewaybillCredentials').css("display","none");
		$('#eInvoiceCredentials').css("display","none");
		$('#payment').css("display","none");
		$('#ewaypayment').css("display","none");
		$('#users').css("display","none");
		$('#commentsTab').css("display","none");
		$('#centersTab').css("display","none");
		$('#refClientTab').css("display","none");
		$('#clientPaymentDetailsTab').css("display","none");
		$('#clientPendingPaymentDetailsTab').css("display","none");
		$('#headerkeysTab').css("display","none");
		$('#asp_apiuserdetailsTab').css("display","none");
	}else if(tabId == 'sandboxCredentials'){
		$('#commentsTab').removeClass('d-block');
		$('#commentsTab').removeClass("active");
		$('#'+tabId).css("display","block");
		$('#details').css("display","none");
		$('#clients').css("display","none");
		$('#invoices').css("display","none");
		$('#productionCredentials').css("display","none");
		$('#ewaybillCredentials').css("display","none");
		$('#eInvoiceCredentials').css("display","none");
		$('#payment').css("display","none");
		$('#ewaypayment').css("display","none");
		$('#sand_edit_btn').css('display','block');
		$('#sand_save_btn').css('display','none');
		$('#save_btn').css('display','none');
		$('#edit_btn').css('display','none');
		$('#prod_save_btn').css('display','none');
		$('#prod_edit_btn').css('display','none');
		$('#eway_save_btn').css('display','none');
		$('#eway_edit_btn').css('display','none');
		$('#ewaySand_save_btn').css('display','none');
		$('#ewaySand_edit_btn').css('display','none');
		$('#einv_save_btn').css('display','none');
		$('#einv_edit_btn').css('display','none');
		$('#einvSand_save_btn').css('display','none');
		$('#einvSand_edit_btn').css('display','none');
		$('#users').css("display","none");
		$('#commentsTab').css("display","none");
		$('#centersTab').css("display","none");
		$('#refClientTab').css("display","none");
		$('#clientPaymentDetailsTab').css("display","none");
		$('#clientPendingPaymentDetailsTab').css("display","none");
		$('#headerkeysTab').css("display","none");
		$('#asp_apiuserdetailsTab').css("display","none");
	}else if(tabId == 'productionCredentials'){
		$('#commentsTab').removeClass('d-block');
		$('#commentsTab').removeClass("active");
		$('#'+tabId).css("display","block");
		$('#details').css("display","none");
		$('#clients').css("display","none");
		$('#invoices').css("display","none");
		$('#sandboxCredentials').css("display","none");
		$('#ewaybillCredentials').css("display","none");
		$('#eInvoiceCredentials').css("display","none");
		$('#payment').css("display","none");
		$('#ewaypayment').css("display","none");
		$('#save_btn').css('display','none');
		$('#edit_btn').css('display','none');
		$('#sand_save_btn').css('display','none');
		$('#sand_edit_btn').css('display','none');
		$('#eway_save_btn').css('display','none');
		$('#eway_edit_btn').css('display','none');
		$('#prod_edit_btn').css('display','block');
		$('#prod_save_btn').css('display','none');
		$('#ewaySand_save_btn').css('display','none');
		$('#ewaySand_edit_btn').css('display','none');
		$('#einv_save_btn').css('display','none');
		$('#einv_edit_btn').css('display','none');
		$('#einvSand_save_btn').css('display','none');
		$('#einvSand_edit_btn').css('display','none');
		$('#users').css("display","none");
		$('#commentsTab').css("display","none");
		$('#centersTab').css("display","none");
		$('#refClientTab').css("display","none");
		$('#clientPaymentDetailsTab').css("display","none");
		$('#clientPendingPaymentDetailsTab').css("display","none");
		$('#headerkeysTab').css("display","none");
		$('#asp_apiuserdetailsTab').css("display","none");
	}else if(tabId == 'ewaybillCredentials'){
		$('#commentsTab').removeClass('d-block');
		$('#commentsTab').removeClass("active");
		$('#'+tabId).css("display","block");
		$('#details').css("display","none");
		$('#clients').css("display","none");
		$('#invoices').css("display","none");
		$('#sandboxCredentials').css("display","none");
		$('#productionCredentials').css("display","none");
		$('#eInvoiceCredentials').css("display","none");
		$('#payment').css("display","none");
		$('#ewaypayment').css("display","none");
		$('#save_btn').css('display','none');
		$('#edit_btn').css('display','none');
		$('#sand_save_btn').css('display','none');
		$('#sand_edit_btn').css('display','none');
		$('#prod_edit_btn').css('display','none');
		$('#prod_save_btn').css('display','none');
		$('#eway_edit_btn').css('display','block');
		$('#eway_save_btn').css('display','none');
		$('#ewaySand_save_btn').css('display','none');
		$('#ewaySand_edit_btn').css('display','block');
		$('#einv_save_btn').css('display','none');
		$('#einv_edit_btn').css('display','none');
		$('#einvSand_save_btn').css('display','none');
		$('#einvSand_edit_btn').css('display','none');
		$('#users').css("display","none");
		$('#commentsTab').css("display","none");
		$('#centersTab').css("display","none");
		$('#refClientTab').css("display","none");
		$('#clientPaymentDetailsTab').css("display","none");
		$('#clientPendingPaymentDetailsTab').css("display","none");
		$('#headerkeysTab').css("display","none");
		$('#asp_apiuserdetailsTab').css("display","none");
	}else if(tabId == 'payment'){
		$('#commentsTab').removeClass('d-block');
		$('#commentsTab').removeClass("active");
		$('#'+tabId).css("display","block");
		$('#details').css("display","none");
		$('#clients').css("display","none");
		$('#invoices').css("display","none");
		$('#sandboxCredentials').css("display","none");
		$('#productionCredentials').css("display","none");
		$('#ewaybillCredentials').css("display","none");
		$('#eInvoiceCredentials').css("display","none");
		$('#ewaypayment').css("display","none");
		$('#users').css("display","none");
		$('#clientPaymentDetailsTab').css("display","none");
		$('#commentsTab').css("display","none");
		$('#centersTab').css("display","none");
		$('#refClientTab').css("display","none");
		$('#clientPendingPaymentDetailsTab').css("display","none");
		$('#headerkeysTab').css("display","none");
		$('#asp_apiuserdetailsTab').css("display","none");
	}else if(tabId == 'ewaypayment'){
		$('#commentsTab').removeClass('d-block');
		$('#commentsTab').removeClass("active");
		$('#'+tabId).css("display","block");
		$('#details').css("display","none");
		$('#clients').css("display","none");
		$('#invoices').css("display","none");
		$('#sandboxCredentials').css("display","none");
		$('#productionCredentials').css("display","none");
		$('#ewaybillCredentials').css("display","none");
		$('#eInvoiceCredentials').css("display","none");
		$('#payment').css("display","none");
		$('#commentsTab').css("display","none");
		$('#centersTab').css("display","none");
		$('#refClientTab').css("display","none");
		$('#clientPaymentDetailsTab').css("display","none");
		$('#clientPendingPaymentDetailsTab').css("display","none");
		$('#headerkeysTab').css("display","none");
		$('#asp_apiuserdetailsTab').css("display","none");
	}else if(tabId == 'users'){
		$('#commentsTab').removeClass('d-block');
		$('#commentsTab').removeClass("active");
		$('#'+tabId).css("display","block");
		$('#details').css("display","none");
		$('#payment').css("display","none");
		$('#clients').css("display","none");
		$('#invoices').css("display","none");
		$('#sandboxCredentials').css("display","none");
		$('#productionCredentials').css("display","none");
		$('#ewaybillCredentials').css("display","none");
		$('#eInvoiceCredentials').css("display","none");
		$('#ewaypayment').css("display","none");
		$('#users').css("display","block");
		$('#commentsTab').css("display","none");
		$('#centersTab').css("display","none");
		$('#refClientTab').css("display","none");
		$('#clientPaymentDetailsTab').css("display","none");
		$('#clientPendingPaymentDetailsTab').css("display","none");
		$('#asp_apiuserdetailsTab').css("display","none");
		$('#headerkeysTab').css("display","none");
	}else if(tabId == 'commentsTab'){
		$('#commentsTab').addClass('d-block');
		$('#commentsTab').addClass("active");
		$('#'+tabId).css("display","block");
		$('#details').css("display","none");
		$('#payment').css("display","none");
		$('#clients').css("display","none");
		$('#invoices').css("display","none");
		$('#sandboxCredentials').css("display","none");
		$('#productionCredentials').css("display","none");
		$('#ewaybillCredentials').css("display","none");
		$('#eInvoiceCredentials').css("display","none");
		$('#ewaypayment').css("display","none");
		$('#users').css("display","none");
		$('#centersTab').css("display","none");
		$('#refClientTab').css("display","none");
		$('#clientPaymentDetailsTab').css("display","none");
		$('#clientPendingPaymentDetailsTab').css("display","none");
		$('#headerkeysTab').css("display","none");
		$('#asp_apiuserdetailsTab').css("display","none");
	}else if(tabId == 'centersTab'){
		$('#commentsTab').removeClass('d-block');
		$('#commentsTab').removeClass("active");
		$('#'+tabId).css("display","block");
		$('#details').css("display","none");
		$('#payment').css("display","none");
		$('#clients').css("display","none");
		$('#invoices').css("display","none");
		$('#sandboxCredentials').css("display","none");
		$('#productionCredentials').css("display","none");
		$('#ewaybillCredentials').css("display","none");
		$('#eInvoiceCredentials').css("display","none");
		$('#ewaypayment').css("display","none");
		$('#users').css("display","none");
		$('#commentsTab').css("display","none");
		//$('#centersTab').css("display","block");
		$('#refClientTab').css("display","none");
		$('#clientPaymentDetailsTab').css("display","none");
		$('#clientPendingPaymentDetailsTab').css("display","none");
		$('#headerkeysTab').css("display","none");
		$('#asp_apiuserdetailsTab').css("display","none");
	}else if(tabId == 'refClientTab'){
		$('#commentsTab').removeClass('d-block');
		$('#commentsTab').removeClass("active");
		$('#'+tabId).css("display","block");
		$('#details').css("display","none");
		$('#payment').css("display","none");
		$('#clients').css("display","none");
		$('#invoices').css("display","none");
		$('#sandboxCredentials').css("display","none");
		$('#productionCredentials').css("display","none");
		$('#ewaybillCredentials').css("display","none");
		$('#eInvoiceCredentials').css("display","none");
		$('#ewaypayment').css("display","none");
		$('#users').css("display","none");
		$('#commentsTab').css("display","none");
		$('#centersTab').css("display","none");
		$('#clientPaymentDetailsTab').css("display","none");
		$('#clientPendingPaymentDetailsTab').css("display","none");
		$('#headerkeysTab').css("display","none");
		$('#asp_apiuserdetailsTab').css("display","none");
	}else if(tabId == 'clientPaymentDetailsTab'){
		$('#commentsTab').removeClass('d-block');
		$('#commentsTab').removeClass("active");
		$('#'+tabId).css("display","block");
		$('#details').css("display","none");
		$('#payment').css("display","none");
		$('#clients').css("display","none");
		$('#invoices').css("display","none");
		$('#sandboxCredentials').css("display","none");
		$('#productionCredentials').css("display","none");
		$('#ewaybillCredentials').css("display","none");
		$('#eInvoiceCredentials').css("display","none");
		$('#ewaypayment').css("display","none");
		$('#users').css("display","none");
		$('#commentsTab').css("display","none");
		$('#centersTab').css("display","none");
		$('#refClientTab').css("display","none");
		$('#clientPendingPaymentDetailsTab').css("display","none");
		$('#headerkeysTab').css("display","none");
		$('#asp_apiuserdetailsTab').css("display","none");
	}else if(tabId == 'clientPendingPaymentDetailsTab'){
		$('#commentsTab').removeClass('d-block');
		$('#commentsTab').removeClass("active");
		
		$('#'+tabId).css("display","block");
		$('#details').css("display","none");
		$('#payment').css("display","none");
		$('#clients').css("display","none");
		$('#invoices').css("display","none");
		$('#sandboxCredentials').css("display","none");
		$('#productionCredentials').css("display","none");
		$('#ewaybillCredentials').css("display","none");
		$('#eInvoiceCredentials').css("display","none");
		$('#ewaypayment').css("display","none");
		$('#users').css("display","none");
		$('#commentsTab').css("display","none");
		$('#centersTab').css("display","none");
		$('#refClientTab').css("display","none");
		$('#clientPaymentDetailsTab').css("display","none");
		$('#headerkeysTab').css("display","none");
		$('#asp_apiuserdetailsTab').css("display","none");
	}else if(tabId == 'headerkeysTab'){
		$('#'+tabId).css("display","block");
		$('#commentsTab').css("display","none");
		$('#commentsTab').removeClass('d-block');
		$('#commentsTab').removeClass("active");
		$('#details').css("display","none");
		$('#payment').css("display","none");
		$('#clients').css("display","none");
		$('#invoices').css("display","none");
		$('#sandboxCredentials').css("display","none");
		$('#productionCredentials').css("display","none");
		$('#ewaybillCredentials').css("display","none");
		$('#eInvoiceCredentials').css("display","none");
		$('#ewaypayment').css("display","none");
		$('#users').css("display","none");
		$('#centersTab').css("display","none");
		$('#refClientTab').css("display","none");
		$('#clientPaymentDetailsTab').css("display","none");
		$('#clientPendingPaymentDetailsTab').css("display","none");
		$('#asp_apiuserdetailsTab').css("display","none");
	}else if(tabId == 'asp_apiuserdetailsTab'){
		$('#'+tabId).css("display","block");
		$('#commentsTab').css("display","none");
		$('#commentsTab').removeClass('d-block');
		$('#commentsTab').removeClass("active");
		$('#details').css("display","none");
		$('#payment').css("display","none");
		$('#clients').css("display","none");
		$('#invoices').css("display","none");
		$('#sandboxCredentials').css("display","none");
		$('#productionCredentials').css("display","none");
		$('#ewaybillCredentials').css("display","none");
		$('#eInvoiceCredentials').css("display","none");
		$('#ewaypayment').css("display","none");
		$('#users').css("display","none");
		$('#centersTab').css("display","none");
		$('#refClientTab').css("display","none");
		$('#clientPaymentDetailsTab').css("display","none");
		$('#clientPendingPaymentDetailsTab').css("display","none");
		$('#headerkeysTab').css("display","none");
	}else if(tabId == 'eInvoiceCredentials'){
		$('#'+tabId).css("display","block");
		
		$('#commentsTab').removeClass('d-block');
		$('#commentsTab').removeClass("active");
		$('#details').css("display","none");
		$('#clients').css("display","none");
		$('#invoices').css("display","none");
		$('#sandboxCredentials').css("display","none");
		$('#productionCredentials').css("display","none");
		$('#ewaybillCredentials').css("display","none");
		$('#payment').css("display","none");
		$('#ewaypayment').css("display","none");
		$('#save_btn').css('display','none');
		$('#edit_btn').css('display','none');
		$('#sand_save_btn').css('display','none');
		$('#sand_edit_btn').css('display','none');
		$('#prod_edit_btn').css('display','none');
		$('#prod_save_btn').css('display','none');
		$('#eway_edit_btn').css('display','none');
		$('#eway_save_btn').css('display','none');
		$('#ewaySand_save_btn').css('display','none');
		$('#ewaySand_edit_btn').css('display','none');
		$('#einv_save_btn').css('display','none');
		$('#einv_edit_btn').css('display','block');
		$('#einvSand_save_btn').css('display','none');
		$('#einvSand_edit_btn').css('display','block');
		$('#users').css("display","none");
		$('#commentsTab').css("display","none");
		$('#centersTab').css("display","none");
		$('#refClientTab').css("display","none");
		$('#clientPaymentDetailsTab').css("display","none");
		$('#clientPendingPaymentDetailsTab').css("display","none");
		$('#headerkeysTab').css("display","none");
		$('#asp_apiuserdetailsTab').css("display","none");
	}
}


</script>