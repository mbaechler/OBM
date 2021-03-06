<?php
///////////////////////////////////////////////////////////////////////////////
// OBM - File : php/campaign/campaign_js.php
//     - Desc : campaign JS File
// 2008-02-11 Christophe Liou Kee On
///////////////////////////////////////////////////////////////////////////////
// $Id:  $ //
///////////////////////////////////////////////////////////////////////////////

require("obminclude/javascript/check_js.inc");

  $extra_js .= "

var fx_slides = [];

function init_fx_slide (nameid) {
  //~ fx_slides[nameid] = new Fx.Slide(\$('slide_detail_' + nameid), {duration: 50,wait:false});
  //~ fx_slides[nameid].hide();
  myslide = new Fx.Slide(\$('slide_detail_' + nameid), {duration: 50,wait:false});
  myslide.hide();
  \$('slide_detail_' + nameid).setStyle('display','block');
  \$('slide_link_' + nameid).addEvent('click', function(e){
    myslide.toggle();
  });
}

";

// to avoid a javascript error when ext_widget is not defined
if ($params["ext_widget"] != "") {
  $extra_js .= "

/*
function check_campaign_get_id(valeur,text) {
    if ((valeur < 1) || (valeur == null)) {
      alert (\"$l_j_select_campaign\");
      return false;
    } else {
      window.opener.document.$params[ext_widget].value=valeur;
      window.opener.document.$params[ext_widget_text].value=text;
      if ('$params[restriction_calendar]' == 'calendar') {
        window.opener.location=('../calendar/calendar_index.php?date=$params[date]&campaign_id='+valeur+'&new_campaign=1');
      }
      window.close();
      return true;
    }
}
*/
";
}

if ($params["ext_element"] != "") {
  require_once("$obminclude/of/of_select.inc");
}

?>