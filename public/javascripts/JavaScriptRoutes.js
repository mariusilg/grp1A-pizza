
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
        checkName(this)
});

$(".editUserForm[name=UserName]").keyup(function(){
    checkOtherName($(".editUserForm[name=ID]"), this)
});

$(".userInfoForm[name=Email]").keyup(function(){
    checkEmail(this)
});
$(".editUserForm[name=Email]").keyup(function(){
    checkOtherEmail($(".editUserForm[name=ID]"), this)
});

function checkName(username) {
    jsRoutes.controllers.JavascriptRoutes.checkName($(username).val()).ajax({
        success: function (data) {
            $(".alertUser").remove();
            if(data == "true") {
                renderErrorMessageUser();
            } else {
                console.log("true");
            }
        }
    });
}

function checkOtherName(id, username) {
    jsRoutes.controllers.JavascriptRoutes.checkOtherName($(id).val(), $(username).val()).ajax({
        success: function (data) {
            $(".alertUser").remove();
            if(data == "true") {
                renderErrorMessageUser();
            } else {
                console.log("true");
            }
        }
    });
}

function checkEmail(email) {
    jsRoutes.controllers.JavascriptRoutes.checkEmail($(email).val()).ajax({
        success: function (data) {
            $(".alertUser").remove();
            if(data == "true") {
                renderErrorMessageMail();
            } else {
                console.log("true");
            }
        }
    });
}

<<<<<<< HEAD
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



=======
function checkOtherEmail(id, email) {
    jsRoutes.controllers.JavascriptRoutes.checkOtherEmail($(id).val(), $(email).val()).ajax({
        success: function (data) {
            $(".alertUser").remove();
            if(data == "true") {
                renderErrorMessageMail();
            } else {
                console.log("true");
            }
        }
    });
}
>>>>>>> ce3863bedb06001af62c90a4d476829852f8ebdd



function renderErrorMessageUser() {
    console.log("false");
    $("#contentContainer").prepend("<div class='alert alertUser'><span class='closebtn' onclick='$(this).parent().remove();'>&times;</span>Username bereits vergeben!</div>");
}

function renderErrorMessageMail() {
    console.log("false");
    $("#contentContainer").prepend("<div class='alert alertUser'><span class='closebtn' onclick='$(this).parent().remove();'>&times;</span>Email bereits vorhanden!</div>");
}