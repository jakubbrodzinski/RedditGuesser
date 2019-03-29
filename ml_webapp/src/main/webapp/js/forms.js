$(document).ready(function () {
    $('form').on('submit',function(e){
        $(this).find("button[type=submit]").prop('disabled',true);
    });
});