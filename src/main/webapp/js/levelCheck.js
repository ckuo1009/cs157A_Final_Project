window.onload = function() {
   
    $('.dashboard-nav-item').hide();
    $('.dashboard-nav-dropdown-item').hide();
	
    var fullPath = window.location.pathname;
    var pageName = fullPath.split("/").pop();

    $.post('permissionServlet', { 'pageName': pageName })
    .done(function(data) {
        
        
        if (data.status === 'fail') {
            console.log('Login failed');
            if (data.level === -1) {
                window.location.href = 'suspended.html';
            } else if(data.message==='You must be logged in to access this page!!'){
                window.location.href = 'perminFail.html';
            }else {
                window.location.href = 'perminFail.html';
            }
        }else if (data.status === 'success') {
            // 從後端收到的主選單和子選單列表
             if (data.level === 0) {
                $('.dashboard-nav-item').show();
                $('.dashboard-nav-dropdown-item').show();
            }
            
            var mainMenu = data.mainMenu;
            var subMenu = data.subMenu;
            
           

            // 根據後端回傳的列表顯示選項
            mainMenu.forEach(function(item) {
                $('#' + item).show();
            });

            subMenu.forEach(function(item) {
                $('#' + item).show();
            });

            // 如果用戶等級是 0，所有選項都顯示
           
         
    	
        }
    })
    .fail(function() {
        alert("Ajax request failed.");
    });
};
