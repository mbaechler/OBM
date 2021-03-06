/* ***** BEGIN LICENSE BLOCK *****
 *   Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is OBM MAJA.
 *
 * The Initial Developer of the Original Code is
 * Nicolas Lascombes.
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 * 
 * ***** END LICENSE BLOCK ***** */

/**
 *  Useful documentation :
 *    - https://developer.mozilla.org/en/XUL/toolbar
 *    - https://developer.mozilla.org/en/XUL/Attribute/currentset
 *    - https://developer.mozilla.org/en/XUL/toolbar#p-currentSet
 *    - profile/localstore.rdf
 *    - firefox_source_3.xxx/mozilla/browser/components/bookmarks/content/addBookmark2.js
 *    - https://developer.mozilla.org/en/DOM/element.removeChild
 *    - https://developer.mozilla.org/en/DOM/element.firstChild
 *    - https://developer.mozilla.org/en/Working_with_windows_in_chrome_code#Example_3.3a_Using_nsIWindowMediator_when_opener_is_not_enough
 *    - https://developer.mozilla.org/en/XUL/Attribute/persist
 *
 *  Useful tools :
 *    - DOM Inspector : https://addons.mozilla.org/fr/firefox/addon/1806
 */

var Toolbar = function() {};

// Toolbar DOM Tree Notes
// 
// to get Toolbar from DOM Tree, it must exist to access it
// so the best way to be sure that it loaded is to use a "window.addEventListener("load", )" function


/**
 * @param String aToolbarButtonNames : button IDS to add separated by commas
 * @param String aButtonBeforeDomId : button ID before which insert
 * @param Struct aOptions
 */
Toolbar.addButtons = function Toolbar_addButtons(aToolbarButtonNames, aButtonBeforeDomId, aOptions) {
  var constParameters = this.getConstParameters();

  this.eachWindows (function (w) {
    var toolbar = w.document.getElementById(constParameters.toolbarID);
    if ( toolbar ) {
      var buttonBefore = null;
      if (aButtonBeforeDomId) {
        try {
          buttonBefore = w.document.getElementById(aButtonBeforeDomId);
        } catch(e) {
          //not found : will insert at end of bar
        }
      }
      
      var addItems = aToolbarButtonNames.split(",");
      
      toolbar.insertItem("separator", buttonBefore, null, false);
      for ( var i = 0; i < addItems.length; i++ ) {
        toolbar.insertItem(addItems[i], buttonBefore, null, false);
      }
      
      // update from DOM modifications to currentset string attribute
      // currentSet != currentset (see : https://developer.mozilla.org/en/XUL/toolbar)
      toolbar.setAttribute("currentset", toolbar.currentSet);
      
      if (aOptions && aOptions.iconsize)
          toolbar.setAttribute("iconsize", aOptions.iconsize);
    }
    
    return null;
  });
}

/**
 * @argument String newToolbar : (optional) save given toolbar settings string
 *                                          or use current toolbar setting
 * @param Struct aOptions
 */
Toolbar.save = function Toolbar_save (newToolbar, aOptions) {
  var constParameters = this.getConstParameters();
  
  if (!newToolbar) {
    var windowManager = Components.classes['@mozilla.org/appshell/window-mediator;1']
                                    .getService(Components.interfaces.nsIWindowMediator);
    var toolBarWindow = windowManager.getMostRecentWindow(null);
   
    if (toolBarWindow) {
      var toolbar = toolBarWindow.document.getElementById(constParameters.toolbarID);
      if (toolbar) {
      	newToolbar = toolbar.currentSet;
      } else {
        Logger.logToFile("no toolbar"+"\n");
      	return false;
      }
    } else {
      Logger.logToFile("no window"+"\n");
      return false;
    }
  }
  
  // There are 2 methods to save Toolbar
  //  - access DOM window, modify toolbar and save it with document.persist method
  //       the problem is that the window need to be opened to modify toolbar
  //  - directly modify the RDF Configuration File
  //       the problem is the currently displayed toolbar window are not refreshed
  //
  // the save functionality means to only change RDF data so we choose to use the second method
  
  // --------------------------------------------------------------------------
  // RDF Resource File
  //
  // solution found by crawling into mozilla source code :
  //        firefox/mozilla/browser/components/bookmarks/content/addBookmark2.js
  
  var RDF = Components.classes["@mozilla.org/rdf/rdf-service;1"]
                             .getService(Components.interfaces.nsIRDFService);
  var localStore = RDF.GetDataSource("rdf:local-store");
  var rAttribute = RDF.GetResource("currentset");
  var rElement   = RDF.GetResource(constParameters.XUL + "#" + constParameters.toolbarID);
  var rDialog    = RDF.GetResource(constParameters.XUL);
  var rPersist   = RDF.GetResource("http://home.netscape.com/NC-rdf#persist");
  var rOldValue = localStore.GetTarget(rElement, rAttribute, true);
/*  
      if (aOptions && aOptions.iconsize)
          toolbar.setAttribute("iconsize", aOptions.iconsize);*/
  if (rOldValue) {
    localStore.Change(rElement, rAttribute, rOldValue, RDF.GetLiteral(newToolbar));
  } else {
    localStore.Assert(rDialog, rPersist, rElement, true);
    localStore.Assert(rElement, rAttribute, RDF.GetLiteral(newToolbar), true);
  }
  
  rAttribute = RDF.GetResource("iconsize");
  rOldValue = localStore.GetTarget(rElement, rAttribute, true);
  
  if (rOldValue) {
    localStore.Unassert(rElement, rAttribute, rOldValue, true);
  }

  if (aOptions && aOptions.iconsize) {
    localStore.Assert(rElement, rAttribute, RDF.GetLiteral(aOptions.iconsize), true);
  }
}

Toolbar.getRDFData = function Toolbar_getRDFData () {
  var constParameters = this.getConstParameters();
  
  var RDF = Components.classes["@mozilla.org/rdf/rdf-service;1"]
                             .getService(Components.interfaces.nsIRDFService);
  var localStore = RDF.GetDataSource("rdf:local-store");
  var rAttribute = RDF.GetResource("currentset");
  var rElement = RDF.GetResource(constParameters.XUL + "#" + constParameters.toolbarID);
  var rOldValue = localStore.GetTarget(rElement, rAttribute, true);

  if (rOldValue) {
  	return rOldValue.Value;
  }
  
  return null;
}

Toolbar.getCurrentSet = function Toolbar_getCurrentSet () {
  var constParameters = this.getConstParameters();
  
  return this.eachWindows (function (w) {
    var toolbar = w.document.getElementById(constParameters.toolbarID);
    if (toolbar) {
      return toolbar.currentSet;
    }
  });
}

Toolbar.eachWindows = function (callback) {
  var windowManager = Components.classes['@mozilla.org/appshell/window-mediator;1']
                                  .getService(Components.interfaces.nsIWindowMediator);

  windows = windowManager.getEnumerator(null);

  while (windows.hasMoreElements() ) {
    var ret = callback(windows.getNext());
    if (ret != null) {
      return ret;
    }
  }
}

Toolbar.flush = function Toolbar_flush () {
  var constParameters = this.getConstParameters();
  
  this.eachWindows (function (w) {
    var toolbar = w.document.getElementById(constParameters.toolbarID);
    if (toolbar) {
      while (toolbar.firstChild) {
        //The list is LIVE so it will re-index each call
        toolbar.removeChild(toolbar.firstChild);
      };
      // update from DOM modifications to currentset string attribute
      toolbar.setAttribute("currentset", toolbar.currentSet);
    }
    return null;
  });
}


Toolbar.detectVersion = function Toolbar_detectVersion(){
  var info = Components.classes["@mozilla.org/xre/app-info;1"]
                 .getService(Components.interfaces.nsIXULAppInfo);
  // Get the name of the application running us
  var retour = {
    name: info.name, // Returns "Firefox" for Firefox
    version: info.version, // Returns "2.0.0.1" for Firefox version 2.0.0.1
  };
  return retour;
}

Toolbar.getConstParameters = function Toolbar_getConstParameters() {
  var aWindowType = "mail:3pane"; // type of the main Thunderbird window used to get it
  var xulparams = this.getXULToolbarParameters();

  // some changes for xul 2.0
  var infos = Toolbar.detectVersion();
  
  if (infos.version.indexOf("2.0") == 0){
    xulparams.toolbarID += "2";
  }
  
  return {
  	mainWindowType:  "mail:3pane", // type of the main Thunderbird window used to get it
  	toolbarID: xulparams.toolbarID,
  	XUL: xulparams.XUL,
  };
}

Toolbar.extend = function () {
  obj = Toolbar;
  function Clone() { } 
  Clone.prototype = obj;
  return new Clone();
}

var MainToolbar = function () {}
MainToolbar = Toolbar.extend(); // MainToolbar extends Toolbar
MainToolbar.constructor = MainToolbar;

MainToolbar.getXULToolbarParameters = function () {
  return {
    toolbarID: "mail-bar", // toolbar id used when not found in localstore.rdf
    XUL: "chrome://messenger/content/messenger.xul" // the toolbar rdf resource ID
  };
}

var AddressBookToolbar = function () {}
AddressBookToolbar = Toolbar.extend(); // ComposeToolbar extends Toolbar
AddressBookToolbar.constructor = AddressBookToolbar;

AddressBookToolbar.getXULToolbarParameters = function () {
  return {
    toolbarID: "ab-bar", // toolbar id used when not found in localstore.rdf
    XUL: "chrome://messenger/content/addressbook/addressbook.xul" // the toolbar rdf resource ID
  };
}

var ComposeToolbar = function () {}
ComposeToolbar = Toolbar.extend(); // ComposeToolbar extends Toolbar
MainToolbar.constructor = ComposeToolbar;

ComposeToolbar.getXULToolbarParameters = function () {
  return {
    toolbarID: "composeToolbar", // toolbar id used when not found in localstore.rdf
    XUL: "chrome://messenger/content/messengercompose/messengercompose.xul" // the toolbar rdf resource ID
  };
}

