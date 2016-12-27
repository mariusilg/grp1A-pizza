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
/* Validating Form Fields.....
  $("#submit").click(function(e) {
    //var email = $("#email").val();
    var name = $("#name").val();
    //var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/; //!(email).match(emailReg)
    if (name === '') {
      alert("Bitte geben Sie einen Namen an!");
      e.preventDefault();
    } else if (password === '') {
      alert("Bitte geben Sie ein Passwort an!");
      e.preventDefault();
    }
  });*/
});