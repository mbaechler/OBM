<?php
///////////////////////////////////////////////////////////////////////////////
// OBM - File : resourcegroup_js.inc                                         //
//     - Desc : ResourceGroup javascript functions File                      //
// 2005-08-01 Aliacom - Pierre Baudracco                                     //
///////////////////////////////////////////////////////////////////////////////
// $Id$
///////////////////////////////////////////////////////////////////////////////

require("obminclude/javascript/check_js.inc");

if ($ext_element != "") {
  require_once("$obminclude/of/of_select.inc");
}

// to avoid a javascript error when ext_widget is not defined
if ($ext_widget != "") {
  $extra_js .= "

function fill_ext_form(int_form) {
   size = int_form.length;
   ext_field = window.opener.document.$ext_widget;
   for(i=0; i <size ; i++) {
     if(int_form.elements[i].type == 'checkbox'){
       if(int_form.elements[i].checked == true) {
	 ext_size = ext_field.length;
	 for(j=0; j< ext_size; j++) {
	   if('cb_g' + ext_field.options[j].value == int_form.elements[i].name) {
	     window.opener.document.$ext_widget.options[j].selected =true;
	   }
	 }
       }
     }
   }
}";
}
?>
