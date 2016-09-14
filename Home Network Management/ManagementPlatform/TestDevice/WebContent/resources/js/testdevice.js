/**
 * Open EditDataModal
 * 
 * @param dataModal
 *            device object data in json format
 */
function openEditSensorModal(dataModal) {
	// deviceIP
	if (dataModal.deviceIP != null)
		$("#sDeviceIP").val(dataModal.deviceIP);
	// groupCode
	if (dataModal.groupCode != null)
		$("#sGroupCode").val(dataModal.groupCode);
	// classCode
	if (dataModal.classCode != null)
		$("#sClassCode").val(dataModal.classCode);
	// instanceCode
	if (dataModal.instanceCode != null)
		$("#sInstanceCode").val(dataModal.instanceCode);

	// instance
	if (dataModal.operationStatus != null)
		document.getElementById("sStatus").value = dataModal.operationStatus;
	// extended attributes
	if (dataModal.listAttributes != null) {
		var html = '';
		for (var i = 0; i < dataModal.listAttributes.length; i++) {
			var attObj = dataModal.listAttributes[i];
			if (attObj != null) {
				var inputId = attObj.attributeName.toLowerCase() + "value";
				html += "<div class='form-group'>";

				html += "<label class='col-md-4 control-label'>"
						+ attObj.attributeName + ":</label>";

				html += "<div class='col-md-4'>";
				html += "<input id='" + inputId
						+ "' class='form-control' value='" + attObj.value
						+ "'";
				if (attObj.changeable == false)
					html += " disabled ";
				html += ">";
				html += "</div>";

				html += "<div class='col-md-2'>";
				if (attObj.changeable == true) {
					html += "<button type='button' onclick='setDeviceAttribute('sDeviceIP', 'sGroupCode', 'sClassCode', 'sInstanceCode', '"
							+ attObj.epc
							+ "', '"
							+ inputId
							+ "')' class='btn btn-default'>Set</button>";
				}
				html += "</div>";

				html += "</div>";
			}
		}
		// append to div
		$("#extendDiv").html(html);
	}

	var html = "<div class='col-md-4'><input class='form-control' value='123'></div><div class='col-md-2'></div></div>";

	// show modal
	$("#editSensorModal").modal("show");
}

/**
 * Open EditModal
 * 
 * @param deviceData
 *            device data in json format
 */
function openEditModal(deviceData) {
	// groupCode
	if (deviceData.profile.groupCode != null)
		$("#eGroupCode").val(deviceData.profile.groupCode);
	// classCode
	if (deviceData.profile.classCode != null)
		$("#eClassCode").val(deviceData.profile.classCode);
	// instanceCode
	if (deviceData.profile.instanceCode != null)
		$("#eInstanceCode").val(deviceData.profile.instanceCode);
	// ip
	if (deviceData.profile.deviceIP != null)
		$("#eDeviceIP").val(deviceData.profile.deviceIP);
	// name
	if (deviceData.profile.deviceName != null)
		$("#eDeviceName").val(deviceData.profile.deviceName);
	// status
	if (deviceData.profile.operationStatus != null)
		document.getElementById("eStatus").value = deviceData.profile.operationStatus;

	// location
	if (deviceData.profile.installLocation != null)
		$("#eInstallLocation").val(deviceData.profile.installLocation);

	// version
	if (deviceData.profile.standardVersionInfo != null)
		$("#eStandardVersionInfo").val(deviceData.profile.standardVersionInfo);

	// identification number
	if (deviceData.profile.identificationNumber != null)
		$("#eIdentificationNumber")
				.val(deviceData.profile.identificationNumber);

	// instantaneousPower
	if (deviceData.profile.instantaneousPower != null)
		$("#eInstantaneousPower").val(deviceData.profile.instantaneousPower);

	// cumulativePower
	if (deviceData.profile.cumulativePower != null)
		$("#eCumulativePower").val(deviceData.profile.cumulativePower);

	// manufactureerFaultCode
	if (deviceData.profile.manufactureerFaultCode != null)
		$("#eManufactureerFaultCode").val(
				deviceData.profile.manufactureerFaultCode);

	// currentLimitSetting
	if (deviceData.profile.currentLimitSetting != null)
		$("#eCurrentLimitSetting").val(deviceData.profile.currentLimitSetting);

	// faultDescription
	if (deviceData.profile.faultStatus != null
			&& deviceData.profile.faultDescription != null) {
		if (deviceData.profile.faultStatus == true) {
			$("#eFaultDescription").val(deviceData.profile.faultDescription);
		}
	}

	// manufacturerCode
	if (deviceData.profile.manufacturerCode != null)
		$("#eManufacturerCode").val(deviceData.profile.manufacturerCode);

	// businessFacilityCode
	if (deviceData.profile.businessFacilityCode != null)
		$("#eBusinessFacilityCode")
				.val(deviceData.profile.businessFacilityCode);

	// productCode
	if (deviceData.profile.productCode != null)
		$("#eProductCode").val(deviceData.profile.productCode);

	// productNumber
	if (deviceData.profile.productNumber != null)
		$("#eProductNumber").val(deviceData.profile.productNumber);

	// productDate
	if (deviceData.profile.productDate != null)
		$("#eProductDate").val(deviceData.profile.productDate);

	// powerSaving
	if (deviceData.profile.powerSaving != null)
		document.getElementById("ePowerSaving").value = deviceData.profile.powerSaving;

	// throughPublicNetwork
	if (deviceData.profile.throughPublicNetwork != null)
		document.getElementById("eThroughPublicNetwork").value = deviceData.profile.throughPublicNetwork;

	// currentTimeSetting
	if (deviceData.profile.currentTimeSetting != null)
		$("#eCurrentTimeSetting").val(deviceData.profile.currentTimeSetting);

	// currentDateSetting
	if (deviceData.profile.currentDateSetting != null) {
		var date = new Date(deviceData.profile.currentDateSetting);
		if (date != null) {
			var strDate = date.getFullYear() + ":" + (date.getMonth() + 1)
					+ ":" + date.getDate();
			$("#eCurrentDateSetting").val(strDate);
		}
	}

	// powerLimit
	if (deviceData.profile.powerLimit != null)
		$("#ePowerLimit").val(deviceData.profile.powerLimit);

	// cumulativeTime
	if (deviceData.profile.cumulativeTime != null)
		$("#eCumulativeTime").val(deviceData.profile.cumulativeTime);

	// show modal
	$("#editModal").modal("show");
}

/**
 * Open EditDirectDeviceModal
 * 
 * @param deviceData
 *            device data in json format
 */
function openEditDirectDeviceModal(deviceData) {
	// ip
	if (deviceData.profile.deviceIP != null)
		$("#dDeviceIP").val(deviceData.profile.deviceIP);
	// name
	if (deviceData.profile.deviceName != null)
		$("#dDeviceName").val(deviceData.profile.deviceName);
	// status
	if (deviceData.profile.operationStatus != null)
		document.getElementById("dStatus").value = deviceData.profile.operationStatus;

	// location
	if (deviceData.profile.installLocation != null)
		$("#dInstallLocation").val(deviceData.profile.installLocation);

	// version
	if (deviceData.profile.standardVersionInfo != null)
		$("#dStandardVersionInfo").val(deviceData.profile.standardVersionInfo);

	// identification number
	if (deviceData.profile.identificationNumber != null)
		$("#dIdentificationNumber")
				.val(deviceData.profile.identificationNumber);

	// instantaneousPower
	if (deviceData.profile.instantaneousPower != null)
		$("#dInstantaneousPower").val(deviceData.profile.instantaneousPower);

	// cumulativePower
	if (deviceData.profile.cumulativePower != null)
		$("#dCumulativePower").val(deviceData.profile.cumulativePower);

	// manufactureerFaultCode
	if (deviceData.profile.manufactureerFaultCode != null)
		$("#dManufactureerFaultCode").val(
				deviceData.profile.manufactureerFaultCode);

	// currentLimitSetting
	if (deviceData.profile.currentLimitSetting != null)
		$("#dCurrentLimitSetting").val(deviceData.profile.currentLimitSetting);

	// faultDescription
	if (deviceData.profile.faultStatus != null
			&& deviceData.profile.faultDescription != null) {
		if (deviceData.profile.faultStatus == true) {
			$("#dFaultDescription").val(deviceData.profile.faultDescription);
		}
	}

	// manufacturerCode
	if (deviceData.profile.manufacturerCode != null)
		$("#dManufacturerCode").val(deviceData.profile.manufacturerCode);

	// businessFacilityCode
	if (deviceData.profile.businessFacilityCode != null)
		$("#dBusinessFacilityCode")
				.val(deviceData.profile.businessFacilityCode);

	// productCode
	if (deviceData.profile.productCode != null)
		$("#dProductCode").val(deviceData.profile.productCode);

	// productNumber
	if (deviceData.profile.productNumber != null)
		$("#dProductNumber").val(deviceData.profile.productNumber);

	// productDate
	if (deviceData.profile.productDate != null)
		$("#dProductDate").val(deviceData.profile.productDate);

	// powerSaving
	if (deviceData.profile.powerSaving != null)
		document.getElementById("dPowerSaving").value = deviceData.profile.powerSaving;

	// throughPublicNetwork
	if (deviceData.profile.throughPublicNetwork != null)
		document.getElementById("dThroughPublicNetwork").value = deviceData.profile.throughPublicNetwork;

	// currentTimeSetting
	if (deviceData.profile.currentTimeSetting != null)
		$("#dCurrentTimeSetting").val(deviceData.profile.currentTimeSetting);

	// currentDateSetting
	if (deviceData.profile.currentDateSetting != null) {
		var date = new Date(deviceData.profile.currentDateSetting);
		if (date != null) {
			var strDate = date.getFullYear() + ":" + (date.getMonth() + 1)
					+ ":" + date.getDate();
			$("#dCurrentDateSetting").val(strDate);
		}
	}

	// powerLimit
	if (deviceData.profile.powerLimit != null)
		$("#dPowerLimit").val(deviceData.profile.powerLimit);

	// cumulativeTime
	if (deviceData.profile.cumulativeTime != null)
		$("#dCumulativeTime").val(deviceData.profile.cumulativeTime);

	// show modal
	$("#editDirectDeviceModal").modal("show");
}

/**
 * Close EditDataModal
 */
function closeEditSensorModal() {
	$("#sDeviceIP").val("");
	$("#sGroupCode").val("");
	$("#sClassCode").val("");
	$("#sInstanceCode").val("");
	$("#extendDiv").html("");

	// hide modal
	$("#editSensorModal").modal("hide");
}

/**
 * Close EditModal
 */
function closeEditModal() {
	$("#eGroupCode").val("");
	$("#eClassCode").val("");
	$("#eInstanceCode").val("");
	$("#eDeviceIP").val("");
	$("#eDeviceName").val("");
	$("#eStandardVersionInfo").val("");
	$("#eIdentificationNumber").val("");
	$("#eInstantaneousPower").val("");
	$("#eCumulativePower").val("");
	$("#eManufactureerFaultCode").val("");
	$("#eCurrentLimitSetting").val("");
	$("#eFaultDescription").val("");
	$("#eManufacturerCode").val("");
	$("#eBusinessFacilityCode").val("");
	$("#eProductCode").val("");
	$("#eProductNumber").val("");
	$("#eProductDate").val("");
	$("#eCurrentTimeSetting").val("");
	$("#eCurrentDateSetting").val("");
	$("#ePowerLimit").val("");
	$("#eCumulativeTime").val("");

	// hide modal
	$("#editModal").modal("hide");
}

/**
 * Close EditDirectDeviceModal
 */
function closeEditDirectDeviceModal() {
	$("#dGroupCode").val("");
	$("#dClassCode").val("");
	$("#dInstanceCode").val("");
	$("#dDeviceIP").val("");
	$("#dDeviceName").val("");
	$("#dStandardVersionInfo").val("");
	$("#dIdentificationNumber").val("");
	$("#dInstantaneousPower").val("");
	$("#dCumulativePower").val("");
	$("#dManufactureerFaultCode").val("");
	$("#dCurrentLimitSetting").val("");
	$("#dFaultDescription").val("");
	$("#dManufacturerCode").val("");
	$("#dBusinessFacilityCode").val("");
	$("#dProductCode").val("");
	$("#dProductNumber").val("");
	$("#dProductDate").val("");
	$("#dCurrentTimeSetting").val("");
	$("#dCurrentDateSetting").val("");
	$("#dPowerLimit").val("");
	$("#dCumulativeTime").val("");

	// hide modal
	$("#editDirectDeviceModal").modal("hide");
}


/**
 * Load EditModal
 * 
 * @param deviceIP
 *            device's IP
 */
function loadEditModal(deviceIP) {
	if (deviceIP == null)
		alert("DeviceIP's is null!");
	else {
		$.ajax({
			url : "/TestDevice/device",
			type : "POST",
			data : {
				deviceIP : deviceIP
			},
			datatype : "JSON",
			success : function(deviceData) {
				if (deviceData != null)
					openEditModal(deviceData);
				else
					alert("Device data is null.");
			},
			error : function(result) {
				alert("Get device data failed!");
			}
		});
	}
}

/**
 * Load EditDirectDeviceModal
 * 
 * @param deviceIP
 *            device's IP
 */
function loadEditDirectDeviceModal(deviceIP) {
	if (deviceIP == null)
		alert("DeviceIP's is null!");
	else {
		$.ajax({
			url : "/TestDevice/device",
			type : "POST",
			data : {
				deviceIP : deviceIP
			},
			datatype : "JSON",
			success : function(deviceData) {
				if (deviceData != null)
					openEditDirectDeviceModal(deviceData);
				else
					alert("Device data is null.");
			},
			error : function(result) {
				alert("Get device data failed!");
			}
		});
	}
}

/**
 * Load EditDataModal
 */
function loadEditSensorModal(deviceIP, groupCode, classCode, instanceCode) {
	if (deviceIP == null) {
		alert("deviceIP is null");
		return;
	}
	if (groupCode == null) {
		alert("groupCode is null");
		return;
	}
	if (classCode == null) {
		alert("classCode is null");
		return;
	}
	if (instanceCode == null) {
		alert("instance is null");
		return;
	}

	$.ajax({
		url : "/TestDevice/sensor",
		type : "POST",
		data : {
			deviceIP : deviceIP,
			groupCode : groupCode,
			classCode : classCode,
			instanceCode : instanceCode
		},
		datatype : "JSON",
		success : function(result) {
			if (result.success != null && result.success == false)
				alert(result.message);
			else
				openEditSensorModal(result);
		},
		error : function(result) {
			alert("Get data object failed!");
		}
	});
}

/**
 * Set device attribute, use ajax call send a request to server to update
 * device's attribute
 * 
 * @param elmIP
 *            element ip
 * @param elmGroupCode
 *            element groupcode
 * @param emlClassCode
 *            element classcode
 * @param emlInstance
 *            element instancecode
 * @param epc
 *            epc
 * @param elmValue
 *            element value
 */
function setDeviceAttribute(elmIP, elmGroupCode, emlClassCode, emlInstance,
		epc, elmValue) {
	var deviceIP = document.getElementById(elmIP).value;
	var groupCode = document.getElementById(elmGroupCode).value;
	var classCode = document.getElementById(emlClassCode).value;
	var instanceCode = document.getElementById(emlInstance).value;
	var value = document.getElementById(elmValue).value;

	if (deviceIP == null) {
		alert("device IP is null");
		return;
	}
	if (groupCode == null) {
		alert("GroupCode is null");
		return;
	}
	if (classCode == null) {
		alert("ClassCode is null");
		return;
	}
	if (value == null) {
		alert("attribute value is null");
		return;
	}
	if (epc == null) {
		alert("attribute EPC is null");
		return;
	}

	$.ajax({
		url : "/TestDevice/attribute",
		type : "POST",
		data : {
			deviceIP : deviceIP,
			groupCode : groupCode,
			classCode : classCode,
			instanceCode : instanceCode,
			epc : epc,
			value : value
		},
		datatype : "JSON",
		success : function(result) {
			if (result.success != null && result.success == false)
				alert(result.message);
			else
				alert(result);
		},
		error : function(result) {
			alert("Call ajax failed.");
		}
	});
}

/**
 * Set direct device attribute, use ajax call send a request to server to update
 * 
 * @param elmIP
 *            element ip
 * @param attibuteName
 *            element attribute name
 * @param elmValue
 *            element attribute value
 */
function setDirectDeviceAttribute(elmIP, attributeName, elmValue) {
	var ip = document.getElementById(elmIP).value;
	var value = document.getElementById(elmValue).value;

	if (ip == null) {
		alert("device IP is null");
		return;
	}
	if (attributeName == null) {
		alert("attributeName name is null");
		return;
	}
	if (value == null) {
		alert("attribute value is null");
		return;
	}

	$.ajax({
		url : "/TestDevice/directattribute",
		type : "POST",
		data : {
			ip : ip,
			attributeName : attributeName,
			value : value
		},
		datatype : "JSON",
		success : function(result) {
			if (result.success != null && result.success == false)
				alert(result.message);
			else
				alert(result);
		},
		error : function(result) {
			alert("Call ajax failed.");
		}
	});
}

/**
 * send request delete all device in database
 */
function deleteDevices() {
	$.ajax({
		url : "/TestDevice/devices",
		type : "DELETE",
		success : function(result) {
			if (result.success != null && result.success == false)
				alert(result.message);
			else {
				alert(result);
				location.reload();
			}
		},
		error : function(result) {
			alert("Call ajax failed.");
		}
	});
}

/**
 * refresh page
 */
function refresh() {
	location.reload();
}