<?php
///////////////////////////////////////////////////////////////////////////////
// OBM - File : people_index.php                                             //
//     - Desc : People Index File                                            //
// 2008-10-06 Vincent Bernard                                                //
///////////////////////////////////////////////////////////////////////////////
// $Id: people_index.php 3102 2008-09-30 08:39:59Z vincentb $ //
///////////////////////////////////////////////////////////////////////////////
// Actions :
// - index (default) -- search fields  -- show the user search form
// - search          -- search fields  -- show the result set of search
// - detailconsult   -- $user_id       -- show the user detail
// - detailupdate    -- $user_id       -- show the user detail form
// - update          -- form fields    -- update the user
///////////////////////////////////////////////////////////////////////////////

$path = "..";
$module = "people";
$obminclude = getenv("OBM_INCLUDE_VAR");
if ($obminclude == "") $obminclude = "obminclude";
include("$obminclude/global.inc");
$params = get_user_params();
page_open(array("sess" => "OBM_Session", "auth" => $auth_class_name, "perm" => "OBM_Perm"));
include("$obminclude/global_pref.inc");
require_once("$obminclude/of/of_category.inc");
require("../profile/profile_query.inc");
require("people_display.inc");
require("people_query.inc");
require("people_js.inc");
require("$obminclude/of/of_right.inc"); // needed by call from calendar

// detailconsult can be accessed without user_id (-> display current user)
if (($action == "detailconsult") && (! $params["user_id"])) $params["user_id"] = $obm["uid"];

get_user_action();
$perm->check_permissions($module, $action);

page_close();

if ($action == "index" || $action == "") {
///////////////////////////////////////////////////////////////////////////////
  $display["search"] = html_user_search_form($params);
  if ($_SESSION['set_display'] == "yes") {
    $display["result"] = dis_user_search_list($params);
  } else {
    $display["msg"] .= display_info_msg($l_no_display);
  }

} elseif ($action == "search") {
///////////////////////////////////////////////////////////////////////////////
  $display["search"] = html_user_search_form($params);
  $display["result"] = dis_user_search_list($params);

} elseif ($action == "ext_search") {
///////////////////////////////////////////////////////////////////////////////
  $user_q = run_query_user_ext_search($params);
  json_search_users($params, $user_q);
  echo "(".$display['json'].")";
  exit();

} elseif ($action == "detailconsult") {
///////////////////////////////////////////////////////////////////////////////
  $display["detail"] = dis_user_consult($params);

} elseif ($action == "detailupdate") {
///////////////////////////////////////////////////////////////////////////////
  $obm_q = run_query_user_detail($params["user_id"]);
  if ($obm_q->num_rows() == 1) {
    $display["detailInfo"] = display_record_info($obm_q);
    $display["detail"] = html_user_form($obm_q, $params);
  } else {
    $display["msg"] .= display_err_msg($l_err_reference);
  }

} elseif ($action == "update") {
///////////////////////////////////////////////////////////////////////////////
  if (check_user_defined_rules() && check_user_data_form($params["user_id"], $params)) {
    $retour = run_query_user_update($params["user_id"], $params);
    if ($retour) {
      set_update_state();
      $display["msg"] .= display_ok_msg("$l_user : $l_update_ok");
      $display["detail"] = dis_user_consult($params);
    } else {
      $display["msg"] .= display_err_msg("$l_user : $l_update_error");
      $display["detail"] = html_user_form("", $params, $err["field"]);
    }
  } else {
    $display["msg"] .= display_err_msg($err["msg"]);
    $display["detail"] = html_user_form("", $params, $err["field"]);
  }

} elseif ($action == 'display') {
///////////////////////////////////////////////////////////////////////////////
  $prefs = get_display_pref($obm['uid'], 'people', 1);
  $display['detail'] = dis_user_display_pref($prefs);

} else if ($action == 'dispref_display') {
///////////////////////////////////////////////////////////////////////////////
  update_display_pref($params);
  $prefs = get_display_pref($obm['uid'], 'people', 1);
  $display['detail'] = dis_user_display_pref($prefs);

} else if ($action == 'dispref_level') {
///////////////////////////////////////////////////////////////////////////////
  update_display_pref($params);
  $prefs = get_display_pref($obm['uid'], 'people', 1);
  $display['detail'] = dis_user_display_pref($prefs);
}

of_category_user_action_switch($module, $action, $params);

///////////////////////////////////////////////////////////////////////////////
// Display
///////////////////////////////////////////////////////////////////////////////
$display['head'] = display_head($l_people);
if (! $params['popup']) {
  update_user_action();
  $display['header'] = display_menu($module);
}
$display['end'] = display_end();

display_page($display);


///////////////////////////////////////////////////////////////////////////////
// Stores User parameters transmited in $params hash
// returns : $params hash with parameters set
///////////////////////////////////////////////////////////////////////////////
function get_user_params() {
  
  // Get global params
  $params = get_global_params('UserObm');

  if (isset($params)) {
    $nb_group = 0;
    while ( list( $key ) = each($params) ) {
      if (strcmp(substr($key, 0, 7),'data-g-') == 0) {
        $nb_group++;
        $group_num = substr($key, 7);
        $params["group_$nb_group"] = $group_num;
      }
    }
    $params['group_nb'] = $nb_group;
    
  }
  
  if (isset ($_FILES['fi_file'])) {
    $params['file_tmp'] = $_FILES['fi_file']['tmp_name'];
    $params['file_name'] = $_FILES['fi_file']['name'];
    $params['size'] = $_FILES['fi_file']['size'];
    $params['type'] = $_FILES['fi_file']['type'];
  }

  if(is_array($params['email'])) {
    $email_aliases = array();
    while(!empty($params['email'])) {
      $email = trim(array_shift($params['email']));
      $domain = array_shift($params['aliases']);
      if(!empty($email)) {
       if(!empty($domain)) {
          $email_aliases[] = $email.'@'.$domain;
        } else {
          $email_aliases[] = $email;
        }
      }
    }

    $params['email'] = implode("\r\n", $email_aliases);
  }

  return $params;
}


///////////////////////////////////////////////////////////////////////////////
// User Action 
///////////////////////////////////////////////////////////////////////////////
function get_user_action() {
  global $params, $actions, $path;
  global $l_header_find,$l_header_new,$l_header_update,$l_header_delete;
  global $l_header_consult,$l_header_display,$l_header_admin,$l_header_import;
  global $l_header_upd_group,$l_header_admin, $l_header_reset;
  global $cright_read, $cright_write, $cright_read_admin, $cright_write_admin;
  
  of_category_user_module_action('people');

// Index
  $actions['people']['index'] = array (
    'Name'     => $l_header_find,
    'Url'      => "$path/people/people_index.php?action=index",
    'Right'    => $cright_read,
    'Condition'=> array ('all')
                                    );

// Get Ids
  $actions['people']['ext_get_ids'] = array (
    'Url'      => "$path/people/people_index.php?action=ext_get_ids",
    'Right'    => $cright_read,
    'Condition'=> array ('none'),
    'popup' => 1
                                    );
                                    
// Get Ids
  $actions['people']['ext_get_id'] = array (
    'Url'      => "$path/people/people_index.php?action=ext_get_id",
    'Right'    => $cright_read,
    'Condition'=> array ('none'),
    'popup' => 1
                                    );

// Search
  $actions['people']['search'] = array (
    'Url'      => "$path/people/people_index.php?action=search",
    'Right'    => $cright_read,
    'Condition'=> array ('None')
                                  );
// Search
  $actions['people']['ext_search'] = array (
    'Url'      => "$path/people/people_index.php?action=ext_search",
    'Right'    => $cright_read,
    'Condition'=> array ('None')
  );  

// Get user id from external window (js)
  $actions['people']['getsearch'] = array (
    'Url'      => "$path/people/people_index.php?action=search",
    'Right'    => $cright_read,
    'Condition'=> array ('None')
                                  );
// Detail Consult
  $actions['people']['detailconsult'] = array (
    'Name'     => $l_header_consult,
    'Url'      => "$path/people/people_index.php?action=detailconsult&amp;user_id=".$params['user_id'],
    'Right'    => $cright_read_admin,
    'Condition'=> array ('detailconsult', 'detailupdate', 'update', 'group_consult', 'group_update')
                                  );

// Detail Update
  $actions['people']['detailupdate'] = array (
     'Name'     => $l_header_update,
     'Url'      => "$path/people/people_index.php?action=detailupdate&amp;user_id=".$params['user_id'],
     'Right'    => $cright_write_admin,
     'Condition'=> array ('detailconsult', 'reset', 'update', 'group_consult', 'group_update')
                                     	   );

// Update
  $actions['people']['update'] = array (
    'Url'      => "$path/people/people_index.php?action=update",
    'Right'    => $cright_write_admin,
    'Condition'=> array ('None') 
                                     );

// Display
  $actions['people']['display'] = array (
    'Name'     => $l_header_display,
    'Url'      => "$path/people/people_index.php?action=display",
    'Right'    => $cright_read,
    'Condition'=> array ('all')
                                      	 );

// Display
  $actions['people']['dispref_display'] = array (
    'Url'      => "$path/people/people_index.php?action=dispref_display",
    'Right'    => $cright_read,
    'Condition'=> array ('None')
                                      	 );
// Display
  $actions['people']['dispref_level'] = array (
    'Url'      => "$path/people/people_index.php?action=dispref_level",
    'Right'    => $cright_read,
    'Condition'=> array ('None')
                                      	 );

}


///////////////////////////////////////////////////////////////////////////////
// User Actions updates (after processing, before displaying menu)
///////////////////////////////////////////////////////////////////////////////
function update_user_action() {
  global $params, $actions, $path;

  $id = $params['user_id'];
  if ($id > 0) {
    // Detail Consult
    $actions['people']['detailconsult']['Url'] = "$path/people/people_index.php?action=detailconsult&amp;user_id=$id";
    $actions['people']['detailconsult']['Condition'][] = 'insert';

    // Detail Update
    $actions['people']['detailupdate']['Url'] = "$path/people/people_index.php?action=detailupdate&amp;user_id=$id";
    $actions['people']['detailupdate']['Condition'][] = 'insert';

  }
}


?>
