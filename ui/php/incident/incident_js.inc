<?php
///////////////////////////////////////////////////////////////////////////////
// OBM - File  : contact_js.inc                                              //
//     - Desc  : Contact javascript functions File                           //
// 2000-01-13 Pierre Baudracco : Last Update 2000-01-13                      //
///////////////////////////////////////////////////////////////////////////////
// $Id$ //
///////////////////////////////////////////////////////////////////////////////


require("$obminclude/javascript/check_js.inc");

$extra_js .= "
function confirm_del() {
  if (confirm (\"$l_delete_confirm\"))
    return true;
  else
    return false;
}

function check_incident(form) {
  // MANDATORY : Check that Contact name is not empty
  if (trim(form.tf_lincident.value) == \"\") {
    alert (\"$l_fill_label\");
    return false;
  }
  if (form.tf_lincident.value.length > 100) {
    alert (\"$l_length_label\");
    return false;
  }
  if (form.tf_date.value == \"\") { 
      alert(\"$l_date\" + \" : $l_fill_date\");
      return false;
    }
 return true;
}


///////////////////////////////////////////////////////////////////////////////
// Check if the Incident Category 1 deletion is possible
///////////////////////////////////////////////////////////////////////////////
function check_cat1_checkdel(form)
{  
   if (form.sel_cat1.selectedIndex >= 0)
   { 
     return true;
   }
   else
   {
     alert(\"$l_j_cat1_delete\");
     return false;
   }
}


///////////////////////////////////////////////////////////////////////////////
// Check if the Category 1 update is possible
///////////////////////////////////////////////////////////////////////////////
function check_cat1_upd(form,formd)
{
  cat1_pos = formd.sel_cat1.selectedIndex;
  if ((cat1_pos >= 0) && (form.tf_cat1.value != \"\")) {
    form.sel_cat1.value=formd.sel_cat1.options[cat1_pos].value;
    return true;
  } else {
    alert(\"$l_j_cat1_update\");
    return false;
  }
}


///////////////////////////////////////////////////////////////////////////////
// Check if the Category 1 creation is possible
///////////////////////////////////////////////////////////////////////////////
function check_cat1_new(form)
{
   if (form.tf_cat1.value != \"\")
      return true;
   else
   {
      alert(\"$l_j_cat1_create\");
      return false;
   }
}

";
?>