<?php
/*
 +-------------------------------------------------------------------------+
 |  Copyright (c) 1997-2009 OBM.org project members team                   |
 |                                                                         |
 | This program is free software; you can redistribute it and/or           |
 | modify it under the terms of the GNU General Public License             |
 | as published by the Free Software Foundation; version 2                 |
 | of the License.                                                         |
 |                                                                         |
 | This program is distributed in the hope that it will be useful,         |
 | but WITHOUT ANY WARRANTY; without even the implied warranty of          |
 | MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           |
 | GNU General Public License for more details.                            |
 +-------------------------------------------------------------------------+
 | http://www.obm.org                                                      |
 +-------------------------------------------------------------------------+
*/
?>
<?php
///////////////////////////////////////////////////////////////////////////////
// OBM - File : chart_index.inc
//     - Desc : chart Main file
// 2006-02-04 Aliacom - Pierre Baudracco
///////////////////////////////////////////////////////////////////////////////
// $Id$
///////////////////////////////////////////////////////////////////////////////
// Actions :
// - index (default)    -- search fields  -- show the invoice search form
// - bar                --                -- display a bar chart
// - bar_multiple       --                -- display a multiple bar chart
///////////////////////////////////////////////////////////////////////////////

$path = "..";
$module = "chart";
$obminclude = getenv("OBM_INCLUDE_VAR");
if ($obminclude == "") $obminclude = "obminclude";
include("$obminclude/global.inc");
$params = get_chart_params();

if ($action == "") $action = "index";

$chart_colors = array(array(42, 180, 180), array(42, 71, 180));

///////////////////////////////////////////////////////////////////////////////
// Main program
///////////////////////////////////////////////////////////////////////////////

if ($action == "index" || $action == "") {
///////////////////////////////////////////////////////////////////////////////

} elseif ($action == "bar") {
///////////////////////////////////////////////////////////////////////////////
  include_once("$obminclude/Artichow/BarPlot.class.php");
  $display["detail"] = dis_chart_bar($params);

} elseif ($action == "bar_multiple") {
///////////////////////////////////////////////////////////////////////////////
  include_once("$obminclude/Artichow/BarPlot.class.php");
  $display["detail"] = dis_chart_bar_multiple($params);

} elseif ($action == "display") {
///////////////////////////////////////////////////////////////////////////////
  $prefs = get_display_pref($uid, "invoice", 1);
  $display["detail"] = dis_invoice_display_pref($prefs);
}
  

//////////////////////////////////////////////////////////////////////////////
// Display the invoice dashboard
// Parameters:
//   - $invoice : invoice hash infos
//////////////////////////////////////////////////////////////////////////////
function dis_chart_bar($chart) {

  $values = $chart["values"];
  $labels = $chart["labels"];
  $title = $chart["title"];
  $xlabels = $chart["xlabels"];

  $graph = new Graph(600, 250);
  $graph->setAntiAliasing(TRUE);

  // Chart infos : colors, size, shaow
  $plot = new BarPlot($values, 1, 1);
  $plot->setBarColor(new Color(42, 71, 180));
  $plot->setBackgroundColor(new Color(240, 240, 240));
  $plot->setBarSize(0.6);
  $plot->setSpace(3, 3, NULL, NULL);
  $plot->barShadow->setSize(2);
  $plot->barShadow->smooth(TRUE);

  // Labels infos
  $label = new Label($labels);
  $label->setFont(new Tuffy(8));
  $label->setAlign(NULL, LABEL_TOP);
  $plot->label = $label;

  // X axis Labels infos
  $xlabel = new Label($xlabels);
  $plot->xAxis->setlabelText($xlabels);

  // Title infos
  if ($title != "") {
    $graph->title->set("$title");
  }

  $graph->add($plot);
  $graph->draw();
}


//////////////////////////////////////////////////////////////////////////////
// Display the invoice dashboard
// Parameters:
//   - $invoice : invoice hash infos
//////////////////////////////////////////////////////////////////////////////
function dis_chart_bar_multiple($chart) {
  global $chart_colors;

  $title = $chart["title"];
  $xlabels = $chart["xlabels"];
  $values = $chart["plots"]["values"];
  $labels = $chart["plots"]["labels"];
  $legends = $chart["plots"]["legends"];
  $new_bar = $chart["plots"]["new_bar"];

  $graph = new Graph(600, 250);
  $graph->setAntiAliasing(TRUE);
  $graph->setBackgroundColor(new Color(240, 240, 240));
  $group = new PlotGroup;
  $group->grid->hideVertical();
  $group->setPadding(45, 22 ,30, 40);
  $group->setSpace(3, 3, NULL, NULL);
  $group->legend->setAlign(NULL, LEGEND_TOP);
  $group->legend->setPosition(1,0);

  // X axis Labels infos
  $xlabel = new Label($xlabels);
  $group->axis->bottom->setlabelText($xlabels);

  // Title infos
  if ($title != "") {
    $graph->title->set("$title");
  }

  $num = 0;
  $nb_bars = array_sum($new_bar);
  $num_bar = 0;

  // $num_plot is the plot number
  // $num_bar is the bar number (1 bar can cumul more than one plot)
  foreach ($values as $num_plot => $plot_values) {
    // Chart infos : colors, size, shadow
    if ($new_bar[$num_plot] == 1) {
      $num_bar++;
    }
    $plot = new BarPlot($plot_values, $num_bar, $nb_bars);
    $plot->setBarColor(new Color($chart_colors[$num_plot][0], $chart_colors[$num_plot][1], $chart_colors[$num_plot][2]));
    $plot->setBarSize(0.6);
    if ($new_bar[$num_plot] == 1) {
      $plot->barShadow->setSize(2);
      $plot->barShadow->smooth(TRUE);
      // Labels infos
      $label = new Label($labels[$num_plot]);
      $label->setFont(new Tuffy(8));
      $label->setAlign(NULL, LABEL_TOP);
      $plot->label = $label;
    }
    $group->add($plot);
    $group->legend->add($plot, utf8_decode($legends[$num_plot]), LEGEND_BACKGROUND);
  }

  $graph->add($group);
  $graph->draw();
}


///////////////////////////////////////////////////////////////////////////////
// Stores Chart parameters transmitted in $chart hash
// returns : $chart hash with parameters set
///////////////////////////////////////////////////////////////////////////////
function get_chart_params() {

  // Get global params
  $params = get_global_params("Chart");

  if (isset ($params["title"])) $params["title"] = stripslashes($params["title"]);
  if (isset ($params["values"])) $params["values"] = unserialize(stripslashes($params["values"]));
  if (isset ($params["labels"])) $params["labels"] = unserialize(stripslashes($params["labels"]));
  if (isset ($params["xlabels"])) $params["xlabels"] = unserialize(stripslashes($params["xlabels"]));

  if (isset ($params["plots"])) $params["plots"] = unserialize(stripslashes($params["plots"]));

  return $params;
}

?>
