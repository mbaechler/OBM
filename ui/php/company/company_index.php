<script language="php">
///////////////////////////////////////////////////////////////////////////////
// OBM - File : company_index.php                                            //
//     - Desc : Company Index File                                           //
// 1999-03-19 Pierre Baudracco                                               //
///////////////////////////////////////////////////////////////////////////////
// $Id$ //
///////////////////////////////////////////////////////////////////////////////
// Actions              -- Parameter
// - index (default)    -- search fields  -- show the company search form
// - search             -- search fields  -- show the result set of search
// - new                --                -- show the new company form
// - detailconsult      -- $param_company -- show the company detail
// - detailupdate       -- $param_company -- show the company detail form
// - insert             -- form fields    -- insert the company
// - update             -- form fields    -- update the company
// - check_delete       -- $param_company -- check links before delete
// - delete             -- $hd_company_id -- delete the company
// - admin              --                -- admin index (kind)
// - kind_insert        -- form fields    -- insert the kind
// - kind_update        -- form fields    -- update the kind
// - kind_checklink     --                -- check if kind is used
// - kind_delete        -- $sel_kind      -- delete the kind
// - activity_insert    -- form fields    -- insert the kind
// - activity_update    -- form fields    -- update the kind
// - activity_checklink --                -- check if kind is used
// - activity_delete    -- $sel_kind      -- delete the kind
// - display            --                -- display and set display parameters
// - dispref_display    --                -- update one field display value
// - dispref_level      --                -- update one field display position 
// External API ---------------------------------------------------------------
// - ext_get_id         -- $title         -- select a company (return id) 
// - ext_get_id_url     -- $url, $title   -- select a company (id), load URL 
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
// Session, Auth, Perms  Management                                          //
///////////////////////////////////////////////////////////////////////////////
$path = "..";
$section = "COM";
$menu = "COMPANY";
$obminclude = getenv("OBM_INCLUDE_VAR");
if ($obminclude == "") $obminclude = "obminclude";
include("$obminclude/global.inc");
page_open(array("sess" => "OBM_Session", "auth" => "OBM_Challenge_Auth", "perm" => "OBM_Perm"));
include("$obminclude/global_pref.inc");

require("company_query.inc");
require("company_display.inc");

// updating the company bookmark : 
if ( ($param_company == $last_company) && (strcmp($action,"delete")==0) ) {
  $last_company = $last_company_default;
} else if ( ($param_company > 0 ) && ($last_company != $param_company) ) {
  $last_company = $param_company;
  run_query_set_user_pref($auth->auth["uid"],"last_company", $param_company);
  $last_company_name = run_query_global_company_name($last_company);
}

page_close();
if ($action == "") $action = "index";
$company = get_param_company();
get_company_action();
$perm->check();

///////////////////////////////////////////////////////////////////////////////
// Main Program                                                              //
///////////////////////////////////////////////////////////////////////////////

if (! $company["popup"]) {
  $display["header"] = generate_menu($menu, $section); // Menu
}

///////////////////////////////////////////////////////////////////////////////
// External calls (main menu not displayed)                                  //
///////////////////////////////////////////////////////////////////////////////
if ($action == "ext_get_id") {
  require("company_js.inc");
  $comp_q = run_query_active_company();
  $display["detail"] = html_select_company($comp_q, $company);

} elseif ($action == "ext_get_id_url") {
///////////////////////////////////////////////////////////////////////////////
  require("company_js.inc");
  $comp_q = run_query_active_company();
  $display["detail"] = html_select_company($comp_q, $company);
  
} elseif ($action == "ext_get_cat_ids") {
  $extra_css = "category.css";
  require("company_js.inc");
  $display["detail"] =  html_category_tree($document);

///////////////////////////////////////////////////////////////////////////////
// Normal calls
///////////////////////////////////////////////////////////////////////////////
} elseif ($action == "index" || $action == "") {
///////////////////////////////////////////////////////////////////////////////
  $type_q = run_query_companytype();
  $act_q = run_query_companyactivity();
  $usr_q = run_query_userobm_active();
  $display["search"] = html_company_search_form($type_q, $act_q, $usr_q, $company);
  if ($set_display == "yes") {
    $display["result"] = dis_company_search_list($company);
  } else {
    $display["msg"] = display_info_msg($l_no_display);
  }

} elseif ($action == "search")  {
///////////////////////////////////////////////////////////////////////////////
  $type_q = run_query_companytype();
  $act_q = run_query_companyactivity();
  $usr_q = run_query_userobm_active();
  $display["search"] = html_company_search_form($type_q, $act_q, $usr_q, $company);
  $display["result"] = dis_company_search_list($company);

} elseif ($action == "new")  {
///////////////////////////////////////////////////////////////////////////////
  $dsrc_q = run_query_datasource();
  $type_q = run_query_companytype();
  $act_q = run_query_companyactivity();
  $usr_q = run_query_userobm_active();
  $cat_q = run_query_companycat();
  $ctry_q = run_query_country();
  require("company_js.inc");
  $display["detail"] = html_company_form($action,"", $dsrc_q, $type_q, $act_q, $usr_q, $cat_q,"", $ctry_q, $company);

} elseif ($action == "detailconsult")  {
///////////////////////////////////////////////////////////////////////////////
  if ($param_company > 0) {
    $comp_q = run_query_detail($param_company);
    $cat_q = run_query_get_companycat_name($param_company);
    if ($comp_q->num_rows() == 1) {
      $display["detailInfo"] = display_record_info($comp_q);
      $display["detail"] = html_company_consult($comp_q, $cat_q);
    } else {
      $display["msg"] .= display_err_msg($l_query_error . " - " . $comp_q->query . " !");
    }
  }

} elseif ($action == "detailupdate")  {
///////////////////////////////////////////////////////////////////////////////
  if ($param_company > 0) {
    $comp_q = run_query_detail($param_company);
    if ($comp_q->num_rows() == 1) {
      $dsrc_q = run_query_datasource();
      $type_q = run_query_companytype();
      $act_q = run_query_companyactivity();
      $users = array($comp_q->f("company_marketingmanager_id"));
      $usr_q = run_query_userobm_active($users);
      $cat_q = run_query_companycat();
      $compcat = run_query_get_companycat($param_company);
      $ctry_q = run_query_country();
      require("company_js.inc");
      $display["detailInfo"] = display_record_info($comp_q);
      $display["detail"] = html_company_form($action, $comp_q, $dsrc_q, $type_q, $act_q, $usr_q, $cat_q,$compcat,$ctry_q, $company);
    } else {
      $display["msg"] .= display_err_msg($l_query_error . " - " . $comp_q->query . " !");
    }
  }

} elseif ($action == "insert")  {
///////////////////////////////////////////////////////////////////////////////
  if (check_data_form("", $company)) {

    // If the context (same companies) was confirmed ok, we proceed
    if ($hd_confirm == $c_yes) {
      $retour = run_query_insert($company);
      if ($retour) {
        $display["msg"] .= display_ok_msg($l_insert_ok);
      } else {
        $display["msg"] .= display_err_msg($l_insert_error);
      }
      $type_q = run_query_companytype();
      $act_q = run_query_companyactivity();
      $usr_q = run_query_userobm_active();
      $display["search"] = html_company_search_form($type_q, $act_q, $usr_q, $company);
    // If it is the first try, we warn the user if some companies seem similar
    } else {
      $obm_q = check_company_context("", $company);
      if ($obm_q->num_rows() > 0) {
        $display["detail"] = dis_company_warn_insert("", $obm_q, $company);
      } else {
        $retour = run_query_insert($company);
        if ($retour) {
          $display["msg"] .= display_ok_msg($l_insert_ok);
        } else {
          $display["msg"] .= display_err_msg($l_insert_error);
        }
        $type_q = run_query_companytype();
	$act_q = run_query_companyactivity();
        $usr_q = run_query_userobm_active();
        $display["search"] = html_company_search_form($type_q, $act_q, $usr_q, $company);
      }
    }

  // Form data are not valid
  } else {
    $display["msg"] = display_warn_msg($l_invalid_data . " : " . $err_msg);
    $dsrc_q = run_query_datasource();
    $type_q = run_query_companytype();
    $act_q = run_query_companyactivity();
    $cat_q = run_query_companycat();
    $users = array($company["marketing_manager"]);
    $usr_q = run_query_userobm_active($users);
    $ctry_q = run_query_country();
    $display["search"] = html_company_form($action, "", $dsrc_q, $type_q, $act_q, $usr_q,$cat_q,"",$ctry_q, $company);
  }

} elseif ($action == "update")  {
///////////////////////////////////////////////////////////////////////////////
  if (check_data_form($param_company, $company)) {
    $retour = run_query_update($param_company, $company);
    if ($retour) {
      $display["msg"] .= display_ok_msg($l_update_ok);
    } else {
      $display["msg"] .= display_err_msg($l_update_error);
    }
    $comp_q = run_query_detail($param_company);
    $cat_q = run_query_get_companycat_name($param_company);
    $display["detailInfo"] .= display_record_info($comp_q);
    $display["detail"] = html_company_consult($comp_q, $cat_q);
  } else {
    $display["msg"] .= display_warn_msg($l_invalid_data . " : " . $err_msg);
    $comp_q = run_query_detail($param_company);
    $dsrc_q = run_query_datasource();
    $type_q = run_query_companytype();
    $act_q = run_query_companyactivity();
    $users = array($company["marketing_manager"]);
    $usr_q = run_query_userobm_active($users);
    $cat_q = run_query_companycat();
    $ctry_q = run_query_country();
    $display["detail"] = html_company_form($action, $comp_q, $dsrc_q, $type_q, $act_q, $usr_q, $cat_q, "", $ctry_q, $company);
  }

} elseif ($action == "check_delete")  {
///////////////////////////////////////////////////////////////////////////////
  require("company_js.inc");
  $display["detail"] = dis_check_links($param_company);

} elseif ($action == "delete")  {
///////////////////////////////////////////////////////////////////////////////
  $retour = run_query_delete($hd_company_id);
  if ($retour) {
    $display["msg"] .= display_ok_msg($l_delete_ok);
  } else {
    $display["msg"] .= display_err_msg($l_delete_error);
  }
  $type_q = run_query_companytype();
  $act_q = run_query_companyactivity();
  $usr_q = run_query_userobm_active();
  $display["search"] = html_company_search_form($type_q, $act_q, $usr_q, $company);

} elseif ($action == "admin")  {
///////////////////////////////////////////////////////////////////////////////
  require("company_js.inc");
  $display["detail"] = dis_admin_index();

} elseif ($action == "kind_insert")  {
///////////////////////////////////////////////////////////////////////////////
  $retour = run_query_kind_insert($company);
  if ($retour) {
    $display["msg"] .= display_ok_msg($l_kind_insert_ok);
  } else {
    $display["msg"] .= display_err_msg($l_kind_insert_error);
  }
  require("company_js.inc");
  $display["detail"] .= dis_admin_index();

} elseif ($action == "kind_update")  {
///////////////////////////////////////////////////////////////////////////////
  $retour = run_query_kind_update($company);
  if ($retour) {
    $display["msg"] .= display_ok_msg($l_kind_update_ok);
  } else {
    $display["msg"] .= display_err_msg($l_kind_update_error);
  }
  require("company_js.inc");
  $display["detail"] .= dis_admin_index();

} elseif ($action == "kind_checklink")  {
///////////////////////////////////////////////////////////////////////////////
  $display["detail"] .= dis_kind_links($company);
  require("company_js.inc");
  $display["detail"] .= dis_admin_index();

} elseif ($action == "kind_delete")  {
///////////////////////////////////////////////////////////////////////////////
  $retour = run_query_kind_delete($company["kind"]);
  if ($retour) {
    $display["msg"] .= display_ok_msg($l_kind_delete_ok);
  } else {
    $display["msg"] .= display_err_msg($l_kind_delete_error);
  }
  require("company_js.inc");
  $display["detail"] .= dis_admin_index();

} elseif ($action == "cat_insert")  {
///////////////////////////////////////////////////////////////////////////////
  $retour = run_query_cat_insert($company);
  if ($retour) {
    $display["msg"] .= display_ok_msg($l_cat_insert_ok);
  } else {
    $display["msg"] .= display_err_msg($l_cat_insert_error);
  }
  require("company_js.inc");
  $display["detail"] .= dis_admin_index();

} elseif ($action == "cat_update")  {
///////////////////////////////////////////////////////////////////////////////
  $retour = run_query_cat_update($company);
  if ($retour) {
    $display["msg"] .= display_ok_msg($l_cat_update_ok);
  } else {
    $display["msg"] .= display_err_msg($l_cat_update_error);
  }
  require("company_js.inc");
  $display["detail"] .= dis_admin_index();

} elseif ($action == "cat_checklink")  {
///////////////////////////////////////////////////////////////////////////////
  $display["detail"] .= dis_cat_links($company);
  require("company_js.inc");
  $display["detail"] .= dis_admin_index();

} elseif ($action == "cat_delete")  {
///////////////////////////////////////////////////////////////////////////////
  $retour = run_query_cat_delete($company["cat"]);
  if ($retour) {
    $display["msg"] .= display_ok_msg($l_cat_delete_ok);
  } else {
    $display["msg"] .= display_err_msg($l_cat_delete_error);
  }
  require("company_js.inc");
  $display["detail"] .= dis_admin_index();


} elseif ($action == "activity_insert")  {
///////////////////////////////////////////////////////////////////////////////
  $retour = run_query_activity_insert($company);
  if ($retour) {
    $display["msg"] .= display_ok_msg($l_act_insert_ok);
  } else {
    $display["msg"] .= display_err_msg($l_act_insert_error);
  }
  require("company_js.inc");
  $display["detail"] .= dis_admin_index();

} elseif ($action == "activity_update")  {
///////////////////////////////////////////////////////////////////////////////
  $retour = run_query_activity_update($company);
  if ($retour) {
    $display["msg"] .= display_ok_msg($l_act_update_ok);
  } else {
    $display["msg"] .= display_err_msg($l_act_update_error);
  }
  require("company_js.inc");
  $display["detail"] .= dis_admin_index();

} elseif ($action == "activity_checklink")  {
///////////////////////////////////////////////////////////////////////////////
  $display["detail"] .= dis_activity_links($company);
  require("company_js.inc");
  $display["detail"] .= dis_admin_index();

} elseif ($action == "activity_delete")  {
///////////////////////////////////////////////////////////////////////////////
  $retour = run_query_activity_delete($company["activity"]);
  if ($retour) {
    $display["msg"] .= display_ok_msg($l_act_delete_ok);
  } else {
    $display["msg"] .= display_err_msg($l_act_delete_error);
  }
  require("company_js.inc");
  $display["detail"] .= dis_admin_index();

}  elseif ($action == "display") {
///////////////////////////////////////////////////////////////////////////////
  $pref_q = run_query_display_pref($auth->auth["uid"], "company", 1);
  $display["detail"] = dis_company_display_pref($pref_q);

} else if ($action == "dispref_display") {
///////////////////////////////////////////////////////////////////////////////
  run_query_display_pref_update($entity, $fieldname, $disstatus);
  $pref_q = run_query_display_pref($auth->auth["uid"], "company", 1);
  $display["detail"] = dis_company_display_pref($pref_q);

} else if ($action == "dispref_level") {
///////////////////////////////////////////////////////////////////////////////
  run_query_display_pref_level_update($entity, $new_level, $fieldorder);
  $pref_q = run_query_display_pref($auth->auth["uid"], "company", 1);
  $display["detail"] = dis_company_display_pref($pref_q);

} elseif ($action == "document_add")  {
///////////////////////////////////////////////////////////////////////////////
  if ($company["doc_nb"] > 0) {
    $nb = run_query_insert_documents($company,"Company");
    $display["msg"] .= display_ok_msg("$nb $l_document_added");
  } else {
    $display["msg"] .= display_err_msg($l_no_document_added);
  }
  $comp_q = run_query_detail($company["id"]);
  $cat_q = run_query_get_companycat_name($param_company);
  if ($comp_q->num_rows() == 1) {
    $display["detailInfo"] = display_record_info($comp_q);
    $display["detail"] = html_company_consult($comp_q, $cat_q);
  } else {
    var_dump($company);    
    $display["msg"] .= display_err_msg($l_query_error . " - " . $comp_q->num_rows() . " !");
  }
}


///////////////////////////////////////////////////////////////////////////////
// Display
///////////////////////////////////////////////////////////////////////////////
$display["head"] = display_head($l_company);
$display["end"] = display_end();

display_page($display);


///////////////////////////////////////////////////////////////////////////////
// Stores Company parameters transmited in $company hash
// returns : $company hash with parameters set
///////////////////////////////////////////////////////////////////////////////
function get_param_company() {
  global $tf_num, $cb_archive, $tf_name, $tf_aka, $tf_ad1, $tf_ad2, $tf_ad3;
  global $tf_zip, $tf_town, $tf_cdx, $sel_ctry, $tf_phone, $tf_fax, $tf_web;
  global $tf_email, $sel_act, $sel_kind,$sel_cat, $sel_market, $ta_com, $param_company;
  global $sel_dsrc, $tf_kind, $tf_act,$tf_cat_code,$tf_cat,$sel_cat;
  global $cdg_param;
  global $popup, $ext_action, $ext_url, $ext_id, $ext_title, $ext_target;  
  global $HTTP_POST_VARS,$HTTP_GET_VARS;

  if (isset ($popup)) $company["popup"] = $popup;
  if (isset ($ext_action)) $company["ext_action"] = $ext_action;
  if (isset ($ext_url)) $company["ext_url"] = urldecode($ext_url);
  if (isset ($ext_id)) $company["ext_id"] = $ext_id;
  if (isset ($ext_id)) $company["id"] = $ext_id;
  if (isset ($ext_title)) $company["ext_title"] = stripslashes(urldecode($ext_title));
  if (isset ($ext_target)) $company["ext_target"] = $ext_target;
  
  if ((is_array ($HTTP_POST_VARS)) && (count($HTTP_POST_VARS) > 0)) {
    $http_obm_vars = $HTTP_POST_VARS;
  } elseif ((is_array ($HTTP_GET_VARS)) && (count($HTTP_GET_VARS) > 0)) {
    $http_obm_vars = $HTTP_GET_VARS;
  }

  if (isset ($http_obm_vars)) {
    $nb_d = 0;
    $nb_comp = 0;
    while ( list( $key ) = each( $http_obm_vars ) ) {
      if (strcmp(substr($key, 0, 4),"cb_d") == 0) {
	$nb_d++;
	$d_num = substr($key, 4);
	$company["doc$nb_d"] = $d_num;
      }
    }
    $company["doc_nb"] = $nb_d;
  }
  
  if (isset ($param_company)) $company["id"] = $param_company;
  if (isset ($tf_num)) $company["num"] = $tf_num;
  if (isset ($cb_archive)) $company["archive"] = ($cb_archive == 1 ? 1 : 0);
  if (isset ($tf_name)) $company["name"] = $tf_name;
  if (isset ($tf_aka)) $company["aka"] = $tf_aka;
  if (isset ($sel_dsrc)) $company["datasource"] = $sel_dsrc;
  if (isset ($sel_kind)) $company["kind"] = $sel_kind;
  if (isset ($sel_cat)) $company["cat"] = $sel_cat;
  if (isset ($sel_act)) $company["activity"] = $sel_act;
  if (isset ($sel_market)) $company["marketing_manager"] = $sel_market;
  if (isset ($tf_ad1)) $company["ad1"] = $tf_ad1;
  if (isset ($tf_ad2)) $company["ad2"] = $tf_ad2;
  if (isset ($tf_ad3)) $company["ad3"] = $tf_ad3;
  if (isset ($tf_zip)) $company["zip"] = $tf_zip;
  if (isset ($tf_town)) $company["town"] = $tf_town;
  if (isset ($tf_cdx)) $company["cdx"] = $tf_cdx;
  if (isset ($sel_ctry)) $company["ctry"] = $sel_ctry;
  if (isset ($tf_phone)) $company["phone"] = $tf_phone;
  if (isset ($tf_fax)) $company["fax"] = $tf_fax;
  if (isset ($tf_web)) $company["web"] = $tf_web;
  if (isset ($tf_email)) $company["email"] = $tf_email;
  if (isset ($ta_com)) $company["com"] = $ta_com;

  // Admin - Kind fields
  // $sel_kind -> "kind" is already set
  if (isset ($tf_kind)) $company["kind_label"] = $tf_kind;

  // Admin - Kind fields
  // $sel_kind -> "kind" is already set
  if (isset ($tf_cat_code)) $company["cat_code"] = $tf_cat_code;
  if (isset ($tf_cat)) $company["cat_label"] = $tf_cat;
  // Admin - Activity fields
  // $sel_act -> "act" is already set
  if (isset ($tf_act)) $company["act_label"] = $tf_act;

  if (debug_level_isset($cdg_param)) {
    if ( $company ) {
      while ( list( $key, $val ) = each( $company ) ) {
        echo "<br />company[$key]=$val";
      }
    }
  }

  return $company;
}


///////////////////////////////////////////////////////////////////////////////
// Company Action 
///////////////////////////////////////////////////////////////////////////////
function get_company_action() {
  global $company, $actions, $path;
  global $l_header_find,$l_header_new_f,$l_header_update,$l_header_delete;
  global $l_header_consult, $l_header_display,$l_header_admin;
  global $company_read, $company_write, $company_admin_read, $company_admin_write;

// Index
  $actions["COMPANY"]["index"] = array (
    'Name'     => $l_header_find,
    'Url'      => "$path/company/company_index.php?action=index",
    'Right'    => $company_read,
    'Condition'=> array ('all') 
                                    	 );

// Search
  $actions["COMPANY"]["search"] = array (
    'Url'      => "$path/company/company_index.php?action=search",
    'Right'    => $company_read,
    'Condition'=> array ('None') 
                                    	 );

// New
  $actions["COMPANY"]["new"] = array (
    'Name'     => $l_header_new_f,
    'Url'      => "$path/company/company_index.php?action=new",
    'Right'    => $company_write,
    'Condition'=> array ('search','index','detailconsult','insert','update','admin','display') 
                                     );

// Detail Consult
  $actions["COMPANY"]["detailconsult"]  = array (
    'Name'     => $l_header_consult,
    'Url'      => "$path/company/company_index.php?action=detailconsult&amp;param_company=".$company["id"]."",
    'Right'    => $company_read,
    'Condition'=> array ('detailupdate') 
                                     		 );

// Detail Update
  $actions["COMPANY"]["detailupdate"] = array (
    'Name'     => $l_header_update,
    'Url'      => "$path/company/company_index.php?action=detailupdate&amp;param_company=".$company["id"]."",
    'Right'    => $company_write,
    'Condition'=> array ('detailconsult', 'update') 
                                     	      );

// Insert
  $actions["COMPANY"]["insert"] = array (
    'Url'      => "$path/company/company_index.php?action=insert",
    'Right'    => $company_write,
    'Condition'=> array ('None') 
                                     	 );

// Update
  $actions["COMPANY"]["update"] = array (
    'Url'      => "$path/company/company_index.php?action=update",
    'Right'    => $company_write,
    'Condition'=> array ('None') 
                                     	 );

// Check Delete
  $actions["COMPANY"]["check_delete"] = array (
    'Name'     => $l_header_delete,
    'Url'      => "$path/company/company_index.php?action=check_delete&amp;param_company=".$company["id"]."",
    'Right'    => $company_write,
    'Condition'=> array ('detailconsult', 'detailupdate', 'update') 
                                     	      );

// Delete
  $actions["COMPANY"]["delete"] = array (
    'Url'      => "$path/company/company_index.php?action=delete",
    'Right'    => $company_write,
    'Condition'=> array ('None') 
                                     	 );

// Admin
  $actions["COMPANY"]["admin"] = array (
    'Name'     => $l_header_admin,
    'Url'      => "$path/company/company_index.php?action=admin",
    'Right'    => $company_admin_read,
    'Condition'=> array ('all') 
                                       );

// Kind Insert
  $actions["COMPANY"]["kind_insert"] = array (
    'Url'      => "$path/company/company_index.php?action=kind_insert",
    'Right'    => $company_admin_write,
    'Condition'=> array ('None') 
                                     	     );

// Kind Update
  $actions["COMPANY"]["kind_update"] = array (
    'Url'      => "$path/company/company_index.php?action=kind_update",
    'Right'    => $company_admin_write,
    'Condition'=> array ('None') 
                                     	      );

// Kind Check Link
  $actions["COMPANY"]["kind_checklink"] = array (
    'Url'      => "$path/company/company_index.php?action=kind_checklink",
    'Right'    => $company_admin_write,
    'Condition'=> array ('None') 
                                     		);

// Kind Delete
  $actions["COMPANY"]["kind_delete"] = array (
    'Url'      => "$path/company/company_index.php?action=kind_delete",
    'Right'    => $company_admin_write,
    'Condition'=> array ('None') 
                                     	       );

					       
					       // Category Insert
  $actions["COMPANY"]["cat_insert"] = array (
    'Url'      => "$path/company/company_index.php?action=cat_insert",
    'Right'    => $company_admin_write,
    'Condition'=> array ('None') 
                                     	     );

// Category Update
  $actions["COMPANY"]["cat_update"] = array (
    'Url'      => "$path/company/company_index.php?action=cat_update",
    'Right'    => $company_admin_write,
    'Condition'=> array ('None') 
                                     	      );

// Category Check Link
  $actions["COMPANY"]["cat_checklink"] = array (
    'Url'      => "$path/company/company_index.php?action=cat_checklink",
    'Right'    => $company_admin_write,
    'Condition'=> array ('None') 
                                     		);

// Category Delete
  $actions["COMPANY"]["cat_delete"] = array (
    'Url'      => "$path/company/company_index.php?action=cat_delete",
    'Right'    => $company_admin_write,
    'Condition'=> array ('None') 
                                     	       );

// Activity Insert
  $actions["COMPANY"]["activity_insert"] = array (
    'Url'      => "$path/company/company_index.php?action=activity_insert",
    'Right'    => $company_admin_write,
    'Condition'=> array ('None') 
                                     	     );

// Activity Update
  $actions["COMPANY"]["activity_update"] = array (
    'Url'      => "$path/company/company_index.php?action=activity_update",
    'Right'    => $company_admin_write,
    'Condition'=> array ('None') 
                                     	      );

// Activity Check Link
  $actions["COMPANY"]["activity_checklink"] = array (
    'Url'      => "$path/company/company_index.php?action=activity_checklink",
    'Right'    => $company_admin_write,
    'Condition'=> array ('None') 
                                     		);

// Activity Delete
  $actions["COMPANY"]["activity_delete"] = array (
    'Url'      => "$path/company/company_index.php?action=activity_delete",
    'Right'    => $company_admin_write,
    'Condition'=> array ('None') 
                                     	       );

// Display
  $actions["COMPANY"]["display"] = array (
    'Name'     => $l_header_display,
    'Url'      => "$path/company/company_index.php?action=display",
    'Right'    => $company_read,
    'Condition'=> array ('all') 
                                      	 );

// Display Préférences
  $actions["COMPANY"]["dispref_display"] = array (
    'Url'      => "$path/company/company_index.php?action=dispref_display",
    'Right'    => $company_read,
    'Condition'=> array ('None') 
                                     		 );

// Display Level
  $actions["COMPANY"]["dispref_level"]  = array (
    'Url'      => "$path/company/company_index.php?action=dispref_level",
    'Right'    => $company_read,
    'Condition'=> array ('None') 
                                     		 );
					       }
					       
// Document add
  $actions["DEAL"]["document_add"] = array (
    'Url'      => "$path/company/company_index.php?action=document_add",
    'Right'    => $company_write,
    'Condition'=> array ('None')
  );     
</SCRIPT>
