require 'sinatra'

get "/search.php" do
  "<a class=\"inactive\" name=\"efahyperlinks\" href=\"http://192.168.2.103:4567/4294202\" target=\"_self\"><span style=\"font-weight:bold;\">Ems</span>straße <span class=\"richtung\">(einwärts)</span></a>"
end

get "/ajaxrequest.php*" do
  "<span class=\"haltestellenlable\" id=\"haltestellenlableID\">Dechaneistraße</span><br /><div class=\"bgdark\"><div class=\"line\">2</div><div class=\"direction\">Clemenshospital</div><div class=\"time\">9min</div><br class=\"clear\" /></div><div class=\"bgwith\"><div class=\"line\">R11</div><div class=\"direction\">Münster(Westf) Hbf</div><div class=\"time\">9min</div><br class=\"clear\" /></div><div class=\"bgdark\"><div class=\"line\">10</div><div class=\"direction\">Mecklenbeck Meckmannweg</div><div class=\"time\">25min</div><br class=\"clear\" /></div><br />18:05:55<br />einwärts<div class=\"bgdark\"><div class=\"line\">2</div><div class=\"direction\">Clemenshospital</div><div class=\"time\">9min</div><br class=\"clear\" /></div><div class=\"bgwith\"><div class=\"line\">R11</div><div class=\"direction\">Münster(Westf) Hbf</div><div class=\"time\">9min</div><br class=\"clear\" /></div><div class=\"bgdark\"><div class=\"line\">10</div><div class=\"direction\">Mecklenbeck Meckmannweg</div><div class=\"time\">25min</div><br class=\"clear\" /></div><br />18:05:55<br />einwärts"
end

not_found do
  status 404
  "Not found. Sorry."
end
