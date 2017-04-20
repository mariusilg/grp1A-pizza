jQuery(document).ready(function ($) {

    var liWidth = $('#slider ul li').width($(window).width());
    var slideCount = liWidth.length;
    var slideWidth = $('#slider ul li').width();
    var slideHeight = $('#slider ul li').height();
    var sliderUlWidth = slideCount * slideWidth;

    $(document).ready(function() {
        autoPlay();
        resizeWindow();
    });

    function autoPlay() {
        setInterval(function () {
            moveRight();
        }, 3000);
    };

    $(window).resize(function() {
       resizeWindow();
        $('.canvas').css({width: $(window).width(), height: "auto"});
    });

    function resizeWindow() {
        liWidth = $('#slider ul li').width($(window).width());
        slideCount = liWidth.length;
        slideWidth = $('#slider ul li').width();
        slideHeight = $('#slider ul li').height();
        sliderUlWidth = slideCount * slideWidth;

        //$('.canvas').css({width: $(window).width(), height: "auto"});
        $('#slider').css({width: slideWidth, height: slideHeight});

        $('#slider ul').css({width: sliderUlWidth, marginLeft: -slideWidth});

        $('#slider ul li:last-child').prependTo('#slider ul');


    }

    function moveLeft() {
        $('#slider ul').animate({
            left: + slideWidth
        }, 200, function () {
            $('#slider ul li:last-child').prependTo('#slider ul');
            $('#slider ul').css('left', '');
        });
    };

    function moveRight() {
        $('#slider ul').animate({
            left: - slideWidth
        }, 200, function () {
            $('#slider ul li:first-child').appendTo('#slider ul');
            $('#slider ul').css('left', '');
        });
    };

    $('a.control_prev').click(function () {
        moveLeft();
    });

    $('a.control_next').click(function () {
        moveRight();
    });

});

