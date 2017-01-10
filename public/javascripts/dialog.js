$(document).ready(function() {
  $("#messageBox").fadeIn(2000);
  $("#messageBox").delay(5000).fadeOut("slow");
  $(".button").each(function() {
     $.data(this, 'upper', $(this).next());
  }).click(function() {
     $.data(this, 'upper').dialog('open');
  });
  $(".dialog").dialog({
    autoOpen: false
  });

    /**
    $(".minmax").each(function() {
        $.data(this, 'upper', $(this).next());
    })
    $("p").toggle(
        function(){$("p").css({"color": "red"});},
        function(){$("p").css({"color": "blue"});},
        function(){$("p").css({"color": "green"});
        });
     */
});