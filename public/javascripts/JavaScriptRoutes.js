
$("#trigger").click(function() {
    //checkIfUserExists("test")
    checkUser();
});

/*function checkIfUserExists(username) {
    var ajaxCallBack = {
        success : onSuccess,
        error : onError
    }
    jsRoutes.controllers.JavascriptRoutes.checkUser("TEST").ajax(ajaxCallBack);


}

var  onSuccess = function(data) {
    $('#content').html(data)

}

var onError = function(error) {
    alert(error + "error");
}*/

$(".userInfoForm[name=UserName]").keyup(function(){
        checkUser(this)

});
function checkUser(username) {
    jsRoutes.controllers.JavascriptRoutes.checkIfUserExists($(username).val()).ajax({
        success: function (data) {
            $(".alertUser").remove();
            if(data == "true") {
                renderErrorMessage();
            } else {
                console.log("true");
            }
        }
    });
}

$(".deleteItem").click(function() {
    //alert(parseInt($(this).attr("data-id")))
    if ($(this).attr("data-name") == "extra") {
        rmExtra(this);
    } else
    if ($(this).attr("data-name") == "size"){
        rmSize(this)
    }

});

function rmExtra(id) {
    jsRoutes.controllers.AssortmentController.rmExtra(parseInt($(id).attr("data-id"))).ajax({
        success: function (data) {
            if(data == "true") {
                $(id).parent().parent().remove();
            }

        }
    });
}

function rmSize(id) {
    jsRoutes.controllers.AssortmentController.rmSize(parseInt($(id).attr("data-id"))).ajax({
        success: function (data) {
            if(data == "true") {
                $(id).parent().parent().remove();
            }

        }
    });
}





function renderErrorMessage() {
    console.log("false");
    $("#contentContainer").prepend("<div class='alert alertUser'><span class='closebtn' onclick='$(this).parent().remove();'>&times;</span>Username bereits vergeben!</div>");
}