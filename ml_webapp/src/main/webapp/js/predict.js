$(document).ready(function () {
    $('#singleCommentButton').click(function (event) {
        if($('#singleCommentCollapse').hasClass('show')){
            event.stopPropagation();
        }
    });

    $('#wholeLinkButton').click(function (event) {
        if($('#wholeLinkCollapse').hasClass('show')){
            event.stopPropagation();
        }
    });

});