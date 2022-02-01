<%@page import="java.util.Collection"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.mastergst.usermanagement.runtime.service.Field"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
	<div class="bluehdr">
          <h3> ${pageCode} - Map the Fields</h3>
        </div>
        <!-- row begin -->
        <input type="hidden" name="pageCode" id="pageCode" value="${pageCode}"/>
        <input type="hidden" name="mappedPage" id="mappedPage" value="${mappedPage}"/>
		<div class="col-sm-12 customtable-wrap" style="padding:20px 20px 0px 20px">
          <table  class="display row-border meterialform" cellspacing="0" cellpadding="10" width="100%" style="border-collapse:separate!important">
              <thead>
                <tr>
                  <th class="hdr_w_25"><span class="tbl-hdr">GST Fields</span></th>
                  <th class="hdr_w_28"><span class="tbl-hdr">Mapping Status</span></th>
                  <th class="hdr_w_35"><span class="tbl-hdr">Excel Fields</span></th>          
                </tr>
              </thead>
              <tbody style="overflow:scroll">
              <%
              StringBuilder fieldsOpts = new StringBuilder();
              	List<String> mapperFields = (List<String>)request.getAttribute("mapperFields");
              	List<String> mapperFieldsLocal = new ArrayList<>();
              	mapperFieldsLocal.addAll(mapperFields);
              	int i=0;
              	String discount = (String)request.getAttribute("discount");
              	Map<String, String> mappings = (Map<String, String>)request.getAttribute("mappings");
              	
              	if(mappings != null){
              		int j=0;
              		Collection<String> mapValues = mappings.values();
	              	if(mapperFieldsLocal != null){
	              		for(String mp : mapperFieldsLocal){
	              			j++;
	              				if(!mapValues.contains(mp)){
	              					fieldsOpts.append("<option class=\"fieldopt_"+(j)+"\" data-opt=\"fieldopt_"+(j)+"\" value=\""+mp+"#MGST#"+(j)+"\">"+mp+"</option>");
	              				}
	              				
	              		}
	              	}
	              	for(String key : mapValues){
	          			mapperFieldsLocal.remove(key);
	              	}
              	}else{
	              	
	              	if(mapperFieldsLocal != null){
	              		for(String mp : mapperFieldsLocal){
	              			fieldsOpts.append("<option class=\"fieldopt_"+(++i)+"\" data-opt=\"fieldopt_"+(i)+"\" value=\""+mp+"#MGST#"+(i)+"\">"+mp+"</option>");
	              		}
	              	}
              	}
              	String fldOpts = fieldsOpts.toString();
              %>
              <c:if test="${fields != null}">
              <c:forEach var="field" items="${fields}">
                  <tr>
                  	 <td>${field.name}
                  	 	<c:if test="${field.name eq 'Discount'}">
                  	 		<div class="form-group col-md-12 col-sm-12 mt-0 mb-0">
								<div class="form-group-inline" style="display:inline-flex;">
									<div class="form-radio mb-0">
										<div class="radio">
											<label>
											<%if(discount != null){%> 
												<input name="discConfig" id="discper" type="radio" value="percentage" checked/>
											<%}else{%>
												<input name="discConfig" id="discper" type="radio" value="percentage" />
											<%} %>
											<i class="helper"></i>In %</label>
										</div>
									</div>
									<div class="form-radio mb-0">
										<div class="radio">
											<label>
											<%if(discount == null){%> 
												<input name="discConfig" id="discAmt" type="radio" value="amount" checked/>
											<%}else{ %>
												<input name="discConfig" id="discAmt" type="radio" value="amount"/>
											<%} %>
											
											
											<i class="helper"></i>In Amount</label>
										</div>
									</div>
								</div>
							</div>	
						</c:if>
						<input type="hidden" class="mapfldnme" data-val="${field.code}" name="${field.code}_fildnme"  id="${field.code}_fildnme" value="${field.name}"/></td>
                     <td>
                     	<img src="${contextPath}/static/mastergst/images/master/arrow-right.png" alt="True" class="img-fluid arrow-wd2" style="margin-left:5%"> 
                     	<%if(mappings == null){%> 
                     	<span class="action-img"><img src="${contextPath}/static/mastergst/images/master/false.png" alt="True" id="${field.code}_fildimg"></span>
                     	<%}else{ %>
                     	<span class="action-img"><img src="${contextPath}/static/mastergst/images/master/if_tick.png" alt="True" id="${field.code}_fildimg"></span>
                     	<%} %>
                     	<img src="${contextPath}/static/mastergst/images/master/arrow-left.png" alt="True" class="img-fluid arrow-wd2">
                     </td>
                     <td>
                     	<select class="form-control form-control-sm mapfildval" style="width:190px;max-width:260px;display:inline-block;margin-left:15%;margin-right:5px" data-val="${field.code}" name="${field.code}_fildval"  id="${field.code}_fildval">
                     		<option class="opt_0" data-opt="opt_0" value=""></option>
                     		
                     		<%if(mappings != null){
                     			Field fieldV = (Field)pageContext.getAttribute("field");
                     			String trField = mappings.get(fieldV.getName());
                     			int index = mapperFields.indexOf(trField);
                     			if(trField != null){
                     				out.print("<option class=\"fieldopt_"+(index+1)+"\" data-opt=\"fieldopt_"+(index+1)+"\" value=\""+trField+"#MGST#"+(index+1)+"\" selected>"+trField+"</option>");
                     			}
                     		}	
                     		%>
                     		<%=fldOpts%>
                     	</select>
						<a href="javascript:void" onclick="clearMappings('${field.code}_fildval')" style="display:inline-block">clear</a>
					</td>
                </tr>
                </c:forEach>
                </c:if>
                </tbody>
              </table>
			  </div>
				<div class="form-group  col-sm-12 col-xs-12  mt-3" style="padding-bottom:100px">
            <button class="btn btn-blue-dark pull-right" type="button" onclick="saveMapperFields()">Save</button>
			<button class="btn btn-blue pull-right mr-2" type="button" data-dismiss="modal">Close</button>
            <a href="javascript:void()" onclick="clearAllMappings('mapfildval')" class="btn btn-default pull-right mr-2">Clear All Mappings</a>
          </div>