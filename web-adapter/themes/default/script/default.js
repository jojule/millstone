// ==========================================================================
// ==========================================================================
//
// Variable handling functions
// 
// ==========================================================================


// --------------------------------------------------------------------------
// Get variable
//
// Params:
// id           ID the the INPUT tag
//
// Returns false iff no error occurred
// --------------------------------------------------------------------------

function getVarById(id) {
  e = document.getElementById(id);
  if (e) {
    return e.value; 
  }
  return false;
}


// --------------------------------------------------------------------------
// Set variable
//
// Params:
// id           ID the the INPUT tag
// value        New value of the variable
// immediate    Should the server side event be created immediately
//
// Returns false iff no error occurred
// --------------------------------------------------------------------------

function setVarById(id,value,immediate) {
  e = document.getElementById(id);
  if (e) {
    e.value = value;
    if (immediate) {
      document.millstone.submit();
    }
    return false; 
  }
  return true;
}


// --------------------------------------------------------------------------
// Check if integer list contains a number.
//
// Params:
// list         Comma separated list of integers
// number       Number to be tested
//
// Returns true iff the number can be found in the list
// --------------------------------------------------------------------------

function listContainsInt(list,number) {
  a = list.split(",");

  for (i=0;i<a.length;i++) {
    if (a[i] == number) return true;
  }
  
  return false;
}


// --------------------------------------------------------------------------
// Add number to integer list, if it does not exit before.
//
// Params:
// list         Comma separated list of integers
// number       Number to be added
//
// Return new list
// --------------------------------------------------------------------------

function listAddInt(list,number) {

  if (listContainsInt(list,number)) 
    return list;
    
  if (list == "") return number;
  else return list + "," + number;
}


// --------------------------------------------------------------------------
// Remove number from integer list.
//
// Params:
// list         Comma separated list of integers
// number       Number to be removed
//
// Return new list
// --------------------------------------------------------------------------

function listRemoveInt(list,number) {

  retval = "";
  a = list.split(',');

  for (i=0;i<a.length;i++) {
    if (a[i] != number) {
      if (i == 0) retval += a[i];
      else retval += "," + a[i];
    }
  }
  
  return retval;
}




// ==========================================================================
// ==========================================================================
//
// CSS class name handling functions
// 
// ==========================================================================



// --------------------------------------------------------------------------
// Get the unselected class name based on current class name.
// selected CSS class names are created by appending the string "-selected"
// to the base (unselected) class name. This function removes the
// selected and highlighted appendix from the class name.
//
// Params:
// currentName  Current class name 
//
// returns Class name without the selected appendix.
// --------------------------------------------------------------------------
function toUnselectedClassName(currentName) {
	if (currentName) {
		i = currentName.lastIndexOf("-selected");
		if (i>=0) {
			return currentName.substring(0,i);
		}
		i = currentName.lastIndexOf("-highlighted");
		if (i>=0) {
			return currentName.substring(0,i);
		}
	}
	return currentName;
}

// --------------------------------------------------------------------------
// Get the  selected class name based on current class name.
// selected CSS class names are created by appending the string "-selected"
// to the base (unselected) class name. This function appends the
// selected appendix from the class name.
//
// Params:
// currentName  Current class name 
//
// returns Class name with the selected appendix.
// --------------------------------------------------------------------------
function toSelectedClassName(currentName) {
	if (currentName) {
		i = currentName.lastIndexOf("-selected");
		if (i>=0) {
			return currentName;
		}		
	}
	return currentName+"-selected";
}

// --------------------------------------------------------------------------
// Get the  highlight class name based on current class name.
// selected CSS class names are created by appending the string "-highlight"
// to the base (unselected) class name. This function appends the
// highlight appendix from the class name.
//
// Params:
// currentName  Current class name 
//
// returns Class name with the highlight appendix.
// --------------------------------------------------------------------------
function toHighlightClassName(currentName) {
	if (currentName) {
		i = currentName.lastIndexOf("-highlighted");
		if (i>=0) {
			return currentName;
		}		
	}
	return currentName+"-highlighted";
}




// ==========================================================================
// ==========================================================================
//
// Window related functions
//
// ==========================================================================



// --------------------------------------------------------------------------
// Open new window with specified url
//
// Params:
// url:     URL to open in new window.
// name:    Name of the target (usually a window or frame). Empty window
//          name opens the result in this window.
// width:   Width of the target (usually a window).
// height:  Height of the target (usually a window).
// border:  Border decoration of the target (default|minimal|none)
// modal:   true if the target should be modal
//
// --------------------------------------------------------------------------

function openWindow(url,name,width,height,border) {
	var props = '';

	// Open the url in this window ?
	if (name == '') {
		name = window.name;
	}

	// Borders		
	if (border == 'minimal') {
	    props += 'toolbar=1,location=0,menubar=0,status=1,resizable=1,scrollbars=1';
	} else if (border == 'none') {
	    props += 'toolbar=0,location=0,menubar=0,status=0,resizable=1,scrollbars=1';	
	} else {
	    props += 'toolbar=1,location=1,menubar=1,status=1,resizable=1,scrollbars=1';
	}
	
	// Height and width
	if ((width > 0) && (height > 0)) {
		props += ',height='+height+',width='+width;
	}
	
	win = window.open(url,name,props);	
	
	win.focus();
}



// ==========================================================================
// ==========================================================================
//
// Element handling functions
//
// ==========================================================================


// --------------------------------------------------------------------------
// Disables a document element.
//
// Params:
// id:      ID of the element
//
// --------------------------------------------------------------------------
function disableElement(element) {
        if (element != null) {
            element.disabled = true;
        }
}

// --------------------------------------------------------------------------
// Enables a document element.
//
// Params:
// id:      ID of the element
//
// --------------------------------------------------------------------------
function enableElement(element) {
        if (element != null) {
			element.disabled = false;        
        }
}

// --------------------------------------------------------------------------
// Hides a document element.
//
// Params:
// id:      ID of the element
//
// --------------------------------------------------------------------------
function hideElement(element) {
        if (element != null) {
            element.style.display="none";
        }
}

// --------------------------------------------------------------------------
// Shows a document element.
//
// Params:
// id:      ID of the element
//
// --------------------------------------------------------------------------
function showElement(element) {
        if (element != null) {
			element.style.display="";        
        }
}

// --------------------------------------------------------------------------
// Check if a document element is visible.
//
// Params:
// id:      ID of the element
//
// --------------------------------------------------------------------------
function isElementVisible(element) {
        if (element != null) {
			return (element.style.display != "none");        
        }
        return false;
}

// --------------------------------------------------------------------------
// Hides or shows a document element depending it current state.
//
// Params:
// id:      ID of the element
//
// --------------------------------------------------------------------------
function toggleElement(element) {
        if (element != null) {
        	if (isElementVisible(element)) {
				hideElement(element);
			} else {
				showElement(element);
			}
        }
}

// --------------------------------------------------------------------------
// Hides a document element by id.
//
// Params:
// id:      ID of the element
//
// --------------------------------------------------------------------------
function hideElementById(id) {
    	hideElement(document.getElementById(id));
}

// --------------------------------------------------------------------------
// Shows an document element by id.
//
// Params:
// id:      ID of the element
//
// --------------------------------------------------------------------------
function showElementById(id) {
    	showElement(document.getElementById(id));
}

// --------------------------------------------------------------------------
// Hides or shows an document element depending its current state.
//
// Params:
// id:      ID of the element
//
// --------------------------------------------------------------------------
function toggleElementById(id) {
    	toggleElement(document.getElementById(id));
}


// --------------------------------------------------------------------------
// Show element properties.
//
// Params:
// el   Element to be inspected.
//
// returns:
// --------------------------------------------------------------------------
function printElementProperties(el) 
{ 
    var result = "" 
    for (var p in el) 
        result += "<B>" + p + "</B> = " + el[p] + "<BR>"; 
  
	var w = window.open(); 
	w.document.write(result); 
	w.document.close(); 
} 





// ==========================================================================
// ==========================================================================
//
// Action related functions
//
// ==========================================================================


// --------------------------------------------------------------------------
// Opens action popup.
//
// Params:
//
// --------------------------------------------------------------------------
function actionPopup(event,actionListId,itemKey,activeActions) {

	document.getElementById(actionListId+"_ACTIVE_ITEM").value = itemKey;
	var popup = document.getElementById(actionListId + '_POPUP');

	// Disable all actions
	for (i=0; i<popup.childNodes.length;i++) {
		if (popup.childNodes[i].style && popup.childNodes[i].className == 'action-item') {
			disableElement(popup.childNodes[i]);
		} 
	}
	
	// Enable all actions, 
	var acts = activeActions.split(',');
	for (a in acts) {
		var s = actionListId + "_" + acts[a];
		var action = document.getElementById(s);
		if (action && action.style) {
			enableElement(action);
		}
	}

	showPopupById(actionListId+"_POPUP",event.clientX,event.clientY);
	
	window.event.returnValue = false;		
}

// --------------------------------------------------------------------------
// Fires action on current item in given popup.
//
// Params:
//
// return:
// --------------------------------------------------------------------------
function fireAction(actionListId,actionVariableId,actionKey) {
	hidePopupById(actionListId+"_POPUP");
	actionVariable = document.getElementById(actionVariableId);
	currentItem = document.getElementById(actionListId+"_ACTIVE_ITEM");
	if (currentItem && actionVariable) {
		actionVariable.value = currentItem.value+","+actionKey;
		void(document.millstone.submit());	
	}
}






// ==========================================================================
// ==========================================================================
//
// Pop-up related functions
//
// ==========================================================================


// Globals for handling browser differences
var ie5 = document.all && document.getElementById;
var ns6 = document.getElementById && !document.all;

// Global array of active popups
var activePopups = new Array();
var popupCount = 0;
var skipNextHideAll = false;

// Hide popups on untargeted clicks 
if (ie5 || ns6) {
	document.onclick=hideAllPopups;
}


// --------------------------------------------------------------------------
// Shows popup on location specified by given coordinates.
//
// Params:
// --------------------------------------------------------------------------
function showPopupById(popupId, clientX, clientY) {

	hideAllPopups();

	popup = document.getElementById(popupId);
	activePopups[popupCount++] = popupId;
	
	// Find out how close the mouse is to the corner of the window
	var rightedge = ie5? document.body.clientWidth-clientX : window.innerWidth-clientX;
	var bottomedge = ie5? document.body.clientHeight-clientY : window.innerHeight-clientY;

	// If the horizontal distance isn't enough to accomodate the width of the context menu
	if (rightedge<popup.offsetWidth) {
		// Move the horizontal position of the menu to the left by it's width
		popup.style.left=ie5? 
			document.body.scrollLeft+clientX-popup.offsetWidth : 
			window.pageXOffset+clientX-popup.offsetWidth;
	} else {
		//position the horizontal position of the menu where the mouse was clicked
		popup.style.left=ie5? document.body.scrollLeft+clientX : window.pageXOffset+clientX;
	}
	
	// Same concept with the vertical position
	if (bottomedge<popup.offsetHeight) {
		popup.style.top=ie5? 
			document.body.scrollTop+clientY-popup.offsetHeight : 
			window.pageYOffset+clientY-popup.offsetHeight;
	} else {
		popup.style.top=ie5? document.body.scrollTop+clientY : window.pageYOffset+clientY;
	}
	
	showElement(popup);

	// Assure that this popup is not hidden	
	skipNextHideAll = true;
}

// --------------------------------------------------------------------------
// Hide popup by id. 
//
// Params:
// id   ID of the popup to hide.
//
// returns:
// --------------------------------------------------------------------------
function hidePopupById(id) {
		popup = document.getElementById(id);
		if (popup) {
			hideElement(popup);
		}		
}


// --------------------------------------------------------------------------
// Hide all visible popups
//
// Params:
//
// returns:
// --------------------------------------------------------------------------
function hideAllPopups() {

	// Do not immediately remove the popup
	if (skipNextHideAll) {
		skipNextHideAll = false;
		return true;
	}

	for (id in activePopups) {
		hidePopupById(activePopups[id]);
	}		
	popupCount = 0;

	return true;
}





// ==========================================================================
// ==========================================================================
//
// Component related functions
// 
// ==========================================================================


// --------------------------------------------------------------------------
// Toggle checkbox state
//
// Params:
// id           Id the the INPUT tag (TYPE="CHECKBOX")
//
// Returns false iff no error occurred
// --------------------------------------------------------------------------

function toggleCheckbox(id,immediate) {
  
  e = document.getElementById(id);
  if (e) {
	e.checked = (e.checked ? false : true);

	if (immediate) document.millstone.submit();
	
	return false;
  }
  
  return true;
}


// --------------------------------------------------------------------------
// Input controlling table row selection changes state.
//
// Params:
// inputid      Id of the INPUT containing the selection state
// key          Key identifying item connected to this table row
// unselected   Class of the unselected row
// selected     Class of the selected row
// immediate    "true" iff the table is in immediate mode
// mode         "single" or "multi"
// --------------------------------------------------------------------------

function tableSelClick(inputid,key,immediate,mode) {

  prev = value = getVarById(inputid);
  
  // Change selection value
  if (mode == "multi") {
    if (listContainsInt(value,key)) 
      value = listRemoveInt(value,key);
    else 
      value = listAddInt(value,key);
  } else 
    value = key;
  setVarById(inputid,value);
  
  // In single select mode change remove old selections
  if (mode == "single") {
    keys = prev.split(",");
    for (i=0;i<keys.length;i++) {
      tr = document.getElementById("" + inputid + "_" + keys[i]);
      if (tr) 
        tr.className = toUnselectedClassName(tr.className);
    }
  }
   
  // Update visual state of the current item
  tr = document.getElementById("" + inputid + "_" + key);
  if (tr) {
    if (listContainsInt(value,key))
      tr.className = toSelectedClassName(tr.className); 
    else 
      tr.className = toUnselectedClassName(tr.className);
  }

  // Submit if in immediate mode  
  if (immediate) 
    document.millstone.submit();

}

// --------------------------------------------------------------------------
// Input controlling tree item selection changes state.
//
// Params:
// inputid      Id of the INPUT containing the selection state
// key          Key identifying item
// immediate    "true" iff the table is in immediate mode
// mode         "single" or "multi"
// --------------------------------------------------------------------------

function treeSelClick(inputid,key,immediate,mode) {

  prev = value = getVarById(inputid);

  // Change selection value
  if (mode == "multi") {
    if (listContainsInt(value,key)) 
      value = listRemoveInt(value,key);
    else 
      value = listAddInt(value,key);
  } else 
    value = key;
  setVarById(inputid,value);
  
  // In single select mode change remove old selections
  if (mode == "single") {
    keys = prev.split(",");
    for (i=0;i<keys.length;i++) {
      itemElement = document.getElementById("" + inputid + "_" + keys[i]);
      if (itemElement)
        itemElement.className = toUnselectedClassName(itemElement.className);
    }
  }
   
  // Update visual state of the current item
  itemElement = document.getElementById("" + inputid + "_" + key);
  if (itemElement) {
    if (listContainsInt(value,key))
      itemElement.className = toSelectedClassName(itemElement.className);
    else 
      itemElement.className = toUnselectedClassName(itemElement.className);
  }

  // Submit if in immediate mode  
  if (immediate) 
    document.millstone.submit();

}

// --------------------------------------------------------------------------
// Input controlling tree item expansion/collapsion changes state.
//
// Params:
// expandid     Id of the INPUT containing the expansion state
// collapseid   Id of the INPUT containing the collapsion state
// key          Key identifying item
// immediate    "true" iff the table is in immediate mode
// --------------------------------------------------------------------------

function treeExpClick(expandid,collapseid,key,immediate) {

  // Fetch the current variable values
  var expanded = getVarById(expandid);
  var collapsed = getVarById(collapseid);

  // Fetch the "template" images containing collapsed 
  // and expanded images and get the current items image
  var expandedImg = document.getElementById(collapseid+"_IMG");
  var collapsedImg = document.getElementById(expandid+"_IMG");
  var img = document.getElementById("img" + expandid + "_" + key);  

  // Fetch the child element node
  var childElement = document.getElementById("" + expandid + "_" + key);


  // Determine which state the item is now
  isCollapsed = (!childElement) || (!isElementVisible(childElement));

  // Expand the item
  if (isCollapsed) {
    // Remove from collapsion list
    if (listContainsInt(collapsed,key)) {
      collapsed = listRemoveInt(collapsed,key);
    }
    setVarById(collapseid,collapsed);

    // Add to expanded list
    if (!listContainsInt(expanded,key)) {
       expanded = listAddInt(expanded,key);
    }  
    setVarById(expandid,expanded);


  // Collapse the item
  } else {
    // Remove from expansion list
    if (listContainsInt(expanded,key)) {
      expanded = listRemoveInt(expanded,key);
    }
    setVarById(expandid,expanded);

    // Add to collapsion list
    if (!listContainsInt(collapsed,key)) {
       collapsed = listAddInt(collapsed,key);
    }  
    setVarById(collapseid,collapsed);
  }
  
  
  // Update visual state of the current item  
  if (childElement && img) {
    if (listContainsInt(expanded,key)) {
      showElement(childElement);
      if (expandedImg) {
        img.src = expandedImg.src;      
      }
    } else {
      hideElement(childElement);
      if (collapsedImg) {
        img.src = collapsedImg.src;      
      }
    }
  } else {
      // Fetch the missing items
      document.millstone.submit();
  }
}


// ==========================================================================
// ==========================================================================
//
// Calendar related functions
// 
// ==========================================================================



// --------------------------------------------------------------------------
// Update contents of calendar to reflect the given year and month.
//
// Params:
//
// Return:
// --------------------------------------------------------------------------
function getCalendarMonth(year, month, weekBegin) {

	var cal = new Array();
	cal[0] = new Array(7);
	cal[1] = new Array(7);
	cal[2] = new Array(7);
	cal[3] = new Array(7);
	cal[4] = new Array(7);
	cal[5] = new Array(7);
	
	var daysInMonth = getDaysInMonth(year,month);
	var calDate = new Date(year, month, 1);
	var weekDayOfFirst = (calDate.getDay()-weekBegin+7)%7;	
	var vardate = 1;
	var i,day,week;
	
	for (day = weekDayOfFirst; day < 7; day++) {
		cal[0][day] = vardate;
		vardate++;
	}
	
	for (week = 1; week < 6; week++) {
		for (day = 0; day < 7; day++) {
			if (vardate <= daysInMonth) {
				cal[week][day] = vardate;
				vardate++;
      		}
      		else cal[week][day] = "";
   		}
	}	
	return cal;
}


function getDaysInMonth(year, month) {
	var lastDate = new Date(year, 1+month, 0);
	return lastDate.getDate();
}


// --------------------------------------------------------------------------
// Update contents of calendar to reflect the current selection.
//
// Params:
//
// Return:
// --------------------------------------------------------------------------
function updateCalendar(calendarId, yearId, monthId, dayId, weekBegin, immediate) {

  if (calendarId != '') {;

	var iWeekBegin = parseInt(weekBegin); 	
	var iYear = parseInt(getVarById(yearId)); 	
	var iMonth = parseInt(getVarById(monthId))-1; 
	var iDay = parseInt(getVarById(dayId));
	
	// Round the date to last available in month
   	if (iDay > getDaysInMonth(iYear,iMonth)) {
   		iDay = getDaysInMonth(iYear,iMonth);
   		setVarById(dayId,iDay);
   	}
	
	// Get the calendar for month
	cal = getCalendarMonth(iYear, iMonth, iWeekBegin);
	
	for (week = 0; week < 6; week++) {
		for (day = 0; day < 7; day++) {	        			
			if ((cal[week][day]) && !isNaN(cal[week][day])) {				
			    el = document.getElementById(calendarId+"_"+week+"_"+day);
			    if (el) {
				   el.innerText = cal[week][day];
				   el.innerHTML = cal[week][day];
				   el.className = "cal-day";				   
				}
			} else {
			    el = document.getElementById(calendarId+"_"+week+"_"+day);
			    if (el) {
				   el.innerText = "";
				   el.innerHTML = "";
				   el.className = "";
				}
         	}
      	}
   	}
   	
   	// Visually select the day   	
	calendarDaySelect(calendarId, iDay)   	
	
  }

  // Submit if in immediate mode  
  if (immediate && immediate == "true") 
    document.millstone.submit();
	
}


// --------------------------------------------------------------------------
// Visually unselect all days.
//
// Params:
//
// Return:
// --------------------------------------------------------------------------
function calendarUnselectAll(calendarId) {

	for (week = 0; week < 6; week++) {
		for (day = 0; day < 7; day++) {
		    el = document.getElementById(calendarId+"_"+week+"_"+day);
		    if (el) {
			   el.className = toUnselectedClassName(el.className);				   
			} else {
			    el = document.getElementById(calendarId+"_"+week+"_"+day);
			    if (el) {
				   el.className = "";
				}
         	}
      	}
   	}
}



// --------------------------------------------------------------------------
// Visually select the current day
//
// Params:
//
// Return:
// --------------------------------------------------------------------------
function calendarDaySelect(calendarId, dayNumber) {
 
	// Visually select the current day
	for (week = 0; week < 6; week++) {
		for (day = 0; day < 7; day++) {
		    var el = document.getElementById(calendarId+"_"+week+"_"+day);
		    if (el) {
		       var iDay = parseInt(el.innerText);		       
			   if (!isNaN(iDay) && (iDay == dayNumber)) {				      
				   el.className = toSelectedClassName(el.className);
				   return;				   			   		
			   }
			}
      	}
   	}
}

// --------------------------------------------------------------------------
// Handle calendar selection click.
// 
// Updates the visual selection and the day variable.
//
// Params:
//
// Return:
// --------------------------------------------------------------------------
function calSel(calendarId, dayVariableId, dayElementId, immediate) {
 
	// Visually unselect all
	calendarUnselectAll(calendarId);

	// Select the current one
	var dayElement = document.getElementById(dayElementId);
	if (dayElement && (dayElement.innerText || dayElement.innerHTML)) {
		setVarById(dayVariableId,dayElement.innerText);
		dayElement.className= toSelectedClassName(dayElement.className);
	}	
	
  // Submit if in immediate mode  
  if (immediate) 
    document.millstone.submit();
}
 