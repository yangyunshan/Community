function like(btn, entityType, entityId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType, "entityId":entityId},
        function (data) {
            console.log(data);
            data = $.parseJSON(data);
            console.log(data.code);
            if (data.code == 0) {
                $(btn).children("i").text(data.likeCount);
            }
        }
    );
}