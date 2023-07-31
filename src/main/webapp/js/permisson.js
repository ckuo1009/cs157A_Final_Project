//其他頁面用

window.onload = function() {
    var fullPath = window.location.pathname;
    var pageName = fullPath.split("/").pop();

    $.post('permissionServlet', { 'pageName': pageName })
    .done(function(data) {
    	console.log(data); 
        
       if (data.status === 'fail') {
    // 根據不同的消息導向不同的頁面
    if (data.level === -1) {
        window.location.href = 'suspended.html';
    }  else if(data.message==='You must be logged in to access this page!!'){
            window.location.href = 'loginFail.html';
            
             } else {
        // 導向一個預設的失敗頁面
        window.location.href = 'perminFail.html';
    }
} else if (data.status === 'success') {
            // 維持在同一頁，可以刷新頁面或者作其他操作
            // 如果你確實需要刷新頁面，你可以使用下面的代碼
            
        }
    })
    .fail(function() {
        alert("Ajax request failed.");
    });
};