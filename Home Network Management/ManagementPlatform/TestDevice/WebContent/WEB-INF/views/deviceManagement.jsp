<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>IoT Devices Mangement</title>
<link href="<c:url value="/resources/css/bootstrap.min.css" />"
	rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/css/treegrid.css" />"
	rel="stylesheet" type="text/css" />
<script type="text/javascript"
	src="<c:url value="/resources/js/jquery-1.12.1.min.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/bootstrap.min.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/testdevice.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/treegrid.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/treegrid.bootstrap3.js"/>"></script>

<script type="text/javascript">
	var statusEPC = "-128";
	var locationEPC = "-127";
	var currentLimitEPC = "-121";
	var powerSaveEPC = "-113"
	var throughPublicEPC = "-109";
	var currentTimeEPC = "-105";
	var currentDateEPC = "-104";
	var powerLimitEPC = "-103";

	var operationStatus = "OperationStatus";
	var installLocation = "InstallationLocation";
	var currentLimit = "Current_Limit_Setting";
	var powerSaving = "Power-saving_Operation_Setting";
	var throughPublicNetwork = "Remote_Control_Setting";
	var currentTimeSetting = "Current_Time_Setting";
	var currentDateSetting = "Current_Date_Setting";
	var powerLimit = "Power_Limit_Setting";

	$(document).ready(function() {
		closeEditModal();
		closeEditSensorModal();
		$('.tree').treegrid();
	});
</script>
</head>
<body>
	<!--navigation-->
	<nav class="nav navbar-inverse">
	<div class="container-fluid">
		<!-- Brand and toggle get grouped for better mobile display -->
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed"
				data-toggle="collapse" data-target="#bs-example-navbar-collapse-1"
				aria-expanded="false">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="/TestDevice">IoT Devices Mangement</a>
		</div>

		<!-- Collect the nav links, forms, and other content for toggling -->
		<div class="collapse navbar-collapse"
			id="bs-example-navbar-collapse-1">
			<ul class="nav navbar-nav">
				<li><a href="/TestDevice/download">Export Home Network
						topology</a></li>
				<li class="dropdown"><a href="#" class="dropdown-toggle"
					data-toggle="dropdown" role="button" aria-haspopup="true"
					aria-expanded="false">UPnP Resources <span class="caret"></span></a>
					<ul class="dropdown-menu">
						<li><a
							href="/TestDevice/UPnPResource?resource=echonetProfile.xml"
							target="_blank">ECHONET Lite Profile Description</a></li>
						<li><a
							href="/TestDevice/UPnPResource?resource=profileService.xml"
							target="_blank">ECHONET Lite Profile Service Description</a></li>
						<li role="separator" class="divider"></li>
						<li><a
							href="/TestDevice/UPnPResource?resource=temperatureSensor.xml"
							target="_blank">Temperature Sensor Description</a></li>
						<li><a
							href="/TestDevice/UPnPResource?resource=temperatureSensorService.xml"
							target="_blank">Temperature Sensor Service Description</a></li>
						<li role="separator" class="divider"></li>
						<li><a
							href="/TestDevice/UPnPResource?resource=humiditySensor.xml"
							target="_blank">Humidity Sensor Description</a></li>
						<li><a
							href="/TestDevice/UPnPResource?resource=humiditySensorService.xml"
							target="_blank">Humidity Sensor Service Description</a></li>
					</ul></li>
					<!-- <li onclick="deleteDevices()"><a href="#">Clear Database</a></li> -->
			</ul>
		</div>
		<!-- /.navbar-collapse -->
	</div>
	<!-- /.container-fluid --> </nav>
	<!-- <div class="col-md-3">
		    <div class="input-group">
		      <input type="text" class="form-control" placeholder="Device IP">
		      <span class="input-group-btn">
		        <button class="btn btn-default" type="button">Search</button>
		      </span>
		    </div>
		</div> -->
	</div>
	<!--content-->
	<div style="margin-top: 50px;">
		<table class="table table-hover tree">
			<thead>
				<tr>
					<th>Device IP</th>
					<th>Device name</th>
					<th>Status</th>
					<th></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:if test="${listDevices!=null}">
					<c:forEach items="${listDevices}" var="devObj" varStatus="loop">
						<c:if test="${devObj!=null}">
							<tr class="treegrid-${loop.index}">
								<td><c:if test="${devObj.profile.deviceIP!=null}">${devObj.profile.deviceIP}</c:if>
								</td>
								<td><c:if test="${devObj.profile.deviceIP!=null}">${devObj.profile.deviceName}</c:if>
								</td>
								<td><c:if
										test="${devObj.profile!=null && devObj.profile.operationStatus!=null}">
										<c:choose>
											<c:when test="${devObj.profile.operationStatus==true}">ON</c:when>
											<c:otherwise>OFF</c:otherwise>
										</c:choose>
									</c:if></td>
								<td><c:if test="${devObj.deviceType!=null}">
										<c:choose>
											<c:when test="${devObj.deviceType=='DirectDevice'}">
												<button type="button"
													onclick="loadEditDirectDeviceModal('${devObj.profile.deviceIP}')"
													class="btn btn-default">Edit</button>
											</c:when>
											<c:when test="${devObj.deviceType=='EchonetLiteDevice'}">
												<button type="button"
													onclick="loadEditModal('${devObj.profile.deviceIP}')"
													class="btn btn-default">Edit</button>
											</c:when>
											<c:otherwise>
											</c:otherwise>
										</c:choose>
									</c:if></td>
								<td><c:if test="${devObj.deviceType!=null}">
										<c:choose>
											<c:when test="${devObj.deviceType=='EchonetLiteDevice'}"></c:when>
											<c:when test="${devObj.deviceType=='DirectDevice'}">DirectDevice</c:when>
											<c:when test="${devObj.deviceType=='HomeGateway'}">HomeGateway</c:when>
											<c:otherwise>Unknown</c:otherwise>
										</c:choose>
									</c:if></td>
							</tr>

							<c:if test="${devObj.eObjList!=null}">
								<c:forEach items="${devObj.eObjList}" var="dataObj">
									<c:if test="${dataObj!=null}">
										<tr class="treegrid-parent-${loop.index} active">
											<td></td>
											<td>${dataObj.name}</td>
											<td><c:if test="${dataObj.operationStatus!=null}">
													<c:choose>
														<c:when test="${dataObj.operationStatus==true}">on</c:when>
														<c:otherwise>off</c:otherwise>
													</c:choose>
												</c:if></td>
											<td><c:if
													test="${devObj.profile.deviceIP!=null && dataObj.groupCode!=null && dataObj.classCode!=null && dataObj.instanceCode!=null}">
													<button
														onclick="loadEditSensorModal('${devObj.profile.deviceIP}', '${dataObj.groupCode}', '${dataObj.classCode}', '${dataObj.instanceCode}')"
														type="button" class="btn btn-default btn-sm">edit</button>
												</c:if></td>
											<td>
												<!-- <button type="button" class="btn btn-default btn-sm">delete</button> -->
											</td>
										</tr>
									</c:if>
								</c:forEach>
							</c:if>

						</c:if>
					</c:forEach>
				</c:if>
			</tbody>
		</table>
	</div>
	<!--edit echonet lite device modal-->
	<div class="modal fade" id="editModal" tabindex="-1" role="dialog">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" onclick="closeEditModal()"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">Edit device</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal" role="form">
						<input id="eGroupCode" type="hidden" value=""> <input
							id="eClassCode" type="hidden" value=""> <input
							id="eInstanceCode" type="hidden" value="">
						<div class="form-group">
							<!--Device IP-->
							<label class="col-md-3 control-label">Device IP:</label>
							<div class="col-md-2">
								<input id="eDeviceIP" class="form-control" value="" disabled>
							</div>
							<div class="col-md-1"></div>
							<!--Device name-->
							<label class="col-md-3 control-label">Device name:</label>
							<div class="col-md-2">
								<input id="eDeviceName" class="form-control" value="" disabled>
							</div>
							<div class="col-md-1"></div>
						</div>
						<div class="form-group">
							<!--Status-->
							<label class="col-md-3 control-label">Operation status:</label>
							<div class="col-md-2">
								<select id="eStatus" class="form-control">
									<option value="true">ON</option>
									<option value="false">OFF</option>
								</select>
							</div>
							<div class="col-md-1">
								<button
									onclick="setDeviceAttribute('eDeviceIP', 'eGroupCode', 'eClassCode', 'eInstanceCode', statusEPC, 'eStatus')"
									type="button" class="btn btn-default">Set</button>
							</div>
							<!--location-->
							<label class="col-md-3 control-label">Installation
								location:</label>
							<div class="col-md-2">
								<select id="eInstallLocation" class="form-control">
									<option value="living room" selected>Living Room</option>
									<option value="dining room">Dining Room</option>
									<option value="kitchen">Kitchen</option>
									<option value="bathroom">Bathroom</option>
									<option value="washroom/changing room">Washroom/Changing
										Room</option>
								</select>
							</div>
							<div class="col-md-1">
								<button
									onclick="setDeviceAttribute('eDeviceIP', 'eGroupCode', 'eClassCode', 'eInstanceCode', locationEPC, 'eInstallLocation')"
									type="button" class="btn btn-default">Set</button>
							</div>
						</div>
						<div class="form-group">
							<!--Version-->
							<label class="col-md-3 control-label">Standard version
								information:</label>
							<div class="col-md-2">
								<input id="eStandardVersionInfo" class="form-control" value=""
									disabled>
							</div>
							<div class="col-md-1"></div>
							<!--Identification-->
							<label class="col-md-3 control-label">Identification
								number:</label>
							<div class="col-md-2">
								<input id="eIdentificationNumber" class="form-control" value=""
									disabled>
							</div>
							<div class="col-md-1"></div>
						</div>
						<div class="form-group">
							<!--Instantaneous power-->
							<label class="col-md-3 control-label">Instantaneous power
								(W):</label>
							<div class="col-md-2">
								<input id="eInstantaneousPower" class="form-control" value=""
									disabled>
							</div>
							<div class="col-md-1"></div>
							<!--Cumulative power-->
							<label class="col-md-3 control-label">Cumulative power
								(kWh):</label>
							<div class="col-md-2">
								<input id="eCumulativePower" class="form-control" value=""
									disabled>
							</div>
							<div class="col-md-1"></div>
						</div>
						<div class="form-group">
							<!--Manufacturer fault code-->
							<label class="col-md-3 control-label">Manufacturer fault
								code:</label>
							<div class="col-md-2">
								<input id="eManufactureerFaultCode" class="form-control"
									value="" disabled>
							</div>
							<div class="col-md-1"></div>
							<!--Current limit setting-->
							<label class="col-md-3 control-label">Current limit
								setting (%):</label>
							<div class="col-md-2">
								<input id="eCurrentLimitSetting" class="form-control" value="">
							</div>
							<div class="col-md-1">
								<button
									onclick="setDeviceAttribute('eDeviceIP', 'eGroupCode', 'eClassCode', 'eInstanceCode', currentLimitEPC, 'eCurrentLimitSetting')"
									type="button" class="btn btn-default">Set</button>
							</div>
						</div>
						<div class="form-group">
							<!--Fault description-->
							<label class="col-md-3 control-label">Fault description:</label>
							<div class="col-md-2">
								<input id="eFaultDescription" class="form-control" value=""
									disabled>
							</div>
							<div class="col-md-1"></div>
							<!--Manufacturer code-->
							<label class="col-md-3 control-label">Manufacturer code:</label>
							<div class="col-md-2">
								<input id="eManufacturerCode" class="form-control" value=""
									disabled>
							</div>
							<div class="col-md-1"></div>
						</div>
						<div class="form-group">
							<!--Facility code-->
							<label class="col-md-3 control-label">Business facility
								code:</label>
							<div class="col-md-2">
								<input id="eBusinessFacilityCode" class="form-control" value=""
									disabled>
							</div>
							<div class="col-md-1"></div>
							<!--Product code-->
							<label class="col-md-3 control-label">Product code:</label>
							<div class="col-md-2">
								<input id="eProductCode" class="form-control" value="" disabled>
							</div>
							<div class="col-md-1"></div>
						</div>
						<div class="form-group">
							<!--Product number-->
							<label class="col-md-3 control-label">Production number:</label>
							<div class="col-md-2">
								<input id="eProductNumber" class="form-control" value=""
									disabled>
							</div>
							<div class="col-md-1"></div>
							<!--Product date-->
							<label class="col-md-3 control-label">Production date:</label>
							<div class="col-md-2">
								<input id="eProductDate" class="form-control" value="" disabled>
							</div>
							<div class="col-md-1"></div>
						</div>
						<div class="form-group">
							<!--Power save-->
							<label class="col-md-3 control-label">Power saving
								setting:</label>
							<div class="col-md-2">
								<select id="ePowerSaving" class="form-control">
									<option value="true">ON</option>
									<option value="false">OFF</option>
								</select>
							</div>
							<div class="col-md-1">
								<button
									onclick="setDeviceAttribute('eDeviceIP', 'eGroupCode', 'eClassCode', 'eInstanceCode', powerSaveEPC, 'ePowerSaving')"
									type="button" class="btn btn-default">Set</button>
							</div>
							<!--Through public network-->
							<label class="col-md-3 control-label">Remote control
								setting:</label>
							<div class="col-md-2">
								<select id="eThroughPublicNetwork" class="form-control">
									<option value="false">YES</option>
									<option value="true">NO</option>
								</select>
							</div>
							<div class="col-md-1">
								<button
									onclick="setDeviceAttribute('eDeviceIP', 'eGroupCode', 'eClassCode', 'eInstanceCode', throughPublicEPC, 'eThroughPublicNetwork')"
									type="button" class="btn btn-default">Set</button>
							</div>
						</div>
						<div class="form-group">
							<!--Current time-->
							<label class="col-md-3 control-label">Current time
								setting:</label>
							<div class="col-md-2">
								<input id="eCurrentTimeSetting" class="form-control" value="">
							</div>
							<div class="col-md-1">
								<button
									onclick="setDeviceAttribute('eDeviceIP', 'eGroupCode', 'eClassCode', 'eInstanceCode', currentTimeEPC, 'eCurrentTimeSetting')"
									type="button" class="btn btn-default">Set</button>
							</div>
							<!--Current date-->
							<label class="col-md-3 control-label">Current date
								setting:</label>
							<div class="col-md-2">
								<input id="eCurrentDateSetting" class="form-control" value="">
							</div>
							<div class="col-md-1">
								<button
									onclick="setDeviceAttribute('eDeviceIP', 'eGroupCode', 'eClassCode', 'eInstanceCode', currentDateEPC, 'eCurrentDateSetting')"
									type="button" class="btn btn-default">Set</button>
							</div>
						</div>
						<div class="form-group">
							<!--Power limit-->
							<label class="col-md-3 control-label">Power limit setting
								(W):</label>
							<div class="col-md-2">
								<input id="ePowerLimit" class="form-control" value="">
							</div>
							<div class="col-md-1">
								<button
									onclick="setDeviceAttribute('eDeviceIP', 'eGroupCode', 'eClassCode', 'eInstanceCode', powerLimitEPC, 'ePowerLimit')"
									type="button" class="btn btn-default">Set</button>
							</div>
							<!--Cumulative time-->
							<label class="col-md-3 control-label">Cumulative
								operating time:</label>
							<div class="col-md-2">
								<input id="eCumulativeTime" class="form-control" value=""
									disabled>
							</div>
							<div class="col-md-1"></div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default"
						onclick="closeEditModal()">Close</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->

	<!--edit direct device modal-->
	<div class="modal fade" id="editDirectDeviceModal" tabindex="-1"
		role="dialog">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close"
						onclick="closeEditDirectDeviceModal()" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">Edit direct device</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal" role="form">
						<div class="form-group">
							<!--Device IP-->
							<label class="col-md-3 control-label">Device IP:</label>
							<div class="col-md-2">
								<input id="dDeviceIP" class="form-control" value="" disabled>
							</div>
							<div class="col-md-1"></div>
							<!--Device name-->
							<label class="col-md-3 control-label">Device name:</label>
							<div class="col-md-2">
								<input id="dDeviceName" class="form-control" value="" disabled>
							</div>
							<div class="col-md-1"></div>
						</div>
						<div class="form-group">
							<!--Status-->
							<label class="col-md-3 control-label">Operation status:</label>
							<div class="col-md-2">
								<select id="dStatus" class="form-control">
									<option value="true">ON</option>
									<option value="false">OFF</option>
								</select>
							</div>
							<div class="col-md-1">
								<button
									onclick="setDirectDeviceAttribute('dDeviceIP', operationStatus, 'dStatus')"
									type="button" class="btn btn-default">Set</button>
							</div>
							<!--location-->
							<label class="col-md-3 control-label">Installation
								location:</label>
							<div class="col-md-2">
								<select id="dInstallLocation" class="form-control">
									<option value="living room" selected>Living Room</option>
									<option value="dining room">Dining Room</option>
									<option value="kitchen">Kitchen</option>
									<option value="bathroom">Bathroom</option>
									<option value="washroom/changing room">Washroom/Changing
										Room</option>
								</select>
							</div>
							<div class="col-md-1">
								<button
									onclick="setDirectDeviceAttribute('dDeviceIP', installLocation, 'dInstallLocation')"
									type="button" class="btn btn-default">Set</button>
							</div>
						</div>
						<div class="form-group">
							<!--Version-->
							<label class="col-md-3 control-label">Standard version
								information:</label>
							<div class="col-md-2">
								<input id="dStandardVersionInfo" class="form-control" value=""
									disabled>
							</div>
							<div class="col-md-1"></div>
							<!--Identification-->
							<label class="col-md-3 control-label">Identification
								number:</label>
							<div class="col-md-2">
								<input id="dIdentificationNumber" class="form-control" value=""
									disabled>
							</div>
							<div class="col-md-1"></div>
						</div>
						<div class="form-group">
							<!--Instantaneous power-->
							<label class="col-md-3 control-label">Instantaneous power
								(W):</label>
							<div class="col-md-2">
								<input id="dInstantaneousPower" class="form-control" value=""
									disabled>
							</div>
							<div class="col-md-1"></div>
							<!--Cumulative power-->
							<label class="col-md-3 control-label">Cumulative power
								(kWh):</label>
							<div class="col-md-2">
								<input id="dCumulativePower" class="form-control" value=""
									disabled>
							</div>
							<div class="col-md-1"></div>
						</div>
						<div class="form-group">
							<!--Manufacturer fault code-->
							<label class="col-md-3 control-label">Manufacturer fault
								code:</label>
							<div class="col-md-2">
								<input id="dManufactureerFaultCode" class="form-control"
									value="" disabled>
							</div>
							<div class="col-md-1"></div>
							<!--Current limit setting-->
							<label class="col-md-3 control-label">Current limit
								setting (%):</label>
							<div class="col-md-2">
								<input id="dCurrentLimitSetting" class="form-control" value="">
							</div>
							<div class="col-md-1">
								<button
									onclick="setDirectDeviceAttribute('dDeviceIP', currentLimit, 'dCurrentLimitSetting')"
									type="button" class="btn btn-default">Set</button>
							</div>
						</div>
						<div class="form-group">
							<!--Fault description-->
							<label class="col-md-3 control-label">Fault description:</label>
							<div class="col-md-2">
								<input id="dFaultDescription" class="form-control" value=""
									disabled>
							</div>
							<div class="col-md-1"></div>
							<!--Manufacturer code-->
							<label class="col-md-3 control-label">Manufacturer code:</label>
							<div class="col-md-2">
								<input id="dManufacturerCode" class="form-control" value=""
									disabled>
							</div>
							<div class="col-md-1"></div>
						</div>
						<div class="form-group">
							<!--Facility code-->
							<label class="col-md-3 control-label">Business facility
								code:</label>
							<div class="col-md-2">
								<input id="dBusinessFacilityCode" class="form-control" value=""
									disabled>
							</div>
							<div class="col-md-1"></div>
							<!--Product code-->
							<label class="col-md-3 control-label">Product code:</label>
							<div class="col-md-2">
								<input id="dProductCode" class="form-control" value="" disabled>
							</div>
							<div class="col-md-1"></div>
						</div>
						<div class="form-group">
							<!--Product number-->
							<label class="col-md-3 control-label">Production number:</label>
							<div class="col-md-2">
								<input id="dProductNumber" class="form-control" value=""
									disabled>
							</div>
							<div class="col-md-1"></div>
							<!--Product date-->
							<label class="col-md-3 control-label">Production date:</label>
							<div class="col-md-2">
								<input id="dProductDate" class="form-control" value="" disabled>
							</div>
							<div class="col-md-1"></div>
						</div>
						<div class="form-group">
							<!--Power save-->
							<label class="col-md-3 control-label">Power saving:</label>
							<div class="col-md-2">
								<select id="dPowerSaving" class="form-control">
									<option value="true">ON</option>
									<option value="false">OFF</option>
								</select>
							</div>
							<div class="col-md-1">
								<button
									onclick="setDirectDeviceAttribute('dDeviceIP', powerSaving, 'dPowerSaving')"
									type="button" class="btn btn-default">Set</button>
							</div>
							<!--Through public network-->
							<label class="col-md-3 control-label">Remote control
								setting:</label>
							<div class="col-md-2">
								<select id="dThroughPublicNetwork" class="form-control">
									<option value="true">YES</option>
									<option value="false">NO</option>
								</select>
							</div>
							<div class="col-md-1">
								<button
									onclick="setDirectDeviceAttribute('dDeviceIP', throughPublicNetwork, 'dThroughPublicNetwork')"
									type="button" class="btn btn-default">Set</button>
							</div>
						</div>
						<div class="form-group">
							<!--Current time-->
							<label class="col-md-3 control-label">Current time
								setting:</label>
							<div class="col-md-2">
								<input id="dCurrentTimeSetting" class="form-control" value="">
							</div>
							<div class="col-md-1">
								<button
									onclick="setDirectDeviceAttribute('dDeviceIP', currentTimeSetting, 'dCurrentTimeSetting')"
									type="button" class="btn btn-default">Set</button>
							</div>
							<!--Current date-->
							<label class="col-md-3 control-label">Current date
								setting:</label>
							<div class="col-md-2">
								<input id="dCurrentDateSetting" class="form-control" value="">
							</div>
							<div class="col-md-1">
								<button
									onclick="setDirectDeviceAttribute('dDeviceIP', currentDateSetting, 'dCurrentDateSetting')"
									type="button" class="btn btn-default">Set</button>
							</div>
						</div>
						<div class="form-group">
							<!--Power limit-->
							<label class="col-md-3 control-label">Power limit setting
								(W):</label>
							<div class="col-md-2">
								<input id="dPowerLimit" class="form-control" value="">
							</div>
							<div class="col-md-1
							">
								<button
									onclick="setDirectDeviceAttribute('dDeviceIP', powerLimit, 'dPowerLimit')"
									type="button" class="btn btn-default">Set</button>
							</div>
							<!--Cumulative time-->
							<label class="col-md-3 control-label">Cumulative
								operating time:</label>
							<div class="col-md-2">
								<input id="dCumulativeTime" class="form-control" value=""
									disabled>
							</div>
							<div class="col-md-1"></div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default"
						onclick="closeEditDirectDeviceModal()">Close</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->

	<!--edit data modal-->
	<div class="modal fade" id="editSensorModal" tabindex="-1"
		role="dialog">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close"
						onclick="closeEditSensorModal()" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">Data Object</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal" role="form">
						<!--device ip-->
						<input id="sDeviceIP" type="hidden" class="form-control" value="">
						<!--group code-->
						<input id="sGroupCode" type="hidden" class="form-control" value="">
						<!--class code-->
						<input id="sClassCode" type="hidden" class="form-control" value="">
						<!--instance-->
						<input id="sInstanceCode" type="hidden" class="form-control"
							value="">
						<div class="form-group">
							<!--status-->
							<label class="col-md-4 control-label">Status:</label>
							<div class="col-md-4">
								<select id="sStatus" class="form-control">
									<option value="true">ON</option>
									<option value="false">OFF</option>
								</select>
							</div>
							<div class="col-md-2">
								<button type="button"
									onclick="setDeviceAttribute('sDeviceIP', 'sGroupCode', 'sClassCode', 'sInstanceCode', statusEPC, 'sStatus')"
									class="btn btn-default">Set</button>
							</div>
						</div>
						<!-- extend div -->
						<div id="extendDiv"></div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default"
						onclick="closeEditSensorModal()">Close</button>
				</div>
			</div>
		</div>
	</div>

</body>
</html>